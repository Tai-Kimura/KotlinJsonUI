package com.kotlinjsonui.dynamic

import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * A Slider's value and a pinch's scale are Floats on Android; SwiftUI hands
 * the same handler a Double (the Slider) or a CGFloat (the pinch), and one
 * declaration serves both faces, so a handler is declared `(Double)`. The
 * Float was handed to it as it stood: the lambda's bridge threw
 * ClassCastException (Float is not Double), which resolveEventHandler
 * swallowed, and the handler was never called — the screen compiled and the
 * slider did nothing (ticket
 * kjui-slider-on-value-change-passes-float-to-double-handler, whose generated
 * face did not compile). A `(Double)` handler is now given the value as a
 * Double; every other declaration gets what it got.
 */
class FloatValueReachesADoubleHandlerTest {

    private fun calls(viewId: String?): Map<String, List<String>> {
        val got = mutableMapOf<String, MutableList<String>>()
        fun add(k: String, v: String) = got.getOrPut(k) { mutableListOf() }.add(v)
        val hD: (Double) -> Unit = { v -> add("Double", "$v") }
        val hDN: (Double?) -> Unit = { v -> add("Double?", "$v") }
        val hF: (Float) -> Unit = { v -> add("Float", "$v") }
        val hA: (Any) -> Unit = { v -> add("Any", "$v:${v.javaClass.simpleName}") }
        val hSD: (String, Double) -> Unit = { id, v -> add("String,Double", "$id,$v") }
        val hSF: (String, Float) -> Unit = { id, v -> add("String,Float", "$id,$v") }
        val hN: () -> Unit = { add("none", "") }
        val data: Map<String, Any> = mapOf(
            "hD" to hD, "hDN" to hDN, "hF" to hF, "hA" to hA, "hSD" to hSD, "hSF" to hSF, "hN" to hN
        )
        for (name in data.keys) ModifierBuilder.resolveEventHandler("@{$name}", data, viewId, 0.25f)
        return got
    }

    @Test
    fun aFloatValueReachesEveryDeclaration_withAnId() {
        assertEquals(
            mapOf(
                "Double" to listOf("0.25"), "Double?" to listOf("0.25"), "Float" to listOf("0.25"),
                "Any" to listOf("0.25:Float"), "String,Double" to listOf("s,0.25"),
                "String,Float" to listOf("s,0.25"), "none" to listOf("")
            ),
            calls("s")
        )
    }

    /** An id-less pinch hands the scale alone (the `valueExpr != null` arm). */
    @Test
    fun aFloatValueReachesEveryOneParameterDeclaration_withoutAnId() {
        assertEquals(
            mapOf(
                "Double" to listOf("0.25"), "Double?" to listOf("0.25"), "Float" to listOf("0.25"),
                "Any" to listOf("0.25:Float"), "none" to listOf("")
            ),
            calls(null)
        )
    }

    /** A handler that throws on its own is not called a second time. */
    @Test
    fun aHandlerIsCalledOnce() {
        var n = 0
        val h: (Float) -> Unit = { n += 1; throw IllegalStateException("handler's own") }
        ModifierBuilder.resolveEventHandler("@{h}", mapOf("h" to h), "s", 0.5f)
        assertEquals(1, n)
    }
}
