package com.example.kotlinjsonui.sample.views.width_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.WidthTestData
import com.example.kotlinjsonui.sample.viewmodels.WidthTestViewModel
import androidx.compose.foundation.background
import androidx.compose.ui.text.style.TextAlign
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
fun WidthTestGeneratedView(
    data: WidthTestData,
    viewModel: WidthTestViewModel
) {
    // Generated Compose code from width_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "width_test",
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
                android.util.Log.e("DynamicView", "Error loading width_test: \$error")
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
        Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color(android.graphics.Color.parseColor("#FFFFFF")))
    ) {
        Button(
            onClick = { viewModel.toggleDynamicMode() },
            modifier = Modifier
                .wrapContentWidth()
                .height(44.dp),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
            colors = ButtonDefaults.buttonColors(
                            containerColor = Color(android.graphics.Color.parseColor("#5856D6"))
                        )
        ) {
            Text(
                text = "Dynamic: ${data.dynamicModeStatus}",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#FFFFFF")),
            )
        }
        Text(
            text = "Width Test",
            fontSize = 24.sp,
            color = Color(android.graphics.Color.parseColor("#000000")),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 20.dp)
        )
        Text(
            text = "matchParent width",
            color = Color(android.graphics.Color.parseColor("#FFFFFF")),
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .height(50.dp)
                .padding(top = 20.dp)
                .background(Color(android.graphics.Color.parseColor("#FF6B6B"))),
            textAlign = TextAlign.Center
        )
        Text(
            text = "Fixed width 200",
            color = Color(android.graphics.Color.parseColor("#FFFFFF")),
            modifier = Modifier
                .align(Alignment.TopStart)
                .width(200.dp)
                .height(50.dp)
                .padding(top = 10.dp)
                .background(Color(android.graphics.Color.parseColor("#4ECDC4"))),
            textAlign = TextAlign.Center
        )
        Text(
            text = "wrapContent width",
            color = Color(android.graphics.Color.parseColor("#FFFFFF")),
            modifier = Modifier
                .align(Alignment.TopStart)
                .wrapContentWidth()
                .height(50.dp)
                .padding(top = 10.dp)
                .background(Color(android.graphics.Color.parseColor("#45B7D1"))),
            textAlign = TextAlign.Center
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(top = 20.dp)
                .background(Color(android.graphics.Color.parseColor("#E0E0E0")))
        ) {
            Text(
                text = "Weight 1",
                color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color(android.graphics.Color.parseColor("#96CEB4"))),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Weight 2 (wrap)",
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .weight(2f)
                    .wrapContentHeight()
                    .background(Color(android.graphics.Color.parseColor("#FFEAA7"))),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Weight 1",
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color(android.graphics.Color.parseColor("#DFE6E9"))),
                textAlign = TextAlign.Center
            )
        }
    }    }
    // >>> GENERATED_CODE_END
}