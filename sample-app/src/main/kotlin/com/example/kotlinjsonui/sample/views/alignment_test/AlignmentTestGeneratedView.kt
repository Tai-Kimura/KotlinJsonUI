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
import androidx.compose.material3.LocalTextStyle
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
import com.kotlinjsonui.core.ScreenMarker
import com.kotlinjsonui.embed.DriveEmbedInitParams

@Composable
fun AlignmentTestGeneratedView(
    data: AlignmentTestData,
    viewModel: AlignmentTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from alignment_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    Box(propagateMinConstraints = true) {
        // Requires KotlinJsonUI >= 2.13.0 (embed init-params)
        DriveEmbedInitParams(viewModel)
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
        Section28(data, viewModel, modifier)    }
        // Requires KotlinJsonUI >= 2.15.1 (screen marker)
        ScreenMarker("alignment_test")
    }
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
private fun Section2(
    data: AlignmentTestData,
    viewModel: AlignmentTestViewModel
) {
    val resolved_text2 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.alignment_test_parent_alignment_single_propert),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text2.family,
        fontWeight = resolved_text2.weight,
        fontSize = resolved_text2.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text2.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
        modifier = Modifier
            .padding(bottom = 10.dp)
            .wrapContentWidth()
            .wrapContentHeight()
    )
}

@Composable
private fun Section3(
    data: AlignmentTestData,
    viewModel: AlignmentTestViewModel
) {
    Box(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth()
            .requiredHeight(120.dp)
            .background(colorResource(R.color.pale_gray))
    ) {
        val resolved_text3 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.alignment_test_aligntop),
            color = colorResource(R.color.black),
            fontFamily = resolved_text3.family,
            fontWeight = resolved_text3.weight,
            fontSize = resolved_text3.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text3.style ?: FontStyle.Normal,
            style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
            modifier = Modifier
                .align(BiasAlignment(-1f, -1f))
                .background(colorResource(R.color.pale_pink))
                .padding(8.dp)
        )
    }
}

@Composable
private fun Section4(
    data: AlignmentTestData,
    viewModel: AlignmentTestViewModel
) {
    Box(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth()
            .requiredHeight(120.dp)
            .background(colorResource(R.color.pale_gray_2))
    ) {
        val resolved_text4 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.alignment_test_alignbottom),
            color = colorResource(R.color.black),
            fontFamily = resolved_text4.family,
            fontWeight = resolved_text4.weight,
            fontSize = resolved_text4.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text4.style ?: FontStyle.Normal,
            style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
            modifier = Modifier
                .align(BiasAlignment(-1f, 1f))
                .background(colorResource(R.color.pale_yellow))
                .padding(8.dp)
        )
    }
}

@Composable
private fun Section5(
    data: AlignmentTestData,
    viewModel: AlignmentTestViewModel
) {
    Box(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth()
            .requiredHeight(120.dp)
            .background(colorResource(R.color.pale_gray_3))
    ) {
        val resolved_text5 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.alignment_test_alignleft),
            color = colorResource(R.color.black),
            fontFamily = resolved_text5.family,
            fontWeight = resolved_text5.weight,
            fontSize = resolved_text5.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text5.style ?: FontStyle.Normal,
            style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
            modifier = Modifier
                .align(BiasAlignment(-1f, -1f))
                .background(colorResource(R.color.pale_cyan))
                .padding(8.dp)
        )
    }
}

@Composable
private fun Section6(
    data: AlignmentTestData,
    viewModel: AlignmentTestViewModel
) {
    Box(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth()
            .requiredHeight(120.dp)
            .background(colorResource(R.color.light_gray))
    ) {
        val resolved_text6 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.alignment_test_alignright),
            color = colorResource(R.color.black),
            fontFamily = resolved_text6.family,
            fontWeight = resolved_text6.weight,
            fontSize = resolved_text6.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text6.style ?: FontStyle.Normal,
            style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
            modifier = Modifier
                .align(BiasAlignment(1f, -1f))
                .wrapContentWidth()
                .wrapContentHeight()
                .background(colorResource(R.color.white_2))
                .padding(8.dp)
        )
    }
}

