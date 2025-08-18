package com.example.kotlinjsonui.sample.views.secure_field_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.SecureFieldTestData
import com.example.kotlinjsonui.sample.viewmodels.SecureFieldTestViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.PasswordVisualTransformation

@Composable
fun SecureFieldTestGeneratedView(
    data: SecureFieldTestData,
    viewModel: SecureFieldTestViewModel
) {
    // Generated Compose code from secure_field_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
        Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color(android.graphics.Color.parseColor("#F8F8F8")))
    ) {
        Button(
            onClick = { },
            modifier = Modifier
        ) {
            Text("Button")
        }
        Text(
            text = "${data.title}",
            fontSize = 24.sp,
            color = Color(android.graphics.Color.parseColor("#000000")),
            modifier = Modifier
                .padding(top = 20.dp)
                .wrapContentWidth()
                .wrapContentHeight()
        )
        Text(
            text = "Regular TextField (not secure)",
            fontSize = 14.sp,
            color = Color(android.graphics.Color.parseColor("#666666")),
            modifier = Modifier.padding(top = 30.dp)
        )
TextField(
            value = "${data.regularText}",
            onValueChange = { newValue -> currentData.value = currentData.value.copy(regularText = newValue) },
            placeholder = { Text("Enter regular text") },
            modifier = Modifier.fillMaxWidth().padding(10.dp)
        )
        Text(
            text = "Secure TextField (password)",
            fontSize = 14.sp,
            color = Color(android.graphics.Color.parseColor("#666666")),
            modifier = Modifier.padding(top = 20.dp)
        )
OutlinedTextField(
            value = "${data.password}",
            onValueChange = { newValue -> currentData.value = currentData.value.copy(password = newValue) },
            placeholder = { Text("Enter password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(10.dp)
        )
        Text(
            text = "Confirm Password (also secure)",
            fontSize = 14.sp,
            color = Color(android.graphics.Color.parseColor("#666666")),
            modifier = Modifier.padding(top = 20.dp)
        )
OutlinedTextField(
            value = "${data.confirmPassword}",
            onValueChange = { newValue -> currentData.value = currentData.value.copy(confirmPassword = newValue) },
            placeholder = { Text("Confirm password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(10.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(15.dp)
                .padding(top = 30.dp)
                .padding(start = 20.dp)
                .padding(end = 20.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(android.graphics.Color.parseColor("#E0E0E0")))
        ) {
            Text(
                text = "Values entered:",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
            )
            Text(
                text = "${data.regularText}",
                fontSize = 12.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier.padding(top = 5.dp)
            )
            Text(
                text = "Password: [hidden]",
                fontSize = 12.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier.padding(top = 5.dp)
            )
            Text(
                text = "Confirm: [hidden]",
                fontSize = 12.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier.padding(top = 5.dp)
            )
        }
    }
    // >>> GENERATED_CODE_END
}