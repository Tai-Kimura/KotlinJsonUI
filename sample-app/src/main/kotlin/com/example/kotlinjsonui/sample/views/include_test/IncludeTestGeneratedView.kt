package com.example.kotlinjsonui.sample.views.include_test

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.R
import com.example.kotlinjsonui.sample.data.IncludeTestData
import com.example.kotlinjsonui.sample.viewmodels.IncludeTestViewModel
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont
import com.kotlinjsonui.core.ScreenMarker
import com.kotlinjsonui.embed.DriveEmbedInitParams

@Composable
fun IncludeTestGeneratedView(
    data: IncludeTestData,
    viewModel: IncludeTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from include_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    Box(propagateMinConstraints = true) {
        // Requires KotlinJsonUI >= 2.13.0 (embed init-params)
        DriveEmbedInitParams(viewModel)
        // Check if Dynamic Mode is active
        if (DynamicModeManager.isActive()) {
            // Dynamic Mode - use SafeDynamicView for real-time updates
            SafeDynamicView(
                layoutName = "include_test",
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
                    android.util.Log.e("DynamicView", "Error loading include_test: \$error")
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
            LazyColumn(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(colorResource(R.color.white))
                .imePadding()
        ) {
            item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Section0(data, viewModel)
                Section1(data, viewModel)
            }
            }
        }    }
        // Requires KotlinJsonUI >= 2.15.1 (screen marker)
        ScreenMarker("include_test")
    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section0(
    data: IncludeTestData,
    viewModel: IncludeTestViewModel
) {
    Button(
        onClick = { data.toggleDynamicMode?.invoke() },
        modifier = Modifier
            .wrapContentWidth()
            .requiredHeight(44.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(android.graphics.Color.parseColor("#5856D6")),
                            disabledContainerColor = Color(android.graphics.Color.parseColor("#5856D6")).copy(alpha = 0.5f),
                            contentColor = colorResource(R.color.white),
                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                        )
    ) {
        val resolved_button1 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = FontWeight.Medium,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = "${data.dynamicModeStatus}",
            fontFamily = resolved_button1.family,
            fontWeight = resolved_button1.weight,
            fontSize = resolved_button1.size ?: TextUnit.Unspecified,
            fontStyle = resolved_button1.style ?: FontStyle.Normal,
        )
    }
}

@Composable
private fun Section1_0(
    data: IncludeTestData,
    viewModel: IncludeTestViewModel
) {
    val resolved_text1 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 24.sp,
        italic = false
    ))
    Text(
        text = "${data.title}",
        color = colorResource(R.color.black),
        fontFamily = resolved_text1.family,
        fontWeight = resolved_text1.weight,
        fontSize = resolved_text1.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text1.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 31.2.sp),
        modifier = Modifier
    )
}

@Composable
private fun Section1_1_0(
    data: IncludeTestData,
    viewModel: IncludeTestViewModel
) {
    val resolved_text2 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.include_test_control_panel),
        color = colorResource(R.color.medium_blue_4),
        fontFamily = resolved_text2.family,
        fontWeight = resolved_text2.weight,
        fontSize = resolved_text2.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text2.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
        modifier = Modifier
    )
}

@Composable
private fun Section1_1_1(
    data: IncludeTestData,
    viewModel: IncludeTestViewModel
) {
    Row(
    ) {
        Button(
            onClick = { data.incrementCount?.invoke() },
            shape = RoundedCornerShape(5.dp),
            contentPadding = PaddingValues(10.dp),
            colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.medium_green_2),
                                            disabledContainerColor = colorResource(R.color.medium_green_2).copy(alpha = 0.5f),
                                            contentColor = colorResource(R.color.white),
                                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                                        )
        ) {
            Text("Count +")
        }
        Button(
            onClick = { data.decrementCount?.invoke() },
            shape = RoundedCornerShape(5.dp),
            contentPadding = PaddingValues(10.dp),
            colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.medium_red_5),
                                            disabledContainerColor = colorResource(R.color.medium_red_5).copy(alpha = 0.5f),
                                            contentColor = colorResource(R.color.white),
                                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                                        )
        ) {
            Text(stringResource(R.string.include_test_count))
        }
        Button(
            onClick = { data.resetCount?.invoke() },
            shape = RoundedCornerShape(5.dp),
            contentPadding = PaddingValues(10.dp),
            colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.medium_blue_2),
                                            disabledContainerColor = colorResource(R.color.medium_blue_2).copy(alpha = 0.5f),
                                            contentColor = colorResource(R.color.white),
                                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                                        )
        ) {
            Text(stringResource(R.string.include_test_reset))
        }
    }
}

