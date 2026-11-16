package com.kotlinjsonui.components

import androidx.compose.material3.Text
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class SafeDynamicViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun safeDynamicView_showsFallbackWhenDynamicModeInactive() {
        composeTestRule.setContent {
            SafeDynamicView(
                layoutName = "test_layout",
                fallback = { Text("Fallback Content") }
            )
        }

        // When dynamic mode is not active, fallback should be shown
        composeTestRule.onNodeWithText("Fallback Content").assertIsDisplayed()
    }

    @Test
    fun safeDynamicView_withData() {
        composeTestRule.setContent {
            SafeDynamicView(
                layoutName = "test_layout",
                data = mapOf("key" to "value"),
                fallback = { Text("Fallback") }
            )
        }

        composeTestRule.onNodeWithText("Fallback").assertIsDisplayed()
    }

    @Test
    fun safeDynamicView_withOnError() {
        var errorMessage: String? = null
        composeTestRule.setContent {
            SafeDynamicView(
                layoutName = "test_layout",
                onError = { errorMessage = it },
                fallback = { Text("Fallback") }
            )
        }

        composeTestRule.onNodeWithText("Fallback").assertIsDisplayed()
    }

    @Test
    fun safeDynamicView_withOnLoading() {
        composeTestRule.setContent {
            SafeDynamicView(
                layoutName = "test_layout",
                onLoading = { Text("Loading...") },
                fallback = { Text("Fallback") }
            )
        }

        // Since dynamic mode is inactive, fallback is shown
        composeTestRule.onNodeWithText("Fallback").assertIsDisplayed()
    }

    @Test
    fun safeDynamicView_withContent() {
        composeTestRule.setContent {
            SafeDynamicView(
                layoutName = "test_layout",
                content = { layoutName -> Text("Content: $layoutName") },
                fallback = { Text("Fallback") }
            )
        }

        composeTestRule.onNodeWithText("Fallback").assertIsDisplayed()
    }

    @Test
    fun safeDynamicView_emptyData() {
        composeTestRule.setContent {
            SafeDynamicView(
                layoutName = "test_layout",
                data = emptyMap(),
                fallback = { Text("Empty Data Fallback") }
            )
        }

        composeTestRule.onNodeWithText("Empty Data Fallback").assertIsDisplayed()
    }

    @Test
    fun safeDynamicView_emptyLayoutName() {
        composeTestRule.setContent {
            SafeDynamicView(
                layoutName = "",
                fallback = { Text("Empty Layout Fallback") }
            )
        }

        composeTestRule.onNodeWithText("Empty Layout Fallback").assertIsDisplayed()
    }
}
