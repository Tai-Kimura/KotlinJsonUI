package com.kotlinjsonui.dynamic.components

import android.content.Context
import androidx.compose.material3.Text
import androidx.compose.material3.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.JsonObject
import com.kotlinjsonui.components.PartialAttribute
import com.kotlinjsonui.components.PartialAttributesText
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ResourceResolver

/**
 * Text/Label component → Text / PartialAttributesText.
 * Reference: text_component.rb in kjui_tools.
 *
 * NOTE: Label is the primary component name in JsonUI.
 * Text is supported as an alias for backward compatibility.
 * Both "type": "Label" and "type": "Text" work identically.
 */
class DynamicTextComponent {
    companion object {
        @Composable
        fun create(json: JsonObject, data: Map<String, Any> = emptyMap()) {
            val context = LocalContext.current

            // Resolve text with binding + resource support
            val text = ResourceResolver.resolveText(json, "text", data, context)

            // Check for partialAttributes or linkable
            val hasPartialAttributes = json.get("partialAttributes")?.isJsonArray == true &&
                    json.get("partialAttributes").asJsonArray.size() > 0
            val isLinkable = json.get("linkable")?.asBoolean == true

            when {
                hasPartialAttributes -> createPartialAttributesText(json, text, data, context)
                isLinkable -> createLinkableText(json, text, data, context)
                else -> createStandardText(json, text, data, context)
            }
        }

        // ── Standard Text ──

        @Composable
        private fun createStandardText(
            json: JsonObject,
            text: String,
            data: Map<String, Any>,
            context: Context
        ) {
            // Font size
            val fontSize = json.get("fontSize")?.asFloat

            // Font color (supports @{binding})
            val fontColor = ColorParser.parseColorWithBinding(json, "fontColor", data, context)

            // Font weight – handle both 'font' and 'fontWeight' attributes
            val fontWeight = resolveFontWeight(json, data)

            // Font family – custom font from 'font' attribute (if not a weight name)
            val fontFamily = resolveFontFamily(json, context, data)

            // Text decoration
            val textDecoration = resolveTextDecoration(json)

            // Text alignment
            val textAlign = resolveTextAlign(json)

            // Auto size (text shrinking)
            val useAutoSize = json.get("autoShrink")?.asBoolean == true ||
                    json.get("minimumScaleFactor") != null

            // Max lines
            val maxLines = when {
                useAutoSize -> 1
                json.get("lines")?.asInt == 0 -> Int.MAX_VALUE
                json.get("lines")?.asInt != null -> json.get("lines").asInt
                else -> Int.MAX_VALUE
            }

            // Overflow
            val overflow = when {
                useAutoSize -> TextOverflow.Ellipsis
                json.get("lines")?.asInt != null && json.get("lines").asInt > 0 -> TextOverflow.Ellipsis
                else -> when (json.get("lineBreakMode")?.asString?.lowercase()) {
                    "clip" -> TextOverflow.Clip
                    "tail", "word", "truncatetail" -> TextOverflow.Ellipsis
                    else -> TextOverflow.Clip
                }
            }

            // Build style (shadow, lineHeight)
            val style = buildTextStyle(json, fontSize)

            // Build modifier using composite builder
            var modifier = ModifierBuilder.buildModifier(json, data, context = context)

            // Handle edgeInset for text-specific padding (overrides regular padding)
            modifier = applyEdgeInset(modifier, json)

            Text(
                text = text,
                fontSize = fontSize?.sp ?: 14.sp,
                color = fontColor ?: Color.Unspecified,
                fontWeight = fontWeight,
                fontFamily = fontFamily,
                textAlign = textAlign,
                textDecoration = textDecoration,
                maxLines = maxLines,
                overflow = overflow,
                autoSize = if (useAutoSize) TextAutoSize.StepBased(
                    minFontSize = ((fontSize ?: 14f) * (json.get("minimumScaleFactor")?.asFloat ?: 0.5f)).sp
                ) else TextAutoSize.None,
                style = style,
                modifier = modifier
            )
        }

        // ── Linkable Text ──

        @Composable
        private fun createLinkableText(
            json: JsonObject,
            text: String,
            data: Map<String, Any>,
            context: Context
        ) {
            val style = buildFullTextStyle(json, data, context)
            val modifier = ModifierBuilder.buildModifier(json, data, context = context)

            PartialAttributesText(
                text = text,
                linkable = true,
                modifier = modifier,
                style = style
            )
        }

        // ── Partial Attributes Text ──

        @Composable
        private fun createPartialAttributesText(
            json: JsonObject,
            text: String,
            data: Map<String, Any>,
            context: Context
        ) {
            val partialAttributes = mutableListOf<PartialAttribute>()

            json.get("partialAttributes")?.asJsonArray?.forEach { attrElement ->
                if (attrElement.isJsonObject) {
                    val attr = attrElement.asJsonObject
                    val range = resolvePartialRange(attr, text)

                    range?.let {
                        val partialAttr = PartialAttribute.fromJsonRange(
                            range = it,
                            text = text,
                            fontColor = attr.get("fontColor")?.asString,
                            fontSize = attr.get("fontSize")?.asInt,
                            fontWeight = attr.get("fontWeight")?.asString
                                ?: attr.get("font")?.asString,
                            background = attr.get("background")?.asString,
                            underline = attr.get("underline")?.asBoolean ?: false,
                            strikethrough = attr.get("strikethrough")?.asBoolean ?: false,
                            onClick = resolvePartialClickHandler(attr, data)
                        )
                        partialAttr?.let { pa -> partialAttributes.add(pa) }
                    }
                }
            }

            val style = buildFullTextStyle(json, data, context)
            val modifier = ModifierBuilder.buildModifier(json, data, context = context)

            PartialAttributesText(
                text = text,
                partialAttributes = partialAttributes,
                modifier = modifier,
                style = style
            )
        }

        // ── Helpers ──

        private val WEIGHT_NAMES = mapOf(
            "thin" to FontWeight.Thin,
            "extralight" to FontWeight.ExtraLight,
            "light" to FontWeight.Light,
            "normal" to FontWeight.Normal,
            "medium" to FontWeight.Medium,
            "semibold" to FontWeight.SemiBold,
            "bold" to FontWeight.Bold,
            "extrabold" to FontWeight.ExtraBold,
            "heavy" to FontWeight.ExtraBold,
            "black" to FontWeight.Black
        )

        private fun resolveFontWeight(json: JsonObject, data: Map<String, Any> = emptyMap()): FontWeight? {
            // 'font' attribute: check binding first, then static value
            val fontValue = resolveStringWithBinding(json, "font", data)
            fontValue?.let { font ->
                val lower = font.lowercase()
                if (WEIGHT_NAMES.containsKey(lower)) {
                    return WEIGHT_NAMES[lower]
                }
            }
            // 'fontWeight' attribute
            val fontWeightValue = resolveStringWithBinding(json, "fontWeight", data)
            fontWeightValue?.let { fw ->
                return WEIGHT_NAMES[fw.lowercase()] ?: FontWeight.Normal
            }
            return null
        }

        private fun resolveFontFamily(json: JsonObject, context: Context, data: Map<String, Any> = emptyMap()): FontFamily? {
            // fontFamily attribute takes priority over font attribute for family resolution
            val fontFamilyValue = resolveStringWithBinding(json, "fontFamily", data)
            fontFamilyValue?.let { family ->
                val fontResName = family.replace("-", "_").replace(" ", "_").lowercase()
                val resId = context.resources.getIdentifier(
                    fontResName, "font", context.packageName
                )
                if (resId != 0) {
                    return FontFamily(Font(resId))
                }
            }

            // Fall back to font attribute (custom font family if not a weight name)
            val fontValue = resolveStringWithBinding(json, "font", data)
            fontValue?.let { font ->
                val lower = font.lowercase()
                if (!WEIGHT_NAMES.containsKey(lower)) {
                    // Custom font family – try to resolve via resource
                    val fontResName = font.replace("-", "_").lowercase()
                    val resId = context.resources.getIdentifier(
                        fontResName, "font", context.packageName
                    )
                    if (resId != 0) {
                        return FontFamily(Font(resId))
                    }
                }
            }
            return null
        }

        /**
         * Resolve a string attribute from JSON with binding support.
         * If the value is @{prop}, looks up the data map for String value.
         */
        private fun resolveStringWithBinding(json: JsonObject, key: String, data: Map<String, Any>): String? {
            val value = json.get(key)?.asString ?: return null
            if (value.startsWith("@{") && value.endsWith("}")) {
                val prop = value.drop(2).dropLast(1)
                return data[prop] as? String
            }
            return value
        }

        private fun resolveTextDecoration(json: JsonObject): TextDecoration? {
            val hasUnderline = json.get("underline")?.asBoolean == true
            val hasStrikethrough = json.get("strikethrough")?.asBoolean == true

            return when {
                hasUnderline && hasStrikethrough -> TextDecoration.combine(
                    listOf(TextDecoration.Underline, TextDecoration.LineThrough)
                )
                hasUnderline -> TextDecoration.Underline
                hasStrikethrough -> TextDecoration.LineThrough
                else -> null
            }
        }

        private fun resolveTextAlign(json: JsonObject): TextAlign? {
            json.get("textAlign")?.asString?.let { align ->
                return when (align.lowercase()) {
                    "center" -> TextAlign.Center
                    "right" -> TextAlign.End
                    "left" -> TextAlign.Start
                    else -> null
                }
            }
            if (json.get("centerHorizontal")?.asBoolean == true) {
                return TextAlign.Center
            }
            return null
        }

        /**
         * Build TextStyle for shadow and lineHeight.
         * Matches text_component.rb style generation.
         */
        private fun buildTextStyle(json: JsonObject, fontSize: Float?): TextStyle {
            var style = TextStyle()

            // Line height calculation matching Ruby implementation
            val lineHeight = when {
                json.get("lineHeightMultiple")?.asFloat != null -> {
                    val baseFontSize = fontSize ?: 14f
                    baseFontSize * json.get("lineHeightMultiple").asFloat
                }
                json.get("lineSpacing")?.asFloat != null -> {
                    val baseFontSize = fontSize ?: 14f
                    baseFontSize + json.get("lineSpacing").asFloat
                }
                fontSize != null -> {
                    // Default lineHeight = fontSize * 1.3 to match iOS compact line spacing
                    (fontSize * 1.3f)
                }
                else -> null
            }
            lineHeight?.let { style = style.copy(lineHeight = it.sp) }

            // Text shadow
            if (json.get("textShadow") != null) {
                style = style.copy(
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color.Black,
                        offset = androidx.compose.ui.geometry.Offset(2f, 2f),
                        blurRadius = 4f
                    )
                )
            }

            return style
        }

