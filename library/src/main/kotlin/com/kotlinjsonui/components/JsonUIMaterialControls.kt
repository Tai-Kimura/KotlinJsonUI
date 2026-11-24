package com.kotlinjsonui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp

/*
 * The material3 controls a JsonUI layout draws, for the kjui codegen and the dynamic face alike (jsonui-cli ticket
 * kjui-wrap-material3-calls-next; the requester's 2026-11-22 instruction: the wrappers are there so that a later
 * change of material3 is fixed here, and generated code is not emitted again).
 *
 * Each takes the values JsonUI knows and no material3 type: a colour a layout does not declare is left at
 * material3's own default (the colour parameters of the *Defaults.colors() functions are Color.Unspecified there
 * too, which keeps the theme's — except where a slot does not, see JsonUISwitch; the others default to the very
 * expression material3 defaults to). The body is the
 * call generated code and the dynamic face made before, so the drawing is the same.
 */

/** A JsonUI Switch: material3's Switch with the track and thumb colours a layout declares. */
@Composable
fun JsonUISwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    checkedTrackColor: Color = Color.Unspecified,
    uncheckedTrackColor: Color = Color.Unspecified,
    checkedThumbColor: Color = Color.Unspecified,
    uncheckedThumbColor: Color = Color.Unspecified,
    checkedBorderColor: Color = Color.Unspecified,
    uncheckedBorderColor: Color = Color.Unspecified,
) {
    // SwitchDefaults.colors does not read Color.Unspecified as "the theme's" for every slot (an unchecked track
    // given Unspecified is drawn transparent), so a colour not declared is material3's default colour itself.
    val theme = SwitchDefaults.colors()
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        colors = SwitchDefaults.colors(
            checkedThumbColor = checkedThumbColor.takeOrElse { theme.checkedThumbColor },
            checkedTrackColor = checkedTrackColor.takeOrElse { theme.checkedTrackColor },
            checkedBorderColor = checkedBorderColor.takeOrElse { theme.checkedBorderColor },
            uncheckedThumbColor = uncheckedThumbColor.takeOrElse { theme.uncheckedThumbColor },
            uncheckedTrackColor = uncheckedTrackColor.takeOrElse { theme.uncheckedTrackColor },
            uncheckedBorderColor = uncheckedBorderColor.takeOrElse { theme.uncheckedBorderColor },
        ),
    )
}

/** A JsonUI CheckBox (and a Radio drawn as a check): material3's Checkbox with the colours a layout declares. */
@Composable
fun JsonUICheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    checkedColor: Color = Color.Unspecified,
    uncheckedColor: Color = Color.Unspecified,
    checkmarkColor: Color = Color.Unspecified,
) {
    Checkbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        colors = CheckboxDefaults.colors(
            checkedColor = checkedColor,
            uncheckedColor = uncheckedColor,
            checkmarkColor = checkmarkColor,
        ),
    )
}

/** A JsonUI Radio's button: material3's RadioButton with the colours a layout declares. */
@Composable
fun JsonUIRadioButton(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selectedColor: Color = Color.Unspecified,
    unselectedColor: Color = Color.Unspecified,
) {
    RadioButton(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = RadioButtonDefaults.colors(selectedColor = selectedColor, unselectedColor = unselectedColor),
    )
}

/**
 * A JsonUI Progress (and a linear Indicator): material3's LinearProgressIndicator, determinate when [progress] is
 * given, indeterminate when it is null.
 */
@Composable
fun JsonUILinearProgressIndicator(
    modifier: Modifier = Modifier,
    progress: (() -> Float)? = null,
    color: Color = ProgressIndicatorDefaults.linearColor,
    trackColor: Color = ProgressIndicatorDefaults.linearTrackColor,
) {
    if (progress != null) {
        LinearProgressIndicator(progress = progress, modifier = modifier, color = color, trackColor = trackColor)
    } else {
        LinearProgressIndicator(modifier = modifier, color = color, trackColor = trackColor)
    }
}

