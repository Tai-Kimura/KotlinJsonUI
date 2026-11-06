package com.kotlinjsonui.components

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

/**
 * A word of the SelectBox and DateSelectBox sheets, in the language the app shows text in: AppCompat's application
 * locales, as KotlinJsonUI.localizedString reads them, then the context's. An app replaces one by declaring the same
 * name in its own values (kjui_sheet_strings.xml lists them).
 */
@Composable
internal fun sheetString(@StringRes id: Int): String {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val locales = AppCompatDelegate.getApplicationLocales()
    if (locales.isEmpty) return context.getString(id)
    val inApp = android.content.res.Configuration(configuration)
    inApp.setLocales(android.os.LocaleList.forLanguageTags(locales.toLanguageTags()))
    return context.createConfigurationContext(inApp).getString(id)
}
