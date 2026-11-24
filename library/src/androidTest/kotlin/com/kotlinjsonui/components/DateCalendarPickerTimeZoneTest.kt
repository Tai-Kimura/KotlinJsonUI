package com.kotlinjsonui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Calendar
import java.util.TimeZone

/**
 * The calendar's day is the box's day in every time zone (ticket 2026-11-24-kjui-dateselectbox-calendar-shifts-the-
 * day-by-the-time-zone). material3 holds a day as its UTC 00:00 millis; the box's Calendar is the device's zone.
 * Handed over as they were, east of UTC (Asia/Tokyo) the calendar opened on the day before and Select alone moved
 * the value a day back; west of UTC (America/Los_Angeles) a pressed day came back as the day before. UTC is the
 * control: it never moved, which is why CI's emulator never showed it.
 *
 * Each zone is read twice:
 *  - open on 2025-11-24 and press Select alone: the value stays 2025-11-24;
 *  - open on 2025-11-24, press the 12th and Select: the value is 2025-11-12.
 */
@RunWith(AndroidJUnit4::class)
class DateCalendarPickerTimeZoneTest {
    @get:Rule val rule = createComposeRule()

    private val before: TimeZone = TimeZone.getDefault()

    @After fun restoreZone() = TimeZone.setDefault(before)

    /** (year, month 0-based, day) the picker gave back after [press] and Select, opened on 2025-11-24 in [zone]. */
    private fun pick(zone: String, press: (() -> Unit)? = null): Triple<Int, Int, Int>? {
        TimeZone.setDefault(TimeZone.getTimeZone(zone))
        val opened = Calendar.getInstance().apply { clear(); set(2025, Calendar.NOVEMBER, 24) }
        val min = Calendar.getInstance().apply { clear(); set(2020, Calendar.JANUARY, 1) }
        val max = Calendar.getInstance().apply { clear(); set(2030, Calendar.DECEMBER, 31) }
        var got: Triple<Int, Int, Int>? = null
        rule.setContent {
            MaterialTheme {
                DateCalendarPicker(
                    selectedDate = opened,
                    onDateSelected = { y, m, d -> got = Triple(y, m, d) },
                    minDate = min,
                    maxDate = max,
                    dateFormat = "yyyy-MM-dd",
                )
            }
        }
        rule.waitForIdle()
        press?.invoke()
        rule.waitForIdle()
        rule.onNodeWithTag("kjui_x7q_done").performClick()
        rule.waitForIdle()
        return got
    }

    private fun pressThe12th() {
        // A day cell reads as its whole date ("Wednesday, November 12, 2025" on an en-US device).
        rule.onNode(hasText("November 12, 2025", substring = true) and hasClickAction()).performClick()
    }

    private val the24th = Triple(2025, Calendar.NOVEMBER, 24)
    private val the12th = Triple(2025, Calendar.NOVEMBER, 12)

    @Test fun select_alone_keeps_the_day_in_tokyo() = assertEquals(the24th, pick("Asia/Tokyo"))
    @Test fun select_alone_keeps_the_day_in_utc() = assertEquals(the24th, pick("UTC"))
    @Test fun select_alone_keeps_the_day_in_los_angeles() = assertEquals(the24th, pick("America/Los_Angeles"))

    @Test fun a_pressed_day_is_the_value_in_tokyo() = assertEquals(the12th, pick("Asia/Tokyo", ::pressThe12th))
    @Test fun a_pressed_day_is_the_value_in_utc() = assertEquals(the12th, pick("UTC", ::pressThe12th))
    @Test fun a_pressed_day_is_the_value_in_los_angeles() =
        assertEquals(the12th, pick("America/Los_Angeles", ::pressThe12th))
}
