package com.kotlinjsonui.dynamic.autofill

import android.app.assist.AssistStructure
import android.os.CancellationSignal
import android.service.autofill.AutofillService
import android.service.autofill.Dataset
import android.service.autofill.Field
import android.service.autofill.FillCallback
import android.service.autofill.FillRequest
import android.service.autofill.FillResponse
import android.service.autofill.Presentations
import android.service.autofill.SaveCallback
import android.service.autofill.SaveRequest
import android.view.autofill.AutofillId
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import java.util.concurrent.CopyOnWriteArrayList

/**
 * A test-only autofill service: it is what the Autofill framework hands a
 * field to. It records the hints of every node a fill request carries, and
 * offers one dataset ([SUGGESTION]) for a node whose hints hold `username`, so
 * a test can see the field as the framework sees it and the suggestion fill it.
 */
class ProbeAutofillService : AutofillService() {

    override fun onFillRequest(request: FillRequest, cancellationSignal: CancellationSignal, callback: FillCallback) {
        val structure = request.fillContexts.last().structure
        val nodes = mutableListOf<Pair<AutofillId, List<String>>>()
        for (i in 0 until structure.windowNodeCount) collect(structure.getWindowNodeAt(i).rootViewNode, nodes)
        nodes.forEach { (_, hints) -> if (hints.isNotEmpty()) seenHints.add(hints) }
        requests.add(nodes.map { it.second })

        val username = nodes.firstOrNull { "username" in it.second }?.first
        if (username == null) {
            callback.onSuccess(null)
            return
        }
        val view = RemoteViews(packageName, android.R.layout.simple_list_item_1).apply {
            setTextViewText(android.R.id.text1, SUGGESTION)
        }
        val dataset = Dataset.Builder()
            .setField(username, Field.Builder().setValue(AutofillValue.forText(SUGGESTION))
                .setPresentations(Presentations.Builder().setMenuPresentation(view).build()).build())
            .build()
        callback.onSuccess(FillResponse.Builder().addDataset(dataset).build())
    }

    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) = callback.onSuccess()

    private fun collect(node: AssistStructure.ViewNode, out: MutableList<Pair<AutofillId, List<String>>>) {
        node.autofillId?.let { id -> out.add(id to (node.autofillHints?.toList() ?: emptyList())) }
        for (i in 0 until node.childCount) collect(node.getChildAt(i), out)
    }

    companion object {
        const val SUGGESTION = "probe-user"
        /** The hints of every node in every fill request, request by request. */
        val requests = CopyOnWriteArrayList<List<List<String>>>()
        /** Every non-empty hint list seen. */
        val seenHints = CopyOnWriteArrayList<List<String>>()
        fun reset() { requests.clear(); seenHints.clear() }
    }
}
