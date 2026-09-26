package com.kotlinjsonui.dynamic.components

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.helpers.TapAccessibility
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Whether a dynamic Label's links are operable (DynamicTextComponent.linksEnabled):
 * not under its own `userInteractionEnabled: false` or a binding resolving
 * false, nor inside a view that stops interaction (DynamicView's
 * TapAccessibility.STOPPED_KEY). canTap and enabled are the Label's own tap
 * and state and do not reach its links. The device test
 * (LabelLinksStopWithUserInteractionTest) measures what each answer does.
 */
class LabelLinksEnabledTest {
    private fun label(extra: String = ""): JsonObject =
        JsonParser.parseString("""{"type": "Label", "text": "t", "linkable": true$extra}""").asJsonObject

    private val data = mapOf<String, Any>("open" to true, "shut" to false)

    @Test
    fun theFlagAndAViewAroundItDecide() {
        val cases = linkedMapOf(
            "no flag" to (label() to true),
            "own false" to (label(""", "userInteractionEnabled": false""") to false),
            "own true" to (label(""", "userInteractionEnabled": true""") to true),
            "own binding false" to (label(""", "userInteractionEnabled": "@{shut}"""") to false),
            "own binding true" to (label(""", "userInteractionEnabled": "@{open}"""") to true),
            "in a view that stops" to (TapAccessibility.markStopped(label()) to false),
            "canTap false" to (label(""", "canTap": false""") to true),
            "enabled false" to (label(""", "enabled": false""") to true),
        )
        val got = cases.mapValues { (_, c) -> DynamicTextComponent.linksEnabled(c.first, data) }
        assertEquals(cases.mapValues { it.value.second }, got)
    }
}
