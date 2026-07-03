package com.example.kotlinjsonui.sample.views.form_test

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.R
import com.example.kotlinjsonui.sample.data.FormTestData
import com.example.kotlinjsonui.sample.viewmodels.FormTestViewModel
import com.kotlinjsonui.components.CustomTextField
import com.kotlinjsonui.components.CustomTextFieldWithMargins
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont

@Composable
fun FormTestGeneratedView(
    data: FormTestData,
    viewModel: FormTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from form_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "form_test",
            modifier = modifier,
            data = data.toMap(),
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
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(colorResource(R.color.white))
            .imePadding()
    ) {
        item {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp)
        ) {
            Section0(data, viewModel)
            Section1(data, viewModel)
            Section2(data, viewModel)
            Section3(data, viewModel)
            Section4(data, viewModel)
            Section5(data, viewModel)
            Section6(data, viewModel)
        }
        }
    }    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section0(
    data: FormTestData,
    viewModel: FormTestViewModel
) {
    Button(
        onClick = { data.toggleDynamicMode?.invoke() },
        modifier = Modifier
            .wrapContentWidth()
            .height(44.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(android.graphics.Color.parseColor("#5856D6")),
                            contentColor = colorResource(R.color.white)
                        )
    ) {
        val resolved_button3 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = FontWeight.Medium,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = "${data.dynamicModeStatus}",
            fontFamily = resolved_button3.family,
            fontWeight = resolved_button3.weight,
            fontSize = resolved_button3.size ?: TextUnit.Unspecified,
            fontStyle = resolved_button3.style ?: FontStyle.Normal,
        )
    }
}

@Composable
private fun Section1(
    data: FormTestData,
    viewModel: FormTestViewModel
) {
    val resolved_text24 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.Bold,
        size = 28.sp,
        italic = false
    ))
    Text(
        text = "${data.title}",
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text24.family,
        fontWeight = resolved_text24.weight,
        fontSize = resolved_text24.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text24.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 36.4.sp),
        modifier = Modifier.padding(bottom = 24.dp)
    )
}

@Composable
private fun Section2(
    data: FormTestData,
    viewModel: FormTestViewModel
) {
    val resolved_text25 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.SemiBold,
        size = 20.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.form_test_personal_information),
        color = colorResource(R.color.medium_blue),
        fontFamily = resolved_text25.family,
        fontWeight = resolved_text25.weight,
        fontSize = resolved_text25.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text25.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 26.0.sp),
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
private fun Section3(
    data: FormTestData,
    viewModel: FormTestViewModel
) {
    val resolved_text26 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.form_test_first_name),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text26.family,
        fontWeight = resolved_text26.weight,
        fontSize = resolved_text26.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text26.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
private fun Section4_0(
    data: FormTestData,
    viewModel: FormTestViewModel
) {
    Switch(
        checked = data.agreeToTerms,
        onCheckedChange = { newValue -> viewModel.updateData(mapOf("agreeToTerms" to newValue)) },
        modifier = Modifier
            .testTag("agreeToggle")
            .semantics { testTagsAsResourceId = true }
            .padding(end = 12.dp)
    )
}

@Composable
private fun Section4_1(
    data: FormTestData,
    viewModel: FormTestViewModel
) {
    val resolved_text42 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.form_test_i_agree_to_the_terms_and_condit),
        color = colorResource(R.color.dark_gray),
        fontFamily = resolved_text42.family,
        fontWeight = resolved_text42.weight,
        fontSize = resolved_text42.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text42.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier.wrapContentWidth()
    )
}

