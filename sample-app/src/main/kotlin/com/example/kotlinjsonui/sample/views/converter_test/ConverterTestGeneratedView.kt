package com.example.kotlinjsonui.sample.views.converter_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.data.ConverterTestData
import com.example.kotlinjsonui.sample.viewmodels.ConverterTestViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.border

@Composable
fun ConverterTestGeneratedView(
    data: ConverterTestData,
    viewModel: ConverterTestViewModel
) {
    // Generated Compose code from converter_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
        LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color(android.graphics.Color.parseColor("#F5F5F5")))
    ) {
        item {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Button(
                onClick = { },
            ) {
                Text("Button")
            }
            Text(
                text = "\${data.title}",
                fontSize = 24.sp,
                color = Color(android.graphics.Color.parseColor("#000000")),
                modifier = Modifier
                    .padding(top = 20.dp)
                    .padding(bottom = 20.dp)
            )
            Text(
                text = "GradientView Test",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                modifier = Modifier.padding(top = 10.dp)
            )
// TODO: Implement component type: GradientView
            Text(
                text = "BlurView Test",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                modifier = Modifier.padding(top = 20.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(top = 10.dp)
                    .background(Color(android.graphics.Color.parseColor("#4CAF50")))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    Text(
                        text = "BACKGROUND TEXT",
                        fontSize = 24.sp,
                        color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Text(
                        text = "This will be blurred",
                        fontSize = 16.sp,
                        color = Color(android.graphics.Color.parseColor("#FFD700")),
                        modifier = Modifier
                            .padding(top = 50.dp)
                            .padding(start = 20.dp)
                    )
                }
// TODO: Implement component type: BlurView
            }
            Text(
                text = "WebView Test",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                modifier = Modifier.padding(top = 20.dp)
            )
// TODO: Implement component type: WebView
            Text(
                text = "TabView Test",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                modifier = Modifier.padding(top = 20.dp)
            )
// TODO: Implement component type: TabView
            Text(
                text = "Collection Test",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                modifier = Modifier.padding(top = 20.dp)
            )
// TODO: Implement component type: Collection
            Text(
                text = "Image Test",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                modifier = Modifier.padding(top = 20.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.placeholder),
                contentDescription = "",
                modifier = Modifier
                    .size(100.dp, 100.dp)
                    .padding(top = 10.dp)
            )
            Text(
                text = "NetworkImage Test",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#333333")),
                modifier = Modifier.padding(top = 20.dp)
            )
            AsyncImage(
                model = "https://picsum.photos/400/300",
                contentDescription = "Image",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(200.dp)
                    .height(150.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .padding(top = 10.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
        }
        }
    }    // >>> GENERATED_CODE_END
}