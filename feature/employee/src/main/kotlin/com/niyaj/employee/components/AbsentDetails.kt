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
import com.niyaj.common.tags.EmployeeTestTags.EMP_ABSENT_NOTE
import com.niyaj.common.tags.EmployeeTestTags.EMP_ABSENT_NOT_AVAILABLE
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
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 *
 */
@Composable
internal fun AbsentDetails(
    absentState: UiState<List<EmployeeAbsentDates>>,
    onExpanded: () -> Unit,
    modifier: Modifier = Modifier,
    absentReportsExpanded: Boolean = false,
) = trace("AbsentDetails") {
    Card(
        modifier = modifier
            .testTag("AbsentDetailsCard")
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                onExpanded()
            },
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 1.dp,
        ),
    ) {
        StandardExpandable(
            expanded = absentReportsExpanded,
            onExpandChanged = {
                onExpanded()
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
                                text = EMP_ABSENT_NOT_AVAILABLE,
                                showImage = false,
                            )
                        }

                        is UiState.Success -> {
                            Spacer(modifier = Modifier.height(SpaceSmall))

                            AbsentDetailsData(
                                items = state.data.toImmutableList(),
                                modifier = Modifier,
                            )
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            rowClickable = false,
            title = {
                IconWithText(
                    text = "Absent Details",
                    icon = PoposIcons.EventBusy,
                )
            },
            expand = { modifier: Modifier ->
                IconButton(
                    modifier = modifier
                        .testTag("AbsentDetailsExpand"),
                    onClick = onExpanded,
                ) {
                    Icon(
                        imageVector = PoposIcons.ArrowDown,
                        contentDescription = "Expand Absent Details",
                        tint = MaterialTheme.colorScheme.secondary,
                    )
                }
            },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AbsentDetailsData(
    items: ImmutableList<EmployeeAbsentDates>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = SpaceMini),
    ) {
        items.forEachIndexed { index, absentReport ->
            val arrangement = if (absentReport.absentDates.isEmpty()) {
                Arrangement.Center
            } else {
                Arrangement.Start
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = SpaceSmall,
                            vertical = SpaceMini,
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconWithText(
                        text = "${absentReport.startDate.toFormattedDate} " +
                            "- ${absentReport.endDate.toFormattedDate}",
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
                        .testTag("AbsentDatesFlowRow")
                        .fillMaxWidth()
                        .padding(SpaceSmall),
                    horizontalArrangement = arrangement,
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
                            text = EMP_ABSENT_NOTE,
                            modifier = Modifier,
                        )
                    }
                }
            }

            if (index != items.size - 1) {
                Spacer(modifier = Modifier.height(SpaceMini))
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(SpaceMini))
            }
        }
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
            modifier = modifier,
            onExpanded = {},
        )
    }
}
