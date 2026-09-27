package com.kotlinjsonui.dynamic.helpers

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

/**
 * `contentInsetAdjustmentBehavior` for the Compose scrollables.
 *
 * The attribute is UIKit's and names something Compose does not have:
 * UIScrollView adjusts its content inset for the safe area BY DEFAULT and the
 * attribute decides whether to stop it. Compose never adjusts — a LazyColumn
 * insets its content only if you hand it a `contentPadding`.
 *
 * So the concept does not port but the EFFECT does, and the effect is what the
 * declaration is about: whether the scrolled content clears the system bars.
 * Which way each value falls is therefore INVERTED from iOS — `never` is the
 * one value that needs no code here, where on iOS it is the only one that does.
 *
 * The mapping is C's (`kjui_tools/lib/compose/helpers/content_inset_helper.rb`)
 * and is reproduced rather than re-derived: the codegen and the dynamic path
 * have to inset by the same amount or the two renders of one layout disagree.
 */
object ContentInsetBehavior {

    /**
     * The safe-area padding this declaration asks for, or null when nothing
     * should be applied.
     *
     * `horizontal` picks the axis for `scrollableAxes`.
     *
     * [insetHorizontal] / [insetVertical] (a Collection's) are added to the
     * safe area, as iOS adds them — measured 2026-09-27 on sjui codegen and
     * SwiftJsonUI Dynamic: insetVertical 8 at the top of a 62pt safe area put
     * the first cell at 70 (4f ruling, round 16). The Collection dropped them
     * for the safe area until jsonui-cli 1.9.0; kjui's codegen, the other way.
     */
    @Composable
    fun safeAreaPadding(value: String?, horizontal: Boolean = false, insetHorizontal: Float? = null, insetVertical: Float? = null): PaddingValues? {
        val insets = when (value?.trim()?.lowercase()) {
            // Compose has no "depending on context", so automatic is always.
            "always", "automatic" -> WindowInsets.safeDrawing
            "scrollableaxes" -> WindowInsets.safeDrawing
                .only(if (horizontal) WindowInsetsSides.Horizontal else WindowInsetsSides.Vertical)
            // `never` — and anything undeclared — emits nothing, which is
            // Compose's own default and is what keeps every existing screen
            // exactly where it is.
            else -> return null
        }
        if (insetHorizontal == null && insetVertical == null) return insets.asPaddingValues()
        val h = (insetHorizontal ?: 0f).dp
        val v = (insetVertical ?: 0f).dp
        return insets.add(WindowInsets(left = h, top = v, right = h, bottom = v)).asPaddingValues()
    }

    /** Whether this declaration asks for an inset the caller has to apply. */
    fun adjusts(value: String?): Boolean =
        when (value?.trim()?.lowercase()) {
            "always", "automatic", "scrollableaxes" -> true
            else -> false
        }
}
