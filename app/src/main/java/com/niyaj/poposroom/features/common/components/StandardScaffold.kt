package com.niyaj.poposroom.features.common.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.rounded.Assessment
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.poposroom.features.common.ui.theme.SpaceMedium
import com.niyaj.poposroom.features.common.ui.theme.SpaceSmall
import com.niyaj.poposroom.features.common.utils.Constants
import com.niyaj.poposroom.features.common.utils.Constants.DELETE_ICON
import com.niyaj.poposroom.features.common.utils.Constants.EDIT_ICON
import com.niyaj.poposroom.features.common.utils.Constants.FAB_TEXT
import com.niyaj.poposroom.features.common.utils.Constants.SEARCH_ICON
import com.niyaj.poposroom.features.common.utils.Constants.SELECTALL_ICON
import com.niyaj.poposroom.features.common.utils.Constants.SETTINGS_ICON
import com.niyaj.poposroom.features.common.utils.Constants.STANDARD_BACK_BUTTON
import com.niyaj.poposroom.features.destinations.AddEditCartOrderScreenDestination
import com.niyaj.poposroom.features.destinations.CartOrderScreenDestination
import com.niyaj.poposroom.features.destinations.CartScreenDestination
import com.niyaj.poposroom.features.destinations.MainFeedScreenDestination
import com.niyaj.poposroom.features.destinations.SelectOrderScreenDestination
import com.niyaj.poposroom.features.main_feed.domain.utils.MainFeedTestTags
import com.ramcosta.composedestinations.navigation.navigate
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardScaffold(
    modifier: Modifier = Modifier,
    navController: NavController,
    title: String,
    floatingActionButton: @Composable () -> Unit = {},
    fabPosition: FabPosition = FabPosition.Center,
    searchText: String = "",
    placeholderText: String = "Search for items",
    showSettings: Boolean = true,
    selectionCount: Int = 0,
    showBottomBarActions: Boolean = false,
    showSearchBar: Boolean = false,
    showBackButton: Boolean = false,
    onClearClick: () -> Unit = {},
    onDeselect: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onSelectAllClick: () -> Unit = {},
    onSearchTextChanged: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    content: @Composable (PaddingValues) -> Unit,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    // Remember a SystemUiController
    val systemUiController = rememberSystemUiController()

    val colorTransitionFraction = scrollBehavior.state.collapsedFraction

    val color = rememberUpdatedState(newValue = containerColor(colorTransitionFraction))
    val shape = rememberUpdatedState(newValue = containerShape(colorTransitionFraction))

    val selectedState = MutableTransitionState(selectionCount)

    SideEffect {
        systemUiController.setStatusBarColor(
            color = color.value,
            darkIcons = true,
        )

        systemUiController.setNavigationBarColor(
            color = color.value
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                navController = navController,
                onCloseClick = {
                    scope.launch {
                        drawerState.close()
                    }
                }
            )
        },
        gesturesEnabled = true
    ) {
        Scaffold(
            topBar = {
                LargeTopAppBar(
                    title = {
                        Text(text = title)
                    },
                    navigationIcon = {
                        if (showBackButton) {
                            IconButton(
                                onClick = onBackClick,
                                modifier = Modifier.testTag(STANDARD_BACK_BUTTON)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.scrim
                                )
                            }
                        } else {
                            AnimatedContent(
                                targetState = selectedState,
                                transitionSpec = {
                                    (fadeIn()).togetherWith(
                                        fadeOut(animationSpec = tween(200))
                                    )
                                },
                                label = "navigationIcon",
                                contentKey = {
                                    it
                                }
                            ) { state ->
                                if (state.currentState != 0) {
                                    IconButton(
                                        onClick = onDeselect
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = Constants.CLEAR_ICON
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
                            }
                        }
                    },
                    actions = {
                        if (showSearchBar) {
                            StandardSearchBar(
                                searchText = searchText,
                                placeholderText = placeholderText,
                                onClearClick = onClearClick,
                                onSearchTextChanged = onSearchTextChanged
                            )
                        } else {
                            AnimatedContent(
                                targetState = selectedState,
                                transitionSpec = {
                                    (fadeIn()).togetherWith(
                                        fadeOut(animationSpec = tween(200))
                                    )
                                },
                                label = "navActions",
                            ) { state ->
                                Row {
                                    if (state.currentState != 0) {
                                        if (!showBottomBarActions) {
                                            if (state.currentState == 1) {
                                                IconButton(onClick = onEditClick) {
                                                    Icon(
                                                        imageVector = Icons.Default.Edit,
                                                        contentDescription = EDIT_ICON
                                                    )
                                                }
                                            }

                                            IconButton(onClick = onDeleteClick) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = DELETE_ICON
                                                )
                                            }

                                            IconButton(onClick = onSelectAllClick) {
                                                Icon(
                                                    imageVector = Icons.Default.Checklist,
                                                    contentDescription = SELECTALL_ICON
                                                )
                                            }
                                        }
                                    } else {
                                        IconButton(onClick = onSearchClick) {
                                            Icon(
                                                imageVector = Icons.Default.Search,
                                                contentDescription = SEARCH_ICON
                                            )
                                        }

                                        if (showSettings) {
                                            IconButton(onClick = onSettingsClick) {
                                                Icon(
                                                    imageVector = Icons.Default.Settings,
                                                    contentDescription = SETTINGS_ICON
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            },
            bottomBar = {
                if (showBottomBarActions) {
                    AnimatedVisibility(
                        visible = selectionCount != 0,
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
                        BottomAppBar(
                            actions = {
                                AnimatedContent(
                                    targetState = selectedState,
                                    transitionSpec = {
                                        fadeIn().togetherWith(fadeOut(tween(200)))
                                    },
                                    label = "bottomActions",
                                    contentKey = {
                                        it.currentState
                                    }
                                ) { state ->
                                    Row {
                                        IconButton(onClick = onSelectAllClick) {
                                            Icon(
                                                imageVector = Icons.Default.Checklist,
                                                contentDescription = SELECTALL_ICON
                                            )
                                        }

                                        if (state.currentState == 1) {
                                            IconButton(onClick = onEditClick) {
                                                Icon(
                                                    imageVector = Icons.Default.Edit,
                                                    contentDescription = EDIT_ICON
                                                )
                                            }
                                        }

                                        IconButton(onClick = onDeleteClick) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = DELETE_ICON
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            },
            floatingActionButton = floatingActionButton,
            floatingActionButtonPosition = fabPosition,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            modifier = modifier
                .testTag(title)
                .fillMaxSize(),
        ) { padding ->
            ElevatedCard(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                shape = shape.value,
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                content(padding)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardScaffold(
    modifier: Modifier = Modifier,
    navController: NavController,
    title: String,
    floatingActionButton: @Composable () -> Unit,
    navActions: @Composable () -> Unit,
    fabPosition: FabPosition = FabPosition.Center,
    selectionCount: Int,
    showBottomBarActions: Boolean = false,
    showBackButton: Boolean = false,
    onDeselect: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onSelectAllClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    content: @Composable (PaddingValues) -> Unit,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    // Remember a SystemUiController
    val systemUiController = rememberSystemUiController()

    val colorTransitionFraction = scrollBehavior.state.collapsedFraction

    val color = rememberUpdatedState(newValue = containerColor(colorTransitionFraction))
    val shape = rememberUpdatedState(newValue = containerShape(colorTransitionFraction))

    val selectedState = MutableTransitionState(selectionCount)

    SideEffect {
        systemUiController.setStatusBarColor(
            color = color.value,
            darkIcons = true,
        )

        systemUiController.setNavigationBarColor(
            color = color.value
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                navController = navController,
                onCloseClick = {
                    scope.launch {
                        drawerState.close()
                    }
                }
            )
        },
        gesturesEnabled = true
    ) {
        Scaffold(
            topBar = {
                LargeTopAppBar(
                    title = {
                        Text(text = title)
                    },
                    navigationIcon = {
                        if (showBackButton) {
                            IconButton(
                                onClick = onBackClick,
                                modifier = Modifier.testTag(STANDARD_BACK_BUTTON)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.scrim
                                )
                            }
                        } else {
                            AnimatedContent(
                                targetState = selectedState,
                                transitionSpec = {
                                    (fadeIn()).togetherWith(
                                        fadeOut(animationSpec = tween(200))
                                    )
                                },
                                label = "navigationIcon",
                                contentKey = {
                                    it
                                }
                            ) { state ->
                                if (state.currentState != 0) {
                                    IconButton(
                                        onClick = onDeselect
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = Constants.CLEAR_ICON
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
                            }
                        }
                    },
                    actions = {
                        navActions()
                    },
                    scrollBehavior = scrollBehavior,
                )
            },
            bottomBar = {
                if (showBottomBarActions) {
                    AnimatedVisibility(
                        visible = selectionCount != 0,
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
                        BottomAppBar(
                            actions = {
                                AnimatedContent(
                                    targetState = selectedState,
                                    transitionSpec = {
                                        fadeIn().togetherWith(fadeOut(tween(200)))
                                    },
                                    label = "bottomActions",
                                    contentKey = {
                                        it.currentState
                                    }
                                ) { state ->
                                    Row {
                                        IconButton(onClick = onSelectAllClick) {
                                            Icon(
                                                imageVector = Icons.Default.Checklist,
                                                contentDescription = SELECTALL_ICON
                                            )
                                        }

                                        if (state.currentState == 1) {
                                            IconButton(onClick = onEditClick) {
                                                Icon(
                                                    imageVector = Icons.Default.Edit,
                                                    contentDescription = EDIT_ICON
                                                )
                                            }
                                        }

                                        IconButton(onClick = onDeleteClick) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = DELETE_ICON
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            },
            floatingActionButton = floatingActionButton,
            floatingActionButtonPosition = fabPosition,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            modifier = modifier
                .testTag(title)
                .fillMaxSize(),
        ) { padding ->
            ElevatedCard(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                shape = shape.value,
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                content(padding)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardScaffoldWithBottomNavigation(
    modifier: Modifier = Modifier,
    navController: NavController,
    title: String = "",
    selectedId: String = "0",
    isScrolling: Boolean = false,
    showFab: Boolean = false,
    showSearchBar: Boolean = false,
    showSearchIcon: Boolean = false,
    searchText: String = "",
    openSearchBar: () -> Unit = {},
    closeSearchBar: () -> Unit = {},
    onSearchTextChanged: (String) -> Unit = {},
    onClearClick: () -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    bottomBar: @Composable () -> Unit = { BottomNavigationBar(navController = navController) },
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
    val navColor = MaterialTheme.colorScheme.surfaceVariant
    val statusColor = MaterialTheme.colorScheme.surface

    SideEffect {
        systemUiController.setStatusBarColor(color = statusColor, darkIcons = true)

        systemUiController.setNavigationBarColor(color = navColor)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                navController = navController,
                onCloseClick = {
                    scope.launch {
                        drawerState.close()
                    }
                }
            )
        },
        gesturesEnabled = true
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        if (title.isNotEmpty()){
                            Text(text = title)
                        } else if (selectedId != "0"){
                            SelectedOrderBox(
                                modifier = Modifier
                                    .padding(horizontal = SpaceMedium),
                                text = selectedId,
                                height = 40.dp,
                                onClick = {
                                    navController.navigate(SelectOrderScreenDestination())
                                }
                            )
                        }
                    },
                    navigationIcon = {
                        if (showSearchBar) {
                            IconButton(
                                onClick = closeSearchBar,
                                modifier = Modifier.testTag(STANDARD_BACK_BUTTON)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
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
                                placeholderText = MainFeedTestTags.MAIN_SEARCH_PLACEHOLDER,
                                onClearClick = onClearClick,
                                onSearchTextChanged = onSearchTextChanged
                            )
                        } else if (showSearchIcon) {
                            IconButton(
                                onClick = openSearchBar
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = SEARCH_ICON
                                )
                            }
                        } else {
                            navActions()
                        }
                    },
                    scrollBehavior = scrollBehavior,
                    colors =TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        scrolledContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            floatingActionButton = {
                AnimatedVisibility(
                    visible = showFab
                ) {
                    FloatingActionButton(
                        onClick = {
                            navController.navigate(AddEditCartOrderScreenDestination())
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
                    visible = !isScrolling,
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
                    .padding(
                        top = padding.calculateTopPadding(),
                        bottom = 0.dp,
                    ),
                shape = shape.value,
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                content(padding)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardScaffoldWithOutDrawer(
    title: String,
    onBackClick: () -> Unit,
    showBottomBar: Boolean = false,
    bottomBar: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    // Remember a SystemUiController
    val systemUiController = rememberSystemUiController()

    val colorTransitionFraction = scrollBehavior.state.collapsedFraction

    val color = rememberUpdatedState(newValue = containerColor(colorTransitionFraction))
    val shape = rememberUpdatedState(newValue = containerShape(colorTransitionFraction))

    SideEffect {
        systemUiController.setStatusBarColor(
            color = color.value,
            darkIcons = true,
        )

        systemUiController.setNavigationBarColor(
            color = color.value
        )
    }

    Scaffold(
        modifier = Modifier
            .testTag(title)
            .fillMaxWidth()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag(STANDARD_BACK_BUTTON)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.scrim
                        )
                    }
                },
                title = {
                    Text(text = title)
                },
                scrollBehavior = scrollBehavior,
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
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
                BottomAppBar {
                    bottomBar()
                }
            }
        }
    ) { padding ->
        ElevatedCard(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            shape = shape.value,
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = colorTransitionFraction.dp
            )
        ) {
            content()
        }
    }
}


@Composable
fun StandardFAB(
    fabVisible: Boolean,
    showScrollToTop: Boolean = false,
    fabText: String = FAB_TEXT,
    fabIcon: ImageVector = Icons.Filled.Add,
    containerColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    onFabClick: () -> Unit,
    onClickScroll: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AnimatedVisibility(
            visible = showScrollToTop,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            ScrollToTop(onClick = onClickScroll, containerColor = containerColor)
        }

        Spacer(modifier = Modifier.height(SpaceSmall))

        AnimatedVisibility(
            visible = fabVisible,
            enter = fadeIn() + slideInVertically(
                initialOffsetY = { fullHeight ->
                    fullHeight / 4
                }
            ),
            exit = fadeOut() + slideOutVertically(
                targetOffsetY = { fullHeight ->
                    fullHeight / 4
                }
            ),
            label = "FloatingActionButton"
        ) {
            ExtendedFloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = onFabClick,
                expanded = !showScrollToTop,
                icon = { Icon(fabIcon, fabText) },
                text = { Text(text = fabText.uppercase()) },
            )
        }
    }
}


@Composable
fun BottomNavigationBar(
    navController: NavController,
) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == MainFeedScreenDestination.route,
            label = {
                val fontWeight = if (currentRoute == MainFeedScreenDestination.route)
                    FontWeight.SemiBold else FontWeight.Normal

                Text(
                    text = "Home",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = fontWeight,
                )
            },
            onClick = {
                navController.navigate(MainFeedScreenDestination())
            },
            icon = {
                val icon = if (currentRoute == MainFeedScreenDestination.route) {
                    Icons.Rounded.Home
                }else Icons.Outlined.Home

                Icon(imageVector = icon, contentDescription = "Home")
            }
        )
        NavigationBarItem(
            selected = currentRoute == CartScreenDestination.route,
            label = {
                val fontWeight = if (currentRoute == CartScreenDestination.route)
                    FontWeight.SemiBold else FontWeight.Normal

                Text(
                    text = "Cart",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = fontWeight,
                )
            },
            onClick = {
                navController.navigate(CartScreenDestination())
            },
            icon = {
                val icon = if (currentRoute == CartScreenDestination.route) {
                    Icons.Rounded.ShoppingCart
                }else Icons.Outlined.ShoppingCart

                Icon(imageVector = icon, contentDescription = "Cart")
            }
        )
        NavigationBarItem(
            selected = currentRoute == CartOrderScreenDestination.route,
            label = {
                val fontWeight = if (currentRoute == CartOrderScreenDestination.route)
                    FontWeight.SemiBold else FontWeight.Normal

                Text(
                    text = "Orders",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = fontWeight,
                )
            },
            onClick = {
                navController.navigate(CartOrderScreenDestination())
            },
            icon = {
                val icon = if (currentRoute == CartOrderScreenDestination.route) {
                    Icons.Rounded.Inventory2
                }else Icons.Outlined.Inventory2

                Icon(imageVector = icon, contentDescription = "Orders")
            }
        )
        NavigationBarItem(
            selected = currentRoute == CartOrderScreenDestination.route,
            label = {
                val fontWeight = if (currentRoute == CartOrderScreenDestination.route)
                    FontWeight.SemiBold else FontWeight.Normal

                Text(
                    text = "Reports",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = fontWeight,
                )
            },
            onClick = {
                navController.navigate(CartOrderScreenDestination())
            },
            icon = {
                val icon = if (currentRoute == CartOrderScreenDestination.route) {
                    Icons.Rounded.Assessment
                }else Icons.Outlined.Assessment

                Icon(imageVector = icon, contentDescription = "Reports")
            }
        )
    }
}

@Composable
internal fun containerColor(colorTransitionFraction: Float): Color {
    return lerp(
        MaterialTheme.colorScheme.background,
        MaterialTheme.colorScheme.surfaceVariant,
        FastOutLinearInEasing.transform(colorTransitionFraction)
    )
}

@Composable
internal fun containerShape(colorTransitionFraction: Float): Shape {
    val data = lerp(
        CornerRadius(48f, 48f),
        CornerRadius(0f, 0f),
        FastOutLinearInEasing.transform(colorTransitionFraction)
    )

    return RoundedCornerShape(data.x, data.y)
}