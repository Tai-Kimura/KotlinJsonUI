// ColorManager.kt
// Auto-generated file - DO NOT EDIT
// Generated at: 2026-01-13 07:56:45

package com.kotlinjsonui.generated

import android.graphics.Color
import android.util.Log
import androidx.compose.ui.graphics.Color as ComposeColor

object ColorManager {
    private const val TAG = "ColorManager"
    
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

    // Android Views colors
    object views {
        // Get Color by key (returns null for binding expressions like @{...})
        fun color(key: String): Int? {
            // Skip binding expressions
            if (key.startsWith("@{") && key.endsWith("}")) {
                return null
            }
            val hexString = colorsData[key]
            if (hexString == null) {
                Log.w(TAG, "Color key '$key' not found in colors.json")
                return try {
                    Color.parseColor(key) // Try to parse key as hex color
                } catch (e: IllegalArgumentException) {
                    null
                }
            }
            return try {
                Color.parseColor(hexString)
            } catch (e: IllegalArgumentException) {
                Log.w(TAG, "Invalid color format '$hexString' for key '$key'")
                null
            }
        }

        val Color.Green: Int?
            get() {
                // Undefined color - needs to be defined in colors.json
                Log.w(TAG, "Color 'Color.Green' is not defined in colors.json")
                return null
            }

        val black: Int?
            get() {
                return try {
                    Color.parseColor("#000000")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#000000' for 'black'")
                    null
                }
            }

        val black2: Int?
            get() {
                return try {
                    Color.parseColor("#00000033")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#00000033' for 'black_2'")
                    null
                }
            }

        val darkBlue: Int?
            get() {
                return try {
                    Color.parseColor("#0000FF")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#0000FF' for 'dark_blue'")
                    null
                }
            }

        val darkGray: Int?
            get() {
                return try {
                    Color.parseColor("#333333")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#333333' for 'dark_gray'")
                    null
                }
            }

        val darkGreen: Int?
            get() {
                return try {
                    Color.parseColor("#2E7D32")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#2E7D32' for 'dark_green'")
                    null
                }
            }

        val darkGreen2: Int?
            get() {
                return try {
                    Color.parseColor("#00FF00")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#00FF00' for 'dark_green_2'")
                    null
                }
            }

        val darkGreen3: Int?
            get() {
                return try {
                    Color.parseColor("#00C853")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#00C853' for 'dark_green_3'")
                    null
                }
            }

        val darkPurple: Int?
            get() {
                return try {
                    Color.parseColor("#6A1B9A")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#6A1B9A' for 'dark_purple'")
                    null
                }
            }

        val darkRed: Int?
            get() {
                return try {
                    Color.parseColor("#FF0000")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#FF0000' for 'dark_red'")
                    null
                }
            }

        val light: Int?
            get() {
                return try {
                    Color.parseColor("#FF00FF")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#FF00FF' for 'light'")
                    null
                }
            }

        val light2: Int?
            get() {
                return try {
                    Color.parseColor("#FFFF00")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#FFFF00' for 'light_2'")
                    null
                }
            }

        val lightBlue: Int?
            get() {
                return try {
                    Color.parseColor("#6666FF")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#6666FF' for 'light_blue'")
                    null
                }
            }

        val lightBlue2: Int?
            get() {
                return try {
                    Color.parseColor("#A29BFE")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#A29BFE' for 'light_blue_2'")
                    null
                }
            }

        val lightBlue3: Int?
            get() {
                return try {
                    Color.parseColor("#A0A0FF")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#A0A0FF' for 'light_blue_3'")
                    null
                }
            }

        val lightCyan: Int?
            get() {
                return try {
                    Color.parseColor("#45B7D1")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#45B7D1' for 'light_cyan'")
                    null
                }
            }

        val lightCyan2: Int?
            get() {
                return try {
                    Color.parseColor("#5AC8FA")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#5AC8FA' for 'light_cyan_2'")
                    null
                }
            }

        val lightGray: Int?
            get() {
                return try {
                    Color.parseColor("#C8C8C8")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#C8C8C8' for 'light_gray'")
                    null
                }
            }

        val lightGray10: Int?
            get() {
                return try {
                    Color.parseColor("#BBBBBB")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#BBBBBB' for 'light_gray_10'")
                    null
                }
            }

        val lightGray2: Int?
            get() {
                return try {
                    Color.parseColor("#C0C0C0")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#C0C0C0' for 'light_gray_2'")
                    null
                }
            }

        val lightGray3: Int?
            get() {
                return try {
                    Color.parseColor("#B8B8B8")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#B8B8B8' for 'light_gray_3'")
                    null
                }
            }

