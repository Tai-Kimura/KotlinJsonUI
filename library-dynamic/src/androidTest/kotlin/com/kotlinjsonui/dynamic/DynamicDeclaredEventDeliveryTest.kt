package com.kotlinjsonui.dynamic

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.pinch
import androidx.compose.ui.test.swipe
import androidx.compose.ui.test.swipeRight
import androidx.compose.ui.geometry.Offset
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Collections

/**
 * Handlers the SSoT declares that KotlinJsonUI Dynamic drew and never called,
 * found by jsonui-cli's runtime handler census on conf_ci (2026-10-03; 0 calls
 * on Dynamic, kjui codegen calling them):
 *
 * - a Radio's items form never read onValueChange (only the options path did)
 *   — kjui-dynamic-radio-items-never-calls-onvaluechange;
 * - a Radio of a `group` wrote the selection and called only onClick —
 *   kjui-radio-group-form-never-calls-onvaluechange (the order is iOS's: the
 *   write, onValueChange with the radio's value, then onClick);
 * - a Button's own modifier chain did not apply onPan / onPinch —
 *   kjui-dynamic-button-never-calls-onpan-or-onpinch;
 * - Segment `valueChange: "@{h}"` was read as onValueChange's spelling and
 *   dropped — kjui-dynamic-segment-valuechange-binding-is-never-called;
 * - a TextField's keyboard actions (onSubmit, the move to nextFocusId) never
 *   reached the field: CustomTextField took them and passed them to nothing —
 *   kjui-textfield-keyboard-actions-never-reach-the-field.
 */
@RunWith(AndroidJUnit4::class)
class DynamicDeclaredEventDeliveryTest {
    @get:Rule val rule = createComposeRule()

    private fun show(layout: String, data: Map<String, Any>) {
        rule.setContent {
            DynamicRuntimeScope(data) { eff ->
                DynamicView(json = JsonParser.parseString(layout).asJsonObject, data = eff)
            }
        }
        rule.waitForIdle()
    }

    private fun log() = Collections.synchronizedList(mutableListOf<String>())

    @Test
    fun aRadioItemsTapCallsOnValueChangeWithTheItem() {
        val calls = log()
        val h: (Any) -> Unit = { calls += it.toString() }
        show(
            """{"type":"View","child":[{"type":"Radio","id":"r","items":["a","b"],"selectedValue":"@{sel}","onValueChange":"@{h}"}]}""",
            mapOf("sel" to "a", "h" to h)
        )
        rule.onAllNodes(hasText("b") and hasClickAction())[0].performClick()
        rule.waitForIdle()
        assertEquals(listOf("b"), calls.toList())
    }

