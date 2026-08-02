package com.kotlinjsonui.dynamic.helpers

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Verifies that the tool-emitted `gravity` attribute is parsed into the
 * same alignment flags that the library already honors via individual
 * `alignTop` / `centerHorizontal` / etc. booleans.
 */
class ModifierBuilderGravityTest {

    @Test
    fun parseGravity_nullWhenAbsent() {
        assertNull(ModifierBuilder.parseGravity(JsonObject()))
    }

    @Test
    fun parseGravity_stringSingleToken() {
        val json = JsonObject().apply { addProperty("gravity", "top") }
        val flags = ModifierBuilder.parseGravity(json)!!
        assertTrue(flags.alignTop)
        assertFalse(flags.alignBottom)
        assertFalse(flags.centerInParent)
    }

    @Test
    fun parseGravity_pipeSeparated() {
        val json = JsonObject().apply { addProperty("gravity", "top|left") }
        val flags = ModifierBuilder.parseGravity(json)!!
        assertTrue(flags.alignTop)
        assertTrue(flags.alignLeft)
    }

    @Test
    fun parseGravity_whitespaceSeparated() {
        val json = JsonObject().apply { addProperty("gravity", "centerHorizontal top") }
        val flags = ModifierBuilder.parseGravity(json)!!
        assertTrue(flags.centerH)
        assertTrue(flags.alignTop)
    }

    @Test
    fun parseGravity_array() {
        val json = JsonObject().apply {
            val arr = JsonArray()
            arr.add(JsonPrimitive("bottom"))
            arr.add(JsonPrimitive("right"))
            add("gravity", arr)
        }
        val flags = ModifierBuilder.parseGravity(json)!!
        assertTrue(flags.alignBottom)
        assertTrue(flags.alignRight)
    }

    @Test
    fun parseGravity_centerMapsToCenterInParent() {
        val json = JsonObject().apply { addProperty("gravity", "center") }
        val flags = ModifierBuilder.parseGravity(json)!!
        assertTrue(flags.centerInParent)
    }

    @Test
    fun parseGravity_startEndAreRTLAliases() {
        val json = JsonObject().apply { addProperty("gravity", "start|end") }
        val flags = ModifierBuilder.parseGravity(json)!!
        assertTrue(flags.alignLeft)
        assertTrue(flags.alignRight)
    }

    @Test
    fun parseGravity_unknownTokenIgnored() {
        val json = JsonObject().apply { addProperty("gravity", "top|bogus") }
        val flags = ModifierBuilder.parseGravity(json)!!
        assertTrue(flags.alignTop)
        assertFalse(flags.alignBottom)
    }

    @Test
    fun resolvedAlignFlags_outerBooleansDoNotLeakIntoContent() {
        // Declaration-faithful (2026-08-02, parity family
        // kjui-dynamic-alignment): alignLeft means "align to PARENT left"
        // (attribute_definitions.json) and must not reposition the node's
        // own children — the flags here feed the container's CONTENT
        // alignment only. The old contract merged the two additively.
        val json = JsonObject().apply {
            addProperty("gravity", "centerVertical")
            addProperty("alignLeft", true)
        }
        val flags = ModifierBuilder.resolvedAlignFlags(json)
        assertTrue(flags.centerV)
        assertFalse(flags.alignLeft)
        assertFalse(flags.alignRight)
    }

    @Test
    fun resolvedAlignFlags_readsTheAlignmentStringAlternative() {
        // `alignment` is the SwiftUI-spelled alternative to gravity and was
        // previously never read at all. SwiftUI reading: `top` is
        // top-and-horizontally-centred.
        val json = JsonObject().apply { addProperty("alignment", "top") }
        val flags = ModifierBuilder.resolvedAlignFlags(json)
        assertTrue(flags.alignTop)
        assertTrue(flags.centerH)
        assertFalse(flags.alignBottom)

        val center = JsonObject().apply { addProperty("alignment", "center") }
        assertTrue(ModifierBuilder.resolvedAlignFlags(center).centerInParent)

        // gravity wins when both are set (same precedence as the static side)
        val both = JsonObject().apply {
            addProperty("gravity", "bottom")
            addProperty("alignment", "top")
        }
        val bothFlags = ModifierBuilder.resolvedAlignFlags(both)
        assertTrue(bothFlags.alignBottom)
        assertFalse(bothFlags.alignTop)
    }

    @Test
    fun resolvedAlignFlags_emptyWhenNothingSet() {
        val flags = ModifierBuilder.resolvedAlignFlags(JsonObject())
        assertEquals(ModifierBuilder.AlignFlags(), flags)
    }
}
