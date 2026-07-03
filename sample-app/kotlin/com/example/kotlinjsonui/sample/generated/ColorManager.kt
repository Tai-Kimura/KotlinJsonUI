// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    ColorManager (colors from colors.json)
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.kotlinjsonui.generated

import android.graphics.Color
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color as ComposeColor

object ColorManager {
    private const val TAG = "ColorManager"

    /** Color modes discovered in colors.json. Add a new top-level mode object there to grow this list. */
    enum class ColorMode(val raw: String) {
        LIGHT("light");

        companion object {
            fun fromRaw(raw: String): ColorMode? = values().firstOrNull { it.raw == raw }
        }
    }

    val fallbackMode: ColorMode = ColorMode.LIGHT

    /** Map from OS appearance (light/dark) to project-specific mode. */
    val systemModeMapping: Map<String, ColorMode> = mapOf(
        "light" to ColorMode.LIGHT
    )

    /** Raw hex palette per mode (snake_case keys). */
    private val palettes: Map<ColorMode, Map<String, String>> = mapOf(
        ColorMode.LIGHT to mapOf(
            "black" to "#000000",
            "black_2" to "#00000033",
            "dark_blue" to "#0000FF",
            "dark_gray" to "#333333",
            "dark_green" to "#2E7D32",
            "dark_green_2" to "#00FF00",
            "dark_green_3" to "#00C853",
            "dark_purple" to "#6A1B9A",
            "dark_red" to "#FF0000",
            "light" to "#FF00FF",
            "light_2" to "#FFFF00",
            "light_blue" to "#6666FF",
            "light_blue_2" to "#A29BFE",
            "light_blue_3" to "#A0A0FF",
            "light_cyan" to "#45B7D1",
            "light_cyan_2" to "#5AC8FA",
            "light_gray" to "#C8C8C8",
            "light_gray_10" to "#BBBBBB",
            "light_gray_2" to "#C0C0C0",
            "light_gray_3" to "#B8B8B8",
            "light_gray_4" to "#B0B0B0",
            "light_gray_5" to "#A8A8A8",
            "light_gray_6" to "#A0A0A0",
            "light_gray_7" to "#989898",
            "light_gray_8" to "#999999",
            "light_gray_9" to "#AAAAAA",
            "light_green" to "#66FF66",
            "light_green_2" to "#A0FFA0",
            "light_lime" to "#4ECDC4",
            "light_orange" to "#FFD700",
            "light_orange_2" to "#FFD93D",
            "light_purple" to "#AF52DE",
            "light_red" to "#FF6B6B",
            "light_red_2" to "#FF6666",
            "light_red_3" to "#FD79A8",
            "light_red_4" to "#FAB1A0",
            "light_red_5" to "#FF6B9D",
            "light_red_6" to "#FFA0A0",
            "light_yellow" to "#96CEB4",
            "medium_blue" to "#007AFF",
            "medium_blue_2" to "#2196F3",
            "medium_blue_3" to "#5856D6",
            "medium_blue_4" to "#0066CC",
            "medium_blue_5" to "#6C5CE7",
            "medium_cyan" to "#607D8B",
            "medium_gray" to "#909090",
            "medium_gray_2" to "#888888",
            "medium_gray_3" to "#808080",
            "medium_gray_4" to "#666666",
            "medium_gray_5" to "#636E72",
            "medium_green" to "#34C759",
            "medium_green_2" to "#4CAF50",
            "medium_green_3" to "#6BCB77",
            "medium_lime" to "#00B894",
            "medium_lime_2" to "#00CEC9",
            "medium_purple" to "#9C27B0",
            "medium_red" to "#FF3B30",
            "medium_red_2" to "#FF5722",
            "medium_red_3" to "#FF9500",
            "medium_red_4" to "#E65100",
            "medium_red_5" to "#FF9800",
            "medium_red_6" to "#FF6600",
            "pale" to "#FFFFA0",
            "pale_blue" to "#B0B0FF",
            "pale_blue_2" to "#C0C0FF",
            "pale_blue_3" to "#CCCCFF",
            "pale_cyan" to "#D0D0FF",
            "pale_gray" to "#E0E0E0",
            "pale_gray_2" to "#D8D8D8",
            "pale_gray_3" to "#D0D0D0",
            "pale_gray_4" to "#CCCCCC",
            "pale_gray_5" to "#DDDDDD",
            "pale_gray_6" to "#DFE6E9",
            "pale_green" to "#CCFFCC",
            "pale_green_2" to "#B0FFB0",
            "pale_green_3" to "#C0FFC0",
            "pale_pink" to "#FFD0D0",
            "pale_pink_2" to "#FFEAA7",
            "pale_red" to "#FFCCCC",
            "pale_red_2" to "#FFB0B0",
            "pale_red_3" to "#FFC0C0",
            "pale_yellow" to "#D0FFD0",
            "white" to "#FFFFFF",
            "white_10" to "#FFE8E8",
            "white_11" to "#E8FFE8",
            "white_12" to "#F5F5F7",
            "white_13" to "#E8F5E9",
            "white_14" to "#FFF3E0",
            "white_15" to "#F3E5F5",
            "white_16" to "#E3F2FD",
            "white_17" to "#F0F0F0",
            "white_18" to "#FFFFCC",
            "white_19" to "#FFE5E5",
            "white_2" to "#FFFFD0",
            "white_20" to "#E8F4FD",
            "white_21" to "#FFF4E6",
            "white_22" to "#E0FFFF",
            "white_23" to "#F5F5F5",
            "white_24" to "#E8E8E8",
            "white_25" to "#FFF0E0",
            "white_26" to "#F0E0FF",
            "white_27" to "#FAFAFA",
            "white_28" to "#F9F9F9",
            "white_3" to "#FFD0FF",
            "white_4" to "#D0FFFF",
            "white_5" to "#FFE0E0",
            "white_6" to "#E0FFE0",
            "white_7" to "#E0E0FF",
            "white_8" to "#FFFFE0",
            "white_9" to "#FFE0FF"
        )
    )

