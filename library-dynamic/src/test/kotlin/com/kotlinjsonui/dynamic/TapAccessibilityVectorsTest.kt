package com.kotlinjsonui.dynamic

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.dynamic.helpers.TapAccessibility
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The tap shapes, run against jsonui-cli's shared table
 * (`shared/core/tap_accessibility_vectors.json`, copied byte-identical into
 * src/test/resources; CI's vendored-attr-guard compares the copy with the file
 * at the pinned ref). The two type lists are held equal to the table's, which
 * carries component_metadata.json's `interactive` declaration — a list edited
 * here alone, or a declaration changed there alone, is red.
 */
class TapAccessibilityVectorsTest {

    private val vectors: JsonObject by lazy {
        val stream = requireNotNull(
            javaClass.classLoader?.getResourceAsStream("tap_accessibility_vectors.json")
        ) { "tap_accessibility_vectors.json missing from test resources" }
        stream.reader(Charsets.UTF_8).use { JsonParser.parseReader(it).asJsonObject }
    }

    /** Each node inside one with `userInteractionEnabled: false` marked as DynamicView marks it. */
    private fun shapes(raw: JsonObject, out: MutableMap<String, String?>, stopped: Boolean = false) {
        val node = if (stopped) TapAccessibility.markStopped(raw) else raw
        node.get("id")?.takeIf { it.isJsonPrimitive }?.let { out[it.asString] = TapAccessibility.shape(node)?.name?.lowercase() }
        TapAccessibility.children(node).forEach { shapes(it, out, stopped || TapAccessibility.stops(raw)) }
    }

    @Test
    fun theTypeListsAreTheTablesDeclaration() {
        assertEquals(vectors.getAsJsonArray("interactive_types").map { it.asString }, TapAccessibility.INTERACTIVE_TYPES.sorted())
        assertEquals(vectors.getAsJsonArray("known_types").map { it.asString }, TapAccessibility.KNOWN_TYPES.sorted())
    }

    /**
     * Which nodes are controls (`controls`): [TapAccessibility.isControl]
     * answers each case as jsonui-cli's `control?` does, the app's own
     * spellings registered as the case says
     * ([com.kotlinjsonui.core.Configuration.customComponentTypes], as an app's
     * initializer hands them over).
     */
    @Test
    fun everyControlCaseIsAnsweredAsTheTableGives() {
        val controls = vectors.getAsJsonObject("controls")
        assertEquals(
            controls.getAsJsonArray("stop_container_types").map { it.asString },
            TapAccessibility.STOP_CONTAINER_TYPES.sorted()
        )
        val cases = controls.getAsJsonArray("cases").map { it.asJsonObject }
        assertEquals(setOf(true, false), cases.map { it.get("control").asBoolean }.toSet())
        val saved = Configuration.customComponentTypes
        try {
            for (case in cases) {
                val type = case.get("type").asString
                val apps = case.getAsJsonArray("app_types")?.map { it.asString }.orEmpty()
                Configuration.customComponentTypes = apps.toSet()
                assertEquals("$type, app types $apps", case.get("control").asBoolean, TapAccessibility.isControl(type))
            }
        } finally {
            Configuration.customComponentTypes = saved
        }
    }

    @Test
    fun everyCaseGetsTheShapesTheTableGives() {
        val cases = vectors.getAsJsonArray("cases").map { it.asJsonObject }
        assertTrue("the table has no cases", cases.isNotEmpty())
        val seen = mutableSetOf<String>()
        for (case in cases) {
            val name = case.get("name").asString
            val got = mutableMapOf<String, String?>()
            shapes(case.getAsJsonObject("layout"), got)
            val expected = case.getAsJsonObject("shapes").entrySet().associate {
                it.key to (if (it.value.isJsonNull) null else it.value.asString)
            }
            assertEquals(name, expected, got.filterKeys { it in expected })
            seen += expected.values.filterNotNull()
        }
        assertEquals(setOf("button", "combine", "none"), seen)
    }
}
