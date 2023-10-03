package com.niyaj.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.common.utils.Constants
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.ui.utils.Screens
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardScaffoldWithBottomNavigation(
    modifier: Modifier = Modifier,
    navController: NavController,
    title: String = "",
    selectedId: String = "0",
    showBottomBar: Boolean = false,
    showFab: Boolean = false,
    showSearchBar: Boolean = false,
    showSearchIcon: Boolean = false,
    searchText: String = "",
    searchPlaceholderText: String = "",
    openSearchBar: () -> Unit = {},
    closeSearchBar: () -> Unit = {},
    onSearchTextChanged: (String) -> Unit = {},
    onClearClick: () -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    bottomBar: @Composable () -> Unit = { AnimatedBottomNavigationBar(navController = navController) },
    navActions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    // Remember a SystemUiController
    val systemUiController = rememberSystemUiController()

    val colorTransitionFraction = scrollBehavior.state.collapsedFraction

    val shape = rememberUpdatedState(newValue = containerShape(colorTransitionFraction))
    val statusColor = MaterialTheme.colorScheme.surface

    SideEffect {
        systemUiController.setStatusBarColor(color = statusColor, darkIcons = true)

        systemUiController.setNavigationBarColor(color = statusColor)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            StandardDrawer(
                navController = navController
            )
        },
        gesturesEnabled = true
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        if (title.isNotEmpty()) {
                            Text(text = title)
                        } else if (selectedId != "0") {
                            SelectedOrderBox(
                                modifier = Modifier
                                    .padding(horizontal = SpaceMedium),
                                text = selectedId,
                                height = 40.dp,
                                onClick = {
                                    navController.navigate(Screens.SelectOrderScreen)
                                }
                            )
                        }
                    },
                    navigationIcon = {
                        if (showSearchBar) {
                            IconButton(
                                onClick = closeSearchBar,
                                modifier = Modifier.testTag(Constants.STANDARD_BACK_BUTTON)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.scrim
                                )
                            }
                        } else {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        drawerState.open()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Apps,
                                    contentDescription = null
                                )
                            }
                        }
                    },
                    actions = {
                        if (showSearchBar) {
                            StandardSearchBar(
                                searchText = searchText,
                                placeholderText = searchPlaceholderText,
                                onClearClick = onClearClick,
                                onSearchTextChanged = onSearchTextChanged
                            )
                        } else if (showSearchIcon) {
                            IconButton(
                                onClick = openSearchBar
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = Constants.SEARCH_ICON
                                )
                            }
                        } else {
                            navActions()
                        }
                    },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        scrolledContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            floatingActionButton = {
                AnimatedVisibility(
                    visible = showFab,
                    label = "FloatingActionButton",
                    enter = fadeIn() + slideInVertically(
                        initialOffsetY = { fullHeight ->
                            fullHeight / 4
                        }
                    ),
                    exit = fadeOut() + slideOutVertically(
                        targetOffsetY = { fullHeight ->
                            fullHeight / 4
                        }
                    )
                ) {
                    FloatingActionButton(
                        onClick = {
                            navController.navigate(Screens.AddEditCartOrderScreen)
                        },
                        containerColor = MaterialTheme.colorScheme.secondary
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Create new order"
                        )
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.End,
            bottomBar = {
                AnimatedVisibility(
                    visible = showBottomBar,
                    label = "BottomBar",
                    enter = fadeIn() + slideInVertically(
                        initialOffsetY = { fullHeight ->
                            fullHeight / 4
                        }
                    ),
                    exit = fadeOut() + slideOutVertically(
                        targetOffsetY = { fullHeight ->
                            fullHeight / 4
                        }
                    )
                ) {
                    bottomBar()
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            modifier = modifier
                .testTag(title)
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
        ) { padding ->
            ElevatedCard(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                shape = shape.value,
                elevation = CardDefaults.cardElevation(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                content(padding)
            }
        }
    }
}