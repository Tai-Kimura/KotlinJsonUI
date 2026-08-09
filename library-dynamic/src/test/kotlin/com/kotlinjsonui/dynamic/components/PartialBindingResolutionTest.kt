package com.kotlinjsonui.dynamic.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * partialAttributes value slots are binding-capable, and the dynamic path
 * reads them from raw JSON maps — so every slot must resolve `@{...}`
 * before use. The raw spelling went to `text.indexOf` / the colour parser
 * untouched, which made a bound `range` never match (the partial silently
 * vanished) and a bound `fontColor` style nothing, while the codegen path —
 * which interpolates `${data.x}` at compose time — rendered both
 * (downstream a-downstream-hour-row-cell, 2026-08-08).
 */
class PartialBindingResolutionTest {

    private val data = mapOf<String, Any>(
        "overrideBoldRange" to "(本日)",
        "overrideBoldColor" to "gold",
        "emphasis" to "",
        "size" to 18
    )

    // ── range ──

    @Test
    fun `bound range resolves to the pattern the data holds`() {
        val attr = mapOf("range" to "@{overrideBoldRange}")
        assertEquals(
            "(本日)",
            DynamicTextComponent.resolvePartialRange(attr, "18:00 - 2:00 (本日)", data)
        )
    }

    @Test
    fun `bound range resolving to empty builds no partial`() {
        val attr = mapOf("range" to "@{emphasis}")
        assertNull(DynamicTextComponent.resolvePartialRange(attr, "any text", data))
    }

    @Test
    fun `static string range passes through unchanged without a context`() {
        // The resource-key half of the chain (binding -> string resource ->
        // literal) needs an Android context; with context = null the literal
        // must survive untouched — the pre-resource behavior every existing
        // literal pattern relies on. The resource half is pinned on-device
        // (a downstream login screen/registration, ja+en, 2026-08-09: key-form ranges
        // "terms_of_service"/"apply_for_membership" resolve and style).
        val range = DynamicTextComponent.resolvePartialRange(
            mapOf("range" to "Sam"), "Sample", emptyMap(), null
        )
        org.junit.Assert.assertEquals("Sam", range)
    }

    @org.junit.Test
    fun `static string range passes through unchanged`() {
        val attr = mapOf("range" to "(本日)")
        assertEquals(
            "(本日)",
            DynamicTextComponent.resolvePartialRange(attr, "18:00 - 2:00 (本日)", data)
        )
    }

    @Test
    fun `numeric range still extracts the substring`() {
        val attr = mapOf("range" to listOf(0, 5))
        assertEquals(
            "18:00",
            DynamicTextComponent.resolvePartialRange(attr, "18:00 - 2:00", data)
        )
    }

    // ── value slots ──

    @Test
    fun `bound fontColor resolves to the token, not the spelling`() {
        assertEquals("gold", DynamicTextComponent.resolvePartialString("@{overrideBoldColor}", data))
    }

    @Test
    fun `static fontColor passes through`() {
        assertEquals("#FF0000", DynamicTextComponent.resolvePartialString("#FF0000", data))
    }

    @Test
    fun `unresolvable binding yields null, never the spelling`() {
        assertNull(DynamicTextComponent.resolvePartialString("@{missing}", data))
    }

    @Test
    fun `bound fontSize resolves as a number`() {
        assertEquals(18, DynamicTextComponent.resolvePartialInt("@{size}", data))
        assertEquals(20, DynamicTextComponent.resolvePartialInt(20, data))
    }
}