        val lightGray4: Int?
            get() {
                return try {
                    Color.parseColor("#B0B0B0")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#B0B0B0' for 'light_gray_4'")
                    null
                }
            }

        val lightGray5: Int?
            get() {
                return try {
                    Color.parseColor("#A8A8A8")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#A8A8A8' for 'light_gray_5'")
                    null
                }
            }

        val lightGray6: Int?
            get() {
                return try {
                    Color.parseColor("#A0A0A0")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#A0A0A0' for 'light_gray_6'")
                    null
                }
            }

        val lightGray7: Int?
            get() {
                return try {
                    Color.parseColor("#989898")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#989898' for 'light_gray_7'")
                    null
                }
            }

        val lightGray8: Int?
            get() {
                return try {
                    Color.parseColor("#999999")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#999999' for 'light_gray_8'")
                    null
                }
            }

        val lightGray9: Int?
            get() {
                return try {
                    Color.parseColor("#AAAAAA")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#AAAAAA' for 'light_gray_9'")
                    null
                }
            }

        val lightGreen: Int?
            get() {
                return try {
                    Color.parseColor("#66FF66")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#66FF66' for 'light_green'")
                    null
                }
            }

        val lightGreen2: Int?
            get() {
                return try {
                    Color.parseColor("#A0FFA0")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#A0FFA0' for 'light_green_2'")
                    null
                }
            }

        val lightLime: Int?
            get() {
                return try {
                    Color.parseColor("#4ECDC4")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#4ECDC4' for 'light_lime'")
                    null
                }
            }

        val lightOrange: Int?
            get() {
                return try {
                    Color.parseColor("#FFD700")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#FFD700' for 'light_orange'")
                    null
                }
            }

        val lightOrange2: Int?
            get() {
                return try {
                    Color.parseColor("#FFD93D")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#FFD93D' for 'light_orange_2'")
                    null
                }
            }

        val lightPurple: Int?
            get() {
                return try {
                    Color.parseColor("#AF52DE")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#AF52DE' for 'light_purple'")
                    null
                }
            }

        val lightRed: Int?
            get() {
                return try {
                    Color.parseColor("#FF6B6B")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#FF6B6B' for 'light_red'")
                    null
                }
            }

        val lightRed2: Int?
            get() {
                return try {
                    Color.parseColor("#FF6666")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#FF6666' for 'light_red_2'")
                    null
                }
            }

        val lightRed3: Int?
            get() {
                return try {
                    Color.parseColor("#FD79A8")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#FD79A8' for 'light_red_3'")
                    null
                }
            }

        val lightRed4: Int?
            get() {
                return try {
                    Color.parseColor("#FAB1A0")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#FAB1A0' for 'light_red_4'")
                    null
                }
            }

        val lightRed5: Int?
            get() {
                return try {
                    Color.parseColor("#FF6B9D")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#FF6B9D' for 'light_red_5'")
                    null
                }
            }

        val lightRed6: Int?
            get() {
                return try {
                    Color.parseColor("#FFA0A0")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#FFA0A0' for 'light_red_6'")
                    null
                }
            }

        val lightYellow: Int?
            get() {
                return try {
                    Color.parseColor("#96CEB4")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#96CEB4' for 'light_yellow'")
                    null
                }
            }

        val mediumBlue: Int?
            get() {
                return try {
                    Color.parseColor("#007AFF")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#007AFF' for 'medium_blue'")
                    null
                }
            }

        val mediumBlue2: Int?
            get() {
                return try {
                    Color.parseColor("#2196F3")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#2196F3' for 'medium_blue_2'")
                    null
                }
            }

        val mediumBlue3: Int?
            get() {
                return try {
                    Color.parseColor("#5856D6")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#5856D6' for 'medium_blue_3'")
                    null
                }
            }

        val mediumBlue4: Int?
            get() {
                return try {
                    Color.parseColor("#0066CC")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#0066CC' for 'medium_blue_4'")
                    null
                }
            }

        val mediumBlue5: Int?
            get() {
                return try {
                    Color.parseColor("#6C5CE7")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#6C5CE7' for 'medium_blue_5'")
                    null
                }
            }

        val mediumCyan: Int?
            get() {
                return try {
                    Color.parseColor("#607D8B")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#607D8B' for 'medium_cyan'")
                    null
                }
            }

        val mediumGray: Int?
            get() {
                return try {
                    Color.parseColor("#909090")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#909090' for 'medium_gray'")
                    null
                }
            }

