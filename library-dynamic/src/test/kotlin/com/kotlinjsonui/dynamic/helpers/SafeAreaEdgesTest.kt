package com.kotlinjsonui.dynamic.helpers

import com.google.gson.Gson
import com.google.gson.JsonObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * `safeAreaInsetPositions` is declared on BOTH SafeAreaView and View — the SSoT
 * says so in as many words, because SafeAreaView is its own definition section
 * and does not inherit View's. Only the SafeAreaView component read it, so a
 * plain View naming the edges reserved nothing.
 *
 * The vocabulary is shared rather than copied, so the two components cannot
 * come to disagree about what `start` means or which spelling wins.
 */
class SafeAreaEdgesTest {

    private fun node(json: String): JsonObject =
        Gson().fromJson(json, JsonObject::class.java)

    @Test
    fun theCanonicalRowIsRead() {
        assertEquals(
            listOf("top", "bottom"),
            SafeAreaEdges.requested(node("{}"), listOf("top", "bottom"))
        )
    }

    @Test
    fun theLegacyEdgesSpellingWinsWhenBothAreWritten() {
        // The priority the SafeAreaView path has always had.
        assertEquals(
            listOf("top"),
            SafeAreaEdges.requested(node("""{"edges":["top"]}"""), listOf("bottom"))
        )
    }

    @Test
    fun aNodeThatNamesNoEdgeAsksForNothing() {
        // Only SafeAreaView defaults to `all`; a plain View that says nothing
        // must reserve nothing, which is why the default is NOT in here.
        assertNull(SafeAreaEdges.requested(node("{}"), null))
        assertNull(SafeAreaEdges.requested(node("""{"edges":[]}"""), null))
        assertNull(SafeAreaEdges.requested(node("{}"), emptyList()))
    }

    @Test
    fun unknownEdgeNamesAreDropped() {
        assertEquals(
            listOf("top"),
            SafeAreaEdges.requested(node("{}"), listOf("top", "sideways"))
        )
        assertNull(SafeAreaEdges.requested(node("{}"), listOf("sideways")))
    }

    @Test
    fun anEnclosingSafeAreaViewRemovesTheEdgeItAlreadyConsumed() {
        assertEquals(
            listOf("top"),
            SafeAreaEdges.filtered(listOf("top", "bottom"), ignoreTop = false, ignoreBottom = true)
        )
    }

    @Test
    fun allExpandsSoTheFilterHasSomethingToRemove() {
        // `all` minus bottom is not `all`; it has to become the remainder or
        // the filter silently keeps reserving the edge it was told to skip.
        assertEquals(
            listOf("top", "start", "end"),
            SafeAreaEdges.filtered(listOf("all"), ignoreTop = false, ignoreBottom = true)
        )
        assertEquals(
            listOf("start", "end"),
            SafeAreaEdges.filtered(listOf("all"), ignoreTop = true, ignoreBottom = true)
        )
    }

    @Test
    fun allSurvivesWhenNothingIsIgnored() {
        assertEquals(
            listOf("all"),
            SafeAreaEdges.filtered(listOf("all"), ignoreTop = false, ignoreBottom = false)
        )
    }
}
