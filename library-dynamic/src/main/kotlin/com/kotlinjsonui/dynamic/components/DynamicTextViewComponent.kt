package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.JsonObject
import com.kotlinjsonui.components.CustomTextField
import com.kotlinjsonui.components.CustomTextFieldWithMargins
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.UnappliedAttributes
import com.kotlinjsonui.dynamic.generated.TextViewAttributes
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ResourceResolver
import com.kotlinjsonui.dynamic.rememberTypedAttrs

/**
 * TextView component → CustomTextField (multiline) / CustomTextFieldWithMargins.
 * Reference: textview_component.rb in kjui_tools.
 *
 * Key differences from TextField:
 * - Default height: 120dp
 * - Default width: fillMaxWidth
 * - Default maxLines: unlimited
 * - singleLine: false
 * - isOutlined: true by default
 */
class DynamicTextViewComponent {
    companion object {
        @Composable
        fun create(json: JsonObject, data: Map<String, Any> = emptyMap()) {
            val context = LocalContext.current
            val a = rememberTypedAttrs(json) { m, canonicalOnly ->
                TextViewAttributes.parse(m, canonicalOnly)
            }
            UnappliedAttributes.check(
                "TextView", json,
                declared = TextViewAttributes.declaredAttributes,
                applied = UnappliedAttributes.COMMON_APPLIED + APPLIED,
                context = context
            )

            // Parse text value with data binding
            val rawText = TypedAttrs.rawString(a.text) ?: ""
            val initialText = ResourceResolver.resolveTextValue(rawText, data, context)
            val textFieldState = rememberTextFieldState(initialText = initialText)

            // Parse placeholder with resource resolution ('placeholder' is a
            // standalone declared row alongside 'hint')
            val rawPlaceholder = a.hint ?: a.placeholder ?: ""
            val placeholderText = ResourceResolver.resolveTextValue(rawPlaceholder, data, context)

            // Enabled state (supports @{binding})
            val isEnabled = TypedAttrs.boolean(a.common.enabled, data) ?: true

            // Text style (supports @{binding})
            val fontSize = TypedAttrs.int(a.fontSize, data) ?: Configuration.TextField.defaultFontSize
            val textColor = ColorParser.parseColorStringWithBinding(
                TypedAttrs.rawString(a.fontColor), data, context
            ) ?: Configuration.TextField.defaultTextColor
            // `font` (the weight spelling, or a family name when it is not a
            // weight) and `fontFamily` reached nothing on this component —
            // both rows were parsed and never read (34: `TextView/font` and
            // `TextView/fontFamily` pixel-identical to their controls). sjui
            // reads the same `fontFamily || font` order
            // (textview_converter.rb:146).
            val fieldFontSpelling = TypedAttrs.string(a.font, data)
            val fieldFontWeight = ResourceResolver.fontWeightFor(fieldFontSpelling)
            val fieldFontFamily = ResourceResolver.resolveFontFamily(
                TypedAttrs.string(a.fontFamily, data), fieldFontSpelling, context
            )

            // Background colors (supports @{binding}); 'highlightBackground'
            // is a declared common attribute for TextView (no Button-style
            // tapBackground rewrite applies here)
            val backgroundColor = ColorParser.parseColorStringWithBinding(
                TypedAttrs.rawString(a.common.background), data, context
            ) ?: Configuration.TextField.defaultBackgroundColor
            val highlightBackgroundColor = ColorParser.parseColorStringWithBinding(
                TypedAttrs.rawString(a.common.highlightBackground), data, context
            ) ?: Configuration.TextField.defaultHighlightBackgroundColor

            // Border color (supports @{binding})
            val borderColor = ColorParser.parseColorStringWithBinding(
                TypedAttrs.rawString(a.common.borderColor), data, context
            ) ?: Configuration.TextField.defaultBorderColor

            // Shape
            val cornerRadius = TypedAttrs.float(a.common.cornerRadius, data)
                ?: Configuration.TextField.defaultCornerRadius.toFloat()
            val shape = RoundedCornerShape(cornerRadius.dp)

            // isOutlined (default true for TextView)
            val isOutlined = true

            // Max lines ('maxLines' is an undeclared legacy runtime extra;
            // default unlimited for TextView)
            val maxLines = TypedAttrs.undeclared(json, "maxLines")?.asInt ?: Int.MAX_VALUE

            // Keyboard options
            val keyboardOptions = buildKeyboardOptions(a)

            // Container inset → contentPadding
            val contentPadding = buildContainerInset(a.containerInset)

            // TextFieldState sync with data binding
            val viewId = a.common.id ?: "textview"

            LaunchedEffect(initialText) {
                if (textFieldState.text.toString() != initialText) {
                    textFieldState.edit { replace(0, length, initialText) }
                }
            }

            LaunchedEffect(textFieldState.text) {
                val newValue = textFieldState.text.toString()
                if (newValue != initialText) {
                    if (rawText.contains("@{")) {
                        val variable = extractBindingVariable(rawText)
                        variable?.let { varName ->
                            val updateData = data["updateData"]
                            if (updateData is Function<*>) {
                                try {
                                    @Suppress("UNCHECKED_CAST")
                                    (updateData as (Map<String, Any>) -> Unit)(mapOf(varName to newValue))
                                } catch (_: Exception) {}
                            }
                        }
                    }
                    val onTextChangeHandler = a.onTextChange
                    if (onTextChangeHandler != null) {
                        ModifierBuilder.resolveEventHandler(onTextChangeHandler, data, viewId, newValue)
                    }
                }
            }

            // Placeholder. hintColor styles it (33 cross-effect: android
            // rendered the default gray for a declared hintColor).
            // hintAttributes.fontColor outranks the flat hintColor.
            val hintColor = ColorParser.parseColorStringWithBinding(
                ResourceResolver.nestedString(a.hintAttributes, "fontColor")
                    ?: TypedAttrs.rawString(a.hintColor), data, context
            )
            val placeholder: @Composable (() -> Unit)? = if (placeholderText.isNotEmpty()) {
                buildPlaceholder(a, placeholderText, TypedAttrs.int(a.fontSize, data), hintColor)
            } else null

            // Check margins
            val hasMargins = hasMarginAttributes(json)

            if (hasMargins) {
                // Box modifier with margins and weight
                var boxModifier: Modifier = Modifier
                boxModifier = ModifierBuilder.applyTestTag(boxModifier, json)
                boxModifier = ModifierBuilder.applyMargins(boxModifier, json, data)

                // TextField modifier with size (default fillMaxWidth + 120dp height)
                var textFieldModifier = buildTextViewSizeModifier(json, a)
                textFieldModifier = ModifierBuilder.applyAlpha(textFieldModifier, json, data)
                textFieldModifier = ModifierBuilder.applyPadding(textFieldModifier, json, data)

                CustomTextFieldWithMargins(
                    state = textFieldState,
                    boxModifier = boxModifier,
                    textFieldModifier = textFieldModifier,
                    placeholder = placeholder,
                    shape = shape,
                    backgroundColor = backgroundColor,
                    highlightBackgroundColor = highlightBackgroundColor,
                    borderColor = borderColor,
                    isOutlined = isOutlined,
                    maxLines = maxLines,
                    singleLine = false,
                    textStyle = TextStyle(
                        fontSize = fontSize.sp,
                        color = textColor,
                        fontWeight = fieldFontWeight,
                        fontFamily = fieldFontFamily
                    ),
                    keyboardOptions = keyboardOptions,
                    contentPadding = contentPadding,
                    enabled = isEnabled
                )
            } else {
                // Regular modifier with size (default fillMaxWidth + 120dp height)
                var modifier = buildTextViewSizeModifier(json, a)
                modifier = ModifierBuilder.applyTestTag(modifier, json)
                modifier = ModifierBuilder.applyAlpha(modifier, json, data)
                modifier = ModifierBuilder.applyPadding(modifier, json, data)

                CustomTextField(
                    state = textFieldState,
                    modifier = modifier,
                    placeholder = placeholder,
                    shape = shape,
                    backgroundColor = backgroundColor,
                    highlightBackgroundColor = highlightBackgroundColor,
                    borderColor = borderColor,
                    isOutlined = isOutlined,
                    maxLines = maxLines,
                    singleLine = false,
                    textStyle = TextStyle(
                        fontSize = fontSize.sp,
                        color = textColor,
                        fontWeight = fieldFontWeight,
                        fontFamily = fieldFontFamily
                    ),
                    keyboardOptions = keyboardOptions,
                    contentPadding = contentPadding,
                    enabled = isEnabled
                )
            }
        }

        // ── Helpers ──

        /** TextView-specific attributes this component applies (see UnappliedAttributes). */
        private val APPLIED: Set<String> = setOf(
            "text", "hint", "placeholder", "enabled", "fontSize", "fontColor",
            "highlightBackground", "containerInset", "keyboardType", "input",
            "returnKeyType", "onTextChange", "hintLineHeightMultiple",
            "hintFontSize", "hintColor", "hintAttributes", "flexible",
            "font", "fontFamily", "hintFont"
        )

        /**
         * Two-way write-back target: canonically a single FLAT identifier
         * (no dots, brackets, `??` or `!`) — kept flat by design so the
         * updateData key matches the ViewModel property. A stray `??`
         * default is stripped defensively (whitespace-insensitive).
         */
        private fun extractBindingVariable(text: String): String? {
            val pattern = "@\\{([^}]+)\\}".toRegex()
            val match = pattern.find(text) ?: return null
            return match.groupValues[1].split("??")[0].trim()
        }

        private fun hasMarginAttributes(json: JsonObject): Boolean {
            return TypedAttrs.rawKey(json, "margins") != null || TypedAttrs.rawKey(json, "topMargin") != null || TypedAttrs.rawKey(json, "bottomMargin") != null ||
                    TypedAttrs.rawKey(json, "leftMargin") != null || TypedAttrs.rawKey(json, "rightMargin") != null ||
                    TypedAttrs.rawKey(json, "startMargin") != null || TypedAttrs.rawKey(json, "endMargin") != null ||
                    TypedAttrs.rawKey(json, "marginTop") != null || TypedAttrs.rawKey(json, "marginBottom") != null ||
                    TypedAttrs.rawKey(json, "marginLeft") != null || TypedAttrs.rawKey(json, "marginRight") != null ||
                    TypedAttrs.rawKey(json, "marginStart") != null || TypedAttrs.rawKey(json, "marginEnd") != null
        }

        /**
         * Build size modifier for TextView.
         * Default: fillMaxWidth + 120dp height (matching textview_component.rb).
         * When flexible=true, uses heightIn(min, max) for auto-expanding height.
         */
        private fun buildTextViewSizeModifier(json: JsonObject, a: TextViewAttributes): Modifier {
            var modifier: Modifier = Modifier

            // Width (default fillMaxWidth) — declared as a dimension union
            // (number | keyword string), wider than the string shape this
            // component inspects, so read raw (see TypedAttrs.rawKey)
            val widthElement = TypedAttrs.rawKey(json, "width")
            modifier = when {
                widthElement == null -> modifier.fillMaxWidth()
                widthElement.isJsonPrimitive && widthElement.asJsonPrimitive.isString -> {
                    when (widthElement.asString) {
                        "matchParent", "match_parent" -> modifier.fillMaxWidth()
                        "wrapContent", "wrap_content" -> modifier
                        else -> {
                            widthElement.asString.toFloatOrNull()?.let {
                                modifier.width(it.dp)
                            } ?: modifier.fillMaxWidth()
                        }
                    }
                }
                // A declared numeric width was silently swallowed by the
                // final else and rendered fillMaxWidth — declaration-faithful
                // fix (parity family F5: every TextView fixture with
                // width: 200 rendered full-bleed on dynamic only). Mirrors
                // the height branch below.
                widthElement.isJsonPrimitive && widthElement.asJsonPrimitive.isNumber -> {
                    modifier.width(widthElement.asFloat.dp)
                }
                else -> modifier.fillMaxWidth()
            }

            // flexible: auto-expand height between minHeight and maxHeight
            // (legacy only honored the literal boolean form)
            val isFlexible = a.flexible ?: false

            if (isFlexible) {
                // A declared numeric height is the growth floor when
                // minHeight is absent (codegen: `minHeight || height || 24`).
                val declaredHeight = TypedAttrs.rawKey(json, "height")
                    ?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isNumber }
                    ?.asFloat
                val minH = TypedAttrs.static(a.common.minHeight)?.toFloat()
                    ?: declaredHeight ?: 24f
                val maxH = TypedAttrs.static(a.common.maxHeight)?.toFloat()
                modifier = if (maxH != null) {
                    modifier.heightIn(min = minH.dp, max = maxH.dp)
                } else {
                    modifier.heightIn(min = minH.dp)
                }
                return modifier
            }

            // Height (default 120dp) — declared as a dimension union
            // (number | keyword string), wider than the declared type, so
            // read raw (see TypedAttrs.rawKey)
            val heightElement = TypedAttrs.rawKey(json, "height")
            modifier = when {
                heightElement == null -> modifier.height(120.dp)
                heightElement.isJsonPrimitive && heightElement.asJsonPrimitive.isString -> {
                    when (heightElement.asString) {
                        "matchParent", "match_parent" -> modifier.fillMaxHeight()
                        "wrapContent", "wrap_content" -> modifier.wrapContentHeight()
                        else -> {
                            heightElement.asString.toFloatOrNull()?.let {
                                modifier.height(it.dp)
                            } ?: modifier.height(120.dp)
                        }
                    }
                }
                heightElement.isJsonPrimitive && heightElement.asJsonPrimitive.isNumber -> {
                    modifier.height(heightElement.asFloat.dp)
                }
                else -> modifier.height(120.dp)
            }

            return modifier
        }

