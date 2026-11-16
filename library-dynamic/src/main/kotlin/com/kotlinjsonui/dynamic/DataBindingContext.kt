package com.kotlinjsonui.dynamic

import androidx.compose.runtime.*
import com.google.gson.JsonElement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * DataBindingContext manages the data state for dynamic UI components
 *
 * Features:
 * - Two-way data binding support
 * - Reactive state management
 * - Expression evaluation
 * - Default value handling
 *
 * The companion object is the CANONICAL binding-expression resolver for
 * the dynamic runtime (renderer SSoT track 15 — see jsonui-cli
 * `shared/core/binding_semantics.json`, vendored test vectors in
 * `src/test/resources/binding_vectors.json`). Every `@{...}` resolution
 * site in library-dynamic routes through these helpers so that path
 * traversal, `??` defaults, negation, coercion and stringification behave
 * identically across text, typed-value and embed-params contexts.
 *
 * Canonical semantics summary:
 * - Path: FLAT key lookup first (the raw inner string as a map key), then
 *   dot-path traversal with bracket array index (`items[0].title`).
 *   Out-of-range index, non-array container or a missing / non-object
 *   intermediate node ⇒ unresolved (never a crash).
 * - `??` default: split on the FIRST `??` only; whitespace-insensitive.
 *   Default literals: double- or single-quoted string, `true`/`false`,
 *   number, `null` (= unresolved). Anything else fails closed to
 *   unresolved. A RESOLVED value (including `false` / `0` / `""`) always
 *   wins over the default.
 * - Negation `@{!path}`: boolean value contexts only — resolve, coerce to
 *   bool, invert. Unresolved stays unresolved (no inversion).
 * - Unresolved: text context ⇒ empty string per occurrence; typed value
 *   context ⇒ null to the caller (the attribute default applies).
 */
class DataBindingContext {
    private val _data = MutableStateFlow<Map<String, Any>>(emptyMap())
    val data: StateFlow<Map<String, Any>> = _data.asStateFlow()

    /**
     * Updates a single value in the data context
     */
    fun updateValue(key: String, value: Any) {
        _data.value = _data.value.toMutableMap().apply {
            put(key, value)
        }
    }

    /**
     * Updates multiple values in the data context
     */
    fun updateValues(updates: Map<String, Any>) {
        _data.value = _data.value.toMutableMap().apply {
            putAll(updates)
        }
    }

    /**
     * Gets a value from the data context
     */
    fun getValue(key: String): Any? {
        return _data.value[key]
    }

    /**
     * Clears all data from the context
     */
    fun clear() {
        _data.value = emptyMap()
    }

    /**
     * Sets the entire data map
     */
    fun setData(data: Map<String, Any>) {
        _data.value = data
    }

