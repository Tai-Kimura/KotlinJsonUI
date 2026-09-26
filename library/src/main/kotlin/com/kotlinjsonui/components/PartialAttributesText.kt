package com.kotlinjsonui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.node.Ref
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.sp
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.LocalInteractionStopped
import kotlin.math.roundToInt

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
 *
 * [linksEnabled] false draws every link as it draws it — the range styles, a
 * detected link's colour and underline — and makes none of them operable:
 * no LinkAnnotation, no range hit-target. `userInteractionEnabled: false` on
 * the Label or on a view around it stops its links as it stops every tap (the
 * tap rule, jsonui-cli shared/core/tap_accessibility.rb), and a pointer
 * blocker is not enough for that: each link is a semantics node of its own
 * with an OnClick action, which TalkBack's double tap calls through the
 * blocker (measured, API 35 emulator, LinkSpanUnderOuterClickableProbe).
 * kjui's codegen passes it from the flag (jsonui-cli 1.9.0), as does the
 * dynamic Label. Inside a stop handed down at run time
 * ([LocalInteractionStopped] — a Collection's cell, an Embed's screen, a tab's
 * view under a stopping node) the links stop too, whatever is passed.
 */
@Composable
fun PartialAttributesText(
    text: String,
    partialAttributes: List<PartialAttribute> = emptyList(),
    linkable: Boolean = false,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    linksEnabled: Boolean = true
) {
    val operable = linksEnabled && !LocalInteractionStopped.current
    val ranges = if (operable) partialAttributes else partialAttributes.map { it.copy(onClick = null) }
    if (linkable) {
        LinkablePartialAttributesText(
            text = text,
            partialAttributes = ranges,
            modifier = modifier,
            style = style,
            linksEnabled = operable
        )
    } else {
        PartialAttributesTextImpl(
            text = text,
            partialAttributes = ranges,
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
                    fontWeight = parseFontWeight(attr.fontWeight) ?: style.fontWeight,
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

                // A range's handler is its hit-target's
                // (PartialTextWithRangeHitTargets), not a LinkAnnotation's.
            }
        }
    }

    PartialTextWithRangeHitTargets(
        fullText = text,
        annotatedString = annotatedString,
        partialAttributes = partialAttributes,
        modifier = modifier,
        style = style
    )
}

