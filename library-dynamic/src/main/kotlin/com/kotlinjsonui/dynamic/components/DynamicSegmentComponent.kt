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
import com.kotlinjsonui.dynamic.rememberTypedAttrs
import androidx.compose.ui.platform.LocalContext

/**
 * Dynamic Segment Component Converter
 * Converts JSON to Segment (TabRow) composable at runtime.
 * Reference: segment_component.rb in kjui_tools.
 *
 * Supported JSON attributes:
 * - selectedIndex/bind: Integer or @{variable} for selected tab index
 * - items/segments: Array of segment titles or @{variable} for dynamic segments
 * - enabled: Boolean or @{variable} to enable/disable
 * - backgroundColor: Color for container (containerColor)
 * - normalColor: Color for unselected text (contentColor)
 * - selectedColor/tintColor/selectedSegmentTintColor: Color for selected text (selectedContentColor)
 * - indicatorColor: Color for tab indicator
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
        /** Segment-specific attributes this component applies (see UnappliedAttributes). */
        private val APPLIED: Set<String> = setOf(
            "selectedIndex", "bind", "items", "enabled",
            "normalColor", "selectedColor", "tintColor",
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
            val bindingVariable = TypedAttrs.binding(a.selectedIndex)
                ?: (a.common.bind as? String)?.let { ModifierBuilder.extractBindingProperty(it) }

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

            // Parse segments
            val segments = parseSegments(json, data)

            // Parse enabled state (supports @{binding})
            val isEnabled = TypedAttrs.boolean(a.common.enabled, data) ?: true

            // Parse colors ('backgroundColor', 'selectedSegmentTintColor' and
            // 'indicatorColor' are undeclared legacy runtime extras on Segment)
            val backgroundColor = ColorParser.parseColorWithBinding(json, "backgroundColor", data, context)
            val normalColor = ColorParser.parseColorStringWithBinding(
                TypedAttrs.rawString(a.normalColor), data, context
            )
            val selectedColor = ColorParser.parseColorStringWithBinding(
                TypedAttrs.rawString(a.selectedColor), data, context
            )
                ?: ColorParser.parseColorStringWithBinding(a.tintColor, data, context)
                ?: ColorParser.parseColorWithBinding(json, "selectedSegmentTintColor", data, context)
            val indicatorColor = ColorParser.parseColorWithBinding(json, "indicatorColor", data, context)

            // Build modifier
            val modifier = ModifierBuilder.buildModifier(json, data, context = context)

            // Handle tab click with binding update + event handler
            val viewId = a.common.id ?: "segment"

            // Create the Segment using the existing component
            Segment(
                selectedTabIndex = selectedIndex,
                modifier = modifier,
                enabled = isEnabled,
                containerColor = backgroundColor,
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

        private fun parseSegments(json: JsonObject, data: Map<String, Any>): List<String> {
            // 'items' accepts a @{binding} string in addition to the declared
            // array shape, and array elements are stringified through gson —
            // wider than the generated List<Any?> coercion, so read raw (see
            // TypedAttrs.rawKey); 'segments' is an undeclared legacy runtime
            // extra spelling.
            val segmentsElement = TypedAttrs.rawKey(json, "items")
                ?: TypedAttrs.undeclared(json, "segments")

            return when {
                segmentsElement == null -> emptyList()
                segmentsElement.isJsonArray -> {
                    segmentsElement.asJsonArray.mapNotNull { element ->
                        when {
                            element.isJsonPrimitive -> element.asString
                            else -> null
                        }
                    }
                }
                segmentsElement.isJsonPrimitive && ModifierBuilder.isBinding(segmentsElement.asString) -> {
                    val variable = ModifierBuilder.extractBindingProperty(segmentsElement.asString)
                    if (variable != null) {
                        when (val segments = data[variable]) {
                            is List<*> -> segments.mapNotNull { it?.toString() }
                            is Array<*> -> segments.mapNotNull { it?.toString() }
                            else -> emptyList()
                        }
                    } else {
                        emptyList()
                    }
                }
                else -> emptyList()
            }
        }
    }
}
