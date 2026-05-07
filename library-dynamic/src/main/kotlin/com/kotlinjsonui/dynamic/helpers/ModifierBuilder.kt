package com.kotlinjsonui.dynamic.helpers

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.DataBindingContext
import com.kotlinjsonui.dynamic.ResourceCache

/**
 * Builds Compose Modifier from JSON attributes.
 * Method order matches modifier_builder.rb in kjui_tools.
 *
 * Modifier application order:
 * testTag → margins → weight → size → alpha → shadow → background(clip+border+bg) → clickable → padding → alignment
 */
object ModifierBuilder {

    // ── Binding Helpers ──────────────────────────────────────────────

    fun isBinding(value: Any?): Boolean {
        return value is String && value.startsWith("@{") && value.endsWith("}")
    }

    fun extractBindingProperty(value: String): String? {
        if (value.startsWith("@{") && value.endsWith("}")) {
            return value.drop(2).dropLast(1)
        }
        return null
    }

    /**
     * Resolve event handler from data map and invoke it.
     * Supports () -> Unit, (String) -> Unit, (String, T) -> Unit signatures.
     */
    fun resolveEventHandler(
        handler: String?,
        data: Map<String, Any>,
        viewId: String? = null,
        valueExpr: Any? = null
    ) {
        if (handler == null) return
        val methodName = if (isBinding(handler)) extractBindingProperty(handler) ?: handler else handler

        val fn = data[methodName] ?: return
        try {
            when {
                valueExpr != null && viewId != null -> {
                    @Suppress("UNCHECKED_CAST")
                    (fn as? (String, Any) -> Unit)?.invoke(viewId, valueExpr)
                        ?: (fn as? (Any) -> Unit)?.invoke(valueExpr)
                        ?: (fn as? () -> Unit)?.invoke()
                }
                viewId != null -> {
                    @Suppress("UNCHECKED_CAST")
                    (fn as? (String) -> Unit)?.invoke(viewId)
                        ?: (fn as? () -> Unit)?.invoke()
                }
                else -> {
                    @Suppress("UNCHECKED_CAST")
                    (fn as? () -> Unit)?.invoke()
                }
            }
        } catch (_: Exception) {
            // Signature mismatch – silently skip
        }
    }

    // ── Individual Modifier Builders ─────────────────────────────────

    /** build_test_tag: id → testTag + semantics */
    fun applyTestTag(modifier: Modifier, json: JsonObject): Modifier {
        val id = json.get("id")?.asString ?: return modifier
        return modifier
            .testTag(id)
            .semantics { testTagsAsResourceId = true }
    }

    /** build_margins: margins array / individual margin properties with binding support */
    fun applyMargins(modifier: Modifier, json: JsonObject, data: Map<String, Any>): Modifier {
        // Handle margins array first
        json.get("margins")?.let { element ->
            if (element.isJsonArray) {
                val arr = element.asJsonArray
                return when (arr.size()) {
                    1 -> modifier.padding(arr[0].asFloat.dp)
                    2 -> modifier.padding(
                        vertical = arr[0].asFloat.dp,
                        horizontal = arr[1].asFloat.dp
                    )
                    4 -> modifier.padding(
                        top = arr[0].asFloat.dp,
                        end = arr[1].asFloat.dp,
                        bottom = arr[2].asFloat.dp,
                        start = arr[3].asFloat.dp
                    )
                    else -> modifier
                }
            }
        }

        // Individual margin properties with binding support
        val top = resolveMarginValue(json, "topMargin", data)
            ?: resolveMarginValue(json, "marginTop", data) ?: 0f
        val bottom = resolveMarginValue(json, "bottomMargin", data)
            ?: resolveMarginValue(json, "marginBottom", data) ?: 0f
        val start = resolveMarginValue(json, "leftMargin", data)
            ?: resolveMarginValue(json, "marginLeft", data)
            ?: resolveMarginValue(json, "startMargin", data)
            ?: resolveMarginValue(json, "marginStart", data) ?: 0f
        val end = resolveMarginValue(json, "rightMargin", data)
            ?: resolveMarginValue(json, "marginRight", data)
            ?: resolveMarginValue(json, "endMargin", data)
            ?: resolveMarginValue(json, "marginEnd", data) ?: 0f

        return if (top > 0 || bottom > 0 || start > 0 || end > 0) {
            modifier.padding(top = top.dp, bottom = bottom.dp, start = start.dp, end = end.dp)
        } else {
            modifier
        }
    }

