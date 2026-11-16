package com.kotlinjsonui.dynamic

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * A resource name that resolves to nothing should say so, in debug builds.
 *
 * `getIdentifier` returns 0 for a name the app does not ship, and every caller
 * reads 0 as "draw nothing" — so a misspelled drawable is a blank area and no
 * more. The codegen path spells the same name as `R.drawable.<name>` and fails
 * to BUILD; that asymmetry is how plan 49-G found `__control/Image__no-src`,
 * where the two paths disagreed about the same missing resource and only one
 * of them said anything.
 *
 * What this does NOT change: `resolveDrawable` still returns 0, so a
 * consumer's rendering is exactly as before. The warning is the whole change,
 * and it is gated on a debuggable build so a release build stays quiet.
 */
class UnresolvedResourceTest {

    private val seen = mutableListOf<String>()

    @Before
    fun arm() {
        seen.clear()
        UnresolvedResource.warningSink = { seen += it }
        DebugDiagnostics.enabledOverride = true
    }

    @After
    fun disarm() {
        UnresolvedResource.warningSink = null
        DebugDiagnostics.enabledOverride = null
    }

    @Test
    fun anUnresolvedNameIsReported() {
        UnresolvedResource.report("drawable", "no_such_asset", null)
        assertEquals(1, seen.size)
        assertTrue("the message should name the resource", seen[0].contains("no_such_asset"))
        assertTrue("the message should name the kind", seen[0].contains("drawable"))
    }

    @Test
    fun theSameNameIsReportedOnlyOnce() {
        // A fixture list or a scrolling collection re-composes constantly; a
        // warning per composition would bury logcat and make the signal
        // useless, which is the same reason UnappliedAttributes dedupes.
        repeat(5) { UnresolvedResource.report("drawable", "repeated_asset", null) }
        assertEquals(1, seen.count { it.contains("repeated_asset") })
    }

    @Test
    fun differentNamesAreReportedSeparately() {
        UnresolvedResource.report("drawable", "first_asset", null)
        UnresolvedResource.report("drawable", "second_asset", null)
        assertEquals(2, seen.size)
    }

    @Test
    fun aReleaseBuildStaysQuiet() {
        DebugDiagnostics.enabledOverride = false
        UnresolvedResource.report("drawable", "release_build_asset", null)
        assertEquals("a release build must not warn", 0, seen.size)
    }

    @Test
    fun theGateIsSharedWithTheUnappliedAttributeWarning() {
        // Both diagnostics answer "the layout declared something that never
        // reached the screen", and both must stay silent in a consumer's
        // release build. One gate, so the two cannot drift apart.
        DebugDiagnostics.enabledOverride = false
        assertEquals(false, DebugDiagnostics.isAppDebuggable(null))
        DebugDiagnostics.enabledOverride = true
        assertEquals(true, DebugDiagnostics.isAppDebuggable(null))
    }
}
