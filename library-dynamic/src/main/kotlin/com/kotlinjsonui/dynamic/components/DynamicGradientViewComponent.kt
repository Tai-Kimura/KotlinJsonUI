package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.kotlinjsonui.core.DeclaredSpelling
import com.kotlinjsonui.dynamic.DynamicView
import com.kotlinjsonui.dynamic.DataBindingContext
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.UnappliedAttributes
import com.kotlinjsonui.dynamic.generated.GradientViewAttributes
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.rememberTypedAttrs

/**
 * Dynamic GradientView Component Converter
 * Converts JSON to Box with gradient background composable at runtime.
 * Reference: gradientview_component.rb in kjui_tools.
 *
 * Supported JSON attributes:
 * - colors/items: Array of color strings for gradient (supports @{binding})
 * - gradientDirection: the declared Vertical | Horizontal | Oblique, as
 *   declared (case and all). The camelCase `leftToRight` / `topToBottom` /
 *   `rightToLeft` / `bottomToTop` are not read (see NOT_READ_DIRECTIONS)
 * - orientation: "horizontal" | "vertical" | "diagonal" gradient direction
 * - startPoint/endPoint: Alternative to orientation (top/bottom, left/right, etc.)
 * - cornerRadius: Float corner radius (applies clip)
 * - child/children: Child components to render inside gradient
 * - Standard modifier attributes (padding, margins, alpha, onClick, etc.)
 *
 * Gradient types:
 * - horizontal → Brush.horizontalGradient
 * - vertical → Brush.verticalGradient
 * - diagonal/other → Brush.linearGradient
 *
 * Defaults to vertical gradient from black to white if no colors specified.
 *
 * Modifier order: testTag → margins → size → alpha → clickable → padding + gradient background
 */
class DynamicGradientViewComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {

            val context = LocalContext.current
            val a = rememberTypedAttrs(json) { m, canonicalOnly ->
                GradientViewAttributes.parse(m, canonicalOnly)
            }
            UnappliedAttributes.check(
                "GradientView", json,
                declared = GradientViewAttributes.declaredAttributes,
                applied = UnappliedAttributes.COMMON_APPLIED + APPLIED,
                context = context
            )

            // `gradient` object wraps { colors, locations, startPoint, endPoint }.
            // When present, each nested field takes precedence over the top-level
            // equivalent (which stays valid as the simpler legacy shape).
            // `gradient` is declared as a color array, but the legacy reader
            // accepts this object wrapper — wider than the declared type, so
            // read raw (see TypedAttrs.rawKey).
            val gradientElement = TypedAttrs.rawKey(json, "gradient")
            val gradientObject = gradientElement
                ?.takeIf { it.isJsonObject }?.asJsonObject
            val effective = gradientObject ?: json

            // The DECLARED shape of `gradient` is the color ARRAY itself —
            // read it first (this path used to accept only the object
            // wrapper and the undeclared 'colors'/'items' spellings, so a
            // declared gradient fell to the black/white default on BOTH
            // kotlin paths while ios/web honored it: parity family
            // kjui-codegen-gradientview / dead declared attr).
            val colorsElement = gradientElement?.takeIf { it.isJsonArray }
                ?: TypedAttrs.undeclared(effective, "colors")
                ?: TypedAttrs.undeclared(effective, "items")
            val colors = when {
                colorsElement?.isJsonArray == true -> {
                    parseColors(colorsElement.asJsonArray, data, context)
                }
                colorsElement?.isJsonPrimitive == true && colorsElement.asString.contains("@{") -> {
                    // Dynamic colors from data binding — canonical
                    // whole-value resolution (flat-first, dot paths).
                    val expr = colorsElement.asString
                    if (expr.startsWith("@{") && expr.endsWith("}")) {
                        resolveColorList(
                            DataBindingContext.evaluateExpression(expr, data).takeIf { it !== expr },
                            data, context
                        )
                    } else {
                        DEFAULT_COLORS
                    }
                }
                else -> DEFAULT_COLORS
            }

