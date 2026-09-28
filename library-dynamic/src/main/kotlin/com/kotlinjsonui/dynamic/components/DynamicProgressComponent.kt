package com.kotlinjsonui.dynamic.components

import com.kotlinjsonui.core.jsonUITintOrNull
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.UnappliedAttributes
import com.kotlinjsonui.dynamic.generated.ProgressAttributes
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ResourceResolver
import com.kotlinjsonui.dynamic.rememberTypedAttrs

/**
 * Dynamic Progress Component Converter
 * Converts JSON to a LinearProgressIndicator composable at runtime.
 * Reference: progress_component.rb in kjui_tools.
 *
 * Attribute access goes through the generated [ProgressAttributes]
 * extraction (typed, alias-aware, L1-marker-aware) via the [TypedAttrs]
 * bridge; the node itself is only passed wholesale to the shared
 * ModifierBuilder pipeline. 'value' is an undeclared legacy runtime
 * extra on Progress.
 *
 * Supported JSON attributes:
 * - progress/value/bind: Float or @{variable} for progress value (0.0 to 1.0)
 * - progressTintColor: String hex color for progress color
 * - trackTintColor: String hex color for background track
 * - Modifiers: testTag, margins, size, alpha, clickable, padding, weight
 *
 * A Progress is a bar: determinate with a value, indeterminate without one.
 * Progress declares no shape, and `style` is the style file's name
 * (common.style), not a shape — a style named `circular` or `large` drew a
 * spinner here while every other path drew a bar (kjui / sjui codegen,
 * jsonui-cli 7007d2bb).
 */
class DynamicProgressComponent {
    companion object {
        /** Progress-specific attributes this component applies (see UnappliedAttributes). */
        private val APPLIED: Set<String> = setOf(
            "progress", "progressTintColor", "trackTintColor"
        )

        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap(),
            parentType: String? = null
        ) {
            val context = LocalContext.current
            val a = rememberTypedAttrs(json) { m, canonicalOnly ->
                ProgressAttributes.parse(m, canonicalOnly)
            }
            UnappliedAttributes.check(
                "Progress", json,
                declared = ProgressAttributes.declaredAttributes,
                applied = UnappliedAttributes.COMMON_APPLIED + APPLIED,
                context = context
            )

            // Determine if this is determinate (has progress/value/bind) or
            // indeterminate. `progress` is the canonical declared value
            // (0..1); 'value' is the undeclared legacy runtime extra
            // (shared/core/attribute_semantics.json → progressValue).
            val progressAttr = a.progress
            val hasValueAttr = TypedAttrs.undeclared(json, "value") != null
            val hasValue = progressAttr != null || hasValueAttr

            // Parse binding variable from bind, canonical progress, or legacy value
            val bindingVariable = progressBindingOf(json, a)

            // Resolve progress value (coerced to 0..1)
            val progressValue = progressValueOf(json, a, data, bindingVariable)

            // State for the progress value
            // Keyed on the value this control declares — its binding's value, or the
            // static one — and not on `data`: every unrelated data change handed a new
            // map and reset what the user had chosen (ticket
            // kjui-dynamic-stateful-components-reset-on-unrelated-data). A bound value that changes
            // still resets it: the view model's word wins.
            var progress by remember(progressValue, bindingVariable) {
                mutableStateOf(progressValue)
            }

            // Parse colors (supports @{binding})
            // progressTintColor is the specific spelling; plain tintColor is
            // the common accent (33 cross-effect: android rendered neither).
            val progressColor = ColorParser.parseColorStringWithBinding(
                TypedAttrs.rawString(a.progressTintColor), data, context
            ) ?: ColorParser.parseColorStringWithBinding(
                a.tintColor, data, context
            ) ?: jsonUITintOrNull() // none of its own: the tint a container handed down
            val trackColor = ColorParser.parseColorStringWithBinding(
                TypedAttrs.rawString(a.trackTintColor), data, context
            )

            // Build modifier: testTag -> margins -> size -> alpha -> clickable -> padding -> weight
            val modifier = ModifierBuilder.buildModifier(json, data, parentType, context)

            when {
                hasValue -> {
                    // Determinate: always LinearProgressIndicator with progress lambda
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = modifier,
                        color = progressColor ?: MaterialTheme.colorScheme.primary,
                        trackColor = trackColor ?: MaterialTheme.colorScheme.surfaceVariant
                    )
                }
                else -> {
                    // Indeterminate: a bar, as the determinate one is
                    LinearProgressIndicator(
                        modifier = modifier,
                        color = progressColor ?: MaterialTheme.colorScheme.primary,
                        trackColor = trackColor ?: MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }

        /** The data key: bindingVariableOf, else the legacy `value`'s binding. */
        internal fun progressBindingOf(json: JsonObject, a: ProgressAttributes): String? =
            bindingVariableOf(a)
                ?: extractBindingVariable(TypedAttrs.undeclared(json, "value")?.asString)

        /** The progress drawn (0..1): the bound value, else `progress`, else the legacy `value`, else 0. */
        internal fun progressValueOf(
            json: JsonObject,
            a: ProgressAttributes,
            data: Map<String, Any>,
            bindingVariable: String?
        ): Float = when {
            bindingVariable != null -> {
                when (val boundValue = data[bindingVariable]) {
                    is Number -> boundValue.toFloat()
                    is String -> boundValue.toFloatOrNull() ?: 0f
                    else -> 0f
                }
            }
            a.progress != null -> (a.progress.valueOrNull() ?: 0.0).toFloat()
            TypedAttrs.undeclared(json, "value") != null -> {
                // undeclared legacy runtime extra
                ResourceResolver.resolveFloat(json, "value", data, 0f) ?: 0f
            }
            else -> 0f
        }.coerceIn(0f, 1f)

        /**
         * The data key the progress is bound to: a bound `progress`. `bind` never reaches here: DynamicView folds it into the attribute
         * it stands for (BindFold, SSoT common.bind primaryValue). It
         * was read before `progress`, so a `bind` beside a bound `progress`
         * drew the other value.
         */
        internal fun bindingVariableOf(a: ProgressAttributes): String? =
            a.progress?.bindingExpressionOrNull()

        private fun extractBindingVariable(value: String?): String? {
            if (value == null) return null
            return ModifierBuilder.extractBindingProperty(value)
        }
    }
}
