package com.kotlinjsonui.core

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit

/**
 * Input structure passed to [Configuration.Font.fontProvider] describing the
 * font-related attributes captured from a JSON layout.
 *
 * `family`, `weight`, and `size` are nullable because the corresponding JSON
 * attributes (`fontFamily`, `font`/weight name, `fontSize`) are optional.
 * `italic` is always emitted (currently `false`) but is reserved for future
 * `italic` attribute support without further breaking changes.
 */
data class FontSpec(
    val family: String? = null,
    val weight: FontWeight? = null,
    val size: TextUnit? = null,
    val italic: Boolean = false
)

/**
 * Result returned by [Configuration.Font.fontProvider] (and
 * [Configuration.Font.resolve]). Each field maps directly onto the
 * corresponding `Text(...)` Composable parameter so generators can destructure
 * without any further translation.
 *
 * Fields are nullable so callers can selectively forward only what they care
 * about (e.g. fall back to `TextUnit.Unspecified` / `FontStyle.Normal` at the
 * call site).
 */
data class ResolvedFont(
    val family: FontFamily? = null,
    val weight: FontWeight? = null,
    val size: TextUnit? = null,
    val style: FontStyle? = null
)
