package com.kotlinjsonui.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** The four rules of a release (CollectionReorderRules), before shared/core's vectors arrive to hold them too. */
class CollectionReorderRulesTest {
    private val none: (Int) -> Boolean = { false }

    @Test fun released_inside_lands_at_the_slot_under_the_finger() {
        assertEquals(3, CollectionReorderRules.land(6, 1, 3, none))
        assertEquals(0, CollectionReorderRules.land(6, 4, 0, none))
    }

    @Test fun above_the_first_row_lands_first_and_below_the_last_lands_last() {
        assertEquals(0, CollectionReorderRules.land(6, 3, -4, none))
        assertEquals(5, CollectionReorderRules.land(6, 1, 99, none))
    }

    @Test fun released_outside_the_container_is_cancelled_and_back_in_place_is_no_move() {
        assertNull(CollectionReorderRules.land(6, 2, null, none))
        assertNull(CollectionReorderRules.land(6, 2, 2, none))
    }

    @Test fun a_locked_row_is_never_passed_and_never_picked_up() {
        val locked: (Int) -> Boolean = { it == 3 }
        assertEquals(2, CollectionReorderRules.land(6, 0, 5, locked))      // stops just before the locked row
        assertEquals(4, CollectionReorderRules.land(6, 5, 0, locked))      // from below, just after it
        assertEquals(0..2, CollectionReorderRules.range(6, 1, locked))
        assertNull(CollectionReorderRules.land(6, 3, 0, locked))           // the locked row itself does not move
        assertNull(CollectionReorderRules.land(6, 2, 99, locked))          // already at the end of its run
    }
}
