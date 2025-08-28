package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.UserProfileTestViewModel

data class UserProfileTestData(
    var userName: String = "John Doe",
    var userEmail: String = "john.doe@example.com",
    var userAvatar: String = "https://i.pravatar.cc/150?img=1",
    var isOnline: Boolean = true,
    var userStatus: String = "Active",
    var notificationCount: Int = 5
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): UserProfileTestData {
            return UserProfileTestData(
                userName = map["userName"] as? String ?: "",
                userEmail = map["userEmail"] as? String ?: "",
                userAvatar = map["userAvatar"] as? String ?: "",
                isOnline = map["isOnline"] as? Boolean ?: false,
                userStatus = map["userStatus"] as? String ?: "",
                notificationCount = (map["notificationCount"] as? Number)?.toInt() ?: 0
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: UserProfileTestViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["userName"] = userName
        map["userEmail"] = userEmail
        map["userAvatar"] = userAvatar
        map["isOnline"] = isOnline
        map["userStatus"] = userStatus
        map["notificationCount"] = notificationCount
        
        // Add onclick action lambdas if viewModel is provided
        viewModel?.let { vm ->
            map["toggleOnlineStatus"] = { vm.toggleOnlineStatus() }
            map["toggleDynamicMode"] = { vm.toggleDynamicMode() }
        }
        
        return map
    }
}
