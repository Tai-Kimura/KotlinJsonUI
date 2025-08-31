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
import androidx.compose.foundation.shape.CircleShape
import com.kotlinjsonui.components.CustomTextField
import com.kotlinjsonui.components.CustomTextFieldWithMargins
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
fun FormTestGeneratedView(
    data: FormTestData,
    viewModel: FormTestViewModel
) {
    // Generated Compose code from form_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "form_test",
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
                android.util.Log.e("DynamicView", "Error loading form_test: \$error")
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
                .padding(16.dp)
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
                fontSize = 28.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            Text(
                text = stringResource(R.string.form_test_personal_information),
                fontSize = 20.sp,
                color = colorResource(R.color.medium_blue),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = stringResource(R.string.form_test_first_name),
                fontSize = 14.sp,
                color = colorResource(R.color.medium_gray_4),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            CustomTextFieldWithMargins(
                value = "${data.firstName}",
                onValueChange = { newValue -> viewModel.updateData(mapOf("firstName" to newValue)) },
                boxModifier = Modifier
                    .padding(bottom = 16.dp),
                textFieldModifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                placeholder = { Text(stringResource(R.string.form_test_enter_your_first_name)) },
                shape = RoundedCornerShape(10.dp),
                backgroundColor = colorResource(R.color.white),
                borderColor = colorResource(R.color.pale_gray),
                isOutlined = true,
                textStyle = TextStyle(fontSize = 16.sp, color = colorResource(R.color.black))
            )
            Text(
                text = stringResource(R.string.form_test_last_name),
                fontSize = 14.sp,
                color = colorResource(R.color.medium_gray_4),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            CustomTextFieldWithMargins(
                value = "${data.lastName}",
                onValueChange = { newValue -> viewModel.updateData(mapOf("lastName" to newValue)) },
                boxModifier = Modifier
                    .padding(bottom = 16.dp),
                textFieldModifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                placeholder = { Text(stringResource(R.string.form_test_enter_your_last_name)) },
                shape = RoundedCornerShape(10.dp),
                backgroundColor = colorResource(R.color.white),
                borderColor = colorResource(R.color.pale_gray),
                isOutlined = true,
                textStyle = TextStyle(fontSize = 16.sp, color = colorResource(R.color.black))
            )
            Text(
                text = stringResource(R.string.form_test_email_address),
                fontSize = 14.sp,
                color = colorResource(R.color.medium_gray_4),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            CustomTextFieldWithMargins(
                value = "${data.email}",
                onValueChange = { newValue -> viewModel.updateData(mapOf("email" to newValue)) },
                boxModifier = Modifier
                    .padding(bottom = 16.dp),
                textFieldModifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                placeholder = { Text(stringResource(R.string.form_test_emailexamplecom)) },
                shape = RoundedCornerShape(10.dp),
                backgroundColor = colorResource(R.color.white),
                borderColor = colorResource(R.color.pale_gray),
                isOutlined = true,
                textStyle = TextStyle(fontSize = 16.sp, color = colorResource(R.color.black))
            )
            Text(
                text = stringResource(R.string.form_test_phone_number),
                fontSize = 14.sp,
                color = colorResource(R.color.medium_gray_4),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            CustomTextFieldWithMargins(
                value = "${data.phone}",
                onValueChange = { newValue -> viewModel.updateData(mapOf("phone" to newValue)) },
                boxModifier = Modifier
                    .padding(bottom = 24.dp),
                textFieldModifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                placeholder = { Text("+1 234 567 8900") },
                shape = RoundedCornerShape(10.dp),
                backgroundColor = colorResource(R.color.white),
                borderColor = colorResource(R.color.pale_gray),
                isOutlined = true,
                textStyle = TextStyle(fontSize = 16.sp, color = colorResource(R.color.black))
            )
            Text(
                text = stringResource(R.string.form_test_address_information),
                fontSize = 20.sp,
                color = colorResource(R.color.medium_blue),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = stringResource(R.string.form_test_street_address),
                fontSize = 14.sp,
                color = colorResource(R.color.medium_gray_4),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            CustomTextFieldWithMargins(
                value = "${data.address}",
                onValueChange = { newValue -> viewModel.updateData(mapOf("address" to newValue)) },
                boxModifier = Modifier
                    .padding(bottom = 16.dp),
                textFieldModifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                placeholder = { Text(stringResource(R.string.form_test_123_main_street)) },
                shape = RoundedCornerShape(10.dp),
                backgroundColor = colorResource(R.color.white),
                borderColor = colorResource(R.color.pale_gray),
                isOutlined = true,
                textStyle = TextStyle(fontSize = 16.sp, color = colorResource(R.color.black))
            )
            Text(
                text = stringResource(R.string.form_test_city),
                fontSize = 14.sp,
                color = colorResource(R.color.medium_gray_4),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            CustomTextFieldWithMargins(
                value = "${data.city}",
                onValueChange = { newValue -> viewModel.updateData(mapOf("city" to newValue)) },
                boxModifier = Modifier
                    .padding(bottom = 16.dp),
                textFieldModifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                placeholder = { Text(stringResource(R.string.form_test_new_york)) },
                shape = RoundedCornerShape(10.dp),
                backgroundColor = colorResource(R.color.white),
                borderColor = colorResource(R.color.pale_gray),
                isOutlined = true,
                textStyle = TextStyle(fontSize = 16.sp, color = colorResource(R.color.black))
            )
            Text(
                text = stringResource(R.string.form_test_zip_code),
                fontSize = 14.sp,
                color = colorResource(R.color.medium_gray_4),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            CustomTextFieldWithMargins(
                value = "${data.zipCode}",
                onValueChange = { newValue -> viewModel.updateData(mapOf("zipCode" to newValue)) },
                boxModifier = Modifier
                    .padding(bottom = 16.dp),
                textFieldModifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                placeholder = { Text("10001") },
                shape = RoundedCornerShape(10.dp),
                backgroundColor = colorResource(R.color.white),
                borderColor = colorResource(R.color.pale_gray),
                isOutlined = true,
                textStyle = TextStyle(fontSize = 16.sp, color = colorResource(R.color.black))
            )
            Text(
                text = stringResource(R.string.form_test_country),
                fontSize = 14.sp,
                color = colorResource(R.color.medium_gray_4),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            CustomTextFieldWithMargins(
                value = "${data.country}",
                onValueChange = { newValue -> viewModel.updateData(mapOf("country" to newValue)) },
                boxModifier = Modifier
                    .padding(bottom = 24.dp),
                textFieldModifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                placeholder = { Text(stringResource(R.string.form_test_united_states)) },
                shape = RoundedCornerShape(10.dp),
                backgroundColor = colorResource(R.color.white),
                borderColor = colorResource(R.color.pale_gray),
                isOutlined = true,
                textStyle = TextStyle(fontSize = 16.sp, color = colorResource(R.color.black))
            )
            Text(
                text = stringResource(R.string.form_test_professional_information),
                fontSize = 20.sp,
                color = colorResource(R.color.medium_blue),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = stringResource(R.string.form_test_company),
                fontSize = 14.sp,
                color = colorResource(R.color.medium_gray_4),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            CustomTextFieldWithMargins(
                value = "${data.company}",
                onValueChange = { newValue -> viewModel.updateData(mapOf("company" to newValue)) },
                boxModifier = Modifier
                    .padding(bottom = 16.dp),
                textFieldModifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                placeholder = { Text(stringResource(R.string.form_test_company_name)) },
                shape = RoundedCornerShape(10.dp),
                backgroundColor = colorResource(R.color.white),
                borderColor = colorResource(R.color.pale_gray),
                isOutlined = true,
                textStyle = TextStyle(fontSize = 16.sp, color = colorResource(R.color.black))
            )
            Text(
                text = stringResource(R.string.form_test_job_title),
                fontSize = 14.sp,
                color = colorResource(R.color.medium_gray_4),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            CustomTextFieldWithMargins(
                value = "${data.jobTitle}",
                onValueChange = { newValue -> viewModel.updateData(mapOf("jobTitle" to newValue)) },
                boxModifier = Modifier
                    .padding(bottom = 24.dp),
                textFieldModifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                placeholder = { Text(stringResource(R.string.form_test_software_engineer)) },
                shape = RoundedCornerShape(10.dp),
                backgroundColor = colorResource(R.color.white),
                borderColor = colorResource(R.color.pale_gray),
                isOutlined = true,
                textStyle = TextStyle(fontSize = 16.sp, color = colorResource(R.color.black))
            )
            Text(
                text = stringResource(R.string.form_test_additional_information),
                fontSize = 20.sp,
                color = colorResource(R.color.medium_blue),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = stringResource(R.string.form_test_bio_flexible_height),
                fontSize = 14.sp,
                color = colorResource(R.color.medium_gray_4),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            CustomTextFieldWithMargins(
                value = data.bio,
                onValueChange = { newValue -> viewModel.updateData(mapOf("bio" to newValue)) },
                boxModifier = Modifier
                    .padding(bottom = 16.dp),
                textFieldModifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = { Text("Tell us about yourself...\nThis field will grow as you type") },
                shape = RoundedCornerShape(10.dp),
                backgroundColor = colorResource(R.color.white),
                borderColor = colorResource(R.color.pale_gray),
                isOutlined = true,
                maxLines = Int.MAX_VALUE,
                singleLine = false,
                textStyle = TextStyle(fontSize = 16.sp, color = colorResource(R.color.dark_gray))
            )
            Text(
                text = stringResource(R.string.form_test_notes_fixed_height),
                fontSize = 14.sp,
                color = colorResource(R.color.medium_gray_4),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            CustomTextFieldWithMargins(
                value = data.notes,
                onValueChange = { newValue -> viewModel.updateData(mapOf("notes" to newValue)) },
                boxModifier = Modifier
                    .padding(bottom = 16.dp),
                textFieldModifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = { Text("Additional notes...\nFixed height field") },
                shape = RoundedCornerShape(10.dp),
                backgroundColor = colorResource(R.color.white),
                borderColor = colorResource(R.color.pale_gray),
                isOutlined = true,
                maxLines = Int.MAX_VALUE,
                singleLine = false,
                textStyle = TextStyle(fontSize = 16.sp, color = colorResource(R.color.dark_gray))
            )
            Text(
                text = stringResource(R.string.form_test_comments_very_flexible),
                fontSize = 14.sp,
                color = colorResource(R.color.medium_gray_4),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            CustomTextFieldWithMargins(
                value = data.comments,
                onValueChange = { newValue -> viewModel.updateData(mapOf("comments" to newValue)) },
                boxModifier = Modifier
                    .padding(bottom = 24.dp),
                textFieldModifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = { Text("Any comments?\nThis can grow very tall (up to 300pt)") },
                shape = RoundedCornerShape(10.dp),
                backgroundColor = colorResource(R.color.white),
                borderColor = colorResource(R.color.pale_gray),
                isOutlined = true,
                maxLines = Int.MAX_VALUE,
                singleLine = false,
                textStyle = TextStyle(fontSize = 16.sp, color = colorResource(R.color.dark_gray))
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(bottom = 24.dp)
            ) {
                Switch(
                    checked = data.agreeToTerms,
                    onCheckedChange = { newValue -> viewModel.updateData(mapOf("agreeToTerms" to newValue)) },
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = stringResource(R.string.form_test_i_agree_to_the_terms_and_condit),
                    fontSize = 14.sp,
                    color = colorResource(R.color.dark_gray),
                    modifier = Modifier.wrapContentWidth()
                )
            }
            Button(
                onClick = { viewModel.submitForm() },
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_blue)
                                )
            ) {
                Text(
                    text = stringResource(R.string.form_test_submit_form),
                    fontSize = 18.sp,
                    color = colorResource(R.color.white),
                )
            }
            Button(
                onClick = { viewModel.clearForm() },
                modifier = Modifier
                    .padding(bottom = 40.dp)
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.white)
                                )
            ) {
                Text(
                    text = stringResource(R.string.form_test_clear_all_fields),
                    fontSize = 18.sp,
                    color = colorResource(R.color.white),
                )
            }
        }
        }
    }    }
    // >>> GENERATED_CODE_END
}