package com.kotlinjsonui.views

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class KjuiSafeAreaViewTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun kjuiSafeAreaView_initializes() {
        val view = KjuiSafeAreaView(context)
        assertNotNull(view)
    }

    @Test
    fun kjuiSafeAreaView_setSafeAreaInsetPositionsTop() {
        val view = KjuiSafeAreaView(context)
        view.setSafeAreaInsetPositions("top")
        assertNotNull(view)
    }

    @Test
    fun kjuiSafeAreaView_setSafeAreaInsetPositionsBottom() {
        val view = KjuiSafeAreaView(context)
        view.setSafeAreaInsetPositions("bottom")
        assertNotNull(view)
    }

    @Test
    fun kjuiSafeAreaView_setSafeAreaInsetPositionsLeft() {
        val view = KjuiSafeAreaView(context)
        view.setSafeAreaInsetPositions("left")
        assertNotNull(view)
    }

    @Test
    fun kjuiSafeAreaView_setSafeAreaInsetPositionsRight() {
        val view = KjuiSafeAreaView(context)
        view.setSafeAreaInsetPositions("right")
        assertNotNull(view)
    }

    @Test
    fun kjuiSafeAreaView_setSafeAreaInsetPositionsVertical() {
        val view = KjuiSafeAreaView(context)
        view.setSafeAreaInsetPositions("vertical")
        assertNotNull(view)
    }

    @Test
    fun kjuiSafeAreaView_setSafeAreaInsetPositionsHorizontal() {
        val view = KjuiSafeAreaView(context)
        view.setSafeAreaInsetPositions("horizontal")
        assertNotNull(view)
    }

    @Test
    fun kjuiSafeAreaView_setSafeAreaInsetPositionsAll() {
        val view = KjuiSafeAreaView(context)
        view.setSafeAreaInsetPositions("all")
        assertNotNull(view)
    }

    @Test
    fun kjuiSafeAreaView_setSafeAreaInsetPositionsMultiple() {
        val view = KjuiSafeAreaView(context)
        view.setSafeAreaInsetPositions("top", "bottom")
        assertNotNull(view)
    }

    @Test
    fun kjuiSafeAreaView_setSafeAreaInsetPositionsList() {
        val view = KjuiSafeAreaView(context)
        view.setSafeAreaInsetPositions(listOf("top", "left", "right"))
        assertNotNull(view)
    }

    @Test
    fun kjuiSafeAreaView_setInsetEnabledTop() {
        val view = KjuiSafeAreaView(context)
        view.setInsetEnabled("top", true)
        assertNotNull(view)
    }

    @Test
    fun kjuiSafeAreaView_setInsetEnabledBottom() {
        val view = KjuiSafeAreaView(context)
        view.setInsetEnabled("bottom", false)
        assertNotNull(view)
    }

    @Test
    fun kjuiSafeAreaView_setInsetEnabledStart() {
        val view = KjuiSafeAreaView(context)
        view.setInsetEnabled("start", true)
        assertNotNull(view)
    }

    @Test
    fun kjuiSafeAreaView_setInsetEnabledEnd() {
        val view = KjuiSafeAreaView(context)
        view.setInsetEnabled("end", true)
        assertNotNull(view)
    }

    @Test
    fun kjuiSafeAreaView_setInsetEnabledLeading() {
        val view = KjuiSafeAreaView(context)
        view.setInsetEnabled("leading", true)
        assertNotNull(view)
    }

    @Test
    fun kjuiSafeAreaView_setInsetEnabledTrailing() {
        val view = KjuiSafeAreaView(context)
        view.setInsetEnabled("trailing", true)
        assertNotNull(view)
    }

    @Test
    fun kjuiSafeAreaView_setInsetEnabledVertical() {
        val view = KjuiSafeAreaView(context)
        view.setInsetEnabled("vertical", true)
        assertNotNull(view)
    }

    @Test
    fun kjuiSafeAreaView_setInsetEnabledHorizontal() {
        val view = KjuiSafeAreaView(context)
        view.setInsetEnabled("horizontal", true)
        assertNotNull(view)
    }

    @Test
    fun kjuiSafeAreaView_setContentInsetAdjustmentBehaviorAlways() {
        val view = KjuiSafeAreaView(context)
        view.setContentInsetAdjustmentBehavior("always")
        assertNotNull(view)
    }

    @Test
    fun kjuiSafeAreaView_setContentInsetAdjustmentBehaviorNever() {
        val view = KjuiSafeAreaView(context)
        view.setContentInsetAdjustmentBehavior("never")
        assertNotNull(view)
    }

    @Test
    fun kjuiSafeAreaView_canAddChildViews() {
        val view = KjuiSafeAreaView(context)
        val child = android.widget.TextView(context)
        view.addView(child)
        assertEquals(1, view.childCount)
    }
}
