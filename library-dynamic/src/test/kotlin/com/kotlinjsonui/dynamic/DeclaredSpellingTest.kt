package com.kotlinjsonui.dynamic

import com.kotlinjsonui.core.DeclaredSpelling
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.generated.AttrWarnings
import com.kotlinjsonui.dynamic.generated.BlurAttributes
import com.kotlinjsonui.dynamic.generated.CollectionAttributes
import com.kotlinjsonui.dynamic.generated.CommonAttributes
import com.kotlinjsonui.dynamic.helpers.EffectStyleTable
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * An enum value is its declared spelling, case and all (4f's ruling, jsonui-cli
 * 1.9.0; jsonui-cli b992cee0 made the generated parses match as written and
 * publish `declaredSpellings`). The hand-written comparisons read
 * DeclaredSpelling.lowered — the lowercased value when it is declared, null
 * otherwise — and a value no parse saw is named in the generated parse's
 * sentence.
 */
class DeclaredSpellingTest {

    private val said = mutableListOf<String>()

    @Before
    fun sinks() {
        DeclaredSpelling.warningSink = { said += it }
        AttrWarnings.handler = { said += it }
    }

    @After
    fun reset() {
        DeclaredSpelling.warningSink = null
        AttrWarnings.handler = null
    }

    @Test
    fun aDeclaredSpellingIsLowered_anythingElseIsNull() {
        val declared = CollectionAttributes.ListStyle.declaredSpellings
        assertEquals("insetgrouped", DeclaredSpelling.lowered("insetGrouped", declared))
        assertNull(DeclaredSpelling.lowered("InsetGrouped", declared))
        assertNull(DeclaredSpelling.lowered("insetgrouped", declared))
        assertNull(DeclaredSpelling.lowered("nope", declared))
        assertNull(DeclaredSpelling.lowered(null, declared))
        assertEquals(emptyList<String>(), said)
    }

    /**
     * A value no parse saw is named in the generated parse's own sentence —
     * machine-compared: the same raw value handed to the generated parse
     * (CommonAttributes.parse) says the same words.
     */
    @Test
    fun aNearMissIsNamedInTheGeneratedParsesSentence_once() {
        val declared = CommonAttributes.Visibility.declaredSpellings
        assertNull(DeclaredSpelling.lowered("Gone", declared, "common.visibility"))
        assertNull(DeclaredSpelling.lowered("Gone", declared, "common.visibility"))
        assertNull(DeclaredSpelling.lowered("hidden", declared, "common.visibility"))
        assertEquals("gone", DeclaredSpelling.lowered("gone", declared, "common.visibility"))
        val helper = said.toList()
        assertEquals(
            listOf(
                "common.visibility: unknown enum value 'Gone' — did you mean 'gone'?",
                "common.visibility: unknown enum value 'hidden'",
            ),
            helper,
        )
        said.clear()
        CommonAttributes.parse(mapOf("visibility" to "Gone"))
        CommonAttributes.parse(mapOf("visibility" to "hidden"))
        assertEquals(helper, said)
    }

    /**
     * A material is its node's declared spelling: the node's own section
     * first (Blur declares Light / Dark / ExtraLight), else common (all
     * fourteen). Both sides: the declared spelling draws its material, another
     * case — or a material only common declares, on a Blur — draws the
     * fallback (regular).
     */
    @Test
    fun aMaterialIsItsNodesDeclaredSpelling() {
        val blur = BlurAttributes.EffectStyle.declaredSpellings
        val common = CommonAttributes.EffectStyle.declaredSpellings
        val regular = EffectStyleTable.scrim("Regular", common)
        assertNotEquals(regular, EffectStyleTable.scrim("Light", blur))
        assertEquals(EffectStyleTable.scrim("Light", common), EffectStyleTable.scrim("Light", blur))
        assertEquals(regular, EffectStyleTable.scrim("light", blur))
        assertEquals(regular, EffectStyleTable.scrim("light", common))
        assertNotEquals(regular, EffectStyleTable.scrim("UltraThin", common))
        assertEquals(regular, EffectStyleTable.scrim("UltraThin", blur))
    }

    /**
     * `alignment` is read raw (ModifierBuilder), so the helper names another
     * case — in the generated parse's own words, machine-compared.
     */
    @Test
    fun anAlignmentIsItsDeclaredSpelling_andARawReadNamesIt() {
        fun flags(v: String) = ModifierBuilder.parseAlignmentString(JsonObject().apply { addProperty("alignment", v) })
        assertNotNull(flags("bottomTrailing"))
        assertNull(flags("BottomTrailing"))
        val helper = said.toList()
        assertEquals(listOf("common.alignment: unknown enum value 'BottomTrailing' — did you mean 'bottomTrailing'?"), helper)
        said.clear()
        CommonAttributes.parse(mapOf("alignment" to "BottomTrailing"))
        assertEquals(helper, said)
    }

