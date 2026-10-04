// Pasted from what `kjui build` (kjui_tools of jsonui-cli train/1.9.11 = 5447c616) emits for three Collections
// of conformance_cell cells — cellIdProperty "cellId", "key", and "cellId" with autoChangeTrackingId — and the
// cell view and view model it scaffolds, for CellDataRefreshProbeTest (jsonui-cli ticket
// ios-cell-ignores-data-change-when-cellid-is-fixed-android-updates). Changed from the output: the packages
// (com.kotlinjsonui.probe.* → com.kotlinjsonui.conformance.celldatarefresh) and the cell's two colorResource(R.color.…)
// (the probe project's resources) → the literal colors conformance_cell.json declares.
// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/conformance_cell.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.kotlinjsonui.conformance.celldatarefresh


data class ConformanceCellData(
    var title: String = ""
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): ConformanceCellData {
            return ConformanceCellData(
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
