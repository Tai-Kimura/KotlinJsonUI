package com.kotlinjsonui.dynamic.components

import androidx.compose.ui.graphics.Color
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Segment item titles and container color must match the static codegen
 * (segment_component.rb):
 *
 * - literal `items` entries run through the same text resolution as
 *   process_text (string resource keys / @{binding} expressions), instead
 *   of being displayed as raw keys;
 * - `containerColor` defaults to Color.Transparent when no backgroundColor
 *   is specified — passing null would fall through to the Material3 TabRow
 *   surface default and paint an opaque band behind the tabs.
 *
 * String-resource lookup itself needs an Android Context (exercised by the
 * conformance suite); these JVM tests pin the resolution routing and the
 * transparent default.
 */
class SegmentResolutionTest {

    private fun json(s: String) = JsonParser.parseString(s).asJsonObject

    // ── items resolution ──

    @Test
    fun `literal items resolve binding expressions like static process_text`() {
        val node = json("""{"type":"Segment","items":["@{firstTitle}","plain"]}""")
        val segments = DynamicSegmentComponent.parseSegments(
            node,
            mapOf("firstTitle" to "Common"),
            context = null
        )
        assertEquals(listOf("Common", "plain"), segments)
    }

    @Test
    fun `literal items without a context pass through unchanged`() {
        // Resource lookup requires a Context; without one the key must
        // survive verbatim (no crash, no empty list).
        val node = json("""{"type":"Segment","items":["mode_same","mode_different"]}""")
        val segments = DynamicSegmentComponent.parseSegments(node, emptyMap(), context = null)
        assertEquals(listOf("mode_same", "mode_different"), segments)
    }

    // Inverted 2026-09-04. This example asserted that `items` accepts a
    // whole-list @{binding}; it now asserts it does not. SSoT declares
    // Segment.items as type "array" with no binding, the iOS dynamic runtime
    // never accepted one, and kjui's codegen stopped in 1.8.39 — this runtime
    // was the last face still taking a shape the contract does not describe.
    @Test
    fun `a whole-list binding is not an items array and yields nothing`() {
        val node = json("""{"type":"Segment","items":"@{tabs}"}""")
        val segments = DynamicSegmentComponent.parseSegments(
            node,
            mapOf("tabs" to listOf("A", "B")),
            context = null
        )
        assertEquals(emptyList<String>(), segments)
    }

    // The undeclared 'segments' alias, removed in the same change. Reading it
    // here while the extract vocabulary had already dropped it meant a layout
    // that still rendered from strings nobody collected.
    @Test
    fun `the undeclared segments alias is not read`() {
        val node = json("""{"type":"Segment","segments":["A","B"]}""")
        val segments = DynamicSegmentComponent.parseSegments(node, emptyMap(), context = null)
        assertEquals(emptyList<String>(), segments)
    }

    // Control for both removals: the declared shape is untouched.
    @Test
    fun `a literal items array is unaffected by the two removals`() {
        val node = json("""{"type":"Segment","items":["A","B"]}""")
        val segments = DynamicSegmentComponent.parseSegments(node, emptyMap(), context = null)
        assertEquals(listOf("A", "B"), segments)
    }

    // ── containerColor default ──

    @Test
    fun `containerColor defaults to transparent when backgroundColor is absent`() {
        val node = json("""{"type":"Segment","items":["a","b"]}""")
        assertEquals(
            Color.Transparent,
            DynamicSegmentComponent.resolveContainerColor(node, emptyMap(), context = null)
        )
    }

    @Test
    fun `containerColor uses backgroundColor when specified`() {
        // Hex parsing goes through android.graphics.Color (stubbed on the
        // JVM), so feed the color through a binding instead.
        val node = json("""{"type":"Segment","backgroundColor":"@{bg}"}""")
        assertEquals(
            Color(0xFFFF0000),
            DynamicSegmentComponent.resolveContainerColor(
                node,
                mapOf("bg" to Color(0xFFFF0000)),
                context = null
            )
        )
    }
}
