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

    /**
     * How a grid row's cells sit in it:
     * - [Mode.FILL] the row takes `height(IntrinsicSize.Min)` and its cell boxes `fillMaxHeight()`;
     * - [Mode.OWN] a cell root of height matchParent holds an image whose height its picture decides
     *   (jsonui-cli shared/core/grid_row_height.json, vendored as [RESOURCE]): IntrinsicSize.Min would take the
     *   picture's own pixel height (a 128 px placeholder, a 600 px picture, measured on an emulator), so each cell
     *   is its own height — its box measures it with no height bound, not the parent's remaining height. The row is
     *   not aligned on Android, where iOS aligns it (jsonui-cli ticket kjui-collection-lazy-grid-and-flow-rows-do-
     *   not-align-cell-heights). kjui's codegen reads the same table (CollectionComponent.cell_row_mode);
     * - [Mode.NONE] as it was: no root of height matchParent, or a SubcomposeLayout in a cell.
     */
    enum class Mode { FILL, OWN, NONE }

    fun mode(cellRoots: List<JsonObject?>): Mode {
        if (cellRoots.none { rootFillsHeight(it) }) return Mode.NONE
        if (cellRoots.any { it != null && subcomposes(it) }) return Mode.NONE
        return if (cellRoots.any { it != null && contentSizedImage(it) }) Mode.OWN else Mode.FILL
    }

    /** The row takes `height(IntrinsicSize.Min)` and its cell boxes `fillMaxHeight()`. */
    fun equalizes(cellRoots: List<JsonObject?>): Boolean = mode(cellRoots) == Mode.FILL

    /** Where the vendored table sits among this library's Java resources. */
    const val RESOURCE: String = "/com/kotlinjsonui/dynamic/grid_row_height.json"

    private val imageTypes: Set<String> by lazy {
        val stream = GridRowHeight::class.java.getResourceAsStream(RESOURCE)
            ?: throw IllegalStateException("KotlinJsonUI: $RESOURCE is not on the classpath (it ships inside library-dynamic)")
        val root = stream.use { com.google.gson.JsonParser.parseReader(java.io.InputStreamReader(it, Charsets.UTF_8)) }.asJsonObject
        root.getAsJsonObject("contentSizedImages").getAsJsonArray("types").map { it.asString }.toSet()
    }

    private val fixedHeight = Regex("""\A\d+(\.\d+)?\z""")

    /**
     * An image whose height its picture decides at or below [node] (styles merged and includes expanded by the
     * caller): its type, read as its canonical type (TypeSynonyms), is in the table, and its height (or
     * frame.height) is not a fixed number.
     */
    fun contentSizedImage(node: JsonObject): Boolean {
        val type = stringOf(node, "type")
        if (type != null && (com.kotlinjsonui.dynamic.TypeSynonyms.entries[type]?.canonical ?: type) in imageTypes) {
            val height = node.get("height") ?: (node.get("frame") as? JsonObject)?.get("height")
            val fixed = height != null && height.isJsonPrimitive &&
                (height.asJsonPrimitive.isNumber || (height.asJsonPrimitive.isString && fixedHeight.matches(height.asString)))
            if (!fixed) return true
        }
        return listOf("child", "children").any { key -> children(node.get(key)).any { contentSizedImage(it) } }
    }

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
