package com.kotlinjsonui.dynamic.helpers

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.google.gson.JsonObject

/**
 * Helper class for building common Modifier properties from JSON
 * Consolidates padding, margin, size, and other common modifier logic
 */
object ModifierBuilder {
    
    /**
     * Build a complete modifier from JSON including size, margins, padding, and opacity
     * Note: This version does NOT support binding expressions in margins
     * Use buildModifier(json, data, defaultFillMaxWidth) for binding support
     */
    fun buildModifier(
        json: JsonObject,
        defaultFillMaxWidth: Boolean = false
    ): Modifier {
        return buildModifier(json, emptyMap(), defaultFillMaxWidth)
    }

    /**
     * Build a complete modifier from JSON with binding support
     * Includes margins, size, padding, and opacity
     * Note: Order matters in Compose - margins must come BEFORE size
     */
    fun buildModifier(
        json: JsonObject,
        data: Map<String, Any>,
        defaultFillMaxWidth: Boolean = false
    ): Modifier {
        var modifier: Modifier = Modifier

        // Apply margins (outer spacing) with binding support - MUST come before size
        modifier = applyMargins(modifier, json, data)

        // Apply size modifiers
        modifier = applySize(modifier, json, defaultFillMaxWidth)

        // Apply padding (inner spacing)
        modifier = applyPadding(modifier, json)

        // Apply opacity/alpha
        modifier = applyOpacity(modifier, json)

        return modifier
    }
    
    /**
     * Apply weight to modifier for Row/Column distribution
     * Note: This returns the weight value, not a modifier.
     * The actual weight modifier must be applied within RowScope/ColumnScope.
     */
    fun getWeight(json: JsonObject): Float? {
        return json.get("weight")?.asFloat
    }
    
    /**
     * Apply width and height to modifier
     * Handles both numeric values and string values like "matchParent", "wrapContent"
     */
    fun applySize(
        modifier: Modifier,
        json: JsonObject,
        defaultFillMaxWidth: Boolean = false
    ): Modifier {
        var result = modifier
        
        // Width - handle both numeric and string values
        json.get("width")?.let { widthElement ->
            result = when {
                widthElement.isJsonPrimitive -> {
                    val primitive = widthElement.asJsonPrimitive
                    when {
                        primitive.isString -> {
                            when (primitive.asString) {
                                "matchParent", "match_parent" -> result.fillMaxWidth()
                                "wrapContent", "wrap_content" -> result.wrapContentWidth()
                                else -> {
                                    // Try to parse as number
                                    try {
                                        val width = primitive.asString.toFloat()
                                        if (width < 0) result.fillMaxWidth() else result.width(width.dp)
                                    } catch (e: NumberFormatException) {
                                        result
                                    }
                                }
                            }
                        }
                        primitive.isNumber -> {
                            val width = primitive.asFloat
                            if (width < 0) result.fillMaxWidth() else result.width(width.dp)
                        }
                        else -> result
                    }
                }
                else -> result
            }
        } ?: run {
            if (defaultFillMaxWidth) {
                result = result.fillMaxWidth()
            }
        }
        
        // Height - handle both numeric and string values
        json.get("height")?.let { heightElement ->
            result = when {
                heightElement.isJsonPrimitive -> {
                    val primitive = heightElement.asJsonPrimitive
                    when {
                        primitive.isString -> {
                            when (primitive.asString) {
                                "matchParent", "match_parent" -> result.fillMaxHeight()
                                "wrapContent", "wrap_content" -> result.wrapContentHeight()
                                else -> {
                                    // Try to parse as number
                                    try {
                                        val height = primitive.asString.toFloat()
                                        if (height < 0) result.fillMaxHeight() else result.height(height.dp)
                                    } catch (e: NumberFormatException) {
                                        result
                                    }
                                }
                            }
                        }
                        primitive.isNumber -> {
                            val height = primitive.asFloat
                            if (height < 0) result.fillMaxHeight() else result.height(height.dp)
                        }
                        else -> result
                    }
                }
                else -> result
            }
        }
        
        return result
    }
    
    /**
     * Apply margins (outer spacing) to modifier
     * Supports: margins array, individual margin properties
     * Note: This version does NOT support binding expressions
     * Use applyMargins(modifier, json, data) for binding support
     */
    fun applyMargins(modifier: Modifier, json: JsonObject): Modifier {
        return applyMargins(modifier, json, emptyMap())
    }

