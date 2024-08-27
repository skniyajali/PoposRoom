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

package com.niyaj.address.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.utils.toFormattedDateAndTime
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Address
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.ItemNotAvailableHalf
import com.niyaj.ui.components.LoadingIndicatorHalf
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.AddressDetailsPreviewParameter
import com.niyaj.ui.utils.DevicePreviews

@Composable
internal fun AddressDetailsCard(
    addressState: UiState<Address>,
    onExpanded: () -> Unit,
    expanded: Boolean,
    onClickEdit: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.background,
) = trace("AddressDetailsCard") {
    ElevatedCard(
        onClick = onExpanded,
        modifier = modifier
            .testTag("EmployeeDetails")
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = containerColor,
        ),
    ) {
        StandardExpandable(
            expanded = expanded,
            onExpandChanged = {
                onExpanded()
            },
            content = {
                Crossfade(
                    targetState = addressState,
                    label = "Address State",
                ) { state ->
                    when (state) {
                        is UiState.Loading -> LoadingIndicatorHalf()

                        is UiState.Empty -> {
                            ItemNotAvailableHalf(
                                text = "Address Details Not Available",
                                showImage = false,
                            )
                        }

                        is UiState.Success -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(SpaceSmall),
                            ) {
                                IconWithText(
                                    text = "Name - ${state.data.addressName}",
                                    icon = PoposIcons.Address,
                                    modifier = Modifier.testTag(state.data.addressName),
                                    tintColor = MaterialTheme.colorScheme.outline,
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                IconWithText(
                                    text = "Short Name - ${state.data.shortName}",
                                    icon = PoposIcons.Home,
                                    modifier = Modifier.testTag(state.data.shortName),
                                    tintColor = MaterialTheme.colorScheme.outline,
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))

                                IconWithText(
                                    text = "Created At : ${state.data.createdAt.toPrettyDate()}",
                                    icon = PoposIcons.CalenderMonth,
                                    modifier = Modifier.testTag(state.data.createdAt.toFormattedDateAndTime),
                                    tintColor = MaterialTheme.colorScheme.outline,
                                )

                                state.data.updatedAt?.let {
                                    Spacer(modifier = Modifier.height(SpaceSmall))
                                    IconWithText(
                                        text = "Updated At : ${it.toFormattedDateAndTime}",
                                        icon = PoposIcons.Login,
                                        tintColor = MaterialTheme.colorScheme.outline,
                                    )
                                }
                            }
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            rowClickable = true,
            title = {
                IconWithText(
                    text = "Address Details",
                    icon = PoposIcons.Address,
                )
            },
            trailing = {
                IconButton(
                    onClick = onClickEdit,
                ) {
                    Icon(
                        imageVector = PoposIcons.Edit,
                        contentDescription = "Edit Address",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            },
            expand = { modifier: Modifier ->
                IconButton(
                    modifier = modifier,
                    onClick = onExpanded,
                ) {
                    Icon(
                        imageVector = PoposIcons.ArrowDown,
                        contentDescription = "Expand More",
                        tint = MaterialTheme.colorScheme.secondary,
                    )
                }
            },
        )
    }
}

@DevicePreviews
@Composable
private fun AddressDetailsCardPreview(
    @PreviewParameter(AddressDetailsPreviewParameter::class)
    addressState: UiState<Address>,
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        AddressDetailsCard(
            modifier = modifier,
            addressState = addressState,
            onExpanded = {},
            expanded = true,
            onClickEdit = {},
        )
    }
}
