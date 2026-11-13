package com.kotlinjsonui.dynamic.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.unit.TextUnit
import com.google.gson.JsonObject
import com.kotlinjsonui.core.jsonUIFontSize

/** The component's `fontScaling` (a literal boolean), or null: the app default (FontScaling). */
internal fun JsonObject.fontScalingAttr(): Boolean? =
    get("fontScaling")?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isBoolean }?.asBoolean

/** The component's `maxFontScale` (a literal number), or null: the app default (FontScaling). */
internal fun JsonObject.maxFontScaleAttr(): Float? =
    get("maxFontScale")?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isNumber }?.asFloat

/**
 * [size] (a declared size, or a line height derived from it, as sp) drawn by the component's own fontScaling /
 * maxFontScale — the Dynamic twin of the generated `jsonUIFontSize(size, fontScaling, maxFontScale)`.
 */
@Composable
@ReadOnlyComposable
internal fun JsonObject.fontSized(size: TextUnit): TextUnit = jsonUIFontSize(size, fontScalingAttr(), maxFontScaleAttr())
