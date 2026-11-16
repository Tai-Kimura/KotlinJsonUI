package com.kotlinjsonui.conformance

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.kotlinjsonui.components.CustomTextField
import com.kotlinjsonui.components.CustomTextFieldWithMargins
import com.kotlinjsonui.core.Configuration
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assume
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `autoFocus` (jsonui-cli 2.3.22) on device — NOT part of the conformance
 * suite, and skipped unless requested:
 *
 *   adb shell am instrument -w \
 *     -e class com.kotlinjsonui.conformance.AutoFocusCodegenProbeTest \
 *     -e autoFocusCodegenProbe 1 \
 *     com.kotlinjsonui.conformance.test/androidx.test.runner.AndroidJUnitRunner
 *
 * Codegen: kjui_tools' TextField and TextView with autoFocus, with an id
 * and without one, pasted unchanged by the generator; a TextField without
 * it is the control. Opened, the field holds the focus (an EditText node
 * focused). The soft keyboard is printed, not asserted: on the headless API
 * 35 emulators here (conf_ci, and an AVD without a hardware keyboard) the
 * IME is not shown even when the field is tapped (imeAfterTap), so the
 * reading says nothing about autoFocus.
 */
@RunWith(AndroidJUnit4::class)
class AutoFocusCodegenProbeTest {
    @Before
    fun skipUnlessRequested() {
        Assume.assumeTrue("set -e autoFocusCodegenProbe 1",
            InstrumentationRegistry.getArguments().getString("autoFocusCodegenProbe") == "1")
    }

    class ProbeData {
        var email = ""
        var note = ""
        var emailFieldIsFocused = false
        var noteViewIsFocused = false
        var plainFieldIsFocused = false
    }

    class ProbeViewModel { fun updateData(values: Map<String, Any?>) {} }

    // {"type":"TextField","text":"@{email}","autoFocus":true}
    @Composable
    private fun TfNoId(data: ProbeData, viewModel: ProbeViewModel) {
        val textFieldState_field1 = rememberTextFieldState(initialText = data.email)
        LaunchedEffect(data.email) { if (textFieldState_field1.text.toString() != data.email) textFieldState_field1.edit { replace(0, length, data.email) } }
        LaunchedEffect(textFieldState_field1.text) { val newValue = textFieldState_field1.text.toString(); if (newValue != data.email) viewModel.updateData(mapOf("email" to newValue)) }
        val focusRequester_textField_0 = remember { FocusRequester() }
        val autoFocusKeyboard_textField_0 = LocalSoftwareKeyboardController.current
        LaunchedEffect(Unit) { focusRequester_textField_0.requestFocus(); autoFocusKeyboard_textField_0?.show() }
        CustomTextField(
            state = textFieldState_field1,
            modifier = Modifier
                .focusRequester(focusRequester_textField_0),
            textStyle = TextStyle(color = Configuration.TextField.defaultTextColor, fontSize = Configuration.TextField.defaultFontSize.sp)
        )
    }

    // {"type":"TextField","id":"email_field","text":"@{email}","autoFocus":true}
    @Composable
    private fun TfId(data: ProbeData, viewModel: ProbeViewModel) {
        val textFieldState_email_field = rememberTextFieldState(initialText = data.email)
        LaunchedEffect(data.email) { if (textFieldState_email_field.text.toString() != data.email) textFieldState_email_field.edit { replace(0, length, data.email) } }
        LaunchedEffect(textFieldState_email_field.text) { val newValue = textFieldState_email_field.text.toString(); if (newValue != data.email) viewModel.updateData(mapOf("email" to newValue)) }
        val focusRequester_email_field = remember { FocusRequester() }
        val autoFocusKeyboard_email_field = LocalSoftwareKeyboardController.current
        LaunchedEffect(Unit) { focusRequester_email_field.requestFocus(); autoFocusKeyboard_email_field?.show() }
        val keyboardController_email_field = LocalSoftwareKeyboardController.current
        LaunchedEffect(data.emailFieldIsFocused) { if (data.emailFieldIsFocused) { focusRequester_email_field.requestFocus(); keyboardController_email_field?.show() } }
        CustomTextField(
            state = textFieldState_email_field,
            modifier = Modifier
                .testTag("email_field")
                .semantics { testTagsAsResourceId = true }
                .onFocusChanged { if (it.isFocused != data.emailFieldIsFocused) viewModel.updateData(mapOf("emailFieldIsFocused" to it.isFocused)) }
                .focusRequester(focusRequester_email_field),
            textStyle = TextStyle(color = Configuration.TextField.defaultTextColor, fontSize = Configuration.TextField.defaultFontSize.sp)
        )
    }

    // {"type":"TextView","text":"@{note}","autoFocus":true}
    @Composable
    private fun TvNoId(data: ProbeData, viewModel: ProbeViewModel) {
        val textFieldState_textView_0 = rememberTextFieldState(initialText = data.note)
        LaunchedEffect(data.note) { if (textFieldState_textView_0.text.toString() != data.note) textFieldState_textView_0.edit { replace(0, length, data.note) } }
        LaunchedEffect(textFieldState_textView_0.text) { val newValue = textFieldState_textView_0.text.toString(); if (newValue != data.note) viewModel.updateData(mapOf("note" to newValue)) }
        val focusRequester_textView_0 = remember { FocusRequester() }
        val autoFocusKeyboard_textView_0 = LocalSoftwareKeyboardController.current
        LaunchedEffect(Unit) { focusRequester_textView_0.requestFocus(); autoFocusKeyboard_textView_0?.show() }
        CustomTextField(
            state = textFieldState_textView_0,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .focusRequester(focusRequester_textView_0),
            isOutlined = true,
            maxLines = Int.MAX_VALUE,
            singleLine = false
        )
    }

