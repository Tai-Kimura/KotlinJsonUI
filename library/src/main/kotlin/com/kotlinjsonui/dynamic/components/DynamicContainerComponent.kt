package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.DynamicView

/**
 * Dynamic Container Component Converter
 * Converts JSON to Container composables (View/Box/Column/Row) at runtime
 * 
 * Supported JSON attributes (matching Ruby implementation):
 * - type: "View" (container type)
 * - orientation: "vertical" | "horizontal" (determines Column vs Row)
 * - child: JsonObject or Array of child components
 * - gravity: String alignment (top, bottom, left, right, center, etc.)
 * - spacing: Number space between children
 * - distribution: String arrangement mode
 * - direction: "bottomToTop" | "rightToLeft" for reverse ordering
 * - background: String hex color for background
 * - borderColor: String hex color for border
 * - borderWidth: Float width of border
 * - cornerRadius: Float corner radius
 * - shadow: Boolean or number for elevation
 * - padding/paddings: Number or Array for padding
 * - margins: Array or individual margin properties
 * - width/height: Number dimensions
 */
class DynamicContainerComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            val orientation = json.get("orientation")?.asString
            val children = parseChildren(json)
            
            // Build modifier with all visual properties
            val modifier = buildModifier(json)
            
            when (orientation) {
                "vertical" -> createColumn(json, modifier, children, data)
                "horizontal" -> createRow(json, modifier, children, data)
                else -> createBox(json, modifier, children, data)
            }
        }
        
        @Composable
        private fun createColumn(
            json: JsonObject,
            modifier: Modifier,
            children: List<JsonObject>,
            data: Map<String, Any>
        ) {
            // Parse gravity/alignment
            val verticalArrangement = parseVerticalArrangement(json)
            val horizontalAlignment = parseHorizontalAlignment(json)
            
            // Check if we need to reverse children
            val finalChildren = if (json.get("direction")?.asString == "bottomToTop") {
                children.reversed()
            } else {
                children
            }
            
            Column(
                modifier = modifier,
                verticalArrangement = verticalArrangement,
                horizontalAlignment = horizontalAlignment
            ) {
                finalChildren.forEach { child ->
                    DynamicView(child, data)
                }
            }
        }
        
        @Composable
        private fun createRow(
            json: JsonObject,
            modifier: Modifier,
            children: List<JsonObject>,
            data: Map<String, Any>
        ) {
            // Parse gravity/alignment
            val horizontalArrangement = parseHorizontalArrangement(json)
            val verticalAlignment = parseVerticalAlignment(json)
            
            // Check if we need to reverse children
            val finalChildren = if (json.get("direction")?.asString == "rightToLeft") {
                children.reversed()
            } else {
                children
            }
            
            Row(
                modifier = modifier,
                horizontalArrangement = horizontalArrangement,
                verticalAlignment = verticalAlignment
            ) {
                finalChildren.forEach { child ->
                    DynamicView(child, data)
                }
            }
        }
        
        @Composable
        private fun createBox(
            json: JsonObject,
            modifier: Modifier,
            children: List<JsonObject>,
            data: Map<String, Any>
        ) {
            // Parse gravity/alignment for Box
            val contentAlignment = parseBoxAlignment(json)
            
            Box(
                modifier = modifier,
                contentAlignment = contentAlignment
            ) {
                children.forEach { child ->
                    DynamicView(child, data)
                }
            }
        }
        
        private fun parseChildren(json: JsonObject): List<JsonObject> {
            return when (val child = json.get("child")) {
                null -> emptyList()
                is JsonObject -> listOf(child.asJsonObject)
                is JsonArray -> child.map { it.asJsonObject }
                else -> emptyList()
            }
        }
        
        private fun parseVerticalArrangement(json: JsonObject): Arrangement.Vertical {
            val spacing = json.get("spacing")?.asFloat
            val distribution = json.get("distribution")?.asString
            val gravity = json.get("gravity")?.asString
            
            return when {
                spacing != null -> Arrangement.spacedBy(spacing.dp)
                distribution == "fillEqually" || distribution == "equalCentering" -> Arrangement.SpaceEvenly
                distribution == "fill" -> Arrangement.SpaceBetween
                distribution == "equalSpacing" -> Arrangement.SpaceAround
                gravity == "top" -> Arrangement.Top
                gravity == "bottom" -> Arrangement.Bottom
                gravity == "centerVertical" -> Arrangement.Center
                else -> Arrangement.Top
            }
        }
        
        private fun parseHorizontalArrangement(json: JsonObject): Arrangement.Horizontal {
            val spacing = json.get("spacing")?.asFloat
            val distribution = json.get("distribution")?.asString
            val gravity = json.get("gravity")?.asString
            
            return when {
                spacing != null -> Arrangement.spacedBy(spacing.dp)
                distribution == "fillEqually" || distribution == "equalCentering" -> Arrangement.SpaceEvenly
                distribution == "fill" -> Arrangement.SpaceBetween
                distribution == "equalSpacing" -> Arrangement.SpaceAround
                gravity == "left" -> Arrangement.Start
                gravity == "right" -> Arrangement.End
                gravity == "centerHorizontal" -> Arrangement.Center
                else -> Arrangement.Start
            }
        }
        
        private fun parseHorizontalAlignment(json: JsonObject): Alignment.Horizontal {
            return when (json.get("gravity")?.asString) {
                "left" -> Alignment.Start
                "right" -> Alignment.End
                "centerHorizontal" -> Alignment.CenterHorizontally
                else -> Alignment.Start
            }
        }
        
        private fun parseVerticalAlignment(json: JsonObject): Alignment.Vertical {
            return when (json.get("gravity")?.asString) {
                "top" -> Alignment.Top
                "bottom" -> Alignment.Bottom
                "centerVertical" -> Alignment.CenterVertically
                else -> Alignment.Top
            }
        }
        
        private fun parseBoxAlignment(json: JsonObject): Alignment {
            return when (json.get("gravity")?.asString) {
                "top" -> Alignment.TopCenter
                "bottom" -> Alignment.BottomCenter
                "left" -> Alignment.CenterStart
                "right" -> Alignment.CenterEnd
                "center" -> Alignment.Center
                "topLeft" -> Alignment.TopStart
                "topRight" -> Alignment.TopEnd
                "bottomLeft" -> Alignment.BottomStart
                "bottomRight" -> Alignment.BottomEnd
                "centerHorizontal" -> Alignment.Center
                "centerVertical" -> Alignment.Center
                else -> Alignment.TopStart
            }
        }
        
        private fun buildModifier(json: JsonObject): Modifier {
            var modifier: Modifier = Modifier
            
            // Width and height
            json.get("width")?.asFloat?.let { width ->
                modifier = if (width < 0) {
                    modifier.fillMaxWidth()
                } else {
                    modifier.width(width.dp)
                }
            }
            
            json.get("height")?.asFloat?.let { height ->
                modifier = if (height < 0) {
                    modifier.fillMaxHeight()
                } else {
                    modifier.height(height.dp)
                }
            }
            
            // Apply margins first
            modifier = applyMargins(modifier, json)
            
            // Background color (before clip for proper rendering)
            json.get("background")?.asString?.let { colorStr ->
                try {
                    val color = Color(android.graphics.Color.parseColor(colorStr))
                    modifier = modifier.background(color)
                } catch (e: Exception) {
                    // Invalid color
                }
            }
            
            // Corner radius (clip)
            json.get("cornerRadius")?.asFloat?.let { radius ->
                val shape = RoundedCornerShape(radius.dp)
                modifier = modifier.clip(shape)
                
                // If we have a border, apply it with the same shape
                json.get("borderColor")?.asString?.let { borderColorStr ->
                    try {
                        val borderColor = Color(android.graphics.Color.parseColor(borderColorStr))
                        val borderWidth = json.get("borderWidth")?.asFloat ?: 1f
                        modifier = modifier.border(borderWidth.dp, borderColor, shape)
                    } catch (e: Exception) {
                        // Invalid border color
                    }
                }
            } ?: run {
                // No corner radius, but might still have border
                json.get("borderColor")?.asString?.let { borderColorStr ->
                    try {
                        val borderColor = Color(android.graphics.Color.parseColor(borderColorStr))
                        val borderWidth = json.get("borderWidth")?.asFloat ?: 1f
                        modifier = modifier.border(borderWidth.dp, borderColor)
                    } catch (e: Exception) {
                        // Invalid border color
                    }
                }
            }
            
            // Shadow/elevation
            json.get("shadow")?.let { shadow ->
                when {
                    shadow.isJsonPrimitive -> {
                        val primitive = shadow.asJsonPrimitive
                        when {
                            primitive.isBoolean && primitive.asBoolean -> {
                                modifier = modifier.shadow(6.dp)
                            }
                            primitive.isNumber -> {
                                modifier = modifier.shadow(primitive.asFloat.dp)
                            }
                        }
                    }
                }
            }
            
            // Apply padding after other visual effects
            modifier = applyPadding(modifier, json)
            
            return modifier
        }
        
        private fun applyPadding(inputModifier: Modifier, json: JsonObject): Modifier {
            var modifier = inputModifier
            
            // Handle paddings array
            json.get("paddings")?.asJsonArray?.let { paddings ->
                return when (paddings.size()) {
                    1 -> modifier.padding(paddings[0].asFloat.dp)
                    2 -> modifier.padding(
                        vertical = paddings[0].asFloat.dp,
                        horizontal = paddings[1].asFloat.dp
                    )
                    4 -> modifier.padding(
                        top = paddings[0].asFloat.dp,
                        end = paddings[1].asFloat.dp,
                        bottom = paddings[2].asFloat.dp,
                        start = paddings[3].asFloat.dp
                    )
                    else -> modifier
                }
            }
            
            // Handle single padding value
            json.get("padding")?.asFloat?.let { padding ->
                return modifier.padding(padding.dp)
            }
            
            // Handle individual padding properties
            val paddingTop = json.get("paddingTop")?.asFloat 
                ?: json.get("paddingVertical")?.asFloat ?: 0f
            val paddingBottom = json.get("paddingBottom")?.asFloat 
                ?: json.get("paddingVertical")?.asFloat ?: 0f
            val paddingStart = json.get("paddingStart")?.asFloat 
                ?: json.get("paddingLeft")?.asFloat 
                ?: json.get("paddingHorizontal")?.asFloat ?: 0f
            val paddingEnd = json.get("paddingEnd")?.asFloat 
                ?: json.get("paddingRight")?.asFloat 
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
        
        private fun applyMargins(inputModifier: Modifier, json: JsonObject): Modifier {
            var modifier = inputModifier
            
            // Handle margins array
            json.get("margins")?.asJsonArray?.let { margins ->
                return when (margins.size()) {
                    1 -> modifier.padding(margins[0].asFloat.dp)
                    2 -> modifier.padding(
                        vertical = margins[0].asFloat.dp,
                        horizontal = margins[1].asFloat.dp
                    )
                    4 -> modifier.padding(
                        top = margins[0].asFloat.dp,
                        end = margins[1].asFloat.dp,
                        bottom = margins[2].asFloat.dp,
                        start = margins[3].asFloat.dp
                    )
                    else -> modifier
                }
            }
            
            // Handle individual margin properties
            val topMargin = json.get("topMargin")?.asFloat ?: 0f
            val bottomMargin = json.get("bottomMargin")?.asFloat ?: 0f
            val leftMargin = json.get("leftMargin")?.asFloat 
                ?: json.get("startMargin")?.asFloat ?: 0f
            val rightMargin = json.get("rightMargin")?.asFloat 
                ?: json.get("endMargin")?.asFloat ?: 0f
            
            return if (topMargin > 0 || bottomMargin > 0 || leftMargin > 0 || rightMargin > 0) {
                modifier.padding(
                    top = topMargin.dp,
                    bottom = bottomMargin.dp,
                    start = leftMargin.dp,
                    end = rightMargin.dp
                )
            } else {
                modifier
            }
        }
    }
}