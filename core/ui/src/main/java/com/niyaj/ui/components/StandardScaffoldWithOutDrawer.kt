package com.niyaj.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.common.utils.Constants
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceLarge
import com.niyaj.designsystem.theme.SpaceMedium

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardScaffoldWithOutDrawer(
    title: String,
    onBackClick: () -> Unit,
    showBottomBar: Boolean = false,
    bottomBar: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
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
            color = color.value,
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
                        modifier = Modifier.testTag(Constants.STANDARD_BACK_BUTTON),
                    ) {
                        Icon(
                            imageVector = PoposIcons.Back,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.scrim,
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
                    },
                ),
                exit = fadeOut() + slideOutVertically(
                    targetOffsetY = { fullHeight ->
                        fullHeight / 4
                    },
                ),
            ) {
                BottomAppBar {
                    bottomBar()
                }
            }
        },
    ) { padding ->
        ElevatedCard(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            shape = shape.value,
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = colorTransitionFraction.dp,
            ),
        ) {
            content()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardBottomSheetScaffold(
    modifier: Modifier = Modifier,
    title: String,
    onBackClick: () -> Unit,
    showBottomBar: Boolean = false,
    bottomBar: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Scaffold(
        modifier = modifier
            .fillMaxWidth()
            .imePadding(),
        topBar = {
            TopAppBar(
                modifier = Modifier,
                title = {
                    Text(text = title)
                },
                actions = {
                    IconButton(
                        onClick = onBackClick,
                    ) {
                        Icon(
                            imageVector = PoposIcons.Close,
                            contentDescription = "Close Sheet",
                        )
                    }
                },
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
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
                BottomAppBar {
                    bottomBar()
                }
            }
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(it),
        ) {
            content()
        }
    }
}


@Composable
fun StandardBottomSheet(
    modifier: Modifier = Modifier,
    title: String,
    onBackClick: () -> Unit = {},
    closeButtonColor: Color = MaterialTheme.colorScheme.error,
    windowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier
            .consumeWindowInsets(windowInsets)
            .imePadding(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = SpaceLarge),
        ) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                elevation = CardDefaults.elevatedCardElevation(0.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SpaceMedium),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(29.dp),
                    ) {
                        Icon(
                            imageVector = PoposIcons.Close,
                            tint = closeButtonColor,
                            contentDescription = null,
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .imePadding(),
            ) {
                content()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardBottomSheetNew(
    modifier: Modifier = Modifier,
    title: String,
    onBackClick: () -> Unit,
    shape: Shape = BottomSheetDefaults.ExpandedShape,
    containerColor: Color = BottomSheetDefaults.ContainerColor,
    contentColor: Color = contentColorFor(backgroundColor = BottomSheetDefaults.ContainerColor),
    tonalElevation: Dp = BottomSheetDefaults.Elevation,
    shadowElevation: Dp = BottomSheetDefaults.Elevation,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(0.dp),
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding(),
        ) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                elevation = CardDefaults.elevatedCardElevation(0.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SpaceMedium),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(29.dp),
                    ) {
                        Icon(
                            imageVector = PoposIcons.Close,
                            tint = contentColor,
                            contentDescription = "Close Sheet",
                        )
                    }
                }
            }

            content()
        }
    }
}