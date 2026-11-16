package com.kotlinjsonui.dynamic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A pager's page-change callback, counted (2.43.1):
 *
 * - The page the pager appears on is not a change: 0 calls at appearance, 1
 *   for one swipe. Through 2.43.0 the collector's first value called it with
 *   the appearance page — [0] here, [2] for a pager bound to page 2 — which
 *   iOS and web do not (ruling 2026-10-02, "only on a change";
 *   pager-page-change-callback-initial-call-differs-by-platform).
 * - A handler that sets the bound page itself — the usual one — no longer
 *   cancels a ViewModel-driven scroll: 0 -> 6 of 7 stopped on 5 with 2.43.0
 *   (calls [5]); it lands on 6 and the callback hears 6 once.
 * - A bare handler name is called, as the binding is (type string | binding).
 * - A bound page past the last page is written back as the page the pager
 *   settles on (10 of 5 -> 4; 2.43.0 kept 10, kjui codegen wrote 4).
 * - The handler the data holds now is called (2.43.0 called the one it held
 *   when the pager first composed: one set later heard nothing).
 * - A scrollTo tells the landing once ([6] for 0 -> 6 of 7; 2.43.0 told
 *   [5, 6] and wrote both back).
 */
@RunWith(AndroidJUnit4::class)
class DynamicPagerPageChangeTest {
    @get:Rule val rule = createComposeRule()

    private fun layout(onValueChange: String) = """
    {"type":"View","orientation":"vertical","child":[
      {"type":"Collection","id":"pager","layout":"horizontal","paging":true,"height":120,
       "currentPage":"@{currentPage}","onValueChange":"$onValueChange","items":"@{items}",
       "sections":[{"cell":"collection_probe_page_cell"}]},
      {"type":"Label","id":"page_label","text":"@{currentPage}"}]}
    """

    private fun waitLabel(text: String) = rule.waitUntil(5_000) {
        try { rule.onNodeWithTag("page_label").assertTextEquals(text); true } catch (e: AssertionError) { false }
    }

    /** Renders the pager on [start] with a handler that records the page and sets the bound page, as a ViewModel's does. */
    private fun show(onValueChange: String, start: Int, pages: Int, calls: MutableList<Int>): (Int) -> Unit {
        var page by mutableStateOf(start)
        val onPage: (Int) -> Unit = { calls += it; page = it }
        rule.setContent {
            DynamicRuntimeScope(mapOf("currentPage" to page, "onPage" to onPage, "items" to probeItems(pages, "collection_probe_page_cell"))) { eff ->
                DynamicView(json = JsonParser.parseString(layout(onValueChange)).asJsonObject, data = eff)
            }
        }
        rule.waitForIdle()
        return { page = it }
    }

    @Test
    fun appearanceIsNoChangeAndOneSwipeIsOne() {
        val calls = mutableListOf<Int>()
        show("@{onPage}", start = 0, pages = 5, calls = calls)
        assertEquals("at appearance", listOf<Int>(), calls.toList())
        rule.onNodeWithTag("pager").performTouchInput { swipeLeft() }
        waitLabel("1"); rule.waitForIdle()
        assertEquals("after one swipe", listOf(1), calls)
    }

    @Test
    fun aRestoredPageIsNoChange() {
        val calls = mutableListOf<Int>()
        show("@{onPage}", start = 2, pages = 5, calls = calls)
        rule.onNodeWithTag("page_label").assertTextEquals("2")
        assertEquals(listOf<Int>(), calls)
    }

    @Test
    fun aHandlerThatSetsThePageDoesNotCancelAViewModelScroll() {
        val calls = mutableListOf<Int>()
        val setPage = show("@{onPage}", start = 0, pages = 7, calls = calls)
        setPage(6)
        rule.mainClock.advanceTimeBy(5_000); rule.waitForIdle()
        rule.onNodeWithTag("page_label").assertTextEquals("6")
        assertEquals(listOf(6), calls)
    }

    @Test
    fun aBareNameIsCalled() {
        val calls = mutableListOf<Int>()
        show("onPage", start = 0, pages = 5, calls = calls)
        rule.onNodeWithTag("pager").performTouchInput { swipeLeft() }
        waitLabel("1"); rule.waitForIdle()
        assertEquals(listOf(1), calls)
    }