    // {"type":"TextView","id":"note_view","text":"@{note}","autoFocus":true}
    @Composable
    private fun TvId(data: ProbeData, viewModel: ProbeViewModel) {
        val textFieldState_note_view = rememberTextFieldState(initialText = data.note)
        LaunchedEffect(data.note) { if (textFieldState_note_view.text.toString() != data.note) textFieldState_note_view.edit { replace(0, length, data.note) } }
        LaunchedEffect(textFieldState_note_view.text) { val newValue = textFieldState_note_view.text.toString(); if (newValue != data.note) viewModel.updateData(mapOf("note" to newValue)) }
        val focusRequester_note_view = remember { FocusRequester() }
        val keyboardController_note_view = LocalSoftwareKeyboardController.current
        LaunchedEffect(data.noteViewIsFocused) { if (data.noteViewIsFocused) { focusRequester_note_view.requestFocus(); keyboardController_note_view?.show() } }
        val autoFocusKeyboard_note_view = LocalSoftwareKeyboardController.current
        LaunchedEffect(Unit) { focusRequester_note_view.requestFocus(); autoFocusKeyboard_note_view?.show() }
        CustomTextField(
            state = textFieldState_note_view,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .testTag("note_view")
                .semantics { testTagsAsResourceId = true }
                .onFocusChanged { if (it.isFocused != data.noteViewIsFocused) viewModel.updateData(mapOf("noteViewIsFocused" to it.isFocused)) }
                .focusRequester(focusRequester_note_view),
            isOutlined = true,
            maxLines = Int.MAX_VALUE,
            singleLine = false
        )
    }

    // {"type":"TextField","id":"plain_field","text":"@{email}"}
    @Composable
    private fun TfNone(data: ProbeData, viewModel: ProbeViewModel) {
        val textFieldState_plain_field = rememberTextFieldState(initialText = data.email)
        LaunchedEffect(data.email) { if (textFieldState_plain_field.text.toString() != data.email) textFieldState_plain_field.edit { replace(0, length, data.email) } }
        LaunchedEffect(textFieldState_plain_field.text) { val newValue = textFieldState_plain_field.text.toString(); if (newValue != data.email) viewModel.updateData(mapOf("email" to newValue)) }
        val focusRequester_plain_field = remember { FocusRequester() }
        val keyboardController_plain_field = LocalSoftwareKeyboardController.current
        LaunchedEffect(data.plainFieldIsFocused) { if (data.plainFieldIsFocused) { focusRequester_plain_field.requestFocus(); keyboardController_plain_field?.show() } }
        CustomTextField(
            state = textFieldState_plain_field,
            modifier = Modifier
                .testTag("plain_field")
                .semantics { testTagsAsResourceId = true }
                .onFocusChanged { if (it.isFocused != data.plainFieldIsFocused) viewModel.updateData(mapOf("plainFieldIsFocused" to it.isFocused)) }
                .focusRequester(focusRequester_plain_field),
            textStyle = TextStyle(color = Configuration.TextField.defaultTextColor, fontSize = Configuration.TextField.defaultFontSize.sp)
        )
    }

    private fun opened(content: @Composable (ProbeData, ProbeViewModel) -> Unit): Pair<Boolean, Boolean> {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val scenario = ActivityScenario.launch(FixtureHostActivity::class.java)
        scenario.onActivity { it.setContent { Column { content(ProbeData(), ProbeViewModel()) } } }
        val focused = device.wait(Until.findObject(By.focused(true).clazz("android.widget.EditText")), 5000) != null
        Thread.sleep(500)
        val ime = device.executeShellCommand("dumpsys input_method").contains("mInputShown=true")
        // positive control of the IME reading: the field tapped as a person does
        device.findObject(By.clazz("android.widget.EditText"))?.click()
        Thread.sleep(800)
        val imeAfterTap = device.executeShellCommand("dumpsys input_method").contains("mInputShown=true")
        println("AF_PROBE focused=$focused ime=$ime imeAfterTap=$imeAfterTap")
        scenario.close()
        Thread.sleep(500)
        return focused to ime
    }

    @Test
    fun theFieldIsFocusedWhenTheScreenAppears() {
        val variants = mapOf<String, @Composable (ProbeData, ProbeViewModel) -> Unit>(
            "TfNoId" to { d, v -> TfNoId(d, v) },
            "TfId" to { d, v -> TfId(d, v) },
            "TvNoId" to { d, v -> TvNoId(d, v) },
            "TvId" to { d, v -> TvId(d, v) }
        )
        for ((name, content) in variants) {
            val (focused, ime) = opened(content)
            println("AF_PROBE $name focused=$focused ime=$ime")
            assertTrue("$name: an EditText holds the focus on appear", focused)
        }
    }

    @Test
    fun theControlTakesNoFocus() {
        val (focused, ime) = opened { d, v -> TfNone(d, v) }
        println("AF_PROBE TfNone focused=$focused ime=$ime")
        assertFalse("a field without autoFocus is not focused on appear", focused)
    }
}
