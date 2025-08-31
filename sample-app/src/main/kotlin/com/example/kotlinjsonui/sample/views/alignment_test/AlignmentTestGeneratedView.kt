package com.example.kotlinjsonui.sample.views.alignment_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.AlignmentTestData
import com.example.kotlinjsonui.sample.viewmodels.AlignmentTestViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.BiasAlignment
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
fun AlignmentTestGeneratedView(
    data: AlignmentTestData,
    viewModel: AlignmentTestViewModel
) {
    // Generated Compose code from alignment_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "alignment_test",
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
                .padding(20.dp)
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
                    .align(Alignment.CenterHorizontally)
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(bottom = 20.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.alignment_test_parent_alignment_single_propert),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(bottom = 10.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 10.dp)
                    .background(colorResource(R.color.pale_gray))
            ) {
                Text(
                    text = stringResource(R.string.alignment_test_aligntop),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .align(BiasAlignment(-1f, -1f))
                        .background(colorResource(R.color.pale_pink))
                        .padding(8.dp)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 10.dp)
                    .background(colorResource(R.color.pale_gray_2))
            ) {
                Text(
                    text = stringResource(R.string.alignment_test_alignbottom),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .align(BiasAlignment(-1f, 1f))
                        .background(colorResource(R.color.pale_yellow))
                        .padding(8.dp)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 10.dp)
                    .background(colorResource(R.color.pale_gray_3))
            ) {
                Text(
                    text = stringResource(R.string.alignment_test_alignleft),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .align(BiasAlignment(-1f, -1f))
                        .background(colorResource(R.color.pale_cyan))
                        .padding(8.dp)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 10.dp)
                    .background(colorResource(R.color.light_gray))
            ) {
                Text(
                    text = stringResource(R.string.alignment_test_alignright),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
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
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 10.dp)
                    .background(colorResource(R.color.light_gray_2))
            ) {
                Text(
                    text = stringResource(R.string.alignment_test_centerhorizontal),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
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
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 10.dp)
                    .background(colorResource(R.color.light_gray_3))
            ) {
                Text(
                    text = stringResource(R.string.alignment_test_centervertical),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
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
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 10.dp)
                    .background(colorResource(R.color.light_gray_4))
            ) {
                Text(
                    text = stringResource(R.string.alignment_test_centerinparent),
                    fontSize = 14.sp,
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .background(colorResource(R.color.pale_red))
                        .padding(8.dp)
                )
            }
            Text(
                text = stringResource(R.string.alignment_test_hstack_alignment_tests),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .padding(bottom = 10.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(bottom = 10.dp)
                    .background(colorResource(R.color.light_gray_5))
            ) {
                Text(
                    text = stringResource(R.string.alignment_test_top),
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .align(Alignment.Top)
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(colorResource(R.color.white_5))
                        .padding(8.dp)
                )
                Text(
                    text = stringResource(R.string.alignment_test_default),
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(colorResource(R.color.pale_gray))
                        .padding(8.dp)
                )
                Text(
                    text = stringResource(R.string.alignment_test_bottom),
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .align(Alignment.Bottom)
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(colorResource(R.color.white_7))
                        .padding(8.dp)
                )
                Text(
                    text = stringResource(R.string.alignment_combo_test_center),
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(colorResource(R.color.white_6))
                        .padding(8.dp)
                )
            }
            Text(
                text = stringResource(R.string.alignment_test_vstack_alignment_tests),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .padding(bottom = 10.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(colorResource(R.color.light_gray_6))
            ) {
                Text(
                    text = stringResource(R.string.alignment_test_alignleft),
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(colorResource(R.color.white_5))
                        .padding(8.dp)
                )
                Text(
                    text = stringResource(R.string.alignment_test_default),
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(colorResource(R.color.pale_gray))
                        .padding(8.dp)
                )
                Text(
                    text = stringResource(R.string.alignment_test_alignright),
                    color = colorResource(R.color.black),
                    modifier = Modifier
                        .align(Alignment.End)
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(colorResource(R.color.white_7))
                        .padding(8.dp)
                )
                Text(
                    text = stringResource(R.string.alignment_test_centerhorizontal),
                    color = colorResource(R.color.black),
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