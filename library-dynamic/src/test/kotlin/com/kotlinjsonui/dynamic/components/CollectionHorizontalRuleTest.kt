package com.kotlinjsonui.dynamic.components

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.generated.CollectionAttributes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * One rule for every horizontal Collection — a single lane, its lanes and
 * its pages (4f ruling, 2026-09-26; SwiftJsonUI Dynamic reads the same):
 * along the scroll axis lineSpacing, else itemSpacing, else 0; between lanes
 * columnSpacing, else itemSpacing, else 0. And a declared section starts a
 * new column on the horizontal grid, as it starts a new row on the vertical.
 *
 * Before: the scroll axis read lineSpacing, else columnSpacing, else
 * itemSpacing; the lanes were not spaced (spacedBy(0)); pages were spaced by
 * columnSpacing; sections ran on into the previous section's part-filled
 * column.
 *
 * The drawing is Compose and is pinned on a device
 * (DynamicCollectionHorizontalRuleTest); the JVM pins the rule and, in the
 * same source-shape form as FlowCollectionScrollRuleTest, the wiring.
 */
class CollectionHorizontalRuleTest {

    private fun spacing(json: String): DynamicCollectionComponent.Companion.HorizontalSpacing {
        val node = Gson().fromJson(json, JsonObject::class.java)
        val a = CollectionAttributes.parse(TypedAttrs.toAttrMap(node))
        return DynamicCollectionComponent.horizontalSpacing(a, a.itemSpacing?.toFloat() ?: 0f)
    }

    @Test
    fun theScrollAxisIsLineSpacingElseItemSpacing() {
        assertEquals(12f, spacing("""{"type":"Collection","lineSpacing":12,"columnSpacing":30}""").alongScroll)
        assertEquals(20f, spacing("""{"type":"Collection","itemSpacing":20,"columnSpacing":30}""").alongScroll)
        // columnSpacing is not on the scroll axis.
        assertEquals(0f, spacing("""{"type":"Collection","columnSpacing":30}""").alongScroll)
    }

    @Test
    fun theLanesAreColumnSpacingElseItemSpacing() {
        assertEquals(30f, spacing("""{"type":"Collection","lineSpacing":12,"columnSpacing":30}""").betweenLanes)
        assertEquals(20f, spacing("""{"type":"Collection","itemSpacing":20,"lineSpacing":12}""").betweenLanes)
        assertEquals(0f, spacing("""{"type":"Collection","lineSpacing":12}""").betweenLanes)
    }

    // ── the wiring ───────────────────────────────────────────────────

    private val source = File(
        "src/main/kotlin/com/kotlinjsonui/dynamic/components/DynamicCollectionComponent.kt"
    )

    private fun code(): List<String> {
        assertTrue("source missing: ${source.absolutePath}", source.isFile)
        return source.readLines().map { it.trim() }.filterNot { it.startsWith("//") || it.startsWith("*") }
    }

    /** The block from a line starting with [start] to the next line equal to [end]. */
    private fun block(start: String, end: String): List<String> {
        val all = code()
        val from = all.indexOfFirst { it.startsWith(start) }
        assertTrue("no line starting with `$start`", from >= 0)
        val to = all.subList(from, all.size).indexOf(end)
        assertTrue("no `$end` after `$start`", to > 0)
        return all.subList(from, from + to)
    }

    @Test
    fun everyHorizontalRouteTakesTheScrollAxisFromTheRule() {
        val lines = code()
        assertTrue(lines.contains("horizontal.alongScroll.dp"))
        // The lazy row, the non-lazy row and the pager take scrollAxisSpacing.
        assertTrue(lines.contains("scrollAxisSpacing = scrollAxisSpacing,"))
        assertTrue(lines.contains("columnSpacing = scrollAxisSpacing,"))
        assertTrue(lines.contains("pageSpacing = scrollAxisSpacing,"))
    }

    @Test
    fun theHorizontalGridSpacesItsLanesAndBreaksBetweenSections() {
        val grid = block("LazyHorizontalGrid(", "} else {")
        assertTrue(grid.joinToString("\n"), grid.any { it.startsWith("verticalArrangement = Arrangement.spacedBy(horizontal.betweenLanes.dp)") })
        assertTrue(grid.joinToString("\n"), grid.any { it.startsWith("horizontalArrangement = Arrangement.spacedBy(scrollAxisSpacing, if (reverseLayout) Alignment.End else Alignment.Start)") })
        assertTrue(grid.joinToString("\n"), grid.contains("breakRowsBetweenSections = plan.hasDeclaredSections,"))
    }
}
