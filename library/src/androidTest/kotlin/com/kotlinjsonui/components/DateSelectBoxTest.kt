package com.kotlinjsonui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class DateSelectBoxTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dateSelectBox_displaysPlaceholder() {
        composeTestRule.setContent {
            DateSelectBox(
                value = "",
                onValueChange = {},
                placeholder = "Select date"
            )
        }

        composeTestRule.onNodeWithText("Select date").assertIsDisplayed()
    }

    @Test
    fun dateSelectBox_displaysFormattedDate() {
        composeTestRule.setContent {
            DateSelectBox(
                value = "2024-01-15",
                onValueChange = {},
                dateFormat = "yyyy-MM-dd"
            )
        }

        composeTestRule.onNodeWithText("2024-01-15").assertIsDisplayed()
    }

    @Test
    fun dateSelectBox_withCustomFormat() {
        composeTestRule.setContent {
            DateSelectBox(
                value = "2024-01-15",
                onValueChange = {},
                dateFormat = "MM/dd/yyyy"
            )
        }

        composeTestRule.onNodeWithText("01/15/2024").assertIsDisplayed()
    }

    @Test
    fun dateSelectBox_timeMode() {
        composeTestRule.setContent {
            DateSelectBox(
                value = "14:30",
                onValueChange = {},
                datePickerMode = "time",
                dateFormat = "HH:mm"
            )
        }

        composeTestRule.onNodeWithText("14:30").assertIsDisplayed()
    }

    @Test
    fun dateSelectBox_dateAndTimeMode() {
        composeTestRule.setContent {
            DateSelectBox(
                value = "2024-01-15 14:30",
                onValueChange = {},
                datePickerMode = "dateAndTime",
                dateFormat = "yyyy-MM-dd HH:mm"
            )
        }

        composeTestRule.onNodeWithText("2024-01-15 14:30").assertIsDisplayed()
    }

    @Test
    fun dateSelectBox_clickOpensSheet() {
        composeTestRule.setContent {
            DateSelectBox(
                value = "",
                onValueChange = {},
                placeholder = "Select date"
            )
        }

        composeTestRule.onNodeWithText("Select date").performClick()
        composeTestRule.waitForIdle()

        // Bottom sheet should be displayed (we just verify no crash)
    }

    @Test
    fun dateSelectBox_disabled() {
        composeTestRule.setContent {
            DateSelectBox(
                value = "",
                onValueChange = {},
                placeholder = "Disabled",
                enabled = false
            )
        }

        composeTestRule.onNodeWithText("Disabled").assertIsDisplayed()
    }

    @Test
    fun dateSelectBox_withCustomColors() {
        composeTestRule.setContent {
            DateSelectBox(
                value = "2024-01-15",
                onValueChange = {},
                backgroundColor = Color.LightGray,
                textColor = Color.DarkGray,
                borderColor = Color.Gray
            )
        }

        composeTestRule.onNodeWithText("2024-01-15").assertIsDisplayed()
    }

    @Test
    fun dateSelectBox_withMinMaxDates() {
        composeTestRule.setContent {
            DateSelectBox(
                value = "2024-01-15",
                onValueChange = {},
                minimumDate = "2024-01-01",
                maximumDate = "2024-12-31"
            )
        }

        composeTestRule.onNodeWithText("2024-01-15").assertIsDisplayed()
    }

    @Test
    fun dateSelectBox_wheelsStyle() {
        composeTestRule.setContent {
            DateSelectBox(
                value = "",
                onValueChange = {},
                datePickerStyle = "wheels",
                placeholder = "Wheels Style"
            )
        }

        composeTestRule.onNodeWithText("Wheels Style").assertIsDisplayed()
    }

    @Test
    fun dateSelectBox_inlineStyle() {
        composeTestRule.setContent {
            DateSelectBox(
                value = "",
                onValueChange = {},
                datePickerStyle = "inline",
                placeholder = "Inline Style"
            )
        }

        composeTestRule.onNodeWithText("Inline Style").assertIsDisplayed()
    }

    @Test
    fun dateSelectBox_compactStyle() {
        composeTestRule.setContent {
            DateSelectBox(
                value = "",
                onValueChange = {},
                datePickerStyle = "compact",
                placeholder = "Compact Style"
            )
        }

        composeTestRule.onNodeWithText("Compact Style").assertIsDisplayed()
    }

    @Test
    fun dateSelectBox_graphicalStyle() {
        composeTestRule.setContent {
            DateSelectBox(
                value = "",
                onValueChange = {},
                datePickerStyle = "graphical",
                placeholder = "Graphical Style"
            )
        }

        composeTestRule.onNodeWithText("Graphical Style").assertIsDisplayed()
    }

    @Test
    fun dateSelectBox_withMinuteInterval() {
        composeTestRule.setContent {
            DateSelectBox(
                value = "",
                onValueChange = {},
                datePickerMode = "time",
                minuteInterval = 15,
                placeholder = "15 min interval"
            )
        }

        composeTestRule.onNodeWithText("15 min interval").assertIsDisplayed()
    }
}
