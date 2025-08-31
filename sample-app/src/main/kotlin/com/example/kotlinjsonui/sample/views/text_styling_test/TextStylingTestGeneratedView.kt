package com.example.kotlinjsonui.sample.views.text_styling_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.TextStylingTestData
import com.example.kotlinjsonui.sample.viewmodels.TextStylingTestViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.layout.PaddingValues
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
fun TextStylingTestGeneratedView(
    data: TextStylingTestData,
    viewModel: TextStylingTestViewModel
) {
    // Generated Compose code from text_styling_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "text_styling_test",
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
                android.util.Log.e("DynamicView", "Error loading text_styling_test: \$error")
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
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(colorResource(R.color.white))
    ) {
        item {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Button(
                onClick = { viewModel.toggleDynamicMode() },
                modifier = Modifier
                    .wrapContentWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_blue_3)
                                )
            ) {
                Text(
                    text = "${data.dynamicModeStatus}",
                    fontSize = 14.sp,
                    color = colorResource(R.color.white),
                )
            }
            Text(
                text = "${data.title}",
                fontSize = 24.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 20.dp)
            )
            Text(
                text = stringResource(R.string.text_styling_test_font_sizes),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp)
            )
            Text(
                text = stringResource(R.string.text_styling_test_fontsize_12),
                fontSize = 12.sp,
                color = colorResource(R.color.black),
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = stringResource(R.string.text_styling_test_fontsize_16),
                fontSize = 16.sp,
                color = colorResource(R.color.black),
                modifier = Modifier.padding(top = 5.dp)
            )
            Text(
                text = stringResource(R.string.text_styling_test_fontsize_20),
                fontSize = 20.sp,
                color = colorResource(R.color.black),
                modifier = Modifier.padding(top = 5.dp)
            )
            Text(
                text = stringResource(R.string.text_styling_test_fontsize_24),
                fontSize = 24.sp,
                color = colorResource(R.color.black),
                modifier = Modifier.padding(top = 5.dp)
            )
            Text(
                text = stringResource(R.string.text_styling_test_font_styles),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp)
            )
            Text(
                text = stringResource(R.string.text_styling_test_bold_text),
                fontSize = 16.sp,
                color = colorResource(R.color.black),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = stringResource(R.string.text_styling_test_underlined_text),
                fontSize = 16.sp,
                color = colorResource(R.color.black),
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = stringResource(R.string.text_styling_test_text_alignment),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp)
            )
            Text(
                text = stringResource(R.string.text_styling_test_left_aligned_default),
                fontSize = 16.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .background(colorResource(R.color.pale_gray))
                    .padding(5.dp),
                textAlign = TextAlign.Start
            )
            Text(
                text = stringResource(R.string.text_styling_test_center_aligned),
                fontSize = 16.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .background(colorResource(R.color.pale_gray))
                    .padding(5.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.text_styling_test_right_aligned),
                fontSize = 16.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .background(colorResource(R.color.pale_gray))
                    .padding(5.dp),
                textAlign = TextAlign.End
            )
            Text(
                text = stringResource(R.string.text_styling_test_line_spacing_test),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp)
            )
            Text(
                text = stringResource(R.string.text_styling_test_this_is_a_multiline_text_with_l),
                fontSize = 14.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .background(colorResource(R.color.white))
                    .padding(10.dp)
            )
            Text(
                text = stringResource(R.string.text_styling_test_this_is_another_multiline_text_),
                fontSize = 14.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .background(colorResource(R.color.white))
                    .padding(10.dp)
            )
            Text(
                text = stringResource(R.string.text_styling_test_font_colors),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp)
            )
            Text(
                text = stringResource(R.string.text_styling_test_red_text),
                fontSize = 16.sp,
                color = colorResource(R.color.dark_red),
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = stringResource(R.string.text_styling_test_green_text),
                fontSize = 16.sp,
                color = colorResource(R.color.dark_green_2),
                modifier = Modifier.padding(top = 5.dp)
            )
            Text(
                text = stringResource(R.string.text_styling_test_blue_text),
                fontSize = 16.sp,
                color = colorResource(R.color.dark_blue),
                modifier = Modifier.padding(top = 5.dp)
            )
        }
        }
    }    }
    // >>> GENERATED_CODE_END
}