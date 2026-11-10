package com.kotlinjsonui.dynamic

import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.DpRect
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A constraint that names a gone element does not apply (attribute_semantics
 * goneModel, 2026-11-10; ticket relative-constraint-to-gone-anchor-keeps-its-place):
 * "gone takes no size and no position: the element is laid out as if it were
 * absent, and a constraint that names a gone element does not apply — the
 * constrained element is placed as it would be without that constraint."
 *
 * The anchor `a` (20 x 20, centred: 40..60 in a 100 x 100 container) is gone,
 * literally or through a binding. `t` (10 x 10) was linked to the box the
 * anchor's own constraints still placed, as if it were there: at y 50 for
 * alignTopView + topMargin 10, where the ruling places it at y 10.
 */
@RunWith(AndroidJUnit4::class)
class RelativeConstraintToAGoneAnchorTest {

    @get:Rule
    val rule = createComposeRule()

    private fun at(constraint: String, visibility: String, data: Map<String, Any> = emptyMap()): Pair<Float, Float> {
        val json = JsonParser.parseString(
            """{"type": "View", "id": "root", "width": "matchParent", "height": "matchParent", "child": [
                 {"type": "View", "id": "frame", "width": 100, "height": 100, "background": "#DDDDDD", "child": [
                   {"type": "View", "id": "a", "width": 20, "height": 20, "background": "#0000FF",
                    "centerInParent": true, "visibility": "$visibility"},
                   {"type": "View", "id": "t", "width": 10, "height": 10, "background": "#FF0000", $constraint}
                 ]}
               ]}"""
        ).asJsonObject
        rule.setContent { DynamicView(json = json, data = data) }
        rule.waitForIdle()
        val f: DpRect = rule.onNodeWithTag("frame", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val t: DpRect = rule.onNodeWithTag("t", useUnmergedTree = true).getUnclippedBoundsInRoot()
        return (t.left - f.left).value to (t.top - f.top).value
    }

    private fun is_(x: Float, y: Float, actual: Pair<Float, Float>) {
        assertEquals("x", x, actual.first, 0.5f)
        assertEquals("y", y, actual.second, 0.5f)
    }

    @Test fun alignTopViewToAGoneAnchor() = is_(0f, 10f, at(""""alignTopView": "a", "topMargin": 10""", "gone"))       // was y 50
    @Test fun alignBottomOfViewToAGoneAnchor() = is_(0f, 0f, at(""""alignBottomOfView": "a"""", "gone"))                // was y 60
    @Test fun alignTopOfViewToAGoneAnchor() = is_(0f, 0f, at(""""alignTopOfView": "a", "bottomMargin": 10""", "gone"))  // was y 20
    @Test fun alignRightOfViewToAGoneAnchor() = is_(10f, 0f, at(""""alignRightOfView": "a", "leftMargin": 10""", "gone")) // was x 70
    @Test fun alignCenterHorizontalViewToAGoneAnchor() = is_(0f, 0f, at(""""alignCenterHorizontalView": "a"""", "gone")) // was x 45

    // Through a binding: gone does not apply the constraint, visible does.
    @Test fun aBoundGoneAnchor() =
        is_(0f, 10f, at(""""alignTopView": "a", "topMargin": 10""", "@{aVis}", mapOf("aVis" to "gone")))
    @Test fun aBoundVisibleAnchorStillHolds() =
        is_(0f, 50f, at(""""alignTopView": "a", "topMargin": 10""", "@{aVis}", mapOf("aVis" to "visible")))

    // invisible keeps its size and position: the constraint applies.
    @Test fun anInvisibleAnchorStillHolds() = is_(0f, 50f, at(""""alignTopView": "a", "topMargin": 10""", "invisible"))
}
