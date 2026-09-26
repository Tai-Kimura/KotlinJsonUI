package com.kotlinjsonui.dynamic.helpers

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The two decisions DynamicView composes around for `userInteractionEnabled`
 * (InteractionMarking): what a node drawn inside a stop reads, and whether a
 * node provides the stop to what it composes. The composition itself — the
 * CompositionLocalProvider around a stopping node, a Collection's cell and an
 * Embed's screen composed under it — is DynamicViewInteractionStopTest's
 * (androidTest, compiled here, run on a device).
 */
class InteractionMarkingTest {

    private fun node(json: String): JsonObject = JsonParser.parseString(json).asJsonObject

    private val label = node("""{"type": "Label", "id": "t", "text": "Open", "onClick": "@{onOpen}"}""")

    @Test
    fun aNodeDrawnInsideAStopHasNoTapNoRoleNoLongPress() {
        val drawn = InteractionMarking.nodeAsDrawn(label, stoppedAround = true)
        assertTrue(TapAccessibility.stoppedAround(drawn))
        assertFalse(ModifierBuilder.tapGateOpen(drawn, emptyMap()))
        assertFalse(TapAccessibility.isButton(drawn))
        val hold = InteractionMarking.nodeAsDrawn(
            node("""{"type": "Image", "id": "i", "src": "x", "onLongPress": "@{onHold}"}"""), stoppedAround = true
        )
        assertFalse(TapAccessibility.hasLongPress(hold))
        assertTrue(ModifierBuilder.gesturesShut(hold, emptyMap()))
        assertFalse(ImageAccessibility.isTappable(hold))
    }

    @Test
    fun aNodeDrawnOutsideAStopIsTheNodeAsWritten() {
        val drawn = InteractionMarking.nodeAsDrawn(label, stoppedAround = false)
        assertSame(label, drawn)
        assertTrue(ModifierBuilder.tapGateOpen(drawn, emptyMap()))
        assertTrue(TapAccessibility.isButton(drawn))
    }

    @Test
    fun falseAndABindingThatIsFalseStopWhatTheNodeComposes() {
        val stopping = node("""{"type": "View", "userInteractionEnabled": false}""")
        val bound = node("""{"type": "View", "userInteractionEnabled": "@{u}"}""")
        assertTrue(InteractionMarking.stopsWhatItComposes(stopping, emptyMap(), stoppedAround = false))
        assertTrue(InteractionMarking.stopsWhatItComposes(bound, mapOf("u" to false), stoppedAround = false))
        assertFalse(InteractionMarking.stopsWhatItComposes(bound, mapOf("u" to true), stoppedAround = false))
        assertFalse(InteractionMarking.stopsWhatItComposes(node("""{"type": "View"}"""), emptyMap(), stoppedAround = false))
        assertFalse(
            InteractionMarking.stopsWhatItComposes(node("""{"type": "View", "userInteractionEnabled": true}"""), emptyMap(), false)
        )
    }

    @Test
    fun insideAStopAlreadyNothingMoreIsProvided() {
        val stopping = node("""{"type": "View", "userInteractionEnabled": false}""")
        assertFalse(InteractionMarking.stopsWhatItComposes(stopping, emptyMap(), stoppedAround = true))
    }

    @Test
    fun theMarkIsOnACopy() {
        InteractionMarking.nodeAsDrawn(label, stoppedAround = true)
        assertFalse(label.has(TapAccessibility.STOPPED_KEY))
        assertEquals("Open", InteractionMarking.nodeAsDrawn(label, true).get("text").asString)
    }
}
