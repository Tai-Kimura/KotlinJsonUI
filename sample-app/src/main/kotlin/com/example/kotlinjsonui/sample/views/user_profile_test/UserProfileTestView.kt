package com.example.kotlinjsonui.sample.views.user_profile_test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinjsonui.sample.viewmodels.UserProfileTestViewModel

@Composable
fun UserProfileTestView(
    viewModel: UserProfileTestViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    
    UserProfileTestGeneratedView(
        data = data,
        viewModel = viewModel
    )
}
