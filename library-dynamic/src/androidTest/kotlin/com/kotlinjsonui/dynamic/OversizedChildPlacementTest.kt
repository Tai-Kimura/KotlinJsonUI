package com.kotlinjsonui.dynamic

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A child declaring a size larger than its View sits where the View places
 * it — at its corner by default, overflowing both sides when centred, the
 * start side when at the end — as web draws it (flex), instead of being
 * centred by Compose's coercion of requiredWidth/requiredHeight
 * (jsonui-cli ticket kjui-oversized-child-is-centred-and-cut-to-its-parent:
 * a 300 child of a 200 Box read (−50, −50) whatever the gravity).
 *
 * Positions are the tagged node's, relative to the container's, in dp.
 */
@RunWith(AndroidJUnit4::class)
class OversizedChildPlacementTest {

    @get:Rule
    val rule = createComposeRule()

    private fun place(container: String, child: String): Pair<Float, Float> {
        rule.setContent {
            DynamicView(
                json = JsonParser.parseString(
                    """{"type":"View","id":"root","width":"matchParent","height":"matchParent","child":[
                         {"type":"View","id":"box","width":200,"height":200 $container,"child":[
                           {"type":"View","id":"kid","background":"#FF0000" $child}]}]}"""
                ).asJsonObject,
                data = emptyMap()
            )
        }
        rule.waitForIdle()
        val density = rule.density.density
        val box = rule.onNodeWithTag("box").fetchSemanticsNode().positionInRoot
        val kid = rule.onNodeWithTag("kid").fetchSemanticsNode().positionInRoot
        return ((kid.x - box.x) / density) to ((kid.y - box.y) / density)
    }

    private fun assertAt(x: Float, y: Float, at: Pair<Float, Float>) {
        assertEquals("x of $at", x, at.first, 0.5f)
        assertEquals("y of $at", y, at.second, 0.5f)
    }

    // ── Box (no orientation): its contentAlignment, or the child's own ──

    @Test
    fun aBoxPutsAnOversizedChildAtItsCorner() =
        assertAt(0f, 0f, place("", ""","width":300,"height":300"""))

    @Test
    fun aCentredBoxOverflowsBothSides() =
        assertAt(-50f, -50f, place(""","gravity":"center"""", ""","width":300,"height":300"""))

    @Test
    fun anEndBoxOverflowsTheStartSide() =
        assertAt(-100f, -100f, place(""","gravity":["right","bottom"]""", ""","width":300,"height":300"""))

    @Test
    fun aChildsOwnPlacementWinsInABox() =
        assertAt(-100f, 0f, place("", ""","width":300,"height":300,"alignRight":true"""))

    @Test
    fun aChildThatFitsIsPlacedAsBefore() =
        assertAt(50f, 50f, place(""","gravity":"center"""", ""","width":100,"height":100"""))

    // ── Column / Row: the cross axis follows the container's alignment ──

    @Test
    fun aCentredColumnOverflowsAWideChildBothSides() =
        assertAt(-50f, 0f, place(""","orientation":"vertical","gravity":"centerHorizontal"""", ""","width":300,"height":50"""))

    @Test
    fun aColumnChildAlignedRightOverflowsTheStartSide() =
        assertAt(-100f, 0f, place(""","orientation":"vertical"""", ""","width":300,"height":50,"alignRight":true"""))

    @Test
    fun aCentredRowOverflowsATallChildBothSides() =
        assertAt(0f, -50f, place(""","orientation":"horizontal","gravity":"centerVertical"""", ""","width":50,"height":300"""))

    @Test
    fun aRowChildAlignedBottomOverflowsTheTopSide() =
        assertAt(0f, -100f, place(""","orientation":"horizontal"""", ""","width":50,"height":300,"alignBottom":true"""))
    // ── Ruling S: a fixed-size row's children in sequence past its edge ──

    private fun rowOfSix(container: String): List<Float> {
        val boxes = (0 until 6).joinToString(",") { """{"type":"View","id":"b$it","width":40,"height":40}""" }
        rule.setContent {
            DynamicView(
                json = JsonParser.parseString(
                    """{"type":"View","id":"root","width":"matchParent","height":"matchParent","child":[
                         {"type":"View","id":"row","width":200,"height":200,"orientation":"horizontal" $container,"child":[$boxes]}]}"""
                ).asJsonObject,
                data = emptyMap()
            )
        }
        rule.waitForIdle()
        val density = rule.density.density
        val row = rule.onNodeWithTag("row").fetchSemanticsNode().positionInRoot.x
        return (0 until 6).map { Math.round((rule.onNodeWithTag("b$it").fetchSemanticsNode().positionInRoot.x - row) / density).toFloat() }
    }

    @Test
    fun aPaddedRowPutsItsChildrenInSequencePastTheEdge() =
        assertEquals(listOf(8f, 48f, 88f, 128f, 168f, 208f), rowOfSix(""","padding":8"""))

    @Test
    fun aRowWithSpacingPutsItsChildrenInSequencePastTheEdge() =
        assertEquals(listOf(0f, 48f, 96f, 144f, 192f, 240f), rowOfSix(""","spacing":8"""))

    @Test
    fun aRowThatFitsIsArrangedAsBefore() {
        val boxes = (0 until 3).joinToString(",") { """{"type":"View","id":"b$it","width":40,"height":40}""" }
        rule.setContent {
            DynamicView(
                json = JsonParser.parseString(
                    """{"type":"View","id":"root","width":"matchParent","height":"matchParent","child":[
                         {"type":"View","id":"row","width":200,"height":200,"orientation":"horizontal","gravity":"centerHorizontal","child":[$boxes]}]}"""
                ).asJsonObject,
                data = emptyMap()
            )
        }
        rule.waitForIdle()
        val density = rule.density.density
        val row = rule.onNodeWithTag("row").fetchSemanticsNode().positionInRoot.x
        assertEquals(40f, (rule.onNodeWithTag("b0").fetchSemanticsNode().positionInRoot.x - row) / density, 0.5f)
    }
}
