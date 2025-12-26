package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.DynamicLayoutLoader
import com.kotlinjsonui.dynamic.DynamicView
import com.kotlinjsonui.dynamic.helpers.ColorParser

/**
 * Dynamic TabView Component Converter
 * Converts JSON to NavigationBar with content switching composable at runtime
 *
 * Supported JSON attributes (matching attribute_definitions.json):
 * - tabs: Array of tab items (required)
 *   - title: String tab title
 *   - icon: String icon name (SF Symbol for iOS, Material icon for Android)
 *   - selectedIcon: String icon for selected state (optional)
 *   - badge: Int/String badge value (supports binding)
 *   - view: String layout file name to display
 * - selectedIndex: Integer or @{binding} for selected tab
 * - tintColor: String hex color for selected tab
 * - unselectedColor: String hex color for unselected tabs
 * - tabBarBackground: String hex color for tab bar background
 * - showLabels: Boolean whether to show tab labels (default: true)
 * - onTabChange: @{callback} for tab change event
 */
class DynamicTabViewComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            // Parse tabs array
            val tabsArray = json.get("tabs")?.asJsonArray ?: return

            // Parse binding variable for selected index
            val bindingVariable = json.get("selectedIndex")?.asString?.let { value ->
                if (value.contains("@{")) {
                    val pattern = "@\\{([^}]+)\\}".toRegex()
                    pattern.find(value)?.groupValues?.get(1)
                } else null
            }

            // Parse onTabChange callback
            val onTabChangeProperty = json.get("onTabChange")?.asString?.let { value ->
                if (value.contains("@{")) {
                    val pattern = "@\\{([^}]+)\\}".toRegex()
                    pattern.find(value)?.groupValues?.get(1)
                } else null
            }

            @Suppress("UNCHECKED_CAST")
            val onTabChangeCallback = onTabChangeProperty?.let {
                data[it] as? ((Int) -> Unit)
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

            // Parse colors
            val tintColor = ColorParser.parseColor(json, "tintColor")
            val unselectedColor = ColorParser.parseColor(json, "unselectedColor")
            val tabBarBackground = ColorParser.parseColor(json, "tabBarBackground")
            val showLabels = json.get("showLabels")?.asBoolean ?: true

            // Build tab items data
            val tabItems = tabsArray.mapIndexed { index, item ->
                val itemObj = item.asJsonObject
                TabItemData(
                    index = index,
                    title = itemObj.get("title")?.asString ?: "Tab ${index + 1}",
                    icon = itemObj.get("icon")?.asString ?: "circle",
                    selectedIcon = itemObj.get("selectedIcon")?.asString,
                    badge = itemObj.get("badge"),
                    view = itemObj.get("view")?.asString
                )
            }

            // Create TabView using Scaffold with NavigationBar
            Scaffold(
                bottomBar = {
                    NavigationBar(
                        containerColor = tabBarBackground ?: NavigationBarDefaults.containerColor
                    ) {
                        tabItems.forEach { tabItem ->
                            val isSelected = selectedTab == tabItem.index
                            val icon = if (isSelected) {
                                tabItem.selectedIcon ?: tabItem.icon
                            } else {
                                tabItem.icon
                            }

                            NavigationBarItem(
                                selected = isSelected,
                                onClick = {
                                    if (selectedTab != tabItem.index) {
                                        selectedTab = tabItem.index
                                        onTabChangeCallback?.invoke(tabItem.index)

                                        // Update bound variable
                                        if (bindingVariable != null) {
                                            val updateData = data["updateData"]
                                            if (updateData is Function<*>) {
                                                try {
                                                    @Suppress("UNCHECKED_CAST")
                                                    (updateData as (Map<String, Any>) -> Unit)(
                                                        mapOf(bindingVariable to tabItem.index)
                                                    )
                                                } catch (e: Exception) {
                                                    // Update function doesn't match expected signature
                                                }
                                            }
                                        }
                                    }
                                },
                                icon = {
                                    val badgeValue = getBadgeValue(tabItem.badge, data)
                                    if (badgeValue != null) {
                                        BadgedBox(badge = { Badge { Text(badgeValue) } }) {
                                            Icon(
                                                imageVector = getIconVector(icon, isSelected),
                                                contentDescription = tabItem.title
                                            )
                                        }
                                    } else {
                                        Icon(
                                            imageVector = getIconVector(icon, isSelected),
                                            contentDescription = tabItem.title
                                        )
                                    }
                                },
                                label = if (showLabels) {
                                    { Text(tabItem.title) }
                                } else null,
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = tintColor ?: MaterialTheme.colorScheme.primary,
                                    selectedTextColor = tintColor ?: MaterialTheme.colorScheme.primary,
                                    unselectedIconColor = unselectedColor ?: MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = unselectedColor ?: MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            ) { innerPadding ->
                // Tab content
                Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                    if (selectedTab < tabItems.size) {
                        val selectedItem = tabItems[selectedTab]
                        if (selectedItem.view != null) {
                            // Load and display the referenced layout
                            val layoutJson = DynamicLayoutLoader.loadLayout(selectedItem.view)
                            if (layoutJson != null) {
                                DynamicView(layoutJson, data)
                            }
                        } else {
                            // Default placeholder when no view specified
                            Text(
                                text = selectedItem.title,
                                modifier = Modifier.fillMaxSize(),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }

        private data class TabItemData(
            val index: Int,
            val title: String,
            val icon: String,
            val selectedIcon: String?,
            val badge: com.google.gson.JsonElement?,
            val view: String?
        )

        private fun getBadgeValue(badge: com.google.gson.JsonElement?, data: Map<String, Any>): String? {
            if (badge == null || badge.isJsonNull) return null

            return when {
                badge.isJsonPrimitive -> {
                    val primitive = badge.asJsonPrimitive
                    when {
                        primitive.isNumber -> {
                            val num = primitive.asInt
                            if (num > 0) num.toString() else null
                        }
                        primitive.isString -> {
                            val str = primitive.asString
                            if (str.startsWith("@{") && str.endsWith("}")) {
                                val bindingProp = str.drop(2).dropLast(1)
                                when (val value = data[bindingProp]) {
                                    is Number -> if (value.toInt() > 0) value.toString() else null
                                    is String -> value.ifEmpty { null }
                                    else -> null
                                }
                            } else {
                                str.ifEmpty { null }
                            }
                        }
                        else -> null
                    }
                }
                else -> null
            }
        }

        private fun getIconVector(iconName: String, filled: Boolean): ImageVector {
            // Map SF Symbol / common names to Material Icons
            val iconMap = mapOf(
                "house" to (Icons.Filled.Home to Icons.Outlined.Home),
                "house.fill" to (Icons.Filled.Home to Icons.Outlined.Home),
                "person" to (Icons.Filled.Person to Icons.Outlined.Person),
                "person.fill" to (Icons.Filled.Person to Icons.Outlined.Person),
                "gearshape" to (Icons.Filled.Settings to Icons.Outlined.Settings),
                "gearshape.fill" to (Icons.Filled.Settings to Icons.Outlined.Settings),
                "gear" to (Icons.Filled.Settings to Icons.Outlined.Settings),
                "magnifyingglass" to (Icons.Filled.Search to Icons.Outlined.Search),
                "heart" to (Icons.Filled.Favorite to Icons.Outlined.FavoriteBorder),
                "heart.fill" to (Icons.Filled.Favorite to Icons.Outlined.FavoriteBorder),
                "star" to (Icons.Filled.Star to Icons.Outlined.Star),
                "star.fill" to (Icons.Filled.Star to Icons.Outlined.Star),
                "bell" to (Icons.Filled.Notifications to Icons.Outlined.Notifications),
                "bell.fill" to (Icons.Filled.Notifications to Icons.Outlined.Notifications),
                "cart" to (Icons.Filled.ShoppingCart to Icons.Outlined.ShoppingCart),
                "cart.fill" to (Icons.Filled.ShoppingCart to Icons.Outlined.ShoppingCart),
                "list.bullet" to (Icons.Filled.List to Icons.Outlined.List),
                "circle" to (Icons.Filled.Circle to Icons.Outlined.Circle)
            )

            val icons = iconMap[iconName] ?: (Icons.Filled.Circle to Icons.Outlined.Circle)
            return if (filled) icons.first else icons.second
        }
    }
}
