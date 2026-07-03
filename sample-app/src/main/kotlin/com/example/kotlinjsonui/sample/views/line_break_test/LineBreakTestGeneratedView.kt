package com.example.kotlinjsonui.sample.views.line_break_test

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.R
import com.example.kotlinjsonui.sample.data.LineBreakTestData
import com.example.kotlinjsonui.sample.viewmodels.LineBreakTestViewModel
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont

@Composable
fun LineBreakTestGeneratedView(
    data: LineBreakTestData,
    viewModel: LineBreakTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from line_break_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "line_break_test",
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
                android.util.Log.e("DynamicView", "Error loading line_break_test: \$error")
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
        ) {
            Section0(data, viewModel)
            Section1(data, viewModel)
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
            Section14(data, viewModel)
            Section15(data, viewModel)
            Section16(data, viewModel)
            Section17(data, viewModel)
            Section18(data, viewModel)
            Section19(data, viewModel)
            Section20(data, viewModel)
        }
        }
    }    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section0(
    data: LineBreakTestData,
    viewModel: LineBreakTestViewModel
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
        val resolved_button21 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = FontWeight.Medium,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = "${data.dynamicModeStatus}",
            fontFamily = resolved_button21.family,
            fontWeight = resolved_button21.weight,
            fontSize = resolved_button21.size ?: TextUnit.Unspecified,
            fontStyle = resolved_button21.style ?: FontStyle.Normal,
        )
    }
}

@Composable
private fun Section1(
    data: LineBreakTestData,
    viewModel: LineBreakTestViewModel
) {
    val resolved_text305 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 24.sp,
        italic = false
    ))
    Text(
        text = "${data.title}",
        color = colorResource(R.color.black),
        fontFamily = resolved_text305.family,
        fontWeight = resolved_text305.weight,
        fontSize = resolved_text305.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text305.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 31.2.sp),
        modifier = Modifier
            .testTag("title_label")
            .semantics { testTagsAsResourceId = true }
            .padding(top = 20.dp)
            .wrapContentWidth()
            .wrapContentHeight()
    )
}

@Composable
private fun Section2(
    data: LineBreakTestData,
    viewModel: LineBreakTestViewModel
) {
    val resolved_text306 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.line_break_test_linebreakmode_word_default),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text306.family,
        fontWeight = resolved_text306.weight,
        fontSize = resolved_text306.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text306.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier.padding(top = 20.dp)
    )
}

@Composable
private fun Section3(
    data: LineBreakTestData,
    viewModel: LineBreakTestViewModel
) {
    val resolved_text307 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = "${data.longText}",
        color = colorResource(R.color.black),
        fontFamily = resolved_text307.family,
        fontWeight = resolved_text307.weight,
        fontSize = resolved_text307.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text307.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier
            .padding(top = 10.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp)
            .fillMaxWidth()
            .height(60.dp)
            .background(colorResource(R.color.pale_gray))
            .padding(10.dp),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun Section4(
    data: LineBreakTestData,
    viewModel: LineBreakTestViewModel
) {
    val resolved_text308 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.line_break_test_linebreakmode_char),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text308.family,
        fontWeight = resolved_text308.weight,
        fontSize = resolved_text308.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text308.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier.padding(top = 20.dp)
    )
}

@Composable
private fun Section5(
    data: LineBreakTestData,
    viewModel: LineBreakTestViewModel
) {
    val resolved_text309 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = "${data.longText}",
        color = colorResource(R.color.black),
        fontFamily = resolved_text309.family,
        fontWeight = resolved_text309.weight,
        fontSize = resolved_text309.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text309.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier
            .padding(top = 10.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp)
            .fillMaxWidth()
            .height(60.dp)
            .background(colorResource(R.color.white_5))
            .padding(10.dp),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun Section6(
    data: LineBreakTestData,
    viewModel: LineBreakTestViewModel
) {
    val resolved_text310 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.line_break_test_linebreakmode_clip),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text310.family,
        fontWeight = resolved_text310.weight,
        fontSize = resolved_text310.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text310.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier.padding(top = 20.dp)
    )
}

@Composable
private fun Section7(
    data: LineBreakTestData,
    viewModel: LineBreakTestViewModel
) {
    val resolved_text311 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = "${data.longText}",
        color = colorResource(R.color.black),
        fontFamily = resolved_text311.family,
        fontWeight = resolved_text311.weight,
        fontSize = resolved_text311.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text311.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier
            .padding(top = 10.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp)
            .fillMaxWidth()
            .height(60.dp)
            .background(colorResource(R.color.white_6))
            .padding(10.dp),
        maxLines = 2,
        overflow = TextOverflow.Clip
    )
}

@Composable
private fun Section8(
    data: LineBreakTestData,
    viewModel: LineBreakTestViewModel
) {
    val resolved_text312 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.line_break_test_linebreakmode_head),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text312.family,
        fontWeight = resolved_text312.weight,
        fontSize = resolved_text312.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text312.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier.padding(top = 20.dp)
    )
}

