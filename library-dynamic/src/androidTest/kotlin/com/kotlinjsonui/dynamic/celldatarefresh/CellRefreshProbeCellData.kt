// Pasted from what `kjui build` (kjui_tools of jsonui-cli 5447c616 + the sibling-locals fix, c38b15be) emits for three
// Collections of cell_refresh_probe_cell cells — cellIdProperty "cellId", "key", and "cellId" with
// autoChangeTrackingId, each in a View of its own — and the cell view and view model it scaffolds, for
// DynamicCellDataRefreshProbeTest (jsonui-cli ticket ios-cell-ignores-data-change-when-cellid-is-fixed-android-updates).
// Changed from the output: the packages (com.kotlinjsonui.probe.* → com.kotlinjsonui.dynamic.celldatarefresh).
// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/cell_refresh_probe_cell.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.kotlinjsonui.dynamic.celldatarefresh


data class CellRefreshProbeCellData(
    var title: String = ""
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): CellRefreshProbeCellData {
            return CellRefreshProbeCellData(
                title = map["title"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["title"] = title
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
