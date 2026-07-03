package com.kotlinjsonui.components

import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import com.kotlinjsonui.core.Configuration

/**
 * Custom Segment component that wraps SecondaryTabRow with Configuration defaults
 */
@Composable
fun Segment(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color? = null,
    contentColor: Color? = null,
    selectedContentColor: Color? = null,
    indicatorColor: Color? = null,
    tabs: @Composable () -> Unit
) {
    SecondaryTabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = containerColor ?: Configuration.Segment.defaultBackgroundColor,
        contentColor = contentColor ?: Configuration.Segment.defaultTextColor,
        indicator = {
            TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(selectedTabIndex, matchContentSize = false),
                color = if (enabled) {
                    indicatorColor ?: Configuration.Segment.defaultSelectedBackgroundColor
                } else {
                    // No indicator when disabled
                    Color.Transparent
                }
            )
        },
        modifier = modifier.alpha(if (enabled) 1f else 0.6f),
        tabs = tabs
    )
}
