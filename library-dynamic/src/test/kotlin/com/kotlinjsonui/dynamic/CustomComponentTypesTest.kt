package com.kotlinjsonui.dynamic

import com.google.gson.JsonParser
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.dynamic.helpers.LayoutPath
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * The app's own component types (Configuration.customComponentTypes) are
 * read as written by what classifies a node by its type — its viewId, its tap
 * and image roles (TypeSynonyms.drawnType) — as kjui's codegen reads the
 * app's converters first. Until 1.9.0 there was no list: an app's own
 * ProgressBar was drawn by the app's handler and named `progress_<path>`, as
 * the built-in Progress, while the codegen named it `progressBar_<path>`.
 *
 * A handler that draws a type the list does not name (a handler written by
 * hand) is said once per type, with the way to fix it.
 */
class CustomComponentTypesTest {
    private var saved: Set<String> = emptySet()
    private val warnings = mutableListOf<String>()

    @Before
    fun save() {
        saved = Configuration.customComponentTypes
        TypeSynonyms.warningSink = { warnings += it }
    }

    @After
    fun restore() {
        Configuration.customComponentTypes = saved
        TypeSynonyms.warningSink = null
    }

    private val progressBar = JsonParser.parseString("""{"type": "ProgressBar", "_layoutPath": "0_16"}""").asJsonObject

    @Test
    fun `a type the app lists is read as written, its viewId too`() {
        Configuration.customComponentTypes = emptySet()
        assertEquals("Progress", TypeSynonyms.drawnType("ProgressBar"))
        assertEquals("progress_0_16", LayoutPath.viewId(progressBar))
        Configuration.customComponentTypes = setOf("ProgressBar")
        assertEquals("ProgressBar", TypeSynonyms.drawnType("ProgressBar"))
        assertEquals("progressBar_0_16", LayoutPath.viewId(progressBar))
        // a spelling the list does not name is still read as the built-in
        assertEquals("Slider", TypeSynonyms.drawnType("SeekBar"))
    }

    @Test
    fun `a type the handler drew is said once when the list does not name it, and not when it does`() {
        // Each spelling here is used by this test only: the once-per-type
        // memory lasts for the process.
        Configuration.customComponentTypes = setOf("Slider")
        TypeSynonyms.noteDrawnByApp("Slider")
        assertEquals("listed: nothing said", emptyList<String>(), warnings)

        Configuration.customComponentTypes = emptySet()
        TypeSynonyms.noteDrawnByApp("SeekBar")
        TypeSynonyms.noteDrawnByApp("SeekBar")
        assertEquals("unlisted: said once", 1, warnings.size)
        val said = warnings[0]
        assertTrue(said, said.startsWith("Custom component 'SeekBar' was drawn by the app but is not in Configuration.customComponentTypes"))
        assertTrue(said, said.contains("follow the built-in 'Slider'"))
        assertTrue(said, said.contains("Run `jui g converter SeekBar` again, or add 'SeekBar' to Configuration.customComponentTypes."))

        // A type no built-in reads otherwise is said without a built-in to follow.
        TypeSynonyms.noteDrawnByApp("ProbeOwnWidget")
        assertEquals(2, warnings.size)
        assertTrue(warnings[1], !warnings[1].contains("follow the built-in"))
    }
}
