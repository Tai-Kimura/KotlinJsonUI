package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.graphics.Color
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.LocalSafeAreaConfig
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.UnappliedAttributes
import com.kotlinjsonui.dynamic.components.DynamicContainerComponent.Companion.renderChildInBox
import com.kotlinjsonui.dynamic.components.DynamicContainerComponent.Companion.renderChildInColumn
import com.kotlinjsonui.dynamic.components.DynamicContainerComponent.Companion.renderChildInRow
import com.kotlinjsonui.dynamic.generated.SafeAreaViewAttributes
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.SafeAreaEdges
import com.kotlinjsonui.dynamic.rememberTypedAttrs
import androidx.compose.ui.platform.LocalContext

/**
 * Dynamic SafeAreaView Component Converter
 * Converts JSON to SafeAreaView composable at runtime
 *
 * Supported JSON attributes:
 * - edges: Array of edges to apply padding ["top", "bottom", "start", "end", "all"]
 * - orientation: "horizontal" or "vertical" - determines stack direction for children
 * - ignoreKeyboard: Boolean to ignore keyboard padding
 * - background: String hex color for background
 * - child/children: Child components to render within safe area
 * - padding/margins: Additional spacing properties
 *
 * This component ensures content doesn't overlap with system UI elements
 * like status bar, navigation bar, and software keyboard
 *
 * SafeAreaView needs special modifier ordering: background must go BEFORE
 * systemBarsPadding so it extends to screen edges. Therefore we build
 * the modifier chain manually instead of using the composite buildModifier.
 *
 * Attribute access goes through the generated [SafeAreaViewAttributes]
 * extraction via the [TypedAttrs] bridge; the node itself is only passed
 * wholesale to the shared ModifierBuilder helpers.
 */
