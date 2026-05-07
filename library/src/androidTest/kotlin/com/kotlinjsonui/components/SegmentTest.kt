package com.kotlinjsonui.components

import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class SegmentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun segment_displaysAllTabs() {
        composeTestRule.setContent {
            Segment(selectedTabIndex = 0) {
                Tab(selected = true, onClick = {}) {
                    Text("Tab 1")
                }
                Tab(selected = false, onClick = {}) {
                    Text("Tab 2")
                }
                Tab(selected = false, onClick = {}) {
                    Text("Tab 3")
                }
            }
        }

        composeTestRule.onNodeWithText("Tab 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tab 2").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tab 3").assertIsDisplayed()
    }

    @Test
    fun segment_withCustomColors() {
        composeTestRule.setContent {
            Segment(
                selectedTabIndex = 0,
                containerColor = Color.Red,
                contentColor = Color.White
            ) {
                Tab(selected = true, onClick = {}) {
                    Text("Custom Color Tab")
                }
            }
        }

        composeTestRule.onNodeWithText("Custom Color Tab").assertIsDisplayed()
    }

    @Test
    fun segment_whenDisabled() {
        composeTestRule.setContent {
            Segment(
                selectedTabIndex = 0,
                enabled = false
            ) {
                Tab(selected = true, onClick = {}) {
                    Text("Disabled Tab")
                }
            }
        }

        composeTestRule.onNodeWithText("Disabled Tab").assertIsDisplayed()
    }

    @Test
    fun segment_withIndicatorColor() {
        composeTestRule.setContent {
            Segment(
                selectedTabIndex = 0,
                indicatorColor = Color.Blue
            ) {
                Tab(selected = true, onClick = {}) {
                    Text("With Indicator")
                }
            }
        }

        composeTestRule.onNodeWithText("With Indicator").assertIsDisplayed()
    }

    @Test
    fun segment_secondTabSelected() {
        composeTestRule.setContent {
            Segment(selectedTabIndex = 1) {
                Tab(selected = false, onClick = {}) {
                    Text("First")
                }
                Tab(selected = true, onClick = {}) {
                    Text("Second")
                }
            }
        }

        composeTestRule.onNodeWithText("First").assertIsDisplayed()
        composeTestRule.onNodeWithText("Second").assertIsDisplayed()
    }
}
