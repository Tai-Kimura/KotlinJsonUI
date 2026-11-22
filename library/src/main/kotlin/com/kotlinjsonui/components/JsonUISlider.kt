package com.kotlinjsonui.components

import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * The Slider the kjui codegen emits, with the same parameters and defaults as material3's value-and-callback Slider.
 *
 * It calls material3's value form of Slider. material3 1.5 (compose-bom 2026.11.01) deprecates that form in favour of
 * the one that takes a SliderState with onValueChange / onValueChangeFinished, which material3 1.4 does not have, so
 * no call in generated code compiles without a warning against both; an app built with allWarningsAsErrors failed on
 * 1.5 (ticket kjui-slider-deprecated-in-material3-1-5). Here the call is compiled with KotlinJsonUI, against its own
 * compose-bom, and the generated code calls this: the app sees no deprecated call on 1.4 or 1.5, and material3 1.5
 * still carries the value form, so it runs on both.
 *
 * When KotlinJsonUI's compose-bom reaches material3 1.5 or later, switch the body to the SliderState form (the
 * parameters here stay as they are).
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
    colors: SliderColors = SliderDefaults.colors()
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        valueRange = valueRange,
        steps = steps,
        onValueChangeFinished = onValueChangeFinished,
        colors = colors
    )
}