        val mediumGray2: Int?
            get() {
                return try {
                    Color.parseColor("#888888")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#888888' for 'medium_gray_2'")
                    null
                }
            }

        val mediumGray3: Int?
            get() {
                return try {
                    Color.parseColor("#808080")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#808080' for 'medium_gray_3'")
                    null
                }
            }

        val mediumGray4: Int?
            get() {
                return try {
                    Color.parseColor("#666666")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#666666' for 'medium_gray_4'")
                    null
                }
            }

        val mediumGray5: Int?
            get() {
                return try {
                    Color.parseColor("#636E72")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#636E72' for 'medium_gray_5'")
                    null
                }
            }

        val mediumGreen: Int?
            get() {
                return try {
                    Color.parseColor("#34C759")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#34C759' for 'medium_green'")
                    null
                }
            }

        val mediumGreen2: Int?
            get() {
                return try {
                    Color.parseColor("#4CAF50")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#4CAF50' for 'medium_green_2'")
                    null
                }
            }

        val mediumGreen3: Int?
            get() {
                return try {
                    Color.parseColor("#6BCB77")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#6BCB77' for 'medium_green_3'")
                    null
                }
            }

        val mediumLime: Int?
            get() {
                return try {
                    Color.parseColor("#00B894")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#00B894' for 'medium_lime'")
                    null
                }
            }

        val mediumLime2: Int?
            get() {
                return try {
                    Color.parseColor("#00CEC9")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#00CEC9' for 'medium_lime_2'")
                    null
                }
            }

        val mediumPurple: Int?
            get() {
                return try {
                    Color.parseColor("#9C27B0")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#9C27B0' for 'medium_purple'")
                    null
                }
            }

        val mediumRed: Int?
            get() {
                return try {
                    Color.parseColor("#FF3B30")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#FF3B30' for 'medium_red'")
                    null
                }
            }

        val mediumRed2: Int?
            get() {
                return try {
                    Color.parseColor("#FF5722")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#FF5722' for 'medium_red_2'")
                    null
                }
            }

        val mediumRed3: Int?
            get() {
                return try {
                    Color.parseColor("#FF9500")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#FF9500' for 'medium_red_3'")
                    null
                }
            }

        val mediumRed4: Int?
            get() {
                return try {
                    Color.parseColor("#E65100")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#E65100' for 'medium_red_4'")
                    null
                }
            }

        val mediumRed5: Int?
            get() {
                return try {
                    Color.parseColor("#FF9800")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#FF9800' for 'medium_red_5'")
                    null
                }
            }

        val mediumRed6: Int?
            get() {
                return try {
                    Color.parseColor("#FF6600")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#FF6600' for 'medium_red_6'")
                    null
                }
            }

        val pale: Int?
            get() {
                return try {
                    Color.parseColor("#FFFFA0")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#FFFFA0' for 'pale'")
                    null
                }
            }

        val paleBlue: Int?
            get() {
                return try {
                    Color.parseColor("#B0B0FF")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#B0B0FF' for 'pale_blue'")
                    null
                }
            }

        val paleBlue2: Int?
            get() {
                return try {
                    Color.parseColor("#C0C0FF")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#C0C0FF' for 'pale_blue_2'")
                    null
                }
            }

        val paleBlue3: Int?
            get() {
                return try {
                    Color.parseColor("#CCCCFF")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#CCCCFF' for 'pale_blue_3'")
                    null
                }
            }

        val paleCyan: Int?
            get() {
                return try {
                    Color.parseColor("#D0D0FF")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#D0D0FF' for 'pale_cyan'")
                    null
                }
            }

        val paleGray: Int?
            get() {
                return try {
                    Color.parseColor("#E0E0E0")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#E0E0E0' for 'pale_gray'")
                    null
                }
            }

        val paleGray2: Int?
            get() {
                return try {
                    Color.parseColor("#D8D8D8")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#D8D8D8' for 'pale_gray_2'")
                    null
                }
            }

        val paleGray3: Int?
            get() {
                return try {
                    Color.parseColor("#D0D0D0")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#D0D0D0' for 'pale_gray_3'")
                    null
                }
            }

        val paleGray4: Int?
            get() {
                return try {
                    Color.parseColor("#CCCCCC")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#CCCCCC' for 'pale_gray_4'")
                    null
                }
            }

        val paleGray5: Int?
            get() {
                return try {
                    Color.parseColor("#DDDDDD")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#DDDDDD' for 'pale_gray_5'")
                    null
                }
            }

        val paleGray6: Int?
            get() {
                return try {
                    Color.parseColor("#DFE6E9")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#DFE6E9' for 'pale_gray_6'")
                    null
                }
            }

