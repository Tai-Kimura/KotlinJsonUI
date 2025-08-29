package com.kotlinjsonui.views

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.*

/**
 * Bottom sheet for date/time selection in KjuiSelectBox
 */
class DatePickerBottomSheet : BottomSheetDialogFragment() {
    
    private var onDateSelectedListener: ((String) -> Unit)? = null
    private var datePickerMode: String = "date"
    private var dateFormat: String = "yyyy-MM-dd"
    private var selectedValue: String = ""
    private var minDate: String? = null
    private var maxDate: String? = null
    private var textColor: Int = android.graphics.Color.BLACK
    private var backgroundColor: Int = android.graphics.Color.WHITE
    
    private val calendar = Calendar.getInstance()
    private val isoDateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val isoTimeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val isoDateTimeFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    
    companion object {
        fun newInstance(
            datePickerMode: String,
            dateFormat: String,
            selectedValue: String,
            minDate: String? = null,
            maxDate: String? = null,
            textColor: Int = android.graphics.Color.BLACK,
            backgroundColor: Int = android.graphics.Color.WHITE
        ): DatePickerBottomSheet {
            return DatePickerBottomSheet().apply {
                this.datePickerMode = datePickerMode
                this.dateFormat = dateFormat
                this.selectedValue = selectedValue
                this.minDate = minDate
                this.maxDate = maxDate
                this.textColor = textColor
                this.backgroundColor = backgroundColor
            }
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
            setBackgroundColor(backgroundColor)
        }
        
        // Parse current value
        if (selectedValue.isNotEmpty()) {
            try {
                val date = when (datePickerMode) {
                    "time" -> isoTimeFormatter.parse(selectedValue)
                    "dateAndTime" -> isoDateTimeFormatter.parse(selectedValue)
                    else -> isoDateFormatter.parse(selectedValue)
                }
                date?.let { calendar.time = it }
            } catch (e: Exception) {
                // Invalid format, use current date/time
            }
        }
        
        // Title
        val title = TextView(context).apply {
            text = when (datePickerMode) {
                "time" -> "時刻を選択"
                "dateAndTime" -> "日付と時刻を選択"
                else -> "日付を選択"
            }
            textSize = 18f
            setTextColor(textColor)
            setPadding(0, 0, 0, 24)
        }
        layout.addView(title)
        
        when (datePickerMode) {
            "time" -> setupTimePicker(layout)
            "dateAndTime" -> setupDateTimePicker(layout)
            else -> setupDatePicker(layout)
        }
        
        return layout
    }
    
