package com.kotlinjsonui.dynamic.helpers

import androidx.compose.ui.graphics.Color
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.TypeSynonyms

/**
 * A container's `tintColor`, handed down to the controls inside it
 * (KotlinJsonUI's `LocalJsonUITint`).
 *
 * tintColor is the accent of a node's operable parts — a control's accent, a
 * link's colour, the cursor — never the text colour (jsonui-cli 1.9.0 ruling).
 * On iOS `.tint` on a container reaches the controls inside it. DynamicView
 * provides the local around what a node composes when the node holds other
 * nodes — its children, or a layout it draws in a composable of its own (a
 * Collection's cells, an Embed's screen, a TabView's tabs) — and every control
 * reads its own tintColor first and the local second. kjui's codegen
 * (InheritedTint) provides and reads the same local by the same rule.
 */
object TintHandDown {
    /** The types that compose a layout of their own, whatever their children. */
    private val HOLDERS = setOf("Collection", "Embed", "TabView")

    /** Whether [json] hands its tintColor down: it declares one and holds other nodes. */
    fun handsDown(json: JsonObject): Boolean {
        val tint = json.get("tintColor") ?: return false
        if (!tint.isJsonPrimitive || !tint.asJsonPrimitive.isString || tint.asString.isEmpty()) return false
        val children = listOf("child", "children").any { key ->
            val value = json.get(key)
            when {
                value == null -> false
                value.isJsonArray -> value.asJsonArray.any { it.isJsonObject }
                else -> value.isJsonObject
            }
        }
        if (children) return true
        val type = json.get("type")?.takeIf { it.isJsonPrimitive }?.asString ?: return false
        return TypeSynonyms.drawnType(type) in HOLDERS
    }

    /** The colour [json] hands down, or null when it hands none (or it resolves to none). */
    fun color(json: JsonObject, data: Map<String, Any>, context: android.content.Context?): Color? {
        if (!handsDown(json)) return null
        return ColorParser.parseColorStringWithBinding(json.get("tintColor").asString, data, context)
    }
}
