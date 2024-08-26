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

package com.niyaj.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.LeadingIconTab
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.ui.utils.DevicePreviews
import kotlinx.coroutines.launch

@SuppressLint("DesignSystem")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OrderTabs(
    tabs: List<OrderTab>,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    containerColor: Color = TabRowDefaults.primaryContainerColor,
    contentColor: Color = TabRowDefaults.primaryContentColor,
) {
    val scope = rememberCoroutineScope()

    // OR ScrollableTabRow()
    TabRow(
        // Our selected tab is our current page
        selectedTabIndex = pagerState.currentPage,
        containerColor = containerColor,
        contentColor = contentColor,
        modifier = modifier,
        // Override the indicator, using the provided pagerTabIndicatorOffset modifier
        indicator = { tabPositions ->
            if (pagerState.currentPage < tabPositions.size) {
                TabRowDefaults.PrimaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    width = 80.dp,
                    height = 4.dp,
                    shape = CutCornerShape(topStart = SpaceMini, topEnd = SpaceMini),
                )
            }
        },
    ) {
        // Add tabs for all of our pages
        tabs.forEachIndexed { index, tab ->
            LeadingIconTab(
                icon = { Icon(imageVector = tab.icon, contentDescription = tab.title) },
                text = {
                    BadgedBox(
                        badge = {
                            AnimatedVisibility(
                                visible = tab.showBadge,
                            ) {
                                Badge(containerColor = MaterialTheme.colorScheme.secondary)
                            }
                        },
                    ) {
                        Text(tab.title)
                    }
                },
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                unselectedContentColor = MaterialTheme.colorScheme.outline,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OrderTabsContent(
    tabs: List<OrderTab>,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
) {
    HorizontalPager(
        modifier = modifier,
        state = pagerState,
        verticalAlignment = Alignment.Top,
    ) { page ->
        tabs[page].screen()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@DevicePreviews
@Composable
private fun OrderTabsPreview(
    modifier: Modifier = Modifier,
) {
    val orderTabs = listOf(
        OrderTab.DineInOrder {
            Text("Dine In Order")
        },
        OrderTab.DineOutOrder {
            Text("Dine Out Order")
        },
    )

    PoposRoomTheme {
        OrderTabs(
            tabs = orderTabs,
            pagerState = rememberPagerState { orderTabs.size },
            modifier = modifier,
        )
    }
}
