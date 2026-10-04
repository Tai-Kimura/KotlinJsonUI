package com.kotlinjsonui.dynamic

import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpRect
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A view aligned to an anchor meets the box the anchor DRAWS (ticket
 * kjui-relative-align-view-measures-the-anchor-with-its-margin). The anchor
 * of the conformance fixtures declares topMargin / leftMargin 120 and is
 * 50x50, so it draws at 120..170 on both axes. Until KotlinJsonUI 2.43.4 its
 * ref box included the margin (0..170), and a target aligned to its top, left
 * or centre — or placed above or left of it — landed at 0 / 85 while iOS and
 * web placed it where the declaration says.
 *
 * The expected edges come from the declaration, not from a render.
 */
@RunWith(AndroidJUnit4::class)
class RelativeAlignToMarginedAnchorTest {

    @get:Rule
    val rule = createComposeRule()

    private fun target(attr: String): DpRect {
        val json = JsonParser.parseString(
            """{"type": "View", "id": "root", "width": "matchParent", "height": "matchParent", "child": [
                 {"type": "View", "id": "anchor", "width": 50, "height": 50, "background": "#CCCCCC",
                  "topMargin": 120, "leftMargin": 120},
                 {"type": "View", "id": "target", "width": 50, "height": 50, "background": "#DDDDDD",
                  "$attr": "anchor"}
               ]}"""
        ).asJsonObject
        rule.setContent { DynamicView(json = json, data = emptyMap()) }
        rule.waitForIdle()
        return rule.onNodeWithTag("target", useUnmergedTree = true).getUnclippedBoundsInRoot()
    }

    private fun near(expected: Float, actual: Dp, what: String) =
        assertEquals(what, expected, actual.value, 0.5f)

    @Test fun alignTopView() = target("alignTopView").let { near(120f, it.top, "top = the anchor's drawn top") }
    @Test fun alignBottomView() = target("alignBottomView").let { near(170f, it.bottom, "bottom = the anchor's drawn bottom") }
    @Test fun alignLeftView() = target("alignLeftView").let { near(120f, it.left, "left = the anchor's drawn left") }
    @Test fun alignRightView() = target("alignRightView").let { near(170f, it.right, "right = the anchor's drawn right") }
    @Test fun alignCenterVerticalView() = target("alignCenterVerticalView").let {
        near(145f, (it.top + it.bottom) / 2, "centre y = the anchor's drawn centre")
    }
    @Test fun alignCenterHorizontalView() = target("alignCenterHorizontalView").let {
        near(145f, (it.left + it.right) / 2, "centre x = the anchor's drawn centre")
    }
    @Test fun alignTopOfView() = target("alignTopOfView").let { near(120f, it.bottom, "bottom = the anchor's drawn top") }
    @Test fun alignBottomOfView() = target("alignBottomOfView").let { near(170f, it.top, "top = the anchor's drawn bottom") }
    @Test fun alignLeftOfView() = target("alignLeftOfView").let { near(120f, it.right, "right = the anchor's drawn left") }
    @Test fun alignRightOfView() = target("alignRightOfView").let { near(170f, it.left, "left = the anchor's drawn right") }
}
