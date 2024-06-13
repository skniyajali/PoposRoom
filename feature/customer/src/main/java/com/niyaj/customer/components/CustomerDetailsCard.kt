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

package com.niyaj.customer.components

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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.utils.toFormattedDateAndTime
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Customer
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.ItemNotAvailableHalf
import com.niyaj.ui.components.LoadingIndicatorHalf
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.CustomerPreviewParameter
import com.niyaj.ui.utils.DevicePreviews

@Composable
internal fun CustomerDetailsCard(
    modifier: Modifier = Modifier,
    customerState: UiState<Customer>,
    onExpanded: () -> Unit,
    doesExpanded: Boolean,
    onClickEdit: () -> Unit,
    shape: Shape = RoundedCornerShape(4.dp),
    containerColor: Color = MaterialTheme.colorScheme.background,
) = trace("CustomerDetailsCard") {
    ElevatedCard(
        onClick = onExpanded,
        modifier = modifier
            .testTag("CustomerDetails")
            .fillMaxWidth(),
        shape = shape,
        colors = CardDefaults.elevatedCardColors().copy(
            containerColor = containerColor,
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp,
        ),
    ) {
        StandardExpandable(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = doesExpanded,
            onExpandChanged = {
                onExpanded()
            },
            title = {
                IconWithText(
                    text = "Customer Details",
                    icon = PoposIcons.Address,
                )
            },
            trailing = {
                IconButton(
                    onClick = onClickEdit,
                ) {
                    Icon(
                        imageVector = PoposIcons.Edit,
                        contentDescription = "Edit Employee",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            },
            rowClickable = true,
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
            content = {
                Crossfade(
                    targetState = customerState,
                    label = "Customer State",
                ) { state ->
                    when (state) {
                        is UiState.Loading -> LoadingIndicatorHalf()
                        is UiState.Empty -> {
                            ItemNotAvailableHalf(
                                text = "Unable to get customer details",
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
                                    modifier = Modifier.testTag(state.data.customerPhone),
                                    text = "Phone - ${state.data.customerPhone}",
                                    icon = PoposIcons.PhoneAndroid,
                                )
                                state.data.customerName?.let { name ->
                                    Spacer(modifier = Modifier.height(SpaceSmall))
                                    IconWithText(
                                        modifier = Modifier.testTag(name),
                                        text = "Name - $name",
                                        icon = PoposIcons.Person4,
                                    )
                                }

                                state.data.customerEmail?.let { email ->
                                    Spacer(modifier = Modifier.height(SpaceSmall))

                                    IconWithText(
                                        modifier = Modifier.testTag(email),
                                        text = "Email : $email",
                                        icon = PoposIcons.Email,
                                    )
                                }

                                Spacer(modifier = Modifier.height(SpaceSmall))

                                IconWithText(
                                    modifier = Modifier.testTag(state.data.createdAt.toFormattedDateAndTime),
                                    text = "Created At : ${state.data.createdAt.toPrettyDate()}",
                                    icon = PoposIcons.CalenderToday,
                                )

                                state.data.updatedAt?.let {
                                    Spacer(modifier = Modifier.height(SpaceSmall))
                                    IconWithText(
                                        text = "Updated At : ${it.toFormattedDateAndTime}",
                                        icon = PoposIcons.Login,
                                    )
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
private fun CustomerDetailsCardPreview(
    @PreviewParameter(CustomerPreviewParameter::class)
    customerState: UiState<Customer>,
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        CustomerDetailsCard(
            modifier = modifier,
            customerState = customerState,
            onExpanded = {},
            doesExpanded = true,
            onClickEdit = {},
        )
    }
}
