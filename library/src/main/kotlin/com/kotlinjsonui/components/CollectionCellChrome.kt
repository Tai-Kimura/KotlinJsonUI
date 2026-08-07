package com.kotlinjsonui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

/**
 * The `listStyle` chrome around one Collection cell, shared by the dynamic
 * component and the generated code (51-E vocabulary): `grouped` = surface
 * background + divider, `insetGrouped` = + 16dp horizontal inset and 12dp
 * corners, `sidebar` = softer surface and 8dp corners, `plain` (or anything
 * unrecognised) = the untouched default. `hideSeparator` suppresses the
 * divider a chrome draws — the declaration's own contract is that it is a
 * NO-OP where the container draws no separators, and only a chrome does.
 */
@Composable
fun CollectionCellChrome(
    style: String?,
    hideSeparator: Boolean = false,
    content: @Composable () -> Unit
) {
    val normalized = style?.lowercase() ?: "plain"
    if (normalized == "plain" || normalized.isEmpty() ||
        normalized !in setOf("grouped", "insetgrouped", "sidebar")
    ) {
        content()
        return
    }
    val inset = when (normalized) {
        "insetgrouped", "sidebar" -> 16.dp
        else -> 0.dp
    }
    val corner = when (normalized) {
        "insetgrouped" -> 12.dp
        "sidebar" -> 8.dp
        else -> 0.dp
    }
    val bg = when (normalized) {
        "sidebar" -> MaterialTheme.colorScheme.surfaceContainerLow
        else -> MaterialTheme.colorScheme.surfaceContainer
    }
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = inset)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(corner))
                .background(bg)
        ) { content() }
        if (!hideSeparator) {
            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
}
