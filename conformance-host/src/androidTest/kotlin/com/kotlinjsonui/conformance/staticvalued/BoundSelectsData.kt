// Pasted from what `kjui build` (kjui_tools of jsonui-cli triage/selectbox-item-binding 640dd2f6, on
// rel/v1.8.121) emits for four SelectBoxes bound to the data — selectedItem, selectedValue, selectedDate,
// selectedIndex — for ticket selectbox-selected-item-binding-is-read-once. Changed from the output: the
// package (com.kotlinjsonui.probe.* → com.kotlinjsonui.conformance.staticvalued) and the unused
// `import com.kotlinjsonui.probe.R`.
// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/bound_selects.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.kotlinjsonui.conformance.staticvalued


data class BoundSelectsData(
    var sbiSel: String = "pp",
    var sbvSel: String = "pp",
    var sbdDate: String = "2026-01-02",
    var sbIdx: Int = 0
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): BoundSelectsData {
            return BoundSelectsData(
                sbiSel = map["sbiSel"] as? String ?: "pp",
                sbvSel = map["sbvSel"] as? String ?: "pp",
                sbdDate = map["sbdDate"] as? String ?: "2026-01-02",
                sbIdx = (map["sbIdx"] as? Number)?.toInt() ?: 0
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["sbiSel"] = sbiSel
        map["sbvSel"] = sbvSel
        map["sbdDate"] = sbdDate
        map["sbIdx"] = sbIdx
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
