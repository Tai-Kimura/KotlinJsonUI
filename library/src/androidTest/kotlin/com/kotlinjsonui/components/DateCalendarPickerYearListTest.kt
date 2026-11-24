package com.kotlinjsonui.components

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

/**
 * The year list hides the day grid. material3's DatePicker draws the year list OVER the weekdays and days and covers
 * them with its containerColor; DateCalendarPicker passed Color.Transparent, so after a tap on the month/year header
 * the days showed through the years (KotlinJsonUI 2.65.0 and earlier). The container is the sheet's colour now.
 *
 * Read on the Thursday weekday cell: Thursday's column lies between the year list's second and third columns at any
 * width (4.5/7 of the width; the year columns are at 1/2 and 5/6), so after the switch that cell shows only the
 * sheet's colour — none of its letter.
 */
@RunWith(AndroidJUnit4::class)
class DateCalendarPickerYearListTest {
    @get:Rule val rule = createComposeRule()

    private val sheet = Color(0xFFFF0000)

    private fun calendar(year: Int, month: Int, day: Int) = Calendar.getInstance().apply { clear(); set(year, month, day) }

    @Test
    fun the_year_list_hides_the_days() {
        val selected = calendar(2026, Calendar.NOVEMBER, 24)
        rule.setContent {
            MaterialTheme {
                Box(Modifier.testTag("picker").background(sheet)) {
                    DateCalendarPicker(
                        selectedDate = selected,
                        onDateSelected = { _, _, _ -> },
                        minDate = calendar(2020, Calendar.JANUARY, 1),
                        maxDate = calendar(2030, Calendar.DECEMBER, 31),
                        dateFormat = "yyyy-MM-dd",
                        modifier = Modifier.width(360.dp),
                        sheetTextColor = Color.Black,
                        sheetBackgroundColor = sheet
                    )
                }
            }
        }
        rule.waitForIdle()

        val locale = Locale.getDefault()
        val thursday = DateFormatSymbols.getInstance(locale).weekdays[Calendar.THURSDAY]
        val cell = rule.onNodeWithContentDescription(thursday).fetchSemanticsNode().boundsInRoot
        val origin = rule.onNodeWithTag("picker").fetchSemanticsNode().boundsInRoot
        assertTrue("the Thursday letter is drawn before the switch", inkIn(cell, origin) > 0)

        val header = SimpleDateFormat(DateFormat.getBestDateTimePattern(locale, "yMMMM"), locale).format(selected.time)
        rule.onNodeWithText(header).performClick()
        rule.waitForIdle()

        assertEquals("no letter of the day grid shows through the year list", 0, inkIn(cell, origin))
    }

    /** Pixels in `cell` (root coordinates) that are not the sheet's colour. */
    private fun inkIn(cell: androidx.compose.ui.geometry.Rect, origin: androidx.compose.ui.geometry.Rect): Int {
        val map = rule.onNodeWithTag("picker").captureToImage().toPixelMap()
        val x0 = (cell.left - origin.left).roundToInt().coerceIn(0, map.width - 1)
        val x1 = (cell.right - origin.left).roundToInt().coerceIn(0, map.width)
        val y0 = (cell.top - origin.top).roundToInt().coerceIn(0, map.height - 1)
        val y1 = (cell.bottom - origin.top).roundToInt().coerceIn(0, map.height)
        var n = 0
        for (y in y0 until y1) for (x in x0 until x1) {
            val c = map[x, y]
            if (!(c.red > 0.9f && c.green < 0.15f && c.blue < 0.15f)) n++
        }
        return n
    }
}
