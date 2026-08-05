package com.kotlinjsonui.dynamic.helpers

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * `contentInsetAdjustmentBehavior`, whose values fall the OPPOSITE way here.
 *
 * The attribute is UIKit's: UIScrollView adjusts its content inset for the
 * safe area by default and the attribute decides whether to stop it. Compose
 * never adjusts — a LazyColumn insets its content only if handed a
 * `contentPadding`. So `never` is the one value that needs no code here, where
 * on iOS it is the only value that does.
 *
 * The mapping is reproduced from the codegen helper rather than re-derived:
 * the two renders of one layout have to inset by the same amount, and a
 * "reasonable" second reading is exactly how the two paths drift apart. The
 * PaddingValues themselves need a composition, so what is pinned here is which
 * values ask for an inset at all.
 */
class ContentInsetBehaviorTest {

    @Test
    fun alwaysAndAutomaticBothAdjust() {
        // Compose has no "depending on context", so automatic is always.
        assertTrue(ContentInsetBehavior.adjusts("always"))
        assertTrue(ContentInsetBehavior.adjusts("automatic"))
    }

    @Test
    fun scrollableAxesAdjusts() {
        assertTrue(ContentInsetBehavior.adjusts("scrollableAxes"))
    }

    @Test
    fun neverEmitsNothing() {
        // Compose's own default, and what keeps every existing screen where it
        // is: they have all been running with no inset, which is what `never`
        // means.
        assertFalse(ContentInsetBehavior.adjusts("never"))
    }

    @Test
    fun anUndeclaredBehaviourEmitsNothing() {
        assertFalse(ContentInsetBehavior.adjusts(null))
        assertFalse(ContentInsetBehavior.adjusts(""))
    }

    @Test
    fun anUnknownValueEmitsNothingRatherThanGuessing() {
        assertFalse(ContentInsetBehavior.adjusts("sideways"))
    }

    @Test
    fun theSpellingIsCaseAndSpaceInsensitive() {
        assertTrue(ContentInsetBehavior.adjusts("  ScrollableAxes "))
        assertTrue(ContentInsetBehavior.adjusts("ALWAYS"))
    }

    @Test
    fun everyDeclaredValueIsAccountedFor() {
        // The SSoT enum is never / always / automatic / scrollableAxes. A value
        // the enum declares and this table does not name would silently mean
        // "no inset", which is the quiet failure this wave keeps finding.
        val declared = listOf("never", "always", "automatic", "scrollableAxes")
        val adjusting = declared.filter { ContentInsetBehavior.adjusts(it) }
        assertTrue("only `never` emits nothing", adjusting.size == declared.size - 1)
        assertFalse(adjusting.contains("never"))
    }
}