        val paleGreen: Int?
            get() {
                return try {
                    Color.parseColor("#CCFFCC")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#CCFFCC' for 'pale_green'")
                    null
                }
            }

        val paleGreen2: Int?
            get() {
                return try {
                    Color.parseColor("#B0FFB0")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#B0FFB0' for 'pale_green_2'")
                    null
                }
            }

        val paleGreen3: Int?
            get() {
                return try {
                    Color.parseColor("#C0FFC0")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#C0FFC0' for 'pale_green_3'")
                    null
                }
            }

        val palePink: Int?
            get() {
                return try {
                    Color.parseColor("#FFD0D0")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#FFD0D0' for 'pale_pink'")
                    null
                }
            }

        val palePink2: Int?
            get() {
                return try {
                    Color.parseColor("#FFEAA7")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#FFEAA7' for 'pale_pink_2'")
                    null
                }
            }

        val paleRed: Int?
            get() {
                return try {
                    Color.parseColor("#FFCCCC")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#FFCCCC' for 'pale_red'")
                    null
                }
            }

        val paleRed2: Int?
            get() {
                return try {
                    Color.parseColor("#FFB0B0")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#FFB0B0' for 'pale_red_2'")
                    null
                }
            }

        val paleRed3: Int?
            get() {
                return try {
                    Color.parseColor("#FFC0C0")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#FFC0C0' for 'pale_red_3'")
                    null
                }
            }

        val paleYellow: Int?
            get() {
                return try {
                    Color.parseColor("#D0FFD0")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#D0FFD0' for 'pale_yellow'")
                    null
                }
            }

        val white: Int?
            get() {
                return try {
                    Color.parseColor("#FFFFFF")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#FFFFFF' for 'white'")
                    null
                }
            }

        val white10: Int?
            get() {
                return try {
                    Color.parseColor("#FFE8E8")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#FFE8E8' for 'white_10'")
                    null
                }
            }

        val white11: Int?
            get() {
                return try {
                    Color.parseColor("#E8FFE8")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#E8FFE8' for 'white_11'")
                    null
                }
            }

        val white12: Int?
            get() {
                return try {
                    Color.parseColor("#F5F5F7")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#F5F5F7' for 'white_12'")
                    null
                }
            }

        val white13: Int?
            get() {
                return try {
                    Color.parseColor("#E8F5E9")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#E8F5E9' for 'white_13'")
                    null
                }
            }

        val white14: Int?
            get() {
                return try {
                    Color.parseColor("#FFF3E0")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#FFF3E0' for 'white_14'")
                    null
                }
            }

        val white15: Int?
            get() {
                return try {
                    Color.parseColor("#F3E5F5")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#F3E5F5' for 'white_15'")
                    null
                }
            }

        val white16: Int?
            get() {
                return try {
                    Color.parseColor("#E3F2FD")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#E3F2FD' for 'white_16'")
                    null
                }
            }

        val white17: Int?
            get() {
                return try {
                    Color.parseColor("#F0F0F0")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#F0F0F0' for 'white_17'")
                    null
                }
            }

        val white18: Int?
            get() {
                return try {
                    Color.parseColor("#FFFFCC")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#FFFFCC' for 'white_18'")
                    null
                }
            }

        val white19: Int?
            get() {
                return try {
                    Color.parseColor("#FFE5E5")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#FFE5E5' for 'white_19'")
                    null
                }
            }

        val white2: Int?
            get() {
                return try {
                    Color.parseColor("#FFFFD0")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#FFFFD0' for 'white_2'")
                    null
                }
            }

        val white20: Int?
            get() {
                return try {
                    Color.parseColor("#E8F4FD")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#E8F4FD' for 'white_20'")
                    null
                }
            }

        val white21: Int?
            get() {
                return try {
                    Color.parseColor("#FFF4E6")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#FFF4E6' for 'white_21'")
                    null
                }
            }

        val white22: Int?
            get() {
                return try {
                    Color.parseColor("#E0FFFF")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#E0FFFF' for 'white_22'")
                    null
                }
            }

        val white23: Int?
            get() {
                return try {
                    Color.parseColor("#F5F5F5")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#F5F5F5' for 'white_23'")
                    null
                }
            }

        val white24: Int?
            get() {
                return try {
                    Color.parseColor("#E8E8E8")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#E8E8E8' for 'white_24'")
                    null
                }
            }

        val white25: Int?
            get() {
                return try {
                    Color.parseColor("#FFF0E0")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#FFF0E0' for 'white_25'")
                    null
                }
            }

