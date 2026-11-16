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
class FieldVm(
    start: String,
    private val answer: Answer,
    /** Each composition that reads the value takes this long: a screen that redraws a list. */
    val composeMillis: Long = 0,
    /** The handler takes this long on the main thread: a view model that rebuilds a list. */
    private val handlerMillis: Long = 0,
    /** false: a sheet's own view model, whose data carries no handler (the screen's data carries it). */
    withHandler: Boolean = true
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val _data = MutableStateFlow(FieldData(age = start, onAgeChange = if (withHandler) ::onAgeChange else null))
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

    /** What the handler was called with, in order (the main thread's). */
    val heard = java.util.Collections.synchronizedList(mutableListOf<String>())

    private fun onAgeChange(v: String) {
        heard += v
        if (handlerMillis > 0) Thread.sleep(handlerMillis)
        when (answer) {
            Answer.DIGITS_SYNC -> digits(v)
            Answer.DIGITS_IMMEDIATE -> scope.launch { digits(v) }
            Answer.DIGITS_DEFAULT -> scope.launch(Dispatchers.Default) { digits(v) }
            Answer.DIGITS_DELAYED -> scope.launch { delay(100); digits(v) }
            Answer.KEEP, Answer.DROP -> Unit
        }
    }

    /** The view model's own word, from outside the field. */
    fun set(v: String) = _data.update { it.copy(age = v) }

    fun close() = scope.cancel()
}

enum class Surface { CODEGEN_MAXLENGTH, CODEGEN_TEXTFIELD, CODEGEN_TEXTVIEW, DYNAMIC_TEXTFIELD, DYNAMIC_TEXTVIEW, SYNCED_STATE_HELPER, DYNAMIC_NO_UPDATE_DATA }

