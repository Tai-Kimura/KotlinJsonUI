package com.kotlinjsonui.dynamic.helpers

import com.google.gson.Gson
import com.google.gson.JsonObject
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The outer placement booleans, in all four states F pinned on the ios side.
 *
 * gson's `asBoolean` on a STRING primitive is `Boolean.parseBoolean`, so
 * `"@{boundAlignTop}"` read as `false` and the placement was dropped without a
 * word. That is the quiet half of the family the dimension crash made loud:
 * the same unresolved binding either throws (numbers) or silently means "no"
 * (booleans), and only the first one tells you.
 *
 * The four states matter separately because two different stages read them —
 * the container decides a ROUTE before the data map is consulted, and the
 * modifier applies a VALUE after. A binding has to count as declared in the
 * first and resolve in the second; a literal `false` must do neither.
 */
class BoundAlignmentTest {

    private fun node(json: String): JsonObject =
        Gson().fromJson(json, JsonObject::class.java)

    @Test
    fun aBoundTrueIsHonoured() {
        val flags = ModifierBuilder.outerAlignFlags(
            node("""{"alignTop":"@{boundAlignTop}"}"""), mapOf("boundAlignTop" to true)
        )
        assertTrue(flags.alignTop)
    }

    @Test
    fun aBoundFalseIsHonoured() {
        val flags = ModifierBuilder.outerAlignFlags(
            node("""{"alignTop":"@{boundAlignTop}"}"""), mapOf("boundAlignTop" to false)
        )
        assertFalse(flags.alignTop)
    }

    @Test
    fun aBoundValueTheDataDoesNotCarryFallsBackToTheAttributeDefault() {
        val flags = ModifierBuilder.outerAlignFlags(
            node("""{"alignTop":"@{boundAlignTop}"}"""), emptyMap()
        )
        assertFalse(flags.alignTop)
    }

    @Test
    fun literalTrueAndFalseAreUnchanged() {
        assertTrue(ModifierBuilder.outerAlignFlags(node("""{"alignTop":true}""")).alignTop)
        assertFalse(ModifierBuilder.outerAlignFlags(node("""{"alignTop":false}""")).alignTop)
    }

    @Test
    fun undeclaredIsFalse() {
        assertFalse(ModifierBuilder.outerAlignFlags(node("""{}""")).alignTop)
    }

    @Test
    fun everyOuterFlagGoesThroughTheSameResolution() {
        val spellings = listOf(
            "alignTop", "alignBottom", "alignLeft", "alignRight",
            "centerHorizontal", "centerVertical", "centerInParent"
        )
        for (key in spellings) {
            val flags = ModifierBuilder.outerAlignFlags(
                node("""{"$key":"@{b}"}"""), mapOf("b" to true)
            )
            val on = when (key) {
                "alignTop" -> flags.alignTop
                "alignBottom" -> flags.alignBottom
                "alignLeft" -> flags.alignLeft
                "alignRight" -> flags.alignRight
                "centerHorizontal" -> flags.centerH
                "centerVertical" -> flags.centerV
                else -> flags.centerInParent
            }
            assertTrue("bound $key was dropped", on)
        }
    }
}
