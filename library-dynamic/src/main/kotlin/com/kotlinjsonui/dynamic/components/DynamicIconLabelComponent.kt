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
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ResourceResolver

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

        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            val context = LocalContext.current

            // Build container modifier
            val modifier = ModifierBuilder.buildModifier(json, data, context = context)

            // Lifecycle effects
            if (ModifierBuilder.hasLifecycleEvents(json)) {
                ModifierBuilder.ApplyLifecycleEffects(json, data)
            }

            // Parse icon resource
            val rawIcon = json.get("icon")?.asString
                ?: json.get("src")?.asString
                ?: ""
            val iconResId = ResourceResolver.resolveDrawable(rawIcon, data, context)

            // Parse text
            val text = ResourceResolver.resolveText(json, "text", data, context)

            // Parse icon attributes
            val iconSize = json.get("iconSize")?.asFloat ?: 24f
            val iconTintColor = ColorParser.parseColorWithBinding(json, "iconColor", data, context)
                ?: ColorParser.parseColorWithBinding(json, "tintColor", data, context)

            // Parse text attributes
            val fontSize = json.get("fontSize")?.asFloat ?: 14f
            val fontColor = ColorParser.parseColorWithBinding(json, "fontColor", data, context)
                ?: Color.Unspecified
            val fontWeight = json.get("fontWeight")?.asString?.let { fw ->
                WEIGHT_NAMES[fw.lowercase()]
            }

            // Parse layout attributes
            val iconPosition = json.get("iconPosition")?.asString ?: "left"
            val spacing = json.get("spacing")?.asFloat ?: 8f

            // Build icon composable content
            val iconContent: @Composable () -> Unit = {
                if (iconResId != 0) {
                    Image(
                        painter = painterResource(id = iconResId),
                        contentDescription = json.get("contentDescription")?.asString ?: "",
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
