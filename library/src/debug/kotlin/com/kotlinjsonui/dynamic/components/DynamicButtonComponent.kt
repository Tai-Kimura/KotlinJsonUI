package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.JsonObject
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.dynamic.processDataBinding
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

            // Loading state
            var isLoading by remember {
                mutableStateOf(
                    json.get("isLoading")?.asBoolean ?: false
                )
            }

            // Parse enabled state (disabled when loading)
            val isEnabled = when {
                isLoading -> false
                json.get("disabled")?.asBoolean == true -> false
                json.get("enabled")?.asBoolean == false -> false
                else -> true
            }

            // Parse onclick handler with loading support
            val onClick: () -> Unit = {
                if (!isLoading) {
                    json.get("onclick")?.asString?.let { methodName ->
                        val handler = data[methodName]
                        if (handler is Function<*>) {
                            try {
                                // Check if handler is async (suspend function)
                                val isAsync = json.get("async")?.asBoolean ?: false
                                if (isAsync) {
                                    isLoading = true
                                    CoroutineScope(Dispatchers.Main).launch {
                                        try {
                                            @Suppress("UNCHECKED_CAST")
                                            withContext(Dispatchers.IO) {
                                                (handler as suspend () -> Unit)()
                                            }
                                        } catch (e: Exception) {
                                            // Try as regular function
                                            try {
                                                @Suppress("UNCHECKED_CAST")
                                                (handler as () -> Unit)()
                                            } catch (e: Exception) {
                                                // Handler doesn't match expected signature
                                            }
                                        } finally {
                                            isLoading = false
                                        }
                                    }
                                } else {
                                    @Suppress("UNCHECKED_CAST")
                                    (handler as () -> Unit)()
                                }
                            } catch (e: Exception) {
                                // Handler doesn't match expected signature
                            }
                        }
                    }
                }
            }

            // Parse text style
            val fontSize = json.get("fontSize")?.asInt ?: Configuration.Button.defaultFontSize
            val fontWeight = when (json.get("fontWeight")?.asString?.lowercase()) {
                "bold" -> FontWeight.Bold
                "semibold" -> FontWeight.SemiBold
                "medium" -> FontWeight.Medium
                "light" -> FontWeight.Light
                "thin" -> FontWeight.Thin
                else -> FontWeight.Normal
            }

            // Parse colors with Configuration defaults
            val textColor = json.get("fontColor")?.asString?.let {
                ColorParser.parseColorString(it)
            } ?: Configuration.Button.defaultTextColor

            val backgroundColor = json.get("background")?.asString?.let {
                ColorParser.parseColorString(it)
            } ?: Configuration.Button.defaultBackgroundColor

            // Parse shape with Configuration default
            val cornerRadius = json.get("cornerRadius")?.asFloat ?: Configuration.Button.defaultCornerRadius.toFloat()
            val shape = RoundedCornerShape(cornerRadius.dp)

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
                containerColor = backgroundColor,
                contentColor = textColor,
                disabledContainerColor = backgroundColor.copy(alpha = 0.5f),
                disabledContentColor = textColor.copy(alpha = 0.5f)
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
                shape = shape,
                colors = colors,
                elevation = elevation,
                contentPadding = contentPadding
            ) {
                if (isLoading) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = textColor ?: Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = json.get("loadingText")?.asString ?: text,
                            fontSize = fontSize.sp,
                            fontWeight = fontWeight
                        )
                    }
                } else {
                    Text(
                        text = text,
                        fontSize = fontSize.sp,
                        fontWeight = fontWeight
                    )
                }
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
            json.get("padding")?.let { paddingElement ->
                when {
                    paddingElement.isJsonPrimitive && paddingElement.asJsonPrimitive.isNumber -> {
                        return PaddingValues(paddingElement.asFloat.dp)
                    }
                    paddingElement.isJsonArray -> {
                        val paddingArray = paddingElement.asJsonArray
                        return when (paddingArray.size()) {
                            1 -> PaddingValues(paddingArray[0].asFloat.dp)
                            2 -> PaddingValues(
                                vertical = paddingArray[0].asFloat.dp,
                                horizontal = paddingArray[1].asFloat.dp
                            )
                            4 -> PaddingValues(
                                start = paddingArray[3].asFloat.dp,
                                top = paddingArray[0].asFloat.dp,
                                end = paddingArray[1].asFloat.dp,
                                bottom = paddingArray[2].asFloat.dp
                            )
                            else -> ButtonDefaults.ContentPadding
                        }
                    }
                }
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
            // Use ModifierBuilder for size and margins only 
            // (padding is handled separately as contentPadding for Button)
            // Order: margins first, then size (same as static generation)
            var modifier: Modifier = Modifier
            modifier = ModifierBuilder.applyMargins(modifier, json)
            modifier = ModifierBuilder.applySize(modifier, json)
            modifier = ModifierBuilder.applyOpacity(modifier, json)
            return modifier
        }
    }
}
