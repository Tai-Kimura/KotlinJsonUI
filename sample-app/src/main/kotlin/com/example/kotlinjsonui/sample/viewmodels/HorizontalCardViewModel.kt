package com.example.kotlinjsonui.sample.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.kotlinjsonui.sample.data.HorizontalCardData

class HorizontalCardViewModel(application: Application) : AndroidViewModel(application) {
    // JSON file reference for hot reload
    val jsonFileName = "horizontal_card"

    // Cell data - managed by parent Collection
    private val _data = MutableStateFlow(HorizontalCardData())
    val data: StateFlow<HorizontalCardData> = _data.asStateFlow()

    // Data is provided by the parent Collection component as a map
    fun updateData(updates: Map<String, Any>) {
        val merged = _data.value.toMap()
        merged.putAll(updates)
        _data.value = HorizontalCardData.fromMap(merged)
    }
}
