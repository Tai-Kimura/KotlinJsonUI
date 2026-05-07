package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.DynamicView
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ResourceResolver

/**
 * Dynamic BlurView Component Converter
 * Converts JSON to Box with .blur() modifier composable at runtime.
 * Reference: blurview_component.rb in kjui_tools.
 *
 * Supported JSON attributes:
 * - blurRadius: Float blur radius in dp (default: 10)
 * - background/backgroundColor: Color with optional opacity
 * - opacity/alpha: Float opacity for background color
 * - cornerRadius: Float corner radius (applies clip)
 * - child/children: Child components to render inside blur
 * - Standard modifier attributes (padding, margins, alpha, onClick, etc.)
 *
 * Modifier order: testTag → margins → size → alpha → clickable → padding + blur
 */
class DynamicBlurViewComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            // Apply lifecycle effects first
            ModifierBuilder.ApplyLifecycleEffects(json, data)

            val context = LocalContext.current

            // Parse blur radius (default 10)
            val blurRadius = ResourceResolver.resolveFloat(json, "blurRadius", data, 10f) ?: 10f

            // Build modifier: testTag → margins → size → alpha → clickable → padding
            var modifier = ModifierBuilder.buildModifier(json, data, context = context)

            // Apply corner radius clip
            json.get("cornerRadius")?.asFloat?.let { radius ->
                modifier = modifier.clip(RoundedCornerShape(radius.dp))
            }

            // Apply background color with opacity. `effectStyle` (iOS Blur) falls
            // back to a tinted translucent overlay when no explicit color is set.
            val bgColor = ColorParser.parseColorWithBinding(json, "background", data, context)
                ?: ColorParser.parseColorWithBinding(json, "backgroundColor", data, context)
                ?: effectStyleColor(json.get("effectStyle")?.asString)
            if (bgColor != null) {
                val opacity = ResourceResolver.resolveFloat(json, "opacity", data)
                    ?: ResourceResolver.resolveFloat(json, "alpha", data)
                val finalColor = if (opacity != null) {
                    bgColor.copy(alpha = opacity.coerceIn(0f, 1f))
                } else {
                    bgColor
                }
                modifier = modifier.background(finalColor)
            }

            // Apply blur effect
            modifier = modifier.blur(blurRadius.dp)

            // Render blur box with children
            Box(modifier = modifier) {
                val children = DynamicContainerComponent.getChildren(json)
                children.forEach { child ->
                    DynamicView(child, data)
                }
            }
        }

        /**
         * Emulate iOS UIBlurEffect.Style names as translucent overlays on Compose.
         * Tool emits `effectStyle: "Light" | "Dark" | "ExtraLight"` from the shared
         * Blur attribute catalog.
         */
        private fun effectStyleColor(style: String?): Color? = when (style?.lowercase()) {
            "light" -> Color.White.copy(alpha = 0.4f)
            "dark" -> Color.Black.copy(alpha = 0.4f)
            "extralight" -> Color.White.copy(alpha = 0.6f)
            else -> null
        }
    }
}
