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
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder

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
            val children = parseChildren(json)
            
            // Check if any child has relative positioning attributes
            if (hasRelativePositioning(children)) {
                // Use ConstraintLayout for relative positioning
                DynamicConstraintLayoutComponent.create(json, data)
                return
            }
            
            val orientation = json.get("orientation")?.asString
            
            // Build modifier with all visual properties
            val modifier = buildModifier(json)
            
            when (orientation) {
                "vertical" -> createColumn(json, modifier, children, data)
                "horizontal" -> createRow(json, modifier, children, data)
                else -> createBox(json, modifier, children, data)
            }
        }
        
        private fun hasRelativePositioning(children: List<JsonObject>): Boolean {
            val relativeAttrs = listOf(
                "alignTopOfView", "alignBottomOfView", "alignLeftOfView", "alignRightOfView",
                "alignTopView", "alignBottomView", "alignLeftView", "alignRightView",
                "alignCenterVerticalView", "alignCenterHorizontalView"
            )
            
            return children.any { child ->
                relativeAttrs.any { attr -> child.has(attr) }
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
            // Support both 'child' and 'children' fields
            val child = json.get("child") ?: json.get("children") ?: return emptyList()
            
            return when {
                child.isJsonObject -> listOf(child.asJsonObject)
                child.isJsonArray -> {
                    val array = child.asJsonArray
                    val result = mutableListOf<JsonObject>()
                    for (element in array) {
                        if (element.isJsonObject) {
                            result.add(element.asJsonObject)
                        }
                    }
                    result
                }
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
            // Use ModifierBuilder for basic size and spacing
            var modifier = ModifierBuilder.buildModifier(json)
            
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
            
            return modifier
        }
    }
}