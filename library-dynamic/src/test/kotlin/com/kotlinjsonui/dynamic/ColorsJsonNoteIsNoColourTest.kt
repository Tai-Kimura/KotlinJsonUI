package com.kotlinjsonui.dynamic

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * A `_` key in colors.json (`_comment`) is a note, not a colour.
 *
 * jsonui-cli writes the notes back where the author put them — at the top of
 * the file or inside a palette — and the generated ColorManager has no colour
 * of that name. ResourceCache read a palette's note, and a flat file's, as a
 * colour, so a layout naming it resolved to the note's text.
 */
class ColorsJsonNoteIsNoColourTest {

    @Test
    fun aNoteInTheActivePaletteIsNoColour() {
        val colors = ResourceCache.parseColorsJson(
            """{"_comment":"file","modes":["light","dark"],"fallback_mode":"light",
               "light":{"_comment":"light note","ink":"#111111"},"dark":{"ink":"#EEEEEE"}}"""
        )
        assertEquals(mapOf("ink" to "#111111"), colors)
    }

    @Test
    fun aNoteOnTopOfAFlatFileIsNoColour() {
        assertEquals(mapOf("ink" to "#111111"), ResourceCache.parseColorsJson("""{"_comment":"n","ink":"#111111"}"""))
    }

    @Test
    fun controlEveryColourIsKept() {
        assertEquals(
            mapOf("ink" to "#111111", "paper" to "#FFFFFF"),
            ResourceCache.parseColorsJson("""{"fallback_mode":"light","light":{"ink":"#111111","paper":"#FFFFFF"}}""")
        )
    }
}
