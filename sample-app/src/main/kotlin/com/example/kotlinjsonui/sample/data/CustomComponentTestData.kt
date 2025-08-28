package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.CustomComponentTestViewModel
import androidx.compose.ui.graphics.Color

data class CustomComponentTestData(
    var cardTitle: String = "'Dynamic Card Title'",
    var cardSubtitle: String = "'Dynamic subtitle for testing'",
    var itemCount: Int = 10,
    var currentStatus: String = "'Online'",
    var statusColor: Color = Color.Green,
    var notificationCount: Int = 3,
    var dynamicModeStatus: String = "'OFF'"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): CustomComponentTestData {
            return CustomComponentTestData(
                cardTitle = map["cardTitle"] as? String ?: "",
                cardSubtitle = map["cardSubtitle"] as? String ?: "",
                itemCount = (map["itemCount"] as? Number)?.toInt() ?: 0,
                currentStatus = map["currentStatus"] as? String ?: "",
                statusColor = map["statusColor"] as? Color ?: Color.Unspecified,
                notificationCount = (map["notificationCount"] as? Number)?.toInt() ?: 0,
                dynamicModeStatus = map["dynamicModeStatus"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: CustomComponentTestViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["cardTitle"] = cardTitle
        map["cardSubtitle"] = cardSubtitle
        map["itemCount"] = itemCount
        map["currentStatus"] = currentStatus
        map["statusColor"] = statusColor
        map["notificationCount"] = notificationCount
        map["dynamicModeStatus"] = dynamicModeStatus
        
        return map
    }
}
