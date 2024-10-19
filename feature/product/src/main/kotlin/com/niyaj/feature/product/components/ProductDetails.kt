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

package com.niyaj.feature.product.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.utils.toDateString
import com.niyaj.common.utils.toFormattedDateAndTime
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Product
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.ItemNotAvailableHalf
import com.niyaj.ui.components.LoadingIndicatorHalf
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.ProductPreviewParameter
import com.niyaj.ui.utils.DevicePreviews

@Composable
internal fun ProductDetails(
    productState: UiState<Product>,
    onExpanded: () -> Unit,
    doesExpanded: Boolean,
    onClickEdit: () -> Unit,
    modifier: Modifier = Modifier,
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
            expanded = doesExpanded,
            onExpandChanged = {
                onExpanded()
            },
            content = {
                Crossfade(
                    targetState = productState,
                    label = "Product State",
                ) { state ->
                    when (state) {
                        is UiState.Loading -> LoadingIndicatorHalf()

                        is UiState.Empty -> {
                            ItemNotAvailableHalf(
                                text = "Product Details Not Available",
                                modifier = Modifier.height(IntrinsicSize.Min),
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
                                    text = "Name - ${state.data.productName}",
                                    icon = PoposIcons.CollectionsBookmark,
                                    modifier = Modifier.testTag(state.data.productName),
                                )

                                IconWithText(
                                    text = "Price - ${state.data.productPrice.toString().toRupee}",
                                    icon = PoposIcons.Rupee,
                                    modifier = Modifier.testTag(state.data.productPrice.toString()),
                                )

                                IconWithText(
                                    text = "Availability : ${state.data.productAvailability}",
                                    icon = if (state.data.productAvailability) {
                                        PoposIcons.RadioButtonChecked
                                    } else {
                                        PoposIcons.RadioButtonUnchecked
                                    },
                                    modifier = Modifier.testTag(state.data.productAvailability.toString()),
                                )

                                IconWithText(
                                    text = "Created At : ${state.data.createdAt.toFormattedDateAndTime}",
                                    icon = PoposIcons.CalenderMonth,
                                    modifier = Modifier.testTag(state.data.createdAt.toDateString),
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            rowClickable = true,
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
private fun ProductOrderDetailsPreview(
    @PreviewParameter(ProductPreviewParameter::class)
    productState: UiState<Product>,
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        ProductDetails(
            productState = productState,
            onExpanded = {},
            doesExpanded = true,
            onClickEdit = {},
            modifier = modifier,
        )
    }
}
