package com.kotlinjsonui.dynamic.helpers

import com.google.gson.JsonElement
import com.google.gson.JsonObject

/**
 * A node's position in its layout, as a name — for a node that needs a name
 * the layout does not give it (a Radio item's value). The rule is jsonui-cli's
 * `shared/core/layout_path.rb` (JsonUIShared::LayoutPath), which the kjui and
 * sjui codegen run: this is its Kotlin twin, held to the same table
 * (`shared/core/layout_path_vectors.json`, copied byte-identical into
 * src/test/resources; LayoutPathVectorsTest), so a layout drawn here and the
 * code generated from it name the node alike.
 *
 * An explicit `id` wins; this is the name when there is none. It is a
 * function of the tree alone — no clock, no counter, no randomness — so it is
 * the same on every composition (a Radio item named by the clock was a new
 * name on each one, and so was never the selected one).
 *
 * The root is `0`; each step appends `_<index>` of the node in its parent's
 * child list. The index is the position as written: a single-object `child`
 * is index 0; a non-object entry still takes its index; a node that carries
 * both `child` and `children` counts them as one list, `child` first.
 *
 * Where it is taken: DynamicView calls [enter] on every node it draws, after
 * the node's include is expanded and its style merged and before the
 * responsive resolution / type synonyms copy it — so the tree is the
 * include-expanded, style-merged one, as the codegen stamps it. An include
 * takes the one index of the root it expands to: the include node carries its
 * stamp, and the expansion merges the include node's keys onto the included
 * root (IncludeExpander.expandInclude), so the root arrives with it and its
 * children are stamped from it. A node no parent stamped — the root of a
 * DynamicView call, a layout loaded by name (Embed, a cell, a tab) — is `0`,
 * as the codegen's lazy stamp names a node no build entry reached.
 */
object LayoutPath {
    /** The key the stamp is written under (the codegen's KEY). */
    const val KEY = "_layoutPath"

    /** The node's path: its parent's stamp, or `0` — a node no parent stamped is a root. */
    fun of(node: JsonObject): String {
        val stamp = node.get(KEY)
        return if (stamp != null && stamp.isJsonPrimitive) stamp.asString else "0"
    }

    /**
     * The one step DynamicView takes at each node it draws: stamps each child
     * with this node's path and the child's index. Writes on the child
     * objects (the copies the responsive resolution and the type synonyms
     * make later carry it); the node's own stamp is its parent's business.
     */
    fun enter(node: JsonObject): String {
        val path = of(node)
        children(node).forEachIndexed { index, child ->
            if (child.isJsonObject) child.asJsonObject.addProperty(KEY, "${path}_$index")
        }
        return path
    }

    /**
     * A handler's viewId: the node's `id`, else its drawn type with the first
     * letter lowercased and its position — `switch_0_1`, `selectBox_0_3`
     * (jsonui-cli shared/core/layout_path.rb `view_id`, held to the same
     * table: layout_path_vectors.json `view_id_cases`). A Radio item's
     * `radio_<path>` is the same form. The handlers were handed a kind word
     * (`switch`, `selectbox`, …) or, on the click / gesture / lifecycle
     * paths, the id or nothing — so two id-less nodes handed theirs the same
     * viewId, and an id-less `(String)` handler was not called at all.
     */
    fun viewId(node: JsonObject): String {
        // Any written id wins, "" too (the shared rule, and SwiftJsonUI's)
        val id = node.get("id")
        if (id != null && id.isJsonPrimitive) return id.asString
        val type = drawnType(node.get("type")?.takeIf { it.isJsonPrimitive }?.asString ?: "")
        val head = type.take(1).lowercase() + type.drop(1)
        return "${head}_${of(node)}"
    }

    /**
     * The type a spelling is drawn as — TypeSynonyms.drawnType, the rule every
     * classifier here reads, and the codegen's (layout_path.rb reads
     * TypeSynonyms.drawn_type): a type the app draws itself
     * (Configuration.customComponentTypes) as written; else a synonym as its
     * `render_as` or canonical section (Picker → SelectBox, Text → Label,
     * CircleImage → CircleImage), then a declared alias section as the
     * section it names (Toggle → Switch, EditText → TextField). It kept a
     * copy of the rule without the app's types, so an app's own ProgressBar
     * was named `progress_<path>` here and `progressBar_<path>` by the
     * codegen.
     */
    fun drawnType(type: String): String = com.kotlinjsonui.dynamic.TypeSynonyms.drawnType(type)

    /**
     * The [index]-th of a list of roots (DynamicViews), read as the child
     * list of a root: `0_<index>`, the name the codegen gives a root's
     * children — the use DynamicViews is documented for, a container's
     * contents. Every entry was `0`, so the siblings' id-less nodes shared
     * their viewIds and Radio groups. An entry a parent already stamped keeps
     * its stamp. Returns a copy (the caller's object is not stamped: drawn
     * alone later, it is a root, `0`). A single root — DynamicView(json),
     * SwiftJsonUI's DynamicView(component:) — is `0` on both runtimes;
     * SwiftJsonUI has no list-of-roots entry.
     */
    fun listEntry(node: JsonObject, index: Int): JsonObject {
        if (node.has(KEY)) return node
        val copy = JsonObject()
        for ((key, value) in node.entrySet()) copy.add(key, value)
        copy.addProperty(KEY, "0_$index")
        return copy
    }

    /** The list positions count over: `child` then `children`, every entry, object or not. */
    fun children(node: JsonObject): List<JsonElement> =
        listOf("child", "children").flatMap { key ->
            val value = node.get(key)
            when {
                value == null -> emptyList()
                value.isJsonArray -> value.asJsonArray.toList()
                value.isJsonObject -> listOf(value)
                else -> emptyList()
            }
        }
}
