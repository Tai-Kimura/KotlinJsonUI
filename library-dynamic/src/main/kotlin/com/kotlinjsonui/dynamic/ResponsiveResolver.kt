package com.kotlinjsonui.dynamic

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.google.gson.JsonArray
import com.google.gson.JsonObject

/**
 * Resolves responsive overrides in JSON layouts based on the current
 * WindowSizeClass width breakpoints and device orientation.
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
 * One key wins, alone: the first of jsonui-cli's PRIORITY_ORDER
 * (shared/core/responsive_resolver.rb) that is in the block and holds —
 *   regular-landscape > medium-landscape > compact-landscape > landscape
 *   > regular > medium > compact > default
 * — and only its overrides go on the base, as every codegen draws it (an
 * if / else if chain). Until 2.43.10 every key that held was applied, lowest
 * first, so a `compact` override reached a regular window wherever the
 * regular branch did not set the same attribute
 * (kjui-dynamic-responsive-stacks-overrides-codegen-picks-one).
 *
 * Window-width conditions (jui-responsive-width-threshold-conditions): a
 * block of `minWidth:N` (window width >= N dp) / `maxWidth:N` (< N) keys and
 * no size class. The smallest maxWidth that holds wins, else the largest
 * minWidth. The width is LocalWindowInfo's containerSize in dp, truncated to
 * Int — the expression kjui codegen emits.
 *
 * jsonui-cli shared/core/responsive_resolution_vectors.json holds the cases
 * (ResponsiveResolverVectorTest).
 */
object ResponsiveResolver {

    /** jsonui-cli shared/core/responsive_resolver.rb PRIORITY_ORDER. */
    internal val PRIORITY_ORDER = listOf(
        "regular-landscape", "medium-landscape", "compact-landscape",
        "landscape", "regular", "medium", "compact"
    )

    /** responsive_resolver.rb WIDTH_CONDITION. */
    private val WIDTH_CONDITION = Regex("(minWidth|maxWidth):(0|[1-9][0-9]*)")

    /** A `minWidth:N` / `maxWidth:N` key: (isMin, N); null for any other key. */
    internal fun widthCondition(key: String): Pair<Boolean, Int>? {
        val match = WIDTH_CONDITION.matchEntire(key) ?: return null
        val value = match.groupValues[2].toIntOrNull() ?: return null
        return (match.groupValues[1] == "minWidth") to value
    }

