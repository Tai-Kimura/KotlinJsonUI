package com.example.kotlinjsonui.sample.views.secure_field_test

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SecureTextField
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
import com.example.kotlinjsonui.sample.data.SecureFieldTestData
import com.example.kotlinjsonui.sample.viewmodels.SecureFieldTestViewModel
import com.kotlinjsonui.components.CustomTextField
import com.kotlinjsonui.components.CustomTextFieldWithMargins
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont

@Composable
fun SecureFieldTestGeneratedView(
    data: SecureFieldTestData,
    viewModel: SecureFieldTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from secure_field_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "secure_field_test",
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
                android.util.Log.e("DynamicView", "Error loading secure_field_test: \$error")
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
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(colorResource(R.color.white))
    ) {
        Section0(data, viewModel)
        Section1(data, viewModel)
        Section2(data, viewModel)
        Section3(data, viewModel)
        Section4(data, viewModel)
    }    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section0(
    data: SecureFieldTestData,
    viewModel: SecureFieldTestViewModel
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
        val resolved_button20 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = FontWeight.Medium,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = "${data.dynamicModeStatus}",
            fontFamily = resolved_button20.family,
            fontWeight = resolved_button20.weight,
            fontSize = resolved_button20.size ?: TextUnit.Unspecified,
            fontStyle = resolved_button20.style ?: FontStyle.Normal,
        )
    }
}

@Composable
private fun Section1(
    data: SecureFieldTestData,
    viewModel: SecureFieldTestViewModel
) {
    val resolved_text297 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 24.sp,
        italic = false
    ))
    Text(
        text = "${data.title}",
        color = colorResource(R.color.black),
        fontFamily = resolved_text297.family,
        fontWeight = resolved_text297.weight,
        fontSize = resolved_text297.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text297.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 31.2.sp),
        modifier = Modifier
            .testTag("title_label")
            .semantics { testTagsAsResourceId = true }
            .padding(top = 20.dp)
            .wrapContentWidth()
            .wrapContentHeight()
    )
}

@Composable
private fun Section2(
    data: SecureFieldTestData,
    viewModel: SecureFieldTestViewModel
) {
    val resolved_text298 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.secure_field_test_regular_textfield_not_secure),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text298.family,
        fontWeight = resolved_text298.weight,
        fontSize = resolved_text298.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text298.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier.padding(top = 30.dp)
    )
}

