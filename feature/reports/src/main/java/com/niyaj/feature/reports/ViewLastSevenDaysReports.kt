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

package com.niyaj.feature.reports

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.StandardScaffoldRouteNew
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun ViewLastSevenDaysReports(
    navController: DestinationsNavigator,
    reportsViewModel: ReportsViewModel = hiltViewModel(),
) {
    val lazyListState = rememberLazyListState()
    val uiState = reportsViewModel.reportsBarData.collectAsStateWithLifecycle().value

    TrackScreenViewEvent(screenName = "ViewLastSevenDaysReports")

    StandardScaffoldRouteNew(
        title = "Last 7 Days Reports",
        showBackButton = true,
        onBackClick = navController::navigateUp
    ) {
        Crossfade(
            targetState = uiState,
            label = "Reports UI State",
        ) { state ->
            when (state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    ItemNotAvailable(text = "No reports available")
                }

                is UiState.Success -> {
                    TrackScrollJank(
                        scrollableState = lazyListState,
                        stateName = "Previous Day Reports::List",
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it),
                        contentPadding = PaddingValues(SpaceSmall),
                        state = lazyListState,
                    ) {
                        items(state.data) { report ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(SpaceSmall),
                                elevation = CardDefaults.cardElevation(2.dp),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(SpaceSmall),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Column {
                                        Text(
                                            text = report.yValue.toString(),
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        Spacer(modifier = Modifier.height(SpaceSmall))
                                        Text(
                                            text = report.xValue.toString()
                                                .substringBefore(".").toRupee,
                                            style = MaterialTheme.typography.labelMedium,
                                        )
                                    }

                                    IconButton(
                                        onClick = {},
                                    ) {
                                        Icon(
                                            imageVector = PoposIcons.ArrowRightAlt,
                                            contentDescription = "View Details",
                                            tint = MaterialTheme.colorScheme.secondary,
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(SpaceSmall))
                        }
                    }
                }
            }
        }
    }
}