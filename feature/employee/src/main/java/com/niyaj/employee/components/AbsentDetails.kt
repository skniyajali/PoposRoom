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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.utils.toFormattedDate
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.LightColor6
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.EmployeeAbsentDates
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.ItemNotAvailableHalf
import com.niyaj.ui.components.LoadingIndicatorHalf
import com.niyaj.ui.components.NoteText
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.EmployeeAbsentsPreviewParameter
import com.niyaj.ui.utils.DevicePreviews

/**
 *
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun AbsentDetails(
    absentState: UiState<List<EmployeeAbsentDates>>,
    absentReportsExpanded: Boolean = false,
    onExpanded: () -> Unit,
) = trace("AbsentDetails") {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
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
            rowClickable = false,
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
                        is UiState.Loading -> LoadingIndicatorHalf()

                        is UiState.Empty -> {
                            ItemNotAvailableHalf(
                                text = "Employee absent reports not available",
                                showImage = false,
                            )
                        }

                        is UiState.Success -> {
                            Spacer(modifier = Modifier.height(SpaceSmall))

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = SpaceMini),
                            ) {
                                state.data.forEachIndexed { index, absentReport ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        horizontalAlignment = Alignment.Start,
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = SpaceSmall, vertical = SpaceMini),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            IconWithText(
                                                text = "${absentReport.startDate.toFormattedDate} - ${absentReport.endDate.toFormattedDate}",
                                                icon = PoposIcons.CalenderMonth,
                                                fontWeight = FontWeight.Bold,
                                                tintColor = MaterialTheme.colorScheme.tertiary,
                                            )
                                            Spacer(modifier = Modifier.height(SpaceSmall))
                                            Text(
                                                text = "${absentReport.absentDates.size} Days Absent",
                                                fontWeight = FontWeight.SemiBold,
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(SpaceMini))
                                        HorizontalDivider(modifier = Modifier.fillMaxWidth())
                                        Spacer(modifier = Modifier.height(SpaceMini))

                                        FlowRow(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(SpaceSmall),
                                            horizontalArrangement = if (absentReport.absentDates.isEmpty()) Arrangement.Center else Arrangement.Start,
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
                                                Spacer(modifier = Modifier.width(SpaceMini))
                                            }

                                            if (absentReport.absentDates.isEmpty()) {
                                                NoteText(
                                                    text = "Did not take a leave on this date period!",
                                                    modifier = Modifier,
                                                )
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

@DevicePreviews
@Composable
private fun AbsentDetailsPreview(
    @PreviewParameter(EmployeeAbsentsPreviewParameter::class)
    absentState: UiState<List<EmployeeAbsentDates>>,
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        AbsentDetails(
            absentState = absentState,
            absentReportsExpanded = true,
            onExpanded = {},
        )
    }
}
