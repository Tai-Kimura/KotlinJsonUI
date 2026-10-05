package com.kotlinjsonui.dynamic

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * User rulings of 2026-10-05 on layouts the SSoT had not declared, as drawn
 * by the Dynamic renderer (the kjui codegen arms are in jsonui-cli
 * oversized_child_placement_spec.rb and text_component_spec.rb).
 */
@RunWith(AndroidJUnit4::class)
class LayoutRulingsTest {

    @get:Rule
    val rule = createComposeRule()

    private fun show(layout: String) {
        rule.setContent {
            DynamicView(json = JsonParser.parseString(layout).asJsonObject, data = emptyMap())
        }
        rule.waitForIdle()
    }

    /** (x, y, width, height) of a tagged node relative to another, in dp, rounded. */
    private fun box(tag: String, relativeTo: String): List<Int> {
        val d = rule.density.density
        val o = rule.onNodeWithTag(relativeTo).fetchSemanticsNode().positionInRoot
        val n = rule.onNodeWithTag(tag).fetchSemanticsNode()
        return listOf(
            Math.round((n.positionInRoot.x - o.x) / d), Math.round((n.positionInRoot.y - o.y) / d),
            Math.round(n.size.width / d), Math.round(n.size.height / d)
        )
    }

    // fillEqually: a child that declares its width keeps it and takes no
    // share; the others split what is left. Was 60 drawn in a 100 slot.
    @Test
    fun fillEquallySplitsWhatTheDeclaredChildLeaves() {
        show("""{"type":"View","id":"root","width":"matchParent","height":"matchParent","child":[
                 {"type":"View","id":"row","width":300,"height":200,"orientation":"horizontal","distribution":"fillEqually","child":[
                   {"type":"View","id":"a","width":60,"height":40},
                   {"type":"View","id":"b","height":40},
                   {"type":"View","id":"c","height":40}]}]}""")
        assertEquals(listOf(0, 0, 60, 40), box("a", "row"))
        assertEquals(listOf(60, 0, 120, 40), box("b", "row"))
        assertEquals(listOf(180, 0, 120, 40), box("c", "row"))
    }

    // bottomToTop stacks from the bottom edge, the first child at the bottom;
    // six 40s in 200 overflow at the top. Was reversed and stacked from the top.
    @Test
    fun bottomToTopStacksFromTheBottomEdge() {
        val boxes = (0 until 6).joinToString(",") { """{"type":"View","id":"b$it","width":40,"height":40}""" }
        show("""{"type":"View","id":"root","width":"matchParent","height":"matchParent","child":[
                 {"type":"View","id":"col","width":200,"height":200,"orientation":"vertical","direction":"bottomToTop","child":[$boxes]}]}""")
        assertEquals(listOf(160, 120, 80, 40, 0, -40), (0 until 6).map { box("b$it", "col")[1] })
    }

    @Test
    fun bottomToTopThatFitsSitsAtTheBottom() {
        val boxes = (0 until 2).joinToString(",") { """{"type":"View","id":"b$it","width":40,"height":40}""" }
        show("""{"type":"View","id":"root","width":"matchParent","height":"matchParent","child":[
                 {"type":"View","id":"col","width":200,"height":200,"orientation":"vertical","direction":"bottomToTop","child":[$boxes]}]}""")
        assertEquals(listOf(160, 120), (0 until 2).map { box("b$it", "col")[1] })
    }

    // rightToLeft stacks a Row from its right edge, the first child rightmost
    // (bottomToTop's ruling turned sideways). Was reversed and stacked from
    // the left.
    @Test
    fun rightToLeftStacksFromTheRightEdge() {
        val boxes = (0 until 6).joinToString(",") { """{"type":"View","id":"b$it","width":40,"height":40}""" }
        show("""{"type":"View","id":"root","width":"matchParent","height":"matchParent","child":[
                 {"type":"View","id":"row","width":200,"height":200,"orientation":"horizontal","direction":"rightToLeft","child":[$boxes]}]}""")
        assertEquals(listOf(160, 120, 80, 40, 0, -40), (0 until 6).map { box("b$it", "row")[0] })
    }

    // A SafeAreaView stacks the same way: bottomToTop from its bottom edge,
    // rightToLeft from its right edge. It reversed the children and stacked
    // them from the top / left. Only the top inset is reserved, so the bottom
    // and both sides are the SafeAreaView's own edges whatever the device's
    // insets are.
    @Test
    fun aSafeAreaViewBottomToTopStacksFromItsBottomEdge() {
        val boxes = (0 until 3).joinToString(",") { """{"type":"View","id":"b$it","width":40,"height":40}""" }
        show("""{"type":"View","id":"root","width":"matchParent","height":"matchParent","child":[
                 {"type":"SafeAreaView","id":"sa","width":200,"height":200,"safeAreaInsetPositions":["top"],"orientation":"vertical","direction":"bottomToTop","child":[$boxes]}]}""")
        assertEquals(listOf(160, 120, 80), (0 until 3).map { box("b$it", "sa")[1] })
    }

    @Test
    fun aSafeAreaViewRightToLeftStacksFromItsRightEdge() {
        val boxes = (0 until 3).joinToString(",") { """{"type":"View","id":"b$it","width":40,"height":40}""" }
        show("""{"type":"View","id":"root","width":"matchParent","height":"matchParent","child":[
                 {"type":"SafeAreaView","id":"sa","width":200,"height":200,"safeAreaInsetPositions":["top"],"orientation":"horizontal","direction":"rightToLeft","child":[$boxes]}]}""")
        assertEquals(listOf(160, 120, 80), (0 until 3).map { box("b$it", "sa")[0] })
    }

    // A hint with no hintAttributes is shown (default placeholder colour).
    // Through 2.43.4 every face required both and the label drew nothing.
    @Test
    fun aHintWithoutAttributesIsShown() {
        show("""{"type":"View","id":"root","width":"matchParent","height":"matchParent","child":[
                 {"type":"Label","id":"l","width":200,"hint":"Conformance Hint"}]}""")
        rule.onNodeWithTag("l").assertTextEquals("Conformance Hint")
    }
}
