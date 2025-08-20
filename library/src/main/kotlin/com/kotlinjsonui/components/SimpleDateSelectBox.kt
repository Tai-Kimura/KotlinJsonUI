package com.kotlinjsonui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simplified date selection component for testing
 */
@Composable
fun SimpleDateSelectBox(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    enabled: Boolean = true,
    backgroundColor: Color = Color.White,
    borderColor: Color = Color(0xFFCCCCCC),
    textColor: Color = Color.Black,
    hintColor: Color = Color(0xFF999999),
    cornerRadius: Int = 8
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    // Parse current value if not empty
    LaunchedEffect(value) {
        if (value.isNotEmpty()) {
            try {
                dateFormat.parse(value)?.let {
                    calendar.time = it
                }
            } catch (e: Exception) {
                // Invalid date format
            }
        }
    }
    
    // Display text
    val displayText = if (value.isNotEmpty()) value else ""
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp)
            .clickable(enabled = enabled) {
                // Show date picker dialog
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        calendar.set(year, month, dayOfMonth)
                        val newValue = dateFormat.format(calendar.time)
                        onValueChange(newValue)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
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
                text = if (displayText.isNotEmpty()) displayText else (placeholder ?: ""),
                color = if (displayText.isNotEmpty()) textColor else hintColor,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Calendar",
                tint = textColor
            )
        }
    }
}