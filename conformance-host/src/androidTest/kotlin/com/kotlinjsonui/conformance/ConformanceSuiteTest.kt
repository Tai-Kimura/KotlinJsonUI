package com.kotlinjsonui.conformance

import android.content.Intent
import android.util.Log
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import com.jsonui.testrunner.actions.ActionExecutor
import com.jsonui.testrunner.assertions.AssertionExecutor
import com.jsonui.testrunner.models.ScreenTest
import com.jsonui.testrunner.runner.LoadedTest
import com.jsonui.testrunner.runner.TestLoader
import java.io.File
import java.security.MessageDigest
import com.kotlinjsonui.core.WebLoadSignal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * JsonUI renderer conformance suite (Android / Compose dynamic mode).
 *
 * Iterates the fixtures listed in assets/conformance/manifest.json, renders
 * each one in [FixtureHostActivity] via library-dynamic's DynamicView, and
 * executes the fixture's screen test (assertions / screenshot) with the
 * vendored jsonui-test-runner executors (UIAutomator, elements resolved by
 * resource-id = Compose testTag).
 *
 * Outputs (app external files dir, `<ext>/conformance/`):
 * - progress.jsonl          incremental, crash-safe outcome log
 * - android.results.json    RESULTS_SCHEMA-compliant results (written when
 *                           every fixture has an outcome)
 * - artifacts/android/<name>.png screenshots for visual fixtures
 *
 * Collect with conformance-host/scripts/collect_results.sh.
 *
 * Instrumentation args:
 * - conformanceFilter=assertable  run only assertable + alias fixtures;
 *   everything else is reported as skipped (no silent truncation).
 */
@RunWith(AndroidJUnit4::class)
class ConformanceSuiteTest {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val targetContext = instrumentation.targetContext
    private val device: UiDevice = UiDevice.getInstance(instrumentation)

    private val defaultTimeoutMs = 5000L

    // Per-fixture settle wait (waitForIdle). MUST stay small: on some hosted
    // runners the accessibility event stream never quiets (continuous
    // recomposition events), so waitForIdle times out EVERY time and the
    // timeout is paid in full per fixture — at 5000ms that turned the suite
    // from ~7 min into ~65 min (progress logs: constant ~7.3s/fixture, runs
    // 28687476052 / 28693915877). Rendering correctness is guaranteed by the
    // readyTag poll, not by this wait; it only lets the compositor settle
    // (stale drop shadows / mid-settle heights in screenshots).
    private val settleTimeoutMs = 1000L

    /**
     * How long to wait for a WebView to report a finished load. Generous on
     * purpose: exceeding it is a NOTICE (`timedOut`), not a failure — the
     * failure is `markerAbsent`, which means no load was ever observed and is
     * a wiring fact that no timeout can fix.
     */
    private val webLoadTimeoutMs = 10000L
    private val actionExecutor = ActionExecutor(device, defaultTimeoutMs)
    private val assertionExecutor = AssertionExecutor(device, defaultTimeoutMs)
    private val testLoader = TestLoader()

    private val outputDir = File(targetContext.getExternalFilesDir(null), "conformance")
    private val artifactsDir = File(outputDir, "artifacts/android")
    private val store = ConformanceResultsStore(outputDir)

    /** screenshot path (relative to conformance/) captured by the current fixture */
    private var lastScreenshot: String? = null

    /** current host Activity scenario (relaunched for assertable fixtures) */
    private var scenario: ActivityScenario<FixtureHostActivity>? = null

