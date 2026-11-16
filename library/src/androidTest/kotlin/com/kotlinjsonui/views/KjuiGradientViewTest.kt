package com.kotlinjsonui.views

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class KjuiGradientViewTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun kjuiGradientView_initializes() {
        val view = KjuiGradientView(context)
        assertNotNull(view)
    }

    @Test
    fun kjuiGradientView_hasGradientBackground() {
        val view = KjuiGradientView(context)
        assertNotNull(view.background)
        assertTrue(view.background is GradientDrawable)
    }

    @Test
    fun kjuiGradientView_setGradientColors() {
        val view = KjuiGradientView(context)
        view.setGradientColors("#FF0000", "#00FF00")
        assertNotNull(view.background)
    }

    @Test
    fun kjuiGradientView_setGradientColorsMultiple() {
        val view = KjuiGradientView(context)
        view.setGradientColors("#FF0000", "#00FF00", "#0000FF")
        assertNotNull(view.background)
    }

    @Test
    fun kjuiGradientView_setGradientDirectionVertical() {
        val view = KjuiGradientView(context)
        view.setGradientDirection("vertical")
        assertNotNull(view.background)
    }

    @Test
    fun kjuiGradientView_setGradientDirectionHorizontal() {
        val view = KjuiGradientView(context)
        view.setGradientDirection("horizontal")
        assertNotNull(view.background)
    }

    @Test
    fun kjuiGradientView_setGradientDirectionDiagonal() {
        val view = KjuiGradientView(context)
        view.setGradientDirection("diagonal")
        assertNotNull(view.background)
    }

    @Test
    fun kjuiGradientView_setGradientDirectionDiagonalReverse() {
        val view = KjuiGradientView(context)
        view.setGradientDirection("diagonal_reverse")
        assertNotNull(view.background)
    }

    @Test
    fun kjuiGradientView_setGradientDirectionTopBottom() {
        val view = KjuiGradientView(context)
        view.setGradientDirection("top_bottom")
        assertNotNull(view.background)
    }

    @Test
    fun kjuiGradientView_setGradientDirectionBottomTop() {
        val view = KjuiGradientView(context)
        view.setGradientDirection("bottom_top")
        assertNotNull(view.background)
    }

    @Test
    fun kjuiGradientView_setGradientDirectionLeftRight() {
        val view = KjuiGradientView(context)
        view.setGradientDirection("left_right")
        assertNotNull(view.background)
    }

    @Test
    fun kjuiGradientView_setGradientDirectionRightLeft() {
        val view = KjuiGradientView(context)
        view.setGradientDirection("right_left")
        assertNotNull(view.background)
    }

    @Test
    fun kjuiGradientView_setGradientDirectionTlBr() {
        val view = KjuiGradientView(context)
        view.setGradientDirection("tl_br")
        assertNotNull(view.background)
    }

    @Test
    fun kjuiGradientView_setGradientDirectionTrBl() {
        val view = KjuiGradientView(context)
        view.setGradientDirection("tr_bl")
        assertNotNull(view.background)
    }

    @Test
    fun kjuiGradientView_setGradientDirectionBlTr() {
        val view = KjuiGradientView(context)
        view.setGradientDirection("bl_tr")
        assertNotNull(view.background)
    }

    @Test
    fun kjuiGradientView_setGradientDirectionBrTl() {
        val view = KjuiGradientView(context)
        view.setGradientDirection("br_tl")
        assertNotNull(view.background)
    }

    @Test
    fun kjuiGradientView_setGradientDirectionUnknown() {
        val view = KjuiGradientView(context)
        view.setGradientDirection("unknown")
        assertNotNull(view.background)
    }

    @Test
    fun kjuiGradientView_setGradientTypeLinear() {
        val view = KjuiGradientView(context)
        view.setGradientType("linear")
        assertNotNull(view.background)
    }

    @Test
    fun kjuiGradientView_setGradientTypeRadial() {
        val view = KjuiGradientView(context)
        view.setGradientType("radial")
        assertNotNull(view.background)
    }

    @Test
    fun kjuiGradientView_setGradientTypeSweep() {
        val view = KjuiGradientView(context)
        view.setGradientType("sweep")
        assertNotNull(view.background)
    }

    @Test
    fun kjuiGradientView_setGradientTypeUnknown() {
        val view = KjuiGradientView(context)
        view.setGradientType("unknown")
        assertNotNull(view.background)
    }

    @Test
    fun kjuiGradientView_canAddChildViews() {
        val view = KjuiGradientView(context)
        val child = android.widget.TextView(context)
        view.addView(child)
        assertEquals(1, view.childCount)
    }
}
