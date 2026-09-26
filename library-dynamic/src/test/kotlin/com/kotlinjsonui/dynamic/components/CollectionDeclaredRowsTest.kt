package com.kotlinjsonui.dynamic.components

import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.UnappliedAttributes
import com.kotlinjsonui.dynamic.generated.CollectionAttributes
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * Collection rows the dynamic face read differently from what it declared
 * (audit 2026-09-26, "Collection: which declared attributes each of the 5
 * paths draws"):
 *
 * - `horizontalScroll` / `cellWidth` / `cellHeight` / `hideSeparator` /
 *   `listStyle` are drawn, yet a Debug build logged each as "not applied"
 *   because the component's applied set did not name them.
 * - `scrollAnimated` was not read: every scrollTo animated.
 * - `contentInsets` is declared for swift only (mode swiftui) and kjui's
 *   codegen does not draw it, but this face padded with it — Debug differed
 *   from Release.
 *
 * The drawing itself is Compose and is pinned on a device
 * (DynamicCollectionDeclaredRowsTest); the JVM pins the parts that decide it.
 */
class CollectionDeclaredRowsTest {

    private fun obj(json: String): JsonObject = Gson().fromJson(json, JsonObject::class.java)

    private val warnings = mutableListOf<String>()

    @Before
    fun sink() {
        UnappliedAttributes.enabledOverride = true
        UnappliedAttributes.warningSink = { warnings.add(it) }
    }

    @After
    fun clear() {
        UnappliedAttributes.enabledOverride = null
        UnappliedAttributes.warningSink = null
    }

    /** What a Debug build logs for `json` on a Collection — the component's own call. */
    private fun unapplied(json: String): List<String> {
        warnings.clear()
        // The component names itself per call site, and the check dedupes
        // per (component, key) for the process — a fresh name per call keeps
        // the arms independent of their order.
        UnappliedAttributes.check(
            "Collection#${System.nanoTime()}", obj(json),
            declared = CollectionAttributes.declaredAttributes,
            applied = UnappliedAttributes.COMMON_APPLIED + DynamicCollectionComponent.APPLIED
        )
        return warnings.toList()
    }

    // ── the applied set ──────────────────────────────────────────────

    @Test
    fun theDrawnRowsAreNotLoggedAsUnapplied() {
        val rows = listOf(
            "horizontalScroll" to "true",
            "cellWidth" to "120",
            "cellHeight" to "44",
            "hideSeparator" to "true",
            "listStyle" to "\"grouped\"",
            "scrollAnimated" to "false",
        )
        for ((key, _) in rows) {
            assertTrue("$key is declared for Collection", CollectionAttributes.isDeclared(key))
        }
        // Every row asked before any verdict, so a red run names all of them.
        val logged = rows.filter { (key, value) ->
            unapplied("""{"type":"Collection","$key":$value}""").isNotEmpty()
        }.map { it.first }
        assertEquals(emptyList<String>(), logged)
    }

    /** Positive control: the same call does log a declared row nothing draws. */
    @Test
    fun aDeclaredRowThisFaceDoesNotDrawIsLogged() {
        val logged = unapplied("""{"type":"Collection","contentInsets":[8,8,8,8]}""")
        assertEquals(1, logged.size)
        assertTrue(logged.single(), "'contentInsets'" in logged.single())
    }

    // ── contentInsets ────────────────────────────────────────────────

    private fun padding(json: String): List<Float> {
        val node = obj(json)
        val a = CollectionAttributes.parse(TypedAttrs.toAttrMap(node))
        val p = DynamicCollectionComponent.parseCollectionPadding(a, node)
        return listOf(
            p.calculateTopPadding().value,
            p.calculateRightPadding(LayoutDirection.Ltr).value,
            p.calculateBottomPadding().value,
            p.calculateLeftPadding(LayoutDirection.Ltr).value,
        )
    }

    private fun declaresPadding(json: String): Boolean {
        val node = obj(json)
        return DynamicCollectionComponent.hasDeclaredContentPadding(
            CollectionAttributes.parse(TypedAttrs.toAttrMap(node)), node
        )
    }

    @Test
    fun contentInsetsDoesNotPadOnThisPlatform() {
        val json = """{"type":"Collection","contentInsets":[8,8,8,8]}"""
        assertEquals(listOf(0f, 0f, 0f, 0f), padding(json))
        // Nor does it stand in for a declared padding: with it, the
        // contentInsetAdjustmentBehavior safe-area padding still applies.
        assertFalse(declaresPadding(json))
    }

    /** Control: `insets` — the cross-platform spelling — still pads, in the same call. */
    @Test
    fun insetsStillPads() {
        val json = """{"type":"Collection","insets":[8,6,4,2]}"""
        assertEquals(listOf(8f, 6f, 4f, 2f), padding(json))
        assertTrue(declaresPadding(json))
        assertEquals(0.dp.value, padding("""{"type":"Collection"}""").sum())
    }

    // ── scrollAnimated ───────────────────────────────────────────────

    private val source = File(
        "src/main/kotlin/com/kotlinjsonui/dynamic/components/DynamicCollectionComponent.kt"
    )

    private fun code(): List<String> {
        assertTrue("source missing: ${source.absolutePath}", source.isFile)
        return source.readLines().map { it.trim() }.filterNot { it.startsWith("//") || it.startsWith("*") }
    }

    /**
     * Every scrollTo scroll asks `scrollAnimated`: an animated scroll where
     * it is true, an immediate one where it is false. The JVM cannot compose
     * to watch the frames (the device test does); what is pinned here is that
     * no scroll call is left that animates unconditionally.
     */
    @Test
    fun everyScrollToCallAsksScrollAnimated() {
        val lines = code()
        assertTrue(lines.joinToString("\n"), lines.contains("val scrollAnimated = a.scrollAnimated != false"))
        // Since jsonui-cli 1.9.0 (round 11) the lazy scrolls go through
        // scrollToAnchored (grid, row), which takes `animated`, and the flow
        // and the pager scroll too; the pager's currentPage leg is its own.
        val scrolls = lines.filter { ("animateScrollTo" in it) && "currentPage" !in it && "animateScrollToPage(target)" !in it }
        assertEquals("the two scrollToAnchored helpers, the flow and the pager", 4, scrolls.size)
        for (line in scrolls) {
            assertTrue(line, "if (animated)" in line || "if (scrollAnimated)" in line || "if (a.scrollAnimated != false)" in line)
            val instant = line.substringAfter(" else ")
            assertTrue(line, "scrollTo" in instant && "animate" !in instant)
        }
        assertTrue(lines.contains("scrollAnimated = scrollAnimated,"))
        assertEquals(2, lines.count { "scrollToAnchored(index, scrollAnchor" in it && "scrollAnimated" in it })
    }
}
