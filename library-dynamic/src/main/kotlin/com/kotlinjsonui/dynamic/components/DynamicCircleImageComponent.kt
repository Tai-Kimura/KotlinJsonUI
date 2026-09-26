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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import android.content.Context
import android.util.Log
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.DebugDiagnostics
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.UnappliedAttributes
import com.kotlinjsonui.dynamic.generated.ImageAttributes
import com.kotlinjsonui.dynamic.helpers.ImageAccessibility
import com.kotlinjsonui.dynamic.helpers.ImageContentScale
import com.kotlinjsonui.dynamic.helpers.ResourceResolver
import com.kotlinjsonui.dynamic.helpers.LocalImageTappable
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.dashedBorder
import com.kotlinjsonui.dynamic.helpers.dottedBorder
import com.kotlinjsonui.dynamic.rememberTypedAttrs

/**
 * Dynamic CircleImage Component Converter
 * Converts JSON to a circular Image composable at runtime
 *
 * Supported JSON attributes (matching Ruby implementation):
 * - srcName/src: a local image NAME or @{variable}, read as Image reads it
 *   (DynamicImageComponent.rawSource / resourceId). A URL belongs to
 *   NetworkImage (see [CircleImageSource]).
 * - size: Number size for both width and height (default 48)
 * - borderWidth: Float border width
 * - borderColor: String hex color for border
 * - borderStyle: "solid" | "dashed" | "dotted"
 * - background: String hex color for background (when image doesn't load)
 * - errorImage / loadingImage: fallback image names, as on Image
 * - contentDescription: String description for accessibility
 * - padding/paddings: Number or Array for padding
 * - margins: Array or individual margin properties
 * - alpha/opacity: Float opacity value (0-1), supports @{binding}
 * - onClick/onclick: String event handler name
 *
 * A CircleImage is an Image spelling and its src is a local image name (the
 * SSoT's Image.src; 4f's ruling, jsonui-cli 1.9.0). It loaded a src beginning
 * "http" from the network, and read the undeclared `url` / `source` the same
 * way — leniency outside the declaration that no other path had.
 * contentMode as on Image (ImageContentScale), default fit — a CircleImage is
 * an Image spelling (type_synonyms.json render_as) and draws as iOS and web
 * draw it. It cropped for every mode (4f ruling, 2026-09-26).
 *
 * CircleImage parses with the generated [ImageAttributes] extraction via
 * the [TypedAttrs] bridge; the node itself is only passed wholesale to
 * the shared ModifierBuilder helpers.
 */
class DynamicCircleImageComponent {
    companion object {
        /** CircleImage-specific attributes this component applies (see UnappliedAttributes). */
        private val APPLIED: Set<String> = setOf(
            "srcName", "src", "contentMode", "errorImage", "loadingImage", "alt"
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

            // The drawable, as Image reads it: a local name, a binding
            // resolved first. A URL there draws what an unresolved name
            // draws, and a debuggable build names it once.
            CircleImageSource.nameUrl(
                ResourceResolver.drawableName(DynamicImageComponent.rawSource(json, a), data), context
            )
            val resourceId = DynamicImageComponent.resourceId(json, a) {
                ResourceResolver.resolveDrawable(it, data, context)
            }

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
            }

            // contentMode, read as Image reads it (a binding resolves) and
            // drawn with Image's table: no contentMode draws the declared
            // default, fit.
            val mode = TypedAttrs.enumStringResolved(a.contentMode, data) { it.json }
            val contentScale = ImageContentScale.scale(mode)
            val contentAlignment = ImageContentScale.alignment(mode)

            // A resource that is not found draws no image, and the node still
            // takes its place and its stages, as on Image.
            if (resourceId != 0) {
                Image(
                    painter = painterResource(id = resourceId),
                    contentDescription = contentDescription,
                    contentScale = contentScale,
                    alignment = contentAlignment,
                    modifier = modifier
                )
            } else {
                Box(modifier = modifier)
            }
        }
    }
}

/**
 * A CircleImage's src is a local image name; a URL belongs to NetworkImage
 * (the SSoT's Image.src, 4f's ruling, jsonui-cli 1.9.0). The component loaded a
 * src beginning "http" from the network; it now reads Image's source, so a URL
 * draws what an unresolved name draws — nothing — and a debuggable build says
 * why, once. A URL is a value with a scheme and "://" (`https://…`,
 * `file://…`); a drawable NAME beginning "http" (`http_badge`) is not one —
 * the old test read it as a URL and loaded it from the network.
 */
internal object CircleImageSource {
    private const val TAG = "JsonUICircleImage"
    const val MESSAGE = "CircleImage src is a local image name; a URL belongs to NetworkImage"

    /** Test hook: receives every emitted warning message. */
    var warningSink: ((String) -> Unit)? = null

    private val named = java.util.concurrent.atomic.AtomicBoolean(false)
    private val URL = Regex("^[A-Za-z][A-Za-z0-9+.-]*://")

    fun looksLikeUrl(value: String?): Boolean = value != null && URL.containsMatchIn(value.trim())

    /** Names a URL the source resolved to, once per process, in a debuggable build. */
    fun nameUrl(resolvedSource: String?, context: Context?) {
        if (!looksLikeUrl(resolvedSource)) return
        if (!DebugDiagnostics.isAppDebuggable(context)) return
        if (!named.compareAndSet(false, true)) return
        val message = "$MESSAGE: '$resolvedSource'"
        warningSink?.invoke(message)
        Log.w(TAG, message)
    }

    internal fun resetForTest() = named.set(false)
}