@Composable
private fun Section1_1_2(
    data: IncludeTestData,
    viewModel: IncludeTestViewModel
) {
    Row(
    ) {
        Button(
            onClick = { data.changeUserName?.invoke() },
            shape = RoundedCornerShape(5.dp),
            contentPadding = PaddingValues(10.dp),
            colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.medium_purple),
                                            disabledContainerColor = colorResource(R.color.medium_purple).copy(alpha = 0.5f),
                                            contentColor = colorResource(R.color.white),
                                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                                        )
        ) {
            Text(stringResource(R.string.include_test_change_name))
        }
        Button(
            onClick = { data.toggleStatus?.invoke() },
            shape = RoundedCornerShape(5.dp),
            contentPadding = PaddingValues(10.dp),
            colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(R.color.medium_cyan),
                                            disabledContainerColor = colorResource(R.color.medium_cyan).copy(alpha = 0.5f),
                                            contentColor = colorResource(R.color.white),
                                            disabledContentColor = colorResource(R.color.white).copy(alpha = 0.5f)
                                        )
        ) {
            Text(stringResource(R.string.include_test_toggle_status))
        }
    }
}

@Composable
private fun Section1_1_3(
    data: IncludeTestData,
    viewModel: IncludeTestViewModel
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(5.dp))
            .background(colorResource(R.color.white))
            .padding(10.dp)
    ) {
        val resolved_text3 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = FontWeight.SemiBold,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.include_test_current_values),
            color = colorResource(R.color.dark_gray),
            fontFamily = resolved_text3.family,
            fontWeight = resolved_text3.weight,
            fontSize = resolved_text3.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text3.style ?: FontStyle.Normal,
            style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
            modifier = Modifier
        )
        val resolved_text4 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = "${data.mainCount}",
            color = colorResource(R.color.medium_gray_4),
            fontFamily = resolved_text4.family,
            fontWeight = resolved_text4.weight,
            fontSize = resolved_text4.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text4.style ?: FontStyle.Normal,
            style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
            modifier = Modifier
        )
        val resolved_text5 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = "${data.userName}",
            color = colorResource(R.color.medium_gray_4),
            fontFamily = resolved_text5.family,
            fontWeight = resolved_text5.weight,
            fontSize = resolved_text5.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text5.style ?: FontStyle.Normal,
            style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
            modifier = Modifier
        )
        val resolved_text6 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = "${data.mainStatus}",
            color = colorResource(R.color.medium_gray_4),
            fontFamily = resolved_text6.family,
            fontWeight = resolved_text6.weight,
            fontSize = resolved_text6.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text6.style ?: FontStyle.Normal,
            style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
            modifier = Modifier
        )
    }
}

@Composable
private fun Section1_1(
    data: IncludeTestData,
    viewModel: IncludeTestViewModel
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(colorResource(R.color.white_20))
            .padding(15.dp)
    ) {
        Section1_1_0(data, viewModel)
        Section1_1_1(data, viewModel)
        Section1_1_2(data, viewModel)
        Section1_1_3(data, viewModel)
    }
}

