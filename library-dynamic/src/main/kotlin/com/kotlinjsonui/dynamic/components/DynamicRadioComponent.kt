package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.res.painterResource
import com.kotlinjsonui.dynamic.R
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.UnappliedAttributes
import com.kotlinjsonui.dynamic.generated.RadioAttributes
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ResourceResolver
import com.kotlinjsonui.dynamic.rememberTypedAttrs
import androidx.compose.ui.platform.LocalContext

/**
 * Dynamic Radio Component Converter
 * Converts JSON to RadioButton/RadioGroup composable at runtime.
 * Reference: radio_component.rb in kjui_tools.
 *
 * Three modes:
 * 1. Radio group with items array (highest priority)
 * 2. Individual radio item (has group or text without items/options)
 * 3. Radio group with options (static array or @{binding})
 *
 * Attribute access goes through the generated [RadioAttributes]
 * extraction (typed, alias-aware, L1-marker-aware) via the [TypedAttrs]
 * bridge; the node itself is only passed wholesale to the shared
 * ModifierBuilder pipeline. Several legacy runtime extras (items,
 * options, selectedValue, onValueChange, selectedColor, unselectedColor,
 * textColor) are not declared for Radio and stay on
 * [TypedAttrs.undeclared].
 *
 * Supported JSON attributes:
 * - bind: @{variable} for selected value binding
 * - options: Array of options or @{variable} for dynamic options
 * - items: Array of items for radio group
 * - selectedValue: @{variable} for selected value in items mode
 * - onValueChange: @{handler} for change callback
 * - selectedColor/unselectedColor: Colors for RadioButtonDefaults.colors
 * - icon/selectedIcon: Custom icon names
 * - group: Group identifier for individual radio item
 * - text: Label text
 * - fontColor/textColor: Label text color
 * - Modifiers: testTag, margins, alpha, padding, weight
 */
class DynamicRadioComponent {
    companion object {
        /** Radio-specific attributes this component applies (see UnappliedAttributes). */
        private val APPLIED: Set<String> = setOf(
            "group", "text", "label", "icon", "selectedIcon", "fontColor", "tintColor",
            "selectedValue", "value"
        )

        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            val a = rememberTypedAttrs(json) { m, canonicalOnly ->
                RadioAttributes.parse(m, canonicalOnly)
            }
            UnappliedAttributes.check(
                "Radio", json,
                declared = RadioAttributes.declaredAttributes,
                applied = UnappliedAttributes.COMMON_APPLIED + APPLIED,
                context = LocalContext.current
            )

            when {
                // Handle radio group with items (highest priority) —
                // 'items' is an undeclared legacy runtime extra on Radio
                TypedAttrs.undeclared(json, "items") != null ->
                    createRadioGroupWithItems(json, a, data)
                // Handle individual radio item —
                // 'options' is an undeclared legacy runtime extra
                rendersAsItem(a, hasOptions = TypedAttrs.undeclared(json, "options") != null) ->
                    createRadioItem(json, a, data)
                // Handle radio group with options
                else -> createRadioGroup(json, a, data)
            }
        }

        // ── Item-mode decisions (pure; pinned by DynamicRadioItemModeTest) ──

        /**
         * Whether the node renders as a single radio row rather than a group.
         *
         * `label` is the cross-platform spelling of the row text and routes
         * here exactly like `text` — the kjui codegen has read both since it
         * was written (`radio_component.rb:19`, `text || label` at :182). The
         * dynamic path read only `text`, so a label-only Radio fell through to
         * the options branch and rendered an empty Column (34: `Radio/label`
         * pixel-identical to its control on android).
         */
        /**
         * `Radio.spacing` — "Space between icon and text", declared
         * `["number","binding"]` and hard-coded at 8.dp here, so no declared
         * value could reach the output. C landed the codegen half
         * (`radio_component.rb:533` `radio_spacing_dp`, default 8) and run 4
         * measured the gap at distance 12 once D raised the fixture from 8 to
         * 16 — at 8 the two paths agreed by sharing a default, which is how a
         * dropped attribute hides: BOTH sides silently right for one value.
         *
         * The vertical gap between a group's own label and its items is a
         * different distance and stays where it is; the codegen does not spell
         * it either.
         */
        internal fun spacingDp(a: RadioAttributes, data: Map<String, Any>): Float =
            TypedAttrs.float(a.spacing, data) ?: 8f

