package com.kotlinjsonui.dynamic.helpers

import androidx.compose.ui.Modifier
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.junit.Assert.assertNotEquals
import org.junit.Test

/**
 * The data map has to REACH the slot that resolves the binding.
 *
 * Every helper here takes `data` with a default of `emptyMap()`, which keeps
 * the public signatures source-compatible — and quietly turns a forgotten
 * argument into "this layout declared nothing". That is the same silent-drop
 * shape the wave has been chasing, this time manufactured by the fix for it:
 * `applySize` resolved bound bounds correctly while `applyConstraints(result,
 * json)` inside it dropped the map, so the bound min/max fixtures stayed inert
 * after they were supposedly fixed.
 *
 * A modifier chain built WITH the data must differ from the one built without
 * it. That is a property of the call graph, not of any single function, and it
 * is the only thing that catches a missed argument two frames down.
 */
class DataThreadingTest {

    private fun node(json: String): JsonObject =
        Gson().fromJson(json, JsonObject::class.java)

    @Test
    fun boundMinWidthReachesTheConstraint() {
        val json = node("""{"width":"wrapContent","height":200,"minWidth":"@{boundMinWidth}"}""")
        assertNotEquals(
            "the data map did not reach the width bound",
            ModifierBuilder.applySize(Modifier, json, data = emptyMap()),
            ModifierBuilder.applySize(Modifier, json, data = mapOf("boundMinWidth" to 150))
        )
    }

    @Test
    fun boundMinHeightReachesTheConstraint() {
        val json = node("""{"width":200,"height":"wrapContent","minHeight":"@{boundMinHeight}"}""")
        assertNotEquals(
            "the data map did not reach the height bound",
            ModifierBuilder.applySize(Modifier, json, data = emptyMap()),
            ModifierBuilder.applySize(Modifier, json, data = mapOf("boundMinHeight" to 150))
        )
    }

    @Test
    fun boundPaddingReachesThePaddingModifier() {
        val json = node("""{"padding":"@{boundPadding}"}""")
        assertNotEquals(
            "the data map did not reach the padding",
            ModifierBuilder.applyPadding(Modifier, json, emptyMap()),
            ModifierBuilder.applyPadding(Modifier, json, mapOf("boundPadding" to 8))
        )
    }

    @Test
    fun boundCornerRadiusReachesTheBackgroundGroup() {
        val json = node("""{"background":"#DDDDDD","cornerRadius":"@{boundCornerRadius}"}""")
        assertNotEquals(
            "the data map did not reach the corner radius",
            ModifierBuilder.applyBackground(Modifier, json, emptyMap(), null),
            ModifierBuilder.applyBackground(Modifier, json, mapOf("boundCornerRadius" to 12), null)
        )
    }

    @Test
    fun boundMarginsReachTheMarginModifier() {
        val json = node("""{"topMargin":"@{boundTopMargin}"}""")
        assertNotEquals(
            "the data map did not reach the margins",
            ModifierBuilder.applyMargins(Modifier, json, emptyMap()),
            ModifierBuilder.applyMargins(Modifier, json, mapOf("boundTopMargin" to 24))
        )
    }

    @Test
    fun theWholePipelineThreadsTheMap() {
        // buildModifier is what every component actually calls; a helper that
        // resolves correctly but is invoked without the map from here would
        // still leave the attribute inert on screen.
        val json = node(
            """{"width":"wrapContent","height":200,"minWidth":"@{boundMinWidth}",
                "padding":"@{boundPadding}","background":"#DDDDDD"}"""
        )
        assertNotEquals(
            "buildModifier did not thread the data map to its helpers",
            ModifierBuilder.buildModifier(json, emptyMap()),
            ModifierBuilder.buildModifier(
                json, mapOf("boundMinWidth" to 150, "boundPadding" to 8)
            )
        )
    }
}
