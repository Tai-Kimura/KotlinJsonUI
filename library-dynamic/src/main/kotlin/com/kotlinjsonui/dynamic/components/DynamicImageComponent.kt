package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.UnappliedAttributes
import com.kotlinjsonui.dynamic.generated.ImageAttributes
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.ImageAccessibility
import com.kotlinjsonui.dynamic.helpers.ImageContentScale
import com.kotlinjsonui.dynamic.helpers.LocalImageTappable
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ResourceResolver
import com.kotlinjsonui.dynamic.rememberTypedAttrs

/**
 * Dynamic Image Component Converter
 * Converts JSON to Image composable at runtime.
 * Reference: image_component.rb in kjui_tools.
 *
 * Source priority: srcName > src > defaultImage > text > "placeholder"
 * Binding: @{variable} resolves via data map then getIdentifier for drawable
 * ContentScale: ImageContentScale (aspectfill->Crop, fit/aspectfit->Fit, fill/scaletofill->FillBounds,
 * positional->None; no contentMode -> Fit, the declared default)
 * Modifier order: testTag -> margins -> size -> alpha -> shadow -> background -> clickable -> padding
 */
class DynamicImageComponent {
    companion object {
        /** Image-specific attributes this component applies (see UnappliedAttributes). */
        private val APPLIED: Set<String> = setOf(
            "srcName", "src", "contentMode", "renderingMode", "errorImage", "loadingImage", "alt"
        )

        /**
         * The Image family's source, a local drawable NAME (the SSoT's
         * Image.src: "Image source name"; a URL belongs to NetworkImage):
         * srcName > src > defaultImage > errorImage > loadingImage > text >
         * "placeholder". A CircleImage is an Image spelling (type_synonyms.json
         * `render_as`) and reads it here too.
         *
         * A STATIC Image has no in-flight state, so `loadingImage` can only
         * mean fallback imagery here rather than a spinner — which is the
         * same reason `errorImage` belongs in the chain. Both were declared
         * on Image and read by neither Compose path until C put them in the
         * codegen's chain (image_component.rb:12-20); this path still fell
         * through to the literal `"placeholder"` drawable, which is why
         * `Image_{errorImage,loadingImage}__static` sat at parity distance
         * 10 across runs 3 and 4.
         * ('defaultImage'/'text' are undeclared legacy extras on Image)
         */
        internal fun rawSource(json: JsonObject, a: ImageAttributes): String =
            TypedAttrs.rawString(a.srcName)
                ?: TypedAttrs.rawString(a.src)
                ?: TypedAttrs.undeclared(json, "defaultImage")?.asString
                ?: a.errorImage
                ?: a.loadingImage
                ?: TypedAttrs.undeclared(json, "text")?.asString
                ?: "placeholder"

        /**
         * The drawable [rawSource] names, looked up by [resolve]
         * (ResourceResolver.resolveDrawable: a binding resolves first); a
         * bound source that resolves to nothing falls back to defaultImage.
         * 0 is none.
         */
        internal fun resourceId(json: JsonObject, a: ImageAttributes, resolve: (String) -> Int): Int {
            val rawSrc = rawSource(json, a)
            val resourceId = resolve(rawSrc)
            if (resourceId != 0 || !ModifierBuilder.isBinding(rawSrc)) return resourceId
            val defaultImage = TypedAttrs.undeclared(json, "defaultImage")?.asString
            return if (defaultImage != null && defaultImage != rawSrc) resolve(defaultImage) else 0
        }

        @Composable
        fun create(json: JsonObject, data: Map<String, Any> = emptyMap()) {
            val context = LocalContext.current
            val a = rememberTypedAttrs(json) { m, canonicalOnly ->
                ImageAttributes.parse(m, canonicalOnly)
            }
            UnappliedAttributes.check(
                "Image", json,
                declared = ImageAttributes.declaredAttributes,
                applied = UnappliedAttributes.COMMON_APPLIED + APPLIED,
                context = context
            )

            // The drawable (rawSource / resourceId below; CircleImage reads it
            // the same way).
            val resourceId = resourceId(json, a) { ResourceResolver.resolveDrawable(it, data, context) }

            // Build modifier using composite builder
            // Order: testTag -> margins -> size -> alpha -> shadow -> background -> clickable -> padding
            var modifier = ModifierBuilder.buildModifier(json, data, context = context)

            // Handle "size" attribute for square dimensions (not covered by
            // buildModifier; undeclared legacy runtime extra)
            TypedAttrs.undeclared(json, "size")?.let { sizeElement ->
                if (sizeElement.isJsonPrimitive && sizeElement.asJsonPrimitive.isNumber) {
                    val s = sizeElement.asFloat
                    modifier = modifier.size(s.dp)
                }
            }

            // A resource that is not found draws no image, and the node still
            // takes its place and its stages — its size, background, border,
            // id and tap. It returned here, before any of them, so the whole
            // node vanished (measured: none of the 13 stages applied, testTag
            // and clickable included).
            if (resourceId == 0) {
                Box(modifier = modifier)
                return
            }

            // What TalkBack reads (ImageAccessibility, the codegen's rule):
            // the alt, nothing for a decorative image, and its id for an
            // image that operates a control and has no alt.
            val contentDescription = ImageAccessibility.contentDescription(
                ImageAccessibility.role(json, LocalImageTappable.current),
                { ResourceResolver.resolveTextValue(ImageAccessibility.alt(json).orEmpty(), data, context) },
                legacy = a.common.id ?: ""
            )

            // ContentScale and alignment (ImageContentScale: the one table
            // Image, NetworkImage and CircleImage draw with; no contentMode
            // draws the declared default, fit).
            val mode = TypedAttrs.enumStringResolved(a.contentMode, data) { it.json }
            val contentScale = ImageContentScale.scale(mode)
            val contentAlignment = ImageContentScale.alignment(mode)

            // Alpha with binding support. `alpha` is an alias spelling of
            // `opacity` (49-E), folded onto the canonical row by the generated
            // parser — one read.
            val alpha = TypedAttrs.float(a.common.opacity, data) ?: 1f

            // renderingMode — `template` means "take the tint, ignore the
            // asset's own colours" (ColorFilter here, `.renderingMode(.template)`
            // on iOS); `original` suppresses a tint that would otherwise
            // apply; no mode → a declared tint still applies. Mirrors the
            // static converter's rendering_color_filter (image_component.rb)
            // — this path used to ignore renderingMode/tintColor entirely
            // (parity family kjui-dynamic-renderingmode).
            val renderingMode = TypedAttrs.enumString(a.renderingMode) { it.json }?.lowercase()
            val tint = ColorParser.parseColorStringWithBinding(
                TypedAttrs.rawString(a.common.tintColor), data, context
            )
            val colorFilter = when (renderingMode) {
                "template" -> ColorFilter.tint(tint ?: LocalContentColor.current)
                "original" -> null
                else -> tint?.let { ColorFilter.tint(it) }
            }

            Image(
                painter = painterResource(id = resourceId),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale,
                alignment = contentAlignment,
                alpha = alpha.coerceIn(0f, 1f),
                colorFilter = colorFilter
            )
        }
    }
}
