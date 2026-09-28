package com.kotlinjsonui.core

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * A container's tintColor reaches the controls inside it through
 * [LocalJsonUITint]; a control's own tintColor wins, and with neither the
 * control draws its own default.
 */
class JsonUITintTest {

    @Test
    fun aHandedDownTintWinsOverTheDefault() {
        assertEquals(Color.Red, tintOr(Color.Red, Color.Blue))
    }

    @Test
    fun noTintHandedDownIsTheDefault() {
        assertEquals(Color.Blue, tintOr(Color.Unspecified, Color.Blue))
    }

    private fun code(path: String): String =
        File("src/main/kotlin/com/kotlinjsonui/$path").readText()
            .lines().joinToString("\n") { it.substringBefore("//") }

    /** The library's own controls: their own colour first, the handed-down tint second. */
    @Test
    fun theLibraryControlsReadTheOwnColourFirst() {
        assertTrue(code("components/Segment.kt").contains("indicatorColor ?: jsonUITintOrNull() ?: Configuration.Segment.defaultSelectedBackgroundColor"))
        assertTrue(code("components/CustomTextField.kt").contains("val caretColor = cursorColor ?: jsonUITintOrNull()"))
        assertEquals(2, Regex("""SolidColor\(caretColor\)""").findAll(code("components/CustomTextField.kt")).count())
    }
}
