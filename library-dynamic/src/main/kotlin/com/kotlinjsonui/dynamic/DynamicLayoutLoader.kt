package com.kotlinjsonui.dynamic

import android.content.Context
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.InputStreamReader

/**
 * Loader for dynamic layout files
 * Handles loading JSON layouts from assets
 *
 * Note: This loader automatically expands includes with ID prefix support.
 * Use loadLayoutRaw() to get the raw JSON without include expansion.
 */
object DynamicLayoutLoader {
    private var context: Context? = null
    private val layoutCache = mutableMapOf<String, JsonObject?>()
    private val rawLayoutCache = mutableMapOf<String, JsonObject?>()
    private val existsCache = mutableMapOf<String, Boolean>()
    // assets/Layouts/ listings, by directory under Layouts/ ("" the root):
    // assets are immutable at runtime.
    private val listingCache = mutableMapOf<String, List<String>>()

    /**
     * Initialize the loader with an application context
     */
    fun init(appContext: Context) {
        context = appContext.applicationContext
        IncludeExpander.init(appContext)
    }

    /**
     * Load a layout from assets/Layouts directory with include expansion.
     * Includes are expanded inline with ID prefix support.
     * Supports both direct paths (e.g., "screens/detail_view") and simple names (e.g., "detail_view").
     * A name not found at its own path is searched for at any depth under Layouts/
     * (LayoutAssetSearch states the rules).
     */
    fun loadLayout(layoutName: String): JsonObject? {
        // Return from cache if available
        layoutCache[layoutName]?.let { return it.deepCopy() }

        // Load raw JSON first
        val rawJson = loadLayoutRaw(layoutName) ?: return null

        // Process includes (expand inline with ID prefixes)
        val expandedJson = IncludeExpander.processIncludes(rawJson.deepCopy())

        // Cache the expanded result
        layoutCache[layoutName] = expandedJson

        return expandedJson.deepCopy()
    }

    /**
     * Load a layout without include expansion (raw JSON)
     * Used internally by IncludeExpander to load included files
     */
    fun loadLayoutRaw(layoutName: String): JsonObject? {
        // Return from cache if available
        rawLayoutCache[layoutName]?.let { return it.deepCopy() }

        val ctx = context ?: return null

        // First, try to load from the exact path
        val directPath = "Layouts/$layoutName.json"
        try {
            ctx.assets.open(directPath).use { inputStream ->
                InputStreamReader(inputStream).use { reader ->
                    val json = JsonParser.parseReader(reader).asJsonObject
                    // Cache the result
                    rawLayoutCache[layoutName] = json
                    return json.deepCopy()
                }
            }
        } catch (e: Exception) {
            // Not found at direct path, try searching in subdirectories
        }

        return searchInSubdirectories(ctx, layoutName)
    }

    /**
     * Search for a layout file at any depth under Layouts/ (LayoutAssetSearch).
     * Until 2.42.0 this looked one directory down, and only for a name without
     * "/": a cell two directories deep did not load in Dynamic mode.
     */
    private fun searchInSubdirectories(ctx: Context, layoutName: String): JsonObject? {
        val path = LayoutAssetSearch.find(layoutName) { dir -> listLayouts(ctx, dir) } ?: return null
        try {
            ctx.assets.open("Layouts/$path").use { inputStream ->
                InputStreamReader(inputStream).use { reader ->
                    val json = JsonParser.parseReader(reader).asJsonObject
                    // Cache with the full path for future lookups
                    rawLayoutCache[layoutName] = json
                    rawLayoutCache[path.removeSuffix(".json")] = json
                    return json.deepCopy()
                }
            }
        } catch (e: Exception) {
            return null
        }
    }

    /** The entries of Layouts/<dir> ("" the root), or none: a file or a missing path. */
    private fun listLayouts(ctx: Context, dir: String): List<String> =
        listingCache.getOrPut(dir) {
            try {
                ctx.assets.list(if (dir.isEmpty()) "Layouts" else "Layouts/$dir")?.toList() ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        }

    /**
     * True when a layout asset with this name exists (its own path, or found
     * at any depth — the same lookup loadLayoutRaw performs). Cached: assets
     * are immutable at runtime.
     */
    fun layoutExists(layoutName: String): Boolean {
        existsCache[layoutName]?.let { return it }
        if (rawLayoutCache[layoutName] != null) {
            existsCache[layoutName] = true
            return true
        }
        val ctx = context ?: return false

        val exists = assetExists(ctx, "Layouts/$layoutName.json") ||
            LayoutAssetSearch.find(layoutName) { dir -> listLayouts(ctx, dir) } != null
        existsCache[layoutName] = exists
        return exists
    }

    /**
     * Resolve the effective layout name for a size-class tier
     * (responsive variant files, `home@regular.json`): `<name>@<tier>`
     * when that variant layout exists, otherwise the base name. No
     * cross-tier fallback — a medium window with only `@regular` shipped
     * renders the base (06 variant-file resolution table).
     */
    fun resolveVariantLayoutName(layoutName: String, tier: String?): String {
        if (tier == null || layoutName.contains('@')) return layoutName
        val candidate = "$layoutName@$tier"
        return if (layoutExists(candidate)) candidate else layoutName
    }

    private fun assetExists(ctx: Context, path: String): Boolean {
        return try {
            ctx.assets.open(path).use { }
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Clear all layout caches (both expanded and raw)
     */
    fun clearCache() {
        layoutCache.clear()
        rawLayoutCache.clear()
        existsCache.clear()
        listingCache.clear()
    }

    /**
     * Remove a specific layout from both caches
     */
    fun clearCache(layoutName: String) {
        layoutCache.remove(layoutName)
        rawLayoutCache.remove(layoutName)
        existsCache.remove(layoutName)
    }
}