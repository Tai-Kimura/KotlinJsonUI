package com.kotlinjsonui.dynamic.helpers

import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.Modifier
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.TypedAttrs

/**
 * The `safeAreaInsetPositions` vocabulary, in one place.
 *
 * The row is declared on BOTH `SafeAreaView` and `View` on purpose — the SSoT
 * says so in as many words, because SafeAreaView is its own definition section
 * and does not inherit View's. Only the SafeAreaView component read it, so a
 * plain View naming the edges reserved nothing.
 *
 * Extracted rather than copied: this is the same rule the font and dimension
 * vocabularies follow. A second copy of "which edge does `start` mean" drifts,
 * and the edges are also the thing a nested SafeAreaView filters.
 */
object SafeAreaEdges {

    /** The canonical spellings; `all` stands for every edge at once. */
    val EDGE_NAMES: Set<String> = setOf("all", "top", "bottom", "start", "end")

    /**
     * The edges a node asks to reserve, or null when it asks for none.
     *
     * `edges` is the legacy spelling and wins when both are written — the
     * priority the SafeAreaView path has always had. A node that declares
     * neither gets null here; only SafeAreaView defaults to `all`, because
     * reserving the whole safe area is what that component IS. A plain View
     * that says nothing must reserve nothing.
     */
    fun requested(json: JsonObject, declared: List<Any?>?): List<String>? {
        val legacy = TypedAttrs.undeclared(json, "edges")
            ?.takeIf { it.isJsonArray }
            ?.asJsonArray
            ?.mapNotNull { it.takeIf { e -> e.isJsonPrimitive }?.asString }
            ?.takeIf { it.isNotEmpty() }
        val canonical = declared?.mapNotNull { it as? String }?.takeIf { it.isNotEmpty() }
        return (legacy ?: canonical)?.filter { it in EDGE_NAMES }?.takeIf { it.isNotEmpty() }
    }

    /**
     * Drop the edges an enclosing SafeAreaView has already consumed, expanding
     * `all` into the remainder so the filter has something to remove from.
     */
    fun filtered(edges: List<String>, ignoreTop: Boolean, ignoreBottom: Boolean): List<String> {
        var out = edges
        if (ignoreBottom) {
            out = out.flatMap { if (it == "all") listOf("top", "start", "end") else listOf(it) }
                .filterNot { it == "bottom" }
        }
        if (ignoreTop) {
            out = out.flatMap { if (it == "all") listOf("bottom", "start", "end") else listOf(it) }
                .filterNot { it == "top" }
        }
        return out.distinct()
    }

    /**
     * Reserve the named edges. `start`/`end` have no horizontal-only inset
     * modifier, so they reserve the full system bars — the behaviour the
     * SafeAreaView path already had.
     */
    fun apply(modifier: Modifier, edges: List<String>): Modifier {
        if (edges.contains("all")) return modifier.systemBarsPadding()
        var result = modifier
        if (edges.contains("top")) result = result.statusBarsPadding()
        if (edges.contains("bottom")) result = result.navigationBarsPadding()
        if (edges.contains("start") || edges.contains("end")) {
            result = result.systemBarsPadding()
        }
        return result
    }

    /** The keyboard inset, unless the node opted out. */
    fun applyKeyboard(modifier: Modifier, ignoreKeyboard: Boolean): Modifier =
        if (ignoreKeyboard) modifier else modifier.imePadding()
}
