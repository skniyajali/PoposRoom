package com.niyaj.order.details

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.RoomService
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.outlined.Print
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.theme.LightColor8
import com.niyaj.designsystem.theme.Pewter
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.AddOnItem
import com.niyaj.model.Address
import com.niyaj.model.CartOrder
import com.niyaj.model.CartProductItem
import com.niyaj.model.Charges
import com.niyaj.model.Customer
import com.niyaj.model.OrderPrice
import com.niyaj.model.OrderType
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.StandardButton
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.components.StandardOutlinedChip
import com.niyaj.ui.components.StandardScaffoldNew
import com.niyaj.ui.components.TextDivider
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.ThreeGridTexts
import com.niyaj.ui.components.TwoGridTexts
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.Screens
import com.ramcosta.composedestinations.annotation.Destination

@OptIn(ExperimentalPermissionsApi::class)
@Destination(
    route = Screens.ORDER_DETAILS_SCREEN
)
@Composable
fun OrderDetailsScreen(
    orderId: Int,
    navController: NavController,
    viewModel: OrderDetailsViewModel = hiltViewModel(),
) {

    val context = LocalContext.current

    val bluetoothPermissions =
        // Checks if the device has Android 12 or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                )
            )
        } else {
            rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                )
            )
        }

    val enableBluetoothContract = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {}

    // This intent will open the enable bluetooth dialog
    val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)

    val bluetoothManager = remember {
        context.getSystemService(BluetoothManager::class.java)
    }

    val bluetoothAdapter: BluetoothAdapter? = remember {
        bluetoothManager.adapter
    }

    val printOrder: (Int) -> Unit = {
        if (bluetoothPermissions.allPermissionsGranted) {
            if (bluetoothAdapter?.isEnabled == true) {
                // Bluetooth is on print the receipt
//                printViewModel.onPrintEvent(PrintEvent.PrintOrder(it))
            } else {
                // Bluetooth is off, ask user to turn it on
                enableBluetoothContract.launch(enableBluetoothIntent)
//                printViewModel.onPrintEvent(PrintEvent.PrintOrder(it))
            }
        } else {
            bluetoothPermissions.launchMultiplePermissionRequest()
        }
    }

    var cartOrderExpended by remember {
        mutableStateOf(true)
    }

    var customerExpended by remember {
        mutableStateOf(false)
    }

    var addressExpended by remember {
        mutableStateOf(false)
    }

    var cartExpended by remember {
        mutableStateOf(true)
    }

    val lazyListState = rememberLazyListState()

    val state = viewModel.orderDetails.collectAsStateWithLifecycle().value
    val charges = viewModel.charges.collectAsStateWithLifecycle().value

    StandardScaffoldNew(
        navController = navController,
        title = "Order Details",
        showBackButton = true,
        showBottomBar = false,
        navActions = {
            IconButton(
                onClick = {
                    printOrder(orderId)
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Print,
                    contentDescription = "Print Order Details"
                )
            }
        }
    ) {
        Crossfade(
            targetState = state,
            label = "Order Details.."
        ) { newState ->
            when (newState){
                is UiState.Empty -> {
                    ItemNotAvailable(text = "Order Details not available")
                }
                is UiState.Loading -> LoadingIndicator()
                is UiState.Success -> {
                    val orderDetails = newState.data
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(SpaceSmall),
                        state = lazyListState,
                    ){
                        item("Order Details") {
                            CartOrderDetails(
                                cartOrder = orderDetails.cartOrder,
                                doesExpanded = cartOrderExpended,
                                onExpandChanged = {
                                    cartOrderExpended = !cartOrderExpended
                                }
                            )
                        }

                        item("Customer Details") {
                            if (orderDetails.cartOrder.customer.customerId != 0) {
                                CustomerDetails(
                                    customer = orderDetails.cartOrder.customer,
                                    doesExpanded = customerExpended,
                                    onExpandChanged = {
                                        customerExpended = !customerExpended
                                    },
                                    onClickViewDetails = {
                                        //TODO:: Add link for customer details screen
//                                navController.navigate(CustomerDetailsScreenDestination(it))
                                    }
                                )
                            }
                        }

                        item("Address Details") {
                            if (orderDetails.cartOrder.address.addressId != 0) {
                                AddressDetails(
                                    address = orderDetails.cartOrder.address,
                                    doesExpanded = addressExpended,
                                    onExpandChanged = {
                                        addressExpended = !addressExpended
                                    },
                                    onClickViewDetails = {
                                        //TODO:: Add link for address details screen
//                                navController.navigate(AddressDetailsScreenDestination(addressId = it))
                                    }
                                )
                            }
                        }

                        item("Order Products") {
                            if (orderDetails.cartProducts.isNotEmpty()) {
                                CartItemDetails(
                                    orderType = orderDetails.cartOrder.orderType,
                                    doesChargesIncluded = orderDetails.cartOrder.doesChargesIncluded,
                                    addOnItems = orderDetails.addOnItems,
                                    cartProduct = orderDetails.cartProducts,
                                    charges = charges,
                                    additionalCharges = orderDetails.charges,
                                    orderPrice = orderDetails.orderPrice,
                                    doesExpanded = cartExpended,
                                    onExpandChanged = {
                                        cartExpended = !cartExpended
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


/**
 * This composable displays the cart order details
 */
@Composable
fun CartOrderDetails(
    cartOrder: CartOrder,
    doesExpanded: Boolean,
    onExpandChanged: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SpaceSmall)
            .clickable {
                onExpandChanged()
            },
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = LightColor8,
        )
    ) {
        StandardExpandable(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = doesExpanded,
            onExpandChanged = {
                onExpandChanged()
            },
            title = {
                IconWithText(
                    text = "Order Details",
                    icon = Icons.Default.Inventory,
                    isTitle = true
                )
            },
            trailing = {
                StandardOutlinedChip(
                    text = cartOrder.orderStatus.name,
                    isSelected = false,
                    isToggleable = false,
                    onClick = {}
                )
            },
            rowClickable = true,
            expand = { modifier: Modifier ->
                IconButton(
                    modifier = modifier,
                    onClick = {
                        onExpandChanged()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Expand More",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SpaceSmall)
                ) {
                    IconWithText(
                        text = cartOrder.orderId.toString(),
                        icon = Icons.Default.Tag
                    )

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    IconWithText(
                        text = "Order Type : ${cartOrder.orderType}",
                        icon = if(cartOrder.orderType == OrderType.DineIn)
                            Icons.Default.RoomService else Icons.Default.DeliveryDining
                    )

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    IconWithText(
                        text = "Created At : ${cartOrder.createdAt.toPrettyDate()}",
                        icon = Icons.Default.MoreTime
                    )

                    cartOrder.updatedAt?.let {
                        Spacer(modifier = Modifier.height(SpaceSmall))

                        IconWithText(
                            text = "Updated At : ${it.toPrettyDate()}",
                            icon = Icons.Default.Update
                        )
                    }
                }
            },
        )
    }
}

/**
 * This composable displays the customer details
 */
@Composable
fun CustomerDetails(
    customer: Customer,
    doesExpanded : Boolean,
    onExpandChanged : () -> Unit,
    onClickViewDetails : (Int) -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SpaceSmall)
            .clickable {
                onExpandChanged()
            },
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        )
    ) {
        StandardExpandable(
            onExpandChanged = {
                onExpandChanged()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = doesExpanded,
            title = {
                IconWithText(
                    text = "Customer Details",
                    icon = Icons.Default.Person,
                    isTitle = true
                )
            },
            rowClickable = true,
            expand = {  modifier: Modifier ->
                IconButton(
                    onClick = {
                        onClickViewDetails(customer.customerId)
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = "View Address Details",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
                IconButton(
                    modifier = modifier,
                    onClick = {
                        onExpandChanged()
                    }
                ) {
                    androidx.compose.material.Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Expand More",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SpaceSmall),
                ) {
                    customer.customerName?.let {
                        IconWithText(
                            text = "Name: $it",
                            icon = Icons.Default.Person,
                        )
                        Spacer(modifier = Modifier.height(SpaceSmall))
                    }

                    IconWithText(
                        text = "Phone: ${customer.customerPhone}",
                        icon = Icons.Default.PhoneAndroid,
                    )

                    customer.customerEmail?.let {
                        Spacer(modifier = Modifier.height(SpaceSmall))

                        IconWithText(
                            text = "Name: $it",
                            icon = Icons.Default.AlternateEmail,
                        )
                    }

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    IconWithText(
                        text = "Created At : ${customer.createdAt.toPrettyDate()}",
                        icon = Icons.Default.MoreTime
                    )

                    customer.updatedAt?.let {
                        Spacer(modifier = Modifier.height(SpaceSmall))

                        IconWithText(
                            text = "Updated At : ${it.toPrettyDate()}",
                            icon = Icons.Default.Update
                        )
                    }

                    Spacer(modifier = Modifier.height(SpaceMedium))

                    StandardButton(
                        onClick = {
                            onClickViewDetails(customer.customerId)
                        },
                        text = "View Customer Details".uppercase(),
                        icon = Icons.Default.OpenInBrowser,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        )
                    )
                }
            },
        )
    }
}

/**
 * This composable displays the address details
 */
@Composable
fun AddressDetails(
    address: Address,
    doesExpanded : Boolean,
    onExpandChanged : () -> Unit,
    onClickViewDetails: (Int) -> Unit,
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SpaceSmall)
            .clickable {
                onExpandChanged()
            },
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.outlineVariant,
        )
    ) {
        StandardExpandable(
            onExpandChanged = {
                onExpandChanged()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = doesExpanded,
            title = {
                IconWithText(
                    text = "Address Details",
                    icon = Icons.Default.LocationOn,
                    isTitle = true
                )
            },
            rowClickable = true,
            expand = {  modifier: Modifier ->
                IconButton(
                    onClick = {
                        onClickViewDetails(address.addressId)
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = "View Address Details",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }

                IconButton(
                    modifier = modifier,
                    onClick = {
                        onExpandChanged()
                    }
                ) {
                    androidx.compose.material.Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Expand More",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SpaceSmall),
                ) {
                    IconWithText(
                        text = "Short Name: ${address.shortName}",
                        icon = Icons.Default.Business,
                    )
                    Spacer(modifier = Modifier.height(SpaceSmall))

                    IconWithText(
                        text = "Name: ${address.addressName}",
                        icon = Icons.Default.Home,
                    )

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    IconWithText(
                        text = "Created At : ${address.createdAt.toPrettyDate()}",
                        icon = Icons.Default.MoreTime
                    )

                    address.updatedAt?.let {
                        Spacer(modifier = Modifier.height(SpaceSmall))

                        IconWithText(
                            text = "Updated At : ${it.toPrettyDate()}",
                            icon = Icons.Default.Update
                        )
                    }

                    Spacer(modifier = Modifier.height(SpaceMedium))

                    StandardButton(
                        onClick = {
                            onClickViewDetails(address.addressId)
                        },
                        text = "View Address Details".uppercase(),
                        icon = Icons.Default.Details,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
            },
        )
    }
}


/**
 * This composable displays the cart items
 */
@Composable
fun CartItemDetails(
    orderType: OrderType,
    doesChargesIncluded: Boolean,
    cartProduct: List<CartProductItem>,
    addOnItems: List<AddOnItem>,
    charges: List<Charges>,
    additionalCharges: List<Charges> = emptyList(),
    orderPrice: OrderPrice,
    doesExpanded : Boolean,
    onExpandChanged : () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SpaceSmall)
            .clickable {
                onExpandChanged()
            },
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Pewter,
        )
    ){
        StandardExpandable(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = doesExpanded,
            onExpandChanged = {
                onExpandChanged()
            },
            title = {
                IconWithText(
                    text = "Cart Items",
                    icon = Icons.Default.ShoppingBag,
                    isTitle = true
                )
            },
            trailing = {
                StandardOutlinedChip(
                    text = "${cartProduct.size} Items",
                    isToggleable = false,
                    isSelected = false,
                    onClick = {}
                )
            },
            rowClickable = true,
            expand = { modifier: Modifier ->
                IconButton(
                    modifier = modifier,
                    onClick = {
                        onExpandChanged()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Expand More",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SpaceSmall)
                ) {
                    ThreeGridTexts(
                        textOne = "Name",
                        textTwo = "Price",
                        textThree = "Qty",
                        isTitle = true
                    )
                    Spacer(modifier = Modifier.height(SpaceSmall))

                    HorizontalDivider(modifier = Modifier.fillMaxWidth())

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    cartProduct.forEach { product ->
                        ThreeGridTexts(
                            textOne = product.productName,
                            textTwo = product.productPrice.toString().toRupee,
                            textThree = product.productQuantity.toString(),
                        )
                        Spacer(modifier = Modifier.height(SpaceSmall))
                    }

                    if(addOnItems.isNotEmpty()){
                        Spacer(modifier = Modifier.height(SpaceSmall))

                        TextDivider(
                            text = "Add On Items"
                        )

                        Spacer(modifier = Modifier.height(SpaceSmall))

                        for(addOnItem in addOnItems){
                            TwoGridTexts(
                                textOne = addOnItem.itemName,
                                textTwo = addOnItem.itemPrice.toString().toRupee,
                            )
                            Spacer(modifier = Modifier.height(SpaceSmall))
                        }
                    }

                    if (charges.isNotEmpty()) {
                        if (doesChargesIncluded && orderType != OrderType.DineIn) {
                            Spacer(modifier = Modifier.height(SpaceSmall))

                            TextDivider(
                                text = "Charges"
                            )

                            Spacer(modifier = Modifier.height(SpaceSmall))

                            for (charge in charges) {
                                if (charge.isApplicable) {
                                    TwoGridTexts(
                                        textOne = charge.chargesName,
                                        textTwo = charge.chargesPrice.toString().toRupee,
                                    )
                                    Spacer(modifier = Modifier.height(SpaceSmall))
                                }
                            }

                        }
                    }

                    if (additionalCharges.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(SpaceSmall))

                        TextDivider(text = "Additional Charges")

                        Spacer(modifier = Modifier.height(SpaceSmall))

                        for (charge in additionalCharges) {
                            TwoGridTexts(
                                textOne = charge.chargesName,
                                textTwo = charge.chargesPrice.toString().toRupee,
                            )
                            Spacer(modifier = Modifier.height(SpaceSmall))
                        }
                    }

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    HorizontalDivider(modifier = Modifier.fillMaxWidth())

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Sub Total",
                            style = MaterialTheme.typography.bodySmall,
                        )

                        Text(
                            text = orderPrice.basePrice.toRupee,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Discount",
                            style = MaterialTheme.typography.bodySmall
                        )

                        Text(
                            text = orderPrice.discountPrice.toRupee,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp)

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Total",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                        )

                        Text(
                            text = (orderPrice.basePrice.minus(orderPrice.discountPrice)).toRupee,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(SpaceSmall))
                }
            },
        )
    }
}