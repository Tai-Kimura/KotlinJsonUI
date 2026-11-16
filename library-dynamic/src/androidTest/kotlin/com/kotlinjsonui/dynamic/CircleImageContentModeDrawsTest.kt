package com.kotlinjsonui.dynamic

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A dynamic CircleImage follows its contentMode, default fit, as Image does
 * and as iOS and web draw it (4f ruling, 2026-09-26, jsonui-cli 1.9.0). It
 * drew ContentScale.Crop for every mode.
 *
 * The image is 2:1 opaque red (res/drawable/content_mode_probe_wide.xml) in a
 * 100x100 circle. Fit draws it 100x50 across the middle; a crop fills the
 * frame. The probe point is inside the circle and above that middle band, so
 * it is red under a crop and not under fit; the centre is red under both
 * (the image drew at all).
 */
@RunWith(AndroidJUnit4::class)
class CircleImageContentModeDrawsTest {
    @get:Rule
    val rule = createComposeRule()

    private fun node(mode: String?): String =
        "{\"type\": \"CircleImage\", \"src\": \"content_mode_probe_wide\", \"width\": 100, \"height\": 100" +
            (mode?.let { ", \"contentMode\": \"$it\"" } ?: "") + "}"

    private fun red(image: ImageBitmap, fx: Float, fy: Float): Boolean {
        val px = image.toPixelMap()
        val c = px[(px.width * fx).toInt(), (px.height * fy).toInt()]
        return c.alpha > 0.9f && c.red > 0.9f && c.green < 0.1f && c.blue < 0.1f
    }

    @Test
    fun noContentModeDrawsFitAndAContentModeIsFollowed() {
        val m = StageMeasurer(rule)
        val errors = mutableListOf<String>()
        m.onError = { errors += it }
        m.start()
        val none = m.capture(node(null))
        val fit = m.capture(node("fit"))
        val crop = m.capture(node("AspectFill"))
        assertEquals("measurements that threw", emptyList<String>(), errors)
        assertNotNull(none); assertNotNull(fit); assertNotNull(crop)

        assertTrue("the image did not draw", red(none!!, 0.5f, 0.5f))
        assertEquals("no contentMode draws otherwise than fit", 0, m.diff(none, fit))
        assertFalse("no contentMode crops", red(none, 0.5f, 0.12f))
        assertTrue("AspectFill does not crop", red(crop!!, 0.5f, 0.12f))
        assertTrue("AspectFill draws as no contentMode does", (m.diff(none, crop) ?: 0) > 0)
    }
}
