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

package com.niyaj.cart.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAll
import androidx.compose.ui.util.trace
import com.niyaj.cart.CartEvent
import com.niyaj.cart.CartState
import com.niyaj.cart.DineInEvent
import com.niyaj.cart.DineOutEvent
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.ProfilePictureSizeSmall
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.AddOnItem
import com.niyaj.model.CartItem
import com.niyaj.model.EmployeeNameAndId
import com.niyaj.model.OrderType
import com.niyaj.ui.components.AnimatedTextDividerDashed
import com.niyaj.ui.components.CartAddOnItems
import com.niyaj.ui.components.CartDeliveryPartners
import com.niyaj.ui.components.CartItemProductDetailsSection
import com.niyaj.ui.components.CartItemTotalPriceSection
import com.niyaj.ui.parameterProvider.AddOnPreviewData
import com.niyaj.ui.parameterProvider.CardOrderPreviewData
import com.niyaj.ui.parameterProvider.CartItemPreviewParameter
import com.niyaj.ui.parameterProvider.CartItemsPreviewParameter
import com.niyaj.ui.utils.DevicePreviews

@Composable
internal fun CartItems(
    modifier: Modifier = Modifier,
    cartState: CartState.Success,
    selectedCartItems: List<Int>,
    addOnItems: List<AddOnItem>,
    deliveryPartners: List<EmployeeNameAndId>,
    showPrintBtn: Boolean,
    onClickEditOrder: (Int) -> Unit,
    onClickViewOrder: (Int) -> Unit,
    onClickPrintOrder: (orderId: Int) -> Unit,
    onEvent: (CartEvent) -> Unit,
    listState: LazyListState = rememberLazyListState(),
) = trace("CartItems") {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(SpaceSmall),
        verticalArrangement = Arrangement.Top,
        state = listState,
    ) {
        itemsIndexed(
            items = cartState.items,
            key = { _, cartItem ->
                cartItem.orderId
            },
        ) { index, cartItem ->
            if (cartItem.cartProducts.isNotEmpty()) {
                CartItemData(
                    cartItem = cartItem,
                    doesSelected = selectedCartItems.contains(cartItem.orderId),
                    addOnItems = addOnItems,
                    deliveryPartners = deliveryPartners,
                    showPrintBtn = showPrintBtn,
                    onClickEditOrder = onClickEditOrder,
                    onClickViewOrder = onClickViewOrder,
                    onClickPrintOrder = onClickPrintOrder,
                    onEvent = onEvent,
                )

                Spacer(modifier = Modifier.height(SpaceSmall))

                if (index == cartState.items.size - 1) {
                    Spacer(modifier = Modifier.height(ProfilePictureSizeSmall))
                }
            }
        }
    }
}

