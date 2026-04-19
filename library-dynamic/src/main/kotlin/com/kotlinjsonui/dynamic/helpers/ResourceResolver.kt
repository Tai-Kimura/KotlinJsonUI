package com.kotlinjsonui.dynamic.helpers

import android.content.Context
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.DataBindingContext
import com.kotlinjsonui.dynamic.ResourceCache

/**
 * Runtime equivalent of resource_resolver.rb in kjui_tools.
 * Resolves text, colors, and drawables from JSON with binding + resource support.
 */
object ResourceResolver {

    /**
     * process_text equivalent: resolve text from JSON with binding + string resource support.
     *
     * Resolution order:
     *   1. Binding: @{prop} → data map value (toString)
     *   2. Binding with default: @{prop ?? "default"} → data map or default
     *   3. String resource: key name → ResourceCache.resolveString
     *   4. Literal string
     */
    fun resolveText(
        json: JsonObject,
        key: String,
        data: Map<String, Any>,
        context: Context?
    ): String {
        val raw = json.get(key)?.asString ?: return ""
        return resolveTextValue(raw, data, context)
    }

    /**
     * Resolve a raw text value (not from JSON, but a standalone string).
     */
    fun resolveTextValue(
        value: String,
        data: Map<String, Any>,
        context: Context?
    ): String {
        // Binding expression
        if (value.contains("@{")) {
            return processDataBinding(value, data, context)
        }

        // String resource resolution
        if (context != null) {
            return ResourceCache.resolveString(value, context)
        }

        return value
    }

    /**
     * process_color equivalent: resolve color from JSON key.
     * Delegates to ColorParser.parseColorWithBinding.
     */
    fun resolveColor(
        json: JsonObject,
        key: String,
        data: Map<String, Any>,
        context: Context?
    ): androidx.compose.ui.graphics.Color? {
        return ColorParser.parseColorWithBinding(json, key, data, context)
    }

    /**
     * Resolve color from a raw string value.
     */
    fun resolveColorValue(
        value: String?,
        data: Map<String, Any>,
        context: Context?
    ): androidx.compose.ui.graphics.Color? {
        return ColorParser.parseColorStringWithBinding(value, data, context)
    }

    /**
     * process_drawable equivalent: resolve drawable resource ID.
     *
     * Resolution order:
     *   1. Static resource name → context.resources.getIdentifier (R.drawable.xxx)
     *   2. Binding: @{prop} → data map value (String → getIdentifier)
     *   3. 0 if not found
     */
    fun resolveDrawable(
        value: String?,
        data: Map<String, Any>,
        context: Context
    ): Int {
        if (value == null) return 0

        // Binding
        val resolved = if (value.startsWith("@{") && value.endsWith("}")) {
            val prop = value.drop(2).dropLast(1)
            data[prop]?.toString() ?: return 0
        } else {
            value
        }

        return context.resources.getIdentifier(resolved, "drawable", context.packageName)
    }

    /**
     * Process data binding expressions in text.
     * Replaces @{key} with values from data map.
     * Supports @{key ?? default} for default values.
     * Also resolves string resources via ResourceCache.
     */
    fun processDataBinding(
        text: String,
        data: Map<String, Any>,
        context: Context? = null
    ): String {
        if (!text.contains("@{")) {
            return context?.let { ResourceCache.resolveString(text, it) } ?: text
        }

        var result = text
        val pattern = "@\\{([^}]+)\\}".toRegex()
        pattern.findAll(text).forEach { match ->
            val variable = match.groupValues[1]
            val value = if (variable.contains(" ?? ")) {
                val parts = variable.split(" ?? ")
                val varName = parts[0].trim()
                data[varName]?.toString()
                    ?: parts[1].trim().removeSurrounding("\"")
            } else {
                data[variable]?.toString() ?: ""
            }
            result = result.replace(match.value, value)
        }

        // Resolve string resources on the result
        return context?.let { ResourceCache.resolveString(result, it) } ?: result
    }

    /**
     * Resolve a boolean value from JSON with binding support.
     * Used for enabled, hidden, etc.
     */
    fun resolveBoolean(
        json: JsonObject,
        key: String,
        data: Map<String, Any>,
        default: Boolean = false
    ): Boolean {
        val element = json.get(key) ?: return default
        if (element.isJsonPrimitive) {
            val p = element.asJsonPrimitive
            if (p.isBoolean) return p.asBoolean
            if (p.isString) {
                val s = p.asString
                if (s.startsWith("@{") && s.endsWith("}")) {
                    val prop = s.drop(2).dropLast(1)
                    return when (val bound = data[prop]) {
                        is Boolean -> bound
                        is String -> bound.equals("true", ignoreCase = true)
                        else -> default
                    }
                }
                return s.equals("true", ignoreCase = true)
            }
        }
        return default
    }

    /**
     * Resolve a float value from JSON with binding support.
     * Used for dimensions, alpha, etc.
     */
    fun resolveFloat(
        json: JsonObject,
        key: String,
        data: Map<String, Any>,
        default: Float? = null
    ): Float? {
        val element = json.get(key) ?: return default
        if (element.isJsonPrimitive) {
            val p = element.asJsonPrimitive
            if (p.isNumber) return p.asFloat
            if (p.isString) {
                val s = p.asString
                if (s.startsWith("@{") && s.endsWith("}")) {
                    val prop = s.drop(2).dropLast(1)
                    return when (val bound = data[prop]) {
                        is Number -> bound.toFloat()
                        is String -> bound.toFloatOrNull() ?: default
                        else -> default
                    }
                }
                return s.toFloatOrNull() ?: default
            }
        }
        return default
    }

    /**
     * Resolve a string value from JSON with binding support.
     * Unlike resolveText, this does NOT do string resource resolution.
     * Used for raw values like visibility, contentMode, etc.
     */
    fun resolveString(
        json: JsonObject,
        key: String,
        data: Map<String, Any>,
        default: String? = null
    ): String? {
        val element = json.get(key) ?: return default
        if (!element.isJsonPrimitive) return default
        val s = element.asString
        if (s.startsWith("@{") && s.endsWith("}")) {
            val prop = s.drop(2).dropLast(1)
            return data[prop]?.toString() ?: default
        }
        return s
    }
}