    private fun resolveMarginValue(json: JsonObject, key: String, data: Map<String, Any>): Float? {
        val element = json.get(key) ?: return null
        return when {
            element.isJsonPrimitive -> {
                val p = element.asJsonPrimitive
                when {
                    p.isNumber -> p.asFloat
                    p.isString -> {
                        val s = p.asString
                        if (s.startsWith("@{") && s.endsWith("}")) {
                            val evaluated = DataBindingContext.evaluateExpression(s, data)
                            when (evaluated) {
                                is Number -> evaluated.toFloat()
                                is String -> evaluated.toFloatOrNull() ?: 0f
                                else -> 0f
                            }
                        } else {
                            s.toFloatOrNull()
                        }
                    }
                    else -> null
                }
            }
            else -> null
        }
    }

    /** build_weight: weight > 0 within Row/Column only */
    fun getWeight(json: JsonObject): Float? {
        return json.get("weight")?.asFloat?.takeIf { it > 0 }
    }

    /** build_size: frame object, width/height, matchParent/wrapContent, min/max, aspectRatio */
    fun applySize(
        modifier: Modifier,
        json: JsonObject,
        defaultFillMaxWidth: Boolean = false
    ): Modifier {
        var result = modifier

        // Handle 'frame' attribute – object with width/height
        json.get("frame")?.let { frameElement ->
            if (frameElement.isJsonObject) {
                val frame = frameElement.asJsonObject
                result = applySingleDimension(result, frame, "width", isWidth = true)
                result = applySingleDimension(result, frame, "height", isWidth = false)
                return applyConstraints(result, json)
            }
        }

        // Width
        val hasWeight = json.has("weight")
        val widthElement = json.get("width")
        if (widthElement != null) {
            val skipWidth = hasWeight && widthElement.isJsonPrimitive &&
                    widthElement.asJsonPrimitive.isNumber && widthElement.asFloat == 0f
            if (!skipWidth) {
                result = applySingleDimension(result, json, "width", isWidth = true)
            }
        } else if (defaultFillMaxWidth) {
            result = result.fillMaxWidth()
        }

        // Height
        val hasHeightWeight = json.has("heightWeight")
        val heightElement = json.get("height")
        if (heightElement != null) {
            val skipHeight = hasHeightWeight && heightElement.isJsonPrimitive &&
                    heightElement.asJsonPrimitive.isNumber && heightElement.asFloat == 0f
            if (!skipHeight) {
                result = applySingleDimension(result, json, "height", isWidth = false)
            }
        }

        return applyConstraints(result, json)
    }

    private fun applySingleDimension(
        modifier: Modifier,
        json: JsonObject,
        key: String,
        isWidth: Boolean
    ): Modifier {
        val element = json.get(key) ?: return modifier
        return when {
            element.isJsonPrimitive -> {
                val p = element.asJsonPrimitive
                when {
                    p.isString -> when (p.asString) {
                        "matchParent", "match_parent" ->
                            if (isWidth) modifier.fillMaxWidth() else modifier.fillMaxHeight()
                        "wrapContent", "wrap_content" ->
                            if (isWidth) modifier.wrapContentWidth() else modifier.wrapContentHeight()
                        else -> {
                            val v = p.asString.toFloatOrNull()
                            if (v != null) {
                                if (v < 0) {
                                    if (isWidth) modifier.fillMaxWidth() else modifier.fillMaxHeight()
                                } else {
                                    if (isWidth) modifier.width(v.dp) else modifier.height(v.dp)
                                }
                            } else modifier
                        }
                    }
                    p.isNumber -> {
                        val v = p.asFloat
                        if (v < 0) {
                            if (isWidth) modifier.fillMaxWidth() else modifier.fillMaxHeight()
                        } else {
                            if (isWidth) modifier.width(v.dp) else modifier.height(v.dp)
                        }
                    }
                    else -> modifier
                }
            }
            else -> modifier
        }
    }

