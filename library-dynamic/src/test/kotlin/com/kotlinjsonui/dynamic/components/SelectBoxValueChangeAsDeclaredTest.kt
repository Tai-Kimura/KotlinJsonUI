package com.kotlinjsonui.dynamic.components

import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.DebugDiagnostics
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.generated.SelectBoxAttributes
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * SelectBox.onValueChange, called as its handler is declared (ruling ③; the
 * kjui codegen's `value_change_call`, spec/compose/view_id_by_position_spec.rb
 * runs the same matrix on the generated Kotlin): a lone `(String)` gets the
 * picked item whatever the binding; `(Int)` its index; `(String, String)` the
 * viewId and the item; `(String, Int)` the viewId and the index; `()`
 * nothing. The index is `indexOf` — also without an index binding — and the
 * prompt's is -1. A date has no index: a date box's `(Int)` / `(String, Int)`
 * handler is not called and is named once per box and handler in a
 * debuggable build.
 *
 * Each handler below is a typed Kotlin lambda, as the generated Data holds
 * it: the runtime cannot read its parameter types (erased), so what these
 * measure is that the right argument reaches each — a lone `(String)` under
 * an index binding was handed the Int and resolveEventHandler's catch
 * swallowed the ClassCastException (0 calls).
 */
class SelectBoxValueChangeAsDeclaredTest {

    private val warnings = mutableListOf<String>()

    @Before
    fun debuggable() {
        DebugDiagnostics.enabledOverride = true
        DynamicSelectBoxComponent.dateHandlerWarningSink = { warnings += it }
    }

    @After
    fun reset() {
        DebugDiagnostics.enabledOverride = null
        DynamicSelectBoxComponent.dateHandlerWarningSink = null
    }

    private fun attrs(json: String): SelectBoxAttributes =
        SelectBoxAttributes.parse(TypedAttrs.toAttrMap(JsonParser.parseString(json).asJsonObject))

    private val bindings = mapOf(
        "an item binding" to """"selectedItem": "@{item}"""",
        "an index binding" to """"selectedIndex": "@{idx}"""",
        "no binding" to null,
    )

    /** The handler of each declared type, recording the arguments it was called with. */
    private fun handlers(calls: MutableList<List<Any?>>): Map<String, Any> {
        val none: () -> Unit = { calls += listOf<Any?>() }
        val item: (String) -> Unit = { s -> calls += listOf<Any?>(s) }
        val index: (Int) -> Unit = { i -> calls += listOf<Any?>(i) }
        val idItem: (String, String) -> Unit = { id, s -> calls += listOf<Any?>(id, s) }
        val idIndex: (String, Int) -> Unit = { id, i -> calls += listOf<Any?>(id, i) }
        val any: (Any) -> Unit = { v -> calls += listOf<Any?>(v) }
        return mapOf(
            "()" to none, "(String)" to item, "(Int)" to index, "(String, String)" to idItem, "(String, Int)" to idIndex,
            "(Any)" to any,
        )
    }

    private val expected = mapOf(
        "()" to listOf<Any?>(),
        "(String)" to listOf<Any?>("b"),
        "(Int)" to listOf<Any?>(1),
        "(String, String)" to listOf<Any?>("selectBox_0_1", "b"),
        "(String, Int)" to listOf<Any?>("selectBox_0_1", 1),
    )

    @Test
    fun eachDeclaredTypeGetsItsArgumentsUnderEachBinding_exactlyOnce() {
        var compared = 0
        for ((bindingLabel, binding) in bindings) {
            val a = attrs("""{"type": "SelectBox", "items": ["a", "b", "b"], "onValueChange": "@{pick}"${binding?.let { ", $it" } ?: ""}}""")
            for (type in expected.keys + "(Any)") {
                val calls = mutableListOf<List<Any?>>()
                val data = mapOf<String, Any>("pick" to handlers(calls).getValue(type))
                DynamicSelectBoxComponent.pickHandlerCall(a, data, "selectBox_0_1", listOf("a", "b", "b"), "b")
                // Any other declared type takes the binding's own value (the
                // codegen's reading): the index for an index binding.
                val want = if (type == "(Any)") listOf<Any?>(if (bindingLabel == "an index binding") 1 else "b") else expected.getValue(type)
                assertEquals("$type under $bindingLabel", listOf(want), calls)
                compared++
            }
        }
        assertEquals(18, compared)
    }