    @Test
    fun runConformanceSuite() {
        val manifestBytes = targetContext.assets.open("conformance/manifest.json")
            .use { it.readBytes() }
        val manifest = ConformanceManifest.parse(String(manifestBytes, Charsets.UTF_8))
        val manifestHash = sha256Hex(manifestBytes)
        val filter = InstrumentationRegistry.getArguments()
            .getString("conformanceFilter", "all")
        val hostMode = InstrumentationRegistry.getArguments()
            .getString("conformanceHostMode", "dynamic")
        // Same process as the app: flip the pipeline before the activity
        // composes its first fixture.
        FixtureHost.hostMode.value = hostMode

        artifactsDir.mkdirs()
        actionExecutor.screenshotHandler = { name -> captureStableScreenshot(name) }

        val outcomes = store.loadCompleted().toMutableMap()
        log("Resuming with ${outcomes.size}/${manifest.fixtures.size} outcomes already recorded")

        // 🔻 A HOOK FOR THE CENSUS'S OWN CONTROL, ON BY DEFAULT. `markerAbsent`
        // claims to detect "the load signal is gone", and a check nobody has
        // ever watched fail is a claim, not a gate. Conservation does not
        // cover this: a predicate that always answered "settled" would satisfy
        // runnable == reachedCapture == the bucket sum AND return
        // markerAbsent == 0 — which is precisely the iOS defect
        // (`if webPending.exists`, evaluated instantly, never waiting). The
        // same census, on the neighbouring face, has already failed this way
        // once.
        //
        // Run with `-e conformanceWebMarkers off` and the suite must go RED
        // with markerAbsent == runnable. If that run stays green, a green run
        // with the flag on means nothing.
        val webMarkersOff =
            InstrumentationRegistry.getArguments().getString("conformanceWebMarkers") == "off"
        WebLoadSignal.enabled = !webMarkersOff
        log("Web load markers: ${if (webMarkersOff) "OFF (census control)" else "on"}")
        // DECLARED, before the run touches anything: how many Web-hosted
        // fixtures THIS INVOCATION is supposed to execute. A run whose
        // `reachedCapture` falls short of it has a wiring problem, which is a
        // different finding from "the page did not load".
        //
        // 🔻 DERIVED FROM `classifySkip`, THE SAME PREDICATE THE LOOP USES,
        // not from a copy of its conditions. The first version re-listed
        // platform/mode by hand and ignored the filter entirely, so any
        // `--filter assertable` run — where every Web visual fixture is
        // skipped by design — would have reported runnable=2 against
        // reachedCapture=0 and failed the suite for doing exactly what it was
        // asked to do. A parallel reimplementation of a gate's own condition
        // is wrong the moment either side moves.
        webLoadCensus.webFixturesRunnable = manifest.fixtures.count { f ->
            f.host == "Web" && classifySkip(f, filter) == null
        }

        try {
            var firstFixture = true

            for (fixture in manifest.fixtures) {
                if (outcomes.containsKey(fixture.id)) continue
                // Per fixture, so `startedCount` answers "did a load begin for
                // THIS one" rather than "has any load ever begun".
                WebLoadSignal.reset()

                val skipped = classifySkip(fixture, filter)
                if (skipped != null) {
                    store.record(skipped)
                    outcomes[fixture.id] = skipped
                    continue
                }

                store.markRunning(fixture.id)
                val result = executeFixture(fixture, firstFixture)
                firstFixture = false
                store.record(result)
                outcomes[fixture.id] = result
                log("${fixture.id}: ${result.status}" +
                    if (result.detail.isNotEmpty()) " (${result.detail})" else "")
            }
        } finally {
            scenario?.close()
            scenario = null
        }

        val resultsFile = store.writeFinalResults(
            manifest = manifest,
            manifestHash = manifestHash,
            outcomes = outcomes,
            runnerName = "uiautomator",
            runnerVersion = UIAUTOMATOR_VERSION,
            webMarkers = mapOf(
                "webFixturesRunnable" to webLoadCensus.webFixturesRunnable,
                "webFixturesReachedCapture" to webLoadCensus.webFixturesReachedCapture,
                "alreadySettled" to webLoadCensus.alreadySettled,
                "waitedThenSettled" to webLoadCensus.waitedThenSettled,
                "timedOut" to webLoadCensus.timedOut,
                "markerAbsent" to webLoadCensus.markerAbsent
            )
        )
        val summary = outcomes.values.groupingBy { it.status }.eachCount()
        log("Suite finished: $summary -> ${resultsFile.name}")
        log("Web load census: $webLoadCensus")
        assertTrue("results file missing", resultsFile.isFile)

        // 🔻 CONSERVATION FIRST. Each of the three below rules out a way the
        // census could be green while measuring nothing, and the last one only
        // means something once they hold.
        val bucketed = webLoadCensus.alreadySettled + webLoadCensus.waitedThenSettled +
            webLoadCensus.timedOut + webLoadCensus.markerAbsent
        // 🔴 NOT EQUALITY, AND THE OWNER OF THE OTHER FACE SAID SO FIRST.
        // SwiftJsonUI's census declares `webFixturesRunnable` "Informational …
        // not the denominator", because a fixture that errors before its
        // screen comes up never reaches the check. This host has a second
        // reason on top of that: the crash-resume loop skips fixtures already
        // in `progress.jsonl` (`if (outcomes.containsKey(fixture.id))
        // continue`) while `webFixturesRunnable` is counted from the manifest
        // and knows nothing about them — so ANY run that retried once would
        // have failed an equality assertion with everything working.
        //
        // What is left here is the direction, which cannot be violated
        // innocently: reaching capture more times than the run had fixtures to
        // run would mean the counter is being incremented from somewhere else.
        //
        // 🔻 THE POPULATION CHECK IS NOT HERE AND CANNOT BE. Both numbers on
        // this line are derived from the set THIS RUN decided to execute, so
        // a fixture that left the corpus shrinks them together and the
        // comparison stays true. `jui conformance gate` compares
        // `webFixturesRunnable` against the manifest's declaration, which is
        // the only number outside the run.
        assertTrue(
            "reachedCapture=${webLoadCensus.webFixturesReachedCapture} exceeds " +
                "runnable=${webLoadCensus.webFixturesRunnable} — the capture counter is " +
                "being incremented outside the loop that owns it",
            webLoadCensus.webFixturesReachedCapture <= webLoadCensus.webFixturesRunnable
        )
        assertEquals(
            "census buckets do not add up: $bucketed bucketed vs " +
                "${webLoadCensus.webFixturesReachedCapture} reached capture",
            webLoadCensus.webFixturesReachedCapture,
            bucketed
        )
        assertEquals(
            "${webLoadCensus.markerAbsent} Web fixture(s) reached capture with no load " +
                "ever observed — the signal is not wired, so the screenshot was taken " +
                "at an uncontrolled moment (census: $webLoadCensus)",
            0,
            webLoadCensus.markerAbsent
        )
    }

