package com.kotlinjsonui.dynamic.helpers

import com.google.gson.JsonObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * `common.clipToBounds`.
 *
 * Compose does not clip a layout's children by default, so an overflowing
 * row/column drew past its declared box on the dynamic path while the codegen
 * path emitted `.clipToBounds()` (`modifier_builder.rb`, end of
 * `build_background`). Plan 34 measured `common/clipToBounds` pixel-identical
 * to its control on android.
 *
 * The attribute is declared binding-capable, and the Ruby `if json_data[...]`
 * is truthy for the binding STRING itself — resolving it here keeps a bound
 * `false` off instead of frozen on.
 */
class ModifierBuilderClipToBoundsTest {

    private fun node(value: Any?): JsonObject = JsonObject().apply {
        when (value) {
            null -> {}
            is Boolean -> addProperty("clipToBounds", value)
            is String -> addProperty("clipToBounds", value)
        }
    }

    @Test
    fun absentIsNull() {
        assertNull(ModifierBuilder.resolveClipToBounds(JsonObject(), emptyMap()))
    }

    @Test
    fun literalTrue() {
        assertEquals(true, ModifierBuilder.resolveClipToBounds(node(true), emptyMap()))
    }

    @Test
    fun literalFalse() {
        assertEquals(false, ModifierBuilder.resolveClipToBounds(node(false), emptyMap()))
    }

    @Test
    fun stringTrue() {
        assertEquals(true, ModifierBuilder.resolveClipToBounds(node("true"), emptyMap()))
    }

    @Test
    fun bindingResolvesTrue() {
        assertEquals(
            true,
            ModifierBuilder.resolveClipToBounds(node("@{clip}"), mapOf("clip" to true))
        )
    }

    @Test
    fun bindingResolvesFalseRatherThanFreezingOn() {
        assertEquals(
            false,
            ModifierBuilder.resolveClipToBounds(node("@{clip}"), mapOf("clip" to false))
        )
    }
}
