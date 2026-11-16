package com.kotlinjsonui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Text
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
            val state = rememberTextFieldState(initialText = "Initial Value")
            CustomTextField(state = state)
        }

        composeTestRule.onNodeWithText("Initial Value").assertIsDisplayed()
    }

    @Test
    fun customTextField_displaysPlaceholder() {
        composeTestRule.setContent {
            val state = rememberTextFieldState(initialText = "")
            CustomTextField(
                state = state,
                placeholder = { Text("Enter text here") }
            )
        }

        composeTestRule.onNodeWithText("Enter text here").assertIsDisplayed()
    }

    @Test
    fun customTextField_updatesValue() {
        composeTestRule.setContent {
            val state = rememberTextFieldState(initialText = "")
            CustomTextField(
                state = state,
                placeholder = { Text("Placeholder") }
            )
        }

        composeTestRule.onNode(hasSetTextAction()).performTextInput("Hello")
        composeTestRule.onNodeWithText("Hello").assertIsDisplayed()
    }

    @Test
    fun customTextField_outlinedStyle() {
        composeTestRule.setContent {
            val state = rememberTextFieldState(initialText = "Outlined")
            CustomTextField(
                state = state,
                isOutlined = true
            )
        }

        composeTestRule.onNodeWithText("Outlined").assertIsDisplayed()
    }

    @Test
    fun customTextField_withError() {
        composeTestRule.setContent {
            val state = rememberTextFieldState(initialText = "Error Value")
            CustomTextField(
                state = state,
                isError = true
            )
        }

        composeTestRule.onNodeWithText("Error Value").assertIsDisplayed()
    }

    @Test
    fun customTextField_singleLine() {
        composeTestRule.setContent {
            val state = rememberTextFieldState(initialText = "Single Line")
            CustomTextField(
                state = state,
                singleLine = true
            )
        }

        composeTestRule.onNodeWithText("Single Line").assertIsDisplayed()
    }

    @Test
    fun customTextField_multiLine() {
        composeTestRule.setContent {
            val state = rememberTextFieldState(initialText = "Multi Line")
            CustomTextField(
                state = state,
                singleLine = false,
                maxLines = 5
            )
        }

        composeTestRule.onNodeWithText("Multi Line").assertIsDisplayed()
    }

    @Test
    fun customTextField_withCustomBackgroundColor() {
        composeTestRule.setContent {
            val state = rememberTextFieldState(initialText = "Custom Background")
            CustomTextField(
                state = state,
                backgroundColor = Color.LightGray
            )
        }

        composeTestRule.onNodeWithText("Custom Background").assertIsDisplayed()
    }

    @Test
    fun customTextField_withModifier() {
        composeTestRule.setContent {
            val state = rememberTextFieldState(initialText = "With Modifier")
            CustomTextField(
                state = state,
                modifier = Modifier.fillMaxWidth()
            )
        }

        composeTestRule.onNodeWithText("With Modifier").assertIsDisplayed()
    }

    @Test
    fun customTextFieldWithMargins_displaysCorrectly() {
        composeTestRule.setContent {
            val state = rememberTextFieldState(initialText = "With Margins")
            CustomTextFieldWithMargins(
                state = state,
                placeholder = { Text("Placeholder") }
            )
        }

        composeTestRule.onNodeWithText("With Margins").assertIsDisplayed()
    }

    @Test
    fun customTextFieldWithMargins_outlinedStyle() {
        composeTestRule.setContent {
            val state = rememberTextFieldState(initialText = "Outlined Margins")
            CustomTextFieldWithMargins(
                state = state,
                isOutlined = true
            )
        }

        composeTestRule.onNodeWithText("Outlined Margins").assertIsDisplayed()
    }

    @Test
    fun rememberSyncedTextFieldState_syncsExternalToState() {
        composeTestRule.setContent {
            val state = rememberSyncedTextFieldState(
                externalValue = "Synced Value",
                onValueChange = {}
            )
            CustomTextField(state = state)
        }

        composeTestRule.onNodeWithText("Synced Value").assertIsDisplayed()
    }
}
