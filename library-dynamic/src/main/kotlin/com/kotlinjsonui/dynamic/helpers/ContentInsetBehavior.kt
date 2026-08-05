package com.kotlinjsonui.dynamic.helpers

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.Composable

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
     */
    @Composable
    fun safeAreaPadding(value: String?, horizontal: Boolean = false): PaddingValues? =
        when (value?.trim()?.lowercase()) {
            // Compose has no "depending on context", so automatic is always.
            "always", "automatic" -> WindowInsets.safeDrawing.asPaddingValues()
            "scrollableaxes" -> WindowInsets.safeDrawing
                .only(if (horizontal) WindowInsetsSides.Horizontal else WindowInsetsSides.Vertical)
                .asPaddingValues()
            // `never` — and anything undeclared — emits nothing, which is
            // Compose's own default and is what keeps every existing screen
            // exactly where it is.
            else -> null
        }

    /** Whether this declaration asks for an inset the caller has to apply. */
    fun adjusts(value: String?): Boolean =
        when (value?.trim()?.lowercase()) {
            "always", "automatic", "scrollableaxes" -> true
            else -> false
        }
}
