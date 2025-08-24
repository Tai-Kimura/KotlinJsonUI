package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.DynamicView
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder

/**
 * Dynamic Collection Component Converter
 * Converts JSON to LazyGrid composable at runtime for grid layouts
 * 
 * Supported JSON attributes (matching Ruby implementation):
 * - bind/items: @{variable} for data source
 * - columns: Number of columns (default: 2)
 * - scrollDirection: "vertical" | "horizontal"
 * - contentPadding: Number or Array for grid content padding
 * - spacing: Number for spacing between items
 * - cell: JsonObject template for custom cell layout
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
            // Parse data binding for items
            val itemsBinding = when {
                json.get("bind")?.asString?.contains("@{") == true -> {
                    val pattern = "@\\{([^}]+)\\}".toRegex()
                    pattern.find(json.get("bind").asString)?.groupValues?.get(1)
                }
                json.get("items")?.asString?.contains("@{") == true -> {
                    val pattern = "@\\{([^}]+)\\}".toRegex()
                    pattern.find(json.get("items").asString)?.groupValues?.get(1)
                }
                else -> null
            }
            
            // Get items from data binding
            val items = when {
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
            
            // Parse spacing
            val spacing = json.get("spacing")?.asFloat?.dp ?: 0.dp
            
            // Build modifier
            val modifier = ModifierBuilder.buildModifier(json)
            
            // Get cell template
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
                        
                        if (cellTemplate != null) {
                            // Render custom cell template
                            DynamicView(
                                json = cellTemplate,
                                data = itemData
                            )
                        } else {
                            // Default cell with card
                            Card(
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Text(
                                    text = item?.toString() ?: "",
                                    modifier = Modifier.padding(16.dp)
                                )
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
                        
                        if (cellTemplate != null) {
                            // Render custom cell template
                            DynamicView(
                                json = cellTemplate,
                                data = itemData
                            )
                        } else {
                            // Default cell with card
                            Card(
                                modifier = Modifier.padding(4.dp)
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
}