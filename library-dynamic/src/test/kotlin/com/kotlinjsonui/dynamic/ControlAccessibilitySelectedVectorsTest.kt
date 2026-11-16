package com.kotlinjsonui.dynamic

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.helpers.ControlAccessibility
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * accessibilitySelected, run against jsonui-cli's shared table
 * (`shared/core/control_accessibility_vectors.json`, copied byte-identical into src/test/resources; CI's
 * vendored-attr-guard compares the copy with the file at the pinned ref): where a vector tells (true, or a binding),
 * TalkBack is told `selected` — a binding only while it resolves true; where it tells nothing, nothing is told
 * whatever the data says.
 */
class ControlAccessibilitySelectedVectorsTest {

    private val cases: List<JsonObject> by lazy {
        val stream = requireNotNull(
            javaClass.classLoader?.getResourceAsStream("control_accessibility_vectors.json")
        ) { "control_accessibility_vectors.json missing from test resources" }
        stream.reader(Charsets.UTF_8).use { JsonParser.parseReader(it).asJsonObject }
            .getAsJsonArray("cases").map { it.asJsonObject }
    }

    /** The data a binding resolves against: every bound name in the layout set to [value]. */
    private fun dataFor(layout: JsonObject, value: Boolean): Map<String, Any> {
        val names = Regex("""@\{\s*([A-Za-z_][A-Za-z0-9_]*)\s*\}""").findAll(layout.toString()).map { it.groupValues[1] }
        return names.associateWith { value }
    }

    @Test fun every_vector() {
        assertTrue("the table has cases", cases.size >= 10)
        for (c in cases) {
            val name = c.get("name").asString
            val layout = c.getAsJsonObject("layout")
            val expected = c.get("selected")
            when {
                expected.isJsonNull -> {
                    assertEquals("$name: tells nothing (data true)", false, ControlAccessibility.selected(layout, dataFor(layout, true)))
                    assertEquals("$name: tells nothing (data false)", false, ControlAccessibility.selected(layout, dataFor(layout, false)))
                }
                expected.isJsonPrimitive && expected.asJsonPrimitive.isBoolean -> {
                    assertEquals("$name: told", true, ControlAccessibility.selected(layout, emptyMap()))
                }
                else -> {
                    assertEquals("$name: told while the binding is true", true, ControlAccessibility.selected(layout, dataFor(layout, true)))
                    assertEquals("$name: nothing while it is false", false, ControlAccessibility.selected(layout, dataFor(layout, false)))
                }
            }
        }
    }
}
