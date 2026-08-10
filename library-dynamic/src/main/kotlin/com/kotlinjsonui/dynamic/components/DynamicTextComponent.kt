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
import com.kotlinjsonui.components.StyledLineState
import com.kotlinjsonui.components.styledTextLines
import androidx.compose.ui.graphics.graphicsLayer
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
            // `linkable` is declared `["boolean","binding"]`; a STATIC-only
            // read dropped the bound form, so `Label/linkable__binding` drew
            // plain text where the codegen and web both drew a link (android
            // parity distance 29). The bound-dropped family.
            val isLinkable = TypedAttrs.boolean(a.linkable, data) == true

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

            // Font color. The value can arrive from three places and only one
            // of them is static: the `highlightAttributes` / `hintAttributes`
            // BAG is a literal object, but the flat `highlightColor` and
            // `hintColor` that feed `hlFallbackColor` / the hint bag are
            // declared `["string","binding"]`. Parsing that branch with the
            // binding-BLIND parser dropped both bound forms — `__static` was
            // active on android while `Label/highlightColor__binding` and
            // `Label/hintColor__binding` rendered the default colour. The
            // binding-aware parser degrades to the same static parse for the
            // bag's literals, so one call serves all three.
            val enabledColor = ((hl?.get("fontColor") as? String) ?: hlFallbackColor)
                ?.let { ColorParser.parseColorStringWithBinding(it, data, context) }
                ?: ColorParser.parseColorStringWithBinding(
                    TypedAttrs.rawString(a.fontColor), data, context
                )
            // `disabledFontColor` replaces the font colour while the label is
            // statically disabled — the same gate the codegen face emits
            // (text_component.rb: enabled == false && disabledFontColor).
            // Nothing here read it, so the android dynamic face was inert
            // while ios and web coloured the disabled label
            // (Label/disabledFontColor__static/binding, run 31243724782
            // cross-effect).
            val fontColor = if (TypedAttrs.boolean(a.common.enabled, data) == false) {
                ColorParser.parseColorStringWithBinding(
                    TypedAttrs.rawString(a.disabledFontColor), data, context
                ) ?: enabledColor
            } else enabledColor

            // Font weight – handle both 'font' and 'fontWeight' attributes
            val fontWeight = when {
                hlFont != null && WEIGHT_NAMES.containsKey(hlFont.lowercase()) ->
                    WEIGHT_NAMES[hlFont.lowercase()]
                hlFont != null ->
                    fontWeightRaw(a, data)?.let { ResourceResolver.fontWeightOf(it) ?: FontWeight.Normal }
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
            // The object face's colour/offset (the drawBehind seam): a face
            // with a declared colour leaves the native decoration and is
            // drawn by styledTextLines in that colour instead; lineOffset is
            // UIKit's baselineOffset — the text shifts, the layout box stays.
            val effectiveTextColor = fontColor
                ?: androidx.compose.material3.LocalContentColor.current
            val underlineLine = decorationLineSpec(a.underline, data, context, effectiveTextColor)
            val strikethroughLine = decorationLineSpec(a.strikethrough, data, context, effectiveTextColor)
            val underlineOffset = decorationLineOffset(a.underline)
            val textDecoration = resolveTextDecoration(
                a,
                suppressUnderline = underlineLine != null,
                suppressStrikethrough = strikethroughLine != null
            )
            val lineState = if (underlineLine != null || strikethroughLine != null) {
                androidx.compose.runtime.remember { StyledLineState() }
            } else null

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

            if (lineState != null) {
                modifier = modifier.styledTextLines(
                    lineState,
                    underline = underlineLine,
                    strikethrough = strikethroughLine
                )
            }
            if (underlineOffset != null && underlineOffset != 0f) {
                val shift = underlineOffset
                modifier = modifier.graphicsLayer { translationY = -shift.dp.toPx() }
            }

            Text(
                text = text,
                onTextLayout = { lineState?.layout = it },
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
                val range = resolvePartialRange(attr, text, data, context)

                range?.let {
                    val partialAttr = PartialAttribute.fromJsonRange(
                        range = it,
                        text = text,
                        // EVERY value slot resolves `@{...}` first: the fields
                        // inside a partial are declared binding-capable like
                        // their Label counterparts, but this map is raw JSON —
                        // `as? String` handed the SPELLING to the colour
                        // parser, so a bound fontColor styled nothing while
                        // the codegen (which interpolates `${data.x}` at
                        // compose time) styled the resolved value
                        // (a downstream hour-row cell, 2026-08-08).
                        fontColor = resolvePartialString(attr["fontColor"], data),
                        fontSize = resolvePartialInt(attr["fontSize"], data),
                        // Declared string|number; PartialAttribute's boundary
                        // is a String, and PartialAttributesText maps names
                        // AND the css numbers in one place — so a numeric 600
                        // travels as "600", not as a silent null.
                        fontWeight = resolvePartialString(attr["fontWeight"], data)
                            ?: (attr["fontWeight"] as? Number)?.toInt()?.toString()
                            ?: resolvePartialString(attr["font"], data),
                        background = resolvePartialString(attr["background"], data),
                        // `partialAttributes[].underline` is declared OBJECT-ONLY
                        // (the boolean face is not in that schema), so `as?
                        // Boolean` forced the one declared face to false and no
                        // partial ever drew a line. Same `textDecoration` ruling
                        // as the Label body, so the same reader.
                        underline = drawsLine(attr["underline"]),
                        strikethrough = drawsLine(attr["strikethrough"]),
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
            // 'fontWeight' attribute — declared string|number, and the number
            // is the css column of the same table the names come from (600 IS
            // semibold). The legacy reader stringified 600 and missed
            // WEIGHT_NAMES, landing on Normal — run 6 measured android inert
            // where ios drew it. Unknown values still fall back to Normal, as
            // the mapping's own comment prescribes.
            fontWeightRaw(a, data)?.let { fw ->
                return ResourceResolver.fontWeightOf(fw) ?: FontWeight.Normal
            }
            return null
        }

        /**
         * `fontWeight` is declared string|number → typed as Any?. The number
         * arrives as a gson Double, and stringifying it minted "600.0" — a
         * spelling no table has, which is how the numeric path stayed dropped
         * even after the css-column lookup existed (smoke: Button active,
         * Label still inert). Numbers go through as numbers.
         */
        private fun fontWeightRaw(a: LabelAttributes, data: Map<String, Any>): Any? {
            val raw = a.fontWeight ?: return null
            val s = raw as? String ?: return raw
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

        /**
         * The `fontFamily` attribute alone (no `font` fallback).
         *
         * Resolution goes through the SHARED [ResourceResolver.resolveFontResource],
         * which tries the generic families (serif / sans-serif / monospace /
         * cursive) before `res/font`. This component used to carry its own
         * private copy that only did the `res/font` lookup, so a declared
         * `serif` found no font file and rendered identically to no
         * declaration at all — `Label/fontFamily` was inert on android in BOTH
         * faces while TextView and TextField, which already called the shared
         * helper, were active. The binding was never the problem here; the
         * duplicated helper was.
         */
        private fun resolveFontFamilyAttr(
            a: LabelAttributes,
            context: Context,
            data: Map<String, Any>
        ): FontFamily? =
            ResourceResolver.resolveFontResource(TypedAttrs.string(a.fontFamily, data), context)

        /** Resolve a family name against the generic families, then `res/font`. */
        private fun resolveFontResource(name: String, context: Context): FontFamily? =
            ResourceResolver.resolveFontResource(name, context)

        /**
         * Whether a declared `underline` / `strikethrough` draws a line.
         *
         * The rows declare `boolean|object` and the contract is the SSoT's
         * `textDecoration` ruling (attribute_semantics.json, 51-E §3):
         *
         *  - the boolean face: `true` draws, `false` does not;
         *  - the OBJECT face draws the line it describes and **must never
         *    render less than the boolean face** — a platform that cannot
         *    honour `lineStyle` or `color` still draws the plain Single line
         *    in the text colour;
         *  - `lineStyle: "None"` is the single object value that draws
         *    nothing, exactly equivalent to `false`.
         *
         * Testing the row with `== true` saw only the boolean face, so every
         * styled object rendered no line at all — the violation the ruling
         * names and assigns to this lane (cross_effect Label/underline__styled
         * and Label/strikethrough__styled: android inert, ios and web active).
         *
         * Compose's `TextDecoration` carries neither colour nor thickness, so
         * the plain line IS the faithful output here; `Double` and `Thick`
         * stay undistinguished on purpose (the ruling keeps that gap in the
         * coverage ledger rather than deleting the enum values).
         */
        internal fun drawsLine(declared: Any?): Boolean = when (declared) {
            null -> false
            is Boolean -> declared
            is Map<*, *> -> !"none".equals(declared["lineStyle"] as? String, ignoreCase = true)
            // A non-empty array face is a presence statement like `true`.
            is List<*> -> declared.isNotEmpty()
            else -> false
        }

        /**
         * The object face as a drawn [com.kotlinjsonui.components.StyledLine]
         * — non-null when the face needs the drawing seam: a declared
         * `color`, or a `lineStyle` (Double/Thick) the native decoration
         * cannot express. `fallbackColor` is the effective text colour, used
         * when the face styles the line but not its colour. A face with a
         * spec must be SUPPRESSED from the native decoration.
         */
        internal fun decorationLineSpec(
            face: Any?,
            data: Map<String, Any>,
            context: Context,
            fallbackColor: Color
        ): com.kotlinjsonui.components.StyledLine? {
            if (!drawsLine(face)) return null
            val map = face as? Map<*, *> ?: return null
            val style = (map["lineStyle"] as? String)?.lowercase() ?: "single"
            val declared = (map["color"] as? String)
                ?.let { ColorParser.parseColorStringWithBinding(it, data, context) }
            if (declared == null && style != "double" && style != "thick") return null
            return com.kotlinjsonui.components.StyledLine(
                color = declared ?: fallbackColor,
                style = style
            )
        }

        /** The object face's underline-only `lineOffset` (UIKit baselineOffset). */
        internal fun decorationLineOffset(face: Any?): Float? =
            ((face as? Map<*, *>)?.takeIf { drawsLine(it) }?.get("lineOffset") as? Number)?.toFloat()

        private fun resolveTextDecoration(
            a: LabelAttributes,
            suppressUnderline: Boolean = false,
            suppressStrikethrough: Boolean = false
        ): TextDecoration? {
            val hasUnderline = drawsLine(a.underline) && !suppressUnderline
            val hasStrikethrough = drawsLine(a.strikethrough) && !suppressStrikethrough

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
        @Composable
        private fun buildTextStyle(
            a: LabelAttributes,
            data: Map<String, Any>,
            fontSize: Float?,
            overrideLineHeightMultiple: Float? = null
        ): TextStyle? {
            // Derive from the CURRENT text style, never a bare TextStyle():
            // the bare one discards the Material defaults — letterSpacing
            // 0.5sp among them — so a style-carrying Label measured ~8dp
            // narrower than the codegen face's LocalTextStyle-inheriting
            // emit and wrapped one line later at the fixture's 200dp
            // boundary (Label_hintAttributes__static android parity d=19,
            // runs 31234163967/31243724782).
            val base = androidx.compose.material3.LocalTextStyle.current
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
            lineHeight?.let { style = (style ?: base).copy(lineHeight = it.sp) }

            // Text shadow
            if (a.textShadow != null) {
                style = (style ?: base).copy(
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
        @Composable
        private fun buildFullTextStyle(
            a: LabelAttributes,
            data: Map<String, Any>,
            context: Context
        ): TextStyle {
            val fontSize = TypedAttrs.float(a.fontSize, data)
            // LocalTextStyle base, not a bare TextStyle(): an unstyled
            // linkable label built its full style from the bare base and
            // dropped the Material defaults the plain-Text path keeps —
            // tighter letterSpacing and leading than the codegen face
            // (Label_linkable__true/binding parity d=33, run 31283456670).
            //
            // And ONLY the base: the codegen face's PartialAttributesText
            // style carries color/font/textAlign fragments and NEVER a
            // lineHeight (text_component.rb partial style_parts — even a
            // declared lineHeightMultiple emits nothing there), so the
            // ambient theme's line height survives. Routing this through
            // buildTextStyle injected the plain-path fontSize*1.3 default,
            // and a partial-attributed label rendered a different line
            // height than its codegen twin wherever the app theme sets one
            // (downstream hour rows, 2026-08-10).
            var style = androidx.compose.material3.LocalTextStyle.current

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
        internal fun resolvePartialRange(
            attr: Map<*, *>,
            text: String,
            data: Map<String, Any>,
            context: Context? = null
        ): Any? {
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
                // A string range is a text pattern AND binding-capable AND a
                // string-resource key: the codegen face resolves it through
                // process_text (stringResource(R.string.key) at compose
                // time), so `range: "terms_of_service"` means the LOCALIZED
                // substring, matched against the equally-localized text. This
                // path resolved only the binding face and handed the raw key
                // to text.indexOf — every key-form range silently vanished on
                // the dynamic face (downstream report) while numeric and literal patterns matched.
                // Chain: binding → string resource → literal, the same order
                // the text itself resolves (ResourceResolver.resolveTextValue).
                is String -> {
                    val bound = resolvePartialString(rangeValue, data)
                    val resolved = bound?.let { b ->
                        if (context != null) ResourceResolver.resolveTextValue(b, data, context) else b
                    }
                    resolved?.takeIf { it.isNotEmpty() }
                }
                else -> null
            }
        }

        /**
         * A partial-map value slot: `@{expr}` resolves against the data map
         * through the same canonical resolver every Label-level binding uses
         * (dot paths, `?? default`); a plain string passes through; an
         * unresolvable binding yields null (slot absent), never the spelling.
         */
        internal fun resolvePartialString(raw: Any?, data: Map<String, Any>): String? {
            val s = raw as? String ?: return null
            val expr = com.kotlinjsonui.dynamic.generated.AttrCoerce.bindingExpression(s) ?: return s
            return DataBindingContext.resolveStringInner(expr, data)
        }

        /** Numeric partial slot: literal number, or a binding resolved as number. */
        internal fun resolvePartialInt(raw: Any?, data: Map<String, Any>): Int? = when (raw) {
            is Number -> raw.toInt()
            is String -> com.kotlinjsonui.dynamic.generated.AttrCoerce.bindingExpression(raw)
                ?.let { DataBindingContext.resolveNumberInner(it, data)?.toInt() }
            else -> null
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
