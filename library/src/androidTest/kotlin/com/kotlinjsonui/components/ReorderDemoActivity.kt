package com.kotlinjsonui.components

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * The reorder demo for the video (ticket jui-collection-drag-to-reorder): the shape of the reporting face's 回る順番
 * — rows in a column an outer scroll carries (lazy: none), a grip at each row's right edge (reorderHandle), a break
 * row that does not move (reorderLockedProperty). The "view model" here is the list itself, moved on onReorder.
 */
class ReorderDemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val stops = remember {
                mutableStateListOf("A ブース 山崎", "B ブース 白州", "C ブース 余市", "── 休憩（動かない）──", "D ブース 宮城峡", "E ブース 秩父", "F ブース 厚岸")
            }
            var last by remember { mutableStateOf("まだ動かしていません") }
            val reorder = CollectionReorder(isEnabled = true, usesHandle = true) { _, from, to ->
                val row = stops.removeAt(from)
                stops.add(to, row)
                last = "onReorder(section 0, from $from, to $to)"
            }
            Column(Modifier.fillMaxSize().background(Color.White).verticalScroll(rememberScrollState()).padding(start = 16.dp, end = 16.dp, top = 40.dp, bottom = 16.dp)) {
                Text("回る順番（並べ替えの確かめ・Android）", fontSize = 20.sp)
                Text("右端の ≡ を長押ししてドラッグ", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 12.dp))
                CollectionReorderContainer(reorder, Modifier.testTag("stops"), scrollsItself = false) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        CollectionReorderRows(0, stops.toList(), locked = setOf(stops.indexOf("── 休憩（動かない）──"))) { i ->
                            val name = stops[i]
                            val isBreak = name.startsWith("──")
                            Row(
                                Modifier.collectionReorderActions().fillMaxWidth().height(64.dp).testTag("stops_item_$i")
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isBreak) Color(0xFFEDE7F6) else Color(0xFFF1F3F4))
                                    .clickable { },
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text("${i + 1}. $name", Modifier.weight(1f).padding(start = 16.dp), fontSize = 17.sp)
                                if (!isBreak) {
                                    Box(Modifier.size(56.dp).collectionReorderHandle().testTag("grip_$i"), contentAlignment = Alignment.Center) {
                                        Text("≡", fontSize = 26.sp, color = Color.DarkGray)
                                    }
                                }
                            }
                        }
                    }
                }
                Text(last, fontSize = 14.sp, color = Color(0xFF1565C0), modifier = Modifier.padding(top = 16.dp).testTag("last"))
            }
        }
    }
}
