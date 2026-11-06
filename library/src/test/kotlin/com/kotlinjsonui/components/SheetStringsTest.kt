package com.kotlinjsonui.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/**
 * The words of the SelectBox and DateSelectBox sheets were Japanese on every screen. They are resources now: English
 * by default, the app's language where the library has it, the same Japanese as before on a Japanese screen.
 */
class SheetStringsTest {
    private val res = File("src/main/res")

    private fun read(dir: String): Map<String, String> {
        val file = File(res, "$dir/kjui_sheet_strings.xml")
        assertTrue("$file is there", file.isFile)
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
        val nodes = doc.getElementsByTagName("string")
        return (0 until nodes.length).associate {
            val node = nodes.item(it)
            node.attributes.getNamedItem("name").nodeValue to node.textContent
        }
    }

    @Test
    fun everyLanguageHasEveryWord() {
        val keys = read("values").keys
        assertEquals(12, keys.size)
        DIRECTORIES.forEach { assertEquals(it, keys, read(it).keys) }
        assertEquals("no directory is missing from this list", DIRECTORIES.toSet() + "values",
            res.list()!!.filter { File(res, "$it/kjui_sheet_strings.xml").isFile }.toSet())
    }

    @Test
    fun aJapaneseScreenReadsAsItDidBefore() {
        assertEquals(
            mapOf(
                "kjui_sheet_select_option" to "選択してください", "kjui_sheet_cancel" to "キャンセル",
                "kjui_sheet_confirm" to "確定", "kjui_sheet_select" to "選択", "kjui_sheet_select_date" to "日付を選択",
                "kjui_sheet_select_time" to "時刻を選択", "kjui_sheet_select_date_and_time" to "日付と時刻を選択",
                "kjui_sheet_year" to "年", "kjui_sheet_month" to "月", "kjui_sheet_day" to "日",
                "kjui_sheet_hour" to "時", "kjui_sheet_minute" to "分",
            ),
            read("values-ja"),
        )
    }

    @Test
    fun theDefaultIsNotJapanese() {
        read("values").forEach { (name, text) -> assertFalse("$name: $text", JAPANESE.containsMatchIn(text)) }
    }

    @Test
    fun theTwoScriptsAreTwoLanguages() {
        assertEquals("請選擇", read("values-b+zh+Hant")["kjui_sheet_select_option"])
        assertEquals("请选择", read("values-b+zh+Hans")["kjui_sheet_select_option"])
    }

    /** The month names of a format that says 月 are that format's, not the screen's language. */
    @Test
    fun theComponentsSpellNoWordOfTheirOwn() {
        listOf("SelectBox.kt", "DateSelectBox.kt").forEach { name ->
            val offenders = File("src/main/kotlin/com/kotlinjsonui/components/$name").readLines().filter { line ->
                val code = line.substringBefore("//")
                !code.contains("月") && Regex("\"[^\"]*\"").findAll(code).any { JAPANESE.containsMatchIn(it.value) }
            }
            assertEquals(name, emptyList<String>(), offenders)
        }
    }

    private companion object {
        val JAPANESE = Regex("[\\u3040-\\u30ff\\u4e00-\\u9fff]")
        val DIRECTORIES = listOf(
            "values-ja", "values-de", "values-fr", "values-nl", "values-da", "values-pl", "values-cs", "values-b+es+419",
            "values-es", "values-pt-rBR", "values-pt-rPT", "values-b+zh+Hant", "values-b+zh+Hans", "values-ko",
        )
    }
}
