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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.launch

/**
 * A reusable SelectBox component for KotlinJsonUI
 * 
 * @param value The currently selected value
 * @param onValueChange Callback when a new value is selected
 * @param options List of options to display in the dropdown
 * @param modifier Modifier for the component
 * @param placeholder Optional placeholder text when no value is selected
 * @param enabled Whether the component is enabled
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
    sheetBackgroundColor: Color = Configuration.SelectBox.defaultSheetBackgroundColor,
    sheetTextColor: Color = Configuration.SelectBox.defaultSheetTextColor
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true  // Always expand to full height
    )
    val scope = rememberCoroutineScope()
    
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
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (value.isNotEmpty()) value else (placeholder ?: ""),
                color = if (value.isNotEmpty()) textColor else hintColor,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Dropdown",
                tint = textColor
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
            windowInsets = WindowInsets(0, 0, 0, 0)  // Ensure full visibility
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()  // Proper padding for navigation bar
                    .padding(bottom = 16.dp)
            ) {
                // Title or header (optional)
                Text(
                    text = "選択してください",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 16.dp)
                )
                
                Divider()
                
                // Scrollable list of options
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .heightIn(max = 400.dp) // Maximum height before scrolling
                ) {
                    items(options) { option ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
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
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (option == value) {
                                        sheetTextColor
                                    } else {
                                        sheetTextColor.copy(alpha = 0.8f)
                                    }
                                )
                                if (option == value) {
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                        if (option != options.last()) {
                            Divider(modifier = Modifier.padding(horizontal = 20.dp))
                        }
                    }
                }
                
                // Cancel button
                Divider()
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { 
                            scope.launch {
                                sheetState.hide()
                                showBottomSheet = false
                            }
                        }
                ) {
                    Text(
                        text = "キャンセル",
                        fontSize = Configuration.SelectBox.SheetButton.defaultFontSize.sp,
                        fontWeight = FontWeight(Configuration.SelectBox.SheetButton.defaultFontWeight),
                        color = Configuration.SelectBox.SheetButton.defaultCancelButtonTextColor,
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
    sheetTextColor: Color = Configuration.SelectBox.defaultSheetTextColor
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val displayText = options.find { it.value == value }?.label ?: ""
    
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
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Dropdown",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
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
            windowInsets = WindowInsets(0, 0, 0, 0)  // Ensure full visibility
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()  // Proper padding for navigation bar
                    .padding(bottom = 16.dp)
            ) {
                // Title or header (optional)
                Text(
                    text = "選択してください",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 16.dp)
                )
                
                Divider()
                
                // Scrollable list of options
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .heightIn(max = 400.dp) // Maximum height before scrolling
                ) {
                    items(options) { option ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onValueChange(option.value)
                                    showBottomSheet = false
                                },
                            color = if (option.value == value) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surface
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
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (option.value == value) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    }
                                )
                                if (option.value == value) {
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                        if (option != options.last()) {
                            Divider(modifier = Modifier.padding(horizontal = 20.dp))
                        }
                    }
                }
                
                // Cancel button
                Divider()
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { 
                            scope.launch {
                                sheetState.hide()
                                showBottomSheet = false
                            }
                        }
                ) {
                    Text(
                        text = "キャンセル",
                        style = MaterialTheme.typography.bodyLarge,
                        color = sheetTextColor,
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