    /**
     * Map the window width to the JSON key used in the responsive block,
     * using the canonical 600/840dp breakpoints.
     */
    internal fun widthSizeClassKey(windowSizeClass: WindowSizeClass): String {
        return when {
            windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND) -> "regular"
            windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND) -> "medium"
            else -> "compact"
        }
    }

    /**
     * The size-class keys that hold in this window, highest priority first.
     * A width key ("compact" / "medium" / "regular") holds in its own band
     * only; "landscape" in any band.
     */
    internal fun resolveMatchingKeys(
        sizeClassKey: String,
        isLandscape: Boolean
    ): List<String> = PRIORITY_ORDER.filter { key ->
        val landscape = key.endsWith("landscape")
        val width = key.removeSuffix("landscape").removeSuffix("-")
        (!landscape || isLandscape) && (width.isEmpty() || width == sizeClassKey) &&
            !(width.isEmpty() && !landscape)
    }

    /**
     * The key of [responsive] that wins: the first of [matchingKeys] the block
     * has — or, for a block of width conditions only, the most narrowing one
     * that holds at [widthDp] (none when the width is unknown).
     */
    internal fun winningKey(responsive: JsonObject, matchingKeys: List<String>, widthDp: Int? = null): String? {
        val keys = responsive.keySet()
        val conditions = keys.mapNotNull { key -> widthCondition(key)?.let { key to it } }
        if (conditions.isNotEmpty() && keys.none { it in PRIORITY_ORDER }) {
            val width = widthDp ?: return null
            val maxes = conditions.filter { !it.second.first }.sortedBy { it.second.second }
            val mins = conditions.filter { it.second.first }.sortedByDescending { it.second.second }
            return (maxes + mins).firstOrNull { (_, c) ->
                if (c.first) width >= c.second else width < c.second
            }?.first
        }
        return matchingKeys.firstOrNull { key -> responsive.get(key)?.isJsonObject == true }
    }

    /**
     * Merge the winning override of the responsive block into the base JSON
     * object; the "responsive" key is removed. "child" and "children" keys in
     * an override are ignored (only attributes are merged).
     */
    internal fun mergeOverrides(
        base: JsonObject,
        responsive: JsonObject,
        matchingKeys: List<String>,
        widthDp: Int? = null
    ): JsonObject {
        val result = base.deepCopy()
        val winner = winningKey(responsive, matchingKeys, widthDp)
        val overrides = winner?.let { responsive.getAsJsonObject(it) }
        if (overrides != null) {
            for (entry in overrides.entrySet()) {
                // Do not override child/children — only attributes
                if (entry.key != "child" && entry.key != "children") {
                    result.add(entry.key, entry.value.deepCopy())
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
    @JvmOverloads
    fun resolveNode(
        json: JsonObject,
        windowSizeClass: WindowSizeClass,
        isLandscape: Boolean,
        widthDp: Int? = null
    ): JsonObject {
        val responsiveElement = json.get("responsive")
        if (responsiveElement == null || !responsiveElement.isJsonObject) {
            return json
        }

        val sizeClassKey = widthSizeClassKey(windowSizeClass)
        val matchingKeys = resolveMatchingKeys(sizeClassKey, isLandscape)
        return mergeOverrides(json, responsiveElement.asJsonObject, matchingKeys, widthDp)
    }

    /**
     * Internal overload using pre-resolved size class key string.
     */
    internal fun resolveNode(
        json: JsonObject,
        sizeClassKey: String,
        isLandscape: Boolean,
        widthDp: Int? = null
    ): JsonObject {
        val responsiveElement = json.get("responsive")
        if (responsiveElement == null || !responsiveElement.isJsonObject) {
            return json
        }

        val matchingKeys = resolveMatchingKeys(sizeClassKey, isLandscape)
        return mergeOverrides(json, responsiveElement.asJsonObject, matchingKeys, widthDp)
    }

    /**
     * Recursively resolve responsive blocks in the entire JSON tree.
     * Walks "child" and "children" arrays, resolving each node.
     *
     * Use this when you want to pre-process an entire layout tree at once
     * (e.g., before caching). For per-node resolution during rendering,
     * use [resolveNode] instead.
     */
    @JvmOverloads
    fun resolveTree(
        json: JsonObject,
        windowSizeClass: WindowSizeClass,
        isLandscape: Boolean,
        widthDp: Int? = null
    ): JsonObject {
        val sizeClassKey = widthSizeClassKey(windowSizeClass)
        return resolveTreeInternal(json, sizeClassKey, isLandscape, widthDp)
    }

    private fun resolveTreeInternal(
        json: JsonObject,
        sizeClassKey: String,
        isLandscape: Boolean,
        widthDp: Int?
    ): JsonObject {
        // Resolve this node's responsive block
        val resolved = resolveNode(json, sizeClassKey, isLandscape, widthDp)

        // Recursively resolve children
        resolveChildArray(resolved, "child", sizeClassKey, isLandscape, widthDp)
        resolveChildArray(resolved, "children", sizeClassKey, isLandscape, widthDp)

        return resolved
    }

    private fun resolveChildArray(
        json: JsonObject,
        key: String,
        sizeClassKey: String,
        isLandscape: Boolean,
        widthDp: Int?
    ) {
        val childElement = json.get(key) ?: return

        when {
            childElement.isJsonArray -> {
                val newArray = JsonArray()
                for (element in childElement.asJsonArray) {
                    if (element.isJsonObject) {
                        newArray.add(resolveTreeInternal(element.asJsonObject, sizeClassKey, isLandscape, widthDp))
                    } else {
                        newArray.add(element)
                    }
                }
                json.add(key, newArray)
            }
            childElement.isJsonObject -> {
                json.add(key, resolveTreeInternal(childElement.asJsonObject, sizeClassKey, isLandscape, widthDp))
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
    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    return ResponsiveResolver.resolveNode(json, windowSizeClass, isWindowLandscape(), windowWidthDp())
}

/**
 * Composable helper that resolves a complete JSON tree using the current
 * window size class and orientation. Useful for pre-processing an entire
 * layout before rendering.
 */
@Composable
fun resolveResponsiveTree(json: JsonObject): JsonObject {
    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    return ResponsiveResolver.resolveTree(json, windowSizeClass, isWindowLandscape(), windowWidthDp())
}

/**
 * Landscape = window wider than tall, derived from the window container size
 * (replaces the deprecated LocalConfiguration.orientation read).
 */
@Composable
private fun isWindowLandscape(): Boolean {
    val containerSize = LocalWindowInfo.current.containerSize
    return containerSize.width > containerSize.height
}

/**
 * The window's width in dp, truncated to Int: kjui codegen's
 * `with(LocalDensity.current) { LocalWindowInfo.current.containerSize.width.toDp().value.toInt() }`.
 */
@Composable
private fun windowWidthDp(): Int {
    val containerSize = LocalWindowInfo.current.containerSize
    return with(LocalDensity.current) { containerSize.width.toDp().value.toInt() }
}
