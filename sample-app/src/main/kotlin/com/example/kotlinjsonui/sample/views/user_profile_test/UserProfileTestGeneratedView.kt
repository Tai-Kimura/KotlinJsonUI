package com.example.kotlinjsonui.sample.views.user_profile_test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinjsonui.sample.R
import com.example.kotlinjsonui.sample.data.UserProfileTestData
import com.example.kotlinjsonui.sample.viewmodels.UserProfileTestViewModel
import com.kotlinjsonui.components.SafeDynamicView
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont

@Composable
fun UserProfileTestGeneratedView(
    data: UserProfileTestData,
    viewModel: UserProfileTestViewModel,
    modifier: Modifier = Modifier
) {
    // Generated Compose code from user_profile_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "user_profile_test",
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
                android.util.Log.e("DynamicView", "Error loading user_profile_test: \$error")
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
        LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(colorResource(R.color.white_23))
            .imePadding()
    ) {
        item {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp)
        ) {
// TODO: Implement component type: SampleCard
            val resolved_text284 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = FontWeight.Bold,
                size = 18.sp,
                italic = false
            ))
            Text(
                text = stringResource(R.string.user_profile_test_team_members),
                color = colorResource(R.color.dark_gray),
                fontFamily = resolved_text284.family,
                fontWeight = resolved_text284.weight,
                fontSize = resolved_text284.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text284.style ?: FontStyle.Normal,
                style = TextStyle(lineHeight = 23.4.sp),
                modifier = Modifier
                    .padding(top = 24.dp)
                    .padding(bottom = 12.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
// TODO: Implement component type: UserAvatar
// TODO: Implement component type: UserAvatar
// TODO: Implement component type: UserAvatar
// TODO: Implement component type: UserAvatar
            }
            val resolved_text285 = Configuration.Font.resolve(FontSpec(
                family = null,
                weight = FontWeight.Bold,
                size = 18.sp,
                italic = false
            ))
            Text(
                text = stringResource(R.string.user_profile_test_actions),
                color = colorResource(R.color.dark_gray),
                fontFamily = resolved_text285.family,
                fontWeight = resolved_text285.weight,
                fontSize = resolved_text285.size ?: TextUnit.Unspecified,
                fontStyle = resolved_text285.style ?: FontStyle.Normal,
                style = TextStyle(lineHeight = 23.4.sp),
                modifier = Modifier
                    .padding(top = 24.dp)
                    .padding(bottom = 12.dp)
            )
            Button(
                onClick = { data.toggleOnlineStatus?.invoke() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_blue),
                                    contentColor = colorResource(R.color.white)
                                )
            ) {
                Text(stringResource(R.string.user_profile_test_toggle_online_status))
            }
            Button(
                onClick = { data.toggleDynamicMode?.invoke() },
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_green),
                                    contentColor = colorResource(R.color.white)
                                )
            ) {
                Text(stringResource(R.string.custom_component_test_toggle_dynamic_mode))
            }
        }
        }
    }    }
    // >>> GENERATED_CODE_END
}