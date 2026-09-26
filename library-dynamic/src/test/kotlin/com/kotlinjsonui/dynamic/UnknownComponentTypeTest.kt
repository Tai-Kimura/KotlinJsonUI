package com.kotlinjsonui.dynamic

import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.components.DynamicSelectBoxComponent
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

    @Test
    fun theDrawnTypeRuleTakesASpellingAsWritten() {
        assertEquals("Switch", com.kotlinjsonui.dynamic.helpers.LayoutPath.drawnType("Toggle"))
        assertEquals("toggle", com.kotlinjsonui.dynamic.helpers.LayoutPath.drawnType("toggle"))
        assertEquals("SelectBox", com.kotlinjsonui.dynamic.helpers.LayoutPath.drawnType("Picker"))
        assertEquals("picker", com.kotlinjsonui.dynamic.helpers.LayoutPath.drawnType("picker"))
    }

    /** selectItemType as written: "date" is a list box. */
    @Test
    fun aDateBoxIsDateAsWritten() {
        fun node(kind: String) = JsonParser.parseString("""{"type": "SelectBox", "selectItemType": "$kind"}""").asJsonObject
        assertTrue(DynamicSelectBoxComponent.isDateBox(node("Date")))
        assertFalse(DynamicSelectBoxComponent.isDateBox(node("date")))
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
        assertTrue(branch.contains("UnknownComponentType.message(type, BUILT_IN_TYPES + TypeSynonyms.entries.keys)"))
        assertFalse(branch.contains("\"Unknown component type: \$type\""))
        assertEquals(
            "Unknown component type 'switch' — did you mean 'Switch'? Type names are case-sensitive.",
            UnknownComponentType.message("switch", BUILT_IN_TYPES + TypeSynonyms.entries.keys),
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
