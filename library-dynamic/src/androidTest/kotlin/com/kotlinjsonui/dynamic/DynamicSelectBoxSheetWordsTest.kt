package com.kotlinjsonui.dynamic

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** The layout's words reach a sheet the way codegen passes them: sheetTitle, cancelButtonText, doneButtonText. */
@RunWith(AndroidJUnit4::class)
class DynamicSelectBoxSheetWordsTest {

    @get:Rule
    val rule = createComposeRule()

    private fun open(id: String) {
        rule.setContent { RenderDynamicView(layoutName = "selectbox_sheet_words_probe") }
        rule.waitUntil(timeoutMillis = 5_000) {
            rule.onAllNodes(hasTestTag(id)).fetchSemanticsNodes().isNotEmpty()
        }
        rule.onNodeWithTag(id).performClick()
        rule.waitForIdle()
    }

    @Test
    fun aListSheetTakesItsWords() {
        open("probe_list")
        rule.onNodeWithText("Pick a store").assertIsDisplayed()
        rule.onNodeWithText("Back").assertIsDisplayed()
    }

    @Test
    fun aDateSheetTakesItsWords() {
        open("probe_date")
        rule.onNodeWithText("When").assertIsDisplayed()
        rule.onNodeWithText("Not now").assertIsDisplayed()
        rule.onNodeWithText("Apply").assertIsDisplayed()
    }
}
