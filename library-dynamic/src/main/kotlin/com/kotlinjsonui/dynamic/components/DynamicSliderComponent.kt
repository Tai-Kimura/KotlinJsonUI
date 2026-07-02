package com.kotlinjsonui.dynamic.components

import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.*
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.UnappliedAttributes
import com.kotlinjsonui.dynamic.generated.SliderAttributes
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ResourceResolver
import com.kotlinjsonui.dynamic.rememberTypedAttrs
import androidx.compose.ui.platform.LocalContext
import kotlin.math.roundToInt

/**
 * Dynamic Slider Component Converter
 * Converts JSON to Slider composable at runtime.
 * Reference: slider_component.rb in kjui_tools.
 *
 * Supported JSON attributes:
 * - value/bind: Float or @{variable} for current value
 * - minimum/minimumValue/min: Float minimum value (default 0)
 * - maximum/maximumValue/max: Float maximum value (default 100)
 * - step: Float step size for discrete slider (steps = ((max-min)/step) - 1)
 * - onValueChange: @{handler} for change callback (updates binding + calls handler)
 * - enabled: Boolean or @{variable} to enable/disable
 * - thumbTintColor: Color for thumb
 * - minimumTrackTintColor: Color for active track
 * - maximumTrackTintColor: Color for inactive track
 * - Modifiers: testTag, margins, size, alpha, clickable, padding, weight
 *
 * Attribute access goes through the generated [SliderAttributes]
 * extraction (typed, alias-aware, L1-marker-aware) via the [TypedAttrs]
 * bridge: the `minimumValue`/`minValue` (`maximumValue`/`maxValue`) alias
 * spellings resolve into [SliderAttributes.minimum]/[SliderAttributes.maximum]
 * natively, and `onValueChanged` into [SliderAttributes.onValueChange].
 */
class DynamicSliderComponent {
    companion object {
        /** Slider-specific attributes this component applies (see UnappliedAttributes). */
        private val APPLIED: Set<String> = setOf(
            "value", "bind", "enabled",
            "minimum", "minValue", "maximum", "maxValue",
            "step", "onValueChange"
        )

        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            val context = LocalContext.current
            val a = rememberTypedAttrs(json) { m, canonicalOnly ->
                SliderAttributes.parse(m, canonicalOnly)
            }
            UnappliedAttributes.check(
                "Slider", json,
                declared = SliderAttributes.declaredAttributes,
                applied = UnappliedAttributes.COMMON_APPLIED + APPLIED,
                context = context
            )

            // Parse binding variable from value or bind
            val bindingVariable = TypedAttrs.binding(a.value)
                ?: (a.common.bind as? String)?.let { ModifierBuilder.extractBindingProperty(it) }

            // Parse min/max: the typed fields carry canonical minimum/maximum
            // plus the minimumValue/minValue (maximumValue/maxValue) alias
            // spellings (skipped for L1-normalized layouts); 'min'/'max' are
            // undeclared legacy spellings, always honored last.
            val minValue = TypedAttrs.float(a.minimum, data)
                ?: ResourceResolver.resolveFloat(json, "min", data)
                ?: 0f
            val maxValue = TypedAttrs.float(a.maximum, data)
                ?: ResourceResolver.resolveFloat(json, "max", data)
                ?: 100f

            // Resolve current value
            val currentValue = when {
                bindingVariable != null -> {
                    when (val boundValue = data[bindingVariable]) {
                        is Number -> boundValue.toFloat()
                        is String -> boundValue.toFloatOrNull() ?: minValue
                        else -> minValue
                    }
                }
                else -> TypedAttrs.float(a.value, data) ?: minValue
            }.coerceIn(minValue, maxValue)

            // State for slider value
            var sliderValue by remember(currentValue, bindingVariable, data) {
                mutableStateOf(currentValue)
            }

            // Update value when data changes
            LaunchedEffect(data, bindingVariable) {
                if (bindingVariable != null) {
                    sliderValue = when (val boundValue = data[bindingVariable]) {
                        is Number -> boundValue.toFloat().coerceIn(minValue, maxValue)
                        is String -> boundValue.toFloatOrNull()?.coerceIn(minValue, maxValue) ?: minValue
                        else -> minValue
                    }
                }
            }

            // Parse enabled state (supports @{binding})
            val isEnabled = TypedAttrs.boolean(a.common.enabled, data) ?: true

            // Calculate steps if specified
            val steps = a.step?.toFloat()?.let { step ->
                if (step > 0) {
                    val calculated = ((maxValue - minValue) / step).roundToInt() - 1
                    if (calculated > 0) calculated else 0
                } else 0
            } ?: 0

            // Handle value change: update binding + call onValueChange handler
            val viewId = a.common.id ?: "slider"
            val onValueChange: (Float) -> Unit = { newValue ->
                sliderValue = newValue

                // Update bound variable
                if (bindingVariable != null) {
                    @Suppress("UNCHECKED_CAST")
                    (data["updateData"] as? (Map<String, Any>) -> Unit)
                        ?.invoke(mapOf(bindingVariable to newValue.toDouble()))
                }

                // Call onValueChange handler if specified (the 'onValueChanged'
                // alias resolves through the typed parse, which already skips
                // it for L1-normalized layouts)
                val handler = TypedAttrs.raw(a.onValueChange) as? String
                if (handler != null && ModifierBuilder.isBinding(handler)) {
                    ModifierBuilder.resolveEventHandler(handler, data, viewId, newValue)
                }
            }

            // Parse colors (supports @{binding}) — 'thumbTintColor',
            // 'minimumTrackTintColor' and 'maximumTrackTintColor' are
            // undeclared legacy runtime extras on Slider
            val thumbColor = ColorParser.parseColorWithBinding(json, "thumbTintColor", data, context)
            val activeTrackColor = ColorParser.parseColorWithBinding(json, "minimumTrackTintColor", data, context)
            val inactiveTrackColor = ColorParser.parseColorWithBinding(json, "maximumTrackTintColor", data, context)

            val colors = if (thumbColor != null || activeTrackColor != null || inactiveTrackColor != null) {
                SliderDefaults.colors(
                    thumbColor = thumbColor ?: SliderDefaults.colors().thumbColor,
                    activeTrackColor = activeTrackColor ?: SliderDefaults.colors().activeTrackColor,
                    inactiveTrackColor = inactiveTrackColor ?: SliderDefaults.colors().inactiveTrackColor
                )
            } else {
                SliderDefaults.colors()
            }

            // Build modifier: testTag, margins, size, alpha, clickable, padding
            val modifier = ModifierBuilder.buildModifier(
                json, data, context = context, defaultFillMaxWidth = true
            )

            // Create the Slider
            Slider(
                value = sliderValue,
                onValueChange = onValueChange,
                valueRange = minValue..maxValue,
                steps = steps,
                modifier = modifier,
                enabled = isEnabled,
                colors = colors
            )
        }
    }
}
