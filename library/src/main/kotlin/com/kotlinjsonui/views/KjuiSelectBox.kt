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
            setImageResource(android.R.drawable.arrow_down_float)
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
    
    private fun Float.dpToPx(): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this,
            context.resources.displayMetrics
        )
    }
}