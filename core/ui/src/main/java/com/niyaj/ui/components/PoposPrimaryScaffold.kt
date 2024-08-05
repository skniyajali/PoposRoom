/*
 * Copyright 2024 Sk Niyaj Ali
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

@file:Suppress("DEPRECATION")

package com.niyaj.ui.components

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
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.common.utils.Constants.CLEAR_ICON
import com.niyaj.common.utils.Constants.DRAWER_ICON
import com.niyaj.common.utils.Constants.STANDARD_BACK_BUTTON
import com.niyaj.designsystem.components.PoposLargeTopAppBar
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.BoneWhite
import com.niyaj.designsystem.theme.LightColor13
import com.niyaj.designsystem.theme.LightColor14
import com.niyaj.designsystem.theme.Pewter
import com.niyaj.designsystem.theme.RoyalPurple
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.ui.utils.DevicePreviews
import kotlinx.coroutines.launch

const val PRIMARY_APP_DRAWER = "primaryAppDrawer"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoposPrimaryScaffold(
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
        systemUiController.setStatusBarColor(color = color.value)

        systemUiController.setNavigationBarColor(color = navColor)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            PoposDrawer(
                modifier = Modifier.testTag(PRIMARY_APP_DRAWER),
                currentRoute = currentRoute,
                onNavigateToScreen = onNavigateToScreen,
            )
        },
        gesturesEnabled = gesturesEnabled,
    ) {
        Scaffold(
            topBar = {
                PoposLargeTopAppBar(
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
                                    contentDescription = "Back Icon",
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
                                        modifier = Modifier.testTag(CLEAR_ICON),
                                    ) {
                                        Icon(
                                            imageVector = PoposIcons.Close,
                                            contentDescription = "Close Icon",
                                        )
                                    }
                                } else {
                                    IconButton(
                                        onClick = {
                                            scope.launch {
                                                drawerState.open()
                                            }
                                        },
                                        modifier = Modifier
                                            .testTag(DRAWER_ICON),
                                    ) {
                                        Icon(
                                            imageVector = PoposIcons.App,
                                            contentDescription = "drawerIcon",
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
                    modifier = Modifier.testTag("primaryTopAppBar"),
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
                .testTag("primaryScaffold")
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
fun PoposScaffold(
    modifier: Modifier = Modifier,
    title: String,
    floatingActionButton: @Composable () -> Unit = {},
    navActions: @Composable RowScope.() -> Unit = {},
    navigationIcon: () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    fabPosition: FabPosition = FabPosition.Center,
    showBottomBar: Boolean = false,
    showBackButton: Boolean = false,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onBackClick: () -> Unit,
    content: @Composable (Shape) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    // Remember a SystemUiController
    val systemUiController = rememberSystemUiController()

    val colorTransitionFraction = scrollBehavior.state.collapsedFraction

    val color = rememberUpdatedState(newValue = containerColorForPrimary(colorTransitionFraction))
    val navColor = MaterialTheme.colorScheme.surface
    val shape = rememberUpdatedState(newValue = containerShape(colorTransitionFraction))

    SideEffect {
        systemUiController.setStatusBarColor(color = color.value)

        systemUiController.setNavigationBarColor(color = navColor)
    }

    Scaffold(
        topBar = {
            PoposLargeTopAppBar(
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
                        navigationIcon()
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
            .imePadding()
            .testTag("primaryScaffold"),
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

@Composable
fun PoposSecondaryScaffold(
    modifier: Modifier = Modifier,
    title: String,
    showBackButton: Boolean = true,
    showBottomBar: Boolean = false,
    showSecondaryBottomBar: Boolean = false,
    showFab: Boolean = true,
    fabPosition: FabPosition = FabPosition.Center,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
    onBackClick: () -> Unit,
    navigationIcon: @Composable () -> Unit = {},
    navActions: @Composable RowScope.() -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit = {},
) {
    // Remember a SystemUiController
    val systemUiController = rememberSystemUiController()

    val colorTransitionFraction = scrollBehavior.state.collapsedFraction
    val shape = rememberUpdatedState(newValue = containerShape(colorTransitionFraction))
    val statusColor = LightColor13
    val navColor = MaterialTheme.colorScheme.surface

    SideEffect {
        systemUiController.setStatusBarColor(color = statusColor)

        systemUiController.setNavigationBarColor(color = navColor)
    }

    Scaffold(
        topBar = {
            PoposLargeTopAppBar(
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
                                )
                            }
                        } else {
                            navigationIcon()
                        }
                    }
                },
                actions = navActions,
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = statusColor,
                    scrolledContainerColor = statusColor,
                ),
                modifier = Modifier
                    .testTag("secondaryTopAppBar")
                    .shadow(2.dp, RoundedCornerShape(bottomStart = 24.dp)),
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
                if (showSecondaryBottomBar) {
                    bottomBar()
                } else {
                    BottomAppBar(
                        containerColor = statusColor,
                        contentPadding = PaddingValues(SpaceMedium),
                        modifier = Modifier
                            .testTag("secondaryBottomAppBar"),
                    ) {
                        bottomBar()
                    }
                }
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
            .imePadding()
            .testTag("secondaryScaffold"),
        containerColor = Color.Transparent,
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            shape = shape.value,
            color = LightColor14,
        ) {
            content(padding)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@DevicePreviews
@Composable
private fun PoposSecondaryScaffoldPreview() {
    PoposSecondaryScaffold(
        modifier = Modifier,
        title = "Secondary Scaffold",
        showBackButton = false,
        showBottomBar = false,
        showFab = false,
        fabPosition = FabPosition.End,
        onBackClick = {},
        navigationIcon = {},
        navActions = {},
        floatingActionButton = {},
        bottomBar = {},
        content = {},
    )
}

@Composable
fun containerColorForPrimary(colorTransitionFraction: Float): Color {
    return lerp(
        MaterialTheme.colorScheme.primary,
        RoyalPurple,
        FastOutLinearInEasing.transform(colorTransitionFraction),
    )
}

@Composable
internal fun containerColorForSecondary(colorTransitionFraction: Float): Color {
    return lerp(
        MaterialTheme.colorScheme.surfaceContainerLowest,
        MaterialTheme.colorScheme.surfaceContainerLow,
        FastOutLinearInEasing.transform(colorTransitionFraction),
    )
}

@Composable
internal fun containerColor(colorTransitionFraction: Float): Color {
    return lerp(
        BoneWhite,
        Pewter,
        FastOutLinearInEasing.transform(colorTransitionFraction),
    )
}

@Composable
fun containerShape(colorTransitionFraction: Float): Shape {
    val data = lerp(
        CornerRadius(48f, 48f),
        CornerRadius(0f, 0f),
        FastOutLinearInEasing.transform(colorTransitionFraction),
    )

    return RoundedCornerShape(data.x, data.y)
}

const val DURATION = 500
const val DOUBLE_DURATION = 1000
