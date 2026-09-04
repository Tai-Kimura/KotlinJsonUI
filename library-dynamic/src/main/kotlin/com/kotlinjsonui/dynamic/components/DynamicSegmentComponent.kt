package com.kotlinjsonui.dynamic.components

import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.google.gson.JsonObject
import com.kotlinjsonui.components.Segment
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.UnappliedAttributes
import com.kotlinjsonui.dynamic.generated.SegmentAttributes
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.ResourceResolver
import com.kotlinjsonui.dynamic.rememberTypedAttrs
import android.content.Context
import androidx.compose.ui.platform.LocalContext

/**
 * Dynamic Segment Component Converter
 * Converts JSON to Segment (TabRow) composable at runtime.
 * Reference: segment_component.rb in kjui_tools.
 *
 * Supported JSON attributes:
 * - selectedIndex/bind: Integer or @{variable} for selected tab index
 * - items: Array of segment titles. Elements are resolved through string
 *   resources (ResourceResolver), matching the static codegen's process_text.
 *   Not a binding: SSoT declares Segment.items as type "array" only. The
 *   'segments' alias and the @{variable} form were both removed 2026-09-04.
 * - enabled: Boolean or @{variable} to enable/disable
 * - backgroundColor: Color for container (containerColor); defaults to
 *   Color.Transparent when unspecified (matching the static codegen)
 * - fontColor: Color for unselected text (contentColor); normalColor is a
 *   declared alias the extraction resolves
 * - selectedFontColor: Color for selected text (selectedContentColor), falling
 *   back to fontColor; selectedColor is its declared alias
 *   (contract: semantics.segmentLabelColors)
 * - tintColor/selectedSegmentTintColor: Color for the SELECTED segment's
 *   background (the indicator); indicatorColor is the explicit spelling
 * - onValueChange: @{handler} for selection change callback (receives index)
 * - Modifiers: testTag, margins, size, alpha, padding, weight
 *
 * Attribute access goes through the generated [SegmentAttributes]
 * extraction (typed, L1-marker-aware) via the [TypedAttrs] bridge; the
 * node itself is only passed wholesale to the shared ModifierBuilder
 * pipeline and to the raw items lookup.
 */
class DynamicSegmentComponent {
    companion object {
        /**
         * The data key this component is bound to.
         *
         * `bind` is the common two-way spelling for this component's primary
         * value, and it holds an `AttrValue<Any>` — so the old
         * `a.common.bind as? String` matched nothing and this fallback
         * returned null for every layout that used it. Kotlin 2.4 reports
         * that cast as one that can never succeed; before the bump the
         * branch was simply dead. CheckBox and Switch read the same row
         * correctly, and that is the shape restored here.
         */
        internal fun bindingVariableOf(a: SegmentAttributes): String? =
            TypedAttrs.binding(a.selectedIndex)
                ?: TypedAttrs.binding(a.common.bind)

        /** Segment-specific attributes this component applies (see UnappliedAttributes). */
        private val APPLIED: Set<String> = setOf(
            "selectedIndex", "bind", "items", "enabled",
            "fontColor", "selectedFontColor", "tintColor",
            "onValueChange"
        )

        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            val context = LocalContext.current
            val a = rememberTypedAttrs(json) { m, canonicalOnly ->
                SegmentAttributes.parse(m, canonicalOnly)
            }
            UnappliedAttributes.check(
                "Segment", json,
                declared = SegmentAttributes.declaredAttributes,
                applied = UnappliedAttributes.COMMON_APPLIED + APPLIED,
                context = context
            )

            // Parse binding variable for selected index
            val bindingVariable = bindingVariableOf(a)

            // Get selected index
            val currentIndex = when {
                bindingVariable != null -> {
                    when (val boundValue = data[bindingVariable]) {
                        is Number -> boundValue.toInt()
                        is String -> boundValue.toIntOrNull() ?: 0
                        else -> 0
                    }
                }
                else -> TypedAttrs.int(a.selectedIndex, data) ?: 0
            }

            var selectedIndex by remember(currentIndex, bindingVariable, data) {
                mutableStateOf(currentIndex)
            }

            // Update value when data changes
            LaunchedEffect(data, bindingVariable) {
                if (bindingVariable != null) {
                    selectedIndex = when (val boundValue = data[bindingVariable]) {
                        is Number -> boundValue.toInt()
                        is String -> boundValue.toIntOrNull() ?: 0
                        else -> 0
                    }
                }
            }

            // Parse segments (literal items resolve through string resources)
            val segments = parseSegments(json, data, context)

            // Parse enabled state (supports @{binding})
            val isEnabled = TypedAttrs.boolean(a.common.enabled, data) ?: true

            // Parse colors ('backgroundColor', 'selectedSegmentTintColor' and
            // 'indicatorColor' are undeclared legacy runtime extras on Segment)
            val containerColor = resolveContainerColor(json, data, context)
            // normalColor / selectedColor are declared aliases — the generated
            // extraction resolves them, so only the canonical names appear here.
            val normalColor = ColorParser.parseColorStringWithBinding(
                a.fontColor, data, context
            )
            val selectedColor = ColorParser.parseColorStringWithBinding(
                a.selectedFontColor, data, context
            )
                ?: ColorParser.parseColorStringWithBinding(
                    a.fontColor, data, context
                )
            // tintColor paints the SELECTED SEGMENT'S BACKGROUND on every
            // platform (ios selectedSegmentTintColor, web bg-*); android used to
            // feed it into the label colour instead, so the same declaration
            // coloured different things (contract: semantics.segmentLabelColors).
            // The indicator is this component's selected-state background.
            val indicatorColor = ColorParser.parseColorWithBinding(json, "indicatorColor", data, context)
                ?: ColorParser.parseColorStringWithBinding(a.tintColor, data, context)
                ?: ColorParser.parseColorWithBinding(json, "selectedSegmentTintColor", data, context)

            // Build modifier
            val modifier = ModifierBuilder.buildModifier(json, data, context = context)

            // Handle tab click with binding update + event handler
            val viewId = a.common.id ?: "segment"

            // Create the Segment using the existing component
            Segment(
                selectedTabIndex = selectedIndex,
                modifier = modifier,
                enabled = isEnabled,
                containerColor = containerColor,
                contentColor = normalColor,
                selectedContentColor = selectedColor,
                indicatorColor = indicatorColor
            ) {
                segments.forEachIndexed { index, segment ->
                    Tab(
                        selected = selectedIndex == index,
                        enabled = isEnabled,
                        onClick = {
                            selectedIndex = index

                            // Update bound variable
                            if (bindingVariable != null) {
                                @Suppress("UNCHECKED_CAST")
                                (data["updateData"] as? (Map<String, Any>) -> Unit)
                                    ?.invoke(mapOf(bindingVariable to index))
                            }

                            // Call onValueChange handler if specified
                            val handler = TypedAttrs.raw(a.onValueChange) as? String
                            if (handler != null && ModifierBuilder.isBinding(handler)) {
                                ModifierBuilder.resolveEventHandler(handler, data, viewId, index)
                            }
                        },
                        text = {
                            Text(
                                text = segment,
                                color = if (selectedIndex == index) {
                                    selectedColor ?: Color.Unspecified
                                } else {
                                    normalColor ?: Color.Unspecified
                                }
                            )
                        }
                    )
                }
            }
        }

