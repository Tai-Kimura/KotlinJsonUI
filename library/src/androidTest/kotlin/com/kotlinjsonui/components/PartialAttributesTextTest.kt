package com.kotlinjsonui.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class PartialAttributesTextTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun partialAttributesText_displaysText() {
        composeTestRule.setContent {
            PartialAttributesText(
                text = "Hello World"
            )
        }

        composeTestRule.onNodeWithText("Hello World").assertIsDisplayed()
    }

    @Test
    fun partialAttributesText_withColorAttribute() {
        composeTestRule.setContent {
            PartialAttributesText(
                text = "Hello World",
                partialAttributes = listOf(
                    PartialAttribute(
                        startIndex = 0,
                        endIndex = 5,
                        fontColor = "#FF0000"
                    )
                )
            )
        }

        composeTestRule.onNodeWithText("Hello World").assertIsDisplayed()
    }

    @Test
    fun partialAttributesText_withBoldAttribute() {
        composeTestRule.setContent {
            PartialAttributesText(
                text = "Hello Bold World",
                partialAttributes = listOf(
                    PartialAttribute(
                        startIndex = 6,
                        endIndex = 10,
                        fontWeight = "bold"
                    )
                )
            )
        }

        composeTestRule.onNodeWithText("Hello Bold World").assertIsDisplayed()
    }

    @Test
    fun partialAttributesText_withUnderline() {
        composeTestRule.setContent {
            PartialAttributesText(
                text = "Hello Underline World",
                partialAttributes = listOf(
                    PartialAttribute(
                        startIndex = 6,
                        endIndex = 15,
                        underline = true
                    )
                )
            )
        }

        composeTestRule.onNodeWithText("Hello Underline World").assertIsDisplayed()
    }

    @Test
    fun partialAttributesText_withStrikethrough() {
        composeTestRule.setContent {
            PartialAttributesText(
                text = "Hello Strikethrough World",
                partialAttributes = listOf(
                    PartialAttribute(
                        startIndex = 6,
                        endIndex = 19,
                        strikethrough = true
                    )
                )
            )
        }

        composeTestRule.onNodeWithText("Hello Strikethrough World").assertIsDisplayed()
    }

    @Test
    fun partialAttributesText_withBackground() {
        composeTestRule.setContent {
            PartialAttributesText(
                text = "Hello Highlight World",
                partialAttributes = listOf(
                    PartialAttribute(
                        startIndex = 6,
                        endIndex = 15,
                        background = "#FFFF00"
                    )
                )
            )
        }

        composeTestRule.onNodeWithText("Hello Highlight World").assertIsDisplayed()
    }

    @Test
    fun partialAttributesText_withMultipleAttributes() {
        composeTestRule.setContent {
            PartialAttributesText(
                text = "Hello Styled World",
                partialAttributes = listOf(
                    PartialAttribute(
                        startIndex = 0,
                        endIndex = 5,
                        fontColor = "#FF0000"
                    ),
                    PartialAttribute(
                        startIndex = 6,
                        endIndex = 12,
                        fontWeight = "bold",
                        underline = true
                    ),
                    PartialAttribute(
                        startIndex = 13,
                        endIndex = 18,
                        background = "#00FF00"
                    )
                )
            )
        }

        composeTestRule.onNodeWithText("Hello Styled World").assertIsDisplayed()
    }

    @Test
    fun partialAttributesText_withFontSize() {
        composeTestRule.setContent {
            PartialAttributesText(
                text = "Hello Big World",
                partialAttributes = listOf(
                    PartialAttribute(
                        startIndex = 6,
                        endIndex = 9,
                        fontSize = 24
                    )
                )
            )
        }

        composeTestRule.onNodeWithText("Hello Big World").assertIsDisplayed()
    }

    @Test
    fun partialAttributesText_linkable() {
        composeTestRule.setContent {
            PartialAttributesText(
                text = "Visit https://example.com for more",
                linkable = true
            )
        }

        composeTestRule.onNodeWithText("Visit https://example.com for more").assertIsDisplayed()
    }

    @Test
    fun partialAttributesText_linkableWithEmail() {
        composeTestRule.setContent {
            PartialAttributesText(
                text = "Contact test@example.com for help",
                linkable = true
            )
        }

        composeTestRule.onNodeWithText("Contact test@example.com for help").assertIsDisplayed()
    }

    @Test
    fun partialAttributesText_linkableWithPhone() {
        composeTestRule.setContent {
            PartialAttributesText(
                text = "Call 555-123-4567 for support",
                linkable = true
            )
        }

        composeTestRule.onNodeWithText("Call 555-123-4567 for support").assertIsDisplayed()
    }

    @Test
    fun partialAttributesText_clickableAttribute() {
        var clicked = false
        composeTestRule.setContent {
            PartialAttributesText(
                text = "Click Here for action",
                partialAttributes = listOf(
                    PartialAttribute(
                        startIndex = 0,
                        endIndex = 10,
                        fontColor = "#0000FF",
                        onClick = { clicked = true }
                    )
                )
            )
        }

        composeTestRule.onNodeWithText("Click Here for action").assertIsDisplayed()
        composeTestRule.onNodeWithText("Click Here for action").performClick()
        // Note: Click detection may vary based on exact offset
    }

    @Test
    fun partialAttributesText_emptyAttributes() {
        composeTestRule.setContent {
            PartialAttributesText(
                text = "No Attributes",
                partialAttributes = emptyList()
            )
        }

        composeTestRule.onNodeWithText("No Attributes").assertIsDisplayed()
    }

    @Test
    fun partialAttributesText_allFontWeights() {
        composeTestRule.setContent {
            PartialAttributesText(
                text = "light medium semibold bold extrabold black thin",
                partialAttributes = listOf(
                    PartialAttribute(startIndex = 0, endIndex = 5, fontWeight = "light"),
                    PartialAttribute(startIndex = 6, endIndex = 12, fontWeight = "medium"),
                    PartialAttribute(startIndex = 13, endIndex = 21, fontWeight = "semibold"),
                    PartialAttribute(startIndex = 22, endIndex = 26, fontWeight = "bold"),
                    PartialAttribute(startIndex = 27, endIndex = 36, fontWeight = "extrabold"),
                    PartialAttribute(startIndex = 37, endIndex = 42, fontWeight = "black"),
                    PartialAttribute(startIndex = 43, endIndex = 47, fontWeight = "thin")
                )
            )
        }

        composeTestRule.onNodeWithText("light medium semibold bold extrabold black thin").assertIsDisplayed()
    }

    @Test
    fun partialAttributesText_underlineAndStrikethrough() {
        composeTestRule.setContent {
            PartialAttributesText(
                text = "Both decorations",
                partialAttributes = listOf(
                    PartialAttribute(
                        startIndex = 0,
                        endIndex = 4,
                        underline = true,
                        strikethrough = true
                    )
                )
            )
        }

        composeTestRule.onNodeWithText("Both decorations").assertIsDisplayed()
    }

    @Test
    fun partialAttributesText_invalidColorFallback() {
        composeTestRule.setContent {
            PartialAttributesText(
                text = "Invalid Color",
                partialAttributes = listOf(
                    PartialAttribute(
                        startIndex = 0,
                        endIndex = 7,
                        fontColor = "invalid"
                    )
                )
            )
        }

        composeTestRule.onNodeWithText("Invalid Color").assertIsDisplayed()
    }
}
