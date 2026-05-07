package com.kotlinjsonui.dynamic

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.io.InputStreamReader
import java.util.Locale

/**
 * ResourceCache manages cached resources for dynamic UI rendering.
 * It loads and caches strings.json and colors.json files for efficient resource resolution.
 * 
 * Features:
 * - Lazy loading: Resources are loaded only when first accessed
 * - Singleton pattern: Single instance shared across the app
 * - Flattened string structure: Nested JSON is flattened to file_key format
 * - Fallback support: Returns original value if resource not found
 */
object ResourceCache {
    private const val TAG = "ResourceCache"
    
    // Cached resources
    private var stringsCache: Map<String, String>? = null
    private var colorsCache: Map<String, String>? = null

    // Reverse map: unprefixed key → prefixed key (e.g. "edit_profile" → "mypage_edit_profile")
    private var keyToPrefixedKey: Map<String, String>? = null
    
    // Flag to track if loading has been attempted
    private var stringsLoadAttempted = false
    private var colorsLoadAttempted = false
    
    /**
     * Initialize the cache with context.
     * This can be called multiple times safely.
     */
    fun init(context: Context) {
        // Load resources lazily when first accessed
        loadStringsIfNeeded(context)
        loadColorsIfNeeded(context)
    }
    
    /**
     * Get a string resource by key.
     * Returns the localized string if found, otherwise returns the key itself.
     * 
     * @param key The string key to look up
     * @param context Context for loading resources if not yet loaded
     * @return The localized string or the original key
     */
    fun getString(key: String, context: Context): String {
        loadStringsIfNeeded(context)
        return stringsCache?.get(key) ?: key
    }
    
    /**
     * Get a color resource by name.
     * Returns the color hex value if found, otherwise returns null.
     * 
     * @param colorName The color name to look up
     * @param context Context for loading resources if not yet loaded
     * @return The color hex value or null
     */
    fun getColor(colorName: String, context: Context): String? {
        loadColorsIfNeeded(context)
        return colorsCache?.get(colorName)
    }
    
    /**
     * Check if a string key exists in the cache
     */
    fun hasString(key: String, context: Context): Boolean {
        loadStringsIfNeeded(context)
        return stringsCache?.containsKey(key) == true
    }
    
    /**
     * Check if a color name exists in the cache
     */
    fun hasColor(colorName: String, context: Context): Boolean {
        loadColorsIfNeeded(context)
        return colorsCache?.containsKey(colorName) == true
    }
    
    /**
     * Load strings.json if not already loaded
     */
    private fun loadStringsIfNeeded(context: Context) {
        if (stringsLoadAttempted) return
        stringsLoadAttempted = true
        
        try {
            // Try to load from Layouts/Resources/strings.json
            val jsonString = try {
                context.assets.open("Layouts/Resources/strings.json").use { inputStream ->
                    InputStreamReader(inputStream).use { it.readText() }
                }
            } catch (e: Exception) {
                // Try alternate path
                try {
                    context.assets.open("strings.json").use { inputStream ->
                        InputStreamReader(inputStream).use { it.readText() }
                    }
                } catch (e2: Exception) {
                    Log.d(TAG, "strings.json not found, using fallback")
                    return
                }
            }
            
            // Parse and flatten the JSON structure
            val gson = Gson()
            val jsonObject = gson.fromJson(jsonString, JsonObject::class.java)
            val flattenedStrings = mutableMapOf<String, String>()
            val reverseKeyMap = mutableMapOf<String, String>()
            val preferredLang = Locale.getDefault().language

            // Flatten the nested structure. Two value shapes are supported:
            //   1. Legacy: { "file1": { "key1": "value1" } }
            //   2. i18n:   { "file1": { "key1": { "en": "Foo", "ja": "フー" } } }
            //
            // In (2), values are resolved against the device locale (with "en"
            // as fallback) for cache population. For both shapes the
            // unprefixed→prefixed map is always populated so R.string lookup
            // (`<fileName>_<key>`) can bypass a missing strings.json value.
            for ((fileName, fileObject) in jsonObject.entrySet()) {
                if (!fileObject.isJsonObject) continue
                val fileJsonObject = fileObject.asJsonObject
                for ((key, value) in fileJsonObject.entrySet()) {
                    val flatKey = "${fileName}_${key}"
                    // Unprefixed → prefixed mapping always recorded, regardless
                    // of whether a cached value can be extracted. This is the
                    // critical input for resolveString's R.string lookup path.
                    reverseKeyMap[key] = flatKey

                    val resolved = extractLocalizedValue(value, preferredLang) ?: continue
                    flattenedStrings[flatKey] = resolved
                    if (!flattenedStrings.containsKey(key)) {
                        flattenedStrings[key] = resolved
                    }
                }
            }

            stringsCache = flattenedStrings
            keyToPrefixedKey = reverseKeyMap
            Log.d(TAG, "Loaded ${flattenedStrings.size} strings (${reverseKeyMap.size} key mappings)")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load strings.json", e)
        }
    }
    
