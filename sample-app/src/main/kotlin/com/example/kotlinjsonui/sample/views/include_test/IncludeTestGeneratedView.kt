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

@Composable
fun IncludeTestGeneratedView(
    data: IncludeTestData,
    viewModel: IncludeTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from include_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
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
            .height(44.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(android.graphics.Color.parseColor("#5856D6")),
                            contentColor = colorResource(R.color.white)
                        )
    ) {
        val resolved_button18 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = FontWeight.Medium,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = "${data.dynamicModeStatus}",
            fontFamily = resolved_button18.family,
            fontWeight = resolved_button18.weight,
            fontSize = resolved_button18.size ?: TextUnit.Unspecified,
            fontStyle = resolved_button18.style ?: FontStyle.Normal,
        )
    }
}

@Composable
private fun Section1_0(
    data: IncludeTestData,
    viewModel: IncludeTestViewModel
) {
    val resolved_text226 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 24.sp,
        italic = false
    ))
    Text(
        text = "${data.title}",
        color = colorResource(R.color.black),
        fontFamily = resolved_text226.family,
        fontWeight = resolved_text226.weight,
        fontSize = resolved_text226.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text226.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 31.2.sp),
        modifier = Modifier
    )
}

@Composable
private fun Section1_1_0(
    data: IncludeTestData,
    viewModel: IncludeTestViewModel
) {
    val resolved_text227 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.include_test_control_panel),
        color = colorResource(R.color.medium_blue_4),
        fontFamily = resolved_text227.family,
        fontWeight = resolved_text227.weight,
        fontSize = resolved_text227.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text227.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
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
                                            contentColor = colorResource(R.color.white)
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
                                            contentColor = colorResource(R.color.white)
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
                                            contentColor = colorResource(R.color.white)
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
                                            contentColor = colorResource(R.color.white)
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
                                            contentColor = colorResource(R.color.white)
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
        val resolved_text228 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = FontWeight.SemiBold,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.include_test_current_values),
            color = colorResource(R.color.dark_gray),
            fontFamily = resolved_text228.family,
            fontWeight = resolved_text228.weight,
            fontSize = resolved_text228.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text228.style ?: FontStyle.Normal,
            style = TextStyle(lineHeight = 18.2.sp),
            modifier = Modifier
        )
        val resolved_text229 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = "${data.mainCount}",
            color = colorResource(R.color.medium_gray_4),
            fontFamily = resolved_text229.family,
            fontWeight = resolved_text229.weight,
            fontSize = resolved_text229.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text229.style ?: FontStyle.Normal,
            style = TextStyle(lineHeight = 18.2.sp),
            modifier = Modifier
        )
        val resolved_text230 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = "${data.userName}",
            color = colorResource(R.color.medium_gray_4),
            fontFamily = resolved_text230.family,
            fontWeight = resolved_text230.weight,
            fontSize = resolved_text230.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text230.style ?: FontStyle.Normal,
            style = TextStyle(lineHeight = 18.2.sp),
            modifier = Modifier
        )
        val resolved_text231 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = "${data.mainStatus}",
            color = colorResource(R.color.medium_gray_4),
            fontFamily = resolved_text231.family,
            fontWeight = resolved_text231.weight,
            fontSize = resolved_text231.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text231.style ?: FontStyle.Normal,
            style = TextStyle(lineHeight = 18.2.sp),
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
        val resolved_text232 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 16.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.include_test_1_basic_include_with_static_dat),
            color = colorResource(R.color.medium_gray_4),
            fontFamily = resolved_text232.family,
            fontWeight = resolved_text232.weight,
            fontSize = resolved_text232.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text232.style ?: FontStyle.Normal,
            style = TextStyle(lineHeight = 20.8.sp),
            modifier = Modifier
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(colorResource(R.color.white))
        ) {
            val resolved_text233 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 24.sp,
                italic = false
            ))
            Text(
                text = "${data.title}",
                color = colorResource(R.color.black),
                fontFamily = resolved_text233.family,
                fontWeight = resolved_text233.weight,
                fontSize = resolved_text233.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text233.style ?: FontStyle.Normal,
                style = TextStyle(lineHeight = 31.2.sp),
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
        val resolved_text234 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 16.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.include_test_2_include_with_data_static_valu),
            color = colorResource(R.color.medium_gray_4),
            fontFamily = resolved_text234.family,
            fontWeight = resolved_text234.weight,
            fontSize = resolved_text234.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text234.style ?: FontStyle.Normal,
            style = TextStyle(lineHeight = 20.8.sp),
            modifier = Modifier
        )
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(colorResource(R.color.white_21))
                .padding(15.dp)
        ) {
            val resolved_text235 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 18.sp,
                italic = false
            ))
            Text(
                text = stringResource(R.string.included2_included_view_2),
                color = colorResource(R.color.medium_red_6),
                fontFamily = resolved_text235.family,
                fontWeight = resolved_text235.weight,
                fontSize = resolved_text235.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text235.style ?: FontStyle.Normal,
                style = TextStyle(lineHeight = 23.4.sp),
                modifier = Modifier
            )
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .background(colorResource(R.color.white))
                    .padding(10.dp)
            ) {
                val resolved_text236 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = "${data.viewTitle}",
                    color = colorResource(R.color.dark_gray),
                    fontFamily = resolved_text236.family,
                    fontWeight = resolved_text236.weight,
                    fontSize = resolved_text236.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text236.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
                    modifier = Modifier
                )
                val resolved_text237 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = "${data.viewStatus}",
                    color = colorResource(R.color.dark_gray),
                    fontFamily = resolved_text237.family,
                    fontWeight = resolved_text237.weight,
                    fontSize = resolved_text237.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text237.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
                    modifier = Modifier
                )
                val resolved_text238 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = "${data.viewCount}",
                    color = colorResource(R.color.dark_gray),
                    fontFamily = resolved_text238.family,
                    fontWeight = resolved_text238.weight,
                    fontSize = resolved_text238.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text238.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
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
        val resolved_text239 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 16.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.include_test_3_include_with_data_using_refer),
            color = colorResource(R.color.medium_gray_4),
            fontFamily = resolved_text239.family,
            fontWeight = resolved_text239.weight,
            fontSize = resolved_text239.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text239.style ?: FontStyle.Normal,
            style = TextStyle(lineHeight = 20.8.sp),
            modifier = Modifier
        )
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(colorResource(R.color.white_21))
                .padding(15.dp)
        ) {
            val resolved_text240 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 18.sp,
                italic = false
            ))
            Text(
                text = stringResource(R.string.included2_included_view_2),
                color = colorResource(R.color.medium_red_6),
                fontFamily = resolved_text240.family,
                fontWeight = resolved_text240.weight,
                fontSize = resolved_text240.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text240.style ?: FontStyle.Normal,
                style = TextStyle(lineHeight = 23.4.sp),
                modifier = Modifier
            )
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .background(colorResource(R.color.white))
                    .padding(10.dp)
            ) {
                val resolved_text241 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = "${data.viewTitle}",
                    color = colorResource(R.color.dark_gray),
                    fontFamily = resolved_text241.family,
                    fontWeight = resolved_text241.weight,
                    fontSize = resolved_text241.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text241.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
                    modifier = Modifier
                )
                val resolved_text242 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = "${data.viewStatus}",
                    color = colorResource(R.color.dark_gray),
                    fontFamily = resolved_text242.family,
                    fontWeight = resolved_text242.weight,
                    fontSize = resolved_text242.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text242.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
                    modifier = Modifier
                )
                val resolved_text243 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = "${data.viewCount}",
                    color = colorResource(R.color.dark_gray),
                    fontFamily = resolved_text243.family,
                    fontWeight = resolved_text243.weight,
                    fontSize = resolved_text243.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text243.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
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
        val resolved_text244 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 16.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.include_test_4_include_with_shareddata_and_d),
            color = colorResource(R.color.medium_gray_4),
            fontFamily = resolved_text244.family,
            fontWeight = resolved_text244.weight,
            fontSize = resolved_text244.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text244.style ?: FontStyle.Normal,
            style = TextStyle(lineHeight = 20.8.sp),
            modifier = Modifier
        )
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(colorResource(R.color.white_21))
                .padding(15.dp)
        ) {
            val resolved_text245 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 18.sp,
                italic = false
            ))
            Text(
                text = stringResource(R.string.included2_included_view_2),
                color = colorResource(R.color.medium_red_6),
                fontFamily = resolved_text245.family,
                fontWeight = resolved_text245.weight,
                fontSize = resolved_text245.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text245.style ?: FontStyle.Normal,
                style = TextStyle(lineHeight = 23.4.sp),
                modifier = Modifier
            )
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .background(colorResource(R.color.white))
                    .padding(10.dp)
            ) {
                val resolved_text246 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = "${data.viewTitle}",
                    color = colorResource(R.color.dark_gray),
                    fontFamily = resolved_text246.family,
                    fontWeight = resolved_text246.weight,
                    fontSize = resolved_text246.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text246.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
                    modifier = Modifier
                )
                val resolved_text247 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = "${data.viewStatus}",
                    color = colorResource(R.color.dark_gray),
                    fontFamily = resolved_text247.family,
                    fontWeight = resolved_text247.weight,
                    fontSize = resolved_text247.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text247.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
                    modifier = Modifier
                )
                val resolved_text248 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = "${data.viewCount}",
                    color = colorResource(R.color.dark_gray),
                    fontFamily = resolved_text248.family,
                    fontWeight = resolved_text248.weight,
                    fontSize = resolved_text248.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text248.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
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
        val resolved_text249 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 16.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.include_test_5_another_included1_with_refere),
            color = colorResource(R.color.medium_gray_4),
            fontFamily = resolved_text249.family,
            fontWeight = resolved_text249.weight,
            fontSize = resolved_text249.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text249.style ?: FontStyle.Normal,
            style = TextStyle(lineHeight = 20.8.sp),
            modifier = Modifier
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(colorResource(R.color.white))
        ) {
            val resolved_text250 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 24.sp,
                italic = false
            ))
            Text(
                text = "${data.title}",
                color = colorResource(R.color.black),
                fontFamily = resolved_text250.family,
                fontWeight = resolved_text250.weight,
                fontSize = resolved_text250.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text250.style ?: FontStyle.Normal,
                style = TextStyle(lineHeight = 31.2.sp),
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