@Composable
private fun Section4(
    data: FormTestData,
    viewModel: FormTestViewModel
) {
    val textFieldState_firstName = rememberTextFieldState(initialText = data.firstName)
    LaunchedEffect(data.firstName) { if (textFieldState_firstName.text.toString() != data.firstName) textFieldState_firstName.edit { replace(0, length, data.firstName) } }
    LaunchedEffect(textFieldState_firstName.text) { val newValue = textFieldState_firstName.text.toString(); if (newValue != data.firstName) viewModel.updateData(mapOf("firstName" to newValue)) }
    val resolved_textfield1 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    CustomTextFieldWithMargins(
        state = textFieldState_firstName,
        boxModifier = Modifier
            .testTag("firstName")
            .semantics { testTagsAsResourceId = true }
            .padding(bottom = 16.dp),
        textFieldModifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        placeholder = { Text(
                            text = stringResource(R.string.form_test_enter_your_first_name),
                            color = Configuration.TextField.defaultPlaceholderColor
                        ) },
        shape = RoundedCornerShape(10.dp),
        backgroundColor = colorResource(R.color.white),
        borderColor = colorResource(R.color.pale_gray),
        isOutlined = true,
        textStyle = TextStyle(fontFamily = resolved_textfield1.family, fontWeight = resolved_textfield1.weight, fontSize = (resolved_textfield1.size ?: TextUnit.Unspecified), fontStyle = (resolved_textfield1.style ?: FontStyle.Normal), color = colorResource(R.color.black))
    )
    val resolved_text27 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.form_test_last_name),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text27.family,
        fontWeight = resolved_text27.weight,
        fontSize = resolved_text27.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text27.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier.padding(bottom = 6.dp)
    )
    val textFieldState_lastName = rememberTextFieldState(initialText = data.lastName)
    LaunchedEffect(data.lastName) { if (textFieldState_lastName.text.toString() != data.lastName) textFieldState_lastName.edit { replace(0, length, data.lastName) } }
    LaunchedEffect(textFieldState_lastName.text) { val newValue = textFieldState_lastName.text.toString(); if (newValue != data.lastName) viewModel.updateData(mapOf("lastName" to newValue)) }
    val resolved_textfield2 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    CustomTextFieldWithMargins(
        state = textFieldState_lastName,
        boxModifier = Modifier
            .testTag("lastName")
            .semantics { testTagsAsResourceId = true }
            .padding(bottom = 16.dp),
        textFieldModifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        placeholder = { Text(
                            text = stringResource(R.string.form_test_enter_your_last_name),
                            color = Configuration.TextField.defaultPlaceholderColor
                        ) },
        shape = RoundedCornerShape(10.dp),
        backgroundColor = colorResource(R.color.white),
        borderColor = colorResource(R.color.pale_gray),
        isOutlined = true,
        textStyle = TextStyle(fontFamily = resolved_textfield2.family, fontWeight = resolved_textfield2.weight, fontSize = (resolved_textfield2.size ?: TextUnit.Unspecified), fontStyle = (resolved_textfield2.style ?: FontStyle.Normal), color = colorResource(R.color.black))
    )
    val resolved_text28 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.form_test_email_address),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text28.family,
        fontWeight = resolved_text28.weight,
        fontSize = resolved_text28.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text28.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier.padding(bottom = 6.dp)
    )
    val textFieldState_email = rememberTextFieldState(initialText = data.email)
    LaunchedEffect(data.email) { if (textFieldState_email.text.toString() != data.email) textFieldState_email.edit { replace(0, length, data.email) } }
    LaunchedEffect(textFieldState_email.text) { val newValue = textFieldState_email.text.toString(); if (newValue != data.email) viewModel.updateData(mapOf("email" to newValue)) }
    val resolved_textfield3 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    CustomTextFieldWithMargins(
        state = textFieldState_email,
        boxModifier = Modifier
            .testTag("email")
            .semantics { testTagsAsResourceId = true }
            .padding(bottom = 16.dp),
        textFieldModifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        placeholder = { Text(
                            text = stringResource(R.string.form_test_emailexamplecom),
                            color = Configuration.TextField.defaultPlaceholderColor
                        ) },
        shape = RoundedCornerShape(10.dp),
        backgroundColor = colorResource(R.color.white),
        borderColor = colorResource(R.color.pale_gray),
        isOutlined = true,
        textStyle = TextStyle(fontFamily = resolved_textfield3.family, fontWeight = resolved_textfield3.weight, fontSize = (resolved_textfield3.size ?: TextUnit.Unspecified), fontStyle = (resolved_textfield3.style ?: FontStyle.Normal), color = colorResource(R.color.black))
    )
    val resolved_text29 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.form_test_phone_number),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text29.family,
        fontWeight = resolved_text29.weight,
        fontSize = resolved_text29.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text29.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier.padding(bottom = 6.dp)
    )
    val textFieldState_phone = rememberTextFieldState(initialText = data.phone)
    LaunchedEffect(data.phone) { if (textFieldState_phone.text.toString() != data.phone) textFieldState_phone.edit { replace(0, length, data.phone) } }
    LaunchedEffect(textFieldState_phone.text) { val newValue = textFieldState_phone.text.toString(); if (newValue != data.phone) viewModel.updateData(mapOf("phone" to newValue)) }
    val resolved_textfield4 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    CustomTextFieldWithMargins(
        state = textFieldState_phone,
        boxModifier = Modifier
            .testTag("phone")
            .semantics { testTagsAsResourceId = true }
            .padding(bottom = 24.dp),
        textFieldModifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        placeholder = { Text(
                            text = "+1 234 567 8900",
                            color = Configuration.TextField.defaultPlaceholderColor
                        ) },
        shape = RoundedCornerShape(10.dp),
        backgroundColor = colorResource(R.color.white),
        borderColor = colorResource(R.color.pale_gray),
        isOutlined = true,
        textStyle = TextStyle(fontFamily = resolved_textfield4.family, fontWeight = resolved_textfield4.weight, fontSize = (resolved_textfield4.size ?: TextUnit.Unspecified), fontStyle = (resolved_textfield4.style ?: FontStyle.Normal), color = colorResource(R.color.black))
    )
    val resolved_text30 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.SemiBold,
        size = 20.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.form_test_address_information),
        color = colorResource(R.color.medium_blue),
        fontFamily = resolved_text30.family,
        fontWeight = resolved_text30.weight,
        fontSize = resolved_text30.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text30.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 26.0.sp),
        modifier = Modifier.padding(bottom = 16.dp)
    )
    val resolved_text31 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.form_test_street_address),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text31.family,
        fontWeight = resolved_text31.weight,
        fontSize = resolved_text31.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text31.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier.padding(bottom = 6.dp)
    )
    val textFieldState_address = rememberTextFieldState(initialText = data.address)
    LaunchedEffect(data.address) { if (textFieldState_address.text.toString() != data.address) textFieldState_address.edit { replace(0, length, data.address) } }
    LaunchedEffect(textFieldState_address.text) { val newValue = textFieldState_address.text.toString(); if (newValue != data.address) viewModel.updateData(mapOf("address" to newValue)) }
    val resolved_textfield5 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    CustomTextFieldWithMargins(
        state = textFieldState_address,
        boxModifier = Modifier
            .testTag("address")
            .semantics { testTagsAsResourceId = true }
            .padding(bottom = 16.dp),
        textFieldModifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        placeholder = { Text(
                            text = stringResource(R.string.form_test_123_main_street),
                            color = Configuration.TextField.defaultPlaceholderColor
                        ) },
        shape = RoundedCornerShape(10.dp),
        backgroundColor = colorResource(R.color.white),
        borderColor = colorResource(R.color.pale_gray),
        isOutlined = true,
        textStyle = TextStyle(fontFamily = resolved_textfield5.family, fontWeight = resolved_textfield5.weight, fontSize = (resolved_textfield5.size ?: TextUnit.Unspecified), fontStyle = (resolved_textfield5.style ?: FontStyle.Normal), color = colorResource(R.color.black))
    )
    val resolved_text32 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.form_test_city),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text32.family,
        fontWeight = resolved_text32.weight,
        fontSize = resolved_text32.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text32.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier.padding(bottom = 6.dp)
    )
    val textFieldState_city = rememberTextFieldState(initialText = data.city)
    LaunchedEffect(data.city) { if (textFieldState_city.text.toString() != data.city) textFieldState_city.edit { replace(0, length, data.city) } }
    LaunchedEffect(textFieldState_city.text) { val newValue = textFieldState_city.text.toString(); if (newValue != data.city) viewModel.updateData(mapOf("city" to newValue)) }
    val resolved_textfield6 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    CustomTextFieldWithMargins(
        state = textFieldState_city,
        boxModifier = Modifier
            .testTag("city")
            .semantics { testTagsAsResourceId = true }
            .padding(bottom = 16.dp),
        textFieldModifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        placeholder = { Text(
                            text = stringResource(R.string.form_test_new_york),
                            color = Configuration.TextField.defaultPlaceholderColor
                        ) },
        shape = RoundedCornerShape(10.dp),
        backgroundColor = colorResource(R.color.white),
        borderColor = colorResource(R.color.pale_gray),
        isOutlined = true,
        textStyle = TextStyle(fontFamily = resolved_textfield6.family, fontWeight = resolved_textfield6.weight, fontSize = (resolved_textfield6.size ?: TextUnit.Unspecified), fontStyle = (resolved_textfield6.style ?: FontStyle.Normal), color = colorResource(R.color.black))
    )
    val resolved_text33 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.form_test_zip_code),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text33.family,
        fontWeight = resolved_text33.weight,
        fontSize = resolved_text33.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text33.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier.padding(bottom = 6.dp)
    )
    val textFieldState_zipCode = rememberTextFieldState(initialText = data.zipCode)
    LaunchedEffect(data.zipCode) { if (textFieldState_zipCode.text.toString() != data.zipCode) textFieldState_zipCode.edit { replace(0, length, data.zipCode) } }
    LaunchedEffect(textFieldState_zipCode.text) { val newValue = textFieldState_zipCode.text.toString(); if (newValue != data.zipCode) viewModel.updateData(mapOf("zipCode" to newValue)) }
    val resolved_textfield7 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    CustomTextFieldWithMargins(
        state = textFieldState_zipCode,
        boxModifier = Modifier
            .testTag("zipCode")
            .semantics { testTagsAsResourceId = true }
            .padding(bottom = 16.dp),
        textFieldModifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        placeholder = { Text(
                            text = "10001",
                            color = Configuration.TextField.defaultPlaceholderColor
                        ) },
        shape = RoundedCornerShape(10.dp),
        backgroundColor = colorResource(R.color.white),
        borderColor = colorResource(R.color.pale_gray),
        isOutlined = true,
        textStyle = TextStyle(fontFamily = resolved_textfield7.family, fontWeight = resolved_textfield7.weight, fontSize = (resolved_textfield7.size ?: TextUnit.Unspecified), fontStyle = (resolved_textfield7.style ?: FontStyle.Normal), color = colorResource(R.color.black))
    )
    val resolved_text34 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.form_test_country),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text34.family,
        fontWeight = resolved_text34.weight,
        fontSize = resolved_text34.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text34.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier.padding(bottom = 6.dp)
    )
    val textFieldState_country = rememberTextFieldState(initialText = data.country)
    LaunchedEffect(data.country) { if (textFieldState_country.text.toString() != data.country) textFieldState_country.edit { replace(0, length, data.country) } }
    LaunchedEffect(textFieldState_country.text) { val newValue = textFieldState_country.text.toString(); if (newValue != data.country) viewModel.updateData(mapOf("country" to newValue)) }
    val resolved_textfield8 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    CustomTextFieldWithMargins(
        state = textFieldState_country,
        boxModifier = Modifier
            .testTag("country")
            .semantics { testTagsAsResourceId = true }
            .padding(bottom = 24.dp),
        textFieldModifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        placeholder = { Text(
                            text = stringResource(R.string.form_test_united_states),
                            color = Configuration.TextField.defaultPlaceholderColor
                        ) },
        shape = RoundedCornerShape(10.dp),
        backgroundColor = colorResource(R.color.white),
        borderColor = colorResource(R.color.pale_gray),
        isOutlined = true,
        textStyle = TextStyle(fontFamily = resolved_textfield8.family, fontWeight = resolved_textfield8.weight, fontSize = (resolved_textfield8.size ?: TextUnit.Unspecified), fontStyle = (resolved_textfield8.style ?: FontStyle.Normal), color = colorResource(R.color.black))
    )
    val resolved_text35 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.SemiBold,
        size = 20.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.form_test_professional_information),
        color = colorResource(R.color.medium_blue),
        fontFamily = resolved_text35.family,
        fontWeight = resolved_text35.weight,
        fontSize = resolved_text35.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text35.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 26.0.sp),
        modifier = Modifier.padding(bottom = 16.dp)
    )
    val resolved_text36 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.form_test_company),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text36.family,
        fontWeight = resolved_text36.weight,
        fontSize = resolved_text36.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text36.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier.padding(bottom = 6.dp)
    )
    val textFieldState_company = rememberTextFieldState(initialText = data.company)
    LaunchedEffect(data.company) { if (textFieldState_company.text.toString() != data.company) textFieldState_company.edit { replace(0, length, data.company) } }
    LaunchedEffect(textFieldState_company.text) { val newValue = textFieldState_company.text.toString(); if (newValue != data.company) viewModel.updateData(mapOf("company" to newValue)) }
    val resolved_textfield9 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    CustomTextFieldWithMargins(
        state = textFieldState_company,
        boxModifier = Modifier
            .testTag("company")
            .semantics { testTagsAsResourceId = true }
            .padding(bottom = 16.dp),
        textFieldModifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        placeholder = { Text(
                            text = stringResource(R.string.form_test_company_name),
                            color = Configuration.TextField.defaultPlaceholderColor
                        ) },
        shape = RoundedCornerShape(10.dp),
        backgroundColor = colorResource(R.color.white),
        borderColor = colorResource(R.color.pale_gray),
        isOutlined = true,
        textStyle = TextStyle(fontFamily = resolved_textfield9.family, fontWeight = resolved_textfield9.weight, fontSize = (resolved_textfield9.size ?: TextUnit.Unspecified), fontStyle = (resolved_textfield9.style ?: FontStyle.Normal), color = colorResource(R.color.black))
    )
    val resolved_text37 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.form_test_job_title),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text37.family,
        fontWeight = resolved_text37.weight,
        fontSize = resolved_text37.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text37.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier.padding(bottom = 6.dp)
    )
    val textFieldState_jobTitle = rememberTextFieldState(initialText = data.jobTitle)
    LaunchedEffect(data.jobTitle) { if (textFieldState_jobTitle.text.toString() != data.jobTitle) textFieldState_jobTitle.edit { replace(0, length, data.jobTitle) } }
    LaunchedEffect(textFieldState_jobTitle.text) { val newValue = textFieldState_jobTitle.text.toString(); if (newValue != data.jobTitle) viewModel.updateData(mapOf("jobTitle" to newValue)) }
    val resolved_textfield10 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    CustomTextFieldWithMargins(
        state = textFieldState_jobTitle,
        boxModifier = Modifier
            .testTag("jobTitle")
            .semantics { testTagsAsResourceId = true }
            .padding(bottom = 24.dp),
        textFieldModifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        placeholder = { Text(
                            text = stringResource(R.string.form_test_software_engineer),
                            color = Configuration.TextField.defaultPlaceholderColor
                        ) },
        shape = RoundedCornerShape(10.dp),
        backgroundColor = colorResource(R.color.white),
        borderColor = colorResource(R.color.pale_gray),
        isOutlined = true,
        textStyle = TextStyle(fontFamily = resolved_textfield10.family, fontWeight = resolved_textfield10.weight, fontSize = (resolved_textfield10.size ?: TextUnit.Unspecified), fontStyle = (resolved_textfield10.style ?: FontStyle.Normal), color = colorResource(R.color.black))
    )
    val resolved_text38 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.SemiBold,
        size = 20.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.form_test_additional_information),
        color = colorResource(R.color.medium_blue),
        fontFamily = resolved_text38.family,
        fontWeight = resolved_text38.weight,
        fontSize = resolved_text38.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text38.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 26.0.sp),
        modifier = Modifier.padding(bottom = 16.dp)
    )
    val resolved_text39 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.form_test_bio_flexible_height),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text39.family,
        fontWeight = resolved_text39.weight,
        fontSize = resolved_text39.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text39.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier.padding(bottom = 6.dp)
    )
    val textFieldState_bio = rememberTextFieldState(initialText = data.bio)
    LaunchedEffect(data.bio) { if (textFieldState_bio.text.toString() != data.bio) textFieldState_bio.edit { replace(0, length, data.bio) } }
    LaunchedEffect(textFieldState_bio.text) { val newValue = textFieldState_bio.text.toString(); if (newValue != data.bio) viewModel.updateData(mapOf("bio" to newValue)) }
    val resolved_textview1 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    CustomTextFieldWithMargins(
        state = textFieldState_bio,
        boxModifier = Modifier
            .testTag("bio")
            .semantics { testTagsAsResourceId = true }
            .padding(bottom = 16.dp),
        textFieldModifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 80.dp, max = 200.dp),
        placeholder = { Text(stringResource(R.string.form_test_tell_us_about_yourself_this_fie)) },
        shape = RoundedCornerShape(10.dp),
        backgroundColor = colorResource(R.color.white),
        borderColor = colorResource(R.color.pale_gray),
        isOutlined = true,
        maxLines = Int.MAX_VALUE,
        singleLine = false,
        textStyle = TextStyle(fontFamily = resolved_textview1.family, fontWeight = resolved_textview1.weight, fontSize = (resolved_textview1.size ?: TextUnit.Unspecified), fontStyle = (resolved_textview1.style ?: FontStyle.Normal), color = colorResource(R.color.dark_gray))
    )
    val resolved_text40 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.form_test_notes_fixed_height),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text40.family,
        fontWeight = resolved_text40.weight,
        fontSize = resolved_text40.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text40.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier.padding(bottom = 6.dp)
    )
    val textFieldState_notes = rememberTextFieldState(initialText = data.notes)
    LaunchedEffect(data.notes) { if (textFieldState_notes.text.toString() != data.notes) textFieldState_notes.edit { replace(0, length, data.notes) } }
    LaunchedEffect(textFieldState_notes.text) { val newValue = textFieldState_notes.text.toString(); if (newValue != data.notes) viewModel.updateData(mapOf("notes" to newValue)) }
    val resolved_textview2 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    CustomTextFieldWithMargins(
        state = textFieldState_notes,
        boxModifier = Modifier
            .testTag("notes")
            .semantics { testTagsAsResourceId = true }
            .padding(bottom = 16.dp),
        textFieldModifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        placeholder = { Text(stringResource(R.string.form_test_additional_notes_fixed_height_f)) },
        shape = RoundedCornerShape(10.dp),
        backgroundColor = colorResource(R.color.white),
        borderColor = colorResource(R.color.pale_gray),
        isOutlined = true,
        maxLines = Int.MAX_VALUE,
        singleLine = false,
        textStyle = TextStyle(fontFamily = resolved_textview2.family, fontWeight = resolved_textview2.weight, fontSize = (resolved_textview2.size ?: TextUnit.Unspecified), fontStyle = (resolved_textview2.style ?: FontStyle.Normal), color = colorResource(R.color.dark_gray)),
        // hideOnFocused = false
    )
    val resolved_text41 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.form_test_comments_very_flexible),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text41.family,
        fontWeight = resolved_text41.weight,
        fontSize = resolved_text41.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text41.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier.padding(bottom = 6.dp)
    )
    val textFieldState_comments = rememberTextFieldState(initialText = data.comments)
    LaunchedEffect(data.comments) { if (textFieldState_comments.text.toString() != data.comments) textFieldState_comments.edit { replace(0, length, data.comments) } }
    LaunchedEffect(textFieldState_comments.text) { val newValue = textFieldState_comments.text.toString(); if (newValue != data.comments) viewModel.updateData(mapOf("comments" to newValue)) }
    val resolved_textview3 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    CustomTextFieldWithMargins(
        state = textFieldState_comments,
        boxModifier = Modifier
            .testTag("comments")
            .semantics { testTagsAsResourceId = true }
            .padding(bottom = 24.dp),
        textFieldModifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp, max = 300.dp),
        placeholder = { Text(stringResource(R.string.form_test_any_comments_this_can_grow_very)) },
        shape = RoundedCornerShape(10.dp),
        backgroundColor = colorResource(R.color.white),
        borderColor = colorResource(R.color.pale_gray),
        isOutlined = true,
        maxLines = Int.MAX_VALUE,
        singleLine = false,
        textStyle = TextStyle(fontFamily = resolved_textview3.family, fontWeight = resolved_textview3.weight, fontSize = (resolved_textview3.size ?: TextUnit.Unspecified), fontStyle = (resolved_textview3.style ?: FontStyle.Normal), color = colorResource(R.color.dark_gray))
    )
    Row(
        modifier = Modifier
            .padding(bottom = 24.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Section4_0(data, viewModel)
        Section4_1(data, viewModel)
    }
}

