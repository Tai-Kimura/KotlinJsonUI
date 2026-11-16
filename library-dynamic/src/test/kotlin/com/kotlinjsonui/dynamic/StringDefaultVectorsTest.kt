package com.kotlinjsonui.dynamic

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Drives the shared data-default vectors
 * (`src/test/resources/string_default_vectors.json`, vendored byte-identical
 * from jsonui-cli `shared/core/string_default_vectors.json`, compared by the
 * COPIES list in CI) through [applyDataSectionDefaults]: dynamic mode must
 * give each property the value the code generators write for it. The same
 * table measures sjui, kjui and rjui in jsonui-cli, and SwiftJsonUI's
 * DynamicView.
 */
class StringDefaultVectorsTest {

    private val vectors: JsonObject by lazy {
        val stream = checkNotNull(
            javaClass.classLoader?.getResourceAsStream("string_default_vectors.json")
        ) { "string_default_vectors.json missing from test resources" }
        stream.bufferedReader().use { JsonParser.parseString(it.readText()).asJsonObject }
    }

    private fun merged(className: String, defaultValue: JsonElement?): Map<String, Any> {
        val entry = JsonObject().apply {
            addProperty("name", "probe")
            addProperty("class", className)
            if (defaultValue != null && !defaultValue.isJsonNull) add("defaultValue", defaultValue)
        }
        val node = JsonObject().apply {
            addProperty("type", "View")
            add("data", com.google.gson.JsonArray().apply { add(entry) })
        }
        return applyDataSectionDefaults(node, emptyMap())
    }

    /** A JSON value as the data map holds one: numbers compared as Double. */
    private fun plain(value: Any?): Any? = when (value) {
        is Number -> value.toDouble()
        is JsonElement -> when {
            value.isJsonNull -> null
            value.isJsonPrimitive && value.asJsonPrimitive.isNumber -> value.asDouble
            value.isJsonPrimitive && value.asJsonPrimitive.isBoolean -> value.asBoolean
            value.isJsonPrimitive -> value.asString
            value.isJsonArray -> value.asJsonArray.map { plain(it) }
            else -> value.asJsonObject.entrySet().associate { (k, v) -> k to plain(v) }
        }
        is List<*> -> value.map { plain(it) }
        is Map<*, *> -> value.entries.associate { (k, v) -> k to plain(v) }
        else -> value
    }

    @Test
    fun everySpellingReadsAsItsText() {
        val rows = vectors.getAsJsonArray("spellings").map { it.asJsonObject }
        assertTrue("the table has spellings", rows.size >= 20)
        val wrong = rows.mapNotNull { row ->
            val got = merged("String", row.get("spelling"))["probe"]
            val want = row.get("text").asString
            if (got == want) null else "${row.get("name").asString}: ${row.get("spelling")} read as $got, want $want"
        }
        assertEquals(wrong.joinToString("\n"), emptyList<String>(), wrong)
    }

    // A String? default reads as a String's, and none stays absent.
    @Test
    fun everyStringOptionalRowReadsAsItsTextOrStaysAbsent() {
        val rows = vectors.getAsJsonArray("optionalStrings").map { it.asJsonObject }
        assertTrue("the table has String? rows", rows.isNotEmpty())
        val wrong = rows.mapNotNull { row ->
            val data = merged("String?", row.get("spelling"))
            val want = row.get("text")
            val ok = if (want.isJsonNull) !data.containsKey("probe") else data["probe"] == want.asString
            if (ok) null else "${row.get("name").asString}: ${row.get("spelling")} read as ${data["probe"]}, want $want"
        }
        assertEquals(wrong.joinToString("\n"), emptyList<String>(), wrong)
    }

    @Test
    fun everyValueWrittenPerPlatformGivesKotlinItsValue() {
        val rows = vectors.getAsJsonArray("platforms").map { it.asJsonObject }
        assertTrue("the table has platform rows", rows.isNotEmpty())
        val wrong = rows.mapNotNull { row ->
            val data = merged(row.get("class").asString, row.get("defaultValue"))
            val want = row.getAsJsonObject("expect").get("kotlin")
            val ok = if (want.isJsonNull) !data.containsKey("probe") else plain(data["probe"]) == plain(want)
            if (ok) null else "${row.get("name").asString}: got ${data["probe"]}, want $want"
        }
        assertEquals(wrong.joinToString("\n"), emptyList<String>(), wrong)
    }

    // The code generators' sentence (jsonui-cli, measured 2026-09-26 on
    // kjui), less the layout: one wording for the notice on every face.
    @Test
    fun theWarningIsTheGeneratorsSentence() {
        assertEquals(
            "data 'mode' defaultValue is given for swift, typescript but not kotlin — kotlin gets the String default \"\"",
            DataDefaultValue.missingPlatformWarning("mode", listOf("swift", "typescript"), "String")
        )
        assertEquals(
            "data 'n' defaultValue is given for swift but not kotlin — kotlin gets the Int default 0",
            DataDefaultValue.missingPlatformWarning("n", listOf("swift"), "Int")
        )
        assertEquals(
            "data 'd' defaultValue is given for swift but not kotlin — kotlin gets the Double default 0.0",
            DataDefaultValue.missingPlatformWarning("d", listOf("swift"), "Double")
        )
        assertEquals(
            "data 'o' defaultValue is given for swift but not kotlin — kotlin gets nil",
            DataDefaultValue.missingPlatformWarning("o", listOf("swift"), "String?")
        )
        assertEquals(
            "data 'c' defaultValue is given for swift but not kotlin — kotlin gets no default (Color has no vocabulary value)",
            DataDefaultValue.missingPlatformWarning("c", listOf("swift"), "Color")
        )
    }

    // The branch this replaced dropped every String holding `(` or `)` as if
    // it were a constructor; a constructor default of another class is still
    // not a value here.
    @Test
    fun aStringWithParenthesesIsTextAndAConstructorIsNot() {
        assertEquals("Hello (world)", merged("String", JsonParser.parseString("\"Hello (world)\""))["probe"])
        assertFalse(merged("CollectionDataSource", JsonParser.parseString("\"CollectionDataSource()\"")).containsKey("probe"))
    }
}
