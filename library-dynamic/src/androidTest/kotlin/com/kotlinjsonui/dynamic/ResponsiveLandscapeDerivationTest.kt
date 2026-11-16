package com.kotlinjsonui.dynamic

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.WindowInfo
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This measures the DERIVATION, and only the derivation.
 *
 * Two different things wear the word "responsive" here:
 *
 *   derive   containerSize -> isLandscape          <- this file, 3 arms
 *   resolve  (sizeClass, isLandscape) -> which keys merge
 *            <- ResponsiveResolverTest, 29 arms, plain JVM
 *
 * The plan originally asked for one arm spanning both ("WindowSizeClass(900,1200)
 * x a landscape containerSize"), which mixes the subjects: WindowSizeClass plays
 * no part in the derivation. ResponsiveResolver.isWindowLandscape() is one line,
 * `containerSize.width > containerSize.height`, and it reads nothing else.
 *
 * On a device because that is where LocalWindowInfo exists -- but NOT by
 * rotating anything. Rotation is the OS deciding what containerSize to report;
 * the claim here is only that the library reads containerSize and compares the
 * two numbers. Providing the CompositionLocal keeps the arm about our comparison
 * instead of about the emulator's orientation handling.
 *
 * The key under test is the plain "landscape" one. Compound keys
 * (`<sizeClass>-landscape`) would drag the real window's size class back in, and
 * the arm would start failing or passing for reasons that belong to resolve.
 *
 * Mutations this is built to kill:
 *   1. `>` becomes `>=`            -- killed by the square case, and only by it.
 *   2. containerSize replaced by a constant -- killed by the two-sided cases,
 *      since no constant yields both outcomes.
 */
@RunWith(AndroidJUnit4::class)
class ResponsiveLandscapeDerivationTest {

    @get:Rule
    val rule = createComposeRule()

    private class FixedWindowInfo(override val containerSize: IntSize) : WindowInfo {
        override val isWindowFocused: Boolean = true
    }

    private val node: JsonObject
        get() = JsonParser.parseString(
            """
            {
              "type": "View",
              "orientation": "vertical",
              "responsive": { "landscape": { "orientation": "horizontal" } }
            }
            """.trimIndent()
        ).asJsonObject

    /** Resolve one node with containerSize forced to [size], off the UI thread's opinion. */
    private fun orientationUnder(size: IntSize): String {
        lateinit var resolved: JsonObject
        rule.setContent {
            CompositionLocalProvider(LocalWindowInfo provides FixedWindowInfo(size)) {
                resolved = resolveResponsiveNode(node)
            }
        }
        rule.waitForIdle()
        return resolved.get("orientation").asString
    }

    @Test
    fun widerThanTallDerivesLandscape() {
        assertEquals(
            "a 1200x800 window is landscape, so the landscape override must win",
            "horizontal",
            orientationUnder(IntSize(1200, 800))
        )
    }

    @Test
    fun tallerThanWideDoesNotDeriveLandscape() {
        assertEquals(
            "an 800x1200 window is portrait, so the base orientation must survive",
            "vertical",
            orientationUnder(IntSize(800, 1200))
        )
    }

    @Test
    fun squareIsNotLandscape() {
        // The whole point of this arm: `>` and `>=` differ here and nowhere else.
        assertEquals(
            "a square window is not landscape -- the comparison is strict",
            "vertical",
            orientationUnder(IntSize(1000, 1000))
        )
    }
}