    /** Returns a skipped result when this host must not execute the fixture. */
    private fun classifySkip(fixture: ManifestFixture, filter: String): FixtureResult? {
        if (!fixture.platforms.contains("android")) {
            return FixtureResult(fixture.id, "skipped", "not applicable to android")
        }
        if (FixtureHost.hostMode.value == "codegen" && fixture.clazz != "visual") {
            // Parity is a visual question (screenshot vs dynamic baseline);
            // interactive fixtures drive dynamic bindings and assert-only
            // fixtures have no screenshot to compare.
            return FixtureResult(
                fixture.id, "skipped",
                "class ${fixture.clazz} not hosted (codegen parity host is visual-only)"
            )
        }
        if (!fixture.runsOnComposeDynamic()) {
            return FixtureResult(
                fixture.id, "skipped",
                "${fixture.modeDetail()} (host runs compose dynamic)"
            )
        }
        if (filter == "assertable" && fixture.clazz != "assertable" && !fixture.isAlias) {
            return FixtureResult(
                fixture.id, "skipped",
                "filtered out (assertable-only run)"
            )
        }
        if (filter == "interactive" && fixture.clazz != "interactive") {
            return FixtureResult(
                fixture.id, "skipped",
                "filtered out (interactive-only run)"
            )
        }
        // Any other filter value is a comma-separated list of manifest
        // sections (the `<component>/` prefix of fixture ids), e.g.
        // `-e conformanceFilter Label,common` — used for per-component
        // verification runs during typed-attribute rollouts.
        if (filter != "all" && filter != "assertable" && filter != "interactive") {
            val sections = filter.split(',').map { it.trim() }.filter { it.isNotEmpty() }
            if (sections.none { fixture.id.startsWith("$it/") }) {
                return FixtureResult(
                    fixture.id, "skipped",
                    "filtered out (section run: $filter)"
                )
            }
        }
        return null
    }

