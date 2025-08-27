package com.example.kotlinjsonui.sample.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import com.example.kotlinjsonui.sample.data.CategoryFooterData

class CategoryFooterViewModel(application: Application) : AndroidViewModel(application) {
    // Cell data - managed by parent Collection
    var data by mutableStateOf(CategoryFooterData())
        private set
    
    // This is a cell view model
    // Data is typically provided by the parent Collection component
    
    fun updateData(newData: CategoryFooterData) {
        data = newData
    }
    
}
