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
 * A child of an overlay (a View without orientation) that centres itself on
 * an axis draws at the centre of that axis whatever its margins on it
 * (ticket relative-center-with-margin-diverges-from-declaration).
 *
 * margins.offsetModel: "an explicit center declaration disables that axis
 * margin". The margins are drawn as padding around the declared size, so the
 * Box centred the box with them in it and the child drew half the difference
 * of the two margins off the centre: 45 rows of the family table of
 * 2026-11-10 on Android, none on iOS. A child the container's gravity centres
 * declares no center and is left as it was.
 *
 * The container is 100 x 100 and the child 20 x 20, so the centre is 40.
 */
@RunWith(AndroidJUnit4::class)
class BoxChildCentredWithMarginsTest {

    @get:Rule
    val rule = createComposeRule()

    private fun at(attrs: String, sibling: Boolean = false, frame: String = "", wrap: Boolean = false): Pair<Float, Float> {
        val other = when {
            wrap -> """, {"type": "View", "id": "k", "width": 6, "height": 100, "background": "#000000"}"""
            sibling -> """, {"type": "View", "id": "k", "width": 6, "height": 6, "background": "#000000"}"""
            else -> ""
        }
        val height = if (wrap) "\"wrapContent\"" else "100"
        val json = JsonParser.parseString(
            """{"type": "View", "id": "root", "width": "matchParent", "height": "matchParent", "child": [
                 {"type": "View", "id": "frame", "width": 100, "height": $height, "background": "#DDDDDD"$frame,
                  "child": [{"type": "View", "id": "s", "width": 20, "height": 20, "background": "#FFAA00", $attrs}$other]}
               ]}"""
        ).asJsonObject
        rule.setContent { DynamicView(json = json, data = emptyMap()) }
        rule.waitForIdle()
        val f: DpRect = rule.onNodeWithTag("frame", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val s: DpRect = rule.onNodeWithTag("s", useUnmergedTree = true).getUnclippedBoundsInRoot()
        return (s.left - f.left).value to (s.top - f.top).value
    }

    private fun is_(x: Float, y: Float, actual: Pair<Float, Float>) {
        assertEquals("x", x, actual.first, 0.5f)
        assertEquals("y", y, actual.second, 0.5f)
    }

    // One child (was x 45 / 35 / 30, y 45 / 35 / 30).
    @Test fun centerHorizontalAndLeftMargin() = is_(40f, 0f, at(""""centerHorizontal": true, "leftMargin": 10"""))
    @Test fun centerHorizontalAndRightMargin() = is_(40f, 0f, at(""""centerHorizontal": true, "rightMargin": 10"""))
    @Test fun centerHorizontalAndTwoMargins() = is_(40f, 0f, at(""""centerHorizontal": true, "leftMargin": 10, "rightMargin": 30"""))
    @Test fun centerVerticalAndTopMargin() = is_(0f, 40f, at(""""centerVertical": true, "topMargin": 10"""))
    @Test fun centerVerticalAndTwoMargins() = is_(0f, 40f, at(""""centerVertical": true, "topMargin": 10, "bottomMargin": 30"""))
    @Test fun centerInParentAndBottomMargin() = is_(40f, 40f, at(""""centerInParent": true, "bottomMargin": 10"""))

    // Two children, and a wrapContent-high overlay (was off as above).
    @Test fun withASibling() = is_(40f, 40f, at(""""centerInParent": true, "leftMargin": 10, "topMargin": 10""", sibling = true))
    @Test fun inAWrapContentOverlay() = is_(40f, 40f, at(""""centerInParent": true, "rightMargin": 10, "bottomMargin": 30""", wrap = true))

    // The other axis keeps its full margin, and alignBottom its own.
    @Test fun centerHorizontalAndAlignBottom() =
        is_(40f, 70f, at(""""centerHorizontal": true, "alignBottom": true, "leftMargin": 10, "bottomMargin": 10"""))
    @Test fun centerVerticalAndLeftMargin() = is_(10f, 40f, at(""""centerVertical": true, "leftMargin": 10, "topMargin": 10"""))

    // Equal margins were at the centre already, and stay.
    @Test fun equalMargins() = is_(40f, 40f, at(""""centerInParent": true, "margins": [10, 10, 10, 10]"""))

    // A child the container's gravity centres declares no center: not moved (it draws as before, half off).
    @Test fun aGravityCentredChildIsLeftAsItWas() = is_(45f, 0f, at(""""leftMargin": 10""", frame = """, "gravity": "centerHorizontal""""))
}
