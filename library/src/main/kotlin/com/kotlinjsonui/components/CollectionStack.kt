package com.kotlinjsonui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Mode for [CollectionStack]. Mirrors `SwiftJsonUI.CollectionStackMode`.
 *
 * Why this exists: Compose's `LazyColumn` / `LazyRow` virtualize cell composition
 * as the viewport changes. With heavy cells (rich text, images) this can cause
 * stutter / re-composition cascades. `CollectionStack` lets layout JSON pick:
 *
 *   - LAZY  : LazyColumn / LazyRow (default, virtualized)
 *   - EAGER : Column(verticalScroll) / Row(horizontalScroll) (no virtualization,
 *             smooth for heavy cells)
 *   - NONE  : Column / Row only (parent already provides scrolling)
 */
enum class CollectionStackMode {
    LAZY,
    EAGER,
    NONE;

    companion object {
        fun fromJson(value: Any?): CollectionStackMode = when (value) {
            "lazy" -> LAZY
            "eager" -> EAGER
            "none", false -> NONE
            true, null -> LAZY
            else -> if (value is String) {
                runCatching { valueOf(value.uppercase()) }.getOrDefault(LAZY)
            } else LAZY
        }
    }
}

enum class CollectionStackAxis {
    VERTICAL,
    HORIZONTAL
}

/**
 * Stack-based collection container with lazy / eager / none modes.
 *
 * Compose's `LazyListScope` (used by `LazyColumn`/`LazyRow`) is incompatible
 * with the `@Composable` lambdas used by `Column`/`Row`, so callers must
 * supply both [lazyContent] and [eagerContent]. The mode-axis switch picks
 * the right closure at runtime; the cell-emission code lives in the caller's
 * generated view.
 *
 * For [CollectionStackMode.NONE] (no scroll container), [lazyContent] is
 * ignored.
 */
@Composable
fun CollectionStack(
    mode: CollectionStackMode,
    axis: CollectionStackAxis = CollectionStackAxis.VERTICAL,
    modifier: Modifier = Modifier,
    spacing: Dp = 0.dp,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    userScrollEnabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    insetLeading: Dp = 0.dp,
    insetTrailing: Dp = 0.dp,
    reverseLayout: Boolean = false,
    lazyState: LazyListState? = null,
    lazyContent: LazyListScope.() -> Unit = {},
    eagerContent: @Composable () -> Unit = {}
) {
    when {
        axis == CollectionStackAxis.VERTICAL && mode == CollectionStackMode.LAZY -> {
            val state = lazyState ?: rememberLazyListState()
            LazyColumn(
                modifier = modifier,
                state = state,
                contentPadding = contentPadding,
                reverseLayout = reverseLayout,
                verticalArrangement = if (spacing > 0.dp) Arrangement.spacedBy(spacing) else Arrangement.Top,
                horizontalAlignment = horizontalAlignment,
                userScrollEnabled = userScrollEnabled,
                content = lazyContent
            )
        }
        axis == CollectionStackAxis.VERTICAL && mode == CollectionStackMode.EAGER -> {
            val scrollState = rememberScrollState()
            Column(
                modifier = if (userScrollEnabled) modifier.verticalScroll(scrollState) else modifier,
                verticalArrangement = if (spacing > 0.dp) Arrangement.spacedBy(spacing) else Arrangement.Top,
                horizontalAlignment = horizontalAlignment
            ) { eagerContent() }
        }
        axis == CollectionStackAxis.VERTICAL && mode == CollectionStackMode.NONE -> {
            Column(
                modifier = modifier,
                verticalArrangement = if (spacing > 0.dp) Arrangement.spacedBy(spacing) else Arrangement.Top,
                horizontalAlignment = horizontalAlignment
            ) { eagerContent() }
        }
        axis == CollectionStackAxis.HORIZONTAL && mode == CollectionStackMode.LAZY -> {
            val state = lazyState ?: rememberLazyListState()
            // contentInset spacers are emulated via leading/trailing PaddingValues
            // so that the LazyRow itself can still measure correctly.
            val resolvedPadding = if (insetLeading > 0.dp || insetTrailing > 0.dp) {
                PaddingValues(start = insetLeading, end = insetTrailing)
            } else {
                contentPadding
            }
            LazyRow(
                modifier = modifier,
                state = state,
                contentPadding = resolvedPadding,
                reverseLayout = reverseLayout,
                horizontalArrangement = if (spacing > 0.dp) Arrangement.spacedBy(spacing) else Arrangement.Start,
                verticalAlignment = verticalAlignment,
                userScrollEnabled = userScrollEnabled,
                content = lazyContent
            )
        }
        axis == CollectionStackAxis.HORIZONTAL && mode == CollectionStackMode.EAGER -> {
            val scrollState = rememberScrollState()
            Row(
                modifier = if (userScrollEnabled) modifier.horizontalScroll(scrollState) else modifier,
                horizontalArrangement = if (spacing > 0.dp) Arrangement.spacedBy(spacing) else Arrangement.Start,
                verticalAlignment = verticalAlignment
            ) {
                if (insetLeading > 0.dp) Spacer(Modifier.width(insetLeading))
                eagerContent()
                if (insetTrailing > 0.dp) Spacer(Modifier.width(insetTrailing))
            }
        }
        axis == CollectionStackAxis.HORIZONTAL && mode == CollectionStackMode.NONE -> {
            Row(
                modifier = modifier,
                horizontalArrangement = if (spacing > 0.dp) Arrangement.spacedBy(spacing) else Arrangement.Start,
                verticalAlignment = verticalAlignment
            ) {
                if (insetLeading > 0.dp) Spacer(Modifier.width(insetLeading))
                eagerContent()
                if (insetTrailing > 0.dp) Spacer(Modifier.width(insetTrailing))
            }
        }
    }
}
