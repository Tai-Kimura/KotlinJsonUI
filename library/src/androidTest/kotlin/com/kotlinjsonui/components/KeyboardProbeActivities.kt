package com.kotlinjsonui.components

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge

/** The reporting face's window: `adjustNothing` in the manifest, edge-to-edge in code. */
class AdjustNothingEdgeToEdgeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
    }
}

/** The framework-resize window, as the control. */
class AdjustResizeActivity : ComponentActivity()

/**
 * How many 60dp fillers go above a field so that its top sits [aboveFoldDp]
 * above the fold of a list [viewportDp] tall, measured with the IME down.
 *
 * ⚠️ WHY IT IS MEASURED. The keyboard arms were written against a portrait
 * phone with the filler counts as literals. Measured 2026-09-25, the list is
 * 858dp on phone_ci (1080x2400 / 420dpi) and 744dp on conf_ci (2560x1600 /
 * 320dpi, landscape): the literal 12 left the field's top 138dp above the
 * fold on the phone and 24dp on the tablet — its centre ON the fold, where
 * the tap landed on nothing, no IME came, and the arms failed with "Condition
 * still not satisfied after 5000 ms" on the fix and on the version before it
 * alike, a red that said nothing about the library. From the list's own
 * height the count is 13 on phone_ci and 11 on conf_ci, and the field's top
 * sits 40–99dp above the fold on both.
 */
internal fun fillersToTheFold(viewportDp: Float, aboveFoldDp: Int = 40, fillerDp: Int = 60): Int =
    kotlin.math.floor((viewportDp - aboveFoldDp) / fillerDp).toInt().coerceAtLeast(0)