    /** gravity holds one or a list, so it has spellings but no enum type (jsonui-cli 21a00af0). */
    @Test
    fun gravityHasDeclaredSpellings() {
        val declared = CommonAttributes.Gravity.declaredSpellings
        assertEquals("centerhorizontal", DeclaredSpelling.lowered("centerHorizontal", declared))
        assertNull(DeclaredSpelling.lowered("CenterHorizontal", declared))
        assertNull(DeclaredSpelling.lowered("centerhorizontal", declared))
    }

    /** This module's copies (the library module does not see the generated tables) are the generated spellings. */
    @Test
    fun theLibraryModulesCopiesAreTheGeneratedSpellings() {
        assertEquals(CommonAttributes.Visibility.declaredSpellings, DeclaredSpelling.VISIBILITY)
        assertEquals(CollectionAttributes.ListStyle.declaredSpellings, DeclaredSpelling.LIST_STYLE)
    }

    /** A row left as written, and why — printed by the scan. */
    private data class Left(
        val path: String,
        val line: String,
        val why: String,
        /** A generated member that must NOT exist: the attribute is not declared as an enum (or at all). */
        val absent: String? = null,
    )

    private val dyn = "src/main/kotlin/com/kotlinjsonui/dynamic/"
    private val lib = "../library/src/main/kotlin/com/kotlinjsonui/"
    private val nested = "declared as an enum inside an object; the generator publishes no declaredSpellings for an object's properties"
    private val fontWhy = "a font is a weight name or a family name, not an enum"
    private val xml = "XML mode (an android.view.View); KotlinJsonUI draws Compose only, XML is frozen"

