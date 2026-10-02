package com.kotlinjsonui.dynamic

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.FileNotFoundException
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * What an included layout reads, answered by jsonui-cli's shared fixture
 * (`shared/core/include_maps_fixture.json`, copied byte-identical into
 * `src/test/resources/`; CI's vendored-attr-guard job compares the copy with
 * the file at the pinned jsonui-cli ref): the including layout's data, with
 * the include node's object maps over it — shared_data, then data (ruling
 * 2026-10-02). jsonui-cli reads the same file against its Python, sjui, kjui
 * and rjui expanders, so a pass here is the Dynamic runtime drawing what the
 * generated code draws. Until 2.43.1 this expander dropped an object map.
 *
 * `expected` is, for every node of the expanded screen with a `text` or a
 * `visibility`, that value as JSON — a whole binding replaced by a literal
 * keeps the literal's type (`false` stays `false`).
 */
class IncludeMapsSharedFixtureTest {

    @After
    fun resetSeam() {
        IncludeExpander.layoutReader = null
    }

    private fun loadFixture(): JsonObject {
        val stream = requireNotNull(
            javaClass.classLoader?.getResourceAsStream("include_maps_fixture.json")
        ) { "include_maps_fixture.json missing from test resources" }
        return stream.reader(Charsets.UTF_8).use { JsonParser.parseReader(it).asJsonObject }
    }

    private fun drawn(node: JsonObject, out: MutableMap<String, JsonElement>) {
        if (node.has("id")) {
            for (key in listOf("text", "visibility")) {
                node.get(key)?.let { out[node.get("id").asString] = it }
            }
        }
        val child = node.get("child") ?: return
        when {
            child.isJsonArray -> child.asJsonArray.forEach { if (it.isJsonObject) drawn(it.asJsonObject, out) }
            child.isJsonObject -> drawn(child.asJsonObject, out)
        }
    }

    private fun expandedDrawn(layouts: JsonObject, screen: String): Map<String, JsonElement> {
        val assets = layouts.entrySet().associate { (path, layout) -> "Layouts/$path" to layout.toString() }
        IncludeExpander.layoutReader = { name ->
            IncludeExpander.readLayout(name) { path ->
                (assets[path] ?: throw FileNotFoundException(path)).byteInputStream()
            }
        }
        val expanded = IncludeExpander.processIncludes(layouts.getAsJsonObject(screen).deepCopy())
        return LinkedHashMap<String, JsonElement>().also { drawn(expanded, it) }
    }

    @Test
    fun everySpecimenDrawsTheSharedFixturesValues() {
        val fixture = loadFixture()
        val screen = fixture.get("screen").asString
        val specimens = fixture.getAsJsonObject("specimens")
        assertTrue("the fixture has no specimens", specimens.size() > 0)
        for (name in specimens.keySet().sorted()) {
            val specimen = specimens.getAsJsonObject(name)
            val expected = specimen.getAsJsonObject("expected").entrySet().associate { (k, v) -> k to v }
            assertEquals("[$name]", expected, expandedDrawn(specimen.getAsJsonObject("layouts"), screen))
        }
    }
}
