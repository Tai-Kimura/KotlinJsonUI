package com.kotlinjsonui.dynamic.helpers

import com.google.gson.Gson
import com.google.gson.JsonObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Dimension slots must resolve `@{binding}` BEFORE parsing a number.
 *
 * `element.asFloat` on `"@{boundCornerRadius}"` throws NumberFormatException.
 * On the codegen path the equivalent mistake is a compile error and C fixed it
 * with `BoundValue.dp`; on the dynamic path it is a CRASH at render time, and
 * it took the whole android lane of the 3PF re-measurement down — the suite
 * never produced a results file:
 *
 *     java.lang.NumberFormatException: For input string: "@{boundCornerRadius}"
 *     java.lang.NumberFormatException: For input string: "@{boundPaddingTop}"
 *
 * Two properties are pinned here, and the first matters more than the second:
 * an unresolvable binding must DEGRADE (to null / the caller's default), never
 * throw. A layout that names data the host does not carry has to render
 * without that attribute, not take the screen with it.
 */
class BoundDimensionTest {

    private fun el(json: String) = Gson().fromJson(json, JsonObject::class.java).get("v")

    // ── never throws ─────────────────────────────────────────────────

    @Test
    fun bindingWithNoDataDegradesInsteadOfThrowing() {
        assertNull(ModifierBuilder.dimen(el("""{"v":"@{boundCornerRadius}"}"""), emptyMap()))
    }

    @Test
    fun bindingResolvingToANonNumberDegrades() {
        assertNull(
            ModifierBuilder.dimen(
                el("""{"v":"@{boundPaddingTop}"}"""), mapOf("boundPaddingTop" to "not a number")
            )
        )
    }

    @Test
    fun nonNumericStringDegrades() {
        assertNull(ModifierBuilder.dimen(el("""{"v":"matchParent"}"""), emptyMap()))
    }

    @Test
    fun objectAndArrayShapesDegrade() {
        assertNull(ModifierBuilder.dimen(el("""{"v":{"a":1}}"""), emptyMap()))
        assertNull(ModifierBuilder.dimen(el("""{"v":[1,2]}"""), emptyMap()))
    }

    @Test
    fun absentIsNull() {
        assertNull(ModifierBuilder.dimen(null, emptyMap()))
        assertNull(ModifierBuilder.dimen(el("""{"v":null}"""), emptyMap()))
    }

    // ── resolves what it can ─────────────────────────────────────────

    @Test
    fun literalNumber() {
        assertEquals(12f, ModifierBuilder.dimen(el("""{"v":12}"""), emptyMap()))
    }

    @Test
    fun numericString() {
        assertEquals(12f, ModifierBuilder.dimen(el("""{"v":"12"}"""), emptyMap()))
    }

    @Test
    fun bindingResolvesThroughData() {
        assertEquals(
            16f,
            ModifierBuilder.dimen(el("""{"v":"@{boundPaddingTop}"}"""), mapOf("boundPaddingTop" to 16))
        )
    }

    @Test
    fun bindingResolvesANumericStringValue() {
        assertEquals(
            16f,
            ModifierBuilder.dimen(el("""{"v":"@{boundWeight}"}"""), mapOf("boundWeight" to "16"))
        )
    }

    // ── the spellings the crashing run named ─────────────────────────

    @Test
    fun everySpellingTheCrashNamedResolves() {
        val spellings = listOf(
            "boundCornerRadius", "boundBorderWidth",
            "boundPaddingTop", "boundPaddingBottom", "boundPaddingLeft"
        )
        for (name in spellings) {
            val value = ModifierBuilder.dimen(el("""{"v":"@{$name}"}"""), mapOf(name to 8))
            assertEquals("binding $name did not resolve", 8f, value)
        }
    }

    // ── weight goes through the same guard ────────────────────────────

    @Test
    fun weightAcceptsABindingAndStillRejectsZero() {
        val bound = Gson().fromJson("""{"weight":"@{boundWeight}"}""", JsonObject::class.java)
        assertEquals(2f, ModifierBuilder.getWeight(bound, mapOf("boundWeight" to 2)))
        assertNull(ModifierBuilder.getWeight(bound, mapOf("boundWeight" to 0)))
        assertNull(ModifierBuilder.getWeight(bound, emptyMap()))
    }
}
