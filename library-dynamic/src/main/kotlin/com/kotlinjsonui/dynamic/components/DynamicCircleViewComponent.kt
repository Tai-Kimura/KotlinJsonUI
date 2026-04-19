package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.DynamicView
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.dashedBorder
import com.kotlinjsonui.dynamic.helpers.dottedBorder

/**
 * Dynamic CircleView Component Converter
 * Converts JSON to CircleView composable at runtime.
 *
 * Supported JSON attributes:
 * - size: Number circle diameter in dp (default 20)
 * - color/background: String hex color for fill
 * - borderWidth: Float border width
 * - borderColor: String hex color for border
 * - borderStyle: "solid" | "dashed" | "dotted" (default "solid")
 * - padding/paddings: Number or Array for padding
 * - margins: Array or individual margin properties
 * - alpha/opacity: Float opacity value (0-1), supports @{binding}
 * - onClick/onclick: String event handler name
 * - child/children: Child components rendered inside the circle
 */
class DynamicCircleViewComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            val context = LocalContext.current

            // Parse size (default 20dp)
            val size = json.get("size")?.asFloat ?: 20f

            // Build modifier in the specified order:
            // testTag → margins → size → clip(CircleShape) → border → background(color, CircleShape) → alpha → clickable → padding
            var modifier: Modifier = Modifier

            // 1. testTag
            modifier = ModifierBuilder.applyTestTag(modifier, json)

            // 2. margins
            modifier = ModifierBuilder.applyMargins(modifier, json, data)

            // 3. size
            modifier = modifier.size(size.dp)

            // 4. clip(CircleShape)
            modifier = modifier.clip(CircleShape)

            // 5. border(CircleShape)
            val borderColor = ColorParser.parseColorWithBinding(json, "borderColor", data, context)
            val borderWidth = json.get("borderWidth")?.asFloat
            if (borderColor != null && borderWidth != null && borderWidth > 0) {
                val borderStyle = json.get("borderStyle")?.asString ?: "solid"
                modifier = when (borderStyle) {
                    "dashed" -> modifier.dashedBorder(borderWidth.dp, borderColor, CircleShape)
                    "dotted" -> modifier.dottedBorder(borderWidth.dp, borderColor, CircleShape)
                    else -> modifier.border(borderWidth.dp, borderColor, CircleShape)
                }
            }

            // 6. background color with CircleShape
            val fillColor = ColorParser.parseColorWithBinding(json, "color", data, context)
                ?: ColorParser.parseColorWithBinding(json, "background", data, context)
            if (fillColor != null) {
                modifier = modifier.background(fillColor, CircleShape)
            }

            // 7. alpha
            modifier = ModifierBuilder.applyAlpha(modifier, json, data)

            // 8. clickable
            modifier = ModifierBuilder.applyClickable(modifier, json, data)

            // 9. padding
            modifier = ModifierBuilder.applyPadding(modifier, json)

            // Lifecycle effects
            if (ModifierBuilder.hasLifecycleEvents(json)) {
                ModifierBuilder.ApplyLifecycleEffects(json, data)
            }

            // Render Box with children
            val children = DynamicContainerComponent.getChildren(json)
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ) {
                children.forEach { child ->
                    DynamicView(child, data)
                }
            }
        }
    }
}
