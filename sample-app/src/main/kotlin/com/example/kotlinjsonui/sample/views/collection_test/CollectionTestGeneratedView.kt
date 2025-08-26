package com.example.kotlinjsonui.sample.views.collection_test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.CollectionTestData
import com.example.kotlinjsonui.sample.viewmodels.CollectionTestViewModel
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.Box
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.SafeDynamicView
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.border

@Composable
fun CollectionTestGeneratedView(
    data: CollectionTestData,
    viewModel: CollectionTestViewModel
) {
    // Generated Compose code from collection_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "collection_test",
            data = data.toMap(viewModel),
            fallback = {
                // Show error or loading state when dynamic view is not available
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Dynamic view not available",
                        color = Color.Gray
                    )
                }
            },
            onError = { error ->
                // Log error or show error UI
                android.util.Log.e("DynamicView", "Error loading collection_test: \$error")
            },
            onLoading = {
                // Show loading indicator
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        ) { jsonContent ->
            // Parse and render the dynamic JSON content
            // This will be handled by the DynamicView implementation
        }
    } else {
        // Static Mode - use generated code
        Column(
        modifier = Modifier
            .background(Color(android.graphics.Color.parseColor("#F5F5F5")))
            .padding(16.dp)
    ) {
        Text(
            text = "Collection Test with cellClasses",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
        )
        Text(
            text = "${data.dynamicModeEnabled}",
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Button(
            onClick = { },
            modifier = Modifier.padding(bottom = 16.dp),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(
                            containerColor = Color(android.graphics.Color.parseColor("#007AFF"))
                        )
        ) {
            Text(
                text = "Toggle Dynamic Mode",
                color = Color(android.graphics.Color.parseColor("#FFFFFF")),
            )
        }
        Text(
            text = "Products Grid",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(top = 8.dp, end = 8.dp, bottom = 8.dp, start = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .height(400.dp)
                .padding(8.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(android.graphics.Color.parseColor("#FFFFFF")))
        ) {
            // Collection with data source: products
            val cellData = data.products.getCellData("product_cell")
            items(cellData.size) { index ->
                val item = cellData[index]
                ProductView(
                    data = item,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
        Text(
            text = "Simple List (1 Column)",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(bottom = 8.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .height(200.dp)
                .padding(8.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(android.graphics.Color.parseColor("#FFFFFF")))
        ) {
            // Collection with data source: simpleItems
            val cellData = data.simpleItems.getCellData("simple_cell")
            items(cellData.size) { index ->
                val item = cellData[index]
                SimpleView(
                    data = item,
                    modifier = Modifier
                )
            }
        }
    }    }
    // >>> GENERATED_CODE_END
}