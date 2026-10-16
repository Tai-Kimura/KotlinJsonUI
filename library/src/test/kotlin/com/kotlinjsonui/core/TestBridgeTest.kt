package com.kotlinjsonui.core

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * The test bridge (jsonui-test-runner spec §10.3–10.4): the one check
 * [JsonUITestValue.check] held to the shared coercion vector, the Kotlin
 * types it hands over, the payload, consumption per launchId × screenId and
 * the bridge JSON.
 */
class TestBridgeTest {

    @After
    fun forgetConsumption() = TestConsumption.clearForTest()

    /**
     * src/test/resources/initial_state_coercion_vectors.json is jsonui-cli
     * shared/core/initial_state_coercion_vectors.json byte for byte (ci.yml
     * compares them), which is jsonui-test-runner's
     * specs/vectors/initial-state-coercion.json. Inputs go through
     * [JsonUITestValue.parseJson] as the intent extra does.
     */
    @Test
    fun theCoercionVector() {
        val vector = JsonParser.parseString(
            File("src/test/resources/initial_state_coercion_vectors.json").readText()
        ).asJsonObject
        val cases = vector.getAsJsonArray("cases")
        assertTrue("the vector has cases", cases.size() > 80)
        val failures = mutableListOf<String>()
        for (element in cases) {
            val case = element.asJsonObject
            val declared = case.getAsJsonArray("data").associate {
                val item = it.asJsonObject
                item.get("name").asString to item.get("class").asString
            }
            @Suppress("UNCHECKED_CAST")
            val input = JsonUITestValue.parseJson(case.get("input").toString()) as Map<String, Any?>
            var updated: Map<String, Any?> = emptyMap()
            val rejected = JsonUITestValue.applyAll(input, declared, update = { updated = it })
            val receipt = TestReceipt.of(input, rejected)

            val expect = case.getAsJsonObject("expect")
            val expectedApplied = expect.getAsJsonArray("applied").map { it.asString }.toSet()
            val expectedRejected = expect.getAsJsonArray("rejected").map {
                val r = it.asJsonObject
                RejectedKey(r.get("key").asString, r.get("reason").asString)
            }.toSet()
            if (receipt.applied.toSet() != expectedApplied || rejected.toSet() != expectedRejected ||
                updated.keys != expectedApplied
            ) {
                failures += "${case.get("name").asString}: applied ${receipt.applied} rejected $rejected " +
                    "(expected $expectedApplied / $expectedRejected)"
            }
        }
        assertEquals(failures.joinToString("\n"), 0, failures.size)
    }

    private fun accepted(value: Any?, declaredClass: String): Any? {
        val verdict = JsonUITestValue.check(value, declaredClass)
        assertTrue("$declaredClass <- $value: $verdict", verdict is TestValueVerdict.Accepted)
        return (verdict as TestValueVerdict.Accepted).value
    }

    private fun json(text: String) = JsonUITestValue.parseJson(text)

    /** What reaches updateData is the Kotlin type the Data field holds, not the parsed JSON number. */
    @Test
    fun acceptedValuesComeBackAsTheFieldsKotlinType() {
        assertEquals(2, accepted(json("2.0"), "Int"))
        assertEquals(-2147483648, accepted(json("-2147483648"), "Integer"))
        assertEquals(1.5, accepted(json("1.5"), "Double"))
        assertEquals(3.0, accepted(json("3"), "Number"))
        assertEquals(1.5f, accepted(json("1.5"), "Float"))
        assertEquals(2f, accepted(json("2"), "CGFloat"))
        assertEquals(true, accepted(json("true"), "Boolean"))
        assertEquals("#FF0000", accepted(json("\"#FF0000\""), "Color"))
        assertEquals(listOf(1, 2), accepted(json("[1, 2.0]"), "[Int]"))
        assertEquals(listOf(true), accepted(json("[true]"), "List<Bool>"))
        assertEquals(listOf("a", 1, 1.5, false), accepted(json("[\"a\", 1, 1.5, false]"), "Array"))
        assertEquals(mapOf("n" to 1, "s" to "x"), accepted(json("{\"n\": 1, \"s\": \"x\"}"), "Object"))
        assertNull(accepted(null, "String?"))
    }

    /** A dictionary spelling is not a typed array, and a closure is not one either. */
    @Test
    fun aDictionaryOrClosureSpellingIsUnsupported() {
        assertEquals(TestValueVerdict.Rejected("unsupported type [String: Int]"),
            JsonUITestValue.check(json("{}"), "[String: Int]"))
        assertEquals(TestValueVerdict.Rejected("unsupported type ([Int]) -> Void"),
            JsonUITestValue.check(json("[1]"), "([Int]) -> Void"))
    }

    @Test
    fun theBridgePublishesWhatAnInitialStateCanSetExceptColour() {
        val declared = mapOf("s" to "String", "c" to "Color?", "cs" to "[Color]", "h" to "(() -> Void)?", "o" to "Int?")
        val data = mapOf("s" to "x", "c" to 0xFF00FF00UL, "cs" to emptyList<Any>(), "h" to Any(), "o" to null)
        assertEquals(mapOf("s" to "x", "o" to null), JsonUITestValue.snapshot(data, declared))
        assertTrue(JsonUITestValue.supports("Color"))
        assertFalse(JsonUITestValue.publishes("Color"))
    }