@Composable
private fun Section5(
    data: FormTestData,
    viewModel: FormTestViewModel
) {
    Button(
        onClick = { data.submitForm?.invoke() },
        modifier = Modifier
            .padding(bottom = 16.dp)
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.medium_blue),
                            contentColor = colorResource(R.color.white)
                        )
    ) {
        val resolved_button4 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = FontWeight.SemiBold,
            size = 18.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.form_test_submit_form),
            fontFamily = resolved_button4.family,
            fontWeight = resolved_button4.weight,
            fontSize = resolved_button4.size ?: TextUnit.Unspecified,
            fontStyle = resolved_button4.style ?: FontStyle.Normal,
        )
    }
}

@Composable
private fun Section6(
    data: FormTestData,
    viewModel: FormTestViewModel
) {
    Button(
        onClick = { data.clearForm?.invoke() },
        modifier = Modifier
            .padding(bottom = 40.dp)
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.white),
                            contentColor = colorResource(R.color.white)
                        ),
        border = BorderStroke(2.dp, colorResource(R.color.medium_red))
    ) {
        val resolved_button5 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = FontWeight.SemiBold,
            size = 18.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.form_test_clear_all_fields),
            fontFamily = resolved_button5.family,
            fontWeight = resolved_button5.weight,
            fontSize = resolved_button5.size ?: TextUnit.Unspecified,
            fontStyle = resolved_button5.style ?: FontStyle.Normal,
        )
    }
}
// >>> RESPONSIVE_HELPERS_END