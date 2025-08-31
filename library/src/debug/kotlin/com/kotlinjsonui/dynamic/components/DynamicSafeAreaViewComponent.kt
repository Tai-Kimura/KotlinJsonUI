package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
            // Parse edges to apply safe area padding
            val edges = json.get("edges")?.asJsonArray?.let { edgesArray ->
                edgesArray.map { it.asString }
            } ?: listOf("all")
            
            // Check if keyboard padding should be applied
            val ignoreKeyboard = json.get("ignoreKeyboard")?.asBoolean ?: false
            
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
            
            // Render content in safe area
            Box(modifier = modifier) {
                if (childrenArray.size() == 1 && childrenArray[0].isJsonObject) {
                    // Single child - render directly
                    DynamicView(
                        json = childrenArray[0].asJsonObject,
                        data = data
                    )
                } else if (childrenArray.size() > 0) {
                    // Multiple children - render all
                    val childList = mutableListOf<JsonObject>()
                    childrenArray.forEach { element ->
                        if (element.isJsonObject) {
                            childList.add(element.asJsonObject)
                        }
                    }
                    DynamicViews(
                        components = childList,
                        data = data
                    )
                }
            }
        }
    }
}