        internal fun rendersAsItem(a: RadioAttributes, hasOptions: Boolean): Boolean =
            a.group != null || ((a.text != null || a.label != null) && !hasOptions)

        /**
         * The row's text as WRITTEN — `text || label`, the codegen's order.
         *
         * This is the layout spelling, so `@{expr}` comes back verbatim; the
         * caller resolves it. Keeping the choice-of-row separate from the
         * resolution is what lets it be pinned on the JVM, where there is no
         * Context to resolve against.
         */
        internal fun itemText(a: RadioAttributes): String =
            TypedAttrs.rawString(a.text) ?: TypedAttrs.rawString(a.label) ?: ""

        /**
         * The option's identity within its group: the declared `value`, with
         * the node id as the fallback (`sjui radio_converter.rb:65` and the
         * rjui single-radio path read the same order).
         */
        internal fun itemValue(a: RadioAttributes, id: String): String =
            a.value?.toString() ?: id

        /**
         * Selected state for a single radio row.
         *
         * A declared `selectedValue` names the group's selected option
         * directly and wins over the group state — a static one selects this
         * row when it matches its value, a bound one is the two-way channel.
         * Only the group data drove selection here, so a Radio declaring
         * selectedValue rendered unselected (34: pixel-identical to its
         * control on android; rjui was corrected the same way in plan 44
         * Phase 0, `checked = selectedValue === value`).
         *
         * Without `selectedValue` the historical rule stands: the group state
         * names this row's id, or a declared `checked` (static or bound) seeds
         * the group while it is still unset.
         */
        internal fun itemIsSelected(
            a: RadioAttributes,
            id: String,
            data: Map<String, Any>
        ): Boolean {
            val declaredSelection = TypedAttrs.string(a.selectedValue, data)
            val token = itemValue(a, id)
            if (declaredSelection != null) return declaredSelection == token
            val selectedVar = selectedVarName(a.group ?: "default")
            // The group's selection is compared against this item's IDENTITY —
            // `value` when declared, the node id otherwise — not against the id
            // unconditionally (codegen: `token = value || id`).
            val groupState = (data[selectedVar] as? String) == token

            // `checked` is a SEED, not an override: it says which option STARTS
            // selected when nothing else has. Both faces of the seed are
            // declared — `["boolean","binding"]` — and the SSoT is explicit
            // that the BOUND form "seeds the glyph rather than the state, since
            // a property initialiser cannot read the data map: it selects only
            // while the group has made no choice yet". `raw(...) as? Boolean`
            // hands back the `"@{expr}"` String for that face, so the cast
            // silently dropped it (Radio/checked__true active on android,
            // Radio/checked__binding inert).
            val seedChecked = TypedAttrs.boolean(a.checked, data) == true

            // "Until the group has made a choice" is the whole guard, and it is
            // the SAME guard whether or not the node names a group: `group`
            // only picks WHICH key holds the selection, it does not decide
            // whether a seed exists. Returning groupState unconditionally for a
            // named group discarded the seed even at first render, where
            // nothing had chosen yet — Radio/checked__true_with_group drew an
            // unselected glyph on android while ios and web drew the seed.
            // Once the group HAS a selection the seed is out of the way, which
            // is what stops it pinning a radio the user can no longer change.
            // (49-E's precedence — bound selectedValue > literal selectedValue
            // > checked — never listed the group state above the seed; the
            // group is the state the seed initialises, not a rival to it.)
            return groupState || (seedChecked && data[selectedVar] == null)
        }

