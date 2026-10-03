package com.kotlinjsonui.core

import androidx.core.os.LocaleListCompat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.util.Calendar
import java.util.Locale

/**
 * Text the user reads follows the app's language (AppCompat's application
 * locales), not the device's; stored values are Locale.ROOT. Until 2.43.3 the
 * date pickers formatted both with Locale.getDefault(), so an app set to
 * English on a Japanese device showed "10月".
 */
class AppLocaleTest {
    private val october = Calendar.getInstance().apply { clear(); set(2026, Calendar.OCTOBER, 4, 9, 5) }.time

    @Test
    fun theAppLanguageWinsOverTheDevice() {
        val locale = AppLocale.resolve(LocaleListCompat.forLanguageTags("en"), Locale.JAPAN)
        assertEquals("en", locale.language)
        assertEquals("October", DateFormats.display("MMMM", locale).format(october))
    }

    @Test
    fun noAppLanguageFallsBackToTheDevice() {
        val locale = AppLocale.resolve(LocaleListCompat.getEmptyLocaleList(), Locale.JAPAN)
        assertEquals(Locale.JAPAN, locale)
        assertEquals("10月", DateFormats.display("MMMM", locale).format(october))
    }

    @Test
    fun aStoredValueIsTheSameOnEveryDevice() {
        val saved = Locale.getDefault()
        try {
            Locale.setDefault(Locale.forLanguageTag("th-TH-u-ca-buddhist-nu-thai"))
            assertEquals("2026-10-04 09:05", DateFormats.value("yyyy-MM-dd HH:mm").format(october))
        } finally {
            Locale.setDefault(saved)
        }
    }

    // A label the app keeps and hands back: written in the app's language,
    // read back the same way (MMM is where a device-locale parse fails:
    // "Oct" is not a month to a Japanese parser).
    @Test
    fun aLabelInTheAppLanguageReadsBack() {
        val en = Locale.ENGLISH
        val label = DateFormats.display("MMM d, yyyy", en).format(october)
        assertEquals("Oct 4, 2026", label)
        assertEquals(dayOf(october), dayOf(DateFormats.parseDisplay(label, "MMM d, yyyy", app = en, device = Locale.JAPAN)))
    }

    // ...and a label written in the device's language before 2.43.3 still reads.
    @Test
    fun aLabelInTheDeviceLanguageStillReads() {
        val label = java.text.SimpleDateFormat("MMM d, yyyy", Locale.JAPAN).format(october)
        assertEquals(dayOf(october), dayOf(DateFormats.parseDisplay(label, "MMM d, yyyy", app = Locale.ENGLISH, device = Locale.JAPAN)))
    }

    // A value 2.43.2 wrote on an Arabic device (Locale.getDefault(): Arabic-Indic
    // digits) and one this version writes both read as the same date in ROOT.
    @Test
    fun aValueFromEitherWriterReads() {
        val old = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.forLanguageTag("ar-EG")).format(october)
        assertEquals("٢٠٢٦-١٠-٠٤", old)
        assertEquals(dayOf(october), dayOf(DateFormats.parseValue(old, "yyyy-MM-dd")))
        assertEquals(dayOf(october), dayOf(DateFormats.parseValue("2026-10-04", "yyyy-MM-dd")))
    }

    private fun dayOf(date: java.util.Date?): String? =
        date?.let { java.text.SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(it) }

    // The date pickers and the Dynamic strings cache take their locale from
    // AppLocale / DateFormats only: no Locale.getDefault(), bare
    // SimpleDateFormat or bare parse left in the files that produced displayed text.
    @Test
    fun theDisplayingFilesTakeNoDeviceLocale() {
        val files = listOf(
            "src/main/kotlin/com/kotlinjsonui/components/DateSelectBox.kt",
            "src/main/kotlin/com/kotlinjsonui/components/SimpleDateSelectBox.kt",
            "src/main/kotlin/com/kotlinjsonui/views/DatePickerBottomSheet.kt",
            "../library-dynamic/src/main/kotlin/com/kotlinjsonui/dynamic/ResourceCache.kt",
        ).map { File(it) }
        files.forEach { assertTrue("${it.path} is where it was", it.isFile) }
        val offenders = files.filter { f ->
            f.readText().let { "Locale.getDefault()" in it || "SimpleDateFormat(" in it || Regex("""\.parse\(""").containsMatchIn(it) }
        }
        assertEquals(emptyList<File>(), offenders)
    }
}