    /**
     * Load colors.json if not already loaded
     */
    private fun loadColorsIfNeeded(context: Context) {
        if (colorsLoadAttempted) return
        colorsLoadAttempted = true
        
        try {
            // Try to load from Layouts/Resources/colors.json
            val jsonString = try {
                context.assets.open("Layouts/Resources/colors.json").use { inputStream ->
                    InputStreamReader(inputStream).use { it.readText() }
                }
            } catch (e: Exception) {
                // Try alternate path
                try {
                    context.assets.open("colors.json").use { inputStream ->
                        InputStreamReader(inputStream).use { it.readText() }
                    }
                } catch (e2: Exception) {
                    Log.d(TAG, "colors.json not found, using fallback")
                    return
                }
            }
            
            // Parse the JSON. Two schemas are supported:
            //   Legacy flat:  { "gold": "#FFC364", "deep_gray": "#221C10", ... }
            //   Themed:       { "modes": ["light", "dark"],
            //                   "fallback_mode": "light",
            //                   "systemModeMapping": { "light": "light", ... },
            //                   "light": { "gold": "#FFC364", ... },
            //                   "dark":  { "gold": "#FFC364", ... } }
            //
            // The themed schema is what `jui build` writes for multi-theme
            // projects, and the tool-generated ColorManager already owns the
            // active palette. ResourceCache only needs to populate a usable
            // fallback so `resolveColor` works when the app hasn't wired
            // `Configuration.themedColorParser`.
            val gson = Gson()
            val jsonObject = gson.fromJson(jsonString, JsonObject::class.java)
            val colors = mutableMapOf<String, String>()

            val activePalette = resolveActivePalette(jsonObject)
            if (activePalette != null) {
                for ((key, value) in activePalette.entrySet()) {
                    if (value.isJsonPrimitive) colors[key] = value.asString
                }
            } else {
                // Legacy flat schema: entries are all primitives.
                for ((key, value) in jsonObject.entrySet()) {
                    if (value.isJsonPrimitive) colors[key] = value.asString
                }
            }

            colorsCache = colors
            Log.d(TAG, "Loaded ${colors.size} colors")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load colors.json", e)
        }
    }
    
    /**
     * Extract a concrete String from a strings.json entry that may be either
     * a primitive or a `{en, ja, ...}` language map. Returns null when no
     * candidate can be produced (e.g. nested object with non-string leaves).
     */
    private fun extractLocalizedValue(element: JsonElement, preferredLang: String): String? {
        if (element.isJsonPrimitive) return element.asString
        if (!element.isJsonObject) return null
        val obj = element.asJsonObject
        obj.get(preferredLang)?.takeIf { it.isJsonPrimitive }?.let { return it.asString }
        obj.get("en")?.takeIf { it.isJsonPrimitive }?.let { return it.asString }
        for (entry in obj.entrySet()) {
            val v = entry.value
            if (v.isJsonPrimitive) return v.asString
        }
        return null
    }

