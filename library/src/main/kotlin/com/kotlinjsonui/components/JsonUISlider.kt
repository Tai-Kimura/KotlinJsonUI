package com.kotlinjsonui.components

import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * The Slider of a JsonUI Slider, for the kjui codegen and the dynamic Slider alike.
 *
 * Why a wrapper: when Compose changes how a slider is written, only this body changes — generated code and the
 * dynamic face call this with the values JsonUI knows, and are not emitted again (ticket
 * kjui-slider-deprecated-in-material3-1-5: material3 1.5 deprecated the value form both called).
 *
 * The body is material3 1.5's SliderState form, written as material3's own value form was: one SliderState per range
 * and steps, its value set from [value] on every composition, so a bound value moves the thumb and a drag reports
 * through [onValueChange] and [onValueChangeFinished] as before. The colours are JsonUI's (thumbTintColor /
 * tintColor, progressTintColor, trackTintColor); [Color.Unspecified] is the theme's, as material3's default.
 */
@Composable
fun JsonUISlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    thumbColor: Color = Color.Unspecified,
    activeTrackColor: Color = Color.Unspecified,
    inactiveTrackColor: Color = Color.Unspecified
) {
    val state = remember(steps, valueRange) { SliderState(value, steps, valueRange) }
    state.value = value
    Slider(
        state = state,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        onValueChangeFinished = onValueChangeFinished,
        colors = SliderDefaults.colors(
            thumbColor = thumbColor,
            activeTrackColor = activeTrackColor,
            inactiveTrackColor = inactiveTrackColor
        )
    )
}