    /**
     * Every `.lowercase()` / `ignoreCase = true` left in the files this family
     * touches — each with why. The generator decides: an attribute with no
     * generated enum (checked by reflection) is not an enum, so it stays.
     */
    private val left = listOf(
        Left("${dyn}components/DynamicTextComponent.kt", "when (align.lowercase()) {",
            "Label.highlightAttributes.textAlign — $nested; hintAttributes declares no textAlign"),
        Left("${dyn}components/DynamicTextComponent.kt", "is Map<*, *> -> !\"none\".equals(declared[\"lineStyle\"] as? String, ignoreCase = true)",
            "Label.underline / strikethrough .lineStyle — $nested"),
        Left("${dyn}components/DynamicTextComponent.kt", "val style = (map[\"lineStyle\"] as? String)?.lowercase() ?: \"single\"",
            "Label.underline / strikethrough .lineStyle — $nested"),
        Left("${lib}components/StyledTextLines.kt", "fun strokeFor(style: String) = when (style.lowercase()) {",
            "Label.underline / strikethrough .lineStyle — $nested"),
        Left("${lib}components/StyledTextLines.kt", "if (face.style.equals(\"double\", ignoreCase = true)) {",
            "Label.underline / strikethrough .lineStyle — $nested"),
        Left("${dyn}components/DynamicTextComponent.kt", "hlFont != null && WEIGHT_NAMES.containsKey(hlFont.lowercase()) ->",
            "Label.font / highlightAttributes.font — $fontWhy", absent = "LabelAttributes\$Font"),
        Left("${dyn}components/DynamicTextComponent.kt", "WEIGHT_NAMES[hlFont.lowercase()]",
            "Label.font / highlightAttributes.font — $fontWhy", absent = "LabelAttributes\$Font"),
        Left("${dyn}components/DynamicTextComponent.kt", "hlFont != null && !WEIGHT_NAMES.containsKey(hlFont.lowercase()) ->",
            "Label.font / highlightAttributes.font — $fontWhy", absent = "LabelAttributes\$Font"),
        Left("${dyn}components/DynamicTextComponent.kt", "val lower = font.lowercase()",
            "Label.font — $fontWhy", absent = "LabelAttributes\$Font"),
        Left("${dyn}components/DynamicTextComponent.kt", "if (!WEIGHT_NAMES.containsKey(font.lowercase())) {",
            "Label.font — $fontWhy", absent = "LabelAttributes\$Font"),
        Left("${dyn}components/DynamicSwitchComponent.kt", "val fontWeightValue = when ((labelAttrs?.get(\"font\") as? String)?.lowercase()) {",
            "Switch.labelAttributes.font — a string, $fontWhy"),
        Left("${dyn}components/DynamicToggleComponent.kt", "when (w.lowercase()) {",
            "Toggle.labelAttributes.fontWeight — undeclared: Toggle is Switch's section, whose labelAttributes declares text / font / fontSize / fontColor"),
        Left("${dyn}components/DynamicSelectBoxComponent.kt", "return when (font?.lowercase()) {",
            "SelectBox.font — $fontWhy", absent = "SelectBoxAttributes\$Font"),
        Left("${dyn}helpers/ResourceResolver.kt", "name?.let { WEIGHT_NAMES[it.lowercase()] ?: it.trim().toIntOrNull()?.let(NUMERIC_WEIGHTS::get) }",
            "Label.fontWeight is string|number — not an enum", absent = "LabelAttributes\$FontWeight"),
        Left("${dyn}helpers/ResourceResolver.kt", "return GENERIC_FAMILIES[name.trim().lowercase()]",
            "a font family name — $fontWhy"),
        Left("${dyn}helpers/ResourceResolver.kt", "val resName = name.replace(\"-\", \"_\").replace(\" \", \"_\").lowercase()",
            "a font name made a res/font identifier — not an enum value"),
        Left("${dyn}helpers/ResourceResolver.kt", "return s.equals(\"true\", ignoreCase = true)",
            "a boolean spelling — not an enum"),
        Left("${lib}components/PartialAttributesText.kt", "private fun parseFontWeight(raw: String?): FontWeight? = when (raw?.trim()?.lowercase()) {",
            "partialAttributes fontWeight is string|number — not an enum", absent = "LabelAttributes\$FontWeight"),
        Left("${dyn}helpers/ContentInsetBehavior.kt", "when (value?.trim()?.lowercase()) {",
            "Collection.contentInsetAdjustmentBehavior is a string, not an enum (ScrollView's enum is read as declared at its call)",
            absent = "CollectionAttributes\$ContentInsetAdjustmentBehavior"),
        Left("${dyn}components/DynamicCollectionComponent.kt", ".replace(Regex(\"([a-z])([A-Z])\")) { \"\${it.groupValues[1]}_\${it.groupValues[2].lowercase()}\" }",
            "a cell class name made a layout file name — not an enum value"),
        Left("${dyn}components/DynamicCollectionComponent.kt", ".lowercase()",
            "a cell class name made a layout file name — not an enum value"),
        Left("${dyn}components/DynamicButtonComponent.kt", "val imagePosition = TypedAttrs.undeclared(json, \"imagePosition\")?.asString?.lowercase() ?: \"leading\"",
            "Button declares no imagePosition", absent = "ButtonAttributes.imagePosition"),
        Left("${dyn}components/DynamicTextFieldComponent.kt", "val isHidden = TypedAttrs.static(a.fontColor)?.lowercase() == \"transparent\"",
            "a colour — not an enum"),
        Left("${dyn}helpers/ModifierBuilder.kt", "if (p.isString) return p.asString.equals(\"true\", ignoreCase = true)",
            "a boolean spelling — not an enum"),
        Left("${lib}binding/BindingAdapters.kt", "view.visibility = when (visibility?.lowercase()) {", xml),
        Left("${lib}views/KjuiSafeAreaView.kt", "contentInsetAdjustmentBehavior = behavior.lowercase()", xml),
        Left("${lib}views/KjuiSafeAreaView.kt", "val positions = positionsString.split(\"|\", \",\").map { it.trim().lowercase() }.toSet()", xml),
        Left("${lib}views/KjuiSafeAreaView.kt", "when (position.lowercase()) {", xml),
        Left("${lib}views/KjuiGradientView.kt", "gradientOrientation = when (direction.lowercase()) {", xml),
        Left("${lib}views/KjuiGradientView.kt", "gradientType = when (type.lowercase()) {", xml),
    )