    private fun executeFixture(fixture: ManifestFixture, firstFixture: Boolean): FixtureResult {
        lastScreenshot = null

        // Parse the fixture's screen test up-front (parse failure = error)
        val screenTest: ScreenTest = try {
            val testJson = targetContext.assets.open("conformance/${fixture.test}")
                .bufferedReader().use { it.readText() }
            when (val loaded = testLoader.loadFromString(testJson, fixture.id)) {
                is LoadedTest.Screen -> loaded.test
                else -> return FixtureResult(
                    fixture.id, "error", "fixture test is not a screen test"
                )
            }
        } catch (e: Exception) {
            return FixtureResult(fixture.id, "error", "test json load failed: ${brief(e)}")
        }

        // Render the fixture and wait until its unique wrapper tag is in the
        // semantics tree (guards against asserting on the previous fixture's
        // tree — all fixtures share the "root"/"target" ids).
        //
        // Assertable AND interactive fixtures get a fresh Activity: after an
        // in-place content swap, non-interactive Compose text nodes are
        // reported to the accessibility tree with visible=false (UIAutomator
        // then cannot find them by resource-id / reads stale trees), while a
        // fresh window exposes them correctly. Both classes assert on text.
        // Visual fixtures only need waitFor(root) + screenshot, so they keep
        // the much faster in-process swap.
        if (fixture.clazz == "assertable" || fixture.clazz == "interactive" || scenario == null) {
            scenario?.close()
            val intent = Intent(targetContext, FixtureHostActivity::class.java)
                .putExtra(FixtureHostActivity.EXTRA_FIXTURE_ID, fixture.id)
            scenario = ActivityScenario.launch(intent)
            device.waitForIdle(settleTimeoutMs)
        } else {
            FixtureHost.show(fixture.id)
        }
        val readyTimeout = if (firstFixture) 15000L else 8000L
        // Visual fixtures wait on the IN-PROCESS layout signal, not the a11y
        // tree: the a11y projection wedges per-boot on CI emulators
        // (AccessibilityManagerService "wait for adding window timeout" —
        // run 30762153614 measured rendering/input/lifecycle all healthy
        // while UIAutomator stayed globally blind) and screenshots capture
        // via SurfaceFlinger, so visual coverage survives a wedged boot.
        // Assertable/interactive fixtures still need the a11y tree (their
        // finds/taps/asserts go through it), so they keep the UIAutomator
        // wait — on a wedged boot they fail into the run script's probe/
        // re-roll path instead of poisoning visual coverage.
        val needsA11y = fixture.clazz == "assertable" || fixture.clazz == "interactive"
        val ready = if (needsA11y) {
            waitForResourceId(FixtureHost.readyTag(fixture.id), readyTimeout)
        } else {
            waitForRendered(fixture.id, readyTimeout)
        }
        if (!ready) {
            val renderErrors = FixtureHost.renderErrors.joinToString("; ")
            // Diagnostic quadruple for the CI-only "nothing renders"
            // signature (runs 30735629989/30741336803/30762153614): decides
            // the failure layer off one results.json — activity never
            // fronted (appWindow=false) vs composition stalled
            // (rendered=false) vs a11y projection wedged (rendered=true,
            // appWindow=false — the 30762153614 verdict).
            val appWindow = device.hasObject(By.pkg(targetContext.packageName))
            val anyTag = device.hasObject(By.res(Regex("conformance_ready_.*").toPattern()))
            val rendered = FixtureHost.renderedIds.contains(fixture.id)
            val fixtureFlow = FixtureHost.currentFixtureId.value
            FixtureHost.show(null)
            return FixtureResult(
                fixture.id, "error",
                "fixture did not render within ${readyTimeout}ms" +
                    " [appWindow=$appWindow anyReadyTag=$anyTag rendered=$rendered flow=$fixtureFlow]" +
                    if (renderErrors.isNotEmpty()) " — render errors: ${brief(renderErrors)}" else ""
            )
        }

        // Visual fixtures: the semantics tree being ready (readyTag) does not
        // guarantee the *rendered frame* has settled after an in-place content
        // swap — calibration runs caught a stale drop shadow from the previous
        // fixture and a flexible-TextView height mid-settle in screenshots.
        // Wait for the frame-committed signal (draw phase + next Choreographer
        // frame), then still give the compositor the calibrated beat: the beat
        // covers post-first-frame settling (shadows, flexible heights) that a
        // single committed frame does not.
        if (fixture.clazz == "visual") {
            waitForPresented(fixture.id, readyTimeout)
            // 🔻 A `WebView` IS IN THE HIERARCHY BEFORE IT HAS PAINTED, so the
            // frame-committed signal above is satisfied while the page is
            // still loading. Keyed on the manifest's DECLARED host rather than
            // on any transient state: the iOS host's first version detected a
            // "pending" marker that was already gone by the first query on a
            // local load, and reported a false 0 of 2 on a run whose pixels
            // were byte-identical to the baseline.
            if (fixture.host == "Web") {
                settleWebLoad(webLoadTimeoutMs, webLoadCensus)
            }
            device.waitForIdle(settleTimeoutMs)
            Thread.sleep(150)
        }

        val status = try {
            for (case in screenTest.cases) {
                for (step in case.steps) {
                    // Non-a11y fixtures already proved layout via the
                    // in-process signal above; the generated waitFor(root)
                    // would re-ask the a11y tree, which may be wedged
                    // (30762153614) while SurfaceFlinger screenshots work.
                    // Every visual fixture is exactly [waitFor, screenshot]
                    // (audited 606/606), so the skip forfeits nothing.
                    if (!needsA11y && step.action == "waitFor") continue
                    when {
                        step.isAction -> actionExecutor.execute(step)
                        step.isAssertion -> assertionExecutor.execute(step)
                        else -> throw IllegalArgumentException("step has neither action nor assert")
                    }
                }
            }
            null // pass
        } catch (e: AssertionError) {
            FixtureResult(fixture.id, "fail", brief(e), lastScreenshot)
        } catch (e: Exception) {
            FixtureResult(fixture.id, "error", brief(e), lastScreenshot)
        }
        if (status != null) return status

        // Steps passed, but surface renderer-reported errors (missing asset,
        // unknown attribute crash, ...) — a visual fixture whose component
        // errored out still "passes" waitFor(root), which would hide the bug.
        val renderErrors = FixtureHost.renderErrors.toList()
        if (renderErrors.isNotEmpty()) {
            return FixtureResult(
                fixture.id, "error",
                "render error: ${brief(renderErrors.joinToString("; "))}",
                lastScreenshot
            )
        }
        return FixtureResult(fixture.id, "pass", "", lastScreenshot)
    }

