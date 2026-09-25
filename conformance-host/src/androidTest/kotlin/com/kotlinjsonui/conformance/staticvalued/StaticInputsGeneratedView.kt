// Pasted from what `kjui build` (kjui_tools of jsonui-cli rel/v1.8.121 = 32785ce8) emits for
// the static inputs layout of StaticValuedControlsProbeTest — ticket
// static-valued-controls-do-not-change-on-a-users-tap. Changed from the output: the package
// (com.kotlinjsonui.probe.* → com.kotlinjsonui.conformance.staticvalued) and the unused
// `import com.kotlinjsonui.probe.R`; nothing else.
package com.kotlinjsonui.conformance.staticvalued

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotlinjsonui.components.CustomTextField
import com.kotlinjsonui.components.CustomTextFieldWithMargins
import com.kotlinjsonui.components.DateSelectBox
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.components.SelectBox
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.ScreenMarker
import com.kotlinjsonui.embed.DriveEmbedInitParams

@Composable
fun StaticInputsGeneratedView(
    data: StaticInputsData,
    viewModel: StaticInputsViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from static_inputs.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    Box(propagateMinConstraints = true) {
        // Requires KotlinJsonUI >= 2.13.0 (embed init-params)
        DriveEmbedInitParams(viewModel)
        // Check if Dynamic Mode is active
        if (DynamicModeManager.isActive()) {
            // Dynamic Mode - use SafeDynamicView for real-time updates
            SafeDynamicView(
                layoutName = "static_inputs",
                modifier = modifier,
                data = data.toMap(),
                fallback = {
                    // Show error or loading state when dynamic view is not available
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Dynamic view not available",
                            color = Color.Gray
                        )
                    }
                },
                onError = { error ->
                    // Log error or show error UI
                    android.util.Log.e("DynamicView", "Error loading static_inputs: \$error")
                },
                onLoading = {
                    // Show loading indicator
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            ) { jsonContent ->
                // Parse and render the dynamic JSON content
                // This will be handled by the DynamicView implementation
            }
        } else {
            // Static Mode - use generated code
            Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val textFieldState_tf = rememberTextFieldState(initialText = "t0")
            val focusRequester_tf = remember { FocusRequester() }
            val keyboardController_tf = LocalSoftwareKeyboardController.current
            LaunchedEffect(data.tfIsFocused) { if (data.tfIsFocused) { focusRequester_tf.requestFocus(); keyboardController_tf?.show() } }
            CustomTextField(
                state = textFieldState_tf,
                modifier = Modifier
                    .testTag("tf")
                    .semantics { testTagsAsResourceId = true }
                    .requiredHeight(40.dp)
                    .onFocusChanged { if (it.isFocused != data.tfIsFocused) viewModel.updateData(mapOf("tfIsFocused" to it.isFocused)) }
                    .focusRequester(focusRequester_tf),
                textStyle = TextStyle(color = Configuration.TextField.defaultTextColor, fontSize = Configuration.TextField.defaultFontSize.sp)
            )
            val textFieldState_tv = rememberTextFieldState(initialText = "v0")
            val focusRequester_tv = remember { FocusRequester() }
            val keyboardController_tv = LocalSoftwareKeyboardController.current
            LaunchedEffect(data.tvIsFocused) { if (data.tvIsFocused) { focusRequester_tv.requestFocus(); keyboardController_tv?.show() } }
            CustomTextField(
                state = textFieldState_tv,
                modifier = Modifier
                    .testTag("tv")
                    .semantics { testTagsAsResourceId = true }
                    .fillMaxWidth()
                    .height(60.dp)
                    .onFocusChanged { if (it.isFocused != data.tvIsFocused) viewModel.updateData(mapOf("tvIsFocused" to it.isFocused)) }
                    .focusRequester(focusRequester_tv),
                isOutlined = true,
                maxLines = Int.MAX_VALUE,
                singleLine = false
            )
            SelectBox(
                value = "pp",
                onValueChange = { },
                options = listOf("pp", "qq"),
                modifier = Modifier
                    .testTag("sbv")
                    .semantics { testTagsAsResourceId = true }
                    .requiredHeight(40.dp)
            )
            DateSelectBox(
                value = "2026-01-02",
                onValueChange = { },
                datePickerMode = "date",
                dateFormat = "yyyy-MM-dd",
                modifier = Modifier
                    .testTag("sbd")
                    .semantics { testTagsAsResourceId = true }
                    .fillMaxWidth()
                    .requiredHeight(40.dp)
            )
        }    }
        // Requires KotlinJsonUI >= 2.15.1 (screen marker)
        ScreenMarker("static_inputs")
    }
    // >>> GENERATED_CODE_END
}