@Composable
private fun Section1_2(
    data: IncludeTestData,
    viewModel: IncludeTestViewModel
) {
    Column(
        modifier = Modifier.width(IntrinsicSize.Min)
    ) {
        val resolved_text7 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 16.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.include_test_1_basic_include_with_static_dat),
            color = colorResource(R.color.medium_gray_4),
            fontFamily = resolved_text7.family,
            fontWeight = resolved_text7.weight,
            fontSize = resolved_text7.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text7.style ?: FontStyle.Normal,
            style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
            modifier = Modifier
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(colorResource(R.color.white))
        ) {
            val resolved_text8 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 24.sp,
                italic = false
            ))
            Text(
                text = "${data.title}",
                color = colorResource(R.color.black),
                fontFamily = resolved_text8.family,
                fontWeight = resolved_text8.weight,
                fontSize = resolved_text8.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text8.style ?: FontStyle.Normal,
                style = LocalTextStyle.current.copy(lineHeight = 31.2.sp),
                modifier = Modifier
                    .testTag("title_label")
                    .semantics { testTagsAsResourceId = true }
                    .padding(top = 20.dp)
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
        }
    }
}

@Composable
private fun Section1_3(
    data: IncludeTestData,
    viewModel: IncludeTestViewModel
) {
    Column(
    ) {
        val resolved_text9 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 16.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.include_test_2_include_with_data_static_valu),
            color = colorResource(R.color.medium_gray_4),
            fontFamily = resolved_text9.family,
            fontWeight = resolved_text9.weight,
            fontSize = resolved_text9.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text9.style ?: FontStyle.Normal,
            style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
            modifier = Modifier
        )
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(colorResource(R.color.white_21))
                .padding(15.dp)
        ) {
            val resolved_text10 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 18.sp,
                italic = false
            ))
            Text(
                text = stringResource(R.string.included2_included_view_2),
                color = colorResource(R.color.medium_red_6),
                fontFamily = resolved_text10.family,
                fontWeight = resolved_text10.weight,
                fontSize = resolved_text10.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text10.style ?: FontStyle.Normal,
                style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
                modifier = Modifier
            )
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .background(colorResource(R.color.white))
                    .padding(10.dp)
            ) {
                val resolved_text11 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = "${data.viewTitle}",
                    color = colorResource(R.color.dark_gray),
                    fontFamily = resolved_text11.family,
                    fontWeight = resolved_text11.weight,
                    fontSize = resolved_text11.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text11.style ?: FontStyle.Normal,
                    style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
                    modifier = Modifier
                )
                val resolved_text12 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = "${data.viewStatus}",
                    color = colorResource(R.color.dark_gray),
                    fontFamily = resolved_text12.family,
                    fontWeight = resolved_text12.weight,
                    fontSize = resolved_text12.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text12.style ?: FontStyle.Normal,
                    style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
                    modifier = Modifier
                )
                val resolved_text13 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = "${data.viewCount}",
                    color = colorResource(R.color.dark_gray),
                    fontFamily = resolved_text13.family,
                    fontWeight = resolved_text13.weight,
                    fontSize = resolved_text13.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text13.style ?: FontStyle.Normal,
                    style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
private fun Section1_4(
    data: IncludeTestData,
    viewModel: IncludeTestViewModel
) {
    Column(
    ) {
        val resolved_text14 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 16.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.include_test_3_include_with_data_using_refer),
            color = colorResource(R.color.medium_gray_4),
            fontFamily = resolved_text14.family,
            fontWeight = resolved_text14.weight,
            fontSize = resolved_text14.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text14.style ?: FontStyle.Normal,
            style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
            modifier = Modifier
        )
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(colorResource(R.color.white_21))
                .padding(15.dp)
        ) {
            val resolved_text15 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 18.sp,
                italic = false
            ))
            Text(
                text = stringResource(R.string.included2_included_view_2),
                color = colorResource(R.color.medium_red_6),
                fontFamily = resolved_text15.family,
                fontWeight = resolved_text15.weight,
                fontSize = resolved_text15.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text15.style ?: FontStyle.Normal,
                style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
                modifier = Modifier
            )
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .background(colorResource(R.color.white))
                    .padding(10.dp)
            ) {
                val resolved_text16 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = "${data.viewTitle}",
                    color = colorResource(R.color.dark_gray),
                    fontFamily = resolved_text16.family,
                    fontWeight = resolved_text16.weight,
                    fontSize = resolved_text16.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text16.style ?: FontStyle.Normal,
                    style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
                    modifier = Modifier
                )
                val resolved_text17 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = "${data.viewStatus}",
                    color = colorResource(R.color.dark_gray),
                    fontFamily = resolved_text17.family,
                    fontWeight = resolved_text17.weight,
                    fontSize = resolved_text17.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text17.style ?: FontStyle.Normal,
                    style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
                    modifier = Modifier
                )
                val resolved_text18 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = "${data.viewCount}",
                    color = colorResource(R.color.dark_gray),
                    fontFamily = resolved_text18.family,
                    fontWeight = resolved_text18.weight,
                    fontSize = resolved_text18.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text18.style ?: FontStyle.Normal,
                    style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