    /** The required arm: a lone `(String)` is called with the ITEM when the index is the binding. */
    @Test
    fun aLoneStringHandlerGetsTheItemWhenTheIndexIsBound() {
        val a = attrs("""{"type": "SelectBox", "items": ["a", "b"], "selectedIndex": "@{idx}", "onValueChange": "@{pick}"}""")
        val got = mutableListOf<String>()
        DynamicSelectBoxComponent.pickHandlerCall(a, mapOf("pick" to { s: String -> got += s }), "selectBox_0", listOf("a", "b"), "b")
        assertEquals(listOf("b"), got)
    }

    @Test
    fun theIndexIsTheItemsFirst_andThePromptsIsMinusOne() {
        val a = attrs("""{"type": "SelectBox", "items": ["a", "b", "b"], "onValueChange": "@{pick}"}""")
        val got = mutableListOf<Int>()
        val data = mapOf<String, Any>("pick" to { i: Int -> got += i })
        DynamicSelectBoxComponent.pickHandlerCall(a, data, "sb", listOf("a", "b", "b"), "b")
        DynamicSelectBoxComponent.pickHandlerCall(a, data, "sb", listOf("a", "b", "b"), "Choose…")
        assertEquals(listOf(1, -1), got)
    }

    @Test
    fun aDateBoxCallsTheDateHandlers_andNamesTheIndexOnesOncePerBoxAndHandler() {
        val a = attrs("""{"type": "SelectBox", "selectItemType": "Date", "selectedDate": "@{day}", "onValueChange": "@{pick}"}""")
        for (type in listOf("()", "(String)", "(String, String)")) {
            val calls = mutableListOf<List<Any?>>()
            DynamicSelectBoxComponent.datePickHandlerCall(a, mapOf("pick" to handlers(calls).getValue(type)), "sb", "2026-09-26", null)
            val want = when (type) {
                "()" -> listOf<Any?>()
                "(String)" -> listOf<Any?>("2026-09-26")
                else -> listOf<Any?>("sb", "2026-09-26")
            }
            assertEquals(type, listOf(want), calls)
        }
        assertEquals(emptyList<String>(), warnings)

        for (type in listOf("(Int)", "(String, Int)")) {
            val calls = mutableListOf<List<Any?>>()
            val data = mapOf("pick" to handlers(calls).getValue(type))
            repeat(2) { DynamicSelectBoxComponent.datePickHandlerCall(a, data, "sb_$type", "2026-09-26", null) }
            assertEquals("$type is not called", emptyList<List<Any?>>(), calls)
        }
        val sentence = "SelectBox.onValueChange pick is not called: " +
            "a date SelectBox has no index: declare onValueChange as (String) or (String, String)"
        // Two boxes, one handler each, two picks each: named once per pair.
        assertEquals(listOf(sentence, sentence), warnings)
    }

    /**
     * A Date SelectBox's value is its selectedDate (4f's ruling, jsonui-cli
     * 1.9.0): selectedItem was read after it, bound and static, and
     * selectedValue / selectedIndex never were — none of the three is read
     * now. A lone bind still stands for selectedDate.
     */
    @Test
    fun aDateBoxReadsItsValueFromSelectedDateAlone() {
        val data = mapOf<String, Any>("day" to "2026-09-26", "other" to "2026-01-01", "idx" to 3)
        fun read(json: String): Pair<String?, String> {
            val a = attrs(json)
            val key = DynamicSelectBoxComponent.dateBindingVariableOf(a)
            return key to DynamicSelectBoxComponent.dateSelectionOf(a, data, key)
        }
        val date = """"type": "SelectBox", "selectItemType": "Date""""
        for (other in listOf(""""selectedItem": "@{other}"""", """"selectedValue": "@{other}"""", """"selectedIndex": "@{idx}"""",
                             """"selectedItem": "2026-01-01"""", """"selectedValue": "2026-01-01"""")) {
            assertEquals(other, null to "", read("{$date, $other}"))
            assertEquals(other, "day" to "2026-09-26", read("""{$date, $other, "selectedDate": "@{day}"}"""))
        }
        assertEquals("day" to "2026-09-26", read("""{$date, "bind": "@{day}"}"""))
        assertEquals(null to "2026-02-02", read("""{$date, "selectedDate": "2026-02-02", "selectedItem": "2026-01-01"}"""))
    }

    @Test
    fun aReleaseBuildNamesNothing() {
        DebugDiagnostics.enabledOverride = false
        val a = attrs("""{"type": "SelectBox", "selectItemType": "Date", "onValueChange": "@{pick}"}""")
        DynamicSelectBoxComponent.datePickHandlerCall(a, mapOf("pick" to { _: Int -> }), "sb_release", "2026-09-26", null)
        assertEquals(emptyList<String>(), warnings)
    }
}
