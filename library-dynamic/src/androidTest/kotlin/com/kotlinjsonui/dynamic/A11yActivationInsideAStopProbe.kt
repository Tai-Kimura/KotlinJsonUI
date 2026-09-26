package com.kotlinjsonui.dynamic

import android.app.Application
import android.graphics.Bitmap
import android.view.accessibility.AccessibilityNodeInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Tab
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.JsonParser
import com.kotlinjsonui.components.CollectionStack
import com.kotlinjsonui.components.CollectionStackAxis
import com.kotlinjsonui.components.CollectionStackMode
import com.kotlinjsonui.components.Segment
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.LocalInteractionStopped
import com.kotlinjsonui.data.CollectionDataSection
import com.kotlinjsonui.data.CollectionDataSource
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.junit.Assert.assertEquals
import org.junit.Assume
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Whether a screen reader can operate what `userInteractionEnabled` stops.
 * Opt-in: the instrumentation argument `a11yActivationProbe=1`
 * (conformance-mobile, dispatched with android_probes).
 *
 * A touch is stopped by the stopping node's pointer input, which consumes
 * every event; TalkBack does not touch — its double tap sends the focused
 * node's ACTION_CLICK, which Compose answers by calling the node's OnClick
 * semantics action. This probe sends ACTION_CLICK through UiAutomation, as an
 * accessibility service does, to each node found by its resource id, and
 * counts what that moved (handler calls, or Switch value writes).
 *
 * Left: what `kjui build` emits for the two layouts below — kjui_tools of
 * jsonui-cli support4f/tap-rule-uie-round5 (on b9d10daf; jsonui-cli 1.9.0 in
 * progress: a control a stop holds reads disabled() and writes nothing, and
 * so does each node inside it a user operates — a Segment's tabs, a Radio
 * group's rows and RadioButtons),
 * ComposeBuilder over a layouts directory holding both, as build_file calls
 * it — the probe's body and the cell's body pasted unchanged (AxCodegen,
 * AxCellGeneratedView). The cell's View / Data /
 * ViewModel are the shapes `kjui g collection` writes (cell_generator.rb),
 * with the updateData case for onRow. The rows:
 * - no stop (the controls): a Label with onClick, a Button, a Switch, a
 *   Segment and a Radio group (the wrapper controls);
 * - inside a View with `userInteractionEnabled: false`: a Button, a Label
 *   with onClick, a View with onClick, a Switch, a Segment, a Radio group;
 * - inside a View with `userInteractionEnabled: "@{gateOpen}"`: the same
 *   four, and a Collection whose cell has a Label with onClick (the cell is
 *   a layout the stop reaches: it reads LocalInteractionStopped);
 * - a Button and a Switch with `userInteractionEnabled: false` of their own.
 *   ax_probe.json:
 *     {"type": "View", "id": "cgRoot", "orientation": "vertical", "spacing": 6, "child": [
 *       {"type": "Label", "id": "cgLblPlain", "text": "cgLblPlain", "onClick": "@{onLblPlain}"}
 *       {"type": "Button", "id": "cgBtnPlain", "text": "cgBtnPlain", "onClick": "@{onBtnPlain}"}
 *       {"type": "Switch", "id": "cgSwPlain", "isOn": "@{swPlain}"}
 *       {"type": "Segment", "id": "cgSegPlain", "items": ["a", "b"], "selectedIndex": "@{segPlain}"}
 *       {"type": "Radio", "id": "cgRadPlain", "items": ["a", "b"], "selectedValue": "@{radPlain}"}
 *       {"type": "View", "id": "cgParFalse", "orientation": "vertical", "spacing": 4, "userInteractionEnabled": false, "child": [
 *         {"type": "Button", "id": "cgBtnInFalse", "text": "cgBtnInFalse", "onClick": "@{onBtnInFalse}"}
 *         {"type": "Label", "id": "cgLblInFalse", "text": "cgLblInFalse", "onClick": "@{onLblInFalse}"}
 *         {"type": "View", "id": "cgViewInFalse", "width": 80, "height": 30, "background": "#3366CC", "onClick": "@{onViewInFalse}"}
 *         {"type": "Switch", "id": "cgSwInFalse", "isOn": "@{swInFalse}"}
 *         {"type": "Segment", "id": "cgSegInFalse", "items": ["a", "b"], "selectedIndex": "@{segInFalse}"}
 *         {"type": "Radio", "id": "cgRadInFalse", "items": ["a", "b"], "selectedValue": "@{radInFalse}"}
 *       ]}
 *       {"type": "View", "id": "cgParBound", "orientation": "vertical", "spacing": 4, "userInteractionEnabled": "@{gateOpen}", "child": [
 *         {"type": "Button", "id": "cgBtnInBound", "text": "cgBtnInBound", "onClick": "@{onBtnInBound}"}
 *         {"type": "Label", "id": "cgLblInBound", "text": "cgLblInBound", "onClick": "@{onLblInBound}"}
 *         {"type": "View", "id": "cgViewInBound", "width": 80, "height": 30, "background": "#3366CC", "onClick": "@{onViewInBound}"}
 *         {"type": "Switch", "id": "cgSwInBound", "isOn": "@{swInBound}"}
 *         {"type": "Collection", "id": "cgListBound", "items": "@{rows}", "width": 200, "height": 40, "layout": "horizontal", "sections": [{"cell": "ax_cell"}]}
 *       ]}
 *       {"type": "Button", "id": "cgBtnSelfFalse", "text": "cgBtnSelfFalse", "onClick": "@{onBtnSelfFalse}", "userInteractionEnabled": false}
 *       {"type": "Switch", "id": "cgSwSelfFalse", "isOn": "@{swSelfFalse}", "userInteractionEnabled": false}
 *     ]}
 *   ax_cell.json:
 *     {"type": "View", "id": "cgCell", "child": [
 *       {"type": "Label", "id": "cgCellLbl", "text": "cgCellLbl", "onClick": "@{onRow}"}
 *     ]}
 *
 * Middle: DynamicView with the same rows, and inside the bound stop one
 * Collection per spelling that draws as a Collection — Collection, and the
 * synonyms TableView, List, ListView and RecyclerView — each with one cell
 * of androidTest assets Layouts/a11y_probe_tap_cell.json (a Label with
 * onClick).
 *
 * Right, not emitted: the candidate fixes the emit chose from, on a Switch
 * inside a stop drawn as the codegen draws `userInteractionEnabled: false` —
 * semantics `disabled()` (what the emit now carries), `Switch(enabled =
 * false)`, and `clearAndSetSemantics` around it — and how each draws against
 * the Switch no stop holds, in pixels.
 *
 * Three runs: the bound stop open, closed, open again. Expected, per the
 * rule (attribute_definitions.json: the flag stops the view and everything
 * in it): the rows with no stop move 1 in every run (the controls: the walk
 * reaches the node and the action operates it); inside a stop, or under the
 * flag of their own, 0; inside the bound stop, and every cell under it, 1
 * open and 0 closed. The candidate fixes are printed, not judged. The
 * wrapper controls' items: aStoppedWrapperControlsItemsReadDisabled.
 */
@RunWith(AndroidJUnit4::class)
class A11yActivationInsideAStopProbe {
    @get:Rule
    val rule = createComposeRule()

    @Before
    fun skipUnlessRequested() {
        val enabled = InstrumentationRegistry.getArguments().getString("a11yActivationProbe") == "1"
        Assume.assumeTrue("opt-in: -Pandroid.testInstrumentationRunnerArguments.a11yActivationProbe=1", enabled)
    }

    private val SIDES = "axSides"

    private val counts = ConcurrentHashMap<String, AtomicInteger>()
    private fun hit(k: String) { counts.getOrPut(k) { AtomicInteger() }.incrementAndGet() }
    private fun calls(k: String) = counts[k]?.get() ?: 0

    private fun cellRows(name: String): CollectionDataSource = CollectionDataSource(
        sections = listOf(
            CollectionDataSection(
                cells = CollectionDataSection.CellData(
                    viewName = if (name.startsWith("cg")) "AxCell" else "a11y_probe_tap_cell",
                    data = listOf(mapOf<String, Any>("title" to name, "onRow" to { hit(name) }))
                )
            )
        )
    )

    /** The codegen rows' data surface. */
    inner class AxProbeData {
        var gateOpen: Boolean? by mutableStateOf(true)
        var swPlain by mutableStateOf(false)
        var swInFalse by mutableStateOf(false)
        var swInBound by mutableStateOf(false)
        var swSelfFalse by mutableStateOf(false)
        // The wrapper controls: a Segment's tabs and a Radio group's items.
        var segPlain by mutableStateOf(0)
        var radPlain by mutableStateOf("a")
        var segInFalse by mutableStateOf(0)
        var radInFalse by mutableStateOf("a")
        val onLblPlain: (() -> Unit)? = { hit("cgLblPlain") }
        val onBtnPlain: (() -> Unit)? = { hit("cgBtnPlain") }
        val onBtnInFalse: (() -> Unit)? = { hit("cgBtnInFalse") }
        val onLblInFalse: (() -> Unit)? = { hit("cgLblInFalse") }
        val onViewInFalse: (() -> Unit)? = { hit("cgViewInFalse") }
        val onBtnInBound: (() -> Unit)? = { hit("cgBtnInBound") }
        val onLblInBound: (() -> Unit)? = { hit("cgLblInBound") }
        val onViewInBound: (() -> Unit)? = { hit("cgViewInBound") }
        val onBtnSelfFalse: (() -> Unit)? = { hit("cgBtnSelfFalse") }
        val rows: CollectionDataSource? = cellRows("cgCell")
    }

    /**
     * A Switch, a Segment's tab and a Radio's item write through updateData:
     * each write counts as the row's.
     */
    inner class AxProbeViewModel(private val data: AxProbeData) {
        fun updateData(m: Map<String, Any>) {
            for ((k, v) in m) {
                hit("cg" + k.replaceFirstChar { it.uppercase() })
                when (k) {
                    "swPlain" -> data.swPlain = v as Boolean
                    "swInFalse" -> data.swInFalse = v as Boolean
                    "swInBound" -> data.swInBound = v as Boolean
                    "swSelfFalse" -> data.swSelfFalse = v as Boolean
                    "segPlain" -> data.segPlain = v as Int
                    "segInFalse" -> data.segInFalse = v as Int
                    "radPlain" -> data.radPlain = v as String
                    "radInFalse" -> data.radInFalse = v as String
                }
            }
        }
    }

    /** What `kjui build` emits for ax_probe, pasted unchanged. */
    @Composable
    private fun AxCodegen(data: AxProbeData, viewModel: AxProbeViewModel, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .testTag("cgRoot")
            .semantics { testTagsAsResourceId = true },
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        val resolved_text1 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = "cgLblPlain",
            fontFamily = resolved_text1.family,
            fontWeight = resolved_text1.weight,
            fontSize = resolved_text1.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text1.style ?: FontStyle.Normal,
            modifier = Modifier
                .testTag("cgLblPlain")
                .semantics { testTagsAsResourceId = true }
                .clickable(role = Role.Button) { data.onLblPlain?.invoke() }
        )
        Button(
            onClick = { data.onBtnPlain?.invoke() },
            modifier = Modifier
                .testTag("cgBtnPlain")
                .semantics { testTagsAsResourceId = true },
            shape = RoundedCornerShape(Configuration.Button.defaultCornerRadius.dp),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(
                            containerColor = Configuration.Button.defaultBackgroundColor,
                            disabledContainerColor = Configuration.Button.defaultBackgroundColor.copy(alpha = 0.5f),
                            contentColor = Configuration.Button.defaultTextColor,
                            disabledContentColor = Configuration.Button.defaultTextColor.copy(alpha = 0.5f)
                        )
        ) {
            Text("cgBtnPlain")
        }
        Switch(
            checked = data.swPlain,
            onCheckedChange = { newValue -> viewModel.updateData(mapOf("swPlain" to newValue)) },
            modifier = Modifier
                .testTag("cgSwPlain")
                .semantics { testTagsAsResourceId = true }
        )
        Segment(
            selectedTabIndex = data.segPlain,
            containerColor = Color.Transparent,
            modifier = Modifier
                .testTag("cgSegPlain")
                .semantics { testTagsAsResourceId = true }
        ) {
            Tab(
                selected = (data.segPlain == 0),
                onClick = {
                    viewModel.updateData(mapOf("segPlain" to 0))
                },
                text = { Text("a") }
            )
            Tab(
                selected = (data.segPlain == 1),
                onClick = {
                    viewModel.updateData(mapOf("segPlain" to 1))
                },
                text = { Text("b") }
            )
        }
        Column(
            modifier = Modifier
                .testTag("cgRadPlain")
                .semantics { testTagsAsResourceId = true }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.updateData(mapOf("radPlain" to "a"))
                    }
            ) {
                RadioButton(
                    selected = data.radPlain == "a",
                    onClick = {
                        viewModel.updateData(mapOf("radPlain" to "a"))
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("a", color = Color.Black)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.updateData(mapOf("radPlain" to "b"))
                    }
            ) {
                RadioButton(
                    selected = data.radPlain == "b",
                    onClick = {
                        viewModel.updateData(mapOf("radPlain" to "b"))
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("b", color = Color.Black)
            }
        }
        Column(
            modifier = Modifier
                .testTag("cgParFalse")
                .semantics { testTagsAsResourceId = true }
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            awaitPointerEvent(PointerEventPass.Initial).changes.forEach { it.consume() }
                        }
                    }
                },
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Button(
                onClick = { },
                modifier = Modifier
                    .testTag("cgBtnInFalse")
                    .semantics { testTagsAsResourceId = true }
                    .semantics { disabled() },
                shape = RoundedCornerShape(Configuration.Button.defaultCornerRadius.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = Configuration.Button.defaultBackgroundColor,
                                    disabledContainerColor = Configuration.Button.defaultBackgroundColor.copy(alpha = 0.5f),
                                    contentColor = Configuration.Button.defaultTextColor,
                                    disabledContentColor = Configuration.Button.defaultTextColor.copy(alpha = 0.5f)
                                )
            ) {
                Text("cgBtnInFalse")
            }
            val resolved_text2 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = null,
                size = null,
                italic = false
            ))
            Text(
                text = "cgLblInFalse",
                fontFamily = resolved_text2.family,
                fontWeight = resolved_text2.weight,
                fontSize = resolved_text2.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text2.style ?: FontStyle.Normal,
                modifier = Modifier
                    .testTag("cgLblInFalse")
                    .semantics { testTagsAsResourceId = true }
            )
            Box(
                modifier = Modifier
                    .testTag("cgViewInFalse")
                    .semantics { testTagsAsResourceId = true }
                    .requiredWidth(80.dp)
                    .requiredHeight(30.dp)
                    .background(Color(android.graphics.Color.parseColor("#3366CC")))
            ) {
            }
            Switch(
                checked = data.swInFalse,
                onCheckedChange = { newValue -> if (false) viewModel.updateData(mapOf("swInFalse" to newValue)) },
                modifier = Modifier
                    .testTag("cgSwInFalse")
                    .semantics { testTagsAsResourceId = true }
                    .semantics { disabled() }
            )
            Segment(
                selectedTabIndex = data.segInFalse,
                containerColor = Color.Transparent,
                modifier = Modifier
                    .testTag("cgSegInFalse")
                    .semantics { testTagsAsResourceId = true }
                    .semantics { disabled() }
            ) {
                Tab(
                    modifier = Modifier.semantics { disabled() },
                    selected = (data.segInFalse == 0),
                    onClick = {
                        if (false) viewModel.updateData(mapOf("segInFalse" to 0))
                    },
                    text = { Text("a") }
                )
                Tab(
                    modifier = Modifier.semantics { disabled() },
                    selected = (data.segInFalse == 1),
                    onClick = {
                        if (false) viewModel.updateData(mapOf("segInFalse" to 1))
                    },
                    text = { Text("b") }
                )
            }
            Column(
                modifier = Modifier
                    .testTag("cgRadInFalse")
                    .semantics { testTagsAsResourceId = true }
                    .semantics { disabled() }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { disabled() }.clickable {
                            if (false) viewModel.updateData(mapOf("radInFalse" to "a"))
                        }
                ) {
                    RadioButton(
                        modifier = Modifier.semantics { disabled() },
                        selected = data.radInFalse == "a",
                        onClick = {
                            if (false) viewModel.updateData(mapOf("radInFalse" to "a"))
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("a", color = Color.Black)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { disabled() }.clickable {
                            if (false) viewModel.updateData(mapOf("radInFalse" to "b"))
                        }
                ) {
                    RadioButton(
                        modifier = Modifier.semantics { disabled() },
                        selected = data.radInFalse == "b",
                        onClick = {
                            if (false) viewModel.updateData(mapOf("radInFalse" to "b"))
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("b", color = Color.Black)
                }
            }
        }
        CompositionLocalProvider(LocalInteractionStopped provides (LocalInteractionStopped.current || !(data.gateOpen ?: false))) {
            Column(
                modifier = Modifier
                    .testTag("cgParBound")
                    .semantics { testTagsAsResourceId = true }
                    .pointerInput((data.gateOpen ?: false)) {
                        if (!((data.gateOpen ?: false))) {
                            awaitPointerEventScope {
                                while (true) {
                                    awaitPointerEvent(PointerEventPass.Initial).changes.forEach { it.consume() }
                                }
                            }
                        }
                    },
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Button(
                    onClick = { if ((data.gateOpen ?: false)) { data.onBtnInBound?.invoke() } },
                    modifier = Modifier
                        .testTag("cgBtnInBound")
                        .semantics { testTagsAsResourceId = true }
                        .then(if (!((data.gateOpen ?: false))) Modifier.semantics { disabled() } else Modifier),
                    shape = RoundedCornerShape(Configuration.Button.defaultCornerRadius.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Configuration.Button.defaultBackgroundColor,
                                        disabledContainerColor = Configuration.Button.defaultBackgroundColor.copy(alpha = 0.5f),
                                        contentColor = Configuration.Button.defaultTextColor,
                                        disabledContentColor = Configuration.Button.defaultTextColor.copy(alpha = 0.5f)
                                    )
                ) {
                    Text("cgBtnInBound")
                }
                val resolved_text3 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = null,
                    size = null,
                    italic = false
                ))
                Text(
                    text = "cgLblInBound",
                    fontFamily = resolved_text3.family,
                    fontWeight = resolved_text3.weight,
                    fontSize = resolved_text3.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text3.style ?: FontStyle.Normal,
                    modifier = Modifier
                        .testTag("cgLblInBound")
                        .semantics { testTagsAsResourceId = true }
                        .then(if ((data.gateOpen ?: false)) Modifier.clickable(role = Role.Button) { data.onLblInBound?.invoke() } else Modifier)
                )
                Box(
                    modifier = Modifier
                        .testTag("cgViewInBound")
                        .semantics { testTagsAsResourceId = true }
                        .requiredWidth(80.dp)
                        .requiredHeight(30.dp)
                        .background(Color(android.graphics.Color.parseColor("#3366CC")))
                        .then(if ((data.gateOpen ?: false)) Modifier.clickable(role = Role.Button) { data.onViewInBound?.invoke() } else Modifier)
                ) {
                }
                Switch(
                    checked = data.swInBound,
                    onCheckedChange = { newValue -> if ((data.gateOpen ?: false)) viewModel.updateData(mapOf("swInBound" to newValue)) },
                    modifier = Modifier
                        .testTag("cgSwInBound")
                        .semantics { testTagsAsResourceId = true }
                        .then(if (!((data.gateOpen ?: false))) Modifier.semantics { disabled() } else Modifier)
                )
                val section0 = data.rows?.sections?.getOrNull(0)
                val cellData0 = section0?.cells
                CollectionStack(
                    mode = CollectionStackMode.LAZY,
                    axis = CollectionStackAxis.HORIZONTAL,
                    modifier = Modifier
                                        .testTag("cgListBound")
                                        .semantics { testTagsAsResourceId = true }
                                        .requiredWidth(200.dp)
                                        .requiredHeight(40.dp),
                    lazyContent = {
                        // Section 1: ax_cell
                        if (section0 != null) {
                            if (cellData0 != null) {
                                items(cellData0.data.size) { cellIndex ->
                                    val currentCellData = cellData0.data[cellIndex]
                                    val cellViewModel: AxCellViewModel = viewModel(key = "ax_cell_cell_0_${cellIndex}_${viewModel.hashCode()}")
                                    LaunchedEffect(currentCellData) { cellViewModel.updateData(currentCellData) }
                                    AxCellView(
                                        viewModel = cellViewModel,
                                        modifier = Modifier.testTag("cgListBound_item_$cellIndex")
                                    )
                                }
                            }
                        }
                    },
                    eagerContent = {
                        // Section 1: ax_cell
                        if (section0 != null) {
                            if (cellData0 != null) {
                                cellData0.data.forEachIndexed { cellIndex, _ ->
                                    val currentCellData = cellData0.data[cellIndex]
                                    val cellViewModel: AxCellViewModel = viewModel(key = "ax_cell_cell_0_${cellIndex}_${viewModel.hashCode()}")
                                    LaunchedEffect(currentCellData) { cellViewModel.updateData(currentCellData) }
                                    AxCellView(
                                        viewModel = cellViewModel,
                                        modifier = Modifier.testTag("cgListBound_item_$cellIndex")
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
        Button(
            onClick = { },
            modifier = Modifier
                .testTag("cgBtnSelfFalse")
                .semantics { testTagsAsResourceId = true }
                .semantics { disabled() }
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            awaitPointerEvent(PointerEventPass.Initial).changes.forEach { it.consume() }
                        }
                    }
                },
            shape = RoundedCornerShape(Configuration.Button.defaultCornerRadius.dp),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(
                            containerColor = Configuration.Button.defaultBackgroundColor,
                            disabledContainerColor = Configuration.Button.defaultBackgroundColor.copy(alpha = 0.5f),
                            contentColor = Configuration.Button.defaultTextColor,
                            disabledContentColor = Configuration.Button.defaultTextColor.copy(alpha = 0.5f)
                        )
        ) {
            Text("cgBtnSelfFalse")
        }
        Switch(
            checked = data.swSelfFalse,
            onCheckedChange = { newValue -> if (false) viewModel.updateData(mapOf("swSelfFalse" to newValue)) },
            modifier = Modifier
                .testTag("cgSwSelfFalse")
                .semantics { testTagsAsResourceId = true }
                .semantics { disabled() }
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            awaitPointerEvent(PointerEventPass.Initial).changes.forEach { it.consume() }
                        }
                    }
                }
        )
    }
    }

    private val rows = listOf(
        "LblPlain", "BtnPlain", "SwPlain",
        "BtnInFalse", "LblInFalse", "ViewInFalse", "SwInFalse",
        "BtnInBound", "LblInBound", "ViewInBound", "SwInBound",
        "BtnSelfFalse", "SwSelfFalse"
    )
    private val synonyms = listOf("Collection", "TableView", "List", "ListView", "RecyclerView")

    /**
     * The wrapper controls (a Segment's tabs and a Radio group's items are
     * nodes of their own inside the control): with no stop, and inside
     * `false`.
     */
    private val wrapperRows = listOf("SegPlain", "RadPlain", "SegInFalse", "RadInFalse")

    private fun dynamicLayout(): String {
        fun kids(s: String, gate: String, more: String = "") =
            "{\"type\": \"View\", \"id\": \"dynPar$s\", \"orientation\": \"vertical\", \"userInteractionEnabled\": $gate, \"child\": [" +
                "{\"type\": \"Button\", \"id\": \"dynBtnIn$s\", \"text\": \"dynBtnIn$s\", \"onClick\": \"@{on_dynBtnIn$s}\"}," +
                "{\"type\": \"Label\", \"id\": \"dynLblIn$s\", \"text\": \"dynLblIn$s\", \"onClick\": \"@{on_dynLblIn$s}\"}," +
                "{\"type\": \"View\", \"id\": \"dynViewIn$s\", \"width\": 80, \"height\": 30, \"background\": \"#3366CC\", \"onClick\": \"@{on_dynViewIn$s}\"}," +
                "{\"type\": \"Switch\", \"id\": \"dynSwIn$s\", \"isOn\": \"@{dynSwIn$s}\"}$more" +
                (if (s == "False") ",{\"type\": \"Segment\", \"id\": \"dynSegInFalse\", \"items\": [\"a\", \"b\"], \"selectedIndex\": \"@{dynSegInFalse}\"}," +
                    "{\"type\": \"Radio\", \"id\": \"dynRadInFalse\", \"items\": [\"a\", \"b\"], \"selectedValue\": \"@{dynRadInFalse}\"}" else "") + "]}"
        val lists = synonyms.joinToString("") { t ->
            ",{\"type\": \"$t\", \"id\": \"dynList$t\", \"items\": \"@{rows_$t}\", \"width\": 200, \"height\": 40, " +
                "\"layout\": \"horizontal\", \"sections\": [{\"cell\": \"a11y_probe_tap_cell\"}]}"
        }
        return "{\"type\": \"View\", \"orientation\": \"vertical\", \"child\": [" +
            "{\"type\": \"Label\", \"id\": \"dynLblPlain\", \"text\": \"dynLblPlain\", \"onClick\": \"@{on_dynLblPlain}\"}," +
            "{\"type\": \"Button\", \"id\": \"dynBtnPlain\", \"text\": \"dynBtnPlain\", \"onClick\": \"@{on_dynBtnPlain}\"}," +
            "{\"type\": \"Switch\", \"id\": \"dynSwPlain\", \"isOn\": \"@{dynSwPlain}\"}," +
            "{\"type\": \"Segment\", \"id\": \"dynSegPlain\", \"items\": [\"a\", \"b\"], \"selectedIndex\": \"@{dynSegPlain}\"}," +
            "{\"type\": \"Radio\", \"id\": \"dynRadPlain\", \"items\": [\"a\", \"b\"], \"selectedValue\": \"@{dynRadPlain}\"}," +
            kids("False", "false") + "," + kids("Bound", "\"@{gateOpen}\"", lists) + "," +
            "{\"type\": \"Button\", \"id\": \"dynBtnSelfFalse\", \"text\": \"dynBtnSelfFalse\", \"onClick\": \"@{on_dynBtnSelfFalse}\", \"userInteractionEnabled\": false}," +
            "{\"type\": \"Switch\", \"id\": \"dynSwSelfFalse\", \"isOn\": \"@{dynSwSelfFalse}\", \"userInteractionEnabled\": false}]}"
    }

    private fun dynamicData(open: Boolean): Map<String, Any> {
        val data = mutableMapOf<String, Any>("gateOpen" to open)
        for (row in rows) {
            val name = "dyn$row"
            if (row.startsWith("Sw")) data[name] = false else data["on_$name"] = { hit(name) }
        }
        // a Switch writes through updateData: each write counts as the row's
        data["updateData"] = { m: Map<String, Any> -> m.keys.forEach { hit(it) } }
        for (t in synonyms) data["rows_$t"] = dynamicRows.getValue(t)
        // the wrapper controls' selections (a write counts through updateData)
        for (row in wrapperRows) data["dyn$row"] = if (row.startsWith("Seg")) 0 else "a"
        return data
    }

    // One data source per list for the whole test, as a screen would hold it.
    private val dynamicRows: Map<String, CollectionDataSource> by lazy {
        synonyms.associateWith { cellRows("dynCell$it") }
    }

    @Composable
    private fun Fixes() {
        var semDisabled by remember { mutableStateOf(false) }
        var enabledFalse by remember { mutableStateOf(false) }
        var cleared by remember { mutableStateOf(false) }
        // the stop as the codegen draws `userInteractionEnabled: false`
        Column(
            modifier = Modifier.pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        awaitPointerEvent(PointerEventPass.Initial).changes.forEach { it.consume() }
                    }
                }
            },
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Switch(
                checked = semDisabled,
                onCheckedChange = { semDisabled = it; hit("fixSemDisabledSw") },
                modifier = Modifier.testTag("fixSemDisabledSw").semantics { disabled() }
            )
            Switch(
                checked = enabledFalse,
                onCheckedChange = { enabledFalse = it; hit("fixEnabledFalseSw") },
                enabled = false,
                modifier = Modifier.testTag("fixEnabledFalseSw")
            )
            Box(Modifier.clearAndSetSemantics { testTag = "fixClearSw" }) {
                Switch(checked = cleared, onCheckedChange = { cleared = it; hit("fixClearSw") })
            }
        }
    }

    /**
     * The codegen rows and the Dynamic rows side by side, each on half the
     * width and each scrolled. They were drawn in a Row as they came: the
     * emitted Radio's rows fill the width, so the codegen column took the
     * whole screen and DynamicView was laid out in the 24 dp left of it — a
     * column one glyph wide whose rows ran off the bottom — and each column
     * is taller than a tablet in landscape (measured on an API 35 Pixel
     * Tablet, 2026-09-27). A node off the screen is not in the tree an
     * accessibility service walks: the Dynamic rows below the first three
     * were "found false", and the rows a stop holds passed on moving 0. The
     * walk scrolls each node into view first (reveal), as a screen reader
     * scrolls to what it reads.
     */
    @Composable
    private fun Sides(data: AxProbeData, viewModel: AxProbeViewModel, dyn: Map<String, Any>, extra: @Composable () -> Unit) {
        Row(
            Modifier.padding(top = 24.dp).testTag(SIDES).semantics { testTagsAsResourceId = true },
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) { AxCodegen(data, viewModel) }
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                DynamicView(json = JsonParser.parseString(dynamicLayout()).asJsonObject, data = dyn)
            }
            extra()
        }
    }

    /** Scrolls the node [id] (under [scope] when given) into view. */
    private fun reveal(id: String, scope: String?) {
        runCatching {
            if (scope != null) rule.onNode(hasTestTag(scope), useUnmergedTree = true).performScrollTo()
            val matcher = if (scope == null) hasTestTag(id) else hasTestTag(id) and hasAnyAncestor(hasTestTag(scope))
            rule.onNode(matcher, useUnmergedTree = true).performScrollTo()
        }
        rule.waitForIdle()
    }

    private fun pixelDifference(a: Bitmap?, b: Bitmap?): String {
        if (a == null || b == null) return "missing"
        if (kotlin.math.abs(a.width - b.width) > 3 || kotlin.math.abs(a.height - b.height) > 3) {
            return "size ${a.width}x${a.height} vs ${b.width}x${b.height}"
        }
        val w = minOf(a.width, b.width)
        val h = minOf(a.height, b.height)
        var best = Int.MAX_VALUE
        var bestLargest = 0
        for (ax in 0..(a.width - w)) for (ay in 0..(a.height - h)) for (bx in 0..(b.width - w)) for (by in 0..(b.height - h)) {
            var over = 0
            var largest = 0
            for (y in 0 until h) for (x in 0 until w) {
                val p = a.getPixel(x + ax, y + ay)
                val q = b.getPixel(x + bx, y + by)
                val d = maxOf(
                    kotlin.math.abs(((p shr 16) and 0xFF) - ((q shr 16) and 0xFF)),
                    kotlin.math.abs(((p shr 8) and 0xFF) - ((q shr 8) and 0xFF)),
                    kotlin.math.abs((p and 0xFF) - (q and 0xFF))
                )
                largest = maxOf(largest, d)
                if (d > 24) over++
            }
            if (over < best) { best = over; bestLargest = largest }
        }
        return "${a.width}x${a.height} vs ${b.width}x${b.height}, pixels over 24: $best largest $bestLargest"
    }

    private fun collect(node: AccessibilityNodeInfo?, out: MutableMap<String, MutableList<AccessibilityNodeInfo>>) {
        if (node == null) return
        node.viewIdResourceName?.let { out.getOrPut(it.substringAfterLast('/')) { mutableListOf() }.add(node) }
        for (i in 0 until node.childCount) collect(node.getChild(i), out)
    }

    /**
     * The tree an accessibility service walks, read afresh: UiAutomation keeps
     * a cache of the nodes it has read, and after a scroll it handed back the
     * nodes where they stood before it for seconds (measured: a Dynamic row
     * 400 px above where Compose had it, 3 s on). Cleared before each walk
     * (UiAutomation.clearCache, API 34; the job runs API 35).
     */
    private fun tree(): Map<String, MutableList<AccessibilityNodeInfo>> {
        val automation = InstrumentationRegistry.getInstrumentation().uiAutomation
        repeat(50) {
            if (android.os.Build.VERSION.SDK_INT >= 34) automation.clearCache()
            val out = mutableMapOf<String, MutableList<AccessibilityNodeInfo>>()
            collect(automation.rootInActiveWindow, out)
            if (out.containsKey(SIDES)) return out
            Thread.sleep(100)
        }
        return emptyMap()
    }

    /**
     * The node for [id] (under [scope] when given), waited for up to 3 s: a
     * node scrolled into view a moment ago may not be in the tree yet.
     */
    private fun find(id: String, scope: String?): AccessibilityNodeInfo? {
        repeat(30) {
            findOnce(id, scope)?.let { return it }
            Thread.sleep(100)
        }
        return null
    }

    /** Where the window's origin is on the screen: the walk's bounds of the sides against Compose's (they never scroll). */
    private fun windowOffset(): Pair<Float, Float> {
        val want = rule.onNode(hasTestTag(SIDES), useUnmergedTree = true).fetchSemanticsNode().boundsInWindow
        val at = tree()[SIDES]?.firstOrNull()?.let { screenBounds(it) } ?: return 0f to 0f
        return (at.left - want.left) to (at.top - want.top)
    }

    private fun findOnce(id: String, scope: String?): AccessibilityNodeInfo? {
        val all = tree()
        if (scope == null) return all[id]?.firstOrNull()
        val root = all[scope]?.firstOrNull() ?: return null
        val under = mutableMapOf<String, MutableList<AccessibilityNodeInfo>>()
        collect(root, under)
        return under[id]?.firstOrNull()
    }

    private data class Target(val name: String, val id: String = name, val scope: String? = null)

    private val targets: List<Target> by lazy {
        rows.map { Target("cg$it") } + Target("cgCell", "cgCellLbl", "cgListBound") +
            rows.map { Target("dyn$it") } + synonyms.map { Target("dynCell$it", "dynCellLbl", "dynList$it") } +
            listOf(Target("fixSemDisabledSw"), Target("fixEnabledFalseSw"), Target("fixClearSw"))
    }

    private fun want(name: String, open: Boolean): Int? = when {
        name.startsWith("fix") -> null
        name.endsWith("Plain") -> 1
        "InFalse" in name || "SelfFalse" in name -> 0
        "InBound" in name || name.startsWith("cgCell") || name.startsWith("dynCell") -> if (open) 1 else 0
        else -> null
    }

    @Test
    fun aScreenReaderCannotOperateWhatAStopHolds() {
        val data = AxProbeData()
        val viewModel = AxProbeViewModel(data)
        var dyn by mutableStateOf(dynamicData(true))
        rule.setContent { Sides(data, viewModel, dyn) { Fixes() } }
        rule.waitForIdle()

        // how each candidate fix draws, against the Switch as emitted outside
        // a stop; the same Switch inside the stop as emitted is the
        // comparison's own control (the same pixels are expected there)
        fun image(tag: String): Bitmap? = runCatching {
            reveal(tag, null)
            rule.onNode(hasTestTag(tag), useUnmergedTree = true).captureToImage().asAndroidBitmap()
        }.getOrNull()
        // In pixels: how many differ by more than 24 (of 255) in a channel,
        // the two laid on each other at the offset of the best agreement
        // (a node's bounds round a pixel apart from place to place). The
        // stopped Switch as emitted against the one no stop holds says what
        // the stop draws; `enabled = false` is the control that greys.
        val plain = image("cgSwPlain")
        for (tag in listOf("cgSwInFalse", "cgSwInBound", "cgSwSelfFalse", "dynSwInFalse", "dynSwSelfFalse",
                "fixSemDisabledSw", "fixEnabledFalseSw", "fixClearSw")) {
            println("A11Y_PROBE visual $tag against cgSwPlain: ${pixelDifference(plain, image(tag))}")
        }

        val mismatches = mutableListOf<String>()
        for ((run, open) in listOf(1 to true, 2 to false, 3 to true)) {
            rule.runOnIdle {
                data.gateOpen = open
                dyn = dynamicData(open)
            }
            rule.waitForIdle()
            Thread.sleep(300)
            val seen = mutableSetOf<String>()
            for (t in targets) {
                // what Compose declares on the node (unmerged), for the table
                val sem = runCatching {
                    val matcher = if (t.scope == null) hasTestTag(t.id) else hasTestTag(t.id) and hasAnyAncestor(hasTestTag(t.scope))
                    val config = rule.onNode(matcher, useUnmergedTree = true).fetchSemanticsNode().config
                    "click=${SemanticsActions.OnClick in config} disabled=${SemanticsProperties.Disabled in config}"
                }.getOrElse { "none" }
                reveal(t.id, t.scope)
                val node = find(t.id, t.scope)?.also { it.refresh() }
                val before = calls(t.name)
                val returned = node?.performAction(AccessibilityNodeInfo.ACTION_CLICK) ?: false
                rule.waitForIdle()
                val moved = calls(t.name) - before
                val expected = want(t.name, open)
                if (node != null) seen += t.name
                println(
                    "A11Y_PROBE run=$run gate=${if (open) "open" else "closed"} ${t.name} found=${node != null} " +
                        "enabled=${node?.isEnabled} clickable=${node?.isClickable} " +
                        "clickAction=${node?.actionList?.any { it.id == AccessibilityNodeInfo.ACTION_CLICK }} $sem " +
                        "performed=$returned moved=$moved want=${expected ?: "-"}"
                )
                // A row the walk did not find is not a row that moved 0.
                if (expected != null && node == null) mismatches += "run $run ${t.name}: no node"
                if (expected != null && moved != expected) {
                    mismatches += "run $run ${t.name}: moved $moved (found ${node != null}, performed $returned), want $expected"
                }
            }
            for (required in listOf("cgCell", "cgSwInFalse", "dynSwInFalse") + synonyms.map { "dynCell$it" }) {
                if (open && required !in seen) mismatches += "run $run: no node for $required"
            }
        }
        for (m in mismatches) println("A11Y_PROBE MISMATCH $m")
        assertEquals("what a screen reader operated against what the stop allows", emptyList<String>(), mismatches)
    }

    private fun descendants(node: AccessibilityNodeInfo?, out: MutableList<AccessibilityNodeInfo> = mutableListOf()): List<AccessibilityNodeInfo> {
        if (node == null) return out
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            out += child
            descendants(child, out)
        }
        return out
    }

    private fun screenBounds(node: AccessibilityNodeInfo) = android.graphics.Rect().also { node.getBoundsInScreen(it) }

    /**
     * The items of the control [tag]: each node under it Compose gives a click
     * action (SemanticsActions.OnClick, unmerged), paired with the node an
     * accessibility service reaches for it — the node of the walk centred on
     * it that holds its bounds, the smallest. They were
     * the nodes under the control with ACTION_CLICK, which Compose leaves off
     * a disabled node and off a selected Tab or RadioButton (measured): the
     * items a stop disables were not counted at all — 0 inside the stop for
     * every control, "as many as with no stop" unreachable — and with no stop
     * a Segment counted 1 of its 2 tabs, a Radio 3 of its 2 rows and 2
     * RadioButtons.
     */
    private fun items(tag: String): List<Pair<SemanticsNode, AccessibilityNodeInfo?>> {
        reveal(tag, null)
        val nodes = rule.onAllNodes(
            hasAnyAncestor(hasTestTag(tag)) and SemanticsMatcher.keyIsDefined(SemanticsActions.OnClick),
            useUnmergedTree = true
        ).fetchSemanticsNodes()
        var paired: List<Pair<SemanticsNode, AccessibilityNodeInfo?>> = nodes.map { it to null }
        repeat(20) {
            val root = find(tag, null) ?: return paired
            val (dx, dy) = windowOffset()
            val walk = descendants(root)
            paired = nodes.map { n ->
                val b = n.boundsInWindow
                // The walk gives a node its touch bounds (a RadioButton's
                // 20 dp glyph reads 48 dp there): the node centred on the
                // item that holds it, the smallest (the first, in tree order).
                n to walk.filter { a ->
                    val ab = screenBounds(a)
                    kotlin.math.abs(ab.exactCenterX() - (b.center.x + dx)) <= 1.5f &&
                        kotlin.math.abs(ab.exactCenterY() - (b.center.y + dy)) <= 1.5f &&
                        ab.left <= b.left + dx + 1.5f && ab.top <= b.top + dy + 1.5f &&
                        ab.right >= b.right + dx - 1.5f && ab.bottom >= b.bottom + dy - 1.5f
                }.minByOrNull { a -> screenBounds(a).let { it.width() * it.height() } }
            }
            if (paired.all { it.second != null }) return paired
            Thread.sleep(100)
        }
        for ((n, a) in paired) if (a == null) println("A11Y_PROBE unpaired $tag item ${n.boundsInWindow}")
        return paired
    }

    private fun selectedNow(item: SemanticsNode): Boolean =
        rule.onNode(SemanticsMatcher("semantics id ${item.id}") { it.id == item.id }, useUnmergedTree = true)
            .fetchSemanticsNode().config.getOrElseNullable(SemanticsProperties.Selected) { null } == true

    /**
     * A wrapper control's items inside a stop (4f's ruling, jsonui-cli 1.9.0:
     * a stopped control does not say it is operable, down to its items). The
     * control's root reads `disabled()`; each tab of a Segment and each row
     * and RadioButton of a Radio group is a node of its own with a click
     * action, which TalkBack reached. For each control, each item (items():
     * every node under it with a click action in its semantics — 2 tabs, or
     * 2 rows and 2 RadioButtons): whether its accessibility node reads
     * enabled, whether it offers ACTION_CLICK, and what ACTION_CLICK on it
     * moved (the control's writes). Expected: with no stop, every item
     * enabled, and each one not selected offering the click and writing 1;
     * inside `false`, every item disabled, offering no click and writing
     * nothing — and as many items as with no stop, more than 0 (a node the
     * tree lost is not an item that reads disabled, and 0 items judge
     * nothing).
     */
    @Test
    fun aStoppedWrapperControlsItemsReadDisabled() {
        val data = AxProbeData()
        val viewModel = AxProbeViewModel(data)
        rule.setContent { Sides(data, viewModel, dynamicData(true)) {} }
        rule.waitForIdle()
        val mismatches = mutableListOf<String>()
        val itemCounts = mutableMapOf<String, Int>()
        for (side in listOf("cg", "dyn")) {
            for (row in wrapperRows) {
                val name = "$side$row"
                val found = items(name)
                itemCounts[name] = found.size
                // Last to first: each item is clicked while it is not the
                // chosen one (a click chooses it), so each is judged.
                for ((i, pair) in found.withIndex().reversed()) {
                    val (item, node) = pair
                    if (node == null) {
                        mismatches += "$name#$i: Compose declares an item the accessibility walk has no node for"
                        continue
                    }
                    node.refresh()
                    val selected = selectedNow(item)
                    val offered = node.actionList.any { it.id == AccessibilityNodeInfo.ACTION_CLICK }
                    val before = calls(name)
                    val performed = node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    rule.waitForIdle()
                    val moved = calls(name) - before
                    val label = (node.text ?: node.contentDescription ?: "").toString()
                    println(
                        "A11Y_PROBE wrap $name#$i label=$label class=${node.className} enabled=${node.isEnabled} " +
                            "selected=$selected clickOffered=$offered performed=$performed moved=$moved"
                    )
                    // With no stop: the item is enabled, offers the click, and
                    // the click operates it (Compose offers none on a selected
                    // Tab or RadioButton: the order above keeps each one
                    // unselected when it is clicked).
                    if (row.endsWith("Plain") && (!node.isEnabled || selected || !offered || moved != 1)) {
                        mismatches += "$name#$i ($label): no stop, enabled=${node.isEnabled} selected=$selected " +
                            "clickOffered=$offered moved=$moved, want enabled, not selected, a click offered, and 1"
                    }
                    if ("InFalse" in row && (node.isEnabled || offered || moved != 0)) {
                        mismatches += "$name#$i ($label): inside the stop, enabled=${node.isEnabled} clickOffered=$offered " +
                            "moved=$moved, want disabled, no click offered, and 0"
                    }
                }
            }
            for (control in listOf("Seg", "Rad")) {
                val plain = itemCounts["${side}${control}Plain"] ?: 0
                val stopped = itemCounts["${side}${control}InFalse"] ?: 0
                println("A11Y_PROBE wrap items ${side}$control: $plain with no stop, $stopped inside the stop")
                // 0 items judged nothing: every check above is per item.
                if (plain == 0 || stopped != plain) mismatches += "${side}$control: $plain items with no stop, $stopped inside the stop"
            }
        }
        for (m in mismatches) println("A11Y_PROBE MISMATCH $m")
        assertEquals("what a stopped wrapper control's items read and do", emptyList<String>(), mismatches)
    }
}