@Composable
private fun Section9(
    data: LineBreakTestData,
    viewModel: LineBreakTestViewModel
) {
    val resolved_text313 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = "${data.longText}",
        color = colorResource(R.color.black),
        fontFamily = resolved_text313.family,
        fontWeight = resolved_text313.weight,
        fontSize = resolved_text313.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text313.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier
            .padding(top = 10.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp)
            .fillMaxWidth()
            .height(60.dp)
            .background(colorResource(R.color.white_7))
            .padding(10.dp),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun Section10(
    data: LineBreakTestData,
    viewModel: LineBreakTestViewModel
) {
    val resolved_text314 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.line_break_test_linebreakmode_middle),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text314.family,
        fontWeight = resolved_text314.weight,
        fontSize = resolved_text314.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text314.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier.padding(top = 20.dp)
    )
}

@Composable
private fun Section11(
    data: LineBreakTestData,
    viewModel: LineBreakTestViewModel
) {
    val resolved_text315 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = "${data.longText}",
        color = colorResource(R.color.black),
        fontFamily = resolved_text315.family,
        fontWeight = resolved_text315.weight,
        fontSize = resolved_text315.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text315.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier
            .padding(top = 10.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp)
            .fillMaxWidth()
            .height(60.dp)
            .background(colorResource(R.color.white_8))
            .padding(10.dp),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun Section12(
    data: LineBreakTestData,
    viewModel: LineBreakTestViewModel
) {
    val resolved_text316 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.line_break_test_linebreakmode_tail),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text316.family,
        fontWeight = resolved_text316.weight,
        fontSize = resolved_text316.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text316.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier.padding(top = 20.dp)
    )
}

@Composable
private fun Section13(
    data: LineBreakTestData,
    viewModel: LineBreakTestViewModel
) {
    val resolved_text317 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = "${data.longText}",
        color = colorResource(R.color.black),
        fontFamily = resolved_text317.family,
        fontWeight = resolved_text317.weight,
        fontSize = resolved_text317.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text317.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier
            .padding(top = 10.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp)
            .fillMaxWidth()
            .height(60.dp)
            .background(colorResource(R.color.white_9))
            .padding(10.dp),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun Section14(
    data: LineBreakTestData,
    viewModel: LineBreakTestViewModel
) {
    val resolved_text318 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 16.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.line_break_test_lines_property_test),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text318.family,
        fontWeight = resolved_text318.weight,
        fontSize = resolved_text318.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text318.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 20.8.sp),
        modifier = Modifier.padding(top = 30.dp)
    )
}

@Composable
private fun Section15(
    data: LineBreakTestData,
    viewModel: LineBreakTestViewModel
) {
    val resolved_text319 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.line_break_test_lines_1),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text319.family,
        fontWeight = resolved_text319.weight,
        fontSize = resolved_text319.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text319.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier.padding(top = 10.dp)
    )
}

@Composable
private fun Section16(
    data: LineBreakTestData,
    viewModel: LineBreakTestViewModel
) {
    val resolved_text320 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = "${data.longText}",
        color = colorResource(R.color.black),
        fontFamily = resolved_text320.family,
        fontWeight = resolved_text320.weight,
        fontSize = resolved_text320.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text320.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier
            .padding(start = 20.dp)
            .padding(end = 20.dp)
            .fillMaxWidth()
            .background(colorResource(R.color.white_4))
            .padding(10.dp),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun Section17(
    data: LineBreakTestData,
    viewModel: LineBreakTestViewModel
) {
    val resolved_text321 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.line_break_test_lines_3),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text321.family,
        fontWeight = resolved_text321.weight,
        fontSize = resolved_text321.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text321.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier.padding(top = 10.dp)
    )
}

@Composable
private fun Section18(
    data: LineBreakTestData,
    viewModel: LineBreakTestViewModel
) {
    val resolved_text322 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = "${data.longText}",
        color = colorResource(R.color.black),
        fontFamily = resolved_text322.family,
        fontWeight = resolved_text322.weight,
        fontSize = resolved_text322.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text322.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier
            .padding(start = 20.dp)
            .padding(end = 20.dp)
            .fillMaxWidth()
            .background(colorResource(R.color.pale_pink))
            .padding(10.dp),
        maxLines = 3,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun Section19(
    data: LineBreakTestData,
    viewModel: LineBreakTestViewModel
) {
    val resolved_text323 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.line_break_test_lines_0_unlimited),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text323.family,
        fontWeight = resolved_text323.weight,
        fontSize = resolved_text323.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text323.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier.padding(top = 10.dp)
    )
}

@Composable
private fun Section20(
    data: LineBreakTestData,
    viewModel: LineBreakTestViewModel
) {
    val resolved_text324 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = "${data.longText}",
        color = colorResource(R.color.black),
        fontFamily = resolved_text324.family,
        fontWeight = resolved_text324.weight,
        fontSize = resolved_text324.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text324.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier
            .padding(start = 20.dp)
            .padding(end = 20.dp)
            .fillMaxWidth()
            .background(colorResource(R.color.pale_yellow))
            .padding(10.dp),
        maxLines = Int.MAX_VALUE
    )
}
// >>> RESPONSIVE_HELPERS_END