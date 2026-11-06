package com.kotlinjsonui.components

import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.core.os.LocaleListCompat
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import com.kotlinjsonui.R
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * A device set to [deviceLanguage], as a screen reads it: the window's own context. A bottom sheet is a window of its
 * own whose LocalContext is that window's, so a CompositionLocal around it does not reach it (measured: English on all).
 */
class SheetStringsProbeActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        val configuration = Configuration(newBase.resources.configuration)
        configuration.setLocales(LocaleList.forLanguageTags(deviceLanguage))
        super.attachBaseContext(newBase.createConfigurationContext(configuration))
    }

    companion object {
        var deviceLanguage = "en"
    }
}

/** The sheets on a device: the words follow the language; a layout's and an app's own word win. */
class SheetStringsLanguageTest {

    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    private fun open(tag: String, content: @Composable () -> Unit, read: () -> Unit) {
        SheetStringsProbeActivity.deviceLanguage = tag
        ActivityScenario.launch(SheetStringsProbeActivity::class.java).use { scenario ->
            scenario.onActivity { it.setContent(content = content) }
            composeTestRule.onNodeWithText("open").performClick()
            read()
        }
    }

    private fun selectBoxSheet(tag: String, title: String, cancel: String) = open(tag, {
        SelectBox(value = "", onValueChange = {}, options = listOf("a", "b"), placeholder = "open")
    }) {
        composeTestRule.onNodeWithText(title).assertIsDisplayed()
        composeTestRule.onNodeWithText(cancel).assertIsDisplayed()
    }

    private fun dateSheet(tag: String, title: String, cancel: String, confirm: String) = open(tag, {
        DateSelectBox(value = "", onValueChange = {}, datePickerMode = "time", placeholder = "open")
    }) {
        composeTestRule.onNodeWithText(title).assertIsDisplayed()
        composeTestRule.onNodeWithText(cancel).assertIsDisplayed()
        composeTestRule.onNodeWithText(confirm).assertIsDisplayed()
    }

    @Test fun selectBoxInEnglish() = selectBoxSheet("en", "Select an option", "Cancel")
    @Test fun selectBoxInJapaneseIsAsItWas() = selectBoxSheet("ja", "選択してください", "キャンセル")
    @Test fun selectBoxInTraditionalChinese() = selectBoxSheet("zh-Hant", "請選擇", "取消")
    @Test fun selectBoxOnATaiwanDevice() = selectBoxSheet("zh-TW", "請選擇", "取消")
    @Test fun selectBoxInSimplifiedChinese() = selectBoxSheet("zh-CN", "请选择", "取消")
    @Test fun selectBoxInLatinAmericanSpanish() = selectBoxSheet("es-MX", "Selecciona una opción", "Cancelar")
    @Test fun selectBoxInEuropeanSpanish() = selectBoxSheet("es-ES", "Selecciona una opción", "Cancelar")
    @Test fun selectBoxInBrazilianPortuguese() = selectBoxSheet("pt-BR", "Selecione uma opção", "Cancelar")
    @Test fun selectBoxInEuropeanPortuguese() = selectBoxSheet("pt-PT", "Selecione uma opção", "Cancelar")
    @Test fun selectBoxInGerman() = selectBoxSheet("de", "Option auswählen", "Abbrechen")
    @Test fun selectBoxInKorean() = selectBoxSheet("ko", "선택하세요", "취소")

    /** A language the library does not have: English, not Japanese. */
    @Test fun selectBoxInALanguageTheLibraryLacks() = selectBoxSheet("th", "Select an option", "Cancel")

    @Test fun dateSheetInEnglish() = dateSheet("en", "Select Time", "Cancel", "OK")
    @Test fun dateSheetInJapaneseIsAsItWas() = dateSheet("ja", "時刻を選択", "キャンセル", "確定")
    @Test fun dateSheetInFrench() = dateSheet("fr", "Sélectionner l'heure", "Annuler", "OK")

