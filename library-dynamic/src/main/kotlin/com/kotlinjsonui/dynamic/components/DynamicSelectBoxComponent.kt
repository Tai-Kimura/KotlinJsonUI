package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.gson.JsonObject
import com.kotlinjsonui.components.SelectBox
import com.kotlinjsonui.components.SelectBoxCaret
import com.kotlinjsonui.components.DateSelectBox
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.UnappliedAttributes
import com.kotlinjsonui.dynamic.generated.SelectBoxAttributes
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.ResourceResolver
import com.kotlinjsonui.dynamic.rememberTypedAttrs
import androidx.compose.ui.platform.LocalContext

/**
 * Dynamic SelectBox Component Converter
 * Converts JSON to SelectBox/DateSelectBox composable at runtime.
 * Reference: selectbox_component.rb in kjui_tools.
 *
 * Supported JSON attributes:
 * - selectedItem/selectedDate/bind: @{variable} for selected value binding
 * - selectItemType: "Date" for date picker mode
 * - items/options: Array or @{variable} for dropdown options
 * - datePickerMode: "date" | "time" | "dateAndTime"
 * - datePickerStyle: "wheels" | "inline" | "compact" | "graphical"
 * - dateFormat/dateStringFormat: Date format pattern
 * - minuteInterval: Integer interval for time picker
 * - minimumDate/maximumDate: Date range constraints
 * - hint/placeholder: Placeholder text
 * - enabled/disabled: Boolean state
 * - background/borderColor/fontColor/hintColor: Colors
 * - cornerRadius: Float corner radius
 * - fontSize: Float font size
 * - font: Font weight string (bold, semibold, medium, light, thin)
 * - cancelButtonBackgroundColor/cancelButtonTextColor: Cancel button colors
 * - caretAttributes: {src, width, height, tintColor, background, rightMargin}
 *   — the closed select draws its own caret when the object is present;
 *   absent keeps the native arrow (SSoT SelectBox.caretAttributes, cross-platform
 *   since jsonui-cli 1.8.101)
 * - onValueChange: @{handler} for change callback
 * - Modifiers: testTag, margins, size, alpha, clickable (alignment/weight are
 *   applied by the parent container). background/borderColor/cornerRadius are
 *   drawn by the composable itself and paddings/padding map to its
 *   contentPadding parameter, so none of them re-apply on the modifier
 *   (matching selectbox_component.rb — otherwise the border is drawn twice)
 *
 * Attribute access goes through the generated [SelectBoxAttributes]
 * extraction (typed, L1-marker-aware) via the [TypedAttrs] bridge; the
 * node itself is only passed wholesale to the shared ModifierBuilder
 * pipeline and to the raw items/options lookup.
 */
/**
 * The raw keys of `caretAttributes`, or null when the object is absent
 * (the native arrow stays, and the picture does not move).
 *
 * Pure — the drawable and the colours are resolved by the composable,
 * because `painterResource` needs a composition and hex parsing needs
 * Android. Numbers arrive as Double through the attr map and are dp
 * integers on the library surface (`cornerRadius` is the precedent).
 * A key of the wrong type is read as absent, not as zero.
 */
internal data class SelectBoxCaretSpec(
    val src: String?,
    val width: Int?,
    val height: Int?,
    val tintColor: String?,
    val background: String?,
    /** Absent: 0 — flush against the trailing edge, the UIKit contract. */
    val rightMargin: Int
)

