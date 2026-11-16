package com.kotlinjsonui.dynamic.helpers

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * `userInteractionEnabled` gates the tap as canTap does (jsonui-cli
 * shared/core/tap_accessibility.rb): `false` — or a binding resolving false —
 * on the node, or on a node around it, attaches no click, and so no
 * Role.Button. A node around it reaches the node as [TapAccessibility.STOPPED_KEY],
 * which DynamicView writes under [LocalInteractionStopped] (the vector tests
 * walk the same marking); the node's own flag is resolved where the click is
 * attached (ModifierBuilder.tapGateOpen).
 */
class UserInteractionGatesTheTapTest {

    private fun node(json: String): JsonObject = JsonParser.parseString(json).asJsonObject

    private val label = """{"type": "Label", "id": "t", "text": "Open", "onClick": "@{onOpen}"}"""

    private fun label(extra: String) = node(label.dropLast(1) + ", $extra}")

    @Test
    fun withNoFlagTheTapIsOpenAndAButton() {
        assertTrue(ModifierBuilder.tapGateOpen(node(label), emptyMap()))
        assertTrue(TapAccessibility.isButton(node(label)))
    }

    @Test
    fun falseShutsTheTapAndTheRole() {
        val n = label("\"userInteractionEnabled\": false")
        assertFalse(ModifierBuilder.tapGateOpen(n, emptyMap()))
        assertFalse(TapAccessibility.isButton(n))
    }

    @Test
    fun trueLeavesItOpen() {
        val n = label("\"userInteractionEnabled\": true")
        assertTrue(ModifierBuilder.tapGateOpen(n, emptyMap()))
        assertTrue(TapAccessibility.isButton(n))
    }

    @Test
    fun aBindingFollowsItsValue() {
        val n = label("\"userInteractionEnabled\": \"@{u}\"")
        assertFalse(ModifierBuilder.tapGateOpen(n, mapOf("u" to false)))
        assertTrue(ModifierBuilder.tapGateOpen(n, mapOf("u" to true)))
        // The shape is the rule's static answer: the gate decides at run time.
        assertTrue(TapAccessibility.isButton(n))
    }

    /**
     * A Label's links are none under a stop (the Linkable Label ticket): the
     * mark from a stop around it — a cell drawn under one — takes them, as
     * the rule's `stopped` does for a node inside the same layout, and so
     * does the Label's own flag, asked with no stop around (holdsAControl
     * folds a child's flag into `stopped` before it asks, so the vectors do
     * not reach that clause). A binding is the gate's to decide at run time.
     */
    @Test
    fun aLinkedLabelMarkedAsInsideAStopOperatesNothing() {
        val linked = """{"type": "Label", "id": "k", "text": "see https://example.com", "linkable": true"""
        assertTrue(TapAccessibility.isOperable(node("$linked}")))
        assertFalse(TapAccessibility.isOperable(TapAccessibility.markStopped(node("$linked}"))))
        assertFalse(TapAccessibility.isOperable(node("$linked, \"userInteractionEnabled\": false}")))
        assertTrue(TapAccessibility.isOperable(node("$linked, \"userInteractionEnabled\": \"@{u}\"}")))
    }

    @Test
    fun aNodeMarkedAsInsideAStopHasNoTap() {
        val original = node(label)
        val marked = TapAccessibility.markStopped(original)
        assertFalse(ModifierBuilder.tapGateOpen(marked, emptyMap()))
        assertFalse(TapAccessibility.isButton(marked))
        assertEquals(null, TapAccessibility.shape(marked))
        // An image in it operates nothing (the image rule asks the tap rule).
        val image = TapAccessibility.markStopped(node("""{"type": "Image", "id": "i", "src": "x", "onClick": "@{onOpen}"}"""))
        assertFalse(ImageAccessibility.isTappable(image))
        // The mark is on a copy: the layout's own node is left as it was.
        assertFalse(original.has(TapAccessibility.STOPPED_KEY))
        assertTrue(ModifierBuilder.tapGateOpen(original, emptyMap()))
    }

    @Test
    fun canTapAndTheFlagAreBothGates() {
        assertFalse(ModifierBuilder.tapGateOpen(label("\"canTap\": false, \"userInteractionEnabled\": true"), emptyMap()))
        assertFalse(ModifierBuilder.tapGateOpen(label("\"canTap\": true, \"userInteractionEnabled\": false"), emptyMap()))
    }
}
