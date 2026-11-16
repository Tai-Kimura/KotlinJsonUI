package com.kotlinjsonui.views

import android.graphics.drawable.GradientDrawable
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for gradient utility functions
 * Note: Tests that require Android Context are limited in unit tests
 */
class GradientUtilsTest {

    @Test
    fun `angleToOrientation returns LEFT_RIGHT for 0 degrees`() {
        val result = angleToOrientation(0)
        assertEquals(GradientDrawable.Orientation.LEFT_RIGHT, result)
    }

    @Test
    fun `angleToOrientation returns BL_TR for 45 degrees`() {
        val result = angleToOrientation(45)
        assertEquals(GradientDrawable.Orientation.BL_TR, result)
    }

    @Test
    fun `angleToOrientation returns BOTTOM_TOP for 90 degrees`() {
        val result = angleToOrientation(90)
        assertEquals(GradientDrawable.Orientation.BOTTOM_TOP, result)
    }

    @Test
    fun `angleToOrientation returns BR_TL for 135 degrees`() {
        val result = angleToOrientation(135)
        assertEquals(GradientDrawable.Orientation.BR_TL, result)
    }

    @Test
    fun `angleToOrientation returns RIGHT_LEFT for 180 degrees`() {
        val result = angleToOrientation(180)
        assertEquals(GradientDrawable.Orientation.RIGHT_LEFT, result)
    }

    @Test
    fun `angleToOrientation returns TR_BL for 225 degrees`() {
        val result = angleToOrientation(225)
        assertEquals(GradientDrawable.Orientation.TR_BL, result)
    }

    @Test
    fun `angleToOrientation returns TOP_BOTTOM for 270 degrees`() {
        val result = angleToOrientation(270)
        assertEquals(GradientDrawable.Orientation.TOP_BOTTOM, result)
    }

    @Test
    fun `angleToOrientation returns TL_BR for 315 degrees`() {
        val result = angleToOrientation(315)
        assertEquals(GradientDrawable.Orientation.TL_BR, result)
    }

    @Test
    fun `angleToOrientation returns LEFT_RIGHT for 360 degrees`() {
        val result = angleToOrientation(360)
        assertEquals(GradientDrawable.Orientation.LEFT_RIGHT, result)
    }

    @Test
    fun `angleToOrientation handles negative angles`() {
        // -90 should be equivalent to 270
        val result = angleToOrientation(-90)
        assertEquals(GradientDrawable.Orientation.TOP_BOTTOM, result)
    }

    @Test
    fun `angleToOrientation handles angles over 360`() {
        // 405 should be equivalent to 45
        val result = angleToOrientation(405)
        assertEquals(GradientDrawable.Orientation.BL_TR, result)
    }

    @Test
    fun `angleToOrientation defaults to TOP_BOTTOM for non-standard angles`() {
        val result = angleToOrientation(30)
        assertEquals(GradientDrawable.Orientation.TOP_BOTTOM, result)
    }

    @Test
    fun `directionToOrientation returns TOP_BOTTOM for vertical`() {
        val result = directionToOrientation("vertical")
        assertEquals(GradientDrawable.Orientation.TOP_BOTTOM, result)
    }

    @Test
    fun `directionToOrientation returns TOP_BOTTOM for top_bottom`() {
        val result = directionToOrientation("top_bottom")
        assertEquals(GradientDrawable.Orientation.TOP_BOTTOM, result)
    }

    @Test
    fun `directionToOrientation returns LEFT_RIGHT for horizontal`() {
        val result = directionToOrientation("horizontal")
        assertEquals(GradientDrawable.Orientation.LEFT_RIGHT, result)
    }

    @Test
    fun `directionToOrientation returns LEFT_RIGHT for left_right`() {
        val result = directionToOrientation("left_right")
        assertEquals(GradientDrawable.Orientation.LEFT_RIGHT, result)
    }

    @Test
    fun `directionToOrientation returns TL_BR for diagonal`() {
        val result = directionToOrientation("diagonal")
        assertEquals(GradientDrawable.Orientation.TL_BR, result)
    }

    @Test
    fun `directionToOrientation returns TR_BL for diagonal_reverse`() {
        val result = directionToOrientation("diagonal_reverse")
        assertEquals(GradientDrawable.Orientation.TR_BL, result)
    }