        // ── Helpers ──

        /**
         * Container background: parsed `backgroundColor`, defaulting to
         * [Color.Transparent] when unspecified. The static codegen emits
         * `containerColor = Color.Transparent` for the no-background case
         * (segment_component.rb); passing null instead would fall through
         * to the Material3 TabRow surface default (an opaque band).
         */
        internal fun resolveContainerColor(
            json: JsonObject,
            data: Map<String, Any>,
            context: Context?
        ): Color = ColorParser.parseColorWithBinding(json, "backgroundColor", data, context)
            ?: Color.Transparent

        internal fun parseSegments(
            json: JsonObject,
            data: Map<String, Any>,
            context: Context?
        ): List<String> {
            // 'items' only, and only the declared array shape. Array elements
            // are stringified through gson — wider than the generated
            // List<Any?> coercion — so read raw (see TypedAttrs.rawKey).
            //
            // Two acceptances were removed (2026-09-04) to match the SSoT and
            // the other faces:
            //   * 'segments' was an undeclared alias read by this runtime
            //     alone. It is absent from attribute_definitions.json, no
            //     consumer layout used it (measured 0 across six faces), and
            //     the extract vocabulary and kjui's codegen dropped it first,
            //     so keeping it here meant a spelling that still rendered
            //     while nothing collected its strings.
            //   * a @{binding} string in place of the array. SSoT declares
            //     Segment.items as type "array" with no binding, the iOS
            //     dynamic runtime never accepted one, and the codegen stopped
            //     accepting it in 1.8.39 — this was the last face still taking
            //     a shape the contract does not describe.
            val segmentsElement = TypedAttrs.rawKey(json, "items")

            return when {
                segmentsElement == null -> emptyList()
                segmentsElement.isJsonArray -> {
                    // Literal titles resolve like the static codegen's
                    // process_text: string resource keys → localized values.
                    // Non-primitive elements (null, object) are dropped, which
                    // is the same set 1.8.39's codegen keeps.
                    segmentsElement.asJsonArray.mapNotNull { element ->
                        when {
                            element.isJsonPrimitive ->
                                ResourceResolver.resolveTextValue(element.asString, data, context)
                            else -> null
                        }
                    }
                }
                else -> emptyList()
            }
        }
    }
}
