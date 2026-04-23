package com.kotlinjsonui.dynamic.helpers

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.google.gson.JsonObject
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.dynamic.ResourceCache

/**
 * Parses colors from JSON with binding and resource support.
 * Resolution order:
 *   1. Configuration.colorParser (custom parser — full JSON context)
 *   2. Binding: @{prop} → data map (Color or String)
 *   3. Configuration.themedColorParser(currentThemeMode, key) — theme-aware
 *      hook, runs before the static ResourceCache so generated ColorManager
 *      can own color resolution per active mode (new in 2.3.0)
 *   4. Resource: name → ResourceCache (colors.json / R.color.xxx)
 *   5. Hex: #RRGGBB / #AARRGGBB / RRGGBB
 */
object ColorParser {

    private var cachedContext: Context? = null

    fun init(context: Context) {
        cachedContext = context
        ResourceCache.init(context)
    }

    /**
     * Parse color from JSON key (static only, no binding).
     * Uses Configuration.colorParser if available.
     */
    fun parseColor(json: JsonObject, key: String): Color? {
        Configuration.colorParser?.let { parser ->
            return parser(json, key)
        }
        val colorString = json.get(key)?.asString ?: return null
        return parseColorString(colorString)
    }

    /**
     * Parse color from JSON key with data binding support.
     * @{prop} → looks up data map for Color or String value.
     */
    fun parseColorWithBinding(
        json: JsonObject,
        key: String,
        data: Map<String, Any>,
        context: Context? = cachedContext
    ): Color? {
        val value = json.get(key)?.asString ?: return null
        return parseColorStringWithBinding(value, data, context)
    }

    /**
     * Parse color string with data binding support.
     */
    fun parseColorStringWithBinding(
        value: String?,
        data: Map<String, Any>,
        context: Context? = cachedContext
    ): Color? {
        if (value == null) return null

        // Check binding
        if (value.startsWith("@{") && value.endsWith("}")) {
            val prop = value.drop(2).dropLast(1)
            return when (val bound = data[prop]) {
                is Color -> bound
                is String -> parseColorString(bound, context)
                is Long -> Color(bound)
                is Int -> Color(bound)
                else -> null
            }
        }

        return parseColorString(value, context)
    }

    /**
     * Parse a color string value.
     * Resolution: themedColorParser → ResourceCache → hex parsing.
     */
    fun parseColorString(colorString: String?, context: Context? = cachedContext): Color? {
        if (colorString == null) return null

        // Theme-aware provider (new in 2.3.0). Apps wire the generated
        // ColorManager here so layout color keys follow the current mode.
        Configuration.themedColorParser?.let { parser ->
            parser(Configuration.currentThemeMode, colorString)?.let { return it }
        }

        // Try resource resolution via ResourceCache
        context?.let { ctx ->
            val resolved = ResourceCache.resolveColor(colorString, ctx)
            if (resolved != null && resolved != colorString) {
                return parseHexColor(resolved)
            }
        }

        // Direct hex parsing
        return parseHexColor(colorString)
    }

    private fun parseHexColor(value: String): Color? {
        return try {
            val hex = if (value.startsWith("#")) value else "#$value"
            Color(android.graphics.Color.parseColor(hex))
        } catch (_: Exception) {
            null
        }
    }
}