class DynamicSafeAreaViewComponent {
    companion object {
        /** SafeAreaView-specific attributes this component applies (see UnappliedAttributes). */
        private val APPLIED: Set<String> = setOf(
            "safeAreaInsetPositions", "orientation", "direction"
        )

        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            val context = LocalContext.current
            val a = rememberTypedAttrs(json) { m, canonicalOnly ->
                SafeAreaViewAttributes.parse(m, canonicalOnly)
            }
            UnappliedAttributes.check(
                "SafeAreaView", json,
                declared = SafeAreaViewAttributes.declaredAttributes,
                applied = UnappliedAttributes.COMMON_APPLIED + APPLIED,
                context = context
            )

            // Get parent SafeAreaConfig (e.g., from TabView)
            val safeAreaConfig = LocalSafeAreaConfig.current

            // Parse edges to apply safe area padding
            // 'edges' is an undeclared legacy runtime extra (canonical
            // spelling is safeAreaInsetPositions); legacy priority order kept
            // Edge vocabulary lives in SafeAreaEdges — the same rows are
            // declared on plain View and must not be read two different ways.
            // SafeAreaView is the one component that defaults to `all`:
            // reserving the whole safe area is what it IS.
            val requestedEdges = SafeAreaEdges.requested(
                json, a.safeAreaInsetPositions,
                SafeAreaViewAttributes.SafeAreaInsetPositions.declaredSpellings, "SafeAreaView.safeAreaInsetPositions"
            ) ?: SafeAreaEdges.ALL
            val edges = SafeAreaEdges.filtered(
                requestedEdges,
                ignoreTop = safeAreaConfig.ignoreTop,
                ignoreBottom = safeAreaConfig.ignoreBottom
            )

            // Check if keyboard padding should be applied
            // ('ignoreKeyboard' is an undeclared legacy runtime extra)
            val ignoreKeyboard = TypedAttrs.undeclared(json, "ignoreKeyboard")?.asBoolean ?: false

            // Parse orientation for child layout (null means Box/ZStack)
            val orientation = TypedAttrs.enumString(a.orientation) { it.json }

            // The standard stages in their standard order, except that the
            // background — and the clickable after it — go BEFORE the
            // system-bar padding, so they reach the screen edges. This chain
            // is built by hand for that one inversion; it applied no alpha,
            // shadow, radius, border or click, which the SSoT declares on
            // every type (`common`).
            // applyClickable also applies userInteractionEnabled and the
            // node's long press, pan and pinch, as on every component.
            var modifier: Modifier = Modifier
            // margins outside the size, as on every component: after it, as
            // this chain had them, they padded the inside of the view and the
            // view took no more room (measured: 111x53 with and without).
            modifier = ModifierBuilder.applyMargins(modifier, json, data)
            modifier = ModifierBuilder.applySize(modifier, json, defaultFillMaxWidth = true, data)
            // offset after size and OUTSIDE the background, so the
            // background moves with the view — as in the standard order.
            modifier = ModifierBuilder.applyOffset(modifier, json, data)
            // testTag after offset, before alpha: the tagged box is the drawn box
            // (margins and offset outside it) — ticket kjui-a11y-bounds-of-a-margined-view-include-its-margin.
            modifier = ModifierBuilder.applyTestTag(modifier, json)
            modifier = ModifierBuilder.applyAlpha(modifier, json, data)
            modifier = ModifierBuilder.applyShadow(modifier, json, data)
            modifier = ModifierBuilder.applyBackground(modifier, json, data, context)
            modifier = ModifierBuilder.applyClickable(modifier, json, data)

            // Apply safe area padding based on edges (after background)
            modifier = SafeAreaEdges.apply(modifier, edges)
            modifier = SafeAreaEdges.applyKeyboard(modifier, ignoreKeyboard)

            modifier = ModifierBuilder.applyPadding(modifier, json, data)

            // Get children - support both 'child' and 'children'
            val childrenArray: JsonArray = when {
                json.has("children") && json.get("children").isJsonArray ->
                    json.getAsJsonArray("children")
                json.has("child") && json.get("child").isJsonArray ->
                    json.getAsJsonArray("child")
                json.has("child") && json.get("child").isJsonObject -> {
                    // Single child as object, wrap in array
                    JsonArray().apply { add(json.getAsJsonObject("child")) }
                }
                else -> JsonArray() // Empty array if no children
            }

            // Render content in safe area based on orientation
            val childList = mutableListOf<JsonObject>()
            childrenArray.forEach { element ->
                if (element.isJsonObject) {
                    childList.add(element.asJsonObject)
                }
            }

            // `direction` reverses the children along the orientation axis —
            // bottomToTop on a vertical stack, rightToLeft on a horizontal
            // one, natural order otherwise (same two values the container
            // paths honour). Nothing here read it, so the declaration was
            // inert on this face (codegen_effect SafeAreaView.direction).
            val direction = TypedAttrs.enumString(a.direction) { it.json }
            if ((direction == "bottomToTop" && orientation == "vertical") ||
                (direction == "rightToLeft" && orientation == "horizontal")
            ) {
                childList.reverse()
            }
            // ...and stacks them from the edge the direction starts from, the
            // first child at the bottom (rightmost), as a View does (user
            // ruling, 2026-10-05; DynamicContainerComponent.stacksFromTheBottom
            // / stacksFromTheEnd). Reversed alone they were stacked from the
            // top: three 40s in a full-height SafeAreaView sat at 80 / 40 / 0
            // where web draws them at the bottom. A gravity naming a place on
            // that axis still wins; SafeAreaView declares no distribution.
            val gravity = ModifierBuilder.resolvedAlignFlags(json)
            val bottomUp = direction == "bottomToTop" && orientation == "vertical" &&
                !(gravity.alignTop || gravity.alignBottom || gravity.centerV || gravity.centerInParent)
            val endFirst = direction == "rightToLeft" && orientation == "horizontal" &&
                !(gravity.alignLeft || gravity.alignRight || gravity.centerH || gravity.centerInParent)

            // Route children through the scope-aware renderers used by
            // DynamicContainerComponent so weight/alignment/visibility are
            // applied in the correct scope. Plain DynamicView(s) calls do
            // not apply weight, which causes children like ScrollView with
            // weight:1 to lose their slot and push later siblings off-screen.
            // `spacing` is declared on SafeAreaView (51-E) and the codegen
            // face emits Arrangement.spacedBy for it; nothing here read it,
            // so the declared gap collapsed to 0 on the dynamic path only
            // (SafeAreaView_spacing__static/binding parity d=27, run
            // 31202080745).
            val spacing = TypedAttrs.float(a.spacing, data)
            when (orientation) {
                "horizontal" -> {
                    Row(
                        modifier = modifier,
                        horizontalArrangement = when {
                            spacing != null && endFirst -> Arrangement.spacedBy(spacing.dp, Alignment.End)
                            spacing != null -> Arrangement.spacedBy(spacing.dp)
                            endFirst -> Arrangement.End
                            else -> Arrangement.Start
                        }
                    ) {
                        childList.forEach { child ->
                            renderChildInRow(child, data, context)
                        }
                    }
                }
                "vertical" -> {
                    Column(
                        modifier = modifier,
                        verticalArrangement = when {
                            spacing != null && bottomUp -> Arrangement.spacedBy(spacing.dp, Alignment.Bottom)
                            spacing != null -> Arrangement.spacedBy(spacing.dp)
                            bottomUp -> Arrangement.Bottom
                            else -> Arrangement.Top
                        }
                    ) {
                        childList.forEach { child ->
                            renderChildInColumn(child, data, context)
                        }
                    }
                }
                else -> { // No orientation = Box (ZStack equivalent)
                    Box(modifier = modifier) {
                        childList.forEach { child ->
                            renderChildInBox(child, data, context)
                        }
                    }
                }
            }
        }
    }
}
