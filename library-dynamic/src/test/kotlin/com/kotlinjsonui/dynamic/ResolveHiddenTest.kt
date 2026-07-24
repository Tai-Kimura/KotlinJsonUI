package com.kotlinjsonui.dynamic

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Unit tests for [resolveHidden] (DynamicView.kt).
 *
 * Spec ruling: `hidden: true` means the component KEEPS its layout space
 * but is not drawn and is not visible to accessibility — the boolean
 * shorthand for visibility:"invisible". It must NOT collapse.
 *
 * These tests cover the resolver semantics (literal booleans, string
 * literals, and canonical `@{...}` boolean bindings). The composable
 * behavior itself — keep-space + alpha(0f) + clearAndSetSemantics via
 * VisibilityWrapper(hidden = true) — cannot be asserted in a plain unit
 * test without instrumentation; the behavioral gate is the conformance
 * fixture `common/hidden__binding_negation` (element must occupy layout
 * space and must NOT be findable by the test driver when hidden).
 */
class ResolveHiddenTest {

    private fun node(json: String): JsonObject =
        JsonParser.parseString(json).asJsonObject

    // ── Literal values ────────────────────────────────────────────────

    @Test
    fun literalTrue_resolvesTrue() {
        assertEquals(true, resolveHidden(node("""{"type":"View","hidden":true}"""), emptyMap()))
    }

    @Test
    fun literalFalse_resolvesFalse() {
        assertEquals(false, resolveHidden(node("""{"type":"View","hidden":false}"""), emptyMap()))
    }

    @Test
    fun missingAttribute_resolvesNull() {
        assertNull(resolveHidden(node("""{"type":"View"}"""), emptyMap()))
    }

    @Test
    fun stringTrue_resolvesTrue() {
        assertEquals(true, resolveHidden(node("""{"type":"View","hidden":"true"}"""), emptyMap()))
    }

    @Test
    fun stringTrueCaseInsensitive_resolvesTrue() {
        assertEquals(true, resolveHidden(node("""{"type":"View","hidden":"TRUE"}"""), emptyMap()))
    }

    @Test
    fun stringFalse_resolvesFalse() {
        assertEquals(false, resolveHidden(node("""{"type":"View","hidden":"false"}"""), emptyMap()))
    }

    @Test
    fun nonPrimitiveValue_resolvesNull() {
        assertNull(resolveHidden(node("""{"type":"View","hidden":{"nested":true}}"""), emptyMap()))
    }

    // ── Canonical boolean bindings ────────────────────────────────────

    @Test
    fun binding_trueProperty_resolvesTrue() {
        val json = node("""{"type":"View","hidden":"@{isHidden}"}""")
        assertEquals(true, resolveHidden(json, mapOf("isHidden" to true)))
    }

    @Test
    fun binding_falseProperty_resolvesFalse() {
        val json = node("""{"type":"View","hidden":"@{isHidden}"}""")
        assertEquals(false, resolveHidden(json, mapOf("isHidden" to false)))
    }

    @Test
    fun binding_unresolvedProperty_resolvesNull() {
        val json = node("""{"type":"View","hidden":"@{isHidden}"}""")
        assertNull(resolveHidden(json, emptyMap()))
    }

    @Test
    fun binding_negation_invertsProperty() {
        val json = node("""{"type":"View","hidden":"@{!isVisible}"}""")
        assertEquals(true, resolveHidden(json, mapOf("isVisible" to false)))
        assertEquals(false, resolveHidden(json, mapOf("isVisible" to true)))
    }

    @Test
    fun binding_negation_unresolvedStaysUnresolved() {
        val json = node("""{"type":"View","hidden":"@{!missing}"}""")
        assertNull(resolveHidden(json, emptyMap()))
    }

    @Test
    fun binding_defaultFallback_appliesWhenUnresolved() {
        val json = node("""{"type":"View","hidden":"@{missing ?? true}"}""")
        assertEquals(true, resolveHidden(json, emptyMap()))
    }

    @Test
    fun binding_dotPath_resolvesNestedProperty() {
        val json = node("""{"type":"View","hidden":"@{state.hidden}"}""")
        assertEquals(true, resolveHidden(json, mapOf("state" to mapOf("hidden" to true))))
    }

    // ── Reactivity contract ───────────────────────────────────────────

    @Test
    fun binding_reResolvesAgainstUpdatedDataMap() {
        // resolveHidden is called on every composition with the current
        // data map — a changed value must flip the result (this is what
        // keeps `hidden` reactive across recompositions).
        val json = node("""{"type":"View","hidden":"@{isHidden}"}""")
        assertEquals(true, resolveHidden(json, mapOf("isHidden" to true)))
        assertEquals(false, resolveHidden(json, mapOf("isHidden" to false)))
    }
}
