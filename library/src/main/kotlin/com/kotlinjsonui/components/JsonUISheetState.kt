package com.kotlinjsonui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable

/**
 * The state of the bottom sheet a JsonUI SelectBox / DateSelectBox opens: hidden until opened, then the full height
 * of its content (no half-open stop).
 *
 * Why a wrapper: when Compose changes how a sheet's state is made, only this body changes (ticket
 * kjui-slider-deprecated-in-material3-1-5: material3 1.5 deprecated rememberModalBottomSheetState(skipPartiallyExpanded
 * = true); this is its replacement, rememberBottomSheetState with Hidden and Expanded only).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun rememberJsonUISheetState(): SheetState =
    rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)
    )
