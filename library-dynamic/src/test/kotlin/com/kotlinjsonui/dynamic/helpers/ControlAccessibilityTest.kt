package com.kotlinjsonui.dynamic.helpers

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * The rule jsonui-cli's codegen applies (shared/core/control_accessibility
 * .rb): accessibilityLabel (alias contentDescription) names the controls a
 * user operates, and a View only when it is one button; accessibilityLiveRegion
 * on a Label or a View is polite / assertive, and off reads nothing.
 */
class ControlAccessibilityTest {

    private fun node(json: String): JsonObject = JsonParser.parseString(json).asJsonObject

    @Test
    fun everyControlTypeIsNamed() {
        for (type in listOf("Button", "CheckBox", "Check", "Switch", "Toggle", "Radio", "Segment", "Slider", "SelectBox", "TextField", "EditText")) {
            assertEquals(type, "Name", ControlAccessibility.writtenLabel(node("""{"type": "$type", "accessibilityLabel": "Name"}""")))
        }
    }

    @Test
    fun theContentDescriptionSpellingReadsTheSame() {
        assertEquals("Name", ControlAccessibility.writtenLabel(node("""{"type": "Button", "text": "Open", "contentDescription": "Name"}""")))
    }

    @Test
    fun aBindingIsResolved() {
        val n = node("""{"type": "Button", "text": "Open", "accessibilityLabel": "@{name}"}""")
        assertEquals("Open the second image", ControlAccessibility.label(n, mapOf("name" to "Open the second image"), null))
    }

    @Test
    fun aViewIsNamedOnlyWhenItIsOneButton() {
        assertEquals("Name", ControlAccessibility.writtenLabel(node(
            """{"type": "View", "onClick": "@{open}", "accessibilityLabel": "Name", "child": [{"type": "Label", "text": "Card"}]}""")))
        assertNull(ControlAccessibility.writtenLabel(node(
            """{"type": "View", "accessibilityLabel": "Name", "child": [{"type": "Label", "text": "Card"}]}""")))
        assertNull(ControlAccessibility.writtenLabel(node(
            """{"type": "View", "onClick": "@{open}", "accessibilityLabel": "Name", "child": [{"type": "Switch"}]}""")))
    }

    @Test
    fun otherTypesAndAnEmptyNameAreNotNamed() {
        assertNull(ControlAccessibility.writtenLabel(node("""{"type": "Label", "text": "x", "accessibilityLabel": "Name"}""")))
        assertNull(ControlAccessibility.writtenLabel(node("""{"type": "Button", "accessibilityLabel": ""}""")))
    }

    @Test
    fun aLabelsAndAViewsLiveRegion() {
        assertEquals(ControlAccessibility.Live.POLITE, ControlAccessibility.live(node("""{"type": "Label", "text": "@{s}", "accessibilityLiveRegion": "polite"}""")))
        assertEquals(ControlAccessibility.Live.ASSERTIVE, ControlAccessibility.live(node("""{"type": "View", "accessibilityLiveRegion": "assertive"}""")))
        assertNull(ControlAccessibility.live(node("""{"type": "Label", "text": "@{s}", "accessibilityLiveRegion": "off"}""")))
        assertNull(ControlAccessibility.live(node("""{"type": "Button", "accessibilityLiveRegion": "polite"}""")))
    }
}
