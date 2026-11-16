package com.kotlinjsonui.views

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.divider.MaterialDivider

/**
 * BottomSheet for displaying SelectBox options
 */
class SelectBoxBottomSheet : BottomSheetDialogFragment() {
    
    private var items: List<String> = emptyList()
    private var selectedValue: String = ""
    private var placeholder: String = "選択してください"
    private var textColor: Int = Color.BLACK
    private var backgroundColor: Int = Color.WHITE
    private var onSelectionListener: ((String) -> Unit)? = null
    
    companion object {
        private const val ARG_ITEMS = "items"
        private const val ARG_SELECTED_VALUE = "selected_value"
        private const val ARG_PLACEHOLDER = "placeholder"
        private const val ARG_TEXT_COLOR = "text_color"
        private const val ARG_BACKGROUND_COLOR = "background_color"
        
        fun newInstance(
            items: List<String>,
            selectedValue: String,
            placeholder: String,
            textColor: Int,
            backgroundColor: Int
        ): SelectBoxBottomSheet {
            return SelectBoxBottomSheet().apply {
                arguments = Bundle().apply {
                    putStringArrayList(ARG_ITEMS, ArrayList(items))
                    putString(ARG_SELECTED_VALUE, selectedValue)
                    putString(ARG_PLACEHOLDER, placeholder)
                    putInt(ARG_TEXT_COLOR, textColor)
                    putInt(ARG_BACKGROUND_COLOR, backgroundColor)
                }
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            items = it.getStringArrayList(ARG_ITEMS) ?: emptyList()
            selectedValue = it.getString(ARG_SELECTED_VALUE, "")
            placeholder = it.getString(ARG_PLACEHOLDER, "選択してください")
            textColor = it.getInt(ARG_TEXT_COLOR, Color.BLACK)
            backgroundColor = it.getInt(ARG_BACKGROUND_COLOR, Color.WHITE)
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(backgroundColor)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        
        // Title
        val titleView = TextView(requireContext()).apply {
            text = placeholder
            textSize = 18f
            setTextColor(textColor)
            gravity = android.view.Gravity.CENTER
            setPadding(0, 32.dpToPx(), 0, 32.dpToPx())
        }
        rootView.addView(titleView)
        
        // Divider
        rootView.addView(createDivider())
        
        // RecyclerView for options
        val recyclerView = RecyclerView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                val maxHeight = (400 * resources.displayMetrics.density).toInt()
                height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
            layoutManager = LinearLayoutManager(requireContext())
            adapter = OptionsAdapter(items, selectedValue, textColor) { selected ->
                onSelectionListener?.invoke(selected)
                dismiss()
            }
            overScrollMode = View.OVER_SCROLL_NEVER
        }
        rootView.addView(recyclerView)
        
        // Divider
        rootView.addView(createDivider())
        
        // Cancel button
        val cancelButton = TextView(requireContext()).apply {
            text = "キャンセル"
            textSize = 16f
            setTextColor(Color.parseColor("#FF0000"))
            gravity = android.view.Gravity.CENTER
            setPadding(0, 32.dpToPx(), 0, 32.dpToPx())
            setBackgroundColor(backgroundColor)
            isClickable = true
            isFocusable = true
            setOnClickListener {
                dismiss()
            }
        }
        rootView.addView(cancelButton)
        
        return rootView
    }
    
    fun setOnSelectionListener(listener: (String) -> Unit) {
        onSelectionListener = listener
    }
    
    private fun createDivider(): View {
        return View(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                1.dpToPx()
            )
            setBackgroundColor(Color.parseColor("#E0E0E0"))
        }
    }
    
    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
    
    /**
     * RecyclerView Adapter for options
     */
    private class OptionsAdapter(
        private val items: List<String>,
        private val selectedValue: String,
        private val textColor: Int,
        private val onItemClick: (String) -> Unit
    ) : RecyclerView.Adapter<OptionsAdapter.ViewHolder>() {
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val density = parent.context.resources.displayMetrics.density
            val textView = TextView(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setPadding(
                    (40 * density).toInt(), 
                    (32 * density).toInt(), 
                    (40 * density).toInt(), 
                    (32 * density).toInt()
                )
                textSize = 16f
                gravity = android.view.Gravity.START or android.view.Gravity.CENTER_VERTICAL
            }
            return ViewHolder(textView)
        }
        
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.textView.apply {
                text = item
                setTextColor(textColor)
                
                // Highlight selected item
                if (item == selectedValue) {
                    setBackgroundColor(Color.parseColor("#F0F0F0"))
                    setTypeface(null, android.graphics.Typeface.BOLD)
                } else {
                    setBackgroundColor(Color.TRANSPARENT)
                    setTypeface(null, android.graphics.Typeface.NORMAL)
                }
                
                setOnClickListener {
                    onItemClick(item)
                }
            }
        }
        
        override fun getItemCount(): Int = items.size
        
        class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)
    }
}