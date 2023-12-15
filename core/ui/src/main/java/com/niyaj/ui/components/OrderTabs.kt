package com.niyaj.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LeadingIconTab
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.niyaj.designsystem.theme.SpaceMini
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OrderTabs(
    tabs: List<OrderTab>,
    pagerState: PagerState,
    containerColor: Color = TabRowDefaults.primaryContainerColor,
    contentColor: Color = TabRowDefaults.primaryContentColor
) {
    val scope = rememberCoroutineScope()

    // OR ScrollableTabRow()
    TabRow(
        // Our selected tab is our current page
        selectedTabIndex = pagerState.currentPage,
        containerColor = containerColor,
        contentColor = contentColor,
        // Override the indicator, using the provided pagerTabIndicatorOffset modifier
        indicator = { tabPositions ->
            if (pagerState.currentPage < tabPositions.size) {
                TabRowDefaults.PrimaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    width = 80.dp,
                    height = 4.dp,
                    shape = CutCornerShape(topStart = SpaceMini, topEnd = SpaceMini)
                )
            }
        }
    ) {
        // Add tabs for all of our pages
        tabs.forEachIndexed { index, tab ->
            LeadingIconTab(
                icon = { Icon(imageVector = tab.icon, contentDescription = tab.title) },
                text = { Text(tab.title) },
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                unselectedContentColor = MaterialTheme.colorScheme.outline,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OrderTabsContent(
    modifier: Modifier = Modifier,
    tabs: List<OrderTab>,
    pagerState: PagerState
) {
    HorizontalPager(
        modifier = modifier,
        state = pagerState,
    ) { page ->
        tabs[page].screen()
    }
}
