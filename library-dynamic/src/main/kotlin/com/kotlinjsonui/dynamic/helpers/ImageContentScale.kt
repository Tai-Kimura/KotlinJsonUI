package com.kotlinjsonui.dynamic.helpers

import com.kotlinjsonui.core.DeclaredSpelling
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

    /**
     * [mode] as declared ([declared]: the node's own section's
     * `ContentMode.declaredSpellings` — Image, NetworkImage; CircleImage is
     * an Image synonym): another case is no spelling of a mode, and draws the
     * default (DeclaredSpelling, jsonui-cli 1.9.0).
     */
    fun scale(mode: String?, declared: Collection<String>): ContentScale = when (DeclaredSpelling.lowered(mode, declared)) {
        "fit", "aspectfit" -> ContentScale.Fit
        "aspectfill" -> ContentScale.Crop
        "fill", "scaletofill" -> ContentScale.FillBounds
        // Positional modes draw unscaled (UIKit contentMode positions).
        "center", "top", "bottom", "left", "right" -> ContentScale.None
        else -> ContentScale.Fit
    }

    fun alignment(mode: String?, declared: Collection<String>): Alignment = when (DeclaredSpelling.lowered(mode, declared)) {
        "top" -> Alignment.TopCenter
        "bottom" -> Alignment.BottomCenter
        "left" -> Alignment.CenterStart
        "right" -> Alignment.CenterEnd
        else -> Alignment.Center
    }
}
