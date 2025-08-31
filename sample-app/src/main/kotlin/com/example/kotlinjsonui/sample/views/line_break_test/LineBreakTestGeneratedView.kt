package com.example.kotlinjsonui.sample.views.line_break_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.LineBreakTestData
import com.example.kotlinjsonui.sample.viewmodels.LineBreakTestViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import androidx.compose.ui.text.style.TextOverflow
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
fun LineBreakTestGeneratedView(
    data: LineBreakTestData,
    viewModel: LineBreakTestViewModel
) {
    // Generated Compose code from line_break_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "line_break_test",
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
                text = stringResource(R.string.line_break_test_linebreakmode_word_default),
                fontSize = 16.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp)
            )
            Text(
                text = "${data.longText}",
                fontSize = 14.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .background(colorResource(R.color.pale_gray))
                    .padding(10.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = stringResource(R.string.line_break_test_linebreakmode_char),
                fontSize = 16.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp)
            )
            Text(
                text = "${data.longText}",
                fontSize = 14.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .background(colorResource(R.color.white_5))
                    .padding(10.dp),
                maxLines = 2
            )
            Text(
                text = stringResource(R.string.line_break_test_linebreakmode_clip),
                fontSize = 16.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp)
            )
            Text(
                text = "${data.longText}",
                fontSize = 14.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .background(colorResource(R.color.white_6))
                    .padding(10.dp),
                maxLines = 2,
                overflow = TextOverflow.Clip
            )
            Text(
                text = stringResource(R.string.line_break_test_linebreakmode_head),
                fontSize = 16.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp)
            )
            Text(
                text = "${data.longText}",
                fontSize = 14.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .background(colorResource(R.color.white_7))
                    .padding(10.dp),
                maxLines = 2
            )
            Text(
                text = stringResource(R.string.line_break_test_linebreakmode_middle),
                fontSize = 16.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp)
            )
            Text(
                text = "${data.longText}",
                fontSize = 14.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .background(colorResource(R.color.white_8))
                    .padding(10.dp),
                maxLines = 2
            )
            Text(
                text = stringResource(R.string.line_break_test_linebreakmode_tail),
                fontSize = 16.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp)
            )
            Text(
                text = "${data.longText}",
                fontSize = 14.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .background(colorResource(R.color.white_9))
                    .padding(10.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = stringResource(R.string.line_break_test_lines_property_test),
                fontSize = 16.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 30.dp)
            )
            Text(
                text = stringResource(R.string.line_break_test_lines_1),
                fontSize = 14.sp,
                color = colorResource(R.color.medium_gray_4),
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = "${data.longText}",
                fontSize = 14.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .background(colorResource(R.color.white_4))
                    .padding(10.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = stringResource(R.string.line_break_test_lines_3),
                fontSize = 14.sp,
                color = colorResource(R.color.medium_gray_4),
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = "${data.longText}",
                fontSize = 14.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .background(colorResource(R.color.pale_pink))
                    .padding(10.dp),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = stringResource(R.string.line_break_test_lines_0_unlimited),
                fontSize = 14.sp,
                color = colorResource(R.color.medium_gray_4),
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = "${data.longText}",
                fontSize = 14.sp,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .background(colorResource(R.color.pale_yellow))
                    .padding(10.dp),
                maxLines = Int.MAX_VALUE
            )
        }
        }
    }    }
    // >>> GENERATED_CODE_END
}