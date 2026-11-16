// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/user_profile_test.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.example.kotlinjsonui.sample.data


data class UserProfileTestData(
    var userName: String = "John Doe",
    var userEmail: String = "john.doe@example.com",
    var userAvatar: String = "https://i.pravatar.cc/150?img=1",
    var isOnline: Boolean = true,
    var userStatus: String = "Active",
    var notificationCount: Int = 5,
    var toggleDynamicMode: (() -> Unit)? = null,
    var toggleOnlineStatus: (() -> Unit)? = null
) {
    companion object {
        // Update properties from map
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any>): UserProfileTestData {
            return UserProfileTestData(
                userName = map["userName"] as? String ?: "John Doe",
                userEmail = map["userEmail"] as? String ?: "john.doe@example.com",
                userAvatar = map["userAvatar"] as? String ?: "https://i.pravatar.cc/150?img=1",
                isOnline = map["isOnline"] as? Boolean ?: true,
                userStatus = map["userStatus"] as? String ?: "Active",
                notificationCount = (map["notificationCount"] as? Number)?.toInt() ?: 5,
                toggleDynamicMode = map["toggleDynamicMode"] as? (() -> Unit)?,
                toggleOnlineStatus = map["toggleOnlineStatus"] as? (() -> Unit)?
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["userName"] = userName
        map["userEmail"] = userEmail
        map["userAvatar"] = userAvatar
        map["isOnline"] = isOnline
        map["userStatus"] = userStatus
        map["notificationCount"] = notificationCount
        toggleDynamicMode?.let { map["toggleDynamicMode"] = it }
        toggleOnlineStatus?.let { map["toggleOnlineStatus"] = it }
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