        /**
         * Build full TextStyle for PartialAttributesText (includes fontSize, color, textAlign).
         */
        private fun buildFullTextStyle(
            json: JsonObject,
            data: Map<String, Any>,
            context: Context
        ): TextStyle {
            var style = buildTextStyle(json, json.get("fontSize")?.asFloat)

            json.get("fontSize")?.asFloat?.let {
                style = style.copy(fontSize = it.sp)
            }

            ColorParser.parseColorWithBinding(json, "fontColor", data, context)?.let {
                style = style.copy(color = it)
            }

            resolveFontWeight(json, data)?.let {
                style = style.copy(fontWeight = it)
            }

            resolveFontFamily(json, context, data)?.let {
                style = style.copy(fontFamily = it)
            }

            resolveTextAlign(json)?.let {
                style = style.copy(textAlign = it)
            }

            return style
        }

        /**
         * Apply edgeInset (text-specific padding) to modifier.
         * edgeInset takes priority over regular padding for Text components.
         */
        private fun applyEdgeInset(modifier: Modifier, json: JsonObject): Modifier {
            val insets = json.get("edgeInset") ?: return modifier
            return when {
                insets.isJsonArray && insets.asJsonArray.size() == 4 -> {
                    val arr = insets.asJsonArray
                    modifier.then(
                        Modifier.padding(
                            top = arr[0].asFloat.dp,
                            end = arr[1].asFloat.dp,
                            bottom = arr[2].asFloat.dp,
                            start = arr[3].asFloat.dp
                        )
                    )
                }
                insets.isJsonPrimitive && insets.asJsonPrimitive.isNumber -> {
                    modifier.then(Modifier.padding(insets.asFloat.dp))
                }
                else -> modifier
            }
        }

