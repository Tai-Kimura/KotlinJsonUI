package com.kotlinjsonui.views

import android.content.Context
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.widget.FrameLayout
import com.kotlinjsonui.R

/**
 * A FrameLayout that displays a gradient background
 * Can contain child views that appear on top of the gradient
 */
class KjuiGradientView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    
    private var gradientDrawable = GradientDrawable()
    
    // Gradient colors
    private var gradientColors: IntArray = intArrayOf(Color.WHITE, Color.BLACK)
        set(value) {
            field = value
            updateGradient()
        }
    
    // Gradient direction/orientation
    private var gradientOrientation: GradientDrawable.Orientation = GradientDrawable.Orientation.TOP_BOTTOM
        set(value) {
            field = value
            updateGradient()
        }
    
    // Gradient type: linear, radial, sweep
    private var gradientType: Int = GradientDrawable.LINEAR_GRADIENT
        set(value) {
            field = value
            updateGradient()
        }
    
    // Gradient radius for radial gradient
    private var gradientRadius: Float = 100f
        set(value) {
            field = value
            updateGradient()
        }
    
    // Center position for radial/sweep gradient
    private var centerX: Float = 0.5f
        set(value) {
            field = value
            updateGradient()
        }
    
    private var centerY: Float = 0.5f
        set(value) {
            field = value
            updateGradient()
        }
    
    // Corner radius
    private var cornerRadius: Float = 0f
        set(value) {
            field = value
            updateGradient()
        }
    
    // Angle for linear gradient (alternative to orientation)
    private var gradientAngle: Int = 0
        set(value) {
            field = value
            gradientOrientation = angleToOrientation(value)
            updateGradient()
        }
    
    init {
        // Process custom attributes
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.KjuiGradientView, defStyleAttr, 0)
            try {
                // Get colors
                val startColor = typedArray.getColor(R.styleable.KjuiGradientView_gradientStartColor, Color.WHITE)
                val endColor = typedArray.getColor(R.styleable.KjuiGradientView_gradientEndColor, Color.BLACK)
                val centerColor = typedArray.getColor(R.styleable.KjuiGradientView_gradientCenterColor, Int.MIN_VALUE)
                
                gradientColors = if (centerColor != Int.MIN_VALUE) {
                    intArrayOf(startColor, centerColor, endColor)
                } else {
                    intArrayOf(startColor, endColor)
                }
                
                // Get colors array if provided
                val colorsArrayId = typedArray.getResourceId(R.styleable.KjuiGradientView_gradientColors, 0)
                if (colorsArrayId != 0) {
                    val colorsArray = resources.getIntArray(colorsArrayId)
                    if (colorsArray.isNotEmpty()) {
                        gradientColors = colorsArray
                    }
                }
                
                // Get orientation/direction
                val orientationInt = typedArray.getInt(R.styleable.KjuiGradientView_gradientOrientation, 0)
                gradientOrientation = when (orientationInt) {
                    1 -> GradientDrawable.Orientation.TR_BL
                    2 -> GradientDrawable.Orientation.RIGHT_LEFT
                    3 -> GradientDrawable.Orientation.BR_TL
                    4 -> GradientDrawable.Orientation.BOTTOM_TOP
                    5 -> GradientDrawable.Orientation.BL_TR
                    6 -> GradientDrawable.Orientation.LEFT_RIGHT
                    7 -> GradientDrawable.Orientation.TL_BR
                    else -> GradientDrawable.Orientation.TOP_BOTTOM
                }
                
                // Get angle if specified
                val angle = typedArray.getInt(R.styleable.KjuiGradientView_gradientAngle, -1)
                if (angle >= 0) {
                    gradientAngle = angle
                }
                
                // Get gradient type
                gradientType = typedArray.getInt(R.styleable.KjuiGradientView_gradientType, GradientDrawable.LINEAR_GRADIENT)
                
                // Get radial gradient properties
                gradientRadius = typedArray.getFloat(R.styleable.KjuiGradientView_gradientRadius, 100f)
                centerX = typedArray.getFloat(R.styleable.KjuiGradientView_gradientCenterX, 0.5f)
                centerY = typedArray.getFloat(R.styleable.KjuiGradientView_gradientCenterY, 0.5f)
                
                // Corner radius is handled by background drawable in XML generation
                // Not needed at runtime since we use GradientDrawable
                
            } finally {
                typedArray.recycle()
            }
        }
        
        updateGradient()
    }
    
    private fun updateGradient() {
        gradientDrawable.apply {
            colors = gradientColors
            orientation = gradientOrientation
            gradientType = this@KjuiGradientView.gradientType
            
            // Set corner radius
            if (cornerRadius > 0) {
                setCornerRadius(cornerRadius)
            }
            
            // Set gradient center and radius for radial/sweep gradients
            if (gradientType == GradientDrawable.RADIAL_GRADIENT) {
                gradientRadius = this@KjuiGradientView.gradientRadius
                setGradientCenter(centerX, centerY)
            } else if (gradientType == GradientDrawable.SWEEP_GRADIENT) {
                setGradientCenter(centerX, centerY)
            }
        }
        
        background = gradientDrawable
    }
    
    private fun angleToOrientation(angle: Int): GradientDrawable.Orientation {
        val normalizedAngle = ((angle % 360) + 360) % 360
        return when (normalizedAngle) {
            0, 360 -> GradientDrawable.Orientation.LEFT_RIGHT
            45 -> GradientDrawable.Orientation.BL_TR
            90 -> GradientDrawable.Orientation.BOTTOM_TOP
            135 -> GradientDrawable.Orientation.BR_TL
            180 -> GradientDrawable.Orientation.RIGHT_LEFT
            225 -> GradientDrawable.Orientation.TR_BL
            270 -> GradientDrawable.Orientation.TOP_BOTTOM
            315 -> GradientDrawable.Orientation.TL_BR
            else -> GradientDrawable.Orientation.TOP_BOTTOM // Default for non-standard angles
        }
    }
    
    
    /**
     * Set gradient colors from color strings
     */
    fun setGradientColors(vararg colorStrings: String) {
        gradientColors = colorStrings.map { Color.parseColor(it) }.toIntArray()
    }
    
    /**
     * Set gradient direction
     */
    fun setGradientDirection(direction: String) {
        gradientOrientation = when (direction.lowercase()) {
            "vertical", "top_bottom" -> GradientDrawable.Orientation.TOP_BOTTOM
            "horizontal", "left_right" -> GradientDrawable.Orientation.LEFT_RIGHT
            "diagonal", "tl_br" -> GradientDrawable.Orientation.TL_BR
            "diagonal_reverse", "tr_bl" -> GradientDrawable.Orientation.TR_BL
            "bottom_top" -> GradientDrawable.Orientation.BOTTOM_TOP
            "right_left" -> GradientDrawable.Orientation.RIGHT_LEFT
            "bl_tr" -> GradientDrawable.Orientation.BL_TR
            "br_tl" -> GradientDrawable.Orientation.BR_TL
            else -> GradientDrawable.Orientation.TOP_BOTTOM
        }
    }
    
    /**
     * Set gradient type
     */
    fun setGradientType(type: String) {
        gradientType = when (type.lowercase()) {
            "linear" -> GradientDrawable.LINEAR_GRADIENT
            "radial" -> GradientDrawable.RADIAL_GRADIENT
            "sweep" -> GradientDrawable.SWEEP_GRADIENT
            else -> GradientDrawable.LINEAR_GRADIENT
        }
    }
}