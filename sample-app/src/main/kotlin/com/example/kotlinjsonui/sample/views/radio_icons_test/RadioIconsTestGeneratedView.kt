package com.example.kotlinjsonui.sample.views.radio_icons_test

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.R
import com.example.kotlinjsonui.sample.data.RadioIconsTestData
import com.example.kotlinjsonui.sample.viewmodels.RadioIconsTestViewModel
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont
import com.kotlinjsonui.dynamic.LocalSafeAreaConfig
import com.kotlinjsonui.dynamic.SafeAreaConfig

@Composable
fun RadioIconsTestGeneratedView(
    data: RadioIconsTestData,
    viewModel: RadioIconsTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from radio_icons_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "radio_icons_test",
            modifier = modifier,
            data = data.toMap(),
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
                android.util.Log.e("DynamicView", "Error loading radio_icons_test: \$error")
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
        val safeAreaConfig = LocalSafeAreaConfig.current
    val edges = mutableListOf("all").apply {
        if (safeAreaConfig.ignoreBottom) {
            remove("bottom")
            if (contains("all")) { remove("all"); addAll(listOf("top", "start", "end")) }
        }
        if (safeAreaConfig.ignoreTop) {
            remove("top")
            if (contains("all")) { remove("all"); addAll(listOf("bottom", "start", "end")) }
        }
    }.distinct()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(if (edges.contains("all")) Modifier.systemBarsPadding() else Modifier)
            .then(if (!edges.contains("all") && edges.contains("top")) Modifier.statusBarsPadding() else Modifier)
            .then(if (!edges.contains("all") && edges.contains("bottom")) Modifier.navigationBarsPadding() else Modifier)
            .imePadding()
    ) {
        LazyColumn(
            modifier = Modifier.imePadding()
        ) {
            item {
            Column(
                modifier = Modifier
                    .testTag("container")
                    .semantics { testTagsAsResourceId = true }
                    .background(colorResource(R.color.white_23))
            ) {
                val resolved_text292 = Configuration.Font.resolve(FontSpec(
                    family = null,
                    weight = FontWeight.Bold,
                    size = 24.sp,
                    italic = false
                ))
                Text(
                    text = stringResource(R.string.radio_icons_test_radio_custom_icons_test),
                    fontFamily = resolved_text292.family,
                    fontWeight = resolved_text292.weight,
                    fontSize = resolved_text292.size ?: TextUnit.Unspecified,
                    fontStyle = resolved_text292.style ?: FontStyle.Normal,
                    style = TextStyle(lineHeight = 31.2.sp),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 20.dp)
                        .padding(bottom = 20.dp),
                    textAlign = TextAlign.Center
                )
                Section1(data, viewModel)
                Section2(data, viewModel)
                Section3(data, viewModel)
                Section4(data, viewModel)
                Section5(data, viewModel)
                Section6(data, viewModel)
                Section7(data, viewModel)
                Section8(data, viewModel)
                Section9(data, viewModel)
                Section10(data, viewModel)
            }
            }
        }
    }    }
    // >>> GENERATED_CODE_END
}

// >>> RESPONSIVE_HELPERS_START
@Composable
private fun Section1(
    data: RadioIconsTestData,
    viewModel: RadioIconsTestViewModel
) {
    val resolved_text293 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.SemiBold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.radio_icons_test_default_radio_group),
        fontFamily = resolved_text293.family,
        fontWeight = resolved_text293.weight,
        fontSize = resolved_text293.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text293.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier
            .padding(top = 10.dp)
            .padding(start = 20.dp)
    )
}

@Composable
private fun Section2(
    data: RadioIconsTestData,
    viewModel: RadioIconsTestViewModel
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(top = 10.dp)
            .padding(start = 20.dp)
    ) {
        RadioButton(
            selected = data.selectedDefaultgroup == "option1",
            onClick = { viewModel.updateData(mapOf("selectedDefaultgroup" to "option1")) }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Option 1", color = Color.Black)
    }
}

@Composable
private fun Section3(
    data: RadioIconsTestData,
    viewModel: RadioIconsTestViewModel
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(top = 10.dp)
            .padding(start = 20.dp)
    ) {
        RadioButton(
            selected = data.selectedDefaultgroup == "option2",
            onClick = { viewModel.updateData(mapOf("selectedDefaultgroup" to "option2")) }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Option 2", color = Color.Black)
    }
}

@Composable
private fun Section4(
    data: RadioIconsTestData,
    viewModel: RadioIconsTestViewModel
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(top = 10.dp)
            .padding(start = 20.dp)
    ) {
        RadioButton(
            selected = data.selectedDefaultgroup == "option3",
            onClick = { viewModel.updateData(mapOf("selectedDefaultgroup" to "option3")) }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Option 3", color = Color.Black)
    }
}