    /**
     * Apply margins (outer spacing) to modifier with binding support
     * Supports: margins array, individual margin properties, binding expressions like @{propertyName}
     */
    fun applyMargins(modifier: Modifier, json: JsonObject, data: Map<String, Any>): Modifier {
        var result = modifier

        // Handle margins array first
        json.get("margins")?.asJsonArray?.let { margins ->
            return when (margins.size()) {
                1 -> result.padding(margins[0].asFloat.dp)
                2 -> result.padding(
                    vertical = margins[0].asFloat.dp,
                    horizontal = margins[1].asFloat.dp
                )
                4 -> result.padding(
                    top = margins[0].asFloat.dp,
                    end = margins[1].asFloat.dp,
                    bottom = margins[2].asFloat.dp,
                    start = margins[3].asFloat.dp
                )
                else -> result
            }
        }

        // Handle individual margin properties with binding support
        val topMargin = resolveMarginValue(json, "topMargin", data)
                       ?: resolveMarginValue(json, "marginTop", data) ?: 0f
        val bottomMargin = resolveMarginValue(json, "bottomMargin", data)
                          ?: resolveMarginValue(json, "marginBottom", data) ?: 0f
        val leftMargin = resolveMarginValue(json, "leftMargin", data)
                        ?: resolveMarginValue(json, "marginLeft", data)
                        ?: resolveMarginValue(json, "startMargin", data)
                        ?: resolveMarginValue(json, "marginStart", data) ?: 0f
        val rightMargin = resolveMarginValue(json, "rightMargin", data)
                         ?: resolveMarginValue(json, "marginRight", data)
                         ?: resolveMarginValue(json, "endMargin", data)
                         ?: resolveMarginValue(json, "marginEnd", data) ?: 0f

        return if (topMargin > 0 || bottomMargin > 0 || leftMargin > 0 || rightMargin > 0) {
            result.padding(
                top = topMargin.dp,
                bottom = bottomMargin.dp,
                start = leftMargin.dp,
                end = rightMargin.dp
            )
        } else {
            result
        }
    }

    /**
     * Resolve margin value from JSON with binding support
     * Handles both numeric values and binding expressions like @{propertyName}
     */
    private fun resolveMarginValue(json: JsonObject, key: String, data: Map<String, Any>): Float? {
        val element = json.get(key) ?: return null

        return when {
            element.isJsonPrimitive -> {
                val primitive = element.asJsonPrimitive
                when {
                    primitive.isNumber -> primitive.asFloat
                    primitive.isString -> {
                        val stringValue = primitive.asString
                        // Check for binding expression @{propertyName}
                        if (stringValue.startsWith("@{") && stringValue.endsWith("}")) {
                            val evaluated = com.kotlinjsonui.dynamic.DataBindingContext.evaluateExpression(stringValue, data)
                            when (evaluated) {
                                is Number -> evaluated.toFloat()
                                is String -> evaluated.toFloatOrNull() ?: 0f
                                else -> 0f
                            }
                        } else {
                            // Try to parse as number
                            stringValue.toFloatOrNull()
                        }
                    }
                    else -> null
                }
            }
            else -> null
        }
    }
    
