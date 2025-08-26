package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.DynamicView
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder

/**
 * Dynamic ScrollView Component Converter
 * Converts JSON to scrollable composable at runtime
 *
 * Supported JSON attributes (matching Ruby implementation):
 * - scrollEnabled: Boolean to enable/disable scrolling (default true)
 * - horizontalScroll: Boolean to enable horizontal scrolling
 * - orientation: "horizontal" | "vertical" (alternative to horizontalScroll)
 * - child: JsonObject or Array of child components
 * - background: String hex color for background
 * - borderColor: String hex color for border
 * - borderWidth: Float width of border
 * - cornerRadius: Float corner radius
 * - shadow: Boolean or number for elevation
 * - padding/paddings: Number or Array for padding
 * - margins: Array or individual margin properties
 * - width/height: Number dimensions
 *
 * Note: Uses regular scrollable modifiers instead of LazyColumn/LazyRow
 * for simpler implementation. Could be enhanced to use lazy layouts
 * for better performance with large content.
 */
class DynamicScrollViewComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            // Determine scroll direction
            val isHorizontal = when {
                // 1. horizontalScroll attribute has highest priority
                json.has("horizontalScroll") -> json.get("horizontalScroll").asBoolean
                // 2. orientation attribute is next
                json.has("orientation") -> json.get("orientation").asString == "horizontal"
                // 3. Check child element's orientation
                else -> {
                    val child = json.get("child")
                    when {
                        child?.isJsonObject == true -> {
                            val childObj = child.asJsonObject
                            childObj.get("type")?.asString == "View" &&
                                    childObj.get("orientation")?.asString == "horizontal"
                        }

                        child?.isJsonArray == true -> {
                            val firstView = child.asJsonArray.firstOrNull { element ->
                                element.isJsonObject &&
                                        element.asJsonObject.get("type")?.asString == "View"
                            }
                            firstView?.asJsonObject?.get("orientation")?.asString == "horizontal"
                        }

                        else -> false
                    }
                }
            }

            // Parse children
            val children = parseChildren(json)

            // Check if scroll is enabled (default true)
            val scrollEnabled = json.get("scrollEnabled")?.asBoolean ?: true

            // Build modifier with scroll
            var modifier = buildModifier(json)

            // Apply scroll modifier based on direction only if scrollEnabled
            if (scrollEnabled) {
                val scrollState = rememberScrollState()
                modifier = if (isHorizontal) {
                    modifier.horizontalScroll(scrollState)
                } else {
                    modifier.verticalScroll(scrollState)
                }
            }

            // Create the scrollable container
            if (isHorizontal) {
                Row(modifier = modifier) {
                    children.forEach { child ->
                        DynamicView(child, data)
                    }
                }
            } else {
                Column(modifier = modifier) {
                    children.forEach { child ->
                        DynamicView(child, data)
                    }
                }
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

        private fun buildModifier(json: JsonObject): Modifier {
            // Use ModifierBuilder for basic size, margins, and padding
            var modifier = ModifierBuilder.buildModifier(json)

            // Background color (before clip for proper rendering)
            json.get("background")?.asString?.let { colorStr ->
                try {
                    val color = Color(android.graphics.Color.parseColor(colorStr))
                    modifier = modifier.background(color)
                } catch (e: Exception) {
                    // Invalid color
                }
            }

            // Corner radius (clip)
            json.get("cornerRadius")?.asFloat?.let { radius ->
                val shape = RoundedCornerShape(radius.dp)
                modifier = modifier.clip(shape)

                // If we have a border, apply it with the same shape
                json.get("borderColor")?.asString?.let { borderColorStr ->
                    try {
                        val borderColor = Color(android.graphics.Color.parseColor(borderColorStr))
                        val borderWidth = json.get("borderWidth")?.asFloat ?: 1f
                        modifier = modifier.border(borderWidth.dp, borderColor, shape)
                    } catch (e: Exception) {
                        // Invalid border color
                    }
                }
            } ?: run {
                // No corner radius, but might still have border
                json.get("borderColor")?.asString?.let { borderColorStr ->
                    try {
                        val borderColor = Color(android.graphics.Color.parseColor(borderColorStr))
                        val borderWidth = json.get("borderWidth")?.asFloat ?: 1f
                        modifier = modifier.border(borderWidth.dp, borderColor)
                    } catch (e: Exception) {
                        // Invalid border color
                    }
                }
            }

            // Shadow/elevation
            json.get("shadow")?.let { shadow ->
                when {
                    shadow.isJsonPrimitive -> {
                        val primitive = shadow.asJsonPrimitive
                        when {
                            primitive.isBoolean && primitive.asBoolean -> {
                                modifier = modifier.shadow(6.dp)
                            }

                            primitive.isNumber -> {
                                modifier = modifier.shadow(primitive.asFloat.dp)
                            }
                        }
                    }
                }
            }

            return modifier
        }
    }
}