@Composable
private fun Section3(
    data: SecureFieldTestData,
    viewModel: SecureFieldTestViewModel
) {
    val textFieldState_regular_field = rememberTextFieldState(initialText = data.regularText)
    LaunchedEffect(data.regularText) { if (textFieldState_regular_field.text.toString() != data.regularText) textFieldState_regular_field.edit { replace(0, length, data.regularText) } }
    LaunchedEffect(textFieldState_regular_field.text) { val newValue = textFieldState_regular_field.text.toString(); if (newValue != data.regularText) viewModel.updateData(mapOf("regularText" to newValue)) }
    val resolved_textfield12 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    CustomTextFieldWithMargins(
        state = textFieldState_regular_field,
        boxModifier = Modifier
            .testTag("regular_field")
            .semantics { testTagsAsResourceId = true }
            .padding(top = 10.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp),
        textFieldModifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        placeholder = { Text(
                        text = stringResource(R.string.secure_field_test_enter_regular_text),
                        color = Configuration.TextField.defaultPlaceholderColor
                    ) },
        shape = RoundedCornerShape(8.dp),
        backgroundColor = colorResource(R.color.white),
        borderColor = colorResource(R.color.pale_gray_4),
        isOutlined = true,
        textStyle = TextStyle(fontFamily = resolved_textfield12.family, fontWeight = resolved_textfield12.weight, fontSize = (resolved_textfield12.size ?: TextUnit.Unspecified), fontStyle = (resolved_textfield12.style ?: FontStyle.Normal), color = colorResource(R.color.black))
    )
    val resolved_text299 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.secure_field_test_secure_textfield_password),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text299.family,
        fontWeight = resolved_text299.weight,
        fontSize = resolved_text299.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text299.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier.padding(top = 20.dp)
    )
    val textFieldState_password_field = rememberTextFieldState(initialText = data.password)
    LaunchedEffect(data.password) { if (textFieldState_password_field.text.toString() != data.password) textFieldState_password_field.edit { replace(0, length, data.password) } }
    LaunchedEffect(textFieldState_password_field.text) { val newValue = textFieldState_password_field.text.toString(); if (newValue != data.password) viewModel.updateData(mapOf("password" to newValue)) }
    val resolved_textfield13 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    CustomTextFieldWithMargins(
        state = textFieldState_password_field,
        boxModifier = Modifier
            .testTag("password_field")
            .semantics { testTagsAsResourceId = true }
            .padding(top = 10.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp),
        textFieldModifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        placeholder = { Text(
                        text = stringResource(R.string.secure_field_test_enter_password),
                        color = Configuration.TextField.defaultPlaceholderColor
                    ) },
        shape = RoundedCornerShape(8.dp),
        backgroundColor = colorResource(R.color.white),
        borderColor = colorResource(R.color.pale_gray_4),
        isOutlined = true,
        isSecure = true,
        textStyle = TextStyle(fontFamily = resolved_textfield13.family, fontWeight = resolved_textfield13.weight, fontSize = (resolved_textfield13.size ?: TextUnit.Unspecified), fontStyle = (resolved_textfield13.style ?: FontStyle.Normal), color = colorResource(R.color.black))
    )
    val resolved_text300 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.secure_field_test_confirm_password_also_secure),
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text300.family,
        fontWeight = resolved_text300.weight,
        fontSize = resolved_text300.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text300.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier.padding(top = 20.dp)
    )
    val textFieldState_confirm_password_field = rememberTextFieldState(initialText = data.confirmPassword)
    LaunchedEffect(data.confirmPassword) { if (textFieldState_confirm_password_field.text.toString() != data.confirmPassword) textFieldState_confirm_password_field.edit { replace(0, length, data.confirmPassword) } }
    LaunchedEffect(textFieldState_confirm_password_field.text) { val newValue = textFieldState_confirm_password_field.text.toString(); if (newValue != data.confirmPassword) viewModel.updateData(mapOf("confirmPassword" to newValue)) }
    val resolved_textfield14 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 16.sp,
        italic = false
    ))
    CustomTextFieldWithMargins(
        state = textFieldState_confirm_password_field,
        boxModifier = Modifier
            .testTag("confirm_password_field")
            .semantics { testTagsAsResourceId = true }
            .padding(top = 10.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp),
        textFieldModifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        placeholder = { Text(
                        text = stringResource(R.string.secure_field_test_confirm_password),
                        color = Configuration.TextField.defaultPlaceholderColor
                    ) },
        shape = RoundedCornerShape(8.dp),
        backgroundColor = colorResource(R.color.white),
        borderColor = colorResource(R.color.pale_gray_4),
        isOutlined = true,
        isSecure = true,
        textStyle = TextStyle(fontFamily = resolved_textfield14.family, fontWeight = resolved_textfield14.weight, fontSize = (resolved_textfield14.size ?: TextUnit.Unspecified), fontStyle = (resolved_textfield14.style ?: FontStyle.Normal), color = colorResource(R.color.black))
    )
}

@Composable
private fun Section4(
    data: SecureFieldTestData,
    viewModel: SecureFieldTestViewModel
) {
    Column(
        modifier = Modifier
            .padding(top = 30.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(8.dp))
            .background(colorResource(R.color.pale_gray))
            .padding(15.dp)
    ) {
        val resolved_text301 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = FontWeight.Bold,
            size = 14.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.secure_field_test_values_entered),
            color = colorResource(R.color.dark_gray),
            fontFamily = resolved_text301.family,
            fontWeight = resolved_text301.weight,
            fontSize = resolved_text301.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text301.style ?: FontStyle.Normal,
            style = TextStyle(lineHeight = 18.2.sp),
            modifier = Modifier
        )
        val resolved_text302 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 12.sp,
            italic = false
        ))
        Text(
            text = "${data.regularText}",
            color = colorResource(R.color.medium_gray_4),
            fontFamily = resolved_text302.family,
            fontWeight = resolved_text302.weight,
            fontSize = resolved_text302.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text302.style ?: FontStyle.Normal,
            style = TextStyle(lineHeight = 15.6.sp),
            modifier = Modifier.padding(top = 5.dp)
        )
        val resolved_text303 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 12.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.secure_field_test_password_hidden),
            color = colorResource(R.color.medium_gray_4),
            fontFamily = resolved_text303.family,
            fontWeight = resolved_text303.weight,
            fontSize = resolved_text303.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text303.style ?: FontStyle.Normal,
            style = TextStyle(lineHeight = 15.6.sp),
            modifier = Modifier.padding(top = 5.dp)
        )
        val resolved_text304 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = 12.sp,
            italic = false
        ))
        Text(
            text = stringResource(R.string.secure_field_test_confirm_hidden),
            color = colorResource(R.color.medium_gray_4),
            fontFamily = resolved_text304.family,
            fontWeight = resolved_text304.weight,
            fontSize = resolved_text304.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text304.style ?: FontStyle.Normal,
            style = TextStyle(lineHeight = 15.6.sp),
            modifier = Modifier.padding(top = 5.dp)
        )
    }
}
// >>> RESPONSIVE_HELPERS_END