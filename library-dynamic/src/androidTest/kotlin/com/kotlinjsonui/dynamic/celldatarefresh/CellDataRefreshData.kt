// Pasted from what `kjui build` (kjui_tools of jsonui-cli 5447c616 + the sibling-locals fix, c38b15be) emits for three
// Collections of cell_refresh_probe_cell cells — cellIdProperty "cellId", "key", and "cellId" with
// autoChangeTrackingId, each in a View of its own — and the cell view and view model it scaffolds, for
// DynamicCellDataRefreshProbeTest (jsonui-cli ticket ios-cell-ignores-data-change-when-cellid-is-fixed-android-updates).
// Changed from the output: the packages (com.kotlinjsonui.probe.* → com.kotlinjsonui.dynamic.celldatarefresh).
// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/cell_data_refresh.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.kotlinjsonui.dynamic.celldatarefresh


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