    /**
     * Each file this family touches reads the declared spellings: the count
     * of lines calling DeclaredSpelling.lowered, and the section whose
     * spellings a helper is handed (the node's own section, else common).
     * Lane 31's list (rel aa1280a), the four switches beside it (TextField
     * contentType, autocapitalizationType, autocorrectionType; TextView
     * keyboardType), and the family outside it: textAlign, contentMode,
     * effectStyle, contentInsetAdjustmentBehavior, alignment, and gravity and
     * safeAreaInsetPositions (jsonui-cli 21a00af0). Code only — a comment
     * does not count.
     */
    @Test
    fun theFamilyReadsTheDeclaredSpellings_andEveryRowLeftSaysWhy() {
        val c = "${dyn}components/"
        val h = "${dyn}helpers/"
        val uses = mapOf(
            c + "DynamicCollectionComponent.kt" to 3, c + "DynamicContainerComponent.kt" to 2,
            c + "DynamicIconLabelComponent.kt" to 1, c + "DynamicImageComponent.kt" to 1,
            c + "DynamicNetworkImageComponent.kt" to 1, c + "DynamicScrollViewComponent.kt" to 2,
            c + "DynamicSwitchComponent.kt" to 1, c + "DynamicTextComponent.kt" to 3,
            c + "DynamicTextFieldComponent.kt" to 8, c + "DynamicTextViewComponent.kt" to 2,
            c + "DynamicButtonComponent.kt" to 1, c + "DynamicCircleImageComponent.kt" to 0,
            c + "DynamicBlurViewComponent.kt" to 0, c + "DynamicToggleComponent.kt" to 0,
            c + "DynamicSelectBoxComponent.kt" to 0,
            h + "ImageContentScale.kt" to 2, h + "EffectStyleTable.kt" to 1, h + "ModifierBuilder.kt" to 1,
            h + "ContentInsetBehavior.kt" to 0, h + "ResourceResolver.kt" to 0, h + "SafeAreaEdges.kt" to 1,
            lib + "components/VisibilityWrapper.kt" to 2, lib + "components/CollectionCellChrome.kt" to 1,
            lib + "components/StyledTextLines.kt" to 0, lib + "components/PartialAttributesText.kt" to 0,
            lib + "binding/BindingAdapters.kt" to 0, lib + "views/KjuiSafeAreaView.kt" to 0,
            lib + "views/KjuiGradientView.kt" to 0,
        )
        val sections = mapOf(
            c + "DynamicTextComponent.kt" to listOf("LabelAttributes.TextAlign.declaredSpellings", "CommonAttributes.Gravity.declaredSpellings"),
            c + "DynamicButtonComponent.kt" to listOf("ButtonAttributes.TextAlign.declaredSpellings"),
            c + "DynamicTextFieldComponent.kt" to listOf("TextFieldAttributes.TextAlign.declaredSpellings"),
            c + "DynamicScrollViewComponent.kt" to listOf("ScrollViewAttributes.ContentInsetAdjustmentBehavior.declaredSpellings"),
            c + "DynamicBlurViewComponent.kt" to listOf("BlurAttributes.EffectStyle.declaredSpellings"),
            c + "DynamicCollectionComponent.kt" to listOf("CommonAttributes.Gravity.declaredSpellings"),
            h + "ModifierBuilder.kt" to listOf("CommonAttributes.EffectStyle.declaredSpellings", "CommonAttributes.Alignment.declaredSpellings"),
        )
        val lowering = Regex("""\.lowercase\(\)|ignoreCase\s*=\s*true""")
        fun code(f: File) = f.readLines().map { l -> l.trimStart().let { t -> if (t.startsWith("//") || t.startsWith("*")) "" else l } }

        // the reflection sees a generated enum, and a generated field, when there is one
        fun generated(member: String): Boolean {
            val (cls, field) = if ("." in member) member.split(".") else listOf(member, null)
            val k = runCatching { Class.forName("com.kotlinjsonui.dynamic.generated.$cls") }.getOrNull() ?: return false
            return field == null || k.declaredFields.any { it.name == field }
        }
        assertTrue(generated("LabelAttributes\$TextAlign"))
        assertTrue(generated("ButtonAttributes.textAlign"))
        assertTrue(!generated("LabelAttributes\$Nope"))

        val unexplained = mutableListOf<String>()
        val matched = mutableSetOf<Left>()
        for ((path, n) in uses) {
            val f = File(path)
            assertTrue("missing ${f.absolutePath}", f.isFile)
            val lines = code(f)
            assertEquals("${f.name} reads DeclaredSpelling.lowered", n, lines.count { "DeclaredSpelling.lowered(" in it })
            for (want in sections[path].orEmpty()) assertTrue("${f.name} does not read $want", lines.any { want in it })
            lines.forEachIndexed { i, l ->
                if (!lowering.containsMatchIn(l)) return@forEachIndexed
                val row = left.firstOrNull { it.path == path && it.line == l.trim() }
                if (row == null) unexplained += "${f.name}:${i + 1}: ${l.trim()}"
                else {
                    matched += row
                    println("left  ${f.name}:${i + 1}  ${row.why}")
                }
            }
        }
        assertEquals("rows lowering with no reason", emptyList<String>(), unexplained)
        assertEquals("rows listed as left that are gone (take them off)", emptyList<Left>(), left - matched)
        for (row in left) row.absent?.let {
            assertTrue("${row.path.substringAfterLast('/')}: $it is generated now — it is declared, fold it", !generated(it))
        }
    }
}
