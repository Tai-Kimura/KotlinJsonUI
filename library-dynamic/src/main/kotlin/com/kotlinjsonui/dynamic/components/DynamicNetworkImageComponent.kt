package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.processDataBinding
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ResourceResolver

/**
 * Dynamic NetworkImage Component Converter
 * Converts JSON to NetworkImage composable at runtime using Coil AsyncImage.
 *
 * Supported JSON attributes (matching Ruby networkimage_component.rb):
 * - source/url/src: String URL or @{variable} for image URL
 * - hint/placeholder: String resource name for placeholder image (.png/.jpg extension stripped)
 * - errorImage: String resource name for error image (.png/.jpg extension stripped)
 * - contentDescription: String description for accessibility (default: "Image")
 * - contentMode: "aspectFit" | "aspectFill" | "fill" | "scaleToFill" | "center" (case-insensitive)
 * - size: Number for square width and height (overrides width/height)
 * - width/height: Number dimensions
 * - cornerRadius: Float corner radius (applied via applyBackground clip)
 * - borderWidth: Float border width
 * - borderColor: String hex color for border
 * - borderStyle: "solid" | "dashed" | "dotted"
 * - alpha/opacity: Float opacity (0.0 to 1.0), supports @{binding}
 * - padding/paddings: Number or Array for padding
 * - margins: Array or individual margin properties
 * - background: String hex color for background
 * - onClick/onclick: String event handler name
 * - id: String for testTag
 * - onAppear/onDisappear: Lifecycle event handlers
 */
class DynamicNetworkImageComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            val context = LocalContext.current

            // ── URL resolution: source > url > src, with @{binding} support ──
            val rawUrl = json.get("source")?.asString
                ?: json.get("url")?.asString
                ?: json.get("src")?.asString
                ?: ""
            val imageUrl = processDataBinding(rawUrl, data)

            // ── Content description ──
            val contentDescription = ResourceResolver.resolveText(json, "contentDescription", data, context)
                .ifEmpty { "Image" }

            // ── Content scale (case-insensitive) ──
            val contentScale = when (json.get("contentMode")?.asString?.lowercase()) {
                "aspectfit" -> ContentScale.Fit
                "aspectfill" -> ContentScale.Crop
                "fill", "scaletofill" -> ContentScale.FillBounds
                "center" -> ContentScale.None
                else -> ContentScale.Fit
            }

            // ── Placeholder: hint > placeholder, strip .png/.jpg extension ──
            val placeholderName = json.get("hint")?.asString
                ?: json.get("placeholder")?.asString
            val placeholderResId = placeholderName?.let { name ->
                val cleanName = name
                    .removeSuffix(".png")
                    .removeSuffix(".jpg")
                    .removeSuffix(".jpeg")
                    .removeSuffix(".webp")
                context.resources.getIdentifier(cleanName, "drawable", context.packageName)
            }?.takeIf { it != 0 }

            // ── Error image: errorImage, strip extension ──
            val errorImageName = json.get("errorImage")?.asString
            val errorResId = errorImageName?.let { name ->
                val cleanName = name
                    .removeSuffix(".png")
                    .removeSuffix(".jpg")
                    .removeSuffix(".jpeg")
                    .removeSuffix(".webp")
                context.resources.getIdentifier(cleanName, "drawable", context.packageName)
            }?.takeIf { it != 0 }

            // ── Build modifier ──
            // Standard order: testTag → margins → size → alpha → shadow → background(clip+border+bg) → clickable → padding
            // Special handling: "size" attribute overrides width/height with square .size(N.dp)
            val sizeValue = json.get("size")?.asFloat
            val modifier = if (sizeValue != null) {
                // Build modifier but skip applySize — we apply square size manually
                var m: Modifier = Modifier
                m = ModifierBuilder.applyTestTag(m, json)
                m = ModifierBuilder.applyMargins(m, json, data)
                // Square size instead of applySize
                m = m.size(sizeValue.dp)
                m = ModifierBuilder.applyAlpha(m, json, data)
                m = ModifierBuilder.applyShadow(m, json)
                m = ModifierBuilder.applyBackground(m, json, data, context)
                m = ModifierBuilder.applyClickable(m, json, data)
                m = ModifierBuilder.applyPadding(m, json)
                m
            } else {
                ModifierBuilder.buildModifier(json, data, context = context)
            }

            // ── Lifecycle effects ──
            if (ModifierBuilder.hasLifecycleEvents(json)) {
                ModifierBuilder.ApplyLifecycleEffects(json, data)
            }

            // ── AsyncImage ──
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
