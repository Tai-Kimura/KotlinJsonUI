package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.KeyboardAvoidanceTestViewModel

data class KeyboardAvoidanceTestData(
    var dynamicModeStatus: String = "OFF",
    var textField1: String = "",
    var textField2: String = "",
    var textField3: String = "",
    var textField4: String = "",
    var textField5: String = "",
    var textView: String = "",
    var title: String = "Keyboard Avoidance Test"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): KeyboardAvoidanceTestData {
            return KeyboardAvoidanceTestData(
                dynamicModeStatus = map["dynamicModeStatus"] as? String ?: "",
                textField1 = map["textField1"] as? String ?: "",
                textField2 = map["textField2"] as? String ?: "",
                textField3 = map["textField3"] as? String ?: "",
                textField4 = map["textField4"] as? String ?: "",
                textField5 = map["textField5"] as? String ?: "",
                textView = map["textView"] as? String ?: "",
                title = map["title"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: KeyboardAvoidanceTestViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["dynamicModeStatus"] = dynamicModeStatus
        map["textField1"] = textField1
        map["textField2"] = textField2
        map["textField3"] = textField3
        map["textField4"] = textField4
        map["textField5"] = textField5
        map["textView"] = textView
        map["title"] = title
        
        // Add onclick action lambdas if viewModel is provided
        viewModel?.let { vm ->
            map["submitForm"] = { vm.submitForm() }
        }
        
        return map
    }
}
