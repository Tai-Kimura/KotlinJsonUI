package com.example.kotlinjsonui.sample.views.implemented_attributes_test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.ImplementedAttributesTestData
import com.example.kotlinjsonui.sample.viewmodels.ImplementedAttributesTestViewModel
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.ButtonDefaults
import com.kotlinjsonui.components.CustomTextField
import com.kotlinjsonui.components.CustomTextFieldWithMargins
import androidx.compose.ui.text.TextStyle
import androidx.compose.material3.TabRow
import androidx.compose.material3.Tab
import com.kotlinjsonui.components.SelectBox
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebChromeClient
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.foundation.layout.PaddingValues
import com.kotlinjsonui.components.Segment
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.wrapContentSize
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.components.SafeDynamicView
import androidx.compose.foundation.layout.Box
import com.kotlinjsonui.core.SafeDynamicView
import androidx.compose.ui.res.stringResource
import com.example.kotlinjsonui.sample.R
import androidx.compose.ui.res.colorResource

@Composable
fun ImplementedAttributesTestGeneratedView(
    data: ImplementedAttributesTestData,
    viewModel: ImplementedAttributesTestViewModel
) {
    // Generated Compose code from implemented_attributes_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "implemented_attributes_test",
            data = data.toMap(viewModel),
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
                android.util.Log.e("DynamicView", "Error loading implemented_attributes_test: \$error")
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
        Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(colorResource(R.color.white_17))
        ) {
            item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.implemented_attributes_test_implemented_attributes_test),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                Text(
                    text = stringResource(R.string.implemented_attributes_test_1_aligncenterverticalview_align),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                ConstraintLayout(
                    modifier = Modifier
                        .height(200.dp)
                        .background(colorResource(R.color.white_17))
                ) {
                    val target1 = createRef()
                    val view_1 = createRef()
                    val view_2 = createRef()

                    Text(
                        modifier = Modifier.constrainAs(target1) {
                            top.linkTo(parent.top, margin = 50.dp)
                            start.linkTo(parent.start, margin = 50.dp)
                        }
                            .background(colorResource(R.color.dark_red))
                            .padding(10.dp),
                        text = "Target View",
                        color = colorResource(R.color.white)
                    )
                    Text(
                        modifier = Modifier.constrainAs(view_1) {
                            top.linkTo(target1.top)
                            bottom.linkTo(target1.bottom)
                            end.linkTo(parent.end, margin = 20.dp)
                        }
                            .background(colorResource(R.color.dark_green_2))
                            .padding(10.dp),
                        text = "Centered V"
                    )
                    Text(
                        modifier = Modifier.constrainAs(view_2) {
                            start.linkTo(target1.start)
                            end.linkTo(target1.end)
                            bottom.linkTo(parent.bottom, margin = 20.dp)
                        }
                            .background(colorResource(R.color.dark_blue))
                            .padding(10.dp),
                        text = "Centered H",
                        color = colorResource(R.color.white)
                    )
                }
                Text(
                    text = stringResource(R.string.implemented_attributes_test_2_idealwidth_idealheight),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .padding(bottom = 10.dp)
                )
                Box(
                    modifier = Modifier
                        .background(colorResource(R.color.pale_red))
                        .padding(10.dp)
                ) {
                    Text(
                        text = stringResource(R.string.implemented_attributes_test_ideal_size_view_200x100),
                        modifier = Modifier.align(Alignment.TopStart)
                    )
                }
                Text(
                    text = stringResource(R.string.implemented_attributes_test_3_cliptobounds),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .padding(bottom = 10.dp)
                )
                Box(
                    modifier = Modifier
                        .width(150.dp)
                        .height(50.dp)
                        .background(colorResource(R.color.pale_green))
                ) {
                    Text(
                        text = stringResource(R.string.implemented_attributes_test_this_text_is_very_long_and_shou),
                        fontSize = 16.sp,
                        modifier = Modifier.align(Alignment.TopStart)
                    )
                }
                Text(
                    text = stringResource(R.string.implemented_attributes_test_4_direction_distribution),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .padding(bottom = 10.dp)
                )
                Row(
                    modifier = Modifier
                        .height(50.dp)
                        .background(colorResource(R.color.pale_blue_3)),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = "3",
                        color = colorResource(R.color.white),
                        modifier = Modifier.background(colorResource(R.color.light_blue)),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "2",
                        modifier = Modifier.background(colorResource(R.color.light_green)),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "1",
                        modifier = Modifier.background(colorResource(R.color.light_red_2)),
                        textAlign = TextAlign.Center
                    )
                }
                Text(
                    text = stringResource(R.string.implemented_attributes_test_5_edgeinset_label_padding),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .padding(bottom = 10.dp)
                )
                Text(
                    text = stringResource(R.string.implemented_attributes_test_text_with_edgeinset),
                    fontSize = 16.sp,
                    modifier = Modifier
                        .background(colorResource(R.color.white_18))
                        .padding(20.dp)
                )
                Text(
                    text = stringResource(R.string.implemented_attributes_test_6_button_state_attributes),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .padding(bottom = 10.dp)
                )
                Button(
                    onClick = { },
                    modifier = Modifier.padding(bottom = 10.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(10.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.pale_gray_4)
                                        )
                ) {
                    Text(
                        text = stringResource(R.string.implemented_attributes_test_tap_me_tapbackground),
                        color = colorResource(R.color.black),
                    )
                }
                Button(
                    onClick = { },
                    modifier = Modifier.padding(bottom = 10.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(10.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.dark_blue),
                                            // hilightColor: #ff0000 - Use InteractionSource for pressed state
                                        )
                ) {
                    Text(
                        text = stringResource(R.string.implemented_attributes_test_highlight_me),
                        color = colorResource(R.color.white),
                    )
                }
                Button(
                    onClick = { },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(10.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.dark_blue),
                                            disabledContainerColor = colorResource(R.color.white_17),
                                            disabledContentColor = colorResource(R.color.light_gray_8)
                                        ),
                    enabled = false
                ) {
                    Text(
                        text = stringResource(R.string.disabled_test_disabled_button),
                        color = colorResource(R.color.white),
                    )
                }
                Text(
                    text = stringResource(R.string.implemented_attributes_test_7_textfield_events),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .padding(bottom = 10.dp)
                )
                CustomTextFieldWithMargins(
                    value = "${data.textFieldValue}",
                    onValueChange = { newValue -> viewModel.updateData(mapOf("textFieldValue" to newValue)) },
                    boxModifier = Modifier
                        .padding(bottom = 10.dp),
                    placeholder = { Text(stringResource(R.string.implemented_attributes_test_type_something)) },
                    textStyle = TextStyle(fontSize = 16.sp, color = colorResource(R.color.black)),
                    onFocus = { viewModel.handleFocus() },
                    onBlur = { viewModel.handleBlur() },
                    onBeginEditing = { viewModel.handleBeginEditing() },
                    onEndEditing = { viewModel.handleEndEditing() }
                )
                Text(
                    text = stringResource(R.string.implemented_attributes_test_8_radio_custom_icons),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .padding(bottom = 10.dp)
                )
                Column(
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(bottom = 5.dp)
                    ) {
                        RadioButton(
                            selected = data.selectedRadiogroup == "radio1",
                            onClick = { viewModel.updateData(mapOf("selectedRadiogroup" to "radio1")) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Option 1", color = Color.Black)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(bottom = 5.dp)
                    ) {
                        RadioButton(
                            selected = data.selectedRadiogroup == "radio2",
                            onClick = { viewModel.updateData(mapOf("selectedRadiogroup" to "radio2")) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Option 2", color = Color.Black)
                    }
                }
                Text(
                    text = stringResource(R.string.implemented_attributes_test_9_segment_control),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .padding(bottom = 10.dp)
                )
                Segment(
                    selectedTabIndex = data.selectedSegment,
                    contentColor = colorResource(R.color.black),
                    selectedContentColor = colorResource(R.color.dark_blue),
                    modifier = Modifier.padding(bottom = 10.dp)
                ) {
                    Tab(
                        selected = (data.selectedSegment == 0),
                        onClick = {
                            viewModel.updateData(mapOf("selectedSegment" to 0))
                        },
                        text = {
                            Text(
                                "First",
                                color = if (data.selectedSegment == 0) colorResource(R.color.dark_blue) else colorResource(R.color.black)
                            )
                        }
                    )
                    Tab(
                        selected = (data.selectedSegment == 1),
                        onClick = {
                            viewModel.updateData(mapOf("selectedSegment" to 1))
                        },
                        text = {
                            Text(
                                "Second",
                                color = if (data.selectedSegment == 1) colorResource(R.color.dark_blue) else colorResource(R.color.black)
                            )
                        }
                    )
                    Tab(
                        selected = (data.selectedSegment == 2),
                        onClick = {
                            viewModel.updateData(mapOf("selectedSegment" to 2))
                        },
                        text = {
                            Text(
                                "Third",
                                color = if (data.selectedSegment == 2) colorResource(R.color.dark_blue) else colorResource(R.color.black)
                            )
                        }
                    )
                }
                Text(
                    text = stringResource(R.string.implemented_attributes_test_10_selectbox_attributes),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .padding(bottom = 10.dp)
                )
                SelectBox(
                    value = "",
                    onValueChange = { },
                    options = listOf("Option A", "Option B", "Option C"),
                    backgroundColor = colorResource(R.color.white),
                    textColor = colorResource(R.color.dark_blue),
                    cancelButtonBackgroundColor = colorResource(R.color.white_19),
                    cancelButtonTextColor = colorResource(R.color.dark_red),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(bottom = 10.dp)
                )
                Text(
                    text = stringResource(R.string.implemented_attributes_test_11_web_component),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .padding(bottom = 10.dp)
                )
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            settings.javaScriptEnabled = true
                            loadUrl("https://www.example.com")
                            webViewClient = WebViewClient()
                            webChromeClient = WebChromeClient()
                        }
                    },
                    update = { webView ->
                    },
                    modifier = Modifier
                        .height(200.dp)
                        .padding(bottom = 20.dp)
                )
            }
            }
        }
    }    }
    // >>> GENERATED_CODE_END
}