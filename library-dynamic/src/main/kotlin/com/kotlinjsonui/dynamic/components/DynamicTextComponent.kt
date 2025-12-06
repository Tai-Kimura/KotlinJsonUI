package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.JsonObject
import com.kotlinjsonui.components.PartialAttribute
import com.kotlinjsonui.components.PartialAttributesText
import com.kotlinjsonui.dynamic.processDataBinding
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder

/**
 * Dynamic Text/Label Component Converter
 * Converts JSON to Text/Label composable at runtime
 *
 * NOTE: Label is the primary component name in JsonUI.
 * Text is supported as an alias for backward compatibility.
 * Both "type": "Label" and "type": "Text" work identically.
 *
 * Supported JSON attributes (matching Ruby implementation):
 * - text: String content (supports @{variable} binding)
 * - fontSize: Int (default 14)
 * - fontColor: String hex color
 * - font: "bold" for bold text
 * - fontWeight: "thin", "light", "normal", "medium", "semibold", "bold", etc.
 * - textAlign: "center", "left", "right"
 * - centerHorizontal: Boolean (alternative to textAlign: center)
 * - underline: Boolean
 * - strikethrough: Boolean
 * - lines: Int (0 for unlimited, default unlimited)
 * - lineBreakMode: "clip", "tail", "word"
 * - edgeInset: Number or Array[4] for text padding
 * - padding/paddings: Number or Array for general padding
 * - margins: Array[1,2,4] or individual margin properties
 * - width/height: Number (-1 for fillMaxWidth)
 * - partialAttributes: Array of partial text attributes
 * - linkable: Boolean to make URLs/emails/phones clickable
 */
class DynamicTextComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {

            // Parse text with data binding support
            val rawText = json.get("text")?.asString ?: ""
            val text = processDataBinding(rawText, data)

            // Check for partialAttributes or linkable
            val hasPartialAttributes = json.get("partialAttributes")?.isJsonArray == true
            val isLinkable = json.get("linkable")?.asBoolean == true