@Composable
private fun SurfaceContent(surface: Surface, vm: FieldVm) {
    val data by vm.data.collectAsState()
    if (vm.composeMillis > 0) { data.age.length; Thread.sleep(vm.composeMillis) }
    when (surface) {
        Surface.CODEGEN_TEXTFIELD -> GeneratedTextFieldShape(vm, data)
        Surface.CODEGEN_TEXTVIEW -> GeneratedTextViewShape(vm, data)
        Surface.CODEGEN_MAXLENGTH -> GeneratedMaxLengthShape(vm, data)
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
        /** Key events through the window, as `adb shell input text` sends them. */
        fun keys(t: String) = instrumentation.sendStringSync(t)
        fun key(code: Int) = instrumentation.sendKeyDownUpSync(code)
        /** One commit each, posted to the main thread `gap` ms apart, as an IME sends them. */
        fun typed(t: String, gap: Long) = t.forEach { c -> commit(c.toString()); if (gap > 0) Thread.sleep(gap) }
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

    private fun drive(
        surface: Surface, start: String, answer: Answer,
        composeMillis: Long = 0, handlerMillis: Long = 0, steps: Driver.() -> Unit
    ): String {
        val vm = FieldVm(start, answer, composeMillis, handlerMillis)
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
        // The generated shape reads the view model's own value. Dynamic and the helper have only
        // what composition holds, and keep the edit when a correction changes nothing there.
        val follows = surface == Surface.CODEGEN_TEXTFIELD || surface == Surface.CODEGEN_TEXTVIEW
        fun same(field: String) = if (follows) "12|2|12" else field
        fun row(name: String, want: String, start: String, answer: Answer, steps: Driver.() -> Unit): Row {
            val got = drive(surface, start, answer, steps = steps)
            val ok = got in want.split(" or ")
            Log.i("TFFAM", "$surface | $name | want=$want | got=$got | ${if (ok) "ok" else "DIFF"}")
            return Row(name, want, if (ok) want else got)
        }
        return listOf(
            // A correction back to the value held before the edit.
            row("same, handler corrects in the call", same("12年|3|12"), "12", Answer.DIGITS_SYNC) { commit("年") },
            row("same, whole text replaced", same("12年|3|12"), "12", Answer.DIGITS_SYNC) { all(); commit("12年") },
            row("same, launched on the main dispatcher", same("12年|3|12"), "12", Answer.DIGITS_IMMEDIATE) { commit("年") },
            // Dynamic and the helper follow this one when composition saw the raw edit before the
            // correction, and keep the edit when it did not: either, by when the other thread runs.
            row("same, launched on another thread", if (follows) "12|2|12" else "12|2|12 or 12年|3|12", "12", Answer.DIGITS_DEFAULT) { commit("年") },
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
            row("composing refused", same("12にほ|4|12"), "12", Answer.DIGITS_SYNC) {
                compose("に"); settled(); compose("にほ"); settled(); finish()
            },
            // A view model that drops the value holds 12: the field shows what it holds.
            row("view model drops the write", same("123|3|12"), "12", Answer.DROP) { commit("3") }
        )
    }

    private fun check(surface: Surface) {
        val rows = rowsOf(surface)
        assertEquals(rows.joinToString("\n") { "${it.name}: ${it.want}" }, rows.joinToString("\n") { "${it.name}: ${it.got}" })
    }

    /**
     * Keys faster than the screen, on a screen and a view model of each speed. On the
     * returned form and on the base these lost keys ("spr" -> "s", "sr"), up to every
     * try: composition's value is behind, and it was written over the newer key.
     */
    private fun burst(surface: Surface) {
        val lost = mutableListOf<String>()
        for (compose in listOf(0L, 100L)) for (handler in listOf(0L, 150L)) for (how in listOf("keys", "gap30")) repeat(3) {
            val got = drive(surface, "", Answer.KEEP, compose, handler) {
                if (how == "keys") keys("spr") else typed("spr", 30)
            }
            Log.i("TFFAM", "$surface | burst compose=$compose handler=$handler $how | want=spr|3|spr | got=$got | ${if (got == "spr|3|spr") "ok" else "DIFF"}")
            if (got != "spr|3|spr") lost += "compose=$compose handler=$handler $how: $got"
        }
        assertEquals(emptyList<String>(), lost)
    }

    @Test fun burstCodegenTextField() = burst(Surface.CODEGEN_TEXTFIELD)
    @Test fun burstCodegenTextView() = burst(Surface.CODEGEN_TEXTVIEW)
    @Test fun burstDynamicTextField() = burst(Surface.DYNAMIC_TEXTFIELD)
    @Test fun burstDynamicTextView() = burst(Surface.DYNAMIC_TEXTVIEW)
    @Test fun burstSyncedStateHelper() = burst(Surface.SYNCED_STATE_HELPER)

    /** What the report did on the search field, and the shapes next to it. */
    @Test
    fun keysOnTheGeneratedShape() {
        val left = android.view.KeyEvent.KEYCODE_DPAD_LEFT
        val del = android.view.KeyEvent.KEYCODE_DEL
        fun row(name: String, want: String, start: String, answer: Answer, compose: Long = 0, surface: Surface = Surface.CODEGEN_TEXTFIELD, steps: Driver.() -> Unit): Pair<String, String> {
            val got = drive(surface, start, answer, compose, 0, steps)
            Log.i("TFFAM", "$surface | $name | want=$want | got=$got | ${if (want == got) "ok" else "DIFF"}")
            return "$name: $want" to "$name: $got"
        }
        val rows = listOf(
            row("left, then a key", "spxr|3|spxr", "", Answer.KEEP, 30) { keys("spr"); settled(); key(left); settled(); keys("x") },
            row("left and a key in one go", "spxr|3|spxr", "", Answer.KEEP, 30) { keys("spr"); key(left); keys("x") },
            row("deletes in a burst", "|0|", "spr", Answer.KEEP, 30) { key(del); key(del); key(del) },
            row("a delete back to a value held earlier", "s|1|s", "", Answer.KEEP, 30) { keys("sp"); key(del) },
            row("digits only, in a burst", "123|3|123", "", Answer.DIGITS_SYNC, 30) { keys("1a2b3") },
            row("digits only, corrected on another thread", "123|3|123", "", Answer.DIGITS_DEFAULT, 30) { keys("1a2b3") },
            row("the view model clears after typing", "|0|", "12", Answer.KEEP) { keys("3"); settled(); main { vm.set("") } },
            row("the view model goes back to a value the field sent", "a|1|a", "", Answer.KEEP) {
                keys("a"); settled(); main { vm.set("b") }; settled(); main { vm.set("a") }
            },
            row("maxLength holds in a burst", "1234|4|1234", "", Answer.KEEP, 30, Surface.CODEGEN_MAXLENGTH) { keys("123456") }
        )
        assertEquals(rows.joinToString("\n") { it.first }, rows.joinToString("\n") { it.second })
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
