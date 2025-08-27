package com.example.kotlinjsonui.sample.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.kotlinjsonui.sample.data.CollectionTestData
import com.example.kotlinjsonui.sample.data.ProductCellData
import com.example.kotlinjsonui.sample.data.SimpleCellData
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.data.CollectionDataSource
import com.kotlinjsonui.data.CollectionDataSection

class CollectionTestViewModel(application: Application) : AndroidViewModel(application) {
    // JSON file reference for hot reload
    val jsonFileName = "collection_test"
    
    // Data model
    private val _data = MutableStateFlow(CollectionTestData())
    val data: StateFlow<CollectionTestData> = _data.asStateFlow()
    
    // Sample data for cells
    private val basicItems = listOf(
        mapOf("title" to "Item 1", "subtitle" to "Description for item 1"),
        mapOf("title" to "Item 2", "subtitle" to "Description for item 2"),
        mapOf("title" to "Item 3", "subtitle" to "Description for item 3"),
        mapOf("title" to "Item 4", "subtitle" to "Description for item 4"),
        mapOf("title" to "Item 5", "subtitle" to "Description for item 5"),
        mapOf("title" to "Item 6", "subtitle" to "Description for item 6")
    )
    
    private val imageItems = listOf(
        mapOf(
            "imageUrl" to "https://picsum.photos/200/150?random=1",
            "title" to "Beautiful Sunset",
            "price" to "$19.99"
        ),
        mapOf(
            "imageUrl" to "https://picsum.photos/200/150?random=2",
            "title" to "Mountain View",
            "price" to "$24.99"
        ),
        mapOf(
            "imageUrl" to "https://picsum.photos/200/150?random=3",
            "title" to "Ocean Waves",
            "price" to "$29.99"
        ),
        mapOf(
            "imageUrl" to "https://picsum.photos/200/150?random=4",
            "title" to "Forest Path",
            "price" to "$34.99"
        ),
        mapOf(
            "imageUrl" to "https://picsum.photos/200/150?random=5",
            "title" to "City Lights",
            "price" to "$39.99"
        ),
        mapOf(
            "imageUrl" to "https://picsum.photos/200/150?random=6",
            "title" to "Desert Dunes",
            "price" to "$44.99"
        )
    )
    
    private val horizontalCards = listOf(
        mapOf("title" to "Card A", "description" to "Quick description A"),
        mapOf("title" to "Card B", "description" to "Quick description B"),
        mapOf("title" to "Card C", "description" to "Quick description C"),
        mapOf("title" to "Card D", "description" to "Quick description D"),
        mapOf("title" to "Card E", "description" to "Quick description E")
    )
    
    private val productItems = listOf(
        mapOf(
            "name" to "iPhone 15 Pro",
            "price" to "$999",
            "stock" to "In Stock (15)",
            "inStock" to true
        ),
        mapOf(
            "name" to "MacBook Air",
            "price" to "$1299",
            "stock" to "In Stock (8)",
            "inStock" to true
        ),
        mapOf(
            "name" to "AirPods Pro",
            "price" to "$249",
            "stock" to "Out of Stock",
            "inStock" to false
        ),
        mapOf(
            "name" to "iPad Air",
            "price" to "$599",
            "stock" to "In Stock (12)",
            "inStock" to true
        )
    )
    
    private val featureItems = listOf(
        mapOf(
            "badge" to "NEW",
            "title" to "Premium Feature",
            "description" to "Unlock amazing capabilities"
        ),
        mapOf(
            "badge" to "HOT",
            "title" to "Popular Choice",
            "description" to "Most loved by users"
        ),
        mapOf(
            "badge" to "SALE",
            "title" to "Limited Offer",
            "description" to "Get it while it lasts"
        ),
        mapOf(
            "badge" to "PRO",
            "title" to "Professional",
            "description" to "For power users"
        )
    )
    
    // Toggle dynamic mode
    fun toggleDynamicMode() {
        val newState = DynamicModeManager.toggleDynamicMode(getApplication())
        // Trigger recomposition
        _data.value = _data.value.copy(dynamicModeEnabled = if (newState == null) false else newState)
    }

    // Update data
    fun updateData(updates: Map<String, Any>) {
        // Handle specific updates if needed
        _data.value = _data.value.copy()
    }
    
