package com.kotlinjsonui.dynamic.helpers

import androidx.compose.ui.text.font.FontWeight
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * The shared font vocabulary.
 *
 * `font` carries a WEIGHT name on every component that reads it and a family
 * name otherwise; `fontFamily` always names a family. Label was the only
 * component with a private copy of this logic, which is why TextView and
 * TextField had none at all (34: `TextView/font`, `TextView/fontFamily`,
 * `TextView/hintFont`, `TextField/fontFamily` all pixel-identical to their
 * controls). Resolution against `res/font` needs a Context and is covered by
 * the instrumented layer; the vocabulary itself is pinned here.
 */
class ResourceResolverFontTest {

    @Test
    fun weightSpellingsResolve() {
        assertEquals(FontWeight.Bold, ResourceResolver.fontWeightFor("bold"))
        assertEquals(FontWeight.SemiBold, ResourceResolver.fontWeightFor("semibold"))
        assertEquals(FontWeight.Medium, ResourceResolver.fontWeightFor("medium"))
        assertEquals(FontWeight.Thin, ResourceResolver.fontWeightFor("thin"))
    }

    @Test
    fun weightSpellingsAreCaseInsensitive() {
        assertEquals(FontWeight.Bold, ResourceResolver.fontWeightFor("Bold"))
        assertEquals(FontWeight.ExtraBold, ResourceResolver.fontWeightFor("HEAVY"))
    }

    @Test
    fun regularIsNormal() {
        // The codegen weight table maps both spellings onto Normal; a missing
        // `regular` row would silently drop the declaration.
        assertEquals(FontWeight.Normal, ResourceResolver.fontWeightFor("regular"))
        assertEquals(FontWeight.Normal, ResourceResolver.fontWeightFor("normal"))
    }

    @Test
    fun familyNamesAreNotWeights() {
        // This is what lets `font: "Noto Sans JP"` fall through to family
        // resolution instead of being swallowed as a weight.
        assertNull(ResourceResolver.fontWeightFor("Noto Sans JP"))
        assertNull(ResourceResolver.fontWeightFor("sans-serif"))
        assertNull(ResourceResolver.fontWeightFor(null))
    }

    @Test
    fun everyCodegenWeightSpellingIsCovered() {
        // Mirrors textview_component.rb WEIGHT_NAMES — a spelling the codegen
        // honours but the dynamic path does not is a desync by construction.
        val codegenSpellings = listOf(
            "thin", "extralight", "light", "normal", "regular", "medium",
            "semibold", "bold", "extrabold", "heavy", "black"
        )
        for (spelling in codegenSpellings) {
            assertEquals(
                "weight spelling '$spelling' is honoured by the codegen but not here",
                true,
                ResourceResolver.WEIGHT_NAMES.containsKey(spelling)
            )
        }
    }
}
