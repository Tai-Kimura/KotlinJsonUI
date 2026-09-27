package com.kotlinjsonui.dynamic.helpers

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Two states, two colours (4f ruling, 2026-09-26): `tapBackground` is the
 * background while the node is PRESSED — on every node with a tap — and
 * `highlightBackground` the background while `highlighted` (View's, boolean
 * or binding) is true. Every node but Button drew `tapBackground` as the
 * highlighted colour and nothing while pressed. (Button declares no
 * `highlighted`: its pressed colour is `tapBackground ?: highlightBackground`,
 * ButtonContainerColorTest.)
 *
 * Colours go through bindings to a Color: hex parsing needs Android's colour
 * parser, which the JVM stubs. What a composed modifier draws is not
 * readable here — the pressed colour is pinned by where it is attached (a
 * composed click in place of the plain one), not drawn.
 */
class TapAndHighlightBackgroundTest {

    private fun node(s: String): JsonObject = JsonParser.parseString(s).asJsonObject
    private val base = Color(0xFF111111)
    private val tap = Color(0xFF00FF00)
    private val highlight = Color(0xFFFF0000)
    private val data = mapOf<String, Any>("bg" to base, "tap" to tap, "hl" to highlight, "on" to true, "off" to false)

    private fun Modifier.elements(): List<Modifier.Element> =
        foldIn(mutableListOf<Modifier.Element>()) { acc, e -> acc.apply { add(e) } }

    /** The colours the background stage draws (BackgroundElement's `color`). */
    private fun backgrounds(m: Modifier): List<Color> = m.elements()
        .filter { it.javaClass.simpleName == "BackgroundElement" }
        .map { e ->
            val f = e.javaClass.declaredFields.first { it.name.startsWith("color") }
            f.isAccessible = true
            Color((f.get(e) as Long).toULong())
        }

    private fun background(json: String) = backgrounds(ModifierBuilder.applyBackground(Modifier, node(json), data, null))

    @Test
    fun highlightedDrawsHighlightBackground_notTapBackground() {
        // the reading of the element itself, on a plain background
        assertEquals(listOf(base), background("""{"type":"View","background":"@{bg}"}"""))
        assertEquals(listOf(highlight),
            background("""{"type":"View","background":"@{bg}","highlighted":true,"highlightBackground":"@{hl}","tapBackground":"@{tap}"}"""))
        assertEquals(listOf(highlight),
            background("""{"type":"View","background":"@{bg}","highlighted":"@{on}","highlightBackground":"@{hl}"}"""))
        // tapBackground is not the highlighted colour
        assertEquals(listOf(base),
            background("""{"type":"View","background":"@{bg}","highlighted":true,"tapBackground":"@{tap}"}"""))
        // not highlighted: the background
        assertEquals(listOf(base),
            background("""{"type":"View","background":"@{bg}","highlighted":"@{off}","highlightBackground":"@{hl}"}"""))
    }

    private fun clickNames(json: String): List<String> =
        ModifierBuilder.applyClickable(Modifier, node(json), data).elements().map { it.javaClass.simpleName }

    @Test
    fun tapBackgroundIsDrawnFromThePressOfANodesTap() {
        // a tap and a tapBackground: the click that draws it while pressed
        val pressed = clickNames("""{"type":"View","onClick":"onTap","tapBackground":"@{tap}"}""")
        assertTrue(pressed.toString(), "ComposedModifier" in pressed)
        assertTrue(pressed.toString(), "ClickableElement" !in pressed)
        // a tap alone: the plain click
        val plain = clickNames("""{"type":"View","onClick":"onTap"}""")
        assertTrue(plain.toString(), "ClickableElement" in plain)
        assertTrue(plain.toString(), "ComposedModifier" !in plain)
        // no tap: nothing is pressed, nothing drawn
        val none = clickNames("""{"type":"View","tapBackground":"@{tap}"}""")
        assertTrue(none.toString(), "ComposedModifier" !in none && "ClickableElement" !in none)
        // a shut tap gate: no tap
        val gated = clickNames("""{"type":"View","onClick":"onTap","canTap":false,"tapBackground":"@{tap}"}""")
        assertTrue(gated.toString(), "ComposedModifier" !in gated)
    }
}
