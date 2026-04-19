package com.kotlinjsonui.data

import com.kotlinjsonui.utils.CellIdGenerator

/**
 * Represents a section in a collection with cells data
 * Similar to SwiftJsonUI's CollectionDataSection
 */
data class CollectionDataSection(
    val header: HeaderFooterData? = null,
    val footer: HeaderFooterData? = null,
    val cells: CellData? = null,
    val columns: Int? = null,  // Section-specific columns
    val cellIdProperty: String? = null,
    val autoChangeTrackingId: Boolean = false
) {
    /**
     * Cell data containing view name and data array
     */
    data class CellData(
        val viewName: String,
        val data: List<Map<String, Any>>
    )

    /**
     * Header/Footer data containing view name and single data item
     */
    data class HeaderFooterData(
        val viewName: String,
        val data: Map<String, Any>
    )

    /**
     * Returns a copy with [cellIdProperty] / [autoChangeTrackingId] applied
     * and each cell enriched with `"cellId"`. Idempotent: reapplying yields
     * the same cellIds because the hash excludes the `"cellId"` entry.
     */
    fun reconfigured(
        cellIdProperty: String?,
        autoChangeTrackingId: Boolean
    ): CollectionDataSection {
        val newCells = cells?.let { c ->
            if (autoChangeTrackingId && !cellIdProperty.isNullOrEmpty()) {
                val enriched = CellIdGenerator.enrichCellIds(
                    c.data,
                    cellIdProperty
                ) { duplicates ->
                    android.util.Log.w(
                        "CellIdGenerator",
                        "Duplicate cellIds detected: $duplicates. " +
                            "Consider adding a unique field to cellIdProperty."
                    )
                }
                c.copy(data = enriched)
            } else c
        }
        return copy(
            cells = newCells,
            cellIdProperty = cellIdProperty,
            autoChangeTrackingId = autoChangeTrackingId
        )
    }
}

/**
 * Collection data source that provides sections for collection views
 * This matches the SwiftJsonUI CollectionDataSource structure
 */
data class CollectionDataSource(
    val sections: List<CollectionDataSection> = emptyList()
) {
    /**
     * Legacy method for backward compatibility
     * Returns data for a specific cell class from all sections
     */
    fun getCellData(className: String): List<Map<String, Any>> {
        val result = mutableListOf<Map<String, Any>>()

        sections.forEach { section ->
            if (section.cells?.viewName == className) {
                result.addAll(section.cells.data)
            }
        }

        return result
    }

    /**
     * Propagates [cellIdProperty] / [autoChangeTrackingId] to every section.
     * Used by the Dynamic-mode converter to enrich the data source without
     * requiring VM changes.
     */
    fun reconfigured(
        cellIdProperty: String?,
        autoChangeTrackingId: Boolean
    ): CollectionDataSource = copy(
        sections = sections.map {
            it.reconfigured(cellIdProperty, autoChangeTrackingId)
        }
    )
}