package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.processDataBinding

/**
 * Dynamic Image Component Converter
 * Converts JSON to Image composable at runtime
 * 
 * Supported JSON attributes (matching Ruby implementation):
 * - src: String resource name (supports @{variable} binding)
 * - contentDescription: String for accessibility
 * - contentMode: String ("aspectFill", "aspectFit", "center")
 * - size: Number for square size
 * - width/height: Number dimensions
 * - padding/paddings: Number or Array for padding
 * - margins: Array or individual margin properties
 * - tint: String hex color for tinting
 * - alpha: Float opacity value (0-1)
 */
class DynamicImageComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            // Parse image source with data binding
            val rawSrc = json.get("src")?.asString ?: "placeholder"
            val imageName = processDataBinding(rawSrc, data)
            
            // Get content description
            val contentDescription = json.get("contentDescription")?.asString ?: ""
            
            // Try to get the resource ID
            val context = LocalContext.current
            val resourceId = context.resources.getIdentifier(
                imageName,
                "drawable",
                context.packageName
            )
            
            // Build modifier
            val modifier = buildModifier(json)
            
            // Parse content scale/mode
            val contentScale = when (json.get("contentMode")?.asString?.lowercase()) {
                "aspectfill" -> ContentScale.Crop
                "aspectfit" -> ContentScale.Fit
                "center" -> ContentScale.None
                "fill" -> ContentScale.FillBounds
                "inside" -> ContentScale.Inside
                else -> ContentScale.Fit // Default
            }
            
            // Parse alpha
            val alpha = json.get("alpha")?.asFloat ?: 1f
            
            // Create the Image
            if (resourceId != 0) {
                Image(
                    painter = painterResource(id = resourceId),
                    contentDescription = contentDescription,
                    modifier = modifier,
                    contentScale = contentScale,
                    alpha = alpha
                )
            } else {
                // Fallback for missing image - could show placeholder or error image
                // For now, just create an empty composable
            }
        }
        
        private fun buildModifier(json: JsonObject): Modifier {
            var modifier: Modifier = Modifier
            
            // Handle size attribute (square size)
            json.get("size")?.asFloat?.let { size ->
                modifier = modifier.size(size.dp)
            } ?: run {
                // Handle width and height separately
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
            }
            
            // Apply margins first
            modifier = applyMargins(modifier, json)
            
            // Apply padding after margins
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