    private fun applyConstraints(modifier: Modifier, json: JsonObject): Modifier {
        var result = modifier
        val hasWidth = json.has("width")
        val hasHeight = json.has("height")
        val maxWidth = parseOptionalDp(json, "maxWidth")
        val maxHeight = parseOptionalDp(json, "maxHeight")
        val minWidth = parseOptionalDp(json, "minWidth")
        val minHeight = parseOptionalDp(json, "minHeight")

        // Width constraints
        if (minWidth != null && maxWidth != null) {
            result = result.widthIn(min = minWidth, max = maxWidth)
        } else if (maxWidth != null) {
            result = if (!hasWidth) result.wrapContentWidth().widthIn(max = maxWidth)
            else result.widthIn(max = maxWidth)
        } else if (minWidth != null) {
            result = result.widthIn(min = minWidth)
        }

        // Height constraints
        if (minHeight != null && maxHeight != null) {
            result = result.heightIn(min = minHeight, max = maxHeight)
        } else if (maxHeight != null) {
            result = if (!hasHeight) result.wrapContentHeight().heightIn(max = maxHeight)
            else result.heightIn(max = maxHeight)
        } else if (minHeight != null) {
            result = result.heightIn(min = minHeight)
        }

        // Aspect ratio
        val aw = json.get("aspectWidth")?.asFloat
        val ah = json.get("aspectHeight")?.asFloat
        if (aw != null && ah != null && ah > 0) {
            result = result.aspectRatio(aw / ah)
        }

        return result
    }

    private fun parseOptionalDp(json: JsonObject, key: String): Dp? {
        val element = json.get(key) ?: return null
        return try {
            if (element.isJsonPrimitive && element.asJsonPrimitive.isNumber) {
                element.asFloat.dp
            } else null
        } catch (_: Exception) { null }
    }

    /** build_alpha / build_visibility: alpha/opacity with binding support */
    fun applyAlpha(modifier: Modifier, json: JsonObject, data: Map<String, Any>): Modifier {
        val raw = json.get("opacity") ?: json.get("alpha") ?: return modifier
        if (raw.isJsonPrimitive) {
            val p = raw.asJsonPrimitive
            if (p.isNumber) {
                return modifier.alpha(p.asFloat.coerceIn(0f, 1f))
            }
            if (p.isString) {
                val s = p.asString
                if (s.startsWith("@{") && s.endsWith("}")) {
                    val prop = s.drop(2).dropLast(1)
                    val value = data[prop]
                    val alphaVal = when (value) {
                        is Number -> value.toFloat()
                        is String -> value.toFloatOrNull() ?: 1f
                        else -> 1f
                    }
                    return modifier.alpha(alphaVal.coerceIn(0f, 1f))
                }
                p.asString.toFloatOrNull()?.let {
                    return modifier.alpha(it.coerceIn(0f, 1f))
                }
            }
        }
        return modifier
    }

    /** build_shadow: shadow attribute → dropShadow */
    fun applyShadow(modifier: Modifier, json: JsonObject): Modifier {
        val shadowElement = json.get("shadow") ?: return modifier
        val cornerRadius = json.get("cornerRadius")?.asFloat
        val shape = if (cornerRadius != null) RoundedCornerShape(cornerRadius.dp) else RectangleShape

        return when {
            shadowElement.isJsonPrimitive && shadowElement.asJsonPrimitive.isString -> {
                modifier.dropShadow(shape = shape, shadow = Shadow(radius = 4.dp))
            }
            shadowElement.isJsonObject -> {
                val radius = shadowElement.asJsonObject.get("radius")?.asFloat ?: 4f
                modifier.dropShadow(shape = shape, shadow = Shadow(radius = radius.dp))
            }
            shadowElement.isJsonPrimitive && shadowElement.asJsonPrimitive.isNumber -> {
                modifier.dropShadow(shape = shape, shadow = Shadow(radius = shadowElement.asFloat.dp))
            }
            else -> modifier
        }
    }

    /** build_background: cornerRadius → clip + border(solid/dashed/dotted) + background color */
    fun applyBackground(
        modifier: Modifier,
        json: JsonObject,
        data: Map<String, Any>,
        context: Context?
    ): Modifier {
        var result = modifier
        val cornerRadius = json.get("cornerRadius")?.asFloat
        val bgColor = ColorParser.parseColorWithBinding(json, "background", data, context)
        val borderColor = ColorParser.parseColorWithBinding(json, "borderColor", data, context)
        val borderWidth = json.get("borderWidth")?.asFloat
        val borderStyle = json.get("borderStyle")?.asString ?: "solid"

        // Clip with corner radius
        if (cornerRadius != null) {
            result = result.clip(RoundedCornerShape(cornerRadius.dp))
        }

        // Border
        if (borderColor != null && borderWidth != null) {
            val borderShape = if (cornerRadius != null) RoundedCornerShape(cornerRadius.dp) else RectangleShape
            result = when (borderStyle) {
                "dashed" -> result.dashedBorder(borderWidth.dp, borderColor, borderShape)
                "dotted" -> result.dottedBorder(borderWidth.dp, borderColor, borderShape)
                else -> result.border(borderWidth.dp, borderColor, borderShape)
            }
        }

        // Background color
        if (bgColor != null) {
            result = result.background(bgColor)
        }

        return result
    }