            // Optional color stop positions 0..1. `locations` is declared,
            // but the read must also serve the nested gradient wrapper and
            // inspects element shapes raw (see TypedAttrs.rawKey).
            val locations = TypedAttrs.rawKey(effective, "locations")?.takeIf { it.isJsonArray }
                ?.asJsonArray
                ?.mapNotNull { it.takeIf { el -> el.isJsonPrimitive && el.asJsonPrimitive.isNumber }?.asFloat }
                ?.takeIf { it.size == colors.size }

            // Determine gradient brush based on gradientDirection/orientation/startPoint.
            val direction = gradientDirectionOf(a, gradientObject)
            val gradientBrush = resolveGradientBrush(effective, direction, colors, locations)

            // Build modifier: testTag → margins → size → alpha → clickable → padding
            var modifier = ModifierBuilder.buildModifier(json, data, context = context)

            // Apply gradient background
            modifier = modifier.background(gradientBrush)

            // Apply corner radius clip
            TypedAttrs.float(a.common.cornerRadius, data)?.let { radius ->
                modifier = modifier.clip(RoundedCornerShape(radius.dp))
            }

            // Render gradient box with children
            Box(modifier = modifier) {
                val children = DynamicContainerComponent.getChildren(json)
                children.forEach { child ->
                    DynamicView(child, data)
                }
            }
        }

        /** GradientView-specific attributes this component applies (see UnappliedAttributes). */
        private val APPLIED: Set<String> = setOf(
            "gradient", "gradientDirection", "locations"
        )

        private val DEFAULT_COLORS = listOf(Color.Black, Color.White)

        /**
         * `gradientDirection` spellings this component used to read as
         * undeclared legacy extras. GradientView declares Vertical /
         * Horizontal / Oblique only, and neither the kjui codegen nor the
         * other platforms' GradientView draws these, so reading them here
         * made this path render a layout differently from every other. They
         * are not read: a value outside the declared spellings draws the
         * default like any other undeclared value, and a debuggable build
         * names it ("GradientView.gradientDirection: unknown enum value
         * 'X'") — the typed parse for the node's own key, DeclaredSpelling
         * for the one inside the `gradient` object wrapper.
         */
        internal val NOT_READ_DIRECTIONS: List<String> =
            listOf("leftToRight", "topToBottom", "rightToLeft", "bottomToTop")

        /**
         * The declared `gradientDirection`, lowercased (DeclaredSpelling), or
         * null when absent or not one of GradientView's declared spellings.
         * Read from the `gradient` object wrapper when there is one (its
         * fields win, as for colors / locations), else from the node through
         * the typed parse.
         */
        internal fun gradientDirectionOf(a: GradientViewAttributes, wrapper: JsonObject?): String? {
            val spellings = GradientViewAttributes.GradientDirection.declaredSpellings
            if (wrapper == null) {
                // The generated parse saw this value and named an undeclared one.
                return DeclaredSpelling.lowered(TypedAttrs.enumString(a.gradientDirection) { it.json }, spellings)
            }
            val raw = TypedAttrs.rawKey(wrapper, "gradientDirection")?.takeIf { it.isJsonPrimitive }?.asString
            return DeclaredSpelling.lowered(raw, spellings, "GradientView.gradientDirection")
        }

        /**
         * Parse color array from JSON, resolving each color string via ColorParser.
         */
        private fun parseColors(
            jsonArray: JsonArray,
            data: Map<String, Any>,
            context: android.content.Context
        ): List<Color> {
            return jsonArray.mapNotNull { element ->
                when {
                    element.isJsonPrimitive ->
                        ColorParser.parseColorStringWithBinding(element.asString, data, context)
                    else -> null
                }
            }.ifEmpty { DEFAULT_COLORS }
        }

        /**
         * Resolve a bound color list from data (List or Array).
         */
        private fun resolveColorList(
            value: Any?,
            data: Map<String, Any>,
            context: android.content.Context
        ): List<Color> {
            return when (value) {
                is List<*> -> value.mapNotNull { colorStr ->
                    ColorParser.parseColorStringWithBinding(colorStr?.toString(), data, context)
                }
                is Array<*> -> value.mapNotNull { colorStr ->
                    ColorParser.parseColorStringWithBinding(colorStr?.toString(), data, context)
                }
                else -> DEFAULT_COLORS
            }.ifEmpty { DEFAULT_COLORS }
        }

        /**
         * Determine gradient Brush from `gradientDirection` / `orientation` /
         * `startPoint`/`endPoint`. When `locations` provides color stops, a
         * linearGradient is built with explicit colorStops.
         */
        private fun resolveGradientBrush(
            effective: JsonObject,
            direction: String?,
            colors: List<Color>,
            locations: List<Float>?
        ): Brush {
            val (start, end) = resolveGradientEndpoints(effective)

            // Explicit color stops via `locations` → always a linearGradient.
            if (locations != null) {
                val stops = colors.zip(locations).map { (c, l) -> l to c }.toTypedArray()
                return Brush.linearGradient(colorStops = stops, start = start, end = end)
            }

            // Direction-hinted convenience brushes. `direction` is the
            // declared gradientDirection only (gradientDirectionOf), as the
            // kjui codegen reads it; with none, the undeclared legacy
            // 'orientation' decides, as it does in the codegen.
            val hint = direction
                ?: TypedAttrs.undeclared(effective, "orientation")
                    ?.takeIf { it.isJsonPrimitive }?.asString
                    ?.replaceFirstChar { it.lowercaseChar() }
            when (hint) {
                "oblique" -> return Brush.linearGradient(colors)
                "horizontal" -> return Brush.horizontalGradient(colors)
                "vertical" -> return Brush.verticalGradient(colors)
            }

            return Brush.linearGradient(colors = colors, start = start, end = end)
        }

        private fun resolveGradientEndpoints(effective: JsonObject): Pair<Offset, Offset> {
            // 'startPoint'/'endPoint' are undeclared legacy runtime extras at
            // node level; plain object fields inside the gradient wrapper.
            val startName = TypedAttrs.undeclared(effective, "startPoint")?.asString
            val endName = TypedAttrs.undeclared(effective, "endPoint")?.asString
            if (startName != null && endName != null) {
                return namedOffset(startName) to namedOffset(endName)
            }
            // Fall back to a VERTICAL run (top → bottom): the canonical
            // default all render paths share — the ios reference renders
            // vertical and the kjui codegen emits Brush.verticalGradient
            // when no direction is declared (a diagonal fallback here was
            // the dynamic-only deviation).
            return Offset(0f, 0f) to Offset(0f, Float.POSITIVE_INFINITY)
        }

        /**
         * Map named gradient anchor points to Compose Offsets.
         * SwiftUI-ish names (`topLeading`, `bottomTrailing`) and plain
         * directional names (`top`, `bottom`, `left`, `right`) are accepted.
         */
        private fun namedOffset(name: String): Offset {
            val inf = Float.POSITIVE_INFINITY
            return when (name) {
                "top" -> Offset(inf / 2, 0f)
                "bottom" -> Offset(inf / 2, inf)
                "left", "leading" -> Offset(0f, inf / 2)
                "right", "trailing" -> Offset(inf, inf / 2)
                "topLeading", "topLeft" -> Offset(0f, 0f)
                "topTrailing", "topRight" -> Offset(inf, 0f)
                "bottomLeading", "bottomLeft" -> Offset(0f, inf)
                "bottomTrailing", "bottomRight" -> Offset(inf, inf)
                "center" -> Offset(inf / 2, inf / 2)
                else -> Offset(0f, 0f)
            }
        }
    }
}
