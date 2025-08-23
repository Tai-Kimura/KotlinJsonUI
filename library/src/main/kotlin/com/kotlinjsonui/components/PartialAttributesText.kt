package com.kotlinjsonui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.kotlinjsonui.core.Configuration

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

/**
 * PartialAttributesText with linkable support.
 * When linkable is true, automatically detects URLs, emails, and phone numbers and makes them clickable.
 */
@Composable
fun PartialAttributesText(
    text: String,
    partialAttributes: List<PartialAttribute> = emptyList(),
    linkable: Boolean = false,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current
) {
    if (linkable) {
        LinkablePartialAttributesText(
            text = text,
            partialAttributes = partialAttributes,
            modifier = modifier,
            style = style
        )
    } else {
        PartialAttributesTextImpl(
            text = text,
            partialAttributes = partialAttributes,
            modifier = modifier,
            style = style
        )
    }
}

@Composable
private fun PartialAttributesTextImpl(
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

@Composable
private fun LinkablePartialAttributesText(
    text: String,
    partialAttributes: List<PartialAttribute>,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current
) {
    val context = LocalContext.current
    
    // Define patterns for linkable content
    val urlPattern = """https?://[^\s]+""".toRegex()
    val emailPattern = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}""".toRegex()
    val phonePattern = """\b\d{3}[-.]?\d{3}[-.]?\d{4}\b""".toRegex()
    
    val annotatedString = buildAnnotatedString {
        append(text)
        
        // First, apply all partial attributes
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
                            tag = "PARTIAL_CLICKABLE",
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
        
        // Then, detect and annotate URLs
        urlPattern.findAll(text).forEach { match ->
            addStringAnnotation(
                tag = "URL",
                annotation = match.value,
                start = match.range.first,
                end = match.range.last + 1
            )
            addStyle(
                style = SpanStyle(
                    color = Configuration.Colors.linkColor,
                    textDecoration = TextDecoration.Underline
                ),
                start = match.range.first,
                end = match.range.last + 1
            )
        }
        
        // Detect and annotate emails
        emailPattern.findAll(text).forEach { match ->
            addStringAnnotation(
                tag = "EMAIL",
                annotation = match.value,
                start = match.range.first,
                end = match.range.last + 1
            )
            addStyle(
                style = SpanStyle(
                    color = Configuration.Colors.linkColor,
                    textDecoration = TextDecoration.Underline
                ),
                start = match.range.first,
                end = match.range.last + 1
            )
        }
        
        // Detect and annotate phone numbers
        phonePattern.findAll(text).forEach { match ->
            addStringAnnotation(
                tag = "PHONE",
                annotation = match.value,
                start = match.range.first,
                end = match.range.last + 1
            )
            addStyle(
                style = SpanStyle(
                    color = Configuration.Colors.linkColor,
                    textDecoration = TextDecoration.Underline
                ),
                start = match.range.first,
                end = match.range.last + 1
            )
        }
    }
    
    ClickableText(
        text = annotatedString,
        modifier = modifier,
        style = style,
        onClick = { offset ->
            // First check for partial attribute clicks
            annotatedString.getStringAnnotations("PARTIAL_CLICKABLE", offset, offset)
                .firstOrNull()?.let { annotation ->
                    // Get the index and call the corresponding onClick
                    val index = annotation.item.toIntOrNull()
                    if (index != null && index < partialAttributes.size) {
                        partialAttributes[index].onClick?.invoke()
                        return@ClickableText
                    }
                }
            
            // Then check for URL clicks
            annotatedString.getStringAnnotations("URL", offset, offset)
                .firstOrNull()?.let { annotation ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(annotation.item))
                    context.startActivity(intent)
                    return@ClickableText
                }
            
            // Check for email clicks
            annotatedString.getStringAnnotations("EMAIL", offset, offset)
                .firstOrNull()?.let { annotation ->
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:${annotation.item}")
                    }
                    context.startActivity(intent)
                    return@ClickableText
                }
            
            // Check for phone clicks
            annotatedString.getStringAnnotations("PHONE", offset, offset)
                .firstOrNull()?.let { annotation ->
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${annotation.item}")
                    }
                    context.startActivity(intent)
                    return@ClickableText
                }
        }
    )
}