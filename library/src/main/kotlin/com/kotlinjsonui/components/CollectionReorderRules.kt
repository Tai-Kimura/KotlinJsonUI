package com.kotlinjsonui.components

/**
 * Where a dragged row lands in a reorderable Collection (`reorderable` / `onReorder`), apart from any drawing: the
 * rules every face follows, held to shared/core's reorder vectors (jsonui-cli; ticket jui-collection-drag-to-reorder,
 * the iOS 27 reorderable's reading):
 *
 *  1. released inside the Collection's container (the part that holds the rows, its header and footer with them),
 *     the row lands at the slot under the finger;
 *  2. above the first row it lands first, below the last row it lands last;
 *  3. released outside the container, the move is cancelled;
 *  4. a locked row (`reorderLockedProperty`) neither moves nor is passed: a move across one stops just before it.
 *
 * Indices are the section's, before the move; the answer is the row's index after it.
 */
object CollectionReorderRules {
    /**
     * The first and last index *from* may land at: the run of rows between the locked rows around it. null when the
     * row at *from* is locked itself (it cannot be picked up).
     */
    fun range(count: Int, from: Int, locked: (Int) -> Boolean): IntRange? {
        if (from !in 0 until count || locked(from)) return null
        var lo = from
        while (lo - 1 >= 0 && !locked(lo - 1)) lo--
        var hi = from
        while (hi + 1 < count && !locked(hi + 1)) hi++
        return lo..hi
    }

    /**
     * The index the row lands at, or null for no move (cancelled, outside the container, locked, or back where it
     * was). *slot*: the slot under the finger when it was released (any Int: one past either end is clamped by rule
     * 2), or null when the finger left the container (rule 3).
     */
    fun land(count: Int, from: Int, slot: Int?, locked: (Int) -> Boolean): Int? {
        val run = range(count, from, locked) ?: return null
        if (slot == null) return null
        val to = slot.coerceIn(0, count - 1).coerceIn(run.first, run.last)
        return if (to == from) null else to
    }
}
