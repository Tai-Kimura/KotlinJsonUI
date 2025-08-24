package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.processDataBinding

/**
 * Dynamic Button Component Converter
 * Converts JSON to Button composable at runtime
 * 
 * Supported JSON attributes (matching Ruby implementation):
 * - text: String button label (supports @{variable} binding)
 * - onclick: String method name for click handler
 * - enabled/disabled: Boolean to enable/disable button
 * - fontSize: Int text size
 * - fontColor: String hex color for text
 * - fontWeight: String weight (normal, bold, etc.)
 * - background: String hex color for background
 * - borderColor: String hex color for border
 * - borderWidth: Float width of border
 * - cornerRadius: Float corner radius
 * - padding/paddings: Number or Array for content padding
 * - margins: Array or individual margin properties
 * - width/height: Number dimensions
 * - shadow: Boolean or number for elevation
 */
class DynamicButtonComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            // Parse button text with data binding
            val rawText = json.get("text")?.asString ?: "Button"
            val text = processDataBinding(rawText, data)
            
            // Parse enabled state
            val isEnabled = when {
                json.get("disabled")?.asBoolean == true -> false
                json.get("enabled")?.asBoolean == false -> false
                else -> true
            }
            
            // Parse onclick handler
            val onClick: () -> Unit = {
                json.get("onclick")?.asString?.let { methodName ->
                    val handler = data[methodName]
                    if (handler is Function<*>) {
                        try {
                            @Suppress("UNCHECKED_CAST")
                            (handler as () -> Unit)()
                        } catch (e: Exception) {
                            // Handler doesn't match expected signature
                        }
                    }
                }
            }
            
            // Parse text style
            val fontSize = json.get("fontSize")?.asInt ?: 14
            val fontWeight = when (json.get("fontWeight")?.asString?.lowercase()) {
                "bold" -> FontWeight.Bold
                "semibold" -> FontWeight.SemiBold
                "medium" -> FontWeight.Medium
                "light" -> FontWeight.Light
                "thin" -> FontWeight.Thin
                else -> FontWeight.Normal
            }
            
            // Parse colors
            val textColor = json.get("fontColor")?.asString?.let {
                try { Color(android.graphics.Color.parseColor(it)) }
                catch (e: Exception) { null }
            }
            
            val backgroundColor = json.get("background")?.asString?.let {
                try { Color(android.graphics.Color.parseColor(it)) }
                catch (e: Exception) { null }
            }
            
            // Parse shape
            val cornerRadius = json.get("cornerRadius")?.asFloat
            val shape = cornerRadius?.let { RoundedCornerShape(it.dp) }
            
            // Parse elevation/shadow
            val elevation = when (val shadow = json.get("shadow")) {
                null -> null
                else -> {
                    if (shadow.isJsonPrimitive) {
                        if (shadow.asJsonPrimitive.isBoolean && shadow.asBoolean) {
                            ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                        } else if (shadow.asJsonPrimitive.isNumber) {
                            ButtonDefaults.buttonElevation(defaultElevation = shadow.asFloat.dp)
                        } else null
                    } else null
                }
            }
            
            // Build button colors
            val colors = ButtonDefaults.buttonColors(
                containerColor = backgroundColor ?: ButtonDefaults.buttonColors().containerColor,
                contentColor = textColor ?: ButtonDefaults.buttonColors().contentColor,
                disabledContainerColor = backgroundColor?.copy(alpha = 0.5f) 
                    ?: ButtonDefaults.buttonColors().disabledContainerColor,
                disabledContentColor = textColor?.copy(alpha = 0.5f)
                    ?: ButtonDefaults.buttonColors().disabledContentColor
            )
            
            // Build content padding
            val contentPadding = buildContentPadding(json)
            
            // Build modifier
            val modifier = buildModifier(json)
            
            // Create the button
            Button(
                onClick = onClick,
                modifier = modifier,
                enabled = isEnabled,
                shape = shape ?: ButtonDefaults.shape,
                colors = colors,
                elevation = elevation,
                contentPadding = contentPadding
            ) {
                Text(
                    text = text,
                    fontSize = fontSize.sp,
                    fontWeight = fontWeight
                )
            }
        }
        
        private fun buildContentPadding(json: JsonObject): PaddingValues {
            // Check for paddings array
            json.get("paddings")?.asJsonArray?.let { paddings ->
                return when (paddings.size()) {
                    1 -> PaddingValues(paddings[0].asFloat.dp)
                    2 -> PaddingValues(
                        vertical = paddings[0].asFloat.dp,
                        horizontal = paddings[1].asFloat.dp
                    )
                    3 -> {
                        // Three values: [top, horizontal, bottom]
                        // PaddingValues doesn't have this constructor, so use 4-parameter version
                        PaddingValues(
                            start = paddings[1].asFloat.dp,
                            top = paddings[0].asFloat.dp,
                            end = paddings[1].asFloat.dp,
                            bottom = paddings[2].asFloat.dp
                        )
                    }
                    4 -> PaddingValues(
                        start = paddings[3].asFloat.dp,
                        top = paddings[0].asFloat.dp,
                        end = paddings[1].asFloat.dp,
                        bottom = paddings[2].asFloat.dp
                    )
                    else -> ButtonDefaults.ContentPadding
                }
            }
            
            // Check for single padding value
            json.get("padding")?.asFloat?.let { padding ->
                return PaddingValues(padding.dp)
            }
            
            // Check for individual padding values
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
                PaddingValues(
                    top = paddingTop.dp,
                    bottom = paddingBottom.dp,
                    start = paddingStart.dp,
                    end = paddingEnd.dp
                )
            } else {
                ButtonDefaults.ContentPadding
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
                modifier = modifier.height(height.dp)
            }
            
            // Margins (Button doesn't use padding modifier, uses contentPadding)
            json.get("margins")?.asJsonArray?.let { margins ->
                modifier = when (margins.size()) {
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
            
            // Individual margin properties
            val topMargin = json.get("topMargin")?.asFloat ?: 0f
            val bottomMargin = json.get("bottomMargin")?.asFloat ?: 0f
            val leftMargin = json.get("leftMargin")?.asFloat 
                ?: json.get("startMargin")?.asFloat ?: 0f
            val rightMargin = json.get("rightMargin")?.asFloat 
                ?: json.get("endMargin")?.asFloat ?: 0f
            
            if (topMargin > 0 || bottomMargin > 0 || leftMargin > 0 || rightMargin > 0) {
                modifier = modifier.padding(
                    top = topMargin.dp,
                    bottom = bottomMargin.dp,
                    start = leftMargin.dp,
                    end = rightMargin.dp
                )
            }
            
            return modifier
        }
    }
}