// The cell, as `kjui g collection` writes it (cell_generator.rb), with the
// updateData case `kjui build` gives a closure property.

data class AxCellData(
    var onRow: (() -> Unit)? = null
)

class AxCellViewModel(application: Application) : AndroidViewModel(application) {
    val jsonFileName = "ax_cell"

    private val _data = MutableStateFlow(AxCellData())
    val data: StateFlow<AxCellData> = _data.asStateFlow()

    @Suppress("UNCHECKED_CAST")
    fun updateData(updates: Map<String, Any>) {
        _data.update { current ->
            var updated = current
            updates.forEach { (key, value) ->
                updated = when (key) {
                    "onRow" -> updated.copy(onRow = value as? () -> Unit)
                    else -> updated
                }
            }
            updated
        }
    }
}

@Composable
fun AxCellView(
    viewModel: AxCellViewModel,
    modifier: Modifier = Modifier
) {
    val data by viewModel.data.collectAsState()

    AxCellGeneratedView(
        data = data,
        viewModel = viewModel,
        modifier = modifier
    )
}

/** What `kjui build` emits for ax_cell, pasted unchanged. */
@Suppress("UNUSED_PARAMETER")
@Composable
fun AxCellGeneratedView(
    data: AxCellData,
    viewModel: AxCellViewModel,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .testTag("cgCell")
            .semantics { testTagsAsResourceId = true }
    ) {
        val resolved_text1 = Configuration.Font.resolve(FontSpec(
            family = null,
            weight = null,
            size = null,
            italic = false
        ))
        Text(
            text = "cgCellLbl",
            fontFamily = resolved_text1.family,
            fontWeight = resolved_text1.weight,
            fontSize = resolved_text1.size ?: TextUnit.Unspecified,
            fontStyle = resolved_text1.style ?: FontStyle.Normal,
            modifier = Modifier
                .testTag("cgCellLbl")
                .semantics { testTagsAsResourceId = true }
                .then(if (!LocalInteractionStopped.current) Modifier.clickable(role = Role.Button) { data.onRow?.invoke() } else Modifier)
        )
    }
}
