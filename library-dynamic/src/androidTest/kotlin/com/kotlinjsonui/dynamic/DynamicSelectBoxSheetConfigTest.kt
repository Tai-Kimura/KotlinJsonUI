package com.kotlinjsonui.dynamic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kotlinjsonui.core.Configuration
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented pin for the Configuration-driven SelectBox sheet chrome
 * (2.22.0), exercised through the REAL screen-root entry
 * (RenderDynamicView + an androidTest asset layout — the registered-
 * renderer seam, per the 2.21.8 lesson).
 *
 * Doubles as the dynamic-parity pin: the probe layout declares a RED
 * field background and NO cancel colors, so the cancel row proves which
 * fallback the dynamic face uses — the old `?: backgroundColor` bug
 * would paint it red, the codegen-canon fallback paints it with
 * Configuration's sheet background.
 */
@RunWith(AndroidJUnit4::class)
class DynamicSelectBoxSheetConfigTest {

    @get:Rule
    val rule = createComposeRule()

    private val sheetBg = Color(0xFF2244CC)

    private var savedSheetBg: Color = Color.White
    private var savedSheetText: Color = Color.Black

    @Before
    fun overrideConfiguration() {
        // Configuration is process-global shared state: save + restore.
        savedSheetBg = Configuration.SelectBox.defaultSheetBackgroundColor
        savedSheetText = Configuration.SelectBox.defaultSheetTextColor
        Configuration.SelectBox.defaultSheetBackgroundColor = sheetBg
        Configuration.SelectBox.defaultSheetTextColor = Color.White
    }

    @After
    fun restoreConfiguration() {
        Configuration.SelectBox.defaultSheetBackgroundColor = savedSheetBg
        Configuration.SelectBox.defaultSheetTextColor = savedSheetText
    }

    @Test
    fun sheetChromeFollowsConfigurationThroughRegisteredRendererEntry() {
        rule.setContent {
            RenderDynamicView(layoutName = "selectbox_sheet_probe")
        }

        // The renderer loads the layout asynchronously from assets.
        rule.waitUntil(timeoutMillis = 5_000) {
            rule.onAllNodes(androidx.compose.ui.test.hasTestTag("probe_select"))
                .fetchSemanticsNodes().isNotEmpty()
        }

        rule.onNodeWithTag("probe_select").performClick()
        rule.waitUntil(timeoutMillis = 5_000) {
            rule.onAllNodes(androidx.compose.ui.test.hasTestTag("kjui_x7q_cancel"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        rule.waitForIdle()

        // Option row: Configuration sheet background reaches the sheet body.
        assertCenterPixel("kjui_x7q_option_0", sheetBg)
        // Cancel row: undeclared cancel background falls to the Configuration
        // sheet background (NOT the red field background = the old dynamic
        // divergence).
        assertCenterPixel("kjui_x7q_cancel", sheetBg)
    }

    private fun assertCenterPixel(tag: String, expected: Color) {
        val pixelMap = rule.onNodeWithTag(tag).captureToImage().toPixelMap()
        val actual = pixelMap[pixelMap.width / 2, pixelMap.height / 2]
        assertEquals(
            "center pixel of '$tag'",
            expected.toArgb(),
            actual.toArgb()
        )
    }
}
