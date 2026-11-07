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
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import com.kotlinjsonui.components.CustomTextField
import com.kotlinjsonui.components.rememberSyncedTextFieldState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.InterceptPlatformTextInput
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

data class FieldData(
    val age: String = "",
    val onAgeChange: ((String) -> Unit)? = null,
    val tfIsFocused: Boolean = false,
    val tvIsFocused: Boolean = false
)

/** How the view model answers an edit. */
enum class Answer { DIGITS_SYNC, DIGITS_IMMEDIATE, DIGITS_DEFAULT, DIGITS_DELAYED, KEEP, DROP }

/** The shape of a generated ViewModel: `updateData` writes the raw value, the handler may correct it. */
class FieldVm(start: String, private val answer: Answer) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val _data = MutableStateFlow(FieldData(age = start, onAgeChange = ::onAgeChange))
    val data = _data

    fun updateData(updates: Map<String, Any>) {
        _data.update { d ->
            var n = d
            for ((k, v) in updates) when (k) {
                "age" -> if (answer != Answer.DROP) n = n.copy(age = v as String)
                "tfIsFocused" -> n = n.copy(tfIsFocused = v as Boolean)
                "tvIsFocused" -> n = n.copy(tvIsFocused = v as Boolean)
            }
            n
        }
    }

    private fun digits(v: String) = _data.update { it.copy(age = v.filter(Char::isDigit)) }

    private fun onAgeChange(v: String) {
        when (answer) {
            Answer.DIGITS_SYNC -> digits(v)
            Answer.DIGITS_IMMEDIATE -> scope.launch { digits(v) }
            Answer.DIGITS_DEFAULT -> scope.launch(Dispatchers.Default) { digits(v) }
            Answer.DIGITS_DELAYED -> scope.launch { delay(100); digits(v) }
            Answer.KEEP, Answer.DROP -> Unit
        }
    }

    fun close() = scope.cancel()
}

enum class Surface { CODEGEN_TEXTFIELD, CODEGEN_TEXTVIEW, DYNAMIC_TEXTFIELD, DYNAMIC_TEXTVIEW, SYNCED_STATE_HELPER, DYNAMIC_NO_UPDATE_DATA }

@Composable
private fun SurfaceContent(surface: Surface, vm: FieldVm) {
    val data by vm.data.collectAsState()
    when (surface) {
        Surface.CODEGEN_TEXTFIELD -> GeneratedTextFieldShape(vm, data)
        Surface.CODEGEN_TEXTVIEW -> GeneratedTextViewShape(vm, data)
        Surface.SYNCED_STATE_HELPER -> CustomTextField(
            state = rememberSyncedTextFieldState(data.age) { v ->
                vm.updateData(mapOf("age" to v))
                data.onAgeChange?.invoke(v)
            },
            modifier = Modifier.fillMaxSize()
        )
        Surface.DYNAMIC_NO_UPDATE_DATA -> DynamicView(
            json = JsonParser.parseString(
                """{"type":"TextField","id":"f","width":"matchParent","height":"matchParent",
                    "text":"@{age}","onTextChange":"@{onAgeChange}"}"""
            ).asJsonObject,
            // What a generated host hands over: data.toMap(), with no updateData.
            data = buildMap { put("age", data.age); data.onAgeChange?.let { put("onAgeChange", it) } }
        )
        Surface.DYNAMIC_TEXTFIELD, Surface.DYNAMIC_TEXTVIEW -> {
            val type = if (surface == Surface.DYNAMIC_TEXTFIELD) "TextField" else "TextView"
            val map = mutableMapOf<String, Any>("age" to data.age, "updateData" to vm::updateData)
            data.onAgeChange?.let { map["onAgeChange"] = it }
            DynamicView(
                json = JsonParser.parseString(
                    """{"type":"$type","id":"f","width":"matchParent","height":"matchParent",
                        "text":"@{age}","onTextChange":"@{onAgeChange}"}"""
                ).asJsonObject,
                data = map
            )
        }
    }
}

