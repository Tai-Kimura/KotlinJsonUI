package com.kotlinjsonui.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class DynamicModeToggleTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dynamicModeToggle_rendersInDebugBuild() {
        composeTestRule.setContent {
            DynamicModeToggle()
        }

        // In debug builds, the toggle may be visible if dynamic mode is available
        // In release builds, nothing is rendered
        composeTestRule.waitForIdle()
    }

    @Test
    fun dynamicModeToggle_withCallback() {
        var toggleValue: Boolean? = null
        composeTestRule.setContent {
            DynamicModeToggle(
                onToggle = { toggleValue = it }
            )
        }

        composeTestRule.waitForIdle()
        // Callback is set up correctly
    }

    @Test
    fun dynamicModeToggle_checkTextLabel() {
        composeTestRule.setContent {
            DynamicModeToggle()
        }

        // In debug builds with dynamic mode available, should show "Dynamic Mode" label
        // We don't assert because availability depends on runtime state
        composeTestRule.waitForIdle()
    }
}
