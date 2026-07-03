package com.kotlinjsonui.graphics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StackBlurTest {

    private fun argb(a: Int, r: Int, g: Int, b: Int): Int =
        (a shl 24) or (r shl 16) or (g shl 8) or b

    // ── invariants ──

    @Test
    fun uniformImageStaysUniform() {
        val color = argb(255, 120, 80, 40)
        val pixels = IntArray(8 * 8) { color }
        StackBlur.blur(pixels, 8, 8, 4)
        pixels.forEach { assertEquals(color, it) }
    }

    @Test
    fun singleBrightPixelSpreadsToNeighbors() {
        val w = 9; val h = 9
        val pixels = IntArray(w * h) { argb(255, 0, 0, 0) }
        pixels[4 * w + 4] = argb(255, 255, 255, 255)
        StackBlur.blur(pixels, w, h, 3)
        // Center loses energy, direct neighbor gains some
        val center = pixels[4 * w + 4]
        val neighbor = pixels[4 * w + 5]
        assertTrue("center red should drop below 255", (center shr 16 and 0xff) < 255)
        assertTrue("neighbor red should rise above 0", (neighbor shr 16 and 0xff) > 0)
    }

    @Test
    fun blurredImageIsSmootherThanInput() {
        // Hard vertical edge: left black, right white
        val w = 16; val h = 4
        val pixels = IntArray(w * h) { i -> if (i % w < w / 2) argb(255, 0, 0, 0) else argb(255, 255, 255, 255) }
        StackBlur.blur(pixels, w, h, 4)
        // The former hard step at the boundary must now be gradual:
        // pixel just left of the boundary is no longer pure black.
        val justLeft = pixels[w / 2 - 1] shr 16 and 0xff
        assertTrue("edge should be softened, got $justLeft", justLeft in 1..254)
    }

    @Test
    fun alphaChannelIsBlurredNotDropped() {
        val w = 8; val h = 8
        val pixels = IntArray(w * h) { argb(0, 0, 0, 0) }
        pixels[3 * w + 3] = argb(255, 255, 0, 0)
        StackBlur.blur(pixels, w, h, 2)
        val neighborAlpha = pixels[3 * w + 4] ushr 24 and 0xff
        assertTrue("alpha should spread with the blur", neighborAlpha > 0)
    }

    // ── edge cases (must not crash) ──

    @Test
    fun radiusZeroIsNoOp() {
        val original = IntArray(4 * 4) { argb(255, it * 10, 0, 0) }
        val pixels = original.copyOf()
        StackBlur.blur(pixels, 4, 4, 0)
        assertTrue(original.contentEquals(pixels))
    }

    @Test
    fun onePixelImage() {
        val pixels = intArrayOf(argb(255, 200, 100, 50))
        StackBlur.blur(pixels, 1, 1, 5)
        assertEquals(argb(255, 200, 100, 50), pixels[0])
    }

    @Test
    fun radiusLargerThanImage() {
        val color = argb(255, 10, 20, 30)
        val pixels = IntArray(3 * 3) { color }
        StackBlur.blur(pixels, 3, 3, 25)
        pixels.forEach { assertEquals(color, it) }
    }

    @Test
    fun singleRowAndSingleColumn() {
        val row = IntArray(10) { argb(255, it * 25, 0, 0) }
        StackBlur.blur(row, 10, 1, 3)
        val col = IntArray(10) { argb(255, 0, it * 25, 0) }
        StackBlur.blur(col, 1, 10, 3)
        // no crash; values stay in byte range
        (row + col).forEach { p ->
            assertTrue((p shr 16 and 0xff) in 0..255)
        }
    }

    @Test
    fun maxHistoricalRadius25() {
        val pixels = IntArray(32 * 32) { i -> if (i % 2 == 0) argb(255, 255, 255, 255) else argb(255, 0, 0, 0) }
        StackBlur.blur(pixels, 32, 32, 25)
        // checkerboard at radius 25 must converge near 50% gray everywhere
        val mid = pixels[16 * 32 + 16] shr 16 and 0xff
        assertTrue("expected near-gray, got $mid", mid in 96..160)
    }
}
