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

    private fun merged(className: String, defaultValue: JsonElement): Map<String, Any> {
        val entry = JsonObject().apply {
            addProperty("name", "probe")
            addProperty("class", className)
            add("defaultValue", defaultValue)
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

    // The branch this replaced dropped every String holding `(` or `)` as if
    // it were a constructor; a constructor default of another class is still
    // not a value here.
    @Test
    fun aStringWithParenthesesIsTextAndAConstructorIsNot() {
        assertEquals("Hello (world)", merged("String", JsonParser.parseString("\"Hello (world)\""))["probe"])
        assertFalse(merged("CollectionDataSource", JsonParser.parseString("\"CollectionDataSource()\"")).containsKey("probe"))
    }
}
