package com.kotlinjsonui.conformance

import android.content.Context
import androidx.compose.runtime.Composable
import com.google.gson.JsonParser
import com.kotlinjsonui.core.JsonUITestValue
import com.kotlinjsonui.core.ScreenMarker
import com.kotlinjsonui.core.TestValueVerdict
import com.kotlinjsonui.core.rememberTestBridge
import com.kotlinjsonui.dynamic.DataBindingContext

/**
 * The testBridge fixtures (jsonui-cli INTERACTIVE_HOST_CONTRACT.md §6):
 * jsonui-test-runner spec §10 — an initialState delivered at launch, the
 * screen's receipt, and `state` read through the bridge.
 *
 * - Codegen face: the kjui-generated screen view carries the bridge itself;
 *   its screen id is the staged layout's (`fx_NNNN`, from the generated
 *   registry).
 * - Dynamic face: this host drives DynamicView directly, so it attaches
 *   KotlinJsonUI's bridge itself ([DynamicTestBridge]) over the fixture's
 *   store, with KotlinJsonUI's check judging every value. The screen id is
 *   this host's choice: [dynamicScreenId].
 *
 * The suite (same process) delivers the payload, checks the receipt, points
 * `state` at [screenId] and remounts — no fixture-specific code anywhere.
 */
object TestBridgeHost {

    /** One `testBridge.data` entry: the layout's declaration, default as JSON text. */
    data class Field(val name: String, val declaredClass: String, val defaultJson: String?)

    @Volatile
    private var fieldsById: Map<String, List<Field>>? = null

    /** The declared fields of a testBridge fixture; null for any other fixture. */
    fun fieldsFor(context: Context, fixtureId: String): List<Field>? =
        (fieldsById ?: parse(context).also { fieldsById = it })[fixtureId]

    private fun parse(context: Context): Map<String, List<Field>> {
        val text = context.assets.open("conformance/manifest.json").bufferedReader().use { it.readText() }
        val out = mutableMapOf<String, List<Field>>()
        for (element in JsonParser.parseString(text).asJsonObject.getAsJsonArray("fixtures")) {
            val fixture = element.asJsonObject
            val id = fixture.get("id")?.asString ?: continue
            val bridge = fixture.get("testBridge")?.takeIf { it.isJsonObject }?.asJsonObject ?: continue
            out[id] = bridge.getAsJsonArray("data").mapNotNull { entry ->
                val o = entry.takeIf { it.isJsonObject }?.asJsonObject ?: return@mapNotNull null
                Field(
                    name = o.get("name")?.asString ?: return@mapNotNull null,
                    declaredClass = o.get("class")?.asString ?: return@mapNotNull null,
                    defaultJson = o.get("defaultValue")?.toString()
                )
            }
        }
        return out
    }

    /** This (dynamic) host's screen id for a fixture: the key of its payload and of `state`. */
    fun dynamicScreenId(fixtureId: String): String = "tb_" + fixtureId.replace('/', '_')

    /** The screen id this face gives [fixtureId]'s screen (§6 jobs 1 and 3); null when it has none. */
    fun screenId(fixtureId: String): String? = when (FixtureHost.hostMode.value) {
        "codegen" -> CodegenFixtureEntries.screenIds[fixtureId]
        else -> dynamicScreenId(fixtureId)
    }

    /**
     * The typed defaults of the declared fields, as the Data would hold them:
     * each `defaultValue` judged by KotlinJsonUI's check against its class.
     * A field whose default is absent or null starts absent (= null).
     */
    fun defaults(fields: List<Field>): Map<String, Any> = buildMap {
        for (field in fields) {
            val raw = field.defaultJson?.let { JsonUITestValue.parseJson(it) } ?: continue
            val verdict = JsonUITestValue.check(raw, field.declaredClass)
            if (verdict is TestValueVerdict.Accepted) verdict.value?.let { put(field.name, it) }
        }
    }
}

/**
 * The dynamic face's bridge for a testBridge fixture: KotlinJsonUI's
 * [rememberTestBridge] over [bindingContext] (the store DynamicView draws
 * from), and the marker that carries it. A null value is an absent key — the
 * store cannot hold null — and reads back as null in the snapshot.
 */
@Composable
fun DynamicTestBridge(
    fixtureId: String,
    fields: List<TestBridgeHost.Field>,
    bindingContext: DataBindingContext,
    stateData: Map<String, Any>
) {
    val screenId = TestBridgeHost.dynamicScreenId(fixtureId)
    val declared = fields.associate { it.name to it.declaredClass }
    val bridge = rememberTestBridge(
        screenId,
        snapshot = { JsonUITestValue.snapshot(declared.keys.associateWith { stateData[it] }, declared) },
        apply = { values ->
            JsonUITestValue.applyAll(values, declared, update = { accepted ->
                val next = LinkedHashMap<String, Any>(bindingContext.data.value)
                accepted.forEach { (key, value) -> if (value == null) next.remove(key) else next[key] = value }
                bindingContext.setData(next)
            })
        }
    )
    ScreenMarker(screenId, bridge)
}
