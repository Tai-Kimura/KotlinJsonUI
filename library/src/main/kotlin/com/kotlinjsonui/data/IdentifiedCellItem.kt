package com.kotlinjsonui.data

/**
 * Provides stable identity for Collection cells with data-based identity.
 * Matches SwiftJsonUI's IdentifiedCellItem structure.
 *
 * Used with cellIdProperty to extract a unique ID from cell data,
 * enabling efficient diffing and animations in LazyGrid/LazyColumn.
 *
 * @param id Unique identifier extracted from cell data
 * @param index Position in the collection
 * @param data Cell data map
 */
data class IdentifiedCellItem(
    val id: String,
    val index: Int,
    val data: Map<String, Any>
)
