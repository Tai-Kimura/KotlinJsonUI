package com.kotlinjsonui.dynamic.components

import com.google.gson.JsonElement
import com.google.gson.JsonObject

/**
 * Whether one row of a non-lazy grid Collection (`lazy: "none"`, columns ≥ 2)
 * sizes to its tallest cell, so a cell whose root is `height: matchParent`
 * fills the row instead of the space left on the screen.
 *
 * The row is a `Row` of `weight(1f)` boxes. Nothing bounded its height, so a
 * cell root drawn `fillMaxHeight()` took every pixel the parent offered — the
 * first row of two cards ran off the screen on a consumer's chat screen
 * (jsonui-cli ticket kjui-collection-grid-row-matchparent-cell-height-
 * unbounded), while iOS draws the same layout with the row at its tallest
 * cell. The codegen face applies the rule its plain Row uses
 * (kjui_tools ModifierBuilder.adjust_for_intrinsic_size!): a matchParent
 * child of a wrapContent Row makes the Row `height(IntrinsicSize.Min)`, and
 * this is the same rule for the grid's rows.
 *
 * `IntrinsicSize.Min` asks every descendant for its intrinsic height, and a
 * SubcomposeLayout (the lazy lists, a pager, a bounded flow's
 * BoxWithConstraints) throws on that question — so a row holding one is left
 * as it was, as the codegen face's subcompose_descendant? leaves it.
 */
internal object GridRowHeight {

    /** The row takes `height(IntrinsicSize.Min)` and its cell boxes `fillMaxHeight()`. */
    fun equalizes(cellRoots: List<JsonObject?>): Boolean =
        cellRoots.any { rootFillsHeight(it) } && cellRoots.none { it != null && subcomposes(it) }

    fun rootFillsHeight(root: JsonObject?): Boolean {
        if (root == null) return false
        val height = stringOf(root, "height") ?: (root.get("frame") as? JsonObject)?.let { stringOf(it, "height") }
        return height == "matchParent"
    }

    /** kjui_tools ModifierBuilder.subcompose_node?, on the cell's layout JSON. */
    fun subcomposes(node: JsonObject): Boolean {
        when (stringOf(node, "type")) {
            "ScrollView", "Scroll" -> return true
            "Collection", "Table" -> {
                val layout = stringOf(node, "layout") ?: stringOf(node, "orientation") ?: "vertical"
                if (layout.lowercase() in setOf("flow", "leftaligned")) return flowTakesBoxWithConstraints(node)
                if (stringOf(node, "lazy") == "none") return false
                if (layout != "horizontal" && stringOf(node, "height") == "wrapContent") return false
                return true
            }
        }
        return listOf("child", "children").any { key -> children(node.get(key)).any { subcomposes(it) } }
    }

    private fun flowTakesBoxWithConstraints(node: JsonObject): Boolean {
        if (stringOf(node, "lazy") == "none") return false
        fun finite(e: JsonElement?): Boolean =
            e != null && e.isJsonPrimitive &&
                (e.asJsonPrimitive.isNumber || (e.asJsonPrimitive.isString && Regex("""\A\d+(\.\d+)?\z""").matches(e.asString)))
        if (finite(node.get("height")) || finite(node.get("maxHeight"))) return false
        val height = stringOf(node, "height") ?: return false
        return height == "matchParent" || (height.startsWith("@{") && height.endsWith("}"))
    }

    private fun children(e: JsonElement?): List<JsonObject> = when {
        e == null -> emptyList()
        e.isJsonObject -> listOf(e.asJsonObject)
        e.isJsonArray -> e.asJsonArray.mapNotNull { if (it.isJsonObject) it.asJsonObject else null }
        else -> emptyList()
    }

    private fun stringOf(o: JsonObject, key: String): String? {
        val e = o.get(key) ?: return null
        return if (e.isJsonPrimitive && e.asJsonPrimitive.isString) e.asString else null
    }
}
