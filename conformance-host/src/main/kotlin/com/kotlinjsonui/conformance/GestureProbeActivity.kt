package com.kotlinjsonui.conformance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kotlinjsonui.embed.EmbedContainer
import com.kotlinjsonui.embed.EmbedIsolatedNavigation
import com.kotlinjsonui.embed.EmbedNavigationMode
import com.kotlinjsonui.embed.EmbedNavigatorRegistry

/**
 * Manual gesture-delegation probe (04a-design.md §2) — NOT part of the
 * conformance suite. Launch with:
 *
 *   adb shell am start -n com.kotlinjsonui.conformance/.GestureProbeActivity
 *
 * Two isolated embeds are nested with the INNER one living inside the
 * OUTER embed's pushed screen, so that after `push-outer` + `push-inner`
 * BOTH BackHandlers are composed and enabled at once — the scenario the
 * "deepest non-empty isolated stack wins" contract is about. Drive the
 * stacks from the host buttons (EmbedNavigatorRegistry) and observe what
 * HW/gesture back pops.
 */
class GestureProbeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ProbeScreen()
                }
            }
        }
    }
}

@Composable
private fun ProbeScreen() {
    Column {
        Text("probe-host")
        Button(onClick = {
            EmbedNavigatorRegistry.get("outer")?.push("outer_pushed")
        }) { Text("push-outer") }
        Button(onClick = {
            EmbedNavigatorRegistry.get("inner")?.push("inner_pushed")
        }) { Text("push-inner") }

        EmbedContainer(
            embedId = "outer",
            navigationMode = EmbedNavigationMode.Isolated,
            isolatedNavigation = EmbedIsolatedNavigation.Automatic,
            destinationContent = { _ ->
                Column {
                    Text("outer-pushed")
                    EmbedContainer(
                        embedId = "inner",
                        navigationMode = EmbedNavigationMode.Isolated,
                        isolatedNavigation = EmbedIsolatedNavigation.Automatic,
                        destinationContent = { _ -> Text("inner-pushed") }
                    ) { _ ->
                        Text("inner-root")
                    }
                }
            }
        ) { _ ->
            Text("outer-root")
        }
    }
}