    /** Reactive current-mode holder. Reading `value` from @Composable code triggers recomposition. */
    private val _currentMode: MutableState<ColorMode> = mutableStateOf(fallbackMode)
    val currentMode: ColorMode get() = _currentMode.value

    private val observers: MutableMap<Any, () -> Unit> = mutableMapOf()
    private var followSystemMode: Boolean = true

    fun setMode(mode: ColorMode) {
        if (_currentMode.value == mode) return
        _currentMode.value = mode
        observers.values.forEach { it.invoke() }
    }

    fun setFollowSystemMode(follow: Boolean) { followSystemMode = follow }
    fun isFollowingSystemMode(): Boolean = followSystemMode

    /** Subscribe with any Any key; returns a closure that unsubscribes. */
    fun subscribe(key: Any, callback: () -> Unit): () -> Unit {
        observers[key] = callback
        return { observers.remove(key) }
    }

    /** Call from Activity/Application when night mode changes, using Configuration.uiMode. */
    fun applySystemMode(osMode: String) {
        if (!followSystemMode) return
        systemModeMapping[osMode]?.let { setMode(it) }
    }

    // ========== Android Views ==========
    object views {
        fun color(key: String): Int? {
            if (key.startsWith("@{") && key.endsWith("}")) return null
            val mode = currentMode
            val hex = palettes[mode]?.get(key) ?: palettes[fallbackMode]?.get(key)
            if (hex != null) {
                return try { Color.parseColor(hex) }
                catch (e: IllegalArgumentException) { Log.w(TAG, "Invalid hex '$hex' for key '$key'"); null }
            }
            return try { Color.parseColor(key) } catch (e: IllegalArgumentException) { null }
        }

        val Color.Green: Int? get() = color("Color.Green")
        val black: Int? get() = color("black")
        val black2: Int? get() = color("black_2")
        val darkBlue: Int? get() = color("dark_blue")
        val darkGray: Int? get() = color("dark_gray")
        val darkGreen: Int? get() = color("dark_green")
        val darkGreen2: Int? get() = color("dark_green_2")
        val darkGreen3: Int? get() = color("dark_green_3")
        val darkPurple: Int? get() = color("dark_purple")
        val darkRed: Int? get() = color("dark_red")
        val light: Int? get() = color("light")
        val light2: Int? get() = color("light_2")
        val lightBlue: Int? get() = color("light_blue")
        val lightBlue2: Int? get() = color("light_blue_2")
        val lightBlue3: Int? get() = color("light_blue_3")
        val lightCyan: Int? get() = color("light_cyan")
        val lightCyan2: Int? get() = color("light_cyan_2")
        val lightGray: Int? get() = color("light_gray")
        val lightGray10: Int? get() = color("light_gray_10")
        val lightGray2: Int? get() = color("light_gray_2")
        val lightGray3: Int? get() = color("light_gray_3")
        val lightGray4: Int? get() = color("light_gray_4")
        val lightGray5: Int? get() = color("light_gray_5")
        val lightGray6: Int? get() = color("light_gray_6")
        val lightGray7: Int? get() = color("light_gray_7")
        val lightGray8: Int? get() = color("light_gray_8")
        val lightGray9: Int? get() = color("light_gray_9")
        val lightGreen: Int? get() = color("light_green")
        val lightGreen2: Int? get() = color("light_green_2")
        val lightLime: Int? get() = color("light_lime")
        val lightOrange: Int? get() = color("light_orange")
        val lightOrange2: Int? get() = color("light_orange_2")
        val lightPurple: Int? get() = color("light_purple")
        val lightRed: Int? get() = color("light_red")
        val lightRed2: Int? get() = color("light_red_2")
        val lightRed3: Int? get() = color("light_red_3")
        val lightRed4: Int? get() = color("light_red_4")
        val lightRed5: Int? get() = color("light_red_5")
        val lightRed6: Int? get() = color("light_red_6")
        val lightYellow: Int? get() = color("light_yellow")
        val mediumBlue: Int? get() = color("medium_blue")
        val mediumBlue2: Int? get() = color("medium_blue_2")
        val mediumBlue3: Int? get() = color("medium_blue_3")
        val mediumBlue4: Int? get() = color("medium_blue_4")
        val mediumBlue5: Int? get() = color("medium_blue_5")
        val mediumCyan: Int? get() = color("medium_cyan")
        val mediumGray: Int? get() = color("medium_gray")
        val mediumGray2: Int? get() = color("medium_gray_2")
        val mediumGray3: Int? get() = color("medium_gray_3")
        val mediumGray4: Int? get() = color("medium_gray_4")
        val mediumGray5: Int? get() = color("medium_gray_5")
        val mediumGreen: Int? get() = color("medium_green")
        val mediumGreen2: Int? get() = color("medium_green_2")
        val mediumGreen3: Int? get() = color("medium_green_3")
        val mediumLime: Int? get() = color("medium_lime")
        val mediumLime2: Int? get() = color("medium_lime_2")
        val mediumPurple: Int? get() = color("medium_purple")
        val mediumRed: Int? get() = color("medium_red")
        val mediumRed2: Int? get() = color("medium_red_2")
        val mediumRed3: Int? get() = color("medium_red_3")
        val mediumRed4: Int? get() = color("medium_red_4")
        val mediumRed5: Int? get() = color("medium_red_5")
        val mediumRed6: Int? get() = color("medium_red_6")
        val pale: Int? get() = color("pale")
        val paleBlue: Int? get() = color("pale_blue")
        val paleBlue2: Int? get() = color("pale_blue_2")
        val paleBlue3: Int? get() = color("pale_blue_3")
        val paleCyan: Int? get() = color("pale_cyan")
        val paleGray: Int? get() = color("pale_gray")
        val paleGray2: Int? get() = color("pale_gray_2")
        val paleGray3: Int? get() = color("pale_gray_3")
        val paleGray4: Int? get() = color("pale_gray_4")
        val paleGray5: Int? get() = color("pale_gray_5")
        val paleGray6: Int? get() = color("pale_gray_6")
        val paleGreen: Int? get() = color("pale_green")
        val paleGreen2: Int? get() = color("pale_green_2")
        val paleGreen3: Int? get() = color("pale_green_3")
        val palePink: Int? get() = color("pale_pink")
        val palePink2: Int? get() = color("pale_pink_2")
        val paleRed: Int? get() = color("pale_red")
        val paleRed2: Int? get() = color("pale_red_2")
        val paleRed3: Int? get() = color("pale_red_3")
        val paleYellow: Int? get() = color("pale_yellow")
        val white: Int? get() = color("white")
        val white10: Int? get() = color("white_10")
        val white11: Int? get() = color("white_11")
        val white12: Int? get() = color("white_12")
        val white13: Int? get() = color("white_13")
        val white14: Int? get() = color("white_14")
        val white15: Int? get() = color("white_15")
        val white16: Int? get() = color("white_16")
        val white17: Int? get() = color("white_17")
        val white18: Int? get() = color("white_18")
        val white19: Int? get() = color("white_19")
        val white2: Int? get() = color("white_2")
        val white20: Int? get() = color("white_20")
        val white21: Int? get() = color("white_21")
        val white22: Int? get() = color("white_22")
        val white23: Int? get() = color("white_23")
        val white24: Int? get() = color("white_24")
        val white25: Int? get() = color("white_25")
        val white26: Int? get() = color("white_26")
        val white27: Int? get() = color("white_27")
        val white28: Int? get() = color("white_28")
        val white3: Int? get() = color("white_3")
        val white4: Int? get() = color("white_4")
        val white5: Int? get() = color("white_5")
        val white6: Int? get() = color("white_6")
        val white7: Int? get() = color("white_7")
        val white8: Int? get() = color("white_8")
        val white9: Int? get() = color("white_9")

