package com.kotlinjsonui.dynamic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * What the user chose in a Dynamic control survives a data change that does
 * not concern it — ticket kjui-dynamic-stateful-components-reset-on-unrelated-data.
 *
 * Every stateful control kept its state in `remember(…, data)`: each new data
 * map — any key changed, say by another control's `updateData` — reset a
 * Switch, a CheckBox, a Toggle, a Radio group, a Segment, a TabView, a Slider
 * and a SelectBox to the value they declare (measured on an emulator,
 * 2026-09-26, KotlinJsonUI 1595e30). The state is now keyed on the declared
 * value — the binding's, or the static one — and follows a bound value when
 * the view model changes it.
 *
 * All the controls on one screen, each changed by the user through its
 * semantics (no accessibility cache in between), then the data replaced:
 *   static      no binding: the choice survives an unrelated key changing
 *   bound       `updateData` writes the choice back: it survives, and the
 *               view model changing the bound value is followed
 *   unwritten   `updateData` records only: the choice survives until the
 *               bound value itself changes
 */
@RunWith(AndroidJUnit4::class)
class DynamicControlStateSurvivesDataTest {

    @get:Rule
    val rule = createComposeRule()

    private fun controls(bound: Boolean): String {
        fun v(static: String, key: String) = if (bound) "\"@{$key}\"" else static
        val items = listOf(
            "{\"type\": \"Switch\", \"id\": \"sw\", \"isOn\": ${v("false", "sw_on")}}",
            if (bound) "{\"type\": \"Toggle\", \"id\": \"tg\", \"data\": \"tg_on\"}"
            else "{\"type\": \"Toggle\", \"id\": \"tg\", \"isOn\": false}",
            "{\"type\": \"CheckBox\", \"id\": \"cb\", \"isOn\": ${v("false", "cb_on")}}",
            "{\"type\": \"Radio\", \"id\": \"rv\", \"items\": [\"ra\", \"rb\"], \"selectedValue\": ${v("\"ra\"", "rv_sel")}}",
            "{\"type\": \"Segment\", \"id\": \"seg\", \"items\": [\"sx\", \"sy\"], \"selectedIndex\": ${v("0", "seg_sel")}}",
            "{\"type\": \"View\", \"width\": 240, \"height\": 150, \"child\": [{\"type\": \"TabView\", \"id\": \"tab\", " +
                "\"tabs\": [{\"title\": \"ta\"}, {\"title\": \"tb\"}], \"selectedIndex\": ${v("0", "tab_sel")}}]}",
            "{\"type\": \"Slider\", \"id\": \"sl\", \"minimumValue\": 0, \"maximumValue\": 1, \"value\": ${v("0.2", "sl_val")}}",
            "{\"type\": \"SelectBox\", \"id\": \"sb\", \"items\": [\"pp\", \"qq\"], \"selectedItem\": ${v("\"pp\"", "sb_sel")}}",
            "{\"type\": \"Progress\", \"id\": \"pr\", \"progress\": ${v("0.3", "pr_val")}}",
        )
        return "{\"type\": \"View\", \"orientation\": \"vertical\", \"child\": [${items.joinToString(",")}]}"
    }

    private val initial = mapOf<String, Any>(
        "sw_on" to false, "tg_on" to false, "cb_on" to false, "rv_sel" to "ra", "seg_sel" to 0, "tab_sel" to 0, "sl_val" to 0.2, "sb_sel" to "pp", "pr_val" to 0.3,
    )

    private fun render(bound: Boolean, writes: Boolean): MutableState<Map<String, Any>> {
        val state = mutableStateOf<Map<String, Any>>(emptyMap())
        val base = initial.toMutableMap<String, Any>()
        base["updateData"] = { m: Map<String, Any> -> if (writes) state.value = state.value + m }
        state.value = base
        val json = JsonParser.parseString(controls(bound)).asJsonObject
        rule.setContent {
            Column(Modifier.verticalScroll(rememberScrollState())) { DynamicView(json = json, data = state.value) }
        }
        rule.waitForIdle()
        return state
    }

    private fun toggle(tag: String): SemanticsNodeInteraction =
        rule.onNode(isToggleable() and (hasTestTag(tag) or hasAnyAncestor(hasTestTag(tag))), useUnmergedTree = true)

    private fun textIn(tag: String, text: String): SemanticsNodeInteraction =
        rule.onNode(hasText(text) and hasAnyAncestor(hasTestTag(tag)))

    /** What each control shows now, read from its semantics. */
    private fun read(): Map<String, Any?> {
        rule.waitForIdle()
        fun on(tag: String) = toggle(tag).fetchSemanticsNode().config.getOrNull(SemanticsProperties.ToggleableState) == ToggleableState.On
        fun sel(n: SemanticsNodeInteraction) = n.fetchSemanticsNode().config.getOrNull(SemanticsProperties.Selected)
        return mapOf(
            "sw" to on("sw"), "tg" to on("tg"), "cb" to on("cb"),
            // A Radio row's selection sits on its RadioButton, a merge boundary of its own.
            "rv" to rule.onAllNodes(hasAnyAncestor(hasTestTag("rv")) and
                androidx.compose.ui.test.SemanticsMatcher.expectValue(SemanticsProperties.Role, androidx.compose.ui.semantics.Role.RadioButton),
                useUnmergedTree = true).fetchSemanticsNodes()
                .indexOfFirst { it.config.getOrNull(SemanticsProperties.Selected) == true }.let { listOf("ra", "rb").getOrNull(it) },
            "seg" to (if (sel(textIn("seg", "sy")) == true) 1 else 0),
            "tab" to (if (sel(rule.onNode(hasTestTag("tab_tab_1"))) == true) 1 else 0),
            "sl" to rule.onNode(hasAnyAncestor(hasTestTag("sl")).or(hasTestTag("sl")) and
                androidx.compose.ui.test.SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo), useUnmergedTree = true)
                .fetchSemanticsNode().config[SemanticsProperties.ProgressBarRangeInfo].current.let { Math.round(it * 10) / 10.0 },
            // A SelectBox shows its selection as the text inside its field.
            "sb" to rule.onNode(hasTestTag("sb")).fetchSemanticsNode().config.getOrNull(SemanticsProperties.Text)
                ?.joinToString("") { it.text },
            // A Progress is not the user's to change: its bound value is followed.
            "pr" to rule.onNode(hasTestTag("pr"), useUnmergedTree = true).fetchSemanticsNode()
                .config[SemanticsProperties.ProgressBarRangeInfo].current.let { Math.round(it * 10) / 10.0 },
        )
    }

    private val declared = mapOf<String, Any?>("sw" to false, "tg" to false, "cb" to false, "rv" to "ra", "seg" to 0, "tab" to 0,
        "sl" to 0.2, "sb" to "pp", "pr" to 0.3)
    private val chosen = mapOf<String, Any?>("sw" to true, "tg" to true, "cb" to true, "rv" to "rb", "seg" to 1, "tab" to 1,
        "sl" to 0.8, "sb" to "qq", "pr" to 0.3)

    /** The user's choice in every control, through each control's own semantics. */
    private fun choose() {
        for (tag in listOf("sw", "tg", "cb")) toggle(tag).performScrollTo().performClick()
        textIn("rv", "rb").performScrollTo().performClick()
        textIn("seg", "sy").performScrollTo().performClick()
        rule.onNode(hasTestTag("tab_tab_1")).performScrollTo().performClick()
        rule.onNode(hasAnyAncestor(hasTestTag("sl")).or(hasTestTag("sl")) and
            androidx.compose.ui.test.SemanticsMatcher.keyIsDefined(SemanticsActions.SetProgress), useUnmergedTree = true)
            .performScrollTo().performSemanticsAction(SemanticsActions.SetProgress) { it(0.8f) }
        rule.onNode(hasTestTag("sb")).performScrollTo().performClick()
        rule.waitForIdle()
        rule.onNode(hasText("qq") and !hasAnyAncestor(hasTestTag("sb"))).performClick()
        rule.waitForIdle()
    }

    private fun check(label: String, want: Map<String, Any?>, got: Map<String, Any?>) {
        println("CONTROLSTATE $label " + got.entries.joinToString(" ") { "${it.key}=${it.value}" })
        assertEquals(label, want, got)
    }

    @Test
    fun static_a_choice_survives_an_unrelated_data_change() {
        val data = render(bound = false, writes = false)
        check("static/declared", declared, read())
        choose()
        check("static/chosen", chosen, read())
        data.value = data.value + ("unrelated" to 1)
        check("static/after_an_unrelated_key_changed", chosen, read())
    }

    @Test
    fun bound_a_written_choice_survives_and_the_view_model_is_followed() {
        val data = render(bound = true, writes = true)
        check("bound/declared", declared, read())
        choose()
        check("bound/chosen", chosen, read())
        data.value = data.value + ("unrelated" to 1)
        check("bound/after_an_unrelated_key_changed", chosen, read())
        // The view model sets every bound value back.
        data.value = data.value + initial
        check("bound/after_the_view_model_changed_them", declared, read())
    }

    @Test
    fun unwritten_a_choice_survives_until_the_bound_value_changes() {
        val data = render(bound = true, writes = false)
        choose()
        check("unwritten/chosen", chosen, read())
        data.value = data.value + ("unrelated" to 1)
        check("unwritten/after_an_unrelated_key_changed", chosen, read())
        // The bound values themselves change (to the chosen ones, then back).
        data.value = data.value + mapOf("sw_on" to true, "tg_on" to true, "cb_on" to true, "rv_sel" to "rb",
            "seg_sel" to 1, "tab_sel" to 1, "sl_val" to 0.8, "sb_sel" to "qq", "pr_val" to 0.9)
        rule.waitForIdle() // composed once with them, or the two changes are one
        check("unwritten/after_the_bound_values_changed", chosen + ("pr" to 0.9), read())
        data.value = data.value + initial
        check("unwritten/after_the_bound_values_changed_back", declared, read())
    }
}
