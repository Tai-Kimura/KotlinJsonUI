package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.UnappliedAttributes
import com.kotlinjsonui.dynamic.generated.IndicatorAttributes
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ResourceResolver
import com.kotlinjsonui.dynamic.rememberTypedAttrs

/**
 * Dynamic Indicator Component Converter
 * Converts JSON to activity/loading indicator composable at runtime.
 * Reference: indicator_component.rb in kjui_tools.
 *
 * Supported JSON attributes:
 * - indicatorStyle: "small" | "medium" | "large" | "linear" (declared; the
 *   legacy `style` / `size` spellings are folded by the layout normalizer —
 *   `style` into indicatorStyle, `size` into width / height — and not read here)
 * - animating: Boolean or @{variable} to control visibility (wraps in if-condition)
 * - color: String hex color for indicator
 * - trackColor: String hex color for track (linear only)
 * - strokeWidth: Float width for circular stroke
 * - Modifiers: testTag, size-by-style, margins, alpha, clickable, padding, alignment, weight
 *
 * Note: Always shows indeterminate progress indicator.
 */
class DynamicIndicatorComponent {
    companion object {
        /** Indicator-specific attributes this component applies (see UnappliedAttributes). */
        private val APPLIED: Set<String> = setOf(
            "color", "indicatorStyle"
        )

        /**
         * The declared `indicatorStyle`, or `medium`. It read `style` — the
         * style-file key — which a normalized layout no longer carries for an
         * Indicator (jui's normalizer folds it into indicatorStyle, jsonui-cli
         * ea985526), so every Indicator drew medium and circular.
         */
        internal fun styleOf(a: IndicatorAttributes): String =
            TypedAttrs.enumString(a.indicatorStyle) { it.json } ?: "medium"

        /** The spinner's size for a style that has one of its own (kjui STYLE_SIZES). */
        internal fun styleSizeDp(style: String): Int? = when (style) {
            "small" -> 16
            "large" -> 48
            else -> null
        }

        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap(),
            parentType: String? = null
        ) {
            val context = LocalContext.current
            val a = rememberTypedAttrs(json) { m, canonicalOnly ->
                IndicatorAttributes.parse(m, canonicalOnly)
            }
            UnappliedAttributes.check(
                "Indicator", json,
                declared = IndicatorAttributes.declaredAttributes,
                applied = UnappliedAttributes.COMMON_APPLIED + APPLIED,
                context = context
            )

            // Parse animating state (supports @{binding}; undeclared legacy
            // runtime extra)
            val isAnimating = ResourceResolver.resolveBoolean(json, "animating", data, default = true)

            // Only show indicator if animating is true
            if (!isAnimating) return

            val style = styleOf(a)

            // The standard stages in their standard order. The chain put the
            // margins after the style's size — inside it, where they pad —
            // read no declared width / height, and applied no shadow,
            // background, radius or border, all of which the SSoT declares on
            // every type (`common`).
            var modifier: Modifier = Modifier
            modifier = ModifierBuilder.applyTestTag(modifier, json)
            modifier = ModifierBuilder.applyMargins(modifier, json, data)
            // The declared width / height, as on every component; without
            // them, the style's size. The undeclared `size` is not read: the
            // normalizer folds it into width / height.
            if (a.common.width != null || a.common.height != null) {
                modifier = ModifierBuilder.applySize(modifier, json, data = data)
            } else {
                styleSizeDp(style)?.let { modifier = modifier.size(it.dp) }
            }
            // offset sits after size and before alpha, the same slot
            // buildModifier uses — outside background/shadow so the
            // decoration moves with the view, inside margins so siblings
            // do not. This chain does not call buildModifier, which is why
            // it needed the line of its own (51-C's warning, measured).
            modifier = ModifierBuilder.applyOffset(modifier, json, data)
            modifier = ModifierBuilder.applyAlpha(modifier, json, data)
            modifier = ModifierBuilder.applyShadow(modifier, json, data)
            modifier = ModifierBuilder.applyBackground(modifier, json, data, context)
            modifier = ModifierBuilder.applyClickable(modifier, json, data)
            modifier = ModifierBuilder.applyPadding(modifier, json, data)
            modifier = ModifierBuilder.applyAlignment(modifier, json, parentType, data)

            // Apply weight if in Row or Column
            val weight = ModifierBuilder.getWeight(json, data)
            // Weight is applied via parentType-aware buildModifier; handled by caller or RowScope/ColumnScope

            // Parse colors (supports @{binding})
            val color = ColorParser.parseColorStringWithBinding(
                TypedAttrs.rawString(a.color), data, context
            )
            // trackColor (undeclared legacy runtime extra)
            val trackColor = ColorParser.parseColorWithBinding(json, "trackColor", data, context)

            // Parse stroke width for circular indicator (undeclared legacy
            // runtime extra)
            val strokeWidth = ResourceResolver.resolveFloat(json, "strokeWidth", data)?.dp

            // Lifecycle effects
            ModifierBuilder.ApplyLifecycleEffects(json, data)

            // Create the appropriate indicator
            if (style == "linear") {
                LinearProgressIndicator(
                    modifier = modifier,
                    color = color ?: MaterialTheme.colorScheme.primary,
                    trackColor = trackColor ?: MaterialTheme.colorScheme.surfaceVariant
                )
            } else {
                // Circular indicator for all other styles (small, medium, large, default)
                if (strokeWidth != null) {
                    CircularProgressIndicator(
                        modifier = modifier,
                        color = color ?: MaterialTheme.colorScheme.primary,
                        trackColor = trackColor ?: MaterialTheme.colorScheme.surfaceVariant,
                        strokeWidth = strokeWidth
                    )
                } else {
                    CircularProgressIndicator(
                        modifier = modifier,
                        color = color ?: MaterialTheme.colorScheme.primary,
                        trackColor = trackColor ?: MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }
    }
}
