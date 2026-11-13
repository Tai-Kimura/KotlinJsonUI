package com.kotlinjsonui.components

import com.kotlinjsonui.components.CollectionReorderRules.RowSpan
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The rule (CollectionReorderRules), on hand-picked cases until shared/core/reorder_vectors.json arrives to hold the
 * three faces to one table. Six rows of 60, from 0.
 */
class CollectionReorderRulesTest {
    private val rows = (0 until 6).map { RowSpan(it * 60f, it * 60f + 60f) }

    @Test fun a_row_lands_past_every_centre_its_own_centre_crossed() {
        assertEquals(1, CollectionReorderRules.target(1, 59f, rows, emptySet()))
        assertEquals(1, CollectionReorderRules.target(1, 60f, rows, emptySet()))    // exactly on a centre: not past
        assertEquals(2, CollectionReorderRules.target(1, 61f, rows, emptySet()))
        assertEquals(0, CollectionReorderRules.target(3, -500f, rows, emptySet()))  // above the first row: first
        assertEquals(5, CollectionReorderRules.target(1, 900f, rows, emptySet()))   // below the last row: last
    }

    @Test fun a_row_never_lands_on_or_past_a_locked_one() {
        assertEquals(2, CollectionReorderRules.target(0, 900f, rows, setOf(3)))
        assertEquals(4, CollectionReorderRules.target(5, -900f, rows, setOf(3)))
        assertEquals(2, CollectionReorderRules.clampedTo(0, 5, setOf(3)))
        assertEquals(4, CollectionReorderRules.clampedTo(5, 0, setOf(3)))
        assertNull(CollectionReorderRules.clampedTo(3, 0, setOf(3)))
        assertTrue(CollectionReorderRules.crossesLocked(0, 4, setOf(3)))
        assertFalse(CollectionReorderRules.crossesLocked(0, 2, setOf(3)))
    }

    @Test fun only_a_release_outside_the_container_is_cancelled_and_only_a_real_move_is_reported() {
        assertTrue(CollectionReorderRules.releaseCommits(10f, 0f, 400f))
        assertTrue(CollectionReorderRules.releaseCommits(0f, 0f, 400f))
        assertFalse(CollectionReorderRules.releaseCommits(401f, 0f, 400f))
        assertFalse(CollectionReorderRules.shouldReport(2, 2))
        assertTrue(CollectionReorderRules.shouldReport(2, 3))
    }

    @Test fun auto_scroll_is_faster_deeper_into_the_edge_band() {
        assertEquals(0f, CollectionReorderRules.autoScrollVelocity(200f, 0f, 400f))
        assertEquals(-800f, CollectionReorderRules.autoScrollVelocity(0f, 0f, 400f))
        assertEquals(400f, CollectionReorderRules.autoScrollVelocity(376f, 0f, 400f))
    }

    @Test fun the_accessibility_moves_stop_at_the_ends_and_at_locked_rows() {
        assertEquals(false to true, CollectionReorderRules.accessibilityMoves(0, 6, emptySet()))
        assertEquals(true to false, CollectionReorderRules.accessibilityMoves(5, 6, emptySet()))
        assertEquals(false to true, CollectionReorderRules.accessibilityMoves(4, 6, setOf(3)))
        assertEquals(false to false, CollectionReorderRules.accessibilityMoves(3, 6, setOf(3)))
    }
}
