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
 * A constraint inside an include names its target as the target is spelled
 * after expansion, answered by jsonui-cli's shared fixture
 * (`shared/core/include_constraint_targets_fixture.json`, copied byte-identical
 * into `src/test/resources/`; CI's vendored-attr-guard job compares the copy
 * with the file at the pinned jsonui-cli ref). jsonui-cli reads the same file
 * against the sjui and kjui codegen expanders.
 *
 * Under an include with an id the elements' ids are prefixed (`cardA`); a
 * relative constraint between two elements of the same partial kept naming
 * `a`, found nothing, and was dropped (jsonui-cli ticket
 * rjui-include-root-sibling-constraint-dropped, row D4). `expected` maps each
 * constrained element's expanded id to its constraint attributes and their
 * expanded targets.
 */
class IncludeConstraintTargetsSharedFixtureTest {

    @After
    fun resetSeam() {
        IncludeExpander.layoutReader = null
    }

    private fun loadFixture(): JsonObject {
        val stream = requireNotNull(
            javaClass.classLoader?.getResourceAsStream("include_constraint_targets_fixture.json")
        ) { "include_constraint_targets_fixture.json missing from test resources" }
        return stream.reader(Charsets.UTF_8).use { JsonParser.parseReader(it).asJsonObject }
    }

    private fun collectConstraints(node: JsonElement?, keys: List<String>, out: MutableMap<String, Map<String, String>>) {
        when {
            node == null -> return
            node.isJsonArray -> node.asJsonArray.forEach { collectConstraints(it, keys, out) }
            node.isJsonObject -> {
                val obj = node.asJsonObject
                val found = keys.mapNotNull { key ->
                    obj.get(key)?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isString }?.let { key to it.asString }
                }.toMap()
                if (found.isNotEmpty()) out[obj.get("id")?.asString ?: "?"] = found
                collectConstraints(obj.get("child"), keys, out)
                collectConstraints(obj.get("children"), keys, out)
            }
        }
    }

    private fun expandedConstraints(layouts: JsonObject, screen: String, keys: List<String>): Map<String, Map<String, String>> {
        val assets = layouts.entrySet().associate { (path, layout) -> "Layouts/$path" to layout.toString() }
        IncludeExpander.layoutReader = { name ->
            IncludeExpander.readLayout(name) { path ->
                (assets[path] ?: throw FileNotFoundException(path)).byteInputStream()
            }
        }
        val expanded = IncludeExpander.processIncludes(layouts.getAsJsonObject(screen).deepCopy())
        return mutableMapOf<String, Map<String, String>>().also { collectConstraints(expanded, keys, it) }
    }

    @Test
    fun everySpecimenNamesItsTargetsAsTheSharedFixtureDoes() {
        val fixture = loadFixture()
        val screen = fixture.get("screen").asString
        val keys = fixture.getAsJsonArray("constraint_keys").map { it.asString }
        assertEquals("the expander and the fixture read the same keys", keys, IncludeExpander.constraintTargetKeys)
        val specimens = fixture.getAsJsonObject("specimens")
        assertTrue("the fixture has no specimens", specimens.size() > 0)
        for (name in specimens.keySet().sorted()) {
            val specimen = specimens.getAsJsonObject(name)
            val expected = specimen.getAsJsonObject("expected").entrySet().associate { (id, attrs) ->
                id to attrs.asJsonObject.entrySet().associate { (k, v) -> k to v.asString }
            }
            assertEquals("[$name]", expected, expandedConstraints(specimen.getAsJsonObject("layouts"), screen, keys))
        }
    }
}
