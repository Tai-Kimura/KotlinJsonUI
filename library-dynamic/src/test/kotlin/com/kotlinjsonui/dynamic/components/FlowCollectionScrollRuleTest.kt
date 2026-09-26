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
        // The flow's own ScrollState (it scrolls to a scrollTo cell since
        // jsonui-cli 1.9.0, round 11), the content's place recorded.
        assertEquals("heightIsSelfBounded -> flow(modifier.then(scrolled))", flowBranch().line("heightIsSelfBounded ->"))
        assertEquals(
            "val scrolled = Modifier.verticalScroll(flowScroll).onGloballyPositioned { flowTargets.content = it }",
            flowBranch().line("val scrolled =")
        )
    }

    @Test
    fun `an unbounded height asks the parent and scrolls only under a finite one`() {
        val branch = flowBranch()
        assertTrue(branch.joinToString("\n"), branch.any { it.startsWith("else -> BoxWithConstraints(modifier = modifier)") })
        val ask = branch.line("val inner = if (constraints.hasBoundedHeight)")
        val scrolled = branch.indexOf(ask) + 1
        assertEquals("Modifier.fillMaxSize().then(scrolled)", branch[scrolled])
        // The infinite-parent arm is the crash shape: it must carry no scroll.
        val unbounded = branch.subList(scrolled + 1, branch.size).line("Modifier.fillMaxWidth()")
        assertFalse(unbounded, "verticalScroll" in unbounded)
        assertEquals(2, branch.count { "then(scrolled)" in it })
        assertEquals(1, branch.count { "verticalScroll(" in it })
    }

    @Test
    fun `the scrolled modifier is the one handed to renderFlowLayout`() {
        assertEquals(
            "modifier = flowModifier.then(Modifier.padding(contentPadding)),",
            flowBranch().line("modifier = ")
        )
    }

    /**
     * 51-E (attribute_semantics.clipToBounds, 2026-08-07): `clipToBounds`
     * defaults to false everywhere — absent means no clip. FlowRow does not
     * lay out the rows past its max height, which the modifier-chain rollout
     * could not reach; so the FlowRow is measured unbounded and aligned over
     * the box (wrapContentHeight, unbounded — FlowRow's `overflow` parameter
     * is deprecated in the resolved foundation-layout), and the declared
     * `clipToBounds: true` keeps its opt-in `.clipToBounds()` in the chain.
     */
    @Test
    fun `the FlowRow lays its overflow out — clipping is the declaration's to decide`() {
        val all = lines()
        val start = all.indexOfFirst { it.startsWith("private fun renderFlowLayout") }
        assertTrue("renderFlowLayout not found", start >= 0)
        val next = all.subList(start + 1, all.size).indexOfFirst { it.startsWith("private fun ") }
        val body = all.subList(start, if (next < 0) all.size else start + 1 + next)
        // The flow's outer container is measured unbounded: the one FlowRow,
        // or — with two or more sections, each its own wrap (4f ruling,
        // 2026-09-26) — the Column that holds their FlowRows. Both take the
        // node's modifier through `overflowVisible`; nothing else takes it.
        assertTrue(
            "the node's modifier is not measured unbounded:\n${body.joinToString("\n")}",
            body.any { it == "val overflowVisible = modifier.wrapContentHeight(Alignment.Top, unbounded = true)" }
        )
        val outer = body.indices
            .filter { body[it] == "FlowRow(" || body[it] == "Column(" }
            .filter { i -> body.subList(i + 1, minOf(i + 3, body.size)).any { it == "modifier = overflowVisible," } }
            .map { body[it] }
        assertEquals(body.joinToString("\n"), listOf("Column(", "FlowRow("), outer.sorted())
        assertEquals(body.joinToString("\n"), 2, body.count { it == "modifier = overflowVisible," })
        assertFalse(body.joinToString("\n"), body.any { it.startsWith("modifier = modifier") })
        // Not the deprecated route: the overload that takes `overflow` warns,
        // and this repo's CI tolerates no compiler warning.
        assertFalse(body.joinToString("\n"), body.any { "FlowRowOverflow" in it })
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
