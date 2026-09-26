package com.kotlinjsonui.dynamic.helpers

import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale

/**
 * What a `contentMode` draws, for every image this runtime draws: Image,
 * NetworkImage and CircleImage (an Image spelling — type_synonyms.json
 * render_as — that follows its contentMode, as on iOS and web).
 *
 * The same table as kjui's codegen (content_scale_helper.rb): `fit` /
 * `AspectFit` fit, `AspectFill` crops, `fill` / `ScaleToFill` stretch, and the
 * positional modes draw unscaled and aligned. Case-insensitive; the caller
 * passes the spelling as declared (a binding already resolved).
 *
 * No contentMode — and a spelling this table does not know — draws the
 * declared default: `fit` (shared/core/attribute_semantics.json
 * `semantics.image.defaultContentMode`, the 2026-08-03 user ruling). The unit
 * test reads that value from a vendored copy of the file and requires
 * `scale(null) == scale(declared)`, so a changed ruling turns it red until
 * this table follows. Image and NetworkImage each carried a copy of this
 * `when`; CircleImage had none and drew ContentScale.Crop whatever its
 * contentMode (4f ruling, 2026-09-26, jsonui-cli 1.9.0).
 */
internal object ImageContentScale {

    fun scale(mode: String?): ContentScale = when (mode?.lowercase()) {
        "fit", "aspectfit" -> ContentScale.Fit
        "aspectfill" -> ContentScale.Crop
        "fill", "scaletofill" -> ContentScale.FillBounds
        // Positional modes draw unscaled (UIKit contentMode positions).
        "center", "top", "bottom", "left", "right" -> ContentScale.None
        else -> ContentScale.Fit
    }

    fun alignment(mode: String?): Alignment = when (mode?.lowercase()) {
        "top" -> Alignment.TopCenter
        "bottom" -> Alignment.BottomCenter
        "left" -> Alignment.CenterStart
        "right" -> Alignment.CenterEnd
        else -> Alignment.Center
    }
}
