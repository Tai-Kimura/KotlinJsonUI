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
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.UnappliedAttributes
import com.kotlinjsonui.dynamic.generated.NetworkImageAttributes
import com.kotlinjsonui.dynamic.processDataBinding
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ResourceResolver
import com.kotlinjsonui.dynamic.rememberTypedAttrs

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
 *
 * Attribute access goes through the generated [NetworkImageAttributes]
 * extraction via the [TypedAttrs] bridge; the node itself is only passed
 * wholesale to the shared ModifierBuilder helpers.
 */
class DynamicNetworkImageComponent {
    companion object {
        /** NetworkImage-specific attributes this component applies (see UnappliedAttributes). */
        private val APPLIED: Set<String> = setOf(
            "url", "src", "contentMode", "hint", "placeholder", "errorImage",
            "loadingImage", "defaultImage"
        )

        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            val context = LocalContext.current
            val a = rememberTypedAttrs(json) { m, canonicalOnly ->
                NetworkImageAttributes.parse(m, canonicalOnly)
            }
            UnappliedAttributes.check(
                "NetworkImage", json,
                declared = NetworkImageAttributes.declaredAttributes,
                applied = UnappliedAttributes.COMMON_APPLIED + APPLIED,
                context = context
            )

            // ── URL resolution: source > url > src, with @{binding} support ──
            // ('source' is an undeclared legacy runtime extra on NetworkImage)
            val rawUrl = TypedAttrs.undeclared(json, "source")?.asString
                ?: TypedAttrs.rawString(a.url)
                ?: TypedAttrs.rawString(a.src)
                ?: ""
            val imageUrl = processDataBinding(rawUrl, data)

            // ── Content description ──
            // ('contentDescription' is an undeclared legacy runtime extra)
            val contentDescription = (
                TypedAttrs.undeclared(json, "contentDescription")?.asString
                    ?.let { ResourceResolver.resolveTextValue(it, data, context) }
                    ?: ""
                ).ifEmpty { "Image" }

            // ── Content scale (case-insensitive; static-only legacy read) ──
            val modeLower =
                TypedAttrs.staticEnumString(a.contentMode) { it.json }?.lowercase()
            val contentScale = when (modeLower) {
                "aspectfit" -> ContentScale.Fit
                "aspectfill" -> ContentScale.Crop
                "fill", "scaletofill" -> ContentScale.FillBounds
                // Positional modes draw unscaled (UIKit contentMode
                // positions).
                "center", "top", "bottom", "left", "right" -> ContentScale.None
                else -> ContentScale.Fit
            }
            val contentAlignment = when (modeLower) {
                "top" -> androidx.compose.ui.Alignment.TopCenter
                "bottom" -> androidx.compose.ui.Alignment.BottomCenter
                "left" -> androidx.compose.ui.Alignment.CenterStart
                "right" -> androidx.compose.ui.Alignment.CenterEnd
                else -> androidx.compose.ui.Alignment.Center
            }

            // ── Placeholder: hint > placeholder > loadingImage (the same
            // in-flight-image chain as the static converter,
            // networkimage_component.rb — the declared `loadingImage` was
            // previously never read here: parity family
            // kjui-dynamic-loadingimage), strip .png/.jpg extension ──
            val placeholderName = a.hint ?: a.placeholder ?: a.loadingImage
            val placeholderResId = placeholderName?.let { name ->
                val cleanName = name
                    .removeSuffix(".png")
                    .removeSuffix(".jpg")
                    .removeSuffix(".jpeg")
                    .removeSuffix(".webp")
                context.resources.getIdentifier(cleanName, "drawable", context.packageName)
            }?.takeIf { it != 0 }

            // ── Error image: errorImage, strip extension ──
            val errorImageName = a.errorImage
            val errorResId = errorImageName?.let { name ->
                val cleanName = name
                    .removeSuffix(".png")
                    .removeSuffix(".jpg")
                    .removeSuffix(".jpeg")
                    .removeSuffix(".webp")
                context.resources.getIdentifier(cleanName, "drawable", context.packageName)
            }?.takeIf { it != 0 }

            // ── Default image: what the view shows when there is no src at
            // all (canonical networkImage.noSrc = defaultImage, shared/core/
            // attribute_semantics.json). Previously parsed but never read —
            // a control declaring only defaultImage rendered blank. ──
            val defaultResId = a.defaultImage?.let { name ->
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
            // ('size' is an undeclared legacy runtime extra)
            val sizeValue = TypedAttrs.undeclared(json, "size")?.asFloat
            val modifier = if (sizeValue != null) {
                // Build modifier but skip applySize — we apply square size manually
                var m: Modifier = Modifier
                m = ModifierBuilder.applyTestTag(m, json)
                m = ModifierBuilder.applyMargins(m, json, data)
                // Square size instead of applySize
                m = m.size(sizeValue.dp)
                m = ModifierBuilder.applyAlpha(m, json, data)
                m = ModifierBuilder.applyShadow(m, json, data)
                m = ModifierBuilder.applyBackground(m, json, data, context)
                m = ModifierBuilder.applyClickable(m, json, data)
                m = ModifierBuilder.applyPadding(m, json, data)
                m
            } else {
                ModifierBuilder.buildModifier(json, data, context = context)
            }

            // ── Lifecycle effects ──
            if (ModifierBuilder.hasLifecycleEvents(json)) {
                ModifierBuilder.ApplyLifecycleEffects(json, data)
            }

            // ── AsyncImage ──
            // Canonical chains (networkImage.noSrc = defaultImage): no src →
            // defaultImage; error → errorImage, falling back to defaultImage.
            // Empty url becomes null so Coil takes the fallback path instead
            // of treating "" as a failing request.
            val effectiveErrorResId = errorResId ?: defaultResId ?: placeholderResId
            val effectiveFallbackResId = defaultResId ?: errorResId ?: placeholderResId
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUrl.takeIf { it.isNotEmpty() })
                    .crossfade(true)
                    .build(),
                contentDescription = contentDescription,
                contentScale = contentScale,
                alignment = contentAlignment,
                placeholder = placeholderResId?.let { painterResource(it) },
                error = effectiveErrorResId?.let { painterResource(it) },
                fallback = effectiveFallbackResId?.let { painterResource(it) },
                modifier = modifier
            )
        }
    }
}
