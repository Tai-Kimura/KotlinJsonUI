package com.kotlinjsonui.conformance

import android.view.accessibility.AccessibilityNodeInfo
import androidx.compose.ui.semantics.disabled
import androidx.compose.foundation.layout.size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import com.kotlinjsonui.components.CustomTextField
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Button
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventTimeoutCancellationException
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.google.gson.JsonParser
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.dynamic.DynamicView
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assume
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * What `userInteractionEnabled: false` stops on device — NOT part of the
 * conformance suite, and skipped unless requested:
 *
 *   adb shell am instrument -w \
 *     -e class com.kotlinjsonui.conformance.InteractionGateProbeTest \
 *     -e interactionGateProbe 1 \
 *     com.kotlinjsonui.conformance.test/androidx.test.runner.AndroidJUnitRunner
 *
 * attribute_definitions.json common.userInteractionEnabled enables user
 * interaction: under `false` (or a binding resolving false) the view's own
 * tap, its long press, its children's taps and a control's own operation
 * do not happen. The rows with no gate are the controls: they say the tap
 * and the press reach.
 *
 * Codegen: what kjui_tools emits (ComposeBuilder) for a View with onClick
 * holding a child with its own onClick, a Label with onClick, a View with
 * onLongPress and a Switch, pasted unchanged; `data` and `viewModel` are
 * the probe's. Dynamic: the same nodes through DynamicView.
 */
@OptIn(ExperimentalComposeUiApi::class)
@RunWith(AndroidJUnit4::class)
class InteractionGateProbeTest {

    @Before
    fun skipUnlessRequested() {
        val enabled = InstrumentationRegistry.getArguments().getString("interactionGateProbe") == "1"
        Assume.assumeTrue("set -e interactionGateProbe 1", enabled)
    }

    private val counts = ConcurrentHashMap<String, AtomicInteger>()
    private fun hit(k: String) { counts.getOrPut(k) { AtomicInteger() }.incrementAndGet() }
    private fun calls(k: String) = counts[k]?.get() ?: 0

    /** The codegen rows' data surface. */
    private inner class CodegenData {
        val gateClosed: Boolean? = false
        val swUieFalse: Boolean = false
        val swUieOld: Boolean = false
        val tfPlain: String = ""
        val tfUieFalse: String = ""
        val tvPlain: String = ""
        val tvUieFalse: String = ""
        val cgTfPlainIsFocused: Boolean = false
        val cgTfUieFalseIsFocused: Boolean = false
        val cgTvPlainIsFocused: Boolean = false
        val cgTvUieFalseIsFocused: Boolean = false
        val onCgBtnPlain: (() -> Unit)? = { hit("cgBtnPlain") }
        val onCgBtnUieFalse: (() -> Unit)? = { hit("cgBtnUieFalse") }
        val onCgDragEnBound: (() -> Unit)? = { hit("cgDragEnBound") }
        val onCgDragEnFalse: (() -> Unit)? = { hit("cgDragEnFalse") }
        val onCgDragPlain: (() -> Unit)? = { hit("cgDragPlain") }
        val onCgHoldEnBound: (() -> Unit)? = { hit("cgHoldEnBound") }
        val onCgHoldEnFalse: (() -> Unit)? = { hit("cgHoldEnFalse") }
        val onCgHoldPlain: (() -> Unit)? = { hit("cgHoldPlain") }
        val onCgImgEnBound: (() -> Unit)? = { hit("cgImgEnBound") }
        val onCgImgEnFalse: (() -> Unit)? = { hit("cgImgEnFalse") }
        val onCgImgPlain: (() -> Unit)? = { hit("cgImgPlain") }
        val onCgKidPlain: (() -> Unit)? = { hit("cgKidPlain") }
        val onCgKidUieBound: (() -> Unit)? = { hit("cgKidUieBound") }
        val onCgKidUieFalse: (() -> Unit)? = { hit("cgKidUieFalse") }
        val onCgLblUieFalse: (() -> Unit)? = { hit("cgLblUieFalse") }
        val onCgLpPlain: (() -> Unit)? = { hit("cgLpPlain") }
        val onCgLpUieBound: (() -> Unit)? = { hit("cgLpUieBound") }
        val onCgLpUieFalse: (() -> Unit)? = { hit("cgLpUieFalse") }
        val onCgLpUieOld: (() -> Unit)? = { hit("cgLpUieOld") }
        val onCgPanPlain: (() -> Unit)? = { hit("cgPanPlain") }
        val onCgPanUieFalse: (() -> Unit)? = { hit("cgPanUieFalse") }
        val onCgParPlain: (() -> Unit)? = { hit("cgParPlain") }
        val onCgParUieBound: (() -> Unit)? = { hit("cgParUieBound") }
        val onCgParUieFalse: (() -> Unit)? = { hit("cgParUieFalse") }
    }

    /** The codegen Switch's `viewModel.updateData`: a switch that switched. */
    private inner class ViewModel {
        fun updateData(m: Map<String, Any>) { m.keys.forEach { hit("update_$it") } }
    }

