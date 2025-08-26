package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.DynamicView
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder

/**
 * Dynamic ZStack Component Converter
 * Converts JSON to Box (z-axis stack) composable at runtime
 * 
 * Supported JSON attributes:
 * - alignment: String alignment value (center, topStart, topCenter, topEnd, centerStart, centerEnd, bottomStart, bottomCenter, bottomEnd)
 * - child/children: Array of child components to stack on z-axis
 * - background: String hex color for background
 * - padding/margins: Spacing properties
 * - width/height: Size properties
 * 
 * Children are rendered in order, with later children appearing on top
 */
class DynamicZStackComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            // Parse alignment
            val alignment = when (json.get("alignment")?.asString?.lowercase()) {
                "topleft", "topstart" -> Alignment.TopStart
                "topcenter", "top" -> Alignment.TopCenter
                "topright", "topend" -> Alignment.TopEnd
                "centerleft", "centerstart", "left" -> Alignment.CenterStart
                "center" -> Alignment.Center
                "centerright", "centerend", "right" -> Alignment.CenterEnd
                "bottomleft", "bottomstart" -> Alignment.BottomStart
                "bottomcenter", "bottom" -> Alignment.BottomCenter
                "bottomright", "bottomend" -> Alignment.BottomEnd
                else -> Alignment.TopStart
            }
            
            // Build modifier with correct order: size -> margins -> background -> padding
            var modifier: Modifier = Modifier
            
            // 1. Apply size
            modifier = ModifierBuilder.applySize(modifier, json)
            
            // 2. Apply margins (outer spacing)
            modifier = ModifierBuilder.applyMargins(modifier, json)
            
            // 3. Background color (before padding so padding is inside the background)
            json.get("background")?.asString?.let { colorStr ->
                try {
                    val color = Color(android.graphics.Color.parseColor(colorStr))
                    modifier = modifier.background(color)
                } catch (e: Exception) {
                    // Invalid color
                }
            }
            
            // 4. Apply padding (inner spacing) - MUST be applied last
            modifier = ModifierBuilder.applyPadding(modifier, json)
            
            // 5. Apply opacity
            modifier = ModifierBuilder.applyOpacity(modifier, json)
            
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
            
            // Create Box with stacked children
            Box(
                modifier = modifier,
                contentAlignment = alignment
            ) {
                // Render each child in order (first child at bottom, last at top)
                childrenArray.forEach { childElement ->
                    if (childElement.isJsonObject) {
                        val childJson = childElement.asJsonObject
                        
                        // Use ModifierBuilder to get alignment (supports BiasAlignment)
                        val childAlignment = ModifierBuilder.getChildAlignment(childJson, "Box")
                        
                        when (childAlignment) {
                            is Alignment -> {
                                Box(
                                    modifier = Modifier.align(childAlignment)
                                ) {
                                    DynamicView(
                                        json = childJson,
                                        data = data
                                    )
                                }
                            }
                            is BiasAlignment -> {
                                Box(
                                    modifier = Modifier.align(childAlignment)
                                ) {
                                    DynamicView(
                                        json = childJson,
                                        data = data
                                    )
                                }
                            }
                            else -> {
                                // Use default alignment
                                DynamicView(
                                    json = childJson,
                                    data = data
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}