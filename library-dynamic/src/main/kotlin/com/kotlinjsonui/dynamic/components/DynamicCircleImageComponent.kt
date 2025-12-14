package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
 * Dynamic CircleImage Component Converter
 * Converts JSON to circular Image/AsyncImage composable at runtime
 * 
 * Supported JSON attributes (matching Ruby implementation):
 * - source/src/url: String image source (local resource or URL) or @{variable}
 * - size: Number size for both width and height (default 48)
 * - borderWidth: Float border width
 * - borderColor: String hex color for border
 * - background: String hex color for background (when image doesn't load)
 * - errorImage: String resource name for error image (network images only)
 * - contentDescription: String description for accessibility
 * - padding/paddings: Number or Array for padding
 * - margins: Array or individual margin properties
 * 
 * Note: Automatically determines if it's a network or local image
 */
class DynamicCircleImageComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            val context = LocalContext.current
            
            // Parse image source with data binding support
            val sourceElement = json.get("source") ?: json.get("src") ?: json.get("url")
            val imageSource = when {
                sourceElement?.isJsonPrimitive == true -> {
                    val sourceString = sourceElement.asString
                    if (sourceString.contains("@{")) {
                        // Extract variable name from @{variable}
                        val pattern = "@\\{([^}]+)\\}".toRegex()
                        val variable = pattern.find(sourceString)?.groupValues?.get(1)
                        data[variable]?.toString() ?: ""
                    } else {
                        sourceString
                    }
                }
                else -> ""
            }
            
            // Determine if it's a network image
            val isNetworkImage = imageSource.startsWith("http://") || 
                                imageSource.startsWith("https://") ||
                                (json.has("url") && !json.has("source") && !json.has("src"))
            
            // Parse content description
            val contentDescription = json.get("contentDescription")?.asString ?: "Profile Image"
            
            // Parse size (default to 48dp for circular images)
            val size = json.get("size")?.asFloat ?: 48f
            
            // Build base modifier
            var modifier: Modifier = Modifier
                .size(size.dp)
                .clip(CircleShape)
            
            // Apply margins and padding
            modifier = ModifierBuilder.applyMargins(modifier, json)
            modifier = ModifierBuilder.applyPadding(modifier, json)
            
            // Background color (visible if image doesn't load)
            val backgroundColor = ColorParser.parseColor(json, "background")
            backgroundColor?.let {
                modifier = modifier.background(it, CircleShape)
            }
            
            // Border (supports solid/dashed/dotted)
            val borderWidth = json.get("borderWidth")?.asFloat
            val borderColor = ColorParser.parseColor(json, "borderColor")
            if (borderWidth != null && borderWidth > 0 && borderColor != null) {
                val borderStyle = json.get("borderStyle")?.asString ?: "solid"
                modifier = when (borderStyle) {
                    "dashed" -> modifier.dashedBorder(borderWidth.dp, borderColor, CircleShape)
                    "dotted" -> modifier.dottedBorder(borderWidth.dp, borderColor, CircleShape)
                    else -> modifier.border(borderWidth.dp, borderColor, CircleShape)
                }
            }
            
            // Create the appropriate image component
            if (isNetworkImage) {
                // Network image using Coil
                val errorImageName = json.get("errorImage")?.asString
                val errorResId = errorImageName?.let { name ->
                    val cleanName = name.replace(".png", "").replace(".jpg", "")
                    context.resources.getIdentifier(cleanName, "drawable", context.packageName)
                }
                
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imageSource)
                        .crossfade(true)
                        .build(),
                    contentDescription = contentDescription,
                    contentScale = ContentScale.Crop,
                    error = errorResId?.let { painterResource(it) },
                    modifier = modifier
                )
            } else {
                // Local image resource
                val resourceName = imageSource
                    .replace(".png", "")
                    .replace(".jpg", "")
                    .replace("-", "_")
                    .lowercase()
                
                val resourceId = context.resources.getIdentifier(
                    resourceName, 
                    "drawable", 
                    context.packageName
                )
                
                if (resourceId != 0) {
                    Image(
                        painter = painterResource(id = resourceId),
                        contentDescription = contentDescription,
                        contentScale = ContentScale.Crop,
                        modifier = modifier
                    )
                } else {
                    // Fallback: show background color only if resource not found
                    Box(modifier = modifier)
                }
            }
        }
    }
}