    private fun captureScreenshot(name: String) {
        val file = File(artifactsDir, "$name.png")
        if (device.takeScreenshot(file)) {
            lastScreenshot = "artifacts/android/$name.png"
        } else {
            throw AssertionError("screenshot capture failed: $name")
        }
    }

    /**
     * Capture until two consecutive frames are byte-identical — the same trick
     * the web host uses, and for the same reason: readiness signals say the
     * content exists, not that the display is showing it. Measured on CI across
     * three runs (30839045057 / 30870693593 / 30871915452), the codegen lane
     * captured `CheckBox/isOn__true` as a frame byte-identical to the fixture
     * generated immediately before it — the registry maps it correctly and the
     * generated view is right, so what landed in the PNG was the previous
     * fixture still on screen. A fixed beat cannot cover an emulator whose
     * present latency varies; stability can, and it costs nothing once the
     * screen has actually caught up.
     */
    private fun captureStableScreenshot(name: String) {
        val file = File(artifactsDir, "$name.png")
        val scratch = File(artifactsDir, "$name.settling.png")
        try {
            if (!device.takeScreenshot(file)) {
                throw AssertionError("screenshot capture failed: $name")
            }
            var previous = file.readBytes()
            for (attempt in 0 until 12) {
                Thread.sleep(100)
                if (!device.takeScreenshot(scratch)) break
                val current = scratch.readBytes()
                scratch.copyTo(file, overwrite = true)
                if (current.contentEquals(previous)) break
                previous = current
                if (attempt == 11) {
                    Log.w("ConformanceSuite", "screenshot never stabilized: $name")
                }
            }
            lastScreenshot = "artifacts/android/$name.png"
        } finally {
            scratch.delete()
        }
    }

