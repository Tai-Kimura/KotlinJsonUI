package com.kotlinjsonui.components

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * The grow arithmetic behind `distribution: "fill"` — the REAL policy, not a
 * copy of it (growSizes is what both fill layouts measure with).
 */
class DistributionFillTest {

    @Test
    fun leftoverIsSplitEquallyOnTopOfContentSizes() {
        // The fixture's shape: contents 22/84/176 in a 600px axis (A, BBBB,
        // CCCCCCCC at 2x). Leftover 318 → +106 each: content order preserved,
        // nothing left over.
        assertEquals(listOf(128, 190, 282), growSizes(listOf(22, 84, 176), emptyList(), 600, 0))
        assertEquals(600, growSizes(listOf(22, 84, 176), emptyList(), 600, 0).sum())
    }

    @Test
    fun remainderPixelsGoToTheFirstChildrenSoTheSumLandsExactly() {
        // 100 - 30 = 70 leftover over 3 → 23,23,24… no: first children get
        // the extra, so 24,24,22+23. Sum must be the axis, whatever the split.
        val r = growSizes(listOf(10, 10, 10), emptyList(), 100, 0)
        assertEquals(100, r.sum())
        assertEquals(r.sorted().reversed(), r) // earlier children never smaller
    }

    @Test
    fun aChildWithADeclaredSizeDoesNotGrow() {
        // explicit > fill: the declared child keeps its 50, the leftover goes
        // to the other two.
        val r = growSizes(listOf(20, 50, 20), listOf(true, false, true), 200, 0)
        assertEquals(50, r[1])
        assertEquals(200, r.sum())
    }

    @Test
    fun gapsArePinnedAndGrowthHappensInWhatTheyLeave() {
        // spacingWins: two 10px gaps come off the top, 80 remains, 20 leftover.
        assertEquals(listOf(30, 30, 40), growSizes(listOf(20, 20, 30), emptyList(), 120, 10))
    }

    @Test
    fun noLeftoverMeansContentSizesStand() {
        // fill never shrinks anyone — an overfull axis keeps content sizes.
        assertEquals(listOf(100, 200), growSizes(listOf(100, 200), emptyList(), 150, 0))
        assertEquals(listOf(75, 75), growSizes(listOf(75, 75), emptyList(), 150, 0))
    }

    @Test
    fun edgeShapes() {
        assertEquals(emptyList<Int>(), growSizes(emptyList(), emptyList(), 100, 0))
        assertEquals(listOf(100), growSizes(listOf(40), emptyList(), 100, 0))
        // nobody may grow → content stands
        assertEquals(listOf(10, 10), growSizes(listOf(10, 10), listOf(false, false), 100, 0))
    }
}
