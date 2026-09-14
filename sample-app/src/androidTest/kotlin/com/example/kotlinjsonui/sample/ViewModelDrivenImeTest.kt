package com.example.kotlinjsonui.sample

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import android.app.Application
import androidx.test.platform.app.InstrumentationRegistry
import com.example.kotlinjsonui.sample.viewmodels.TextViewHintTestViewModel
import com.example.kotlinjsonui.sample.views.text_view_hint_test.TextViewHintTestGeneratedView
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.FileInputStream

/**
 * Target B: does the IME appear when a ViewModel sets <id>IsFocused, with no
 * rotation and no user gesture involved?
 *
 * Not an Android 17 question. The emitted call is
 * `keyboardController?.show()` -- an IME appearance with nothing a person did
 * behind it -- and platforms have been narrowing that shape for years. So this
 * runs on any API level; the existing API 35 images are enough. Target A, "does
 * it come back after a rotation", is the Android 17 one and needs API 37, which
 * this machine does not have.
 *
 * THREE arms, because one would not be a measurement. If the only thing
 * measured were "the keyboard appeared", a negative reads three ways:
 *
 *   the show() call never ran
 *   it ran and the platform refused
 *   the harness cannot raise an IME here at all (emulator config, soft keyboard
 *   disabled) -- and this one looks exactly like the second
 *
 * A table of negatives looks the same whether the instrument works or is dead.
 * So the first arm is a positive control that a tap raises the IME on this
 * device, and the second separates "focus moved" from "keyboard shown". With
 * both of those green, a negative third arm is a result rather than a fault.
 *
 * The specimen is a TextView-derived screen on purpose: of the eight generated
 * screens carrying this wiring, seven get it from TextView and one from
 * TextField. Picking the TextField screen would measure the seventh of the
 * population that is easiest to think of.
 *
 * Not run yet. The AVDs on this machine carry other lanes' conformance
 * baselines, so execution belongs to the conformance leg, which boots one once.
 */
@RunWith(AndroidJUnit4::class)
class ViewModelDrivenImeTest {

    @get:Rule
    val rule = createComposeRule()

    private fun imeShown(): Boolean {
        val out = InstrumentationRegistry.getInstrumentation().uiAutomation
            .executeShellCommand("dumpsys input_method")
        return FileInputStream(out.fileDescriptor).use { stream ->
            stream.readBytes().toString(Charsets.UTF_8).contains("mInputShown=true")
        }
    }

    /** Waits out the IME animation rather than asserting on the first frame. */
    private fun awaitIme(expected: Boolean, timeoutMs: Long = 5_000): Boolean {
        val deadline = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < deadline) {
            if (imeShown() == expected) return true
            Thread.sleep(200)
        }
        return imeShown() == expected
    }

    /** The generated ViewModels are AndroidViewModels, so they need the app. */
    private fun newViewModel(): TextViewHintTestViewModel =
        TextViewHintTestViewModel(
            InstrumentationRegistry.getInstrumentation()
                .targetContext.applicationContext as Application
        )

    private fun setContent(viewModel: TextViewHintTestViewModel) {
        rule.setContent {
            TextViewHintTestGeneratedView(
                data = viewModel.data.value,
                viewModel = viewModel
            )
        }
        rule.waitForIdle()
    }

    @Test
    fun aTapRaisesTheKeyboardOnThisDevice() {
        // Positive control. Without it, every negative below is ambiguous
        // between "the platform refused" and "this harness cannot show an IME".
        val viewModel = newViewModel()
        setContent(viewModel)

        rule.onNodeWithTag("simpleTextView").performClick()
        rule.waitForIdle()

        assertTrue(
            "a tap did not raise the IME -- this harness cannot measure target B " +
                "at all, and the arms below say nothing about the platform",
            awaitIme(expected = true)
        )
    }

    @Test
    fun theViewModelFlagMovesFocus() {
        // Separates requestFocus() from show(). The screen writes focus changes
        // back through onFocusChanged, so the data class is the observer.
        val viewModel = newViewModel()
        setContent(viewModel)
        assertFalse(
            "the specimen must start unfocused",
            viewModel.data.value.simpleTextViewIsFocused
        )

        viewModel.updateData(mapOf("simpleTextViewIsFocused" to true))
        rule.waitForIdle()

        assertTrue(
            "requestFocus() did not take -- the show() result below is about a " +
                "field that never held focus",
            viewModel.data.value.simpleTextViewIsFocused
        )
    }

    @Test
    fun theViewModelFlagRaisesTheKeyboard() {
        // The question itself. Read it only alongside the two arms above: this
        // one failing while they pass is a finding about keyboardController.show()
        // without user activation, and the docs sentence that promises it needs
        // a qualifier. This one failing when they also fail is a broken harness.
        val viewModel = newViewModel()
        setContent(viewModel)

        viewModel.updateData(mapOf("simpleTextViewIsFocused" to true))
        rule.waitForIdle()

        assertTrue(
            "focus moved but the IME did not appear: keyboardController?.show() " +
                "is being ignored without user activation",
            awaitIme(expected = true)
        )
    }
}