    private fun waitForResourceId(resourceId: String, timeoutMs: Long): Boolean {
        val deadline = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < deadline) {
            if (device.findObject(By.res(resourceId)) != null) return true
            Thread.sleep(50)
        }
        return false
    }

    /** In-process layout signal (FixtureHost.renderedIds) — a11y-independent. */
    private val webLoadCensus = WebLoadCensus()

    private fun waitForRendered(fixtureId: String, timeoutMs: Long): Boolean {
        val deadline = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < deadline) {
            if (FixtureHost.renderedIds.contains(fixtureId)) return true
            Thread.sleep(50)
        }
        return false
    }

    /**
     * Frame-committed signal (FixtureHost.presentedIds) — what a SurfaceFlinger
     * capture actually needs. Layout finishing is one phase too early: on CI
     * 2026-08-04 (run 30870693593) `CheckBox/isOn__true` was captured as a
     * frame byte-identical to another fixture's while its 15 siblings matched
     * their dynamic counterparts at distance <= 1. Returns false without
     * failing the fixture — the caller keeps the layout signal as the hard
     * gate, so a host that somehow never draws degrades to the old timing
     * rather than erroring the whole visual class.
     */
    /**
     * How many Web fixtures reached capture, and what the load signal said.
     *
     * 🔻 THE DENOMINATOR IS `webFixturesReachedCapture`, NOT the bucket. A
     * `markerAbsent` of 2 does not distinguish "two fixtures reached capture
     * and neither signalled" from "nothing reached capture at all" — and the
     * second is what a mis-wired census produces, which is the likelier
     * failure on the run that introduces one. So `runnable` (declared by the
     * manifest) and `reachedCapture` (observed by the run) are both recorded,
     * and the four buckets are required to sum to the second.
     */
    private data class WebLoadCensus(
        var webFixturesRunnable: Int = 0,
        var webFixturesReachedCapture: Int = 0,
        var alreadySettled: Int = 0,
        var waitedThenSettled: Int = 0,
        var timedOut: Int = 0,
        var markerAbsent: Int = 0
    )

    /**
     * Wait for this fixture's WebView to report a finished load.
     *
     * The counters are reset immediately before the fixture renders, so
     * `startedCount` says whether a load began at all. That separates "the
     * page is still loading" (timedOut) from "no load was ever observed"
     * (markerAbsent) — the second means the signal is not wired, which no
     * amount of waiting fixes and which a single bucket would hide.
     */
    private fun settleWebLoad(timeoutMs: Long, census: WebLoadCensus) {
        census.webFixturesReachedCapture += 1
        if (WebLoadSignal.finishedCount >= 1) {
            census.alreadySettled += 1
            return
        }
        val deadline = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < deadline) {
            if (WebLoadSignal.finishedCount >= 1) {
                census.waitedThenSettled += 1
                return
            }
            Thread.sleep(25)
        }
        if (WebLoadSignal.startedCount >= 1) census.timedOut += 1
        else census.markerAbsent += 1
    }

    private fun waitForPresented(fixtureId: String, timeoutMs: Long): Boolean {
        val deadline = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < deadline) {
            if (FixtureHost.presentedIds.contains(fixtureId)) return true
            Thread.sleep(25)
        }
        return false
    }

    /** Single-line, bounded detail string (RESULTS_SCHEMA: no newlines). */
    private fun brief(e: Throwable): String = brief(e.message ?: e.javaClass.simpleName)

    private fun brief(message: String): String =
        message.replace('\n', ' ').replace('\r', ' ').trim().take(300)

    private fun sha256Hex(bytes: ByteArray): String =
        MessageDigest.getInstance("SHA-256").digest(bytes)
            .joinToString("") { "%02x".format(it) }

    private fun log(message: String) {
        Log.i("ConformanceSuite", message)
    }

    companion object {
        /** keep in sync with the uiautomator dependency in build.gradle.kts */
        private const val UIAUTOMATOR_VERSION = "2.3.0"
    }
}