@Composable
private fun Section5(
    data: RadioIconsTestData,
    viewModel: RadioIconsTestViewModel
) {
    val resolved_text294 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.SemiBold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.radio_icons_test_custom_icon_radio_group),
        fontFamily = resolved_text294.family,
        fontWeight = resolved_text294.weight,
        fontSize = resolved_text294.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text294.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier
            .padding(top = 30.dp)
            .padding(start = 20.dp)
    )
}

@Composable
private fun Section6(
    data: RadioIconsTestData,
    viewModel: RadioIconsTestViewModel
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(top = 10.dp)
            .padding(start = 20.dp)
    ) {
        val isSelected = data.selectedCustomgroup == "custom1"
        IconButton(
            onClick = { viewModel.updateData(mapOf("selectedCustomgroup" to "custom1")) }
        ) {
            Icon(
                imageVector = if (isSelected) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = "Star Option",
                tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text("Star Option", color = Color.Black)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(top = 10.dp)
            .padding(start = 20.dp)
    ) {
        val isSelected = data.selectedCustomgroup == "custom2"
        IconButton(
            onClick = { viewModel.updateData(mapOf("selectedCustomgroup" to "custom2")) }
        ) {
            Icon(
                imageVector = if (isSelected) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = "Heart Option",
                tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text("Heart Option", color = Color.Black)
    }
}

@Composable
private fun Section7(
    data: RadioIconsTestData,
    viewModel: RadioIconsTestViewModel
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(top = 10.dp)
            .padding(start = 20.dp)
    ) {
        Checkbox(
            checked = data.selectedCustomgroup == "custom3",
            onCheckedChange = { viewModel.updateData(mapOf("selectedCustomgroup" to "custom3")) }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Square Option", color = Color.Black)
    }
}

@Composable
private fun Section8(
    data: RadioIconsTestData,
    viewModel: RadioIconsTestViewModel
) {
    val resolved_text295 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = FontWeight.SemiBold,
        size = 18.sp,
        italic = false
    ))
    Text(
        text = stringResource(R.string.radio_icons_test_radio_with_items),
        fontFamily = resolved_text295.family,
        fontWeight = resolved_text295.weight,
        fontSize = resolved_text295.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text295.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 23.4.sp),
        modifier = Modifier
            .padding(top = 30.dp)
            .padding(start = 20.dp)
    )
}

@Composable
private fun Section9(
    data: RadioIconsTestData,
    viewModel: RadioIconsTestViewModel
) {
    Column(
        modifier = Modifier
            .padding(top = 10.dp)
            .padding(start = 20.dp)
            .padding(end = 20.dp)
    ) {
        Text("Select Color:", color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    viewModel.updateData(mapOf("selectedColor" to "Red"))
                }
        ) {
            RadioButton(
                selected = data.selectedColor == "Red",
                onClick = {
                    viewModel.updateData(mapOf("selectedColor" to "Red"))
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Red", color = Color.Black)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    viewModel.updateData(mapOf("selectedColor" to "Green"))
                }
        ) {
            RadioButton(
                selected = data.selectedColor == "Green",
                onClick = {
                    viewModel.updateData(mapOf("selectedColor" to "Green"))
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Green", color = Color.Black)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    viewModel.updateData(mapOf("selectedColor" to "Blue"))
                }
        ) {
            RadioButton(
                selected = data.selectedColor == "Blue",
                onClick = {
                    viewModel.updateData(mapOf("selectedColor" to "Blue"))
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Blue", color = Color.Black)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    viewModel.updateData(mapOf("selectedColor" to "Yellow"))
                }
        ) {
            RadioButton(
                selected = data.selectedColor == "Yellow",
                onClick = {
                    viewModel.updateData(mapOf("selectedColor" to "Yellow"))
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Yellow", color = Color.Black)
        }
    }
}

@Composable
private fun Section10(
    data: RadioIconsTestData,
    viewModel: RadioIconsTestViewModel
) {
    val resolved_text296 = Configuration.Font.resolve(FontSpec(
        family = null,
        weight = null,
        size = 14.sp,
        italic = false
    ))
    Text(
        text = "${data.selectedColor}",
        color = colorResource(R.color.medium_gray_4),
        fontFamily = resolved_text296.family,
        fontWeight = resolved_text296.weight,
        fontSize = resolved_text296.size ?: TextUnit.Unspecified,
        fontStyle = resolved_text296.style ?: FontStyle.Normal,
        style = TextStyle(lineHeight = 18.2.sp),
        modifier = Modifier
            .padding(top = 10.dp)
            .padding(start = 20.dp)
    )
}
// >>> RESPONSIVE_HELPERS_END