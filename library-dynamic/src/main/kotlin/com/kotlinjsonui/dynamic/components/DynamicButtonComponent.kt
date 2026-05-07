package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.JsonObject
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ResourceResolver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Button component → Button composable.
 * Reference: button_component.rb in kjui_tools.
 *
 * Click event handling:
 *   onclick (lowercase) → selector format (string only)
 *   onClick (camelCase) → binding format only (@{functionName})
 */
class DynamicButtonComponent {
    companion object {
        @Composable
        fun create(json: JsonObject, data: Map<String, Any> = emptyMap()) {
            val context = LocalContext.current

            // Resolve button text with binding + resource support
            val text = ResourceResolver.resolveText(json, "text", data, context)
                .ifEmpty { "Button" }

            // Loading state
            var isLoading by remember {
                mutableStateOf(json.get("isLoading")?.asBoolean ?: false)
            }

            // Enabled state (supports @{binding})
            val isEnabled = when {
                isLoading -> false
                else -> ResourceResolver.resolveBoolean(json, "enabled", data, default = true) &&
                        !ResourceResolver.resolveBoolean(json, "disabled", data, default = false)
            }

            // Click handler
            val viewId = json.get("id")?.asString ?: "button"
            val onClick: () -> Unit = buildClickHandler(json, data, viewId, isLoading) { loading ->
                isLoading = loading
            }

            // Text style
            val fontSize = json.get("fontSize")?.asFloat
                ?: Configuration.Button.defaultFontSize.toFloat()
            val fontWeight = when (json.get("fontWeight")?.asString?.lowercase()) {
                "bold" -> FontWeight.Bold
                "semibold" -> FontWeight.SemiBold
                "medium" -> FontWeight.Medium
                "light" -> FontWeight.Light
                "thin" -> FontWeight.Thin
                else -> FontWeight.Normal
            }

            // Colors (supports @{binding})
            val textColor = ColorParser.parseColorWithBinding(json, "fontColor", data, context)
                ?: Configuration.Button.defaultTextColor
            val backgroundColor = ColorParser.parseColorWithBinding(json, "background", data, context)
                ?: Configuration.Button.defaultBackgroundColor
            val disabledBgColor = ColorParser.parseColorWithBinding(json, "disabledBackground", data, context)
                ?: backgroundColor.copy(alpha = 0.5f)
            val disabledTextColor = ColorParser.parseColorWithBinding(json, "disabledFontColor", data, context)
                ?: textColor.copy(alpha = 0.5f)
            // `highlightBackground` is the pressed-state container color (tool-emitted
            // per the backfill catalog). `hilightColor` is a legacy typo alias kept
            // for compatibility until backfill lands the `aliases` declaration.
            val pressedBgColor = ColorParser.parseColorWithBinding(json, "highlightBackground", data, context)
                ?: ColorParser.parseColorWithBinding(json, "hilightBackground", data, context)

            // Shape
            val cornerRadius = json.get("cornerRadius")?.asFloat
                ?: Configuration.Button.defaultCornerRadius.toFloat()
            val shape = RoundedCornerShape(cornerRadius.dp)

            // Pressed-state container swap via interactionSource (only when highlightBackground is set).
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val containerColor = if (pressedBgColor != null && isPressed) pressedBgColor else backgroundColor

            // Button colors
            val colors = ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = textColor,
                disabledContainerColor = disabledBgColor,
                disabledContentColor = disabledTextColor
            )

            // Border
            val borderColor = ColorParser.parseColorWithBinding(json, "borderColor", data, context)
            val borderWidth = json.get("borderWidth")?.asFloat ?: 1f
            val border = borderColor?.let { BorderStroke(borderWidth.dp, it) }

            // Content padding
            val contentPadding = buildContentPadding(json)

            // Elevation
            val elevation = resolveElevation(json)

            // Optional leading/trailing icon
            val imageResId = json.get("image")?.asString?.let {
                ResourceResolver.resolveDrawable(it, data, context).takeIf { id -> id != 0 }
            }
            val imagePosition = json.get("imagePosition")?.asString?.lowercase() ?: "leading"
            val iconSize = json.get("iconSize")?.asFloat ?: 18f

            // Text alignment (Button-level). Compose default is center; tool allows override.
            val textAlign = json.get("textAlign")?.asString?.let { align ->
                when (align.lowercase()) {
                    "left", "start" -> TextAlign.Start
                    "right", "end" -> TextAlign.End
                    "center" -> TextAlign.Center
                    else -> null
                }
            }

            // Modifier: only testTag, margins, size, weight, alpha (not padding – handled by contentPadding)
            var modifier: Modifier = Modifier
            modifier = ModifierBuilder.applyTestTag(modifier, json)
            modifier = ModifierBuilder.applyMargins(modifier, json, data)
            modifier = ModifierBuilder.applySize(modifier, json)
            modifier = ModifierBuilder.applyAlpha(modifier, json, data)

            Button(
                onClick = onClick,
                modifier = modifier,
                enabled = isEnabled,
                shape = shape,
                colors = colors,
                elevation = elevation,
                border = border,
                contentPadding = contentPadding,
                interactionSource = interactionSource
            ) {
                if (isLoading) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = textColor
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = json.get("loadingText")?.asString ?: text,
                            fontSize = fontSize.sp,
                            fontWeight = fontWeight,
                            textAlign = textAlign
                        )
                    }
                } else if (imageResId != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (imagePosition == "leading") {
                            Icon(
                                painter = painterResource(id = imageResId),
                                contentDescription = null,
                                modifier = Modifier.size(iconSize.dp),
                                tint = textColor
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = text,
                            fontSize = fontSize.sp,
                            fontWeight = fontWeight,
                            textAlign = textAlign
                        )
                        if (imagePosition == "trailing") {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                painter = painterResource(id = imageResId),
                                contentDescription = null,
                                modifier = Modifier.size(iconSize.dp),
                                tint = textColor
                            )
                        }
                    }
                } else {
                    Text(
                        text = text,
                        fontSize = fontSize.sp,
                        fontWeight = fontWeight,
                        textAlign = textAlign
                    )
                }
            }
        }

        // ── Click Handler ──

        private fun buildClickHandler(
            json: JsonObject,
            data: Map<String, Any>,
            viewId: String,
            isLoading: Boolean,
            setLoading: (Boolean) -> Unit
        ): () -> Unit = {
            if (!isLoading) {
                val methodName = resolveClickMethodName(json)
                methodName?.let { name ->
                    val handler = data[name]
                    if (handler is Function<*>) {
                        val isAsync = json.get("async")?.asBoolean ?: false
                        if (isAsync) {
                            setLoading(true)
                            CoroutineScope(Dispatchers.Main).launch {
                                try {
                                    @Suppress("UNCHECKED_CAST")
                                    withContext(Dispatchers.IO) {
                                        (handler as suspend () -> Unit)()
                                    }
                                } catch (_: Exception) {
                                    try {
                                        @Suppress("UNCHECKED_CAST")
                                        (handler as () -> Unit)()
                                    } catch (_: Exception) {}
                                } finally {
                                    setLoading(false)
                                }
                            }
                        } else {
                            ModifierBuilder.resolveEventHandler(
                                json.get("onclick")?.asString ?: json.get("onClick")?.asString,
                                data, viewId
                            )
                        }
                    }
                }
            }
        }

        /**
         * Resolve click method name from onclick/onClick attributes.
         * onclick → selector format (string only)
         * onClick → binding format only (@{functionName})
         */
        private fun resolveClickMethodName(json: JsonObject): String? {
            json.get("onclick")?.asString?.let { value ->
                if (!value.contains("@{")) return value
            }
            json.get("onClick")?.asString?.let { value ->
                return ModifierBuilder.extractBindingProperty(value)
            }
            return null
        }

        // ── Content Padding ──

        private fun buildContentPadding(json: JsonObject): PaddingValues {
            // paddings (array or single number)
            json.get("paddings")?.let { element ->
                if (element.isJsonPrimitive && element.asJsonPrimitive.isNumber) {
                    return PaddingValues(element.asFloat.dp)
                }
                if (element.isJsonArray) {
                    return parsePaddingArray(element.asJsonArray)
                }
            }

            // padding (single value or array)
            json.get("padding")?.let { element ->
                if (element.isJsonPrimitive && element.asJsonPrimitive.isNumber) {
                    return PaddingValues(element.asFloat.dp)
                }
                if (element.isJsonArray) {
                    return parsePaddingArray(element.asJsonArray)
                }
            }

            // Individual padding values
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

        private fun parsePaddingArray(arr: com.google.gson.JsonArray): PaddingValues {
            return when (arr.size()) {
                1 -> PaddingValues(arr[0].asFloat.dp)
                2 -> PaddingValues(
                    vertical = arr[0].asFloat.dp,
                    horizontal = arr[1].asFloat.dp
                )
                3 -> PaddingValues(
                    start = arr[1].asFloat.dp,
                    top = arr[0].asFloat.dp,
                    end = arr[1].asFloat.dp,
                    bottom = arr[2].asFloat.dp
                )
                4 -> PaddingValues(
                    start = arr[3].asFloat.dp,
                    top = arr[0].asFloat.dp,
                    end = arr[1].asFloat.dp,
                    bottom = arr[2].asFloat.dp
                )
                else -> ButtonDefaults.ContentPadding
            }
        }

        // ── Elevation ──

        @Composable
        private fun resolveElevation(json: JsonObject): androidx.compose.material3.ButtonElevation? {
            val shadow = json.get("shadow") ?: return null
            return when {
                shadow.isJsonPrimitive && shadow.asJsonPrimitive.isBoolean && shadow.asBoolean ->
                    ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                shadow.isJsonPrimitive && shadow.asJsonPrimitive.isNumber ->
                    ButtonDefaults.buttonElevation(defaultElevation = shadow.asFloat.dp)
                else -> null
            }
        }
    }
}
