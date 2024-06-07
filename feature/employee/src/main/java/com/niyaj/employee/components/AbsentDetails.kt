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

package com.niyaj.employee.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.utils.toFormattedDate
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.LightColor6
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.EmployeeAbsentDates
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.event.UiState

/**
 *
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AbsentDetails(
    absentState: UiState<List<EmployeeAbsentDates>>,
    absentReportsExpanded: Boolean = false,
    onExpanded: () -> Unit,
) = trace("AbsentDetails") {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onExpanded()
            }
            .testTag("AbsentDetails"),
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 1.dp,
        ),
    ) {
        StandardExpandable(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = absentReportsExpanded,
            onExpandChanged = {
                onExpanded()
            },
            title = {
                IconWithText(
                    text = "Absent Details",
                    icon = PoposIcons.EventBusy,
                )
            },
            rowClickable = true,
            expand = { modifier: Modifier ->
                IconButton(
                    modifier = modifier,
                    onClick = onExpanded,
                ) {
                    Icon(
                        imageVector = PoposIcons.ArrowDown,
                        contentDescription = "Expand Absent Details",
                        tint = MaterialTheme.colorScheme.secondary,
                    )
                }
            },
            content = {
                Crossfade(
                    targetState = absentState,
                    label = "AbsentDetails",
                ) { state ->
                    when (state) {
                        is UiState.Loading -> LoadingIndicator()

                        is UiState.Empty -> {
                            ItemNotAvailable(
                                text = "Employee absent reports not available",
                                showImage = false,
                            )
                        }

                        is UiState.Success -> {
                            Spacer(modifier = Modifier.height(SpaceSmall))

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(),
                            ) {
                                state.data.forEachIndexed { index, absentReport ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(SpaceSmall),
                                        horizontalAlignment = Alignment.Start,
                                    ) {
                                        Column {
                                            Text(
                                                text = "${absentReport.startDate.toFormattedDate} - ${absentReport.endDate.toFormattedDate}",
                                                fontWeight = FontWeight.Bold,
                                            )
                                            Spacer(modifier = Modifier.height(SpaceSmall))
                                            Text(
                                                text = "${absentReport.absentDates.size} Days Absent",
                                                fontWeight = FontWeight.SemiBold,
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(SpaceSmall))
                                        HorizontalDivider(modifier = Modifier.fillMaxWidth())
                                        Spacer(modifier = Modifier.height(SpaceSmall))

                                        FlowRow(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(SpaceSmall),
                                        ) {
                                            absentReport.absentDates.forEach { date ->
                                                Card(
                                                    modifier = Modifier
                                                        .testTag(date.plus(absentReport.startDate)),
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = LightColor6,
                                                    ),
                                                ) {
                                                    Text(
                                                        text = date.toFormattedDate,
                                                        style = MaterialTheme.typography.labelMedium,
                                                        textAlign = TextAlign.Start,
                                                        fontWeight = FontWeight.SemiBold,
                                                        modifier = Modifier
                                                            .padding(SpaceSmall),
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(SpaceSmall))
                                            }
                                        }
                                    }

                                    if (index != state.data.size - 1) {
                                        Spacer(modifier = Modifier.height(SpaceMini))
                                        HorizontalDivider(modifier = Modifier.fillMaxWidth())
                                        Spacer(modifier = Modifier.height(SpaceMini))
                                    }
                                }
                            }
                        }
                    }
                }
            },
        )
    }
}
