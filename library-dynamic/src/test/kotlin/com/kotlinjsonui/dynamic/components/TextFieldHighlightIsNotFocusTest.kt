package com.kotlinjsonui.dynamic.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * highlightBackground is the background while `highlighted` holds, which only
 * a View declares (4f's ruling, jsonui-cli 1.9.0). TextField and TextView read
 * it as their focused background (CustomTextField's highlightBackgroundColor),
 * a meaning no other path gave it — kjui's codegen stopped with jsonui-cli
 * 6f37b5c2, and SwiftJsonUI's Dynamic never read it there. They hand
 * CustomTextField the node's background only, as the codegen does, so the
 * focused colour is CustomTextField's own default.
 *
 * By the source: what the components hand CustomTextField is inside a
 * composable. Code only — a comment does not count. Both sides, on
 * specimens: the old reading is caught, the new one is not.
 */
class TextFieldHighlightIsNotFocusTest {

    private val reads = Regex("""a\.common\.highlightBackground\b|highlightBackgroundColor\s*=""")

    private fun code(name: String) =
        File("src/main/kotlin/com/kotlinjsonui/dynamic/components/$name.kt").readLines()
            .map { l -> l.trimStart().let { t -> if (t.startsWith("//") || t.startsWith("*")) "" else l } }
            .joinToString("\n")

    @Test
    fun theScanTellsTheReadingApart() {
        assertTrue(reads.containsMatchIn("TypedAttrs.rawString(a.common.highlightBackground), data, context"))
        assertTrue(reads.containsMatchIn("highlightBackgroundColor = highlightBackgroundColor,"))
        assertTrue(!reads.containsMatchIn("val backgroundColor = DynamicTextFieldComponent.fieldBackground(a.common, data, context)"))
    }

    @Test
    fun aTextFieldAndATextViewHandNoFocusColour() {
        for (name in listOf("DynamicTextFieldComponent", "DynamicTextViewComponent")) {
            val found = reads.findAll(code(name)).map { it.value }.toList()
            assertEquals("$name reads highlightBackground as its focus colour", emptyList<String>(), found)
            // and still hands CustomTextField its background
            assertTrue(name, Regex("""backgroundColor = backgroundColor,""").findAll(code(name)).count() >= 2)
        }
    }
}
