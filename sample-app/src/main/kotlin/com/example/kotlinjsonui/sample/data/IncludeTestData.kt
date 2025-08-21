package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.IncludeTestViewModel

data class IncludeTestData(
    var dynamicModeStatus: String = "OFF",
    var mainCount: Int = 100,
    var mainStatus: String = "Main Active",
    var title: String = "Include Component Test",
    var userName: String = "Test User",
    var title: String = "Included1",
    var viewCount: Int = 0,
    var viewStatus: String = "Default Status",
    var viewTitle: String = "Default Title",
    var viewCount: Int = 0,
    var viewStatus: String = "Default Status",
    var viewTitle: String = "Default Title",
    var viewCount: Int = 0,
    var viewStatus: String = "Default Status",
    var viewTitle: String = "Default Title",
    var title: String = "Included1"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): IncludeTestData {
            return IncludeTestData(
                dynamicModeStatus = map["dynamicModeStatus"] as? String ?: "",
                mainCount = (map["mainCount"] as? Number)?.toInt() ?: 0,
                mainStatus = map["mainStatus"] as? String ?: "",
                title = map["title"] as? String ?: "",
                userName = map["userName"] as? String ?: "",
                title = map["title"] as? String ?: "",
                viewCount = (map["viewCount"] as? Number)?.toInt() ?: 0,
                viewStatus = map["viewStatus"] as? String ?: "",
                viewTitle = map["viewTitle"] as? String ?: "",
                viewCount = (map["viewCount"] as? Number)?.toInt() ?: 0,
                viewStatus = map["viewStatus"] as? String ?: "",
                viewTitle = map["viewTitle"] as? String ?: "",
                viewCount = (map["viewCount"] as? Number)?.toInt() ?: 0,
                viewStatus = map["viewStatus"] as? String ?: "",
                viewTitle = map["viewTitle"] as? String ?: "",
                title = map["title"] as? String ?: ""
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
        map["title"] = title
        map["viewCount"] = viewCount
        map["viewStatus"] = viewStatus
        map["viewTitle"] = viewTitle
        map["viewCount"] = viewCount
        map["viewStatus"] = viewStatus
        map["viewTitle"] = viewTitle
        map["viewCount"] = viewCount
        map["viewStatus"] = viewStatus
        map["viewTitle"] = viewTitle
        map["title"] = title
        
        // Add onclick action lambdas if viewModel is provided
        viewModel?.let { vm ->
            map["toggleDynamicMode"] = { vm.toggleDynamicMode() }
            map["incrementCount"] = { vm.incrementCount() }
            map["decrementCount"] = { vm.decrementCount() }
            map["resetCount"] = { vm.resetCount() }
            map["changeUserName"] = { vm.changeUserName() }
            map["toggleStatus"] = { vm.toggleStatus() }
        }
        
        return map
    }
}