        @Composable
        private fun buildPlaceholder(
            a: TextViewAttributes,
            text: String,
            declaredFontSize: Int?,
            hintColor: androidx.compose.ui.graphics.Color? = null
        ): @Composable () -> Unit {
            // The nested bag wins over every flat hint* spelling — the same
            // cascade the codegen placeholder reads
            // (textview_component.rb#build_placeholder `hint_attr(...) || flat`).
            val hintLineHeightMultiple =
                ResourceResolver.nestedNumber(a.hintAttributes, "lineHeightMultiple")?.toFloat()
                    ?: a.hintLineHeightMultiple?.toFloat()
            val hintFontSize = ResourceResolver.nestedNumber(a.hintAttributes, "fontSize")?.toFloat()
                ?: a.hintFontSize?.toFloat()
            // `hintFont` carries the placeholder's WEIGHT spelling, exactly as
            // the codegen placeholder reads it
            // (textview_component.rb:37 `hint_attr('font') || hintFont`,
            // resolved through the same weight table). The row was parsed and
            // never read here (34: `TextView/hintFont` pixel-identical to its
            // control).
            val hintFontWeight = ResourceResolver.fontWeightFor(
                ResourceResolver.nestedString(a.hintAttributes, "font") ?: a.hintFont
            )

            return {
                // Derive from LocalTextStyle — a bare TextStyle() discards the
                // M3 placeholder typography (bodyLarge) and drops to the raw
                // 14sp default, the same theme-destruction bug the Label
                // edgeInset ruling fixed. Only declared fields override.
                // The lineHeight multiple resolves against the hint's own
                // rendered size (declared hintFontSize, else the inherited
                // style size) — the codegen placeholder does the same, and
                // the old TextView-fontSize basis measurably desynced them.
                val base = LocalTextStyle.current
                val effectiveSize = hintFontSize?.sp ?: base.fontSize
                // Multiple basis: hint size, else the field's DECLARED size,
                // else the inherited M3 size — the exact chain the codegen
                // placeholder resolves at generation time.
                val multipleBasis = hintFontSize
                    ?: declaredFontSize?.toFloat()
                    ?: (if (base.fontSize != androidx.compose.ui.unit.TextUnit.Unspecified) base.fontSize.value else 16f)
                val lineHeight = if (hintLineHeightMultiple != null) {
                    (multipleBasis * hintLineHeightMultiple).sp
                } else base.lineHeight
                Text(
                    text = text,
                    style = base.copy(
                        color = hintColor ?: base.color,
                        fontSize = effectiveSize,
                        fontWeight = hintFontWeight ?: base.fontWeight,
                        lineHeight = lineHeight
                    )
                )
            }
        }