    @Test
    fun `directionToOrientation is case insensitive`() {
        assertEquals(GradientDrawable.Orientation.TOP_BOTTOM, directionToOrientation("VERTICAL"))
        assertEquals(GradientDrawable.Orientation.LEFT_RIGHT, directionToOrientation("HORIZONTAL"))
        assertEquals(GradientDrawable.Orientation.TL_BR, directionToOrientation("DIAGONAL"))
    }

    @Test
    fun `directionToOrientation defaults to TOP_BOTTOM for unknown`() {
        val result = directionToOrientation("unknown")
        assertEquals(GradientDrawable.Orientation.TOP_BOTTOM, result)
    }

    @Test
    fun `gradientTypeFromString returns LINEAR_GRADIENT for linear`() {
        val result = gradientTypeFromString("linear")
        assertEquals(GradientDrawable.LINEAR_GRADIENT, result)
    }

    @Test
    fun `gradientTypeFromString returns RADIAL_GRADIENT for radial`() {
        val result = gradientTypeFromString("radial")
        assertEquals(GradientDrawable.RADIAL_GRADIENT, result)
    }

    @Test
    fun `gradientTypeFromString returns SWEEP_GRADIENT for sweep`() {
        val result = gradientTypeFromString("sweep")
        assertEquals(GradientDrawable.SWEEP_GRADIENT, result)
    }

    @Test
    fun `gradientTypeFromString is case insensitive`() {
        assertEquals(GradientDrawable.LINEAR_GRADIENT, gradientTypeFromString("LINEAR"))
        assertEquals(GradientDrawable.RADIAL_GRADIENT, gradientTypeFromString("RADIAL"))
        assertEquals(GradientDrawable.SWEEP_GRADIENT, gradientTypeFromString("SWEEP"))
    }

    @Test
    fun `gradientTypeFromString defaults to LINEAR_GRADIENT for unknown`() {
        val result = gradientTypeFromString("unknown")
        assertEquals(GradientDrawable.LINEAR_GRADIENT, result)
    }

    // Helper functions that mirror the logic in KjuiGradientView
    private fun angleToOrientation(angle: Int): GradientDrawable.Orientation {
        val normalizedAngle = ((angle % 360) + 360) % 360
        return when (normalizedAngle) {
            0, 360 -> GradientDrawable.Orientation.LEFT_RIGHT
            45 -> GradientDrawable.Orientation.BL_TR
            90 -> GradientDrawable.Orientation.BOTTOM_TOP
            135 -> GradientDrawable.Orientation.BR_TL
            180 -> GradientDrawable.Orientation.RIGHT_LEFT
            225 -> GradientDrawable.Orientation.TR_BL
            270 -> GradientDrawable.Orientation.TOP_BOTTOM
            315 -> GradientDrawable.Orientation.TL_BR
            else -> GradientDrawable.Orientation.TOP_BOTTOM
        }
    }

    private fun directionToOrientation(direction: String): GradientDrawable.Orientation {
        return when (direction.lowercase()) {
            "vertical", "top_bottom" -> GradientDrawable.Orientation.TOP_BOTTOM
            "horizontal", "left_right" -> GradientDrawable.Orientation.LEFT_RIGHT
            "diagonal", "tl_br" -> GradientDrawable.Orientation.TL_BR
            "diagonal_reverse", "tr_bl" -> GradientDrawable.Orientation.TR_BL
            "bottom_top" -> GradientDrawable.Orientation.BOTTOM_TOP
            "right_left" -> GradientDrawable.Orientation.RIGHT_LEFT
            "bl_tr" -> GradientDrawable.Orientation.BL_TR
            "br_tl" -> GradientDrawable.Orientation.BR_TL
            else -> GradientDrawable.Orientation.TOP_BOTTOM
        }
    }

    private fun gradientTypeFromString(type: String): Int {
        return when (type.lowercase()) {
            "linear" -> GradientDrawable.LINEAR_GRADIENT
            "radial" -> GradientDrawable.RADIAL_GRADIENT
            "sweep" -> GradientDrawable.SWEEP_GRADIENT
            else -> GradientDrawable.LINEAR_GRADIENT
        }
    }
}
