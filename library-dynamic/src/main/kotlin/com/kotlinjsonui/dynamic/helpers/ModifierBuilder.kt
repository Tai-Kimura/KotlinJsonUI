package com.kotlinjsonui.dynamic.helpers

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventTimeoutCancellationException
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.disabled
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
                valueExpr != null -> {
                    // Payload without an id (e.g. onPan/onPinch on an id-less
                    // node): (T) -> Unit gets the payload, () -> Unit ignores it.
                    @Suppress("UNCHECKED_CAST")
                    (fn as? (Any) -> Unit)?.invoke(valueExpr)
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
                    1 -> modifier.padding((dimen(arr[0], data) ?: 0f).dp)
                    2 -> modifier.padding(
                        vertical = (dimen(arr[0], data) ?: 0f).dp,
                        horizontal = (dimen(arr[1], data) ?: 0f).dp
                    )
                    4 -> modifier.padding(
                        top = (dimen(arr[0], data) ?: 0f).dp,
                        end = (dimen(arr[1], data) ?: 0f).dp,
                        bottom = (dimen(arr[2], data) ?: 0f).dp,
                        start = (dimen(arr[3], data) ?: 0f).dp
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
                            // Canonical number value context; unresolved →
                            // attribute default (no margin).
                            DataBindingContext.resolveNumber(s, data)?.toFloat() ?: 0f
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
    fun getWeight(json: JsonObject, data: Map<String, Any> = emptyMap()): Float? {
        return dimen(json.get("weight"), data)?.takeIf { it > 0 }
    }

    /** build_size: frame object, width/height, matchParent/wrapContent, min/max, aspectRatio */
    fun applySize(
        modifier: Modifier,
        json: JsonObject,
        defaultFillMaxWidth: Boolean = false,
        data: Map<String, Any> = emptyMap()
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

        // Ordering per canonical size.maxBoundsClampFill (shared/core/
        // attribute_semantics.json): a min/max bound must precede a FILL —
        // fillMaxWidth() first pins the incoming constraints and a widthIn
        // after it is a no-op, which is exactly the matchParent+maxWidth
        // bug this ordering fixes. An EXPLICIT numeric size is the opposite
        // case: the declared dimension wins and the bound stays inert, so
        // the bound is applied after it (mirrors kjui codegen 847fb56).

        // Width
        val hasWeight = json.has("weight")
        val widthElement = json.get("width")
        var widthBoundsApplied = false
        if (widthElement != null) {
            val skipWidth = hasWeight && widthElement.isJsonPrimitive &&
                    widthElement.asJsonPrimitive.isNumber && widthElement.asFloat == 0f
            if (!skipWidth) {
                if (isFillDimension(widthElement)) {
                    result = applyWidthConstraints(result, json)
                    widthBoundsApplied = true
                }
                result = applySingleDimension(result, json, "width", isWidth = true)
            }
        } else if (defaultFillMaxWidth) {
            result = applyWidthConstraints(result, json)
            widthBoundsApplied = true
            result = result.fillMaxWidth()
        }
        if (!widthBoundsApplied) {
            result = applyWidthConstraints(result, json)
        }

        // Height
        val hasHeightWeight = json.has("heightWeight")
        val heightElement = json.get("height")
        var heightBoundsApplied = false
        if (heightElement != null) {
            val skipHeight = hasHeightWeight && heightElement.isJsonPrimitive &&
                    heightElement.asJsonPrimitive.isNumber && heightElement.asFloat == 0f
            if (!skipHeight) {
                if (isFillDimension(heightElement)) {
                    result = applyHeightConstraints(result, json)
                    heightBoundsApplied = true
                }
                result = applySingleDimension(result, json, "height", isWidth = false)
            }
        }
        if (!heightBoundsApplied) {
            result = applyHeightConstraints(result, json)
        }

        return applyAspectRatio(result, json)
    }

    /** matchParent (or a negative number, the legacy fill spelling)? */
    private fun isFillDimension(element: com.google.gson.JsonElement): Boolean {
        if (!element.isJsonPrimitive) return false
        val p = element.asJsonPrimitive
        return when {
            p.isString -> p.asString == "matchParent" || p.asString == "match_parent" ||
                    (p.asString.toFloatOrNull()?.let { it < 0 } ?: false)
            p.isNumber -> p.asFloat < 0
            else -> false
        }
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

    private fun applyConstraints(
        modifier: Modifier,
        json: JsonObject,
        data: Map<String, Any> = emptyMap()
    ): Modifier {
        // Frame-object path: explicit sizes, bounds stay inert after them.
        return applyAspectRatio(
            applyHeightConstraints(applyWidthConstraints(modifier, json, data), json, data),
            json, data
        )
    }

    private fun applyWidthConstraints(
        modifier: Modifier,
        json: JsonObject,
        data: Map<String, Any> = emptyMap()
    ): Modifier {
        var result = modifier
        val hasWidth = json.has("width")
        val maxWidth = parseOptionalDp(json.get("maxWidth"), data)
        val minWidth = parseOptionalDp(json.get("minWidth"), data)
        if (minWidth != null && maxWidth != null) {
            result = result.widthIn(min = minWidth, max = maxWidth)
        } else if (maxWidth != null) {
            result = if (!hasWidth) result.wrapContentWidth().widthIn(max = maxWidth)
            else result.widthIn(max = maxWidth)
        } else if (minWidth != null) {
            result = result.widthIn(min = minWidth)
        }
        return result
    }

    private fun applyHeightConstraints(
        modifier: Modifier,
        json: JsonObject,
        data: Map<String, Any> = emptyMap()
    ): Modifier {
        var result = modifier
        val hasHeight = json.has("height")
        val maxHeight = parseOptionalDp(json.get("maxHeight"), data)
        val minHeight = parseOptionalDp(json.get("minHeight"), data)
        if (minHeight != null && maxHeight != null) {
            result = result.heightIn(min = minHeight, max = maxHeight)
        } else if (maxHeight != null) {
            result = if (!hasHeight) result.wrapContentHeight().heightIn(max = maxHeight)
            else result.heightIn(max = maxHeight)
        } else if (minHeight != null) {
            result = result.heightIn(min = minHeight)
        }
        return result
    }

    private fun applyAspectRatio(
        modifier: Modifier,
        json: JsonObject,
        data: Map<String, Any> = emptyMap()
    ): Modifier {
        val aw = dimen(json.get("aspectWidth"), data)
        val ah = dimen(json.get("aspectHeight"), data)
        return if (aw != null && ah != null && ah > 0) {
            modifier.aspectRatio(aw / ah)
        } else modifier
    }

    /**
     * A bound min/max used to be DROPPED here (the isNumber guard let the
     * binding string through as null), so the bound bounds fixtures measured
     * inert. Resolving first keeps the guard's safety — an unresolved binding
     * is still null — while letting a resolvable one through.
     */
    private fun parseOptionalDp(element: com.google.gson.JsonElement?, data: Map<String, Any>): Dp? =
        dimen(element, data)?.dp

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
                    // Canonical number value context (dot paths, `??`
                    // default); unresolved → attribute default (opaque).
                    val alphaVal = DataBindingContext.resolveNumber(s, data)?.toFloat() ?: 1f
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
    fun applyShadow(
        modifier: Modifier,
        json: JsonObject,
        data: Map<String, Any> = emptyMap()
    ): Modifier {
        val shadowElement = json.get("shadow") ?: return modifier
        val cornerRadius = dimen(json.get("cornerRadius"), data)
        val shape = if (cornerRadius != null) RoundedCornerShape(cornerRadius.dp) else RectangleShape

        return when {
            shadowElement.isJsonPrimitive && shadowElement.asJsonPrimitive.isString -> {
                // The string form is the UIKit pipe contract
                // 'color|offsetX|offsetY|opacity|radius' — exactly five
                // fields; anything else draws nothing (the canonical guard
                // all render paths share).
                val parts = shadowElement.asString.split("|")
                if (parts.size != 5) return modifier
                val color = ColorParser.parseColorString(parts[0]) ?: return modifier
                val x = parts[1].toFloatOrNull() ?: return modifier
                val y = parts[2].toFloatOrNull() ?: return modifier
                val alpha = parts[3].toFloatOrNull() ?: return modifier
                val radius = parts[4].toFloatOrNull() ?: return modifier
                modifier.dropShadow(
                    shape = shape,
                    shadow = Shadow(
                        radius = radius.dp,
                        color = color,
                        offset = DpOffset(x.dp, y.dp),
                        alpha = alpha.coerceIn(0f, 1f)
                    )
                )
            }
            shadowElement.isJsonObject -> {
                val obj = shadowElement.asJsonObject
                val radius = dimen(obj.get("radius"), data) ?: 4f
                val color = obj.get("color")?.takeIf { it.isJsonPrimitive }
                    ?.let { ColorParser.parseColorString(it.asString) }
                val offsetX = dimen(obj.get("offsetX"), data) ?: 0f
                val offsetY = dimen(obj.get("offsetY"), data) ?: 0f
                val alpha = dimen(obj.get("opacity"), data) ?: 1f
                modifier.dropShadow(
                    shape = shape,
                    shadow = Shadow(
                        radius = radius.dp,
                        color = color ?: Color.Black,
                        offset = DpOffset(offsetX.dp, offsetY.dp),
                        alpha = alpha.coerceIn(0f, 1f)
                    )
                )
            }
            shadowElement.isJsonPrimitive && shadowElement.asJsonPrimitive.isNumber -> {
                modifier.dropShadow(shape = shape, shadow = Shadow(radius = (dimen(shadowElement, data) ?: 4f).dp))
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
        val cornerRadius = dimen(json.get("cornerRadius"), data)
        val bgColor = ColorParser.parseColorWithBinding(json, "background", data, context)
        val borderColor = ColorParser.parseColorWithBinding(json, "borderColor", data, context)
        val borderWidth = dimen(json.get("borderWidth"), data)
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

        // clipToBounds — last in the background group, exactly where
        // build_background emits it in the kjui codegen (modifier_builder.rb).
        // Compose does not clip a layout's children by default, so an
        // overflowing row/column drew past its declared box on the dynamic
        // path while the codegen path clipped (34: `common/clipToBounds`
        // rendered pixel-identical to its control on android).
        if (resolveClipToBounds(json, data) == true) {
            result = result.clipToBounds()
        }

        return result
    }

    /**
     * common.clipToBounds — literal boolean or `@{binding}` (the attribute is
     * declared binding-capable). The codegen's Ruby `if json_data[...]` is
     * truthy for the binding STRING itself; resolving it here keeps a bound
     * `false` off instead of frozen on.
     */
    /**
     * A dimension slot's value, with `@{binding}` resolved BEFORE any numeric
     * parse. `element.asFloat` on a binding string throws
     * NumberFormatException — on the dynamic path that is a crash, not a
     * compile error, and it took the whole android conformance lane down
     * (`@{boundCornerRadius}`, `@{boundPaddingTop}`, …).
     *
     * The attribute name stays a literal at the CALL SITE (`json.get("x")`)
     * so the coverage scans that grep for spellings keep seeing it.
     */
    internal fun dimen(element: com.google.gson.JsonElement?, data: Map<String, Any>): Float? =
        ResourceResolver.resolveFloatElement(element, data)

    internal fun resolveClipToBounds(json: JsonObject, data: Map<String, Any>): Boolean? {
        val raw = json.get("clipToBounds") ?: return null
        if (!raw.isJsonPrimitive) return null
        val p = raw.asJsonPrimitive
        if (p.isBoolean) return p.asBoolean
        if (p.isString && isBinding(p.asString)) {
            return DataBindingContext.resolveBoolean(p.asString, data)
        }
        if (p.isString) return p.asString.equals("true", ignoreCase = true)
        return null
    }

    /** build_clickable: onClick/onclick → .clickable { handler }, onLongPress → long-press gesture */
    fun applyClickable(
        modifier: Modifier,
        json: JsonObject,
        data: Map<String, Any>
    ): Modifier {
        // onLongPress first: the outer pointerInput sees the gesture before the
        // inner .clickable, fires after the long-press timeout and consumes the
        // remaining events, so a long press never also triggers onClick.
        var result = applyLongPressable(modifier, json, data)
        result = applyPannable(result, json, data)
        result = applyPinchable(result, json, data)
        val enabled = resolveEnabled(json, data)
        val handler = json.get("onClick")?.asString ?: json.get("onclick")?.asString
        if (handler != null) {
            val viewId = json.get("id")?.asString
            result = result.clickable(enabled = enabled != false) {
                resolveEventHandler(handler, data, viewId)
            }
        }
        // `common.enabled` must be readable from the a11y tree (that is what a
        // UI test asserts), with or without a click handler — mirrors the
        // static codegen's build_disabled_semantics. The kjui codegen got this
        // in 2026-07; the dynamic path had been skipped, so the View-hosted
        // enabled__false fixture failed here while passing on web.
        if (enabled == false) {
            result = result.semantics { disabled() }
        }
        return result
    }

    /**
     * common.enabled — resolved value, or null when the attribute is absent.
     * Accepts the literal boolean and the `@{binding}` form (canonical bool
     * value context via DataBindingContext); an unresolved binding falls back
     * to the attribute default (enabled).
     */
    private fun resolveEnabled(json: JsonObject, data: Map<String, Any>): Boolean? {
        val raw = json.get("enabled") ?: return null
        if (!raw.isJsonPrimitive) return null
        val p = raw.asJsonPrimitive
        if (p.isBoolean) return p.asBoolean
        if (p.isString && isBinding(p.asString)) {
            return DataBindingContext.resolveBoolean(p.asString, data)
        }
        return null
    }

    /**
     * onLongPress (common attribute, platform swift/kotlin) → long-press
     * gesture. Kept separate from [applyClickable] so components with a
     * native onClick parameter (e.g. Button) can add long-press support
     * without a second click handler.
     *
     * Detection watches the Initial pointer pass: inner click handlers
     * (Button's own `.clickable`, later modifiers in this chain) consume the
     * down event in the Main pass, which would starve a `detectTapGestures`
     * based detector. Watching Initial sees every gesture; when the press
     * outlives the long-press timeout the handler fires and the remaining
     * events are consumed so the inner onClick does not also fire.
     *
     * `data` participates in the pointerInput keys: when the handler map is
     * replaced (state-driven hosts rebuild it per recomposition) the gesture
     * coroutine restarts and captures the fresh closures.
     */
    fun applyLongPressable(
        modifier: Modifier,
        json: JsonObject,
        data: Map<String, Any>
    ): Modifier {
        val handler = json.get("onLongPress")?.asString ?: return modifier
        val viewId = json.get("id")?.asString
        return modifier.pointerInput(handler, data) {
            awaitEachGesture {
                awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                val longPressed = try {
                    withTimeout(viewConfiguration.longPressTimeoutMillis) {
                        var event: PointerEvent
                        do {
                            event = awaitPointerEvent(PointerEventPass.Initial)
                        } while (event.changes.any { it.pressed })
                    }
                    false
                } catch (_: PointerEventTimeoutCancellationException) {
                    true
                }
                if (longPressed) {
                    resolveEventHandler(handler, data, viewId)
                    // Swallow the rest of the gesture (including the up) so
                    // inner click handlers treat it as cancelled.
                    var event: PointerEvent
                    do {
                        event = awaitPointerEvent(PointerEventPass.Initial)
                        event.changes.forEach { it.consume() }
                    } while (event.changes.any { it.pressed })
                }
            }
        }
    }

    /**
     * onPan (common attribute) → drag gesture. Fires on every drag event with
     * the cumulative translation (Offset) since the gesture began —
     * accumulated from per-event deltas so the payload matches SwiftUI's
     * DragGesture.Value.translation. Declaring onPan means this node owns
     * drags: detectDragGestures consumes them, so a surrounding scroll
     * container will not also scroll from touches on this node.
     *
     * The host-contract () -> Unit closure is reached through
     * [resolveEventHandler]'s cast ladder; an (Offset)-typed handler
     * receives the payload.
     */
    fun applyPannable(
        modifier: Modifier,
        json: JsonObject,
        data: Map<String, Any>
    ): Modifier {
        val handler = json.get("onPan")?.asString ?: return modifier
        val viewId = json.get("id")?.asString
        return modifier.pointerInput(handler, data) {
            var total = Offset.Zero
            detectDragGestures(
                onDragStart = { total = Offset.Zero },
                onDrag = { change, dragAmount ->
                    change.consume()
                    total += dragAmount
                    resolveEventHandler(handler, data, viewId, total)
                }
            )
        }
    }

    /**
     * onPinch (common attribute) → pinch/zoom gesture. Fires with the
     * cumulative scale factor since the gesture began (Float, matching
     * MagnifyGesture.Value.magnification on iOS). A raw awaitEachGesture
     * loop rather than detectTransformGestures because the scale must reset
     * per gesture and detectTransformGestures has no gesture-start hook.
     * calculateZoom() is 1f for single-pointer events, so taps and
     * one-finger drags pass through untouched (onPan and onClick on the
     * same node keep working).
     */
    fun applyPinchable(
        modifier: Modifier,
        json: JsonObject,
        data: Map<String, Any>
    ): Modifier {
        val handler = json.get("onPinch")?.asString ?: return modifier
        val viewId = json.get("id")?.asString
        return modifier.pointerInput(handler, data) {
            awaitEachGesture {
                awaitFirstDown(requireUnconsumed = false)
                var scale = 1f
                var event: PointerEvent
                do {
                    event = awaitPointerEvent()
                    val zoom = event.calculateZoom()
                    if (zoom != 1f) {
                        scale *= zoom
                        event.changes.forEach { it.consume() }
                        resolveEventHandler(handler, data, viewId, scale)
                    }
                } while (event.changes.any { it.pressed })
            }
        }
    }

    /** build_padding: padding/paddings (array/single), individual padding properties */
    fun applyPadding(
        modifier: Modifier,
        json: JsonObject,
        data: Map<String, Any> = emptyMap()
    ): Modifier {
        // Handle paddings attribute first
        json.get("paddings")?.let { element ->
            if (element.isJsonPrimitive && element.asJsonPrimitive.isNumber) {
                return modifier.padding((dimen(element, data) ?: 0f).dp)
            }
            if (element.isJsonArray) {
                val arr = element.asJsonArray
                return when (arr.size()) {
                    1 -> modifier.padding((dimen(arr[0], data) ?: 0f).dp)
                    2 -> modifier.padding(
                        vertical = (dimen(arr[0], data) ?: 0f).dp,
                        horizontal = (dimen(arr[1], data) ?: 0f).dp
                    )
                    3 -> modifier.padding(
                        start = (dimen(arr[1], data) ?: 0f).dp,
                        top = (dimen(arr[0], data) ?: 0f).dp,
                        end = (dimen(arr[1], data) ?: 0f).dp,
                        bottom = (dimen(arr[2], data) ?: 0f).dp
                    )
                    4 -> modifier.padding(
                        top = (dimen(arr[0], data) ?: 0f).dp,
                        end = (dimen(arr[1], data) ?: 0f).dp,
                        bottom = (dimen(arr[2], data) ?: 0f).dp,
                        start = (dimen(arr[3], data) ?: 0f).dp
                    )
                    else -> modifier
                }
            }
        }

        // Handle single padding value
        json.get("padding")?.let { element ->
            when {
                element.isJsonPrimitive && element.asJsonPrimitive.isNumber -> {
                    return modifier.padding((dimen(element, data) ?: 0f).dp)
                }
                element.isJsonArray -> {
                    val arr = element.asJsonArray
                    return when (arr.size()) {
                        1 -> modifier.padding((dimen(arr[0], data) ?: 0f).dp)
                        2 -> modifier.padding(
                            vertical = (dimen(arr[0], data) ?: 0f).dp,
                            horizontal = (dimen(arr[1], data) ?: 0f).dp
                        )
                        3 -> modifier.padding(
                            start = (dimen(arr[1], data) ?: 0f).dp,
                            top = (dimen(arr[0], data) ?: 0f).dp,
                            end = (dimen(arr[1], data) ?: 0f).dp,
                            bottom = (dimen(arr[2], data) ?: 0f).dp
                        )
                        4 -> modifier.padding(
                            top = (dimen(arr[0], data) ?: 0f).dp,
                            end = (dimen(arr[1], data) ?: 0f).dp,
                            bottom = (dimen(arr[2], data) ?: 0f).dp,
                            start = (dimen(arr[3], data) ?: 0f).dp
                        )
                        else -> modifier
                    }
                }
            }
        }

        // Individual padding properties
        val paddingTop = dimen(json.get("paddingTop"), data)
            ?: dimen(json.get("topPadding"), data)
            ?: dimen(json.get("paddingVertical"), data) ?: 0f
        val paddingBottom = dimen(json.get("paddingBottom"), data)
            ?: dimen(json.get("bottomPadding"), data)
            ?: dimen(json.get("paddingVertical"), data) ?: 0f
        val paddingStart = dimen(json.get("paddingStart"), data)
            ?: dimen(json.get("startPadding"), data)
            ?: dimen(json.get("paddingLeft"), data)
            ?: dimen(json.get("leftPadding"), data)
            ?: dimen(json.get("paddingHorizontal"), data) ?: 0f
        val paddingEnd = dimen(json.get("paddingEnd"), data)
            ?: dimen(json.get("endPadding"), data)
            ?: dimen(json.get("paddingRight"), data)
            ?: dimen(json.get("rightPadding"), data)
            ?: dimen(json.get("paddingHorizontal"), data) ?: 0f

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

    /**
     * Parse `paddings`/`padding` into [PaddingValues] for composables that
     * take content padding as a parameter instead of a modifier (SelectBox:
     * selectbox_component.rb emits `contentPadding = PaddingValues(...)` and
     * never a `.padding()` modifier there — a modifier padding would inset
     * the component's self-drawn border/background instead of its content).
     *
     * The 4-element order is the JSON convention [top, right, bottom, left]
     * (same as the padding modifier): right -> end, left -> start.
     * Returns null when neither key is present.
     */
    fun parseContentPadding(
        json: JsonObject,
        data: Map<String, Any> = emptyMap()
    ): PaddingValues? {
        val element = json.get("paddings") ?: json.get("padding") ?: return null
        if (element.isJsonPrimitive && element.asJsonPrimitive.isNumber) {
            return PaddingValues((dimen(element, data) ?: 0f).dp)
        }
        if (element.isJsonArray) {
            val arr = element.asJsonArray
            return when (arr.size()) {
                1 -> PaddingValues((dimen(arr[0], data) ?: 0f).dp)
                2 -> PaddingValues(
                    vertical = (dimen(arr[0], data) ?: 0f).dp,
                    horizontal = (dimen(arr[1], data) ?: 0f).dp
                )
                4 -> PaddingValues(
                    top = (dimen(arr[0], data) ?: 0f).dp,
                    end = (dimen(arr[1], data) ?: 0f).dp,
                    bottom = (dimen(arr[2], data) ?: 0f).dp,
                    start = (dimen(arr[3], data) ?: 0f).dp
                )
                else -> null
            }
        }
        return null
    }

    /** build_alignment: parent_type (Row/Column/Box) dependent alignment */
    fun applyAlignment(
        modifier: Modifier,
        json: JsonObject,
        parentType: String?,
        data: Map<String, Any> = emptyMap()
    ): Modifier {
        val alignment = getChildAlignment(json, parentType ?: return modifier, data)
            ?: return modifier

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
        return foldGravityTokens(tokens)
    }

    private fun foldGravityTokens(tokens: List<String>): AlignFlags {
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
     * `alignment` is the SwiftUI-spelled string alternative to `gravity`
     * (attribute_definitions.json) and resolves to the same child-positioning
     * flags. The nine values follow the SwiftUI reading — `top` is
     * top-and-horizontally-centred, `leading` is leading-and-vertically-
     * centred — and the token table is the canon shared with the static
     * converter (ALIGNMENT_GRAVITY in container_component.rb); change both
     * together, never one alone. Case-insensitive like the static side.
     */
    private val ALIGNMENT_GRAVITY_TOKENS: Map<String, List<String>> = mapOf(
        "topleading" to listOf("top", "left"),
        "top" to listOf("top", "centerHorizontal"),
        "toptrailing" to listOf("top", "right"),
        "leading" to listOf("centerVertical", "left"),
        "center" to listOf("center"),
        "trailing" to listOf("centerVertical", "right"),
        "bottomleading" to listOf("bottom", "left"),
        "bottom" to listOf("bottom", "centerHorizontal"),
        "bottomtrailing" to listOf("bottom", "right")
    )

    internal fun parseAlignmentString(json: JsonObject): AlignFlags? {
        val element = json.get("alignment") ?: return null
        if (!element.isJsonPrimitive || !element.asJsonPrimitive.isString) return null
        val tokens = ALIGNMENT_GRAVITY_TOKENS[element.asString.lowercase()] ?: return null
        return foldGravityTokens(tokens)
    }

    /**
     * INNER content-alignment flags: `gravity`, or the `alignment` string
     * alternative when gravity is absent (gravity wins when both are set —
     * same precedence as the static converter).
     *
     * Declaration-faithful (2026-08-02, parity family kjui-dynamic-alignment):
     * the node's own outer placement booleans (`alignTop` / `centerInParent` /
     * …) mean "align to PARENT" per attribute_definitions.json and must not
     * leak into the node's children — they used to be folded in here, so a
     * `centerInParent` container silently centered its own children too,
     * while the declared `alignment` string was never read at all.
     * For the node's placement inside its parent use [outerAlignFlags].
     */
    internal fun resolvedAlignFlags(json: JsonObject): AlignFlags {
        return parseGravity(json) ?: parseAlignmentString(json) ?: AlignFlags()
    }

    /**
     * OUTER placement flags: only the individual align/center booleans.
     *
     * Canonical semantics (matches build_alignment /
     * build_relative_positioning in the static modifier_builder.rb):
     * `alignTop`/`alignBottom`/`alignLeft`/`alignRight`/`center*` place the
     * node inside its parent, while `gravity` places the node's own children
     * and must NOT contribute here — otherwise a child like
     * `{ gravity: "center", alignBottom: true, alignRight: true }` gets
     * centered in the parent instead of bottom-end aligned.
     */
    internal fun outerAlignFlags(
        json: JsonObject,
        data: Map<String, Any> = emptyMap()
    ): AlignFlags = AlignFlags(
        // gson's asBoolean on a STRING is Boolean.parseBoolean, so a binding
        // form silently read as `false` and the placement was dropped without
        // a word — the bound align/center fixtures measured inert for that
        // reason, not because the flags did nothing. Resolving keeps a literal
        // false false and lets a bound true through.
        alignTop = ResourceResolver.resolveBoolean(json, "alignTop", data),
        alignBottom = ResourceResolver.resolveBoolean(json, "alignBottom", data),
        alignLeft = ResourceResolver.resolveBoolean(json, "alignLeft", data),
        alignRight = ResourceResolver.resolveBoolean(json, "alignRight", data),
        centerH = ResourceResolver.resolveBoolean(json, "centerHorizontal", data),
        centerV = ResourceResolver.resolveBoolean(json, "centerVertical", data),
        centerInParent = ResourceResolver.resolveBoolean(json, "centerInParent", data)
    )

    /**
     * Get alignment for child element based on parent type.
     * Returns Alignment value appropriate for the parent type.
     * Reads the OUTER placement booleans only — the child's `gravity` is
     * consumed by the child's own container path, not by its parent.
     */
    fun getChildAlignment(
        json: JsonObject,
        parentType: String,
        data: Map<String, Any> = emptyMap()
    ): Any? {
        val flags = outerAlignFlags(json, data)
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
                if (startMargin == null) startMargin = "${dimen(arr[3], data) ?: 0f}.dp"
                if (endMargin == null) endMargin = "${dimen(arr[1], data) ?: 0f}.dp"
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

        // Parent constraints (individual align booleans only — `gravity` is
        // inner content alignment and never links the node to its parent,
        // matching build_relative_positioning in the static tool)
        val flags = outerAlignFlags(json, data)
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
        modifier = applySize(modifier, json, defaultFillMaxWidth, data)
        // 5. alpha
        modifier = applyAlpha(modifier, json, data)
        // 6. shadow
        modifier = applyShadow(modifier, json, data)
        // 7. background (clip + border + bg)
        modifier = applyBackground(modifier, json, data, context)
        // 8. clickable
        modifier = applyClickable(modifier, json, data)
        // 9. padding
        modifier = applyPadding(modifier, json, data)
        // 10. alignment – handled by container

        return modifier
    }

    // ── Lifecycle Effects ────────────────────────────────────────────

    /** build_lifecycle_effects: onAppear → LaunchedEffect, onDisappear → DisposableEffect */
    // Handlers arrive as Any in the data map; erasure makes the () -> Unit
    // cast uncheckable, and a wrong-typed value degrades to a no-op via `as?`.
    @Suppress("UNCHECKED_CAST")
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
