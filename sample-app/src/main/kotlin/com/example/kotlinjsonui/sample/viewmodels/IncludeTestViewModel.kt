package com.example.kotlinjsonui.sample.viewmodels
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import com.example.kotlinjsonui.sample.data.IncludeTestData
import com.kotlinjsonui.core.DynamicModeManager
class IncludeTestViewModel(application: Application) : AndroidViewModel(application) {
    // JSON file reference for hot reload
    val jsonFileName = "include_test"
    
    // Data model
    private val _data = MutableStateFlow(IncludeTestData())
    // The layout binds each display line as one value (binding-mixed-text,
    // jsonui-cli 1.9.6: a layout holds no logic); the lines are composed here,
    // from the state, every time it changes — this screen's own row and the
    // lines it hands included2 through the include maps.
    val data: StateFlow<IncludeTestData> = _data.map(::withDisplayTexts)
        .stateIn(viewModelScope, SharingStarted.Eagerly, withDisplayTexts(_data.value))

    private fun withDisplayTexts(d: IncludeTestData): IncludeTestData = d.copy(
        mainCountLabel = "Count=${d.mainCount}, ",
        userNameLabel = "User=${d.userName}, ",
        mainStatusLabel = "Status=${d.mainStatus}",
        userTitleLine = "Title: ${d.userName}",
        mainStatusLine = "Status: ${d.mainStatus}",
        mainCountLine = "Count: ${d.mainCount}"
    )

    // Dynamic mode toggle
    fun toggleDynamicMode() {
        // Toggle the actual DynamicModeManager
        val newState = DynamicModeManager.toggleDynamicMode(getApplication())
        
        // Update the UI status based on actual state
        val statusText = if (newState == true) "ON" else "OFF"
        _data.value = _data.value.copy(dynamicModeStatus = "Dynamic Mode: ${statusText}")
    }
    // Child ViewModels for included views
    val included1ViewModel = Included1ViewModel(getApplication())
    val included2ViewModel = Included2ViewModel(getApplication())
    // Action handlers
    fun incrementCount() {
        _data.value = _data.value.copy(mainCount = _data.value.mainCount + 1)
    }
    
    fun decrementCount() {
        _data.value = _data.value.copy(mainCount = _data.value.mainCount - 1)
    }
    
    fun resetCount() {
        _data.value = _data.value.copy(mainCount = 0)
    }
    
    fun changeUserName() {
        val names = listOf("Alice", "Bob", "Charlie", "Diana", "Eve")
        val currentIndex = names.indexOf(_data.value.userName)
        val nextIndex = (currentIndex + 1) % names.size
        _data.value = _data.value.copy(userName = names[nextIndex])
    }
    
    fun toggleStatus() {
        val newStatus = if (_data.value.mainStatus == "Main Active") "Main Inactive" else "Main Active"
        _data.value = _data.value.copy(mainStatus = newStatus)
    }
    
    // Add more action handlers as needed
    fun updateData(updates: Map<String, Any>) {
        val currentDataMap = _data.value.toMap().toMutableMap()
        currentDataMap.putAll(updates)
        _data.value = IncludeTestData.fromMap(currentDataMap)
    }
    
    init {
        // Wire JSON-declared event handlers: current kjui codegen invokes
        // handlers through the data model (data.<name>?.invoke(...)).
        _data.value = _data.value.copy(
            toggleDynamicMode = { toggleDynamicMode() },
            incrementCount = { incrementCount() },
            decrementCount = { decrementCount() },
            resetCount = { resetCount() },
            changeUserName = { changeUserName() },
            toggleStatus = { toggleStatus() }
        )
    }
}
