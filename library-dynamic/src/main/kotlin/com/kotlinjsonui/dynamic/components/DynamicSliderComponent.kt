package com.kotlinjsonui.dynamic.components

import com.kotlinjsonui.core.jsonUITintOrNull
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.*
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.UnappliedAttributes
import com.kotlinjsonui.dynamic.generated.SliderAttributes
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.LayoutPath
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
 * - maximum/maximumValue/max: Float maximum value (default 1 — the declared default, 51-E)
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
        /** The value drawn: the bound value, else `value`, else the minimum; within the range. */
        internal fun valueOf(
            a: SliderAttributes,
            data: Map<String, Any>,
            bindingVariable: String?,
            minValue: Float,
            maxValue: Float
        ): Float = when {
            bindingVariable != null -> {
                when (val boundValue = data[bindingVariable]) {
                    is Number -> boundValue.toFloat()
                    is String -> boundValue.toFloatOrNull() ?: minValue
                    else -> minValue
                }
            }
            else -> TypedAttrs.float(a.value, data) ?: minValue
        }.coerceIn(minValue, maxValue)

        /**
         * The data key the value is bound to: a bound `value`. `bind` never reaches here: DynamicView folds it into the attribute
         * it stands for (BindFold, SSoT common.bind primaryValue).
         */
        internal fun bindingVariableOf(a: SliderAttributes): String? =
            TypedAttrs.binding(a.value)

        /** Slider-specific attributes this component applies (see UnappliedAttributes). */
        private val APPLIED: Set<String> = setOf(
            "value", "enabled",
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
            val bindingVariable = bindingVariableOf(a)

            // Parse min/max: the typed fields carry canonical minimum/maximum
            // plus the minimumValue/minValue (maximumValue/maxValue) alias
            // spellings (skipped for L1-normalized layouts); 'min'/'max' are
            // undeclared legacy spellings, always honored last.
            val minValue = TypedAttrs.float(a.minimum, data)
                ?: ResourceResolver.resolveFloat(json, "min", data)
                ?: 0f
            // The DECLARED default is 1, not 100 (51-E slider-range ruling:
            // an undeclared slider runs 0..1, the same unitless-track
            // convention Progress.progress carries; the codegen moved with
            // the ruling and this path lagged — the whole android Slider
            // parity family, control included, was this one constant).
            val maxValue = TypedAttrs.float(a.maximum, data)
                ?: ResourceResolver.resolveFloat(json, "max", data)
                ?: 1f

            // Resolve current value
            val currentValue = valueOf(a, data, bindingVariable, minValue, maxValue)

            // State for slider value
            // Keyed on the value this control declares — its binding's value, or the
            // static one — and not on `data`: every unrelated data change handed a new
            // map and reset what the user had chosen (ticket
            // kjui-dynamic-stateful-components-reset-on-unrelated-data). A bound value that changes
            // still resets it: the view model's word wins.
            var sliderValue by remember(currentValue, bindingVariable) {
                mutableStateOf(currentValue)
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
            val viewId = LayoutPath.viewId(json)
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

            // Parse colors (supports @{binding}). 'progressTintColor' and
            // 'trackTintColor' are the spellings attribute_definitions
            // declares — filled track and unfilled track respectively;
            // 'minimum/maximumTrackTintColor' are the undeclared UIKit legacy
            // and read BEHIND them, canonical-first (slider.trackColors in
            // shared/core/attribute_semantics.json). Plain tintColor is the
            // last-resort accent for the active track (UISlider heritage).
            // tintColor colours the thumb too, as kjui's codegen reads it
            // (thumbTintColor || tintColor). With no accent of its own, the
            // thumb and the filled track take the tint a container handed
            // down (LocalJsonUITint).
            val ownTint = ColorParser.parseColorWithBinding(json, "tintColor", data, context)
            val thumbColor = ColorParser.parseColorWithBinding(json, "thumbTintColor", data, context)
                ?: ownTint
                ?: jsonUITintOrNull()
            val activeTrackColor = ColorParser.parseColorWithBinding(json, "progressTintColor", data, context)
                ?: ColorParser.parseColorWithBinding(json, "minimumTrackTintColor", data, context)
                ?: ownTint
                ?: jsonUITintOrNull()
            val inactiveTrackColor = ColorParser.parseColorWithBinding(json, "trackTintColor", data, context)
                ?: ColorParser.parseColorWithBinding(json, "maximumTrackTintColor", data, context)

            val colors = if (thumbColor != null || activeTrackColor != null || inactiveTrackColor != null) {
                SliderDefaults.colors(
                    thumbColor = thumbColor ?: SliderDefaults.colors().thumbColor,
                    activeTrackColor = activeTrackColor ?: SliderDefaults.colors().activeTrackColor,
                    inactiveTrackColor = inactiveTrackColor ?: SliderDefaults.colors().inactiveTrackColor
                )
            } else {
                SliderDefaults.colors()
            }

            // Build modifier: the standard stages; the clickable stage is a
            // control's (no outer click — onClick is called when the value
            // change finishes), and `enabled` is the Slider's own, on this node.
            val modifier = ModifierBuilder.buildModifier(
                json, data, context = context, defaultFillMaxWidth = true,
                control = ModifierBuilder.ControlTap.ENABLED_ON_NODE
            )
            // The declared onClick, called when the value change finishes — the
            // Slider's own operation (ModifierBuilder.onClickFromOperation), as
            // kjui's codegen emits it (onValueChangeFinished).
            val onClick = ModifierBuilder.onClickFromOperation(json, data)

            // Create the Slider
            Slider(
                value = sliderValue,
                onValueChange = onValueChange,
                onValueChangeFinished = onClick,
                valueRange = minValue..maxValue,
                steps = steps,
                modifier = modifier,
                enabled = isEnabled,
                colors = colors
            )
        }
    }
}
