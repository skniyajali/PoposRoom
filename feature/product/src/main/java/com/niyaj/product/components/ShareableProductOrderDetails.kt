package com.niyaj.product.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Feed
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.niyaj.common.utils.toFormattedDateAndTime
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.common.utils.toRupee
import com.niyaj.common.utils.toTime
import com.niyaj.designsystem.theme.ButtonSize
import com.niyaj.designsystem.theme.Pewter
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.gradient6
import com.niyaj.designsystem.theme.rainbowColorsBrush
import com.niyaj.model.Product
import com.niyaj.model.ProductWiseOrder
import com.niyaj.product.details.ProductTotalOrderDetails
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.StandardOutlinedIconButton
import com.niyaj.ui.components.StandardSuggestionChip
import com.niyaj.ui.components.TextWithCount
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.CaptureController
import com.niyaj.ui.utils.ScrollableCapturable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareableProductOrderDetails(
    modifier: Modifier = Modifier,
    captureController: CaptureController,
    productState: UiState<Product>,
    totalOrderDetails: ProductTotalOrderDetails,
    ordersState: UiState<List<ProductWiseOrder>>,
    onDismiss: () -> Unit,
    onClickShare: () -> Unit,
    onCaptured: (Bitmap?, Throwable?) -> Unit,
    onClickPrintOrder: () -> Unit,
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier
            .fillMaxSize(),
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
        ) {
            Crossfade(
                targetState = productState,
                label = "Products State",
            ) { state ->
                when (state) {
                    is UiState.Success -> {
                        when (ordersState) {
                            is UiState.Success -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    CapturableOrderDetailsCard(
                                        modifier = Modifier.weight(3f),
                                        product = state.data,
                                        totalOrder = totalOrderDetails,
                                        orders = ordersState.data,
                                        captureController = captureController,
                                        onCaptured = onCaptured
                                    )


                                    DialogButtons(
                                        modifier = Modifier,
                                        onDismiss = onDismiss,
                                        onClickShare = onClickShare,
                                        onClickPrintOrder = onClickPrintOrder,
                                    )
                                }
                            }

                            else -> LoadingIndicator()
                        }
                    }

                    else -> LoadingIndicator()
                }
            }
        }
    }
}

@Composable
fun CapturableOrderDetailsCard(
    modifier: Modifier = Modifier,
    product: Product,
    totalOrder: ProductTotalOrderDetails,
    orders: List<ProductWiseOrder>,
    captureController: CaptureController,
    onCaptured: (Bitmap?, Throwable?) -> Unit,
) {
    ScrollableCapturable(
        controller = captureController,
        onCaptured = onCaptured,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
        ) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmall),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 2.dp
                ),
                shape = RoundedCornerShape(SpaceSmall)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(gradient6)
                        )

                        CircularBox(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = 40.dp),
                            icon = Icons.AutoMirrored.Filled.Feed,
                            text = product.productName,
                            showBorder = true,
                            doesSelected = false,
                            borderStroke = BorderStroke(3.dp, rainbowColorsBrush),
                            size = 80.dp,
                        )
                    }

                    ShareableProductDetails(
                        modifier = Modifier.padding(top = 40.dp),
                        product = product
                    )

                    ProductTotalOrdersDetails(details = totalOrder)

                    ShareableOrderDetails(orders = orders, productPrice = product.productPrice)
                }
            }
        }
    }
}

@Composable
fun ShareableOrderDetails(
    modifier: Modifier = Modifier,
    orders: List<ProductWiseOrder>,
    productPrice: Int,
) {
    val groupedOrders = remember { orders.groupBy { it.orderedDate.toPrettyDate() } }
    // Track the currently loaded dates
    var loadedDates by remember { mutableStateOf(groupedOrders.keys.take(1)) }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        loadedDates.forEachIndexed { index, date ->
            key(index) {
                ShareableProductOrderDetailsCard(
                    date = date,
                    orders = groupedOrders[date] ?: emptyList(),
                    productPrice = productPrice
                )
            }
        }

        PaginationButtons(
            modifier = Modifier.padding(top = SpaceSmall),
            showViewMoreBtn = groupedOrders.keys.size != loadedDates.size,
            showViewLessBtn = loadedDates.size > 1,
            onClickViewLess = {
                loadedDates = loadedDates.dropLast(1)
            },
            onClickViewMore = {
                val remainingDates = groupedOrders.keys - loadedDates.toSet()
                if (remainingDates.isNotEmpty()) {
                    loadedDates = loadedDates + remainingDates.take(1)
                }
            }
        )
    }
}

