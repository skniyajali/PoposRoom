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

package com.niyaj.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.tags.HomeScreenTestTags
import com.niyaj.common.tags.ProductTestTags
import com.niyaj.common.utils.createDottedString
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.ProductWithQuantity
import com.niyaj.ui.components.CircularBoxWithQty
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.ItemNotFound
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.TrackScrollJank
import kotlinx.collections.immutable.ImmutableList

@Composable
fun HomeScreenProducts(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    products: ImmutableList<ProductWithQuantity>,
    onIncrease: (Int) -> Unit,
    onDecrease: (Int) -> Unit,
    onCreateProduct: () -> Unit,
) = trace("MainFeedProducts") {
    TrackScrollJank(scrollableState = lazyListState, stateName = "products:list")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceSmall),
        verticalArrangement = Arrangement.spacedBy(SpaceSmall),
    ) {
        IconWithText(
            text = "Products",
            icon = PoposIcons.Dns,
        )

        LazyColumn(
            state = lazyListState,
        ) {
            items(
                items = products,
                key = {
                    it.productId
                },
            ) { product ->
                HomeScreenProductCard(
                    product = product,
                    onIncrease = onIncrease,
                    onDecrease = onDecrease,
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item {
                ItemNotFound(
                    btnText = HomeScreenTestTags.CREATE_NEW_PRODUCT,
                    onBtnClick = onCreateProduct,
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }
        }
    }
}

@Composable
fun HomeScreenProductCard(
    modifier: Modifier = Modifier,
    product: ProductWithQuantity,
    onIncrease: (Int) -> Unit,
    onDecrease: (Int) -> Unit,
) = trace("MainFeedProductData") {
    val productName = createDottedString(product.productName, 20)

    ListItem(
        modifier = modifier
            .testTag(ProductTestTags.PRODUCT_TAG.plus(product.productId))
            .fillMaxWidth()
            .clip(RoundedCornerShape(SpaceMini)),
        headlineContent = {
            Text(
                text = productName,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        supportingContent = {
            Text(text = product.productPrice.toRupee)
        },
        leadingContent = {
            CircularBoxWithQty(
                text = product.productName,
                qty = product.quantity,
            )
        },
        trailingContent = {
            IncreaseAndDecreaseButton(
                height = 50.dp,
                iconSize = 26.dp,
                enableDecrease = product.quantity > 0,
                onClickIncrease = { onIncrease(product.productId) },
                onClickDecrease = { onDecrease(product.productId) },
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        shadowElevation = 1.dp,
        tonalElevation = 1.dp,
    )
}

@DevicePreviews
@Composable
private fun HomeScreenProductCardPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        HomeScreenProductCard(
            modifier = modifier,
            product = ProductWithQuantity(
                categoryId = 1,
                productId = 1,
                productName = "Chicken Biryani",
                productPrice = 120,
                quantity = 0,
            ),
            onIncrease = {},
            onDecrease = {},
        )
    }
}

@Composable
private fun IncreaseAndDecreaseButton(
    modifier: Modifier = Modifier,
    height: Dp,
    iconSize: Dp,
    enableDecrease: Boolean,
    onClickIncrease: () -> Unit,
    onClickDecrease: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = MaterialTheme.colorScheme.tertiary,
) {
    Surface(
        modifier = modifier
            .wrapContentWidth()
            .height(height),
        shape = RoundedCornerShape(2.dp),
        shadowElevation = 2.dp,
        tonalElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FilledTonalIconButton(
                modifier = Modifier
                    .height(height)
                    .widthIn(min = height + 10.dp),
                onClick = onClickDecrease,
                enabled = enableDecrease,
                shape = RoundedCornerShape(topStart = 2.dp, bottomStart = SpaceMini),
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = containerColor,
                    contentColor = MaterialTheme.colorScheme.error,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow.copy(
                        alpha = 1f,
                    ),
                ),
            ) {
                Icon(
                    imageVector = PoposIcons.Remove,
                    contentDescription = "Decrease Quantity",
                    modifier = Modifier.size(iconSize),
                )
            }

            VerticalDivider(
                modifier = Modifier
                    .width(0.5.dp)
                    .fillMaxHeight(),
            )
            FilledTonalIconButton(
                modifier = Modifier
                    .height(height)
                    .widthIn(min = height + 10.dp),
                onClick = onClickIncrease,
                shape = RoundedCornerShape(topEnd = 2.dp, bottomEnd = SpaceMini),
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = containerColor,
                    contentColor = contentColor,
                ),
            ) {
                Icon(
                    imageVector = PoposIcons.Add,
                    contentDescription = "Increase Quantity",
                    modifier = Modifier.size(iconSize),
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun IncreaseAndDecreaseButtonPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        IncreaseAndDecreaseButton(
            modifier = modifier,
            height = 50.dp,
            iconSize = 24.dp,
            enableDecrease = false,
            onClickIncrease = {},
            onClickDecrease = {},
        )
    }
}
