// Pasted from what `kjui build` (kjui_tools of jsonui-cli train/1.9.11 = 5447c616) emits for three Collections
// of conformance_cell cells — cellIdProperty "cellId", "key", and "cellId" with autoChangeTrackingId — and the
// cell view and view model it scaffolds, for CellDataRefreshProbeTest (jsonui-cli ticket
// ios-cell-ignores-data-change-when-cellid-is-fixed-android-updates). Changed from the output: the packages
// (com.kotlinjsonui.probe.* → com.kotlinjsonui.conformance.celldatarefresh) and the cell's two colorResource(R.color.…)
// (the probe project's resources) → the literal colors conformance_cell.json declares.
// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/cell_data_refresh.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.kotlinjsonui.conformance.celldatarefresh


data class CellDataRefreshData(
    var rows_cellid: com.kotlinjsonui.data.CollectionDataSource = com.kotlinjsonui.data.CollectionDataSource(sections = listOf(com.kotlinjsonui.data.CollectionDataSection(cells = com.kotlinjsonui.data.CollectionDataSection.CellData(viewName = "", data = listOf())))),
    var rows_key: com.kotlinjsonui.data.CollectionDataSource = com.kotlinjsonui.data.CollectionDataSource(sections = listOf(com.kotlinjsonui.data.CollectionDataSection(cells = com.kotlinjsonui.data.CollectionDataSection.CellData(viewName = "", data = listOf())))),
    var rows_tracked: com.kotlinjsonui.data.CollectionDataSource = com.kotlinjsonui.data.CollectionDataSource(sections = listOf(com.kotlinjsonui.data.CollectionDataSection(cells = com.kotlinjsonui.data.CollectionDataSection.CellData(viewName = "", data = listOf()))))
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): CellDataRefreshData {
            return CellDataRefreshData(
                rows_cellid = map["rows_cellid"] as? com.kotlinjsonui.data.CollectionDataSource ?: com.kotlinjsonui.data.CollectionDataSource(sections = listOf(com.kotlinjsonui.data.CollectionDataSection(cells = com.kotlinjsonui.data.CollectionDataSection.CellData(viewName = "", data = listOf())))),
                rows_key = map["rows_key"] as? com.kotlinjsonui.data.CollectionDataSource ?: com.kotlinjsonui.data.CollectionDataSource(sections = listOf(com.kotlinjsonui.data.CollectionDataSection(cells = com.kotlinjsonui.data.CollectionDataSection.CellData(viewName = "", data = listOf())))),
                rows_tracked = map["rows_tracked"] as? com.kotlinjsonui.data.CollectionDataSource ?: com.kotlinjsonui.data.CollectionDataSource(sections = listOf(com.kotlinjsonui.data.CollectionDataSection(cells = com.kotlinjsonui.data.CollectionDataSection.CellData(viewName = "", data = listOf()))))
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["rows_cellid"] = rows_cellid
        map["rows_key"] = rows_key
        map["rows_tracked"] = rows_tracked
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
