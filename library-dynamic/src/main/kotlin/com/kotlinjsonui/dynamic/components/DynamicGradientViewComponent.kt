package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.DynamicView
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ColorParser
import androidx.compose.ui.platform.LocalContext

/**
 * Dynamic GradientView Component Converter
 * Converts JSON to Box with gradient background composable at runtime
 * 
 * Supported JSON attributes (matching Ruby implementation):
 * - colors/items: Array of color strings for gradient
 * - orientation: "horizontal" | "vertical" | "diagonal" gradient direction
 * - startPoint/endPoint: Alternative to orientation (top/bottom, left/right, etc.)
 * - cornerRadius: Float corner radius
 * - child: JsonObject or Array of child components
 * - width/height: Number dimensions
 * - padding/paddings: Number or Array for padding
 * - margins: Array or individual margin properties
 * 
 * Note: Defaults to vertical gradient from black to white if no colors specified
 */
class DynamicGradientViewComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            // Apply lifecycle effects first
            ModifierBuilder.ApplyLifecycleEffects(json, data)

            val context = LocalContext.current

            // Parse gradient colors (supports @{binding})
            val colorsElement = json.get("colors") ?: json.get("items")
            val colors = when {
                colorsElement?.isJsonArray == true -> {
                    parseColors(colorsElement.asJsonArray, data, context)
                }
                colorsElement?.isJsonPrimitive == true && colorsElement.asString.contains("@{") -> {
                    // Dynamic colors from data binding
                    val pattern = "@\\{([^}]+)\\}".toRegex()
                    val variable = pattern.find(colorsElement.asString)?.groupValues?.get(1)
                    if (variable != null) {
                        when (val colorList = data[variable]) {
                            is List<*> -> colorList.mapNotNull { colorStr ->
                                ColorParser.parseColorStringWithBinding(colorStr?.toString(), data, context)
                            }
                            is Array<*> -> colorList.mapNotNull { colorStr ->
                                ColorParser.parseColorStringWithBinding(colorStr?.toString(), data, context)
                            }
                            else -> listOf(Color.Black, Color.White)
                        }
                    } else {
                        listOf(Color.Black, Color.White)
                    }
                }
                else -> listOf(Color.Black, Color.White) // Default gradient
            }
            
            // Determine gradient type
            val gradientBrush = when {
                json.has("orientation") -> {
                    when (json.get("orientation").asString) {
                        "horizontal" -> Brush.horizontalGradient(colors)
                        "vertical" -> Brush.verticalGradient(colors)
                        "diagonal" -> Brush.linearGradient(
                            colors = colors,
                            start = Offset(0f, 0f),
                            end = Offset.Infinite
                        )
                        else -> Brush.verticalGradient(colors)
                    }
                }
                json.has("startPoint") && json.has("endPoint") -> {
                    val startPoint = json.get("startPoint").asString
                    val endPoint = json.get("endPoint").asString
                    when {
                        (startPoint in listOf("top", "bottom") && endPoint in listOf("top", "bottom")) ->
                            Brush.verticalGradient(colors)
                        (startPoint in listOf("left", "leading", "right", "trailing") && 
                         endPoint in listOf("left", "leading", "right", "trailing")) ->
                            Brush.horizontalGradient(colors)
                        else -> Brush.linearGradient(
                            colors = colors,
                            start = Offset(0f, 0f),
                            end = Offset.Infinite
                        )
                    }
                }
                else -> Brush.verticalGradient(colors) // Default to vertical
            }
            
            // Build modifier
            var modifier = ModifierBuilder.buildModifier(json)
            
            // Apply gradient background
            modifier = modifier.background(gradientBrush)
            
            // Apply corner radius if specified
            json.get("cornerRadius")?.asFloat?.let { radius ->
                val shape = RoundedCornerShape(radius.dp)
                modifier = modifier.clip(shape)
            }
            
            // Create the gradient box
            Box(modifier = modifier) {
                // Parse and render children
                val children = parseChildren(json)
                children.forEach { child ->
                    DynamicView(child, data)
                }
            }
        }
        
        private fun parseColors(jsonArray: JsonArray, data: Map<String, Any>, context: android.content.Context): List<Color> {
            return jsonArray.mapNotNull { element ->
                when {
                    element.isJsonPrimitive -> {
                        ColorParser.parseColorStringWithBinding(element.asString, data, context)
                    }
                    else -> null
                }
            }.ifEmpty {
                listOf(Color.Black, Color.White) // Fallback to default
            }
        }
        
        private fun parseChildren(json: JsonObject): List<JsonObject> {
            return when (val child = json.get("child")) {
                null -> emptyList()
                is JsonObject -> listOf(child.asJsonObject)
                is JsonArray -> child.map { it.asJsonObject }
                else -> emptyList()
            }
        }
    }
}