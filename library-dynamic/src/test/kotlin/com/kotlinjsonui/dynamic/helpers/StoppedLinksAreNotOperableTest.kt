package com.kotlinjsonui.dynamic.helpers

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * A Label's links stop with `userInteractionEnabled: false` — its own, or a
 * node's around it — so a tappable holding it does not count them as
 * something operable on its own: it is one button (COMBINE), as over plain
 * text. Links that are not stopped still count (NONE).
 *
 * The same row is in jsonui-cli's shared/core/tap_accessibility_vectors.json
 * from jsonui-cli 1.9.0 ("a Label with links inside a node with
 * userInteractionEnabled: false does not count: its links stop with it"); the
 * vendored copy here is pinned to an earlier ref, so the row is held here
 * until the pin moves (TapAccessibilityVectorsTest runs the table).
 */
class StoppedLinksAreNotOperableTest {
    private fun tappable(child: String): JsonObject = JsonParser.parseString(
        """{"type": "View", "id": "t", "onClick": "@{onOpen}", "child": [{"type": "Label", "id": "l", "text": "x"}, $child]}"""
    ).asJsonObject

    private val linked = """{"type": "Label", "id": "k", "text": "see https://example.com", "linkable": true}"""
    private val ranged = """{"type": "Label", "id": "k", "text": "Terms", "partialAttributes": [{"range": "Terms", "onClick": "@{onTerms}"}]}"""

    @Test
    fun stoppedLinksDoNotMakeTheTappableHoldAControl() {
        val cases = linkedMapOf(
            "linkable, not stopped (control)" to (tappable(linked) to TapAccessibility.Shape.NONE),
            "range, not stopped (control)" to (tappable(ranged) to TapAccessibility.Shape.NONE),
            "linkable in a View with false" to
                (tappable("""{"type": "View", "userInteractionEnabled": false, "child": [$linked]}""") to TapAccessibility.Shape.COMBINE),
            "range in a View with false" to
                (tappable("""{"type": "View", "userInteractionEnabled": false, "child": [$ranged]}""") to TapAccessibility.Shape.COMBINE),
            "linkable with its own false" to
                (tappable(linked.dropLast(1) + """, "userInteractionEnabled": false}""") to TapAccessibility.Shape.COMBINE),
        )
        assertEquals(cases.mapValues { it.value.second }, cases.mapValues { TapAccessibility.shape(it.value.first) })
    }
}