@Composable
private fun Section7(
    data: AlignmentTestData,
    viewModel: AlignmentTestViewModel
) {
    Box(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth()
            .requiredHeight(120.dp)
            .background(colorResource(R.color.light_gray_2))
    ) {
        val resolved_text7 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.alignment_test_centerhorizontal),
            color = colorResource(R.color.black),
            fontFamily = resolved_text7.family,
            fontWeight = resolved_text7.weight,
            fontSize = resolved_text7.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text7.style ?: FontStyle.Normal,
            style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
            modifier = Modifier
                .align(BiasAlignment(0f, -1f))
                .wrapContentWidth()
                .wrapContentHeight()
                .background(colorResource(R.color.white_3))
                .padding(8.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun Section8(
    data: AlignmentTestData,
    viewModel: AlignmentTestViewModel
) {
    Box(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth()
            .requiredHeight(120.dp)
            .background(colorResource(R.color.light_gray_3))
    ) {
        val resolved_text8 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.alignment_test_centervertical),
            color = colorResource(R.color.black),
            fontFamily = resolved_text8.family,
            fontWeight = resolved_text8.weight,
            fontSize = resolved_text8.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text8.style ?: FontStyle.Normal,
            style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
            modifier = Modifier
                .align(BiasAlignment(-1f, 0f))
                .wrapContentWidth()
                .wrapContentHeight()
                .background(colorResource(R.color.white_4))
                .padding(8.dp)
        )
    }
}

@Composable
private fun Section9(
    data: AlignmentTestData,
    viewModel: AlignmentTestViewModel
) {
    Box(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth()
            .requiredHeight(120.dp)
            .background(colorResource(R.color.light_gray_4))
    ) {
        val resolved_text9 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.alignment_test_centerinparent),
            color = colorResource(R.color.black),
            fontFamily = resolved_text9.family,
            fontWeight = resolved_text9.weight,
            fontSize = resolved_text9.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text9.style ?: FontStyle.Normal,
            style = LocalTextStyle.current.copy(lineHeight = 18.2.sp),
            modifier = Modifier
                .align(Alignment.Center)
                .background(colorResource(R.color.pale_red))
                .padding(8.dp)
        )
    }
}

@Composable
private fun Section10(
    data: AlignmentTestData,
    viewModel: AlignmentTestViewModel
) {
    val resolved_text10 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.alignment_test_hstack_alignment_tests),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text10.family,
        fontWeight = resolved_text10.weight,
        fontSize = resolved_text10.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text10.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
        modifier = Modifier
            .padding(top = 20.dp)
            .padding(bottom = 10.dp)
    )
}

@Composable
private fun Section11(
    data: AlignmentTestData,
    viewModel: AlignmentTestViewModel
) {
    Row(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth()
            .requiredHeight(100.dp)
            .background(colorResource(R.color.light_gray_5))
    ) {
        val resolved_text11 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.alignment_test_top),
            color = colorResource(R.color.black),
            fontFamily = resolved_text11.family,
            fontWeight = resolved_text11.weight,
            fontSize = resolved_text11.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text11.style ?: FontStyle.Normal,
            modifier = Modifier
                .align(Alignment.Top)
                .wrapContentWidth()
                .wrapContentHeight()
                .background(colorResource(R.color.white_5))
                .padding(8.dp)
        )
        val resolved_text12 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.alignment_test_default),
            color = colorResource(R.color.black),
            fontFamily = resolved_text12.family,
            fontWeight = resolved_text12.weight,
            fontSize = resolved_text12.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text12.style ?: FontStyle.Normal,
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .background(colorResource(R.color.pale_gray))
                .padding(8.dp)
        )
        val resolved_text13 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.alignment_test_bottom),
            color = colorResource(R.color.black),
            fontFamily = resolved_text13.family,
            fontWeight = resolved_text13.weight,
            fontSize = resolved_text13.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text13.style ?: FontStyle.Normal,
            modifier = Modifier
                .align(Alignment.Bottom)
                .wrapContentWidth()
                .wrapContentHeight()
                .background(colorResource(R.color.white_7))
                .padding(8.dp)
        )
        val resolved_text14 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.alignment_test_center),
            color = colorResource(R.color.black),
            fontFamily = resolved_text14.family,
            fontWeight = resolved_text14.weight,
            fontSize = resolved_text14.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text14.style ?: FontStyle.Normal,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .wrapContentWidth()
                .wrapContentHeight()
                .background(colorResource(R.color.white_6))
                .padding(8.dp)
        )
    }
}

