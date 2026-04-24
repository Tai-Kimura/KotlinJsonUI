package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.LocalSafeAreaConfig
import com.kotlinjsonui.dynamic.components.DynamicContainerComponent.Companion.renderChildInBox
import com.kotlinjsonui.dynamic.components.DynamicContainerComponent.Companion.renderChildInColumn
import com.kotlinjsonui.dynamic.components.DynamicContainerComponent.Companion.renderChildInRow
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ColorParser
import androidx.compose.ui.platform.LocalContext

/**
 * Dynamic SafeAreaView Component Converter
 * Converts JSON to SafeAreaView composable at runtime
 *
 * Supported JSON attributes:
 * - edges: Array of edges to apply padding ["top", "bottom", "start", "end", "all"]
 * - orientation: "horizontal" or "vertical" - determines stack direction for children
 * - ignoreKeyboard: Boolean to ignore keyboard padding
 * - background: String hex color for background
 * - child/children: Child components to render within safe area
 * - padding/margins: Additional spacing properties
 *
 * This component ensures content doesn't overlap with system UI elements
 * like status bar, navigation bar, and software keyboard
 *
 * SafeAreaView needs special modifier ordering: background must go BEFORE
 * systemBarsPadding so it extends to screen edges. Therefore we build
 * the modifier chain manually instead of using the composite buildModifier.
 */
class DynamicSafeAreaViewComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            val context = LocalContext.current

            // Get parent SafeAreaConfig (e.g., from TabView)
            val safeAreaConfig = LocalSafeAreaConfig.current

            // Apply lifecycle effects first
            ModifierBuilder.ApplyLifecycleEffects(json, data)

            // Parse edges to apply safe area padding
            // Support both "edges" and "safeAreaInsetPositions" (alias for cross-platform compatibility)
            val edgesArray = json.get("edges")?.asJsonArray
                ?: json.get("safeAreaInsetPositions")?.asJsonArray
            val requestedEdges = edgesArray?.let { arr ->
                arr.map { it.asString }
            } ?: listOf("all")

            // Filter edges based on parent SafeAreaConfig
            val edges = requestedEdges.toMutableList().apply {
                if (safeAreaConfig.ignoreBottom) {
                    remove("bottom")
                    if (contains("all")) {
                        remove("all")
                        addAll(listOf("top", "start", "end"))
                    }
                }
                if (safeAreaConfig.ignoreTop) {
                    remove("top")
                    if (contains("all")) {
                        remove("all")
                        addAll(listOf("bottom", "start", "end"))
                    }
                }
            }.distinct()

            // Check if keyboard padding should be applied
            val ignoreKeyboard = json.get("ignoreKeyboard")?.asBoolean ?: false

            // Parse orientation for child layout (null means Box/ZStack)
            val orientation = json.get("orientation")?.asString

            // Parse background color (supports @{binding})
            val backgroundColor = ColorParser.parseColorWithBinding(json, "background", data, context)

            // Build modifier manually: special ordering required for SafeAreaView
            // background must go BEFORE systemBarsPadding so it extends to screen edges
            var modifier = ModifierBuilder.applyTestTag(Modifier, json)
            modifier = ModifierBuilder.applySize(modifier, json, defaultFillMaxWidth = true)

            // Apply background color BEFORE systemBarsPadding so it extends to screen edges
            backgroundColor?.let {
                modifier = modifier.background(it)
            }

            // Apply safe area padding based on edges (after background)
            modifier = when {
                edges.contains("all") -> {
                    modifier.systemBarsPadding()
                }
                else -> {
                    if (edges.contains("top")) {
                        modifier = modifier.statusBarsPadding()
                    }
                    if (edges.contains("bottom")) {
                        modifier = modifier.navigationBarsPadding()
                    }
                    // For start/end, we use systemBarsPadding which handles both
                    if (edges.contains("start") || edges.contains("end")) {
                        modifier = modifier.systemBarsPadding()
                    }
                    modifier
                }
            }

            // Apply keyboard padding unless ignored
            if (!ignoreKeyboard) {
                modifier = modifier.imePadding()
            }

            // Apply additional margins and padding
            modifier = ModifierBuilder.applyMargins(modifier, json, data)
            modifier = ModifierBuilder.applyPadding(modifier, json)

            // Get children - support both 'child' and 'children'
            val childrenArray: JsonArray = when {
                json.has("children") && json.get("children").isJsonArray ->
                    json.getAsJsonArray("children")
                json.has("child") && json.get("child").isJsonArray ->
                    json.getAsJsonArray("child")
                json.has("child") && json.get("child").isJsonObject -> {
                    // Single child as object, wrap in array
                    JsonArray().apply { add(json.getAsJsonObject("child")) }
                }
                else -> JsonArray() // Empty array if no children
            }

            // Render content in safe area based on orientation
            val childList = mutableListOf<JsonObject>()
            childrenArray.forEach { element ->
                if (element.isJsonObject) {
                    childList.add(element.asJsonObject)
                }
            }

            // Route children through the scope-aware renderers used by
            // DynamicContainerComponent so weight/alignment/visibility are
            // applied in the correct scope. Plain DynamicView(s) calls do
            // not apply weight, which causes children like ScrollView with
            // weight:1 to lose their slot and push later siblings off-screen.
            when (orientation) {
                "horizontal" -> {
                    Row(modifier = modifier) {
                        childList.forEach { child ->
                            renderChildInRow(child, data, context)
                        }
                    }
                }
                "vertical" -> {
                    Column(modifier = modifier) {
                        childList.forEach { child ->
                            renderChildInColumn(child, data, context)
                        }
                    }
                }
                else -> { // No orientation = Box (ZStack equivalent)
                    Box(modifier = modifier) {
                        childList.forEach { child ->
                            renderChildInBox(child, data, context)
                        }
                    }
                }
            }
        }
    }
}
