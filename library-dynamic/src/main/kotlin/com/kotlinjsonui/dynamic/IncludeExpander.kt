package com.kotlinjsonui.dynamic

import android.content.Context
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
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
     * Initialize with application context
     */
    fun init(appContext: Context) {
        context = appContext.applicationContext
    }

    /**
     * Process includes in JSON, expanding them inline with ID prefixes
     */
    fun processIncludes(json: JsonObject, idPrefix: String? = null): JsonObject {
        val ctx = context ?: return json

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
    private fun expandInclude(ctx: Context, includeJson: JsonObject, parentPrefix: String?): JsonObject {
        val layoutName = includeJson.get("include")?.asString ?: return includeJson
        val includeId = if (includeJson.has("id")) includeJson.get("id").asString else null

        // Calculate new prefix
        val newPrefix = when {
            parentPrefix != null && includeId != null -> combineWithPrefix(parentPrefix, includeId)
            includeId != null -> includeId
            else -> parentPrefix
        }

        // Load the included layout
        val includedJson = loadLayoutFile(ctx, layoutName) ?: return includeJson

        // Apply styles if present
        val styledJson = if (includedJson.has("style")) {
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

    /**
     * Convert snake_case to camelCase
     */
    private fun toCamelCase(str: String): String {
        if (!str.contains("_")) return str
        val parts = str.split("_")
        return parts[0] + parts.drop(1).joinToString("") { it.replaceFirstChar { c -> c.uppercaseChar() } }
    }

    /**
     * Combine prefix and name in camelCase
     * e.g., prefix="header1", name="title" -> "header1Title"
     * e.g., prefix="header1", name="title_label" -> "header1TitleLabel"
     */
    private fun combineWithPrefix(prefix: String, name: String): String {
        val camelName = toCamelCase(name)
        return prefix + camelName.replaceFirstChar { it.uppercaseChar() }
    }

    /**
     * Load a layout file from assets
     */
    private fun loadLayoutFile(ctx: Context, layoutName: String): JsonObject? {
        // Try direct path first
        val directPath = "Layouts/$layoutName.json"
        try {
            ctx.assets.open(directPath).use { inputStream ->
                InputStreamReader(inputStream).use { reader ->
                    return JsonParser.parseReader(reader).asJsonObject
                }
            }
        } catch (e: Exception) {
            // Not found at direct path
        }

        // Search in subdirectories
        if (!layoutName.contains("/")) {
            try {
                val layoutsDir = ctx.assets.list("Layouts") ?: return null
                for (item in layoutsDir) {
                    try {
                        val subItems = ctx.assets.list("Layouts/$item")
                        if (subItems != null && subItems.isNotEmpty()) {
                            val subPath = "Layouts/$item/$layoutName.json"
                            try {
                                ctx.assets.open(subPath).use { inputStream ->
                                    InputStreamReader(inputStream).use { reader ->
                                        return JsonParser.parseReader(reader).asJsonObject
                                    }
                                }
                            } catch (e: Exception) {
                                // Not found in this subdirectory
                            }
                        }
                    } catch (e: Exception) {
                        // Not a directory
                    }
                }
            } catch (e: Exception) {
                // Error listing directories
            }
        }

        return null
    }
}