class DynamicSelectBoxComponent {
    companion object {
        /** SelectBox-specific attributes this component applies (see UnappliedAttributes). */
        /**
         * A key of the `labelAttributes` object.
         *
         * On a SelectBox the collapsed text IS the label, so these keys win
         * over the component-level ones — the same precedence the kjui codegen
         * (`selectbox_component.rb:222`) and the web converter use. The row was
         * parsed and never read (34: `SelectBox/labelAttributes`
         * pixel-identical to its control).
         *
         * `fontColor` / `fontSize` are the keys the library surface carries;
         * `font` and `textAlign` have no SelectBox parameter on either path
         * yet, so they stay unread here exactly as they do in the codegen.
         */
        internal fun labelAttr(a: SelectBoxAttributes, key: String): Any? =
            a.labelAttributes?.get(key)

        /**
         * The data key this box reads and writes: `selectedItem` >
         * `selectedValue` > `selectedIndex` > `bind` — the codegen's order
         * (selectbox_component.rb).
         *
         * `selectedValue` is declared two-way and was NOT a candidate, so a
         * bound one fell through to the literal seed and the closed box drew
         * `@{boundSelectedValue}` verbatim.
         *
         * `selectedIndex` was not a candidate either: a bound index was read
         * for the seed but never written back, and its handler got the item
         * String where the codegen and SwiftJsonUI pass the Int index.
         */
        internal fun bindingVariableOf(a: SelectBoxAttributes): String? =
            TypedAttrs.binding(a.selectedItem)
                ?: TypedAttrs.binding(a.selectedValue)
                ?: TypedAttrs.binding(a.selectedIndex)
                ?: TypedAttrs.binding(a.common.bind)

        /** The bound key is `selectedIndex` — an Int slot — because no item-valued binding outranks it. */
        internal fun isIndexBinding(a: SelectBoxAttributes): Boolean =
            TypedAttrs.binding(a.selectedItem) == null &&
                TypedAttrs.binding(a.selectedValue) == null &&
                TypedAttrs.binding(a.selectedIndex) != null

        /**
         * The item the bound value names, for the closed box: the data String
         * of an item binding, `options[index]` of an index binding (never the
         * number itself), null without a binding.
         */
        internal fun boundSelection(
            a: SelectBoxAttributes,
            data: Map<String, Any>,
            options: List<String>
        ): String? {
            val key = bindingVariableOf(a) ?: return null
            return if (isIndexBinding(a)) {
                (data[key] as? Number)?.toInt()?.let { options.getOrNull(it) }
            } else {
                data[key]?.toString()
            }
        }

        /**
         * What a pick writes back AND hands the onValueChange handler — one
         * value, the new value of the selection binding: the Int index of
         * the picked item for an index binding (what the kjui codegen and
         * SwiftJsonUI's `.onChange(of:)` pass), the item String otherwise.
         */
        internal fun selectionPayload(a: SelectBoxAttributes, options: List<String>, newValue: String): Any =
            if (isIndexBinding(a)) options.indexOf(newValue) else newValue

        /**
         * Same row, the date-picker variant's precedence: `selectedDate`
         * first, then `selectedItem`, then the common `bind`.
         */
        internal fun dateBindingVariableOf(a: SelectBoxAttributes): String? =
            TypedAttrs.binding(a.selectedDate)
                ?: TypedAttrs.binding(a.selectedItem)
                ?: TypedAttrs.binding(a.common.bind)

        /**
         * What the closed box shows: the bound value if there is one, else the
         * literal seed (`selectedItem` / `selectedValue`, or the item
         * `selectedIndex` names).
         *
         * STATIC only for the seed — a bound `selectedValue` is the channel
         * above, and printing its expression is the bug this replaced.
         */
        internal fun initialSelection(a: SelectBoxAttributes, data: Map<String, Any>): String {
            val bindingVariable = bindingVariableOf(a)
            // An index binding's data value is a number; the seed below
            // resolves it through TypedAttrs.int to the item it names.
            val current = if (bindingVariable != null && !isIndexBinding(a)) {
                data[bindingVariable]?.toString() ?: ""
            } else ""
            // `selectedItem` is declared `["string","binding"]`, so its STATIC
            // face is a seed exactly like `selectedValue`'s — but only the
            // bound face was ever read (via bindingVariableOf), so a literal
            // `selectedItem: "Two"` selected nothing and the closed box drew
            // empty (`SelectBox/selectedItem__static` inert on android, active
            // on ios). The kjui codegen already seeds from all three spellings
            // (selectbox_component.rb:54-60), so this also re-syncs the paths.
            val seed = TypedAttrs.static(a.selectedItem)
                ?: TypedAttrs.static(a.selectedValue)
                // `raw(...) as? Number` cannot match the BOUND face — a binding
                // arrives as the `"@{expr}"` String, so the cast silently
                // dropped every bound `selectedIndex`. There is no
                // `selectedIndex__binding` fixture, so nothing could have
                // measured it; found by the read-discipline scan (51-G #2).
                ?: TypedAttrs.int(a.selectedIndex, data)?.let { idx ->
                    TypedAttrs.static(a.items)?.getOrNull(idx)?.let { item ->
                        when (item) {
                            is Map<*, *> -> (item["value"] ?: item["label"])?.toString()
                            else -> item.toString()
                        }
                    }
                }
            return current.ifEmpty { seed ?: "" }
        }

        /** The same cascade as `hintAttributes`, via the shared helpers. */
        internal fun labelString(a: SelectBoxAttributes, key: String): String? =
            ResourceResolver.nestedString(a.labelAttributes, key)

        internal fun labelNumber(a: SelectBoxAttributes, key: String): Double? =
            ResourceResolver.nestedNumber(a.labelAttributes, key)

        internal fun caretSpecOf(a: SelectBoxAttributes): SelectBoxCaretSpec? {
            val bag = a.caretAttributes ?: return null
            return SelectBoxCaretSpec(
                src = ResourceResolver.nestedString(bag, "src")?.takeIf { it.isNotBlank() },
                width = ResourceResolver.nestedNumber(bag, "width")?.toInt(),
                height = ResourceResolver.nestedNumber(bag, "height")?.toInt(),
                tintColor = ResourceResolver.nestedString(bag, "tintColor"),
                background = ResourceResolver.nestedString(bag, "background"),
                rightMargin = ResourceResolver.nestedNumber(bag, "rightMargin")?.toInt() ?: 0
            )
        }

        private val APPLIED: Set<String> = setOf(
            "selectItemType", "selectedItem", "selectedValue", "selectedIndex",
            "selectedDate", "bind",
            "items", "enabled", "prompt", "hint", "placeholder",
            "fontColor", "hintColor", "fontSize", "font", "labelAttributes",
            "caretAttributes",
            "datePickerMode", "datePickerStyle", "dateStringFormat",
            "minuteInterval", "minimumDate", "maximumDate",
            "onValueChange", "onValueChanged"
        )

        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            val a = rememberTypedAttrs(json) { m, canonicalOnly ->
                SelectBoxAttributes.parse(m, canonicalOnly)
            }
            UnappliedAttributes.check(
                "SelectBox", json,
                declared = SelectBoxAttributes.declaredAttributes,
                applied = UnappliedAttributes.COMMON_APPLIED + APPLIED,
                context = LocalContext.current
            )

            val isDatePicker =
                TypedAttrs.enumString(a.selectItemType) { it.json } == "Date"

            if (isDatePicker) {
                createDatePicker(json, a, data)
            } else {
                createDropdown(json, a, data)
            }
        }

