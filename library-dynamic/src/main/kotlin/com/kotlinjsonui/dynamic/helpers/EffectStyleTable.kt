package com.kotlinjsonui.dynamic.helpers

import androidx.compose.ui.graphics.Color

/**
 * `effectStyle` — the UIKit visual-effect material, as Compose draws it.
 *
 * The attribute is declared on `common`, not just on Blur, and the dynamic
 * renderer read it only inside [com.kotlinjsonui.dynamic.components.DynamicBlurViewComponent]:
 * a plain View declaring a material got nothing. C landed the codegen half
 * (`kjui_tools/lib/compose/helpers/effect_style_helper.rb`, plan 49 lane C) and
 * run 4 measured the gap as six android parity deviations — `common_effectStyle__*`
 * at distance 9–18, the fixtures newly generated for exactly this.
 *
 * The tables are C's, reproduced value for value rather than re-derived. Two
 * renderers of one layout that each decide what "Thick" means is how the three
 * platforms drifted apart on `distribution`, and this attribute has fourteen
 * spellings to disagree over.
 *
 * Compose has no material blur, so both paths spell it the same way the Blur
 * component already did: a translucent scrim under a real `Modifier.blur`.
 *
 * The fourteen spellings collapse to nine materials — the UIKit trio, the five
 * SwiftUI material names, and five `system*Material` aliases that normalise onto
 * them. That normalisation is the generated [com.kotlinjsonui.dynamic.generated.CommonAttributes.EffectStyle]
 * enum's job, so this table keys on the canonical spelling and never repeats the
 * alias list.
 */
object EffectStyleTable {

    /** The declared default, and the fallback all three converters already used. */
    const val DEFAULT = "regular"

    /** Scrim colour per material. */
    private val SCRIM: Map<String, Color> = mapOf(
        "light" to Color.White.copy(alpha = 0.4f),
        "extralight" to Color.White.copy(alpha = 0.6f),
        "dark" to Color.Black.copy(alpha = 0.4f),
        "ultrathin" to Color.White.copy(alpha = 0.3f),
        "thin" to Color.White.copy(alpha = 0.5f),
        "regular" to Color.White.copy(alpha = 0.7f),
        "thick" to Color.White.copy(alpha = 0.85f),
        "chrome" to Color.White.copy(alpha = 0.95f),
        "prominent" to Color.White.copy(alpha = 0.9f)
    )

    /** Blur radius per material, in the same five steps rjui uses in px. */
    private val BLUR_DP: Map<String, Int> = mapOf(
        "ultrathin" to 4,
        "extralight" to 4,
        "thin" to 8,
        "light" to 8,
        "regular" to 12,
        "prominent" to 12,
        "dark" to 12,
        "thick" to 16,
        "chrome" to 20
    )

    /**
     * The alias spellings the generated enum normalises away. Kept here only so
     * a RAW read — the Blur component's legacy path, and any caller that has a
     * string rather than the typed row — answers the same as the typed one.
     */
    private val ALIASES: Map<String, String> = mapOf(
        "systemultrathinmaterial" to "ultrathin",
        "systemthinmaterial" to "thin",
        "systemmaterial" to "regular",
        "systemthickmaterial" to "thick",
        "systemchromematerial" to "chrome"
    )

    /**
     * Canonical table key for a declared value. An unrecognised material falls
     * back to `regular` — C's `key_for`, and the reason a typo dims the view
     * rather than silently doing nothing.
     */
    fun keyFor(value: String?): String {
        val k = value?.trim()?.lowercase().orEmpty()
        val resolved = ALIASES[k] ?: k
        return if (SCRIM.containsKey(resolved)) resolved else DEFAULT
    }

    /** Scrim for a declared material, or null when nothing is declared. */
    fun scrim(value: String?): Color? {
        if (value.isNullOrBlank()) return null
        return SCRIM[keyFor(value)]
    }

    /** Blur radius in dp for a declared material, or null when nothing is declared. */
    fun blurDp(value: String?): Int? {
        if (value.isNullOrBlank()) return null
        return BLUR_DP[keyFor(value)]
    }
}