    /**
     * Apply padding (inner spacing) to modifier
     * Supports: padding, paddings array, individual padding properties
     */
    fun applyPadding(modifier: Modifier, json: JsonObject): Modifier {
        var result = modifier

        // Handle paddings - can be a single number or an array
        json.get("paddings")?.let { paddingsElement ->
            // Handle single number value
            if (paddingsElement.isJsonPrimitive && paddingsElement.asJsonPrimitive.isNumber) {
                return result.padding(paddingsElement.asFloat.dp)
            }
            // Handle array
            if (!paddingsElement.isJsonArray) return@let
            val paddings = paddingsElement.asJsonArray
            return when (paddings.size()) {
                1 -> result.padding(paddings[0].asFloat.dp)
                2 -> result.padding(
                    vertical = paddings[0].asFloat.dp,
                    horizontal = paddings[1].asFloat.dp
                )
                3 -> result.padding(
                    start = paddings[1].asFloat.dp,
                    top = paddings[0].asFloat.dp,
                    end = paddings[1].asFloat.dp,
                    bottom = paddings[2].asFloat.dp
                )
                4 -> result.padding(
                    top = paddings[0].asFloat.dp,
                    end = paddings[1].asFloat.dp,
                    bottom = paddings[2].asFloat.dp,
                    start = paddings[3].asFloat.dp
                )
                else -> result
            }
        }
        
        // Handle single padding value
        json.get("padding")?.let { paddingElement ->
            when {
                paddingElement.isJsonPrimitive && paddingElement.asJsonPrimitive.isNumber -> {
                    return result.padding(paddingElement.asFloat.dp)
                }
                paddingElement.isJsonArray -> {
                    // If padding is an array, handle it like paddings
                    val paddingArray = paddingElement.asJsonArray
                    return when (paddingArray.size()) {
                        1 -> result.padding(paddingArray[0].asFloat.dp)
                        2 -> result.padding(
                            vertical = paddingArray[0].asFloat.dp,
                            horizontal = paddingArray[1].asFloat.dp
                        )
                        3 -> result.padding(
                            start = paddingArray[1].asFloat.dp,
                            top = paddingArray[0].asFloat.dp,
                            end = paddingArray[1].asFloat.dp,
                            bottom = paddingArray[2].asFloat.dp
                        )
                        4 -> result.padding(
                            top = paddingArray[0].asFloat.dp,
                            end = paddingArray[1].asFloat.dp,
                            bottom = paddingArray[2].asFloat.dp,
                            start = paddingArray[3].asFloat.dp
                        )
                        else -> result
                    }
                }
            }
        }
        
        // Handle individual padding properties
        val paddingTop = json.get("paddingTop")?.asFloat ?: 
                        json.get("topPadding")?.asFloat ?: 
                        json.get("paddingVertical")?.asFloat ?: 0f
        val paddingBottom = json.get("paddingBottom")?.asFloat ?: 
                           json.get("bottomPadding")?.asFloat ?: 
                           json.get("paddingVertical")?.asFloat ?: 0f
        val paddingStart = json.get("paddingStart")?.asFloat ?: 
                          json.get("startPadding")?.asFloat ?: 
                          json.get("paddingLeft")?.asFloat ?: 
                          json.get("leftPadding")?.asFloat ?: 
                          json.get("paddingHorizontal")?.asFloat ?: 0f
        val paddingEnd = json.get("paddingEnd")?.asFloat ?: 
                        json.get("endPadding")?.asFloat ?: 
                        json.get("paddingRight")?.asFloat ?: 
                        json.get("rightPadding")?.asFloat ?: 
                        json.get("paddingHorizontal")?.asFloat ?: 0f
        
        return if (paddingTop > 0 || paddingBottom > 0 || paddingStart > 0 || paddingEnd > 0) {
            result.padding(
                top = paddingTop.dp,
                bottom = paddingBottom.dp,
                start = paddingStart.dp,
                end = paddingEnd.dp
            )
        } else {
            result
        }
    }
    
    /**
     * Apply opacity/alpha to modifier
     * Supports both "opacity" and "alpha" JSON properties
     * Values are expected to be between 0.0 and 1.0
     */
    fun applyOpacity(modifier: Modifier, json: JsonObject): Modifier {
        // Check for opacity first, then alpha
        val opacityValue = json.get("opacity")?.asFloat ?: json.get("alpha")?.asFloat
        
        return if (opacityValue != null) {
            // Clamp the value between 0 and 1
            val clampedAlpha = opacityValue.coerceIn(0f, 1f)
            modifier.alpha(clampedAlpha)
        } else {
            modifier
        }
    }
    
    /**
     * Apply only size modifiers (width/height) without padding/margins
     * Useful for components that handle spacing internally
     */
    fun buildSizeModifier(
        json: JsonObject,
        defaultFillMaxWidth: Boolean = false
    ): Modifier {
        return applySize(Modifier, json, defaultFillMaxWidth)
    }
    
    /**
     * Apply only spacing modifiers (margins and padding)
     */
    fun buildSpacingModifier(json: JsonObject): Modifier {
        var modifier: Modifier = Modifier
        modifier = applyMargins(modifier, json)
        modifier = applyPadding(modifier, json)
        return modifier
    }
    