        // ── Dropdown SelectBox ──

        @Composable
        private fun createDropdown(
            json: JsonObject,
            a: SelectBoxAttributes,
            data: Map<String, Any>
        ) {
            val context = LocalContext.current

            // Parse options (before the selection: an index binding names one)
            val options = parseOptions(json, data)

            val bindingVariable = bindingVariableOf(a)
            val currentValue = boundSelection(a, data, options) ?: ""

            var selectedValue by remember(currentValue, bindingVariable, data) {
                mutableStateOf(currentValue.ifEmpty { initialSelection(a, data) })
            }

            LaunchedEffect(data, bindingVariable) {
                if (bindingVariable != null) {
                    selectedValue = boundSelection(a, data, options) ?: ""
                }
            }

            // Parse enabled state ('disabled' is an undeclared legacy runtime extra)
            val isEnabled = when {
                TypedAttrs.undeclared(json, "disabled")?.asBoolean == true -> false
                else -> TypedAttrs.boolean(a.common.enabled, data) ?: true
            }

            // Parse placeholder — spec canonical `prompt` (primary) plus the
            // `hint` / `placeholder` aliases. Routed through ResourceResolver
            // so a snake_case key like "select_box_prompt" resolves to the
            // Android string resource at runtime (matches codegen behavior).
            val placeholder = when {
                a.prompt != null -> ResourceResolver.resolveTextValue(a.prompt, data, context)
                a.hint != null -> ResourceResolver.resolveTextValue(a.hint, data, context)
                a.placeholder != null -> ResourceResolver.resolveTextValue(a.placeholder, data, context)
                else -> null
            }

            // Parse colors
            val backgroundColor = ColorParser.parseColorStringWithBinding(
                TypedAttrs.rawString(a.common.background), data, context
            ) ?: Color.White
            val borderColor = ColorParser.parseColorStringWithBinding(
                TypedAttrs.rawString(a.common.borderColor), data, context
            ) ?: Color(0xFFCCCCCC)
            val textColor = ColorParser.parseColorStringWithBinding(
                labelString(a, "fontColor") ?: a.fontColor, data, context
            ) ?: Color.Black
            val hintColor = ColorParser.parseColorStringWithBinding(
                TypedAttrs.rawString(a.hintColor), data, context
            ) ?: Color(0xFF999999)

            val cornerRadius = TypedAttrs.int(a.common.cornerRadius, data) ?: 8

            // Font styling
            val fontSize = labelNumber(a, "fontSize")?.toFloat()
                ?: a.fontSize?.toFloat()
            val fontWeight = parseFontWeight(a.font)

            // 'cancelButtonBackgroundColor'/'cancelButtonTextColor' are
            // undeclared legacy runtime extras on SelectBox
            val cancelButtonBackgroundColor = ColorParser.parseColorWithBinding(
                json, "cancelButtonBackgroundColor", data, context
            )
            val cancelButtonTextColor = ColorParser.parseColorWithBinding(
                json, "cancelButtonTextColor", data, context
            )

            // Handle value change
            val viewId = a.common.id ?: "selectbox"
            val onValueChange: (String) -> Unit = { newValue ->
                selectedValue = newValue

                // The writeback and the handler carry the SAME value: the Int
                // index for an index binding, the item String otherwise.
                // Handing the item String to an `(Int) -> Unit` handler was
                // a ClassCastException that resolveEventHandler's catch
                // turned into silence.
                val payload = selectionPayload(a, options, newValue)

                // Update bound variable
                if (bindingVariable != null) {
                    @Suppress("UNCHECKED_CAST")
                    (data["updateData"] as? (Map<String, Any>) -> Unit)
                        ?.invoke(mapOf(bindingVariable to payload))
                }

                // Call onValueChange handler if specified
                // (onValueChanged is the declared alias spelling)
                val handler = TypedAttrs.raw(a.onValueChange) as? String
                    ?: TypedAttrs.raw(a.onValueChanged) as? String
                if (handler != null && ModifierBuilder.isBinding(handler)) {
                    ModifierBuilder.resolveEventHandler(handler, data, viewId, payload)
                }
            }

            // Build modifier (default fill width). SelectBox draws its own
            // background/border/corner shape from the composable parameters,
            // so the modifier chain must not re-apply them.
            val modifier = buildSelfDrawnModifier(json, data)

            // paddings/padding become contentPadding (never a .padding()
            // modifier, which would inset the self-drawn border instead of
            // the content). Fallback mirrors the composable default.
            val contentPadding = ModifierBuilder.parseContentPadding(json, data)

            // caretAttributes → the library's SelectBoxCaret. `src` resolves as
            // a drawable name (Image's path: resolveDrawable, 0 = not found →
            // the default glyph, reported by UnresolvedResource in debug
            // builds); colours go through the same parser as the field's.
            // The Date branch never reads this — a date picker has no
            // closed-state caret (SSoT).
            val caret = caretSpecOf(a)?.let { spec ->
                val resId = spec.src?.let { ResourceResolver.resolveDrawable(it, data, context) } ?: 0
                SelectBoxCaret(
                    painter = if (resId != 0) painterResource(id = resId) else null,
                    width = spec.width,
                    height = spec.height,
                    tintColor = ColorParser.parseColorStringWithBinding(spec.tintColor, data, context),
                    background = ColorParser.parseColorStringWithBinding(spec.background, data, context),
                    rightMargin = spec.rightMargin
                )
            }

            SelectBox(
                value = selectedValue,
                onValueChange = onValueChange,
                options = options,
                modifier = modifier,
                placeholder = placeholder,
                enabled = isEnabled,
                backgroundColor = backgroundColor,
                borderColor = borderColor,
                textColor = textColor,
                hintColor = hintColor,
                cornerRadius = cornerRadius,
                // fontSize/fontWeight were parsed but never forwarded — the
                // library SelectBox has carried both params all along (33
                // cross-effect: android rendered default size/weight).
                fontSize = fontSize?.toInt() ?: 16,
                fontWeight = fontWeight,
                contentPadding = contentPadding ?: PaddingValues(horizontal = 16.dp),
                // Undeclared cancel colors fall to the SAME Configuration
                // defaults the codegen face reaches by omitting the params
                // (generated code emits these only when declared). The old
                // `?: backgroundColor` / `?: textColor` fallbacks silently
                // restyled the cancel row with the FIELD colors, a
                // dynamic-only divergence from the codegen canon.
                cancelButtonBackgroundColor = cancelButtonBackgroundColor
                    ?: Configuration.SelectBox.defaultSheetBackgroundColor,
                cancelButtonTextColor = cancelButtonTextColor
                    ?: Configuration.SelectBox.SheetButton.defaultCancelButtonTextColor,
                caret = caret
            )
        }

