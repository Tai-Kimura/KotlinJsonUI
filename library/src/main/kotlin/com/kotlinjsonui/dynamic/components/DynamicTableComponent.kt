package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.DynamicView
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder

/**
 * Dynamic Table Component Converter
 * Converts JSON to LazyColumn table composable at runtime
 * 
 * Supported JSON attributes (matching Ruby implementation):
 * - bind/items: @{variable} for data source
 * - header: Array of column headers
 * - cell: JsonObject template for custom row layout
 * - rowHeight: Number for row height (default: 60)
 * - rowSpacing/spacing: Number for spacing between rows
 * - contentPadding: Number or Array for table content padding
 * - separatorStyle: "single" | "none" for row separators
 * - separatorInset: Object with left/start padding for separator
 * - width/height: Number dimensions
 * - padding/paddings: Number or Array for padding
 * - margins: Array or individual margin properties
 * - background: Background color
 */
class DynamicTableComponent {
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
            
            // Parse table configuration
            val rowHeight = json.get("rowHeight")?.asFloat?.dp ?: 60.dp
            val rowSpacing = (json.get("rowSpacing") ?: json.get("spacing"))?.asFloat?.dp ?: 0.dp
            val separatorStyle = json.get("separatorStyle")?.asString ?: "single"
            
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
            
            // Parse separator inset
            val separatorInset = json.get("separatorInset")?.asJsonObject
            val separatorStartPadding = when {
                separatorInset != null -> {
                    (separatorInset.get("left") ?: separatorInset.get("start"))?.asFloat?.dp ?: 0.dp
                }
                else -> 0.dp
            }
            
            // Parse header
            val headerElement = json.get("header")
            val headers = when {
                headerElement?.isJsonArray == true -> {
                    headerElement.asJsonArray.map { it.asString }
                }
                else -> null
            }
            
            // Get cell template
            val cellTemplate = json.get("cell")?.asJsonObject
            
            // Build modifier
            val modifier = ModifierBuilder.buildModifier(json)
            
            // Create the table
            LazyColumn(
                modifier = modifier,
                contentPadding = contentPadding,
                verticalArrangement = Arrangement.spacedBy(rowSpacing)
            ) {
                // Header row
                if (headers != null) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            headers.forEach { header ->
                                Text(
                                    text = header,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        
                        // Header separator
                        if (separatorStyle != "none") {
                            Divider(
                                color = Color.LightGray,
                                thickness = 1.dp
                            )
                        }
                    }
                }
                
                // Table rows
                items(items) { item ->
                    // Create item data context
                    val itemData = data.toMutableMap().apply {
                        put("item", item ?: "")
                    }
                    
                    if (cellTemplate != null) {
                        // Custom cell template
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { 
                                    // Handle row click - could trigger onRowClick event
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            DynamicView(
                                json = cellTemplate,
                                data = itemData
                            )
                        }
                    } else {
                        // Default row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(rowHeight)
                                .clickable { 
                                    // Handle row click
                                }
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Parse item based on type
                            when (item) {
                                is Map<*, *> -> {
                                    // If item is a map, display columns
                                    if (headers != null) {
                                        headers.forEach { header ->
                                            Text(
                                                text = item[header]?.toString() ?: "",
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    } else {
                                        Text(text = item.toString())
                                    }
                                }
                                is List<*> -> {
                                    // If item is a list, display as columns
                                    item.forEach { col ->
                                        Text(
                                            text = col?.toString() ?: "",
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                                else -> {
                                    // Default: display as single text
                                    Text(text = item.toString())
                                }
                            }
                        }
                    }
                    
                    // Row separator
                    if (separatorStyle != "none") {
                        Divider(
                            modifier = if (separatorStartPadding > 0.dp) {
                                Modifier.padding(start = separatorStartPadding)
                            } else {
                                Modifier
                            },
                            color = Color.LightGray,
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }
    }
}