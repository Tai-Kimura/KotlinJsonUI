package com.kotlinjsonui.dynamic.components

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

/**
 * The Radio's labels and buttons, by the source: every path draws its labels
 * through the one RadioLabel and its buttons with the shared colours
 * (RadioLabelAndButtonColorsTest pins what those are).
 */
class RadioEveryPathTakesTheStyleTest {

    /**
     * Every label and every button on every path takes them: each `Text(`
     * in the component is the one RadioLabel, and every RadioButton passes
     * the shared colours. Code only — a comment does not count. Both sides
     * of the scan, on specimens: a bare Text and an uncoloured RadioButton
     * are caught, the shared ones pass.
     */
    @Test
    fun everyLabelAndButtonOnEveryPathTakesThem() {
        fun calls(code: String, head: String): List<String> {
            val out = mutableListOf<String>()
            // at a word boundary: `itemText(` is not a Text call
            fun next(from: Int): Int {
                var k = code.indexOf(head, from)
                while (k > 0 && (code[k - 1].isLetterOrDigit() || code[k - 1] == '_')) k = code.indexOf(head, k + 1)
                return k
            }
            var i = next(0)
            while (i >= 0) {
                var depth = 0; var j = i + head.length - 1
                while (j < code.length) {
                    if (code[j] == '(') depth++
                    if (code[j] == ')') { depth--; if (depth == 0) break }
                    j++
                }
                out += code.substring(i, j + 1)
                i = next(j)
            }
            return out
        }
        fun bareTexts(code: String) = calls(code, "Text(").filterNot { t ->
            // RadioLabel's own Text, which takes the style
            "color = style.color" in t && "fontSize = style.size" in t
        }.filterNot { it.startsWith("RadioLabel") }
        fun uncoloured(code: String) = calls(code, "RadioButton(").filterNot { "colors = " in it }
        // the scan tells them apart
        assertEquals(1, bareTexts("""Text(text = label)""").size)
        assertEquals(1, bareTexts("""Text(text = item, color = textColor)""").size)
        assertEquals(0, bareTexts("""RadioLabel(text = item, style = labelStyle)""").size)
        assertEquals(0, bareTexts("""val t = itemText(a)""").size)
        assertEquals(1, uncoloured("""RadioButton(selected = s, onClick = { f(x) }, enabled = e)""").size)
        assertEquals(0, uncoloured("""RadioButton(selected = s, onClick = { f(x) }, colors = colors)""").size)

        val code = File("src/main/kotlin/com/kotlinjsonui/dynamic/components/DynamicRadioComponent.kt").readLines()
            .map { l -> l.trimStart().let { t -> if (t.startsWith("//") || t.startsWith("*")) "" else l } }
            .joinToString("\n")
            .replace(Regex("""\bRadioLabel\("""), "RL(") // not a Text( call
        assertEquals("labels drawn without the declared style", emptyList<String>(), bareTexts(code))
        assertEquals("buttons drawn without the shared colours", emptyList<String>(), uncoloured(code))
        assertEquals(4, calls(code, "RadioButton(").size)
        assertEquals(4, Regex("""\bRL\(text = """).findAll(code).count())
    }
}
