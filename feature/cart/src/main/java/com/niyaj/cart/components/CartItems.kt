package com.niyaj.cart.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.niyaj.designsystem.theme.ProfilePictureSizeSmall
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.AddOnItem
import com.niyaj.model.CartItem


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
            .padding(start = SpaceSmall, top = SpaceSmall, end = SpaceSmall, bottom = 0.dp),
        verticalArrangement = Arrangement.Top,
        state = listState,
    ) {
        itemsIndexed(
            items = cartItems,
            key = { _, cartItem ->
                cartItem.orderId
            }
        ) { index, cartItem ->
            if (cartItem.cartProducts.isNotEmpty()) {
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


@OptIn(ExperimentalMaterial3Api::class)
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
    onClickPrintOrder: (orderId: Int) -> Unit = {},
) {
    val newOrderId = if (!cartItem.customerAddress.isNullOrEmpty()) {
        cartItem.customerAddress!!.uppercase().plus(" -")
            .plus(cartItem.orderId)
    } else {
        cartItem.orderId.toString()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        shape = RoundedCornerShape(6.dp),
        onClick = {
            onSelectCartOrder(cartItem.orderId)
        }
    ) {
        CartItemOrderDetailsSection(
            orderId = newOrderId,
            orderType = cartItem.orderType,
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

        CartItemProductDetailsSection(
            cartProducts = cartItem.cartProducts,
            decreaseQuantity = {
                onClickDecreaseQty(cartItem.orderId, it)
            },
            increaseQuantity = {
                onClickIncreaseQty(cartItem.orderId, it)
            }
        )


        if (addOnItems.isNotEmpty()) {
            CartAddOnItems(
                addOnItems = addOnItems,
                selectedAddOnItem = cartItem.addOnItems,
                onClick = {
                    onClickAddOnItem(it, cartItem.orderId)
                },
            )
        }

        CartItemTotalPriceSection(
            itemCount = cartItem.cartProducts.size,
            orderType = cartItem.orderType,
            basePrice = cartItem.orderPrice.basePrice,
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