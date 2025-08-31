// ColorManager.kt
// Auto-generated file - DO NOT EDIT
// Generated at: 2025-09-01 02:20:12

package com.kotlinjsonui.generated

import android.graphics.Color
import androidx.compose.ui.graphics.Color as ComposeColor

object ColorManager {
    // Load colors from colors.json
    private val colorsData: Map<String, String> = mapOf(
        "white" to "#FFFFFF",
        "black" to "#000000",
        "dark_gray" to "#333333",
        "pale_gray" to "#E0E0E0",
        "pale_pink" to "#FFD0D0",
        "pale_gray_2" to "#D8D8D8",
        "pale_yellow" to "#D0FFD0",
        "pale_gray_3" to "#D0D0D0",
        "pale_cyan" to "#D0D0FF",
        "light_gray" to "#C8C8C8",
        "white_2" to "#FFFFD0",
        "light_gray_2" to "#C0C0C0",
        "white_3" to "#FFD0FF",
        "light_gray_3" to "#B8B8B8",
        "white_4" to "#D0FFFF",
        "light_gray_4" to "#B0B0B0",
        "pale_red" to "#FFCCCC",
        "light_gray_5" to "#A8A8A8",
        "pale_green" to "#CCFFCC",
        "light_gray_6" to "#A0A0A0",
        "white_5" to "#FFE0E0",
        "white_6" to "#E0FFE0",
        "white_7" to "#E0E0FF",
        "white_8" to "#FFFFE0",
        "white_9" to "#FFE0FF",
        "light_gray_7" to "#989898",
        "pale_red_2" to "#FFB0B0",
        "pale_green_2" to "#B0FFB0",
        "pale_blue" to "#B0B0FF",
        "medium_gray" to "#909090",
        "pale_red_3" to "#FFC0C0",
        "pale_green_3" to "#C0FFC0",
        "pale_blue_2" to "#C0C0FF",
        "medium_gray_2" to "#888888",
        "white_10" to "#FFE8E8",
        "medium_gray_3" to "#808080",
        "white_11" to "#E8FFE8",
        "medium_gray_4" to "#666666",
        "pale_gray_4" to "#CCCCCC",
        "medium_red" to "#FF3B30",
        "medium_green" to "#34C759",
        "medium_blue" to "#007AFF",
        "light_gray_8" to "#999999",
        "medium_green_2" to "#4CAF50",
        "medium_blue_2" to "#2196F3",
        "medium_red_2" to "#FF5722",
        "medium_purple" to "#9C27B0",
        "medium_red_3" to "#FF9500",
        "white_12" to "#F5F5F7",
        "light_orange" to "#FFD700",
        "black_2" to "#00000033",
        "medium_blue_3" to "#5856D6",
        "white_13" to "#E8F5E9",
        "dark_green" to "#2E7D32",
        "white_14" to "#FFF3E0",
        "medium_red_4" to "#E65100",
        "white_15" to "#F3E5F5",
        "dark_purple" to "#6A1B9A",
        "light_red" to "#FF6B6B",
        "light_gray_9" to "#AAAAAA",
        "light_gray_10" to "#BBBBBB",
        "white_16" to "#E3F2FD",
        "white_17" to "#F0F0F0",
        "dark_red" to "#FF0000",
        "dark_green_2" to "#00FF00",
        "dark_blue" to "#0000FF",
        "pale_blue_3" to "#CCCCFF",
        "light_red_2" to "#FF6666",
        "light_green" to "#66FF66",
        "light_blue" to "#6666FF",
        "white_18" to "#FFFFCC",
        "light" to "#FF00FF",
        "white_19" to "#FFE5E5",
        "white_20" to "#E8F4FD",
        "medium_blue_4" to "#0066CC",
        "medium_red_5" to "#FF9800",
        "medium_cyan" to "#607D8B",
        "white_21" to "#FFF4E6",
        "medium_red_6" to "#FF6600",
        "white_22" to "#E0FFFF",
        "pale_gray_5" to "#DDDDDD",
        "light_2" to "#FFFF00",
        "white_23" to "#F5F5F5",
        "light_lime" to "#4ECDC4",
        "light_cyan" to "#45B7D1",
        "light_yellow" to "#96CEB4",
        "white_24" to "#E8E8E8",
        "pale_pink_2" to "#FFEAA7",
        "light_blue_2" to "#A29BFE",
        "medium_blue_5" to "#6C5CE7",
        "light_red_3" to "#FD79A8",
        "medium_lime" to "#00B894",
        "medium_lime_2" to "#00CEC9",
        "medium_gray_5" to "#636E72",
        "light_red_4" to "#FAB1A0",
        "light_orange_2" to "#FFD93D",
        "medium_green_3" to "#6BCB77",
        "light_red_5" to "#FF6B9D",
        "white_25" to "#FFF0E0",
        "white_26" to "#F0E0FF",
        "white_27" to "#FAFAFA",
        "white_28" to "#F9F9F9",
        "light_purple" to "#AF52DE",
        "light_cyan_2" to "#5AC8FA",
        "dark_green_3" to "#00C853",
        "light_red_6" to "#FFA0A0",
        "light_green_2" to "#A0FFA0",
        "light_blue_3" to "#A0A0FF",
        "pale" to "#FFFFA0",
        "pale_gray_6" to "#DFE6E9"
    )