        private fun buildKeyboardOptions(a: TextViewAttributes): KeyboardOptions {
            val keyboardType = TypedAttrs.enumString(a.keyboardType) { it.json }?.let { type ->
                when (type.lowercase()) {
                    "email" -> KeyboardType.Email
                    "number" -> KeyboardType.Number
                    "decimal" -> KeyboardType.Decimal
                    "phone" -> KeyboardType.Phone
                    "url" -> KeyboardType.Uri
                    else -> KeyboardType.Text
                }
            } ?: TypedAttrs.enumString(a.input) { it.json }?.let { input ->
                when (input.lowercase()) {
                    "email" -> KeyboardType.Email
                    "number" -> KeyboardType.Number
                    "decimal" -> KeyboardType.Decimal
                    "phone" -> KeyboardType.Phone
                    "url" -> KeyboardType.Uri
                    else -> KeyboardType.Text
                }
            } ?: KeyboardType.Text

            val imeAction = when (TypedAttrs.enumString(a.returnKeyType) { it.json }) {
                "Done" -> ImeAction.Done
                "Next" -> ImeAction.Next
                else -> ImeAction.Default
            }

            return KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction)
        }

        private fun buildContainerInset(inset: Any?): PaddingValues? {
            return when {
                inset is List<*> && inset.all { it is Number } -> {
                    val arr = inset.map { (it as Number).toFloat() }
                    when (arr.size) {
                        4 -> PaddingValues(
                            top = arr[0].dp,
                            end = arr[1].dp,
                            bottom = arr[2].dp,
                            start = arr[3].dp
                        )
                        2 -> PaddingValues(
                            vertical = arr[0].dp,
                            horizontal = arr[1].dp
                        )
                        else -> null
                    }
                }
                inset is Number -> PaddingValues(inset.toFloat().dp)
                else -> null
            }
        }
    }
}
