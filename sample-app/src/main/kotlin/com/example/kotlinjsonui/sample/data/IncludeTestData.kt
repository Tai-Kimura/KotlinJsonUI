package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.IncludeTestViewModel

data class IncludeTestData(
    var dynamicModeStatus: String = "OFF",
    var mainCount: Int = 100,
    var mainStatus: String = "Main Active",
    var title: String = "Include Component Test",
    var userName: String = "Test User"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): IncludeTestData {
            return IncludeTestData(
                dynamicModeStatus = map["dynamicModeStatus"] as? String ?: "",
                mainCount = (map["mainCount"] as? Number)?.toInt() ?: 0,
                mainStatus = map["mainStatus"] as? String ?: "",
                title = map["title"] as? String ?: "",
                userName = map["userName"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: IncludeTestViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["dynamicModeStatus"] = dynamicModeStatus
        map["mainCount"] = mainCount
        map["mainStatus"] = mainStatus
        map["title"] = title
        map["userName"] = userName
        
        // Add onclick action lambdas if viewModel is provided
        viewModel?.let { vm ->
            map["incrementCount"] = { vm.incrementCount() }
            map["decrementCount"] = { vm.decrementCount() }
            map["resetCount"] = { vm.resetCount() }
            map["changeUserName"] = { vm.changeUserName() }
            map["toggleStatus"] = { vm.toggleStatus() }
        }
        
        return map
    }
}
