package com.kotlinjsonui.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import com.kotlinjsonui.core.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.res.painterResource
import com.kotlinjsonui.R
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.launch

/**
 * The closed select's caret, when the layout styles it (`SelectBox.caretAttributes`,
 * shared/core/attribute_definitions.json — a cross-platform object since
 * jsonui-cli 1.8.101; it was UIKit-only before).
 *
 * Absent (`caret = null`, the default): the component keeps drawing the
 * native `ic_arrow_drop_down` inside the content padding, exactly the picture
 * it drew before the parameter existed, so existing layouts do not move.
 *
 * Present: the component draws the caret itself — a box of [width] x [height]
 * dp filled with [background], the glyph ([painter], or the default drop-down
 * arrow when null) tinted with [tintColor], placed [rightMargin] dp from the
 * select's trailing edge (0 = flush against the edge, the UIKit contract this
 * object came from). The native indicator is not drawn.
 *
 * Both faces resolve `src` to a [Painter] before calling: the codegen emits
 * `painterResource(R.drawable.<name>)`, dynamic mode goes through
 * `ResourceResolver.resolveDrawable` — the same split Image uses.
 */
data class SelectBoxCaret(
    /** The caret image. Null draws the default drop-down glyph. */
    val painter: Painter? = null,
    /** Caret box width in dp. Null: the default glyph's 24dp. */
    val width: Int? = null,
    /** Caret box height in dp. Null: the default glyph's 24dp. */
    val height: Int? = null,
    /**
     * Glyph tint. Null keeps the tint the native arrow had (the text color)
     * for the default glyph, and draws a supplied [painter] untinted.
     */
    val tintColor: Color? = null,
    /** Fill of the caret's own box (width x height), not of the select. */
    val background: Color? = null,
    /** Distance from the select's trailing edge to the caret's trailing edge, dp. */
    val rightMargin: Int = 0
)

/** The default drop-down glyph is a 24dp vector (res/drawable/ic_arrow_drop_down.xml). */
internal const val DEFAULT_CARET_GLYPH_DP = 24

/**
 * How far the closed-state text must stop short of the row's end so it never
 * runs under a self-drawn caret. The caret is positioned from the select's
 * edge (not inside the content padding), so the reserve is the caret's
 * footprint minus whatever end inset the content padding already provides;
 * never negative.
 */
internal fun caretTextReserve(caretWidth: Dp, rightMargin: Dp, endInset: Dp): Dp =
    (caretWidth + rightMargin - endInset).coerceAtLeast(0.dp)

/**
 * The self-drawn caret. [defaultTint] is what the native arrow used on this
 * overload, so a caret object without `tintColor` keeps that colour.
 */