private fun Section1_5(
    data: IncludeTestData,
    viewModel: IncludeTestViewModel
) {
    Column(
    ) {
        val resolved_text19 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 16.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.include_test_4_include_with_shareddata_and_d),
            color = colorResource(R.color.medium_gray_4),
            fontFamily = resolved_text19.family,
            fontWeight = resolved_text19.weight,
            fontSize = resolved_text19.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text19.style ?: FontStyle.Normal,
            style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
            modifier = Modifier
        )
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(colorResource(R.color.white_21))
                .padding(15.dp)
        ) {
            val resolved_text20 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 18.sp,
                italic = false
            ))
            Text(
                text = stringResource(R.string.included2_included_view_2),
                color = colorResource(R.color.medium_red_6),
                fontFamily = resolved_text20.family,
                fontWeight = resolved_text20.weight,
                fontSize = resolved_text20.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text20.style ?: FontStyle.Normal,
                style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
                modifier = Modifier
            )
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .background(colorResource(R.color.white))
                    .padding(10.dp)
            ) {
                val resolved_text21 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = "${data.viewTitle}",
                    color = colorResource(R.color.dark_gray),
                    fontFamily = resolved_text21.family,
                    fontWeight = resolved_text21.weight,
                    fontSize = resolved_text21.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text21.style ?: FontStyle.Normal,
                    style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
                    modifier = Modifier
                )
                val resolved_text22 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = "${data.viewStatus}",
                    color = colorResource(R.color.dark_gray),
                    fontFamily = resolved_text22.family,
                    fontWeight = resolved_text22.weight,
                    fontSize = resolved_text22.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text22.style ?: FontStyle.Normal,
                    style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
                    modifier = Modifier
                )
                val resolved_text23 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = "${data.viewCount}",
                    color = colorResource(R.color.dark_gray),
                    fontFamily = resolved_text23.family,
                    fontWeight = resolved_text23.weight,
                    fontSize = resolved_text23.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text23.style ?: FontStyle.Normal,
                    style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
private fun Section1_6(
    data: IncludeTestData,
    viewModel: IncludeTestViewModel
) {
    Column(
        modifier = Modifier.width(IntrinsicSize.Min)
    ) {
        val resolved_text24 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 16.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.include_test_5_another_included1_with_refere),
            color = colorResource(R.color.medium_gray_4),
            fontFamily = resolved_text24.family,
            fontWeight = resolved_text24.weight,
            fontSize = resolved_text24.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text24.style ?: FontStyle.Normal,
            style = LocalTextStyle.current.copy(lineHeight = 20.8.sp),
            modifier = Modifier
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(colorResource(R.color.white))
        ) {
            val resolved_text25 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 24.sp,
                italic = false
            ))
            Text(
                text = "${data.title}",
                color = colorResource(R.color.black),
                fontFamily = resolved_text25.family,
                fontWeight = resolved_text25.weight,
                fontSize = resolved_text25.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text25.style ?: FontStyle.Normal,
                style = LocalTextStyle.current.copy(lineHeight = 31.2.sp),
                modifier = Modifier
                    .testTag("title_label")
                    .semantics { testTagsAsResourceId = true }
                    .padding(top = 20.dp)
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
        }
    }
}

@Composable
private fun Section1(
    data: IncludeTestData,
    viewModel: IncludeTestViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Section1_0(data, viewModel)
        Section1_1(data, viewModel)
        Section1_2(data, viewModel)
        Section1_3(data, viewModel)
        Section1_4(data, viewModel)
        Section1_5(data, viewModel)
        Section1_6(data, viewModel)
    }
}
// >>> RESPONSIVE_HELPERS_END