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
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.UnappliedAttributes
import com.kotlinjsonui.dynamic.generated.ButtonAttributes
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.rememberTypedAttrs
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
            val a = rememberTypedAttrs(json) { m, canonicalOnly ->
                ButtonAttributes.parse(m, canonicalOnly)
            }
            UnappliedAttributes.check(
                "Button", json,
                declared = ButtonAttributes.declaredAttributes,
                applied = UnappliedAttributes.COMMON_APPLIED + APPLIED,
                context = context
            )

            // Resolve button text with binding + resource support
            val text = (TypedAttrs.rawString(a.text)
                ?.let { ResourceResolver.resolveTextValue(it, data, context) } ?: "")
                .ifEmpty { "Button" }

            // Loading state (undeclared legacy runtime extra)
            var isLoading by remember {
                mutableStateOf(TypedAttrs.undeclared(json, "isLoading")?.asBoolean ?: false)
            }

            // Enabled state (supports @{binding}; 'disabled' is undeclared legacy)
            val isEnabled = when {
                isLoading -> false
                else -> (TypedAttrs.boolean(a.common.enabled, data) ?: true) &&
                        !ResourceResolver.resolveBoolean(json, "disabled", data, default = false)
            }

            // Click handler
            val viewId = a.common.id ?: "button"
            val onClick: () -> Unit = buildClickHandler(json, a, data, viewId, isLoading) { loading ->
                isLoading = loading
            }

            // Text style
            val fontSize = a.fontSize?.toFloat()
                ?: Configuration.Button.defaultFontSize.toFloat()
            val fontWeight = when ((TypedAttrs.static(a.fontWeight) as? String)?.lowercase()) {
                "bold" -> FontWeight.Bold
                "semibold" -> FontWeight.SemiBold
                "medium" -> FontWeight.Medium
                "light" -> FontWeight.Light
                "thin" -> FontWeight.Thin
                else -> FontWeight.Normal
            }

            // Colors (supports @{binding})
            val textColor = ColorParser.parseColorStringWithBinding(
                TypedAttrs.rawString(a.fontColor), data, context
            ) ?: Configuration.Button.defaultTextColor
            val backgroundColor = ColorParser.parseColorStringWithBinding(
                TypedAttrs.rawString(a.common.background), data, context
            ) ?: Configuration.Button.defaultBackgroundColor
            val disabledBgColor = ColorParser.parseColorStringWithBinding(
                TypedAttrs.rawString(a.common.disabledBackground), data, context
            ) ?: backgroundColor.copy(alpha = 0.5f)
            val disabledTextColor = ColorParser.parseColorStringWithBinding(
                TypedAttrs.rawString(a.disabledFontColor), data, context
            ) ?: textColor.copy(alpha = 0.5f)
            // Pressed-state container color: 'tapBackground' (tap state)
            // and 'highlightBackground' (highlighted state) are two
            // DISTINCT declared attributes; Compose has no separate
            // highlighted state, so both map to the pressed container
            // here, tapBackground winning when both are set.
            // 'hilightBackground' is an undeclared legacy typo spelling,
            // always honored last.
            val pressedBgColor = ColorParser.parseColorStringWithBinding(
                a.tapBackground ?: a.highlightBackground, data, context
            ) ?: ColorParser.parseColorStringWithBinding(
                TypedAttrs.undeclared(json, "hilightBackground")?.asString, data, context
            )

            // Shape
            val cornerRadius = TypedAttrs.float(a.common.cornerRadius, data)
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
            val borderColor = ColorParser.parseColorStringWithBinding(
                TypedAttrs.rawString(a.common.borderColor), data, context
            )
            val borderWidth = TypedAttrs.float(a.common.borderWidth, data) ?: 1f
            val border = borderColor?.let { BorderStroke(borderWidth.dp, it) }

            // Content padding (padding/paddings accept edge-inset arrays —
            // wider than the declared type, so read raw; see TypedAttrs.rawKey)
            val contentPadding = buildContentPadding(json)

            // Elevation
            val elevation = resolveElevation(a.common.shadow)

            // Optional leading/trailing icon ('imagePosition'/'iconSize' are
            // undeclared legacy runtime extras)
            val imageResId = a.image?.let {
                ResourceResolver.resolveDrawable(it, data, context).takeIf { id -> id != 0 }
            }
            val imagePosition = TypedAttrs.undeclared(json, "imagePosition")?.asString?.lowercase() ?: "leading"
            val iconSize = TypedAttrs.undeclared(json, "iconSize")?.asFloat ?: 18f

            // Text alignment (Button-level). Compose default is center; tool allows override.
            // The legacy reader also honored the undeclared "start"/"end"
            // spellings — preserved through the enum unknown-passthrough.
            val textAlign = TypedAttrs.enumString(a.textAlign) { it.json }?.let { align ->
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
                            text = TypedAttrs.undeclared(json, "loadingText")?.asString ?: text,
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
            a: ButtonAttributes,
            data: Map<String, Any>,
            viewId: String,
            isLoading: Boolean,
            setLoading: (Boolean) -> Unit
        ): () -> Unit = {
            if (!isLoading) {
                val methodName = resolveClickMethodName(a)
                methodName?.let { name ->
                    val handler = data[name]
                    if (handler is Function<*>) {
                        val isAsync = TypedAttrs.undeclared(json, "async")?.asBoolean ?: false
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
                                a.common.onclick as? String
                                    ?: TypedAttrs.raw(a.common.onClick) as? String,
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
        private fun resolveClickMethodName(a: ButtonAttributes): String? {
            (a.common.onclick as? String)?.let { value ->
                if (!value.contains("@{")) return value
            }
            (TypedAttrs.raw(a.common.onClick) as? String)?.let { value ->
                return ModifierBuilder.extractBindingProperty(value)
            }
            return null
        }

        // ── Content Padding ──

        private fun buildContentPadding(json: JsonObject): PaddingValues {
            // paddings (array or single number)
            TypedAttrs.rawKey(json, "paddings")?.let { element ->
                if (element.isJsonPrimitive && element.asJsonPrimitive.isNumber) {
                    return PaddingValues(element.asFloat.dp)
                }
                if (element.isJsonArray) {
                    return parsePaddingArray(element.asJsonArray)
                }
            }

            // padding (single value or array)
            TypedAttrs.rawKey(json, "padding")?.let { element ->
                if (element.isJsonPrimitive && element.asJsonPrimitive.isNumber) {
                    return PaddingValues(element.asFloat.dp)
                }
                if (element.isJsonArray) {
                    return parsePaddingArray(element.asJsonArray)
                }
            }

            // Individual padding values ('paddingVertical'/'paddingHorizontal'
            // are undeclared legacy spellings)
            val paddingTop = TypedAttrs.rawKey(json, "paddingTop")?.asFloat
                ?: TypedAttrs.undeclared(json, "paddingVertical")?.asFloat ?: 0f
            val paddingBottom = TypedAttrs.rawKey(json, "paddingBottom")?.asFloat
                ?: TypedAttrs.undeclared(json, "paddingVertical")?.asFloat ?: 0f
            val paddingStart = TypedAttrs.rawKey(json, "paddingStart")?.asFloat
                ?: TypedAttrs.rawKey(json, "paddingLeft")?.asFloat
                ?: TypedAttrs.undeclared(json, "paddingHorizontal")?.asFloat ?: 0f
            val paddingEnd = TypedAttrs.rawKey(json, "paddingEnd")?.asFloat
                ?: TypedAttrs.rawKey(json, "paddingRight")?.asFloat
                ?: TypedAttrs.undeclared(json, "paddingHorizontal")?.asFloat ?: 0f

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
        private fun resolveElevation(shadow: Any?): androidx.compose.material3.ButtonElevation? {
            return when (shadow) {
                true -> ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                is Number -> ButtonDefaults.buttonElevation(defaultElevation = shadow.toFloat().dp)
                else -> null
            }
        }

        /** Button-specific attributes this component applies (see UnappliedAttributes). */
        private val APPLIED: Set<String> = setOf(
            "text", "enabled", "fontSize", "fontWeight", "fontColor",
            "disabledFontColor", "tapBackground", "highlightBackground",
            "image", "textAlign", "disabledBackground"
        )
    }
}
