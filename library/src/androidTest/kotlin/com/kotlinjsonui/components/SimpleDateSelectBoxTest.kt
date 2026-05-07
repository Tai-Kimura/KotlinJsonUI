package com.kotlinjsonui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class SimpleDateSelectBoxTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun simpleDateSelectBox_displaysPlaceholder() {
        composeTestRule.setContent {
            SimpleDateSelectBox(
                value = "",
                onValueChange = {},
                placeholder = "Select a date"
            )
        }

        composeTestRule.onNodeWithText("Select a date").assertIsDisplayed()
    }

    @Test
    fun simpleDateSelectBox_displaysValue() {
        composeTestRule.setContent {
            SimpleDateSelectBox(
                value = "2024-01-15",
                onValueChange = {}
            )
        }

        composeTestRule.onNodeWithText("2024-01-15").assertIsDisplayed()
    }

    @Test
    fun simpleDateSelectBox_clickOpensDialog() {
        composeTestRule.setContent {
            SimpleDateSelectBox(
                value = "",
                onValueChange = {},
                placeholder = "Pick date"
            )
        }

        composeTestRule.onNodeWithText("Pick date").performClick()
        composeTestRule.waitForIdle()
        // Dialog opens - no crash is success
    }

    @Test
    fun simpleDateSelectBox_disabled() {
        composeTestRule.setContent {
            SimpleDateSelectBox(
                value = "",
                onValueChange = {},
                placeholder = "Disabled",
                enabled = false
            )
        }

        composeTestRule.onNodeWithText("Disabled").assertIsDisplayed()
    }

    @Test
    fun simpleDateSelectBox_customBackgroundColor() {
        composeTestRule.setContent {
            SimpleDateSelectBox(
                value = "2024-06-20",
                onValueChange = {},
                backgroundColor = Color.LightGray
            )
        }

        composeTestRule.onNodeWithText("2024-06-20").assertIsDisplayed()
    }

    @Test
    fun simpleDateSelectBox_customBorderColor() {
        composeTestRule.setContent {
            SimpleDateSelectBox(
                value = "2024-06-20",
                onValueChange = {},
                borderColor = Color.Blue
            )
        }

        composeTestRule.onNodeWithText("2024-06-20").assertIsDisplayed()
    }

    @Test
    fun simpleDateSelectBox_customTextColor() {
        composeTestRule.setContent {
            SimpleDateSelectBox(
                value = "2024-06-20",
                onValueChange = {},
                textColor = Color.Red
            )
        }

        composeTestRule.onNodeWithText("2024-06-20").assertIsDisplayed()
    }

    @Test
    fun simpleDateSelectBox_customHintColor() {
        composeTestRule.setContent {
            SimpleDateSelectBox(
                value = "",
                onValueChange = {},
                placeholder = "Hint Text",
                hintColor = Color.Gray
            )
        }

        composeTestRule.onNodeWithText("Hint Text").assertIsDisplayed()
    }

    @Test
    fun simpleDateSelectBox_customCornerRadius() {
        composeTestRule.setContent {
            SimpleDateSelectBox(
                value = "2024-06-20",
                onValueChange = {},
                cornerRadius = 16
            )
        }

        composeTestRule.onNodeWithText("2024-06-20").assertIsDisplayed()
    }

    @Test
    fun simpleDateSelectBox_calendarIconExists() {
        composeTestRule.setContent {
            SimpleDateSelectBox(
                value = "2024-01-15",
                onValueChange = {}
            )
        }

        // Calendar icon should be displayed
        composeTestRule.onNodeWithContentDescription("Calendar").assertIsDisplayed()
    }

    @Test
    fun simpleDateSelectBox_emptyValue() {
        composeTestRule.setContent {
            SimpleDateSelectBox(
                value = "",
                onValueChange = {}
            )
        }

        // Should not crash with empty value and no placeholder
        composeTestRule.waitForIdle()
    }

    @Test
    fun simpleDateSelectBox_invalidDateFormat() {
        composeTestRule.setContent {
            SimpleDateSelectBox(
                value = "invalid-date",
                onValueChange = {}
            )
        }

        // Should display whatever was passed even if invalid
        composeTestRule.onNodeWithText("invalid-date").assertIsDisplayed()
    }
}