@Composable
private fun CartItemData(
    modifier: Modifier = Modifier,
    cartItem: CartItem,
    doesSelected: Boolean,
    addOnItems: List<AddOnItem>,
    deliveryPartners: List<EmployeeNameAndId>,
    showPrintBtn: Boolean,
    onClickEditOrder: (Int) -> Unit,
    onClickViewOrder: (Int) -> Unit,
    onClickPrintOrder: (orderId: Int) -> Unit,
    onEvent: (CartEvent) -> Unit,
) = trace("CartItem") {
    val interactionSource = remember {
        MutableInteractionSource()
    }

    val newOrderId = if (!cartItem.customerAddress.isNullOrEmpty()) {
        cartItem.customerAddress!!.uppercase().plus(" - ")
            .plus(cartItem.orderId)
    } else {
        cartItem.orderId.toString()
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(interactionSource, indication = null) {
                if (cartItem.orderType == OrderType.DineIn) {
                    onEvent(DineInEvent.SelectDineInCart(cartItem.orderId))
                } else {
                    onEvent(DineOutEvent.SelectDineOutCart(cartItem.orderId))
                }
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        shape = RoundedCornerShape(6.dp),
    ) {
        CartItemOrderDetailsSection(
            orderId = newOrderId,
            orderType = cartItem.orderType,
            customerPhone = cartItem.customerPhone,
            selected = doesSelected,
            onClick = {
                if (cartItem.orderType == OrderType.DineIn) {
                    onEvent(DineInEvent.SelectDineInCart(cartItem.orderId))
                } else {
                    onEvent(DineOutEvent.SelectDineOutCart(cartItem.orderId))
                }
            },
            onEditClick = {
                onClickEditOrder(cartItem.orderId)
            },
            onViewClick = {
                onClickViewOrder(cartItem.orderId)
            },
        )

        CartItemProductDetailsSection(
            cartProducts = cartItem.cartProducts,
            decreaseQuantity = {
                onEvent(CartEvent.DecreaseQuantity(cartItem.orderId, it))
            },
            increaseQuantity = {
                onEvent(CartEvent.IncreaseQuantity(cartItem.orderId, it))
            },
        )

        AnimatedVisibility(
            visible = deliveryPartners.isNotEmpty(),
            enter = fadeIn(tween(500)),
            exit = fadeOut(tween(600)),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Spacer(modifier = Modifier.height(SpaceSmall))

                AnimatedTextDividerDashed(text = "Delivery Partner")

                Spacer(modifier = Modifier.height(SpaceSmall))

                CartDeliveryPartners(
                    partners = deliveryPartners,
                    doesSelected = {
                        it == cartItem.deliveryPartnerId
                    },
                    onClick = {
                        val newId = if (it == cartItem.deliveryPartnerId) 0 else it
                        onEvent(DineOutEvent.UpdateDeliveryPartner(cartItem.orderId, newId))
                    },
                    backgroundColor = Color.Transparent,
                )
            }
        }

        AnimatedVisibility(
            visible = addOnItems.isNotEmpty(),
            enter = fadeIn(tween(500)),
            exit = fadeOut(tween(600)),
        ) {
            val addOnSelectedColor = if (cartItem.orderType == OrderType.DineIn) {
                MaterialTheme.colorScheme.secondary
            } else {
                MaterialTheme.colorScheme.primary
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Spacer(modifier = Modifier.height(SpaceSmall))

                AnimatedTextDividerDashed(text = "AddOn Items")

                Spacer(modifier = Modifier.height(SpaceSmall))

                CartAddOnItems(
                    addOnItems = addOnItems,
                    selectedAddOnItem = cartItem.addOnItems,
                    selectedColor = addOnSelectedColor,
                    onClick = {
                        onEvent(CartEvent.UpdateAddOnItemInCart(cartItem.orderId, it))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }
        }

        CartItemTotalPriceSection(
            itemCount = cartItem.cartProducts.size,
            orderType = cartItem.orderType,
            showPrintBtn = showPrintBtn,
            totalPrice = cartItem.orderPrice,
            onClickPlaceOrder = {
                onEvent(CartEvent.PlaceCartOrder(cartItem.orderId))
            },
            onClickPrintOrder = {
                onClickPrintOrder(cartItem.orderId)
                onEvent(CartEvent.PlaceCartOrder(cartItem.orderId))
            },
        )
    }
}

@DevicePreviews
@Composable
private fun CartItemPreview(
    @PreviewParameter(CartItemPreviewParameter::class)
    cartItem: CartItem,
    modifier: Modifier = Modifier,
    addOnItems: List<AddOnItem> = AddOnPreviewData.addOnItemList.take(5),
    deliveryPartners: List<EmployeeNameAndId> = CardOrderPreviewData.sampleEmployeeNameAndIds.take(5),
) {
    PoposRoomTheme {
        CartItemData(
            modifier = modifier,
            cartItem = cartItem,
            doesSelected = false,
            addOnItems = addOnItems,
            deliveryPartners = deliveryPartners,
            showPrintBtn = false,
            onClickEditOrder = {},
            onClickViewOrder = {},
            onClickPrintOrder = {},
            onEvent = {},
        )
    }
}

@DevicePreviews
@Composable
private fun CartItemsPreview(
    @PreviewParameter(CartItemsPreviewParameter::class)
    cartItems: List<CartItem>,
    modifier: Modifier = Modifier,
    addOnItems: List<AddOnItem> = AddOnPreviewData.addOnItemList.take(5),
    deliveryPartners: List<EmployeeNameAndId> = CardOrderPreviewData.sampleEmployeeNameAndIds.take(5),
) {
    PoposRoomTheme {
        CartItems(
            modifier = modifier,
            cartState = CartState.Success(items = cartItems),
            selectedCartItems = listOf(),
            addOnItems = addOnItems,
            deliveryPartners = deliveryPartners,
            showPrintBtn = cartItems.fastAll { it.orderType == OrderType.DineOut },
            onClickEditOrder = {},
            onClickViewOrder = {},
            onClickPrintOrder = {},
            onEvent = {},
        )
    }
}
