@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package com.kotlinjsonui.dynamic

import android.os.SystemClock
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.ExtractedTextRequest
import android.view.inputmethod.InputConnection
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.InterceptPlatformTextInput
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A screen that holds a sheet's data and hands it to the sheet's generated view, with the
 * sheet's own view model beside it: `XGeneratedView(data = screenData, viewModel = sheetVm)`.
 * The handler is the screen's, in the data it passes; the sheet's view model has none.
 * Through jsonui-cli 2.5.9 the field called the passed data's handler; 2.5.10 and 2.5.11
 * called the sheet view model's (null), and the edit never reached the screen (jsonui-cli
 * ticket kjui-textfield-writeback-reads-handler-from-own-viewmodel).
 */
class ScreenVm(start: String, private val keeps: Boolean) {
    /** What the handler was called with, in order. */
    val heard = java.util.Collections.synchronizedList(mutableListOf<String>())
    private val _data = MutableStateFlow(FieldData(age = start, onAgeChange = ::onAgeChange))
    val data = _data

    /** keeps: the screen writes the value into the data it passes; else it only takes it. */
    private fun onAgeChange(v: String) {
        heard += v
        if (keeps) _data.update { it.copy(age = v) }
    }
}

enum class Holder { PAIR, SCREEN_KEEPS, SCREEN_TAKES }

@Composable
private fun Sheet(shape: Surface, holder: Holder, sheetVm: FieldVm, screen: ScreenVm) {
    val data by (if (holder == Holder.PAIR) sheetVm.data else screen.data).collectAsState()
    if (sheetVm.composeMillis > 0) { data.age.length; Thread.sleep(sheetVm.composeMillis) }
    when (shape) {
        Surface.CODEGEN_TEXTVIEW -> GeneratedTextViewShape(sheetVm, data)
        else -> GeneratedTextFieldShape(sheetVm, data)
    }
}

@RunWith(AndroidJUnit4::class)
class TextFieldPassedDataTest {
    private val instrumentation get() = InstrumentationRegistry.getInstrumentation()
    private fun main(block: () -> Unit) = instrumentation.runOnMainSync(block)

    private inner class Driver(val ic: InputConnection) {
        fun keys(t: String) = instrumentation.sendStringSync(t)
        fun commit(t: String) = main { ic.commitText(t, 1) }
        fun typed(t: String, gap: Long) = t.forEach { c -> commit(c.toString()); if (gap > 0) Thread.sleep(gap) }
        fun end() = main { val n = ic.getExtractedText(ExtractedTextRequest(), 0).text.length; ic.setSelection(n, n) }
        fun read(): String {
            var r = ""
            main { r = ic.getExtractedText(ExtractedTextRequest(), 0).text.toString() }
            return r
        }
    }

    /** The field and the last value the handler was given ("-": never called), once nothing has moved for 600 ms. */
    private fun drive(shape: Surface, holder: Holder, start: String, compose: Long = 0, steps: Driver.() -> Unit): String {
        val pair = holder == Holder.PAIR
        val sheetVm = FieldVm(if (pair) start else "", Answer.KEEP, compose, withHandler = pair)
        val screen = ScreenVm(start, holder == Holder.SCREEN_KEEPS)
        val heard = if (pair) sheetVm.heard else screen.heard
        var captured: InputConnection? = null
        val scenario = ActivityScenario.launch(ComponentActivity::class.java)
        try {
            scenario.onActivity { activity ->
                activity.setContentView(ComposeView(activity).apply {
                    setContent {
                        InterceptPlatformTextInput(interceptor = { request, _ ->
                            captured = request.createInputConnection(EditorInfo())
                            awaitCancellation()
                        }) { Sheet(shape, holder, sheetVm, screen) }
                    }
                })
            }
            instrumentation.waitForIdleSync()
            var x = 0f; var y = 0f
            scenario.onActivity { a -> val v = a.window.decorView; x = v.width / 2f; y = v.height / 2f }
            val t = SystemClock.uptimeMillis()
            instrumentation.sendPointerSync(MotionEvent.obtain(t, t, MotionEvent.ACTION_DOWN, x, y, 0))
            instrumentation.sendPointerSync(MotionEvent.obtain(t, t + 40, MotionEvent.ACTION_UP, x, y, 0))
            val deadline = SystemClock.uptimeMillis() + 15000
            while (captured == null && SystemClock.uptimeMillis() < deadline) Thread.sleep(30)
            val d = Driver(checkNotNull(captured) { "the field never opened an input session ($shape, $holder)" })
            fun state() = d.read() + "|" + (heard.lastOrNull() ?: "-")
            fun settled(): String {
                val until = SystemClock.uptimeMillis() + 8000
                var last = state(); var since = SystemClock.uptimeMillis()
                while (SystemClock.uptimeMillis() < until) {
                    Thread.sleep(40)
                    val now = state()
                    if (now != last) { last = now; since = SystemClock.uptimeMillis() }
                    if (SystemClock.uptimeMillis() - since >= 600) break
                }
                return last
            }
            settled()
            d.steps()
            return settled()
        } finally {
            sheetVm.close()
            scenario.close()
        }
    }

    private fun rows(shape: Surface, holder: Holder): List<Pair<String, String>> {
        fun row(name: String, want: String, start: String, compose: Long = 0, steps: Driver.() -> Unit): Pair<String, String> {
            val got = drive(shape, holder, start, compose, steps)
            Log.i("TFPASS", "$shape | $holder | $name | want=$want | got=$got | ${if (got == want) "ok" else "DIFF"}")
            return "$name: $want" to "$name: $got"
        }
        return listOf(
            row("the value the data holds is shown, and nothing is told on display", "a@b|-", "a@b") { },
            row("keys reach the handler", "spr|spr", "") { keys("spr") },
            row("a key after the shown value", "a@bc|a@bc", "a@b") { end(); keys("c") },
            row("keys faster than a slow screen", "spr|spr", "", compose = 100) { keys("spr") },
            row("one commit each, 30 ms apart", "spr|spr", "", compose = 100) { typed("spr", 30) }
        )
    }

    private fun check(shape: Surface, holder: Holder) {
        val r = rows(shape, holder)
        assertEquals(r.joinToString("\n") { it.first }, r.joinToString("\n") { it.second })
    }

    // The data and the view model one pair (what a generated host passes): the same rows.
    @Test fun textFieldPair() = check(Surface.CODEGEN_TEXTFIELD, Holder.PAIR)
    @Test fun textViewPair() = check(Surface.CODEGEN_TEXTVIEW, Holder.PAIR)
    // The screen's data and the sheet's own view model.
    @Test fun textFieldScreenKeeps() = check(Surface.CODEGEN_TEXTFIELD, Holder.SCREEN_KEEPS)
    @Test fun textFieldScreenTakes() = check(Surface.CODEGEN_TEXTFIELD, Holder.SCREEN_TAKES)
    @Test fun textViewScreenKeeps() = check(Surface.CODEGEN_TEXTVIEW, Holder.SCREEN_KEEPS)
    @Test fun textViewScreenTakes() = check(Surface.CODEGEN_TEXTVIEW, Holder.SCREEN_TAKES)
}
