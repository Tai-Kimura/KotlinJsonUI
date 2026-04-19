package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.processDataBinding
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
 * - borderStyle: "solid" | "dashed" | "dotted"
 * - background: String hex color for background (when image doesn't load)
 * - errorImage: String resource name for error image (network images only)
 * - contentDescription: String description for accessibility
 * - padding/paddings: Number or Array for padding
 * - margins: Array or individual margin properties
 * - alpha/opacity: Float opacity value (0-1), supports @{binding}
 * - onClick/onclick: String event handler name
 *
 * Note: Automatically determines if it's a network or local image.
 * Network detection: has 'url' key OR source/src starts with "http".
 * ContentScale is always Crop for circular images.
 */
class DynamicCircleImageComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            val context = LocalContext.current

            // Determine if network image: has 'url' key OR source starts with "http"
            val hasUrl = json.has("url")
            val rawSource = json.get("url")?.asString
                ?: json.get("source")?.asString
                ?: json.get("src")?.asString
                ?: ""
            val resolvedSource = processDataBinding(rawSource, data)
            val isNetworkImage = hasUrl || resolvedSource.startsWith("http")

            // Parse content description
            val contentDescription = json.get("contentDescription")?.asString ?: "Profile Image"

            // Parse size (default 48dp for circular images)
            val size = json.get("size")?.asFloat ?: 48f

            // Build modifier in the order specified by the Ruby reference:
            // testTag -> size -> clip(CircleShape) -> border(CircleShape) -> background(CircleShape) -> margins -> alpha -> clickable -> padding -> weight -> alignment
            var modifier: Modifier = Modifier

            // 1. testTag
            modifier = ModifierBuilder.applyTestTag(modifier, json)

            // 2. size
            modifier = modifier.size(size.dp)

            // 3. clip(CircleShape) - always circular
            modifier = modifier.clip(CircleShape)

            // 4. border(CircleShape)
            val borderColor = ColorParser.parseColorWithBinding(json, "borderColor", data, context)
            val borderWidth = json.get("borderWidth")?.asFloat
            if (borderColor != null && borderWidth != null && borderWidth > 0) {
                val borderStyle = json.get("borderStyle")?.asString ?: "solid"
                modifier = when (borderStyle) {
                    "dashed" -> modifier.dashedBorder(borderWidth.dp, borderColor, CircleShape)
                    "dotted" -> modifier.dottedBorder(borderWidth.dp, borderColor, CircleShape)
                    else -> modifier.border(borderWidth.dp, borderColor, CircleShape)
                }
            }

            // 5. background(CircleShape)
            ColorParser.parseColorWithBinding(json, "background", data, context)?.let { bgColor ->
                modifier = modifier.background(bgColor, CircleShape)
            }

            // 6. margins
            modifier = ModifierBuilder.applyMargins(modifier, json, data)

            // 7. alpha
            modifier = ModifierBuilder.applyAlpha(modifier, json, data)

            // 8. clickable
            modifier = ModifierBuilder.applyClickable(modifier, json, data)

            // 9. padding
            modifier = ModifierBuilder.applyPadding(modifier, json)

            // Lifecycle effects
            if (ModifierBuilder.hasLifecycleEvents(json)) {
                ModifierBuilder.ApplyLifecycleEffects(json, data)
            }

            // Render the appropriate image component
            if (isNetworkImage) {
                // Error image for network images
                val errorImageName = json.get("errorImage")?.asString
                val errorResId = errorImageName?.let { name ->
                    val cleanName = name.replace(".png", "").replace(".jpg", "")
                        .replace("-", "_").lowercase()
                    context.resources.getIdentifier(cleanName, "drawable", context.packageName)
                }?.takeIf { it != 0 }

                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(resolvedSource)
                        .crossfade(true)
                        .build(),
                    contentDescription = contentDescription,
                    contentScale = ContentScale.Crop,
                    error = errorResId?.let { painterResource(it) },
                    modifier = modifier
                )
            } else {
                // Local image: clean name (.png/.jpg removed, - -> _, lowercase)
                val resourceName = resolvedSource
                    .replace(".png", "")
                    .replace(".jpg", "")
                    .replace("-", "_")
                    .lowercase()

                val resourceId = if (resourceName.isNotEmpty()) {
                    context.resources.getIdentifier(
                        resourceName,
                        "drawable",
                        context.packageName
                    )
                } else 0

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
