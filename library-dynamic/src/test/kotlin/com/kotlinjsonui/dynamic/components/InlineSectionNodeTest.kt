package com.kotlinjsonui.dynamic.components

import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.DebugDiagnostics
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

/**
 * A Collection section's `cell` / `header` / `footer` is declared a string —
 * the name of the layout it draws. A layout that writes a node there instead
 * (an inline cell, not declared) reached `sectionObj.get("cell")?.asString`,
 * and Gson throws on a JsonObject's asString: the whole Collection failed to
 * compose. Every such read goes through sectionViewName, which draws nothing
 * for an inline node and, in a debuggable build, names it once.
 */
class InlineSectionNodeTest {

    private val said = mutableListOf<String>()

    @Before
    fun debuggable() {
        DebugDiagnostics.enabledOverride = true
        DynamicCollectionComponent.inlineSectionSink = { said += it }
    }

    @After
    fun reset() {
        DebugDiagnostics.enabledOverride = null
        DynamicCollectionComponent.inlineSectionSink = null
    }

    private fun section(json: String) = JsonParser.parseString(json).asJsonObject

    @Test
    fun anInlineNodeIsNotDrawnAndIsNamedOnce() {
        val inline = section("""{"cell": {"type": "Label", "text": "x"}, "header": ["h"], "footer": 3}""")
        assertNull(DynamicCollectionComponent.sectionViewName(inline, "cell"))
        assertNull(DynamicCollectionComponent.sectionViewName(inline, "cell"))
        assertNull(DynamicCollectionComponent.sectionViewName(inline, "header"))
        assertEquals("3", DynamicCollectionComponent.sectionViewName(inline, "footer"))
        assertEquals(
            listOf(
                "Collection section cell is a node, not the name of a layout (inline cells are not declared): not drawn",
                "Collection section header is a node, not the name of a layout (inline cells are not declared): not drawn",
            ),
            said,
        )
    }

    @Test
    fun aNamedLayoutIsItsName() {
        assertEquals("RowCell", DynamicCollectionComponent.sectionViewName(section("""{"cell": "RowCell"}"""), "cell"))
        assertNull(DynamicCollectionComponent.sectionViewName(section("""{}"""), "cell"))
        assertNull(DynamicCollectionComponent.sectionViewName(section("""{"cell": null}"""), "cell"))
        assertEquals(emptyList<String>(), said)
    }

    /** TabView reads a tab's `view` (a name) and never its `child`: an inline child is not drawn, and nothing throws. */
    @Test
    fun aTabsInlineChildIsNotReadAndDoesNotThrow() {
        val tabs = JsonParser.parseString(
            """[{"title": "A", "child": [{"type": "Label", "text": "inline"}]}, {"title": "B", "view": "b_screen"}]"""
        ).asJsonArray
        val items = DynamicTabViewComponent.tabItemsOf(tabs)
        assertEquals(listOf(null, "b_screen"), items.map { it.view })
        assertEquals(listOf("A", "B"), items.map { it.title })
    }
}