@Composable
private fun LinkablePartialAttributesText(
    text: String,
    partialAttributes: List<PartialAttribute>,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    linksEnabled: Boolean = true
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
                    fontWeight = parseFontWeight(attr.fontWeight) ?: style.fontWeight,
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

                // A range's handler is its hit-target's (see PartialAttributesTextImpl).
            }
        }

        // Then, detect and link URLs. LinkAnnotation.Url with no listener opens
        // the URL via the platform default handler (ACTION_VIEW equivalent).
        urlPattern.findAll(text).forEach { match ->
            if (linksEnabled) addLink(
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
            if (linksEnabled) addLink(
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
            if (linksEnabled) addLink(
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

    PartialTextWithRangeHitTargets(
        fullText = text,
        annotatedString = annotatedString,
        partialAttributes = partialAttributes,
        modifier = modifier,
        style = style
    )
}

/**
 * Renders the annotated text and, for every clickable partial attribute, an
 * invisible hit-target sized to the range's real glyph rectangle.
 *
 * Why: UI tests — and TalkBack users outside the links menu — target a range
 * by its text. Each target carries the range's text as contentDescription and
 * fires the range's onClick, so `tap { id, text }` in jsonui-test (its Android
 * driver finds `By.desc(rangeText).clickable(true)`) resolves to the actual
 * glyph rect instead of a proportional estimate. iOS exposes the same range as
 * a real a11y link element.
 *
 * The target is the range's ONLY tappable: the range adds no LinkAnnotation.
 * It did, and Compose now exposes a LinkAnnotation as a semantics node of its
 * own, so a range was two TalkBack nodes with the same action; and Material 3's
 * Text drew the LinkAnnotation in the theme's primary colour over the range's
 * own style, so a declared fontColor lost (both measured on an API 35
 * emulator; 4f rulings, jsonui-cli 1.9.0). Without it, a range draws its
 * SpanStyle — its declaration — operable or stopped alike.
 *
 * The root [Layout] carries the caller's [modifier] (testTag / size / weight
 * semantics stay on one node, geometry identical to Text(modifier)); the text
 * is measured with the incoming constraints and the layout adopts its size,
 * so wrapContent / matchParent / centered labels behave exactly as before.
 * A range wrapped across lines gets one union rect (path bounds).
 */
@Composable
private fun PartialTextWithRangeHitTargets(
    fullText: String,
    annotatedString: AnnotatedString,
    partialAttributes: List<PartialAttribute>,
    modifier: Modifier,
    style: TextStyle
) {
    val clickableAttrs = partialAttributes.filter { attr ->
        attr.onClick != null &&
            attr.startIndex >= 0 && attr.endIndex <= fullText.length && attr.startIndex < attr.endIndex
    }

    if (clickableAttrs.isEmpty()) {
        Text(text = annotatedString, modifier = modifier, style = style)
        return
    }

    // Plain holder, not state: onTextLayout fires during the Text's measure,
    // strictly before this Layout's measure block reads it in the same pass.
    val layoutHolder = remember { Ref<TextLayoutResult>() }

    Layout(
        content = {
            Text(
                text = annotatedString,
                style = style,
                onTextLayout = { layoutHolder.value = it }
            )
            clickableAttrs.forEach { attr ->
                val rangeText = fullText.substring(attr.startIndex, attr.endIndex)
                Box(
                    Modifier
                        .semantics { contentDescription = rangeText }
                        .clickable { attr.onClick?.invoke() }
                )
            }
        },
        // mergeDescendants: the caller's testTag lands on THIS node, so it
        // must also carry the text — the inner Text's semantics merge up,
        // exactly what the plain Text(modifier) branch exposes. Without the
        // merge the tagged node answered text='' to UiAutomator and every
        // subrange tap failed with "Element has no text" (the driver computes
        // the glyph rect from node.text). The clickable hit-target Boxes are
        // their own merge boundaries, so they stay separate a11y nodes.
        modifier = modifier.semantics(mergeDescendants = true) {}
    ) { measurables, constraints ->
        val textPlaceable = measurables.first().measure(constraints)
        val textLayout = layoutHolder.value

        val placed = measurables.drop(1).mapIndexedNotNull { index, measurable ->
            val attr = clickableAttrs[index]
            val bounds = textLayout?.getPathForRange(attr.startIndex, attr.endIndex)?.getBounds()
            if (bounds == null || bounds.isEmpty) return@mapIndexedNotNull null
            val placeable = measurable.measure(
                Constraints.fixed(
                    bounds.width.roundToInt().coerceAtLeast(1),
                    bounds.height.roundToInt().coerceAtLeast(1)
                )
            )
            Triple(placeable, bounds.left.roundToInt(), bounds.top.roundToInt())
        }

        layout(textPlaceable.width, textPlaceable.height) {
            textPlaceable.place(0, 0)
            placed.forEach { (placeable, x, y) -> placeable.place(x, y) }
        }
    }
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

/**
 * The weight a partial attribute's `fontWeight`/`font` spelling names — by
 * name, or by the numeric css spelling of `font_weight_mapping.json` (600 IS
 * semibold, 400/700 are `normal`/`bold` in numeric form). The SSoT declares
 * `fontWeight` as string|number on all three platforms, and this component is
 * the one seam both the codegen emit and the dynamic renderer pass through, so
 * the two spellings are answered HERE once rather than per caller. Unknown
 * values return null and the span inherits the label's own weight.
 */
private fun parseFontWeight(raw: String?): FontWeight? = when (raw?.trim()?.lowercase()) {
    null -> null
    "thin", "100" -> FontWeight.Thin
    "ultralight", "extralight", "200" -> FontWeight.ExtraLight
    "light", "300" -> FontWeight.Light
    "regular", "normal", "400" -> FontWeight.Normal
    "medium", "500" -> FontWeight.Medium
    "semibold", "600" -> FontWeight.SemiBold
    "bold", "700" -> FontWeight.Bold
    "extrabold" -> FontWeight.ExtraBold
    "black", "900" -> FontWeight.Black
    else -> null
}