/**
 * A field follows what the view model holds, also when the view model takes an
 * edit back to the value it held before the edit (jsonui-cli ticket
 * kjui-textfield-corrected-value-not-written-back).
 *
 * Runs on the real frame clock and the real main dispatcher: no compose test
 * rule, whose clock orders the frames itself, and edits go through the field's
 * own InputConnection, so composing text is the one an IME sends.
 */
@RunWith(AndroidJUnit4::class)
class TextFieldCorrectedValueTest {
    private val instrumentation get() = InstrumentationRegistry.getInstrumentation()

    private class Row(val name: String, val want: String, val got: String)

    private fun main(block: () -> Unit) = instrumentation.runOnMainSync(block)

    private inner class Driver(val ic: InputConnection, val vm: FieldVm) {
        fun commit(t: String) = main { ic.commitText(t, 1) }
        fun compose(t: String) = main { ic.setComposingText(t, 1) }
        fun finish() = main { ic.finishComposingText() }
        fun select(a: Int, b: Int) = main { ic.setSelection(a, b) }
        fun all() = main { ic.performContextMenuAction(android.R.id.selectAll) }
        fun read(): String {
            var r = ""
            main {
                val e = ic.getExtractedText(ExtractedTextRequest(), 0)
                r = "${e.text}|${e.selectionStart}"
            }
            return r
        }
        /** The state once nothing has moved for 600 ms, with the view model's value. */
        fun settled(): String {
            val deadline = SystemClock.uptimeMillis() + 8000
            var last = read() + "|" + vm.data.value.age
            var since = SystemClock.uptimeMillis()
            while (SystemClock.uptimeMillis() < deadline) {
                Thread.sleep(40)
                val now = read() + "|" + vm.data.value.age
                if (now != last) { last = now; since = SystemClock.uptimeMillis() }
                if (SystemClock.uptimeMillis() - since >= 600) break
            }
            return last
        }
    }

    private fun drive(surface: Surface, start: String, answer: Answer, steps: Driver.() -> Unit): String {
        val vm = FieldVm(start, answer)
        var captured: InputConnection? = null
        val scenario = ActivityScenario.launch(ComponentActivity::class.java)
        try {
            scenario.onActivity { activity ->
                activity.setContentView(ComposeView(activity).apply {
                    setContent {
                        InterceptPlatformTextInput(interceptor = { request, next ->
                            // The only connection: the test is the IME. Handing the
                            // request on as well opens the device keyboard's, which
                            // ends the composing region under the test.
                            captured = request.createInputConnection(EditorInfo())
                            awaitCancellation()
                        }) { SurfaceContent(surface, vm) }
                    }
                })
            }
            instrumentation.waitForIdleSync()
            var tapX = 0f; var tapY = 0f
            scenario.onActivity { a -> val v = a.window.decorView; tapX = v.width / 2f; tapY = v.height / 2f }
            val t = SystemClock.uptimeMillis()
            instrumentation.sendPointerSync(MotionEvent.obtain(t, t, MotionEvent.ACTION_DOWN, tapX, tapY, 0))
            instrumentation.sendPointerSync(MotionEvent.obtain(t, t + 40, MotionEvent.ACTION_UP, tapX, tapY, 0))
            val deadline = SystemClock.uptimeMillis() + 15000
            while (captured == null && SystemClock.uptimeMillis() < deadline) Thread.sleep(30)
            val d = Driver(checkNotNull(captured) { "the field never opened an input session on $surface" }, vm)
            d.settled()
            d.steps()
            return d.settled()
        } finally {
            vm.close()
            scenario.close()
        }
    }

