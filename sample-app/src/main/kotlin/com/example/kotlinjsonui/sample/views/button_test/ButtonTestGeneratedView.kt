package com.example.kotlinjsonui.sample.views.button_test

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
import com.example.kotlinjsonui.sample.data.ButtonTestData
import com.example.kotlinjsonui.sample.viewmodels.ButtonTestViewModel
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
fun ButtonTestGeneratedView(
    data: ButtonTestData,
    viewModel: ButtonTestViewModel
) {
    // Generated Compose code from button_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "button_test",
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
                android.util.Log.e("DynamicView", "Error loading button_test: \$error")
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
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.button_test_button_height_test),
                fontSize = 20.sp,
                color = colorResource(R.color.black),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 20.dp)
            )
            Text(
                text = stringResource(R.string.button_test_height_55_padding_12_20),
                fontSize = 14.sp,
                color = colorResource(R.color.medium_gray_4),
                modifier = Modifier.padding(bottom = 5.dp)
            )
            Button(
                onClick = { },
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .height(55.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp, horizontal = 20.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_blue)
                                )
            ) {
                Text(
                    text = stringResource(R.string.button_test_test_button_1),
                    color = colorResource(R.color.white),
                )
            }
            Text(
                text = stringResource(R.string.button_test_height_55_no_padding),
                fontSize = 14.sp,
                color = colorResource(R.color.medium_gray_4),
                modifier = Modifier.padding(bottom = 5.dp)
            )
            Button(
                onClick = { },
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .height(55.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_green)
                                )
            ) {
                Text(
                    text = stringResource(R.string.button_test_test_button_2),
                    color = colorResource(R.color.white),
                )
            }
            Text(
                text = stringResource(R.string.button_test_no_height_padding_12_20),
                fontSize = 14.sp,
                color = colorResource(R.color.medium_gray_4),
                modifier = Modifier.padding(bottom = 5.dp)
            )
            Button(
                onClick = { },
                modifier = Modifier.padding(bottom = 20.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp, horizontal = 20.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_red_3)
                                )
            ) {
                Text(
                    text = stringResource(R.string.button_test_test_button_3),
                    color = colorResource(R.color.white),
                )
            }
            Text(
                text = stringResource(R.string.button_test_with_bottommargin_8_height_55_p),
                fontSize = 14.sp,
                color = colorResource(R.color.medium_gray_4),
                modifier = Modifier.padding(bottom = 5.dp)
            )
            Button(
                onClick = { },
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .height(55.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp, horizontal = 20.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_blue)
                                )
            ) {
                Text(
                    text = stringResource(R.string.button_test_like_primarybutton_style),
                    color = colorResource(R.color.white),
                )
            }
        }
        }
    }    }
    // >>> GENERATED_CODE_END
}