package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.UnappliedAttributes
import com.kotlinjsonui.dynamic.generated.IconLabelAttributes
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ResourceResolver
import com.kotlinjsonui.dynamic.rememberTypedAttrs

/**
 * Dynamic IconLabel Component Converter
 * Converts JSON to IconLabel (icon with text) composable at runtime.
 *
 * Supported JSON attributes:
 * - icon/src: String drawable resource name, supports @{binding}
 * - text: String text content, supports @{binding} and string resources
 * - iconPosition: "left" | "right" | "top" | "bottom" (default "left")
 *   - left/right → Row layout
 *   - top/bottom → Column layout
 * - iconSize: Number icon size in dp (default 24)
 * - iconColor/tintColor: String hex color for icon tint, supports @{binding}
 * - fontSize: Float text size in sp (default 14)
 * - fontColor: String hex color for text, supports @{binding}
 * - fontWeight: String font weight name (e.g., "bold", "medium")
 * - spacing: Number spacing between icon and text in dp (default 8)
 * - padding/paddings: Number or Array for padding
 * - margins: Array or individual margin properties
 * - alpha/opacity: Float opacity value (0-1), supports @{binding}
 * - onClick/onclick: String event handler name
 */
class DynamicIconLabelComponent {
    companion object {

        private val WEIGHT_NAMES = mapOf(
            "thin" to FontWeight.Thin,
            "extralight" to FontWeight.ExtraLight,
            "light" to FontWeight.Light,
            "normal" to FontWeight.Normal,
            "medium" to FontWeight.Medium,
            "semibold" to FontWeight.SemiBold,
            "bold" to FontWeight.Bold,
            "extrabold" to FontWeight.ExtraBold,
            "heavy" to FontWeight.ExtraBold,
            "black" to FontWeight.Black
        )

        /** IconLabel-specific attributes this component applies (see UnappliedAttributes). */
        private val APPLIED: Set<String> = setOf(
            "text", "fontSize", "fontColor", "iconPosition", "tintColor"
        )

        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            val context = LocalContext.current
            val a = rememberTypedAttrs(json) { m, canonicalOnly ->
                IconLabelAttributes.parse(m, canonicalOnly)
            }
            UnappliedAttributes.check(
                "IconLabel", json,
                declared = IconLabelAttributes.declaredAttributes,
                applied = UnappliedAttributes.COMMON_APPLIED + APPLIED,
                context = context
            )

            // Build container modifier
            val modifier = ModifierBuilder.buildModifier(json, data, context = context)

            // Lifecycle effects
            if (ModifierBuilder.hasLifecycleEvents(json)) {
                ModifierBuilder.ApplyLifecycleEffects(json, data)
            }

            // Parse icon resource ('icon'/'src' are undeclared legacy runtime
            // extras — the declared rows are icon_on/icon_off, which this
            // component does not consume)
            val rawIcon = TypedAttrs.undeclared(json, "icon")?.asString
                ?: TypedAttrs.undeclared(json, "src")?.asString
                ?: ""
            val iconResId = ResourceResolver.resolveDrawable(rawIcon, data, context)

            // Parse text with binding + string resource support
            val text = TypedAttrs.rawString(a.text)
                ?.let { ResourceResolver.resolveTextValue(it, data, context) }
                ?: ""

            // Parse icon attributes ('iconSize'/'iconColor' are undeclared
            // legacy runtime extras; 'tintColor' is the declared common row)
            val iconSize = TypedAttrs.undeclared(json, "iconSize")?.asFloat ?: 24f
            val iconTintColor = ColorParser.parseColorWithBinding(json, "iconColor", data, context)
                ?: ColorParser.parseColorStringWithBinding(
                    TypedAttrs.rawString(a.common.tintColor), data, context
                )

            // Parse text attributes ('fontWeight' is an undeclared legacy
            // runtime extra — 'font' is the declared row, not consumed here)
            val fontSize = a.fontSize?.toFloat() ?: 14f
            val fontColor = ColorParser.parseColorStringWithBinding(a.fontColor, data, context)
                ?: Color.Unspecified
            val fontWeight = TypedAttrs.undeclared(json, "fontWeight")?.asString?.let { fw ->
                WEIGHT_NAMES[fw.lowercase()]
            }

            // Parse layout attributes (iconPosition feeds the existing
            // lowercase switch; 'spacing' is an undeclared legacy runtime
            // extra — 'iconMargin' is the declared row, not consumed here)
            val iconPosition = TypedAttrs.enumString(a.iconPosition) { it.json }
                ?.lowercase() ?: "left"
            val spacing = TypedAttrs.undeclared(json, "spacing")?.asFloat ?: 8f

            // Build icon composable content ('contentDescription' is an
            // undeclared legacy runtime extra)
            val iconContent: @Composable () -> Unit = {
                if (iconResId != 0) {
                    Image(
                        painter = painterResource(id = iconResId),
                        contentDescription = TypedAttrs.undeclared(json, "contentDescription")?.asString ?: "",
                        modifier = Modifier.size(iconSize.dp),
                        colorFilter = iconTintColor?.let { ColorFilter.tint(it) }
                    )
                }
            }

            // Build text composable content
            val textContent: @Composable () -> Unit = {
                if (text.isNotEmpty()) {
                    Text(
                        text = text,
                        fontSize = fontSize.sp,
                        color = fontColor,
                        fontWeight = fontWeight
                    )
                }
            }

            // Render based on icon position
            when (iconPosition) {
                "left" -> {
                    Row(
                        modifier = modifier,
                        horizontalArrangement = Arrangement.spacedBy(spacing.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        iconContent()
                        textContent()
                    }
                }
                "right" -> {
                    Row(
                        modifier = modifier,
                        horizontalArrangement = Arrangement.spacedBy(spacing.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        textContent()
                        iconContent()
                    }
                }
                "top" -> {
                    Column(
                        modifier = modifier,
                        verticalArrangement = Arrangement.spacedBy(spacing.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        iconContent()
                        textContent()
                    }
                }
                "bottom" -> {
                    Column(
                        modifier = modifier,
                        verticalArrangement = Arrangement.spacedBy(spacing.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        textContent()
                        iconContent()
                    }
                }
                else -> {
                    // Default to "left"
                    Row(
                        modifier = modifier,
                        horizontalArrangement = Arrangement.spacedBy(spacing.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        iconContent()
                        textContent()
                    }
                }
            }
        }
    }
}
