package com.kotlinjsonui.dynamic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * TabView onValueChange is told when the selected VALUE changes, once (ruling
 * 2026-10-02, "only when the value changes", as iOS): not at appearance, once
 * for another tab, not for the tab already selected, once when the view model
 * writes the bound index. Measured with 2.43.0 at the five moments below:
 * [] / [1] / [1] / [1, 0] / [1, 0] — the view model's write was never told.
 */
@RunWith(AndroidJUnit4::class)
class DynamicTabViewValueChangeTest {
    @get:Rule val rule = createComposeRule()

    private val layout = """
    {"type":"View","orientation":"vertical","child":[
      {"type":"TabView","id":"tabs","height":400,"selectedIndex":"@{tab}","onValueChange":"@{onTab}",
       "tabs":[{"title":"A"},{"title":"B"}]}]}
    """

    // Two "B"s are drawn (the tab and its content): tap the clickable one.
    private fun tap(title: String) = rule.onAllNodes(hasText(title) and hasClickAction())[0].performClick()

    private fun show(writesBack: Boolean, calls: MutableList<Int>): (Int) -> Unit {
        var tab by mutableStateOf(0)
        val onTab: (Int) -> Unit = { calls += it; if (writesBack) tab = it }
        rule.setContent {
            DynamicRuntimeScope(mapOf("tab" to tab, "onTab" to onTab)) { eff ->
                DynamicView(json = JsonParser.parseString(layout).asJsonObject, data = eff)
            }
        }
        rule.waitForIdle()
        return { tab = it }
    }

    @Test
    fun aValueChangeIsToldOnce() {
        val calls = mutableListOf<Int>()
        val setTab = show(writesBack = true, calls = calls)
        assertEquals("appearance", listOf<Int>(), calls.toList())
        tap("B"); rule.waitForIdle()
        assertEquals("another tab", listOf(1), calls.toList())
        tap("B"); rule.waitForIdle()
        assertEquals("the selected tab again", listOf(1), calls.toList())
        tap("A"); rule.waitForIdle()
        assertEquals("back", listOf(1, 0), calls.toList())
        setTab(1); rule.waitForIdle()
        assertEquals("the view model's write", listOf(1, 0, 1), calls.toList())
    }

    @Test
    fun aHandlerThatDoesNotWriteBackHearsTheTapOnce() {
        // Control: the bound value stays 0; the tap is still one change.
        val calls = mutableListOf<Int>()
        show(writesBack = false, calls = calls)
        tap("B"); rule.waitForIdle()
        assertEquals(listOf(1), calls)
    }
}
