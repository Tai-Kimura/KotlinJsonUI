package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.ProductCellViewModel

data class ProductCellData(
    var name: String = "",
    var price: String = "",
    var stock: String = "",
    var inStock: Boolean = true
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): ProductCellData {
            return ProductCellData(
                name = map["name"] as? String ?: "",
                price = map["price"] as? String ?: "",
                stock = map["stock"] as? String ?: "",
                inStock = map["inStock"] as? Boolean ?: false
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: ProductCellViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["name"] = name
        map["price"] = price
        map["stock"] = stock
        map["inStock"] = inStock
        
        return map
    }
}
