// Pasted from what `kjui build` emits for the static layouts of StaticValuedControlsProbeTest —
// kjui_tools of jsonui-cli triage/static-valued-controls 46b7d599 (on rel/v1.8.121 = 32785ce8): a static value seeds the control's own state (ticket
// static-valued-controls-do-not-change-on-a-users-tap). Changed from the output: the package
// (com.kotlinjsonui.probe.* → com.kotlinjsonui.conformance.staticvalued) and the unused
// `import com.kotlinjsonui.probe.R`.
// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/static_controls.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.kotlinjsonui.conformance.staticvalued


data class StaticControlsData(
    var selectedRadiogroup: String = "",
    var selectedGrp: String = ""
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): StaticControlsData {
            return StaticControlsData(
                selectedRadiogroup = map["selectedRadiogroup"] as? String ?: "",
                selectedGrp = map["selectedGrp"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["selectedRadiogroup"] = selectedRadiogroup
        map["selectedGrp"] = selectedGrp
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