        val white26: Int?
            get() {
                return try {
                    Color.parseColor("#F0E0FF")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#F0E0FF' for 'white_26'")
                    null
                }
            }

        val white27: Int?
            get() {
                return try {
                    Color.parseColor("#FAFAFA")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#FAFAFA' for 'white_27'")
                    null
                }
            }

        val white28: Int?
            get() {
                return try {
                    Color.parseColor("#F9F9F9")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#F9F9F9' for 'white_28'")
                    null
                }
            }

        val white3: Int?
            get() {
                return try {
                    Color.parseColor("#FFD0FF")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#FFD0FF' for 'white_3'")
                    null
                }
            }

        val white4: Int?
            get() {
                return try {
                    Color.parseColor("#D0FFFF")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#D0FFFF' for 'white_4'")
                    null
                }
            }

        val white5: Int?
            get() {
                return try {
                    Color.parseColor("#FFE0E0")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#FFE0E0' for 'white_5'")
                    null
                }
            }

        val white6: Int?
            get() {
                return try {
                    Color.parseColor("#E0FFE0")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#E0FFE0' for 'white_6'")
                    null
                }
            }

        val white7: Int?
            get() {
                return try {
                    Color.parseColor("#E0E0FF")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#E0E0FF' for 'white_7'")
                    null
                }
            }

        val white8: Int?
            get() {
                return try {
                    Color.parseColor("#FFFFE0")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#FFFFE0' for 'white_8'")
                    null
                }
            }

        val white9: Int?
            get() {
                return try {
                    Color.parseColor("#FFE0FF")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color format '#FFE0FF' for 'white_9'")
                    null
                }
            }

    }

    // Jetpack Compose colors
    object compose {
        // Get Compose Color by key (returns null for binding expressions like @{...})
        fun color(key: String): ComposeColor? {
            // Skip binding expressions
            if (key.startsWith("@{") && key.endsWith("}")) {
                return null
            }
            val androidColor = views.color(key) ?: return null
            return ComposeColor(androidColor)
        }

        val Color.Green: ComposeColor?
            get() {
                val androidColor = views.Color.Green ?: return null
                return ComposeColor(androidColor)
            }

        val black: ComposeColor?
            get() {
                val androidColor = views.black ?: return null
                return ComposeColor(androidColor)
            }

        val black2: ComposeColor?
            get() {
                val androidColor = views.black2 ?: return null
                return ComposeColor(androidColor)
            }

        val darkBlue: ComposeColor?
            get() {
                val androidColor = views.darkBlue ?: return null
                return ComposeColor(androidColor)
            }

        val darkGray: ComposeColor?
            get() {
                val androidColor = views.darkGray ?: return null
                return ComposeColor(androidColor)
            }

        val darkGreen: ComposeColor?
            get() {
                val androidColor = views.darkGreen ?: return null
                return ComposeColor(androidColor)
            }

        val darkGreen2: ComposeColor?
            get() {
                val androidColor = views.darkGreen2 ?: return null
                return ComposeColor(androidColor)
            }

        val darkGreen3: ComposeColor?
            get() {
                val androidColor = views.darkGreen3 ?: return null
                return ComposeColor(androidColor)
            }

        val darkPurple: ComposeColor?
            get() {
                val androidColor = views.darkPurple ?: return null
                return ComposeColor(androidColor)
            }

        val darkRed: ComposeColor?
            get() {
                val androidColor = views.darkRed ?: return null
                return ComposeColor(androidColor)
            }

        val light: ComposeColor?
            get() {
                val androidColor = views.light ?: return null
                return ComposeColor(androidColor)
            }

        val light2: ComposeColor?
            get() {
                val androidColor = views.light2 ?: return null
                return ComposeColor(androidColor)
            }

        val lightBlue: ComposeColor?
            get() {
                val androidColor = views.lightBlue ?: return null
                return ComposeColor(androidColor)
            }

        val lightBlue2: ComposeColor?
            get() {
                val androidColor = views.lightBlue2 ?: return null
                return ComposeColor(androidColor)
            }

        val lightBlue3: ComposeColor?
            get() {
                val androidColor = views.lightBlue3 ?: return null
                return ComposeColor(androidColor)
            }

        val lightCyan: ComposeColor?
            get() {
                val androidColor = views.lightCyan ?: return null
                return ComposeColor(androidColor)
            }

        val lightCyan2: ComposeColor?
            get() {
                val androidColor = views.lightCyan2 ?: return null
                return ComposeColor(androidColor)
            }

        val lightGray: ComposeColor?
            get() {
                val androidColor = views.lightGray ?: return null
                return ComposeColor(androidColor)
            }