        /**
         * Resolve partial attribute range: supports numeric array [start, end] or string pattern.
         */
        private fun resolvePartialRange(attr: JsonObject, text: String): Any? {
            val rangeElement = attr.get("range") ?: return null
            return when {
                rangeElement.isJsonArray -> {
                    val arr = rangeElement.asJsonArray
                    if (arr.size() == 2) {
                        // Numeric range – extract substring for localization support
                        val start = arr[0].asInt
                        val end = arr[1].asInt
                        if (start in 0..text.length && end in start..text.length) {
                            text.substring(start, end)
                        } else {
                            listOf(start, end)
                        }
                    } else null
                }
                rangeElement.isJsonPrimitive && rangeElement.asJsonPrimitive.isString -> {
                    rangeElement.asString
                }
                else -> null
            }
        }

        /**
         * Resolve click handler for partial attributes.
         * onclick (lowercase) → selector format (string only)
         * onClick (camelCase) → binding format only (@{functionName})
         */
        private fun resolvePartialClickHandler(
            attr: JsonObject,
            data: Map<String, Any>
        ): (() -> Unit)? {
            val methodName = when {
                attr.get("onclick")?.asString != null -> {
                    val value = attr.get("onclick").asString
                    if (!value.contains("@{")) value else null
                }
                attr.get("onClick")?.asString != null -> {
                    val value = attr.get("onClick").asString
                    ModifierBuilder.extractBindingProperty(value)
                }
                else -> null
            } ?: return null

            val handler = data[methodName] ?: return null
            return if (handler is Function<*>) {
                {
                    try {
                        @Suppress("UNCHECKED_CAST")
                        (handler as () -> Unit)()
                    } catch (_: Exception) {}
                }
            } else null
        }
    }
}
