package com.kotlinjsonui.dynamic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.InputStreamReader

/**
 * Dynamic draws what the declaration says this face draws, and nothing else.
 *
 * jsonui-cli's shared/core/component_metadata.json (vendored as an androidTest
 * resource; CI compares it with the pinned jsonui-cli ref) declares, per
 * component, the faces that draw it — `platforms.kotlin_dynamic` for this one.
 * A type it declares drawn here must not be reported as an unknown type; one
 * it declares not drawn here must be. The types are read from the
 * declaration, not listed: change the declaration and this follows it.
 */
@RunWith(AndroidJUnit4::class)
class ComponentPlatformDeclarationTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun eachTypeIsDrawnWhereTheDeclarationSaysAndUnknownWhereItDoesNot() {
        val root = javaClass.getResourceAsStream("/component_metadata.json")!!
            .use { JsonParser.parseReader(InputStreamReader(it, Charsets.UTF_8)).asJsonObject }
        // What a type needs to draw at all, as the stage arm draws it.
        val extra = StageMeasurer.TYPES.toMap()
        var json by mutableStateOf("{\"type\": \"View\"}")
        val unknown = mutableListOf<String>()
        rule.setContent {
            DynamicView(
                json = JsonParser.parseString(json).asJsonObject,
                data = mapOf("t" to ""),
                onError = { e -> if (e.message?.startsWith("Unknown component type") == true) unknown += e.message!! }
            )
        }
        fun unknownFor(type: String): Boolean {
            unknown.clear()
            json = "{\"type\": \"$type\"${extra[type] ?: ""}}"
            rule.waitForIdle()
            // UnknownComponentType's sentence (it was "Unknown component type: <type>")
            return unknown.any { it == UnknownComponentType.TEMPLATE.format(type) || it.startsWith(UnknownComponentType.TEMPLATE.format(type) + " —") }
        }
        // control: a type no case takes is reported
        assertTrue("an undeclared type was not reported", unknownFor("ProbeUndeclaredType"))

        var drawnHere = 0
        val mismatches = mutableListOf<String>()
        for ((type, value) in root.entrySet().sortedBy { it.key }) {
            val platforms = value.takeIf { it.isJsonObject }?.asJsonObject?.getAsJsonObject("platforms") ?: continue
            val declared = platforms.get("kotlin_dynamic")?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isBoolean }
                ?.asBoolean ?: continue
            if (declared) drawnHere++
            val isUnknown = unknownFor(type)
            if (declared && isUnknown) mismatches += "$type: declared drawn here, reported unknown"
            if (!declared && !isUnknown) mismatches += "$type: declared not drawn here, drawn"
        }
        assertTrue("the declaration's types were not read", drawnHere >= 20)
        assertEquals("Dynamic disagrees with component_metadata.json kotlin_dynamic", emptyList<String>(), mismatches)
    }
}