        val lightGray10: ComposeColor?
            get() {
                val androidColor = views.lightGray10 ?: return null
                return ComposeColor(androidColor)
            }

        val lightGray2: ComposeColor?
            get() {
                val androidColor = views.lightGray2 ?: return null
                return ComposeColor(androidColor)
            }

        val lightGray3: ComposeColor?
            get() {
                val androidColor = views.lightGray3 ?: return null
                return ComposeColor(androidColor)
            }

        val lightGray4: ComposeColor?
            get() {
                val androidColor = views.lightGray4 ?: return null
                return ComposeColor(androidColor)
            }

        val lightGray5: ComposeColor?
            get() {
                val androidColor = views.lightGray5 ?: return null
                return ComposeColor(androidColor)
            }

        val lightGray6: ComposeColor?
            get() {
                val androidColor = views.lightGray6 ?: return null
                return ComposeColor(androidColor)
            }

        val lightGray7: ComposeColor?
            get() {
                val androidColor = views.lightGray7 ?: return null
                return ComposeColor(androidColor)
            }

        val lightGray8: ComposeColor?
            get() {
                val androidColor = views.lightGray8 ?: return null
                return ComposeColor(androidColor)
            }

        val lightGray9: ComposeColor?
            get() {
                val androidColor = views.lightGray9 ?: return null
                return ComposeColor(androidColor)
            }

        val lightGreen: ComposeColor?
            get() {
                val androidColor = views.lightGreen ?: return null
                return ComposeColor(androidColor)
            }

        val lightGreen2: ComposeColor?
            get() {
                val androidColor = views.lightGreen2 ?: return null
                return ComposeColor(androidColor)
            }

        val lightLime: ComposeColor?
            get() {
                val androidColor = views.lightLime ?: return null
                return ComposeColor(androidColor)
            }

        val lightOrange: ComposeColor?
            get() {
                val androidColor = views.lightOrange ?: return null
                return ComposeColor(androidColor)
            }

        val lightOrange2: ComposeColor?
            get() {
                val androidColor = views.lightOrange2 ?: return null
                return ComposeColor(androidColor)
            }

        val lightPurple: ComposeColor?
            get() {
                val androidColor = views.lightPurple ?: return null
                return ComposeColor(androidColor)
            }

        val lightRed: ComposeColor?
            get() {
                val androidColor = views.lightRed ?: return null
                return ComposeColor(androidColor)
            }

        val lightRed2: ComposeColor?
            get() {
                val androidColor = views.lightRed2 ?: return null
                return ComposeColor(androidColor)
            }

        val lightRed3: ComposeColor?
            get() {
                val androidColor = views.lightRed3 ?: return null
                return ComposeColor(androidColor)
            }

        val lightRed4: ComposeColor?
            get() {
                val androidColor = views.lightRed4 ?: return null
                return ComposeColor(androidColor)
            }

        val lightRed5: ComposeColor?
            get() {
                val androidColor = views.lightRed5 ?: return null
                return ComposeColor(androidColor)
            }

        val lightRed6: ComposeColor?
            get() {
                val androidColor = views.lightRed6 ?: return null
                return ComposeColor(androidColor)
            }

        val lightYellow: ComposeColor?
            get() {
                val androidColor = views.lightYellow ?: return null
                return ComposeColor(androidColor)
            }

        val mediumBlue: ComposeColor?
            get() {
                val androidColor = views.mediumBlue ?: return null
                return ComposeColor(androidColor)
            }

        val mediumBlue2: ComposeColor?
            get() {
                val androidColor = views.mediumBlue2 ?: return null
                return ComposeColor(androidColor)
            }

        val mediumBlue3: ComposeColor?
            get() {
                val androidColor = views.mediumBlue3 ?: return null
                return ComposeColor(androidColor)
            }

        val mediumBlue4: ComposeColor?
            get() {
                val androidColor = views.mediumBlue4 ?: return null
                return ComposeColor(androidColor)
            }

        val mediumBlue5: ComposeColor?
            get() {
                val androidColor = views.mediumBlue5 ?: return null
                return ComposeColor(androidColor)
            }

        val mediumCyan: ComposeColor?
            get() {
                val androidColor = views.mediumCyan ?: return null
                return ComposeColor(androidColor)
            }

        val mediumGray: ComposeColor?
            get() {
                val androidColor = views.mediumGray ?: return null
                return ComposeColor(androidColor)
            }

        val mediumGray2: ComposeColor?
            get() {
                val androidColor = views.mediumGray2 ?: return null
                return ComposeColor(androidColor)
            }

