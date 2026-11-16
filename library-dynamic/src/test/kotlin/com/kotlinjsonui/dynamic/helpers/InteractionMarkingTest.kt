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

    /**
     * A control a stop holds writes nothing: `updateData`, which every
     * control writes its value through, is a no-op for it — inside a stop
     * (the local), or under its own flag false or bound false. Beside no stop,
     * and for what is not a control (a View, a container), the data is the
     * data as given (the same map).
     */
    @Test
    fun aControlAStopHoldsWritesNothing() {
        var writes = 0
        val data: Map<String, Any> = mapOf("updateData" to { _: Map<String, Any> -> writes++ }, "u" to false)
        @Suppress("UNCHECKED_CAST")
        fun write(d: Map<String, Any>) = (d["updateData"] as (Map<String, Any>) -> Unit)(mapOf("on" to true))
        val switch = node("""{"type": "Switch", "id": "s", "isOn": "@{on}"}""")

        write(InteractionMarking.dataAsDrawn(switch, data, stoppedAround = true))
        write(InteractionMarking.dataAsDrawn(node("""{"type": "Switch", "userInteractionEnabled": false}"""), data, stoppedAround = false))
        write(InteractionMarking.dataAsDrawn(node("""{"type": "Slider", "userInteractionEnabled": "@{u}"}"""), data, stoppedAround = false))
        assertEquals(0, writes)

        assertSame(data, InteractionMarking.dataAsDrawn(switch, data, stoppedAround = false))
        assertSame(data, InteractionMarking.dataAsDrawn(node("""{"type": "View"}"""), data, stoppedAround = true))
        assertSame(data, InteractionMarking.dataAsDrawn(node("""{"type": "Collection"}"""), data, stoppedAround = true))
        val open = data + ("u" to true)
        assertSame(open, InteractionMarking.dataAsDrawn(node("""{"type": "Switch", "userInteractionEnabled": "@{u}"}"""), open, stoppedAround = false))
        write(InteractionMarking.dataAsDrawn(switch, data, stoppedAround = false))
        assertEquals(1, writes)
    }

    /** The controls and the containers, as the rule's `control?` reads them. */
    @Test
    fun aControlIsAnInteractiveTypeOperatedWhereItIs() {
        for (t in listOf("Switch", "Toggle", "CheckBox", "Radio", "Segment", "Slider", "SelectBox", "TextField", "TextView", "Button")) {
            assertTrue(t, TapAccessibility.isControl(t))
        }
        for (t in listOf("TabView", "ScrollView", "Collection", "TableView", "Web", "Embed", "View", "Label", "AppCard")) {
            assertFalse(t, TapAccessibility.isControl(t))
        }
    }

    /**
     * The nodes inside a stopped control a user operates on their own — a
     * Radio's rows and RadioButtons, a Segment's Tabs, a CheckBox's Checkbox
     * — read `disabled()` as the control's root does
     * (ModifierBuilder.stoppedItem): under the control's own flag, a bound
     * one that is false, or the mark of a stop around it; nothing otherwise.
     * What the semantics do on a device is A11yActivationInsideAStopProbe's
     * (androidTest, aStoppedWrapperControlsItemsReadDisabled).
     */
    @Test
    fun aStoppedControlsItemsReadDisabledAndNothingElseDoes() {
        val segment = node("""{"type": "Segment", "id": "s", "items": ["a", "b"]}""")
        assertSame(androidx.compose.ui.Modifier, ModifierBuilder.stoppedItem(segment, emptyMap()))
        val own = node("""{"type": "Segment", "id": "s", "items": ["a", "b"], "userInteractionEnabled": false}""")
        assertTrue(ModifierBuilder.stoppedItem(own, emptyMap()) !== androidx.compose.ui.Modifier)
        val bound = node("""{"type": "Segment", "id": "s", "items": ["a", "b"], "userInteractionEnabled": "@{u}"}""")
        assertTrue(ModifierBuilder.stoppedItem(bound, mapOf("u" to false)) !== androidx.compose.ui.Modifier)
        assertSame(androidx.compose.ui.Modifier, ModifierBuilder.stoppedItem(bound, mapOf("u" to true)))
        val marked = InteractionMarking.nodeAsDrawn(segment, stoppedAround = true)
        assertTrue(ModifierBuilder.stoppedItem(marked, emptyMap()) !== androidx.compose.ui.Modifier)
    }
}
