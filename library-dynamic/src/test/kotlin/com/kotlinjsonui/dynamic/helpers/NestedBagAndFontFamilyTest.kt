package com.kotlinjsonui.dynamic.helpers

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Two rules the SSoT wrote down on 2026-08-05 after the 3PF sweep.
 *
 * 1. A nested attribute bag (`hintAttributes`, `labelAttributes`) OUTRANKS the
 *    flat spelling for the same fact: a bag scoped to one part of a component
 *    is the more specific statement, which is the ordinary cascade rule. All
 *    four readers agreed; the single converter that had it backwards was
 *    contradicting its own comment.
 *
 * 2. `fontFamily: "serif"` names the PLATFORM's serif face, not a font file
 *    called `serif`. Resolving the generic families against `res/font` finds
 *    nothing and silently falls back to the default, which is how a declared
 *    family rendered identically to no declaration at all.
 *
 * The `res/font` lookup itself needs a Context and belongs to the instrumented
 * layer; the vocabulary and the cascade are pinned here.
 */
class NestedBagAndFontFamilyTest {

    // ── the cascade ──────────────────────────────────────────────────

    @Test
    fun nestedKeyIsReadFromTheBag() {
        val bag = mapOf<String, Any?>("fontColor" to "#FF0000", "fontSize" to 24.0)
        assertEquals("#FF0000", ResourceResolver.nestedString(bag, "fontColor"))
        assertEquals(24.0, ResourceResolver.nestedNumber(bag, "fontSize"))
    }

    @Test
    fun absentBagAndAbsentKeyBothYieldNull() {
        assertNull(ResourceResolver.nestedString(null, "fontColor"))
        assertNull(ResourceResolver.nestedNumber(null, "fontSize"))
        assertNull(ResourceResolver.nestedString(emptyMap(), "fontColor"))
        assertNull(ResourceResolver.nestedNumber(emptyMap(), "fontSize"))
    }

    @Test
    fun aWrongTypeInTheBagYieldsNullSoTheFlatSpellingStillWins() {
        // The cascade is `nested ?: flat`; a bag holding the wrong shape must
        // not swallow the flat value by returning a non-null nonsense.
        val bag = mapOf<String, Any?>("fontColor" to 42, "fontSize" to "big")
        assertNull(ResourceResolver.nestedString(bag, "fontColor"))
        assertNull(ResourceResolver.nestedNumber(bag, "fontSize"))
    }

    @Test
    fun anyNumberShapeIsAccepted() {
        // gson hands numbers over as Double; a hand-built map may carry Int.
        assertEquals(24.0, ResourceResolver.nestedNumber(mapOf("fontSize" to 24), "fontSize"))
        assertEquals(24.0, ResourceResolver.nestedNumber(mapOf("fontSize" to 24.0f), "fontSize"))
    }

    // ── generic families ─────────────────────────────────────────────

    @Test
    fun genericFamiliesResolveWithoutAFontResource() {
        for (name in listOf("serif", "sans-serif", "monospace", "cursive")) {
            assertEquals(
                "generic family '$name' did not resolve",
                true,
                ResourceResolver.genericFontFamily(name) != null
            )
        }
    }

    @Test
    fun genericFamiliesAreCaseAndSpacingInsensitive() {
        assertEquals(
            ResourceResolver.genericFontFamily("serif"),
            ResourceResolver.genericFontFamily("  Serif ")
        )
    }

    @Test
    fun serifAndMonospaceAreDistinctFromSansSerif() {
        // A fixture can only discriminate if the families differ; sans-serif
        // IS the platform default, which is why the current fontFamily
        // fixtures cannot move a pixel no matter what this resolves to.
        val sans = ResourceResolver.genericFontFamily("sans-serif")
        assertEquals(false, sans == ResourceResolver.genericFontFamily("serif"))
        assertEquals(false, sans == ResourceResolver.genericFontFamily("monospace"))
    }

    @Test
    fun aCustomNameIsNotAGenericFamily() {
        // Falls through to the res/font lookup, which is the instrumented
        // layer's business.
        assertNull(ResourceResolver.genericFontFamily("Noto Sans JP"))
        assertNull(ResourceResolver.genericFontFamily(null))
    }
}
