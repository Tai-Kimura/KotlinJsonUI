package com.example.kotlinjsonui.sample.views.alignment_test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.BiasAlignment
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
import com.example.kotlinjsonui.sample.data.AlignmentTestData
import com.example.kotlinjsonui.sample.viewmodels.AlignmentTestViewModel
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont

@Composable
fun AlignmentTestGeneratedView(
    data: AlignmentTestData,
    viewModel: AlignmentTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from alignment_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "alignment_test",
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
                android.util.Log.e("DynamicView", "Error loading alignment_test: \$error")
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
                .wrapContentHeight()
                .padding(20.dp)
        ) {
            Section0(data, viewModel)
            val resolved_text180 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = 24.sp,
                italic = false
            ))
            Text(
                text = "${data.title}",
                color = colorResource(R.color.black),
                fontFamily = resolved_text180.family,
                fontWeight = resolved_text180.weight,
                fontSize = resolved_text180.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text180.style ?: FontStyle.Normal,
                style = TextStyle(lineHeight = 31.2.sp),
                modifier = Modifier
                    .testTag("title_label")
                    .semantics { testTagsAsResourceId = true }
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 20.dp)
                    .wrapContentWidth()
                    .wrapContentHeight(),
                textAlign = TextAlign.Center
            )
            Section2(data, viewModel)
            Box(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(colorResource(R.color.pale_gray))
            ) {
                val resolved_text182 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.alignment_test_aligntop),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text182.family,
                    fontWeight = resolved_text182.weight,
                    fontSize = resolved_text182.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text182.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
                    modifier = Modifier
                        .align(BiasAlignment(-1f, -1f))
                        .background(colorResource(R.color.pale_pink))
                        .padding(8.dp)
                )
            }
            Box(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(colorResource(R.color.pale_gray_2))
            ) {
                val resolved_text183 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.alignment_test_alignbottom),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text183.family,
                    fontWeight = resolved_text183.weight,
                    fontSize = resolved_text183.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text183.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
                    modifier = Modifier
                        .align(BiasAlignment(-1f, 1f))
                        .background(colorResource(R.color.pale_yellow))
                        .padding(8.dp)
                )
            }
            Box(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(colorResource(R.color.pale_gray_3))
            ) {
                val resolved_text184 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.alignment_test_alignleft),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text184.family,
                    fontWeight = resolved_text184.weight,
                    fontSize = resolved_text184.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text184.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
                    modifier = Modifier
                        .align(BiasAlignment(-1f, -1f))
                        .background(colorResource(R.color.pale_cyan))
                        .padding(8.dp)
                )
            }
            Box(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(colorResource(R.color.light_gray))
            ) {
                val resolved_text185 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.alignment_test_alignright),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text185.family,
                    fontWeight = resolved_text185.weight,
                    fontSize = resolved_text185.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text185.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
                    modifier = Modifier
                        .align(BiasAlignment(1f, -1f))
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(colorResource(R.color.white_2))
                        .padding(8.dp)
                )
            }
            Box(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(colorResource(R.color.light_gray_2))
            ) {
                val resolved_text186 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.alignment_test_centerhorizontal),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text186.family,
                    fontWeight = resolved_text186.weight,
                    fontSize = resolved_text186.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text186.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
                    modifier = Modifier
                        .align(BiasAlignment(0f, -1f))
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(colorResource(R.color.white_3))
                        .padding(8.dp),
                    textAlign = TextAlign.Center
                )
            }
            Box(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(colorResource(R.color.light_gray_3))
            ) {
                val resolved_text187 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.alignment_test_centervertical),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text187.family,
                    fontWeight = resolved_text187.weight,
                    fontSize = resolved_text187.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text187.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
                    modifier = Modifier
                        .align(BiasAlignment(-1f, 0f))
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(colorResource(R.color.white_4))
                        .padding(8.dp)
                )
            }
            Box(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(colorResource(R.color.light_gray_4))
            ) {
                val resolved_text188 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = 14.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.alignment_test_centerinparent),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text188.family,
                    fontWeight = resolved_text188.weight,
                    fontSize = resolved_text188.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text188.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 18.2.sp),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .background(colorResource(R.color.pale_red))
                        .padding(8.dp)
                )
            }
            Section10(data, viewModel)
            Row(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(colorResource(R.color.light_gray_5))
            ) {
                val resolved_text190 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = null,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.alignment_test_top),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text190.family,
                    fontWeight = resolved_text190.weight,
                    fontSize = resolved_text190.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text190.style ?: FontStyle.Normal,
                    modifier = Modifier
                        .align(Alignment.Top)
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(colorResource(R.color.white_5))
                        .padding(8.dp)
                )
                val resolved_text191 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = null,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.alignment_test_default),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text191.family,
                    fontWeight = resolved_text191.weight,
                    fontSize = resolved_text191.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text191.style ?: FontStyle.Normal,
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(colorResource(R.color.pale_gray))
                        .padding(8.dp)
                )
                val resolved_text192 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = null,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.alignment_test_bottom),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text192.family,
                    fontWeight = resolved_text192.weight,
                    fontSize = resolved_text192.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text192.style ?: FontStyle.Normal,
                    modifier = Modifier
                        .align(Alignment.Bottom)
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(colorResource(R.color.white_7))
                        .padding(8.dp)
                )
                val resolved_text193 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = null,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.alignment_combo_test_center),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text193.family,
                    fontWeight = resolved_text193.weight,
                    fontSize = resolved_text193.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text193.style ?: FontStyle.Normal,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(colorResource(R.color.white_6))
                        .padding(8.dp)
                )
            }
            Section12(data, viewModel)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(colorResource(R.color.light_gray_6))
            ) {
                val resolved_text195 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = null,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.alignment_test_alignleft),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text195.family,
                    fontWeight = resolved_text195.weight,
                    fontSize = resolved_text195.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text195.style ?: FontStyle.Normal,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(colorResource(R.color.white_5))
                        .padding(8.dp)
                )
                val resolved_text196 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = null,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.alignment_test_default),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text196.family,
                    fontWeight = resolved_text196.weight,
                    fontSize = resolved_text196.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text196.style ?: FontStyle.Normal,
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(colorResource(R.color.pale_gray))
                        .padding(8.dp)
                )
                val resolved_text197 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = null,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.alignment_test_alignright),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text197.family,
                    fontWeight = resolved_text197.weight,
                    fontSize = resolved_text197.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text197.style ?: FontStyle.Normal,
                    modifier = Modifier
                        .align(Alignment.End)
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(colorResource(R.color.white_7))
                        .padding(8.dp)
                )
                val resolved_text198 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = null,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.alignment_test_centerhorizontal),
                    color = colorResource(R.color.black),
                    fontFamily = resolved_text198.family,
                    fontWeight = resolved_text198.weight,
                    fontSize = resolved_text198.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text198.style ?: FontStyle.Normal,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(colorResource(R.color.white_6))
                        .padding(8.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
        }
    }    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section0(
    data: AlignmentTestData,
    viewModel: AlignmentTestViewModel
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
        val resolved_button15 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = FontWeight.Medium,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = "${data.dynamicModeStatus}",
            fontFamily = resolved_button15.family,
            fontWeight = resolved_button15.weight,
            fontSize = resolved_button15.size ?: TextUnit.Unspecified,
            fontStyle = resolved_button15.style ?: FontStyle.Normal,
        )
    }
}

@Composable
private fun Section2(
    data: AlignmentTestData,
    viewModel: AlignmentTestViewModel
) {
    val resolved_text181 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.alignment_test_parent_alignment_single_propert),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text181.family,
        fontWeight = resolved_text181.weight,
        fontSize = resolved_text181.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text181.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier
            .padding(bottom = 10.dp)
            .wrapContentWidth()
            .wrapContentHeight()
    )
}

@Composable
private fun Section10(
    data: AlignmentTestData,
    viewModel: AlignmentTestViewModel
) {
    val resolved_text189 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.alignment_test_hstack_alignment_tests),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text189.family,
        fontWeight = resolved_text189.weight,
        fontSize = resolved_text189.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text189.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier
            .padding(top = 20.dp)
            .padding(bottom = 10.dp)
    )
}

@Composable
private fun Section12(
    data: AlignmentTestData,
    viewModel: AlignmentTestViewModel
) {
    val resolved_text194 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.alignment_test_vstack_alignment_tests),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text194.family,
        fontWeight = resolved_text194.weight,
        fontSize = resolved_text194.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text194.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier
            .padding(top = 20.dp)
            .padding(bottom = 10.dp)
    )
}
// >>> RESPONSIVE_HELPERS_END