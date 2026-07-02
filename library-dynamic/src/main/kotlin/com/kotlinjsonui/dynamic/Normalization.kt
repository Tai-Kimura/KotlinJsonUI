package com.kotlinjsonui.dynamic

import androidx.compose.runtime.compositionLocalOf
import com.google.gson.JsonObject

/**
 * Helpers around the `$jui` normalization marker that `jui build`
 * (jui_tools normalizer, `"build": {"normalizeLayouts": true}`) writes
 * into distributed layout JSON:
 *
 * ```json
 * { "$jui": { "normalized": "L1", "schemaVersion": 1 }, ... }
 * ```
 *
 * A layout that carries the L1 (or higher, e.g. L2) marker has already
 * had alias attribute spellings rewritten to their canonical names, so
 * consumers may take the canonical-only code path and skip alias
 * fallbacks. Raw (L0) layouts keep the legacy alias-fallback behavior.
 *
 * The marker only ever appears on the layout ROOT object; child nodes
 * inherit the state through [LocalLayoutCanonicalized], provided by the
 * root `DynamicView` call. The marker key itself is build metadata, not
 * a renderable attribute — it is ignored (never warned about) by the
 * renderer.
 */
object Normalization {
    const val MARKER_KEY: String = "\$jui"

    private val CANONICAL_LEVELS = setOf("L1", "L2")

    /**
     * True when [layout] carries a normalization marker of at least L1
     * (L2 includes L1 canonicalization).
     */
    fun isCanonicalized(layout: JsonObject): Boolean {
        val marker = layout.get(MARKER_KEY) ?: return false
        if (!marker.isJsonObject) return false
        val level = marker.asJsonObject.get("normalized") ?: return false
        if (!level.isJsonPrimitive || !level.asJsonPrimitive.isString) return false
        return level.asString in CANONICAL_LEVELS
    }
}

/**
 * True while composing the subtree of a layout whose root carried the
 * `$jui` L1/L2 normalization marker. Components consult this to skip
 * alias-spelling fallbacks (the normalizer has already rewritten them,
 * so any leftover alias spelling in the input is stale).
 */
val LocalLayoutCanonicalized = compositionLocalOf { false }
