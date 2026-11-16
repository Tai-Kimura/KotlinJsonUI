package com.kotlinjsonui.dynamic

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.components.DynamicEmbedComponent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/**
 * Executes the SHARED binding-resolution test vectors
 * (`src/test/resources/binding_vectors.json` — vendored byte-identical
 * from jsonui-cli `shared/core/binding_vectors.json`; the canonical
 * semantics live in `shared/core/binding_semantics.json`).
 *
 * Case routing:
 * - context "text"        → [processDataBinding] with a null Android
 *                           Context (text resolver, no string-resource
 *                           step)
 * - context "value"       → [DataBindingContext.resolveString] /
 *                           [DataBindingContext.resolveNumber] /
 *                           [DataBindingContext.resolveBoolean] per
 *                           valueType; `{"outcome": "unresolved"}` means
 *                           the resolver must return null (the caller's
 *                           attribute default applies)
 * - context "embedParams" → [DynamicEmbedComponent.resolveParams]
 * - kind "validation"     → SKIPPED (authoring-time rules enforced by the
 *                           codegen tools' validators, not the runtime)
 *
 * Cases with "dataDefaults" merge the defaults into the data map first,
 * defaults filling only absent keys — mirroring DynamicView's
 * applyDataSectionDefaults precedence (defaultValue → inline `??` →
 * context unresolved behavior).
 */
class BindingVectorsTest {

    private val vectors: JsonObject by lazy {
        val stream = checkNotNull(
            javaClass.classLoader?.getResourceAsStream("binding_vectors.json")
        ) { "binding_vectors.json missing from test resources" }
        stream.bufferedReader().use { JsonParser.parseString(it.readText()).asJsonObject }
    }

    private fun runtimeCases(): List<JsonObject> = vectors.getAsJsonArray("cases")
        .map { it.asJsonObject }
        .filter { it.get("kind")?.asString != "validation" }

    // ── Vector file contract ──────────────────────────────────────────

    @Test
    fun vectorsVersionIsOne() {
        // A future re-vendor with a bumped version must be noticed here
        // (and this harness re-checked against the new semantics).
        assertEquals(1, vectors.get("version").asInt)
        assertEquals(1, vectors.get("semanticsVersion").asInt)
    }

    @Test
    fun allRuntimeContextsArePresent() {
        val contexts = runtimeCases().map { it.get("context").asString }.toSet()
        assertEquals(setOf("text", "value", "embedParams"), contexts)
    }

    // ── Runtime case execution ────────────────────────────────────────

    @Test
    fun allRuntimeVectorCasesPass() {
        val failures = mutableListOf<String>()
        var executed = 0

        for (case in runtimeCases()) {
            val id = case.get("id").asString
            try {
                when (case.get("context").asString) {
                    "text" -> runTextCase(case)
                    "value" -> runValueCase(case)
                    "embedParams" -> runEmbedParamsCase(case)
                    else -> throw AssertionError("unknown context")
                }
                executed++
            } catch (e: AssertionError) {
                failures.add("[$id] ${e.message}")
            }
        }

        if (failures.isNotEmpty()) {
            fail("${failures.size} of ${runtimeCases().size} vector cases failed:\n" +
                failures.joinToString("\n"))
        }
        assertTrue("expected a substantial vector suite, ran $executed", executed >= 70)
    }

    private fun runTextCase(case: JsonObject) {
        val template = case.get("template").asString
        val data = effectiveData(case)
        val expected = case.getAsJsonObject("expect").get("text").asString
        // Text resolver without an Android Context — no string-resource step.
        assertEquals(expected, processDataBinding(template, data))
    }

    private fun runValueCase(case: JsonObject) {
        val expr = case.get("expr").asString
        val data = effectiveData(case)
        val expect = case.getAsJsonObject("expect")
        val unresolved = expect.get("outcome")?.asString == "unresolved"

        when (val valueType = case.get("valueType").asString) {
            "string" -> {
                val actual = DataBindingContext.resolveString(expr, data)
                if (unresolved) assertNull("expected unresolved, got '$actual'", actual)
                else assertEquals(expect.get("value").asString, actual)
            }
            "bool" -> {
                val actual = DataBindingContext.resolveBoolean(expr, data)
                if (unresolved) assertNull("expected unresolved, got '$actual'", actual)
                else assertEquals(expect.get("value").asBoolean, actual)
            }
            "number" -> {
                val actual = DataBindingContext.resolveNumber(expr, data)
                if (unresolved) assertNull("expected unresolved, got '$actual'", actual)
                else {
                    assertEquals(
                        "number mismatch",
                        expect.get("value").asDouble,
                        actual ?: Double.NaN,
                        1e-9
                    )
                }
            }
            else -> throw AssertionError("unknown valueType $valueType")
        }
    }

