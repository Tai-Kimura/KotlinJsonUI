package com.kotlinjsonui.core

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pins for [KotlinJsonUI.localizedString] — the context-free accessor the
 * codegen face emits for a generated Data class's resolvable defaultValue.
 */
@RunWith(AndroidJUnit4::class)
class LocalizedStringTest {

    @Test
    fun resolvesAfterInitialize() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        KotlinJsonUI.initialize(context)
        assertEquals(
            context.getString(android.R.string.ok),
            KotlinJsonUI.localizedString(android.R.string.ok, "fallback")
        )
    }

    @Test
    fun missingResourceFallsBack() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        KotlinJsonUI.initialize(context)
        // 0 is never a valid resource id.
        assertEquals("today", KotlinJsonUI.localizedString(0, "today"))
    }
}
