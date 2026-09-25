package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.UnappliedAttributes
import com.kotlinjsonui.dynamic.generated.ImageAttributes
import com.kotlinjsonui.dynamic.processDataBinding
import com.kotlinjsonui.dynamic.helpers.ImageAccessibility
import com.kotlinjsonui.dynamic.helpers.ResourceResolver
import com.kotlinjsonui.dynamic.helpers.LocalImageTappable
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.dashedBorder
import com.kotlinjsonui.dynamic.helpers.dottedBorder
import com.kotlinjsonui.dynamic.rememberTypedAttrs

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
 *
 * CircleImage parses with the generated [ImageAttributes] extraction via
 * the [TypedAttrs] bridge; the node itself is only passed wholesale to
 * the shared ModifierBuilder helpers.
 */
class DynamicCircleImageComponent {
    companion object {
        /** CircleImage-specific attributes this component applies (see UnappliedAttributes). */
        private val APPLIED: Set<String> = setOf(
            "src", "errorImage", "alt"
        )

        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            val context = LocalContext.current
            val a = rememberTypedAttrs(json) { m, canonicalOnly ->
                ImageAttributes.parse(m, canonicalOnly)
            }
            UnappliedAttributes.check(
                "CircleImage", json,
                declared = ImageAttributes.declaredAttributes,
                applied = UnappliedAttributes.COMMON_APPLIED + APPLIED,
                context = context
            )

            // Determine if network image: has 'url' key OR source starts with "http"
            // ('url' and 'source' are undeclared legacy runtime extras on
            // CircleImage — Image declares only src/srcName)
            val urlElement = TypedAttrs.undeclared(json, "url")
            val hasUrl = urlElement != null
            val rawSource = urlElement?.asString
                ?: TypedAttrs.undeclared(json, "source")?.asString
                ?: TypedAttrs.rawString(a.src)
                ?: ""
            val resolvedSource = processDataBinding(rawSource, data)
            val isNetworkImage = hasUrl || resolvedSource.startsWith("http")

            // What TalkBack reads (ImageAccessibility, the codegen's rule):
            // the alt, nothing for a decorative image, and "Profile Image" for an
            // image that operates a control and has no alt.
            val contentDescription = ImageAccessibility.contentDescription(
                ImageAccessibility.role(json, LocalImageTappable.current),
                { ResourceResolver.resolveTextValue(ImageAccessibility.alt(json).orEmpty(), data, context) },
                legacy = "Profile Image"
            )

            // Parse size (default 48dp for circular images;
            // 'size' is an undeclared legacy runtime extra)
            val size = TypedAttrs.undeclared(json, "size")?.asFloat ?: 48f

            // The standard stages in their standard order, in a circle:
            // testTag → margins → size → offset → alpha → shadow(circle) →
            // clip(circle) → clip(cornerRadius) → border(circle) →
            // background(circle) → clickable → padding.
            // The chain put the margins after the size — inside it, where
            // they padded (measured: the view stayed 48x48) — and the offset
            // after the background, so the circle stayed and only the image
            // moved; it read no declared width / height, and applied no
            // shadow or radius.
            var modifier: Modifier = Modifier
            modifier = ModifierBuilder.applyTestTag(modifier, json)
            modifier = ModifierBuilder.applyMargins(modifier, json, data)
            // The declared width / height, as on every component; without
            // them, the legacy `size`.
            modifier = if (a.common.width != null || a.common.height != null) {
                ModifierBuilder.applySize(modifier, json, data = data)
            } else {
                modifier.size(size.dp)
            }
            modifier = ModifierBuilder.applyOffset(modifier, json, data)
            modifier = ModifierBuilder.applyAlpha(modifier, json, data)
            modifier = ModifierBuilder.applyShadow(modifier, json, data, ownShape = CircleShape)
            modifier = modifier.clip(CircleShape)
            // cornerRadius applies as declared, inside the circle, so the
            // image stays a circle (ruled 2026-09-26: no exception for circles).
            TypedAttrs.float(a.common.cornerRadius, data)?.let {
                modifier = modifier.clip(RoundedCornerShape(it.dp))
            }

            val borderColor = ColorParser.parseColorStringWithBinding(
                TypedAttrs.rawString(a.common.borderColor), data, context
            )
            val borderWidth = TypedAttrs.float(a.common.borderWidth, data)
            if (borderColor != null && borderWidth != null && borderWidth > 0) {
                val borderStyle =
                    TypedAttrs.enumString(a.common.borderStyle) { it.json } ?: "solid"
                modifier = when (borderStyle) {
                    "dashed" -> modifier.dashedBorder(borderWidth.dp, borderColor, CircleShape)
                    "dotted" -> modifier.dottedBorder(borderWidth.dp, borderColor, CircleShape)
                    else -> modifier.border(borderWidth.dp, borderColor, CircleShape)
                }
            }

            ColorParser.parseColorStringWithBinding(
                TypedAttrs.rawString(a.common.background), data, context
            )?.let { bgColor ->
                modifier = modifier.background(bgColor, CircleShape)
            }

            modifier = ModifierBuilder.applyClickable(modifier, json, data)
            modifier = ModifierBuilder.applyPadding(modifier, json, data)

            // Lifecycle effects
            if (ModifierBuilder.hasLifecycleEvents(json)) {
                ModifierBuilder.ApplyLifecycleEffects(json, data)
            }

            // Render the appropriate image component
            if (isNetworkImage) {
                // Error image for network images
                val errorImageName = a.errorImage
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
