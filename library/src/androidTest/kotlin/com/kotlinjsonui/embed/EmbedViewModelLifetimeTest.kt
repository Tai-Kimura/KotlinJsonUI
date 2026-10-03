package com.kotlinjsonui.embed

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Collections

/** An embedded screen's ViewModel: who it is, what it heard, whether it was cleared. */
class EmbeddedPaneViewModel : ViewModel() {
    val serial = PaneLog.next++
    init {
        PaneLog.created += serial
        viewModelScope.launch { PaneLog.bus.collect { PaneLog.heard += "$serial:$it" } }
    }
    override fun onCleared() { PaneLog.cleared += serial }
}

object PaneLog {
    var next = 0
    val created: MutableList<Int> = Collections.synchronizedList(mutableListOf())
    val heard: MutableList<String> = Collections.synchronizedList(mutableListOf())
    val cleared: MutableList<Int> = Collections.synchronizedList(mutableListOf())
    val bus = MutableSharedFlow<String>(extraBufferCapacity = 8)
    fun reset() { next = 0; created.clear(); heard.clear(); cleared.clear() }
}

/**
 * An Embed's ViewModels live as long as the host destination, not as long as
 * its composition (jsonui-cli ticket kjui-embed-viewmodel-store-cleared-when-
 * host-destination-is-covered). Through 2.43.1 the store was a `remember`
 * cleared in onDispose: a push over the host destroyed the Embed's VMs, the
 * return made new ones, and what was sent while covered was lost. iOS keeps
 * them. Two Embeds of the same screen still get their own VMs (keyed by the
 * Embed's id).
 */
@RunWith(AndroidJUnit4::class)
class EmbedViewModelLifetimeTest {
    @get:Rule val rule = createComposeRule()
    private lateinit var nav: NavHostController
    private val panes = mutableMapOf<String, Int>()

    @Before fun reset() { PaneLog.reset() }

    private fun show() {
        rule.setContent {
            nav = rememberNavController()
            NavHost(nav, startDestination = "root") {
                composable("root") { Text("root") }
                composable("host") {
                    Column {
                        listOf("left", "right").forEach { id ->
                            EmbedContainer(embedId = id) { scope ->
                                val vm: EmbeddedPaneViewModel = viewModel(viewModelStoreOwner = scope.viewModelStoreOwner)
                                panes[id] = vm.serial
                                Text("$id ${vm.serial}")
                            }
                        }
                    }
                }
                composable("cover") { Text("cover") }
            }
        }
        rule.waitForIdle()
        rule.runOnIdle { nav.navigate("host") }
        rule.waitForIdle()
    }

    @Test
    fun coveringTheHostKeepsTheEmbedsViewModelAndItHearsWhatIsSentMeanwhile() {
        show()
        val before = panes.toMap()
        rule.runOnIdle { nav.navigate("cover") }
        rule.waitForIdle()
        rule.runOnIdle { PaneLog.bus.tryEmit("while-covered") }
        rule.waitForIdle()
        rule.runOnIdle { nav.popBackStack() }
        rule.waitForIdle()
        assertEquals("same ViewModels after the return", before, panes.toMap())
        assertEquals("created", 2, PaneLog.created.size)
        assertEquals("cleared while covered", listOf<Int>(), PaneLog.cleared.toList())
        assertEquals(before.values.map { "$it:while-covered" }.sorted(), PaneLog.heard.toList().sorted())
    }

    @Test
    fun poppingTheHostClearsTheEmbedsViewModels() {
        show()
        val before = panes.values.toSet()
        rule.runOnIdle { nav.popBackStack() }
        rule.waitForIdle()
        assertEquals(before, PaneLog.cleared.toSet())
    }

    @Test
    fun twoEmbedsOfTheSameScreenHaveTheirOwnViewModels() {
        show()
        assertEquals(2, panes.size)
        assertNotEquals(panes["left"], panes["right"])
        assertTrue(PaneLog.cleared.isEmpty())
    }

    @Test
    fun aSlotWhoseIdChangesInPlaceClearsTheOldViewModel() {
        val id = androidx.compose.runtime.mutableStateOf("a")
        var serial = -1
        rule.setContent {
            EmbedContainer(embedId = id.value) { scope ->
                val vm: EmbeddedPaneViewModel = viewModel(viewModelStoreOwner = scope.viewModelStoreOwner)
                serial = vm.serial
                Text(vm.serial.toString())
            }
        }
        rule.waitForIdle()
        val first = serial
        rule.runOnIdle { id.value = "b" }
        rule.waitForIdle()
        assertNotEquals(first, serial)
        assertEquals(listOf(first), PaneLog.cleared.toList())
    }
}
