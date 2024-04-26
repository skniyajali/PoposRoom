package com.niyaj.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.common.utils.Constants
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.ui.utils.Screens
import kotlinx.coroutines.launch

@SuppressLint("DesignSystem")
@Stable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardScaffoldWithBottomNavigation(
    modifier: Modifier = Modifier,
    title: String = "",
    currentRoute: String,
    selectedId: String = "0",
    showFab: Boolean = false,
    showBottomBar: Boolean = false,
    showSearchBar: Boolean = false,
    showSearchIcon: Boolean = false,
    showBackButton: Boolean = false,
    searchText: String = "",
    searchPlaceholderText: String = "",
    openSearchBar: () -> Unit = {},
    closeSearchBar: () -> Unit = {},
    onSearchTextChanged: (String) -> Unit = {},
    onClearClick: () -> Unit = {},
    onBackClick: () -> Unit,
    onNavigateToScreen: (String) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    bottomBar: @Composable () -> Unit = {
        AnimatedBottomNavigationBar(
            currentRoute = currentRoute,
            onNavigateToDestination = onNavigateToScreen
        )
    },
    navActions: @Composable RowScope.() -> Unit = {},
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val layoutDirection = LocalLayoutDirection.current

    // Remember a SystemUiController
    val systemUiController = rememberSystemUiController()

    val colorTransitionFraction = remember { scrollBehavior.state.collapsedFraction }

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
                currentRoute = currentRoute,
                onNavigateToScreen = onNavigateToScreen
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
                                    onNavigateToScreen(Screens.SELECT_ORDER_SCREEN)
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
                                    imageVector = PoposIcons.Back,
                                    contentDescription = Constants.STANDARD_BACK_BUTTON
                                )
                            }
                        } else if (showBackButton) {
                            IconButton(
                                onClick = onBackClick
                            ) {
                                Icon(
                                    imageVector = PoposIcons.Back,
                                    contentDescription = Constants.STANDARD_BACK_BUTTON
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
                                    imageVector = PoposIcons.App,
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
                                    imageVector = PoposIcons.Search,
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
                            onNavigateToScreen(Screens.ADD_EDIT_CART_ORDER_SCREEN)
                        },
                        containerColor = MaterialTheme.colorScheme.secondary
                    ) {
                        Icon(
                            imageVector = PoposIcons.Add,
                            contentDescription = "Create new order"
                        )
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.End,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            modifier = modifier
                .testTag(title)
                .fillMaxSize(),
        ) { padding ->
            ElevatedCard(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = padding.calculateStartPadding(layoutDirection),
                        top = padding.calculateTopPadding(),
                        end = padding.calculateEndPadding(layoutDirection)
                    )
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal),
                    )
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                elevation = CardDefaults.cardElevation(),
                shape = shape.value,
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                content()
            }
        }
    }
}