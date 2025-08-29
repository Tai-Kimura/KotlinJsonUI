package com.kotlinjsonui.views

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.kotlinjsonui.R

/**
 * Custom SelectBox view for KotlinJsonUI
 * Displays selected value with dropdown icon and shows options in BottomSheet
 */
class KjuiSelectBox @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    
    private val textView: TextView
    private val dropdownIcon: ImageView
    
    var selectedValue: String = ""
        set(value) {
            field = value
            updateDisplay()
            onSelectionChangeListener?.onSelectionChanged(value)
        }
    
    var placeholder: String = "選択してください"
        set(value) {
            field = value
            updateDisplay()
        }
    
    var items: List<String> = emptyList()
        set(value) {
            field = value
            updateDisplay()
        }
    
    var hintColor: Int = Color.parseColor("#999999")
        set(value) {
            field = value
            updateDisplay()
        }
    
    var textColor: Int = Color.BLACK
        set(value) {
            field = value
            updateDisplay()
        }
    
    var bgColor: Int = Color.WHITE
        set(value) {
            field = value
            updateBackground()
        }
    
    var borderColor: Int = Color.parseColor("#CCCCCC")
        set(value) {
            field = value
            updateBackground()
        }
    
    var cornerRadius: Float = 8f.dpToPx()
        set(value) {
            field = value
            updateBackground()
        }
    
    var borderWidth: Float = 1f.dpToPx()
        set(value) {
            field = value
            updateBackground()
        }
    
    var fontSize: Float = 16f
        set(value) {
            field = value
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, value)
        }
    
    // Date picker properties
    var datePickerMode: String = ""
        set(value) {
            field = value
            updateDropdownIcon()
        }
    
    var dateFormat: String = "yyyy-MM-dd"
    
    var minDate: String? = null
    
    var maxDate: String? = null
    
    private var onSelectionChangeListener: OnSelectionChangeListener? = null
    
    interface OnSelectionChangeListener {
        fun onSelectionChanged(value: String)
    }
    
    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        isClickable = true
        isFocusable = true
        
        val padding = 16f.dpToPx().toInt()
        setPadding(padding, padding, padding, padding)
        
        // Process custom attributes if available
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.KjuiSelectBox, defStyleAttr, 0)
            try {
                // Get items string and split by pipe
                typedArray.getString(R.styleable.KjuiSelectBox_items)?.let { itemsString ->
                    items = itemsString.split("|").map { it.trim() }
                }
                
                // Get selected value
                typedArray.getString(R.styleable.KjuiSelectBox_selectedValue)?.let {
                    selectedValue = it
                }
                
                // Get placeholder
                typedArray.getString(R.styleable.KjuiSelectBox_placeholder)?.let {
                    placeholder = it
                }
                
                // Get colors
                hintColor = typedArray.getColor(R.styleable.KjuiSelectBox_hintColor, hintColor)
                textColor = typedArray.getColor(R.styleable.KjuiSelectBox_textColor, textColor)
                bgColor = typedArray.getColor(R.styleable.KjuiSelectBox_backgroundColor, bgColor)
                borderColor = typedArray.getColor(R.styleable.KjuiSelectBox_borderColor, borderColor)
                
                // Get dimensions
                cornerRadius = typedArray.getDimension(R.styleable.KjuiSelectBox_cornerRadius, cornerRadius)
                borderWidth = typedArray.getDimension(R.styleable.KjuiSelectBox_borderWidth, borderWidth)
                fontSize = typedArray.getDimension(R.styleable.KjuiSelectBox_fontSize, fontSize * resources.displayMetrics.scaledDensity) / resources.displayMetrics.scaledDensity
                
                // Get date picker attributes
                typedArray.getString(R.styleable.KjuiSelectBox_datePickerMode)?.let {
                    datePickerMode = it
                }
                typedArray.getString(R.styleable.KjuiSelectBox_dateFormat)?.let {
                    dateFormat = it
                }
                typedArray.getString(R.styleable.KjuiSelectBox_minDate)?.let {
                    minDate = it
                }
                typedArray.getString(R.styleable.KjuiSelectBox_maxDate)?.let {
                    maxDate = it
                }
            } finally {
                typedArray.recycle()
            }
        }
        
        // Create TextView for displaying selected value
        textView = TextView(context).apply {
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
            gravity = Gravity.CENTER_VERTICAL
        }
        addView(textView)
        
        // Create dropdown icon
        dropdownIcon = ImageView(context).apply {
            layoutParams = LayoutParams(24f.dpToPx().toInt(), 24f.dpToPx().toInt()).apply {
                marginStart = 8f.dpToPx().toInt()
            }
            updateDropdownIcon()
            setColorFilter(textColor)
        }
        addView(dropdownIcon)
        
        // Set initial background
        updateBackground()
        updateDisplay()
        
        // Set click listener
        setOnClickListener {
            showBottomSheet()
        }
    }
    
    fun setOnSelectionChangeListener(listener: OnSelectionChangeListener) {
        onSelectionChangeListener = listener
    }
    
    fun setOnSelectionChangeListener(listener: (String) -> Unit) {
        onSelectionChangeListener = object : OnSelectionChangeListener {
            override fun onSelectionChanged(value: String) {
                listener(value)
            }
        }
    }
    
    private fun updateDisplay() {
        if (selectedValue.isEmpty()) {
            textView.text = placeholder
            textView.setTextColor(hintColor)
        } else {
            textView.text = selectedValue
            textView.setTextColor(textColor)
        }
        dropdownIcon.setColorFilter(textColor)
    }
    
    private fun updateBackground() {
        background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(bgColor)
            setStroke(borderWidth.toInt(), borderColor)
            cornerRadii = FloatArray(8) { cornerRadius }
        }
    }
    
    private fun showBottomSheet() {
        val activity = context as? AppCompatActivity ?: return
        val fragmentManager = activity.supportFragmentManager
        
        // Check if this is a date picker mode
        if (datePickerMode.isNotEmpty() && datePickerMode in listOf("date", "time", "dateAndTime")) {
            val dateBottomSheet = DatePickerBottomSheet.newInstance(
                datePickerMode = datePickerMode,
                dateFormat = dateFormat,
                selectedValue = selectedValue,
                minDate = minDate,
                maxDate = maxDate,
                textColor = textColor,
                backgroundColor = bgColor
            )
            
            dateBottomSheet.setOnDateSelectedListener { value ->
                selectedValue = value
            }
            
            dateBottomSheet.show(fragmentManager, "DatePickerBottomSheet")
        } else {
            // Regular select box
            val bottomSheet = SelectBoxBottomSheet.newInstance(
                items = items,
                selectedValue = selectedValue,
                placeholder = placeholder,
                textColor = textColor,
                backgroundColor = bgColor
            )
            
            bottomSheet.setOnSelectionListener { value ->
                selectedValue = value
            }
            
            bottomSheet.show(fragmentManager, "SelectBoxBottomSheet")
        }
    }
    
    private fun updateDropdownIcon() {
        val iconRes = when (datePickerMode) {
            "time" -> android.R.drawable.ic_menu_recent_history
            "date", "dateAndTime" -> android.R.drawable.ic_menu_today
            else -> android.R.drawable.arrow_down_float
        }
        dropdownIcon.setImageResource(iconRes)
    }
    
    private fun Float.dpToPx(): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this,
            context.resources.displayMetrics
        )
    }
}