    companion object {
        private val BINDING_PATTERN = "@\\{([^}]+)\\}".toRegex()
        private val INDEX_SEGMENT = Regex("""^([^\[\]]+)\[(\d+)]$""")
        private val QUOTED_DOUBLE = Regex("^\"[^\"]*\"$")
        private val QUOTED_SINGLE = Regex("^'[^']*'$")

        /**
         * Evaluates a whole binding expression like `@{variable}` or
         * `@{path ?? default}` against the data map.
         *
         * Returns the resolved value, the typed default when the path is
         * unresolved, or null when the whole expression is unresolved.
         * A non-binding input string is returned unchanged (legacy
         * behavior relied on by callers that pass raw values through).
         *
         * Negation (`@{!path}`) is NOT interpreted here — it is only
         * meaningful in boolean value contexts; use [resolveBoolean].
         */
        fun evaluateExpression(expression: String, data: Map<String, Any>): Any? {
            if (!expression.startsWith("@{") || !expression.endsWith("}")) {
                return expression
            }
            return evaluateInner(expression.substring(2, expression.length - 1), data)
        }

        /**
         * [evaluateExpression] over the INNER expression (without the
         * `@{...}` wrapper) — the shape [com.kotlinjsonui.dynamic
         * .generated.AttrValue.Binding] carries.
         */
        fun evaluateInner(inner: String, data: Map<String, Any>): Any? {
            val trimmed = inner.trim()
            val defaultIdx = trimmed.indexOf("??")
            if (defaultIdx >= 0) {
                // Split on the FIRST `??` only; the remainder must parse as
                // a single default literal or the default fails closed.
                val path = trimmed.substring(0, defaultIdx).trim()
                val resolved = resolvePath(path, data)
                if (resolved != null) return resolved
                return parseDefaultLiteral(trimmed.substring(defaultIdx + 2).trim())
            }
            return resolvePath(trimmed, data)
        }

        /**
         * Canonical path resolution: FLAT key first, then dot-path
         * traversal with bracket array index. Null ⇒ unresolved.
         */
        fun resolvePath(path: String, data: Map<String, Any>): Any? {
            if (path.isEmpty()) return null
            // Flat key shadows the nested path (determinism guarantee).
            if (data.containsKey(path)) return unwrapScalar(data[path])
            var current: Any? = data
            for (segment in path.split(".")) {
                current = resolveSegment(current, segment) ?: return null
            }
            return unwrapScalar(current)
        }

        private fun resolveSegment(current: Any?, segment: String): Any? {
            val match = INDEX_SEGMENT.matchEntire(segment)
            if (match != null) {
                val index = match.groupValues[2].toIntOrNull() ?: return null
                val container = lookupKey(current, match.groupValues[1])
                return when (container) {
                    is List<*> -> container.getOrNull(index)
                    is Array<*> -> container.getOrNull(index)
                    is com.google.gson.JsonArray ->
                        if (index < container.size()) container.get(index) else null
                    else -> null // non-array / missing ⇒ unresolved
                }
            }
            return lookupKey(current, segment)
        }

        /** Map-like key lookup; non-object intermediates ⇒ unresolved. */
        private fun lookupKey(current: Any?, key: String): Any? = when (current) {
            is Map<*, *> -> current[key]
            is com.google.gson.JsonObject -> current.get(key)
            else -> null
        }

        /**
         * Unwrap gson scalar wrappers that leak into data maps (include
         * data, embed params) so downstream coercion sees plain values.
         * JsonNull stays unresolved.
         */
        private fun unwrapScalar(value: Any?): Any? = when (value) {
            is JsonElement -> when {
                value.isJsonNull -> null
                value.isJsonPrimitive -> {
                    val p = value.asJsonPrimitive
                    when {
                        p.isBoolean -> p.asBoolean
                        p.isNumber -> p.asNumber
                        else -> p.asString
                    }
                }
                else -> value
            }
            else -> value
        }

        /**
         * Parses a `??` default literal. Canonical literal forms only:
         * double- OR single-quoted string, true/false, number, null.
         * Anything else (barewords, a second `??`, ...) fails closed to
         * unresolved (null).
         */
        private fun parseDefaultLiteral(literal: String): Any? {
            return when {
                literal == "null" -> null
                literal == "true" -> true
                literal == "false" -> false
                QUOTED_DOUBLE.matches(literal) || QUOTED_SINGLE.matches(literal) ->
                    literal.substring(1, literal.length - 1)
                else -> literal.toIntOrNull() ?: literal.toDoubleOrNull()
            }
        }

        // ── Canonical coercion ────────────────────────────────────────

        /**
         * Bool coercion: Bool; integral number != 0; String "true"/"1" ⇒
         * true, "false"/"0" ⇒ false (case-insensitive). Else unresolved.
         */
        fun coerceToBoolean(value: Any?): Boolean? = when (value) {
            is Boolean -> value
            is Number -> {
                val d = value.toDouble()
                if (d.isFinite() && d == Math.floor(d)) d != 0.0 else null
            }
            is String -> when (value.lowercase()) {
                "true", "1" -> true
                "false", "0" -> false
                else -> null
            }
            else -> null
        }

        /** Number coercion: number or numeric string; else unresolved. */
        fun coerceToNumber(value: Any?): Double? = when (value) {
            is Number -> value.toDouble()
            is String -> value.toDoubleOrNull()
            else -> null
        }

        /**
         * Canonical text stringification: integral numbers render WITHOUT
         * a decimal point ("5", not "5.0" — gson parses JSON ints as
         * Double); bool "true"/"false"; null / Map / List ⇒ unresolved
         * (null — the text context renders it as an empty string).
         */
        fun stringify(value: Any?): String? = when (value) {
            null -> null
            is String -> value
            is Boolean -> value.toString()
            is Number -> stringifyNumber(value)
            is Map<*, *>, is List<*>, is Array<*> -> null
            is JsonElement -> stringify(unwrapScalar(value).takeIf { it !is JsonElement })
            else -> value.toString()
        }

        private fun stringifyNumber(n: Number): String {
            if (n is Int || n is Long || n is Short || n is Byte || n is java.math.BigInteger) {
                return n.toString()
            }
            val d = n.toDouble()
            if (d.isFinite() && d == Math.floor(d) && kotlin.math.abs(d) < 1e15) {
                return d.toLong().toString()
            }
            return n.toString()
        }

        // ── Typed whole-value resolvers (value context) ───────────────

        /**
         * Boolean value context: supports negation (`@{!path}` — resolve,
         * coerce, invert; unresolved stays unresolved). Null ⇒ unresolved
         * (the caller's attribute default applies).
         */
        fun resolveBoolean(expression: String, data: Map<String, Any>): Boolean? {
            if (!expression.startsWith("@{") || !expression.endsWith("}")) return null
            return resolveBooleanInner(expression.substring(2, expression.length - 1), data)
        }

        /** [resolveBoolean] over the inner expression (AttrValue.Binding shape). */
        fun resolveBooleanInner(inner: String, data: Map<String, Any>): Boolean? {
            val trimmed = inner.trim()
            if (trimmed.startsWith("!")) {
                // Negation form is `!path` only (no `??` default) — an
                // unresolved path stays unresolved, no inversion.
                val resolved = resolvePath(trimmed.substring(1).trim(), data) ?: return null
                return coerceToBoolean(resolved)?.not()
            }
            return coerceToBoolean(evaluateInner(trimmed, data))
        }

        /** Number value context. Null ⇒ unresolved (caller default applies). */
        fun resolveNumber(expression: String, data: Map<String, Any>): Double? {
            if (!expression.startsWith("@{") || !expression.endsWith("}")) return null
            return resolveNumberInner(expression.substring(2, expression.length - 1), data)
        }

        /** [resolveNumber] over the inner expression (AttrValue.Binding shape). */
        fun resolveNumberInner(inner: String, data: Map<String, Any>): Double? =
            coerceToNumber(evaluateInner(inner, data))

        /**
         * String value context: scalars stringify canonically; objects and
         * arrays do NOT stringify into a value context (null ⇒ unresolved).
         */
        fun resolveString(expression: String, data: Map<String, Any>): String? {
            if (!expression.startsWith("@{") || !expression.endsWith("}")) return null
            return resolveStringInner(expression.substring(2, expression.length - 1), data)
        }

        /** [resolveString] over the inner expression (AttrValue.Binding shape). */
        fun resolveStringInner(inner: String, data: Map<String, Any>): String? =
            stringify(evaluateInner(inner, data))

        // ── Text interpolation (text context) ─────────────────────────

        /**
         * Replaces every `@{...}` occurrence in a mixed-text string with
         * its stringified resolved value; unresolved occurrences render as
         * empty strings.
         */
        fun processBindings(text: String, data: Map<String, Any>): String {
            return BINDING_PATTERN.replace(text) { matchResult ->
                stringify(evaluateExpression(matchResult.value, data)) ?: ""
            }
        }
    }
}

/**
 * Composable that provides a DataBindingContext
 */
@Composable
fun rememberDataBindingContext(
    initialData: Map<String, Any> = emptyMap()
): DataBindingContext {
    return remember {
        DataBindingContext().apply {
            setData(initialData)
        }
    }
}

/**
 * Composable that provides the current data state from a DataBindingContext
 */
@Composable
fun DataBindingContext.collectAsState(): State<Map<String, Any>> {
    return data.collectAsState()
}
