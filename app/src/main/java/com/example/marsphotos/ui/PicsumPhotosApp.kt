@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.marsphotos.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel


import com.example.marsphotos.ui.screens.PicsumHomeScreen
import com.example.marsphotos.ui.screens.PicsumViewModel

@Composable
fun PicsumPhotosApp(picsumViewModel: PicsumViewModel) {
    Surface {
        PicsumHomeScreen(
            picsumUiState = picsumViewModel.picsumUiState,
        )
    }
}

