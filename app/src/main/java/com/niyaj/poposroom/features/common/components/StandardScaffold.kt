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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.poposroom.features.common.utils.Constants
import com.niyaj.poposroom.features.common.utils.Constants.DELETE_ICON
import com.niyaj.poposroom.features.common.utils.Constants.EDIT_ICON
import com.niyaj.poposroom.features.common.utils.Constants.FAB_TEXT
import com.niyaj.poposroom.features.common.utils.Constants.SEARCH_ICON
import com.niyaj.poposroom.features.common.utils.Constants.SELECTALL_ICON
import com.niyaj.poposroom.features.common.utils.Constants.SETTINGS_ICON
import com.niyaj.poposroom.features.common.utils.Constants.STANDARD_BACK_BUTTON
import com.niyaj.poposroom.features.destinations.AddOnItemScreenDestination
import com.niyaj.poposroom.features.destinations.AddressScreenDestination
import com.niyaj.poposroom.features.destinations.CategoryScreenDestination
import com.niyaj.poposroom.features.destinations.ChargesScreenDestination
import com.ramcosta.composedestinations.navigation.navigate
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardScaffold(
    modifier: Modifier = Modifier,
    navController: NavController,
    title: String,
    searchText : String = "",
    placeholderText: String = "Search for items",
    showFab: Boolean = true,
    fabText: String = FAB_TEXT,
    fabIcon: ImageVector = Icons.Filled.Add,
    fabExtended: Boolean = false,
    showSettings: Boolean = true,
    selectionCount: Int = 0,
    showBottomBarActions: Boolean = false,
    showSearchBar : Boolean = false,
    showBackButton: Boolean = false,
    onClearClick: () -> Unit = {},
    onDeselect: () -> Unit = {},
    onFabClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onSelectAllClick: () -> Unit = {},
    onSearchTextChanged : (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    content: @Composable (PaddingValues) -> Unit,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    // icons to mimic drawer destinations
    val items = listOf("Address", Icons.Default.Face, Icons.Default.Email)
    val selectedItem = remember { mutableStateOf(items[0]) }
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

    val currentRoute = navController.currentBackStackEntry?.destination?.route


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Link, contentDescription = null) },
                    label = { Text("AddOnItem") },
                    selected = currentRoute == AddOnItemScreenDestination.route,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(AddOnItemScreenDestination())
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Business, contentDescription = null) },
                    label = { Text("Address") },
                    selected = currentRoute == AddressScreenDestination.route,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(AddressScreenDestination())
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Bolt, contentDescription = null) },
                    label = { Text("Charges") },
                    selected = currentRoute == ChargesScreenDestination.route,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(ChargesScreenDestination())
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Category, contentDescription = null) },
                    label = { Text("Category") },
                    selected = currentRoute == CategoryScreenDestination.route,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(CategoryScreenDestination())
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
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
                        if(showBackButton) {
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
                        }else {
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
                            initialOffsetY = {
                                fullHeight -> fullHeight / 4
                            }
                        ),
                        exit = fadeOut() + slideOutVertically(
                            targetOffsetY = {
                                fullHeight -> fullHeight / 4
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
            floatingActionButton = {
                AnimatedVisibility(
                    visible = (showFab && selectionCount == 0 && !showSearchBar),
                    enter = fadeIn(),
                    exit = fadeOut(),
                    label = "FloatingActionButton"
                ) {
                    ExtendedFloatingActionButton(
                        containerColor = MaterialTheme.colorScheme.primary,
                        onClick = onFabClick,
                        expanded = fabExtended,
                        icon = { Icon(fabIcon, fabText) },
                        text = { Text(text = fabText.uppercase()) },
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.Center,
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
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                content(padding)
            }
        }
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