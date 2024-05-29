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

package com.niyaj.product.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.utils.toDateString
import com.niyaj.common.utils.toFormattedDateAndTime
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Product
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.event.UiState

@Composable
fun ProductDetails(
    modifier: Modifier = Modifier,
    productState: UiState<Product>,
    onExpanded: () -> Unit,
    doesExpanded: Boolean,
    onClickEdit: () -> Unit,
) = trace("ProductDetails") {
    ElevatedCard(
        onClick = onExpanded,
        modifier = modifier
            .testTag("ProductDetails")
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.background,
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
                    text = "Product Details",
                    icon = PoposIcons.Feed,
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
                    targetState = productState,
                    label = "Product State",
                ) { state ->
                    when (state) {
                        is UiState.Loading -> LoadingIndicator()

                        is UiState.Empty -> {
                            ItemNotAvailable(
                                text = "Product Details Not Available",
                                showImage = false,
                            )
                        }

                        is UiState.Success -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(SpaceSmall),
                                verticalArrangement = Arrangement.spacedBy(SpaceSmall),
                            ) {
                                IconWithText(
                                    modifier = Modifier.testTag(state.data.productName),
                                    text = "Name - ${state.data.productName}",
                                    icon = PoposIcons.CollectionsBookmark,
                                )

                                IconWithText(
                                    modifier = Modifier.testTag(state.data.productPrice.toString()),
                                    text = "Price - ${state.data.productPrice.toString().toRupee}",
                                    icon = PoposIcons.Rupee,
                                )

                                IconWithText(
                                    modifier = Modifier.testTag(state.data.productAvailability.toString()),
                                    text = "Availability : ${state.data.productAvailability}",
                                    icon = if (state.data.productAvailability) {
                                        PoposIcons.RadioButtonChecked
                                    } else {
                                        PoposIcons.RadioButtonUnchecked
                                    },
                                )

                                IconWithText(
                                    modifier = Modifier.testTag(state.data.createdAt.toDateString),
                                    text = "Created At : ${state.data.createdAt.toFormattedDateAndTime}",
                                    icon = PoposIcons.CalenderToday,
                                )

                                state.data.updatedAt?.let {
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
