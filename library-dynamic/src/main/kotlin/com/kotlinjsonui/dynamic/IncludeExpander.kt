package com.kotlinjsonui.dynamic

import android.content.Context
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import java.io.InputStream
import java.io.InputStreamReader
import java.util.regex.Pattern

/**
 * Expands include directives in JSON layouts with ID prefix support.
 *
 * When an include has an id attribute, all child elements' ids and data bindings
 * are prefixed with that id in camelCase format.
 *
 * Example:
 * - include id="header1" + child id="title_label" → "header1TitleLabel"
 * - include id="header1" + data name="title" → "header1Title"
 * - include id="header1" + @{title} → @{header1Title}
 */
object IncludeExpander {
    private var context: Context? = null
    private val bindingPattern = Pattern.compile("@\\{([^}]+)\\}")

    /**
     * Test seam: reads an included layout by its name. Production leaves it
     * null and reads `Layouts/<name>.json` from the assets (see
     * [candidatePaths]); a JVM test sets it so whole layouts can be expanded
     * without an Android Context.
     */
    internal var layoutReader: ((String) -> JsonObject?)? = null

    /**
     * Initialize with application context
     */
    fun init(appContext: Context) {
        context = appContext.applicationContext
    }

    /**
     * Process includes in JSON, expanding them inline with ID prefixes
     */
    fun processIncludes(json: JsonObject, idPrefix: String? = null): JsonObject {
        val ctx = context
        if (ctx == null && layoutReader == null) return json

        // If this is an include element, expand it
        if (json.has("include")) {
            return expandInclude(ctx, json, idPrefix)
        }

        // Apply ID prefix to current element's id
        if (idPrefix != null && json.has("id")) {
            val currentId = json.get("id").asString
            json.addProperty("id", combineWithPrefix(idPrefix, currentId))
        }

        // Process children recursively
        val childKey = when {
            json.has("child") -> "child"
            json.has("children") -> "children"
            else -> null
        }

        if (childKey != null) {
            val children = json.get(childKey)
            if (children.isJsonArray) {
                val processedChildren = JsonArray()
                children.asJsonArray.forEach { child ->
                    if (child.isJsonObject) {
                        processedChildren.add(processIncludes(child.asJsonObject, idPrefix))
                    } else {
                        processedChildren.add(child)
                    }
                }
                json.add(childKey, processedChildren)
            } else if (children.isJsonObject) {
                json.add(childKey, processIncludes(children.asJsonObject, idPrefix))
            }

            // Normalize "children" to "child"
            if (childKey == "children") {
                json.add("child", json.remove("children"))
            }
        }

        return json
    }

    /**
     * Expand an include directive
     */
    private fun expandInclude(ctx: Context?, includeJson: JsonObject, parentPrefix: String?): JsonObject {
        val layoutName = includeJson.get("include")?.asString ?: return includeJson
        val includeId = if (includeJson.has("id")) includeJson.get("id").asString else null

        // Calculate new prefix. A top-level include's id is camel-cased, as
        // the codegen does (`to_camel_case`); it was the raw id, so
        // `hero_card` + `title` gave `hero_cardTitle` here and `heroCardTitle`
        // in the generated code.
        val newPrefix = when {
            parentPrefix != null && includeId != null -> combineWithPrefix(parentPrefix, includeId)
            includeId != null -> toCamelCase(includeId)
            else -> parentPrefix
        }

        // Load the included layout
        val reader = layoutReader
        val includedJson = (if (reader != null) reader(layoutName) else ctx?.let { loadLayoutFile(it, layoutName) })
            ?: return includeJson

        // Apply styles if present
        val styledJson = if (includedJson.has("style") && ctx != null) {
            DynamicStyleLoader.applyStyle(ctx, includedJson)
        } else {
            includedJson
        }

        // Merge properties from include element (except 'include' and 'id')
        includeJson.entrySet().forEach { (key, value) ->
            if (key != "include" && key != "id") {
                if (key == "data" || key == "shared_data") {
                    // Merge data/shared_data arrays
                    if (value.isJsonArray) {
                        val existing = styledJson.get(key)?.asJsonArray ?: JsonArray()
                        value.asJsonArray.forEach { existing.add(it) }
                        styledJson.add(key, existing)
                    }
                } else {
                    // Override other properties
                    styledJson.add(key, value)
                }
            }
        }

        // Apply ID prefix to all elements and bindings
        val prefixedJson = if (newPrefix != null) {
            applyIdPrefix(styledJson, newPrefix)
        } else {
            styledJson
        }

        // Recursively process any nested includes
        return processIncludes(prefixedJson, newPrefix)
    }

    /**
     * Apply ID prefix to all ids, data names, and @{} bindings
     */
    private fun applyIdPrefix(json: JsonObject, prefix: String): JsonObject {
        // Apply prefix to data definitions
        if (json.has("data") && json.get("data").isJsonArray) {
            val dataArray = json.getAsJsonArray("data")
            val newDataArray = JsonArray()
            dataArray.forEach { item ->
                if (item.isJsonObject) {
                    val dataItem = item.asJsonObject
                    if (dataItem.has("name")) {
                        val newItem = dataItem.deepCopy()
                        val name = dataItem.get("name").asString
                        newItem.addProperty("name", combineWithPrefix(prefix, name))
                        newDataArray.add(newItem)
                    } else {
                        newDataArray.add(item)
                    }
                } else {
                    newDataArray.add(item)
                }
            }
            json.add("data", newDataArray)
        }

        // Transform all @{} bindings in the JSON
        return transformBindings(json, prefix) as JsonObject
    }

