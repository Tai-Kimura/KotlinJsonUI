package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.gson.JsonObject
import com.google.gson.JsonArray
import com.kotlinjsonui.dynamic.DynamicView
import com.kotlinjsonui.dynamic.DynamicLayoutLoader
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.data.CollectionDataSource
import com.kotlinjsonui.data.CollectionDataSection

/**
 * Dynamic Collection Component Converter
 * Converts JSON to LazyGrid composable at runtime for grid layouts
 * 
 * Supported JSON attributes (matching SwiftUI implementation):
 * - sections: Array of section definitions with cell types and columns
 * - cellClasses: String array of cell class names (legacy)
 * - headerClasses: String array of header class names
 * - footerClasses: String array of footer class names
 * - items: @{variable} for data source
 * - columns: Number of columns (default: 2)
 * - layout: "vertical" | "horizontal"
 * - scrollDirection: "vertical" | "horizontal" (deprecated, use layout)
 * - contentPadding: Number or Array for grid content padding
 * - itemSpacing/spacing: Number for uniform spacing between items
 * - lineSpacing: Number for vertical spacing between rows (minimumLineSpacing in iOS)
 * - columnSpacing: Number for horizontal spacing between columns (minimumInteritemSpacing in iOS)
 * - cellHeight: Number for fixed cell height
 * - cellWidth: Number for fixed cell width (horizontal layout)
 * - cell: JsonObject template for custom cell layout (fallback)
 * - showsVerticalScrollIndicator: Boolean
 * - showsHorizontalScrollIndicator: Boolean
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
            // Check if sections are defined
            val sections = json.get("sections")?.asJsonArray
            // Support both 'layout' and 'orientation' attributes for horizontal/vertical
            val layout = json.get("layout")?.asString
                ?: json.get("orientation")?.asString
                ?: "vertical"
            val isHorizontal = layout == "horizontal"
            
            // Legacy: Extract cellClasses, headerClasses, footerClasses
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
                else -> null
            }
            
            // Get collection data source if sections are defined
            val collectionDataSource = if (sections != null && itemsBinding != null) {
                data[itemsBinding] as? CollectionDataSource
            } else null
            
            // Parse grid configuration with default columns
            val defaultColumns = json.get("columns")?.asInt ?: 1
            
            // Calculate actual grid columns based on sections
            val gridColumns = if (sections != null) {
                // Collect all unique column counts from sections
                val sectionColumns = sections.map { sectionJson ->
                    sectionJson.asJsonObject.get("columns")?.asInt ?: defaultColumns
                }.distinct()
                
                // If sections have different column counts, calculate LCM
                if (sectionColumns.size > 1) {
                    calculateLCM(sectionColumns)
                } else {
                    sectionColumns.firstOrNull() ?: defaultColumns
                }
            } else {
                defaultColumns
            }
            
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
            // lineSpacing: vertical spacing between rows (minimumLineSpacing in iOS)
            // columnSpacing: horizontal spacing between columns (minimumInteritemSpacing in iOS)
            // itemSpacing/spacing: uniform spacing (fallback)
            val defaultSpacing = json.get("itemSpacing")?.asFloat ?: json.get("spacing")?.asFloat ?: 0f
            val lineSpacing = (json.get("lineSpacing")?.asFloat ?: defaultSpacing).dp
            val columnSpacing = (json.get("columnSpacing")?.asFloat ?: defaultSpacing).dp
            
            // Build modifier
            val modifier = ModifierBuilder.buildModifier(json)
            
            // Get cell height/width if specified
            val cellHeight = json.get("cellHeight")?.asFloat?.dp
            val cellWidth = json.get("cellWidth")?.asFloat?.dp
            
            // Get cell template (fallback if no cellClasses)
            val cellTemplate = json.get("cell")?.asJsonObject
            
            // Create the appropriate grid based on layout
            if (isHorizontal) {
                LazyHorizontalGrid(
                    rows = GridCells.Fixed(gridColumns),
                    modifier = modifier,
                    contentPadding = contentPadding,
                    verticalArrangement = Arrangement.spacedBy(lineSpacing),
                    horizontalArrangement = Arrangement.spacedBy(columnSpacing)
                ) {
                    generateCollectionItems(
                        sections = sections,
                        collectionDataSource = collectionDataSource,
                        cellClassName = cellClassName,
                        cellTemplate = cellTemplate,
                        data = data,
                        cellWidth = cellWidth,
                        cellHeight = cellHeight,
                        isHorizontal = true,
                        gridColumns = gridColumns,
                        defaultColumns = defaultColumns
                    )
                }
            } else {
                // Vertical grid (default)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(gridColumns),
                    modifier = modifier,
                    contentPadding = contentPadding,
                    verticalArrangement = Arrangement.spacedBy(lineSpacing),
                    horizontalArrangement = Arrangement.spacedBy(columnSpacing)
                ) {
                    generateCollectionItems(
                        sections = sections,
                        collectionDataSource = collectionDataSource,
                        cellClassName = cellClassName,
                        cellTemplate = cellTemplate,
                        data = data,
                        cellWidth = cellWidth,
                        cellHeight = cellHeight,
                        isHorizontal = false,
                        gridColumns = gridColumns,
                        defaultColumns = defaultColumns
                    )
                }
            }
        }
        
        private fun LazyGridScope.generateCollectionItems(
            sections: JsonArray?,
            collectionDataSource: CollectionDataSource?,
            cellClassName: String?,
            cellTemplate: JsonObject?,
            data: Map<String, Any>,
            cellWidth: androidx.compose.ui.unit.Dp?,
            cellHeight: androidx.compose.ui.unit.Dp?,
            isHorizontal: Boolean,
            gridColumns: Int,
            defaultColumns: Int
        ) {
            when {
                // If we have sections and data source
                sections != null && collectionDataSource != null -> {
                    sections.forEach { sectionJson ->
                        val sectionObj = sectionJson.asJsonObject
                        val cellViewName = sectionObj.get("cell")?.asString
                        val headerViewName = sectionObj.get("header")?.asString
                        val footerViewName = sectionObj.get("footer")?.asString
                        val sectionColumns = sectionObj.get("columns")?.asInt ?: defaultColumns
                        val sectionIndex = sections.indexOf(sectionJson)
                        
                        // Calculate span for items in this section
                        val itemSpan = gridColumns / sectionColumns
                        
                        collectionDataSource.sections.getOrNull(sectionIndex)?.let { section ->
                            // Render header if present
                            if (headerViewName != null && section.header != null) {
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    Box(modifier = Modifier.fillMaxWidth()) {
                                        val headerData = (section.header as? CollectionDataSection.HeaderFooterData)?.data ?: emptyMap()
                                        renderCellView(headerViewName, headerData, -1, data)
                                    }
                                }
                            }
                            
                            // Render cells
                            section.cells?.let { cellData ->
                                items(
                                    count = cellData.data.size,
                                    span = if (itemSpan > 1) { { GridItemSpan(itemSpan) } } else null
                                ) { cellIndex ->
                                    val item = cellData.data[cellIndex]
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .then(
                                                if (isHorizontal && cellWidth != null) Modifier.width(cellWidth)
                                                else if (!isHorizontal && cellHeight != null) Modifier.height(cellHeight)
                                                else Modifier
                                            )
                                    ) {
                                        renderCellView(cellViewName ?: cellClassName, item, cellIndex, data)
                                    }
                                }
                            }
                            
                            // Render footer if present
                            if (footerViewName != null && section.footer != null) {
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    Box(modifier = Modifier.fillMaxWidth()) {
                                        val footerData = (section.footer as? CollectionDataSection.HeaderFooterData)?.data ?: emptyMap()
                                        renderCellView(footerViewName, footerData, -2, data)
                                    }
                                }
                            }
                        }
                    }
                }
                // If we have cell template
                cellTemplate != null -> {
                    // Use a default list of 10 items for template
                    items(10) { index ->
                        Box(
                            modifier = Modifier.then(
                                if (isHorizontal && cellWidth != null) Modifier.width(cellWidth)
                                else if (!isHorizontal && cellHeight != null) Modifier.height(cellHeight)
                                else Modifier
                            )
                        ) {
                            val cellData = data.toMutableMap().apply {
                                put("index", index)
                            }
                            DynamicView(
                                json = cellTemplate,
                                data = cellData
                            )
                        }
                    }
                }
                // Default placeholder
                else -> {
                    items(10) { index ->
                        Card(
                            modifier = Modifier
                                .padding(4.dp)
                                .then(
                                    if (isHorizontal && cellWidth != null) Modifier.width(cellWidth)
                                    else if (!isHorizontal && cellHeight != null) Modifier.height(cellHeight)
                                    else Modifier.fillMaxWidth().height(80.dp)
                                )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = androidx.compose.ui.Alignment.Center
                            ) {
                                Text("Item $index")
                            }
                        }
                    }
                }
            }
        }
        
        private fun calculateLCM(numbers: List<Int>): Int {
            fun gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)
            fun lcm(a: Int, b: Int): Int = (a * b) / gcd(a, b)
            
            return numbers.reduce { acc, n -> lcm(acc, n) }
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
        
        @Composable
        private fun renderCellView(
            cellClassName: String?,
            item: Any?,
            index: Int,
            data: Map<String, Any>
        ) {
            if (cellClassName == null) {
                // Default cell
                Card(
                    modifier = Modifier.padding(4.dp).fillMaxWidth()
                ) {
                    Text(
                        text = item?.toString() ?: "",
                        modifier = Modifier.padding(16.dp)
                    )
                }
                return
            }
            
            // Convert cell class name to JSON file name
            val cellJsonName = cellClassName
                .replace(Regex("([a-z])([A-Z])")) { "${it.groupValues[1]}_${it.groupValues[2].lowercase()}" }
                .lowercase()
            
            // Initialize DynamicLayoutLoader with context
            val context = LocalContext.current
            DynamicLayoutLoader.init(context)
            
            // Load the cell JSON from assets
            val cellJson = DynamicLayoutLoader.loadLayout(cellJsonName)
            
            if (cellJson != null) {
                // Create item data context for the cell
                val cellData = when (item) {
                    is Map<*, *> -> {
                        // If item is already a map, merge it with data
                        data.toMutableMap().apply {
                            (item as Map<String, Any>).forEach { (key, value) ->
                                put(key, value)
                            }
                            put("index", index)
                        }
                    }
                    else -> {
                        // Otherwise, put item as "item" field
                        data.toMutableMap().apply {
                            put("item", item ?: "")
                            put("index", index)
                        }
                    }
                }
                
                // Render the cell view with item data
                DynamicView(
                    json = cellJson,
                    data = cellData
                )
            } else {
                // Fallback - display error
                Card(
                    modifier = Modifier.padding(4.dp).fillMaxWidth()
                ) {
                    Text(
                        text = "Cell not found: $cellJsonName",
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}