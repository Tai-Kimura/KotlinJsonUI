package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
        /** The SSoT default of `keyboardAvoidancePadding`; absent means 20, not 0. */
        const val DEFAULT_KEYBOARD_CLEARANCE_DP = 20

        /**
         * `keyboardAvoidancePadding` in dp, or the SSoT default when the
         * layout does not declare it — the same rule the codegen applies, so
         * one layout renders the same clearance both ways.
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

            ModifierBuilder.ApplyLifecycleEffects(json, data)

            // Determine scroll direction
            val isHorizontal = determineScrollDirection(json, a)

            // Keyboard avoidance (default true)
            val keyboardAvoidance = a.keyboardAvoidance != false

            // Build modifier
            var modifier = ModifierBuilder.buildModifier(json, data, context = context)
            if (keyboardAvoidance) {
                modifier = modifier.imePadding()
                // `keyboardAvoidancePadding` (SSoT: number, default 20): while
                // the IME is up the scrollable's viewport ends this far above
                // it, so bringIntoView — which stops a focused field at the
                // viewport edge — leaves the clearance. After imePadding and
                // on the scrollable itself, the same emit the codegen makes
                // (scrollview_component.rb); a padding inside the content
                // would only add scrollable space. Reported 2026-09-22.
                val clearance = keyboardClearanceDp(a)
                val imeUp = WindowInsets.ime.getBottom(LocalDensity.current) > 0
                modifier = modifier.padding(bottom = if (imeUp) clearance.dp else 0.dp)
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
                TypedAttrs.enumString(a.contentInsetAdjustmentBehavior) { it.json },
                horizontal = isHorizontal
            ) ?: PaddingValues(0.dp)

            // defaultScrollAnchor — where the scroll STARTS. Everything sits
            // in ONE lazy item here, so indices cannot anchor; scrollBy is
            // item-agnostic: a huge delta clamps at the end (bottom), and
            // backing up half the consumed extent is the centre. One-shot,
            // same contract as the codegen emit and Collection's anchor.
            val anchor = TypedAttrs.enumString(a.defaultScrollAnchor) { it.json }?.lowercase()
                ?.takeIf { it == "bottom" || it == "center" }
            val listState = rememberLazyListState()
            if (anchor != null) {
                LaunchedEffect(Unit) {
                    val consumed = listState.scrollBy(1e9f)
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
                        children.forEach { child ->
                            DynamicView(child, data)
                        }
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = modifier,
                    contentPadding = safeInset,
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
