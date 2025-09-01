package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.processDataBinding
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder

/**
 * Dynamic Image Component Converter
 * Converts JSON to Image composable at runtime
 *
 * Supported JSON attributes (matching Ruby implementation):
 * - src: String resource name (supports @{variable} binding)
 * - contentDescription: String for accessibility
 * - contentMode: String ("aspectFill", "aspectFit", "center")
 * - size: Number for square size
 * - width/height: Number dimensions
 * - padding/paddings: Number or Array for padding
 * - margins: Array or individual margin properties
 * - tint: String hex color for tinting
 * - alpha: Float opacity value (0-1)
 */
class DynamicImageComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            // Parse image source with data binding
            val rawSrc = json.get("src")?.asString ?: "placeholder"
            val imageName = processDataBinding(rawSrc, data)

            // Get content description
            val contentDescription = json.get("contentDescription")?.asString ?: ""

            // Try to get the resource ID
            val context = LocalContext.current
            val resourceId = context.resources.getIdentifier(
                imageName,
                "drawable",
                context.packageName
            )

            // Build modifier
            val modifier = buildModifier(json)

            // Parse content scale/mode
            val contentScale = when (json.get("contentMode")?.asString?.lowercase()) {
                "aspectfill" -> ContentScale.Crop
                "aspectfit" -> ContentScale.Fit
                "center" -> ContentScale.None
                "fill" -> ContentScale.FillBounds
                "inside" -> ContentScale.Inside
                else -> ContentScale.Fit // Default
            }

            // Parse alpha
            val alpha = json.get("alpha")?.asFloat ?: 1f

            // Create the Image
            if (resourceId != 0) {
                Image(
                    painter = painterResource(id = resourceId),
                    contentDescription = contentDescription,
                    modifier = modifier,
                    contentScale = contentScale,
                    alpha = alpha
                )
            } else {
                // Fallback for missing image - could show placeholder or error image
                // For now, just create an empty composable
            }
        }

        private fun buildModifier(json: JsonObject): Modifier {
            // Use ModifierBuilder for basic size and spacing
            var modifier = ModifierBuilder.buildModifier(json)

            // Background color (before clip for proper rendering)
            json.get("background")?.asString?.let { colorStr ->
                ColorParser.parseColorString(colorStr)?.let { color ->
                    modifier = modifier.background(color)
                }
            }

            // Corner radius (clip)
            json.get("cornerRadius")?.asFloat?.let { radius ->
                val shape = RoundedCornerShape(radius.dp)
                modifier = modifier.clip(shape)

                // If we have a border, apply it with the same shape
                json.get("borderColor")?.asString?.let { borderColorStr ->
                    ColorParser.parseColorString(borderColorStr)?.let { borderColor ->
                        val borderWidth = json.get("borderWidth")?.asFloat ?: 1f
                        modifier = modifier.border(borderWidth.dp, borderColor, shape)
                    }
                }
            } ?: run {
                // No corner radius, but might still have border
                json.get("borderColor")?.asString?.let { borderColorStr ->
                    ColorParser.parseColorString(borderColorStr)?.let { borderColor ->
                        val borderWidth = json.get("borderWidth")?.asFloat ?: 1f
                        modifier = modifier.border(borderWidth.dp, borderColor)
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
