package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
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
import com.kotlinjsonui.dynamic.UnreadAttributes
import com.kotlinjsonui.dynamic.generated.SelectBoxAttributes
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.dynamic.helpers.LayoutPath
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
 * - enabled: Boolean state (the undeclared `disabled` is not read — NOT_READ)
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
         * `selectedValue` > `selectedIndex` — the codegen's order
         * (selectbox_component.rb). `bind` never reaches here: DynamicView folds it into the attribute
         * it stands for (BindFold, SSoT common.bind primaryValue).
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

        /** The item the closed box starts on: the bound selection, or with none the static seed. */
        internal fun selectionOf(a: SelectBoxAttributes, data: Map<String, Any>, options: List<String>): String =
            (boundSelection(a, data, options) ?: "").ifEmpty { initialSelection(a, data) }

        /** The date the box starts on: the bound value, else the static `selectedDate` (dateBindingVariableOf). */
        internal fun dateSelectionOf(a: SelectBoxAttributes, data: Map<String, Any>, bindingVariable: String?): String =
            if (bindingVariable != null) {
                data[bindingVariable]?.toString() ?: ""
            } else TypedAttrs.static(a.selectedDate) ?: ""

        /**
         * What a pick writes back — the new value of the selection binding:
         * the Int index of the picked item for an index binding, the item
         * String otherwise. The onValueChange handler is offered it first
         * (callValueChange).
         */
        internal fun selectionPayload(a: SelectBoxAttributes, options: List<String>, newValue: String): Any =
            if (isIndexBinding(a)) options.indexOf(newValue) else newValue

        /** The sentence for a date box's handler that asks for an index (the shared validator's, kjui's comment). */
        internal const val DATE_PICK_HAS_NO_INDEX =
            "a date SelectBox has no index: declare onValueChange as (String) or (String, String)"

        /** Test hook: receives every date-handler message emitted. */
        internal var dateHandlerWarningSink: ((String) -> Unit)? = null
        private val dateHandlerWarned = java.util.concurrent.ConcurrentHashMap.newKeySet<String>()

        /**
         * onValueChange, called as the handler is declared (ruling ③, the
         * kjui codegen's `value_change_call`): `()` with nothing; a lone
         * `(String)` with the picked ITEM, whatever the binding; `(Int)` with
         * its index; `(String, String)` with the viewId and the item;
         * `(String, Int)` with the viewId and the index. The index is
         * `indexOf` — an item's first index, -1 for the prompt — also where
         * no index binding computed it.
         *
         * Kotlin erases a closure's parameter types, so the handler is
         * offered the binding's own value first (the index for an index
         * binding, else the item — any other declared type, `(Any)` or
         * `(String, Any)`, takes that, as the codegen's reading does) and
         * then the other. An argument of the wrong type fails at the
         * closure's entry cast, before its body runs, so no handler runs
         * twice. It was handed only the binding's value: a lone `(String)`
         * under an index binding, or an `(Int)` without one, got the wrong
         * type, and resolveEventHandler's catch turned the
         * ClassCastException into silence.
         *
         * [index] null is a date: no index is offered. Returns false when
         * the handler refused every argument it was offered.
         */
        @Suppress("UNCHECKED_CAST")
        internal fun callValueChange(fn: Any, viewId: String, item: String, index: Int?, indexFirst: Boolean): Boolean {
            val values: List<Any> = when {
                index == null -> listOf(item)
                indexFirst -> listOf(index, item)
                else -> listOf(item, index)
            }
            for (value in values) {
                try {
                    when (fn) {
                        is Function0<*> -> fn()
                        is Function1<*, *> -> (fn as (Any?) -> Any?)(value)
                        is Function2<*, *, *> -> (fn as (Any?, Any?) -> Any?)(viewId, value)
                        else -> return false
                    }
                    return true
                } catch (_: ClassCastException) {
                    continue
                } catch (_: Exception) {
                    // the handler's own failure: it was called
                    return true
                }
            }
            return false
        }

        /**
         * A date SelectBox's handler that refused the date — declared
         * `(Int)` or `(String, Int)`: a date has no index, so it is not
         * called, and a debuggable build names it once per box and handler
         * (the kjui codegen writes the same sentence in a comment where the
         * call would be; the shared validator warns). A handler that throws
         * ClassCastException from its own body reads the same.
         */
        internal fun reportDateHandler(viewId: String, handler: String, context: android.content.Context?) {
            if (!com.kotlinjsonui.dynamic.DebugDiagnostics.isAppDebuggable(context)) return
            if (!dateHandlerWarned.add("$viewId:$handler")) return
            val message = "SelectBox.onValueChange $handler is not called: $DATE_PICK_HAS_NO_INDEX"
            dateHandlerWarningSink?.invoke(message)
            android.util.Log.w("DynamicSelectBox", message)
        }

        /** A list box's pick: onValueChange called as its handler is declared, the index `indexOf` the item. */
        internal fun pickHandlerCall(
            a: SelectBoxAttributes,
            data: Map<String, Any>,
            viewId: String,
            options: List<String>,
            newValue: String
        ) {
            val name = valueChangeHandler(a) ?: return
            val fn = data[name] ?: return
            callValueChange(fn, viewId, newValue, options.indexOf(newValue), indexFirst = isIndexBinding(a))
        }

        /** A date box's pick: onValueChange with the date; a handler that takes an index is not called, and is named. */
        internal fun datePickHandlerCall(
            a: SelectBoxAttributes,
            data: Map<String, Any>,
            viewId: String,
            newValue: String,
            context: android.content.Context?
        ) {
            val name = valueChangeHandler(a) ?: return
            val fn = data[name] ?: return
            if (!callValueChange(fn, viewId, newValue, index = null, indexFirst = false)) {
                reportDateHandler(viewId, name, context)
            }
        }

        /**
         * A Date box: selectItemType "Date", as written — the SSoT enum is
         * ["Normal", "Date"], and every path compares it so (the generated
         * enum parse matches any case, so "date" drew a date picker here and a
         * list on the codegens; the validator names any other value).
         */
        internal fun isDateBox(json: JsonObject): Boolean =
            TypedAttrs.rawKey(json, "selectItemType")?.takeIf { it.isJsonPrimitive }?.asString == "Date"

        /**
         * The handler's name, `@{x}` → `x`; null without a binding.
         * `onValueChanged` is onValueChange's declared alias since jsonui-cli
         * 1.9.6 (its own attribute before), and the generated parse folds it
         * into onValueChange.
         */
        private fun valueChangeHandler(a: SelectBoxAttributes): String? {
            val handler = TypedAttrs.raw(a.onValueChange) as? String ?: return null
            return if (ModifierBuilder.isBinding(handler)) ModifierBuilder.extractBindingProperty(handler) else null
        }

        /**
         * A Date SelectBox's value is its `selectedDate` (4f's ruling,
         * jsonui-cli 1.9.0: SSoT common.bind primaryValue, by selectItemType),
         * (a lone `bind` arrives as it, BindFold). It fell back to
         * `selectedItem`, which sjui and SwiftJsonUI never read; the shared
         * validator names a Date box's selectedValue / selectedItem /
         * selectedIndex.
         */
        internal fun dateBindingVariableOf(a: SelectBoxAttributes): String? =
            TypedAttrs.binding(a.selectedDate)

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

        /**
         * Keys SelectBox does not declare that this component used to read
         * as runtime extras: `disabled`. It is not read; a debuggable build
         * names it when a layout still writes it (UnreadAttributes), as the
         * jui validator does ("Unknown attribute"). The declared way is
         * `enabled: false` (or a bound `enabled`).
         */
        internal val NOT_READ: List<String> = listOf("disabled")

        private val APPLIED: Set<String> = setOf(
            "selectItemType", "selectedItem", "selectedValue", "selectedIndex",
            "selectedDate",
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
            val context = LocalContext.current
            UnappliedAttributes.check(
                "SelectBox", json,
                declared = SelectBoxAttributes.declaredAttributes,
                applied = UnappliedAttributes.COMMON_APPLIED + APPLIED,
                context = context
            )
            UnreadAttributes.check("SelectBox", json, NOT_READ, context)

            val isDatePicker = isDateBox(json)

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

            // The bound selection, or with none the static seed (initialSelection).
            val seed = selectionOf(a, data, options)
            // Keyed on the value this control declares — its binding's value, or the
            // static seed — and not on `data`: every unrelated data change handed a new
            // map and reset what the user had chosen (ticket
            // kjui-dynamic-stateful-components-reset-on-unrelated-data). A bound value that changes
            // still resets it: the view model's word wins.
            var selectedValue by remember(seed, bindingVariable) {
                mutableStateOf(seed)
            }

            // Enabled state (supports @{binding}). The declared `enabled`
            // only: the undeclared `disabled` is not read (see NOT_READ).
            val isEnabled = TypedAttrs.boolean(a.common.enabled, data) ?: true

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
            val viewId = LayoutPath.viewId(json)
            // The declared onClick, called after the selection — the SelectBox's
            // own operation; no outer `.clickable` calls it
            // (ModifierBuilder.onClickFromOperation).
            val onClick = ModifierBuilder.onClickFromOperation(json, data)
            val onValueChange: (String) -> Unit = { newValue ->
                selectedValue = newValue

                // The writeback: the Int index for an index binding, the item
                // String otherwise. The handler takes what it is declared to
                // take (callValueChange).
                val payload = selectionPayload(a, options, newValue)

                // Update bound variable
                if (bindingVariable != null) {
                    @Suppress("UNCHECKED_CAST")
                    (data["updateData"] as? (Map<String, Any>) -> Unit)
                        ?.invoke(mapOf(bindingVariable to payload))
                }

                // onValueChange, as its handler is declared (callValueChange)
                pickHandlerCall(a, data, viewId, options, newValue)
                onClick?.invoke()
            }

            // Build modifier (default fill width). SelectBox draws its own
            // background/border/corner shape from the composable parameters,
            // so the modifier chain must not re-apply them.
            val modifier = buildSelfDrawnModifier(json, data, RoundedCornerShape(cornerRadius.dp))

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

            // Parse binding variable: selectedDate
            val bindingVariable = dateBindingVariableOf(a)

            // Get current value. Unbound, the static date is where the box
            // starts (ticket static-valued-controls-do-not-change-on-a-users-tap:
            // a literal selectedDate was dropped — the box drew empty and the
            // calendar opened on today), as initialSelection seeds the list box.
            val currentValue = dateSelectionOf(a, data, bindingVariable)

            // Keyed on the value this control declares — its binding's value, or the
            // static one — and not on `data`: every unrelated data change handed a new
            // map and reset what the user had chosen (ticket
            // kjui-dynamic-stateful-components-reset-on-unrelated-data). A bound value that changes
            // still resets it: the view model's word wins.
            var selectedDate by remember(currentValue, bindingVariable) {
                mutableStateOf(currentValue)
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

            // Enabled state (supports @{binding}). The declared `enabled`
            // only: the undeclared `disabled` is not read (see NOT_READ).
            val isEnabled = TypedAttrs.boolean(a.common.enabled, data) ?: true

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
            val viewId = LayoutPath.viewId(json)
            // The declared onClick, called after the selection — the SelectBox's
            // own operation; no outer `.clickable` calls it
            // (ModifierBuilder.onClickFromOperation).
            val onClick = ModifierBuilder.onClickFromOperation(json, data)
            val onValueChange: (String) -> Unit = { newValue ->
                selectedDate = newValue

                // Update bound variable
                if (bindingVariable != null) {
                    @Suppress("UNCHECKED_CAST")
                    (data["updateData"] as? (Map<String, Any>) -> Unit)
                        ?.invoke(mapOf(bindingVariable to newValue))
                }

                // onValueChange with the date; one declared to take an index
                // is not called and is named (reportDateHandler)
                datePickHandlerCall(a, data, viewId, newValue, context)
                onClick?.invoke()
            }

            // Build modifier (default fill width for date pickers).
            // DateSelectBox also draws its own background/border/corner
            // shape, so the modifier chain must not re-apply them.
            val modifier = buildSelfDrawnModifier(json, data, RoundedCornerShape(cornerRadius.dp))

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
         * Modifier chain for a component that draws its own decoration —
         * testTag, margins, size, offset, alpha, shadow, the control's
         * clickable stage (ModifierBuilder.applyControlClickable) — which
         * deliberately omits:
         *
         * - background (clip + border + background color): the composable
         *   renders border/background/corner shape from its own parameters;
         *   re-applying them on the modifier draws the frame twice (outer
         *   modifier frame + inner self-drawn frame),
         * - padding: content padding is a composable parameter
         *   (see [ModifierBuilder.parseContentPadding]).
         *
         * The shadow draws no frame, only the shade outside the view: it is
         * cast in [ownShape], the corner the composable draws itself. It was
         * omitted with the frame, and the SSoT declares it on every type.
         */
        internal fun buildSelfDrawnModifier(
            json: JsonObject,
            data: Map<String, Any>,
            ownShape: Shape? = null
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
            modifier = ModifierBuilder.applyShadow(modifier, json, data, ownShape)
            // A control's clickable stage: the gestures and the blocker, no
            // outer click (onClick is called after the selection), and
            // `enabled` is on this node — the composable's own clickable.
            modifier = ModifierBuilder.applyControlClickable(
                modifier, json, data, ModifierBuilder.ControlTap.ENABLED_ON_NODE
            )
            return modifier
        }

        internal fun parseOptions(json: JsonObject, data: Map<String, Any>): List<String> {
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
