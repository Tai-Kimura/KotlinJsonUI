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

        artifactsDir.mkdirs()
        actionExecutor.screenshotHandler = { name -> captureScreenshot(name) }

        val outcomes = store.loadCompleted().toMutableMap()
        log("Resuming with ${outcomes.size}/${manifest.fixtures.size} outcomes already recorded")

        try {
            var firstFixture = true

            for (fixture in manifest.fixtures) {
                if (outcomes.containsKey(fixture.id)) continue

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
            runnerVersion = UIAUTOMATOR_VERSION
        )
        val summary = outcomes.values.groupingBy { it.status }.eachCount()
        log("Suite finished: $summary -> ${resultsFile.name}")
        assertTrue("results file missing", resultsFile.isFile)
    }

    /** Returns a skipped result when this host must not execute the fixture. */
    private fun classifySkip(fixture: ManifestFixture, filter: String): FixtureResult? {
        if (!fixture.platforms.contains("android")) {
            return FixtureResult(fixture.id, "skipped", "not applicable to android")
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
        if (!waitForResourceId(FixtureHost.readyTag(fixture.id), readyTimeout)) {
            val renderErrors = FixtureHost.renderErrors.joinToString("; ")
            FixtureHost.show(null)
            return FixtureResult(
                fixture.id, "error",
                "fixture did not render within ${readyTimeout}ms" +
                    if (renderErrors.isNotEmpty()) " — render errors: ${brief(renderErrors)}" else ""
            )
        }

        // Visual fixtures: the semantics tree being ready (readyTag) does not
        // guarantee the *rendered frame* has settled after an in-place content
        // swap — calibration runs caught a stale drop shadow from the previous
        // fixture and a flexible-TextView height mid-settle in screenshots.
        // Give the compositor a beat before capturing.
        if (fixture.clazz == "visual") {
            device.waitForIdle(settleTimeoutMs)
            Thread.sleep(150)
        }

        val status = try {
            for (case in screenTest.cases) {
                for (step in case.steps) {
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

    private fun waitForResourceId(resourceId: String, timeoutMs: Long): Boolean {
        val deadline = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < deadline) {
            if (device.findObject(By.res(resourceId)) != null) return true
            Thread.sleep(50)
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
