package com.kotlinjsonui.dynamic

import com.kotlinjsonui.core.Configuration
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The error view DynamicView draws where it cannot draw a node is a
 * debuggable app's, and only when the app asks for it (showErrorsInDebug,
 * true by default); a release app draws its fallback, or nothing (4f's
 * ruling R). The flag alone decided it, so a release app drew the error box.
 */
class ErrorDisplayTest {
    private val flag = Configuration.showErrorsInDebug
    private val fallback = Configuration.fallbackComponent

    @After
    fun restore() {
        Configuration.showErrorsInDebug = flag
        Configuration.fallbackComponent = fallback
        DebugDiagnostics.enabledOverride = null
    }

    @Test
    fun aDebuggableAppThatAsksDrawsTheErrorView() {
        DebugDiagnostics.enabledOverride = true
        Configuration.showErrorsInDebug = true
        assertTrue(ErrorDisplay.showsErrors(null))
        assertEquals(ErrorDisplay.Draw.ERROR_VIEW, ErrorDisplay.forUnknownType(null))
        Configuration.fallbackComponent = { _, _ -> }
        assertEquals(ErrorDisplay.Draw.ERROR_VIEW, ErrorDisplay.forUnknownType(null))
    }

    @Test
    fun aDebuggableAppThatDoesNotAskDrawsItsFallbackOrNothing() {
        DebugDiagnostics.enabledOverride = true
        Configuration.showErrorsInDebug = false
        Configuration.fallbackComponent = null
        assertEquals(ErrorDisplay.Draw.NOTHING, ErrorDisplay.forUnknownType(null))
        Configuration.fallbackComponent = { _, _ -> }
        assertEquals(ErrorDisplay.Draw.FALLBACK, ErrorDisplay.forUnknownType(null))
    }

    @Test
    fun aReleaseAppNeverDrawsTheErrorViewEvenWithTheFlagUp() {
        DebugDiagnostics.enabledOverride = false
        Configuration.showErrorsInDebug = true
        assertFalse(ErrorDisplay.showsErrors(null))
        Configuration.fallbackComponent = null
        assertEquals(ErrorDisplay.Draw.NOTHING, ErrorDisplay.forUnknownType(null))
        Configuration.fallbackComponent = { _, _ -> }
        assertEquals(ErrorDisplay.Draw.FALLBACK, ErrorDisplay.forUnknownType(null))
    }

    @Test
    fun theFlagIsReadInOnePlace() {
        val source = java.io.File("src/main/kotlin/com/kotlinjsonui/dynamic/DynamicView.kt").readText()
        assertFalse("DynamicView reads showErrorsInDebug itself", source.contains("Configuration.showErrorsInDebug"))
        assertTrue(source.contains("ErrorDisplay.showsErrors(context)"))
        assertTrue(source.contains("ErrorDisplay.forUnknownType(context)"))
    }
}
