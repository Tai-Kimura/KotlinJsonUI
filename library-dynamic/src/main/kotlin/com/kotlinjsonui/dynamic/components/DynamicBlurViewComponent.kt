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
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.UnappliedAttributes
import com.kotlinjsonui.dynamic.generated.BlurAttributes
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ResourceResolver
import com.kotlinjsonui.dynamic.rememberTypedAttrs

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
        /** Blur-specific attributes this component applies (see UnappliedAttributes). */
        private val APPLIED: Set<String> = setOf(
            "effectStyle"
        )

        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            // Apply lifecycle effects first
            ModifierBuilder.ApplyLifecycleEffects(json, data)

            val context = LocalContext.current
            val a = rememberTypedAttrs(json) { m, canonicalOnly ->
                BlurAttributes.parse(m, canonicalOnly)
            }
            UnappliedAttributes.check(
                "Blur", json,
                declared = BlurAttributes.declaredAttributes,
                applied = UnappliedAttributes.COMMON_APPLIED + APPLIED,
                context = context
            )

            // Parse blur radius (default 10; undeclared legacy runtime extra)
            val blurRadius = ResourceResolver.resolveFloat(json, "blurRadius", data, 10f) ?: 10f

            // Build modifier: testTag → margins → size → alpha → clickable → padding
            var modifier = ModifierBuilder.buildModifier(json, data, context = context)

            // Apply corner radius clip
            TypedAttrs.float(a.common.cornerRadius, data)?.let { radius ->
                modifier = modifier.clip(RoundedCornerShape(radius.dp))
            }

            // Apply background color with opacity. `effectStyle` (iOS Blur) falls
            // back to a tinted translucent overlay when no explicit color is set.
            // ('backgroundColor' is an undeclared legacy runtime extra)
            val bgColor = ColorParser.parseColorStringWithBinding(
                TypedAttrs.rawString(a.common.background), data, context
            )
                ?: ColorParser.parseColorWithBinding(json, "backgroundColor", data, context)
                ?: effectStyleColor(TypedAttrs.enumString(a.effectStyle) { it.json })
            if (bgColor != null) {
                // opacity and alpha are both declared rows with identical
                // semantics; legacy read order (opacity first) kept.
                val opacity = TypedAttrs.float(a.common.opacity, data)
                    ?: TypedAttrs.float(a.common.alpha, data)
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
