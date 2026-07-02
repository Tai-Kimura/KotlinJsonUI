package com.kotlinjsonui.conformance

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * One fixture entry from conformance manifest.json (see RESULTS_SCHEMA.md in
 * the conformance suite for the full contract).
 */
data class ManifestFixture(
    val id: String,
    val clazz: String, // "assertable" | "visual"
    val aliasOf: String?,
    val platforms: List<String>,
    /** raw `mode` value: null, a string, or a list of strings */
    val modes: List<String>?,
    val layout: String,
    val test: String
) {
    val isAlias: Boolean get() = aliasOf != null

    /** true when this host (Compose dynamic mode) should execute the fixture */
    fun runsOnComposeDynamic(): Boolean = modes == null || modes.contains("compose")

    fun modeDetail(): String = "mode: ${modes?.joinToString(",") ?: "null"}"
}

data class ConformanceManifest(
    val fixtures: List<ManifestFixture>
) {
    companion object {
        private val json = Json { ignoreUnknownKeys = true; isLenient = true }

        fun parse(manifestText: String): ConformanceManifest {
            val root = json.parseToJsonElement(manifestText).jsonObject
            val fixtures = root.getValue("fixtures").jsonArray.map { element ->
                val o = element.jsonObject
                ManifestFixture(
                    id = o.getValue("id").jsonPrimitive.content,
                    clazz = o.getValue("class").jsonPrimitive.content,
                    aliasOf = o["aliasOf"]?.takeIf { it !is JsonNull }?.jsonPrimitive?.content,
                    platforms = o.getValue("platforms").jsonArray.map { it.jsonPrimitive.content },
                    modes = parseModes(o["mode"]),
                    layout = o.getValue("layout").jsonPrimitive.content,
                    test = o.getValue("test").jsonPrimitive.content
                )
            }
            return ConformanceManifest(fixtures)
        }

        private fun parseModes(element: JsonElement?): List<String>? = when {
            element == null || element is JsonNull -> null
            element is kotlinx.serialization.json.JsonArray ->
                element.map { it.jsonPrimitive.content }
            else -> listOf(element.jsonPrimitive.content)
        }
    }
}