@Composable
fun ShareableProductOrderDetailsCard(
    modifier: Modifier = Modifier,
    date: String,
    orders: List<ProductWiseOrder>,
    productPrice: Int,
) {
    val grpByOrderType = remember { orders.groupBy { it.orderType } }
    val totalSales = remember { orders.sumOf { it.quantity }.times(productPrice) }.toString()

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        TextWithCount(
            modifier = Modifier
                .background(Color.Transparent),
            text = date,
            trailingText = totalSales.toRupee,
            count = orders.size,
            leadingIcon = Icons.Default.CalendarMonth
        )

        grpByOrderType.forEach { (orderType, grpOrders) ->
            val totalPrice =
                remember { grpOrders.sumOf { it.quantity }.times(productPrice) }.toString()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Pewter)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "$orderType - ${totalPrice.toRupee}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(SpaceMini)
                        .align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(SpaceMini))

            grpOrders.forEachIndexed { index, order ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SpaceSmall),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(SpaceSmall)
                ) {
                    IconWithText(
                        text = "${order.orderId}",
                        icon = Icons.Default.Tag,
                        isTitle = true,
                        modifier = Modifier.weight(0.5f)
                    )

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        order.customerPhone?.let {
                            Text(text = it)
                        }

                        order.customerAddress?.let {
                            Spacer(modifier = Modifier.height(SpaceMini))
                            Text(text = it)
                        }
                    }

                    Text(
                        text = "${order.quantity} Qty",
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(0.7f)
                    )

                    Text(
                        text = order.orderedDate.toTime,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(0.8f)
                    )
                }

                if (index != orders.size - 1 && index != grpOrders.size - 1) {
                    Spacer(modifier = Modifier.height(SpaceMini))
                    HorizontalDivider(modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(SpaceMini))
                }
            }
        }
    }
}

@Composable
fun PaginationButtons(
    modifier: Modifier = Modifier,
    showViewMoreBtn: Boolean,
    showViewLessBtn: Boolean,
    onClickViewLess: () -> Unit,
    onClickViewMore: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(SpaceSmall, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // "View Less" button
        AnimatedVisibility(
            visible = showViewLessBtn,
            enter = fadeIn(),
            exit = fadeOut(tween(500))
        ) {
            FilledTonalButton(
                onClick = onClickViewLess,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Icon(imageVector = Icons.Default.ArrowDropUp, contentDescription = "View Less")
                Text("View Less")
            }
        }

        // "Load More" button
        FilledTonalButton(
            onClick = onClickViewMore,
            enabled = showViewMoreBtn,
        ) {
            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "View More")
            Text("Load More")
        }
    }
}

@Composable
fun ShareableProductDetails(
    modifier: Modifier = Modifier,
    product: Product,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SpaceMini),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(SpaceMini))

        Text(
            text = "Product Details".uppercase(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement
                .spacedBy(SpaceSmall, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StandardSuggestionChip(
                icon = Icons.Default.CollectionsBookmark,
                text = product.productName,
            )

            StandardSuggestionChip(
                icon = Icons.Default.CurrencyRupee,
                text = product.productPrice.toString(),
            )
        }

        StandardSuggestionChip(
            icon = Icons.Default.CalendarMonth,
            text = (product.updatedAt ?: product.createdAt).toFormattedDateAndTime,
        )

        Spacer(modifier = Modifier.height(SpaceMini))
    }
}

@Composable
fun DialogButtons(
    modifier: Modifier = Modifier,
    shareButtonColor: Color = MaterialTheme.colorScheme.primary,
    onDismiss: () -> Unit,
    onClickShare: () -> Unit,
    onClickPrintOrder: () -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
        shadowElevation = 4.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = SpaceMedium, horizontal = SpaceSmall),
            horizontalArrangement = Arrangement.spacedBy(SpaceMedium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StandardOutlinedIconButton(
                modifier = Modifier
                    .size(ButtonSize),
                icon = Icons.Default.Close,
                onClick = onDismiss,
                borderColor = MaterialTheme.colorScheme.error,
            )

            StandardOutlinedIconButton(
                modifier = Modifier
                    .size(ButtonSize),
                icon = Icons.Default.Print,
                onClick = onClickPrintOrder,
                borderColor = MaterialTheme.colorScheme.secondary,
            )

            Button(
                onClick = onClickShare,
                modifier = Modifier
                    .heightIn(ButtonSize)
                    .weight(1f),
                shape = RoundedCornerShape(SpaceMini),
                colors = ButtonDefaults.buttonColors(
                    containerColor = shareButtonColor,
                ),
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share Data"
                )
                Spacer(modifier = Modifier.width(SpaceMini))
                Text(text = "Share")
            }
        }
    }
}