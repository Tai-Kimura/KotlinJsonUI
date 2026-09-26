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
}
