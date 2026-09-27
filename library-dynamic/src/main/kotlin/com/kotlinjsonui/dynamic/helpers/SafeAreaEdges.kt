package com.kotlinjsonui.dynamic.helpers

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.gson.JsonObject
import com.kotlinjsonui.core.DeclaredSpelling
import com.kotlinjsonui.dynamic.TypedAttrs

/**
 * The `safeAreaInsetPositions` vocabulary, in one place.
 *
 * The row is declared on BOTH `SafeAreaView` and `View` on purpose — the SSoT
 * says so in as many words, because SafeAreaView is its own definition section
 * and does not inherit View's. Only the SafeAreaView component read it, so a
 * plain View naming the edges reserved nothing.
 *
 * The six declared words, as written (4f ruling, 2026-09-26, the same on every
 * path): `top` / `bottom` are that edge; `leading` is where reading starts and
 * `trailing` where it ends (WindowInsetsSides.Start / End); `vertical` is top
 * and bottom; `all` is every edge. Any other word — `start`, `end`, `left`,
 * `right`, `horizontal`, another case — reserves no edge, and is named in the
 * sentence the generated parse names an enum value in. `leading`, `trailing`
 * and `vertical` reserved nothing here, and the undeclared `start` / `end`
 * reserved every system bar.
 *
 * Each edge is the system bars' inset on that side only: the navigation bar
 * sits on a side in landscape, and `navigationBarsPadding` reserved it there
 * for a `bottom`.
 */
object SafeAreaEdges {

    /** An edge a node reserves. */
    enum class Edge(internal val sides: WindowInsetsSides) {
        TOP(WindowInsetsSides.Top),
        BOTTOM(WindowInsetsSides.Bottom),
        LEADING(WindowInsetsSides.Start),
        TRAILING(WindowInsetsSides.End)
    }

    /** What each declared word reserves (the word lowered, as DeclaredSpelling answers it). */
    private val WORDS: Map<String, Set<Edge>> = mapOf(
        "top" to setOf(Edge.TOP),
        "bottom" to setOf(Edge.BOTTOM),
        "leading" to setOf(Edge.LEADING),
        "trailing" to setOf(Edge.TRAILING),
        "vertical" to setOf(Edge.TOP, Edge.BOTTOM),
        "all" to Edge.entries.toSet()
    )

    /**
     * The edges a node asks to reserve, or null when it names none.
     *
     * [declaredSpellings] is the node's own section's
     * (`ViewAttributes.SafeAreaInsetPositions` / `SafeAreaViewAttributes.…`)
     * and [context] names it in the sentence (`View.safeAreaInsetPositions`).
     *
     * `edges` is the legacy spelling and wins when both are written — the
     * priority the SafeAreaView path has always had. A node that declares
     * neither gets null here; only SafeAreaView defaults to `all`, because
     * reserving the whole safe area is what that component IS. A plain View
     * that says nothing must reserve nothing. A node that names only words
     * that reserve nothing (`[]`, `["left"]`) reserves nothing — the default
     * is for a node that names none, as in kjui's codegen; a SafeAreaView
     * took `all` for them here.
     */
    fun requested(
        json: JsonObject,
        declared: List<Any?>?,
        declaredSpellings: Collection<String>,
        context: String
    ): Set<Edge>? {
        val legacy = TypedAttrs.undeclared(json, "edges")
            ?.takeIf { it.isJsonArray }
            ?.asJsonArray
            ?.mapNotNull { it.takeIf { e -> e.isJsonPrimitive }?.asString }
            ?.takeIf { it.isNotEmpty() }
        val canonical = declared?.mapNotNull { it as? String }
        return edgesOf(legacy ?: canonical ?: return null, declaredSpellings, context)
    }

    /** The edges the written words reserve (none for a word that is not declared). */
    fun edgesOf(words: List<String>, declaredSpellings: Collection<String>, context: String): Set<Edge> =
        words.flatMap { word ->
            WORDS[DeclaredSpelling.lowered(word, declaredSpellings, context)].orEmpty()
        }.toSet()

    /** Every edge: what a SafeAreaView that names none reserves. */
    val ALL: Set<Edge> = Edge.entries.toSet()

    /** Drop the edges an enclosing TabView has already reserved (LocalSafeAreaConfig). */
    fun filtered(edges: Set<Edge>, ignoreTop: Boolean, ignoreBottom: Boolean): Set<Edge> =
        edges.filterNot { (ignoreTop && it == Edge.TOP) || (ignoreBottom && it == Edge.BOTTOM) }.toSet()

    /** Reserve the named edges: the system bars' inset on those sides only. */
    @Composable
    fun apply(modifier: Modifier, edges: Set<Edge>): Modifier {
        if (edges.isEmpty()) return modifier
        val sides = edges.map { it.sides }.reduce { a, b -> a + b }
        return modifier.windowInsetsPadding(WindowInsets.systemBars.only(sides))
    }

    /** The keyboard inset, unless the node opted out. */
    fun applyKeyboard(modifier: Modifier, ignoreKeyboard: Boolean): Modifier =
        if (ignoreKeyboard) modifier else modifier.imePadding()
}
