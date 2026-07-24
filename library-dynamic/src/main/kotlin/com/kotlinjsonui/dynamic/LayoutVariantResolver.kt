package com.kotlinjsonui.dynamic

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable

/**
 * Responsive variant-file support (`home@regular.json`).
 *
 * A variant file replaces the WHOLE layout tree of its base screen when
 * the current window size-class tier matches its suffix. Resolution per
 * tier X: `<base>@<X>` when that layout exists, otherwise the base — no
 * cross-tier fallback. The v1 file-suffix vocabulary is
 * compact / medium / regular (landscape stays inline-`responsive`).
 *
 * Old library versions never probe for `@` files, so a project shipping
 * variants degrades gracefully to the base layout on outdated runtimes.
 */
object LayoutVariantResolver {
    val VALID_CLASSES = listOf("compact", "medium", "regular")

    /** Strip a variant suffix from a layout name: "home@regular" → "home". */
    fun baseOf(layoutName: String): String {
        val idx = layoutName.lastIndexOf('@')
        return if (idx > 0) layoutName.substring(0, idx) else layoutName
    }

    /** True when the layout name carries an `@` variant suffix. */
    fun isVariant(layoutName: String): Boolean =
        layoutName.substringAfterLast('/').contains('@')
}

/**
 * Current size-class tier ("compact" / "medium" / "regular") from the
 * window width — the same 600/840dp breakpoints the inline `responsive`
 * resolution uses (ResponsiveResolver.widthSizeClassKey).
 */
@Composable
fun currentSizeClassTier(): String {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    return ResponsiveResolver.widthSizeClassKey(windowSizeClass)
}
