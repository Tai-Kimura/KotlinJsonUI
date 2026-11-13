package com.kotlinjsonui.dynamic.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.unit.TextUnit
import com.google.gson.JsonObject
import com.kotlinjsonui.core.jsonUILetterSpacing

/** The text style's letter spacing drawn by the component's fontScaling / maxFontScale (jsonUILetterSpacing). */
@Composable
@ReadOnlyComposable
internal fun JsonObject.letterSpaced(): TextUnit = jsonUILetterSpacing(fontScalingAttr(), maxFontScaleAttr())
