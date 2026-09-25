package com.kotlinjsonui.dynamic

import com.google.gson.JsonParser
import com.google.gson.JsonObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Assert.fail
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * TypeSynonyms reads the vendored copy of jsonui-cli's type_synonyms.json
 * from the classpath (CI compares the copy with the pinned jsonui-cli ref).
 * The expected entries are counted from the file on disk, a second reader,
 * not written out here.
 */
class TypeSynonymsTest {

    private val onDisk = JsonParser.parseString(
        File("src/main/resources/com/kotlinjsonui/dynamic/type_synonyms.json").readText()
    ).asJsonObject.getAsJsonObject("synonyms")

    @Test
    fun `every entry of the vendored file is read`() {
        assertTrue("the vendored file lists no synonyms", onDisk.size() >= 40)
        assertEquals(onDisk.keySet().map { it.lowercase() }.toSet(), TypeSynonyms.entries.keys)
        for ((spelling, value) in onDisk.entrySet()) {
            val entry = TypeSynonyms.entries.getValue(spelling.lowercase())
            assertEquals(spelling, value.asJsonObject.get("canonical").asString, entry.canonical)
            assertEquals(spelling, value.asJsonObject.get("render_as")?.asString, entry.renderAs)
        }
    }

    @Test
    fun `a synonym is drawn as its target, whatever its case, and anything else as itself`() {
        assertEquals("Progress", TypeSynonyms.drawnAs("ProgressBar"))
        assertEquals("Progress", TypeSynonyms.drawnAs("progressbar"))
        // render_as: drawn by the converter it names, not the canonical one
        assertEquals("CircleImage", TypeSynonyms.drawnAs("CircleImageView"))
        assertEquals("Label", TypeSynonyms.drawnAs("Label"))
        assertEquals("ProbeCustomType", TypeSynonyms.drawnAs("ProbeCustomType"))
    }

    @After
    fun clearSink() { TypeSynonyms.warningSink = null }

    private fun node(json: String): JsonObject = JsonParser.parseString(json).asJsonObject

    @Test
    fun `a synonym node is drawn as its target with the attributes its spelling means`() {
        val drawn = TypeSynonyms.canonicalize(node("""{"type": "HStack", "id": "h", "child": []}"""))
        assertEquals("View", drawn.get("type").asString)
        assertEquals("horizontal", drawn.get("orientation").asString)
        assertEquals("h", drawn.get("id").asString)
        assertEquals("vertical", TypeSynonyms.canonicalize(node("""{"type": "column"}""")).get("orientation").asString)
        // ZStack / Box mean a View without orientation: nothing is added
        assertFalse(TypeSynonyms.canonicalize(node("""{"type": "ZStack"}""")).has("orientation"))
        assertEquals("CircleImage", TypeSynonyms.canonicalize(node("""{"type": "CircleImageView"}""")).get("type").asString)
    }

    @Test
    fun `the node's own value wins over the spelling's, and a warning names both`() {
        val warnings = mutableListOf<String>()
        TypeSynonyms.warningSink = { warnings += it }
        val original = node("""{"type": "Row", "orientation": "vertical"}""")
        val drawn = TypeSynonyms.canonicalize(original)
        assertEquals("View", drawn.get("type").asString)
        assertEquals("vertical", drawn.get("orientation").asString)
        assertEquals(1, warnings.size)
        assertTrue(warnings[0], warnings[0].contains("Row") && warnings[0].contains("horizontal") && warnings[0].contains("vertical"))
        // the node as written is not changed: the app's handler was given it
        assertEquals("Row", original.get("type").asString)
        assertNotSame(original, drawn)
    }

    @Test
    fun `a type that is not a synonym is the node itself`() {
        val n = node("""{"type": "View", "orientation": "vertical"}""")
        assertSame(n, TypeSynonyms.canonicalize(n))
        val undeclared = node("""{"type": "Triangle"}""")
        assertSame(undeclared, TypeSynonyms.canonicalize(undeclared))
    }

    @Test
    fun `a missing table throws, naming the resource`() {
        try {
            TypeSynonyms.parse(null)
            fail("a missing table was read as empty")
        } catch (e: IllegalStateException) {
            assertTrue(e.message!!, e.message!!.contains(TypeSynonyms.RESOURCE))
        }
    }
}
