package com.kotlinjsonui.dynamic.helpers

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * The wiring of the standard modifier chain, asserted in one test.
 *
 * Measured 2026-09-01: deleting any single apply line from the hand-written
 * `buildModifier` body left the unit suite green for 8 of its 9 stages
 * (782 tests). The one catch, `background`, is a SelectBox guard asserting
 * element names for its own reasons — incidental coverage, not a wiring test,
 * and it vanishes silently if that guard changes. Same shape as the SwiftUI
 * side (21 of 23 undetected), so both chains are driven by a declared order
 * and the wiring can only break as "a name left the list", which is what this
 * asserts.
 *
 * ONE layer, not two — decided by measurement, not preference. A second layer
 * comparing the actually-constructed `Modifier` element names against a
 * hand-written expectation was considered and rejected because the
 * stage → element-name correspondence does not close (probed 2026-09-02 on an
 * all-attributes fixture):
 *   - one class name serves two stages (PaddingElement: margins AND padding;
 *     GraphicsLayerElement: alpha AND shadow),
 *   - one stage emits several (size → SizeElement x2),
 *   - contributions are fixture-conditional (a minimal node emits 2 elements,
 *     the all-attributes node 13, and `defaultFillMaxWidth` adds FillElement
 *     on the minimal node only).
 * An element-name expectation therefore pins (stages x fixture x flags), and a
 * mismatch could not name which stage moved. The sequence IS stable per
 * fixture — that is what the migration's equivalence capture used — but
 * stability is not attribution.
 */
class StandardModifierOrderTest {

    /**
     * Written out here rather than read back from `standardOrder`: comparing
     * the constant against itself is true no matter what it holds. Adding or
     * removing a stage must touch both places, which is what makes the change
     * a statement of intent instead of a side effect.
     *
     * `weight` and `alignment` are ABSENT on purpose, not lost: weight only
     * exists in RowScope/ColumnScope so the caller applies it, and alignment
     * is handled by the container. Their absence is not a migration gap.
     */
    private val expectedOrder = listOf(
        "testTag",
        "margins",
        "size",
        "offset",
        "alpha",
        "shadow",
        "background",
        "clickable",
        "padding",
    )

    @Test
    fun `every stage is wired in order`() {
        assertEquals(
            "the applied chain no longer matches the declared order — a stage " +
                "was added, removed, or moved without updating both places",
            expectedOrder,
            ModifierBuilder.standardOrder.map { it.name }
        )
    }
}
