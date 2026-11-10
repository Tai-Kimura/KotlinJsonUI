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
 * A child of a container with a sibling constraint draws its margin once
 * (ticket kjui-dynamic-relative-container-bottom-margin-applied-twice).
 *
 * The child's margins are drawn as padding around its declared size, so its
 * ref box already holds them. A parent alignment (alignTop / Bottom / Left /
 * Right) and a facing link (align*OfView) added the margin again and drew it
 * twice; an edge alignment (align*View) took it away and drew none. A centred
 * axis drew half a margin off, where margins.offsetModel says a center
 * declaration disables that axis' margin (ticket
 * relative-center-with-margin-diverges-from-declaration).
 *
 * The container is 100 x 100 and every margin is 10. The expected positions
 * are the declaration's (margins.offsetModel), which iOS and web drew in the
 * family table of 2026-11-10; Android drew the values in each test's name
 * comment before the fix.
 */
@RunWith(AndroidJUnit4::class)
class RelativeChildMarginIsDrawnOnceTest {

    @get:Rule
    val rule = createComposeRule()

    private fun bounds(children: String, id: String): Pair<Float, Float> {
        val json = JsonParser.parseString(
            """{"type": "View", "id": "root", "width": "matchParent", "height": "matchParent", "child": [
                 {"type": "View", "id": "frame", "width": 100, "height": 100, "background": "#DDDDDD",
                  "child": [$children]}
               ]}"""
        ).asJsonObject
        rule.setContent { DynamicView(json = json, data = emptyMap()) }
        rule.waitForIdle()
        val frame: DpRect = rule.onNodeWithTag("frame", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val box: DpRect = rule.onNodeWithTag(id, useUnmergedTree = true).getUnclippedBoundsInRoot()
        return (box.left - frame.left).value to (box.top - frame.top).value
    }

    /** `s` (20 x 20, [attrs]) with a sibling constrained to it, which makes the container relative. */
    private fun free(attrs: String) = bounds(
        """{"type": "View", "id": "s", "width": 20, "height": 20, "background": "#FFAA00", $attrs},
           {"type": "View", "id": "k", "width": 6, "height": 6, "background": "#000000", "alignLeftOfView": "s"}""",
        "s"
    )

    /** `t` (10 x 10, [attrs]) constrained to `a` (20 x 20, centred: 40..60 on both axes). */
    private fun tied(attrs: String) = bounds(
        """{"type": "View", "id": "a", "width": 20, "height": 20, "background": "#0000FF", "centerInParent": true},
           {"type": "View", "id": "t", "width": 10, "height": 10, "background": "#FF0000", $attrs}""",
        "t"
    )

    private fun at(x: Float, y: Float, actual: Pair<Float, Float>) {
        assertEquals("x", x, actual.first, 0.5f)
        assertEquals("y", y, actual.second, 0.5f)
    }

    // A parent alignment and the margin on that side: the margin once (was twice).
    @Test fun alignTopAndTopMargin() = at(0f, 10f, free(""""alignTop": true, "topMargin": 10"""))          // was y 20
    @Test fun alignBottomAndBottomMargin() = at(0f, 70f, free(""""alignBottom": true, "bottomMargin": 10"""))  // was y 60
    @Test fun alignLeftAndLeftMargin() = at(10f, 0f, free(""""alignLeft": true, "leftMargin": 10"""))      // was x 20
    @Test fun alignRightAndRightMargin() = at(70f, 0f, free(""""alignRight": true, "rightMargin": 10"""))   // was x 60
    @Test fun alignBottomRightAndBottomMargin() =
        at(80f, 70f, free(""""alignBottom": true, "alignRight": true, "bottomMargin": 10"""))              // was y 60

    // The margin on the other axis was right, and stays.
    @Test fun alignBottomAndLeftMargin() = at(10f, 80f, free(""""alignBottom": true, "leftMargin": 10"""))

    // A centred axis takes no margin (was half a margin off).
    @Test fun centerHorizontalAndLeftMargin() = at(40f, 0f, free(""""centerHorizontal": true, "leftMargin": 10"""))   // was x 45
    @Test fun centerHorizontalAndRightMargin() = at(40f, 0f, free(""""centerHorizontal": true, "rightMargin": 10"""))  // was x 35
    @Test fun centerVerticalAndTopMargin() = at(0f, 40f, free(""""centerVertical": true, "topMargin": 10"""))          // was y 45
    @Test fun centerVerticalAndBottomMargin() = at(0f, 40f, free(""""centerVertical": true, "bottomMargin": 10"""))    // was y 35

    // A facing link: the margin once, away from the anchor (was twice).
    @Test fun alignTopOfViewAndBottomMargin() = at(0f, 20f, tied(""""alignTopOfView": "a", "bottomMargin": 10"""))     // was y 10
    @Test fun alignBottomOfViewAndTopMargin() = at(0f, 70f, tied(""""alignBottomOfView": "a", "topMargin": 10"""))     // was y 80
    @Test fun alignLeftOfViewAndRightMargin() = at(20f, 0f, tied(""""alignLeftOfView": "a", "rightMargin": 10"""))     // was x 10
    @Test fun alignRightOfViewAndLeftMargin() = at(70f, 0f, tied(""""alignRightOfView": "a", "leftMargin": 10"""))     // was x 80

    // An edge alignment: the margin inside the anchor's edge (was none).
    @Test fun alignTopViewAndTopMargin() = at(0f, 50f, tied(""""alignTopView": "a", "topMargin": 10"""))               // was y 40
    @Test fun alignBottomViewAndBottomMargin() = at(0f, 40f, tied(""""alignBottomView": "a", "bottomMargin": 10"""))   // was y 50
    @Test fun alignLeftViewAndLeftMargin() = at(50f, 0f, tied(""""alignLeftView": "a", "leftMargin": 10"""))           // was x 40
    @Test fun alignRightViewAndRightMargin() = at(40f, 0f, tied(""""alignRightView": "a", "rightMargin": 10"""))       // was x 50
}
