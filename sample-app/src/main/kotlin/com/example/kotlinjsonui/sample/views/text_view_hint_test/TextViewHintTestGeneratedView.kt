package com.example.kotlinjsonui.sample.views.text_view_hint_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.TextViewHintTestData
import com.example.kotlinjsonui.sample.viewmodels.TextViewHintTestViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.shape.CircleShape
import com.kotlinjsonui.components.CustomTextField
import com.kotlinjsonui.components.CustomTextFieldWithMargins
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.wrapContentSize
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.components.SafeDynamicView
import androidx.compose.foundation.layout.Box
import com.kotlinjsonui.core.SafeDynamicView

@Composable
fun TextViewHintTestGeneratedView(
    data: TextViewHintTestData,
    viewModel: TextViewHintTestViewModel
) {
    // Generated Compose code from text_view_hint_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "text_view_hint_test",
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
                android.util.Log.e("DynamicView", "Error loading text_view_hint_test: \$error")
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
        Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(20.dp)
            .background(Color(android.graphics.Color.parseColor("#FFFFFF")))
    ) {
        Button(
            onClick = { viewModel.toggleDynamicMode() },
            modifier = Modifier
                .wrapContentWidth()
                .height(32.dp),
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
            text = "TextView Hint Test",
            fontSize = 24.sp,
            color = Color(android.graphics.Color.parseColor("#000000")),
            modifier = Modifier.padding(bottom = 20.dp)
        )
        Text(
            text = "Simple TextView with hint:",
            fontSize = 14.sp,
            color = Color(android.graphics.Color.parseColor("#666666")),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        CustomTextFieldWithMargins(
            value = data.simpleText,
            onValueChange = { newValue -> viewModel.updateData(mapOf("simpleText" to newValue)) },
            boxModifier = Modifier
                .padding(bottom = 20.dp),
            textFieldModifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            placeholder = { Text("This is a simple hint") },
            shape = RoundedCornerShape(8.dp),
            backgroundColor = Color(android.graphics.Color.parseColor("#FFFFFF")),
            isOutlined = true,
            maxLines = Int.MAX_VALUE,
            singleLine = false,
            textStyle = TextStyle(fontSize = 16.sp, color = Color(android.graphics.Color.parseColor("#000000")))
        )
        Text(
            text = "Flexible TextView with multi-line hint:",
            fontSize = 14.sp,
            color = Color(android.graphics.Color.parseColor("#666666")),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        CustomTextFieldWithMargins(
            value = data.flexibleText,
            onValueChange = { newValue -> viewModel.updateData(mapOf("flexibleText" to newValue)) },
            boxModifier = Modifier
                .padding(bottom = 20.dp),
            textFieldModifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            placeholder = { Text("Multi-line hint\nLine 2 of hint\nLine 3 of hint") },
            shape = RoundedCornerShape(8.dp),
            backgroundColor = Color(android.graphics.Color.parseColor("#FFFFFF")),
            isOutlined = true,
            maxLines = Int.MAX_VALUE,
            singleLine = false,
            textStyle = TextStyle(fontSize = 16.sp, color = Color(android.graphics.Color.parseColor("#000000")))
        )
    }    }
    // >>> GENERATED_CODE_END
}