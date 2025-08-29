package com.kotlinjsonui.binding

import android.content.res.ColorStateList
import android.graphics.Color as AndroidColor
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SeekBar
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kotlinjsonui.data.CollectionDataSource

object BindingAdapters {
    
    @JvmStatic
    @BindingAdapter("android:visibility")
    fun setVisibility(view: View, visibility: String?) {
        view.visibility = when (visibility?.lowercase()) {
            "visible" -> View.VISIBLE
            "invisible" -> View.INVISIBLE
            "gone" -> View.GONE
            else -> View.VISIBLE
        }
    }
    
    @JvmStatic
    @BindingAdapter("android:progress")
    fun setProgress(seekBar: SeekBar, progress: Double) {
        seekBar.progress = progress.toInt()
    }
    
    @JvmStatic
    @BindingAdapter("android:progress")
    fun setProgress(progressBar: ProgressBar, progress: Double) {
        progressBar.progress = progress.toInt()
    }
    
    @JvmStatic
    @BindingAdapter("android:progress")
    fun setProgressView(view: View, progress: Double) {
        // For generic views with progress, this is a no-op
        // The actual progress implementation would be in a custom view
        when (view) {
            is SeekBar -> view.progress = progress.toInt()
            is ProgressBar -> view.progress = progress.toInt()
        }
    }
    
    @JvmStatic
    @BindingAdapter("app:items")
    fun setRecyclerViewItems(recyclerView: RecyclerView, items: CollectionDataSource?) {
        // This would need proper implementation with an adapter
        // For now, just a placeholder to make data binding compile
        // The actual implementation would set up the RecyclerView adapter
    }
    
    @JvmStatic
    @BindingAdapter("android:tint")
    fun setTintColor(view: View, color: Color?) {
        if (color != null) {
            view.backgroundTintList = ColorStateList.valueOf(color.toArgb())
        }
    }
    
    @JvmStatic
    @BindingAdapter("android:tint")
    fun setTintColor(imageView: ImageView, color: Color?) {
        if (color != null) {
            imageView.setColorFilter(color.toArgb())
        }
    }
    
    @JvmStatic
    @BindingAdapter("android:tint")
    fun setTintColorString(imageView: ImageView, colorString: String?) {
        if (!colorString.isNullOrEmpty()) {
            try {
                val color = AndroidColor.parseColor(colorString)
                imageView.setColorFilter(color)
            } catch (e: IllegalArgumentException) {
                // Invalid color string, ignore
            }
        }
    }
}