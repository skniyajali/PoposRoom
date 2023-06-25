package com.niyaj.poposroom.features.cart.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.poposroom.features.addon_item.domain.model.AddOnItem
import com.niyaj.poposroom.features.cart.domain.model.CartItem
import com.niyaj.poposroom.features.common.ui.theme.ProfilePictureSizeSmall
import com.niyaj.poposroom.features.common.ui.theme.SpaceMini
import com.niyaj.poposroom.features.common.ui.theme.SpaceSmall


@Composable
fun CartItems(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    cartItems: List<CartItem>,
    selectedCartItems: List<Int>,
    addOnItems: List<AddOnItem>,
    showPrintBtn: Boolean = false,
    onSelectCartOrder: (Int) -> Unit,
    onClickEditOrder: (Int) -> Unit,
    onClickViewOrder: (Int) -> Unit,
    onClickDecreaseQty: (orderId: Int, productId: Int) -> Unit,
    onClickIncreaseQty: (orderId: Int, productId: Int) -> Unit,
    onClickAddOnItem: (addOnItemId: Int, orderId: Int) -> Unit,
    onClickPlaceOrder: (orderId: Int) -> Unit,
    onClickPrintOrder: (orderId: Int) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceSmall),
        verticalArrangement = Arrangement.Top,
        state = listState,
    ) {
        itemsIndexed(
            items = cartItems,
            key = { _, cartItem ->
                cartItem.orderId
            }
        ){ index, cartItem ->
            if(cartItem.cartProducts.isNotEmpty()) {
                CartItem(
                    cartItem = cartItem,
                    doesSelected = selectedCartItems.contains(cartItem.orderId),
                    addOnItems = addOnItems,
                    showPrintBtn = showPrintBtn,
                    onSelectCartOrder = onSelectCartOrder,
                    onClickEditOrder = onClickEditOrder,
                    onClickViewOrder = onClickViewOrder,
                    onClickDecreaseQty = onClickDecreaseQty,
                    onClickIncreaseQty = onClickIncreaseQty,
                    onClickAddOnItem = onClickAddOnItem,
                    onClickPlaceOrder = onClickPlaceOrder,
                    onClickPrintOrder = onClickPrintOrder
                )

                Spacer(modifier = Modifier.height(SpaceSmall))

                if (index == cartItems.size - 1) {
                    Spacer(modifier = Modifier.height(ProfilePictureSizeSmall))
                }
            }
        }
    }
}


@Composable
fun CartItem(
    cartItem: CartItem,
    doesSelected: Boolean,
    addOnItems: List<AddOnItem>,
    showPrintBtn: Boolean = false,
    onSelectCartOrder: (Int) -> Unit,
    onClickEditOrder: (Int) -> Unit,
    onClickViewOrder: (Int) -> Unit,
    onClickDecreaseQty: (orderId: Int, productId: Int) -> Unit,
    onClickIncreaseQty: (orderId: Int, productId: Int) -> Unit,
    onClickAddOnItem: (addOnItemId: Int, orderId: Int) -> Unit,
    onClickPlaceOrder: (orderId: Int) -> Unit,
    onClickPrintOrder: (orderId: Int) -> Unit = {}
) {
    val newOrderId = if(!cartItem.customerAddress.isNullOrEmpty()){
        cartItem.customerAddress.uppercase().plus(" -")
            .plus(cartItem.orderId)
    }else{
        cartItem.orderId.toString()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colors.surface,
                RoundedCornerShape(6.dp)
            )
            .clickable(
                indication = null,
                interactionSource = remember {
                    MutableInteractionSource()
                }
            ) {
                onSelectCartOrder(cartItem.orderId)
            },
        elevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            CartItemOrderDetailsSection(
                orderId = newOrderId,
                orderType =  cartItem.orderType,
                customerPhone = cartItem.customerPhone,
                selected = doesSelected,
                onClick = {
                    onSelectCartOrder(cartItem.orderId)
                },
                onEditClick = {
                    onClickEditOrder(cartItem.orderId)
                },
                onViewClick = {
                    onClickViewOrder(cartItem.orderId)
                }
            )

            Spacer(modifier = Modifier.height(SpaceMini))

            CartItemProductDetailsSection(
                cartProducts = cartItem.cartProducts,
                decreaseQuantity = {
                    onClickDecreaseQty(cartItem.orderId, it)
                },
                increaseQuantity = {
                    onClickIncreaseQty(cartItem.orderId, it)
                }
            )


            if(addOnItems.isNotEmpty()){
                Spacer(modifier = Modifier.height(SpaceSmall))

                CartAddOnItems(
                    addOnItems = addOnItems,
                    selectedAddOnItem = cartItem.addOnItems.collectAsStateWithLifecycle(initialValue = emptyList()).value,
                    onClick = {
                        onClickAddOnItem(it, cartItem.orderId)
                    },
                )
            }

            Spacer(modifier = Modifier.height(SpaceSmall))

            CartItemTotalPriceSection(
                itemCount = cartItem.cartProducts.size,
                orderType = cartItem.orderType,
                totalPrice = cartItem.orderPrice.totalPrice,
                discountPrice = cartItem.orderPrice.discountPrice,
                showPrintBtn = showPrintBtn,
                onClickPlaceOrder = {
                    onClickPlaceOrder(cartItem.orderId)
                },
                onClickPrintOrder = {
                    onClickPrintOrder(cartItem.orderId)
                }
            )
        }
    }
}