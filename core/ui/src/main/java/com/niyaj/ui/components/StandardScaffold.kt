package com.niyaj.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.common.utils.Constants
import com.niyaj.common.utils.Constants.DRAWER_ICON
import com.niyaj.common.utils.Constants.STANDARD_BACK_BUTTON
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.LightColor6
import com.niyaj.designsystem.theme.Parchment
import com.niyaj.designsystem.theme.RoyalPurple
import com.niyaj.designsystem.theme.SeaShell
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardScaffold(
    modifier: Modifier = Modifier,
    navController: NavController,
    title: String,
    floatingActionButton: @Composable () -> Unit,
    navActions: @Composable RowScope.() -> Unit,
    bottomBar: @Composable () -> Unit = {},
    fabPosition: FabPosition = FabPosition.Center,
    selectionCount: Int,
    showBottomBar: Boolean = false,
    showBackButton: Boolean = false,
    onDeselect: () -> Unit = {},
    onBackClick: () -> Unit = { navController.navigateUp() },
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    content: @Composable (PaddingValues) -> Unit,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    // Remember a SystemUiController
    val systemUiController = rememberSystemUiController()

    val colorTransitionFraction = scrollBehavior.state.collapsedFraction

    val color = rememberUpdatedState(newValue = containerColorForPrimary(colorTransitionFraction))
    val navColor = MaterialTheme.colorScheme.surface
    val shape = rememberUpdatedState(newValue = containerShape(colorTransitionFraction))

    val selectedState = updateTransition(targetState = selectionCount, label = "selection count")

    SideEffect {
        systemUiController.setStatusBarColor(
            color = color.value,
            darkIcons = false,
        )

        systemUiController.setNavigationBarColor(
            color = navColor,
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            StandardDrawer(
                navController = navController,
            )
        },
        gesturesEnabled = true,
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
                                modifier = Modifier.testTag(STANDARD_BACK_BUTTON),
                            ) {
                                Icon(
                                    imageVector = PoposIcons.Back,
                                    contentDescription = null,
                                )
                            }
                        } else {
                            AnimatedContent(
                                targetState = selectedState,
                                transitionSpec = {
                                    (fadeIn()).togetherWith(
                                        fadeOut(animationSpec = tween(200)),
                                    )
                                },
                                label = "navigationIcon",
                                contentKey = {
                                    it
                                },
                            ) { state ->
                                if (state.currentState != 0) {
                                    IconButton(
                                        onClick = onDeselect,
                                    ) {
                                        Icon(
                                            imageVector = PoposIcons.Close,
                                            contentDescription = Constants.CLEAR_ICON,
                                        )
                                    }
                                } else {
                                    IconButton(
                                        onClick = {
                                            scope.launch {
                                                drawerState.open()
                                            }
                                        },
                                    ) {
                                        Icon(
                                            imageVector = PoposIcons.App,
                                            contentDescription = DRAWER_ICON,
                                        )
                                    }
                                }
                            }
                        }
                    },
                    actions = navActions,
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        scrolledContainerColor = RoyalPurple,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                )
            },
            bottomBar = {
                AnimatedVisibility(
                    visible = showBottomBar,
                    label = "BottomBar",
                    enter = fadeIn() + slideInVertically(
                        initialOffsetY = { fullHeight ->
                            fullHeight / 4
                        },
                    ),
                    exit = fadeOut() + slideOutVertically(
                        targetOffsetY = { fullHeight ->
                            fullHeight / 4
                        },
                    ),
                ) {
                    bottomBar()
                }
            },
            containerColor = MaterialTheme.colorScheme.primary,
            floatingActionButton = floatingActionButton,
            floatingActionButtonPosition = fabPosition,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            modifier = modifier
                .testTag(title)
                .fillMaxSize()
                .navigationBarsPadding()
                .imePadding(),
        ) { padding ->
            ElevatedCard(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .consumeWindowInsets(padding)
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal),
                    )
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                shape = shape.value,
                elevation = CardDefaults.cardElevation(),
            ) {
                content(padding)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardScaffoldRoute(
    modifier: Modifier = Modifier,
    currentRoute: String,
    title: String,
    floatingActionButton: @Composable () -> Unit,
    navActions: @Composable RowScope.() -> Unit,
    bottomBar: @Composable () -> Unit = {},
    fabPosition: FabPosition = FabPosition.Center,
    selectionCount: Int,
    showBottomBar: Boolean = false,
    showBackButton: Boolean = false,
    gesturesEnabled: Boolean = true,
    onDeselect: () -> Unit = {},
    onBackClick: () -> Unit,
    onNavigateToScreen: (String) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    content: @Composable (Shape) -> Unit,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    // Remember a SystemUiController
    val systemUiController = rememberSystemUiController()

    val colorTransitionFraction = scrollBehavior.state.collapsedFraction

    val color = rememberUpdatedState(newValue = containerColorForPrimary(colorTransitionFraction))
    val navColor = MaterialTheme.colorScheme.surface
    val shape = rememberUpdatedState(newValue = containerShape(colorTransitionFraction))

    val selectedState = updateTransition(targetState = selectionCount, label = "selection count")

    SideEffect {
        systemUiController.setStatusBarColor(
            color = color.value,
            darkIcons = false,
        )

        systemUiController.setNavigationBarColor(
            color = navColor,
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            StandardDrawer(
                currentRoute = currentRoute,
                onNavigateToScreen = onNavigateToScreen,
            )
        },
        gesturesEnabled = gesturesEnabled,
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
                                modifier = Modifier.testTag(STANDARD_BACK_BUTTON),
                            ) {
                                Icon(
                                    imageVector = PoposIcons.Back,
                                    contentDescription = null,
                                )
                            }
                        } else {
                            AnimatedContent(
                                targetState = selectedState,
                                transitionSpec = {
                                    (fadeIn()).togetherWith(
                                        fadeOut(animationSpec = tween(200)),
                                    )
                                },
                                label = "navigationIcon",
                                contentKey = {
                                    it
                                },
                            ) { state ->
                                if (state.currentState != 0) {
                                    IconButton(
                                        onClick = onDeselect,
                                    ) {
                                        Icon(
                                            imageVector = PoposIcons.Close,
                                            contentDescription = Constants.CLEAR_ICON,
                                        )
                                    }
                                } else {
                                    IconButton(
                                        onClick = {
                                            scope.launch {
                                                drawerState.open()
                                            }
                                        },
                                    ) {
                                        Icon(
                                            imageVector = PoposIcons.App,
                                            contentDescription = DRAWER_ICON,
                                        )
                                    }
                                }
                            }
                        }
                    },
                    actions = navActions,
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        scrolledContainerColor = RoyalPurple,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                )
            },
            bottomBar = {
                AnimatedVisibility(
                    visible = showBottomBar,
                    label = "BottomBar",
                    enter = fadeIn() + slideInVertically(
                        initialOffsetY = { fullHeight ->
                            fullHeight / 4
                        },
                    ),
                    exit = fadeOut() + slideOutVertically(
                        targetOffsetY = { fullHeight ->
                            fullHeight / 4
                        },
                    ),
                ) {
                    bottomBar()
                }
            },
            containerColor = MaterialTheme.colorScheme.primary,
            floatingActionButton = floatingActionButton,
            floatingActionButtonPosition = fabPosition,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            modifier = modifier
                .testTag(title)
                .fillMaxSize()
                .navigationBarsPadding()
                .imePadding(),
        ) { padding ->
            ElevatedCard(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .consumeWindowInsets(padding)
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal),
                    )
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                shape = shape.value,
                elevation = CardDefaults.cardElevation(),
            ) {
                content(shape.value)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardScaffoldNew(
    navController: NavController,
    modifier: Modifier = Modifier,
    title: String,
    showDrawer: Boolean = true,
    showBackButton: Boolean = false,
    showBottomBar: Boolean = false,
    showFab: Boolean = false,
    fabPosition: FabPosition = FabPosition.Center,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onBackClick: () -> Unit = { navController.navigateUp() },
    navigationIcon: @Composable () -> Unit = {},
    navActions: @Composable RowScope.() -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = { AnimatedBottomNavigationBar(navController) },
    content: @Composable (PaddingValues) -> Unit = {},
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    // Remember a SystemUiController
    val systemUiController = rememberSystemUiController()

    val colorTransitionFraction = scrollBehavior.state.collapsedFraction

    val color = rememberUpdatedState(newValue = containerColorForSecondary(colorTransitionFraction))
    val boxColor = rememberUpdatedState(newValue = containerColor(colorTransitionFraction))
    val shape = rememberUpdatedState(newValue = containerShape(colorTransitionFraction))
    val navColor = MaterialTheme.colorScheme.surface

    SideEffect {
        systemUiController.setStatusBarColor(
            color = color.value,
            darkIcons = true,
        )

        systemUiController.setNavigationBarColor(color = navColor)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            StandardDrawer(
                navController = navController,
            )
        },
        gesturesEnabled = true,
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
                                modifier = Modifier.testTag(STANDARD_BACK_BUTTON),
                            ) {
                                Icon(
                                    imageVector = PoposIcons.Back,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.scrim,
                                )
                            }
                        } else if (showDrawer) {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        drawerState.open()
                                    }
                                },
                            ) {
                                Icon(
                                    imageVector = PoposIcons.App,
                                    contentDescription = null,
                                )
                            }
                        } else navigationIcon()
                    },
                    actions = {
                        navActions()
                    },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = color.value,
                        scrolledContainerColor = color.value,
                    ),
                )
            },
            bottomBar = {
                AnimatedVisibility(
                    visible = showBottomBar,
                    label = "BottomBar",
                    enter = fadeIn() + slideInVertically(
                        initialOffsetY = { fullHeight ->
                            fullHeight / 4
                        },
                    ),
                    exit = fadeOut() + slideOutVertically(
                        targetOffsetY = { fullHeight ->
                            fullHeight / 4
                        },
                    ),
                ) {
                    bottomBar()
                }
            },
            floatingActionButton = {
                AnimatedVisibility(
                    visible = showFab,
                    label = "BottomBar",
                    enter = fadeIn() + slideInVertically(
                        initialOffsetY = { fullHeight ->
                            fullHeight / 4
                        },
                    ),
                    exit = fadeOut() + slideOutVertically(
                        targetOffsetY = { fullHeight ->
                            fullHeight / 4
                        },
                    ),
                ) {
                    floatingActionButton()
                }
            },
            floatingActionButtonPosition = fabPosition,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            modifier = modifier
                .testTag(title)
                .fillMaxSize()
                .navigationBarsPadding()
                .imePadding(),
            containerColor = MaterialTheme.colorScheme.background,
        ) { padding ->
            ElevatedCard(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .consumeWindowInsets(padding)
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal),
                    )
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                shape = shape.value,
                elevation = CardDefaults.cardElevation(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = boxColor.value,
                ),
            ) {
                content(padding)
            }
        }
    }
}


