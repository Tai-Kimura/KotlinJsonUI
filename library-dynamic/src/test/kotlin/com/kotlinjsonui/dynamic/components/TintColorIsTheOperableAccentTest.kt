package com.kotlinjsonui.dynamic.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * tintColor is the accent of what can be operated — a control's colours, the
 * links' colour, the caret — and not a text colour (4f ruling, 2026-09-26).
 * The Dynamic renderer had no general reading of it: TextView passed no
 * caret, CheckBox's checked colour did not fall back to it, and a linkable
 * Label's links took the configured link colour whatever the tint. (Radio's
 * buttons: RadioLabelAndButtonColorsTest.)
 */
class TintColorIsTheOperableAccentTest {

    private fun code(path: String) = File(path).readLines()
        .map { l -> l.trimStart().let { t -> if (t.startsWith("//") || t.startsWith("*")) "" else l } }
        .joinToString("\n")

    /** Where each path takes it, by the source. */
    @Test
    fun eachOperablePartTakesTheTint() {
        val dir = "src/main/kotlin/com/kotlinjsonui/dynamic/components/"
        // the caret: both TextView fields
        val textView = code(dir + "DynamicTextViewComponent.kt")
        assertTrue(Regex("""val cursorColor = ColorParser\.parseColorStringWithBinding\(\s*TypedAttrs\.rawString\(a\.common\.tintColor\)""")
            .containsMatchIn(textView))
        assertEquals(2, Regex("""cursorColor = cursorColor""").findAll(textView).count())
        // the checked box: after the declared and the legacy spelling
        val checkBox = code(dir + "DynamicCheckBoxComponent.kt")
        assertTrue(Regex("""TypedAttrs\.undeclared\(json, "checkColor"\)\?\.asString, data, context\s*\)\s*\?: ColorParser\.parseColorStringWithBinding\(\s*TypedAttrs\.rawString\(a\.common\.tintColor\)""")
            .containsMatchIn(checkBox))
        // the links: the linkable Label passes its tint, and the library draws
        // every link in the colour it is handed
        val text = code(dir + "DynamicTextComponent.kt")
        assertTrue(text.contains("linkColor = linkColor(a, data, context)"))
        val library = code("../library/src/main/kotlin/com/kotlinjsonui/components/PartialAttributesText.kt")
        assertEquals(3, Regex("""color = linkColor,""").findAll(library).count())
        assertEquals(0, Regex("""color = Configuration\.Colors\.linkColor""").findAll(library).count())
        assertTrue(library.contains("linkColor = linkColor.takeOrElse { Configuration.Colors.linkColor }"))
    }
}
