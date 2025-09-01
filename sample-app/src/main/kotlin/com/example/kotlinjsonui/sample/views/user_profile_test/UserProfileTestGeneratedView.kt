package com.example.kotlinjsonui.sample.views.user_profile_test

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
import com.example.kotlinjsonui.sample.data.UserProfileTestData
import com.example.kotlinjsonui.sample.viewmodels.UserProfileTestViewModel
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.Box
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.components.SafeDynamicView
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ButtonDefaults
import com.example.kotlinjsonui.sample.extensions.SampleCard
import com.example.kotlinjsonui.sample.extensions.UserAvatar
import com.example.kotlinjsonui.sample.extensions.StatusBadge
import androidx.compose.ui.res.stringResource
import com.example.kotlinjsonui.sample.R
import androidx.compose.ui.res.colorResource

@Composable
fun UserProfileTestGeneratedView(
    data: UserProfileTestData,
    viewModel: UserProfileTestViewModel
) {
    // Generated Compose code from user_profile_test.json
    // This will be updated when you run 'kjui build'
    // >>> GENERATED_CODE_START
    // Check if Dynamic Mode is active
    if (DynamicModeManager.isActive()) {
        // Dynamic Mode - use SafeDynamicView for real-time updates
        SafeDynamicView(
            layoutName = "user_profile_test",
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
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(colorResource(R.color.white_23))
    ) {
        item {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp)
        ) {
            SampleCard(
                title = "User Profile",
                subtitle = "Manage your account",
                count = 3
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        UserAvatar(
                            name = data.userName,
                            avatarUrl = data.userAvatar,
                            size = 64,
                            isOnline = data.isOnline
                        )
                        Column(
                            modifier = Modifier
                                .wrapContentWidth()
                                .wrapContentHeight()
                                .padding(start = 16.dp)
                        ) {
                            Text(
                                text = "${data.userName}",
                                fontSize = 20.sp,
                                color = colorResource(R.color.dark_gray),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                            )
                            Text(
                                text = "${data.userEmail}",
                                fontSize = 14.sp,
                                color = colorResource(R.color.medium_gray_4),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .padding(top = 12.dp)
                            .padding(bottom = 12.dp)
                            .background(colorResource(R.color.pale_gray))
                    ) {
                    }
                    StatusBadge(
                        title = "Status",
                        status = data.userStatus,
                        color = colorResource(R.color.medium_green_2),
                        count = data.notificationCount
                    )
                }
            }
            Text(
                text = stringResource(R.string.user_profile_test_team_members),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 24.dp)
                    .padding(bottom = 12.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                UserAvatar(
                    name = "Alice Johnson",
                    size = 48,
                    isOnline = true,
                    modifier = Modifier.padding(end = 12.dp)
                )
                UserAvatar(
                    name = "Bob Smith",
                    avatarUrl = "https://i.pravatar.cc/150?img=3",
                    size = 48,
                    modifier = Modifier.padding(end = 12.dp)
                )
                UserAvatar(
                    name = "Carol Williams",
                    avatarUrl = "https://i.pravatar.cc/150?img=5",
                    size = 48,
                    isOnline = true,
                    modifier = Modifier.padding(end = 12.dp)
                )
                UserAvatar(
                    name = "David Brown",
                    size = 48
                )
            }
            Text(
                text = stringResource(R.string.user_profile_test_actions),
                fontSize = 18.sp,
                color = colorResource(R.color.dark_gray),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 24.dp)
                    .padding(bottom = 12.dp)
            )
            Button(
                onClick = { viewModel.toggleOnlineStatus() },
                modifier = Modifier.padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_blue)
                                )
            ) {
                Text(
                    text = stringResource(R.string.user_profile_test_toggle_online_status),
                    color = colorResource(R.color.white),
                )
            }
            Button(
                onClick = { viewModel.toggleDynamicMode() },
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.medium_green)
                                )
            ) {
                Text(
                    text = stringResource(R.string.custom_component_test_toggle_dynamic_mode),
                    color = colorResource(R.color.white),
                )
            }
        }
        }
    }    }
    // >>> GENERATED_CODE_END
}