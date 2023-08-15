package com.niyaj.profile

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.niyaj.ui.components.StandardScaffoldNew
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.Screens
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch

@RootNavGraph(start = true)
@Destination(
    route = Screens.ProfileScreen
)
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

    StandardScaffoldNew(
        navController = navController,
        title = PROFILE_SCREEN,
        showBottomBar = false,
        showBackButton = true,
        snackbarHostState = snackbarState,
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
                            .fillMaxWidth()
                            .padding(SpaceSmall),
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