        val mediumGray3: ComposeColor?
            get() {
                val androidColor = views.mediumGray3 ?: return null
                return ComposeColor(androidColor)
            }

        val mediumGray4: ComposeColor?
            get() {
                val androidColor = views.mediumGray4 ?: return null
                return ComposeColor(androidColor)
            }

        val mediumGray5: ComposeColor?
            get() {
                val androidColor = views.mediumGray5 ?: return null
                return ComposeColor(androidColor)
            }

        val mediumGreen: ComposeColor?
            get() {
                val androidColor = views.mediumGreen ?: return null
                return ComposeColor(androidColor)
            }

        val mediumGreen2: ComposeColor?
            get() {
                val androidColor = views.mediumGreen2 ?: return null
                return ComposeColor(androidColor)
            }

        val mediumGreen3: ComposeColor?
            get() {
                val androidColor = views.mediumGreen3 ?: return null
                return ComposeColor(androidColor)
            }

        val mediumLime: ComposeColor?
            get() {
                val androidColor = views.mediumLime ?: return null
                return ComposeColor(androidColor)
            }

        val mediumLime2: ComposeColor?
            get() {
                val androidColor = views.mediumLime2 ?: return null
                return ComposeColor(androidColor)
            }

        val mediumPurple: ComposeColor?
            get() {
                val androidColor = views.mediumPurple ?: return null
                return ComposeColor(androidColor)
            }

        val mediumRed: ComposeColor?
            get() {
                val androidColor = views.mediumRed ?: return null
                return ComposeColor(androidColor)
            }

        val mediumRed2: ComposeColor?
            get() {
                val androidColor = views.mediumRed2 ?: return null
                return ComposeColor(androidColor)
            }

        val mediumRed3: ComposeColor?
            get() {
                val androidColor = views.mediumRed3 ?: return null
                return ComposeColor(androidColor)
            }

        val mediumRed4: ComposeColor?
            get() {
                val androidColor = views.mediumRed4 ?: return null
                return ComposeColor(androidColor)
            }

        val mediumRed5: ComposeColor?
            get() {
                val androidColor = views.mediumRed5 ?: return null
                return ComposeColor(androidColor)
            }

        val mediumRed6: ComposeColor?
            get() {
                val androidColor = views.mediumRed6 ?: return null
                return ComposeColor(androidColor)
            }

        val pale: ComposeColor?
            get() {
                val androidColor = views.pale ?: return null
                return ComposeColor(androidColor)
            }

        val paleBlue: ComposeColor?
            get() {
                val androidColor = views.paleBlue ?: return null
                return ComposeColor(androidColor)
            }

        val paleBlue2: ComposeColor?
            get() {
                val androidColor = views.paleBlue2 ?: return null
                return ComposeColor(androidColor)
            }

        val paleBlue3: ComposeColor?
            get() {
                val androidColor = views.paleBlue3 ?: return null
                return ComposeColor(androidColor)
            }

        val paleCyan: ComposeColor?
            get() {
                val androidColor = views.paleCyan ?: return null
                return ComposeColor(androidColor)
            }

        val paleGray: ComposeColor?
            get() {
                val androidColor = views.paleGray ?: return null
                return ComposeColor(androidColor)
            }

        val paleGray2: ComposeColor?
            get() {
                val androidColor = views.paleGray2 ?: return null
                return ComposeColor(androidColor)
            }

        val paleGray3: ComposeColor?
            get() {
                val androidColor = views.paleGray3 ?: return null
                return ComposeColor(androidColor)
            }

        val paleGray4: ComposeColor?
            get() {
                val androidColor = views.paleGray4 ?: return null
                return ComposeColor(androidColor)
            }

        val paleGray5: ComposeColor?
            get() {
                val androidColor = views.paleGray5 ?: return null
                return ComposeColor(androidColor)
            }

        val paleGray6: ComposeColor?
            get() {
                val androidColor = views.paleGray6 ?: return null
                return ComposeColor(androidColor)
            }

        val paleGreen: ComposeColor?
            get() {
                val androidColor = views.paleGreen ?: return null
                return ComposeColor(androidColor)
            }

        val paleGreen2: ComposeColor?
            get() {
                val androidColor = views.paleGreen2 ?: return null
                return ComposeColor(androidColor)
            }

        val paleGreen3: ComposeColor?
            get() {
                val androidColor = views.paleGreen3 ?: return null
                return ComposeColor(androidColor)
            }

        val palePink: ComposeColor?
            get() {
                val androidColor = views.palePink ?: return null
                return ComposeColor(androidColor)
            }

