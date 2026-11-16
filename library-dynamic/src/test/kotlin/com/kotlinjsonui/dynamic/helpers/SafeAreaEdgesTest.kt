package com.kotlinjsonui.dynamic.helpers

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.kotlinjsonui.core.DeclaredSpelling
import com.kotlinjsonui.dynamic.generated.CommonAttributes
import com.kotlinjsonui.dynamic.generated.SafeAreaViewAttributes
import com.kotlinjsonui.dynamic.generated.ViewAttributes
import com.kotlinjsonui.dynamic.helpers.SafeAreaEdges.Edge
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * `safeAreaInsetPositions` is declared on BOTH SafeAreaView and View — the SSoT
 * says so in as many words, because SafeAreaView is its own definition section
 * and does not inherit View's. Only the SafeAreaView component read it, so a
 * plain View naming the edges reserved nothing.
 *
 * The six declared words mean one thing on every path (4f ruling,
 * 2026-09-26): top / bottom that edge, leading / trailing where reading starts
 * / ends, vertical top and bottom, all every edge; any other word reserves no
 * edge and is named.
 */
class SafeAreaEdgesTest {

    private val view = ViewAttributes.SafeAreaInsetPositions.declaredSpellings
    private val said = mutableListOf<String>()

    @Before
    fun sink() {
        DeclaredSpelling.warningSink = { said += it }
    }

    @After
    fun reset() {
        DeclaredSpelling.warningSink = null
    }

    private fun node(json: String): JsonObject =
        Gson().fromJson(json, JsonObject::class.java)

    private fun edges(vararg words: String) =
        SafeAreaEdges.edgesOf(words.toList(), view, "View.safeAreaInsetPositions")

    @Test
    fun eachDeclaredWordReservesItsEdges() {
        assertEquals(setOf(Edge.TOP), edges("top"))
        assertEquals(setOf(Edge.BOTTOM), edges("bottom"))
        assertEquals(setOf(Edge.LEADING), edges("leading"))
        assertEquals(setOf(Edge.TRAILING), edges("trailing"))
        assertEquals(setOf(Edge.TOP, Edge.BOTTOM), edges("vertical"))
        assertEquals(Edge.entries.toSet(), edges("all"))
        assertEquals(setOf(Edge.TOP, Edge.LEADING), edges("top", "leading"))
        assertEquals(emptyList<String>(), said)
    }

    /** leading / trailing are the reading direction's, not the screen's left / right. */
    @Test
    fun leadingAndTrailingAreTheReadingDirectionsSides() {
        assertEquals(androidx.compose.foundation.layout.WindowInsetsSides.Start, Edge.LEADING.sides)
        assertEquals(androidx.compose.foundation.layout.WindowInsetsSides.End, Edge.TRAILING.sides)
    }

    /** The vocabulary is the generated one, word for word. */
    @Test
    fun theWordsAreTheDeclaredItems() {
        assertEquals(listOf("top", "bottom", "leading", "trailing", "vertical", "all"), view)
        assertEquals(view, SafeAreaViewAttributes.SafeAreaInsetPositions.declaredSpellings)
        for (w in view) assertEquals(w, true, edges(w).isNotEmpty())
    }

    /**
     * An undeclared word reserves no edge and is named — once, in the
     * sentence the generated parse names an enum value in (machine-compared
     * with CommonAttributes.parse's).
     */
    @Test
    fun anUndeclaredWordReservesNothingAndIsNamed() {
        for (w in listOf("start", "end", "left", "right", "horizontal", "Top", "ALL")) {
            assertEquals(w, emptySet<Edge>(), edges(w))
        }
        assertEquals(setOf(Edge.TOP), edges("top", "left"))
        assertEquals(
            listOf(
                "View.safeAreaInsetPositions: unknown enum value 'start'",
                "View.safeAreaInsetPositions: unknown enum value 'end'",
                "View.safeAreaInsetPositions: unknown enum value 'left'",
                "View.safeAreaInsetPositions: unknown enum value 'right'",
                "View.safeAreaInsetPositions: unknown enum value 'horizontal'",
                "View.safeAreaInsetPositions: unknown enum value 'Top' — did you mean 'top'?",
                "View.safeAreaInsetPositions: unknown enum value 'ALL' — did you mean 'all'?",
            ),
            said
        )
        // the generated parse's sentence, for an enum value it saw
        val parsed = mutableListOf<String>()
        com.kotlinjsonui.dynamic.generated.AttrWarnings.handler = { parsed += it }
        try {
            CommonAttributes.parse(mapOf("visibility" to "Gone"))
        } finally {
            com.kotlinjsonui.dynamic.generated.AttrWarnings.handler = null
        }
        assertEquals(listOf("common.visibility: unknown enum value 'Gone' — did you mean 'gone'?"), parsed)
    }

