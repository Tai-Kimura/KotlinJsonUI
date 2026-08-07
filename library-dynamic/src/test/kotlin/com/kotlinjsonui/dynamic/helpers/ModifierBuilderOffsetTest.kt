package com.kotlinjsonui.dynamic.helpers

import androidx.compose.ui.Modifier
import com.google.gson.JsonObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertSame
import org.junit.Test

/**
 * `common.offsetX` / `common.offsetY` — "Moves the view without changing the
 * space it occupies or the position of its siblings."
 *
 * The primitive is E's ruling (plan 51-E), which overrode what C and this lane
 * had agreed between ourselves: `absoluteOffset`, because `graphicsLayer`
 * leaves the hit area behind (breaking the `onClick` the same node declares)
 * and plain `offset` mirrors under RTL while `offsetX` is an absolute-axis
 * spelling like `leftMargin`.
 *
 * Modifier elements are plain data here, so the chain can be compared without
 * a composition: two chains built from equal inputs are equal, and an offset
 * that reached the chain makes it differ from the bare one.
 */
class ModifierBuilderOffsetTest {

    private fun node(vararg pairs: Pair<String, Any>): JsonObject = JsonObject().apply {
        pairs.forEach { (k, v) ->
            when (v) {
                is Number -> addProperty(k, v)
                is String -> addProperty(k, v)
            }
        }
    }

    private fun offsetOf(json: JsonObject, data: Map<String, Any> = emptyMap()): Modifier =
        ModifierBuilder.applyOffset(Modifier, json, data)

    @Test
    fun neitherSpellingLeavesTheChainUntouched() {
        // Not merely "equal": an attribute nobody declared must not add a
        // layout node at all.
        assertSame(Modifier, offsetOf(JsonObject()))
    }

    @Test
    fun aDeclaredOffsetReachesTheChain() {
        assertNotEquals(Modifier as Modifier, offsetOf(node("offsetX" to 12)))
        assertNotEquals(Modifier as Modifier, offsetOf(node("offsetY" to 12)))
    }

    @Test
    fun eitherSpellingAloneImpliesZeroForTheOther() {
        // Declared: "Pairs with offsetY; either one alone implies 0 for the
        // other." So a lone offsetX must build the same chain as the explicit
        // pair with a zero partner.
        assertEquals(
            offsetOf(node("offsetX" to 12, "offsetY" to 0)),
            offsetOf(node("offsetX" to 12))
        )
        assertEquals(
            offsetOf(node("offsetX" to 0, "offsetY" to 12)),
            offsetOf(node("offsetY" to 12))
        )
    }

    @Test
    fun theBoundFaceResolvesThroughTheDataMap() {
        // Declared `["number","binding"]`, and the bound face travels as the
        // `"@{expr}"` String — the shape that has been silently dropped all
        // over this codebase today.
        assertEquals(
            offsetOf(node("offsetX" to 12, "offsetY" to 34)),
            offsetOf(
                node("offsetX" to "@{dx}", "offsetY" to "@{dy}"),
                mapOf("dx" to 12, "dy" to 34)
            )
        )
    }

    @Test
    fun anUnresolvedBindingDoesNotInventAnOffset() {
        // The data map has no such key: nothing is known, so nothing moves.
        assertSame(Modifier, offsetOf(node("offsetX" to "@{missing}")))
    }
}