        /** Fixed values from `light` palette (not affected by setMode). */
        object light {
            val Color.Green: Int? get() = null
            val black: Int? get() = try { Color.parseColor("#000000") } catch (e: IllegalArgumentException) { null }
            val black2: Int? get() = try { Color.parseColor("#00000033") } catch (e: IllegalArgumentException) { null }
            val darkBlue: Int? get() = try { Color.parseColor("#0000FF") } catch (e: IllegalArgumentException) { null }
            val darkGray: Int? get() = try { Color.parseColor("#333333") } catch (e: IllegalArgumentException) { null }
            val darkGreen: Int? get() = try { Color.parseColor("#2E7D32") } catch (e: IllegalArgumentException) { null }
            val darkGreen2: Int? get() = try { Color.parseColor("#00FF00") } catch (e: IllegalArgumentException) { null }
            val darkGreen3: Int? get() = try { Color.parseColor("#00C853") } catch (e: IllegalArgumentException) { null }
            val darkPurple: Int? get() = try { Color.parseColor("#6A1B9A") } catch (e: IllegalArgumentException) { null }
            val darkRed: Int? get() = try { Color.parseColor("#FF0000") } catch (e: IllegalArgumentException) { null }
            val light: Int? get() = try { Color.parseColor("#FF00FF") } catch (e: IllegalArgumentException) { null }
            val light2: Int? get() = try { Color.parseColor("#FFFF00") } catch (e: IllegalArgumentException) { null }
            val lightBlue: Int? get() = try { Color.parseColor("#6666FF") } catch (e: IllegalArgumentException) { null }
            val lightBlue2: Int? get() = try { Color.parseColor("#A29BFE") } catch (e: IllegalArgumentException) { null }
            val lightBlue3: Int? get() = try { Color.parseColor("#A0A0FF") } catch (e: IllegalArgumentException) { null }
            val lightCyan: Int? get() = try { Color.parseColor("#45B7D1") } catch (e: IllegalArgumentException) { null }
            val lightCyan2: Int? get() = try { Color.parseColor("#5AC8FA") } catch (e: IllegalArgumentException) { null }
            val lightGray: Int? get() = try { Color.parseColor("#C8C8C8") } catch (e: IllegalArgumentException) { null }
            val lightGray10: Int? get() = try { Color.parseColor("#BBBBBB") } catch (e: IllegalArgumentException) { null }
            val lightGray2: Int? get() = try { Color.parseColor("#C0C0C0") } catch (e: IllegalArgumentException) { null }
            val lightGray3: Int? get() = try { Color.parseColor("#B8B8B8") } catch (e: IllegalArgumentException) { null }
            val lightGray4: Int? get() = try { Color.parseColor("#B0B0B0") } catch (e: IllegalArgumentException) { null }
            val lightGray5: Int? get() = try { Color.parseColor("#A8A8A8") } catch (e: IllegalArgumentException) { null }
            val lightGray6: Int? get() = try { Color.parseColor("#A0A0A0") } catch (e: IllegalArgumentException) { null }
            val lightGray7: Int? get() = try { Color.parseColor("#989898") } catch (e: IllegalArgumentException) { null }
            val lightGray8: Int? get() = try { Color.parseColor("#999999") } catch (e: IllegalArgumentException) { null }
            val lightGray9: Int? get() = try { Color.parseColor("#AAAAAA") } catch (e: IllegalArgumentException) { null }
            val lightGreen: Int? get() = try { Color.parseColor("#66FF66") } catch (e: IllegalArgumentException) { null }
            val lightGreen2: Int? get() = try { Color.parseColor("#A0FFA0") } catch (e: IllegalArgumentException) { null }
            val lightLime: Int? get() = try { Color.parseColor("#4ECDC4") } catch (e: IllegalArgumentException) { null }
            val lightOrange: Int? get() = try { Color.parseColor("#FFD700") } catch (e: IllegalArgumentException) { null }
            val lightOrange2: Int? get() = try { Color.parseColor("#FFD93D") } catch (e: IllegalArgumentException) { null }
            val lightPurple: Int? get() = try { Color.parseColor("#AF52DE") } catch (e: IllegalArgumentException) { null }
            val lightRed: Int? get() = try { Color.parseColor("#FF6B6B") } catch (e: IllegalArgumentException) { null }
            val lightRed2: Int? get() = try { Color.parseColor("#FF6666") } catch (e: IllegalArgumentException) { null }
            val lightRed3: Int? get() = try { Color.parseColor("#FD79A8") } catch (e: IllegalArgumentException) { null }
            val lightRed4: Int? get() = try { Color.parseColor("#FAB1A0") } catch (e: IllegalArgumentException) { null }
            val lightRed5: Int? get() = try { Color.parseColor("#FF6B9D") } catch (e: IllegalArgumentException) { null }
            val lightRed6: Int? get() = try { Color.parseColor("#FFA0A0") } catch (e: IllegalArgumentException) { null }
            val lightYellow: Int? get() = try { Color.parseColor("#96CEB4") } catch (e: IllegalArgumentException) { null }
            val mediumBlue: Int? get() = try { Color.parseColor("#007AFF") } catch (e: IllegalArgumentException) { null }
            val mediumBlue2: Int? get() = try { Color.parseColor("#2196F3") } catch (e: IllegalArgumentException) { null }
            val mediumBlue3: Int? get() = try { Color.parseColor("#5856D6") } catch (e: IllegalArgumentException) { null }
            val mediumBlue4: Int? get() = try { Color.parseColor("#0066CC") } catch (e: IllegalArgumentException) { null }
            val mediumBlue5: Int? get() = try { Color.parseColor("#6C5CE7") } catch (e: IllegalArgumentException) { null }
            val mediumCyan: Int? get() = try { Color.parseColor("#607D8B") } catch (e: IllegalArgumentException) { null }
            val mediumGray: Int? get() = try { Color.parseColor("#909090") } catch (e: IllegalArgumentException) { null }
            val mediumGray2: Int? get() = try { Color.parseColor("#888888") } catch (e: IllegalArgumentException) { null }
            val mediumGray3: Int? get() = try { Color.parseColor("#808080") } catch (e: IllegalArgumentException) { null }
            val mediumGray4: Int? get() = try { Color.parseColor("#666666") } catch (e: IllegalArgumentException) { null }
            val mediumGray5: Int? get() = try { Color.parseColor("#636E72") } catch (e: IllegalArgumentException) { null }
            val mediumGreen: Int? get() = try { Color.parseColor("#34C759") } catch (e: IllegalArgumentException) { null }
            val mediumGreen2: Int? get() = try { Color.parseColor("#4CAF50") } catch (e: IllegalArgumentException) { null }
            val mediumGreen3: Int? get() = try { Color.parseColor("#6BCB77") } catch (e: IllegalArgumentException) { null }
            val mediumLime: Int? get() = try { Color.parseColor("#00B894") } catch (e: IllegalArgumentException) { null }
            val mediumLime2: Int? get() = try { Color.parseColor("#00CEC9") } catch (e: IllegalArgumentException) { null }
            val mediumPurple: Int? get() = try { Color.parseColor("#9C27B0") } catch (e: IllegalArgumentException) { null }
            val mediumRed: Int? get() = try { Color.parseColor("#FF3B30") } catch (e: IllegalArgumentException) { null }
            val mediumRed2: Int? get() = try { Color.parseColor("#FF5722") } catch (e: IllegalArgumentException) { null }
            val mediumRed3: Int? get() = try { Color.parseColor("#FF9500") } catch (e: IllegalArgumentException) { null }
            val mediumRed4: Int? get() = try { Color.parseColor("#E65100") } catch (e: IllegalArgumentException) { null }
            val mediumRed5: Int? get() = try { Color.parseColor("#FF9800") } catch (e: IllegalArgumentException) { null }
            val mediumRed6: Int? get() = try { Color.parseColor("#FF6600") } catch (e: IllegalArgumentException) { null }
            val pale: Int? get() = try { Color.parseColor("#FFFFA0") } catch (e: IllegalArgumentException) { null }
            val paleBlue: Int? get() = try { Color.parseColor("#B0B0FF") } catch (e: IllegalArgumentException) { null }
            val paleBlue2: Int? get() = try { Color.parseColor("#C0C0FF") } catch (e: IllegalArgumentException) { null }
            val paleBlue3: Int? get() = try { Color.parseColor("#CCCCFF") } catch (e: IllegalArgumentException) { null }
            val paleCyan: Int? get() = try { Color.parseColor("#D0D0FF") } catch (e: IllegalArgumentException) { null }
            val paleGray: Int? get() = try { Color.parseColor("#E0E0E0") } catch (e: IllegalArgumentException) { null }
            val paleGray2: Int? get() = try { Color.parseColor("#D8D8D8") } catch (e: IllegalArgumentException) { null }
            val paleGray3: Int? get() = try { Color.parseColor("#D0D0D0") } catch (e: IllegalArgumentException) { null }
            val paleGray4: Int? get() = try { Color.parseColor("#CCCCCC") } catch (e: IllegalArgumentException) { null }
            val paleGray5: Int? get() = try { Color.parseColor("#DDDDDD") } catch (e: IllegalArgumentException) { null }
            val paleGray6: Int? get() = try { Color.parseColor("#DFE6E9") } catch (e: IllegalArgumentException) { null }
            val paleGreen: Int? get() = try { Color.parseColor("#CCFFCC") } catch (e: IllegalArgumentException) { null }
            val paleGreen2: Int? get() = try { Color.parseColor("#B0FFB0") } catch (e: IllegalArgumentException) { null }
            val paleGreen3: Int? get() = try { Color.parseColor("#C0FFC0") } catch (e: IllegalArgumentException) { null }
            val palePink: Int? get() = try { Color.parseColor("#FFD0D0") } catch (e: IllegalArgumentException) { null }
            val palePink2: Int? get() = try { Color.parseColor("#FFEAA7") } catch (e: IllegalArgumentException) { null }
            val paleRed: Int? get() = try { Color.parseColor("#FFCCCC") } catch (e: IllegalArgumentException) { null }
            val paleRed2: Int? get() = try { Color.parseColor("#FFB0B0") } catch (e: IllegalArgumentException) { null }
            val paleRed3: Int? get() = try { Color.parseColor("#FFC0C0") } catch (e: IllegalArgumentException) { null }
            val paleYellow: Int? get() = try { Color.parseColor("#D0FFD0") } catch (e: IllegalArgumentException) { null }
            val white: Int? get() = try { Color.parseColor("#FFFFFF") } catch (e: IllegalArgumentException) { null }
            val white10: Int? get() = try { Color.parseColor("#FFE8E8") } catch (e: IllegalArgumentException) { null }
            val white11: Int? get() = try { Color.parseColor("#E8FFE8") } catch (e: IllegalArgumentException) { null }
            val white12: Int? get() = try { Color.parseColor("#F5F5F7") } catch (e: IllegalArgumentException) { null }
            val white13: Int? get() = try { Color.parseColor("#E8F5E9") } catch (e: IllegalArgumentException) { null }
            val white14: Int? get() = try { Color.parseColor("#FFF3E0") } catch (e: IllegalArgumentException) { null }
            val white15: Int? get() = try { Color.parseColor("#F3E5F5") } catch (e: IllegalArgumentException) { null }
            val white16: Int? get() = try { Color.parseColor("#E3F2FD") } catch (e: IllegalArgumentException) { null }
            val white17: Int? get() = try { Color.parseColor("#F0F0F0") } catch (e: IllegalArgumentException) { null }
            val white18: Int? get() = try { Color.parseColor("#FFFFCC") } catch (e: IllegalArgumentException) { null }
            val white19: Int? get() = try { Color.parseColor("#FFE5E5") } catch (e: IllegalArgumentException) { null }
            val white2: Int? get() = try { Color.parseColor("#FFFFD0") } catch (e: IllegalArgumentException) { null }
            val white20: Int? get() = try { Color.parseColor("#E8F4FD") } catch (e: IllegalArgumentException) { null }
            val white21: Int? get() = try { Color.parseColor("#FFF4E6") } catch (e: IllegalArgumentException) { null }
            val white22: Int? get() = try { Color.parseColor("#E0FFFF") } catch (e: IllegalArgumentException) { null }
            val white23: Int? get() = try { Color.parseColor("#F5F5F5") } catch (e: IllegalArgumentException) { null }
            val white24: Int? get() = try { Color.parseColor("#E8E8E8") } catch (e: IllegalArgumentException) { null }
            val white25: Int? get() = try { Color.parseColor("#FFF0E0") } catch (e: IllegalArgumentException) { null }
            val white26: Int? get() = try { Color.parseColor("#F0E0FF") } catch (e: IllegalArgumentException) { null }
            val white27: Int? get() = try { Color.parseColor("#FAFAFA") } catch (e: IllegalArgumentException) { null }
            val white28: Int? get() = try { Color.parseColor("#F9F9F9") } catch (e: IllegalArgumentException) { null }
            val white3: Int? get() = try { Color.parseColor("#FFD0FF") } catch (e: IllegalArgumentException) { null }
            val white4: Int? get() = try { Color.parseColor("#D0FFFF") } catch (e: IllegalArgumentException) { null }
            val white5: Int? get() = try { Color.parseColor("#FFE0E0") } catch (e: IllegalArgumentException) { null }
            val white6: Int? get() = try { Color.parseColor("#E0FFE0") } catch (e: IllegalArgumentException) { null }
            val white7: Int? get() = try { Color.parseColor("#E0E0FF") } catch (e: IllegalArgumentException) { null }
            val white8: Int? get() = try { Color.parseColor("#FFFFE0") } catch (e: IllegalArgumentException) { null }
            val white9: Int? get() = try { Color.parseColor("#FFE0FF") } catch (e: IllegalArgumentException) { null }
        }
    }