            if (hasPartialAttributes || isLinkable) {
                // Use PartialAttributesText for partial attributes or linkable text
                createPartialAttributesText(json, text, data)
            } else {
                // Use standard Text composable
                createStandardText(json, text)
            }
        }

        @Composable
        private fun createPartialAttributesText(
            json: JsonObject,
            text: String,
            data: Map<String, Any>
        ) {
            val partialAttributes = mutableListOf<PartialAttribute>()

            // Parse partial attributes
            json.get("partialAttributes")?.asJsonArray?.forEach { attrElement ->
                if (attrElement.isJsonObject) {
                    val attr = attrElement.asJsonObject
                    val range = when {
                        attr.get("range")?.isJsonArray == true -> {
                            val rangeArray = attr.get("range").asJsonArray
                            if (rangeArray.size() == 2) {
                                listOf(rangeArray[0].asInt, rangeArray[1].asInt)
                            } else null
                        }

                        attr.get("range")?.isJsonPrimitive == true -> {
                            attr.get("range").asString
                        }

                        else -> null
                    }

                    range?.let {
                        val partialAttr = PartialAttribute.fromJsonRange(
                            range = it,
                            text = text,
                            fontColor = attr.get("fontColor")?.asString,
                            fontSize = attr.get("fontSize")?.asInt,
                            fontWeight = attr.get("fontWeight")?.asString,
                            background = attr.get("background")?.asString,
                            underline = attr.get("underline")?.asBoolean ?: false,
                            strikethrough = attr.get("strikethrough")?.asBoolean ?: false,
                            onClick = attr.get("onclick")?.asString?.let { methodName ->
                                // Get the function from the data map
                                val handler = data[methodName]
                                if (handler is Function<*>) {
                                    {
                                        try {
                                            @Suppress("UNCHECKED_CAST")
                                            (handler as () -> Unit)()
                                        } catch (e: Exception) {
                                            // Handler doesn't match expected signature
                                        }
                                    }
                                } else {
                                    null
                                }
                            }
                        )
                        partialAttr?.let { partialAttributes.add(it) }
                    }
                }
            }

            // Build text style
            val style = buildTextStyle(json)

            // Build modifier
            val modifier = buildModifier(json)

            PartialAttributesText(
                text = text,
                partialAttributes = partialAttributes,
                linkable = json.get("linkable")?.asBoolean ?: false,
                modifier = modifier,
                style = style
            )
        }

        @Composable
        private fun createStandardText(json: JsonObject, text: String) {
            // Font size
            val fontSize = json.get("fontSize")?.asInt ?: 14

            // Font color (official attribute)
            val fontColor = json.get("fontColor")?.asString?.let {
                ColorParser.parseColorString(it)
            } ?: Color.Black

            // Font weight - handle both 'font' and 'fontWeight' attributes
            val fontWeight = when {
                json.get("font")?.asString == "bold" -> FontWeight.Bold
                json.get("fontWeight")?.asString != null -> {
                    when (json.get("fontWeight")?.asString?.lowercase()) {
                        "thin" -> FontWeight.Thin
                        "extralight" -> FontWeight.ExtraLight
                        "light" -> FontWeight.Light
                        "normal" -> FontWeight.Normal
                        "medium" -> FontWeight.Medium
                        "semibold" -> FontWeight.SemiBold
                        "bold" -> FontWeight.Bold
                        "extrabold" -> FontWeight.ExtraBold
                        "black" -> FontWeight.Black
                        else -> FontWeight.Normal
                    }
                }

                else -> FontWeight.Normal
            }

            // Text alignment
            val textAlign = when {
                json.get("textAlign")?.asString != null -> {
                    when (json.get("textAlign")?.asString?.lowercase()) {
                        "center" -> TextAlign.Center
                        "right" -> TextAlign.End
                        "left" -> TextAlign.Start
                        else -> TextAlign.Start
                    }
                }

                json.get("centerHorizontal")?.asBoolean == true -> TextAlign.Center
                else -> TextAlign.Start
            }

            // Text decorations
            val textDecoration = when {
                json.get("underline")?.asBoolean == true && json.get("strikethrough")?.asBoolean == true ->
                    TextDecoration.combine(
                        listOf(
                            TextDecoration.Underline,
                            TextDecoration.LineThrough
                        )
                    )

                json.get("underline")?.asBoolean == true -> TextDecoration.Underline
                json.get("strikethrough")?.asBoolean == true -> TextDecoration.LineThrough
                else -> TextDecoration.None
            }

            // Lines (maxLines)
            val maxLines = when {
                json.get("lines")?.asInt == 0 -> Int.MAX_VALUE
                json.get("lines")?.asInt != null -> json.get("lines").asInt
                else -> Int.MAX_VALUE
            }

            // Line break mode (overflow)
            val overflow = when (json.get("lineBreakMode")?.asString?.lowercase()) {
                "clip" -> TextOverflow.Clip
                "tail", "word" -> TextOverflow.Ellipsis
                else -> TextOverflow.Clip
            }

            // Build modifier
            val modifier = buildModifier(json)

            Text(
                text = text,
                fontSize = fontSize.sp,
                color = fontColor,
                fontWeight = fontWeight,
                textAlign = textAlign,
                textDecoration = textDecoration,
                maxLines = maxLines,
                overflow = overflow,
                modifier = modifier
            )
        }

        @Composable
        private fun buildTextStyle(json: JsonObject): TextStyle {
            var style = LocalTextStyle.current

            json.get("fontSize")?.asInt?.let { fontSize ->
                style = style.copy(fontSize = fontSize.sp)
            }

            json.get("fontColor")?.asString?.let { colorStr ->
                ColorParser.parseColorString(colorStr)?.let { color ->
                    style = style.copy(color = color)
                }
            }

            // Font weight
            val fontWeight = when {
                json.get("font")?.asString == "bold" -> FontWeight.Bold
                json.get("fontWeight")?.asString != null -> {
                    when (json.get("fontWeight")?.asString?.lowercase()) {
                        "thin" -> FontWeight.Thin
                        "extralight" -> FontWeight.ExtraLight
                        "light" -> FontWeight.Light
                        "normal" -> FontWeight.Normal
                        "medium" -> FontWeight.Medium
                        "semibold" -> FontWeight.SemiBold
                        "bold" -> FontWeight.Bold
                        "extrabold" -> FontWeight.ExtraBold
                        "black" -> FontWeight.Black
                        else -> null
                    }
                }

                else -> null
            }
            fontWeight?.let { style = style.copy(fontWeight = it) }

            // Line height
            json.get("lineHeight")?.asFloat?.let { lineHeight ->
                style = style.copy(lineHeight = lineHeight.sp)
            }

            // Letter spacing
            json.get("letterSpacing")?.asFloat?.let { letterSpacing ->
                style = style.copy(letterSpacing = letterSpacing.sp)
            }

            // Text alignment
            val textAlign = when {
                json.get("textAlign")?.asString != null -> {
                    when (json.get("textAlign")?.asString?.lowercase()) {
                        "center" -> TextAlign.Center
                        "right" -> TextAlign.End
                        "left" -> TextAlign.Start
                        else -> null
                    }
                }

                json.get("centerHorizontal")?.asBoolean == true -> TextAlign.Center
                else -> null
            }
            textAlign?.let { style = style.copy(textAlign = it) }

            return style
        }

        private fun buildModifier(json: JsonObject): Modifier {
            // Start with base modifier
            var modifier: Modifier = Modifier

            // 1. Apply size first (includes padding in the total size)
            modifier = ModifierBuilder.applySize(modifier, json)

            // 2. Apply margins (outer spacing)
            modifier = ModifierBuilder.applyMargins(modifier, json)

            // 3. Corner radius (clip) - apply before background for proper rendering
            json.get("cornerRadius")?.asFloat?.let { radius ->
                val shape = RoundedCornerShape(radius.dp)
                modifier = modifier.clip(shape)
            }

            // 4. Background color
            json.get("background")?.asString?.let { colorStr ->
                ColorParser.parseColorString(colorStr)?.let { color ->
                    modifier = modifier.background(color)
                }
            }

            // 5. Border (after background)
            json.get("borderColor")?.asString?.let { borderColorStr ->
                ColorParser.parseColorString(borderColorStr)?.let { borderColor ->
                    val borderWidth = json.get("borderWidth")?.asFloat ?: 1f
                    val shape = json.get("cornerRadius")?.asFloat?.let {
                        RoundedCornerShape(it.dp)
                    }
                    if (shape != null) {
                        modifier = modifier.border(borderWidth.dp, borderColor, shape)
                    } else {
                        modifier = modifier.border(borderWidth.dp, borderColor)
                    }
                }
            }

            // 6. Shadow/elevation
            json.get("shadow")?.let { shadow ->
                when {
                    shadow.isJsonPrimitive -> {
                        val primitive = shadow.asJsonPrimitive
                        when {
                            primitive.isBoolean && primitive.asBoolean -> {
                                modifier = modifier.shadow(6.dp)
                            }
                            primitive.isNumber -> {
                                modifier = modifier.shadow(primitive.asFloat.dp)
                            }
                        }
                    }
                }
            }

            // 7. Apply padding (inner spacing)
            modifier = ModifierBuilder.applyPadding(modifier, json)

            // 8. Apply opacity last
            modifier = ModifierBuilder.applyOpacity(modifier, json)

            return modifier
        }
    }
}