    private fun runEmbedParamsCase(case: JsonObject) {
        val paramsJson = case.getAsJsonObject("params")
        val parentData = toDataMap(case.getAsJsonObject("parentData"))
        val expected = case.getAsJsonObject("expect").getAsJsonObject("params")
        val actual = DynamicEmbedComponent.resolveParams(paramsJson, parentData)
        assertParamsEqual("", expected, actual)
    }

    // ── Helpers ───────────────────────────────────────────────────────

    /**
     * dataDefaults fill only ABSENT keys (applyDataSectionDefaults
     * semantics), then runtime data wins.
     */
    private fun effectiveData(case: JsonObject): Map<String, Any> {
        val data = toDataMap(case.getAsJsonObject("data") ?: JsonObject())
        val defaults = case.getAsJsonObject("dataDefaults")?.let { toDataMap(it) } ?: emptyMap()
        val merged = LinkedHashMap<String, Any?>(defaults.filterKeys { it !in data })
        merged.putAll(data)
        @Suppress("UNCHECKED_CAST")
        return merged as Map<String, Any>
    }

    /**
     * Vector JSON → the plain-Kotlin data-map shape ViewModels provide:
     * numbers become Double (mirroring gson's JSON-int-as-Double parse —
     * the stringification rules must absorb that), objects become Maps,
     * arrays become Lists. JSON null stays a null map VALUE (the key is
     * present — flat lookup must still treat it as unresolved).
     */
    private fun toDataMap(json: JsonObject): Map<String, Any> {
        val out = LinkedHashMap<String, Any?>()
        for ((key, value) in json.entrySet()) {
            out[key] = fromElement(value)
        }
        @Suppress("UNCHECKED_CAST")
        return out as Map<String, Any>
    }

    private fun fromElement(element: JsonElement): Any? = when {
        element.isJsonNull -> null
        element.isJsonObject -> toDataMap(element.asJsonObject)
        element.isJsonArray -> element.asJsonArray.map { fromElement(it) }
        else -> {
            val p = element.asJsonPrimitive
            when {
                p.isBoolean -> p.asBoolean
                p.isNumber -> p.asDouble
                else -> p.asString
            }
        }
    }

    /**
     * JSON-semantic comparison for resolved params: numbers compare
     * numerically (integer identity in the ACTUAL map is exercised by
     * gson's Number wrappers; "5" must not have silently become "5.0"
     * where the source was integral — see integerIdentity test below),
     * maps recurse, everything else compares by equality/string form.
     */
    private fun assertParamsEqual(path: String, expected: JsonObject, actual: Map<String, Any>) {
        assertEquals(
            "key set mismatch at '$path' (expected ${expected.keySet()}, got ${actual.keys})",
            expected.keySet(),
            actual.keys
        )
        for (key in expected.keySet()) {
            val exp = expected.get(key)
            val act = actual.getValue(key)
            val childPath = if (path.isEmpty()) key else "$path.$key"
            when {
                exp.isJsonObject -> {
                    @Suppress("UNCHECKED_CAST")
                    val actMap = act as? Map<String, Any>
                        ?: throw AssertionError("expected object at '$childPath', got $act")
                    assertParamsEqual(childPath, exp.asJsonObject, actMap)
                }
                exp.isJsonPrimitive && exp.asJsonPrimitive.isNumber -> {
                    val actNum = (act as? Number)?.toDouble()
                        ?: throw AssertionError("expected number at '$childPath', got $act")
                    assertEquals("number mismatch at '$childPath'", exp.asDouble, actNum, 1e-9)
                }
                exp.isJsonPrimitive && exp.asJsonPrimitive.isBoolean ->
                    assertEquals("bool mismatch at '$childPath'", exp.asBoolean, act)
                else ->
                    assertEquals("value mismatch at '$childPath'", exp.asString, act.toString())
            }
        }
    }

    // ── KJUI-specific supplements (not in the shared vectors) ─────────

    /**
     * Embed params keep native JSON integer identity: a literal 5 must
     * reach the embedded screen's data map spelling itself "5" (not
     * "5.0") — gson JsonPrimitive number identity, relied on by the
     * embedded layout's data section.
     */
    @Test
    fun embedParams_integerIdentityPreserved() {
        val params = JsonParser.parseString("""{"count": 5, "ratio": 0.5}""").asJsonObject
        val resolved = DynamicEmbedComponent.resolveParams(params, emptyMap())
        assertEquals("5", resolved.getValue("count").toString())
        assertEquals("0.5", resolved.getValue("ratio").toString())
    }
}