    // ========== Jetpack Compose ==========
    object compose {
        /** Reading this from @Composable code triggers recomposition on mode change. */
        fun color(key: String): ComposeColor? {
            val int = views.color(key) ?: return null
            return ComposeColor(int)
        }

        val Color.Green: ComposeColor? get() = color("Color.Green")
        val black: ComposeColor? get() = color("black")
        val black2: ComposeColor? get() = color("black_2")
        val darkBlue: ComposeColor? get() = color("dark_blue")
        val darkGray: ComposeColor? get() = color("dark_gray")
        val darkGreen: ComposeColor? get() = color("dark_green")
        val darkGreen2: ComposeColor? get() = color("dark_green_2")
        val darkGreen3: ComposeColor? get() = color("dark_green_3")
        val darkPurple: ComposeColor? get() = color("dark_purple")
        val darkRed: ComposeColor? get() = color("dark_red")
        val light: ComposeColor? get() = color("light")
        val light2: ComposeColor? get() = color("light_2")
        val lightBlue: ComposeColor? get() = color("light_blue")
        val lightBlue2: ComposeColor? get() = color("light_blue_2")
        val lightBlue3: ComposeColor? get() = color("light_blue_3")
        val lightCyan: ComposeColor? get() = color("light_cyan")
        val lightCyan2: ComposeColor? get() = color("light_cyan_2")
        val lightGray: ComposeColor? get() = color("light_gray")
        val lightGray10: ComposeColor? get() = color("light_gray_10")
        val lightGray2: ComposeColor? get() = color("light_gray_2")
        val lightGray3: ComposeColor? get() = color("light_gray_3")
        val lightGray4: ComposeColor? get() = color("light_gray_4")
        val lightGray5: ComposeColor? get() = color("light_gray_5")
        val lightGray6: ComposeColor? get() = color("light_gray_6")
        val lightGray7: ComposeColor? get() = color("light_gray_7")
        val lightGray8: ComposeColor? get() = color("light_gray_8")
        val lightGray9: ComposeColor? get() = color("light_gray_9")
        val lightGreen: ComposeColor? get() = color("light_green")
        val lightGreen2: ComposeColor? get() = color("light_green_2")
        val lightLime: ComposeColor? get() = color("light_lime")
        val lightOrange: ComposeColor? get() = color("light_orange")
        val lightOrange2: ComposeColor? get() = color("light_orange_2")
        val lightPurple: ComposeColor? get() = color("light_purple")
        val lightRed: ComposeColor? get() = color("light_red")
        val lightRed2: ComposeColor? get() = color("light_red_2")
        val lightRed3: ComposeColor? get() = color("light_red_3")
        val lightRed4: ComposeColor? get() = color("light_red_4")
        val lightRed5: ComposeColor? get() = color("light_red_5")
        val lightRed6: ComposeColor? get() = color("light_red_6")
        val lightYellow: ComposeColor? get() = color("light_yellow")
        val mediumBlue: ComposeColor? get() = color("medium_blue")
        val mediumBlue2: ComposeColor? get() = color("medium_blue_2")
        val mediumBlue3: ComposeColor? get() = color("medium_blue_3")
        val mediumBlue4: ComposeColor? get() = color("medium_blue_4")
        val mediumBlue5: ComposeColor? get() = color("medium_blue_5")
        val mediumCyan: ComposeColor? get() = color("medium_cyan")
        val mediumGray: ComposeColor? get() = color("medium_gray")
        val mediumGray2: ComposeColor? get() = color("medium_gray_2")
        val mediumGray3: ComposeColor? get() = color("medium_gray_3")
        val mediumGray4: ComposeColor? get() = color("medium_gray_4")
        val mediumGray5: ComposeColor? get() = color("medium_gray_5")
        val mediumGreen: ComposeColor? get() = color("medium_green")
        val mediumGreen2: ComposeColor? get() = color("medium_green_2")
        val mediumGreen3: ComposeColor? get() = color("medium_green_3")
        val mediumLime: ComposeColor? get() = color("medium_lime")
        val mediumLime2: ComposeColor? get() = color("medium_lime_2")
        val mediumPurple: ComposeColor? get() = color("medium_purple")
        val mediumRed: ComposeColor? get() = color("medium_red")
        val mediumRed2: ComposeColor? get() = color("medium_red_2")
        val mediumRed3: ComposeColor? get() = color("medium_red_3")
        val mediumRed4: ComposeColor? get() = color("medium_red_4")
        val mediumRed5: ComposeColor? get() = color("medium_red_5")
        val mediumRed6: ComposeColor? get() = color("medium_red_6")
        val pale: ComposeColor? get() = color("pale")
        val paleBlue: ComposeColor? get() = color("pale_blue")
        val paleBlue2: ComposeColor? get() = color("pale_blue_2")
        val paleBlue3: ComposeColor? get() = color("pale_blue_3")
        val paleCyan: ComposeColor? get() = color("pale_cyan")
        val paleGray: ComposeColor? get() = color("pale_gray")
        val paleGray2: ComposeColor? get() = color("pale_gray_2")
        val paleGray3: ComposeColor? get() = color("pale_gray_3")
        val paleGray4: ComposeColor? get() = color("pale_gray_4")
        val paleGray5: ComposeColor? get() = color("pale_gray_5")
        val paleGray6: ComposeColor? get() = color("pale_gray_6")
        val paleGreen: ComposeColor? get() = color("pale_green")
        val paleGreen2: ComposeColor? get() = color("pale_green_2")
        val paleGreen3: ComposeColor? get() = color("pale_green_3")
        val palePink: ComposeColor? get() = color("pale_pink")
        val palePink2: ComposeColor? get() = color("pale_pink_2")
        val paleRed: ComposeColor? get() = color("pale_red")
        val paleRed2: ComposeColor? get() = color("pale_red_2")
        val paleRed3: ComposeColor? get() = color("pale_red_3")
        val paleYellow: ComposeColor? get() = color("pale_yellow")
        val white: ComposeColor? get() = color("white")
        val white10: ComposeColor? get() = color("white_10")
        val white11: ComposeColor? get() = color("white_11")
        val white12: ComposeColor? get() = color("white_12")
        val white13: ComposeColor? get() = color("white_13")
        val white14: ComposeColor? get() = color("white_14")
        val white15: ComposeColor? get() = color("white_15")
        val white16: ComposeColor? get() = color("white_16")
        val white17: ComposeColor? get() = color("white_17")
        val white18: ComposeColor? get() = color("white_18")
        val white19: ComposeColor? get() = color("white_19")
        val white2: ComposeColor? get() = color("white_2")
        val white20: ComposeColor? get() = color("white_20")
        val white21: ComposeColor? get() = color("white_21")
        val white22: ComposeColor? get() = color("white_22")
        val white23: ComposeColor? get() = color("white_23")
        val white24: ComposeColor? get() = color("white_24")
        val white25: ComposeColor? get() = color("white_25")
        val white26: ComposeColor? get() = color("white_26")
        val white27: ComposeColor? get() = color("white_27")
        val white28: ComposeColor? get() = color("white_28")
        val white3: ComposeColor? get() = color("white_3")
        val white4: ComposeColor? get() = color("white_4")
        val white5: ComposeColor? get() = color("white_5")
        val white6: ComposeColor? get() = color("white_6")
        val white7: ComposeColor? get() = color("white_7")
        val white8: ComposeColor? get() = color("white_8")
        val white9: ComposeColor? get() = color("white_9")