    /**
     * Determine the active palette inside a themed colors.json. Honors
     * `fallback_mode` first (the tool writes it as the deterministic default)
     * and falls back to the first mode in the `modes` array. Returns null
     * when the JSON doesn't look themed, so callers fall back to legacy flat
     * parsing.
     */
    private fun resolveActivePalette(root: JsonObject): JsonObject? {
        val modesArray = root.get("modes")
        val fallbackMode = root.get("fallback_mode")
        if (modesArray?.isJsonArray != true && fallbackMode?.isJsonPrimitive != true) {
            return null
        }

        val candidate: String? = when {
            fallbackMode?.isJsonPrimitive == true -> fallbackMode.asString
            modesArray?.isJsonArray == true && modesArray.asJsonArray.size() > 0 ->
                modesArray.asJsonArray[0].takeIf { it.isJsonPrimitive }?.asString
            else -> null
        }
        val mode = candidate ?: return null
        val palette = root.get(mode) ?: return null
        return if (palette.isJsonObject) palette.asJsonObject else null
    }

    /**
     * Clear the cache (useful for testing or when resources are updated)
     */
    fun clearCache() {
        stringsCache = null
        colorsCache = null
        keyToPrefixedKey = null
        stringsLoadAttempted = false
        colorsLoadAttempted = false
    }
    
    /**
     * Process a text string that might be a string resource key.
     * If the text matches a key in strings cache, returns the localized value.
     * Otherwise returns the original text.
     * 
     * @param text The text that might be a resource key
     * @param context Context for loading resources if not yet loaded
     * @return The resolved string or original text
     */
    fun resolveString(text: String?, context: Context): String {
        if (text.isNullOrEmpty()) return ""

        // Check if it's a data binding expression - don't resolve those
        if (text.contains("@{")) return text

        // Load strings.json for key mapping (unprefixed → prefixed)
        loadStringsIfNeeded(context)

        // Always prefer Android native string resources (R.string.*) over strings.json values,
        // because strings.json may contain English defaults while R.string has proper localization.

        // First try with prefixed key (e.g. "detail_button" → R.string.candidate_card_detail_button)
        val prefixedKey = keyToPrefixedKey?.get(text)
        if (prefixedKey != null) {
            try {
                val resId = context.resources.getIdentifier(prefixedKey, "string", context.packageName)
                if (resId != 0) {
                    return context.getString(resId)
                }
            } catch (_: Exception) { }
        }

        // Then try with original key (R.string.xxx)
        try {
            val resId = context.resources.getIdentifier(text, "string", context.packageName)
            if (resId != 0) {
                return context.getString(resId)
            }
        } catch (_: Exception) { }

        // Last resort: use strings.json cached value if available
        val cached = stringsCache?.get(text)
        if (cached != null && cached != text) return cached

        return text
    }
    
    /**
     * Process a color string that might be a color resource name.
     * If the color name exists in colors cache, returns the hex value.
     * Otherwise attempts to parse as a hex color.
     * 
     * @param colorName The color name or hex value
     * @param context Context for loading resources if not yet loaded
     * @return The resolved color hex value or null if invalid
     */
    fun resolveColor(colorName: String?, context: Context): String? {
        if (colorName.isNullOrEmpty()) return null
        
        // Check if it's a data binding expression - don't resolve those
        if (colorName.contains("@{")) return colorName
        
        // If it already looks like a hex color, return it
        if (colorName.startsWith("#")) {
            return colorName
        }
        
        // Try to resolve as a color resource
        loadColorsIfNeeded(context)
        colorsCache?.get(colorName)?.let { return it }
        
        // If not found, try to parse as hex without #
        return if (colorName.matches(Regex("[0-9a-fA-F]{6,8}"))) {
            "#$colorName"
        } else {
            null
        }
    }
}