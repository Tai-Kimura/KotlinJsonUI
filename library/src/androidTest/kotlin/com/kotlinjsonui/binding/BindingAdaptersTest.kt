package com.kotlinjsonui.binding

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SeekBar
import androidx.compose.ui.graphics.Color
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BindingAdaptersTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun setVisibility_visible() {
        val view = View(context)
        BindingAdapters.setVisibility(view, "visible")
        assertEquals(View.VISIBLE, view.visibility)
    }

    @Test
    fun setVisibility_invisible() {
        val view = View(context)
        BindingAdapters.setVisibility(view, "invisible")
        assertEquals(View.INVISIBLE, view.visibility)
    }

    @Test
    fun setVisibility_gone() {
        val view = View(context)
        BindingAdapters.setVisibility(view, "gone")
        assertEquals(View.GONE, view.visibility)
    }

    @Test
    fun setVisibility_null() {
        val view = View(context)
        view.visibility = View.GONE
        BindingAdapters.setVisibility(view, null)
        assertEquals(View.VISIBLE, view.visibility)
    }

    @Test
    fun setVisibility_unknown() {
        val view = View(context)
        BindingAdapters.setVisibility(view, "unknown")
        assertEquals(View.VISIBLE, view.visibility)
    }

    @Test
    fun setVisibility_caseInsensitive() {
        val view = View(context)
        BindingAdapters.setVisibility(view, "GONE")
        assertEquals(View.GONE, view.visibility)
    }

    @Test
    fun setProgress_seekBar() {
        val seekBar = SeekBar(context)
        seekBar.max = 100
        BindingAdapters.setProgress(seekBar, 50.0)
        assertEquals(50, seekBar.progress)
    }

    @Test
    fun setProgress_progressBar() {
        val progressBar = ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal)
        progressBar.max = 100
        BindingAdapters.setProgress(progressBar, 75.0)
        assertEquals(75, progressBar.progress)
    }

    @Test
    fun setProgressView_seekBar() {
        val seekBar = SeekBar(context)
        seekBar.max = 100
        BindingAdapters.setProgressView(seekBar as View, 25.0)
        assertEquals(25, seekBar.progress)
    }

    @Test
    fun setProgressView_progressBar() {
        val progressBar = ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal)
        progressBar.max = 100
        BindingAdapters.setProgressView(progressBar as View, 60.0)
        assertEquals(60, progressBar.progress)
    }

    @Test
    fun setProgressView_genericView() {
        val view = View(context)
        // Should not crash for generic views
        BindingAdapters.setProgressView(view, 50.0)
    }

    @Test
    fun setTintColor_view() {
        val view = View(context)
        BindingAdapters.setTintColor(view, Color.Red)
        assertNotNull(view.backgroundTintList)
    }

    @Test
    fun setTintColor_view_null() {
        val view = View(context)
        BindingAdapters.setTintColor(view, null)
        // Should not crash
    }

    @Test
    fun setTintColor_imageView() {
        val imageView = ImageView(context)
        BindingAdapters.setTintColor(imageView, Color.Blue)
        // Color filter should be applied
    }

    @Test
    fun setTintColor_imageView_null() {
        val imageView = ImageView(context)
        BindingAdapters.setTintColor(imageView, null)
        // Should not crash
    }

    @Test
    fun setTintColorString_validColor() {
        val imageView = ImageView(context)
        BindingAdapters.setTintColorString(imageView, "#FF0000")
        // Color filter should be applied
    }

    @Test
    fun setTintColorString_invalidColor() {
        val imageView = ImageView(context)
        BindingAdapters.setTintColorString(imageView, "invalid")
        // Should not crash
    }

    @Test
    fun setTintColorString_null() {
        val imageView = ImageView(context)
        BindingAdapters.setTintColorString(imageView, null)
        // Should not crash
    }

    @Test
    fun setTintColorString_empty() {
        val imageView = ImageView(context)
        BindingAdapters.setTintColorString(imageView, "")
        // Should not crash
    }

    @Test
    fun setRecyclerViewItems_null() {
        val recyclerView = androidx.recyclerview.widget.RecyclerView(context)
        BindingAdapters.setRecyclerViewItems(recyclerView, null)
        // Should not crash
    }
}
