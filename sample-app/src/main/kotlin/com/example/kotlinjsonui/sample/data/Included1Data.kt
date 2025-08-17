package com.example.kotlinjsonui.sample.data

data class Included1Data(
    var title: String = "Included1"
    // Add more data properties as needed based on your JSON structure
) {
    // Update properties from map
    fun update(map: Map<String, Any>) {
        map["title"]?.let { 
            if (it is String) title = it 
        }
    }
    
    // Convert to map for dynamic mode
    fun toMap(viewModel: Any? = null): Map<String, Any> {
        return mutableMapOf(
            "title" to title
            // Add action handlers if viewModel is provided
        )
    }
}
