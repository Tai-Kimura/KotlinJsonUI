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

        // Binding — canonical whole-value string resolution (flat-first,
        // dot paths, `?? default`); unresolved → 0 (no drawable)
        val resolved = if (value.startsWith("@{") && value.endsWith("}")) {
            DataBindingContext.resolveString(value, data) ?: return 0
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

        // DataBindingContext is the canonical resolver: flat-key-first
        // lookup, dot paths ("profile.name"), bracket array access
        // ("items[0].title"), `?? default` and canonical stringification
        // (integral numbers render without a decimal point). Unresolved
        // occurrences render as empty strings.
        val result = DataBindingContext.processBindings(text, data)

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
                    // Canonical boolean value context: dot paths, `??`
                    // default, negation (@{!prop}) and bool coercion
                    // (bool / integral number / "true"/"1"/"false"/"0").
                    return DataBindingContext.resolveBoolean(s, data) ?: default
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
                    // Canonical number value context: dot paths, `??`
                    // default, number-or-numeric-string coercion.
                    return DataBindingContext.resolveNumber(s, data)?.toFloat() ?: default
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
            // Canonical string value context: dot paths, `?? default`,
            // canonical stringification (objects/arrays stay unresolved).
            return DataBindingContext.resolveString(s, data) ?: default
        }
        return s
    }
}
