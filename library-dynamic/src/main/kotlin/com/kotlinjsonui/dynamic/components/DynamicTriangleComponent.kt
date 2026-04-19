package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder

/**
 * Dynamic Triangle Component Converter
 * Converts JSON to Triangle shape composable at runtime using Canvas.
 *
 * Supported JSON attributes:
 * - size: Number for both width and height (square bounding box)
 * - width: Number for triangle bounding box width
 * - height: Number for triangle bounding box height
 * - color/background: String hex color for fill (default #000000)
 * - direction: "up" | "down" | "left" | "right" (default "up")
 * - padding/paddings: Number or Array for padding
 * - margins: Array or individual margin properties
 * - alpha/opacity: Float opacity value (0-1), supports @{binding}
 * - onClick/onclick: String event handler name
 */
class DynamicTriangleComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            val context = LocalContext.current

            // Parse size: "size" for square, or individual width/height
            val sizeValue = json.get("size")?.asFloat
            val triangleWidth = sizeValue ?: json.get("width")?.asFloat ?: 20f
            val triangleHeight = sizeValue ?: json.get("height")?.asFloat ?: 20f

            // Parse color
            val fillColor = ColorParser.parseColorWithBinding(json, "color", data, context)
                ?: ColorParser.parseColorWithBinding(json, "background", data, context)
                ?: Color.Black

            // Parse direction
            val direction = json.get("direction")?.asString ?: "up"

            // Build modifier in the specified order:
            // testTag → margins → size → alpha → clickable → padding
            var modifier: Modifier = Modifier

            // 1. testTag
            modifier = ModifierBuilder.applyTestTag(modifier, json)

            // 2. margins
            modifier = ModifierBuilder.applyMargins(modifier, json, data)

            // 3. size
            modifier = modifier.size(width = triangleWidth.dp, height = triangleHeight.dp)

            // 4. alpha
            modifier = ModifierBuilder.applyAlpha(modifier, json, data)

            // 5. clickable
            modifier = ModifierBuilder.applyClickable(modifier, json, data)

            // 6. padding
            modifier = ModifierBuilder.applyPadding(modifier, json)

            // Lifecycle effects
            if (ModifierBuilder.hasLifecycleEvents(json)) {
                ModifierBuilder.ApplyLifecycleEffects(json, data)
            }

            // Draw triangle on Canvas
            Canvas(modifier = modifier) {
                val w = size.width
                val h = size.height

                val path = Path().apply {
                    when (direction) {
                        "up" -> {
                            moveTo(w / 2f, 0f)
                            lineTo(w, h)
                            lineTo(0f, h)
                            close()
                        }
                        "down" -> {
                            moveTo(0f, 0f)
                            lineTo(w, 0f)
                            lineTo(w / 2f, h)
                            close()
                        }
                        "left" -> {
                            moveTo(0f, h / 2f)
                            lineTo(w, 0f)
                            lineTo(w, h)
                            close()
                        }
                        "right" -> {
                            moveTo(0f, 0f)
                            lineTo(w, h / 2f)
                            lineTo(0f, h)
                            close()
                        }
                        else -> {
                            // Default to "up"
                            moveTo(w / 2f, 0f)
                            lineTo(w, h)
                            lineTo(0f, h)
                            close()
                        }
                    }
                }

                drawPath(path = path, color = fillColor)
            }
        }
    }
}