    /** Without updateData nothing is applied, and every value the check accepts says why. */
    @Test
    fun withoutUpdateDataEveryAcceptedKeyIsRejectedWithTheReason() {
        val rejected = JsonUITestValue.applyAll(
            mapOf("a" to "x", "b" to json("1.5"), "z" to 1),
            mapOf("a" to "String", "b" to "Int"),
            update = null
        )
        assertEquals(
            setOf(
                RejectedKey("a", "viewModel has no updateData"),
                RejectedKey("b", "type mismatch: expected Int"),
                RejectedKey("z", "not a data field")
            ),
            rejected.toSet()
        )
    }

    @Test
    fun aColourSpellingIsResolvedOrRejected() {
        val colours = mapOf("red" to 0xFFFF0000L)
        val colorOf = { s: String -> colours[s] }
        var updated: Map<String, Any?> = emptyMap()
        val declared = mapOf("c" to "Color", "o" to "Color?", "bad" to "Color", "cs" to "[Color]")
        val rejected = JsonUITestValue.applyAll(
            mapOf("c" to "red", "o" to null, "bad" to "nope", "cs" to listOf("red", "nope")),
            declared,
            update = { updated = it }
        ) { key, accepted -> JsonUITestValue.resolveColor(accepted, declared.getValue(key), colorOf) }
        assertEquals(mapOf("c" to 0xFFFF0000L, "o" to null), updated)
        assertEquals(
            setOf(
                RejectedKey("bad", "type mismatch: expected Color"),
                RejectedKey("cs", "type mismatch: expected [Color]")
            ),
            rejected.toSet()
        )
    }

    // -- payload ---------------------------------------------------------------

    @Test
    fun aPayloadIsVersionOneWithALaunchId() {
        val payload = TestPayload.parse("""{"version":1,"launchId":"L1","screens":{"home":{"a":1},"bad":3}}""")!!
        assertEquals("L1", payload.launchId)
        assertEquals(setOf("home"), payload.screens.keys)
        assertNull(TestPayload.parse("""{"version":2,"launchId":"L1","screens":{}}"""))
        assertNull(TestPayload.parse("""{"version":1,"screens":{"home":{"a":1}}}"""))
        assertNull(TestPayload.parse("not json"))
        assertNull(TestPayload.parse(""))
        assertNull(TestPayload.parse(null))
    }

    // -- consumption -----------------------------------------------------------

    @Test
    fun aScreenAppliesOncePerLaunchAndKeepsItsReceipt() {
        var applies = 0
        val apply = { applies++; TestReceipt(listOf("a"), emptyList()) }
        val first = TestConsumption.consume("L1", "home", apply)
        val again = TestConsumption.consume("L1", "home", apply)
        assertEquals(1, applies)
        assertEquals(first, again)
        assertEquals(first, TestConsumption.receipt("L1", "home"))

        TestConsumption.consume("L1", "other", apply)
        assertEquals("another screen of the same launch applies its own", 2, applies)
    }

    @Test
    fun aNewLaunchAppliesAgainAndForgetsTheLastOne() {
        var applies = 0
        val apply = { applies++; TestReceipt(emptyList(), emptyList()) }
        TestConsumption.consume("L1", "home", apply)
        TestConsumption.consume("L2", "home", apply)
        assertEquals(2, applies)
        assertNull("nothing is carried from one launch to the next", TestConsumption.receipt("L1", "home"))
    }

    @Test
    fun aReceiptListsAppliedAndRejectedSorted() {
        val receipt = TestReceipt.of(
            linkedMapOf("z" to 1, "b" to 2, "y" to 3, "a" to 4),
            listOf(RejectedKey("y", "not a data field"), RejectedKey("b", "not a data field"))
        )
        assertEquals(listOf("a", "z"), receipt.applied)
        assertEquals(listOf("b", "y"), receipt.rejected.map { it.key })
    }

    // -- bridge JSON -------------------------------------------------------------

    @Test
    fun theBridgeJson() {
        val json = testBridgeJson(
            "L1",
            TestReceipt(listOf("a"), listOf(RejectedKey("x", "not a data field"))),
            linkedMapOf("z" to null, "a" to 1, "n" to listOf(true, "s"), "m" to mapOf("k" to 1.5))
        )
        assertEquals(
            """{"data":{"a":1,"m":{"k":1.5},"n":[true,"s"],"z":null},"initialState":{"applied":["a"],""" +
                """"rejected":[{"key":"x","reason":"not a data field"}]},"launchId":"L1"}""",
            json
        )
        val bare = JsonParser.parseString(testBridgeJson(null, null, emptyMap())).asJsonObject
        assertTrue(bare.get("launchId").isJsonNull)
        assertTrue(bare.get("initialState").isJsonNull)
        assertEquals(JsonObject(), bare.getAsJsonObject("data"))
    }
}
