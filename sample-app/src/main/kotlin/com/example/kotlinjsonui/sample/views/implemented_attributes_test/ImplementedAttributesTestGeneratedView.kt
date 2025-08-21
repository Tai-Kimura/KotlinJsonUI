package com.example.kotlinjsonui.sample.views.implemented_attributes_test

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
import com.example.kotlinjsonui.sample.data.ImplementedAttributesTestData
import com.example.kotlinjsonui.sample.viewmodels.ImplementedAttributesTestViewModel
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.ButtonDefaults
import com.kotlinjsonui.components.CustomTextField
import com.kotlinjsonui.components.CustomTextFieldWithMargins
import androidx.compose.ui.text.TextStyle
import androidx.compose.material3.TabRow
import androidx.compose.material3.Tab
import com.kotlinjsonui.components.SelectBox
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebChromeClient
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun ImplementedAttributesTestGeneratedView(
    data: ImplementedAttributesTestData,
    viewModel: ImplementedAttributesTestViewModel
) {
    // Generated Compose code from implemented_attributes_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
        Box(
        modifier = Modifier
            Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color(android.graphics.Color.parseColor("#F0F0F0")))
        ) {
            item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Implemented Attributes Test",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                Text(
                    text = "1. alignCenterVerticalView & alignCenterHorizontalView",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                ConstraintLayout(
                    modifier = Modifier
                        .height(200.dp)
                        .background(Color(android.graphics.Color.parseColor("#f0f0f0")))
                ) {
                    val target1 = createRef()
                    val view_1 = createRef()
                    val view_2 = createRef()

                    Text(
                        modifier = Modifier.constrainAs(target1) {
                            top.linkTo(parent.top, margin = 50.dp)
                            start.linkTo(parent.start, margin = 50.dp)
                        }
                            .background(Color(android.graphics.Color.parseColor("#ff0000")))
                            .padding(10.dp),
                        text = "Target View",
                        color = Color(android.graphics.Color.parseColor("#ffffff"))
                    )
                    Text(
                        modifier = Modifier.constrainAs(view_1) {
                            top.linkTo(target1.top)
                            bottom.linkTo(target1.bottom)
                            end.linkTo(parent.end, margin = 20.dp)
                        }
                            .background(Color(android.graphics.Color.parseColor("#00ff00")))
                            .padding(10.dp),
                        text = "Centered V"
                    )
                    Text(
                        modifier = Modifier.constrainAs(view_2) {
                            start.linkTo(target1.start)
                            end.linkTo(target1.end)
                            bottom.linkTo(parent.bottom, margin = 20.dp)
                        }
                            .background(Color(android.graphics.Color.parseColor("#0000ff")))
                            .padding(10.dp),
                        text = "Centered H",
                        color = Color(android.graphics.Color.parseColor("#ffffff"))
                    )
                }
                Text(
                    text = "2. idealWidth & idealHeight",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .padding(bottom = 10.dp)
                )
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .background(Color(android.graphics.Color.parseColor("#ffcccc")))
                ) {
                    Text(
                        text = "Ideal Size View (200x100)",
                        modifier = Modifier.align(Alignment.TopStart)
                    )
                }
                Text(
                    text = "3. clipToBounds",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .padding(bottom = 10.dp)
                )
                Box(
                    modifier = Modifier
                        .width(150.dp)
                        .height(50.dp)
                        .background(Color(android.graphics.Color.parseColor("#ccffcc")))
                ) {
                    Text(
                        text = "This text is very long and should be clipped to the bounds of the parent view",
                        fontSize = 16.sp,
                        modifier = Modifier.align(Alignment.TopStart)
                    )
                }
                Text(
                    text = "4. direction & distribution",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .padding(bottom = 10.dp)
                )
                Row(
                    modifier = Modifier
                        .height(50.dp)
                        .background(Color(android.graphics.Color.parseColor("#ccccff"))),
                    reverseLayout = true,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = "1",
                        modifier = Modifier.background(Color(android.graphics.Color.parseColor("#ff6666"))),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "2",
                        modifier = Modifier.background(Color(android.graphics.Color.parseColor("#66ff66"))),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "3",
                        color = Color(android.graphics.Color.parseColor("#ffffff")),
                        modifier = Modifier.background(Color(android.graphics.Color.parseColor("#6666ff"))),
                        textAlign = TextAlign.Center
                    )
                }
                Text(
                    text = "5. edgeInset (Label padding)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .padding(bottom = 10.dp)
                )
                Text(
                    text = "Text with edgeInset",
                    fontSize = 16.sp,
                    modifier = Modifier
                        .background(Color(android.graphics.Color.parseColor("#ffffcc")))
                        .padding(20.dp)
                )
                Text(
                    text = "6. Button State Attributes",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .padding(bottom = 10.dp)
                )
                Button(
                    onClick = { },
                    modifier = Modifier
                        .padding(10.dp)
                        .padding(bottom = 10.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(android.graphics.Color.parseColor("#cccccc"))
                                        )
                ) {
                    Text(
                        text = "Tap Me (tapBackground)",
                        color = Color(android.graphics.Color.parseColor("#000000")),
                    )
                }
                Button(
                    onClick = { },
                    modifier = Modifier
                        .padding(10.dp)
                        .padding(bottom = 10.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(android.graphics.Color.parseColor("#0000ff")),
                                            // hilightColor: #ff0000 - Use InteractionSource for pressed state
                                        )
                ) {
                    Text(
                        text = "Highlight Me",
                        color = Color(android.graphics.Color.parseColor("#ffffff")),
                    )
                }
                Button(
                    onClick = { },
                    modifier = Modifier.padding(10.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(android.graphics.Color.parseColor("#0000ff")),
                                            disabledContainerColor = Color(android.graphics.Color.parseColor("#f0f0f0")),
                                            disabledContentColor = Color(android.graphics.Color.parseColor("#999999"))
                                        ),
                    enabled = false
                ) {
                    Text(
                        text = "Disabled Button",
                        color = Color(android.graphics.Color.parseColor("#ffffff")),
                    )
                }
                Text(
                    text = "7. TextField Events",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .padding(bottom = 10.dp)
                )
                CustomTextFieldWithMargins(
                    value = data.textFieldValue,
                    onValueChange = { newValue -> viewModel.updateData(mapOf("textFieldValue" to newValue)) },
                    boxModifier = Modifier
                        .padding(bottom = 10.dp),
                    placeholder = { Text("Type something...") },
                    textStyle = TextStyle(fontSize = 16.sp, color = Color(android.graphics.Color.parseColor("#000000")))
                )
                Text(
                    text = "8. Radio Custom Icons",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .padding(bottom = 10.dp)
                )
                Column(
                ) {
                    Column(
                        modifier = Modifier.padding(bottom = 5.dp)
                    ) {
                    }
                    Column(
                        modifier = Modifier.padding(bottom = 5.dp)
                    ) {
                    }
                }
                Text(
                    text = "9. Segment Control",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .padding(bottom = 10.dp)
                )
                TabRow(
                    selectedTabIndex = 0,
                    modifier = Modifier.padding(bottom = 10.dp)
                ) {
                }
                Text(
                    text = "10. SelectBox Attributes",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .padding(bottom = 10.dp)
                )
                SelectBox(
                    value = "",
                    onValueChange = { },
                    options = listOf("Option A", "Option B", "Option C"),
                    backgroundColor = Color(android.graphics.Color.parseColor("#FFFFFF")),
                    textColor = Color(android.graphics.Color.parseColor("#0000ff")),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(bottom = 10.dp)
                )
                Text(
                    text = "11. Web Component",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .padding(bottom = 10.dp)
                )
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            settings.javaScriptEnabled = true
                            loadUrl("https://www.example.com")
                            webViewClient = WebViewClient()
                            webChromeClient = WebChromeClient()
                        }
                    },
                    update = { webView ->
                    },
                    modifier = Modifier
                        .height(200.dp)
                        .padding(bottom = 20.dp)
                )
            }
            }
        }
    }    // >>> GENERATED_CODE_END
}