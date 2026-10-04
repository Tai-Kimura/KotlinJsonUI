package com.kotlinjsonui.conformance

import com.google.gson.JsonElement
import com.google.gson.JsonObject

/**
 * CONTROL BRANCH ONLY (support2/conformance-frames-on-2.43.3, never merged).
 * KotlinJsonUI 2.43.3 has no ModifierBuilder.marginPaddings; this is 2.43.4's
 * function copied verbatim for static values (the frames reader leaves bound
 * margins out anyway), so ConformanceFrames reads 2.43.3 renders with the
 * same rule. 2.43.3's applyMargins drew the same per-edge padding.
 */
object MarginShim {
    data class MarginPaddings(val top: Float, val bottom: Float, val start: Float, val end: Float)

    private fun dimen(e: JsonElement): Float? =
        if (e.isJsonPrimitive && e.asJsonPrimitive.isNumber) e.asFloat
        else if (e.isJsonPrimitive && e.asJsonPrimitive.isString) e.asString.toFloatOrNull() else null

    private fun value(json: JsonObject, key: String): Float? = json.get(key)?.let { dimen(it) }

    fun marginPaddings(json: JsonObject, @Suppress("UNUSED_PARAMETER") data: Map<String, Any>): MarginPaddings? {
        json.get("margins")?.let { element ->
            if (element.isJsonArray) {
                val arr = element.asJsonArray
                return when (arr.size()) {
                    1 -> (dimen(arr[0]) ?: 0f).let { MarginPaddings(it, it, it, it) }
                    2 -> {
                        val v = dimen(arr[0]) ?: 0f
                        val h = dimen(arr[1]) ?: 0f
                        MarginPaddings(v, v, h, h)
                    }
                    4 -> MarginPaddings(dimen(arr[0]) ?: 0f, dimen(arr[2]) ?: 0f, dimen(arr[3]) ?: 0f, dimen(arr[1]) ?: 0f)
                    else -> null
                }
            }
        }
        val top = value(json, "topMargin") ?: value(json, "marginTop") ?: 0f
        val bottom = value(json, "bottomMargin") ?: value(json, "marginBottom") ?: 0f
        val start = value(json, "leftMargin") ?: value(json, "marginLeft") ?: value(json, "startMargin") ?: value(json, "marginStart") ?: 0f
        val end = value(json, "rightMargin") ?: value(json, "marginRight") ?: value(json, "endMargin") ?: value(json, "marginEnd") ?: 0f
        return if (top > 0 || bottom > 0 || start > 0 || end > 0) MarginPaddings(top, bottom, start, end) else null
    }
}
