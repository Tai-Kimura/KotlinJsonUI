package com.kotlinjsonui.dynamic.components

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.google.gson.JsonObject
import com.kotlinjsonui.components.SelectBox
import com.kotlinjsonui.components.DateSelectBox
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.UnappliedAttributes
import com.kotlinjsonui.dynamic.generated.SelectBoxAttributes
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
 * - onValueChange: @{handler} for change callback
 * - Modifiers: testTag, margins, size, alpha, clickable, padding, alignment, weight
 *
 * Attribute access goes through the generated [SelectBoxAttributes]
 * extraction (typed, L1-marker-aware) via the [TypedAttrs] bridge; the
 * node itself is only passed wholesale to the shared ModifierBuilder
 * pipeline and to the raw items/options lookup.
 */
class DynamicSelectBoxComponent {
    companion object {
        /** SelectBox-specific attributes this component applies (see UnappliedAttributes). */
        private val APPLIED: Set<String> = setOf(
            "selectItemType", "selectedItem", "selectedDate", "bind",
            "items", "enabled", "prompt", "hint", "placeholder",
            "fontColor", "hintColor", "fontSize", "font",
            "datePickerMode", "datePickerStyle", "dateStringFormat",
            "minuteInterval", "minimumDate", "maximumDate",
            "onValueChange"
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

            // Parse binding variable: selectedItem > bind
            val bindingVariable = TypedAttrs.binding(a.selectedItem)
                ?: (a.common.bind as? String)?.let { ModifierBuilder.extractBindingProperty(it) }

            // Get current selected value
            val currentValue = if (bindingVariable != null) {
                data[bindingVariable]?.toString() ?: ""
            } else ""

            var selectedValue by remember(currentValue, bindingVariable, data) {
                mutableStateOf(currentValue)
            }

            LaunchedEffect(data, bindingVariable) {
                if (bindingVariable != null) {
                    selectedValue = data[bindingVariable]?.toString() ?: ""
                }
            }

            // Parse options
            val options = parseOptions(json, data)

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
            val textColor = ColorParser.parseColorStringWithBinding(a.fontColor, data, context)
                ?: Color.Black
            val hintColor = ColorParser.parseColorStringWithBinding(
                TypedAttrs.rawString(a.hintColor), data, context
            ) ?: Color(0xFF999999)

            val cornerRadius = TypedAttrs.int(a.common.cornerRadius, data) ?: 8

            // Font styling
            val fontSize = a.fontSize?.toFloat()
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

                // Update bound variable
                if (bindingVariable != null) {
                    @Suppress("UNCHECKED_CAST")
                    (data["updateData"] as? (Map<String, Any>) -> Unit)
                        ?.invoke(mapOf(bindingVariable to newValue))
                }

                // Call onValueChange handler if specified
                val handler = TypedAttrs.raw(a.onValueChange) as? String
                if (handler != null && ModifierBuilder.isBinding(handler)) {
                    ModifierBuilder.resolveEventHandler(handler, data, viewId, newValue)
                }
            }

            // Build modifier (default fill width)
            val modifier = ModifierBuilder.buildModifier(
                json, data, context = context, defaultFillMaxWidth = true
            )

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
                cancelButtonBackgroundColor = cancelButtonBackgroundColor ?: backgroundColor,
                cancelButtonTextColor = cancelButtonTextColor ?: textColor
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
            val bindingVariable = TypedAttrs.binding(a.selectedDate)
                ?: TypedAttrs.binding(a.selectedItem)
                ?: (a.common.bind as? String)?.let { ModifierBuilder.extractBindingProperty(it) }

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
            val minimumDate = TypedAttrs.rawString(a.minimumDate)
            val maximumDate = TypedAttrs.rawString(a.maximumDate)

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
            val textColor = ColorParser.parseColorStringWithBinding(a.fontColor, data, context)
                ?: Color.Black
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
                val handler = TypedAttrs.raw(a.onValueChange) as? String
                if (handler != null && ModifierBuilder.isBinding(handler)) {
                    ModifierBuilder.resolveEventHandler(handler, data, viewId, newValue)
                }
            }

            // Build modifier (default fill width for date pickers)
            val modifier = ModifierBuilder.buildModifier(
                json, data, context = context, defaultFillMaxWidth = true
            )

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
