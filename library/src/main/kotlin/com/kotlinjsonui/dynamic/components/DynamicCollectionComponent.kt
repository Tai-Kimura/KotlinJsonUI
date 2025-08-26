package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.gson.JsonObject
import com.google.gson.JsonArray
import com.kotlinjsonui.dynamic.DynamicView
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder

/**
 * Dynamic Collection Component Converter
 * Converts JSON to LazyGrid composable at runtime for grid layouts
 * 
 * Supported JSON attributes (matching SwiftUI implementation):
 * - cellClasses: String array of cell class names
 * - headerClasses: String array of header class names (not yet fully supported in grid)
 * - footerClasses: String array of footer class names (not yet fully supported in grid)
 * - items: @{variable} for data source with getCellData support
 * - columns: Number of columns (default: 2)
 * - scrollDirection: "vertical" | "horizontal"
 * - contentPadding: Number or Array for grid content padding
 * - itemSpacing/spacing: Number for spacing between items
 * - cellHeight: Number for fixed cell height
 * - cell: JsonObject template for custom cell layout (fallback)
 * - width/height: Number dimensions
 * - padding/paddings: Number or Array for padding
 * - margins: Array or individual margin properties
 * - background: Background color
 */
class DynamicCollectionComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            // Extract cellClasses, headerClasses, footerClasses as string arrays
            val cellClasses = extractStringArray(json.get("cellClasses")?.asJsonArray)
            val headerClasses = extractStringArray(json.get("headerClasses")?.asJsonArray)
            val footerClasses = extractStringArray(json.get("footerClasses")?.asJsonArray)
            
            // Get first cell class name for primary cell type
            val cellClassName = cellClasses.firstOrNull()
            
            // Parse data binding for items
            val itemsBinding = when {
                json.get("items")?.asString?.contains("@{") == true -> {
                    val pattern = "@\\{([^}]+)\\}".toRegex()
                    pattern.find(json.get("items").asString)?.groupValues?.get(1)
                }
                json.get("bind")?.asString?.contains("@{") == true -> {
                    val pattern = "@\\{([^}]+)\\}".toRegex()
                    pattern.find(json.get("bind").asString)?.groupValues?.get(1)
                }
                else -> null
            }
            
            // Get items from data source
            val items = when {
                // If we have cellClasses and items binding, try to use getCellData
                cellClassName != null && itemsBinding != null -> {
                    val dataSource = data[itemsBinding]
                    when (dataSource) {
                        // Check if it's a collection data source with getCellData method
                        is CollectionDataSource -> {
                            dataSource.getCellData(cellClassName)
                        }
                        // Regular list/array
                        is List<*> -> dataSource
                        is Array<*> -> dataSource.toList()
                        else -> emptyList<Any>()
                    }
                }
                // If we have cellClasses but no items binding, look for collectionDataSource
                cellClassName != null -> {
                    when (val dataSource = data["collectionDataSource"]) {
                        is CollectionDataSource -> {
                            dataSource.getCellData(cellClassName)
                        }
                        else -> emptyList<Any>()
                    }
                }
                // Fallback to regular items binding without cellClasses
                itemsBinding != null -> {
                    when (val boundValue = data[itemsBinding]) {
                        is List<*> -> boundValue
                        is Array<*> -> boundValue.toList()
                        else -> emptyList<Any>()
                    }
                }
                else -> emptyList<Any>()
            }
            
            // Parse grid configuration
            val columns = json.get("columns")?.asInt ?: 2
            val scrollDirection = json.get("scrollDirection")?.asString ?: "vertical"
            
            // Parse content padding
            val contentPadding = when {
                json.has("contentPadding") -> {
                    val padding = json.get("contentPadding")
                    when {
                        padding.isJsonArray -> {
                            val array = padding.asJsonArray
                            if (array.size() == 4) {
                                PaddingValues(
                                    top = array[0].asFloat.dp,
                                    end = array[1].asFloat.dp,
                                    bottom = array[2].asFloat.dp,
                                    start = array[3].asFloat.dp
                                )
                            } else {
                                PaddingValues(0.dp)
                            }
                        }
                        padding.isJsonPrimitive && padding.asJsonPrimitive.isNumber -> {
                            PaddingValues(padding.asFloat.dp)
                        }
                        else -> PaddingValues(0.dp)
                    }
                }
                else -> PaddingValues(0.dp)
            }
            
            // Parse spacing (check both itemSpacing and spacing)
            val spacing = (json.get("itemSpacing")?.asFloat ?: json.get("spacing")?.asFloat ?: 0f).dp
            
            // Build modifier
            val modifier = ModifierBuilder.buildModifier(json)
            
            // Get cell height if specified
            val cellHeight = json.get("cellHeight")?.asFloat?.dp
            
            // Get cell template (fallback if no cellClasses)
            val cellTemplate = json.get("cell")?.asJsonObject
            
            // Create the appropriate grid based on scroll direction
            if (scrollDirection == "horizontal") {
                LazyHorizontalGrid(
                    rows = GridCells.Fixed(columns),
                    modifier = modifier,
                    contentPadding = contentPadding,
                    verticalArrangement = Arrangement.spacedBy(spacing),
                    horizontalArrangement = Arrangement.spacedBy(spacing)
                ) {
                    items(items.size) { index ->
                        val item = items[index]
                        
                        // Create item data context
                        val itemData = data.toMutableMap().apply {
                            put("item", item ?: "")
                            put("index", index)
                        }
                        
                        // Cell modifier
                        val cellModifier = Modifier.then(
                            if (cellHeight != null) Modifier.height(cellHeight) else Modifier
                        ).then(
                            if (columns > 1) Modifier.fillMaxWidth() else Modifier
                        )
                        
                        when {
                            // If we have a cell class name, create a custom view for it
                            cellClassName != null -> {
                                // Create a view based on the cell class name
                                // This would ideally create a specific cell view component
                                Card(
                                    modifier = cellModifier.padding(4.dp)
                                ) {
                                    // In a real implementation, this would dynamically create
                                    // the appropriate cell view based on cellClassName
                                    Box(modifier = Modifier.padding(8.dp)) {
                                        when (item) {
                                            is Map<*, *> -> {
                                                // If item is a map, try to display it intelligently
                                                val displayText = item["title"]?.toString() 
                                                    ?: item["text"]?.toString()
                                                    ?: item["name"]?.toString()
                                                    ?: item.toString()
                                                Text(text = displayText)
                                            }
                                            else -> Text(text = item?.toString() ?: "")
                                        }
                                    }
                                }
                            }
                            // If we have a cell template, use it
                            cellTemplate != null -> {
                                Box(modifier = cellModifier) {
                                    DynamicView(
                                        json = cellTemplate,
                                        data = itemData
                                    )
                                }
                            }
                            // Default cell
                            else -> {
                                Card(
                                    modifier = cellModifier.padding(4.dp)
                                ) {
                                    Text(
                                        text = item?.toString() ?: "",
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Vertical grid (default)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    modifier = modifier,
                    contentPadding = contentPadding,
                    verticalArrangement = Arrangement.spacedBy(spacing),
                    horizontalArrangement = Arrangement.spacedBy(spacing)
                ) {
                    items(items.size) { index ->
                        val item = items[index]
                        
                        // Create item data context
                        val itemData = data.toMutableMap().apply {
                            put("item", item ?: "")
                            put("index", index)
                        }
                        
                        // Cell modifier
                        val cellModifier = Modifier.then(
                            if (cellHeight != null) Modifier.height(cellHeight) else Modifier
                        ).then(
                            if (columns > 1) Modifier.fillMaxWidth() else Modifier
                        )
                        
                        when {
                            // If we have a cell class name, create a custom view for it
                            cellClassName != null -> {
                                // Create a view based on the cell class name
                                // This would ideally create a specific cell view component
                                Card(
                                    modifier = cellModifier.padding(4.dp)
                                ) {
                                    // In a real implementation, this would dynamically create
                                    // the appropriate cell view based on cellClassName
                                    Box(modifier = Modifier.padding(8.dp)) {
                                        when (item) {
                                            is Map<*, *> -> {
                                                // If item is a map, try to display it intelligently
                                                val displayText = item["title"]?.toString() 
                                                    ?: item["text"]?.toString()
                                                    ?: item["name"]?.toString()
                                                    ?: item.toString()
                                                Text(text = displayText)
                                            }
                                            else -> Text(text = item?.toString() ?: "")
                                        }
                                    }
                                }
                            }
                            // If we have a cell template, use it
                            cellTemplate != null -> {
                                Box(modifier = cellModifier) {
                                    DynamicView(
                                        json = cellTemplate,
                                        data = itemData
                                    )
                                }
                            }
                            // Default cell
                            else -> {
                                Card(
                                    modifier = cellModifier.padding(4.dp)
                                ) {
                                    Text(
                                        text = item?.toString() ?: "",
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        
        private fun extractStringArray(jsonArray: JsonArray?): List<String> {
            if (jsonArray == null) return emptyList()
            
            return jsonArray.mapNotNull { element ->
                when {
                    element.isJsonPrimitive && element.asJsonPrimitive.isString -> {
                        element.asString
                    }
                    else -> null
                }
            }
        }
    }
    
    /**
     * Interface for collection data sources that support getCellData
     * This would be implemented by ViewModels or data providers
     */
    interface CollectionDataSource {
        fun getCellData(className: String): List<Any>
    }
}