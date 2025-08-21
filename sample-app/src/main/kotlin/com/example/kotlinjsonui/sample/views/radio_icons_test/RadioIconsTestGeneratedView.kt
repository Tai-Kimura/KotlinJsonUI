package com.example.kotlinjsonui.sample.views.radio_icons_test

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
import com.example.kotlinjsonui.sample.data.RadioIconsTestData
import com.example.kotlinjsonui.sample.viewmodels.RadioIconsTestViewModel

@Composable
fun RadioIconsTestGeneratedView(
    data: RadioIconsTestData,
    viewModel: RadioIconsTestViewModel
) {
    // Generated Compose code from radio_icons_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
        Box(
        modifier = Modifier
            Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        LazyColumn(
        ) {
            item {
            Column(
                modifier = Modifier.background(Color(android.graphics.Color.parseColor("#F5F5F5")))
            ) {
                Text(
                    text = "Radio Custom Icons Test",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 20.dp)
                        .padding(bottom = 20.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Default Radio Group",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                )
                Column(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                ) {
                }
                Column(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                ) {
                }
                Column(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                ) {
                }
                Text(
                    text = "Custom Icon Radio Group",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(top = 30.dp)
                        .padding(start = 20.dp)
                )
                Column(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                ) {
                }
                Column(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                ) {
                }
                Column(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                ) {
                }
                Text(
                    text = "Radio with Items",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(top = 30.dp)
                        .padding(start = 20.dp)
                )
                Column(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                        .padding(end = 20.dp)
                ) {
                }
                Text(
                    text = "${data.selectedColor}",
                    fontSize = 14.sp,
                    color = Color(android.graphics.Color.parseColor("#666666")),
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 20.dp)
                )
            }
            }
        }
    }    // >>> GENERATED_CODE_END
}