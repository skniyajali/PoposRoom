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

package com.niyaj.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.common.utils.Constants
import com.niyaj.designsystem.components.PoposCenterAlignedTopAppBar
import com.niyaj.designsystem.icon.PoposIcons

@Stable
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun StandardScaffoldWithOutDrawer(
    modifier: Modifier = Modifier,
    title: String,
    showSearchBar: Boolean = false,
    showSearchIcon: Boolean = false,
    searchText: String = "",
    searchPlaceholderText: String = "",
    openSearchBar: () -> Unit = {},
    onSearchTextChanged: (String) -> Unit = {},
    onClearClick: () -> Unit = {},
    onBackClick: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    floatingActionButton: @Composable () -> Unit = {},
    navActions: @Composable RowScope.() -> Unit = {},
    content: @Composable () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val systemUiController = rememberSystemUiController()

    val navColor = MaterialTheme.colorScheme.surface

    SideEffect {
        systemUiController.setStatusBarColor(color = navColor, darkIcons = true)

        systemUiController.setNavigationBarColor(color = navColor)
    }

    Scaffold(
        topBar = {
            PoposCenterAlignedTopAppBar(
                title = {
                    Text(text = title)
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                    ) {
                        Icon(
                            imageVector = PoposIcons.Back,
                            contentDescription = Constants.STANDARD_BACK_BUTTON,
                        )
                    }
                },
                actions = {
                    if (showSearchBar) {
                        StandardSearchBar(
                            searchText = searchText,
                            placeholderText = searchPlaceholderText,
                            onClearClick = onClearClick,
                            onSearchTextChanged = onSearchTextChanged,
                        )
                    } else {
                        navActions()

                        if (showSearchIcon) {
                            IconButton(
                                onClick = openSearchBar,
                            ) {
                                Icon(
                                    imageVector = PoposIcons.Search,
                                    contentDescription = Constants.SEARCH_ICON,
                                )
                            }
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = FabPosition.End,
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
            elevation = CardDefaults.cardElevation(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        ) {
            content()
        }
    }
}