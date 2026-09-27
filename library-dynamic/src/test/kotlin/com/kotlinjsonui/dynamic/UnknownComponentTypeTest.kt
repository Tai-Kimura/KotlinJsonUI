package com.kotlinjsonui.dynamic

import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.components.DynamicSelectBoxComponent
import com.kotlinjsonui.dynamic.generated.JsonUIBindPrimaryValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Type names are matched as written (4f's ruling, jsonui-cli 1.9.0). A type
 * DynamicView cannot draw is named with the one sentence (UnknownComponentType)
 * jsonui-cli's validator and codegen say — held to its table
 * (shared/core/unknown_component_type_vectors.json, copied byte-identical into
 * src/test/resources; CI's vendored-fixture step compares the copy). The
 * dispatch, the type synonyms and the drawn-type rule take a spelling as
 * written: a lowercase `switch` was drawn as a Switch here (and by SwiftJsonUI)
 * and as nothing by the codegen.
 */
class UnknownComponentTypeTest {

    @Test
    fun everyCaseOfTheSharedTable() {
        val stream = requireNotNull(javaClass.classLoader?.getResourceAsStream("unknown_component_type_vectors.json"))
        val cases = stream.reader(Charsets.UTF_8).use { JsonParser.parseReader(it) }.asJsonObject.getAsJsonArray("cases")
        assertTrue("the table has fewer cases than it did (5)", cases.size() >= 5)
        for (c in cases.map { it.asJsonObject }) {
            assertEquals(
                c.get("name").asString,
                c.get("expect").asString,
                UnknownComponentType.message(c.get("written").asString, c.getAsJsonArray("known").map { it.asString }),
            )
        }
    }

    /** The table's `case_only` section: the Ruby validator's own rows (jsonui-cli UnknownTypeCaseRows). */
    private val caseOnly by lazy {
        val stream = requireNotNull(javaClass.classLoader?.getResourceAsStream("unknown_component_type_vectors.json"))
        stream.reader(Charsets.UTF_8).use { JsonParser.parseReader(it) }.asJsonObject.getAsJsonObject("case_only")
    }
    private val notDrawn: Set<String> by lazy {
        caseOnly.getAsJsonObject("not_drawn").getAsJsonArray("kotlin_dynamic").map { it.asString }.toSet()
    }

    /**
     * The types this runtime does not draw are the declaration's
     * (component_metadata.json `kotlin_dynamic: false`, which the table
     * carries): exactly the SSoT's sections the dispatch has no case for, both
     * ways — a moved declaration or a moved case reddens this. SwiftJsonUI's
     * TypeNameCaseVectorsTests is the same arm.
     */
    @Test
    fun theTypesItDoesNotDrawAreTheDeclarations() {
        val known = caseOnly.getAsJsonArray("known").map { it.asString }.toSet()
        val sections = known - TypeSynonyms.entries.keys - com.kotlinjsonui.dynamic.generated.JsonUIComponentAliases.canonical.keys
        val uncased = sections - BUILT_IN_TYPES
        assertTrue("the table's sections are more than a handful (${sections.size})", sections.size >= 20)
        assertEquals("declared not drawn (kotlin_dynamic: false) vs sections with no case", notDrawn, uncased)
        assertTrue(uncased.containsAll(notDrawn) && notDrawn.containsAll(uncased))
    }

    /**
     * Every row but those naming a type this runtime does not draw: the
     * dispatch's sentence (unknownTypeMessage), byte for byte. Those rows are
     * the validator's words, not this runtime's — it does not offer what it
     * cannot draw.
     */
    @Test
    fun itSaysTheValidatorsSentenceForEveryKnownTypeInAnotherCase() {
        val rows = caseOnly.getAsJsonArray("rows").map { it.asJsonObject }
        var compared = 0
        var excluded = 0
        var offeredBeyondDrawn = 0
        val wrong = mutableListOf<String>()
        for (row in rows) {
            val written = row.get("written").asString
            val expect = row.get("expect").asString
            if (notDrawn.any { it.equals(written, ignoreCase = true) }) {
                excluded++
                assertTrue(written, notDrawn.any { expect.contains("'$it'") })
                continue
            }
            val said = unknownTypeMessage(written)
            if (said != expect) wrong += "$written: $said"
            compared++
            val offered = Regex("did you mean '([^']+)'").find(expect)?.groupValues?.get(1)
            if (offered != null && offered !in BUILT_IN_TYPES) offeredBeyondDrawn++
        }
        assertEquals("sentences off the validator's", emptyList<String>(), wrong)
        assertTrue("compared $compared rows", compared > 200)
        // kotlin_dynamic declares nothing it does not draw (the table), so no row is left out
        if (notDrawn.isEmpty()) assertEquals("rows left out with nothing declared", 0, excluded)
        // The rows a search of the drawn types alone would miss: a synonym in
        // another case (the control for the search's pool).
        assertTrue("rows offering a type beyond the drawn ones: $offeredBeyondDrawn", offeredBeyondDrawn > 100)
    }

    @Test
    fun theDrawnTypeRuleTakesASpellingAsWritten() {
        assertEquals("Switch", com.kotlinjsonui.dynamic.helpers.LayoutPath.drawnType("Toggle"))
        assertEquals("toggle", com.kotlinjsonui.dynamic.helpers.LayoutPath.drawnType("toggle"))
        assertEquals("SelectBox", com.kotlinjsonui.dynamic.helpers.LayoutPath.drawnType("Picker"))
        assertEquals("picker", com.kotlinjsonui.dynamic.helpers.LayoutPath.drawnType("picker"))
    }

    /** selectItemType as written: "date" is a list box in the draw and in the fold alike. */
    @Test
    fun aDateBoxIsDateAsWritten() {
        fun node(kind: String) = JsonParser.parseString("""{"type": "SelectBox", "selectItemType": "$kind"}""").asJsonObject
        assertTrue(DynamicSelectBoxComponent.isDateBox(node("Date")))
        assertFalse(DynamicSelectBoxComponent.isDateBox(node("date")))
        assertEquals(listOf("selectedDate"), JsonUIBindPrimaryValue.attributesFor("SelectBox", TypedAttrs.toAttrMap(node("Date"))))
        assertEquals("selectedValue", JsonUIBindPrimaryValue.attributesFor("SelectBox", TypedAttrs.toAttrMap(node("date"))).first())
    }

    private val view = File("src/main/kotlin/com/kotlinjsonui/dynamic/DynamicView.kt").readText()

    /**
     * The dispatch's cases are BUILT_IN_TYPES, each a spelling the SSoT
     * declares (a section, or a synonym drawn as itself — CircleImage), and
     * the dispatch matches them as written.
     */
    @Test
    fun theDispatchMatchesTheBuiltInTypesAsWritten() {
        val start = view.indexOf("if (!handledByApp) when (drawnType) {")
        assertTrue("the dispatch is not `when (drawnType)`", start >= 0)
        val body = view.substring(start, view.indexOf("else -> {", start))
        val cases = Regex(""""([A-Za-z]+)"(?=[^\n]*->)""").findAll(body).map { it.groupValues[1] }.toSet()
        assertEquals(BUILT_IN_TYPES, cases)
        assertFalse("the dispatch lowercases the type", body.contains("lowercase()"))
        val synonyms = TypeSynonyms.entries
        for (type in BUILT_IN_TYPES) {
            val known = com.kotlinjsonui.dynamic.generated.JsonUIComponentAliases.canonical.containsKey(type) ||
                type in DECLARED_SECTIONS || synonyms[type]?.drawnAs == type
            assertTrue("$type is not a spelling the SSoT declares", known)
        }
    }

    /** The unknown-type branch names the node's type with UnknownComponentType. */
    @Test
    fun theUnknownBranchSaysTheSentence() {
        val start = view.indexOf("if (!handledByApp) when (drawnType) {")
        val branch = view.substring(view.indexOf("else -> {", start), view.indexOf("// Apply visibility/hidden wrapper", start))
        // the branch says unknownTypeMessage's sentence — the one the case_only
        // arm holds to the validator's rows
        assertTrue(branch.contains("val message = unknownTypeMessage(type)"))
        assertTrue(view.contains("UnknownComponentType.message(type, BUILT_IN_TYPES + TypeSynonyms.entries.keys)"))
        assertFalse(branch.contains("\"Unknown component type: \$type\""))
        assertEquals(
            "Unknown component type 'switch' — did you mean 'Switch'? Type names are case-sensitive.",
            unknownTypeMessage("switch"),
        )
    }

    private companion object {
        /** attribute_definitions.json's component sections (a copy — the SSoT is not vendored here). */
        val DECLARED_SECTIONS = setOf(
            "Blur", "Button", "Check", "CheckBox", "CircleView", "Collection", "EditText", "Embed", "GradientView",
            "IconLabel", "Image", "Indicator", "Input", "Label", "NetworkImage", "Progress", "Radio", "SafeAreaView",
            "ScrollView", "Segment", "SelectBox", "Slider", "Switch", "TabView", "TextField", "TextView", "Toggle",
            "View", "Web",
        )
    }
}
