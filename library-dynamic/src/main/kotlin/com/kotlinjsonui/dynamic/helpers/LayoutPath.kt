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
