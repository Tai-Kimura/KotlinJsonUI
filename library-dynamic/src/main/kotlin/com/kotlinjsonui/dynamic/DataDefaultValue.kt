package com.kotlinjsonui.dynamic

import com.google.gson.JsonElement

/**
 * What a data property's `defaultValue` means in dynamic mode — the reading
 * the code generators apply (jsonui-cli `JsonUIShared::StringLiterals.default_text`
 * and `TypeConverterCore#normalize_data_property`), so a layout shows the
 * same text generated or dynamic. Measured against the same table:
 * `src/test/resources/string_default_vectors.json`, vendored byte-identical
 * from jsonui-cli `shared/core/string_default_vectors.json`
 * (StringDefaultVectorsTest; the COPIES list in CI compares the copy).
 *
 * Until 2.42.0 dynamic mode took the value as it stood: a `"'…'"` or
 * `"\"…\""` spelling showed its quotes, a value written per platform
 * reached the data map as a Map, and a String holding `(` or `)` was
 * dropped as if it were a constructor.
 */
internal object DataDefaultValue {
    private const val LANGUAGE = "kotlin"

    // Dynamic mode renders Compose; `xml` is the codegen's other mode.
    private val MODES = listOf("compose", "xml")

    private val PLATFORM_LANGUAGES = setOf("swift", "kotlin", "typescript")

    /**
     * The element this platform reads from [value]: the `kotlin` entry of a
     * value written per platform (`{ "swift": …, "kotlin": … }`, and its
     * `compose` entry when written per mode), the value itself otherwise —
     * or null when it is written per platform and names no `kotlin`.
     */
    fun select(value: JsonElement): JsonElement? {
        if (!value.isJsonObject) return value
        val entries = value.asJsonObject
        val mine = entries.get(LANGUAGE)
        if (mine != null) {
            if (mine.isJsonObject) {
                MODES.forEach { mode -> mine.asJsonObject.get(mode)?.let { return it } }
            }
            return mine
        }
        val keys = entries.keySet()
        return if (keys.isNotEmpty() && keys.all { it in PLATFORM_LANGUAGES }) null else value
    }

    /** The declared class, itself possibly written per platform. */
    fun className(declared: JsonElement?): String? {
        val chosen = declared?.let { select(it) } ?: return null
        return if (chosen.isJsonPrimitive && chosen.asJsonPrimitive.isString) chosen.asString else null
    }

    /**
     * A class's value when the layout gives this platform none: the
     * vocabulary `jui g project` writes a spec's types with — "" / 0 / 0.0 /
     * false / [] — and null (no value) for an optional or any other class.
     */
    fun vocabulary(className: String?): Any? {
        val type = className?.trim() ?: return null
        if (type.endsWith("?")) return null
        return when (type) {
            "String" -> ""
            "Int", "Integer" -> 0
            "Double", "Float", "CGFloat" -> 0.0
            "Bool", "Boolean" -> false
            else -> {
                val list = type.startsWith("Array(") || type.startsWith("List<") ||
                    (type.startsWith("[") && type.endsWith("]") && !type.contains(":"))
                if (list) emptyList<Any?>() else null
            }
        }
    }

    /**
     * The text a String default's spelling means:
     *   bare (canonical)  the text as written
     *   ''                empty
     *   "…"               JSON's escapes; when they do not read as JSON (an
     *                     escape JSON does not have, a bare `"`, a raw
     *                     control character, a lone surrogate), the text
     *                     between the quotes as written
     *   '…'               the text between the quotes as written
     */
    fun text(raw: String): String {
        if (raw == "''") return ""
        if (raw.length >= 2 && raw.startsWith("\"") && raw.endsWith("\"")) {
            val inner = raw.substring(1, raw.length - 1)
            return jsonString(inner) ?: inner
        }
        if (raw.length >= 2 && raw.startsWith("'") && raw.endsWith("'")) {
            return raw.substring(1, raw.length - 1)
        }
        return raw
    }

    /** [inner] read as the inside of a JSON string, or null when it is not one. */
    private fun jsonString(inner: String): String? {
        val out = StringBuilder()
        var i = 0
        while (i < inner.length) {
            val c = inner[i]
            when {
                c == '"' || c < ' ' -> return null
                c != '\\' -> {
                    out.append(c)
                    i += 1
                }
                i + 1 >= inner.length -> return null
                else -> {
                    when (val escape = inner[i + 1]) {
                        '"', '\\', '/' -> out.append(escape)
                        'b' -> out.append('\b')
                        'f' -> out.append('\u000C')
                        'n' -> out.append('\n')
                        'r' -> out.append('\r')
                        't' -> out.append('\t')
                        'u' -> {
                            if (i + 6 > inner.length) return null
                            // Four hex digits: toIntOrNull alone takes a sign.
                            val hex = inner.substring(i + 2, i + 6)
                            if (!hex.all { it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F' }) return null
                            out.append(hex.toInt(16).toChar())
                            i += 4
                        }
                        else -> return null
                    }
                    i += 2
                }
            }
        }
        val decoded = out.toString()
        // Every surrogate in a pair: a lone one is not text.
        var k = 0
        while (k < decoded.length) {
            val unit = decoded[k]
            if (unit.isHighSurrogate()) {
                if (k + 1 >= decoded.length || !decoded[k + 1].isLowSurrogate()) return null
                k += 2
                continue
            }
            if (unit.isLowSurrogate()) return null
            k += 1
        }
        return decoded
    }
}