    @Composable
    private fun Codegen(data: CodegenData, viewModel: ViewModel) {
        Row {
            Column {
                Box(
                    modifier = Modifier
                        .testTag("cgParPlain")
                        .semantics { testTagsAsResourceId = true }
                        .background(Color(android.graphics.Color.parseColor("#DDDDDD")))
                        .clickable { data.onCgParPlain?.invoke() }
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .testTag("cgKidPlain")
                            .semantics { testTagsAsResourceId = true }
                            .requiredWidth(80.dp)
                            .requiredHeight(30.dp)
                            .background(Color(android.graphics.Color.parseColor("#3366CC")))
                            .clickable(role = Role.Button) { data.onCgKidPlain?.invoke() }
                    ) {
                    }
                }
                Box(
                    modifier = Modifier
                        .testTag("cgParUieFalse")
                        .semantics { testTagsAsResourceId = true }
                        .background(Color(android.graphics.Color.parseColor("#DDDDDD")))
                        .clickable { data.onCgParUieFalse?.invoke() }
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    awaitPointerEvent(PointerEventPass.Initial).changes.forEach { it.consume() }
                                }
                            }
                        }
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .testTag("cgKidUieFalse")
                            .semantics { testTagsAsResourceId = true }
                            .requiredWidth(80.dp)
                            .requiredHeight(30.dp)
                            .background(Color(android.graphics.Color.parseColor("#3366CC")))
                            .clickable(role = Role.Button) { data.onCgKidUieFalse?.invoke() }
                    ) {
                    }
                }
                Box(
                    modifier = Modifier
                        .testTag("cgParUieBound")
                        .semantics { testTagsAsResourceId = true }
                        .background(Color(android.graphics.Color.parseColor("#DDDDDD")))
                        .clickable { data.onCgParUieBound?.invoke() }
                        .pointerInput((data.gateClosed ?: false)) {
                            if (!((data.gateClosed ?: false))) {
                                awaitPointerEventScope {
                                    while (true) {
                                        awaitPointerEvent(PointerEventPass.Initial).changes.forEach { it.consume() }
                                    }
                                }
                            }
                        }
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .testTag("cgKidUieBound")
                            .semantics { testTagsAsResourceId = true }
                            .requiredWidth(80.dp)
                            .requiredHeight(30.dp)
                            .background(Color(android.graphics.Color.parseColor("#3366CC")))
                            .clickable(role = Role.Button) { data.onCgKidUieBound?.invoke() }
                    ) {
                    }
                }
                val resolved_text1 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = null,
                    italic = false
                ))
                Text(
                    text = "cgLblUieFalse",
                    fontFamily = resolved_text1.family,
                    fontWeight = resolved_text1.weight,
                    fontSize = resolved_text1.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text1.style ?: FontStyle.Normal,
                    modifier = Modifier
                        .testTag("cgLblUieFalse")
                        .semantics { testTagsAsResourceId = true }
                        .clickable(role = Role.Button) { data.onCgLblUieFalse?.invoke() }
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    awaitPointerEvent(PointerEventPass.Initial).changes.forEach { it.consume() }
                                }
                            }
                        }
                )
            }
            Column {
                Box(
                    modifier = Modifier
                        .testTag("cgLpPlain")
                        .semantics { testTagsAsResourceId = true }
                        .requiredWidth(80.dp)
                        .requiredHeight(30.dp)
                        .background(Color(android.graphics.Color.parseColor("#3366CC")))
                        .pointerInput(data) {
                            awaitEachGesture {
                                awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                                val longPressed = try {
                                    withTimeout(viewConfiguration.longPressTimeoutMillis) {
                                        var event: PointerEvent
                                        do {
                                            event = awaitPointerEvent(PointerEventPass.Initial)
                                        } while (event.changes.any { it.pressed })
                                    }
                                    false
                                } catch (_: PointerEventTimeoutCancellationException) {
                                    true
                                }
                                if (longPressed) {
                                    data.onCgLpPlain?.invoke()
                                    var event: PointerEvent
                                    do {
                                        event = awaitPointerEvent(PointerEventPass.Initial)
                                        event.changes.forEach { it.consume() }
                                    } while (event.changes.any { it.pressed })
                                }
                            }
                        }
                ) {
                }
                Box(
                    modifier = Modifier
                        .testTag("cgLpUieFalse")
                        .semantics { testTagsAsResourceId = true }
                        .requiredWidth(80.dp)
                        .requiredHeight(30.dp)
                        .background(Color(android.graphics.Color.parseColor("#3366CC")))
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    awaitPointerEvent(PointerEventPass.Initial).changes.forEach { it.consume() }
                                }
                            }
                        }
                ) {
                }
                Box(
                    modifier = Modifier
                        .testTag("cgLpUieBound")
                        .semantics { testTagsAsResourceId = true }
                        .requiredWidth(80.dp)
                        .requiredHeight(30.dp)
                        .background(Color(android.graphics.Color.parseColor("#3366CC")))
                        .pointerInput(data) {
                            awaitEachGesture {
                                awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                                val longPressed = try {
                                    withTimeout(viewConfiguration.longPressTimeoutMillis) {
                                        var event: PointerEvent
                                        do {
                                            event = awaitPointerEvent(PointerEventPass.Initial)
                                        } while (event.changes.any { it.pressed })
                                    }
                                    false
                                } catch (_: PointerEventTimeoutCancellationException) {
                                    true
                                }
                                if (longPressed && (data.gateClosed ?: false)) {
                                    data.onCgLpUieBound?.invoke()
                                    var event: PointerEvent
                                    do {
                                        event = awaitPointerEvent(PointerEventPass.Initial)
                                        event.changes.forEach { it.consume() }
                                    } while (event.changes.any { it.pressed })
                                }
                            }
                        }
                        .pointerInput((data.gateClosed ?: false)) {
                            if (!((data.gateClosed ?: false))) {
                                awaitPointerEventScope {
                                    while (true) {
                                        awaitPointerEvent(PointerEventPass.Initial).changes.forEach { it.consume() }
                                    }
                                }
                            }
                        }
                ) {
                }
                Box(
                    modifier = Modifier
                        .testTag("cgPanUieFalse")
                        .semantics { testTagsAsResourceId = true }
                        .requiredWidth(120.dp)
                        .requiredHeight(30.dp)
                        .background(Color(android.graphics.Color.parseColor("#3366CC")))
                        .pointerInput(data) {
                            var total = Offset.Zero
                            detectDragGestures(
                                onDragStart = { total = Offset.Zero },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    total += dragAmount
                                    data.onCgPanUieFalse?.invoke()
                                }
                            )
                        }
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    awaitPointerEvent(PointerEventPass.Initial).changes.forEach { it.consume() }
                                }
                            }
                        }
                ) {
                }
                Box(
                    modifier = Modifier
                        .testTag("cgPanPlain")
                        .semantics { testTagsAsResourceId = true }
                        .requiredWidth(120.dp)
                        .requiredHeight(30.dp)
                        .background(Color(android.graphics.Color.parseColor("#3366CC")))
                        .pointerInput(data) {
                            var total = Offset.Zero
                            detectDragGestures(
                                onDragStart = { total = Offset.Zero },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    total += dragAmount
                                    data.onCgPanPlain?.invoke()
                                }
                            )
                        }
                ) {
                }
                Switch(
                    checked = data.swUieFalse,
                    onCheckedChange = { newValue -> viewModel.updateData(mapOf("swUieFalse" to newValue)) },
                    modifier = Modifier
                        .testTag("cgSwUieFalse")
                        .semantics { testTagsAsResourceId = true }
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    awaitPointerEvent(PointerEventPass.Initial).changes.forEach { it.consume() }
                                }
                            }
                        }
                )
                Button(
                    onClick = { data.onCgBtnUieFalse?.invoke() },
                    modifier = Modifier
                        .testTag("cgBtnUieFalse")
                        .semantics { testTagsAsResourceId = true }
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    awaitPointerEvent(PointerEventPass.Initial).changes.forEach { it.consume() }
                                }
                            }
                        },
                    shape = RoundedCornerShape(Configuration.Button.defaultCornerRadius.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                            containerColor = Configuration.Button.defaultBackgroundColor,
                            disabledContainerColor = Configuration.Button.defaultBackgroundColor.copy(alpha = 0.5f),
                            contentColor = Configuration.Button.defaultTextColor,
                            disabledContentColor = Configuration.Button.defaultTextColor.copy(alpha = 0.5f)
                        )
                ) {
                    Text("cgBtnUieFalse")
                }
                Button(
                    onClick = { data.onCgBtnPlain?.invoke() },
                    modifier = Modifier
                        .testTag("cgBtnPlain")
                        .semantics { testTagsAsResourceId = true },
                    shape = RoundedCornerShape(Configuration.Button.defaultCornerRadius.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                            containerColor = Configuration.Button.defaultBackgroundColor,
                            disabledContainerColor = Configuration.Button.defaultBackgroundColor.copy(alpha = 0.5f),
                            contentColor = Configuration.Button.defaultTextColor,
                            disabledContentColor = Configuration.Button.defaultTextColor.copy(alpha = 0.5f)
                        )
                ) {
                    Text("cgBtnPlain")
                }
                Box(
                    modifier = Modifier
                        .testTag("cgLpUieOld")
                        .semantics { testTagsAsResourceId = true }
                        .requiredWidth(80.dp)
                        .requiredHeight(30.dp)
                        .background(Color(android.graphics.Color.parseColor("#3366CC")))
                        .pointerInput(data) {
                            awaitEachGesture {
                                awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                                val longPressed = try {
                                    withTimeout(viewConfiguration.longPressTimeoutMillis) {
                                        var event: PointerEvent
                                        do {
                                            event = awaitPointerEvent(PointerEventPass.Initial)
                                        } while (event.changes.any { it.pressed })
                                    }
                                    false
                                } catch (_: PointerEventTimeoutCancellationException) {
                                    true
                                }
                                if (longPressed) {
                                    data.onCgLpUieOld?.invoke()
                                    var event: PointerEvent
                                    do {
                                        event = awaitPointerEvent(PointerEventPass.Initial)
                                        event.changes.forEach { it.consume() }
                                    } while (event.changes.any { it.pressed })
                                }
                            }
                        }
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    awaitPointerEvent(PointerEventPass.Initial).changes.forEach { it.consume() }
                                }
                            }
                        }
                ) {
                }
                Switch(
                    checked = data.swUieOld,
                    onCheckedChange = { newValue -> viewModel.updateData(mapOf("swUieOld" to newValue)) },
                    modifier = Modifier
                        .testTag("cgSwUieOld")
                        .semantics { testTagsAsResourceId = true }
                )
            }
            Column {
                val textFieldState_cgTfPlain = rememberTextFieldState(initialText = data.tfPlain)
                LaunchedEffect(data.tfPlain) { if (textFieldState_cgTfPlain.text.toString() != data.tfPlain) textFieldState_cgTfPlain.edit { replace(0, length, data.tfPlain) } }
                LaunchedEffect(textFieldState_cgTfPlain.text) { val newValue = textFieldState_cgTfPlain.text.toString(); if (newValue != data.tfPlain) viewModel.updateData(mapOf("tfPlain" to newValue)) }
                val focusRequester_cgTfPlain = remember { FocusRequester() }
                val keyboardController_cgTfPlain = LocalSoftwareKeyboardController.current
                LaunchedEffect(data.cgTfPlainIsFocused) { if (data.cgTfPlainIsFocused) { focusRequester_cgTfPlain.requestFocus(); keyboardController_cgTfPlain?.show() } }
                CustomTextField(
                    state = textFieldState_cgTfPlain,
                    modifier = Modifier
                        .testTag("cgTfPlain")
                        .semantics { testTagsAsResourceId = true }
                        .requiredWidth(120.dp)
                        .onFocusChanged { if (it.isFocused != data.cgTfPlainIsFocused) viewModel.updateData(mapOf("cgTfPlainIsFocused" to it.isFocused)) }
                        .focusRequester(focusRequester_cgTfPlain),
                    placeholder = { Text(
                            text = "tf",
                            color = Configuration.TextField.defaultPlaceholderColor
                        ) },
                    textStyle = TextStyle(color = Configuration.TextField.defaultTextColor, fontSize = Configuration.TextField.defaultFontSize.sp)
                )
                val textFieldState_cgTfUieFalse = rememberTextFieldState(initialText = data.tfUieFalse)
                LaunchedEffect(data.tfUieFalse) { if (textFieldState_cgTfUieFalse.text.toString() != data.tfUieFalse) textFieldState_cgTfUieFalse.edit { replace(0, length, data.tfUieFalse) } }
                LaunchedEffect(textFieldState_cgTfUieFalse.text) { val newValue = textFieldState_cgTfUieFalse.text.toString(); if (newValue != data.tfUieFalse) viewModel.updateData(mapOf("tfUieFalse" to newValue)) }
                val focusRequester_cgTfUieFalse = remember { FocusRequester() }
                val keyboardController_cgTfUieFalse = LocalSoftwareKeyboardController.current
                LaunchedEffect(data.cgTfUieFalseIsFocused) { if (data.cgTfUieFalseIsFocused) { focusRequester_cgTfUieFalse.requestFocus(); keyboardController_cgTfUieFalse?.show() } }
                CustomTextField(
                    state = textFieldState_cgTfUieFalse,
                    modifier = Modifier
                        .testTag("cgTfUieFalse")
                        .semantics { testTagsAsResourceId = true }
                        .requiredWidth(120.dp)
                        .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                awaitPointerEvent(PointerEventPass.Initial).changes.forEach { it.consume() }
                            }
                        }
                    }
                        .onFocusChanged { if (it.isFocused != data.cgTfUieFalseIsFocused) viewModel.updateData(mapOf("cgTfUieFalseIsFocused" to it.isFocused)) }
                        .focusRequester(focusRequester_cgTfUieFalse),
                    placeholder = { Text(
                            text = "tf",
                            color = Configuration.TextField.defaultPlaceholderColor
                        ) },
                    textStyle = TextStyle(color = Configuration.TextField.defaultTextColor, fontSize = Configuration.TextField.defaultFontSize.sp)
                )
                val textFieldState_cgTvPlain = rememberTextFieldState(initialText = data.tvPlain)
                LaunchedEffect(data.tvPlain) { if (textFieldState_cgTvPlain.text.toString() != data.tvPlain) textFieldState_cgTvPlain.edit { replace(0, length, data.tvPlain) } }
                LaunchedEffect(textFieldState_cgTvPlain.text) { val newValue = textFieldState_cgTvPlain.text.toString(); if (newValue != data.tvPlain) viewModel.updateData(mapOf("tvPlain" to newValue)) }
                val focusRequester_cgTvPlain = remember { FocusRequester() }
                val keyboardController_cgTvPlain = LocalSoftwareKeyboardController.current
                LaunchedEffect(data.cgTvPlainIsFocused) { if (data.cgTvPlainIsFocused) { focusRequester_cgTvPlain.requestFocus(); keyboardController_cgTvPlain?.show() } }
                CustomTextField(
                    state = textFieldState_cgTvPlain,
                    modifier = Modifier
                        .testTag("cgTvPlain")
                        .semantics { testTagsAsResourceId = true }
                        .requiredWidth(120.dp)
                        .requiredHeight(50.dp)
                        .height(50.dp)
                        .onFocusChanged { if (it.isFocused != data.cgTvPlainIsFocused) viewModel.updateData(mapOf("cgTvPlainIsFocused" to it.isFocused)) }
                        .focusRequester(focusRequester_cgTvPlain),
                    isOutlined = true,
                    maxLines = Int.MAX_VALUE,
                    singleLine = false
                )
                val textFieldState_cgTvUieFalse = rememberTextFieldState(initialText = data.tvUieFalse)
                LaunchedEffect(data.tvUieFalse) { if (textFieldState_cgTvUieFalse.text.toString() != data.tvUieFalse) textFieldState_cgTvUieFalse.edit { replace(0, length, data.tvUieFalse) } }
                LaunchedEffect(textFieldState_cgTvUieFalse.text) { val newValue = textFieldState_cgTvUieFalse.text.toString(); if (newValue != data.tvUieFalse) viewModel.updateData(mapOf("tvUieFalse" to newValue)) }
                val focusRequester_cgTvUieFalse = remember { FocusRequester() }
                val keyboardController_cgTvUieFalse = LocalSoftwareKeyboardController.current
                LaunchedEffect(data.cgTvUieFalseIsFocused) { if (data.cgTvUieFalseIsFocused) { focusRequester_cgTvUieFalse.requestFocus(); keyboardController_cgTvUieFalse?.show() } }
                CustomTextField(
                    state = textFieldState_cgTvUieFalse,
                    modifier = Modifier
                        .testTag("cgTvUieFalse")
                        .semantics { testTagsAsResourceId = true }
                        .requiredWidth(120.dp)
                        .requiredHeight(50.dp)
                        .height(50.dp)
                        .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                awaitPointerEvent(PointerEventPass.Initial).changes.forEach { it.consume() }
                            }
                        }
                    }
                        .onFocusChanged { if (it.isFocused != data.cgTvUieFalseIsFocused) viewModel.updateData(mapOf("cgTvUieFalseIsFocused" to it.isFocused)) }
                        .focusRequester(focusRequester_cgTvUieFalse),
                    isOutlined = true,
                    maxLines = Int.MAX_VALUE,
                    singleLine = false
                )
            }
            Column {
                Image(
                    painter = painterResource(id = R.drawable.conformance_sample),
                    contentDescription = "img Plain",
                    modifier = Modifier
                        .testTag("cgImgPlain")
                        .semantics { testTagsAsResourceId = true }
                        .size(40.dp, 30.dp)
                        .clickable(role = Role.Button) { data.onCgImgPlain?.invoke() }
                )
                Box(
                    modifier = Modifier
                        .testTag("cgHoldPlain")
                        .semantics { testTagsAsResourceId = true }
                        .requiredWidth(40.dp)
                        .requiredHeight(30.dp)
                        .background(Color(android.graphics.Color.parseColor("#3366CC")))
                        .pointerInput(data) {
                            awaitEachGesture {
                                awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                                val longPressed = try {
                                    withTimeout(viewConfiguration.longPressTimeoutMillis) {
                                        var event: PointerEvent
                                        do {
                                            event = awaitPointerEvent(PointerEventPass.Initial)
                                        } while (event.changes.any { it.pressed })
                                    }
                                    false
                                } catch (_: PointerEventTimeoutCancellationException) {
                                    true
                                }
                                if (longPressed) {
                                    data.onCgHoldPlain?.invoke()
                                    var event: PointerEvent
                                    do {
                                        event = awaitPointerEvent(PointerEventPass.Initial)
                                        event.changes.forEach { it.consume() }
                                    } while (event.changes.any { it.pressed })
                                }
                            }
                        }
                ) {
                }
                Box(
                    modifier = Modifier
                        .testTag("cgDragPlain")
                        .semantics { testTagsAsResourceId = true }
                        .requiredWidth(120.dp)
                        .requiredHeight(30.dp)
                        .background(Color(android.graphics.Color.parseColor("#3366CC")))
                        .pointerInput(data) {
                            var total = Offset.Zero
                            detectDragGestures(
                                onDragStart = { total = Offset.Zero },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    total += dragAmount
                                    data.onCgDragPlain?.invoke()
                                }
                            )
                        }
                ) {
                }
                Image(
                    painter = painterResource(id = R.drawable.conformance_sample),
                    contentDescription = "img EnFalse",
                    modifier = Modifier
                        .testTag("cgImgEnFalse")
                        .semantics { testTagsAsResourceId = true }
                        .size(40.dp, 30.dp)
                        .clickable(enabled = false) { data.onCgImgEnFalse?.invoke() }
                        .semantics { disabled() }
                )
                Box(
                    modifier = Modifier
                        .testTag("cgHoldEnFalse")
                        .semantics { testTagsAsResourceId = true }
                        .requiredWidth(40.dp)
                        .requiredHeight(30.dp)
                        .background(Color(android.graphics.Color.parseColor("#3366CC")))
                        .semantics { disabled() }
                ) {
                }
                Box(
                    modifier = Modifier
                        .testTag("cgDragEnFalse")
                        .semantics { testTagsAsResourceId = true }
                        .requiredWidth(120.dp)
                        .requiredHeight(30.dp)
                        .background(Color(android.graphics.Color.parseColor("#3366CC")))
                        .semantics { disabled() }
                ) {
                }
                Image(
                    painter = painterResource(id = R.drawable.conformance_sample),
                    contentDescription = "img EnBound",
                    modifier = Modifier
                        .testTag("cgImgEnBound")
                        .semantics { testTagsAsResourceId = true }
                        .size(40.dp, 30.dp)
                        .clickable(enabled = (data.gateClosed ?: false), role = Role.Button) { data.onCgImgEnBound?.invoke() }
                        .semantics { if (!(data.gateClosed ?: false)) disabled() }
                )
                Box(
                    modifier = Modifier
                        .testTag("cgHoldEnBound")
                        .semantics { testTagsAsResourceId = true }
                        .requiredWidth(40.dp)
                        .requiredHeight(30.dp)
                        .background(Color(android.graphics.Color.parseColor("#3366CC")))
                        .pointerInput(data) {
                            awaitEachGesture {
                                awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                                val longPressed = try {
                                    withTimeout(viewConfiguration.longPressTimeoutMillis) {
                                        var event: PointerEvent
                                        do {
                                            event = awaitPointerEvent(PointerEventPass.Initial)
                                        } while (event.changes.any { it.pressed })
                                    }
                                    false
                                } catch (_: PointerEventTimeoutCancellationException) {
                                    true
                                }
                                if (longPressed && (data.gateClosed ?: false)) {
                                    data.onCgHoldEnBound?.invoke()
                                    var event: PointerEvent
                                    do {
                                        event = awaitPointerEvent(PointerEventPass.Initial)
                                        event.changes.forEach { it.consume() }
                                    } while (event.changes.any { it.pressed })
                                }
                            }
                        }
                        .semantics { if (!(data.gateClosed ?: false)) disabled() }
                ) {
                }
                Box(
                    modifier = Modifier
                        .testTag("cgDragEnBound")
                        .semantics { testTagsAsResourceId = true }
                        .requiredWidth(120.dp)
                        .requiredHeight(30.dp)
                        .background(Color(android.graphics.Color.parseColor("#3366CC")))
                        .pointerInput(data) {
                            var total = Offset.Zero
                            detectDragGestures(
                                onDragStart = { total = Offset.Zero },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    total += dragAmount
                                    if ((data.gateClosed ?: false)) data.onCgDragEnBound?.invoke()
                                }
                            )
                        }
                        .semantics { if (!(data.gateClosed ?: false)) disabled() }
                ) {
                }
            }
        }
    }

    private fun par(name: String, gate: String) =
        "{\"type\": \"View\", \"id\": \"dynPar$name\", \"onClick\": \"@{on_dynPar$name}\", \"padding\": 16, " +
            "\"background\": \"#DDDDDD\"$gate, \"child\": [{\"type\": \"View\", \"id\": \"dynKid$name\", " +
            "\"onClick\": \"@{on_dynKid$name}\", \"width\": 80, \"height\": 30, \"background\": \"#3366CC\"}]}"

    private fun leaf(id: String, event: String, extra: String = "") =
        "{\"type\": \"View\", \"id\": \"$id\", \"$event\": \"@{on_$id}\", \"width\": 120, \"height\": 30, " +
            "\"background\": \"#3366CC\"$extra}"

    private fun tabs(id: String, extra: String) =
        "{\"type\": \"View\", \"width\": 280, \"height\": 150, \"child\": [{\"type\": \"TabView\", \"id\": \"$id\", " +
            "\"tabs\": [{\"title\": \"a_$id\"}, {\"title\": \"b_$id\"}]$extra}]}"

    private val enabledGates = listOf("Plain" to "", "EnFalse" to ", \"enabled\": false", "EnBound" to ", \"enabled\": \"@{gate_closed}\"")
    private val enabledRows = enabledGates.flatMap { (g, gate) ->
        listOf(
            "{\"type\": \"Image\", \"id\": \"dynImg$g\", \"srcName\": \"conformance_sample\", \"alt\": \"img $g\", \"width\": 40, " +
                "\"height\": 30, \"onClick\": \"@{on_dynImg$g}\"$gate}",
            leaf("dynHold$g", "onLongPress", gate),
            leaf("dynDrag$g", "onPan", gate),
        )
    }

    private val uieFalse = ", \"userInteractionEnabled\": false"
    private val uieBound = ", \"userInteractionEnabled\": \"@{gate_closed}\""

    private val dynamicLayout = "{\"type\": \"View\", \"orientation\": \"horizontal\", \"child\": [" +
        "{\"type\": \"View\", \"orientation\": \"vertical\", \"child\": [" + listOf(
            par("Plain", ""),
            par("UieFalse", uieFalse),
            par("UieBound", uieBound),
            "{\"type\": \"Label\", \"id\": \"dynLblUieFalse\", \"text\": \"dynLblUieFalse\", \"onClick\": \"@{on_dynLblUieFalse}\"$uieFalse}",
            "{\"type\": \"Switch\", \"id\": \"dynSwUieFalse\", \"isOn\": \"@{sw_dyn}\"$uieFalse}",
            "{\"type\": \"Switch\", \"id\": \"dynSwPlain\", \"isOn\": \"@{sw_dyn_plain}\"}",
            "{\"type\": \"Button\", \"id\": \"dynBtnPlain\", \"text\": \"dynBtnPlain\", \"onClick\": \"@{on_dynBtnPlain}\"}",
            "{\"type\": \"Button\", \"id\": \"dynBtnUieFalse\", \"text\": \"dynBtnUieFalse\", \"onClick\": \"@{on_dynBtnUieFalse}\"$uieFalse}",
        ).joinToString(",") + "]}," +
        "{\"type\": \"View\", \"orientation\": \"vertical\", \"child\": [" + listOf(
            leaf("dynLpPlain", "onLongPress"),
            leaf("dynLpUieFalse", "onLongPress", uieFalse),
            leaf("dynLpUieBound", "onLongPress", uieBound),
            leaf("dynPanPlain", "onPan"),
            leaf("dynPanUieFalse", "onPan", uieFalse),
            tabs("dynTabPlain", ""),
            tabs("dynTabUieFalse", uieFalse),
            "{\"type\": \"TextField\", \"id\": \"dynTfPlain\", \"text\": \"@{dyn_tf_plain}\", \"hint\": \"tf\", \"width\": 120}",
            "{\"type\": \"TextField\", \"id\": \"dynTfUieFalse\", \"text\": \"@{dyn_tf_uie}\", \"hint\": \"tf\", \"width\": 120$uieFalse}",
            "{\"type\": \"TextView\", \"id\": \"dynTvPlain\", \"text\": \"@{dyn_tv_plain}\", \"width\": 120, \"height\": 50}",
            "{\"type\": \"TextView\", \"id\": \"dynTvUieFalse\", \"text\": \"@{dyn_tv_uie}\", \"width\": 120, \"height\": 50$uieFalse}",
            "{\"type\": \"SafeAreaView\", \"id\": \"dynSafeUieFalse\", \"width\": 160, \"height\": 40$uieFalse, \"child\": [" +
                "{\"type\": \"Button\", \"id\": \"dynSafeKid\", \"text\": \"dynSafeKid\", \"onClick\": \"@{on_dynSafeKid}\"}]}",
        ).joinToString(",") + "]}," +
        "{\"type\": \"View\", \"orientation\": \"vertical\", \"child\": [" + enabledRows.joinToString(",") + "]}]}"

    private fun collect(node: AccessibilityNodeInfo?, out: MutableMap<String, AccessibilityNodeInfo>) {
        if (node == null) return
        node.viewIdResourceName?.let { out.putIfAbsent(it.substringAfterLast('/'), node) }
        for (i in 0 until node.childCount) collect(node.getChild(i), out)
    }

    @Test
    fun theGateStopsTheViewAndWhatIsInIt() {
        val data = mutableMapOf<String, Any>("gate_closed" to false, "sw_dyn" to false, "sw_dyn_plain" to false)
        for (n in listOf("dynParPlain", "dynKidPlain", "dynParUieFalse", "dynKidUieFalse", "dynParUieBound", "dynKidUieBound",
                "dynLblUieFalse", "dynLpPlain", "dynLpUieFalse", "dynLpUieBound", "dynBtnPlain", "dynBtnUieFalse", "dynSafeKid")) {
            data["on_$n"] = { hit(n) }
        }
        // a pan handler takes the translation
        for (n in listOf("dynPanPlain", "dynPanUieFalse")) data["on_$n"] = { _: Any? -> hit(n) }
        for ((g, _) in enabledGates) {
            for (n in listOf("dynImg$g", "dynHold$g")) data["on_$n"] = { hit(n) }
            data["on_dynDrag$g"] = { _: Any? -> hit("dynDrag$g") }
        }
        data["updateData"] = { m: Map<String, Any> -> m.keys.forEach { hit("update_$it") } }
        for (k in listOf("dyn_tf_plain", "dyn_tf_uie", "dyn_tv_plain", "dyn_tv_uie")) data[k] = ""

        val scenario = ActivityScenario.launch(FixtureHostActivity::class.java)
        scenario.onActivity { activity ->
            activity.setContent {
                androidx.compose.foundation.layout.Row(Modifier.padding(top = 40.dp).semantics { testTagsAsResourceId = true }) {
                    Codegen(CodegenData(), ViewModel())
                    DynamicView(json = JsonParser.parseString(dynamicLayout).asJsonObject, data = data)
                }
            }
        }
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.wait(Until.hasObject(By.res("dynSwPlain")), 10_000)
        val density = InstrumentationRegistry.getInstrumentation().targetContext.resources.displayMetrics.density

        fun find(id: String) = device.findObject(By.res(id)).also { if (it == null) println("GATE_PROBE no node $id") }
        fun tapPadding(kid: String) {
            val b = find(kid)?.visibleBounds ?: return
            device.click(b.right + (8 * density).toInt(), b.centerY()); device.waitForIdle()
        }
        fun tap(id: String) { find(id)?.click(); device.waitForIdle() }
        fun press(id: String) { find(id)?.click(1500); device.waitForIdle() }
        fun drag(id: String) {
            val b = find(id)?.visibleBounds ?: return
            device.swipe(b.left + 10, b.centerY(), b.right - 10, b.centerY(), 20); device.waitForIdle()
        }
        for (p in listOf("cg", "dyn")) {
            for (name in listOf("Plain", "UieFalse", "UieBound")) {
                tapPadding("${p}Kid$name")
                tap("${p}Kid$name")
            }
            tap("${p}LblUieFalse")
            for (name in listOf("Plain", "UieFalse", "UieBound")) press("${p}Lp$name")
            for (name in listOf("Plain", "UieFalse")) drag("${p}Pan$name")
            tap("${p}SwUieFalse")
            for (name in listOf("Plain", "UieFalse")) tap("${p}Btn$name")
        }
        tap("dynSafeKid")
        // the fields last: a focused one brings the keyboard up
        val focused = mutableMapOf<String, Boolean?>()
        for (suffix in listOf("UieFalse", "Plain")) for (p in listOf("cg", "dyn")) for (kind in listOf("Tf", "Tv")) {
            val id = "$p$kind$suffix"
            tap(id)
            Thread.sleep(300)
            focused[id] = if (p == "cg") calls("update_${id}IsFocused") > 0 else find(id)?.let { o ->
                o.isFocused || o.findObjects(By.focused(true)).isNotEmpty()
            }
        }
        println("GATE_PROBE focused=$focused")
        for (p in listOf("cg", "dyn")) for ((g, _) in enabledGates) {
            tap("${p}Img$g"); press("${p}Hold$g"); drag("${p}Drag$g")
        }
        press("cgLpUieOld")
        tap("cgSwUieOld")
        tap("dynSwPlain")
        val tabSwitched = mutableMapOf<String, Boolean?>()
        for (t in listOf("dynTabUieFalse", "dynTabPlain")) {
            tap("${t}_tab_1")
            tabSwitched[t] = find("${t}_tab_1")?.isSelected
        }
        Thread.sleep(500)
        println("GATE_PROBE counts=${counts.keys.sorted().joinToString(",") { "$it=${calls(it)}" }}")
        println("GATE_PROBE tabs=$tabSwitched")
        scenario.close()

        val want = mutableMapOf<String, Int>()
        for (p in listOf("cg", "dyn")) {
            // the parent: its padding tap and the tap in its child's frame
            want["${p}ParPlain"] = 1; want["${p}KidPlain"] = 1
            for (name in listOf("UieFalse", "UieBound")) { want["${p}Par$name"] = 0; want["${p}Kid$name"] = 0 }
            want["${p}LblUieFalse"] = 0
            want["${p}LpPlain"] = 1; want["${p}LpUieFalse"] = 0; want["${p}LpUieBound"] = 0
            want["${p}PanUieFalse"] = 0
            want["${p}BtnPlain"] = 1; want["${p}BtnUieFalse"] = 0
        }
        // as kjui emitted them before: the long press fired, the switch switched
        want["cgLpUieOld"] = 1
        want["update_swUieOld"] = 1
        want["update_swUieFalse"] = 0
        want["update_sw_dyn"] = 0
        want["update_sw_dyn_plain"] = 1
        for ((id, n) in want.toSortedMap()) println("GATE_PROBE_WANT $id calls=${calls(id)} want=$n")
        for (p in listOf("cg", "dyn")) println("GATE_PROBE_WANT ${p}PanPlain calls=${calls("${p}PanPlain")} want>0")
        for ((id, n) in want) assertEquals("$id calls", n, calls(id))
        for (p in listOf("cg", "dyn")) assertTrue("${p}PanPlain: the drag reaches", calls("${p}PanPlain") > 0)
        assertEquals("the child of a gated SafeAreaView", 0, calls("dynSafeKid"))
        for (p in listOf("cg", "dyn")) for (kind in listOf("Img", "Hold", "Drag")) {
            val c = { g: String -> calls("${p}$kind$g") }
            println("GATE_PROBE_EN $p$kind plain=${c("Plain")} false=${c("EnFalse")} bound=${c("EnBound")}")
        }
        for (p in listOf("cg", "dyn")) for (kind in listOf("Img", "Hold", "Drag")) {
            assertTrue("$p$kind: the ungated gesture reaches", calls("${p}${kind}Plain") > 0)
            assertEquals("$p$kind under enabled false", 0, calls("${p}${kind}EnFalse"))
            assertEquals("$p$kind under a bound enabled resolving false", 0, calls("${p}${kind}EnBound"))
        }
        for (p in listOf("cg", "dyn")) for (kind in listOf("Tf", "Tv")) {
            assertEquals("$p$kind: the ungated field takes focus", true, focused["$p${kind}Plain"])
            assertEquals("$p$kind: the gated field does not", false, focused["$p${kind}UieFalse"])
        }
        assertEquals("the tab view with no gate switches", true, tabSwitched["dynTabPlain"])
        assertEquals("the tab view under the gate does not", false, tabSwitched["dynTabUieFalse"])
    }
}