        object light {
            val Color.Green: ComposeColor? get() = views.light.Color.Green?.let { ComposeColor(it) }
            val black: ComposeColor? get() = views.light.black?.let { ComposeColor(it) }
            val black2: ComposeColor? get() = views.light.black2?.let { ComposeColor(it) }
            val darkBlue: ComposeColor? get() = views.light.darkBlue?.let { ComposeColor(it) }
            val darkGray: ComposeColor? get() = views.light.darkGray?.let { ComposeColor(it) }
            val darkGreen: ComposeColor? get() = views.light.darkGreen?.let { ComposeColor(it) }
            val darkGreen2: ComposeColor? get() = views.light.darkGreen2?.let { ComposeColor(it) }
            val darkGreen3: ComposeColor? get() = views.light.darkGreen3?.let { ComposeColor(it) }
            val darkPurple: ComposeColor? get() = views.light.darkPurple?.let { ComposeColor(it) }
            val darkRed: ComposeColor? get() = views.light.darkRed?.let { ComposeColor(it) }
            val light: ComposeColor? get() = views.light.light?.let { ComposeColor(it) }
            val light2: ComposeColor? get() = views.light.light2?.let { ComposeColor(it) }
            val lightBlue: ComposeColor? get() = views.light.lightBlue?.let { ComposeColor(it) }
            val lightBlue2: ComposeColor? get() = views.light.lightBlue2?.let { ComposeColor(it) }
            val lightBlue3: ComposeColor? get() = views.light.lightBlue3?.let { ComposeColor(it) }
            val lightCyan: ComposeColor? get() = views.light.lightCyan?.let { ComposeColor(it) }
            val lightCyan2: ComposeColor? get() = views.light.lightCyan2?.let { ComposeColor(it) }
            val lightGray: ComposeColor? get() = views.light.lightGray?.let { ComposeColor(it) }
            val lightGray10: ComposeColor? get() = views.light.lightGray10?.let { ComposeColor(it) }
            val lightGray2: ComposeColor? get() = views.light.lightGray2?.let { ComposeColor(it) }
            val lightGray3: ComposeColor? get() = views.light.lightGray3?.let { ComposeColor(it) }
            val lightGray4: ComposeColor? get() = views.light.lightGray4?.let { ComposeColor(it) }
            val lightGray5: ComposeColor? get() = views.light.lightGray5?.let { ComposeColor(it) }
            val lightGray6: ComposeColor? get() = views.light.lightGray6?.let { ComposeColor(it) }
            val lightGray7: ComposeColor? get() = views.light.lightGray7?.let { ComposeColor(it) }
            val lightGray8: ComposeColor? get() = views.light.lightGray8?.let { ComposeColor(it) }
            val lightGray9: ComposeColor? get() = views.light.lightGray9?.let { ComposeColor(it) }
            val lightGreen: ComposeColor? get() = views.light.lightGreen?.let { ComposeColor(it) }
            val lightGreen2: ComposeColor? get() = views.light.lightGreen2?.let { ComposeColor(it) }
            val lightLime: ComposeColor? get() = views.light.lightLime?.let { ComposeColor(it) }
            val lightOrange: ComposeColor? get() = views.light.lightOrange?.let { ComposeColor(it) }
            val lightOrange2: ComposeColor? get() = views.light.lightOrange2?.let { ComposeColor(it) }
            val lightPurple: ComposeColor? get() = views.light.lightPurple?.let { ComposeColor(it) }
            val lightRed: ComposeColor? get() = views.light.lightRed?.let { ComposeColor(it) }
            val lightRed2: ComposeColor? get() = views.light.lightRed2?.let { ComposeColor(it) }
            val lightRed3: ComposeColor? get() = views.light.lightRed3?.let { ComposeColor(it) }
            val lightRed4: ComposeColor? get() = views.light.lightRed4?.let { ComposeColor(it) }
            val lightRed5: ComposeColor? get() = views.light.lightRed5?.let { ComposeColor(it) }
            val lightRed6: ComposeColor? get() = views.light.lightRed6?.let { ComposeColor(it) }
            val lightYellow: ComposeColor? get() = views.light.lightYellow?.let { ComposeColor(it) }
            val mediumBlue: ComposeColor? get() = views.light.mediumBlue?.let { ComposeColor(it) }
            val mediumBlue2: ComposeColor? get() = views.light.mediumBlue2?.let { ComposeColor(it) }
            val mediumBlue3: ComposeColor? get() = views.light.mediumBlue3?.let { ComposeColor(it) }
            val mediumBlue4: ComposeColor? get() = views.light.mediumBlue4?.let { ComposeColor(it) }
            val mediumBlue5: ComposeColor? get() = views.light.mediumBlue5?.let { ComposeColor(it) }
            val mediumCyan: ComposeColor? get() = views.light.mediumCyan?.let { ComposeColor(it) }
            val mediumGray: ComposeColor? get() = views.light.mediumGray?.let { ComposeColor(it) }
            val mediumGray2: ComposeColor? get() = views.light.mediumGray2?.let { ComposeColor(it) }
            val mediumGray3: ComposeColor? get() = views.light.mediumGray3?.let { ComposeColor(it) }
            val mediumGray4: ComposeColor? get() = views.light.mediumGray4?.let { ComposeColor(it) }
            val mediumGray5: ComposeColor? get() = views.light.mediumGray5?.let { ComposeColor(it) }
            val mediumGreen: ComposeColor? get() = views.light.mediumGreen?.let { ComposeColor(it) }
            val mediumGreen2: ComposeColor? get() = views.light.mediumGreen2?.let { ComposeColor(it) }
            val mediumGreen3: ComposeColor? get() = views.light.mediumGreen3?.let { ComposeColor(it) }
            val mediumLime: ComposeColor? get() = views.light.mediumLime?.let { ComposeColor(it) }
            val mediumLime2: ComposeColor? get() = views.light.mediumLime2?.let { ComposeColor(it) }
            val mediumPurple: ComposeColor? get() = views.light.mediumPurple?.let { ComposeColor(it) }
            val mediumRed: ComposeColor? get() = views.light.mediumRed?.let { ComposeColor(it) }
            val mediumRed2: ComposeColor? get() = views.light.mediumRed2?.let { ComposeColor(it) }
            val mediumRed3: ComposeColor? get() = views.light.mediumRed3?.let { ComposeColor(it) }
            val mediumRed4: ComposeColor? get() = views.light.mediumRed4?.let { ComposeColor(it) }
            val mediumRed5: ComposeColor? get() = views.light.mediumRed5?.let { ComposeColor(it) }
            val mediumRed6: ComposeColor? get() = views.light.mediumRed6?.let { ComposeColor(it) }
            val pale: ComposeColor? get() = views.light.pale?.let { ComposeColor(it) }
            val paleBlue: ComposeColor? get() = views.light.paleBlue?.let { ComposeColor(it) }
            val paleBlue2: ComposeColor? get() = views.light.paleBlue2?.let { ComposeColor(it) }
            val paleBlue3: ComposeColor? get() = views.light.paleBlue3?.let { ComposeColor(it) }
            val paleCyan: ComposeColor? get() = views.light.paleCyan?.let { ComposeColor(it) }
            val paleGray: ComposeColor? get() = views.light.paleGray?.let { ComposeColor(it) }
            val paleGray2: ComposeColor? get() = views.light.paleGray2?.let { ComposeColor(it) }
            val paleGray3: ComposeColor? get() = views.light.paleGray3?.let { ComposeColor(it) }
            val paleGray4: ComposeColor? get() = views.light.paleGray4?.let { ComposeColor(it) }
            val paleGray5: ComposeColor? get() = views.light.paleGray5?.let { ComposeColor(it) }
            val paleGray6: ComposeColor? get() = views.light.paleGray6?.let { ComposeColor(it) }
            val paleGreen: ComposeColor? get() = views.light.paleGreen?.let { ComposeColor(it) }
            val paleGreen2: ComposeColor? get() = views.light.paleGreen2?.let { ComposeColor(it) }
            val paleGreen3: ComposeColor? get() = views.light.paleGreen3?.let { ComposeColor(it) }
            val palePink: ComposeColor? get() = views.light.palePink?.let { ComposeColor(it) }
            val palePink2: ComposeColor? get() = views.light.palePink2?.let { ComposeColor(it) }
            val paleRed: ComposeColor? get() = views.light.paleRed?.let { ComposeColor(it) }
            val paleRed2: ComposeColor? get() = views.light.paleRed2?.let { ComposeColor(it) }
            val paleRed3: ComposeColor? get() = views.light.paleRed3?.let { ComposeColor(it) }
            val paleYellow: ComposeColor? get() = views.light.paleYellow?.let { ComposeColor(it) }
            val white: ComposeColor? get() = views.light.white?.let { ComposeColor(it) }
            val white10: ComposeColor? get() = views.light.white10?.let { ComposeColor(it) }
            val white11: ComposeColor? get() = views.light.white11?.let { ComposeColor(it) }
            val white12: ComposeColor? get() = views.light.white12?.let { ComposeColor(it) }
            val white13: ComposeColor? get() = views.light.white13?.let { ComposeColor(it) }
            val white14: ComposeColor? get() = views.light.white14?.let { ComposeColor(it) }
            val white15: ComposeColor? get() = views.light.white15?.let { ComposeColor(it) }
            val white16: ComposeColor? get() = views.light.white16?.let { ComposeColor(it) }
            val white17: ComposeColor? get() = views.light.white17?.let { ComposeColor(it) }
            val white18: ComposeColor? get() = views.light.white18?.let { ComposeColor(it) }
            val white19: ComposeColor? get() = views.light.white19?.let { ComposeColor(it) }
            val white2: ComposeColor? get() = views.light.white2?.let { ComposeColor(it) }
            val white20: ComposeColor? get() = views.light.white20?.let { ComposeColor(it) }
            val white21: ComposeColor? get() = views.light.white21?.let { ComposeColor(it) }
            val white22: ComposeColor? get() = views.light.white22?.let { ComposeColor(it) }
            val white23: ComposeColor? get() = views.light.white23?.let { ComposeColor(it) }
            val white24: ComposeColor? get() = views.light.white24?.let { ComposeColor(it) }
            val white25: ComposeColor? get() = views.light.white25?.let { ComposeColor(it) }
            val white26: ComposeColor? get() = views.light.white26?.let { ComposeColor(it) }
            val white27: ComposeColor? get() = views.light.white27?.let { ComposeColor(it) }
            val white28: ComposeColor? get() = views.light.white28?.let { ComposeColor(it) }
            val white3: ComposeColor? get() = views.light.white3?.let { ComposeColor(it) }
            val white4: ComposeColor? get() = views.light.white4?.let { ComposeColor(it) }
            val white5: ComposeColor? get() = views.light.white5?.let { ComposeColor(it) }
            val white6: ComposeColor? get() = views.light.white6?.let { ComposeColor(it) }
            val white7: ComposeColor? get() = views.light.white7?.let { ComposeColor(it) }
            val white8: ComposeColor? get() = views.light.white8?.let { ComposeColor(it) }
            val white9: ComposeColor? get() = views.light.white9?.let { ComposeColor(it) }
        }
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══