    /** build_clickable: onClick/onclick → .clickable { handler } */
    fun applyClickable(
        modifier: Modifier,
        json: JsonObject,
        data: Map<String, Any>
    ): Modifier {
        val handler = json.get("onClick")?.asString ?: json.get("onclick")?.asString ?: return modifier
        val viewId = json.get("id")?.asString
        return modifier.clickable {
            resolveEventHandler(handler, data, viewId)
        }
    }

    /** build_padding: padding/paddings (array/single), individual padding properties */
    fun applyPadding(modifier: Modifier, json: JsonObject): Modifier {
        // Handle paddings attribute first
        json.get("paddings")?.let { element ->
            if (element.isJsonPrimitive && element.asJsonPrimitive.isNumber) {
                return modifier.padding(element.asFloat.dp)
            }
            if (element.isJsonArray) {
                val arr = element.asJsonArray
                return when (arr.size()) {
                    1 -> modifier.padding(arr[0].asFloat.dp)
                    2 -> modifier.padding(
                        vertical = arr[0].asFloat.dp,
                        horizontal = arr[1].asFloat.dp
                    )
                    3 -> modifier.padding(
                        start = arr[1].asFloat.dp,
                        top = arr[0].asFloat.dp,
                        end = arr[1].asFloat.dp,
                        bottom = arr[2].asFloat.dp
                    )
                    4 -> modifier.padding(
                        top = arr[0].asFloat.dp,
                        end = arr[1].asFloat.dp,
                        bottom = arr[2].asFloat.dp,
                        start = arr[3].asFloat.dp
                    )
                    else -> modifier
                }
            }
        }

        // Handle single padding value
        json.get("padding")?.let { element ->
            when {
                element.isJsonPrimitive && element.asJsonPrimitive.isNumber -> {
                    return modifier.padding(element.asFloat.dp)
                }
                element.isJsonArray -> {
                    val arr = element.asJsonArray
                    return when (arr.size()) {
                        1 -> modifier.padding(arr[0].asFloat.dp)
                        2 -> modifier.padding(
                            vertical = arr[0].asFloat.dp,
                            horizontal = arr[1].asFloat.dp
                        )
                        3 -> modifier.padding(
                            start = arr[1].asFloat.dp,
                            top = arr[0].asFloat.dp,
                            end = arr[1].asFloat.dp,
                            bottom = arr[2].asFloat.dp
                        )
                        4 -> modifier.padding(
                            top = arr[0].asFloat.dp,
                            end = arr[1].asFloat.dp,
                            bottom = arr[2].asFloat.dp,
                            start = arr[3].asFloat.dp
                        )
                        else -> modifier
                    }
                }
            }
        }

        // Individual padding properties
        val paddingTop = json.get("paddingTop")?.asFloat
            ?: json.get("topPadding")?.asFloat
            ?: json.get("paddingVertical")?.asFloat ?: 0f
        val paddingBottom = json.get("paddingBottom")?.asFloat
            ?: json.get("bottomPadding")?.asFloat
            ?: json.get("paddingVertical")?.asFloat ?: 0f
        val paddingStart = json.get("paddingStart")?.asFloat
            ?: json.get("startPadding")?.asFloat
            ?: json.get("paddingLeft")?.asFloat
            ?: json.get("leftPadding")?.asFloat
            ?: json.get("paddingHorizontal")?.asFloat ?: 0f
        val paddingEnd = json.get("paddingEnd")?.asFloat
            ?: json.get("endPadding")?.asFloat
            ?: json.get("paddingRight")?.asFloat
            ?: json.get("rightPadding")?.asFloat
            ?: json.get("paddingHorizontal")?.asFloat ?: 0f

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

    /** build_alignment: parent_type (Row/Column/Box) dependent alignment */
    fun applyAlignment(
        modifier: Modifier,
        json: JsonObject,
        parentType: String?
    ): Modifier {
        val alignment = getChildAlignment(json, parentType ?: return modifier) ?: return modifier

        // Alignment modifiers require scope context.
        // In Dynamic mode we pass it via BoxScope / RowScope / ColumnScope extension.
        // Here we store the alignment info; the container component will apply it.
        return modifier
    }

    /**
     * Normalized alignment flags derived from `gravity` plus individual
     * `alignTop`/`centerHorizontal`/etc. booleans. The tool's emitter may
     * write either form, so the runtime must honor both.
     */
    internal data class AlignFlags(
        val alignTop: Boolean = false,
        val alignBottom: Boolean = false,
        val alignLeft: Boolean = false,
        val alignRight: Boolean = false,
        val centerH: Boolean = false,
        val centerV: Boolean = false,
        val centerInParent: Boolean = false
    )

    /**
     * Parse the optional `gravity` attribute into [AlignFlags].
     *
     * Accepted forms:
     *   - String: "top", "top|left", "top left" (pipe, whitespace or comma)
     *   - JsonArray: ["top", "left"]
     *
     * Enum values follow `attribute_definitions.json`:
     * top, bottom, centerVertical, left, right, centerHorizontal, center.
     * `start`/`end` are also accepted as RTL-aware aliases for left/right.
     */
    internal fun parseGravity(json: JsonObject): AlignFlags? {
        val element = json.get("gravity") ?: return null
        val tokens: List<String> = when {
            element.isJsonArray ->
                element.asJsonArray.mapNotNull { if (it.isJsonPrimitive) it.asString else null }
            element.isJsonPrimitive ->
                element.asString.split(Regex("[|\\s,]+")).filter { it.isNotEmpty() }
            else -> return null
        }
        if (tokens.isEmpty()) return null

        var alignTop = false
        var alignBottom = false
        var alignLeft = false
        var alignRight = false
        var centerH = false
        var centerV = false
        var centerInParent = false

        tokens.forEach { token ->
            when (token) {
                "top" -> alignTop = true
                "bottom" -> alignBottom = true
                "left", "start" -> alignLeft = true
                "right", "end" -> alignRight = true
                "centerVertical" -> centerV = true
                "centerHorizontal" -> centerH = true
                "center" -> centerInParent = true
            }
        }
        return AlignFlags(alignTop, alignBottom, alignLeft, alignRight, centerH, centerV, centerInParent)
    }

    /**
     * Merge `gravity` with individual align/center booleans. Either source
     * setting a flag switches it on; the two are additive.
     */
    internal fun resolvedAlignFlags(json: JsonObject): AlignFlags {
        val fromGravity = parseGravity(json) ?: AlignFlags()
        return AlignFlags(
            alignTop = fromGravity.alignTop || json.get("alignTop")?.asBoolean == true,
            alignBottom = fromGravity.alignBottom || json.get("alignBottom")?.asBoolean == true,
            alignLeft = fromGravity.alignLeft || json.get("alignLeft")?.asBoolean == true,
            alignRight = fromGravity.alignRight || json.get("alignRight")?.asBoolean == true,
            centerH = fromGravity.centerH || json.get("centerHorizontal")?.asBoolean == true,
            centerV = fromGravity.centerV || json.get("centerVertical")?.asBoolean == true,
            centerInParent = fromGravity.centerInParent || json.get("centerInParent")?.asBoolean == true
        )
    }

    /**
     * Get alignment for child element based on parent type.
     * Returns Alignment value appropriate for the parent type.
     */
    fun getChildAlignment(json: JsonObject, parentType: String): Any? {
        val flags = resolvedAlignFlags(json)
        return when (parentType) {
            "Row", "HStack" -> when {
                flags.alignTop -> Alignment.Top
                flags.alignBottom -> Alignment.Bottom
                flags.centerV || flags.centerInParent -> Alignment.CenterVertically
                else -> null
            }
            "Column", "VStack" -> when {
                flags.alignLeft -> Alignment.Start
                flags.alignRight -> Alignment.End
                flags.centerH || flags.centerInParent -> Alignment.CenterHorizontally
                else -> null
            }
            "Box", "ZStack" -> {
                val hBoth = flags.alignLeft && flags.alignRight
                val vBoth = flags.alignTop && flags.alignBottom

                when {
                    flags.centerInParent -> Alignment.Center
                    hBoth && vBoth -> Alignment.Center
                    hBoth && flags.alignTop -> BiasAlignment(0f, -1f)
                    hBoth && flags.alignBottom -> BiasAlignment(0f, 1f)
                    hBoth -> BiasAlignment(0f, 0f)
                    vBoth && flags.alignLeft -> Alignment.CenterStart
                    vBoth && flags.alignRight -> Alignment.CenterEnd
                    vBoth -> BiasAlignment(0f, 0f)
                    flags.alignTop && flags.alignLeft -> Alignment.TopStart
                    flags.alignTop && flags.alignRight -> Alignment.TopEnd
                    flags.alignTop && flags.centerH -> BiasAlignment(0f, -1f)
                    flags.alignBottom && flags.alignLeft -> Alignment.BottomStart
                    flags.alignBottom && flags.alignRight -> Alignment.BottomEnd
                    flags.alignBottom && flags.centerH -> BiasAlignment(0f, 1f)
                    flags.alignLeft && flags.centerV -> Alignment.CenterStart
                    flags.alignRight && flags.centerV -> Alignment.CenterEnd
                    flags.centerH && flags.centerV -> Alignment.Center
                    flags.alignTop -> BiasAlignment(-1f, -1f)
                    flags.alignBottom -> BiasAlignment(-1f, 1f)
                    flags.alignLeft -> BiasAlignment(-1f, -1f)
                    flags.alignRight -> BiasAlignment(1f, -1f)
                    flags.centerH -> BiasAlignment(0f, -1f)
                    flags.centerV -> BiasAlignment(-1f, 0f)
                    else -> null
                }
            }
            else -> null
        }
    }

    /**
     * build_relative_positioning: ConstraintLayout constraint references.
     * Returns list of constraint strings for ConstraintSet DSL.
     */
    fun buildRelativePositioning(
        json: JsonObject,
        data: Map<String, Any>
    ): List<String> {
        val constraints = mutableListOf<String>()

        val topMargin = constraintMarginValue(json, "topMargin", data)
        val bottomMargin = constraintMarginValue(json, "bottomMargin", data)
        var startMargin = constraintMarginValue(json, "leftMargin", data)
            ?: constraintMarginValue(json, "startMargin", data)
        var endMargin = constraintMarginValue(json, "rightMargin", data)
            ?: constraintMarginValue(json, "endMargin", data)

        // Override from margins array
        json.get("margins")?.let { element ->
            if (element.isJsonArray && element.asJsonArray.size() == 4) {
                val arr = element.asJsonArray
                if (startMargin == null) startMargin = "${arr[3].asFloat}.dp"
                if (endMargin == null) endMargin = "${arr[1].asFloat}.dp"
            }
        }

        fun marginSuffix(margin: String?) = if (margin != null) ", margin = $margin" else ""

        // Relative to other views
        json.get("alignTopOfView")?.asString?.let {
            constraints += "bottom.linkTo($it.top${marginSuffix(bottomMargin)})"
        }
        json.get("alignBottomOfView")?.asString?.let {
            constraints += "top.linkTo($it.bottom${marginSuffix(topMargin)})"
        }
        json.get("alignLeftOfView")?.asString?.let {
            constraints += "end.linkTo($it.start${marginSuffix(endMargin)})"
        }
        json.get("alignRightOfView")?.asString?.let {
            constraints += "start.linkTo($it.end${marginSuffix(startMargin)})"
        }

        // Align edges with other views
        json.get("alignTopView")?.asString?.let {
            val m = topMargin?.let { v -> ", margin = (-$v)" } ?: ""
            constraints += "top.linkTo($it.top$m)"
        }
        json.get("alignBottomView")?.asString?.let {
            val m = bottomMargin?.let { v -> ", margin = (-$v)" } ?: ""
            constraints += "bottom.linkTo($it.bottom$m)"
        }
        json.get("alignLeftView")?.asString?.let {
            val m = startMargin?.let { v -> ", margin = (-$v)" } ?: ""
            constraints += "start.linkTo($it.start$m)"
        }
        json.get("alignRightView")?.asString?.let {
            val m = endMargin?.let { v -> ", margin = (-$v)" } ?: ""
            constraints += "end.linkTo($it.end$m)"
        }

        // Center with other views
        json.get("alignCenterVerticalView")?.asString?.let {
            constraints += "top.linkTo($it.top)"
            constraints += "bottom.linkTo($it.bottom)"
        }
        json.get("alignCenterHorizontalView")?.asString?.let {
            constraints += "start.linkTo($it.start)"
            constraints += "end.linkTo($it.end)"
        }

        // Parent constraints (honors `gravity` in addition to individual flags)
        val flags = resolvedAlignFlags(json)
        if (flags.alignTop) {
            constraints += "top.linkTo(parent.top${marginSuffix(topMargin)})"
        }
        if (flags.alignBottom) {
            constraints += "bottom.linkTo(parent.bottom${marginSuffix(bottomMargin)})"
        }
        if (flags.alignLeft) {
            constraints += "start.linkTo(parent.start${marginSuffix(startMargin)})"
        }
        if (flags.alignRight) {
            constraints += "end.linkTo(parent.end${marginSuffix(endMargin)})"
        }
        if (flags.centerH) {
            constraints += "start.linkTo(parent.start)"
            constraints += "end.linkTo(parent.end)"
        }
        if (flags.centerV) {
            constraints += "top.linkTo(parent.top)"
            constraints += "bottom.linkTo(parent.bottom)"
        }
        if (flags.centerInParent) {
            constraints += "top.linkTo(parent.top)"
            constraints += "bottom.linkTo(parent.bottom)"
            constraints += "start.linkTo(parent.start)"
            constraints += "end.linkTo(parent.end)"
        }

        return constraints
    }

    private fun constraintMarginValue(json: JsonObject, key: String, data: Map<String, Any>): String? {
        val element = json.get(key) ?: return null
        return when {
            element.isJsonPrimitive -> {
                val p = element.asJsonPrimitive
                when {
                    p.isNumber && p.asFloat > 0 -> "${p.asFloat}.dp"
                    p.isString -> {
                        val s = p.asString
                        if (isBinding(s)) {
                            val prop = extractBindingProperty(s) ?: return null
                            "data.$prop.dp"
                        } else {
                            val num = s.toFloatOrNull()
                            if (num != null && num > 0) "${num}.dp" else null
                        }
                    }
                    else -> null
                }
            }
            else -> null
        }
    }

    // ── Composite Builder ────────────────────────────────────────────

    /**
     * Build a complete modifier applying all attributes in the standard order.
     * Matches the order of modifier_builder.rb build methods.
     */
    fun buildModifier(
        json: JsonObject,
        data: Map<String, Any>,
        parentType: String? = null,
        context: Context? = null,
        defaultFillMaxWidth: Boolean = false
    ): Modifier {
        var modifier: Modifier = Modifier

        // 1. testTag
        modifier = applyTestTag(modifier, json)
        // 2. margins
        modifier = applyMargins(modifier, json, data)
        // 3. weight – caller must apply in RowScope/ColumnScope
        // 4. size
        modifier = applySize(modifier, json, defaultFillMaxWidth)
        // 5. alpha
        modifier = applyAlpha(modifier, json, data)
        // 6. shadow
        modifier = applyShadow(modifier, json)
        // 7. background (clip + border + bg)
        modifier = applyBackground(modifier, json, data, context)
        // 8. clickable
        modifier = applyClickable(modifier, json, data)
        // 9. padding
        modifier = applyPadding(modifier, json)
        // 10. alignment – handled by container

        return modifier
    }

    // ── Lifecycle Effects ────────────────────────────────────────────

    /** build_lifecycle_effects: onAppear → LaunchedEffect, onDisappear → DisposableEffect */
    @Composable
    fun ApplyLifecycleEffects(json: JsonObject, data: Map<String, Any>) {
        json.get("onAppear")?.asString?.let { handler ->
            val clean = handler.replace(":", "")
            LaunchedEffect(Unit) {
                (data[clean] as? (() -> Unit))?.invoke()
                    ?: (data[handler] as? (() -> Unit))?.invoke()
            }
        }

        json.get("onDisappear")?.asString?.let { handler ->
            val clean = handler.replace(":", "")
            DisposableEffect(Unit) {
                onDispose {
                    (data[clean] as? (() -> Unit))?.invoke()
                        ?: (data[handler] as? (() -> Unit))?.invoke()
                }
            }
        }
    }

    fun hasLifecycleEvents(json: JsonObject): Boolean {
        return json.has("onAppear") || json.has("onDisappear")
    }
}
