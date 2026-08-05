package com.kotlinjsonui.dynamic.components

import android.content.Context
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.JsonObject
import com.kotlinjsonui.components.PartialAttribute
import com.kotlinjsonui.components.PartialAttributesText
import com.kotlinjsonui.dynamic.DataBindingContext
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.UnappliedAttributes
import com.kotlinjsonui.dynamic.generated.LabelAttributes
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ResourceResolver
import com.kotlinjsonui.dynamic.rememberTypedAttrs

/**
 * Text/Label component → Text / PartialAttributesText.
 * Reference: text_component.rb in kjui_tools.
 *
 * NOTE: Label is the primary component name in JsonUI.
 * Text is supported as an alias for backward compatibility.
 * Both "type": "Label" and "type": "Text" work identically.
 *
 * Attribute access goes through the generated [LabelAttributes]
 * extraction (typed, alias-aware, L1-marker-aware) via the [TypedAttrs]
 * bridge; the node itself is only passed wholesale to the shared
 * ModifierBuilder pipeline.
 */
class DynamicTextComponent {
    companion object {
        @Composable
        fun create(json: JsonObject, data: Map<String, Any> = emptyMap()) {
            val context = LocalContext.current
            val a = rememberTypedAttrs(json) { m, canonicalOnly ->
                LabelAttributes.parse(m, canonicalOnly)
            }
            UnappliedAttributes.check(
                "Label", json,
                declared = LabelAttributes.declaredAttributes,
                applied = UnappliedAttributes.COMMON_APPLIED + APPLIED,
                context = context
            )

            // Resolve text with binding + resource support
            val declaredText = TypedAttrs.rawString(a.text)
                ?.let { ResourceResolver.resolveTextValue(it, data, context) }
                ?: ""

            // `hint` + `hintAttributes` — a Label's placeholder. UIKit's
            // SJUILabel swaps in the hint, styled by hintAttributes, when the
            // text is empty, and it requires BOTH: a hint with no attributes
            // shows nothing. The kjui codegen states the same rule verbatim
            // (text_component.rb#hint_overrides) rather than inventing a
            // divergence; the dynamic path read none of the three rows.
            // `placeholder` is the declared alias of `hint`.
            val hintText = (a.hint ?: a.placeholder)
                ?.takeIf { it.isNotEmpty() && a.hintAttributes != null }
                ?.let { ResourceResolver.resolveTextValue(it, data, context) }
            val showHint = declaredText.isEmpty() && hintText != null
            val text = if (showHint) hintText!! else declaredText
            // The hint branch is OUTERMOST: an empty label is a hint first and
            // a selected label second (the codegen orders them the same way).
            // `hintColor` is the colour-only fallback when the bag carries no
            // fontColor, exactly as hint_overrides reads it.
            val hintStyle: Map<String, Any?>? = if (showHint) {
                a.hintAttributes.orEmpty() + mapOf(
                    "fontColor" to (
                        (a.hintAttributes?.get("fontColor") as? String)
                            ?: TypedAttrs.rawString(a.hintColor)
                        )
                )
            } else null

            // Check for partialAttributes or linkable
            val partialAttributes = a.partialAttributes.orEmpty().filterIsInstance<Map<*, *>>()
            val isLinkable = TypedAttrs.static(a.linkable) == true

            when {
                partialAttributes.isNotEmpty() ->
                    createPartialAttributesText(json, a, partialAttributes, text, data, context)
                isLinkable -> createLinkableText(json, a, text, data, context)
                else -> createStandardText(json, a, text, data, context, hintStyle)
            }
        }

        // ── Standard Text ──

        @Composable
        private fun createStandardText(
            json: JsonObject,
            a: LabelAttributes,
            text: String,
            data: Map<String, Any>,
            context: Context,
            hintStyle: Map<String, Any?>? = null
        ) {
            // highlightAttributes / highlightColor take over while `selected`
            // is true (SSoT: "Decides which attribute set is in force").
            // Mirrors text_component.rb highlight_overrides: the highlight's
            // `font` replaces the base `font` wholesale, and a bare
            // `highlightColor` is the color-only fallback when the
            // highlightAttributes object contributes nothing.
            val isSelected = TypedAttrs.boolean(a.selected, data) == true
            val hl: Map<String, Any?>? = hintStyle
                ?: if (isSelected) a.highlightAttributes?.takeIf { it.isNotEmpty() } else null
            val hlFallbackColor: String? =
                if (hintStyle == null && isSelected && hl == null) {
                    TypedAttrs.rawString(a.highlightColor)
                } else null
            val hlFont = hl?.get("font") as? String

            // Font size
            val fontSize = (hl?.get("fontSize") as? Number)?.toFloat()
                ?: TypedAttrs.float(a.fontSize, data)

            // Font color (supports @{binding}; highlight values are static)
            val fontColor = ((hl?.get("fontColor") as? String) ?: hlFallbackColor)
                ?.let { ColorParser.parseColorString(it, context) }
                ?: ColorParser.parseColorStringWithBinding(
                    TypedAttrs.rawString(a.fontColor), data, context
                )

            // Font weight – handle both 'font' and 'fontWeight' attributes
            val fontWeight = when {
                hlFont != null && WEIGHT_NAMES.containsKey(hlFont.lowercase()) ->
                    WEIGHT_NAMES[hlFont.lowercase()]
                hlFont != null ->
                    fontWeightString(a, data)?.let { WEIGHT_NAMES[it.lowercase()] ?: FontWeight.Normal }
                else -> resolveFontWeight(a, data)
            }

            // Font family – custom font from 'font' attribute (if not a weight name)
            val fontFamily = when {
                hlFont != null && !WEIGHT_NAMES.containsKey(hlFont.lowercase()) ->
                    resolveFontResource(hlFont, context) ?: resolveFontFamilyAttr(a, context, data)
                hlFont != null -> resolveFontFamilyAttr(a, context, data)
                else -> resolveFontFamily(a, context, data)
            }

            // Text decoration
            val textDecoration = resolveTextDecoration(a)

            // Text alignment
            val textAlign = (hl?.get("textAlign") as? String)?.let { align ->
                when (align.lowercase()) {
                    "center" -> TextAlign.Center
                    "right" -> TextAlign.End
                    "left" -> TextAlign.Start
                    else -> null
                }
            } ?: resolveTextAlign(a)

            // Auto size (text shrinking)
            val useAutoSize = a.autoShrink == true || a.minimumScaleFactor != null

            // Max lines
            val lines = TypedAttrs.int(a.lines, data)
            val maxLines = when {
                useAutoSize -> 1
                lines == 0 -> Int.MAX_VALUE
                lines != null -> lines
                else -> Int.MAX_VALUE
            }

            // Overflow
            val overflow = when {
                useAutoSize -> TextOverflow.Ellipsis
                lines != null && lines > 0 -> TextOverflow.Ellipsis
                else -> when (
                    TypedAttrs.enumString(a.lineBreakMode) { it.json }?.lowercase()
                ) {
                    "clip" -> TextOverflow.Clip
                    "tail", "word", "truncatetail" -> TextOverflow.Ellipsis
                    else -> TextOverflow.Clip
                }
            }

            // Build style (shadow, lineHeight)
            val style = buildTextStyle(
                a, data, fontSize,
                overrideLineHeightMultiple = (hl?.get("lineHeightMultiple") as? Number)?.toFloat()
            )

            // Build modifier using composite builder
            var modifier = ModifierBuilder.buildModifier(json, data, context = context)

            // Handle edgeInset for text-specific padding (overrides regular padding)
            modifier = applyEdgeInset(modifier, a.edgeInset)

            Text(
                text = text,
                // No declared size inherits LocalTextStyle (the codegen emit
                // is `resolved.size ?: TextUnit.Unspecified` — same rule).
                fontSize = fontSize?.sp ?: TextUnit.Unspecified,
                color = fontColor ?: Color.Unspecified,
                fontWeight = fontWeight,
                fontFamily = fontFamily,
                textAlign = textAlign,
                textDecoration = textDecoration,
                maxLines = maxLines,
                overflow = overflow,
                autoSize = if (useAutoSize) TextAutoSize.StepBased(
                    minFontSize = ((fontSize ?: 14f) *
                        (TypedAttrs.float(a.minimumScaleFactor, data) ?: 0.5f)).sp
                ) else null,
                style = style ?: LocalTextStyle.current,
                modifier = modifier
            )
        }

        // ── Linkable Text ──

        @Composable
        private fun createLinkableText(
            json: JsonObject,
            a: LabelAttributes,
            text: String,
            data: Map<String, Any>,
            context: Context
        ) {
            val style = buildFullTextStyle(a, data, context)
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
            a: LabelAttributes,
            partialAttrMaps: List<Map<*, *>>,
            text: String,
            data: Map<String, Any>,
            context: Context
        ) {
            val partialAttributes = mutableListOf<PartialAttribute>()

            partialAttrMaps.forEach { attr ->
                val range = resolvePartialRange(attr, text)

                range?.let {
                    val partialAttr = PartialAttribute.fromJsonRange(
                        range = it,
                        text = text,
                        fontColor = attr["fontColor"] as? String,
                        fontSize = (attr["fontSize"] as? Number)?.toInt(),
                        fontWeight = attr["fontWeight"] as? String
                            ?: attr["font"] as? String,
                        background = attr["background"] as? String,
                        underline = attr["underline"] as? Boolean ?: false,
                        strikethrough = attr["strikethrough"] as? Boolean ?: false,
                        onClick = resolvePartialClickHandler(attr, data)
                    )
                    partialAttr?.let { pa -> partialAttributes.add(pa) }
                }
            }

            val style = buildFullTextStyle(a, data, context)
            val modifier = ModifierBuilder.buildModifier(json, data, context = context)

            PartialAttributesText(
                text = text,
                partialAttributes = partialAttributes,
                modifier = modifier,
                style = style
            )
        }

        // ── Helpers ──

        /** Label-specific attributes this component applies (see UnappliedAttributes). */
        private val APPLIED: Set<String> = setOf(
            "text", "partialAttributes", "linkable",
            "fontSize", "fontColor", "font", "fontWeight", "fontFamily",
            "autoShrink", "minimumScaleFactor", "lines", "lineBreakMode",
            "textAlign", "underline", "strikethrough", "textShadow",
            "lineHeight", "lineHeightMultiple", "lineSpacing", "edgeInset",
            "onclick", "selected", "highlightAttributes", "highlightColor",
            "hint", "placeholder", "hintAttributes", "hintColor"
        )

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

        private fun resolveFontWeight(a: LabelAttributes, data: Map<String, Any>): FontWeight? {
            // 'font' attribute: check binding first, then static value
            val fontValue = TypedAttrs.string(a.font, data)
            fontValue?.let { font ->
                val lower = font.lowercase()
                if (WEIGHT_NAMES.containsKey(lower)) {
                    return WEIGHT_NAMES[lower]
                }
            }
            // 'fontWeight' attribute (string | number in the definitions;
            // numeric weights had no effect in the legacy reader either)
            val fontWeightValue = fontWeightString(a, data)
            fontWeightValue?.let { fw ->
                return WEIGHT_NAMES[fw.lowercase()] ?: FontWeight.Normal
            }
            return null
        }

        /** `fontWeight` is declared string|number → typed as Any?. */
        private fun fontWeightString(a: LabelAttributes, data: Map<String, Any>): String? {
            val raw = a.fontWeight ?: return null
            val s = raw as? String ?: return raw.toString()
            if (s.startsWith("@{") && s.endsWith("}")) {
                // Canonical string value context (flat-first, dot paths,
                // `?? default`).
                return DataBindingContext.resolveString(s, data)
            }
            return s
        }

        private fun resolveFontFamily(
            a: LabelAttributes,
            context: Context,
            data: Map<String, Any>
        ): FontFamily? {
            // fontFamily attribute takes priority over font attribute for family resolution
            resolveFontFamilyAttr(a, context, data)?.let { return it }

            // Fall back to font attribute (custom font family if not a weight name)
            val fontValue = TypedAttrs.string(a.font, data)
            fontValue?.let { font ->
                if (!WEIGHT_NAMES.containsKey(font.lowercase())) {
                    resolveFontResource(font, context)?.let { return it }
                }
            }
            return null
        }

        /** The `fontFamily` attribute alone (no `font` fallback). */
        private fun resolveFontFamilyAttr(
            a: LabelAttributes,
            context: Context,
            data: Map<String, Any>
        ): FontFamily? {
            val fontFamilyValue = TypedAttrs.string(a.fontFamily, data) ?: return null
            val fontResName = fontFamilyValue.replace("-", "_").replace(" ", "_").lowercase()
            val resId = context.resources.getIdentifier(
                fontResName, "font", context.packageName
            )
            return if (resId != 0) FontFamily(Font(resId)) else null
        }

        /** Resolve a family name against the app's font resources. */
        private fun resolveFontResource(name: String, context: Context): FontFamily? {
            val fontResName = name.replace("-", "_").lowercase()
            val resId = context.resources.getIdentifier(
                fontResName, "font", context.packageName
            )
            return if (resId != 0) FontFamily(Font(resId)) else null
        }

        private fun resolveTextDecoration(a: LabelAttributes): TextDecoration? {
            // boolean-or-object attributes: only the simple boolean form
            // toggles the decoration here (matches the legacy reader).
            val hasUnderline = a.underline == true
            val hasStrikethrough = a.strikethrough == true

            return when {
                hasUnderline && hasStrikethrough -> TextDecoration.combine(
                    listOf(TextDecoration.Underline, TextDecoration.LineThrough)
                )
                hasUnderline -> TextDecoration.Underline
                hasStrikethrough -> TextDecoration.LineThrough
                else -> null
            }
        }

        private fun resolveTextAlign(a: LabelAttributes): TextAlign? {
            TypedAttrs.staticEnumString(a.textAlign) { it.json }?.let { align ->
                return when (align.lowercase()) {
                    "center" -> TextAlign.Center
                    "right" -> TextAlign.End
                    "left" -> TextAlign.Start
                    else -> null
                }
            }
            if (TypedAttrs.static(a.common.centerHorizontal) == true) {
                return TextAlign.Center
            }
            return null
        }

        /**
         * Build TextStyle for shadow and lineHeight.
         * Matches text_component.rb style generation.
         */
        /**
         * Returns null when neither lineHeight nor shadow applies — the
         * caller then falls back to LocalTextStyle, matching the codegen
         * emit which only passes a `style =` argument when it has style
         * parts (an empty TextStyle would DISCARD the Material theme's
         * bodyLarge defaults and change the effective font size).
         */
        private fun buildTextStyle(
            a: LabelAttributes,
            data: Map<String, Any>,
            fontSize: Float?,
            overrideLineHeightMultiple: Float? = null
        ): TextStyle? {
            var style: TextStyle? = null

            // Line height calculation matching Ruby implementation
            // (the highlight override resolves against the highlight's own
            // font size, which is what the caller passes as fontSize)
            val lineHeightMultiple = overrideLineHeightMultiple
                ?: TypedAttrs.float(a.lineHeightMultiple, data)
            val lineSpacing = TypedAttrs.float(a.lineSpacing, data)
            val lineHeight = when {
                lineHeightMultiple != null -> (fontSize ?: 14f) * lineHeightMultiple
                lineSpacing != null -> (fontSize ?: 14f) + lineSpacing
                fontSize != null -> {
                    // Default lineHeight = fontSize * 1.3 to match iOS compact line spacing
                    (fontSize * 1.3f)
                }
                else -> null
            }
            lineHeight?.let { style = (style ?: TextStyle()).copy(lineHeight = it.sp) }

            // Text shadow
            if (a.textShadow != null) {
                style = (style ?: TextStyle()).copy(
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
            a: LabelAttributes,
            data: Map<String, Any>,
            context: Context
        ): TextStyle {
            val fontSize = TypedAttrs.float(a.fontSize, data)
            var style = buildTextStyle(a, data, fontSize) ?: TextStyle()

            fontSize?.let {
                style = style.copy(fontSize = it.sp)
            }

            ColorParser.parseColorStringWithBinding(
                TypedAttrs.rawString(a.fontColor), data, context
            )?.let {
                style = style.copy(color = it)
            }

            resolveFontWeight(a, data)?.let {
                style = style.copy(fontWeight = it)
            }

            resolveFontFamily(a, context, data)?.let {
                style = style.copy(fontFamily = it)
            }

            resolveTextAlign(a)?.let {
                style = style.copy(textAlign = it)
            }

            return style
        }

        /**
         * Apply edgeInset (text-specific padding) to modifier.
         * edgeInset takes priority over regular padding for Text components.
         */
        private fun applyEdgeInset(modifier: Modifier, edgeInset: Any?): Modifier {
            return when {
                edgeInset is List<*> && edgeInset.size == 4 &&
                    edgeInset.all { it is Number } -> {
                    modifier.then(
                        Modifier.padding(
                            top = (edgeInset[0] as Number).toFloat().dp,
                            end = (edgeInset[1] as Number).toFloat().dp,
                            bottom = (edgeInset[2] as Number).toFloat().dp,
                            start = (edgeInset[3] as Number).toFloat().dp
                        )
                    )
                }
                edgeInset is Number -> modifier.then(Modifier.padding(edgeInset.toFloat().dp))
                else -> modifier
            }
        }

        /**
         * Resolve partial attribute range: supports numeric array [start, end] or string pattern.
         */
        private fun resolvePartialRange(attr: Map<*, *>, text: String): Any? {
            return when (val rangeValue = attr["range"]) {
                is List<*> -> {
                    if (rangeValue.size == 2 && rangeValue.all { it is Number }) {
                        // Numeric range – extract substring for localization support
                        val start = (rangeValue[0] as Number).toInt()
                        val end = (rangeValue[1] as Number).toInt()
                        if (start in 0..text.length && end in start..text.length) {
                            text.substring(start, end)
                        } else {
                            listOf(start, end)
                        }
                    } else null
                }
                is String -> rangeValue
                else -> null
            }
        }

        /**
         * Resolve click handler for partial attributes.
         * onclick (lowercase) → selector format (string only)
         * onClick (camelCase) → binding format only (@{functionName})
         */
        private fun resolvePartialClickHandler(
            attr: Map<*, *>,
            data: Map<String, Any>
        ): (() -> Unit)? {
            val onclick = attr["onclick"] as? String
            val onClick = attr["onClick"] as? String
            val methodName = when {
                onclick != null -> if (!onclick.contains("@{")) onclick else null
                onClick != null -> ModifierBuilder.extractBindingProperty(onClick)
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
