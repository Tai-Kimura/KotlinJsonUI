package com.kotlinjsonui.dynamic.components

import com.kotlinjsonui.core.DeclaredSpelling
import com.kotlinjsonui.components.DEFAULT_KEYBOARD_CLEARANCE_DP
import com.kotlinjsonui.components.keyboardAvoidance
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.PaddingValues
import com.kotlinjsonui.dynamic.helpers.ContentInsetBehavior
import androidx.compose.ui.platform.LocalContext
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.DynamicView
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.UnappliedAttributes
import com.kotlinjsonui.dynamic.generated.ScrollViewAttributes
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.rememberTypedAttrs

/**
 * ScrollView component → LazyColumn / LazyRow.
 * Reference: scrollview_component.rb in kjui_tools.
 *
 * Scroll direction priority:
 *   1. horizontalScroll attribute
 *   2. orientation attribute
 *   3. First child View's orientation
 *
 * Attribute access goes through the generated [ScrollViewAttributes]
 * extraction via the [TypedAttrs] bridge; the node itself is only passed
 * wholesale to the shared ModifierBuilder pipeline.
 */
class DynamicScrollViewComponent {
    companion object {
        /** The delta that scrolls to the end: 2^24, exact in float32 (see the anchor). */
        internal const val SCROLL_TO_END_DELTA = 16_777_216f

        /** The SSoT default of `keyboardAvoidancePadding`; absent means 20, not 0. */
        /**
         * `keyboardAvoidancePadding` in dp, or the SSoT default when the
         * layout does not declare it — the same rule the codegen applies, so
         * one layout renders the same clearance both ways. The default is
         * the library's (`DEFAULT_KEYBOARD_CLEARANCE_DP`), spelled once.
         */
        internal fun keyboardClearanceDp(a: ScrollViewAttributes): Int =
            a.keyboardAvoidancePadding?.toInt() ?: DEFAULT_KEYBOARD_CLEARANCE_DP

        /** ScrollView-specific attributes this component applies (see UnappliedAttributes). */
        private val APPLIED: Set<String> = setOf(
            "keyboardAvoidance", "keyboardAvoidancePadding", "scrollEnabled", "orientation", "defaultScrollAnchor"
        )

        @Composable
        fun create(json: JsonObject, data: Map<String, Any> = emptyMap()) {
            val context = LocalContext.current
            val a = rememberTypedAttrs(json) { m, canonicalOnly ->
                ScrollViewAttributes.parse(m, canonicalOnly)
            }
            UnappliedAttributes.check(
                "ScrollView", json,
                declared = ScrollViewAttributes.declaredAttributes,
                applied = UnappliedAttributes.COMMON_APPLIED + APPLIED,
                context = context
            )

            // Determine scroll direction
            val isHorizontal = determineScrollDirection(json, a)

            // Keyboard avoidance (default true)
            val keyboardAvoidance = a.keyboardAvoidance != false

            // The list state is created up here, before the modifier, because
            // keyboard avoidance scrolls through it.
            val listState = rememberLazyListState()

            // Build modifier
            var modifier = ModifierBuilder.buildModifier(json, data, context = context)
            if (keyboardAvoidance) {
                // The library's `Modifier.keyboardAvoidance` — the viewport
                // half (imePadding + the `keyboardAvoidancePadding` band) AND
                // the follow (scroll the focused field up once the IME is
                // up). The codegen emits the same call, so the behaviour has
                // one implementation (2.38.0; until 2.37.0 each render spelled
                // the viewport half itself and left the follow to Compose,
                // which a user's device did not do — see KeyboardAvoidance.kt).
                modifier = modifier.keyboardAvoidance(listState, keyboardClearanceDp(a))
            }

            // scrollEnabled - controls whether user can scroll
            val scrollEnabled = TypedAttrs.boolean(a.scrollEnabled, data) ?: true

            // Get children
            val children = DynamicContainerComponent.getChildren(json)

            // `contentInsetAdjustmentBehavior` — UIKit adjusts by default and
            // the attribute stops it; Compose never adjusts, so the values
            // needing code are the opposite ones. Same mapping the codegen
            // emits (ContentInsetHelper), because the two renders of one
            // layout have to inset by the same amount.
            val safeInset = ContentInsetBehavior.safeAreaPadding(
                // as declared (ScrollView's row is an enum; Collection's is a string)
                DeclaredSpelling.lowered(
                    TypedAttrs.enumString(a.contentInsetAdjustmentBehavior) { it.json },
                    ScrollViewAttributes.ContentInsetAdjustmentBehavior.declaredSpellings
                ),
                horizontal = isHorizontal
            ) ?: PaddingValues(0.dp)

            // defaultScrollAnchor — where the scroll STARTS. Everything sits
            // in ONE lazy item here, so indices cannot anchor; scrollBy is
            // item-agnostic: a huge delta clamps at the end (bottom), and
            // backing up half the consumed extent is the centre. One-shot,
            // same contract as the codegen emit and Collection's anchor.
            val anchor = DeclaredSpelling.lowered(TypedAttrs.enumString(a.defaultScrollAnchor) { it.json }, ScrollViewAttributes.DefaultScrollAnchor.declaredSpellings)
                ?.takeIf { it == "bottom" || it == "center" }
            if (anchor != null) {
                LaunchedEffect(Unit) {
                    // 2^24, not 1e9f: scrollBy reports `delta − leftover` in
                    // float32, and near 1e9 floats are 64 apart, so a 1200 px
                    // extent came back as 1216 and the centre started 8 px
                    // (4 dp) short (jsonui-cli ticket kjui-scrollview-center-
                    // anchor-is-off-by-4). Below 2^24 every integer is exact.
                    val consumed = listState.scrollBy(SCROLL_TO_END_DELTA)
                    if (anchor == "center") listState.scrollBy(-consumed / 2f)
                }
            }

            if (isHorizontal) {
                LazyRow(
                    state = listState,
                    modifier = modifier,
                    contentPadding = safeInset,
                    userScrollEnabled = scrollEnabled
                ) {
                    item {
                        // A LazyRow measures its items with the viewport's
                        // height as the cross-axis max, so a child declaring
                        // more (content 600 in a 200-high ScrollView) was cut
                        // to 200 and centred. Unbounded and top-anchored, it
                        // keeps its declared height from y 0, as web does
                        // (jsonui-cli ticket kjui-horizontal-scrollview-
                        // clamps-content-height-to-the-viewport).
                        Row(Modifier.wrapContentHeight(align = Alignment.Top, unbounded = true)) {
                            children.forEach { child ->
                                DynamicView(child, data)
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = modifier,
                    contentPadding = safeInset,
                    // The ScrollView's own content gravity across the scroll
                    // (common.gravity is "Content gravity/alignment";
                    // gravityDefaults top|start), read as a Column's is. It
                    // was not read, so a ScrollView declaring
                    // centerHorizontal drew a narrow child at the start,
                    // where web centred it (2026-10-05). A child's gravity is
                    // the child's content's and does not place the child.
                    // The kjui codegen: scrollview_component.rb
                    // cross_alignment.
                    horizontalAlignment = DynamicContainerComponent.parseColumnHorizontalAlignment(json),
                    userScrollEnabled = scrollEnabled
                ) {
                    item {
                        children.forEach { child ->
                            DynamicView(child, data)
                        }
                    }
                }
            }
        }

        private fun determineScrollDirection(json: JsonObject, a: ScrollViewAttributes): Boolean {
            // 1. horizontalScroll attribute (highest priority;
            //    undeclared legacy runtime extra)
            TypedAttrs.undeclared(json, "horizontalScroll")?.let {
                return it.asBoolean
            }

            // 2. orientation attribute
            if (a.orientation != null) {
                return TypedAttrs.enumString(a.orientation) { it.json } == "horizontal"
            }

            // 3. First child View's orientation
            val children = DynamicContainerComponent.getChildren(json)
            val firstView = children.firstOrNull { it.get("type")?.asString == "View" }
            if (firstView != null) {
                return firstView.get("orientation")?.asString == "horizontal"
            }

            return false
        }
    }
}