    private fun rowsOf(surface: Surface): List<Row> {
        fun row(name: String, want: String, start: String, answer: Answer, steps: Driver.() -> Unit): Row {
            val got = drive(surface, start, answer, steps)
            Log.i("TFFAM", "$surface | $name | want=$want | got=$got | ${if (want == got) "ok" else "DIFF"}")
            return Row(name, want, got)
        }
        return listOf(
            // A correction back to the value held before the edit.
            row("same, handler corrects in the call", "12|2|12", "12", Answer.DIGITS_SYNC) { commit("年") },
            row("same, whole text replaced", "12|2|12", "12", Answer.DIGITS_SYNC) { all(); commit("12年") },
            row("same, launched on the main dispatcher", "12|2|12", "12", Answer.DIGITS_IMMEDIATE) { commit("年") },
            row("same, launched on another thread", "12|2|12", "12", Answer.DIGITS_DEFAULT) { commit("年") },
            row("same, 100 ms later", "12|2|12", "12", Answer.DIGITS_DELAYED) { commit("年") },
            // A correction to a new value: already written back.
            row("new value, handler corrects in the call", "3|1|3", "12", Answer.DIGITS_SYNC) { all(); commit("3年") },
            // The field stays usable after a correction.
            row("typing after a correction", "123|3|123", "12", Answer.DIGITS_SYNC) { commit("年"); settled(); commit("3") },
            // The view model takes the edit: the cursor and the IME are left alone.
            row("the next key right after a corrected one", "123|3|123", "12", Answer.DIGITS_SYNC) { commit("年"); commit("3") },
            row("typing kept", "123|3|123", "12", Answer.KEEP) { commit("3") },
            row("typing in the middle keeps the cursor", "12x34|3|12x34", "1234", Answer.KEEP) { select(2, 2); settled(); commit("x") },
            row("cursor moved, nothing typed", "1234|2|1234", "1234", Answer.KEEP) { select(2, 2) },
            row("a burst in one turn", "12abcd|6|12abcd", "12", Answer.KEEP) { main { ic.commitText("a", 1); ic.commitText("b", 1); ic.commitText("c", 1); ic.commitText("d", 1) } },
            row("composing keeps its region", "12にほん|5|12にほん", "12", Answer.KEEP) {
                compose("に"); settled(); compose("にほ"); settled(); compose("にほん"); settled(); finish()
            },
            row("composing in the middle", "1にほ2|3|1にほ2", "12", Answer.KEEP) {
                select(1, 1); settled(); compose("に"); settled(); compose("にほ"); settled(); finish()
            },
            // The view model refuses what is being composed: nothing is left behind.
            row("composing refused", "12|2|12", "12", Answer.DIGITS_SYNC) {
                compose("に"); settled(); compose("にほ"); settled(); finish()
            },
            // A view model that drops the value holds 12: the field shows what it holds.
            row("view model drops the write", "12|2|12", "12", Answer.DROP) { commit("3") }
        )
    }

    private fun check(surface: Surface) {
        val rows = rowsOf(surface)
        assertEquals(rows.joinToString("\n") { "${it.name}: ${it.want}" }, rows.joinToString("\n") { "${it.name}: ${it.got}" })
    }

    @Test fun codegenTextField() = check(Surface.CODEGEN_TEXTFIELD)
    @Test fun codegenTextView() = check(Surface.CODEGEN_TEXTVIEW)
    @Test fun dynamicTextField() = check(Surface.DYNAMIC_TEXTFIELD)
    @Test fun dynamicTextView() = check(Surface.DYNAMIC_TEXTVIEW)
    @Test fun syncedStateHelper() = check(Surface.SYNCED_STATE_HELPER)

    /**
     * A host whose data has no updateData (a generated host's data.toMap()): the
     * edit never reached the data, so the value being the old one says nothing.
     * A handler that only looks and a handler that corrected to the same value
     * leave the same state, and the field keeps the edit in both: reverting would
     * make a field over a handler that only looks refuse every key. The value
     * still replaces the edit when it changes. iOS's plain value holds the same
     * rule (DynamicLocalState).
     */
    @Test
    fun aHostWithoutUpdateDataKeepsTheEdit() {
        assertEquals("123|3|12", drive(Surface.DYNAMIC_NO_UPDATE_DATA, "12", Answer.KEEP) { commit("3") })
        assertEquals("12年|3|12", drive(Surface.DYNAMIC_NO_UPDATE_DATA, "12", Answer.DIGITS_SYNC) { commit("年") })
        assertEquals("3|1|3", drive(Surface.DYNAMIC_NO_UPDATE_DATA, "12", Answer.DIGITS_SYNC) { all(); commit("3年") })
    }
}