    /**
     * Transform @{variableName} to @{prefixVariableName} in all string values
     */
    private fun transformBindings(element: JsonElement, prefix: String): JsonElement {
        return when {
            element.isJsonObject -> {
                val obj = element.asJsonObject
                val result = JsonObject()
                obj.entrySet().forEach { (key, value) ->
                    result.add(key, transformBindings(value, prefix))
                }
                result
            }
            element.isJsonArray -> {
                val arr = JsonArray()
                element.asJsonArray.forEach { item ->
                    arr.add(transformBindings(item, prefix))
                }
                arr
            }
            element.isJsonPrimitive && element.asJsonPrimitive.isString -> {
                val str = element.asString
                val transformed = transformBindingString(str, prefix)
                JsonPrimitive(transformed)
            }
            else -> element
        }
    }

    /**
     * Transform @{} bindings in a string
     */
    private fun transformBindingString(str: String, prefix: String): String {
        val matcher = bindingPattern.matcher(str)
        val result = StringBuffer()

        while (matcher.find()) {
            val varName = matcher.group(1) ?: continue
            val replacement = if (varName.contains(".")) {
                // Keep this.xxx, item.xxx as-is
                "@{$varName}"
            } else {
                "@{${combineWithPrefix(prefix, varName)}}"
            }
            matcher.appendReplacement(result, replacement.replace("$", "\\$"))
        }
        matcher.appendTail(result)

        return result.toString()
    }

    // 🔻 THE CODEGEN'S SPELLING IS THE SPELLING. An element inside an include
    // that carries an id is addressed by one id on every face (ruling U8,
    // include-child-ids-are-spelled-differently-on-each-platform), and the one
    // chosen is what the Ruby codegen writes — kjui_tools / sjui_tools
    // include_expander.rb `to_camel_case` / `combine_with_prefix`. These two
    // functions are that Ruby, answer for answer:
    // - segments split like Ruby's `split('_')`: leading and inner empty
    //   segments kept, trailing ones dropped (`_leading` → `Leading`,
    //   `a__b` → `aB`, `trailing_` → `trailing`);
    // - every segment after the first goes through `capitalize`, which
    //   LOWERCASES the rest (`verify_2FA_form` → `verify2faForm`,
    //   `clear_URL_button` → `clearUrlButton`); the first stays as written
    //   (`URL_field` → `URLField`);
    // - after a prefix only a leading `[a-z]` is raised (`sub(/^[a-z]/)`).
    // This used to keep each segment's case, so the dynamic ids and the
    // generated code's differed on exactly those shapes. Measured
    // against both Ruby expanders (ruby 2.6.10).

    /** Ruby's `String#split('_')`: trailing empty segments dropped. */
    internal fun rubySplit(str: String): List<String> = str.split("_").dropLastWhile { it.isEmpty() }

    /** Ruby's `String#capitalize` (identifiers are ASCII). */
    internal fun rubyCapitalize(part: String): String =
        part.take(1).uppercase() + part.drop(1).lowercase()

    /**
     * Convert snake_case to camelCase
     */
    internal fun toCamelCase(str: String): String {
        if (!str.contains("_")) return str
        val parts = rubySplit(str)
        if (parts.isEmpty()) return ""
        return parts[0] + parts.drop(1).joinToString("") { rubyCapitalize(it) }
    }

    /**
     * Combine prefix and name in camelCase
     * e.g., prefix="header1", name="title" -> "header1Title"
     * e.g., prefix="header1", name="title_label" -> "header1TitleLabel"
     */
    internal fun combineWithPrefix(prefix: String, name: String): String {
        val camelName = toCamelCase(name)
        val head = camelName.firstOrNull()
        return if (head != null && head in 'a'..'z') {
            prefix + head.uppercaseChar() + camelName.drop(1)
        } else {
            prefix + camelName
        }
    }

    /**
     * Where an included layout is looked for: the LAYOUTS ROOT, and nothing
     * else — `Layouts/<name>.json`, whether the name is bare or a path. It
     * is never resolved from the including file's directory, and a bare name
     * is not searched for in subdirectories either.
     *
     * The second of those is a change: a bare name used to fall back to every
     * first-level directory under Layouts, which no other reader of the same
     * layouts does — the normalizer (`layouts_dir / f"{include}.json"`), the
     * codegen (`File.join(layouts_root, …)`), the web output and the SwiftUI
     * runtime all look at the root only, so the same layout could resolve to
     * a file here that exists nowhere for them. Ruled root-only on
     * 2026-09-25 (include-child-ids-are-spelled-differently-on-each-platform);
     * the consumers' includes measured that day all carry a path, so no
     * include that resolved before stops resolving.
     */
    internal fun candidatePaths(layoutName: String): List<String> = listOf("Layouts/$layoutName.json")

    /**
     * Reads an included layout through [open] (an asset opener in
     * production). Split out so the lookup is exercised on the JVM with the
     * real loop, not only by its list of paths.
     */
    internal fun readLayout(layoutName: String, open: (String) -> InputStream): JsonObject? {
        for (path in candidatePaths(layoutName)) {
            try {
                open(path).use { inputStream ->
                    InputStreamReader(inputStream).use { reader ->
                        return JsonParser.parseReader(reader).asJsonObject
                    }
                }
            } catch (e: Exception) {
                // Not found at this path
            }
        }
        return null
    }

    /**
     * Load a layout file from assets
     */
    private fun loadLayoutFile(ctx: Context, layoutName: String): JsonObject? =
        readLayout(layoutName) { ctx.assets.open(it) }
}