        // ── Date Picker SelectBox ──

        @Composable
        private fun createDatePicker(
            json: JsonObject,
            a: SelectBoxAttributes,
            data: Map<String, Any>
        ) {
            val context = LocalContext.current

            // Parse binding variable: selectedDate > selectedItem > bind
            val bindingVariable = dateBindingVariableOf(a)

            // Get current value
            val currentValue = if (bindingVariable != null) {
                data[bindingVariable]?.toString() ?: ""
            } else ""

            var selectedDate by remember(currentValue, bindingVariable, data) {
                mutableStateOf(currentValue)
            }

            LaunchedEffect(data, bindingVariable) {
                if (bindingVariable != null) {
                    selectedDate = data[bindingVariable]?.toString() ?: ""
                }
            }

            // Parse date picker attributes ('dateFormat' is an undeclared
            // legacy spelling of the declared 'dateStringFormat')
            val datePickerMode = TypedAttrs.enumString(a.datePickerMode) { it.json } ?: "date"
            val datePickerStyle = TypedAttrs.enumString(a.datePickerStyle) { it.json } ?: "compact"
            val dateFormat = TypedAttrs.undeclared(json, "dateFormat")?.asString
                ?: a.dateStringFormat
                ?: "yyyy-MM-dd"
            val minuteInterval = a.minuteInterval?.toInt() ?: 1
            // Both rows are declared binding-supported and were handed to the
            // picker as the LAYOUT spelling, so a bound bound arrived as
            // `@{expr}` and constrained nothing. Found by counting the raw
            // reads rather than by stopping at the one that was reported.
            val minimumDate = TypedAttrs.string(a.minimumDate, data)
            val maximumDate = TypedAttrs.string(a.maximumDate, data)

            // Parse enabled state ('disabled' is an undeclared legacy runtime extra)
            val isEnabled = when {
                TypedAttrs.undeclared(json, "disabled")?.asBoolean == true -> false
                else -> TypedAttrs.boolean(a.common.enabled, data) ?: true
            }

            // Parse placeholder — spec canonical `prompt` (primary) plus the
            // `hint` / `placeholder` aliases. Routed through ResourceResolver
            // so a snake_case key like "select_box_prompt" resolves to the
            // Android string resource at runtime (matches codegen behavior).
            val placeholder = when {
                a.prompt != null -> ResourceResolver.resolveTextValue(a.prompt, data, context)
                a.hint != null -> ResourceResolver.resolveTextValue(a.hint, data, context)
                a.placeholder != null -> ResourceResolver.resolveTextValue(a.placeholder, data, context)
                else -> null
            }

            // Parse colors
            val backgroundColor = ColorParser.parseColorStringWithBinding(
                TypedAttrs.rawString(a.common.background), data, context
            ) ?: Color.White
            val borderColor = ColorParser.parseColorStringWithBinding(
                TypedAttrs.rawString(a.common.borderColor), data, context
            ) ?: Color(0xFFCCCCCC)
            val textColor = ColorParser.parseColorStringWithBinding(
                labelString(a, "fontColor") ?: a.fontColor, data, context
            ) ?: Color.Black
            val hintColor = ColorParser.parseColorStringWithBinding(
                TypedAttrs.rawString(a.hintColor), data, context
            ) ?: Color(0xFF999999)

            val cornerRadius = TypedAttrs.int(a.common.cornerRadius, data) ?: 8

            // Handle value change
            val viewId = a.common.id ?: "selectbox"
            val onValueChange: (String) -> Unit = { newValue ->
                selectedDate = newValue

                // Update bound variable
                if (bindingVariable != null) {
                    @Suppress("UNCHECKED_CAST")
                    (data["updateData"] as? (Map<String, Any>) -> Unit)
                        ?.invoke(mapOf(bindingVariable to newValue))
                }

                // Call onValueChange handler if specified
                // (onValueChanged is the declared alias spelling)
                val handler = TypedAttrs.raw(a.onValueChange) as? String
                    ?: TypedAttrs.raw(a.onValueChanged) as? String
                if (handler != null && ModifierBuilder.isBinding(handler)) {
                    ModifierBuilder.resolveEventHandler(handler, data, viewId, newValue)
                }
            }

            // Build modifier (default fill width for date pickers).
            // DateSelectBox also draws its own background/border/corner
            // shape, so the modifier chain must not re-apply them.
            val modifier = buildSelfDrawnModifier(json, data)

            DateSelectBox(
                value = selectedDate,
                onValueChange = onValueChange,
                datePickerMode = datePickerMode,
                datePickerStyle = datePickerStyle,
                dateFormat = dateFormat,
                minuteInterval = minuteInterval,
                minimumDate = minimumDate,
                maximumDate = maximumDate,
                modifier = modifier,
                placeholder = placeholder,
                enabled = isEnabled,
                backgroundColor = backgroundColor,
                borderColor = borderColor,
                textColor = textColor,
                hintColor = hintColor,
                cornerRadius = cornerRadius
            )
        }

