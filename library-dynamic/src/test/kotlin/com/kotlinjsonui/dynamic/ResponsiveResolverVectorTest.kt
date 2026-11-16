package com.kotlinjsonui.dynamic

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Which key of a responsive block wins — the rule every codegen draws
 * (jsonui-cli shared/core/responsive_resolver.rb build_branches, an if / else
 * if chain) — held on the Dynamic resolver. The cases are jsonui-cli
 * shared/core/responsive_resolution_vectors.json, copied byte-identical into
 * src/test/resources (CI compares the copy with the file at the pinned
 * jsonui-cli ref).
 *
 * - one key wins, alone (kjui-dynamic-responsive-stacks-overrides-codegen-picks-one);
 * - minWidth:N holds at width >= N dp, maxWidth:N at width < N; the smallest
 *   maxWidth that holds wins, else the largest minWidth
 *   (jui-responsive-width-threshold-conditions). The width is the window's
 *   dp truncated to Int, so the vectors' fractional width reaches this face
 *   as its Int — the codegen's expression.
 */
class ResponsiveResolverVectorTest {

    private val vectors: JsonObject by lazy {
        val text = javaClass.classLoader!!.getResourceAsStream("responsive_resolution_vectors.json")!!
            .bufferedReader().use { it.readText() }
        JsonParser.parseString(text).asJsonObject
    }

    @Test
    fun everyCaseDrawsItsWinnerAlone() {
        val cases = vectors.getAsJsonArray("cases")
        assertTrue(cases.size() >= 20)
        // Every case is judged, and every miss is named at once.
        val misses = mutableListOf<String>()
        for (element in cases) {
            val c = element.asJsonObject
            val name = c.get("name").asString
            val responsive = JsonObject()
            for (key in c.getAsJsonArray("keys")) {
                responsive.add(key.asString, JsonObject().apply { addProperty("tag", key.asString) })
            }
            val json = JsonObject().apply {
                addProperty("type", "View")
                addProperty("tag", "base")
                add("responsive", responsive)
            }
            val env = c.getAsJsonObject("env")
            val sizeClass = env.get("sizeClass")?.asString ?: "compact"
            val landscape = env.get("landscape")?.asBoolean ?: false
            val widthDp = env.get("width")?.asDouble?.toInt()
            val winner = c.get("winner").takeIf { !it.isJsonNull }?.asString ?: "base"
            val drawn = ResponsiveResolver.resolveNode(json, sizeClass, landscape, widthDp).get("tag").asString
            if (drawn != winner) misses += "$name: drew $drawn, expected $winner"
        }
        assertEquals(emptyList<String>(), misses)
    }

    @Test
    fun everyKeyCaseParsesAsItSays() {
        val cases = vectors.getAsJsonArray("key_cases")
        assertTrue(cases.size() >= 15)
        for (element in cases) {
            val c = element.asJsonObject
            val key = c.get("key").asString
            val condition = c.get("condition").takeIf { !it.isJsonNull }?.asJsonObject
            val expected = condition?.let { (it.get("kind").asString == "min") to it.get("value").asInt }
            assertEquals(key, expected, ResponsiveResolver.widthCondition(key))
        }
    }

    @Test
    fun widthConditionsHoldNothingWithoutAWidth() {
        val json = JsonParser.parseString(
            """{ "type": "View", "tag": "base", "responsive": { "minWidth:0": { "tag": "min" } } }"""
        ).asJsonObject
        assertEquals("base", ResponsiveResolver.resolveNode(json, "regular", false, null).get("tag").asString)
    }
}