@Composable
private fun SelectBoxCaretIndicator(
    caret: SelectBoxCaret,
    defaultTint: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(end = caret.rightMargin.dp)
            .size(
                width = (caret.width ?: DEFAULT_CARET_GLYPH_DP).dp,
                height = (caret.height ?: DEFAULT_CARET_GLYPH_DP).dp
            )
            .background(caret.background ?: Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = caret.painter ?: painterResource(R.drawable.ic_arrow_drop_down),
            contentDescription = "Dropdown",
            tint = caret.tintColor ?: if (caret.painter == null) defaultTint else Color.Unspecified,
            // The box IS the declared size; the glyph fills it (Icon's own
            // 24dp default would leave a 32x32 box with a 24dp arrow inside).
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * A reusable SelectBox component for KotlinJsonUI
 * 
 * @param value The currently selected value
 * @param onValueChange Callback when a new value is selected
 * @param options List of options to display in the dropdown
 * @param modifier Modifier for the component
 * @param placeholder Optional placeholder text when no value is selected
 * @param enabled Whether the component is enabled
 * @param caret The closed select's caret when the layout styles it; null keeps the native arrow
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectBox(
    value: String,
    onValueChange: (String) -> Unit,
    options: List<String>,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    enabled: Boolean = true,
    backgroundColor: Color = Color.White,
    borderColor: Color = Color(0xFFCCCCCC),
    textColor: Color = Color.Black,
    hintColor: Color = Color(0xFF999999),
    cornerRadius: Int = 8,
    fontSize: Int = 16,
    fontWeight: FontWeight = FontWeight.Normal,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    sheetBackgroundColor: Color = Configuration.SelectBox.defaultSheetBackgroundColor,
    sheetTextColor: Color = Configuration.SelectBox.defaultSheetTextColor,
    cancelButtonBackgroundColor: Color = Configuration.SelectBox.defaultSheetBackgroundColor,
    cancelButtonTextColor: Color = Configuration.SelectBox.SheetButton.defaultCancelButtonTextColor,
    caret: SelectBoxCaret? = null
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true  // Always expand to full height
    )
    val scope = rememberCoroutineScope()
    // A self-drawn caret sits outside the content padding (positioned from
    // the select's edge), so the text reserves its footprint explicitly.
    val textEndReserve = if (caret != null) {
        caretTextReserve(
            caretWidth = (caret.width ?: DEFAULT_CARET_GLYPH_DP).dp,
            rightMargin = caret.rightMargin.dp,
            endInset = contentPadding.calculateEndPadding(LocalLayoutDirection.current)
        )
    } else 0.dp
    
    // Custom SelectBox field that looks like OutlinedTextField
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(enabled = enabled) { 
                showBottomSheet = true
            }
            .border(
                width = 1.dp,
                color = if (enabled) borderColor else borderColor.copy(alpha = 0.38f),
                shape = RoundedCornerShape(cornerRadius.dp)
            )
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(cornerRadius.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (value.isNotEmpty()) value else (placeholder ?: ""),
                color = if (value.isNotEmpty()) textColor else hintColor,
                fontSize = fontSize.sp,
                fontWeight = fontWeight,
                modifier = Modifier.weight(1f).padding(end = textEndReserve)
            )
            if (caret == null) {
                // No caretAttributes: the native indicator, unchanged.
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_drop_down),
                    contentDescription = "Dropdown",
                    tint = textColor
                )
            }
        }
        if (caret != null) {
            SelectBoxCaretIndicator(
                caret = caret,
                defaultTint = textColor,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
    
    // Bottom Sheet with options
    if (showBottomSheet) {
        // Ensure sheet is fully expanded when shown
        LaunchedEffect(showBottomSheet) {
            sheetState.expand()
        }
        
        ModalBottomSheet(
            onDismissRequest = { 
                scope.launch {
                    sheetState.hide()
                    showBottomSheet = false
                }
            },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            containerColor = sheetBackgroundColor,
            contentColor = sheetTextColor,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()  // Proper padding for navigation bar
                    .padding(bottom = 16.dp)
                    // The sheet is its own window: re-enable testTag -> resource-id
                    // mapping so UI-test drivers (UIAutomator) can find the
                    // kjui_x7q_* elements inside it.
                    .semantics { testTagsAsResourceId = true }
            ) {
                // Title or header (optional)
                Text(
                    text = "選択してください",
                    style = sheetTextStyle(
                        MaterialTheme.typography.titleMedium,
                        Configuration.SelectBox.sheetFontFamily,
                        Configuration.SelectBox.sheetTitleFontSize,
                        null
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 16.dp)
                )

                HorizontalDivider()

                // Option-row style: MaterialTheme bodyLarge with the
                // Configuration sheet-typography overrides merged in (null
                // overrides = the style the sheet has always had).
                val itemStyle = sheetTextStyle(
                    MaterialTheme.typography.bodyLarge,
                    Configuration.SelectBox.sheetFontFamily,
                    Configuration.SelectBox.sheetItemFontSize,
                    Configuration.SelectBox.sheetItemFontWeight
                )

                // Scrollable list of options
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .heightIn(max = 400.dp) // Maximum height before scrolling
                        .testTag("kjui_x7q_optionList")
                ) {
                    // When a placeholder is supplied, expose it as the first
                    // selectable row. Picking it clears the current value
                    // ("" → unselected state) so the button face reverts to
                    // the placeholder text.
                    val placeholderRow = placeholder?.takeIf { it.isNotEmpty() }
                    if (placeholderRow != null) {
                        item {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("kjui_x7q_option_placeholder")
                                    .clickable {
                                        onValueChange("")
                                        scope.launch {
                                            sheetState.hide()
                                            showBottomSheet = false
                                        }
                                    },
                                color = sheetBackgroundColor
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp, vertical = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = placeholderRow,
                                        style = itemStyle,
                                        color = sheetTextColor.copy(alpha = 0.6f)
                                    )
                                }
                            }
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp))
                        }
                    }
                    itemsIndexed(options) { index, option ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("kjui_x7q_option_$index")
                                .clickable {
                                    onValueChange(option)
                                    scope.launch {
                                        sheetState.hide()
                                        showBottomSheet = false
                                    }
                                },
                            color = if (option == value) {
                                sheetBackgroundColor.copy(alpha = 0.9f)
                            } else {
                                sheetBackgroundColor
                            }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = option,
                                    style = itemStyle,
                                    color = if (option == value) {
                                        sheetTextColor
                                    } else {
                                        sheetTextColor.copy(alpha = 0.8f)
                                    }
                                )
                                if (option == value) {
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(
                                        painter = painterResource(R.drawable.ic_check),
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                        if (option != options.last()) {
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp))
                        }
                    }
                }

                // Cancel button
                HorizontalDivider()
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("kjui_x7q_cancel")
                        .clickable {
                            scope.launch {
                                sheetState.hide()
                                showBottomSheet = false
                            }
                        },
                    color = cancelButtonBackgroundColor
                ) {
                    Text(
                        text = "キャンセル",
                        fontSize = Configuration.SelectBox.SheetButton.defaultFontSize.sp,
                        fontWeight = FontWeight(Configuration.SelectBox.SheetButton.defaultFontWeight),
                        fontFamily = Configuration.SelectBox.sheetFontFamily,
                        color = cancelButtonTextColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * A more advanced SelectBox with custom option rendering
 * 
 * @param value The currently selected value
 * @param onValueChange Callback when a new value is selected
 * @param options List of SelectOption objects
 * @param modifier Modifier for the component
 * @param placeholder Optional placeholder text
 * @param enabled Whether the component is enabled
 * @param caret The closed select's caret when the layout styles it; null keeps the native arrow
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SelectBox(
    value: T?,
    onValueChange: (T) -> Unit,
    options: List<SelectOption<T>>,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    enabled: Boolean = true,
    sheetBackgroundColor: Color = Configuration.SelectBox.defaultSheetBackgroundColor,
    sheetTextColor: Color = Configuration.SelectBox.defaultSheetTextColor,
    caret: SelectBoxCaret? = null
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val displayText = options.find { it.value == value }?.label ?: ""
    // Same caret contract as the String overload (this overload's row inset
    // is a fixed 16dp) — one face, not a split.
    val textEndReserve = if (caret != null) {
        caretTextReserve(
            caretWidth = (caret.width ?: DEFAULT_CARET_GLYPH_DP).dp,
            rightMargin = caret.rightMargin.dp,
            endInset = 16.dp
        )
    } else 0.dp
    
    // Custom SelectBox field that looks like OutlinedTextField
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { showBottomSheet = true },
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (enabled) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outline.copy(alpha = 0.38f)
        )
    ) {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = displayText.ifEmpty { placeholder ?: "" },
                    color = if (displayText.isNotEmpty()) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.weight(1f).padding(end = textEndReserve)
                )
                if (caret == null) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_drop_down),
                        contentDescription = "Dropdown",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (caret != null) {
                SelectBoxCaretIndicator(
                    caret = caret,
                    defaultTint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
    }
    
    // Bottom Sheet with options
    if (showBottomSheet) {
        // Ensure sheet is fully expanded when shown
        LaunchedEffect(showBottomSheet) {
            sheetState.expand()
        }
        
        ModalBottomSheet(
            onDismissRequest = { 
                scope.launch {
                    sheetState.hide()
                    showBottomSheet = false
                }
            },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            containerColor = sheetBackgroundColor,
            contentColor = sheetTextColor,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()  // Proper padding for navigation bar
                    .padding(bottom = 16.dp)
                    // The sheet is its own window: re-enable testTag -> resource-id
                    // mapping so UI-test drivers (UIAutomator) can find the
                    // kjui_x7q_* elements inside it.
                    .semantics { testTagsAsResourceId = true }
            ) {
                // Title or header (optional)
                Text(
                    text = "選択してください",
                    style = sheetTextStyle(
                        MaterialTheme.typography.titleMedium,
                        Configuration.SelectBox.sheetFontFamily,
                        Configuration.SelectBox.sheetTitleFontSize,
                        null
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 16.dp)
                )

                HorizontalDivider()

                // Same sheet styling contract as the String overload: rows
                // derive from the sheet colors (NOT the Material color
                // scheme — this overload used primaryContainer/surface until
                // 2.22.0, a face split with no design behind it) and carry
                // the Configuration typography overrides.
                val itemStyle = sheetTextStyle(
                    MaterialTheme.typography.bodyLarge,
                    Configuration.SelectBox.sheetFontFamily,
                    Configuration.SelectBox.sheetItemFontSize,
                    Configuration.SelectBox.sheetItemFontWeight
                )

                // Scrollable list of options
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .heightIn(max = 400.dp) // Maximum height before scrolling
                        .testTag("kjui_x7q_optionList")
                ) {
                    itemsIndexed(options) { index, option ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("kjui_x7q_option_$index")
                                .clickable {
                                    onValueChange(option.value)
                                    showBottomSheet = false
                                },
                            color = if (option.value == value) {
                                sheetBackgroundColor.copy(alpha = 0.9f)
                            } else {
                                sheetBackgroundColor
                            }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = option.label,
                                    style = itemStyle,
                                    color = if (option.value == value) {
                                        sheetTextColor
                                    } else {
                                        sheetTextColor.copy(alpha = 0.8f)
                                    }
                                )
                                if (option.value == value) {
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(
                                        painter = painterResource(R.drawable.ic_check),
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                        if (option != options.last()) {
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp))
                        }
                    }
                }

                // Cancel button — same styling contract as the String
                // overload (SheetButton config; the bare Surface + bodyLarge
                // this overload had until 2.22.0 was the other half of the
                // face split noted above).
                HorizontalDivider()
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("kjui_x7q_cancel")
                        .clickable {
                            scope.launch {
                                sheetState.hide()
                                showBottomSheet = false
                            }
                        },
                    color = sheetBackgroundColor
                ) {
                    Text(
                        text = "キャンセル",
                        fontSize = Configuration.SelectBox.SheetButton.defaultFontSize.sp,
                        fontWeight = FontWeight(Configuration.SelectBox.SheetButton.defaultFontWeight),
                        fontFamily = Configuration.SelectBox.sheetFontFamily,
                        color = Configuration.SelectBox.SheetButton.defaultCancelButtonTextColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * Data class for select options with label and value
 */
data class SelectOption<T>(
    val label: String,
    val value: T
)