    @Test
    fun aBareOnItemAppearIsCalled() {
        val appeared = mutableListOf<Int>()
        val onAppear: (Int) -> Unit = { appeared += it }
        val json = """
        {"type":"View","orientation":"vertical","child":[
          {"type":"Collection","id":"list","height":300,"onItemAppear":"appeared","items":"@{items}",
           "sections":[{"cell":"collection_probe_page_cell"}]}]}
        """
        rule.setContent {
            DynamicView(json = JsonParser.parseString(json).asJsonObject,
                data = mapOf("appeared" to onAppear, "items" to probeItems(3, "collection_probe_page_cell")))
        }
        rule.waitForIdle()
        assertTrue("appeared=$appeared", appeared.contains(0))
    }

    private val scrollLayout = """
    {"type":"View","orientation":"vertical","child":[
      {"type":"Collection","id":"pager","layout":"horizontal","paging":true,"height":120,
       "currentPage":"@{currentPage}","onValueChange":"@{onPage}","scrollTo":"@{target}","items":"@{items}",
       "sections":[{"cell":"collection_probe_page_cell"}]},
      {"type":"Label","id":"page_label","text":"@{currentPage}"}]}
    """

    @Test
    fun aPagePastTheLastIsWrittenBackAsTheLast() {
        // The handler only records: the 4 must come from the binding's
        // write-back, not from a handler that sets the page (which would
        // pass without it).
        val calls = mutableListOf<Int>()
        var page by mutableStateOf(0)
        val onPage: (Int) -> Unit = { calls += it }
        rule.setContent {
            DynamicRuntimeScope(mapOf("currentPage" to page, "onPage" to onPage, "items" to probeItems(5, "collection_probe_page_cell"))) { eff ->
                DynamicView(json = JsonParser.parseString(layout("@{onPage}")).asJsonObject, data = eff)
            }
        }
        rule.waitForIdle()
        page = 10
        rule.mainClock.advanceTimeBy(5_000); rule.waitForIdle()
        rule.onNodeWithTag("page_label").assertTextEquals("4")
        assertEquals(listOf(4), calls)
    }

    @Test
    fun aHandlerSetLaterIsCalled() {
        val calls = mutableListOf<Int>()
        var handler by mutableStateOf<((Int) -> Unit)?>(null)
        var page by mutableStateOf(0)
        rule.setContent {
            val d = mutableMapOf<String, Any>("currentPage" to page, "items" to probeItems(5, "collection_probe_page_cell"))
            handler?.let { d["onPage"] = it }
            DynamicRuntimeScope(d) { eff -> DynamicView(json = JsonParser.parseString(layout("@{onPage}")).asJsonObject, data = eff) }
        }
        rule.waitForIdle()
        handler = { calls += it; page = it }
        rule.waitForIdle()
        rule.onNodeWithTag("pager").performTouchInput { swipeLeft() }
        waitLabel("1"); rule.waitForIdle()
        assertEquals(listOf(1), calls)
    }

    @Test
    fun aScrollToTellsTheLandingOnce() {
        val calls = mutableListOf<Int>()
        val written = mutableListOf<Int>()
        var page by mutableStateOf(0)
        var target by mutableStateOf<Any>("")
        val onPage: (Int) -> Unit = { calls += it }
        rule.setContent {
            DynamicRuntimeScope(mapOf("currentPage" to page, "onPage" to onPage, "target" to target, "items" to probeItems(7, "collection_probe_page_cell"))) { eff ->
                (eff["currentPage"] as? Number)?.toInt()?.let { if (written.lastOrNull() != it) written += it }
                DynamicView(json = JsonParser.parseString(scrollLayout).asJsonObject, data = eff)
            }
        }
        rule.waitForIdle()
        target = 6
        rule.mainClock.advanceTimeBy(5_000); rule.waitForIdle()
        rule.onNodeWithTag("page_label").assertTextEquals("6")
        assertEquals(listOf(6), calls)
        assertEquals(listOf(0, 6), written)
    }
}
