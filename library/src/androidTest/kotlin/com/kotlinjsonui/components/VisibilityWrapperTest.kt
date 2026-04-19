package com.kotlinjsonui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import org.junit.Rule
import org.junit.Test

class VisibilityWrapperTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun visibilityWrapper_visibleString_showsContent() {
        composeTestRule.setContent {
            VisibilityWrapper(visibility = "visible") {
                Text("Visible Content")
            }
        }

        composeTestRule.onNodeWithText("Visible Content").assertIsDisplayed()
    }

    @Test
    fun visibilityWrapper_goneString_hidesContent() {
        composeTestRule.setContent {
            VisibilityWrapper(visibility = "gone") {
                Text("Gone Content")
            }
        }

        composeTestRule.onNodeWithText("Gone Content").assertDoesNotExist()
    }

    @Test
    fun visibilityWrapper_invisibleString_rendersButNotVisible() {
        composeTestRule.setContent {
            VisibilityWrapper(visibility = "invisible") {
                Text("Invisible Content")
            }
        }

        // The node exists in the tree but with alpha 0
        composeTestRule.onNodeWithText("Invisible Content").assertExists()
    }

    @Test
    fun visibilityWrapper_hiddenFalse_showsContent() {
        composeTestRule.setContent {
            VisibilityWrapper(hidden = false) {
                Text("Not Hidden Content")
            }
        }

        composeTestRule.onNodeWithText("Not Hidden Content").assertIsDisplayed()
    }

    @Test
    fun visibilityWrapper_hiddenTrue_hidesContent() {
        composeTestRule.setContent {
            VisibilityWrapper(hidden = true) {
                Text("Hidden Content")
            }
        }

        composeTestRule.onNodeWithText("Hidden Content").assertDoesNotExist()
    }

    @Test
    fun visibilityWrapper_combinedHiddenTakePrecedence() {
        composeTestRule.setContent {
            VisibilityWrapper(visibility = "visible", hidden = true) {
                Text("Should Be Hidden")
            }
        }

        // hidden=true takes precedence over visibility="visible"
        composeTestRule.onNodeWithText("Should Be Hidden").assertDoesNotExist()
    }

    @Test
    fun visibilityWrapper_combinedNotHiddenUsesVisibility() {
        composeTestRule.setContent {
            VisibilityWrapper(visibility = "gone", hidden = false) {
                Text("Gone by Visibility")
            }
        }

        composeTestRule.onNodeWithText("Gone by Visibility").assertDoesNotExist()
    }

    @Test
    fun visibilityWrapper_nullVisibilityDefaultsToVisible() {
        composeTestRule.setContent {
            VisibilityWrapper(visibility = null, hidden = null) {
                Text("Default Visible")
            }
        }

        composeTestRule.onNodeWithText("Default Visible").assertIsDisplayed()
    }

    @Test
    fun visibilityWrapper_unknownVisibilityDefaultsToVisible() {
        composeTestRule.setContent {
            VisibilityWrapper(visibility = "unknown") {
                Text("Unknown Visibility")
            }
        }

        composeTestRule.onNodeWithText("Unknown Visibility").assertIsDisplayed()
    }

    @Test
    fun visibilityWrapper_caseInsensitiveGONE() {
        composeTestRule.setContent {
            VisibilityWrapper(visibility = "GONE") {
                Text("Case Insensitive Gone")
            }
        }

        composeTestRule.onNodeWithText("Case Insensitive Gone").assertDoesNotExist()
    }

    @Test
    fun visibilityWrapper_caseInsensitiveINVISIBLE() {
        composeTestRule.setContent {
            VisibilityWrapper(visibility = "INVISIBLE") {
                Text("Case Insensitive Invisible")
            }
        }

        composeTestRule.onNodeWithText("Case Insensitive Invisible").assertExists()
    }

    @Test
    fun visibilityWrapper_withModifier() {
        composeTestRule.setContent {
            VisibilityWrapper(
                visibility = "visible",
                modifier = Modifier.size(100.dp)
            ) {
                Text("With Modifier")
            }
        }

        composeTestRule.onNodeWithText("With Modifier").assertIsDisplayed()
    }
}
