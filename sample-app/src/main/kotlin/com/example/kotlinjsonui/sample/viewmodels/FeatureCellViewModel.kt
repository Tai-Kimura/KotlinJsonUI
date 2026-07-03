package com.example.kotlinjsonui.sample.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.kotlinjsonui.sample.data.FeatureCellData

class FeatureCellViewModel(application: Application) : AndroidViewModel(application) {
    // JSON file reference for hot reload
    val jsonFileName = "feature_cell"

    // Cell data - managed by parent Collection
    private val _data = MutableStateFlow(FeatureCellData())
    val data: StateFlow<FeatureCellData> = _data.asStateFlow()

    // Data is provided by the parent Collection component as a map
    fun updateData(updates: Map<String, Any>) {
        val merged = _data.value.toMap()
        merged.putAll(updates)
        _data.value = FeatureCellData.fromMap(merged)
    }
}
