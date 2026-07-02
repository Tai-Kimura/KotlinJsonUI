package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.UnappliedAttributes
import com.kotlinjsonui.dynamic.generated.ImageAttributes
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
 * ContentScale: aspectfill->Crop, aspectfit->Fit, fill/scaletofill->FillBounds, center->None
 * Modifier order: testTag -> margins -> size -> alpha -> shadow -> background -> clickable -> padding
 */
class DynamicImageComponent {
    companion object {
        /** Image-specific attributes this component applies (see UnappliedAttributes). */
        private val APPLIED: Set<String> = setOf(
            "srcName", "src", "contentMode"
        )

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

            // Source priority: srcName > src > defaultImage > text > "placeholder"
            // ('defaultImage'/'text' are undeclared legacy extras on Image)
            val defaultImage = TypedAttrs.undeclared(json, "defaultImage")?.asString
            val rawSrc = TypedAttrs.rawString(a.srcName)
                ?: TypedAttrs.rawString(a.src)
                ?: defaultImage
                ?: TypedAttrs.undeclared(json, "text")?.asString
                ?: "placeholder"

            // Resolve drawable resource ID (handles @{binding} internally)
            var resourceId = ResourceResolver.resolveDrawable(rawSrc, data, context)

            // Fallback to defaultImage when binding src resolves to 0
            if (resourceId == 0 && ModifierBuilder.isBinding(rawSrc)) {
                if (defaultImage != null && defaultImage != rawSrc) {
                    resourceId = ResourceResolver.resolveDrawable(defaultImage, data, context)
                }
            }

            // Nothing to render if resource not found
            if (resourceId == 0) return

            // Content description ('contentDescription' is an undeclared
            // legacy runtime extra)
            val contentDescription = TypedAttrs.undeclared(json, "contentDescription")?.asString
                ?: a.common.id
                ?: ""

            // ContentScale mapping (case-insensitive)
            val contentScale = when (
                TypedAttrs.enumStringResolved(a.contentMode, data) { it.json }?.lowercase()
            ) {
                "aspectfill" -> ContentScale.Crop
                "aspectfit" -> ContentScale.Fit
                "fill", "scaletofill" -> ContentScale.FillBounds
                "center" -> ContentScale.None
                else -> ContentScale.Fit
            }

            // Alpha with binding support (alpha and opacity are both
            // declared rows with identical semantics; legacy read order kept)
            val alpha = TypedAttrs.float(a.common.alpha, data)
                ?: TypedAttrs.float(a.common.opacity, data)
                ?: 1f

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

            // Lifecycle effects
            ModifierBuilder.ApplyLifecycleEffects(json, data)

            Image(
                painter = painterResource(id = resourceId),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale,
                alpha = alpha.coerceIn(0f, 1f)
            )
        }
    }
}
