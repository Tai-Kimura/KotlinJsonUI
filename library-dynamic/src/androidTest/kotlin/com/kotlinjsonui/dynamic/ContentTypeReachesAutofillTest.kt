package com.kotlinjsonui.dynamic

import android.os.ParcelFileDescriptor
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.google.gson.JsonParser
import com.kotlinjsonui.components.CustomTextField
import com.kotlinjsonui.dynamic.autofill.ProbeAutofillService
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A TextField's `contentType` as the Android Autofill framework sees it.
 *
 * Until 2.5.11 neither the Dynamic field nor the codegen one carried Compose's
 * autofill hint (`Modifier.semantics { contentType = … }`): the keyboard was
 * all `contentType` reached, so the framework never knew a field was a
 * username or a password and filled nothing from it. Here a test-only autofill
 * service ([ProbeAutofillService]) is the framework's other end: it records the
 * hints a fill request carries and offers one suggestion for a username.
 */
@RunWith(AndroidJUnit4::class)
class ContentTypeReachesAutofillTest {

    @get:Rule
    val rule = createComposeRule()

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private var previousService: String = ""

    private fun shell(command: String): String {
        val fd: ParcelFileDescriptor = instrumentation.uiAutomation.executeShellCommand(command)
        return ParcelFileDescriptor.AutoCloseInputStream(fd).use { it.readBytes().decodeToString().trim() }
    }

    private val probe = "${instrumentation.context.packageName}/${ProbeAutofillService::class.java.name}"

    /**
     * The service the framework uses for user 0, as `dumpsys autofill` names
     * it ("Component: ComponentInfo{pkg/cls}"), or null when there is none.
     */
    private fun frameworkService(): String? =
        Regex("""Component: ComponentInfo\{([^}]+)\}""").find(shell("dumpsys autofill"))?.groupValues?.get(1)

    /**
     * Make *component* the autofill service and wait until the framework uses
     * it. `settings get` answers the new value at once, but system_server
     * switches its service later, from an observer of the setting: on CI
     * (2.5.12's conformance, API 35) a field focused 130 ms after the put
     * opened its fill session with the service before, 52 ms before the
     * framework switched, and the probe never had a request (ticket
     * 2026-11-08-kjui-autofill-test-first-request-goes-to-the-old-service).
     */
    private fun useService(component: String?) {
        if (component == null) shell("settings delete secure autofill_service")
        else shell("settings put secure autofill_service $component")
        val want = component?.let { c -> if ("/." in c) c.replace("/.", "/" + c.substringBefore("/") + ".") else c }
        val deadline = System.currentTimeMillis() + 10_000
        while (frameworkService() != want && System.currentTimeMillis() < deadline) Thread.sleep(100)
        assertEquals("the framework's autofill service", want, frameworkService())
    }

    @Before
    fun useTheProbeService() {
        previousService = shell("settings get secure autofill_service")
        useService(probe)
        ProbeAutofillService.reset()
    }

    @After
    fun restoreTheService() {
        useService(previousService.takeUnless { it.isEmpty() || it == "null" })
    }

    private fun dynamicField(contentType: String?) {
        val ct = contentType?.let { ""","contentType":"$it"""" } ?: ""
        val json = JsonParser.parseString(
            """{"type":"TextField","id":"f","hint":"h","width":"matchParent","height":56$ct}"""
        ).asJsonObject
        rule.setContent { Column { DynamicView(json = json, data = emptyMap()) } }
    }

    /** Focus the field and wait for the framework's fill request. */
    private fun focusAndWaitForARequest(tag: String): Boolean {
        rule.onNodeWithTag(tag, useUnmergedTree = true).performClick()
        return try {
            rule.waitUntil(10_000) { ProbeAutofillService.requests.isNotEmpty() }
            true
        } catch (_: Throwable) {
            false
        }
    }

    private fun hints() = ProbeAutofillService.seenHints.flatten()

    @Test
    fun aDynamicUsernameFieldIsAUsernameToTheFramework() {
        dynamicField("username")
        assertTrue("a fill request came", focusAndWaitForARequest("f"))
        assertTrue("hints: ${hints()}", "username" in hints())
    }

    @Test
    fun aDynamicUsernameWebauthnFieldIsAUsernameToTheFramework() {
        dynamicField("usernameWebauthn")
        assertTrue("a fill request came", focusAndWaitForARequest("f"))
        assertTrue("hints: ${hints()}", "username" in hints())
    }

    /**
     * `password` reached the framework before 2.5.11 too: it makes the field a
     * BasicSecureTextField, which says "password" of itself. `newPassword` is
     * where the declaration adds something: the framework is told it is a NEW
     * password (a provider may offer to generate one), not one to fill.
     */
    @Test
    fun aDynamicNewPasswordFieldIsANewPasswordToTheFramework() {
        dynamicField("newPassword")
        assertTrue("a fill request came", focusAndWaitForARequest("f"))
        assertTrue("hints: ${hints()}", "newPassword" in hints())
    }

    /** The control: no contentType, no hint (a request may or may not come). */
    @Test
    fun aDynamicFieldWithNoContentTypeCarriesNoHint() {
        dynamicField(null)
        focusAndWaitForARequest("f")
        assertFalse("hints: ${hints()}", "username" in hints() || "password" in hints())
    }

    /**
     * The order CI's slow first test had: the field is on screen when the
     * service changes, and it is focused right after. Without the wait for the
     * framework the request went to the service before (GMS) and none came.
     */
    @Test
    fun aFocusRightAfterTheSwitchReachesTheProbe() {
        useService(previousService.takeUnless { it.isEmpty() || it == "null" })
        dynamicField("username")
        rule.waitForIdle()
        useService(probe)
        ProbeAutofillService.reset()
        assertTrue("a fill request came", focusAndWaitForARequest("f"))
        assertTrue("hints: ${hints()}", "username" in hints())
    }

    /** The codegen shape: the CustomTextField kjui emits, with the modifier it now writes. */
    @Test
    fun theCodegenUsernameFieldIsAUsernameToTheFramework() {
        rule.setContent {
            Column {
                CustomTextField(
                    state = rememberTextFieldState(),
                    modifier = Modifier.fillMaxWidth().height(56.dp).testTag("g")
                        .semantics { contentType = ContentType.Username }
                )
            }
        }
        assertTrue("a fill request came", focusAndWaitForARequest("g"))
        assertTrue("hints: ${hints()}", "username" in hints())
    }

    /** The suggestion the framework offers for the username fills the field. */
    @Test
    fun theSuggestionFillsTheDynamicUsernameField() {
        dynamicField("username")
        assertTrue("a fill request came", focusAndWaitForARequest("f"))
        val device = UiDevice.getInstance(instrumentation)
        val suggestion = device.wait(Until.findObject(By.text(ProbeAutofillService.SUGGESTION)), 10_000)
        assertTrue("the suggestion is shown", suggestion != null)
        suggestion!!.click()
        rule.waitUntil(10_000) {
            rule.onNodeWithTag("f", useUnmergedTree = true).fetchSemanticsNode()
                .config.getOrNull(SemanticsProperties.EditableText)?.text == ProbeAutofillService.SUGGESTION
        }
    }
}
