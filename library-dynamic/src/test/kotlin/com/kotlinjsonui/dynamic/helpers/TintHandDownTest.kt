package com.kotlinjsonui.dynamic.helpers

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * A container's tintColor reaches the controls inside it (LocalJsonUITint): a
 * node that holds other nodes and declares a tintColor provides it, and every
 * control reads its own tintColor first and the handed-down one second. The
 * composition itself runs on a device; here, which nodes hand it down and that
 * each control's read falls back to the local.
 */
class TintHandDownTest {

    private fun node(json: String): JsonObject = JsonParser.parseString(json).asJsonObject

    @Test
    fun aNodeHoldingOthersHandsItsTintDown() {
        assertTrue(TintHandDown.handsDown(node("""{"type":"View","tintColor":"#FF0000","child":[{"type":"Switch"}]}""")))
        assertTrue(TintHandDown.handsDown(node("""{"type":"View","tintColor":"@{accent}","children":[{"type":"Label"}]}""")))
        assertTrue(TintHandDown.handsDown(node("""{"type":"ScrollView","tintColor":"#FF0000","child":{"type":"View"}}""")))
    }

    @Test
    fun aNodeDrawingALayoutOfItsOwnHandsItDown() {
        for (type in listOf("Collection", "Embed", "TabView")) {
            assertTrue(type, TintHandDown.handsDown(node("""{"type":"$type","tintColor":"#FF0000"}""")))
        }
    }

    @Test
    fun aLeafOrANodeWithoutATintHandsNothingDown() {
        assertFalse(TintHandDown.handsDown(node("""{"type":"Switch","tintColor":"#FF0000"}""")))
        assertFalse(TintHandDown.handsDown(node("""{"type":"View","tintColor":"#FF0000"}""")))
        assertFalse(TintHandDown.handsDown(node("""{"type":"View","tintColor":"#FF0000","child":[]}""")))
        assertFalse(TintHandDown.handsDown(node("""{"type":"View","child":[{"type":"Switch"}]}""")))
        assertFalse(TintHandDown.handsDown(node("""{"type":"View","tintColor":"","child":[{"type":"Switch"}]}""")))
    }

    private fun code(path: String): String =
        File(path).readText()
            .replace(Regex("""/\*[\s\S]*?\*/"""), "")
            .lines().joinToString("\n") { it.substringBefore("//") }

    /**
     * Each control reads the handed-down tint where it draws its accent, after
     * its own (the library's Segment and CustomTextField — the Segment's
     * indicator and the text fields' caret — read it in the library).
     */
    @Test
    fun everyControlFallsBackToTheHandedDownTint() {
        val c = "src/main/kotlin/com/kotlinjsonui/dynamic/components/"
        val reads = mapOf(
            "DynamicSwitchComponent.kt" to 2,
            "DynamicCheckBoxComponent.kt" to 1,
            "DynamicSliderComponent.kt" to 2,
            "DynamicProgressComponent.kt" to 1,
            "DynamicRadioComponent.kt" to 2,
            "DynamicTabViewComponent.kt" to 2,
        )
        val found = reads.keys.associateWith { Regex("""jsonUITintOr(Null)?\(""").findAll(code(c + it)).count() }
        assertEquals(reads, found)
        val view = code("src/main/kotlin/com/kotlinjsonui/dynamic/DynamicView.kt")
        assertTrue(view.contains("TintHandDown.color(responsiveJson, effectiveData, context)"))
        assertTrue(view.contains("CompositionLocalProvider(LocalJsonUITint provides tintHandedDown)"))
    }
}
