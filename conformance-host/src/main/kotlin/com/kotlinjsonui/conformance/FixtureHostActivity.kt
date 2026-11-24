package com.kotlinjsonui.conformance

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import android.os.Bundle
import android.os.SystemClock
import android.view.Choreographer
import android.view.View
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.DynamicView
import java.util.Locale
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

    /**
     * Bumped to rebuild the fixture view AND its data store in place, in the
     * same Activity and launch (INTERACTIVE_HOST_CONTRACT.md §6 job 4,
     * `testBridge.remount`): the composition is keyed on it.
     */
    val remountGeneration = MutableStateFlow(0)

    fun remount() {
        currentFixtureId.value?.let {
            renderedIds.remove(it)
            presentedIds.remove(it)
            presentTimings.remove(it)
            drawArmed.remove(it)
        }
        remountGeneration.value += 1
    }

    /**
     * Fixtures a test supplies itself, by id: the layout and the testBridge
     * fields, in place of the synced assets and manifest. For a device test
     * that must run where the conformance fixtures are not synced (the
     * android-library-tests job builds a clean checkout, whose
     * src/main/assets/conformance/ is gitignored) — TestBridgeRunnerE2ETest.
     * Dynamic face only.
     */
    val inlineFixtures: MutableMap<String, InlineFixture> = ConcurrentHashMap()

    /**
     * The text size each fixture is drawn at, from the manifest's `textScale` (absent: the device's own): the
     * fixture is composed with a Density of that font scale. Measured on an API 35 emulator,
     * `Density(density, fontScale)` converts sp exactly as the device set to that font scale does — Android 14+'s
     * non-linear conversion included (15 sp at 2.0: 70.875 px both ways).
     */
    val textScales: MutableMap<String, Float> = ConcurrentHashMap()

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
     * [markDrawn] runs in the draw phase. The Choreographer callback it posts
     * fires when the UI thread starts the NEXT frame, which says nothing about
     * the RenderThread: a frame it is still drawing has not reached the
     * compositor. A blur (RenderEffect) drawn for the first time is such a
     * frame — run 43684339304 captured common/effectStyle__light byte-identical
     * to the fixture before it, common/tintColor__binding, after this signal,
     * the 150 ms beat and two agreeing captures 100 ms apart had all passed.
     *
     * So on API 29+ the signal is a frame commit: in that next frame the
     * callback asks for one more frame (invalidate) and registers a
     * ViewTreeObserver frame-commit callback, which runs once that frame has
     * been rendered and submitted to the swap chain. The RenderThread renders
     * frames in order, so the drawn frame was submitted before it. Below 29
     * the Choreographer callback stays the signal.
     */
    val presentedIds: MutableSet<String> = ConcurrentHashMap.newKeySet()

    /**
     * Per fixture, ms from the draw to the Choreographer callback and to the
     * frame commit (null below API 29), read by the suite for its log line.
     */
    val presentTimings: MutableMap<String, Pair<Long, Long?>> = ConcurrentHashMap()

    fun markDrawn(fixtureId: String, view: View?) {
        // Draw runs every frame; only the first one per fixture needs to arm
        // the callback, and `add` returning false makes that check free.
        if (!drawArmed.add(fixtureId)) return
        val drawnAt = SystemClock.uptimeMillis()
        Choreographer.getInstance().postFrameCallback {
            val nextFrameMs = SystemClock.uptimeMillis() - drawnAt
            if (view == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.Q || !view.isAttachedToWindow) {
                presentTimings[fixtureId] = nextFrameMs to null
                presentedIds.add(fixtureId)
                return@postFrameCallback
            }
            view.viewTreeObserver.registerFrameCommitCallback {
                presentTimings[fixtureId] = nextFrameMs to (SystemClock.uptimeMillis() - drawnAt)
                presentedIds.add(fixtureId)
            }
            view.invalidate()
        }
    }

    private val drawArmed: MutableSet<String> = ConcurrentHashMap.newKeySet()

    /** Show a fixture (or null to blank the screen). Clears collected errors. */
    fun show(fixtureId: String?) {
        renderErrors.clear()
        fixtureId?.let {
            renderedIds.remove(it)
            presentedIds.remove(it)
            presentTimings.remove(it)
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

/** A fixture supplied by a test ([FixtureHost.inlineFixtures]). */
data class InlineFixture(val layoutJson: String, val testBridgeFields: List<TestBridgeHost.Field>?)

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
    /**
     * Every fixture renders in en-US, whatever the device's language. A library's inner words follow the language
     * (material3 DatePicker's month-and-year header and its year arrow, dates and numbers formatted by the default
     * locale), and a fixture that taps one by its text, or a picture of those words, must read the same on a local
     * device set to another language as on CI's emulator, which is en-US. The java default (java.text formatters)
     * and the context's configuration (Compose's LocalConfiguration, resources) are both pinned.
     */
    override fun attachBaseContext(newBase: Context) {
        Locale.setDefault(HOST_LOCALE)
        val config = Configuration(newBase.resources.configuration).apply { setLocales(LocaleList(HOST_LOCALE)) }
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

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
            val generation by FixtureHost.remountGeneration.collectAsState()
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
                        key(id, generation) { FixtureScreen(id) }
                    }
                }
            }
        }
    }

    companion object {
        const val EXTRA_FIXTURE_ID = "fixtureId"

        // the language every fixture renders in (attachBaseContext)
        val HOST_LOCALE: Locale = Locale.US
    }
}

@Composable
private fun FixtureScreen(fixtureId: String) {
    val context = LocalContext.current
    val hostView = LocalView.current
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
                    FixtureHost.markDrawn(fixtureId, hostView)
                }
        ) {
            if (entry != null) {
                TextScaled(fixtureId) { entry() }
            } else {
                // Same channel a dynamic load failure uses, so the suite
                // reports a fixture the registry does not carry as an error
                // rather than asserting against a blank screen.
                remember(fixtureId) {
                    FixtureHost.renderErrors.add("no generated view for fixture: $fixtureId")
                }
            }
        }
        return
    }

    val layoutJson: JsonObject? = remember(fixtureId) {
        try {
            (FixtureHost.inlineFixtures[fixtureId]?.layoutJson
                ?: context.assets.open(ConformanceStateRegistry.layoutAssetPath(context, fixtureId))
                    .bufferedReader()
                    .use { it.readText() })
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
                FixtureHost.markDrawn(fixtureId, hostView)
            }
    ) {
        layoutJson?.let { json ->
            TextScaled(fixtureId) {
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
}

/** [content] drawn at the fixture's text size (FixtureHost.textScales), or as it is when the manifest gives none. */
@Composable
private fun TextScaled(fixtureId: String, content: @Composable () -> Unit) {
    val scale = FixtureHost.textScales[fixtureId]
    if (scale == null) {
        content()
        return
    }
    val density = androidx.compose.ui.platform.LocalDensity.current
    androidx.compose.runtime.CompositionLocalProvider(
        androidx.compose.ui.platform.LocalDensity provides androidx.compose.ui.unit.Density(density.density, fontScale = scale),
    ) { content() }
}
