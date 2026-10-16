package com.kotlinjsonui.conformance

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
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
    val test: String,
    /**
     * The component that HOSTS this fixture, which is not the same as the
     * component under test. `__control/Web` has `component == "__control"`
     * and `host == "Web"`, so keying on the component misses every control.
     * The iOS host measured that: host=="Web" gives 5 fixtures where
     * component=="Web" gives 4.
     */
    val host: String?,
    /**
     * `state.afterFirstDraw.name` (jsonui-cli INTERACTIVE_HOST_CONTRACT.md
     * "afterFirstDraw"): the host fires the change after the first draw; the
     * suite waits for its stamp and the landing before the screenshot.
     */
    val afterFirstDraw: String? = null,
    /** `testBridge` (INTERACTIVE_HOST_CONTRACT.md §6), or null. */
    val testBridge: TestBridgeSpec? = null
) {
    val isAlias: Boolean get() = aliasOf != null

    /** true when this host (Compose dynamic mode) should execute the fixture */
    fun runsOnComposeDynamic(): Boolean = modes == null || modes.contains("compose")

    fun modeDetail(): String = "mode: ${modes?.joinToString(",") ?: "null"}"
}

/**
 * A fixture's `testBridge`: the payload values for its screen (absent on a
 * fixture that only reads `state`), the receipt the screen must give, and
 * whether the host remounts it after the receipt check.
 */
data class TestBridgeSpec(
    val initialState: JsonObject?,
    val receiptApplied: Set<String>?,
    val receiptRejected: Set<Pair<String, String>>?,
    val remount: Boolean
)

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
                    test = o.getValue("test").jsonPrimitive.content,
                    host = o["host"]?.takeIf { it !is JsonNull }?.jsonPrimitive?.content,
                    afterFirstDraw = (o["state"] as? JsonObject)?.get("afterFirstDraw")
                        ?.let { it as? JsonObject }?.get("name")?.jsonPrimitive?.content,
                    testBridge = (o["testBridge"] as? JsonObject)?.let { tb ->
                        val receipt = tb["receipt"] as? JsonObject
                        TestBridgeSpec(
                            initialState = tb["initialState"] as? JsonObject,
                            receiptApplied = receipt?.get("applied")?.jsonArray?.map { it.jsonPrimitive.content }?.toSet(),
                            receiptRejected = receipt?.get("rejected")?.jsonArray?.map {
                                val r = it.jsonObject
                                r.getValue("key").jsonPrimitive.content to r.getValue("reason").jsonPrimitive.content
                            }?.toSet(),
                            remount = (tb["remount"] as? kotlinx.serialization.json.JsonPrimitive)?.content == "true"
                        )
                    }
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
