package com.example.kotlinjsonui.sample.views.form_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.FormTestData
import com.example.kotlinjsonui.sample.viewmodels.FormTestViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.material3.ButtonDefaults

@Composable
fun FormTestGeneratedView(
    data: FormTestData,
    viewModel: FormTestViewModel
) {
    // Generated Compose code from form_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
        LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color(android.graphics.Color.parseColor("#F0F0F0")))
    ) {
        item {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp)
        ) {
            Button(
                onClick = { },
            ) {
                Text("Button")
            }
            Text(
                text = "\${data.title}",
                fontSize = 28.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            Text(
                text = "Personal Information",
                fontSize = 20.sp,
                color = Color(android.graphics.Color.parseColor("#007AFF")),
                fontWeight = FontWeight.Semibold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "First Name",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            TextField(
                value = "\${data.firstName}",
                onValueChange = { newValue -> currentData.value = currentData.value.copy(firstName = newValue) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(14.dp)
                    .padding(bottom = 16.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, Color(android.graphics.Color.parseColor("#E0E0E0")), RoundedCornerShape(10.dp))
                    .background(Color(android.graphics.Color.parseColor("#FFFFFF"))),
                textStyle = TextStyle(fontSize = 16.sp)
            )
            Text(
                text = "Last Name",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            TextField(
                value = "\${data.lastName}",
                onValueChange = { newValue -> currentData.value = currentData.value.copy(lastName = newValue) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(14.dp)
                    .padding(bottom = 16.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, Color(android.graphics.Color.parseColor("#E0E0E0")), RoundedCornerShape(10.dp))
                    .background(Color(android.graphics.Color.parseColor("#FFFFFF"))),
                textStyle = TextStyle(fontSize = 16.sp)
            )
            Text(
                text = "Email Address",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            TextField(
                value = "\${data.email}",
                onValueChange = { newValue -> currentData.value = currentData.value.copy(email = newValue) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(14.dp)
                    .padding(bottom = 16.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, Color(android.graphics.Color.parseColor("#E0E0E0")), RoundedCornerShape(10.dp))
                    .background(Color(android.graphics.Color.parseColor("#FFFFFF"))),
                textStyle = TextStyle(fontSize = 16.sp)
            )
            Text(
                text = "Phone Number",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            TextField(
                value = "\${data.phone}",
                onValueChange = { newValue -> currentData.value = currentData.value.copy(phone = newValue) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(14.dp)
                    .padding(bottom = 24.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, Color(android.graphics.Color.parseColor("#E0E0E0")), RoundedCornerShape(10.dp))
                    .background(Color(android.graphics.Color.parseColor("#FFFFFF"))),
                textStyle = TextStyle(fontSize = 16.sp)
            )
            Text(
                text = "Address Information",
                fontSize = 20.sp,
                color = Color(android.graphics.Color.parseColor("#007AFF")),
                fontWeight = FontWeight.Semibold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "Street Address",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            TextField(
                value = "\${data.address}",
                onValueChange = { newValue -> currentData.value = currentData.value.copy(address = newValue) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(14.dp)
                    .padding(bottom = 16.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, Color(android.graphics.Color.parseColor("#E0E0E0")), RoundedCornerShape(10.dp))
                    .background(Color(android.graphics.Color.parseColor("#FFFFFF"))),
                textStyle = TextStyle(fontSize = 16.sp)
            )
            Text(
                text = "City",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            TextField(
                value = "\${data.city}",
                onValueChange = { newValue -> currentData.value = currentData.value.copy(city = newValue) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(14.dp)
                    .padding(bottom = 16.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, Color(android.graphics.Color.parseColor("#E0E0E0")), RoundedCornerShape(10.dp))
                    .background(Color(android.graphics.Color.parseColor("#FFFFFF"))),
                textStyle = TextStyle(fontSize = 16.sp)
            )
            Text(
                text = "ZIP Code",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            TextField(
                value = "\${data.zipCode}",
                onValueChange = { newValue -> currentData.value = currentData.value.copy(zipCode = newValue) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(14.dp)
                    .padding(bottom = 16.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, Color(android.graphics.Color.parseColor("#E0E0E0")), RoundedCornerShape(10.dp))
                    .background(Color(android.graphics.Color.parseColor("#FFFFFF"))),
                textStyle = TextStyle(fontSize = 16.sp)
            )
            Text(
                text = "Country",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            TextField(
                value = "\${data.country}",
                onValueChange = { newValue -> currentData.value = currentData.value.copy(country = newValue) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(14.dp)
                    .padding(bottom = 24.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, Color(android.graphics.Color.parseColor("#E0E0E0")), RoundedCornerShape(10.dp))
                    .background(Color(android.graphics.Color.parseColor("#FFFFFF"))),
                textStyle = TextStyle(fontSize = 16.sp)
            )
            Text(
                text = "Professional Information",
                fontSize = 20.sp,
                color = Color(android.graphics.Color.parseColor("#007AFF")),
                fontWeight = FontWeight.Semibold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "Company",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            TextField(
                value = "\${data.company}",
                onValueChange = { newValue -> currentData.value = currentData.value.copy(company = newValue) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(14.dp)
                    .padding(bottom = 16.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, Color(android.graphics.Color.parseColor("#E0E0E0")), RoundedCornerShape(10.dp))
                    .background(Color(android.graphics.Color.parseColor("#FFFFFF"))),
                textStyle = TextStyle(fontSize = 16.sp)
            )
            Text(
                text = "Job Title",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            TextField(
                value = "\${data.jobTitle}",
                onValueChange = { newValue -> currentData.value = currentData.value.copy(jobTitle = newValue) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(14.dp)
                    .padding(bottom = 24.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, Color(android.graphics.Color.parseColor("#E0E0E0")), RoundedCornerShape(10.dp))
                    .background(Color(android.graphics.Color.parseColor("#FFFFFF"))),
                textStyle = TextStyle(fontSize = 16.sp)
            )
            Text(
                text = "Additional Information",
                fontSize = 20.sp,
                color = Color(android.graphics.Color.parseColor("#007AFF")),
                fontWeight = FontWeight.Semibold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "Bio (Flexible Height)",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = "\${data.bio}",
                onValueChange = { newValue -> currentData.value = currentData.value.copy(bio = newValue) },
                placeholder = { Text("Tell us about yourself...
                This field will grow as you type") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(14.dp)
                    .padding(bottom = 16.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, Color(android.graphics.Color.parseColor("#E0E0E0")), RoundedCornerShape(10.dp))
                    .background(Color(android.graphics.Color.parseColor("#FFFFFF"))),
                textStyle = TextStyle(fontSize = 16.sp, color = Color(android.graphics.Color.parseColor("#333333"))),
                maxLines = Int.MAX_VALUE,
                singleLine = false
            )
            Text(
                text = "Notes (Fixed Height)",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = "\${data.notes}",
                onValueChange = { newValue -> currentData.value = currentData.value.copy(notes = newValue) },
                placeholder = { Text("Additional notes...
                Fixed height field") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(14.dp)
                    .padding(bottom = 16.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, Color(android.graphics.Color.parseColor("#E0E0E0")), RoundedCornerShape(10.dp))
                    .background(Color(android.graphics.Color.parseColor("#FFFFFF"))),
                textStyle = TextStyle(fontSize = 16.sp, color = Color(android.graphics.Color.parseColor("#333333"))),
                maxLines = Int.MAX_VALUE,
                singleLine = false
            )
            Text(
                text = "Comments (Very Flexible)",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#666666")),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = "\${data.comments}",
                onValueChange = { newValue -> currentData.value = currentData.value.copy(comments = newValue) },
                placeholder = { Text("Any comments?
                This can grow very tall (up to 300pt)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(14.dp)
                    .padding(bottom = 24.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, Color(android.graphics.Color.parseColor("#E0E0E0")), RoundedCornerShape(10.dp))
                    .background(Color(android.graphics.Color.parseColor("#FFFFFF"))),
                textStyle = TextStyle(fontSize = 16.sp, color = Color(android.graphics.Color.parseColor("#333333"))),
                maxLines = Int.MAX_VALUE,
                singleLine = false
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(bottom = 24.dp)
            ) {
// TODO: Implement component type: Toggle
                Text(
                    text = "I agree to the Terms and Conditions",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#333333")),
                    modifier = Modifier.wrapContentWidth()
                )
            }
            Button(
                onClick = { viewModel.submitForm() },
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#007AFF"))
                                )
            ) {
                Text(
                    text = "Submit Form",
                    fontSize = 18.sp,
                    color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                )
            }
            Button(
                onClick = { viewModel.clearForm() },
                modifier = Modifier
                    .padding(bottom = 40.dp)
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#FFFFFF"))
                                )
            ) {
                Text(
                    text = "Clear All Fields",
                    fontSize = 18.sp,
                    color = Color(android.graphics.Color.parseColor("#FF3B30")),
                )
            }
        }
        }
    }    // >>> GENERATED_CODE_END
}