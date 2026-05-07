package com.kotlinjsonui.dynamic

import android.content.res.Configuration
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.window.core.layout.WindowWidthSizeClass
import com.google.gson.JsonArray
import com.google.gson.JsonObject

/**
 * Resolves responsive overrides in JSON layouts based on the current
 * WindowWidthSizeClass and device orientation.
 *
 * JSON format:
 * ```json
 * {
 *   "type": "View",
 *   "orientation": "vertical",
 *   "spacing": 8,
 *   "responsive": {
 *     "regular": { "orientation": "horizontal", "spacing": 24 },
 *     "medium": { "spacing": 12 },
 *     "landscape": { "spacing": 16 },
 *     "regular-landscape": { "orientation": "horizontal", "spacing": 32 }
 *   },
 *   "child": [...]
 * }
 * ```
 *
 * Priority (highest to lowest):
 *   compound (e.g. "regular-landscape") > "landscape" > "regular" > "medium" > "compact" > default
 */
object ResponsiveResolver {

    // Size class keys in ascending priority order
    private val SIZE_CLASS_PRIORITY = listOf("compact", "medium", "regular")

    /**
     * Map WindowWidthSizeClass to the JSON key used in the responsive block.
     */
    internal fun widthSizeClassKey(widthSizeClass: WindowWidthSizeClass): String {
        return when (widthSizeClass) {
            WindowWidthSizeClass.COMPACT -> "compact"
            WindowWidthSizeClass.MEDIUM -> "medium"
            WindowWidthSizeClass.EXPANDED -> "regular"
            else -> "compact"
        }
    }

    /**
     * Determine which responsive keys match the current environment,
     * ordered from lowest to highest priority.
     *
     * Priority: compact < medium < regular < landscape < compound-landscape
     */
    internal fun resolveMatchingKeys(
        sizeClassKey: String,
        isLandscape: Boolean
    ): List<String> {
        val keys = mutableListOf<String>()

        // Add size class keys up to and including the current one (lower priority first)
        val currentIndex = SIZE_CLASS_PRIORITY.indexOf(sizeClassKey)
        if (currentIndex >= 0) {
            for (i in 0..currentIndex) {
                keys.add(SIZE_CLASS_PRIORITY[i])
            }
        }

        // Landscape (higher priority than plain size class)
        if (isLandscape) {
            keys.add("landscape")
            // Compound key (highest priority)
            keys.add("$sizeClassKey-landscape")
        }

        return keys
    }

    /**
     * Merge overrides from the responsive block into the base JSON object.
     * Override properties replace base properties; the "responsive" key is removed.
     * "child" and "children" keys in overrides are ignored (only attributes are merged).
     */
    internal fun mergeOverrides(base: JsonObject, responsive: JsonObject, matchingKeys: List<String>): JsonObject {
        val result = base.deepCopy()

        // Apply matching overrides in priority order (lowest first, so highest wins)
        for (key in matchingKeys) {
            val overrideElement = responsive.get(key)
            if (overrideElement != null && overrideElement.isJsonObject) {
                val overrides = overrideElement.asJsonObject
                for (entry in overrides.entrySet()) {
                    // Do not override child/children — only attributes
                    if (entry.key != "child" && entry.key != "children") {
                        result.add(entry.key, entry.value.deepCopy())
                    }
                }
            }
        }

        // Remove the responsive block from the result
        result.remove("responsive")

        return result
    }

    /**
     * Resolve responsive overrides for a single JSON node (non-recursive).
     * Returns a new JsonObject with overrides applied and "responsive" removed,
     * or the original object unchanged if no "responsive" block is present.
     */
    fun resolveNode(
        json: JsonObject,
        widthSizeClass: WindowWidthSizeClass,
        isLandscape: Boolean
    ): JsonObject {
        val responsiveElement = json.get("responsive")
        if (responsiveElement == null || !responsiveElement.isJsonObject) {
            return json
        }

        val sizeClassKey = widthSizeClassKey(widthSizeClass)
        val matchingKeys = resolveMatchingKeys(sizeClassKey, isLandscape)
        return mergeOverrides(json, responsiveElement.asJsonObject, matchingKeys)
    }

    /**
     * Internal overload using pre-resolved size class key string.
     */
    internal fun resolveNode(
        json: JsonObject,
        sizeClassKey: String,
        isLandscape: Boolean
    ): JsonObject {
        val responsiveElement = json.get("responsive")
        if (responsiveElement == null || !responsiveElement.isJsonObject) {
            return json
        }

        val matchingKeys = resolveMatchingKeys(sizeClassKey, isLandscape)
        return mergeOverrides(json, responsiveElement.asJsonObject, matchingKeys)
    }

    /**
     * Recursively resolve responsive blocks in the entire JSON tree.
     * Walks "child" and "children" arrays, resolving each node.
     *
     * Use this when you want to pre-process an entire layout tree at once
     * (e.g., before caching). For per-node resolution during rendering,
     * use [resolveNode] instead.
     */
    fun resolveTree(
        json: JsonObject,
        widthSizeClass: WindowWidthSizeClass,
        isLandscape: Boolean
    ): JsonObject {
        val sizeClassKey = widthSizeClassKey(widthSizeClass)
        return resolveTreeInternal(json, sizeClassKey, isLandscape)
    }

    private fun resolveTreeInternal(
        json: JsonObject,
        sizeClassKey: String,
        isLandscape: Boolean
    ): JsonObject {
        // Resolve this node's responsive block
        val resolved = resolveNode(json, sizeClassKey, isLandscape)

        // Recursively resolve children
        resolveChildArray(resolved, "child", sizeClassKey, isLandscape)
        resolveChildArray(resolved, "children", sizeClassKey, isLandscape)

        return resolved
    }

    private fun resolveChildArray(
        json: JsonObject,
        key: String,
        sizeClassKey: String,
        isLandscape: Boolean
    ) {
        val childElement = json.get(key) ?: return

        when {
            childElement.isJsonArray -> {
                val newArray = JsonArray()
                for (element in childElement.asJsonArray) {
                    if (element.isJsonObject) {
                        newArray.add(resolveTreeInternal(element.asJsonObject, sizeClassKey, isLandscape))
                    } else {
                        newArray.add(element)
                    }
                }
                json.add(key, newArray)
            }
            childElement.isJsonObject -> {
                json.add(key, resolveTreeInternal(childElement.asJsonObject, sizeClassKey, isLandscape))
            }
        }
    }
}

/**
 * Composable helper that resolves responsive overrides for a single JSON node
 * using the current window size class and orientation.
 * Called within DynamicView before component routing so that individual
 * component implementations do not need to know about responsive blocks.
 */
@Composable
fun resolveResponsiveNode(json: JsonObject): JsonObject {
    if (!json.has("responsive")) return json
    val windowAdaptiveInfo = currentWindowAdaptiveInfo()
    val widthSizeClass = windowAdaptiveInfo.windowSizeClass.windowWidthSizeClass
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    return ResponsiveResolver.resolveNode(json, widthSizeClass, isLandscape)
}

/**
 * Composable helper that resolves a complete JSON tree using the current
 * window size class and orientation. Useful for pre-processing an entire
 * layout before rendering.
 */
@Composable
fun resolveResponsiveTree(json: JsonObject): JsonObject {
    val windowAdaptiveInfo = currentWindowAdaptiveInfo()
    val widthSizeClass = windowAdaptiveInfo.windowSizeClass.windowWidthSizeClass
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    return ResponsiveResolver.resolveTree(json, widthSizeClass, isLandscape)
}
