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
import com.kotlinjsonui.dynamic.DynamicView
import com.kotlinjsonui.dynamic.DynamicViews
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ColorParser

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
 */
class DynamicSafeAreaViewComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            // Apply lifecycle effects first
            ModifierBuilder.ApplyLifecycleEffects(json, data)

            // Parse edges to apply safe area padding
            // Support both "edges" and "safeAreaInsetPositions" (alias for cross-platform compatibility)
            val edgesArray = json.get("edges")?.asJsonArray
                ?: json.get("safeAreaInsetPositions")?.asJsonArray
            val edges = edgesArray?.let { arr ->
                arr.map { it.asString }
            } ?: listOf("all")
            
            // Check if keyboard padding should be applied
            val ignoreKeyboard = json.get("ignoreKeyboard")?.asBoolean ?: false

            // Parse orientation for child layout
            val orientation = json.get("orientation")?.asString ?: "vertical"
            
            // Parse background color
            val backgroundColor = json.get("background")?.asString?.let {
                ColorParser.parseColorString(it)
            }
            
            // Build base modifier
            var modifier = ModifierBuilder.buildSizeModifier(json, defaultFillMaxWidth = true)
            
            // Apply safe area padding based on edges
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
            modifier = ModifierBuilder.applyMargins(modifier, json)
            modifier = ModifierBuilder.applyPadding(modifier, json)
            
            // Apply background color
            backgroundColor?.let {
                modifier = modifier.background(it)
            }
            
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

            when (orientation) {
                "horizontal" -> {
                    Row(modifier = modifier) {
                        if (childList.size == 1) {
                            DynamicView(
                                json = childList[0],
                                data = data
                            )
                        } else if (childList.isNotEmpty()) {
                            DynamicViews(
                                components = childList,
                                data = data
                            )
                        }
                    }
                }
                else -> { // "vertical" or default
                    Column(modifier = modifier) {
                        if (childList.size == 1) {
                            DynamicView(
                                json = childList[0],
                                data = data
                            )
                        } else if (childList.isNotEmpty()) {
                            DynamicViews(
                                components = childList,
                                data = data
                            )
                        }
                    }
                }
            }
        }
    }
}