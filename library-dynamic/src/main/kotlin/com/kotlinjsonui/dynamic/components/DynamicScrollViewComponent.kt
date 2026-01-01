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
import androidx.compose.foundation.layout.imePadding
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.DynamicView
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.dashedBorder
import com.kotlinjsonui.dynamic.helpers.dottedBorder
import androidx.compose.ui.platform.LocalContext

/**
 * Dynamic ScrollView Component Converter
 * Converts JSON to scrollable composable at runtime
 *
 * Supported JSON attributes (matching Ruby implementation):
 * - scrollEnabled: Boolean to enable/disable scrolling (default true)
 * - horizontalScroll: Boolean to enable horizontal scrolling
 * - orientation: "horizontal" | "vertical" (alternative to horizontalScroll)
 * - keyboardAvoidance: Boolean to enable keyboard avoidance (default true)
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
            // Apply lifecycle effects first
            ModifierBuilder.ApplyLifecycleEffects(json, data)

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

            // Check if keyboard avoidance is enabled (default true)
            val keyboardAvoidance = json.get("keyboardAvoidance")?.asBoolean ?: true

            val context = LocalContext.current

            // Build modifier with scroll
            var modifier = buildModifier(json, data, context)

            // Apply keyboard avoidance if enabled
            if (keyboardAvoidance) {
                modifier = modifier.imePadding()
            }

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

        private fun buildModifier(json: JsonObject, data: Map<String, Any>, context: android.content.Context): Modifier {
            // Use ModifierBuilder for basic size, margins, and padding
            var modifier = ModifierBuilder.buildModifier(json)

            // Background color (before clip for proper rendering, supports @{binding})
            ColorParser.parseColorWithBinding(json, "background", data, context)?.let { color ->
                modifier = modifier.background(color)
            }

            // Corner radius (clip)
            val shape = json.get("cornerRadius")?.asFloat?.let { radius ->
                RoundedCornerShape(radius.dp).also {
                    modifier = modifier.clip(it)
                }
            }

            // Border (supports solid/dashed/dotted, supports @{binding})
            ColorParser.parseColorWithBinding(json, "borderColor", data, context)?.let { borderColor ->
                    val borderWidth = json.get("borderWidth")?.asFloat ?: 1f
                    val borderStyle = json.get("borderStyle")?.asString ?: "solid"

                    modifier = when (borderStyle) {
                        "dashed" -> modifier.dashedBorder(borderWidth.dp, borderColor, shape)
                        "dotted" -> modifier.dottedBorder(borderWidth.dp, borderColor, shape)
                        else -> { // "solid" or default
                            if (shape != null) {
                                modifier.border(borderWidth.dp, borderColor, shape)
                            } else {
                                modifier.border(borderWidth.dp, borderColor)
                            }
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