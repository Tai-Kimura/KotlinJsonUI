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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.DynamicView
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder

/**
 * Dynamic GradientView Component Converter
 * Converts JSON to Box with gradient background composable at runtime.
 * Reference: gradientview_component.rb in kjui_tools.
 *
 * Supported JSON attributes:
 * - colors/items: Array of color strings for gradient (supports @{binding})
 * - orientation: "horizontal" | "vertical" | "diagonal" gradient direction
 * - startPoint/endPoint: Alternative to orientation (top/bottom, left/right, etc.)
 * - cornerRadius: Float corner radius (applies clip)
 * - child/children: Child components to render inside gradient
 * - Standard modifier attributes (padding, margins, alpha, onClick, etc.)
 *
 * Gradient types:
 * - horizontal → Brush.horizontalGradient
 * - vertical → Brush.verticalGradient
 * - diagonal/other → Brush.linearGradient
 *
 * Defaults to vertical gradient from black to white if no colors specified.
 *
 * Modifier order: testTag → margins → size → alpha → clickable → padding + gradient background
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

            // Parse gradient colors from 'colors' or 'items' array
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
                        resolveColorList(data[variable], data, context)
                    } else {
                        DEFAULT_COLORS
                    }
                }
                else -> DEFAULT_COLORS
            }

            // Determine gradient brush based on orientation or startPoint/endPoint
            val gradientBrush = resolveGradientBrush(json, colors)

            // Build modifier: testTag → margins → size → alpha → clickable → padding
            var modifier = ModifierBuilder.buildModifier(json, data, context = context)

            // Apply gradient background
            modifier = modifier.background(gradientBrush)

            // Apply corner radius clip
            json.get("cornerRadius")?.asFloat?.let { radius ->
                modifier = modifier.clip(RoundedCornerShape(radius.dp))
            }

            // Render gradient box with children
            Box(modifier = modifier) {
                val children = DynamicContainerComponent.getChildren(json)
                children.forEach { child ->
                    DynamicView(child, data)
                }
            }
        }

        private val DEFAULT_COLORS = listOf(Color.Black, Color.White)

        /**
         * Parse color array from JSON, resolving each color string via ColorParser.
         */
        private fun parseColors(
            jsonArray: JsonArray,
            data: Map<String, Any>,
            context: android.content.Context
        ): List<Color> {
            return jsonArray.mapNotNull { element ->
                when {
                    element.isJsonPrimitive ->
                        ColorParser.parseColorStringWithBinding(element.asString, data, context)
                    else -> null
                }
            }.ifEmpty { DEFAULT_COLORS }
        }

        /**
         * Resolve a bound color list from data (List or Array).
         */
        private fun resolveColorList(
            value: Any?,
            data: Map<String, Any>,
            context: android.content.Context
        ): List<Color> {
            return when (value) {
                is List<*> -> value.mapNotNull { colorStr ->
                    ColorParser.parseColorStringWithBinding(colorStr?.toString(), data, context)
                }
                is Array<*> -> value.mapNotNull { colorStr ->
                    ColorParser.parseColorStringWithBinding(colorStr?.toString(), data, context)
                }
                else -> DEFAULT_COLORS
            }.ifEmpty { DEFAULT_COLORS }
        }

        /**
         * Determine gradient Brush based on 'orientation' or 'startPoint'/'endPoint'.
         * Defaults to vertical gradient.
         */
        private fun resolveGradientBrush(json: JsonObject, colors: List<Color>): Brush {
            // Orientation-based
            if (json.has("orientation")) {
                return when (json.get("orientation").asString) {
                    "horizontal" -> Brush.horizontalGradient(colors)
                    "vertical" -> Brush.verticalGradient(colors)
                    "diagonal" -> Brush.linearGradient(
                        colors = colors,
                        start = Offset(0f, 0f),
                        end = Offset.Infinite
                    )
                    else -> Brush.linearGradient(
                        colors = colors,
                        start = Offset(0f, 0f),
                        end = Offset.Infinite
                    )
                }
            }

            // startPoint/endPoint-based
            if (json.has("startPoint") && json.has("endPoint")) {
                val startPoint = json.get("startPoint").asString
                val endPoint = json.get("endPoint").asString
                return when {
                    startPoint in VERTICAL_POINTS && endPoint in VERTICAL_POINTS ->
                        Brush.verticalGradient(colors)
                    startPoint in HORIZONTAL_POINTS && endPoint in HORIZONTAL_POINTS ->
                        Brush.horizontalGradient(colors)
                    else -> Brush.linearGradient(
                        colors = colors,
                        start = Offset(0f, 0f),
                        end = Offset.Infinite
                    )
                }
            }

            // Default to vertical
            return Brush.verticalGradient(colors)
        }

        private val VERTICAL_POINTS = setOf("top", "bottom")
        private val HORIZONTAL_POINTS = setOf("left", "leading", "right", "trailing")
    }
}