    /** A per-app language set through AppCompat comes before the device's. */
    @Test
    fun theAppsLanguageComesBeforeTheDevices() {
        SheetStringsProbeActivity.deviceLanguage = "en"
        ActivityScenario.launch(SheetStringsProbeActivity::class.java).use { scenario ->
            val instrumentation = InstrumentationRegistry.getInstrumentation()
            var first: SheetStringsProbeActivity? = null
            scenario.onActivity { first = it }
            instrumentation.runOnMainSync { AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("ko")) }
            try {
                // Setting the language recreates the activity: what is drawn before that is gone with the old one.
                composeTestRule.waitUntil(10_000) {
                    var now: SheetStringsProbeActivity? = null
                    scenario.onActivity { now = it }
                    now !== first
                }
                scenario.onActivity {
                    it.setContent { SelectBox(value = "", onValueChange = {}, options = listOf("a"), placeholder = "open") }
                }
                composeTestRule.onNodeWithText("open").performClick()
                composeTestRule.onNodeWithText("선택하세요").assertIsDisplayed()
            } finally {
                // While the activity is there: with no AppCompat activity alive AppCompat cannot reach the setting,
                // and the language stays for every test after this one (measured).
                instrumentation.runOnMainSync {
                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
                }
                instrumentation.waitForIdleSync()
            }
        }
    }

    /** The attributes: a layout's words come before the library's, whatever the language. */
    @Test
    fun aLayoutsWordsComeFirstOnAList() = open("ja", {
        SelectBox(
            value = "", onValueChange = {}, options = listOf("a"), placeholder = "open",
            sheetTitle = "Pick a store", cancelButtonText = "Back",
        )
    }) {
        composeTestRule.onNodeWithText("Pick a store").assertIsDisplayed()
        composeTestRule.onNodeWithText("Back").assertIsDisplayed()
    }

    @Test
    fun aLayoutsWordsComeFirstOnADate() = open("ja", {
        DateSelectBox(
            value = "", onValueChange = {}, datePickerMode = "time", placeholder = "open",
            sheetTitle = "When", cancelButtonText = "Back", doneButtonText = "Apply",
        )
    }) {
        composeTestRule.onNodeWithText("When").assertIsDisplayed()
        composeTestRule.onNodeWithText("Back").assertIsDisplayed()
        composeTestRule.onNodeWithText("Apply").assertIsDisplayed()
    }

    private fun dateWords(mode: String, style: String, title: String, cancel: String?, confirm: String, wordsGiven: Boolean) = open("ja", {
        if (wordsGiven) {
            DateSelectBox(
                value = "", onValueChange = {}, datePickerMode = mode, datePickerStyle = style, placeholder = "open",
                sheetTitle = "When", cancelButtonText = "Back", doneButtonText = "Apply",
            )
        } else {
            DateSelectBox(value = "", onValueChange = {}, datePickerMode = mode, datePickerStyle = style, placeholder = "open")
        }
    }) {
        composeTestRule.onNodeWithText(if (wordsGiven) "When" else title).assertIsDisplayed()
        if (cancel != null) composeTestRule.onNodeWithText(if (wordsGiven) "Back" else cancel).assertIsDisplayed()
        composeTestRule.onNodeWithText(if (wordsGiven) "Apply" else confirm).assertIsDisplayed()
    }

    /** The date sheet is written once per mode and style: every one takes the words, and keeps its Japanese. */
    @Test fun timeWheelWords() = dateWords("time", "wheels", "時刻を選択", "キャンセル", "確定", true)
    @Test fun timeCompactWords() = dateWords("time", "compact", "時刻を選択", "キャンセル", "確定", true)
    @Test fun dateAndTimeWheelWords() = dateWords("dateAndTime", "wheels", "日付と時刻を選択", "キャンセル", "確定", true)
    @Test fun dateAndTimeCompactWords() = dateWords("dateAndTime", "compact", "日付と時刻を選択", "キャンセル", "確定", true)
    @Test fun dateWheelWords() = dateWords("date", "wheels", "日付を選択", "キャンセル", "確定", true)
    @Test fun dateCalendarWords() = dateWords("date", "compact", "日付を選択", null, "選択", true)
    @Test fun timeWheelInJapanese() = dateWords("time", "wheels", "時刻を選択", "キャンセル", "確定", false)
    @Test fun dateAndTimeWheelInJapanese() = dateWords("dateAndTime", "wheels", "日付と時刻を選択", "キャンセル", "確定", false)
    @Test fun dateAndTimeCompactInJapanese() = dateWords("dateAndTime", "compact", "日付と時刻を選択", "キャンセル", "確定", false)
    @Test fun dateWheelInJapanese() = dateWords("date", "wheels", "日付を選択", "キャンセル", "確定", false)
    @Test fun dateCalendarInJapanese() = dateWords("date", "compact", "日付を選択", null, "選択", false)

    @Test
    fun anAppsOwnWordWins() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("Day (the app's word)", context.getString(R.string.kjui_sheet_day))
    }
}
