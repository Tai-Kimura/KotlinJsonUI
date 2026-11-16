package com.kotlinjsonui.dynamic.components

import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.generated.AttrEnum
import com.kotlinjsonui.dynamic.generated.IndicatorAttributes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * An Indicator draws the declared `indicatorStyle` (small / medium / large /
 * linear, jsonui-cli ea985526). It read `style` — the style-file key — and an
 * undeclared `size`; jui's normalizer now folds both (`style` into
 * indicatorStyle, `size` into width / height), so a normalized layout carried
 * neither and every Indicator drew medium and circular.
 */
class IndicatorStyleTest {

    private fun attrs(json: String): IndicatorAttributes =
        IndicatorAttributes.parse(TypedAttrs.toAttrMap(JsonParser.parseString(json).asJsonObject))

    @Test
    fun theFourDeclaredStylesAreKnown() {
        assertEquals(
            listOf("small", "medium", "large", "linear"),
            IndicatorAttributes.IndicatorStyle.entries.map { it.json }
        )
        for (style in listOf("small", "medium", "large", "linear")) {
            val parsed = attrs("""{"type":"Indicator","indicatorStyle":"$style"}""").indicatorStyle
            assertTrue("$style parses as declared", parsed is AttrEnum.Known<*>)
        }
    }

    @Test
    fun theStyleIsIndicatorStyle_andMediumWithoutIt() {
        for (style in listOf("small", "medium", "large", "linear")) {
            assertEquals(style, DynamicIndicatorComponent.styleOf(attrs("""{"type":"Indicator","indicatorStyle":"$style"}""")))
        }
        assertEquals("medium", DynamicIndicatorComponent.styleOf(attrs("""{"type":"Indicator"}""")))
    }

    @Test
    fun theLegacySpellingsAreNotRead() {
        // what the normalizer folds away; left unfolded, they draw nothing of their own
        assertEquals("medium", DynamicIndicatorComponent.styleOf(attrs("""{"type":"Indicator","style":"large"}""")))
        assertEquals("medium", DynamicIndicatorComponent.styleOf(attrs("""{"type":"Indicator","style":"linear","size":30}""")))
    }

    @Test
    fun smallAndLargeHaveTheirOwnSize_theCodegensStyleSizes() {
        // kjui_tools indicator_component.rb STYLE_SIZES = { small 16, large 48 }
        assertEquals(16, DynamicIndicatorComponent.styleSizeDp("small"))
        assertEquals(48, DynamicIndicatorComponent.styleSizeDp("large"))
        assertEquals(null, DynamicIndicatorComponent.styleSizeDp("medium"))
        assertEquals(null, DynamicIndicatorComponent.styleSizeDp("linear"))
    }

    /** The component body reads neither legacy key (a source read: the body is a composable). */
    @Test
    fun theComponentReadsNeitherStyleNorSize() {
        val file = File("src/main/kotlin/com/kotlinjsonui/dynamic/components/DynamicIndicatorComponent.kt")
        assertTrue(file.absolutePath, file.isFile)
        val code = file.readLines().filterNot { it.trimStart().let { t -> t.startsWith("//") || t.startsWith("*") } }
            .joinToString("\n")
        assertFalse(code.contains("\"style\""))
        assertFalse(code.contains("\"size\""))
        assertTrue(code.contains("styleOf(a)"))
    }

    // kjui-indicator-style-loses-to-a-declared-wrapcontent: only a length beats
    // the style's size; a wrapContent axis draws it. matchParent is not ruled
    // and keeps its old picture.
    private fun plan(style: String, json: String): DynamicIndicatorComponent.SizePlan {
        val o = JsonParser.parseString(json).asJsonObject
        return DynamicIndicatorComponent.planSize(o, style, o.has("width"), o.has("height"))
    }

    @Test
    fun wrapContentOnBothAxesDrawsTheStyleSize_theConformanceFixtures() {
        for ((style, dp) in listOf("small" to 16, "large" to 48)) {
            val p = plan(style, """{"width":"wrapContent","height":"wrapContent"}""")
            assertEquals(DynamicIndicatorComponent.SizePlan(declared = null, styleBothDp = dp), p)
        }
        assertEquals(48, plan("large", """{"width":"wrap_content","height":"wrap_content"}""").styleBothDp)
        assertEquals(48, plan("large", """{"width":"wrapContent"}""").styleBothDp)
    }

    @Test
    fun mixed_theLengthAxisTakesTheLength_theWrapAxisTheStyleSize() {
        val p = plan("large", """{"width":30,"height":"wrapContent","minWidth":10}""")
        assertEquals(null, p.styleBothDp)
        assertEquals(null, p.styleWidthDp)
        assertEquals(48, p.styleHeightDp)
        val declared = p.declared!!
        assertEquals(30, declared.get("width").asInt)
        assertFalse(declared.has("height"))
        assertTrue("other keys stay for the size builder", declared.has("minWidth"))

        val q = plan("small", """{"width":"wrapContent","height":"@{h}"}""")
        assertEquals(16, q.styleWidthDp)
        assertEquals(null, q.styleHeightDp)
        assertFalse(q.declared!!.has("width"))
    }

    @Test
    fun aLengthOrMatchParentStillWins() {
        val both = plan("large", """{"width":40,"height":40}""")
        assertEquals(DynamicIndicatorComponent.SizePlan(declared = both.declared), both)
        assertEquals(40, both.declared!!.get("height").asInt)
        val fill = plan("large", """{"width":"matchParent"}""")
        assertEquals(DynamicIndicatorComponent.SizePlan(declared = fill.declared), fill)
        assertEquals("matchParent", fill.declared!!.get("width").asString)
    }

    @Test
    fun withoutAStyleSizeWrapContentGoesToTheSizeBuilderAsBefore() {
        for (style in listOf("medium", "linear")) {
            val p = plan(style, """{"width":"wrapContent","height":"wrapContent"}""")
            assertEquals("wrapContent", p.declared!!.get("width").asString)
            assertEquals(null, p.styleBothDp)
        }
        assertEquals(DynamicIndicatorComponent.SizePlan(declared = null), plan("medium", "{}"))
        assertEquals(DynamicIndicatorComponent.SizePlan(declared = null, styleBothDp = 48), plan("large", "{}"))
    }
}