        /**
         * Which option an ITEMS-mode group starts on.
         *
         * `selectedValue` is declared on Radio, and the item-mode path reads it
         * through the typed accessor — but this path reached for it through the
         * `undeclared` escape hatch and then only asked whether it was a
         * binding. A literal `selectedValue: "Gamma"` therefore named nothing
         * and the group rendered with nothing selected
         * (`Radio/selectedValue__gamma`).
         *
         * The two faces do different jobs, exactly as they do on SelectBox: the
         * bound one is the group's live channel, the literal one is the seed.
         */
        internal fun groupInitialSelection(
            a: RadioAttributes,
            data: Map<String, Any>
        ): String {
            val bindingVariable = extractBindingVariable(TypedAttrs.rawString(a.selectedValue))
            if (bindingVariable != null) return (data[bindingVariable] as? String) ?: ""
            return TypedAttrs.static(a.selectedValue) ?: ""
        }

        /** Group name → the data key holding that group's selection. */
        internal fun selectedVarName(group: String): String =
            if (group.lowercase() != "default") {
                "selected${group.replaceFirstChar { it.uppercase() }}"
            } else {
                "selectedRadiogroup"
            }

        // ── Radio group with options (static array or @{binding}) ──

        @Composable
        private fun createRadioGroup(
            json: JsonObject,
            a: RadioAttributes,
            data: Map<String, Any>
        ) {
            val context = LocalContext.current

            // Parse binding variable ('bind' is a common declared row)
            val bindingVariable = extractBindingVariable(a.common.bind as? String)

            // Get selected value from data
            val currentSelected = if (bindingVariable != null) {
                (data[bindingVariable] as? String) ?: ""
            } else ""

            var selectedValue by remember(currentSelected, bindingVariable, data) {
                mutableStateOf(currentSelected)
            }

            LaunchedEffect(data, bindingVariable) {
                if (bindingVariable != null) {
                    selectedValue = (data[bindingVariable] as? String) ?: ""
                }
            }

            // Parse options: static array or @{binding}
            val options = parseOptions(json, data)

            // Parse colors (supports @{binding}) — 'selectedColor' /
            // 'unselectedColor' are undeclared legacy runtime extras
            // (the declared spellings are checkedColor/uncheckedColor)
            // Declared spellings first (checkedColor/uncheckedColor/iconColor
            // were parsed but never read — 33 cross-effect measured android
            // rendering the default glyph for all three).
            val selectedColor = ColorParser.parseColorStringWithBinding(
                a.checkedColor, data, context
            ) ?: ColorParser.parseColorStringWithBinding(
                TypedAttrs.undeclared(json, "selectedColor")?.asString, data, context
            )
            val unselectedColor = ColorParser.parseColorStringWithBinding(
                a.uncheckedColor, data, context
            ) ?: ColorParser.parseColorStringWithBinding(
                a.iconColor, data, context
            ) ?: ColorParser.parseColorStringWithBinding(
                TypedAttrs.undeclared(json, "unselectedColor")?.asString, data, context
            )

            val colors = if (selectedColor != null || unselectedColor != null) {
                RadioButtonDefaults.colors(
                    selectedColor = selectedColor ?: RadioButtonDefaults.colors().selectedColor,
                    unselectedColor = unselectedColor ?: RadioButtonDefaults.colors().unselectedColor
                )
            } else {
                RadioButtonDefaults.colors()
            }

            // Handle value change
            val viewId = a.common.id ?: "radio"
            val onValueChange: (String) -> Unit = { newValue ->
                selectedValue = newValue

                // Update bound variable
                if (bindingVariable != null) {
                    @Suppress("UNCHECKED_CAST")
                    (data["updateData"] as? (Map<String, Any>) -> Unit)
                        ?.invoke(mapOf(bindingVariable to newValue))
                }

                // Call onValueChange handler if specified
                // (undeclared legacy runtime extra on Radio)
                val handler = TypedAttrs.undeclared(json, "onValueChange")?.asString
                if (handler != null && ModifierBuilder.isBinding(handler)) {
                    ModifierBuilder.resolveEventHandler(handler, data, viewId, newValue)
                }
            }

            // Build modifier
            val modifier = ModifierBuilder.buildModifier(json, data, context = context)

            Column(modifier = modifier) {
                options.forEach { (value, label) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onValueChange(value) }
                    ) {
                        RadioButton(
                            selected = selectedValue == value,
                            onClick = { onValueChange(value) },
                            colors = colors
                        )
                        Spacer(modifier = Modifier.width(spacingDp(a, data).dp))
                        Text(text = label)
                    }
                }
            }
        }

        // ── Individual radio item (group/text mode) ──

        @Composable
        private fun createRadioItem(
            json: JsonObject,
            a: RadioAttributes,
            data: Map<String, Any>
        ) {
            val context = LocalContext.current
            val id = a.common.id ?: "radio_${System.currentTimeMillis()}"
            // Resolve the binding / string-resource name — the raw spelling
            // used to reach the label unchanged, so a bound row drew the
            // characters `@{expr}` on screen (smoke run: Radio/text__binding
            // rendered "@{boundText}").
            val text = ResourceResolver.resolveTextValue(itemText(a), data, context)
            val radioValue = itemValue(a, id)
            val selectedVar = selectedVarName(a.group ?: "default")
            val isSelected = itemIsSelected(a, id, data)

            // Build modifier
            val modifier = ModifierBuilder.buildModifier(json, data, context = context)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
            ) {
                val icon = a.icon
                val selectedIcon = a.selectedIcon

                // Update function for selection. A bound `selectedValue` is
                // the declared two-way channel, so the tap writes this row's
                // value back through it as well as through the group state.
                val selectionBinding =
                    extractBindingVariable(TypedAttrs.rawString(a.selectedValue))
                val onSelect: () -> Unit = {
                    val updates = mutableMapOf<String, Any>(selectedVar to id)
                    if (selectionBinding != null) updates[selectionBinding] = radioValue
                    @Suppress("UNCHECKED_CAST")
                    (data["updateData"] as? (Map<String, Any>) -> Unit)?.invoke(updates)
                }

                // iconSize sizes the GLYPH: Material draws its radio glyph at
                // a fixed 20dp, so a bare .size(N) just clips it. Scaling by
                // N/20 inside the N-dp box draws it at the declared size
                // (same emission as the kjui codegen icon_appearance_args).
                val glyphModifier = a.iconSize?.let {
                    Modifier.size(it.dp).scale((it / 20.0).toFloat())
                } ?: Modifier

                when {
                    // Standard radio button (circle icons or no icons)
                    (icon == "circle" || icon == null) &&
                            (selectedIcon == "checkmark.circle.fill" || selectedIcon == null) -> {
                        // Declared color skin — this branch (the one every
                        // icon-less fixture takes) never received it (33
                        // cross-effect round-2: android still default).
                        val stdSelectedColor = ColorParser.parseColorStringWithBinding(
                            a.checkedColor, data, context
                        )
                        val stdUnselectedColor = ColorParser.parseColorStringWithBinding(
                            a.uncheckedColor, data, context
                        ) ?: ColorParser.parseColorStringWithBinding(
                            a.iconColor, data, context
                        )
                        RadioButton(
                            selected = isSelected,
                            onClick = onSelect,
                            modifier = glyphModifier,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = stdSelectedColor
                                    ?: RadioButtonDefaults.colors().selectedColor,
                                unselectedColor = stdUnselectedColor
                                    ?: RadioButtonDefaults.colors().unselectedColor
                            )
                        )
                    }
                    // Square checkbox appearance
                    icon == "square" &&
                            (selectedIcon == "checkmark.square.fill" || selectedIcon == null) -> {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { onSelect() }
                        )
                    }
                    // Custom icons
                    icon != null || selectedIcon != null -> {
                        val iconResId = mapIconResId(icon ?: "star")
                        val selectedIconResId = mapIconResId(selectedIcon ?: "star.fill")

                        IconButton(onClick = onSelect) {
                            Icon(
                                painter = painterResource(if (isSelected) selectedIconResId else iconResId),
                                contentDescription = text,
                                tint = if (isSelected) {
                                    // 'selectedColor' is an undeclared legacy runtime extra
                                    ColorParser.parseColorStringWithBinding(
                                        TypedAttrs.undeclared(json, "selectedColor")?.asString,
                                        data, context
                                    )
                                        ?: ColorParser.parseColorStringWithBinding(
                                            TypedAttrs.rawString(a.common.tintColor), data, context
                                        )
                                        ?: MaterialTheme.colorScheme.primary
                                } else {
                                    Color.Gray
                                }
                            )
                        }
                    }
                    // Default radio button
                    else -> {
                        // The default-glyph path dropped the color skin
                        // entirely (33 cross-effect) — rebuild the declared
                        // checked/unchecked/icon colors here.
                        val itemSelectedColor = ColorParser.parseColorStringWithBinding(
                            a.checkedColor, data, context
                        )
                        val itemUnselectedColor = ColorParser.parseColorStringWithBinding(
                            a.uncheckedColor, data, context
                        ) ?: ColorParser.parseColorStringWithBinding(
                            a.iconColor, data, context
                        )
                        RadioButton(
                            selected = isSelected,
                            onClick = onSelect,
                            modifier = glyphModifier,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = itemSelectedColor
                                    ?: RadioButtonDefaults.colors().selectedColor,
                                unselectedColor = itemUnselectedColor
                                    ?: RadioButtonDefaults.colors().unselectedColor
                            )
                        )
                    }
                }

                // Add label text
                if (text.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(spacingDp(a, data).dp))
                    // 'textColor' is an undeclared legacy runtime extra
                    val textColor =
                        ColorParser.parseColorStringWithBinding(
                            TypedAttrs.rawString(a.fontColor), data, context
                        )
                            ?: ColorParser.parseColorStringWithBinding(
                                TypedAttrs.undeclared(json, "textColor")?.asString, data, context
                            )
                            ?: Color.Black
                    // font (weight spelling) / fontSize were never read on
                    // the label (33 cross-effect: android rendered default
                    // weight/size for font: bold / fontSize).
                    //
                    // `font` is declared `["string","binding"]` and `rawString`
                    // returns `"@{expr}"` verbatim, so the local table could
                    // only match the static face — Radio/font__static was
                    // active on android while Radio/font__binding drew the
                    // default weight. Resolve, then use the shared table (the
                    // local copy also stopped three names short of it).
                    val labelWeight =
                        ResourceResolver.fontWeightFor(TypedAttrs.string(a.font, data))
                    val labelSize = TypedAttrs.float(a.fontSize, data)
                    Text(
                        text = text,
                        color = textColor,
                        fontWeight = labelWeight,
                        fontSize = labelSize?.sp ?: androidx.compose.ui.unit.TextUnit.Unspecified
                    )
                }
            }
        }

        // ── Radio group with items array ──

        @Composable
        private fun createRadioGroupWithItems(
            json: JsonObject,
            a: RadioAttributes,
            data: Map<String, Any>
        ) {
            val context = LocalContext.current
            // 'items' is an undeclared legacy runtime extra on Radio
            val items = TypedAttrs.undeclared(json, "items")
                ?.asJsonArray?.map { it.asString } ?: emptyList()

            // `selectedValue` IS declared on Radio — the item-mode path reads it
            // through the typed accessor — so reaching for it through the
            // `undeclared` escape hatch here was the bug, not a legacy quirk:
            // the hatch returns the raw element, and only its BOUND face was
            // ever consulted. A literal `selectedValue: "Gamma"` therefore
            // named nothing and the group rendered with no option selected
            // (Radio/selectedValue__gamma, redistributed by E2).
            //
            // Same two-face split as SelectBox's seed: the binding is the
            // channel, the literal is the seed.
            val bindingVariable = extractBindingVariable(TypedAttrs.rawString(a.selectedValue))
            val currentSelected = groupInitialSelection(a, data)

            var selectedValue by remember(currentSelected, bindingVariable, data) {
                mutableStateOf(currentSelected)
            }

            LaunchedEffect(data, bindingVariable) {
                if (bindingVariable != null) {
                    selectedValue = (data[bindingVariable] as? String) ?: ""
                }
            }

            // Handle value change
            val onValueChange: (String) -> Unit = { newValue ->
                selectedValue = newValue
                if (bindingVariable != null) {
                    @Suppress("UNCHECKED_CAST")
                    (data["updateData"] as? (Map<String, Any>) -> Unit)
                        ?.invoke(mapOf(bindingVariable to newValue))
                }
            }

            // Build modifier
            val modifier = ModifierBuilder.buildModifier(json, data, context = context)

            // Parse text color ('textColor' is an undeclared legacy runtime extra)
            val textColor = ColorParser.parseColorStringWithBinding(
                TypedAttrs.rawString(a.fontColor), data, context
            )
                ?: ColorParser.parseColorStringWithBinding(
                    TypedAttrs.undeclared(json, "textColor")?.asString, data, context
                )
                ?: Color.Black

            Column(modifier = modifier) {
                // Add label if present
                TypedAttrs.rawString(a.text)?.let {
                    val label = ResourceResolver.resolveTextValue(it, data, context)
                    Text(text = label, color = textColor)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Generate radio items
                items.forEach { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onValueChange(item) }
                    ) {
                        RadioButton(
                            selected = selectedValue == item,
                            onClick = { onValueChange(item) }
                        )
                        Spacer(modifier = Modifier.width(spacingDp(a, data).dp))
                        Text(text = item, color = textColor)
                    }
                }
            }
        }

        // ── Helpers ──

        private fun parseOptions(json: JsonObject, data: Map<String, Any>): List<Pair<String, String>> {
            // 'options' is an undeclared legacy runtime extra on Radio
            val optionsElement = TypedAttrs.undeclared(json, "options") ?: return emptyList()

            return when {
                optionsElement.isJsonArray -> {
                    optionsElement.asJsonArray.map { element ->
                        when {
                            element.isJsonObject -> {
                                val obj = element.asJsonObject
                                Pair(
                                    obj.get("value")?.asString ?: "",
                                    obj.get("label")?.asString ?: ""
                                )
                            }
                            element.isJsonPrimitive -> {
                                val value = element.asString
                                Pair(value, value)
                            }
                            else -> Pair("", "")
                        }
                    }
                }
                optionsElement.isJsonPrimitive && ModifierBuilder.isBinding(optionsElement.asString) -> {
                    val variable = ModifierBuilder.extractBindingProperty(optionsElement.asString)
                    @Suppress("UNCHECKED_CAST")
                    val dynamicOptions = variable?.let { data[it] as? List<String> } ?: emptyList()
                    dynamicOptions.map { Pair(it, it) }
                }
                else -> emptyList()
            }
        }

        private fun extractBindingVariable(value: String?): String? {
            if (value == null) return null
            return ModifierBuilder.extractBindingProperty(value)
        }

        private fun mapIconResId(iconName: String): Int {
            return when (iconName) {
                "circle" -> R.drawable.ic_panorama_fish_eye
                "checkmark.circle.fill" -> R.drawable.ic_check_circle_filled
                "star" -> R.drawable.ic_star_outlined
                "star.fill" -> R.drawable.ic_star_filled
                "heart" -> R.drawable.ic_favorite_border
                "heart.fill" -> R.drawable.ic_favorite_filled
                "square" -> R.drawable.ic_check_box_outline_blank
                "checkmark.square.fill" -> R.drawable.ic_check_box_filled
                else -> R.drawable.ic_star_outlined
            }
        }
    }
}