        val palePink2: ComposeColor?
            get() {
                val androidColor = views.palePink2 ?: return null
                return ComposeColor(androidColor)
            }

        val paleRed: ComposeColor?
            get() {
                val androidColor = views.paleRed ?: return null
                return ComposeColor(androidColor)
            }

        val paleRed2: ComposeColor?
            get() {
                val androidColor = views.paleRed2 ?: return null
                return ComposeColor(androidColor)
            }

        val paleRed3: ComposeColor?
            get() {
                val androidColor = views.paleRed3 ?: return null
                return ComposeColor(androidColor)
            }

        val paleYellow: ComposeColor?
            get() {
                val androidColor = views.paleYellow ?: return null
                return ComposeColor(androidColor)
            }

        val white: ComposeColor?
            get() {
                val androidColor = views.white ?: return null
                return ComposeColor(androidColor)
            }

        val white10: ComposeColor?
            get() {
                val androidColor = views.white10 ?: return null
                return ComposeColor(androidColor)
            }

        val white11: ComposeColor?
            get() {
                val androidColor = views.white11 ?: return null
                return ComposeColor(androidColor)
            }

        val white12: ComposeColor?
            get() {
                val androidColor = views.white12 ?: return null
                return ComposeColor(androidColor)
            }

        val white13: ComposeColor?
            get() {
                val androidColor = views.white13 ?: return null
                return ComposeColor(androidColor)
            }

        val white14: ComposeColor?
            get() {
                val androidColor = views.white14 ?: return null
                return ComposeColor(androidColor)
            }

        val white15: ComposeColor?
            get() {
                val androidColor = views.white15 ?: return null
                return ComposeColor(androidColor)
            }

        val white16: ComposeColor?
            get() {
                val androidColor = views.white16 ?: return null
                return ComposeColor(androidColor)
            }

        val white17: ComposeColor?
            get() {
                val androidColor = views.white17 ?: return null
                return ComposeColor(androidColor)
            }

        val white18: ComposeColor?
            get() {
                val androidColor = views.white18 ?: return null
                return ComposeColor(androidColor)
            }

        val white19: ComposeColor?
            get() {
                val androidColor = views.white19 ?: return null
                return ComposeColor(androidColor)
            }

        val white2: ComposeColor?
            get() {
                val androidColor = views.white2 ?: return null
                return ComposeColor(androidColor)
            }

        val white20: ComposeColor?
            get() {
                val androidColor = views.white20 ?: return null
                return ComposeColor(androidColor)
            }

        val white21: ComposeColor?
            get() {
                val androidColor = views.white21 ?: return null
                return ComposeColor(androidColor)
            }

        val white22: ComposeColor?
            get() {
                val androidColor = views.white22 ?: return null
                return ComposeColor(androidColor)
            }

        val white23: ComposeColor?
            get() {
                val androidColor = views.white23 ?: return null
                return ComposeColor(androidColor)
            }

        val white24: ComposeColor?
            get() {
                val androidColor = views.white24 ?: return null
                return ComposeColor(androidColor)
            }

        val white25: ComposeColor?
            get() {
                val androidColor = views.white25 ?: return null
                return ComposeColor(androidColor)
            }

        val white26: ComposeColor?
            get() {
                val androidColor = views.white26 ?: return null
                return ComposeColor(androidColor)
            }

        val white27: ComposeColor?
            get() {
                val androidColor = views.white27 ?: return null
                return ComposeColor(androidColor)
            }

        val white28: ComposeColor?
            get() {
                val androidColor = views.white28 ?: return null
                return ComposeColor(androidColor)
            }

        val white3: ComposeColor?
            get() {
                val androidColor = views.white3 ?: return null
                return ComposeColor(androidColor)
            }

        val white4: ComposeColor?
            get() {
                val androidColor = views.white4 ?: return null
                return ComposeColor(androidColor)
            }

        val white5: ComposeColor?
            get() {
                val androidColor = views.white5 ?: return null
                return ComposeColor(androidColor)
            }

        val white6: ComposeColor?
            get() {
                val androidColor = views.white6 ?: return null
                return ComposeColor(androidColor)
            }

        val white7: ComposeColor?
            get() {
                val androidColor = views.white7 ?: return null
                return ComposeColor(androidColor)
            }

        val white8: ComposeColor?
            get() {
                val androidColor = views.white8 ?: return null
                return ComposeColor(androidColor)
            }

        val white9: ComposeColor?
            get() {
                val androidColor = views.white9 ?: return null
                return ComposeColor(androidColor)
            }

    }
}

// Note: Color parsing extensions are provided by KotlinJsonUI library