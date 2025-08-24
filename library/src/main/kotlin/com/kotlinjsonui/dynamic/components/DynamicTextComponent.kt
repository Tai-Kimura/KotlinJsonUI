package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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

/**
 * Dynamic Text Component Converter
 * Converts JSON to Text/Label composable at runtime
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
                try { Color(android.graphics.Color.parseColor(it)) } 
                catch (e: Exception) { Color.Black }
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
                    TextDecoration.combine(listOf(TextDecoration.Underline, TextDecoration.LineThrough))
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
                try {
                    style = style.copy(color = Color(android.graphics.Color.parseColor(colorStr)))
                } catch (e: Exception) {
                    // Keep default color
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
            var modifier: Modifier = Modifier
            
            // Width and height
            json.get("width")?.asFloat?.let { width ->
                modifier = if (width < 0) {
                    modifier.fillMaxWidth()
                } else {
                    modifier.width(width.dp)
                }
            }
            
            json.get("height")?.asFloat?.let { height ->
                modifier = modifier.height(height.dp)
            }
            
            // Margins (outside spacing) - applied first
            modifier = applyMargins(modifier, json)
            
            // Padding (inside spacing) - applied after margins
            modifier = applyPadding(modifier, json)
            
            return modifier
        }
        
        private fun applyPadding(inputModifier: Modifier, json: JsonObject): Modifier {
            var modifier = inputModifier
            // Handle edgeInset for text-specific padding (priority)
            json.get("edgeInset")?.let { edgeInset ->
                return when {
                    edgeInset.isJsonArray -> {
                        val array = edgeInset.asJsonArray
                        when (array.size()) {
                            4 -> modifier.padding(
                                top = array[0].asFloat.dp,
                                end = array[1].asFloat.dp,
                                bottom = array[2].asFloat.dp,
                                start = array[3].asFloat.dp
                            )
                            else -> modifier
                        }
                    }
                    edgeInset.isJsonPrimitive -> modifier.padding(edgeInset.asFloat.dp)
                    else -> modifier
                }
            }
            
            // Handle paddings array
            json.get("paddings")?.asJsonArray?.let { paddings ->
                return when (paddings.size()) {
                    1 -> modifier.padding(paddings[0].asFloat.dp)
                    2 -> modifier.padding(
                        vertical = paddings[0].asFloat.dp,
                        horizontal = paddings[1].asFloat.dp
                    )
                    4 -> modifier.padding(
                        top = paddings[0].asFloat.dp,
                        end = paddings[1].asFloat.dp,
                        bottom = paddings[2].asFloat.dp,
                        start = paddings[3].asFloat.dp
                    )
                    else -> modifier
                }
            }
            
            // Handle single padding value
            json.get("padding")?.asFloat?.let { padding ->
                return modifier.padding(padding.dp)
            }
            
            // Handle individual padding properties
            val paddingTop = json.get("paddingTop")?.asFloat ?: json.get("paddingVertical")?.asFloat ?: 0f
            val paddingBottom = json.get("paddingBottom")?.asFloat ?: json.get("paddingVertical")?.asFloat ?: 0f
            val paddingStart = json.get("paddingStart")?.asFloat ?: json.get("paddingLeft")?.asFloat ?: json.get("paddingHorizontal")?.asFloat ?: 0f
            val paddingEnd = json.get("paddingEnd")?.asFloat ?: json.get("paddingRight")?.asFloat ?: json.get("paddingHorizontal")?.asFloat ?: 0f
            
            return if (paddingTop > 0 || paddingBottom > 0 || paddingStart > 0 || paddingEnd > 0) {
                modifier.padding(
                    top = paddingTop.dp,
                    bottom = paddingBottom.dp,
                    start = paddingStart.dp,
                    end = paddingEnd.dp
                )
            } else {
                modifier
            }
        }
        
        private fun applyMargins(inputModifier: Modifier, json: JsonObject): Modifier {
            var modifier = inputModifier
            // Handle margins array
            json.get("margins")?.asJsonArray?.let { margins ->
                return when (margins.size()) {
                    1 -> modifier.padding(margins[0].asFloat.dp)
                    2 -> modifier.padding(
                        vertical = margins[0].asFloat.dp,
                        horizontal = margins[1].asFloat.dp
                    )
                    4 -> modifier.padding(
                        top = margins[0].asFloat.dp,
                        end = margins[1].asFloat.dp,
                        bottom = margins[2].asFloat.dp,
                        start = margins[3].asFloat.dp
                    )
                    else -> modifier
                }
            }
            
            // Handle individual margin properties
            val topMargin = json.get("topMargin")?.asFloat ?: 0f
            val bottomMargin = json.get("bottomMargin")?.asFloat ?: 0f
            val leftMargin = json.get("leftMargin")?.asFloat ?: json.get("startMargin")?.asFloat ?: 0f
            val rightMargin = json.get("rightMargin")?.asFloat ?: json.get("endMargin")?.asFloat ?: 0f
            
            return if (topMargin > 0 || bottomMargin > 0 || leftMargin > 0 || rightMargin > 0) {
                modifier.padding(
                    top = topMargin.dp,
                    bottom = bottomMargin.dp,
                    start = leftMargin.dp,
                    end = rightMargin.dp
                )
            } else {
                modifier
            }
        }
    }
}