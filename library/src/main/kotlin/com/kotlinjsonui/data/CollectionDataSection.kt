package com.kotlinjsonui.data

/**
 * Represents a section in a collection with cells data
 * Similar to SwiftJsonUI's CollectionDataSection
 */
data class CollectionDataSection(
    val header: HeaderFooterData? = null,
    val footer: HeaderFooterData? = null,
    val cells: CellData? = null,
    val columns: Int? = null  // Section-specific columns
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
}