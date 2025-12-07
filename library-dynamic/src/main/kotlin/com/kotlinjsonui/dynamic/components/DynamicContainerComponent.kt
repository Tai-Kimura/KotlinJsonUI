package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.DynamicView
import com.kotlinjsonui.dynamic.helpers.ColorParser
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
            // Apply lifecycle effects first
            ModifierBuilder.ApplyLifecycleEffects(json, data)

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
                    // Get weight for this child
                    val weightValue = ModifierBuilder.getWeight(child)
                    // Only use weight if it's greater than 0
                    val weight = if (weightValue != null && weightValue > 0f) weightValue else null
                    
                    // Get alignment for this child
                    val childAlignment = ModifierBuilder.getChildAlignment(child, "Column")
                    
                    // If weight is specified, modify the child JSON to fill available space
                    val modifiedChild = if (weight != null) {
                        val childCopy = child.deepCopy()
                        // In a Column, weighted children should fill width
                        childCopy.addProperty("width", "matchParent")
                        // If height is 0, set it to fill
                        val heightValue = child.get("height")
                        if (heightValue != null && heightValue.isJsonPrimitive &&
                            heightValue.asJsonPrimitive.isNumber && heightValue.asFloat == 0f) {
                            childCopy.remove("height")
                        }
                        childCopy
                    } else {
                        child
                    }
                    
                    // Apply weight and/or alignment modifiers if needed
                    when {
                        weight != null && childAlignment != null && childAlignment is Alignment.Horizontal -> {
                            Box(
                                modifier = Modifier
                                    .weight(weight)
                                    .fillMaxWidth()
                                    .align(childAlignment)
                            ) {
                                DynamicView(modifiedChild, data)
                            }
                        }
                        weight != null -> {
                            Box(
                                modifier = Modifier
                                    .weight(weight)
                                    .fillMaxWidth()
                            ) {
                                DynamicView(modifiedChild, data)
                            }
                        }
                        childAlignment != null && childAlignment is Alignment.Horizontal -> {
                            Box(
                                modifier = Modifier.align(childAlignment)
                            ) {
                                DynamicView(child, data)
                            }
                        }
                        else -> {
                            DynamicView(child, data)
                        }
                    }
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
                    // Get weight for this child (also check widthWeight for Row)
                    val weightValue = ModifierBuilder.getWeight(child) 
                        ?: child.get("widthWeight")?.asFloat
                    // Only use weight if it's greater than 0
                    val weight = if (weightValue != null && weightValue > 0f) weightValue else null
                    
                    // Get alignment for this child
                    val childAlignment = ModifierBuilder.getChildAlignment(child, "Row")
                    
                    // If weight is specified, modify the child JSON to fill available space
                    val modifiedChild = if (weight != null) {
                        val childCopy = child.deepCopy()
                        // In a Row, weighted children should fill height
                        childCopy.addProperty("height", "matchParent")
                        // If width is 0, remove it to let weight handle it
                        val widthValue = child.get("width")
                        if (widthValue != null && widthValue.isJsonPrimitive &&
                            widthValue.asJsonPrimitive.isNumber && widthValue.asFloat == 0f) {
                            childCopy.remove("width")
                        }
                        childCopy
                    } else {
                        child
                    }
                    
                    // Apply weight and/or alignment modifiers if needed
                    when {
                        weight != null && childAlignment != null && childAlignment is Alignment.Vertical -> {
                            Box(
                                modifier = Modifier
                                    .weight(weight)
                                    .fillMaxHeight()
                                    .align(childAlignment)
                            ) {
                                DynamicView(modifiedChild, data)
                            }
                        }
                        weight != null -> {
                            Box(
                                modifier = Modifier
                                    .weight(weight)
                                    .fillMaxHeight()
                            ) {
                                DynamicView(modifiedChild, data)
                            }
                        }
                        childAlignment != null && childAlignment is Alignment.Vertical -> {
                            Box(
                                modifier = Modifier.align(childAlignment)
                            ) {
                                DynamicView(child, data)
                            }
                        }
                        else -> {
                            DynamicView(child, data)
                        }
                    }
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
                    // Get alignment for this child
                    val childAlignment = ModifierBuilder.getChildAlignment(child, "Box")
                    
                    // Apply alignment modifier if needed
                    when (childAlignment) {
                        is Alignment -> {
                            Box(
                                modifier = Modifier.align(childAlignment)
                            ) {
                                DynamicView(child, data)
                            }
                        }
                        is BiasAlignment -> {
                            Box(
                                modifier = Modifier.align(childAlignment)
                            ) {
                                DynamicView(child, data)
                            }
                        }
                        else -> {
                            DynamicView(child, data)
                        }
                    }
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
            // Build modifier with correct order: size -> margins -> background -> padding
            var modifier: Modifier = Modifier
            
            // 1. Apply size
            modifier = ModifierBuilder.applySize(modifier, json)
            
            // 2. Apply margins (outer spacing)
            modifier = ModifierBuilder.applyMargins(modifier, json)
            
            // 3. Background color (before padding so padding is inside the background)
            json.get("background")?.asString?.let { colorStr ->
                ColorParser.parseColorString(colorStr)?.let { color ->
                    modifier = modifier.background(color)
                }
            }
            
            // Corner radius (clip)
            json.get("cornerRadius")?.asFloat?.let { radius ->
                val shape = RoundedCornerShape(radius.dp)
                modifier = modifier.clip(shape)
                
                // If we have a border, apply it with the same shape
                json.get("borderColor")?.asString?.let { borderColorStr ->
                    ColorParser.parseColorString(borderColorStr)?.let { borderColor ->
                        val borderWidth = json.get("borderWidth")?.asFloat ?: 1f
                        modifier = modifier.border(borderWidth.dp, borderColor, shape)
                    }
                }
            } ?: run {
                // No corner radius, but might still have border
                json.get("borderColor")?.asString?.let { borderColorStr ->
                    ColorParser.parseColorString(borderColorStr)?.let { borderColor ->
                        val borderWidth = json.get("borderWidth")?.asFloat ?: 1f
                        modifier = modifier.border(borderWidth.dp, borderColor)
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
            
            // 4. Apply padding (inner spacing) - MUST be applied last
            modifier = ModifierBuilder.applyPadding(modifier, json)
            
            // 5. Apply opacity
            modifier = ModifierBuilder.applyOpacity(modifier, json)
            
            return modifier
        }
    }
}