@SuppressLint("DesignSystem")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardScaffoldRouteNew(
    modifier: Modifier = Modifier,
    title: String,
    showBackButton: Boolean = true,
    showBottomBar: Boolean = false,
    showFab: Boolean = true,
    fabPosition: FabPosition = FabPosition.Center,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onBackClick: () -> Unit,
    navigationIcon: @Composable () -> Unit = {},
    navActions: @Composable RowScope.() -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit = {},
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    // Remember a SystemUiController
    val systemUiController = rememberSystemUiController()

    val colorTransitionFraction = scrollBehavior.state.collapsedFraction

    val color = rememberUpdatedState(newValue = containerColorForSecondary(colorTransitionFraction))
    val boxColor = rememberUpdatedState(newValue = containerColor(colorTransitionFraction))
    val shape = rememberUpdatedState(newValue = containerShape(colorTransitionFraction))
    val navColor = MaterialTheme.colorScheme.surface

    SideEffect {
        systemUiController.setStatusBarColor(color = color.value, darkIcons = true)

        systemUiController.setNavigationBarColor(color = navColor)
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(text = title)
                },
                navigationIcon = {
                    AnimatedContent(
                        targetState = showBackButton,
                        label = "Show Navigation Icon",
                        transitionSpec = {
                            (fadeIn()).togetherWith(
                                fadeOut(animationSpec = tween(200)),
                            )
                        },
                    ) {
                        if (it) {
                            IconButton(
                                onClick = onBackClick,
                                modifier = Modifier.testTag(STANDARD_BACK_BUTTON),
                            ) {
                                Icon(
                                    imageVector = PoposIcons.Back,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.scrim,
                                )
                            }
                        } else navigationIcon()
                    }
                },
                actions = navActions,
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = color.value,
                    scrolledContainerColor = color.value,
                ),
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                label = "BottomBar",
                enter = fadeIn() + slideInVertically(
                    initialOffsetY = { fullHeight ->
                        fullHeight / 4
                    },
                ),
                exit = fadeOut() + slideOutVertically(
                    targetOffsetY = { fullHeight ->
                        fullHeight / 4
                    },
                ),
            ) {
                bottomBar()
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = showFab,
                label = "BottomBar",
                enter = fadeIn() + slideInVertically(
                    initialOffsetY = { fullHeight ->
                        fullHeight / 4
                    },
                ),
                exit = fadeOut() + slideOutVertically(
                    targetOffsetY = { fullHeight ->
                        fullHeight / 4
                    },
                ),
            ) {
                floatingActionButton()
            }
        },
        floatingActionButtonPosition = fabPosition,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
            .testTag(title)
            .fillMaxSize()
            .imePadding(),
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Surface(
            color = boxColor.value,
            tonalElevation = 2.dp,
            shadowElevation = 2.dp,
            shape = shape.value,
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
        ) {
            content(padding)
        }
    }
}

@Composable
internal fun containerColorForPrimary(colorTransitionFraction: Float): Color {
    return lerp(
        MaterialTheme.colorScheme.primary,
        RoyalPurple,
        FastOutLinearInEasing.transform(colorTransitionFraction),
    )
}

@Composable
internal fun containerColorForSecondary(colorTransitionFraction: Float): Color {
    return lerp(
        SeaShell,
        Parchment,
        FastOutLinearInEasing.transform(colorTransitionFraction),
    )
}

@Composable
internal fun containerColor(colorTransitionFraction: Float): Color {
    return lerp(
        LightColor6,
        MaterialTheme.colorScheme.surfaceContainerLowest,
        FastOutLinearInEasing.transform(colorTransitionFraction),
    )
}


@Composable
internal fun containerShape(colorTransitionFraction: Float): Shape {
    val data = lerp(
        CornerRadius(48f, 48f),
        CornerRadius(0f, 0f),
        FastOutLinearInEasing.transform(colorTransitionFraction),
    )

    return RoundedCornerShape(data.x, data.y)
}

const val Duration = 500
const val DoubleDuration = 1000