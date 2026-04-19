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
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ResourceResolver

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
        @Composable
        fun create(json: JsonObject, data: Map<String, Any> = emptyMap()) {
            val context = LocalContext.current

            // Source priority: srcName > src > defaultImage > text > "placeholder"
            val rawSrc = json.get("srcName")?.asString
                ?: json.get("src")?.asString
                ?: json.get("defaultImage")?.asString
                ?: json.get("text")?.asString
                ?: "placeholder"

            // Resolve drawable resource ID (handles @{binding} internally)
            var resourceId = ResourceResolver.resolveDrawable(rawSrc, data, context)

            // Fallback to defaultImage when binding src resolves to 0
            if (resourceId == 0 && ModifierBuilder.isBinding(rawSrc)) {
                val fallback = json.get("defaultImage")?.asString
                if (fallback != null && fallback != rawSrc) {
                    resourceId = ResourceResolver.resolveDrawable(fallback, data, context)
                }
            }

            // Nothing to render if resource not found
            if (resourceId == 0) return

            // Content description
            val contentDescription = json.get("contentDescription")?.asString
                ?: json.get("id")?.asString
                ?: ""

            // ContentScale mapping (case-insensitive)
            val contentScale = when (
                ResourceResolver.resolveString(json, "contentMode", data)?.lowercase()
            ) {
                "aspectfill" -> ContentScale.Crop
                "aspectfit" -> ContentScale.Fit
                "fill", "scaletofill" -> ContentScale.FillBounds
                "center" -> ContentScale.None
                else -> ContentScale.Fit
            }

            // Alpha with binding support
            val alpha = ResourceResolver.resolveFloat(json, "alpha", data)
                ?: ResourceResolver.resolveFloat(json, "opacity", data)
                ?: 1f

            // Build modifier using composite builder
            // Order: testTag -> margins -> size -> alpha -> shadow -> background -> clickable -> padding
            var modifier = ModifierBuilder.buildModifier(json, data, context = context)

            // Handle "size" attribute for square dimensions (not covered by buildModifier)
            json.get("size")?.let { sizeElement ->
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