    @Test
    fun theCanonicalRowIsRead() {
        assertEquals(
            setOf(Edge.TOP, Edge.BOTTOM),
            SafeAreaEdges.requested(node("{}"), listOf("top", "bottom"), view, "View.safeAreaInsetPositions")
        )
    }

    @Test
    fun theLegacyEdgesSpellingWinsWhenBothAreWritten() {
        // The priority the SafeAreaView path has always had.
        assertEquals(
            setOf(Edge.TOP),
            SafeAreaEdges.requested(node("""{"edges":["top"]}"""), listOf("bottom"), view, "View.safeAreaInsetPositions")
        )
    }

    @Test
    fun aNodeThatNamesNoEdgeAsksForNothing() {
        // Only SafeAreaView defaults to `all`; a plain View that says nothing
        // must reserve nothing, which is why the default is NOT in here.
        assertNull(SafeAreaEdges.requested(node("{}"), null, view, "View.safeAreaInsetPositions"))
        assertNull(SafeAreaEdges.requested(node("""{"edges":[]}"""), null, view, "View.safeAreaInsetPositions"))
    }

    /**
     * A node that names only words reserving nothing asks for nothing — not
     * for the SafeAreaView default, which is for a node that names none (as in
     * kjui's codegen).
     */
    @Test
    fun namingOnlyWordsThatReserveNothingIsNotNamingNone() {
        val safeArea = SafeAreaViewAttributes.SafeAreaInsetPositions.declaredSpellings
        assertEquals(emptySet<Edge>(), SafeAreaEdges.requested(node("{}"), emptyList(), safeArea, "SafeAreaView.safeAreaInsetPositions"))
        assertEquals(emptySet<Edge>(), SafeAreaEdges.requested(node("{}"), listOf("left"), safeArea, "SafeAreaView.safeAreaInsetPositions"))
    }

    @Test
    fun anEnclosingTabViewRemovesTheEdgeItAlreadyReserved() {
        assertEquals(
            setOf(Edge.TOP),
            SafeAreaEdges.filtered(setOf(Edge.TOP, Edge.BOTTOM), ignoreTop = false, ignoreBottom = true)
        )
        // `all` minus bottom is every other edge — leading and trailing too
        assertEquals(
            setOf(Edge.TOP, Edge.LEADING, Edge.TRAILING),
            SafeAreaEdges.filtered(SafeAreaEdges.ALL, ignoreTop = false, ignoreBottom = true)
        )
        assertEquals(
            setOf(Edge.LEADING, Edge.TRAILING),
            SafeAreaEdges.filtered(SafeAreaEdges.ALL, ignoreTop = true, ignoreBottom = true)
        )
        assertEquals(SafeAreaEdges.ALL, SafeAreaEdges.filtered(SafeAreaEdges.ALL, ignoreTop = false, ignoreBottom = false))
    }

    /**
     * Both components read their own section's spellings and name the node's
     * section; each edge is the system bars' inset on that side only (no
     * status / navigation / system bar padding of its own).
     */
    @Test
    fun theTwoComponentsReadTheirOwnSectionAndReserveBySide() {
        val dir = "src/main/kotlin/com/kotlinjsonui/dynamic/"
        fun code(path: String) = File(dir + path).readLines()
            .filterNot { it.trimStart().let { t -> t.startsWith("//") || t.startsWith("*") } }.joinToString("\n")
        val container = code("components/DynamicContainerComponent.kt")
        val safeArea = code("components/DynamicSafeAreaViewComponent.kt")
        assertTrue(container.contains("ViewAttributes.SafeAreaInsetPositions.declaredSpellings, \"View.safeAreaInsetPositions\""))
        assertTrue(safeArea.contains("SafeAreaViewAttributes.SafeAreaInsetPositions.declaredSpellings, \"SafeAreaView.safeAreaInsetPositions\""))
        val edgesCode = code("helpers/SafeAreaEdges.kt")
        for (bar in listOf("statusBarsPadding", "navigationBarsPadding", "systemBarsPadding")) {
            assertEquals(bar, false, edgesCode.contains(bar))
        }
        for (c in listOf(container, safeArea)) {
            for (bar in listOf("statusBarsPadding", "navigationBarsPadding", "systemBarsPadding")) {
                assertEquals(bar, false, c.contains(bar))
            }
        }
    }
}
