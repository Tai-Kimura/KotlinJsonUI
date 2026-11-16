package com.kotlinjsonui.dynamic

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A Radio item with no id is named by its position in the layout
 * (helpers/LayoutPath, stamped by DynamicView) — the codegen's
 * `radio_<path>` — and so reads selected once it is selected.
 *
 * It was `radio_<the clock>`: a new name on every composition. Selecting an
 * id-less item wrote one name to the group and the next composition asked
 * for another, so the item never read selected (jsonui-cli
 * docs/bugs/kjui-radio-default-id-is-random.md, the Dynamic twin). The JVM
 * half (the shared vectors, the name) is LayoutPathVectorsTest; this is the
 * drawn half — DynamicView's own walk, containers and all.
 */
@RunWith(AndroidJUnit4::class)
class RadioDefaultIdIsItsPositionTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun anIdLessItemWritesItsPositionAndReadsSelected() {
        val writes = mutableListOf<Map<String, Any>>()
        val state = mutableStateOf<Map<String, Any>>(emptyMap())
        val write: (Map<String, Any>) -> Unit = { m -> writes += m; state.value = state.value + m }
        state.value = mapOf("updateData" to write)
        val json = JsonParser.parseString(
            """
            {"type": "View", "orientation": "vertical", "child": [
              {"type": "Radio", "text": "first"},
              {"type": "View", "child": [{"type": "Radio", "text": "nested"}]},
              {"type": "Radio", "id": "named", "text": "named"}
            ]}
            """.trimIndent()
        ).asJsonObject
        rule.setContent { DynamicView(json = json, data = state.value) }
        rule.waitForIdle()

        val buttons = rule.onAllNodes(
            SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton), useUnmergedTree = true
        )
        fun selected(): List<Boolean> = (0 until 3).map {
            buttons[it].fetchSemanticsNode().config.getOrNull(SemanticsProperties.Selected) == true
        }

        buttons[1].performClick()
        rule.waitForIdle()
        assertEquals("radio_0_1_0", writes.last()["selectedRadiogroup"])
        assertEquals(listOf(false, true, false), selected())

        buttons[0].performClick()
        rule.waitForIdle()
        assertEquals("radio_0_0", writes.last()["selectedRadiogroup"])
        assertEquals(listOf(true, false, false), selected())

        buttons[2].performClick()
        rule.waitForIdle()
        assertEquals("named", writes.last()["selectedRadiogroup"])
        assertEquals(listOf(false, false, true), selected())
    }
}