    // Android View colors
    object android {
        // Get Color by key
        fun color(key: String): Int {
            val hexString = colorsData[key]
            if (hexString == null) {
                println("Warning: Color key '$key' not found in colors.json")
                return Color.GRAY // Default fallback color
            }
            return try {
                Color.parseColor(hexString)
            } catch (e: IllegalArgumentException) {
                println("Warning: Invalid color format '$hexString' for key '$key'")
                Color.GRAY
            }
        }

        val black: Int
            get() {
                return try {
                    Color.parseColor("#000000")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val black2: Int
            get() {
                return try {
                    Color.parseColor("#00000033")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val darkBlue: Int
            get() {
                return try {
                    Color.parseColor("#0000FF")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val darkGray: Int
            get() {
                return try {
                    Color.parseColor("#333333")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val darkGreen: Int
            get() {
                return try {
                    Color.parseColor("#2E7D32")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val darkGreen2: Int
            get() {
                return try {
                    Color.parseColor("#00FF00")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val darkGreen3: Int
            get() {
                return try {
                    Color.parseColor("#00C853")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val darkPurple: Int
            get() {
                return try {
                    Color.parseColor("#6A1B9A")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val darkRed: Int
            get() {
                return try {
                    Color.parseColor("#FF0000")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val light: Int
            get() {
                return try {
                    Color.parseColor("#FF00FF")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val light2: Int
            get() {
                return try {
                    Color.parseColor("#FFFF00")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val lightBlue: Int
            get() {
                return try {
                    Color.parseColor("#6666FF")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val lightBlue2: Int
            get() {
                return try {
                    Color.parseColor("#A29BFE")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val lightBlue3: Int
            get() {
                return try {
                    Color.parseColor("#A0A0FF")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val lightCyan: Int
            get() {
                return try {
                    Color.parseColor("#45B7D1")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val lightCyan2: Int
            get() {
                return try {
                    Color.parseColor("#5AC8FA")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val lightGray: Int
            get() {
                return try {
                    Color.parseColor("#C8C8C8")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val lightGray10: Int
            get() {
                return try {
                    Color.parseColor("#BBBBBB")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val lightGray2: Int
            get() {
                return try {
                    Color.parseColor("#C0C0C0")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val lightGray3: Int
            get() {
                return try {
                    Color.parseColor("#B8B8B8")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val lightGray4: Int
            get() {
                return try {
                    Color.parseColor("#B0B0B0")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val lightGray5: Int
            get() {
                return try {
                    Color.parseColor("#A8A8A8")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val lightGray6: Int
            get() {
                return try {
                    Color.parseColor("#A0A0A0")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val lightGray7: Int
            get() {
                return try {
                    Color.parseColor("#989898")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val lightGray8: Int
            get() {
                return try {
                    Color.parseColor("#999999")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val lightGray9: Int
            get() {
                return try {
                    Color.parseColor("#AAAAAA")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val lightGreen: Int
            get() {
                return try {
                    Color.parseColor("#66FF66")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val lightGreen2: Int
            get() {
                return try {
                    Color.parseColor("#A0FFA0")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val lightLime: Int
            get() {
                return try {
                    Color.parseColor("#4ECDC4")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val lightOrange: Int
            get() {
                return try {
                    Color.parseColor("#FFD700")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val lightOrange2: Int
            get() {
                return try {
                    Color.parseColor("#FFD93D")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val lightPurple: Int
            get() {
                return try {
                    Color.parseColor("#AF52DE")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val lightRed: Int
            get() {
                return try {
                    Color.parseColor("#FF6B6B")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val lightRed2: Int
            get() {
                return try {
                    Color.parseColor("#FF6666")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val lightRed3: Int
            get() {
                return try {
                    Color.parseColor("#FD79A8")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val lightRed4: Int
            get() {
                return try {
                    Color.parseColor("#FAB1A0")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val lightRed5: Int
            get() {
                return try {
                    Color.parseColor("#FF6B9D")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val lightRed6: Int
            get() {
                return try {
                    Color.parseColor("#FFA0A0")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val lightYellow: Int
            get() {
                return try {
                    Color.parseColor("#96CEB4")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val mediumBlue: Int
            get() {
                return try {
                    Color.parseColor("#007AFF")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val mediumBlue2: Int
            get() {
                return try {
                    Color.parseColor("#2196F3")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val mediumBlue3: Int
            get() {
                return try {
                    Color.parseColor("#5856D6")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val mediumBlue4: Int
            get() {
                return try {
                    Color.parseColor("#0066CC")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val mediumBlue5: Int
            get() {
                return try {
                    Color.parseColor("#6C5CE7")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val mediumCyan: Int
            get() {
                return try {
                    Color.parseColor("#607D8B")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val mediumGray: Int
            get() {
                return try {
                    Color.parseColor("#909090")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val mediumGray2: Int
            get() {
                return try {
                    Color.parseColor("#888888")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val mediumGray3: Int
            get() {
                return try {
                    Color.parseColor("#808080")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val mediumGray4: Int
            get() {
                return try {
                    Color.parseColor("#666666")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val mediumGray5: Int
            get() {
                return try {
                    Color.parseColor("#636E72")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val mediumGreen: Int
            get() {
                return try {
                    Color.parseColor("#34C759")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val mediumGreen2: Int
            get() {
                return try {
                    Color.parseColor("#4CAF50")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val mediumGreen3: Int
            get() {
                return try {
                    Color.parseColor("#6BCB77")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val mediumLime: Int
            get() {
                return try {
                    Color.parseColor("#00B894")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val mediumLime2: Int
            get() {
                return try {
                    Color.parseColor("#00CEC9")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val mediumPurple: Int
            get() {
                return try {
                    Color.parseColor("#9C27B0")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val mediumRed: Int
            get() {
                return try {
                    Color.parseColor("#FF3B30")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val mediumRed2: Int
            get() {
                return try {
                    Color.parseColor("#FF5722")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val mediumRed3: Int
            get() {
                return try {
                    Color.parseColor("#FF9500")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val mediumRed4: Int
            get() {
                return try {
                    Color.parseColor("#E65100")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val mediumRed5: Int
            get() {
                return try {
                    Color.parseColor("#FF9800")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val mediumRed6: Int
            get() {
                return try {
                    Color.parseColor("#FF6600")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val pale: Int
            get() {
                return try {
                    Color.parseColor("#FFFFA0")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val paleBlue: Int
            get() {
                return try {
                    Color.parseColor("#B0B0FF")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val paleBlue2: Int
            get() {
                return try {
                    Color.parseColor("#C0C0FF")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val paleBlue3: Int
            get() {
                return try {
                    Color.parseColor("#CCCCFF")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val paleCyan: Int
            get() {
                return try {
                    Color.parseColor("#D0D0FF")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val paleGray: Int
            get() {
                return try {
                    Color.parseColor("#E0E0E0")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val paleGray2: Int
            get() {
                return try {
                    Color.parseColor("#D8D8D8")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val paleGray3: Int
            get() {
                return try {
                    Color.parseColor("#D0D0D0")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val paleGray4: Int
            get() {
                return try {
                    Color.parseColor("#CCCCCC")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val paleGray5: Int
            get() {
                return try {
                    Color.parseColor("#DDDDDD")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val paleGray6: Int
            get() {
                return try {
                    Color.parseColor("#DFE6E9")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val paleGreen: Int
            get() {
                return try {
                    Color.parseColor("#CCFFCC")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val paleGreen2: Int
            get() {
                return try {
                    Color.parseColor("#B0FFB0")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val paleGreen3: Int
            get() {
                return try {
                    Color.parseColor("#C0FFC0")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val palePink: Int
            get() {
                return try {
                    Color.parseColor("#FFD0D0")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val palePink2: Int
            get() {
                return try {
                    Color.parseColor("#FFEAA7")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val paleRed: Int
            get() {
                return try {
                    Color.parseColor("#FFCCCC")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val paleRed2: Int
            get() {
                return try {
                    Color.parseColor("#FFB0B0")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val paleRed3: Int
            get() {
                return try {
                    Color.parseColor("#FFC0C0")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val paleYellow: Int
            get() {
                return try {
                    Color.parseColor("#D0FFD0")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val white: Int
            get() {
                return try {
                    Color.parseColor("#FFFFFF")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val white10: Int
            get() {
                return try {
                    Color.parseColor("#FFE8E8")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val white11: Int
            get() {
                return try {
                    Color.parseColor("#E8FFE8")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val white12: Int
            get() {
                return try {
                    Color.parseColor("#F5F5F7")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val white13: Int
            get() {
                return try {
                    Color.parseColor("#E8F5E9")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val white14: Int
            get() {
                return try {
                    Color.parseColor("#FFF3E0")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val white15: Int
            get() {
                return try {
                    Color.parseColor("#F3E5F5")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val white16: Int
            get() {
                return try {
                    Color.parseColor("#E3F2FD")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val white17: Int
            get() {
                return try {
                    Color.parseColor("#F0F0F0")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val white18: Int
            get() {
                return try {
                    Color.parseColor("#FFFFCC")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val white19: Int
            get() {
                return try {
                    Color.parseColor("#FFE5E5")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val white2: Int
            get() {
                return try {
                    Color.parseColor("#FFFFD0")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val white20: Int
            get() {
                return try {
                    Color.parseColor("#E8F4FD")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val white21: Int
            get() {
                return try {
                    Color.parseColor("#FFF4E6")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val white22: Int
            get() {
                return try {
                    Color.parseColor("#E0FFFF")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val white23: Int
            get() {
                return try {
                    Color.parseColor("#F5F5F5")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val white24: Int
            get() {
                return try {
                    Color.parseColor("#E8E8E8")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val white25: Int
            get() {
                return try {
                    Color.parseColor("#FFF0E0")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val white26: Int
            get() {
                return try {
                    Color.parseColor("#F0E0FF")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val white27: Int
            get() {
                return try {
                    Color.parseColor("#FAFAFA")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val white28: Int
            get() {
                return try {
                    Color.parseColor("#F9F9F9")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val white3: Int
            get() {
                return try {
                    Color.parseColor("#FFD0FF")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val white4: Int
            get() {
                return try {
                    Color.parseColor("#D0FFFF")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val white5: Int
            get() {
                return try {
                    Color.parseColor("#FFE0E0")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val white6: Int
            get() {
                return try {
                    Color.parseColor("#E0FFE0")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val white7: Int
            get() {
                return try {
                    Color.parseColor("#E0E0FF")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val white8: Int
            get() {
                return try {
                    Color.parseColor("#FFFFE0")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

        val white9: Int
            get() {
                return try {
                    Color.parseColor("#FFE0FF")
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }

    }

    // Compose colors
    object compose {
        // Get Compose Color by key
        fun color(key: String): ComposeColor {
            val androidColor = android.color(key)
            return ComposeColor(androidColor)
        }

        val black: ComposeColor
            get() = ComposeColor(android.black)

        val black2: ComposeColor
            get() = ComposeColor(android.black2)

        val darkBlue: ComposeColor
            get() = ComposeColor(android.darkBlue)

        val darkGray: ComposeColor
            get() = ComposeColor(android.darkGray)

        val darkGreen: ComposeColor
            get() = ComposeColor(android.darkGreen)

        val darkGreen2: ComposeColor
            get() = ComposeColor(android.darkGreen2)

        val darkGreen3: ComposeColor
            get() = ComposeColor(android.darkGreen3)

        val darkPurple: ComposeColor
            get() = ComposeColor(android.darkPurple)

        val darkRed: ComposeColor
            get() = ComposeColor(android.darkRed)

        val light: ComposeColor
            get() = ComposeColor(android.light)

        val light2: ComposeColor
            get() = ComposeColor(android.light2)

        val lightBlue: ComposeColor
            get() = ComposeColor(android.lightBlue)

        val lightBlue2: ComposeColor
            get() = ComposeColor(android.lightBlue2)

        val lightBlue3: ComposeColor
            get() = ComposeColor(android.lightBlue3)

        val lightCyan: ComposeColor
            get() = ComposeColor(android.lightCyan)

        val lightCyan2: ComposeColor
            get() = ComposeColor(android.lightCyan2)

        val lightGray: ComposeColor
            get() = ComposeColor(android.lightGray)

        val lightGray10: ComposeColor
            get() = ComposeColor(android.lightGray10)

        val lightGray2: ComposeColor
            get() = ComposeColor(android.lightGray2)

        val lightGray3: ComposeColor
            get() = ComposeColor(android.lightGray3)

        val lightGray4: ComposeColor
            get() = ComposeColor(android.lightGray4)

        val lightGray5: ComposeColor
            get() = ComposeColor(android.lightGray5)

        val lightGray6: ComposeColor
            get() = ComposeColor(android.lightGray6)

        val lightGray7: ComposeColor
            get() = ComposeColor(android.lightGray7)

        val lightGray8: ComposeColor
            get() = ComposeColor(android.lightGray8)

        val lightGray9: ComposeColor
            get() = ComposeColor(android.lightGray9)

        val lightGreen: ComposeColor
            get() = ComposeColor(android.lightGreen)

        val lightGreen2: ComposeColor
            get() = ComposeColor(android.lightGreen2)

        val lightLime: ComposeColor
            get() = ComposeColor(android.lightLime)

        val lightOrange: ComposeColor
            get() = ComposeColor(android.lightOrange)

        val lightOrange2: ComposeColor
            get() = ComposeColor(android.lightOrange2)

        val lightPurple: ComposeColor
            get() = ComposeColor(android.lightPurple)

        val lightRed: ComposeColor
            get() = ComposeColor(android.lightRed)

        val lightRed2: ComposeColor
            get() = ComposeColor(android.lightRed2)

        val lightRed3: ComposeColor
            get() = ComposeColor(android.lightRed3)

        val lightRed4: ComposeColor
            get() = ComposeColor(android.lightRed4)

        val lightRed5: ComposeColor
            get() = ComposeColor(android.lightRed5)

        val lightRed6: ComposeColor
            get() = ComposeColor(android.lightRed6)

        val lightYellow: ComposeColor
            get() = ComposeColor(android.lightYellow)

        val mediumBlue: ComposeColor
            get() = ComposeColor(android.mediumBlue)

        val mediumBlue2: ComposeColor
            get() = ComposeColor(android.mediumBlue2)

        val mediumBlue3: ComposeColor
            get() = ComposeColor(android.mediumBlue3)

        val mediumBlue4: ComposeColor
            get() = ComposeColor(android.mediumBlue4)

        val mediumBlue5: ComposeColor
            get() = ComposeColor(android.mediumBlue5)

        val mediumCyan: ComposeColor
            get() = ComposeColor(android.mediumCyan)

        val mediumGray: ComposeColor
            get() = ComposeColor(android.mediumGray)

        val mediumGray2: ComposeColor
            get() = ComposeColor(android.mediumGray2)

        val mediumGray3: ComposeColor
            get() = ComposeColor(android.mediumGray3)

        val mediumGray4: ComposeColor
            get() = ComposeColor(android.mediumGray4)

        val mediumGray5: ComposeColor
            get() = ComposeColor(android.mediumGray5)

        val mediumGreen: ComposeColor
            get() = ComposeColor(android.mediumGreen)

        val mediumGreen2: ComposeColor
            get() = ComposeColor(android.mediumGreen2)

        val mediumGreen3: ComposeColor
            get() = ComposeColor(android.mediumGreen3)

        val mediumLime: ComposeColor
            get() = ComposeColor(android.mediumLime)

        val mediumLime2: ComposeColor
            get() = ComposeColor(android.mediumLime2)

        val mediumPurple: ComposeColor
            get() = ComposeColor(android.mediumPurple)

        val mediumRed: ComposeColor
            get() = ComposeColor(android.mediumRed)

        val mediumRed2: ComposeColor
            get() = ComposeColor(android.mediumRed2)

        val mediumRed3: ComposeColor
            get() = ComposeColor(android.mediumRed3)

        val mediumRed4: ComposeColor
            get() = ComposeColor(android.mediumRed4)

        val mediumRed5: ComposeColor
            get() = ComposeColor(android.mediumRed5)

        val mediumRed6: ComposeColor
            get() = ComposeColor(android.mediumRed6)

        val pale: ComposeColor
            get() = ComposeColor(android.pale)

        val paleBlue: ComposeColor
            get() = ComposeColor(android.paleBlue)

        val paleBlue2: ComposeColor
            get() = ComposeColor(android.paleBlue2)

        val paleBlue3: ComposeColor
            get() = ComposeColor(android.paleBlue3)

        val paleCyan: ComposeColor
            get() = ComposeColor(android.paleCyan)

        val paleGray: ComposeColor
            get() = ComposeColor(android.paleGray)

        val paleGray2: ComposeColor
            get() = ComposeColor(android.paleGray2)

        val paleGray3: ComposeColor
            get() = ComposeColor(android.paleGray3)

        val paleGray4: ComposeColor
            get() = ComposeColor(android.paleGray4)

        val paleGray5: ComposeColor
            get() = ComposeColor(android.paleGray5)

        val paleGray6: ComposeColor
            get() = ComposeColor(android.paleGray6)

        val paleGreen: ComposeColor
            get() = ComposeColor(android.paleGreen)

        val paleGreen2: ComposeColor
            get() = ComposeColor(android.paleGreen2)

        val paleGreen3: ComposeColor
            get() = ComposeColor(android.paleGreen3)

        val palePink: ComposeColor
            get() = ComposeColor(android.palePink)

        val palePink2: ComposeColor
            get() = ComposeColor(android.palePink2)

        val paleRed: ComposeColor
            get() = ComposeColor(android.paleRed)

        val paleRed2: ComposeColor
            get() = ComposeColor(android.paleRed2)

        val paleRed3: ComposeColor
            get() = ComposeColor(android.paleRed3)

        val paleYellow: ComposeColor
            get() = ComposeColor(android.paleYellow)

        val white: ComposeColor
            get() = ComposeColor(android.white)

        val white10: ComposeColor
            get() = ComposeColor(android.white10)

        val white11: ComposeColor
            get() = ComposeColor(android.white11)

        val white12: ComposeColor
            get() = ComposeColor(android.white12)

        val white13: ComposeColor
            get() = ComposeColor(android.white13)

        val white14: ComposeColor
            get() = ComposeColor(android.white14)

        val white15: ComposeColor
            get() = ComposeColor(android.white15)

        val white16: ComposeColor
            get() = ComposeColor(android.white16)

        val white17: ComposeColor
            get() = ComposeColor(android.white17)

        val white18: ComposeColor
            get() = ComposeColor(android.white18)

        val white19: ComposeColor
            get() = ComposeColor(android.white19)

        val white2: ComposeColor
            get() = ComposeColor(android.white2)

        val white20: ComposeColor
            get() = ComposeColor(android.white20)

        val white21: ComposeColor
            get() = ComposeColor(android.white21)

        val white22: ComposeColor
            get() = ComposeColor(android.white22)

        val white23: ComposeColor
            get() = ComposeColor(android.white23)

        val white24: ComposeColor
            get() = ComposeColor(android.white24)

        val white25: ComposeColor
            get() = ComposeColor(android.white25)

        val white26: ComposeColor
            get() = ComposeColor(android.white26)

        val white27: ComposeColor
            get() = ComposeColor(android.white27)

        val white28: ComposeColor
            get() = ComposeColor(android.white28)

        val white3: ComposeColor
            get() = ComposeColor(android.white3)

        val white4: ComposeColor
            get() = ComposeColor(android.white4)

        val white5: ComposeColor
            get() = ComposeColor(android.white5)

        val white6: ComposeColor
            get() = ComposeColor(android.white6)

        val white7: ComposeColor
            get() = ComposeColor(android.white7)

        val white8: ComposeColor
            get() = ComposeColor(android.white8)

        val white9: ComposeColor
            get() = ComposeColor(android.white9)

    }
}