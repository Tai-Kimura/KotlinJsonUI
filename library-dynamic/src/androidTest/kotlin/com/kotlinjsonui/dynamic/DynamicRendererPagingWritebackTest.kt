package com.kotlinjsonui.dynamic

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented pin for the REAL screen-root entry: consumer apps render
 * through SafeDynamicView -> registered renderer -> RenderDynamicView,
 * which enters the JsonObject overload of DynamicView directly. The
 * two-way paging wiring shipped in 2.21.7 was green under a hand-wrapped
 * DynamicRuntimeScope (DynamicPagingBindingTest) yet dead on device,
 * because no scope existed on this path — the writer CompositionLocal
 * was null and every swipe writeback was silently dropped (a downstream
 * detail hero's dots, 2026-08-10, second filing). This test goes through
 * the registered-renderer entry itself, loading the layout from
 * androidTest assets, so the seam can never silently regress again.
 */
@RunWith(AndroidJUnit4::class)
class DynamicRendererPagingWritebackTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun swipeReachesSiblingReaderThroughRegisteredRendererEntry() {
        rule.setContent {
            RenderDynamicView(
                layoutName = "paging_writeback_probe",
                data = mapOf<String, Any>("currentPage" to 0)
            )
        }

        // The renderer loads the layout asynchronously from assets.
        rule.waitUntil(timeoutMillis = 5_000) {
            try {
                rule.onNodeWithTag("probe_page_label").assertTextEquals("0")
                true
            } catch (e: AssertionError) {
                false
            }
        }

        rule.onNodeWithTag("probe_pager").performTouchInput { swipeLeft() }
        rule.waitUntil(timeoutMillis = 5_000) {
            try {
                rule.onNodeWithTag("probe_page_label").assertTextEquals("1")
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }
}
