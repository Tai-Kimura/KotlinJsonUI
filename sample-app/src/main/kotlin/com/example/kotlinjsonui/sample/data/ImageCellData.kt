package com.example.kotlinjsonui.sample.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kotlinjsonui.sample.viewmodels.ImageCellViewModel

data class ImageCellData(
    var imageUrl: String = "https://picsum.photos/200/150",
    var title: String = "Image Item",
    var price: String = "$99.99"
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): ImageCellData {
            return ImageCellData(
                imageUrl = map["imageUrl"] as? String ?: "",
                title = map["title"] as? String ?: "",
                price = map["price"] as? String ?: ""
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(viewModel: ImageCellViewModel? = null): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["imageUrl"] = imageUrl
        map["title"] = title
        map["price"] = price
        
        return map
    }
}
