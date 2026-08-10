package com.kotlinjsonui.dynamic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.assertTextEquals
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonParser
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented pin for the two-way `currentPage` binding on a paging
 * Collection (a downstream detail hero's image carousel + page dots,
 * 2026-08-10): the codegen face writes every swipe back into view state,
 * so sibling `@{currentPage}` readers follow; the dynamic face must do
 * the same through the layout root's DynamicRuntimeScope.
 */
@RunWith(AndroidJUnit4::class)
class DynamicPagingBindingTest {

    @get:Rule
    val rule = createComposeRule()

    private val layoutJson = """
        {
          "type": "View",
          "orientation": "vertical",
          "child": [
            {
              "type": "Collection",
              "id": "pager",
              "layout": "horizontal",
              "paging": true,
              "height": 120,
              "currentPage": "@{currentPage}",
              "cell": { "type": "Label", "text": "page" }
            },
            {
              "type": "Label",
              "id": "page_label",
              "text": "@{currentPage}"
            }
          ]
        }
    """.trimIndent()

    @Test
    fun swipeWritesCurrentPageBackAndUpstreamChangeStillWins() {
        var upstreamPage by mutableStateOf(0)

        rule.setContent {
            val data = mapOf<String, Any>("currentPage" to upstreamPage)
            DynamicRuntimeScope(data) { effectiveData ->
                DynamicView(
                    json = JsonParser.parseString(layoutJson).asJsonObject,
                    data = effectiveData
                )
            }
        }

        rule.onNodeWithTag("page_label").assertTextEquals("0")

        // Leg 3 (pager -> binding): a swipe must reach the sibling reader.
        rule.onNodeWithTag("pager").performTouchInput { swipeLeft() }
        rule.waitUntil(timeoutMillis = 5_000) {
            try {
                rule.onNodeWithTag("page_label").assertTextEquals("1")
                true
            } catch (e: AssertionError) {
                false
            }
        }

        // Authority (data map) moves: the stale override must NOT shadow it.
        upstreamPage = 3
        rule.waitUntil(timeoutMillis = 5_000) {
            try {
                rule.onNodeWithTag("page_label").assertTextEquals("3")
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }
}