    init {
        // items1: Basic Collection with Headers & Footers
        val items1 = CollectionDataSource(
            sections = listOf(
                CollectionDataSection(
                    header = CollectionDataSection.HeaderFooterData(
                        viewName = "SectionHeader",
                        data = mapOf("title" to "Section 1 Header")
                    ),
                    cells = CollectionDataSection.CellData(
                        viewName = "BasicCell",
                        data = basicItems
                    ),
                    footer = CollectionDataSection.HeaderFooterData(
                        viewName = "SectionFooter",
                        data = mapOf("text" to "End of Section 1")
                    )
                )
            )
        )
        
        // mixedItems: Grid Collection with Image cells
        val mixedItems = CollectionDataSource(
            sections = listOf(
                CollectionDataSection(
                    header = CollectionDataSection.HeaderFooterData(
                        viewName = "GridHeader",
                        data = mapOf("title" to "Image Gallery")
                    ),
                    cells = CollectionDataSection.CellData(
                        viewName = "ImageCell",
                        data = imageItems
                    ),
                    footer = CollectionDataSection.HeaderFooterData(
                        viewName = "GridFooter",
                        data = mapOf("title" to "Total: ${imageItems.size} items")
                    )
                )
            )
        )
        
        // horizontalItems: Horizontal scrolling cards
        val horizontalItems = CollectionDataSource(
            sections = listOf(
                CollectionDataSection(
                    header = CollectionDataSection.HeaderFooterData(
                        viewName = "HorizontalHeader",
                        data = mapOf("title" to "Horizontal Scroll")
                    ),
                    cells = CollectionDataSection.CellData(
                        viewName = "HorizontalCard",
                        data = horizontalCards
                    )
                )
            )
        )
        
        // sectionedItems: Products with category headers
        val sectionedItems = CollectionDataSource(
            sections = listOf(
                CollectionDataSection(
                    header = CollectionDataSection.HeaderFooterData(
                        viewName = "CategoryHeader",
                        data = mapOf("title" to "Electronics")
                    ),
                    cells = CollectionDataSection.CellData(
                        viewName = "ProductCell",
                        data = productItems
                    ),
                    footer = CollectionDataSection.HeaderFooterData(
                        viewName = "CategoryFooter",
                        data = mapOf("title" to "More in Electronics →")
                    )
                )
            )
        )
        
        // multiSectionItems: Multiple sections with different cell types
        val multiSectionItems = CollectionDataSource(
            sections = listOf(
                // Section 1: Products (3 columns)
                CollectionDataSection(
                    header = CollectionDataSection.HeaderFooterData(
                        viewName = "CategoryHeader",
                        data = mapOf("title" to "Popular Products")
                    ),
                    cells = CollectionDataSection.CellData(
                        viewName = "ProductCell",
                        data = productItems
                    ),
                    footer = CollectionDataSection.HeaderFooterData(
                        viewName = "CategoryFooter",
                        data = mapOf("title" to "View all products")
                    )
                ),
                // Section 2: Feature cells (2 columns)
                CollectionDataSection(
                    header = CollectionDataSection.HeaderFooterData(
                        viewName = "FeaturedHeader",
                        data = mapOf("title" to "Featured")
                    ),
                    cells = CollectionDataSection.CellData(
                        viewName = "FeatureCell",
                        data = featureItems
                    )
                ),
                // Section 3: Image gallery (4 columns)
                CollectionDataSection(
                    header = CollectionDataSection.HeaderFooterData(
                        viewName = "GridHeader",
                        data = mapOf("title" to "Gallery")
                    ),
                    cells = CollectionDataSection.CellData(
                        viewName = "ImageCell",
                        data = imageItems.take(8) // Show 8 items in grid
                    ),
                    footer = CollectionDataSection.HeaderFooterData(
                        viewName = "GridFooter",
                        data = mapOf("title" to "End of gallery")
                    )
                )
            )
        )
        
        // Update the data with all collections
        _data.value = _data.value.copy(
            items1 = items1,
            mixedItems = mixedItems,
            horizontalItems = horizontalItems,
            sectionedItems = sectionedItems,
            multiSectionItems = multiSectionItems
        )
    }
}