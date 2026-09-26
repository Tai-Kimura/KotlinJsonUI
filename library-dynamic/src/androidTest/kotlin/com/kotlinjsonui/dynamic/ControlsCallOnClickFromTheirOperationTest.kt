package com.kotlinjsonui.dynamic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.SemanticsMatcher
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
 * A control calls its declared onClick from its own operation, after it —
 * not from an outer `.clickable`
 * (jsonui-cli docs/bugs/kjui-dynamic-components-that-skip-the-common-modifiers.md,
 * item A; kjui codegen 62e15706).
 *
 * The outer clickable sat on the control's own node, and Compose applies a
 * node's semantics innermost first with a later AccessibilityAction replacing
 * an earlier one: TalkBack's action ran the handler and did not operate the
 * control, while a touch operated it and never reached the handler (0 calls
 * on Switch / CheckBox in CommonStageFamilyProbe). The tap rule gives these
 * types the shape `none` — a control already, left as it was.
 *
 * For each control, three copies: `p` (onClick), `g` (onClick, canTap
 * false) and `d` (onClick, enabled false). Each is operated once, by touch
 * (performClick) in one test and by the node's own accessibility action in
 * the other, and must:
 * - `p`: operate, and call its handler exactly once;
 * - `g`: operate, and not call;
 * - `d`: neither operate nor call — and the operated node reads disabled,
 *   so an accessibility service does not dispatch its action.
 * TextField / TextView call no onClick at all (their own tap focuses them;
 * iOS attaches no tap to a text field): they focus, and never call.
 */
@RunWith(AndroidJUnit4::class)
class ControlsCallOnClickFromTheirOperationTest {

    @get:Rule
    val rule = createComposeRule()

    // rv: a Radio group (items); ri: a Radio item, whose selection is the
    // view model's (updateData), so the data here is writable.
    private val kinds = listOf("sw", "tg", "cb", "rv", "ri", "seg", "sl", "sb", "tf", "tv")
    private val variants = listOf("p", "g", "d")

    private fun node(kind: String, variant: String): String {
        val tag = "${kind}_$variant"
        val gate = when (variant) {
            "g" -> ", \"canTap\": false"
            "d" -> ", \"enabled\": false"
            else -> ""
        }
        val common = "\"id\": \"$tag\", \"onClick\": \"@{tap_$tag}\"$gate"
        return when (kind) {
            "sw" -> "{\"type\": \"Switch\", $common, \"isOn\": false}"
            "tg" -> "{\"type\": \"Toggle\", $common, \"isOn\": false}"
            "cb" -> "{\"type\": \"CheckBox\", $common, \"isOn\": false}"
            "rv" -> "{\"type\": \"Radio\", $common, \"items\": [\"ra\", \"rb\"], \"selectedValue\": \"ra\"}"
            "ri" -> "{\"type\": \"Radio\", $common, \"text\": \"r\", \"group\": \"g_$variant\"}"
            "seg" -> "{\"type\": \"Segment\", $common, \"items\": [\"sx\", \"sy\"], \"selectedIndex\": 0}"
            "sl" -> "{\"type\": \"Slider\", $common, \"width\": 240, \"minimumValue\": 0, \"maximumValue\": 1, \"value\": 0.2}"
            "sb" -> "{\"type\": \"SelectBox\", $common, \"items\": [\"pp\", \"qq\"], \"selectedItem\": \"pp\"}"
            "tf" -> "{\"type\": \"TextField\", $common, \"width\": 240, \"hint\": \"f\"}"
            "tv" -> "{\"type\": \"TextView\", $common, \"width\": 240, \"hint\": \"v\"}"
            else -> error(kind)
        }
    }

    private val calls = mutableMapOf<String, Int>()

    private fun render() {
        val state = mutableStateOf<Map<String, Any>>(emptyMap())
        val data = mutableMapOf<String, Any>()
        for (kind in kinds) for (variant in variants) {
            val tag = "${kind}_$variant"
            val tap: () -> Unit = { calls[tag] = (calls[tag] ?: 0) + 1 }
            data["tap_$tag"] = tap
        }
        val write: (Map<String, Any>) -> Unit = { m -> state.value = state.value + m }
        data["updateData"] = write
        state.value = data
        val children = kinds.flatMap { k -> variants.map { v -> node(k, v) } }
        val json = JsonParser.parseString(
            "{\"type\": \"View\", \"orientation\": \"vertical\", \"child\": [${children.joinToString(",")}]}"
        ).asJsonObject
        rule.setContent {
            Column(Modifier.verticalScroll(rememberScrollState())) { DynamicView(json = json, data = state.value) }
        }
        rule.waitForIdle()
    }

    private fun under(tag: String) = hasTestTag(tag) or hasAnyAncestor(hasTestTag(tag))

    private fun toggle(tag: String): SemanticsNodeInteraction =
        rule.onNode(isToggleable() and under(tag), useUnmergedTree = true)