    /**
     * Get alignment for child element based on parent type
     * Returns appropriate Alignment value or null if no alignment specified
     * 
     * @param json Child's JSON configuration
     * @param parentType Type of parent container ("Row", "Column", "Box")
     * @return Alignment value appropriate for the parent type
     */
    fun getChildAlignment(json: JsonObject, parentType: String): Any? {
        return when (parentType) {
            "Row", "HStack" -> {
                // In Row, only vertical alignment is valid
                when {
                    json.get("alignTop")?.asBoolean == true -> Alignment.Top
                    json.get("alignBottom")?.asBoolean == true -> Alignment.Bottom
                    json.get("centerVertical")?.asBoolean == true -> Alignment.CenterVertically
                    else -> null
                }
            }
            "Column", "VStack" -> {
                // In Column, only horizontal alignment is valid
                when {
                    json.get("alignLeft")?.asBoolean == true -> Alignment.Start
                    json.get("alignRight")?.asBoolean == true -> Alignment.End
                    json.get("centerHorizontal")?.asBoolean == true -> Alignment.CenterHorizontally
                    else -> null
                }
            }
            "Box", "ZStack" -> {
                // In Box, all alignments are valid
                val alignTop = json.get("alignTop")?.asBoolean == true
                val alignBottom = json.get("alignBottom")?.asBoolean == true  
                val alignLeft = json.get("alignLeft")?.asBoolean == true
                val alignRight = json.get("alignRight")?.asBoolean == true
                val centerHorizontal = json.get("centerHorizontal")?.asBoolean == true
                val centerVertical = json.get("centerVertical")?.asBoolean == true
                val centerInParent = json.get("centerInParent")?.asBoolean == true
                
                when {
                    centerInParent -> Alignment.Center
                    
                    // Use BiasAlignment for single-direction alignments that need edge positioning
                    alignTop && !alignBottom && !alignLeft && !alignRight && !centerHorizontal -> 
                        BiasAlignment(-1f, -1f) // Top edge, horizontally start
                    alignBottom && !alignTop && !alignLeft && !alignRight && !centerHorizontal -> 
                        BiasAlignment(-1f, 1f) // Bottom edge, horizontally start
                    alignLeft && !alignRight && !alignTop && !alignBottom && !centerVertical -> 
                        BiasAlignment(-1f, -1f) // Left edge, vertically top
                    alignRight && !alignLeft && !alignTop && !alignBottom && !centerVertical -> 
                        BiasAlignment(1f, -1f) // Right edge, vertically top
                    
                    // Combined alignments with standard Alignment
                    alignTop && alignLeft -> Alignment.TopStart
                    alignTop && alignRight -> Alignment.TopEnd
                    alignTop && centerHorizontal -> Alignment.TopCenter
                    alignBottom && alignLeft -> Alignment.BottomStart
                    alignBottom && alignRight -> Alignment.BottomEnd
                    alignBottom && centerHorizontal -> Alignment.BottomCenter
                    centerVertical && alignLeft -> Alignment.CenterStart
                    centerVertical && alignRight -> Alignment.CenterEnd
                    centerVertical && centerHorizontal -> Alignment.Center
                    
                    // Center alignments for single-direction center
                    centerHorizontal -> BiasAlignment(0f, -1f) // Horizontally centered, top edge
                    centerVertical -> BiasAlignment(-1f, 0f) // Vertically centered, left edge
                    
                    else -> null
                }
            }
            else -> null
        }
    }

    /**
     * Check if JSON has lifecycle events (onAppear/onDisappear)
     */
    fun hasLifecycleEvents(json: JsonObject): Boolean {
        return json.has("onAppear") || json.has("onDisappear")
    }

    /**
     * Get onAppear handler name from JSON
     */
    fun getOnAppearHandler(json: JsonObject): String? {
        return json.get("onAppear")?.asString
    }

    /**
     * Get onDisappear handler name from JSON
     */
    fun getOnDisappearHandler(json: JsonObject): String? {
        return json.get("onDisappear")?.asString
    }

    /**
     * Apply lifecycle effects (onAppear/onDisappear) from JSON
     * Should be called at the beginning of a Composable function
     *
     * @param json The JSON object containing lifecycle event handlers
     * @param data The data map containing function references for event handlers
     */
    @Composable
    fun ApplyLifecycleEffects(json: JsonObject, data: Map<String, Any>) {
        // Handle onAppear
        json.get("onAppear")?.asString?.let { handlerName ->
            // Clean up handler name (remove colon if present)
            val cleanHandler = handlerName.replace(":", "")

            LaunchedEffect(Unit) {
                // Try to find and invoke the handler function from data
                (data[cleanHandler] as? (() -> Unit))?.invoke()
                    ?: (data[handlerName] as? (() -> Unit))?.invoke()
            }
        }

        // Handle onDisappear
        json.get("onDisappear")?.asString?.let { handlerName ->
            // Clean up handler name (remove colon if present)
            val cleanHandler = handlerName.replace(":", "")

            DisposableEffect(Unit) {
                onDispose {
                    // Try to find and invoke the handler function from data
                    (data[cleanHandler] as? (() -> Unit))?.invoke()
                        ?: (data[handlerName] as? (() -> Unit))?.invoke()
                }
            }
        }
    }
}