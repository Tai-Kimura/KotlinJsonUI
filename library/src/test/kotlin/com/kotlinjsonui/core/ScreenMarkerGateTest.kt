package com.kotlinjsonui.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * [ScreenMarker] draws in a debuggable app, or when the app declares
 * [SCREEN_MARKER_META_DATA] true, and in nothing else — a shipped app is
 * neither. Until 2.43.7 only the debuggable flag counted, so a UI test of a
 * minified, non-debuggable release-like build failed every screen with
 * marker-absent (ticket kjui-screenmarker-gated-on-debuggable-blocks-r8-ui-tests).
 */
class ScreenMarkerGateTest {

    @Test
    fun aShippedAppDrawsNoMarker() {
        assertEquals(false, screenMarkerEnabled(debuggable = false, metaDataFlag = false))
    }

    @Test
    fun aDebuggableAppDraws() {
        assertEquals(true, screenMarkerEnabled(debuggable = true, metaDataFlag = false))
    }

    @Test
    fun aReleaseLikeBuildThatDeclaresTheFlagDraws() {
        assertEquals(true, screenMarkerEnabled(debuggable = false, metaDataFlag = true))
    }

    @Test
    fun aDebuggableAppThatAlsoDeclaresTheFlagDraws() {
        assertEquals(true, screenMarkerEnabled(debuggable = true, metaDataFlag = true))
    }

    /** The flag's spelling is written in the code and in the consumer document; the two agree. */
    @Test
    fun theDocumentNamesTheFlagTheCodeReads() {
        val doc = File("../Docs/ui-tests-on-release-builds.md")
        assertTrue("$doc is missing", doc.isFile)
        val text = doc.readText()
        assertEquals("com.kotlinjsonui.SCREEN_MARKER", SCREEN_MARKER_META_DATA)
        assertTrue(
            "the document does not name android:name=\"$SCREEN_MARKER_META_DATA\"",
            text.contains("android:name=\"$SCREEN_MARKER_META_DATA\"")
        )
    }
}
