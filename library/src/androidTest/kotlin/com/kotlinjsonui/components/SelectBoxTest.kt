package com.kotlinjsonui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class SelectBoxTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun selectBox_displaysPlaceholder() {
        composeTestRule.setContent {
            SelectBox(
                value = "",
                options = listOf("Option 1", "Option 2"),
                onValueChange = {},
                placeholder = "Select an option"
            )
        }

        composeTestRule.onNodeWithText("Select an option").assertIsDisplayed()
    }

    @Test
    fun selectBox_displaysSelectedValue() {
        composeTestRule.setContent {
            SelectBox(
                value = "Option 1",
                options = listOf("Option 1", "Option 2"),
                onValueChange = {},
                placeholder = "Select an option"
            )
        }

        composeTestRule.onNodeWithText("Option 1").assertIsDisplayed()
    }

    @Test
    fun selectBox_clickOpensSheet() {
        composeTestRule.setContent {
            SelectBox(
                value = "",
                options = listOf("Option 1", "Option 2", "Option 3"),
                onValueChange = {},
                placeholder = "Select an option"
            )
        }

        // Click on the select box
        composeTestRule.onNodeWithText("Select an option").performClick()

        // Wait for the bottom sheet to appear
        composeTestRule.waitForIdle()

        // Options should be displayed in the bottom sheet
        composeTestRule.onNodeWithText("Option 1").assertIsDisplayed()
    }

    @Test
    fun selectBox_withCustomColors() {
        composeTestRule.setContent {
            SelectBox(
                value = "Test",
                options = listOf("Test"),
                onValueChange = {},
                backgroundColor = Color.Red,
                textColor = Color.White
            )
        }

        composeTestRule.onNodeWithText("Test").assertIsDisplayed()
    }

    @Test
    fun selectBox_disabled() {
        composeTestRule.setContent {
            SelectBox(
                value = "",
                options = listOf("Option 1"),
                onValueChange = {},
                placeholder = "Disabled",
                enabled = false
            )
        }

        composeTestRule.onNodeWithText("Disabled").assertIsDisplayed()
    }

    @Test
    fun selectBox_emptyOptions() {
        composeTestRule.setContent {
            SelectBox(
                value = "",
                options = emptyList(),
                onValueChange = {},
                placeholder = "No options"
            )
        }

        composeTestRule.onNodeWithText("No options").assertIsDisplayed()
    }

    @Test
    fun selectBox_withCornerRadius() {
        composeTestRule.setContent {
            SelectBox(
                value = "Test",
                options = listOf("Test"),
                onValueChange = {},
                cornerRadius = 16
            )
        }

        composeTestRule.onNodeWithText("Test").assertIsDisplayed()
    }
}
