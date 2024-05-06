/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.niyaj.profile

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.common.tags.ProfileTestTags.CREATE_NEW_PROFILE
import com.niyaj.common.tags.ProfileTestTags.PROFILE_NOT_AVAILABLE
import com.niyaj.common.tags.ProfileTestTags.PROFILE_SCREEN
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.profile.destinations.AddEditProfileScreenDestination
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.StandardScaffoldRouteNew
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.Screens
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch

@RootNavGraph(start = true)
@Destination(route = Screens.PROFILE_SCREEN)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditProfileScreenDestination, String>,
) {
    val snackbarState = remember { SnackbarHostState() }
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val uiState = viewModel.profile.collectAsStateWithLifecycle().value

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    snackbarState.showSnackbar(result.value)
                }
            }
        }
    }

    StandardScaffoldRouteNew(
        title = PROFILE_SCREEN,
        showBottomBar = false,
        showBackButton = true,
        snackbarHostState = snackbarState,
        onBackClick = navController::navigateUp
    ) {
        Crossfade(
            targetState = uiState,
            label = "ProfileState"
        ) { state ->
            when (state) {
                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = PROFILE_NOT_AVAILABLE,
                        buttonText = CREATE_NEW_PROFILE,
                        onClick = {
                            navController.navigate(AddEditProfileScreenDestination())
                        }
                    )
                }

                is UiState.Loading -> LoadingIndicator()

                is UiState.Success -> {
                    val profile = state.data

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it),
                        contentPadding = PaddingValues(SpaceSmall),
                        state = lazyListState,
                    ) {
                        item {
                            Text(text = profile.name)
                        }
                    }
                }
            }
        }
    }
}