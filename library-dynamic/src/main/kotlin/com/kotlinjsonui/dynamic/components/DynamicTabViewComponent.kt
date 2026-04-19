package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.ui.res.painterResource
import com.kotlinjsonui.dynamic.R
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.google.gson.JsonObject
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.dynamic.DynamicLayoutLoader
import com.kotlinjsonui.dynamic.DynamicView
import com.kotlinjsonui.dynamic.LocalSafeAreaConfig
import com.kotlinjsonui.dynamic.SafeAreaConfig
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

            // Parse colors - handle both static values and bindings
            val tintColor = ColorParser.parseColorWithBinding(json, "tintColor", data)
            val unselectedColor = ColorParser.parseColorWithBinding(json, "unselectedColor", data)
            val tabBarBackground = ColorParser.parseColorWithBinding(json, "tabBarBackground", data)
            val showLabels = json.get("showLabels")?.asBoolean ?: true

            // Get TabView id for test automation
            val tabViewId = json.get("id")?.asString

            // Build tab items data
            val tabItems = tabsArray.mapIndexed { index, item ->
                val itemObj = item.asJsonObject
                TabItemData(
                    index = index,
                    title = itemObj.get("title")?.asString ?: "Tab ${index + 1}",
                    icon = itemObj.get("icon")?.asString ?: "circle",
                    selectedIcon = itemObj.get("selectedIcon")?.asString,
                    iconType = itemObj.get("iconType")?.asString ?: "system",
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
                                modifier = if (tabViewId != null) {
                                    Modifier.testTag("${tabViewId}_tab_${tabItem.index}")
                                } else {
                                    Modifier
                                },
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
                                            TabIcon(
                                                iconName = icon,
                                                iconType = tabItem.iconType,
                                                isSelected = isSelected,
                                                contentDescription = tabItem.title
                                            )
                                        }
                                    } else {
                                        TabIcon(
                                            iconName = icon,
                                            iconType = tabItem.iconType,
                                            isSelected = isSelected,
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
                // Tab content - only apply bottom padding for NavigationBar
                // Child views (SafeAreaView) handle their own top safe area
                Box(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()).fillMaxSize()) {
                    // Provide SafeAreaConfig to tell child views to ignore bottom safe area
                    CompositionLocalProvider(
                        LocalSafeAreaConfig provides SafeAreaConfig(ignoreBottom = true)
                    ) {
                        if (selectedTab < tabItems.size) {
                            val selectedItem = tabItems[selectedTab]
                            if (selectedItem.view != null) {
                                // First, check if there's a registered custom component handler
                                val viewJson = JsonObject().apply {
                                    addProperty("type", selectedItem.view)
                                }
                                val handled = Configuration.customComponentHandler?.invoke(
                                    selectedItem.view,
                                    viewJson,
                                    data
                                ) ?: false

                                if (!handled) {
                                    // Fallback: Load and display the referenced layout from JSON
                                    val layoutJson = DynamicLayoutLoader.loadLayout(selectedItem.view)
                                    if (layoutJson != null) {
                                        DynamicView(layoutJson, data)
                                    } else {
                                        // Layout not found - show error message
                                        Text(
                                            text = "Layout not found: ${selectedItem.view}",
                                            modifier = Modifier.fillMaxSize(),
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
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
        }

        private data class TabItemData(
            val index: Int,
            val title: String,
            val icon: String,
            val selectedIcon: String?,
            val iconType: String,
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

        @Composable
        private fun TabIcon(
            iconName: String,
            iconType: String,
            isSelected: Boolean,
            contentDescription: String
        ) {
            if (iconType == "resource") {
                // Use drawable resource
                val context = LocalContext.current
                val drawableId = context.resources.getIdentifier(
                    iconName,
                    "drawable",
                    context.packageName
                )
                if (drawableId != 0) {
                    Icon(
                        painter = painterResource(id = drawableId),
                        contentDescription = contentDescription
                    )
                } else {
                    // Fallback to bundled icon resource
                    Icon(
                        painter = painterResource(getIconResId(iconName, isSelected)),
                        contentDescription = contentDescription
                    )
                }
            } else {
                // Use bundled icon resources
                Icon(
                    painter = painterResource(getIconResId(iconName, isSelected)),
                    contentDescription = contentDescription
                )
            }
        }

        private fun getIconResId(iconName: String, filled: Boolean): Int {
            // Map SF Symbol / common names to drawable resources
            val iconMap = mapOf(
                "house" to (R.drawable.ic_home_filled to R.drawable.ic_home_outlined),
                "house.fill" to (R.drawable.ic_home_filled to R.drawable.ic_home_outlined),
                "person" to (R.drawable.ic_person_filled to R.drawable.ic_person_outlined),
                "person.fill" to (R.drawable.ic_person_filled to R.drawable.ic_person_outlined),
                "gearshape" to (R.drawable.ic_settings_filled to R.drawable.ic_settings_outlined),
                "gearshape.fill" to (R.drawable.ic_settings_filled to R.drawable.ic_settings_outlined),
                "gear" to (R.drawable.ic_settings_filled to R.drawable.ic_settings_outlined),
                "magnifyingglass" to (R.drawable.ic_search_filled to R.drawable.ic_search_outlined),
                "heart" to (R.drawable.ic_favorite_filled to R.drawable.ic_favorite_border),
                "heart.fill" to (R.drawable.ic_favorite_filled to R.drawable.ic_favorite_border),
                "star" to (R.drawable.ic_star_filled to R.drawable.ic_star_outlined),
                "star.fill" to (R.drawable.ic_star_filled to R.drawable.ic_star_outlined),
                "bell" to (R.drawable.ic_notifications_filled to R.drawable.ic_notifications_outlined),
                "bell.fill" to (R.drawable.ic_notifications_filled to R.drawable.ic_notifications_outlined),
                "cart" to (R.drawable.ic_shopping_cart_filled to R.drawable.ic_shopping_cart_outlined),
                "cart.fill" to (R.drawable.ic_shopping_cart_filled to R.drawable.ic_shopping_cart_outlined),
                "list.bullet" to (R.drawable.ic_list_filled to R.drawable.ic_list_outlined),
                "circle" to (R.drawable.ic_circle_filled to R.drawable.ic_circle_outlined)
            )

            val icons = iconMap[iconName] ?: (R.drawable.ic_circle_filled to R.drawable.ic_circle_outlined)
            return if (filled) icons.first else icons.second
        }
    }
}
