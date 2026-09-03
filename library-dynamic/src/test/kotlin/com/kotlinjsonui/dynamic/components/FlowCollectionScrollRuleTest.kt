package com.kotlinjsonui.dynamic.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Ruling (2026-09-03): a flow Collection with `lazy` in effect (LAZY or
 * EAGER) scrolls vertically inside its own bounds; `lazy: "none"` only
 * wraps and the parent scrolls. "Its own bounds" is literal — a vertically
 * scrollable node measured with an infinite max height throws, which is
 * what wrapContent (or matchParent under a LazyColumn cell) is handed — so
 * wrapContent never scrolls, a self-bounded height (number / maxHeight)
 * always may, and matchParent asks the parent's constraints at runtime.
 *
 * The rendering is Compose and belongs to the conformance host
 * (`Collection/flowOverflow__{scroll,none,wrap}`); what is pinned here, in
 * the same source-shape form as [ComponentRawReadGateTest], is the dispatch
 * that decides it — the flow branch applied no scroll on any mode until
 * 2.27.0, and the JVM cannot compose to see that.
 */
class FlowCollectionScrollRuleTest {

    private val source = File(
        "src/main/kotlin/com/kotlinjsonui/dynamic/components/DynamicCollectionComponent.kt"
    )

    private fun lines(): List<String> {
        assertTrue("source missing: ${source.absolutePath}", source.isFile)
        return source.readLines().map { it.trim() }.filterNot { it.startsWith("//") }
    }

    /** The `if (isFlow) { ... return }` block of the dispatcher, comments dropped. */
    private fun flowBranch(): List<String> {
        val all = lines()
        val start = all.indexOf("if (isFlow) {")
        assertTrue("flow branch not found", start >= 0)
        val end = all.subList(start, all.size).indexOf("return")
        assertTrue("flow branch has no return", end > 0)
        return all.subList(start, start + end)
    }

    private fun List<String>.line(prefix: String): String {
        val found = firstOrNull { it.startsWith(prefix) }
        assertTrue("no line starting with `$prefix` in:\n${joinToString("\n")}", found != null)
        return found!!
    }

    @Test
    fun `the mode gate is every mode but NONE, and never wrapContent`() {
        val gate = flowBranch().line("val flowScrolls =")
        // Not "only EAGER" — that is the column path's condition and would
        // leave the LAZY default unscrolled, the pre-2.27.0 picture under a
        // different spelling.
        assertTrue(gate, "collectionMode != CollectionStackMode.NONE" in gate)
        assertFalse(gate, "== CollectionStackMode.EAGER" in gate)
        assertTrue("wrapContent guard missing: $gate", "!heightIsWrapContent" in gate)
        assertEquals("!flowScrolls -> flow(modifier)", flowBranch().line("!flowScrolls ->"))
    }

    @Test
    fun `a self-bounded height scrolls without asking the parent`() {
        val bounded = lines().line("val heightIsSelfBounded =")
        val definition = lines().let { it.subList(it.indexOf(bounded), it.indexOf(bounded) + 2) }.joinToString(" ")
        assertTrue(definition, "DimensionValue.Number" in definition)
        assertTrue(definition, "a.common.maxHeight" in definition)
        assertEquals(
            "heightIsSelfBounded -> flow(modifier.verticalScroll(rememberScrollState()))",
            flowBranch().line("heightIsSelfBounded ->")
        )
    }

    @Test
    fun `an unbounded height asks the parent and scrolls only under a finite one`() {
        val branch = flowBranch()
        assertTrue(branch.joinToString("\n"), branch.any { it.startsWith("else -> BoxWithConstraints(modifier = modifier)") })
        val ask = branch.line("val inner = if (constraints.hasBoundedHeight)")
        val scrolled = branch.indexOf(ask) + 1
        assertEquals("Modifier.fillMaxSize().verticalScroll(rememberScrollState())", branch[scrolled])
        // The infinite-parent arm is the crash shape: it must carry no scroll.
        val unbounded = branch.subList(scrolled + 1, branch.size).line("Modifier.fillMaxWidth()")
        assertFalse(unbounded, "verticalScroll" in unbounded)
        assertEquals(2, branch.count { "verticalScroll(rememberScrollState())" in it })
    }

    @Test
    fun `the scrolled modifier is the one handed to renderFlowLayout`() {
        assertEquals(
            "modifier = flowModifier.then(Modifier.padding(contentPadding)),",
            flowBranch().line("modifier = ")
        )
    }

    @Test
    fun `the height reads sit before the flow branch, not after it`() {
        // They were declared after the flow `return`; hoisting them is what
        // lets the flow branch share the column path's guard.
        val all = lines()
        val flow = all.indexOf("if (isFlow) {")
        for (prefix in listOf("val heightIsWrapContent", "val heightIsSelfBounded")) {
            val decl = all.indexOfFirst { it.startsWith(prefix) }
            assertTrue("$prefix declared at $decl, flow branch at $flow", decl in 0 until flow)
        }
    }
}
