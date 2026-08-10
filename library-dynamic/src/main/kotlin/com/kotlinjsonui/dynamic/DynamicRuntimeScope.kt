package com.kotlinjsonui.dynamic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Per-layout-root runtime value store for two-way bindings that the
 * codegen face keeps inside its generated view state.
 *
 * A declaration like `currentPage: "@{currentPage}"` on a paging
 * collection is a TWO-WAY binding: the generated code writes the page
 * back into its view state on every swipe, so sibling readers of the
 * same property (a page indicator, a label) follow along with no
 * view-model involvement. The dynamic face renders from an immutable
 * data map and had no equivalent write channel — the page indicator's
 * dots never moved on the dynamic face while codegen tracked the swipe
 * (a downstream detail hero, 2026-08-10).
 *
 * [DynamicRuntimeScope] hosts a snapshot-backed override map at each
 * layout root (screen render and collection cell render). Components
 * with a two-way contract write through [LocalDynamicRuntimeWriter];
 * the scope re-renders its subtree with `data + overrides`, so every
 * `@{prop}` reader inside the SAME layout root sees the new value.
 * Values arriving from the real data map (a view-model update) still
 * win the moment they change, because the merge is recomputed on every
 * recomposition with the override applied over the CURRENT map.
 */
val LocalDynamicRuntimeWriter = staticCompositionLocalOf<((String, Any) -> Unit)?> { null }

@Composable
fun DynamicRuntimeScope(
    data: Map<String, Any>,
    content: @Composable (Map<String, Any>) -> Unit
) {
    val overrides = remember { mutableStateMapOf<String, Any>() }
    // For each override, the upstream value observed when it was written.
    // An override only bridges until the data map itself moves: the data
    // map is the authority, so once data[key] differs from this baseline
    // the override is ignored (and replaced on the next write).
    val baseline = remember { mutableMapOf<String, Any?>() }
    val currentData = rememberUpdatedState(data)
    // Stable identity so providing it never invalidates the whole subtree.
    val writer = remember(overrides, baseline) {
        { key: String, value: Any ->
            baseline[key] = currentData.value[key]
            overrides[key] = value
        }
    }
    // Reading the override map's entries here subscribes this scope to writes.
    val effective: Map<String, Any> =
        if (overrides.isEmpty()) {
            data
        } else {
            val live = overrides.filter { (key, value) -> data[key] == baseline[key] && data[key] != value }
            if (live.isEmpty()) data else data + live
        }
    CompositionLocalProvider(LocalDynamicRuntimeWriter provides writer) {
        content(effective)
    }
}
