package com.kotlinjsonui.core

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * The language the app shows text in: AppCompat's application locales
 * (the per-app language — the source [KotlinJsonUI.localizedString] reads),
 * then the device's default.
 *
 * Until 2.43.3 the date pickers and the Dynamic strings cache took
 * `Locale.getDefault()`, so month names, the calendar and digits came from
 * the device when the app's language was set to another one.
 */
object AppLocale {
    /** The app-language locale for text the user reads. */
    fun current(): Locale =
        resolve(AppCompatDelegate.getApplicationLocales(), Locale.getDefault())

    /** [applicationLocales]' first locale when it holds one, else [device]. */
    fun resolve(applicationLocales: LocaleListCompat, device: Locale): Locale =
        if (applicationLocales.isEmpty) device else applicationLocales[0] ?: device
}

/**
 * The two kinds of date format the library uses, one rule each.
 *
 * - [display]: text the user reads (a date picker's label) — the app's
 *   language ([AppLocale]).
 * - [value]: a stored or bound value (`yyyy-MM-dd`, `HH:mm`) — [Locale.ROOT],
 *   so a value written in one language parses in every other (the device
 *   locale gave a Thai or Arabic device its own calendar or digits in the
 *   value itself).
 */
object DateFormats {
    fun display(pattern: String, locale: Locale = AppLocale.current()): SimpleDateFormat =
        SimpleDateFormat(pattern, locale)

    fun value(pattern: String): SimpleDateFormat = SimpleDateFormat(pattern, Locale.ROOT)

    /**
     * Read a stored value in [Locale.ROOT] — what this version writes, and what
     * reads the values written before 2.43.3 too: those were formatted with
     * Locale.getDefault(), and on Android (API 35, measured 2026-10-04) that
     * changed only the digits (Arabic-Indic, Persian, Bengali, Thai, Myanmar,
     * Devanagari), which a ROOT parse reads; th-TH and ja-JP-u-ca-japanese
     * wrote Gregorian years. Null when it does not read.
     */
    fun parseValue(text: String, pattern: String): Date? =
        parseFirst(text, pattern, listOf(Locale.ROOT))

    /**
     * Read text in display form (a value an app keeps as the label it shows):
     * the app's language first, then the device locale — the language a
     * label was written in before 2.43.3. Null when neither reads it.
     */
    fun parseDisplay(
        text: String,
        pattern: String,
        app: Locale = AppLocale.current(),
        device: Locale = Locale.getDefault(),
    ): Date? = parseFirst(text, pattern, listOf(app, device))

    private fun parseFirst(text: String, pattern: String, locales: List<Locale>): Date? {
        for (locale in locales.distinct()) {
            try {
                SimpleDateFormat(pattern, locale).parse(text)?.let { return it }
            } catch (_: ParseException) {
                // the next locale
            }
        }
        return null
    }
}
