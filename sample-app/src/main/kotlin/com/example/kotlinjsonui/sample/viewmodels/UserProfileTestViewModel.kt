package com.example.kotlinjsonui.sample.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.kotlinjsonui.sample.data.UserProfileTestData
import com.kotlinjsonui.core.DynamicModeManager

class UserProfileTestViewModel(application: Application) : AndroidViewModel(application) {
    // JSON file reference for hot reload
    val jsonFileName = "user_profile_test"
    
    // Data model
    private val _data = MutableStateFlow(UserProfileTestData())
    val data: StateFlow<UserProfileTestData> = _data.asStateFlow()
    
    fun updateUserName(name: String) {
        _data.value = _data.value.copy(userName = name)
    }
    
    fun updateUserEmail(email: String) {
        _data.value = _data.value.copy(userEmail = email)
    }
    
    fun toggleOnlineStatus() {
        _data.value = _data.value.copy(isOnline = !_data.value.isOnline)
    }
    
    fun updateNotificationCount(count: Int) {
        _data.value = _data.value.copy(notificationCount = count)
    }
    
    fun updateUserStatus(status: String) {
        _data.value = _data.value.copy(userStatus = status)
    }
    
    fun toggleDynamicMode() {
        // Toggle the actual DynamicModeManager
        val newState = DynamicModeManager.toggleDynamicMode(getApplication())
        
        // Update notification count to show something changed
        if (newState == true) {
            _data.value = _data.value.copy(notificationCount = 10)
        } else {
            _data.value = _data.value.copy(notificationCount = 5)
        }
    }
    
    // Add more action handlers as needed
    fun updateData(updates: Map<String, Any>) {
        val currentDataMap = _data.value.toMap(this).toMutableMap()
        currentDataMap.putAll(updates)
        _data.value = UserProfileTestData.fromMap(currentDataMap)
    }
}