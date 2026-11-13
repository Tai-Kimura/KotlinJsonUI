package com.kotlinjsonui.components

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp

/**
 * Reorderable rows on the real clock (ticket jui-collection-drag-to-reorder): a LazyColumn of the library's own
 * CollectionReorder rows, tagged `stops_item_<n>` and exposed as resource ids, for touches injected in real time
 * (UiAutomation) — a compose test rule drives its own clock, under which a real long press never fires. [ids] is the
 * order the "view model" holds, moved on onReorder; [moves] the onReorder calls.
 */
class ReorderRealClockActivity : ComponentActivity() {
    companion object {
        val INITIAL = (0 until 20).map { "id$it" }
        val ids = mutableStateListOf<String>()
        val moves = mutableListOf<Triple<Int, Int, Int>>()
        @Volatile var resumed = false
        fun reset() { resumed = false; ids.clear(); ids.addAll(INITIAL); moves.clear() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val reorder = CollectionReorder(isEnabled = true) { s, f, t -> moves.add(Triple(s, f, t)); ids.add(t, ids.removeAt(f)) }
        setContent {
            Box(Modifier.fillMaxSize().background(Color.White).semantics { testTagsAsResourceId = true }) {
                val state = rememberCollectionReorder(reorder)
                CollectionReorderScope(state) {
                    LazyColumn(
                        Modifier.fillMaxSize().padding(top = 40.dp).testTag("stops").collectionReorderContainer(state),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        collectionReorderRows(state, 0, ids.toList()) { i ->
                            Box(Modifier.collectionReorderActions().fillMaxWidth().height(64.dp).testTag("stops_item_$i")
                                .background(Color(0xFFF1F3F4))) { Text(ids[i], Modifier.padding(16.dp)) }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() { super.onResume(); resumed = true }
}
