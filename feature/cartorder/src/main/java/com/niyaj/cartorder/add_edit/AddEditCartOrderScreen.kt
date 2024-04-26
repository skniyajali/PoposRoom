package com.niyaj.cartorder.add_edit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.common.tags.CartOrderTestTags.ADDRESS_NAME_ERROR_FIELD
import com.niyaj.common.tags.CartOrderTestTags.ADDRESS_NAME_FIELD
import com.niyaj.common.tags.CartOrderTestTags.ADD_EDIT_CART_ORDER_SCREEN
import com.niyaj.common.tags.CartOrderTestTags.CART_ADD_ON_ITEMS
import com.niyaj.common.tags.CartOrderTestTags.CART_CHARGES_ITEMS
import com.niyaj.common.tags.CartOrderTestTags.CHARGES_INCLUDED_FIELD
import com.niyaj.common.tags.CartOrderTestTags.CREATE_NEW_CART_ORDER
import com.niyaj.common.tags.CartOrderTestTags.CUSTOMER_PHONE_ERROR_FIELD
import com.niyaj.common.tags.CartOrderTestTags.CUSTOMER_PHONE_FIELD
import com.niyaj.common.tags.CartOrderTestTags.EDIT_CART_ORDER
import com.niyaj.common.tags.CartOrderTestTags.ORDER_ID_FIELD
import com.niyaj.common.tags.CartOrderTestTags.ORDER_TYPE_FIELD
import com.niyaj.common.tags.ProductTestTags.ADD_EDIT_PRODUCT_BUTTON
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.LightColor6
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.OrderType
import com.niyaj.ui.components.AnimatedTextDividerDashed
import com.niyaj.ui.components.CartAddOnItems
import com.niyaj.ui.components.CartChargesItem
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.LoadingIndicatorHalf
import com.niyaj.ui.components.MultiSelector
import com.niyaj.ui.components.PhoneNoCountBox
import com.niyaj.ui.components.StandardButton
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.components.StandardScaffoldRouteNew
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Destination(route = Screens.ADD_EDIT_CART_ORDER_SCREEN)
@Composable
fun AddEditCartOrderScreen(
    cartOrderId: Int = 0,
    navController: NavController,
    viewModel: AddEditCartOrderViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>,
) {
    val lazyListState = rememberLazyListState()

    val customers = viewModel.customers.collectAsStateWithLifecycle().value
    val addresses = viewModel.addresses.collectAsStateWithLifecycle().value

    val newAddress = viewModel.newAddress.collectAsStateWithLifecycle().value
    val newCustomer = viewModel.newCustomer.collectAsStateWithLifecycle().value

    val addressError = viewModel.addressError.collectAsStateWithLifecycle().value
    val customerError = viewModel.customerError.collectAsStateWithLifecycle().value
    val orderId = viewModel.orderId.collectAsStateWithLifecycle().value

    val addOnState = viewModel.addOnItems.collectAsStateWithLifecycle().value
    val selectedAddOns = viewModel.selectedAddOnItems.toList()

    val chargesState = viewModel.charges.collectAsStateWithLifecycle().value
    val selectedCharges = viewModel.selectedCharges.toList()

    val enableBtn = listOf(
        addressError,
        customerError,
    ).all { it == null }

    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value

    LaunchedEffect(key1 = event) {
        event?.let { data ->
            when (data) {
                is UiEvent.OnError -> {
                    resultBackNavigator.navigateBack(data.errorMessage)
                }

                is UiEvent.OnSuccess -> {
                    resultBackNavigator.navigateBack(data.successMessage)
                }
            }
        }
    }

    var addressToggled by remember { mutableStateOf(false) }
    var customerToggled by remember { mutableStateOf(false) }

    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    val title = if (cartOrderId == 0) CREATE_NEW_CART_ORDER else EDIT_CART_ORDER

    TrackScreenViewEvent(screenName = Screens.ADD_EDIT_CART_ORDER_SCREEN)

    StandardScaffoldRouteNew(
        title = title,
        showBackButton = true,
        showBottomBar = true,
        bottomBar = {
            BottomAppBar(
                containerColor = LightColor6
            ) {
                StandardButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(ADD_EDIT_PRODUCT_BUTTON)
                        .padding(SpaceSmall),
                    enabled = enableBtn,
                    text = title,
                    icon = if (cartOrderId == 0) PoposIcons.Add else PoposIcons.Edit,
                    onClick = {
                        viewModel.onEvent(AddEditCartOrderEvent.CreateOrUpdateCartOrder(cartOrderId))
                    },
                )
            }
        },
        onBackClick = navController::navigateUp,
    ) {
        TrackScrollJank(scrollableState = lazyListState, stateName = "CreateOrUpdate Cart Order")

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .testTag(ADD_EDIT_CART_ORDER_SCREEN)
                .fillMaxWidth()
                .padding(SpaceMedium),
        ) {
            item(ORDER_TYPE_FIELD) {
                val orderTypes = listOf(
                    OrderType.DineIn.name, OrderType.DineOut.name,
                )
                val icons = listOf(
                    PoposIcons.DinnerDining, PoposIcons.DeliveryDining,
                )

                MultiSelector(
                    options = orderTypes,
                    icons = icons,
                    selectedOption = viewModel.state.orderType.name,
                    onOptionSelect = { option ->
                        viewModel.onEvent(
                            AddEditCartOrderEvent.OrderTypeChanged(OrderType.valueOf(option)),
                        )
                    },
                    modifier = Modifier
                        .height(48.dp)
                        .testTag(ORDER_TYPE_FIELD)
                        .fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(SpaceMedium))
            }

            item(ORDER_ID_FIELD) {
                StandardOutlinedTextField(
                    value = orderId.toString(),
                    label = ORDER_ID_FIELD,
                    leadingIcon = PoposIcons.Tag,
                    readOnly = true,
                    onValueChange = {},
                )
            }

            item(ADDRESS_NAME_FIELD) {
                Spacer(modifier = Modifier.height(SpaceSmall))

                AnimatedVisibility(
                    visible = viewModel.state.orderType != OrderType.DineIn,
                    enter = fadeIn(tween(500)),
                    exit = fadeOut(tween(600)),
                ) {
                    ExposedDropdownMenuBox(
                        expanded = addressToggled,
                        onExpandedChange = {
                            addressToggled = !addressToggled
                        },
                    ) {
                        StandardOutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .onGloballyPositioned { coordinates ->
                                    //This is used to assign to the DropDown the same width
                                    textFieldSize = coordinates.size.toSize()
                                }
                                .menuAnchor(),
                            value = newAddress.addressName,
                            label = ADDRESS_NAME_FIELD,
                            leadingIcon = PoposIcons.Address,
                            isError = addressError != null,
                            errorText = addressError,
                            errorTextTag = ADDRESS_NAME_ERROR_FIELD,
                            onValueChange = {
                                viewModel.onEvent(
                                    AddEditCartOrderEvent.AddressNameChanged(it),
                                )
                                addressToggled = true
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = addressToggled,
                                )
                            },
                        )
                        if (addresses.isNotEmpty()) {
                            DropdownMenu(
                                expanded = addressToggled,
                                onDismissRequest = {
                                    addressToggled = false
                                },
                                properties = PopupProperties(
                                    focusable = false,
                                    dismissOnBackPress = true,
                                    dismissOnClickOutside = true,
                                    excludeFromSystemGesture = true,
                                    clippingEnabled = true,
                                ),
                                modifier = Modifier
                                    .width(with(LocalDensity.current) { textFieldSize.width.toDp() }),
                            ) {
                                addresses.forEachIndexed { index, address ->
                                    DropdownMenuItem(
                                        modifier = Modifier
                                            .testTag(address.addressName)
                                            .fillMaxWidth(),
                                        onClick = {
                                            viewModel.onEvent(
                                                AddEditCartOrderEvent.AddressChanged(address),
                                            )
                                            addressToggled = false
                                        },
                                        text = {
                                            Text(text = address.addressName)
                                        },
                                        leadingIcon = {
                                            CircularBox(
                                                icon = PoposIcons.Address,
                                                doesSelected = false,
                                                size = 30.dp,
                                                showBorder = false,
                                                text = address.addressName,
                                            )
                                        },
                                    )

                                    if (index != addresses.size - 1) {
                                        HorizontalDivider(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 44.dp),
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(SpaceSmall))
                }
            }

            item(CUSTOMER_PHONE_FIELD) {
                AnimatedVisibility(
                    visible = viewModel.state.orderType != OrderType.DineIn,
                    enter = fadeIn(tween(500)),
                    exit = fadeOut(tween(600)),
                ) {
                    ExposedDropdownMenuBox(
                        expanded = customerToggled,
                        onExpandedChange = {
                            customerToggled = !customerToggled
                        },
                    ) {
                        StandardOutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .onGloballyPositioned { coordinates ->
                                    //This is used to assign to the DropDown the same width
                                    textFieldSize = coordinates.size.toSize()
                                }
                                .menuAnchor(),
                            value = newCustomer.customerPhone,
                            label = CUSTOMER_PHONE_FIELD,
                            leadingIcon = PoposIcons.PhoneAndroid,
                            isError = customerError != null,
                            errorText = customerError,
                            errorTextTag = CUSTOMER_PHONE_ERROR_FIELD,
                            keyboardType = KeyboardType.Number,
                            onValueChange = {
                                viewModel.onEvent(
                                    AddEditCartOrderEvent.CustomerPhoneChanged(it),
                                )
                                customerToggled = true
                            },
                            trailingIcon = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    PhoneNoCountBox(
                                        count = newCustomer.customerPhone.length,
                                    )

                                    Spacer(modifier = Modifier.width(1.dp))

                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = customerToggled,
                                    )
                                }
                            },
                        )

                        if (customers.isNotEmpty()) {
                            DropdownMenu(
                                expanded = customerToggled,
                                onDismissRequest = {
                                    customerToggled = false
                                },
                                properties = PopupProperties(
                                    focusable = false,
                                    dismissOnBackPress = true,
                                    dismissOnClickOutside = true,
                                    excludeFromSystemGesture = true,
                                    clippingEnabled = true,
                                ),
                                modifier = Modifier
                                    .width(with(LocalDensity.current) { textFieldSize.width.toDp() }),
                            ) {
                                customers.forEachIndexed { index, customer ->
                                    DropdownMenuItem(
                                        modifier = Modifier
                                            .testTag(customer.customerPhone)
                                            .fillMaxWidth(),
                                        onClick = {
                                            viewModel.onEvent(
                                                AddEditCartOrderEvent.CustomerChanged(customer),
                                            )
                                            customerToggled = false
                                        },
                                        text = {
                                            Text(text = customer.customerPhone)
                                        },
                                        leadingIcon = {
                                            CircularBox(
                                                icon = PoposIcons.PhoneAndroid,
                                                doesSelected = false,
                                                size = 30.dp,
                                                showBorder = false,
                                            )
                                        },
                                    )

                                    if (index != customers.size - 1) {
                                        HorizontalDivider(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 44.dp),
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(SpaceSmall))
                }
            }

            item(CHARGES_INCLUDED_FIELD) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        modifier = Modifier.testTag(CHARGES_INCLUDED_FIELD),
                        checked = viewModel.state.doesChargesIncluded,
                        onCheckedChange = {
                            viewModel.onEvent(AddEditCartOrderEvent.DoesChargesIncluded)
                        },
                    )
                    Spacer(modifier = Modifier.width(SpaceSmall))
                    Text(
                        text = if (viewModel.state.doesChargesIncluded)
                            "Charges included"
                        else
                            "Charges not included",
                        style = MaterialTheme.typography.labelMedium,
                    )
                }

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item(CART_ADD_ON_ITEMS) {
                Crossfade(
                    targetState = addOnState,
                    label = "AddOnState",
                ) { items ->
                    when (items) {
                        is UiState.Empty -> {}
                        is UiState.Loading -> LoadingIndicatorHalf()
                        is UiState.Success -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                AnimatedTextDividerDashed(text = "AddOn Items")

                                Spacer(modifier = Modifier.height(SpaceSmall))

                                CartAddOnItems(
                                    addOnItems = items.data,
                                    selectedAddOnItem = selectedAddOns,
                                    backgroundColor = Color.Transparent,
                                    onClick = {
                                        viewModel.onEvent(AddEditCartOrderEvent.SelectAddOnItem(it))
                                    },
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item(CART_CHARGES_ITEMS) {
                Crossfade(
                    targetState = chargesState,
                    label = "Charges",
                ) { items ->
                    when (items) {
                        is UiState.Empty -> {}
                        is UiState.Loading -> LoadingIndicatorHalf()
                        is UiState.Success -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                AnimatedTextDividerDashed(text = "Charges")

                                Spacer(modifier = Modifier.height(SpaceSmall))

                                CartChargesItem(
                                    chargesList = items.data,
                                    selectedItem = selectedCharges,
                                    backgroundColor = Color.Transparent,
                                    onClick = {
                                        viewModel.onEvent(AddEditCartOrderEvent.SelectCharges(it))
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}