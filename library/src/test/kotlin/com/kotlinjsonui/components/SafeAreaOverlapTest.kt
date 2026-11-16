package com.kotlinjsonui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

/**
 * `contentInsetAdjustmentBehavior` adds the part of the window's safe area the
 * scrollable's frame overlaps — UIKit's `.always` (jsonui-cli ticket
 * kjui-collection-content-inset-always-adds-whole-window-insets). Density 1,
 * so px == dp. A 1000 x 2000 window with a status bar of 100 and a
 * navigation bar of 50.
 */
class SafeAreaOverlapTest {
    private val window = IntSize(1000, 2000)
    private val bars = WindowInsets(left = 0, top = 100, right = 0, bottom = 50)

    private fun overlap(insets: WindowInsets = bars, frame: Rect?): SafeAreaOverlap =
        SafeAreaOverlap(insets, Density(1f)) { window }.also { o -> frame?.let { o.place(it, window) } }

    private fun PaddingValues.sides(dir: LayoutDirection = LayoutDirection.Ltr) = listOf(
        calculateTopPadding().value, calculateBottomPadding().value,
        calculateLeftPadding(dir).value, calculateRightPadding(dir).value,
    )

    /** The reported shape: inside a SafeAreaView, below a 44 chip row — no band at all. */
    @Test fun a_frame_clear_of_the_bars_adds_nothing() {
        assertEquals(listOf(0f, 0f, 0f, 0f), overlap(frame = Rect(0f, 144f, 1000f, 1950f)).contentPadding().sides())
    }

    /** A frame from edge to edge (under both bars) adds each bar whole, as before. */
    @Test fun a_full_window_frame_adds_the_bars() {
        assertEquals(listOf(100f, 50f, 0f, 0f), overlap(frame = Rect(0f, 0f, 1000f, 2000f)).contentPadding().sides())
    }

    /** Partly under a bar: only the part under it. */
    @Test fun a_frame_partly_under_a_bar_adds_the_part() {
        assertEquals(listOf(60f, 30f, 0f, 0f), overlap(frame = Rect(0f, 40f, 1000f, 1980f)).contentPadding().sides())
    }

    /** Never more than the frame itself is tall. */
    @Test fun a_frame_shorter_than_the_bar_adds_its_own_height() {
        assertEquals(30f, overlap(frame = Rect(0f, 0f, 1000f, 30f)).contentPadding().calculateTopPadding().value)
    }

    /** Before the frame is first placed nothing is added. */
    @Test fun before_the_first_placement_nothing_is_added() {
        assertEquals(listOf(0f, 0f, 0f, 0f), overlap(frame = null).contentPadding().sides())
    }

    /** The declared content padding is added side by side (rounds 16 and 17). */
    @Test fun the_own_padding_is_added() {
        val own = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp)
        assertEquals(listOf(116f, 66f, 16f, 16f), overlap(frame = Rect(0f, 0f, 1000f, 2000f)).contentPadding(own).sides())
        assertEquals(listOf(16f, 16f, 16f, 16f), overlap(frame = Rect(0f, 144f, 1000f, 1950f)).contentPadding(own).sides())
    }

    /** scrollableAxes: the vertical sides only — a side cutout is not added to a vertical list. */
    @Test fun the_sides_limit_it() {
        val cutout = WindowInsets(left = 80, top = 100, right = 0, bottom = 50)
        val vertical = overlap(cutout.only(WindowInsetsSides.Vertical), Rect(0f, 0f, 1000f, 2000f))
        assertEquals(listOf(100f, 50f, 0f, 0f), vertical.contentPadding().sides())
        val all = overlap(cutout, Rect(0f, 0f, 1000f, 2000f))
        assertEquals(listOf(100f, 50f, 80f, 0f), all.contentPadding().sides())
    }

    /** Right to left: the window's left inset is still the left padding. */
    @Test fun left_and_right_are_the_windows_in_either_direction() {
        val cutout = WindowInsets(left = 80, top = 0, right = 0, bottom = 0)
        val o = overlap(cutout, Rect(0f, 0f, 1000f, 2000f))
        assertEquals(80f, o.contentPadding().calculateLeftPadding(LayoutDirection.Rtl).value)
        assertEquals(0f, o.contentPadding().calculateRightPadding(LayoutDirection.Rtl).value)
    }

    /** A move (rotation, a split window, a parent that grows) is read on the next measure: placed again, read again. */
    @Test fun a_new_frame_is_followed() {
        val o = overlap(frame = Rect(0f, 144f, 1000f, 1950f))
        val padding = o.contentPadding()
        assertEquals(0f, padding.calculateTopPadding().value)
        o.place(Rect(0f, 0f, 1000f, 1950f), window)
        assertEquals(100f, padding.calculateTopPadding().value)
    }

    /** An inset change (the IME, a bar appearing) is read on the next measure too, from the same padding. */
    @Test fun an_inset_change_is_followed() {
        val ime = object : WindowInsets {
            var bottom by mutableIntStateOf(50)
            override fun getLeft(density: Density, layoutDirection: LayoutDirection) = 0
            override fun getTop(density: Density) = 100
            override fun getRight(density: Density, layoutDirection: LayoutDirection) = 0
            override fun getBottom(density: Density) = bottom
        }
        val padding = overlap(ime, Rect(0f, 0f, 1000f, 2000f)).contentPadding()
        assertEquals(50f, padding.calculateBottomPadding().value)
        ime.bottom = 300
        assertEquals(300f, padding.calculateBottomPadding().value)
    }

    /** A re-placement at the same frame writes nothing, so it invalidates nothing — no measure loop. */
    @Test fun the_same_frame_writes_nothing() {
        val frame = Rect(0f, 0f, 1000f, 2000f)
        val o = overlap(frame = frame)
        val before = o.frame
        o.place(Rect(0f, 0f, 1000f, 2000f), window)
        assertSame(before, o.frame)
    }
}