@Composable
private fun Section12(
    data: AlignmentTestData,
    viewModel: AlignmentTestViewModel
) {
    val resolved_text15 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.alignment_test_vstack_alignment_tests),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text15.family,
        fontWeight = resolved_text15.weight,
        fontSize = resolved_text15.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text15.style ?: FontStyle.Normal,
        style = LocalTextStyle.current.copy(lineHeight = 23.4.sp),
        modifier = Modifier
            .padding(top = 20.dp)
            .padding(bottom = 10.dp)
    )
}

@Composable
private fun Section13(
    data: AlignmentTestData,
    viewModel: AlignmentTestViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(200.dp)
            .background(colorResource(R.color.light_gray_6))
    ) {
        val resolved_text16 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.alignment_test_alignleft),
            color = colorResource(R.color.black),
            fontFamily = resolved_text16.family,
            fontWeight = resolved_text16.weight,
            fontSize = resolved_text16.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text16.style ?: FontStyle.Normal,
            modifier = Modifier
                .align(Alignment.Start)
                .wrapContentWidth()
                .wrapContentHeight()
                .background(colorResource(R.color.white_5))
                .padding(8.dp)
        )
        val resolved_text17 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.alignment_test_default),
            color = colorResource(R.color.black),
            fontFamily = resolved_text17.family,
            fontWeight = resolved_text17.weight,
            fontSize = resolved_text17.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text17.style ?: FontStyle.Normal,
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .background(colorResource(R.color.pale_gray))
                .padding(8.dp)
        )
        val resolved_text18 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.alignment_test_alignright),
            color = colorResource(R.color.black),
            fontFamily = resolved_text18.family,
            fontWeight = resolved_text18.weight,
            fontSize = resolved_text18.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text18.style ?: FontStyle.Normal,
            modifier = Modifier
                .align(Alignment.End)
                .wrapContentWidth()
                .wrapContentHeight()
                .background(colorResource(R.color.white_7))
                .padding(8.dp)
        )
        val resolved_text19 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = stringResource(R.string.alignment_test_centerhorizontal),
            color = colorResource(R.color.black),
            fontFamily = resolved_text19.family,
            fontWeight = resolved_text19.weight,
            fontSize = resolved_text19.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text19.style ?: FontStyle.Normal,
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

@Composable
private fun Section28(
    data: AlignmentTestData,
    viewModel: AlignmentTestViewModel,
    modifier: Modifier
) {
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
                    .testTag("title_label")
                    .semantics { testTagsAsResourceId = true }
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 20.dp)
                    .wrapContentWidth()
                    .wrapContentHeight(),
                textAlign = TextAlign.Center
            )
            Section2(data, viewModel)
            Section3(data, viewModel)
            Section4(data, viewModel)
            Section5(data, viewModel)
            Section6(data, viewModel)
            Section7(data, viewModel)
            Section8(data, viewModel)
            Section9(data, viewModel)
            Section10(data, viewModel)
            Section11(data, viewModel)
            Section12(data, viewModel)
            Section13(data, viewModel)
        }
        }
    }
}
// >>> RESPONSIVE_HELPERS_END