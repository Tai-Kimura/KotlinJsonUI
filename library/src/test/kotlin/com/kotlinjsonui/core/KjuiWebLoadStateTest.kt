package com.kotlinjsonui.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The decisions behind the `Web` attributes `onLoadFailed` and `reloadToken`
 * (ssot-web-component-has-no-load-failure-event-or-reload-trigger). The
 * WebView wiring around them needs a device; these are the parts that decide.
 */
class KjuiWebLoadStateTest {

    // --- reloadToken ------------------------------------------------------

    @Test
    fun theTokenTheViewIsCreatedWithReloadsNothing() {
        val state = KjuiWebLoadState()
        assertFalse(state.reloadTokenChanged(0))
    }

    @Test
    fun aMovedTokenReloadsOnceAndAnUnmovedOneDoesNot() {
        val state = KjuiWebLoadState()
        state.reloadTokenChanged(0)
        assertTrue(state.reloadTokenChanged(1))
        // `update` runs again on every recomposition that reads the data.
        assertFalse(state.reloadTokenChanged(1))
        assertTrue(state.reloadTokenChanged(2))
    }

    @Test
    fun aTokenThatArrivesAfterCreationIsAChange() {
        // A dynamic binding with nothing behind it yet resolves to null.
        val state = KjuiWebLoadState()
        state.reloadTokenChanged(null)
        assertTrue(state.reloadTokenChanged(0))
    }

    @Test
    fun equalValuesAreTheSameTokenWhateverInstanceCarriesThem() {
        val state = KjuiWebLoadState()
        state.reloadTokenChanged(Integer.valueOf(1000))
        assertFalse(state.reloadTokenChanged(Integer.valueOf(1000)))
    }

    // --- onLoadFailed -----------------------------------------------------

    @Test
    fun aFailureReachesTheHandlerBoundAtTheTime() {
        val state = KjuiWebLoadState()
        val calls = mutableListOf<String>()
        state.onLoadFailed = { calls += "first" }
        state.onLoadFailed = { calls += "second" } // the next `update`
        state.reportLoadFailure()
        assertEquals(listOf("second"), calls)
    }

    @Test
    fun aFailureWithNoHandlerIsANoOp() {
        KjuiWebLoadState().reportLoadFailure()
    }

    @Test
    fun onlyAMainFrameErrorIsALoadFailure() {
        assertTrue(KjuiWebLoadState.isLoadFailure(true, "net::ERR_NAME_NOT_RESOLVED"))
        assertTrue(KjuiWebLoadState.isLoadFailure(true, null))
        assertFalse(KjuiWebLoadState.isLoadFailure(false, "net::ERR_NAME_NOT_RESOLVED"))
    }

    @Test
    fun aNavigationAnotherOneReplacedIsNotALoadFailure() {
        assertFalse(KjuiWebLoadState.isLoadFailure(true, "net::ERR_ABORTED"))
    }

    @Test
    fun onlyAMainFrame4xxOr5xxIsAnHttpLoadFailure() {
        assertTrue(KjuiWebLoadState.isHttpLoadFailure(true, 400))
        assertTrue(KjuiWebLoadState.isHttpLoadFailure(true, 404))
        assertTrue(KjuiWebLoadState.isHttpLoadFailure(true, 503))
        assertFalse(KjuiWebLoadState.isHttpLoadFailure(true, 399))
        assertFalse(KjuiWebLoadState.isHttpLoadFailure(false, 404))
    }
}
