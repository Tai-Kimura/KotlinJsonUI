package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * The card a Collection draws for a cell it cannot render from a layout (no cell class, or a cell
 * layout that is not found): material3's Card with its own defaults for everything but the modifier.
 *
 * It sits behind this wrapper, as the controls in com.kotlinjsonui.components do, so that a later
 * change of material3's Card is fixed in one place rather than at each call site.
 */
@Composable
internal fun JsonUICard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(modifier = modifier, content = content)
}