/** A JsonUI Indicator (and the spinner a loading image or cell shows): material3's indeterminate CircularProgressIndicator. */
@Composable
fun JsonUICircularProgressIndicator(
    modifier: Modifier = Modifier,
    color: Color = ProgressIndicatorDefaults.circularColor,
    strokeWidth: Dp = ProgressIndicatorDefaults.CircularStrokeWidth,
    trackColor: Color = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
) {
    CircularProgressIndicator(modifier = modifier, color = color, strokeWidth = strokeWidth, trackColor = trackColor)
}

/** An icon a JsonUI control draws (a TabView tab, a CheckBox or Radio drawn with images): material3's Icon. */
@Composable
fun JsonUIIcon(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    Icon(painter = painter, contentDescription = contentDescription, modifier = modifier, tint = tint)
}

/** An icon a JsonUI control draws from a vector (a Radio drawn with icons): material3's Icon. */
@Composable
fun JsonUIIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    Icon(imageVector = imageVector, contentDescription = contentDescription, modifier = modifier, tint = tint)
}

/** A CheckBox drawn with its icon / selectedIcon images: material3's IconToggleButton. */
@Composable
fun JsonUIIconToggleButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    IconToggleButton(checked = checked, onCheckedChange = onCheckedChange, modifier = modifier, enabled = enabled, content = content)
}

/** A Radio drawn with icons: material3's IconButton. */
@Composable
fun JsonUIIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    IconButton(onClick = onClick, modifier = modifier, enabled = enabled, content = content)
}

/**
 * A JsonUI Button: material3's Button with the colours, shape, border and padding a layout declares. The colours
 * not declared are material3's (Color.Unspecified, as ButtonDefaults.buttonColors takes them). [elevation] is the
 * resting elevation: [Dp.Unspecified] is material3's own, null none (the dynamic face draws a Button without a
 * declared shadow flat), a length that one.
 */
@Composable
fun JsonUIButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    containerColor: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
    disabledContainerColor: Color = Color.Unspecified,
    disabledContentColor: Color = Color.Unspecified,
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    elevation: Dp? = Dp.Unspecified,
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor,
        ),
        elevation = when {
            elevation == null -> null
            elevation == Dp.Unspecified -> ButtonDefaults.buttonElevation()
            else -> ButtonDefaults.buttonElevation(defaultElevation = elevation)
        },
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content,
    )
}

/** The line between a JsonUI Table's rows: material3's HorizontalDivider. */
@Composable
fun JsonUIDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = DividerDefaults.Thickness,
    color: Color = DividerDefaults.color,
) {
    HorizontalDivider(modifier = modifier, thickness = thickness, color = color)
}

/** A JsonUI TabView's frame: material3's Scaffold with the tab bar at the bottom. */
@Composable
fun JsonUITabScaffold(
    modifier: Modifier = Modifier,
    bottomBar: @Composable () -> Unit = {},
    containerColor: Color = MaterialTheme.colorScheme.background,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(modifier = modifier, bottomBar = bottomBar, containerColor = containerColor, content = content)
}

/** A JsonUI TabView's tab bar: material3's NavigationBar. */
@Composable
fun JsonUITabBar(
    modifier: Modifier = Modifier,
    containerColor: Color = NavigationBarDefaults.containerColor,
    content: @Composable RowScope.() -> Unit,
) {
    NavigationBar(modifier = modifier, containerColor = containerColor, content = content)
}

/** One tab of a JsonUI TabView's tab bar: material3's NavigationBarItem with the colours a layout declares. */
@Composable
fun RowScope.JsonUITabBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    selectedIconColor: Color = Color.Unspecified,
    selectedTextColor: Color = Color.Unspecified,
    unselectedIconColor: Color = Color.Unspecified,
    unselectedTextColor: Color = Color.Unspecified,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = icon,
        modifier = modifier,
        enabled = enabled,
        label = label,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = selectedIconColor,
            selectedTextColor = selectedTextColor,
            unselectedIconColor = unselectedIconColor,
            unselectedTextColor = unselectedTextColor,
        ),
    )
}

/** A TabView tab's icon with its badge text: material3's BadgedBox with a Badge holding [badge]. */
@Composable
fun JsonUIBadgedBox(
    badge: String,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    BadgedBox(badge = { Badge { Text(badge) } }, modifier = modifier, content = content)
}