        // ── Helpers ──

        /**
         * Modifier chain for a component that draws its own decoration.
         * Mirrors the static selectbox_component.rb chain — testTag,
         * margins, size, alpha, clickable — and deliberately omits:
         *
         * - shadow / background (clip + border + background color): the
         *   composable renders border/background/corner shape from its own
         *   parameters; re-applying them on the modifier draws the frame
         *   twice (outer modifier frame + inner self-drawn frame),
         * - padding: content padding is a composable parameter
         *   (see [ModifierBuilder.parseContentPadding]).
         */
        internal fun buildSelfDrawnModifier(
            json: JsonObject,
            data: Map<String, Any>
        ): Modifier {
            var modifier: Modifier = Modifier
            modifier = ModifierBuilder.applyTestTag(modifier, json)
            modifier = ModifierBuilder.applyMargins(modifier, json, data)
            modifier = ModifierBuilder.applySize(modifier, json, defaultFillMaxWidth = true, data)
            // offset sits after size and before alpha, the same slot
            // buildModifier uses — outside background/shadow so the
            // decoration moves with the view, inside margins so siblings
            // do not. This chain does not call buildModifier, which is why
            // it needed the line of its own (51-C's warning, measured).
            modifier = ModifierBuilder.applyOffset(modifier, json, data)
            modifier = ModifierBuilder.applyAlpha(modifier, json, data)
            modifier = ModifierBuilder.applyClickable(modifier, json, data)
            return modifier
        }