    private fun setupDatePicker(layout: LinearLayout) {
        // Display current date
        val dateDisplay = TextView(context).apply {
            text = SimpleDateFormat(dateFormat, Locale.getDefault()).format(calendar.time)
            textSize = 16f
            setTextColor(textColor)
            setPadding(0, 16, 0, 16)
        }
        layout.addView(dateDisplay)
        
        // Date picker button
        val pickDateButton = MaterialButton(requireContext()).apply {
            text = "日付を選択"
            setOnClickListener {
                DatePickerDialog(
                    requireContext(),
                    { _, year, month, dayOfMonth ->
                        calendar.set(year, month, dayOfMonth)
                        dateDisplay.text = SimpleDateFormat(dateFormat, Locale.getDefault()).format(calendar.time)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).apply {
                    // Set min/max dates if provided
                    minDate?.let {
                        try {
                            val minCal = isoDateFormatter.parse(it)
                            minCal?.let { date -> datePicker.minDate = date.time }
                        } catch (e: Exception) {
                            // Invalid date format
                        }
                    }
                    maxDate?.let {
                        try {
                            val maxCal = isoDateFormatter.parse(it)
                            maxCal?.let { date -> datePicker.maxDate = date.time }
                        } catch (e: Exception) {
                            // Invalid date format
                        }
                    }
                    show()
                }
            }
        }
        layout.addView(pickDateButton)
        
        // Buttons row
        addButtonRow(layout) {
            val formattedDate = isoDateFormatter.format(calendar.time)
            onDateSelectedListener?.invoke(formattedDate)
        }
    }
    
    private fun setupTimePicker(layout: LinearLayout) {
        // Display current time
        val timeDisplay = TextView(context).apply {
            text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
            textSize = 16f
            setTextColor(textColor)
            setPadding(0, 16, 0, 16)
        }
        layout.addView(timeDisplay)
        
        // Time picker button
        val pickTimeButton = MaterialButton(requireContext()).apply {
            text = "時刻を選択"
            setOnClickListener {
                TimePickerDialog(
                    requireContext(),
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        timeDisplay.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true // 24-hour format
                ).show()
            }
        }
        layout.addView(pickTimeButton)
        
        // Buttons row
        addButtonRow(layout) {
            val formattedTime = isoTimeFormatter.format(calendar.time)
            onDateSelectedListener?.invoke(formattedTime)
        }
    }
    
    private fun setupDateTimePicker(layout: LinearLayout) {
        // Display current date and time
        val dateTimeDisplay = TextView(context).apply {
            text = SimpleDateFormat(dateFormat, Locale.getDefault()).format(calendar.time)
            textSize = 16f
            setTextColor(textColor)
            setPadding(0, 16, 0, 16)
        }
        layout.addView(dateTimeDisplay)
        
        // Date picker button
        val pickDateButton = MaterialButton(requireContext()).apply {
            text = "日付を選択"
            setOnClickListener {
                DatePickerDialog(
                    requireContext(),
                    { _, year, month, dayOfMonth ->
                        calendar.set(year, month, dayOfMonth)
                        dateTimeDisplay.text = SimpleDateFormat(dateFormat, Locale.getDefault()).format(calendar.time)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).apply {
                    // Set min/max dates if provided
                    minDate?.let {
                        try {
                            val minCal = isoDateFormatter.parse(it)
                            minCal?.let { date -> datePicker.minDate = date.time }
                        } catch (e: Exception) {
                            // Invalid date format
                        }
                    }
                    maxDate?.let {
                        try {
                            val maxCal = isoDateFormatter.parse(it)
                            maxCal?.let { date -> datePicker.maxDate = date.time }
                        } catch (e: Exception) {
                            // Invalid date format
                        }
                    }
                    show()
                }
            }
        }
        layout.addView(pickDateButton)
        
        // Time picker button
        val pickTimeButton = MaterialButton(requireContext()).apply {
            text = "時刻を選択"
            setOnClickListener {
                TimePickerDialog(
                    requireContext(),
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        dateTimeDisplay.text = SimpleDateFormat(dateFormat, Locale.getDefault()).format(calendar.time)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true // 24-hour format
                ).show()
            }
        }
        layout.addView(pickTimeButton)
        
        // Buttons row
        addButtonRow(layout) {
            val formattedDateTime = isoDateTimeFormatter.format(calendar.time)
            onDateSelectedListener?.invoke(formattedDateTime)
        }
    }
    
    private fun addButtonRow(layout: LinearLayout, onConfirm: () -> Unit) {
        val buttonRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 24, 0, 0)
        }
        
        // Cancel button
        val cancelButton = MaterialButton(requireContext()).apply {
            text = "キャンセル"
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginEnd = 8
            }
            setOnClickListener {
                dismiss()
            }
        }
        buttonRow.addView(cancelButton)
        
        // Confirm button
        val confirmButton = MaterialButton(requireContext()).apply {
            text = "確定"
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginStart = 8
            }
            setOnClickListener {
                onConfirm()
                dismiss()
            }
        }
        buttonRow.addView(confirmButton)
        
        layout.addView(buttonRow)
    }
    
    fun setOnDateSelectedListener(listener: (String) -> Unit) {
        onDateSelectedListener = listener
    }
}