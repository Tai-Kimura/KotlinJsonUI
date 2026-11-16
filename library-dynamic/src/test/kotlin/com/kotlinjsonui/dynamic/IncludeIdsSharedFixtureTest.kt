package com.kotlinjsonui.dynamic

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.FileNotFoundException
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The ids an expanded screen carries, answered by jsonui-cli's shared fixture
 * (`shared/core/include_ids_fixture.json`, copied byte-identical into
 * `src/test/resources/`; CI's vendored-attr-guard job compares the copy with
 * the file at the pinned jsonui-cli ref). jsonui-cli reads the same file
 * against its Python expander and the generated SwiftUI / Compose / React
 * code, so a pass here is the Dynamic runtime landing on the ids they land on.
 *
 * `expected` is a SORTED LIST WITH DUPLICATES KEPT, and is compared as one: a
 * partial included twice under the same id carries its ids twice, and a set
 * would make that specimen read the same as including it once.
 *
 * Layouts are served through [IncludeExpander.readLayout], so a name goes
 * through the same candidate paths as on a device (`Layouts/<name>.json`,
 * from the layouts root) before it reaches the fixture's keys.
 */
class IncludeIdsSharedFixtureTest {

    @After
    fun resetSeam() {
        IncludeExpander.layoutReader = null
    }

    private fun loadFixture(): JsonObject {
        val stream = requireNotNull(
            javaClass.classLoader?.getResourceAsStream("include_ids_fixture.json")
        ) { "include_ids_fixture.json missing from test resources" }
        return stream.reader(Charsets.UTF_8).use { JsonParser.parseReader(it).asJsonObject }
    }

    private fun collectIds(node: JsonObject, ids: MutableList<String>) {
        if (node.has("id")) ids.add(node.get("id").asString)
        for (key in listOf("child", "children")) {
            val child = node.get(key) ?: continue
            when {
                child.isJsonArray -> child.asJsonArray.forEach { if (it.isJsonObject) collectIds(it.asJsonObject, ids) }
                child.isJsonObject -> collectIds(child.asJsonObject, ids)
            }
        }
    }

    private fun expandedIds(layouts: JsonObject, screen: String): List<String> {
        // The assets tree a device would have: each layout under Layouts/, at
        // the path the fixture keys it by.
        val assets = layouts.entrySet().associate { (path, layout) -> "Layouts/$path" to layout.toString() }
        IncludeExpander.layoutReader = { name ->
            IncludeExpander.readLayout(name) { path ->
                (assets[path] ?: throw FileNotFoundException(path)).byteInputStream()
            }
        }
        val expanded = IncludeExpander.processIncludes(layouts.getAsJsonObject(screen).deepCopy())
        return mutableListOf<String>().also { collectIds(expanded, it) }.sorted()
    }

    @Test
    fun everySpecimenExpandsToTheSharedFixturesIds() {
        val fixture = loadFixture()
        val screen = fixture.get("screen").asString
        val specimens = fixture.getAsJsonObject("specimens")
        assertTrue("the fixture has no specimens", specimens.size() > 0)
        for (name in specimens.keySet().sorted()) {
            val specimen = specimens.getAsJsonObject(name)
            val expected = specimen.getAsJsonArray("expected").map { it.asString }
            assertEquals("[$name]", expected, expandedIds(specimen.getAsJsonObject("layouts"), screen))
        }
    }
}
