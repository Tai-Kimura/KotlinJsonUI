package com.kotlinjsonui.conformance

import android.graphics.Rect
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.StaleObjectException
import com.kotlinjsonui.conformance.ScreenMarkerProbeTest.Companion.BoundsRead
import com.kotlinjsonui.conformance.ScreenMarkerProbeTest.Companion.readBounds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith

/**
 * ScreenMarkerProbeTest.readBounds with stand-ins for the find and the read,
 * so the race that failed CI run 37630851587 (a node replaced between
 * `findObject` and `visibleBounds`, right after a scroll) is made on demand.
 * Not opt-in: it touches no screen.
 */
@RunWith(AndroidJUnit4::class)
class ScreenMarkerProbeStaleReadTest {

    private val rect = Rect(1, 2, 31, 42)

    @Test
    fun aReadThatGoesStaleOnceIsFoundAgainAndRead() {
        var finds = 0
        var reads = 0
        val result = readBounds({ finds++; "handle$finds" }) { handle ->
            reads++
            if (handle == "handle1") throw StaleObjectException() else rect
        }
        assertEquals(BoundsRead.Read(rect), result)
        assertEquals("the object is found again after the stale read", 2, finds)
        assertEquals(2, reads)
    }

    @Test
    fun aReadThatStaysStaleIsReportedStaleAndDoesNotThrow() {
        var finds = 0
        val result = readBounds({ finds++; "handle" }) { throw StaleObjectException() }
        assertEquals(BoundsRead.Stale, result)
        assertEquals("one find again, not a loop", 2, finds)
    }

    @Test
    fun anObjectNotFoundIsMissing() {
        assertEquals(BoundsRead.Missing, readBounds<String>({ null }) { rect })
    }

    @Test
    fun aReadThatSucceedsIsReadOnce() {
        var finds = 0
        assertEquals(BoundsRead.Read(rect), readBounds({ finds++; "handle" }) { rect })
        assertEquals(1, finds)
    }

    @Test
    fun anotherFailureIsNotAbsorbed() {
        // Caught by name: an unrelated runtime failure still fails the probe.
        assertThrows(IllegalStateException::class.java) {
            readBounds({ "handle" }) { throw IllegalStateException("not a stale node") }
        }
    }
}