        private fun parseOptions(json: JsonObject, data: Map<String, Any>): List<String> {
            // 'items' accepts a @{binding} string in addition to the declared
            // array shape, and primitive elements are stringified through
            // gson — wider than the generated List<Any?> coercion, so read
            // raw (see TypedAttrs.rawKey); 'options' is an undeclared legacy
            // runtime extra spelling.
            val optionsElement = TypedAttrs.rawKey(json, "items")
                ?: TypedAttrs.undeclared(json, "options")

            return when {
                optionsElement == null -> emptyList()
                optionsElement.isJsonArray -> {
                    optionsElement.asJsonArray.mapNotNull { element ->
                        when {
                            element.isJsonPrimitive -> element.asString
                            element.isJsonObject -> {
                                val obj = element.asJsonObject
                                obj.get("label")?.asString ?: obj.get("value")?.asString
                            }
                            else -> null
                        }
                    }
                }
                optionsElement.isJsonPrimitive && ModifierBuilder.isBinding(optionsElement.asString) -> {
                    val variable = ModifierBuilder.extractBindingProperty(optionsElement.asString)
                    if (variable != null) {
                        when (val options = data[variable]) {
                            is List<*> -> options.mapNotNull { it?.toString() }
                            is Array<*> -> options.mapNotNull { it?.toString() }
                            else -> emptyList()
                        }
                    } else {
                        emptyList()
                    }
                }
                else -> emptyList()
            }
        }

        private fun parseFontWeight(font: String?): FontWeight {
            return when (font?.lowercase()) {
                "bold" -> FontWeight.Bold
                "semibold" -> FontWeight.SemiBold
                "medium" -> FontWeight.Medium
                "light" -> FontWeight.Light
                "thin" -> FontWeight.Thin
                else -> FontWeight.Normal
            }
        }
    }
}
