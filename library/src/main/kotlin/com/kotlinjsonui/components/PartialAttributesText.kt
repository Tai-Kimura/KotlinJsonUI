package com.kotlinjsonui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.LinkAnnotation
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
    val context = LocalContext.current
    val annotatedString = buildAnnotatedString {
        append(text)

        partialAttributes.forEach { attr ->
            val start = attr.startIndex
            val end = attr.endIndex

            // Validate range
            if (start >= 0 && end <= text.length && start < end) {

                // Build SpanStyle
                val spanStyle = SpanStyle(
                    color = attr.fontColor?.let { resolveColorString(it, context) } ?: style.color,
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
                    background = attr.background?.let { resolveColorString(it, context) } ?: Color.Transparent,
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

                // Attach the click handler directly; styling stays on the
                // SpanStyles above (styles = null avoids double styling).
                attr.onClick?.let { onClick ->
                    try {
                        addLink(
                            LinkAnnotation.Clickable(
                                tag = "CLICKABLE",
                                styles = null,
                                linkInteractionListener = { onClick() }
                            ),
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

    Text(
        text = annotatedString,
        modifier = modifier,
        style = style
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
                    color = attr.fontColor?.let { resolveColorString(it, context) } ?: style.color,
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
                    background = attr.background?.let { resolveColorString(it, context) } ?: Color.Transparent,
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

                // Attach the click handler directly (see PartialAttributesTextImpl)
                attr.onClick?.let { onClick ->
                    try {
                        addLink(
                            LinkAnnotation.Clickable(
                                tag = "PARTIAL_CLICKABLE",
                                styles = null,
                                linkInteractionListener = { onClick() }
                            ),
                            start = start,
                            end = end
                        )
                    } catch (e: Exception) {
                        // Handle out of bounds
                    }
                }
            }
        }

        // Then, detect and link URLs. LinkAnnotation.Url with no listener opens
        // the URL via the platform default handler (ACTION_VIEW equivalent).
        urlPattern.findAll(text).forEach { match ->
            addLink(
                LinkAnnotation.Url(match.value),
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

        // Detect and link emails
        emailPattern.findAll(text).forEach { match ->
            addLink(
                LinkAnnotation.Clickable(
                    tag = "EMAIL",
                    styles = null,
                    linkInteractionListener = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:${match.value}")
                        }
                        context.startActivity(intent)
                    }
                ),
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

        // Detect and link phone numbers
        phonePattern.findAll(text).forEach { match ->
            addLink(
                LinkAnnotation.Clickable(
                    tag = "PHONE",
                    styles = null,
                    linkInteractionListener = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${match.value}")
                        }
                        context.startActivity(intent)
                    }
                ),
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

    Text(
        text = annotatedString,
        modifier = modifier,
        style = style
    )
}

/**
 * Resolve a color string to a Color.
 * Tries Android color resource by name first, then hex parsing, then fallback.
 */
private fun resolveColorString(colorString: String, context: android.content.Context): Color? {
    // 1. Try to resolve as Android color resource (e.g., "gold" -> R.color.gold)
    try {
        val resId = context.resources.getIdentifier(colorString, "color", context.packageName)
        if (resId != 0) {
            val androidColor = androidx.core.content.ContextCompat.getColor(context, resId)
            return Color(androidColor)
        }
    } catch (_: Exception) {}

    // 2. Try to parse as hex color (e.g., "#FFD700")
    try {
        return Color(android.graphics.Color.parseColor(colorString))
    } catch (_: Exception) {}

    return null
}
