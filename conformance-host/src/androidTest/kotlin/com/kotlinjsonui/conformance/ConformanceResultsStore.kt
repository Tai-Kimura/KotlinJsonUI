package com.kotlinjsonui.conformance

import java.io.File
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

/** Outcome of a single fixture. */
data class FixtureResult(
    val id: String,
    val status: String, // pass | fail | error | skipped | running
    val detail: String = "",
    val screenshot: String? = null
)

/**
 * Crash-safe result store.
 *
 * Appends one JSON line per fixture outcome to progress.jsonl as the suite
 * runs. A "running" marker is written before executing a fixture; if the app
 * process is killed mid-fixture, the next instrumentation invocation finds
 * the dangling marker. A single dangling marker is NOT an error: on slow CI
 * runners the caller's per-attempt timeout legitimately chops one in-flight
 * fixture per attempt, and treating that as an error poisons the final
 * results (the report gate fails on chop artifacts, not real failures). So
 * the first dangle re-runs the fixture; only a fixture that dangles twice
 * (started twice, never finished — a genuine hang or a crash loop) becomes
 * an error. When every manifest fixture has an outcome, [writeFinalResults]
 * emits the RESULTS_SCHEMA compliant android.results.json.
 */
class ConformanceResultsStore(private val outputDir: File) {
    private val progressFile = File(outputDir, "progress.jsonl")
    private val json = Json { ignoreUnknownKeys = true }

    init {
        outputDir.mkdirs()
    }

    /**
     * id -> latest recorded outcome. A fixture whose latest marker is
     * "running" dangled: dangled once -> dropped from the map so the caller
     * re-runs it (per-attempt timeout chop on a slow runner); dangled twice
     * -> recorded as an error (genuine hang / crash loop).
     */
    fun loadCompleted(): Map<String, FixtureResult> {
        if (!progressFile.isFile) return emptyMap()
        val latest = LinkedHashMap<String, FixtureResult>()
        val startCounts = HashMap<String, Int>()
        val finishCounts = HashMap<String, Int>()
        progressFile.readLines().forEach { line ->
            if (line.isBlank()) return@forEach
            val o = try {
                json.parseToJsonElement(line).jsonObject
            } catch (_: Exception) {
                return@forEach // torn write from a crash — ignore
            }
            val id = o["id"]?.jsonPrimitive?.content ?: return@forEach
            val status = o["status"]?.jsonPrimitive?.content ?: "error"
            if (status == "running") {
                startCounts[id] = (startCounts[id] ?: 0) + 1
            } else {
                finishCounts[id] = (finishCounts[id] ?: 0) + 1
            }
            latest[id] = FixtureResult(
                id = id,
                status = status,
                detail = o["detail"]?.jsonPrimitive?.content ?: "",
                screenshot = o["screenshot"]?.jsonPrimitive?.content
            )
        }
        val resolved = LinkedHashMap<String, FixtureResult>()
        latest.forEach { (id, r) ->
            if (r.status != "running") {
                resolved[id] = r
                return@forEach
            }
            val dangles = (startCounts[id] ?: 0) - (finishCounts[id] ?: 0)
            if (dangles >= 2) {
                resolved[id] = r.copy(
                    status = "error",
                    detail = "instrumentation died executing this fixture twice " +
                        "(started $dangles times without finishing — hang or crash loop)"
                )
            }
            // dangles == 1: omit -> the suite re-runs this fixture this attempt
        }
        return resolved
    }

    fun markRunning(id: String) {
        appendLine(FixtureResult(id, "running"))
    }

    fun record(result: FixtureResult) {
        appendLine(result)
    }

    private fun appendLine(result: FixtureResult) {
        val line = buildJsonObject {
            put("id", result.id)
            put("status", result.status)
            put("detail", result.detail)
            result.screenshot?.let { put("screenshot", it) }
        }.toString()
        progressFile.appendText(line + "\n")
    }

    /**
     * Write android.results.json (atomically) with one entry per manifest
     * fixture, in manifest order.
     */
    fun writeFinalResults(
        manifest: ConformanceManifest,
        manifestHash: String,
        outcomes: Map<String, FixtureResult>,
        runnerName: String,
        runnerVersion: String
    ): File {
        val payload = buildJsonObject {
            put("platform", "android")
            put("manifestHash", manifestHash)
            put("runner", buildJsonObject {
                put("name", runnerName)
                put("version", runnerVersion)
            })
            put("results", buildJsonArray {
                manifest.fixtures.forEach { fixture ->
                    val r = outcomes[fixture.id] ?: FixtureResult(
                        fixture.id, "error", "no outcome recorded (suite aborted)"
                    )
                    add(buildJsonObject {
                        put("id", r.id)
                        put("status", r.status)
                        put("detail", r.detail)
                        r.screenshot?.let { put("screenshot", it) }
                    })
                }
            })
        }

        val pretty = Json { prettyPrint = true; prettyPrintIndent = "  " }
        val target = File(outputDir, "android.results.json")
        val tmp = File(outputDir, "android.results.json.tmp")
        tmp.writeText(pretty.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), payload) + "\n")
        tmp.renameTo(target)
        return target
    }
}
