package com.kotlinjsonui.dynamic.helpers

import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.runtime.Composable
import com.kotlinjsonui.components.SafeAreaOverlap
import com.kotlinjsonui.components.rememberSafeAreaOverlap

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
     * The safe area this declaration asks for, as the part of the window's
     * safe area the scrollable's frame OVERLAPS (UIKit's `.always`): attach
     * its `modifier` to the scrollable and pass `contentPadding(own)`. Null
     * when nothing should be applied. The same helper the codegen calls
     * (com.kotlinjsonui.components.rememberSafeAreaOverlap), so the two
     * renders of one layout inset by the same amount. [safeAreaPadding] —
     * the whole window's inset wherever the scrollable sits — drew a band of
     * the status bar's height inside a SafeAreaView (jsonui-cli ticket
     * kjui-collection-content-inset-always-adds-whole-window-insets).
     */
    @Composable
    fun overlap(value: String?, horizontal: Boolean = false): SafeAreaOverlap? =
        when (value?.trim()?.lowercase()) {
            "always", "automatic" -> rememberSafeAreaOverlap()
            "scrollableaxes" -> rememberSafeAreaOverlap(if (horizontal) WindowInsetsSides.Horizontal else WindowInsetsSides.Vertical)
            else -> null
        }

    /** Whether this declaration asks for an inset the caller has to apply. */
    fun adjusts(value: String?): Boolean =
        when (value?.trim()?.lowercase()) {
            "always", "automatic", "scrollableaxes" -> true
            else -> false
        }
}
