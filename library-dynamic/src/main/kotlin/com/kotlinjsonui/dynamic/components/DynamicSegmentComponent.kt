package com.kotlinjsonui.dynamic.components

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.google.gson.JsonObject
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kotlinjsonui.components.Segment
import com.kotlinjsonui.components.SegmentTab
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.UnappliedAttributes
import com.kotlinjsonui.dynamic.generated.SegmentAttributes
import androidx.compose.ui.platform.testTag
import com.kotlinjsonui.dynamic.helpers.LayoutPath
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.ResourceResolver
import androidx.compose.ui.unit.TextUnit
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
 * - background / backgroundColor: the track (containerColor); iOS's when neither is written
 *   (it was Color.Transparent, under Material's TabRow).
 * - fontColor: Color for unselected text (contentColor); normalColor is a
 *   declared alias the extraction resolves
 * - selectedFontColor: Color for selected text (selectedContentColor), falling
 *   back to fontColor; selectedColor is its declared alias
 *   (contract: semantics.segmentLabelColors)
 * - tintColor/selectedSegmentTintColor: Color for the SELECTED segment's
 *   background (the indicator); indicatorColor is the explicit spelling
 * - onValueChange: @{handler} for selection change callback (receives index)
 * - valueChange: the selector spelling (a bare method name) — the callback
 *   where no onValueChange is declared
 * - Modifiers: testTag, margins, size, alpha, padding, weight
 *
 * Attribute access goes through the generated [SegmentAttributes]
 * extraction (typed, L1-marker-aware) via the [TypedAttrs] bridge; the
 * node itself is only passed wholesale to the shared ModifierBuilder
 * pipeline and to the raw items lookup.
 */
class DynamicSegmentComponent {
    companion object {
        /** The selected index: the bound value, else `selectedIndex`, else 0. */
        internal fun selectedIndexOf(a: SegmentAttributes, data: Map<String, Any>, bindingVariable: String?): Int =
            when {
                bindingVariable != null -> {
                    when (val boundValue = data[bindingVariable]) {
                        is Number -> boundValue.toInt()
                        is String -> boundValue.toIntOrNull() ?: 0
                        else -> 0
                    }
                }
                else -> TypedAttrs.int(a.selectedIndex, data) ?: 0
            }

        /**
         * The data key the selection is bound to: a bound `selectedIndex`. `bind` never reaches here: DynamicView folds it into the attribute
         * it stands for (BindFold, SSoT common.bind primaryValue).
         */
        internal fun bindingVariableOf(a: SegmentAttributes): String? =
            TypedAttrs.binding(a.selectedIndex)

        /** Segment-specific attributes this component applies (see UnappliedAttributes). */
        private val APPLIED: Set<String> = setOf(
            "selectedIndex", "items", "enabled",
            "fontColor", "selectedFontColor", "tintColor",
            "onValueChange", "valueChange"
        )

        /**
         * The data's name for a `valueChange` selector: camelCased as the code
         * generators name it (`seg_changed` → `segChanged`: kjui's
         * camelize_selector, sjui's to_camel_case). A binding is returned as
         * written: `valueChange` is declared `string`, and `@{h}` is its
         * spelling too — it was null ("onValueChange's spelling"), and with no
         * onValueChange `valueChange: "@{h}"` called nothing (jsonui-cli ticket
         * kjui-dynamic-segment-valuechange-binding-is-never-called; iOS
         * Dynamic's same fix: SwiftJsonUI 7ea1e4a). Null for none or a blank one.
         */
        /**
         * The testTag of a Segment's tab: `<id>_tab_<n>`, what the Android
         * driver's selectTab waits for (ActionExecutor executeSelectTab), the
         * id rjui gives each segment button and kjui codegen / sjui each
         * segment (jsonui-cli 11dfe22c). The tabs were untagged, so a
         * Segment could not be selected from a UI test (jsonui-cli ticket
         * jui-segment-tabs-carry-no-tab-ids-so-selecttab-cannot-reach-them).
         * None without an id, like the Segment's own tag; the Segment's
         * testTagsAsResourceId covers its tabs.
         */
        internal fun tabTestTag(id: String?, index: Int): String? =
            id?.takeIf { it.isNotEmpty() }?.let { "${it}_tab_$index" }

        internal fun valueChangeSelector(value: String?): String? {
            if (value == null || value.isBlank()) return null
            if (ModifierBuilder.isBinding(value)) return value
            val parts = value.split("_")
            return parts.first() + parts.drop(1).joinToString("") { part ->
                part.take(1).uppercase() + part.drop(1).lowercase()
            }
        }

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
            val currentIndex = selectedIndexOf(a, data, bindingVariable)

            // Keyed on the value this control declares — its binding's value, or the
            // static one — and not on `data`: every unrelated data change handed a new
            // map and reset what the user had chosen (ticket
            // kjui-dynamic-stateful-components-reset-on-unrelated-data). A bound value that changes
            // still resets it: the view model's word wins.
            var selectedIndex by remember(currentIndex, bindingVariable) {
                mutableStateOf(currentIndex)
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

            // Build modifier: the standard stages; the clickable stage is a
            // control's (onClick from each tab's selection), and the Segment
            // carries the tag while each Tab carries `enabled` (WRAPPER).
            // `background` is the track (containerColor), drawn inside the Segment's capsule, as the
            // codegen draws it; not a rectangle behind the capsule.
            val modifier = ModifierBuilder.buildModifier(
                json, data, context = context, control = ModifierBuilder.ControlTap.WRAPPER, handles = setOf("background")
            )

            // Handle tab click with binding update + event handler
            val viewId = LayoutPath.viewId(json)
            // The declared onClick, called from the tab's selection after it —
            // the Segment's own operation; no outer `.clickable` calls it
            // (ModifierBuilder.onClickFromOperation).
            val onClick = ModifierBuilder.onClickFromOperation(json, data)

            // Create the Segment using the existing component
            Segment(
                selectedTabIndex = selectedIndex,
                modifier = modifier,
                enabled = isEnabled,
                containerColor = containerColor,
                contentColor = normalColor,
                selectedContentColor = selectedColor,
                indicatorColor = indicatorColor,
                // No cornerRadius: the track and the fill are capsules whatever the layout declares,
                // as iOS draws them (the requester's ruling 2026-11-16).
            ) {
                segments.forEachIndexed { index, segment ->
                    SegmentTab(
                        // Each tab is a node of its own: it reads the stop
                        // too (stoppedItem). With an id it carries
                        // `<id>_tab_<n>` (tabTestTag).
                        modifier = ModifierBuilder.stoppedItem(json, data).let { m ->
                            tabTestTag(a.common.id, index)?.let { m.testTag(it) } ?: m
                        },
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

                            // Call onValueChange handler if specified — or,
                            // where none is declared, `valueChange`, the
                            // selector spelling (Segment's own attribute in the
                            // definitions, no platform named): kjui build, sjui
                            // build and rjui call it so; this runtime read it
                            // not at all, so a debug build did not call what the
                            // release build called (the orchestrator's ruling).
                            val handler = TypedAttrs.raw(a.onValueChange) as? String
                            if (handler != null && ModifierBuilder.isBinding(handler)) {
                                ModifierBuilder.resolveEventHandler(handler, data, viewId, index)
                            } else {
                                valueChangeSelector(a.valueChange)?.let {
                                    ModifierBuilder.resolveEventHandler(it, data, viewId, index)
                                }
                            }
                            onClick?.invoke()
                        },
                        text = {
                            // Resolved here, where the tab's text style (its weight) is the current one (P1).
                            val segmentFont = ResourceResolver.providerFont(
                                weight = androidx.compose.material3.LocalTextStyle.current.fontWeight
                            )
                            Text(
                                text = segment,
                                color = if (selectedIndex == index) {
                                    selectedColor ?: Color.Unspecified
                                } else {
                                    normalColor ?: Color.Unspecified
                                },
                                // Through the app's fontProvider (P1): a Segment declares no font attribute, so
                                // FontSpec is empty and a null slot draws the tab's style as before.
                                fontFamily = segmentFont.family,
                                fontWeight = segmentFont.weight,
                                fontSize = segmentFont.size ?: TextUnit.Unspecified,
                                fontStyle = segmentFont.style,
                                // One line, as iOS draws a segment's label (the ruling 2026-11-15).
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                }
            }
        }

        // ── Helpers ──

        /**
         * The track: the declared `background` (or the legacy `backgroundColor`); null when
         * neither is written, and the Segment draws iOS's track (the requester's ruling
         * 2026-11-15, jsonui-cli ticket jui-segment-android-draws-no-track). It was
         * Color.Transparent, under Material's TabRow.
         */
        internal fun resolveContainerColor(
            json: JsonObject,
            data: Map<String, Any>,
            context: Context?
        ): Color? = ColorParser.parseColorWithBinding(json, "background", data, context)
            ?: ColorParser.parseColorWithBinding(json, "backgroundColor", data, context)

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