    @Test
    fun aGroupRadioTapWritesThenCallsOnValueChangeThenOnClick() {
        val calls = log()
        val h: (Any) -> Unit = { calls += "h:$it" }
        val k: () -> Unit = { calls += "k" }
        show(
            """{"type":"View","child":[{"type":"Radio","id":"r1","group":"g","text":"A","onValueChange":"@{h}","onClick":"@{k}"}]}""",
            mapOf("h" to h, "k" to k)
        )
        rule.onAllNodes(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton))[0].performClick()
        rule.waitForIdle()
        assertEquals(listOf("h:r1", "k"), calls.toList())
    }

    @Test
    fun aGroupRadioWithNoOnValueChangeCallsOnlyOnClick() {
        // Control: the tap reaches the radio; nothing else is called.
        val calls = log()
        val k: () -> Unit = { calls += "k" }
        show(
            """{"type":"View","child":[{"type":"Radio","id":"r1","group":"g","text":"A","onClick":"@{k}"}]}""",
            mapOf("k" to k)
        )
        rule.onAllNodes(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton))[0].performClick()
        rule.waitForIdle()
        assertEquals(listOf("k"), calls.toList())
    }

    @Test
    fun aButtonsDragAndPinchCallOnPanAndOnPinch() {
        val pans = log()
        val pinches = log()
        val pan: (Any) -> Unit = { pans += it.toString() }
        val pinchH: (Any) -> Unit = { pinches += it.toString() }
        show(
            """{"type":"View","orientation":"vertical","child":[
                {"type":"Button","id":"bp","text":"P","width":200,"height":80,"onPan":"@{pan}"},
                {"type":"Button","id":"bz","text":"Z","width":200,"height":80,"onPinch":"@{pinch}"}]}""",
            mapOf("pan" to pan, "pinch" to pinchH)
        )
        // A finger's drag: down, small moves past the touch slop, up (the
        // census drove it with UiDevice.swipe; a test swipe of the whole
        // width did not start a drag on the Button).
        rule.onNodeWithTag("bp").performTouchInput {
            swipe(centerLeft + Offset(20f, 0f), centerRight - Offset(20f, 0f), durationMillis = 600)
        }
        rule.onNodeWithTag("bz").performTouchInput {
            pinch(center - Offset(10f, 0f), center - Offset(60f, 0f), center + Offset(10f, 0f), center + Offset(60f, 0f))
        }
        rule.waitForIdle()
        assertTrue("onPan on a Button: $pans", pans.isNotEmpty())
        assertTrue("onPinch on a Button: $pinches", pinches.isNotEmpty())
    }

    @Test
    fun aTextViewsDragDoesNotCallOnPanAndALabelsDoes() {
        // common.onPan notApplicableTo TextView (ruling 2026-10-03): the faces
        // agree that a TextView's own drag takes the gesture. The Label is the
        // control — the same drag on it is a pan.
        val onText = log()
        val onLabel = log()
        val textPan: (Any) -> Unit = { onText += it.toString() }
        val labelPan: (Any) -> Unit = { onLabel += it.toString() }
        show(
            """{"type":"View","orientation":"vertical","child":[
                {"type":"TextView","id":"tv","width":200,"height":80,"onPan":"@{textPan}"},
                {"type":"Label","id":"lb","text":"L","width":200,"height":80,"onPan":"@{labelPan}"}]}""",
            mapOf("textPan" to textPan, "labelPan" to labelPan)
        )
        rule.onNodeWithTag("tv").performTouchInput { swipeRight() }
        rule.onNodeWithTag("lb").performTouchInput { swipeRight() }
        rule.waitForIdle()
        assertEquals("onPan on a TextView", listOf<String>(), onText.toList())
        assertTrue("onPan on a Label (control): $onLabel", onLabel.isNotEmpty())
    }

    private fun submitField(returnKeyType: String?, calls: MutableList<String>) {
        val h: () -> Unit = { calls += "submit" }
        val rk = returnKeyType?.let { ",\"returnKeyType\":\"$it\"" } ?: ""
        show(
            """{"type":"View","child":[{"type":"TextField","id":"tf","width":200,"height":60,"onSubmit":"@{h}"$rk}]}""",
            mapOf("h" to h)
        )
        rule.onNodeWithTag("tf").performClick()
        rule.waitForIdle()
    }

    @OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)
    @Test
    fun enterOnASingleLineFieldCallsOnSubmit() {
        // No returnKeyType: the IME action is Default (the test API will not
        // perform it), so the single-line field's Enter.
        val calls = log()
        submitField(null, calls)
        rule.onNodeWithTag("tf").performKeyInput { pressKey(Key.Enter) }
        rule.waitForIdle()
        assertEquals(listOf("submit"), calls.toList())
    }

    @OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)
    @Test
    fun enterOnADoneFieldCallsOnSubmit() {
        val calls = log()
        submitField("Done", calls)
        rule.onNodeWithTag("tf").performKeyInput { pressKey(Key.Enter) }
        rule.waitForIdle()
        assertEquals(listOf("submit"), calls.toList())
    }

    @Test
    fun theDoneActionCallsOnSubmit() {
        val calls = log()
        submitField("Done", calls)
        rule.onNodeWithTag("tf").performImeAction()
        rule.waitForIdle()
        assertEquals(listOf("submit"), calls.toList())
    }

    @Test
    fun theKeyboardsActionMovesTheFocusToNextFocusId() {
        show(
            """{"type":"View","orientation":"vertical","child":[
                {"type":"TextField","id":"f1","fieldId":"a","nextFocusId":"c","width":200,"height":60},
                {"type":"TextField","id":"f2","fieldId":"b","width":200,"height":60},
                {"type":"TextField","id":"f3","fieldId":"c","width":200,"height":60}]}""",
            emptyMap()
        )
        // The declared field is not the adjacent one: Compose's own Next
        // moves to f2, the declaration to f3 — the two answers differ.
        rule.onNodeWithTag("f1").performClick()
        rule.onNodeWithTag("f1").performImeAction()
        rule.waitForIdle()
        rule.onNodeWithTag("f3").assertIsFocused()
    }

    @Test
    fun aSegmentValueChangeBindingIsCalledWithTheIndex() {
        val calls = log()
        val h: (Any) -> Unit = { calls += it.toString() }
        show(
            """{"type":"View","child":[{"type":"Segment","id":"s","items":["a","b"],"selectedIndex":0,"valueChange":"@{h}"}]}""",
            mapOf("h" to h)
        )
        rule.onAllNodes(hasText("b") and hasClickAction())[0].performClick()
        rule.waitForIdle()
        assertEquals(listOf("1"), calls.toList())
    }
}
