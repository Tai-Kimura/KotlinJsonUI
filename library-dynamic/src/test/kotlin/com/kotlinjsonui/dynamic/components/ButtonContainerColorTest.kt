package com.kotlinjsonui.dynamic.components

import androidx.compose.ui.graphics.Color
import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.generated.ButtonAttributes
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

/**
 * A Button's container while it is pressed is `tapBackground`, else
 * `highlightBackground` (4f ruling, 2026-09-26): Button declares no
 * `highlighted` — View does — and its highlightBackground is UIButton's
 * highlighted, the pressed state. tapBackground wins when both are set.
 */
class ButtonContainerColorTest {

    private val base = Color(0xFF111111)
    private val tap = Color(0xFF00FF00)
    private val highlight = Color(0xFFFF0000)

    @Test
    fun thePressedColourIsTheContainerOnlyWhilePressed() {
        assertEquals(tap, DynamicButtonComponent.containerColorOf(base, tap, pressed = true))
        assertEquals(base, DynamicButtonComponent.containerColorOf(base, tap, pressed = false))
        assertEquals(base, DynamicButtonComponent.containerColorOf(base, null, pressed = true))
    }

    private fun pressed(json: String, data: Map<String, Any>): Color? {
        val node = JsonParser.parseString(json).asJsonObject
        val a = ButtonAttributes.parse(TypedAttrs.toAttrMap(node))
        return DynamicButtonComponent.pressedColorOf(a, data, null)
    }

    /** Colours through bindings to a Color: hex parsing needs Android's parser. */
    @Test
    fun thePressedColourIsTapBackgroundElseHighlightBackground() {
        val d = mapOf<String, Any>("t" to tap, "h" to highlight)
        assertEquals(tap, pressed("""{"type":"Button","text":"x","tapBackground":"@{t}","highlightBackground":"@{h}"}""", d))
        assertEquals(highlight, pressed("""{"type":"Button","text":"x","highlightBackground":"@{h}"}""", d))
        assertEquals(tap, pressed("""{"type":"Button","text":"x","tapBackground":"@{t}"}""", d))
        // `hilightBackground` is declared nowhere: not read
        assertEquals(null, pressed("""{"type":"Button","text":"x","hilightBackground":"@{h}"}""", d))
        assertEquals(null, pressed("""{"type":"Button","text":"x"}""", d))
        // `highlighted` is not a Button's: it changes nothing
        assertEquals(highlight, pressed("""{"type":"Button","text":"x","highlighted":false,"highlightBackground":"@{h}"}""", d))
    }

    /** The Button reads no `highlighted` — by the source. */
    @Test
    fun theButtonReadsNoHighlighted() {
        val code = File("src/main/kotlin/com/kotlinjsonui/dynamic/components/DynamicButtonComponent.kt").readLines()
            .map { l -> l.trimStart().let { t -> if (t.startsWith("//") || t.startsWith("*")) "" else l } }
            .joinToString("\n")
        assertEquals(1, Regex("""containerColorOf\(backgroundColor, pressedBgColor, isPressed\)""").findAll(code).count())
        assertEquals(0, Regex("""resolveHighlighted|"highlighted"""").findAll(code).count())
    }
}
