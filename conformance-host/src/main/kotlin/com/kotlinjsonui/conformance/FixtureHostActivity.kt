package com.kotlinjsonui.conformance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.DynamicView
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

    /** Render/load errors reported by DynamicView for the current fixture. */
    val renderErrors = CopyOnWriteArrayList<String>()

    /** Show a fixture (or null to blank the screen). Clears collected errors. */
    fun show(fixtureId: String?) {
        renderErrors.clear()
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

        intent.getStringExtra(EXTRA_FIXTURE_ID)?.let { FixtureHost.show(it) }

        setContent {
            val fixtureId by FixtureHost.currentFixtureId.collectAsState()
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
