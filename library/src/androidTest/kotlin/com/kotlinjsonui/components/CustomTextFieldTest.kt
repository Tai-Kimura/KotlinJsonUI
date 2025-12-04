package com.kotlinjsonui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class CustomTextFieldTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun customTextField_displaysInitialValue() {
        composeTestRule.setContent {
            CustomTextField(
                value = "Initial Value",
                onValueChange = {}
            )
        }

        composeTestRule.onNodeWithText("Initial Value").assertIsDisplayed()
    }

    @Test
    fun customTextField_displaysPlaceholder() {
        composeTestRule.setContent {
            CustomTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Enter text here") }
            )
        }

        composeTestRule.onNodeWithText("Enter text here").assertIsDisplayed()
    }

    @Test
    fun customTextField_updatesValue() {
        composeTestRule.setContent {
            var text by remember { mutableStateOf("") }
            CustomTextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("Placeholder") }
            )
        }

        composeTestRule.onNode(hasSetTextAction()).performTextInput("Hello")
        composeTestRule.onNodeWithText("Hello").assertIsDisplayed()
    }

    @Test
    fun customTextField_outlinedStyle() {
        composeTestRule.setContent {
            CustomTextField(
                value = "Outlined",
                onValueChange = {},
                isOutlined = true
            )
        }

        composeTestRule.onNodeWithText("Outlined").assertIsDisplayed()
    }

    @Test
    fun customTextField_secureField() {
        composeTestRule.setContent {
            CustomTextField(
                value = "password",
                onValueChange = {},
                isSecure = true
            )
        }

        // Secure field should exist (password input)
        composeTestRule.onNode(hasSetTextAction()).assertExists()
    }

    @Test
    fun customTextField_withError() {
        composeTestRule.setContent {
            CustomTextField(
                value = "Error Value",
                onValueChange = {},
                isError = true
            )
        }

        composeTestRule.onNodeWithText("Error Value").assertIsDisplayed()
    }

    @Test
    fun customTextField_singleLine() {
        composeTestRule.setContent {
            CustomTextField(
                value = "Single Line",
                onValueChange = {},
                singleLine = true
            )
        }

        composeTestRule.onNodeWithText("Single Line").assertIsDisplayed()
    }

    @Test
    fun customTextField_multiLine() {
        composeTestRule.setContent {
            CustomTextField(
                value = "Multi Line",
                onValueChange = {},
                singleLine = false,
                maxLines = 5
            )
        }

        composeTestRule.onNodeWithText("Multi Line").assertIsDisplayed()
    }

    @Test
    fun customTextField_withCustomBackgroundColor() {
        composeTestRule.setContent {
            CustomTextField(
                value = "Custom Background",
                onValueChange = {},
                backgroundColor = Color.LightGray
            )
        }

        composeTestRule.onNodeWithText("Custom Background").assertIsDisplayed()
    }

    @Test
    fun customTextField_withModifier() {
        composeTestRule.setContent {
            CustomTextField(
                value = "With Modifier",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth()
            )
        }

        composeTestRule.onNodeWithText("With Modifier").assertIsDisplayed()
    }

    @Test
    fun customTextFieldWithMargins_displaysCorrectly() {
        composeTestRule.setContent {
            CustomTextFieldWithMargins(
                value = "With Margins",
                onValueChange = {},
                placeholder = { Text("Placeholder") }
            )
        }

        composeTestRule.onNodeWithText("With Margins").assertIsDisplayed()
    }

    @Test
    fun customTextFieldWithMargins_outlinedStyle() {
        composeTestRule.setContent {
            CustomTextFieldWithMargins(
                value = "Outlined Margins",
                onValueChange = {},
                isOutlined = true
            )
        }

        composeTestRule.onNodeWithText("Outlined Margins").assertIsDisplayed()
    }
}
