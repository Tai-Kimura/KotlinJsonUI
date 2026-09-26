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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
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
 * jsonui-cli support4f/tap-rule-uie-round4 (on aea0a0a4; jsonui-cli 1.9.0 in
 * progress: a control a stop holds reads disabled() and writes nothing),
 * ComposeBuilder over a layouts directory holding both, as build_file calls
 * it — the probe's body and the cell's body pasted unchanged (AxCodegen,
 * AxCellGeneratedView). The cell's View / Data /
 * ViewModel are the shapes `kjui g collection` writes (cell_generator.rb),
 * with the updateData case for onRow. The rows:
 * - no stop (the controls): a Label with onClick, a Button, a Switch;
 * - inside a View with `userInteractionEnabled: false`: a Button, a Label
 *   with onClick, a View with onClick, a Switch;
 * - inside a View with `userInteractionEnabled: "@{gateOpen}"`: the same
 *   four, and a Collection whose cell has a Label with onClick (the cell is
 *   a layout the stop reaches: it reads LocalInteractionStopped);
 * - a Button and a Switch with `userInteractionEnabled: false` of their own.
 *   ax_probe.json:
 *     {"type": "View", "id": "cgRoot", "orientation": "vertical", "spacing": 6, "child": [
 *       {"type": "Label", "id": "cgLblPlain", "text": "cgLblPlain", "onClick": "@{onLblPlain}"}
 *       {"type": "Button", "id": "cgBtnPlain", "text": "cgBtnPlain", "onClick": "@{onBtnPlain}"}
 *       {"type": "Switch", "id": "cgSwPlain", "isOn": "@{swPlain}"}
 *       {"type": "View", "id": "cgParFalse", "orientation": "vertical", "spacing": 4, "userInteractionEnabled": false, "child": [
 *         {"type": "Button", "id": "cgBtnInFalse", "text": "cgBtnInFalse", "onClick": "@{onBtnInFalse}"}
 *         {"type": "Label", "id": "cgLblInFalse", "text": "cgLblInFalse", "onClick": "@{onLblInFalse}"}
 *         {"type": "View", "id": "cgViewInFalse", "width": 80, "height": 30, "background": "#3366CC", "onClick": "@{onViewInFalse}"}
 *         {"type": "Switch", "id": "cgSwInFalse", "isOn": "@{swInFalse}"}
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
 * open and 0 closed. The candidate fixes are printed, not judged.
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

    /** A Switch writes through updateData: each write counts as the row's. */
    inner class AxProbeViewModel(private val data: AxProbeData) {
        fun updateData(m: Map<String, Any>) {
            for ((k, v) in m) {
                hit("cg" + k.replaceFirstChar { it.uppercase() })
                val on = v as Boolean
                when (k) {
                    "swPlain" -> data.swPlain = on
                    "swInFalse" -> data.swInFalse = on
                    "swInBound" -> data.swInBound = on
                    "swSelfFalse" -> data.swSelfFalse = on
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

    private fun dynamicLayout(): String {
        fun kids(s: String, gate: String, more: String = "") =
            "{\"type\": \"View\", \"id\": \"dynPar$s\", \"orientation\": \"vertical\", \"userInteractionEnabled\": $gate, \"child\": [" +
                "{\"type\": \"Button\", \"id\": \"dynBtnIn$s\", \"text\": \"dynBtnIn$s\", \"onClick\": \"@{on_dynBtnIn$s}\"}," +
                "{\"type\": \"Label\", \"id\": \"dynLblIn$s\", \"text\": \"dynLblIn$s\", \"onClick\": \"@{on_dynLblIn$s}\"}," +
                "{\"type\": \"View\", \"id\": \"dynViewIn$s\", \"width\": 80, \"height\": 30, \"background\": \"#3366CC\", \"onClick\": \"@{on_dynViewIn$s}\"}," +
                "{\"type\": \"Switch\", \"id\": \"dynSwIn$s\", \"isOn\": \"@{dynSwIn$s}\"}$more]}"
        val lists = synonyms.joinToString("") { t ->
            ",{\"type\": \"$t\", \"id\": \"dynList$t\", \"items\": \"@{rows_$t}\", \"width\": 200, \"height\": 40, " +
                "\"layout\": \"horizontal\", \"sections\": [{\"cell\": \"a11y_probe_tap_cell\"}]}"
        }
        return "{\"type\": \"View\", \"orientation\": \"vertical\", \"child\": [" +
            "{\"type\": \"Label\", \"id\": \"dynLblPlain\", \"text\": \"dynLblPlain\", \"onClick\": \"@{on_dynLblPlain}\"}," +
            "{\"type\": \"Button\", \"id\": \"dynBtnPlain\", \"text\": \"dynBtnPlain\", \"onClick\": \"@{on_dynBtnPlain}\"}," +
            "{\"type\": \"Switch\", \"id\": \"dynSwPlain\", \"isOn\": \"@{dynSwPlain}\"}," +
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

    private fun tree(): Map<String, MutableList<AccessibilityNodeInfo>> {
        val automation = InstrumentationRegistry.getInstrumentation().uiAutomation
        repeat(50) {
            val out = mutableMapOf<String, MutableList<AccessibilityNodeInfo>>()
            collect(automation.rootInActiveWindow, out)
            if (out.containsKey("cgSwPlain")) return out
            Thread.sleep(100)
        }
        return emptyMap()
    }

    /** The node for [id], searched under the node [scope] when given. */
    private fun find(id: String, scope: String?): AccessibilityNodeInfo? {
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
        rule.setContent {
            Row(
                Modifier.padding(top = 24.dp).semantics { testTagsAsResourceId = true },
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                AxCodegen(data, viewModel)
                DynamicView(json = JsonParser.parseString(dynamicLayout()).asJsonObject, data = dyn)
                Fixes()
            }
        }
        rule.waitForIdle()

        // how each candidate fix draws, against the Switch as emitted outside
        // a stop; the same Switch inside the stop as emitted is the
        // comparison's own control (the same pixels are expected there)
        fun image(tag: String): Bitmap? = runCatching {
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
                val node = find(t.id, t.scope)
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
