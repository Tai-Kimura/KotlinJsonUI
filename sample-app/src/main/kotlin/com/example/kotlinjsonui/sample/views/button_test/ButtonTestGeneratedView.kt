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
            .background(Color(android.graphics.Color.parseColor("#FFFFFF")))
    ) {
        item {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Button Height Test",
                fontSize = 20.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 20.dp)
            )
            Text(
                text = "Height 55, Padding [12, 20]",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
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
                                    containerColor = Color(android.graphics.Color.parseColor("#007AFF"))
                                )
            ) {
                Text(
                    text = "Test Button 1",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Text(
                text = "Height 55, No Padding",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier.padding(bottom = 5.dp)
            )
            Button(
                onClick = { },
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .height(55.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#34C759"))
                                )
            ) {
                Text(
                    text = "Test Button 2",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Text(
                text = "No Height, Padding [12, 20]",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier.padding(bottom = 5.dp)
            )
            Button(
                onClick = { },
                modifier = Modifier.padding(bottom = 20.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp, horizontal = 20.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#FF9500"))
                                )
            ) {
                Text(
                    text = "Test Button 3",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Text(
                text = "With bottomMargin 8, Height 55, Padding [12, 20]",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
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
                                    containerColor = Color(android.graphics.Color.parseColor("#007AFF"))
                                )
            ) {
                Text(
                    text = "Like primary_button style",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
        }
        }
    }    }
    // >>> GENERATED_CODE_END
}