package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.DynamicView
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ColorParser

/**
 * Dynamic TabView Component Converter
 * Converts JSON to TabRow with content switching composable at runtime
 * 
 * Supported JSON attributes (matching Ruby implementation):
 * - items: Array of tab items with title and child properties
 * - selectedIndex/bind: Integer or @{variable} for selected tab (optional)
 * - backgroundColor: String hex color for tab row background
 * - selectedColor: String hex color for selected tab
 * - normalColor: String hex color for unselected tabs
 * - indicatorColor: String hex color for tab indicator
 * - width/height: Number dimensions
 * - padding/paddings: Number or Array for padding
 * - margins: Array or individual margin properties
 * 
 * Each item in items array can have:
 * - title: String tab title
 * - child: JsonObject for tab content
 */
class DynamicTabViewComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            // Parse binding variable for selected index
            val bindingVariable = when {
                json.get("selectedIndex")?.asString?.contains("@{") == true -> {
                    val pattern = "@\\{([^}]+)\\}".toRegex()
                    pattern.find(json.get("selectedIndex").asString)?.groupValues?.get(1)
                }
                json.get("bind")?.asString?.contains("@{") == true -> {
                    val pattern = "@\\{([^}]+)\\}".toRegex()
                    pattern.find(json.get("bind").asString)?.groupValues?.get(1)
                }
                else -> null
            }
            
            // Get initial selected index
            val initialIndex = when {
                bindingVariable != null -> {
                    when (val boundValue = data[bindingVariable]) {
                        is Number -> boundValue.toInt()
                        is String -> boundValue.toIntOrNull() ?: 0
                        else -> 0
                    }
                }
                json.get("selectedIndex")?.isJsonPrimitive == true -> {
                    val indexElement = json.get("selectedIndex")
                    when {
                        indexElement.asJsonPrimitive.isNumber -> indexElement.asInt
                        else -> 0
                    }
                }
                else -> 0
            }
            
            // State for selected tab
            var selectedTab by remember(initialIndex, bindingVariable, data) { 
                mutableStateOf(
                    if (bindingVariable != null) {
                        when (val boundValue = data[bindingVariable]) {
                            is Number -> boundValue.toInt()
                            is String -> boundValue.toIntOrNull() ?: 0
                            else -> 0
                        }
                    } else {
                        initialIndex
                    }
                )
            }
            
            // Update value when data changes
            LaunchedEffect(data, bindingVariable) {
                if (bindingVariable != null) {
                    selectedTab = when (val boundValue = data[bindingVariable]) {
                        is Number -> boundValue.toInt()
                        is String -> boundValue.toIntOrNull() ?: 0
                        else -> 0
                    }
                }
            }
            
            // Parse tab items
            val items = json.get("items")?.asJsonArray ?: return
            
            // Parse colors
            val backgroundColor = ColorParser.parseColor(json, "backgroundColor")
            val selectedColor = ColorParser.parseColor(json, "selectedColor")
            val normalColor = ColorParser.parseColor(json, "normalColor")
            val indicatorColor = ColorParser.parseColor(json, "indicatorColor")
            
            // Build modifier
            val modifier = ModifierBuilder.buildModifier(json)
            
            // Create the TabView
            Column(modifier = modifier) {
                // TabRow for tab selection
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = backgroundColor ?: TabRowDefaults.containerColor,
                    contentColor = normalColor ?: TabRowDefaults.contentColor,
                    indicator = { tabPositions ->
                        if (tabPositions.isNotEmpty() && selectedTab < tabPositions.size) {
                            TabRowDefaults.Indicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = indicatorColor ?: MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                ) {
                    items.forEachIndexed { index, item ->
                        val itemObj = item.asJsonObject
                        val title = itemObj.get("title")?.asString ?: "Tab ${index + 1}"
                        
                        Tab(
                            selected = selectedTab == index,
                            onClick = {
                                selectedTab = index
                                
                                // Update bound variable
                                if (bindingVariable != null) {
                                    val updateData = data["updateData"]
                                    if (updateData is Function<*>) {
                                        try {
                                            @Suppress("UNCHECKED_CAST")
                                            (updateData as (Map<String, Any>) -> Unit)(
                                                mapOf(bindingVariable to index)
                                            )
                                        } catch (e: Exception) {
                                            // Update function doesn't match expected signature
                                        }
                                    }
                                }
                            },
                            text = {
                                Text(
                                    text = title,
                                    color = if (selectedTab == index) {
                                        selectedColor ?: LocalContentColor.current
                                    } else {
                                        normalColor ?: LocalContentColor.current
                                    }
                                )
                            }
                        )
                    }
                }
                
                // Tab content
                if (selectedTab < items.size()) {
                    val selectedItem = items[selectedTab].asJsonObject
                    val childElement = selectedItem.get("child")
                    
                    if (childElement != null && childElement.isJsonObject) {
                        // Render the child content for the selected tab
                        DynamicView(
                            json = childElement.asJsonObject,
                            data = data
                        )
                    } else {
                        // Default content if no child specified
                        val title = selectedItem.get("title")?.asString ?: "Tab ${selectedTab + 1}"
                        Text(
                            text = "Content for $title",
                            modifier = Modifier.fillMaxSize(),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}