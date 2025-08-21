package com.kotlinjsonui.components

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp

data class PartialAttribute(
    val startIndex: Int,
    val endIndex: Int,
    val fontColor: String? = null,
    val fontSize: Int? = null,
    val fontWeight: String? = null,
    val background: String? = null,
    val underline: Boolean = false,
    val strikethrough: Boolean = false,
    val onClick: (() -> Unit)? = null
) {
    companion object {
        /**
         * Create PartialAttribute from JSON range which can be:
         * - List<Int> like [14, 21] for index range
         * - String like "partial" for text pattern matching
         */
        fun fromJsonRange(
            range: Any,
            text: String,
            fontColor: String? = null,
            fontSize: Int? = null,
            fontWeight: String? = null,
            background: String? = null,
            underline: Boolean = false,
            strikethrough: Boolean = false,
            onClick: (() -> Unit)? = null
        ): PartialAttribute? {
            val (start, end) = when (range) {
                is List<*> -> {
                    if (range.size == 2) {
                        val startIdx = (range[0] as? Number)?.toInt() ?: return null
                        val endIdx = (range[1] as? Number)?.toInt() ?: return null
                        startIdx to endIdx
                    } else null
                }
                is String -> {
                    // Find the text pattern in the string
                    val index = text.indexOf(range)
                    if (index >= 0) {
                        index to (index + range.length)
                    } else null
                }
                else -> null
            } ?: return null
            
            return PartialAttribute(
                startIndex = start,
                endIndex = end,
                fontColor = fontColor,
                fontSize = fontSize,
                fontWeight = fontWeight,
                background = background,
                underline = underline,
                strikethrough = strikethrough,
                onClick = onClick
            )
        }
    }
}

@Composable
fun PartialAttributesText(
    text: String,
    partialAttributes: List<PartialAttribute>,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current
) {
    val annotatedString = buildAnnotatedString {
        append(text)
        
        partialAttributes.forEach { attr ->
            val start = attr.startIndex
            val end = attr.endIndex
            
            // Validate range
            if (start >= 0 && end <= text.length && start < end) {
                
                // Build SpanStyle
                val spanStyle = SpanStyle(
                    color = attr.fontColor?.let { 
                        try {
                            Color(android.graphics.Color.parseColor(it))
                        } catch (e: Exception) {
                            style.color
                        }
                    } ?: style.color,
                    fontSize = attr.fontSize?.sp ?: style.fontSize,
                    fontWeight = when (attr.fontWeight?.lowercase()) {
                        "bold" -> FontWeight.Bold
                        "semibold" -> FontWeight.SemiBold
                        "medium" -> FontWeight.Medium
                        "light" -> FontWeight.Light
                        "thin" -> FontWeight.Thin
                        "extrabold" -> FontWeight.ExtraBold
                        "black" -> FontWeight.Black
                        else -> style.fontWeight
                    },
                    background = attr.background?.let {
                        try {
                            Color(android.graphics.Color.parseColor(it))
                        } catch (e: Exception) {
                            Color.Transparent
                        }
                    } ?: Color.Transparent,
                    textDecoration = when {
                        attr.underline && attr.strikethrough -> 
                            TextDecoration.combine(listOf(TextDecoration.Underline, TextDecoration.LineThrough))
                        attr.underline -> TextDecoration.Underline
                        attr.strikethrough -> TextDecoration.LineThrough
                        else -> TextDecoration.None
                    }
                )
                
                try {
                    addStyle(spanStyle, start, end)
                } catch (e: Exception) {
                    // Handle out of bounds
                }
                
                // Add clickable annotation with index
                attr.onClick?.let {
                    try {
                        addStringAnnotation(
                            tag = "CLICKABLE",
                            annotation = partialAttributes.indexOf(attr).toString(),
                            start = start,
                            end = end
                        )
                    } catch (e: Exception) {
                        // Handle out of bounds
                    }
                }
            }
        }
    }
    
    ClickableText(
        text = annotatedString,
        modifier = modifier,
        style = style,
        onClick = { offset ->
            annotatedString.getStringAnnotations("CLICKABLE", offset, offset)
                .firstOrNull()?.let { annotation ->
                    // Get the index and call the corresponding onClick
                    val index = annotation.item.toIntOrNull()
                    if (index != null && index < partialAttributes.size) {
                        partialAttributes[index].onClick?.invoke()
                    }
                }
        }
    )
}