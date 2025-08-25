package com.kotlinjsonui.dynamic.helpers

import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.gson.JsonObject

/**
 * Helper class for building common Modifier properties from JSON
 * Consolidates padding, margin, size, and other common modifier logic
 */
object ModifierBuilder {
    
    /**
     * Build a complete modifier from JSON including size, margins, and padding
     */
    fun buildModifier(
        json: JsonObject,
        defaultFillMaxWidth: Boolean = false
    ): Modifier {
        var modifier: Modifier = Modifier
        
        // Apply size modifiers
        modifier = applySize(modifier, json, defaultFillMaxWidth)
        
        // Apply margins (outer spacing)
        modifier = applyMargins(modifier, json)
        
        // Apply padding (inner spacing)
        modifier = applyPadding(modifier, json)
        
        return modifier
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
     */
    fun applyMargins(modifier: Modifier, json: JsonObject): Modifier {
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
        
        // Handle individual margin properties
        val topMargin = json.get("topMargin")?.asFloat ?: 
                       json.get("marginTop")?.asFloat ?: 0f
        val bottomMargin = json.get("bottomMargin")?.asFloat ?: 
                          json.get("marginBottom")?.asFloat ?: 0f
        val leftMargin = json.get("leftMargin")?.asFloat ?: 
                        json.get("marginLeft")?.asFloat ?: 
                        json.get("startMargin")?.asFloat ?: 
                        json.get("marginStart")?.asFloat ?: 0f
        val rightMargin = json.get("rightMargin")?.asFloat ?: 
                         json.get("marginRight")?.asFloat ?: 
                         json.get("endMargin")?.asFloat ?: 
                         json.get("marginEnd")?.asFloat ?: 0f
        
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
     * Apply padding (inner spacing) to modifier
     * Supports: padding, paddings array, individual padding properties
     */
    fun applyPadding(modifier: Modifier, json: JsonObject): Modifier {
        var result = modifier
        
        // Handle paddings array first
        json.get("paddings")?.asJsonArray?.let { paddings ->
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
}