package com.example.kotlinjsonui.sample.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.kotlinjsonui.sample.data.CollectionTestData
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class CollectionTestViewModel(application: Application) : AndroidViewModel(application) {
    // JSON file reference for hot reload
    val jsonFileName = "collection_test"
    
    // Data model
    private val _data = MutableStateFlow(CollectionTestData())
    val data: StateFlow<CollectionTestData> = _data.asStateFlow()
    
    // Dynamic mode status
    var dynamicModeEnabled by mutableStateOf(false)
    var dynamicModeStatus by mutableStateOf("Static Mode")
    
    // Product data for collection
    val products = listOf(
        mapOf(
            "name" to "iPhone 15 Pro",
            "price" to "999",
            "stock" to 15,
            "inStock" to true
        ),
        mapOf(
            "name" to "MacBook Air",
            "price" to "1299",
            "stock" to 8,
            "inStock" to true
        ),
        mapOf(
            "name" to "AirPods Pro",
            "price" to "249",
            "stock" to 0,
            "inStock" to false
        ),
        mapOf(
            "name" to "iPad Air",
            "price" to "599",
            "stock" to 12,
            "inStock" to true
        ),
        mapOf(
            "name" to "Apple Watch",
            "price" to "399",
            "stock" to 5,
            "inStock" to true
        ),
        mapOf(
            "name" to "HomePod mini",
            "price" to "99",
            "stock" to 0,
            "inStock" to false
        )
    )
    
    // Simple items for list
    val simpleItems = listOf(
        mapOf("title" to "Total Sales", "value" to "$12,450"),
        mapOf("title" to "New Orders", "value" to "34"),
        mapOf("title" to "Pending", "value" to "8"),
        mapOf("title" to "Completed", "value" to "156")
    )
    
    // Toggle dynamic mode
    fun toggleDynamicMode() {
        dynamicModeEnabled = !dynamicModeEnabled
        dynamicModeStatus = if (dynamicModeEnabled) "Dynamic Mode" else "Static Mode"
        
        // Trigger recomposition
        _data.value = _data.value.copy()
    }
    
    // Get all data for dynamic view
    fun getAllData(): Map<String, Any> {
        return mapOf(
            "dynamicModeEnabled" to dynamicModeEnabled,
            "dynamicModeStatus" to dynamicModeStatus,
            "products" to products,
            "simpleItems" to simpleItems,
            "toggleDynamicMode" to ::toggleDynamicMode
        )
    }
    
    // Update data
    fun updateData(updates: Map<String, Any>) {
        // Handle specific updates if needed
        _data.value = _data.value.copy()
    }
}