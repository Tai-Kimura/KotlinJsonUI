package com.kotlinjsonui.conformance

import android.os.Bundle
import android.view.Choreographer
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.DynamicView
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Shared channel between the conformance instrumentation suite and
 * [FixtureHostActivity]. Instrumented tests run in the same process as the
 * app, so the suite swaps fixtures by updating [currentFixtureId] instead of
 * relaunching the Activity for each of the 600+ fixtures.
 */
object FixtureHost {
    /** Fixture id from manifest.json, e.g. "Label/text__static". */
    val currentFixtureId = MutableStateFlow<String?>(null)

    /**
     * Rendering pipeline for the run: "dynamic" (DynamicView, the baseline
     * pipeline) or "codegen" (kjui-generated Compose views — what production
     * apps ship; see scripts/generate_codegen_host.rb). The instrumentation
     * suite sets this from its `conformanceHostMode` argument before the
     * activity launches; parity of the two pipelines is judged by
     * `jui conformance parity` against the dynamic baselines.
     */
    val hostMode = MutableStateFlow("dynamic")

    /** Render/load errors reported by DynamicView for the current fixture. */
    val renderErrors = CopyOnWriteArrayList<String>()

    /**
     * In-process render signal: fixture ids whose ready-Box has completed
     * layout (onGloballyPositioned). The suite polls THIS for visual
     * fixtures instead of the a11y tree — the accessibility projection can
     * wedge per-boot on CI emulators (AccessibilityManagerService "wait for
     * adding window timeout", run 30762153614: rendering + input + lifecycle
     * all healthy while UIAutomator is globally blind), and screenshots go
     * through SurfaceFlinger, so visual coverage must not hinge on a11y.
     */
    val renderedIds: MutableSet<String> = ConcurrentHashMap.newKeySet()

    fun markRendered(fixtureId: String) {
        renderedIds.add(fixtureId)
    }

    /**
     * Fixture ids whose content has been DRAWN and whose frame has been handed
     * to the compositor. Layout completing is not the same event: measured on
     * CI 2026-08-04 (run 30870693593), `CheckBox/isOn__true` captured a frame
     * byte-identical to a different fixture's while every sibling matched at
     * distance <= 1 — layout had signalled, the screenshot went through
     * SurfaceFlinger, and the frame on the display was still the other one.
     *
     * [markDrawn] runs in the draw phase; the Choreographer callback it posts
     * fires at the start of the NEXT frame, by which point the drawn frame has
     * been submitted. That is the signal a SurfaceFlinger capture needs.
     */
    val presentedIds: MutableSet<String> = ConcurrentHashMap.newKeySet()

    fun markDrawn(fixtureId: String) {
        // Draw runs every frame; only the first one per fixture needs to arm
        // the callback, and `add` returning false makes that check free.
        if (!drawArmed.add(fixtureId)) return
        Choreographer.getInstance().postFrameCallback {
            presentedIds.add(fixtureId)
        }
    }

    private val drawArmed: MutableSet<String> = ConcurrentHashMap.newKeySet()

    /** Show a fixture (or null to blank the screen). Clears collected errors. */
    fun show(fixtureId: String?) {
        renderErrors.clear()
        fixtureId?.let {
            renderedIds.remove(it)
            presentedIds.remove(it)
            drawArmed.remove(it)
        }
        currentFixtureId.value = fixtureId
    }

    /**
     * testTag placed on the wrapper Box of the rendered fixture. Unique per
     * fixture so the test suite can be sure the semantics tree it queries
     * belongs to the fixture it just requested (all fixtures share the
     * "root"/"target" ids).
     */
    fun readyTag(fixtureId: String): String =
        "conformance_ready_" + fixtureId.replace('/', '-')
}

/**
 * Single-Activity host that renders one conformance fixture at a time via
 * KotlinJsonUI's Compose dynamic mode (library-dynamic / DynamicView).
 *
 * The fixture can be selected:
 * - by the instrumentation suite through [FixtureHost.show], or
 * - manually via an intent extra, e.g.
 *   `adb shell am start -n com.kotlinjsonui.conformance/.FixtureHostActivity \
 *      -e fixtureId Label/text__static`
 */
class FixtureHostActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Fixtures swap in place inside one window, so a soft keyboard raised
        // by an interactive fixture stays up over everything that follows —
        // measured on CI 2026-08-04 (run 30870693593): `TextField/hint__static`
        // was captured with the IME occupying the lower third of the frame in
        // one lane and absent in the other. Nothing in this host ever needs the
        // IME visible (UiAutomator's setText does not require it), so keep it
        // down for the whole run.
        // ALWAYS_HIDDEN alone was not enough (run 30871915452 still captured
        // TextField/hint__static with the full keyboard over the lower half):
        // it only applies as the window comes forward, and a TextField that
        // takes focus afterwards raises the IME again. ALT_FOCUSABLE_IM makes
        // the window ineligible for IME input at all, so focus no longer
        // summons a keyboard — UiAutomator's setText does not go through the
        // IME, so nothing in this host loses anything.
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)

        intent.getStringExtra(EXTRA_FIXTURE_ID)?.let { FixtureHost.show(it) }

        setContent {
            val fixtureId by FixtureHost.currentFixtureId.collectAsState()
            // ALWAYS_HIDDEN only applies when the window comes to the front, so
            // it cannot undo an IME raised mid-run. Dismiss on every swap too.
            LaunchedEffect(fixtureId) {
                currentFocus?.clearFocus()
                getSystemService(InputMethodManager::class.java)
                    ?.hideSoftInputFromWindow(window.decorView.windowToken, 0)
            }
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    fixtureId?.let { id ->
                        key(id) { FixtureScreen(id) }
                    }
                }
            }
        }
    }

    companion object {
        const val EXTRA_FIXTURE_ID = "fixtureId"
    }
}

@Composable
private fun FixtureScreen(fixtureId: String) {
    val context = LocalContext.current
    val hostMode by FixtureHost.hostMode.collectAsState()

    if (hostMode == "codegen") {
        // Generated views check DynamicModeManager.isActive() in their body;
        // switch it off before the first codegen fixture composes so nothing
        // slips through the dynamic path.
        remember {
            com.kotlinjsonui.core.DynamicModeManager
                .setDynamicModeEnabled(context, false)
        }
        val entry = CodegenFixtureEntries.map[fixtureId]
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .testTag(FixtureHost.readyTag(fixtureId))
                .semantics { testTagsAsResourceId = true }
                .onGloballyPositioned { FixtureHost.markRendered(fixtureId) }
                .drawWithContent {
                    drawContent()
                    FixtureHost.markDrawn(fixtureId)
                }
        ) {
            if (entry != null) {
                entry()
            } else {
                // Same channel a dynamic load failure uses, so the suite
                // reports a fixture the registry does not carry as an error
                // rather than asserting against a blank screen.
                remember(fixtureId) {
                    FixtureHost.renderErrors.add("no generated view for fixture: $fixtureId")
                    true
                }
            }
        }
        return
    }

    val layoutJson: JsonObject? = remember(fixtureId) {
        try {
            context.assets.open(ConformanceStateRegistry.layoutAssetPath(context, fixtureId))
                .bufferedReader()
                .use { it.readText() }
                .let { JsonParser.parseString(it).asJsonObject }
        } catch (e: Exception) {
            FixtureHost.renderErrors.add("layout load failed: ${e.message}")
            null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            // Keep fixture content out of the system-bar regions: with
            // edge-to-edge (targetSdk 35+) a node rendered behind the status
            // bar is reported visible=false to the instrumentation's
            // accessibility tree and becomes unfindable by resource-id.
            .systemBarsPadding()
            .testTag(FixtureHost.readyTag(fixtureId))
            .semantics { testTagsAsResourceId = true }
            .onGloballyPositioned { FixtureHost.markRendered(fixtureId) }
            .drawWithContent {
                drawContent()
                FixtureHost.markDrawn(fixtureId)
            }
    ) {
        layoutJson?.let { json ->
            DynamicView(
                json = json,
                // Generic conformanceState provider (INTERACTIVE_HOST_CONTRACT.md):
                // manifest-declared handler closures + two-way write-back state.
                // Empty for non-interactive fixtures apart from `updateData`.
                data = rememberConformanceData(fixtureId),
                onError = { e ->
                    FixtureHost.renderErrors.add(e.message ?: e.javaClass.simpleName)
                }
            )
        }
    }
}