    private fun radioButton(tag: String, index: Int): SemanticsNodeInteraction =
        rule.onAllNodes(
            hasAnyAncestor(hasTestTag(tag)) and SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton),
            useUnmergedTree = true
        )[index]

    private fun tab(tag: String, text: String): SemanticsNodeInteraction =
        rule.onNode(
            hasAnyAncestor(hasTestTag(tag)) and SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab) and
                androidx.compose.ui.test.hasAnyDescendant(hasText(text)),
            useUnmergedTree = true
        )

    private fun slider(tag: String): SemanticsNodeInteraction =
        rule.onNode(under(tag) and SemanticsMatcher.keyIsDefined(SemanticsActions.SetProgress), useUnmergedTree = true)

    // The field is the node holding its EditableText. It was found by SetText,
    // which BasicTextField defines only while the field is editable: a
    // disabled field (`d`) has Disabled and EditableText and no SetText, so
    // the `d` row found no node and both tests stopped there, on every commit
    // since this test was added (measured on an API 35 emulator, 2026-09-27).
    private fun field(tag: String): SemanticsNodeInteraction =
        rule.onNode(under(tag) and SemanticsMatcher.keyIsDefined(SemanticsProperties.EditableText), useUnmergedTree = true)

    /** The node the user operates, for each kind. */
    private fun target(kind: String, tag: String): SemanticsNodeInteraction = when (kind) {
        "sw", "tg", "cb" -> toggle(tag)
        "rv" -> radioButton(tag, 1)
        "ri" -> radioButton(tag, 0)
        "seg" -> tab(tag, "sy")
        "sl" -> slider(tag)
        "sb" -> rule.onNode(hasTestTag(tag))
        "tf", "tv" -> field(tag)
        else -> error(kind)
    }

    /** Whether the control's own operation happened (its state moved off the declared one). */
    private fun operated(kind: String, tag: String): Boolean {
        rule.waitForIdle()
        return when (kind) {
            "sw", "tg", "cb" -> toggle(tag).fetchSemanticsNode().config
                .getOrNull(SemanticsProperties.ToggleableState) == ToggleableState.On
            "rv" -> radioButton(tag, 1).fetchSemanticsNode().config.getOrNull(SemanticsProperties.Selected) == true
            "ri" -> radioButton(tag, 0).fetchSemanticsNode().config.getOrNull(SemanticsProperties.Selected) == true
            "seg" -> tab(tag, "sy").fetchSemanticsNode().config.getOrNull(SemanticsProperties.Selected) == true
            "sl" -> slider(tag).fetchSemanticsNode().config[SemanticsProperties.ProgressBarRangeInfo].current
                .let { Math.round(it * 10) / 10.0 } != 0.2
            "sb" -> rule.onNode(hasTestTag(tag)).fetchSemanticsNode().config.getOrNull(SemanticsProperties.Text)
                ?.joinToString("") { it.text }?.contains("qq") == true
            "tf", "tv" -> field(tag).fetchSemanticsNode().config.getOrNull(SemanticsProperties.Focused) == true
            else -> error(kind)
        }
    }

    private fun disabled(kind: String, tag: String): Boolean =
        target(kind, tag).fetchSemanticsNode().config.getOrNull(SemanticsProperties.Disabled) != null

    /** Operate once — by touch, or by the node's own accessibility action. */
    private fun operate(kind: String, tag: String, byAction: Boolean) {
        val node = target(kind, tag).performScrollTo()
        when {
            kind == "sl" && byAction -> node.performSemanticsAction(SemanticsActions.SetProgress) { it(0.8f) }
            byAction -> node.performSemanticsAction(SemanticsActions.OnClick)
            else -> node.performClick()
        }
        rule.waitForIdle()
        // A SelectBox's operation is the pick: open, then choose the option.
        if (kind == "sb") {
            // The sheet's option — not the text a SelectBox shows in its own
            // field (sb_p shows "qq" once it has picked it).
            val inABox = variants.map { under("sb_$it") }.reduce { x, y -> x or y }
            val option = rule.onAllNodes(hasText("qq") and !inABox)
            if (option.fetchSemanticsNodes().isNotEmpty()) {
                if (byAction) option[0].performSemanticsAction(SemanticsActions.OnClick) else option[0].performClick()
                rule.waitForIdle()
            }
        }
    }

    private fun run(byAction: Boolean) {
        render()
        val rows = mutableListOf<String>()
        val wrong = mutableListOf<String>()
        for (kind in kinds) for (variant in variants) {
            val tag = "${kind}_$variant"
            val textField = kind == "tf" || kind == "tv"
            if (variant == "d") {
                // enabled false: the operated node reads disabled — an
                // accessibility service does not dispatch a disabled node's
                // action — and a touch neither operates nor calls.
                if (!disabled(kind, tag)) wrong += "$tag is not disabled"
                operate(kind, tag, byAction = false)
            } else {
                operate(kind, tag, byAction)
            }
            val op = operated(kind, tag)
            val n = calls[tag] ?: 0
            rows += "$tag operated=$op calls=$n"
            val wantOp = variant != "d"
            val wantCalls = if (variant == "p" && !textField) 1 else 0
            if (op != wantOp || n != wantCalls) wrong += "$tag operated=$op (want $wantOp) calls=$n (want $wantCalls)"
        }
        println("CONTROLS_ONCLICK ${if (byAction) "action" else "touch"} " + rows.joinToString(" | "))
        assertEquals("controls whose onClick is not called from their operation", emptyList<String>(), wrong)
    }

    @Test
    fun byTouch_eachControlOperatesAndCallsOnClickOnce_gatedByCanTapAndEnabled() = run(byAction = false)

    @Test
    fun byItsAccessibilityAction_eachControlOperatesAndCallsOnClickOnce_gatedByCanTapAndEnabled() = run(byAction = true)
}
