package com.example.kotlinjsonui.sample.views.disabled_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.DisabledTestData
import com.example.kotlinjsonui.sample.viewmodels.DisabledTestViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.ButtonDefaults

@Composable
fun DisabledTestGeneratedView(
    data: DisabledTestData,
    viewModel: DisabledTestViewModel
) {
    // Generated Compose code from disabled_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
        LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color(android.graphics.Color.parseColor("#F8F8F8")))
    ) {
        item {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Button(
                onClick = { },
            ) {
                Text("Button")
            }
            Text(
                text = "\${data.title}",
                fontSize = 24.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .padding(top = 20.dp)
                    .wrapContentWidth()
                    .wrapContentHeight()
            )
            Text(
                text = "Enabled Button",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                modifier = Modifier.padding(top = 20.dp)
            )
            Button(
                onClick = { viewModel.onEnabledButtonTap() },
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .fillMaxWidth()
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#007AFF"))
                                ),
                enabled = true
            ) {
                Text(
                    text = "Enabled Button",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Text(
                text = "Disabled Button",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                modifier = Modifier.padding(top = 20.dp)
            )
            Button(
                onClick = { viewModel.onDisabledButtonTap() },
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .fillMaxWidth()
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#007AFF")),
                                    disabledContainerColor = Color(android.graphics.Color.parseColor("#CCCCCC")),
                                    disabledContentColor = Color(android.graphics.Color.parseColor("#999999"))
                                ),
                enabled = false
            ) {
                Text(
                    text = "Disabled Button",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Text(
                text = "TouchDisabledState Button",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                modifier = Modifier.padding(top = 20.dp)
            )
            Button(
                onClick = { viewModel.onTouchDisabledTap() },
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .fillMaxWidth()
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#FF9500"))
                                )
            ) {
                Text(
                    text = "Touch Disabled",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Text(
                text = "Enabled TextField",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                modifier = Modifier.padding(top = 20.dp)
            )
            TextField(
                value = "\${data.textFieldValue}",
                onValueChange = { newValue -> currentData.value = currentData.value.copy(textFieldValue = newValue) },
                placeholder = { Text("Enabled - can type here") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .padding(10.dp)
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, Color(android.graphics.Color.parseColor("#CCCCCC")), RoundedCornerShape(8.dp))
                    .background(Color(android.graphics.Color.parseColor("#FFFFFF")))
            )
            Text(
                text = "Disabled TextField",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                modifier = Modifier.padding(top = 20.dp)
            )
            TextField(
                value = "Disabled text field",
                onValueChange = { },
                placeholder = { Text("Disabled - cannot type") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .padding(10.dp)
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, Color(android.graphics.Color.parseColor("#CCCCCC")), RoundedCornerShape(8.dp))
                    .background(Color(android.graphics.Color.parseColor("#F0F0F0")))
            )
            Text(
                text = "Dynamic Enable/Disable Test",
                fontSize = 16.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 30.dp)
            )
            Button(
                onClick = { viewModel.toggleEnableState() },
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .fillMaxWidth()
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#34C759"))
                                )
            ) {
                Text(
                    text = "Toggle Enable State",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Button(
                onClick = { viewModel.onDynamicButtonTap() },
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(start = 20.dp)
                    .padding(end = 20.dp)
                    .fillMaxWidth()
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#5856D6")),
                                    disabledContainerColor = Color(android.graphics.Color.parseColor("#D0D0D0")),
                                    disabledContentColor = Color(android.graphics.Color.parseColor("#888888"))
                                ),
                enabled = data.isEnabled
            ) {
                Text(
                    text = "Dynamic Button",
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Text(
                text = "\${data.isEnabled}",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                textAlign = TextAlign.Center
            )
        }
        }
    }    // >>> GENERATED_CODE_END
}