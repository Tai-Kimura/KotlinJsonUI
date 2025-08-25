package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
            
            // Parse background color
            val backgroundColor = json.get("background")?.asString?.let {
                try {
                    Color(android.graphics.Color.parseColor(it))
                } catch (e: Exception) {
                    null
                }
            }
            
            // Build modifier
            var modifier = ModifierBuilder.buildModifier(json)
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
            
            // Create Box with stacked children
            Box(
                modifier = modifier,
                contentAlignment = alignment
            ) {
                // Render each child in order (first child at bottom, last at top)
                childrenArray.forEach { childElement ->
                    if (childElement.isJsonObject) {
                        val childJson = childElement.asJsonObject
                        
                        // Check if child has its own alignment
                        val childAlignment = parseChildAlignment(childJson)
                        
                        if (childAlignment != null) {
                            // Child has specific alignment
                            Box(
                                modifier = Modifier.align(childAlignment)
                            ) {
                                DynamicView(
                                    json = childJson,
                                    data = data
                                )
                            }
                        } else {
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
        
        private fun parseChildAlignment(json: JsonObject): Alignment? {
            // Check for individual alignment properties
            val hasAlignment = json.has("alignTop") || json.has("alignBottom") ||
                             json.has("alignLeft") || json.has("alignRight") ||
                             json.has("centerHorizontal") || json.has("centerVertical") ||
                             json.has("centerInParent")
            
            if (!hasAlignment) return null
            
            // Determine alignment based on combination of properties
            val alignTop = json.get("alignTop")?.asBoolean == true
            val alignBottom = json.get("alignBottom")?.asBoolean == true
            val alignLeft = json.get("alignLeft")?.asBoolean == true
            val alignRight = json.get("alignRight")?.asBoolean == true
            val centerHorizontal = json.get("centerHorizontal")?.asBoolean == true
            val centerVertical = json.get("centerVertical")?.asBoolean == true
            val centerInParent = json.get("centerInParent")?.asBoolean == true
            
            return when {
                centerInParent -> Alignment.Center
                alignTop && alignLeft -> Alignment.TopStart
                alignTop && alignRight -> Alignment.TopEnd
                alignTop && centerHorizontal -> Alignment.TopCenter
                alignTop -> Alignment.TopCenter
                alignBottom && alignLeft -> Alignment.BottomStart
                alignBottom && alignRight -> Alignment.BottomEnd
                alignBottom && centerHorizontal -> Alignment.BottomCenter
                alignBottom -> Alignment.BottomCenter
                alignLeft && centerVertical -> Alignment.CenterStart
                alignRight && centerVertical -> Alignment.CenterEnd
                alignLeft -> Alignment.CenterStart
                alignRight -> Alignment.CenterEnd
                centerHorizontal && centerVertical -> Alignment.Center
                centerHorizontal -> Alignment.Center
                centerVertical -> Alignment.Center
                else -> null
            }
        }
    }
}