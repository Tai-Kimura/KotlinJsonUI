package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.dashedBorder
import com.kotlinjsonui.dynamic.helpers.dottedBorder

/**
 * Dynamic NetworkImage Component Converter
 * Converts JSON to NetworkImage composable at runtime using Coil
 * 
 * Supported JSON attributes (matching Ruby implementation):
 * - source/url/src: String URL or @{variable} for image URL
 * - placeholder: String resource name for placeholder image
 * - errorImage: String resource name for error image
 * - contentDescription: String description for accessibility
 * - contentMode: "aspectFit" | "aspectFill" | "fill" | "scaleToFill" | "center"
 * - size: Number for both width and height
 * - width/height: Number dimensions
 * - cornerRadius: Float corner radius
 * - borderWidth: Float border width
 * - borderColor: String hex color for border
 * - alpha: Float opacity (0.0 to 1.0)
 * - padding/paddings: Number or Array for padding
 * - margins: Array or individual margin properties
 */
class DynamicNetworkImageComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            val context = LocalContext.current
            
            // Parse image URL with data binding support
            val urlElement = json.get("source") ?: json.get("url") ?: json.get("src")
            val imageUrl = when {
                urlElement?.isJsonPrimitive == true -> {
                    val urlString = urlElement.asString
                    if (urlString.contains("@{")) {
                        // Extract variable name from @{variable}
                        val pattern = "@\\{([^}]+)\\}".toRegex()
                        val variable = pattern.find(urlString)?.groupValues?.get(1)
                        data[variable]?.toString() ?: ""
                    } else {
                        urlString
                    }
                }
                else -> ""
            }
            
            // Parse content description
            val contentDescription = json.get("contentDescription")?.asString ?: "Image"
            
            // Parse content scale (case-insensitive)
            val contentScale = when (json.get("contentMode")?.asString?.lowercase()) {
                "aspectfit" -> ContentScale.Fit
                "aspectfill" -> ContentScale.Crop
                "fill", "scaletofill" -> ContentScale.FillBounds
                "center" -> ContentScale.None
                else -> ContentScale.Fit
            }
            
            // Parse placeholder and error images
            val placeholderName = json.get("placeholder")?.asString
            val errorImageName = json.get("errorImage")?.asString
            
            // Get resource IDs for placeholder and error images
            val placeholderResId = placeholderName?.let { name ->
                val cleanName = name.replace(".png", "").replace(".jpg", "")
                context.resources.getIdentifier(cleanName, "drawable", context.packageName)
            }
            
            val errorResId = errorImageName?.let { name ->
                val cleanName = name.replace(".png", "").replace(".jpg", "")
                context.resources.getIdentifier(cleanName, "drawable", context.packageName)
            }
            
            // Build modifier
            // Compose Modifier order (top to bottom = outer to inner):
            // 1. margins (outer spacing)
            // 2. size
            // 3. clip/border
            // 4. padding (inner spacing)
            var modifier: Modifier = Modifier

            // 1. Margins first (outer spacing, before size)
            modifier = ModifierBuilder.applyMargins(modifier, json)

            // 2. Size
            json.get("size")?.asFloat?.let { size ->
                modifier = modifier.size(size.dp)
            } ?: run {
                modifier = ModifierBuilder.applySize(modifier, json)
            }

            // 3. Corner radius and shape (after size)
            val cornerRadius = json.get("cornerRadius")?.asFloat
            val shape = if (cornerRadius != null && cornerRadius > 0) {
                if (cornerRadius >= 500) CircleShape else RoundedCornerShape(cornerRadius.dp)
            } else null

            if (shape != null) {
                modifier = modifier.clip(shape)
            }

            // Apply border (supports solid/dashed/dotted)
            val borderWidth = json.get("borderWidth")?.asFloat
            val borderColor = ColorParser.parseColor(json, "borderColor")
            if (borderWidth != null && borderWidth > 0 && borderColor != null) {
                val borderStyle = json.get("borderStyle")?.asString ?: "solid"
                modifier = when (borderStyle) {
                    "dashed" -> modifier.dashedBorder(borderWidth.dp, borderColor, shape)
                    "dotted" -> modifier.dottedBorder(borderWidth.dp, borderColor, shape)
                    else -> modifier.border(borderWidth.dp, borderColor, shape ?: RectangleShape)
                }
            }

            // 4. Padding (inner spacing, after clip)
            modifier = ModifierBuilder.applyPadding(modifier, json)

            // Alpha/opacity
            json.get("alpha")?.asFloat?.let { alpha ->
                modifier = modifier.alpha(alpha.coerceIn(0f, 1f))
            }
            
            // Create the AsyncImage
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = contentDescription,
                contentScale = contentScale,
                placeholder = placeholderResId?.let { painterResource(it) },
                error = errorResId?.let { painterResource(it) },
                modifier = modifier
            )
        }
    }
}