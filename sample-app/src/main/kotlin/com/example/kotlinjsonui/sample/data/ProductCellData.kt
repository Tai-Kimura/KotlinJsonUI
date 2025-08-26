package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.ProductCellViewModel

data class ProductCellData(
    var item: Map<String, Any> = {}
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): ProductCellData {
            return ProductCellData(
                item = map["item"] as? Map<String, Any>
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: ProductCellViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["item"] = item
        
        return map
    }
}
