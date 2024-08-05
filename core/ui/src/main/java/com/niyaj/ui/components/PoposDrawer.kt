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

package com.niyaj.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.niyaj.core.ui.R
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.IconSizeLarge
import com.niyaj.designsystem.theme.IconSizeSmall
import com.niyaj.designsystem.theme.LightColor12
import com.niyaj.designsystem.theme.ProfilePictureSizeSmall
import com.niyaj.designsystem.theme.SpaceLarge
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.ui.utils.Screens

@Stable
@Composable
fun PoposDrawer(
    currentRoute: String,
    onNavigateToScreen: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val expanded = remember { mutableStateOf(false) }
    val settingsExpanded = remember { mutableStateOf(false) }
    val employeeExpanded = remember { mutableStateOf(false) }
    val customersExpanded = remember { mutableStateOf(false) }
    val ordersExpanded = remember { mutableStateOf(false) }
    val marketItemExpanded = remember { mutableStateOf(false) }

    ModalDrawerSheet(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.weight(0.3f),
        ) {
            Spacer(modifier = Modifier.height(SpaceSmall))

            PoposDrawerHeader(onNavigateToScreen)

            Spacer(modifier = Modifier.height(SpaceLarge))
        }

        LazyColumn(
            modifier = Modifier
                .weight(2.5f)
                .testTag("drawerList"),
        ) {
            item("Divider") {
                HorizontalDivider(thickness = 1.dp, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(SpaceMini))
            }

            item(Screens.HOME_SCREEN) {
                Spacer(modifier = Modifier.height(SpaceMedium))

                PoposDrawerItem(
                    text = "Home",
                    icon = if (currentRoute == Screens.HOME_SCREEN) PoposIcons.Home else PoposIcons.OutlinedHome,
                    selected = currentRoute == Screens.HOME_SCREEN,
                    onClick = {
                        onNavigateToScreen(Screens.HOME_SCREEN)
                    },
                )
            }

            item(Screens.CART_SCREEN) {
                Spacer(modifier = Modifier.height(SpaceSmall))

                PoposDrawerItem(
                    text = "View Cart",
                    icon = if (currentRoute == Screens.CART_SCREEN) PoposIcons.Cart else PoposIcons.OutlinedCart,
                    selected = currentRoute == Screens.CART_SCREEN,
                    onClick = {
                        onNavigateToScreen(Screens.CART_SCREEN)
                    },
                )
            }

            item(Screens.ORDER_SCREEN) {
                Spacer(modifier = Modifier.height(SpaceSmall))

                PoposDrawerItem(
                    text = "View Orders",
                    icon = if (currentRoute == Screens.ORDER_SCREEN) PoposIcons.Order else PoposIcons.OutlinedOrder,
                    selected = currentRoute == Screens.ORDER_SCREEN,
                    onClick = {
                        onNavigateToScreen(Screens.ORDER_SCREEN)
                    },
                )
            }

            item(Screens.REPORT_SCREEN) {
                Spacer(modifier = Modifier.height(SpaceSmall))

                PoposDrawerItem(
                    text = "View Reports",
                    icon = if (currentRoute == Screens.REPORT_SCREEN) PoposIcons.Assessment else PoposIcons.OutlinedAssessment,
                    selected = currentRoute == Screens.REPORT_SCREEN,
                    onClick = {
                        onNavigateToScreen(Screens.REPORT_SCREEN)
                    },
                )
            }

            item(Screens.EXPENSES_SCREEN) {
                Spacer(modifier = Modifier.height(SpaceSmall))

                PoposDrawerItem(
                    text = "View Expenses",
                    icon = if (currentRoute == Screens.EXPENSES_SCREEN) PoposIcons.StickyNote2 else PoposIcons.OutlinedStickyNote2,
                    selected = currentRoute == Screens.EXPENSES_SCREEN,
                    onClick = {
                        onNavigateToScreen(Screens.EXPENSES_SCREEN)
                    },
                )
            }

            item(Screens.MARKET_LIST_SCREEN) {
                Spacer(modifier = Modifier.height(SpaceSmall))

                PoposDrawerItem(
                    text = "View Market List",
                    icon = if (currentRoute == Screens.MARKET_LIST_SCREEN) {
                        PoposIcons.ShoppingBag
                    } else {
                        PoposIcons.OutlinedShoppingBag
                    },
                    selected = currentRoute == Screens.MARKET_LIST_SCREEN,
                    onClick = {
                        onNavigateToScreen(Screens.MARKET_LIST_SCREEN)
                    },
                )
            }

            item(Screens.DELIVERY_REPORT_SCREEN) {
                Spacer(modifier = Modifier.height(SpaceSmall))

                PoposDrawerItem(
                    text = "Delivery Reports",
                    icon = if (currentRoute == Screens.DELIVERY_REPORT_SCREEN) {
                        PoposIcons.DeliveryDining
                    } else {
                        PoposIcons.OutlinedDeliveryDining
                    },
                    selected = currentRoute == Screens.DELIVERY_REPORT_SCREEN,
                    onClick = {
                        onNavigateToScreen(Screens.DELIVERY_REPORT_SCREEN)
                    },
                )
            }

            item(key = "Orders, Cart Orders..") {
                Spacer(modifier = Modifier.height(SpaceMedium))

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LightColor12),
                )

                Spacer(modifier = Modifier.height(SpaceSmall))

                val doesExpanded = currentRoute in listOf(
                    Screens.CART_ORDER_SCREEN,
                )

                StandardExpandable(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = IconSizeSmall),
                    dividerModifier = Modifier.padding(end = IconSizeSmall),
                    expanded = ordersExpanded.value || doesExpanded,
                    onExpandChanged = {
                        ordersExpanded.value = it
                    },
                    title = {
                        Text(text = "Orders, Cart Orders..")
                    },
                    leading = {
                        Icon(
                            imageVector = PoposIcons.OutlinedAllInbox,
                            contentDescription = "Cart Order Icon",
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(modifier = Modifier.width(SpaceSmall))
                    },
                    expand = { modifier: Modifier ->
                        IconButton(
                            modifier = modifier,
                            onClick = {
                                ordersExpanded.value = !ordersExpanded.value
                            },
                        ) {
                            Icon(
                                imageVector = PoposIcons.ArrowDown,
                                contentDescription = "Expand Cart Order",
                            )
                        }
                    },
                    content = {
                        Column(
                            modifier = Modifier
                                .padding(SpaceSmall),
                        ) {
                            PoposDrawerItem(
                                text = "Orders",
                                icon = if (currentRoute == Screens.ORDER_SCREEN) {
                                    PoposIcons.Order
                                } else {
                                    PoposIcons.OutlinedOrder
                                },
                                selected = currentRoute == Screens.ORDER_SCREEN,
                                onClick = {
                                    onNavigateToScreen(Screens.ORDER_SCREEN)
                                },
                            )
                            Spacer(modifier = Modifier.height(SpaceSmall))
                            PoposDrawerItem(
                                text = "Cart Orders",
                                icon = if (currentRoute == Screens.CART_ORDER_SCREEN) {
                                    PoposIcons.BreakfastDining
                                } else {
                                    PoposIcons.OutlinedBreakfastDining
                                },
                                selected = currentRoute == Screens.CART_ORDER_SCREEN,
                                onClick = {
                                    onNavigateToScreen(Screens.CART_ORDER_SCREEN)
                                },
                            )
                        }
                    },
                )
            }

            item(key = "Market Item, Measure Units..") {
                Spacer(modifier = Modifier.height(SpaceSmall))

                val doesExpanded = currentRoute in listOf(
                    Screens.MARKET_ITEM_SCREEN,
                    Screens.MEASURE_UNIT_SCREEN,
                    Screens.MARKET_TYPE_SCREEN,
                )

                StandardExpandable(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = IconSizeSmall),
                    dividerModifier = Modifier.padding(end = IconSizeSmall),
                    expanded = marketItemExpanded.value || doesExpanded,
                    onExpandChanged = {
                        marketItemExpanded.value = it
                    },
                    title = {
                        Text(text = "Market Item, Measure Units..")
                    },
                    leading = {
                        Icon(
                            imageVector = PoposIcons.OutlinedKitchen,
                            contentDescription = "Market Item, Measure Units Icon",
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(modifier = Modifier.width(SpaceSmall))
                    },
                    expand = { modifier: Modifier ->
                        IconButton(
                            modifier = modifier,
                            onClick = {
                                marketItemExpanded.value = !marketItemExpanded.value
                            },
                        ) {
                            Icon(
                                imageVector = PoposIcons.ArrowDown,
                                contentDescription = "Expand Cart Order",
                            )
                        }
                    },
                    content = {
                        Column(
                            modifier = Modifier
                                .padding(SpaceSmall),
                            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
                        ) {
                            PoposDrawerItem(
                                text = "Market Types",
                                icon = if (currentRoute == Screens.MARKET_TYPE_SCREEN) {
                                    PoposIcons.Category
                                } else {
                                    PoposIcons.OutlinedCategory
                                },
                                selected = currentRoute == Screens.MARKET_TYPE_SCREEN,
                                onClick = {
                                    onNavigateToScreen(Screens.MARKET_TYPE_SCREEN)
                                },
                            )

                            PoposDrawerItem(
                                text = "Market Item",
                                icon = if (currentRoute == Screens.MARKET_ITEM_SCREEN) {
                                    PoposIcons.Kitchen
                                } else {
                                    PoposIcons.OutlinedKitchen
                                },
                                selected = currentRoute == Screens.MARKET_ITEM_SCREEN,
                                onClick = {
                                    onNavigateToScreen(Screens.MARKET_ITEM_SCREEN)
                                },
                            )

                            PoposDrawerItem(
                                text = "Measure Units",
                                icon = if (currentRoute == Screens.MEASURE_UNIT_SCREEN) {
                                    PoposIcons.MonitorWeight
                                } else {
                                    PoposIcons.OutlinedMonitorWeight
                                },
                                selected = currentRoute == Screens.MEASURE_UNIT_SCREEN,
                                onClick = {
                                    onNavigateToScreen(Screens.MEASURE_UNIT_SCREEN)
                                },
                            )
                        }
                    },
                )
            }

            item(key = "Customers, Addresses") {
                Spacer(modifier = Modifier.height(SpaceSmall))

                val doesExpanded = currentRoute in listOf(
                    Screens.CUSTOMER_SCREEN,
                    Screens.ADDRESS_SCREEN,
                )

                StandardExpandable(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = IconSizeSmall),
                    dividerModifier = Modifier.padding(end = IconSizeSmall),
                    expanded = customersExpanded.value || doesExpanded,
                    onExpandChanged = {
                        customersExpanded.value = it
                    },
                    title = {
                        Text(text = "Customer, Address")
                    },
                    leading = {
                        Icon(
                            imageVector = PoposIcons.OutlinedBadge,
                            contentDescription = "Customers, Addresses Icon",
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(modifier = Modifier.width(SpaceSmall))
                    },
                    expand = { modifier: Modifier ->
                        IconButton(
                            modifier = modifier,
                            onClick = {
                                customersExpanded.value = !customersExpanded.value
                            },
                        ) {
                            Icon(
                                imageVector = PoposIcons.ArrowDown,
                                contentDescription = "Expand Customer, Addresses",
                            )
                        }
                    },
                    content = {
                        Column(
                            modifier = Modifier.padding(SpaceSmall),
                        ) {
                            PoposDrawerItem(
                                text = "Customer",
                                icon = if (currentRoute == Screens.CUSTOMER_SCREEN) {
                                    PoposIcons.PeopleAlt
                                } else {
                                    PoposIcons.OutlinedPeopleAlt
                                },
                                selected = currentRoute == Screens.CUSTOMER_SCREEN,
                                onClick = {
                                    onNavigateToScreen(Screens.CUSTOMER_SCREEN)
                                },
                            )

                            Spacer(modifier = Modifier.height(SpaceSmall))

                            PoposDrawerItem(
                                text = "Address",
                                icon = if (currentRoute == Screens.ADDRESS_SCREEN) {
                                    PoposIcons.Address
                                } else {
                                    PoposIcons.OutlinedAddress
                                },
                                selected = currentRoute == Screens.ADDRESS_SCREEN,
                                onClick = {
                                    onNavigateToScreen(Screens.ADDRESS_SCREEN)
                                },
                            )
                        }
                    },
                )
            }

            item(key = "Employee, Salary, Advance") {
                Spacer(modifier = Modifier.height(SpaceSmall))

                val doesExpanded = currentRoute in listOf(
                    Screens.EMPLOYEE_SCREEN,
                    Screens.PAYMENT_SCREEN,
                    Screens.ABSENT_SCREEN,
                )

                StandardExpandable(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = IconSizeSmall),
                    dividerModifier = Modifier.padding(end = IconSizeSmall),
                    expanded = employeeExpanded.value || doesExpanded,
                    onExpandChanged = {
                        employeeExpanded.value = it
                    },
                    title = {
                        Text(text = "Employees, Salary, Advance")
                    },
                    leading = {
                        Icon(
                            imageVector = PoposIcons.OutlinedPeopleAlt,
                            contentDescription = "Employee, Salary, Advance Icon",
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(modifier = Modifier.width(SpaceSmall))
                    },
                    expand = { modifier: Modifier ->
                        IconButton(
                            modifier = modifier,
                            onClick = {
                                employeeExpanded.value = !employeeExpanded.value
                            },
                        ) {
                            Icon(
                                imageVector = PoposIcons.ArrowDown,
                                contentDescription = "Expand Employee",
                            )
                        }
                    },
                    content = {
                        Column(
                            modifier = Modifier.padding(SpaceSmall),
                        ) {
                            PoposDrawerItem(
                                text = "Employees",
                                icon = if (currentRoute == Screens.EMPLOYEE_SCREEN) {
                                    PoposIcons.SwitchAccount
                                } else {
                                    PoposIcons.OutlinedSwitchAccount
                                },
                                selected = currentRoute == Screens.EMPLOYEE_SCREEN,
                                onClick = {
                                    onNavigateToScreen(Screens.EMPLOYEE_SCREEN)
                                },
                            )

                            Spacer(modifier = Modifier.height(SpaceSmall))

                            PoposDrawerItem(
                                text = "Employee Absent Report",
                                icon = if (currentRoute == Screens.ABSENT_SCREEN) {
                                    PoposIcons.EventBusy
                                } else {
                                    PoposIcons.OutlinedEventBusy
                                },
                                selected = currentRoute == Screens.ABSENT_SCREEN,
                                onClick = {
                                    onNavigateToScreen(Screens.ABSENT_SCREEN)
                                },
                            )

                            Spacer(modifier = Modifier.height(SpaceSmall))

                            PoposDrawerItem(
                                text = "Employee Payments",
                                icon = if (currentRoute == Screens.PAYMENT_SCREEN) {
                                    PoposIcons.Money
                                } else {
                                    PoposIcons.OutlinedMoney
                                },
                                selected = currentRoute == Screens.PAYMENT_SCREEN,
                                onClick = {
                                    onNavigateToScreen(Screens.PAYMENT_SCREEN)
                                },
                            )
                        }
                    },
                )
            }

            item(key = "Products, Categories..") {
                Spacer(modifier = Modifier.height(SpaceSmall))

                val doesExpanded = currentRoute in listOf(
                    Screens.CATEGORY_SCREEN,
                    Screens.PRODUCT_SCREEN,
                    Screens.ADD_ON_ITEM_SCREEN,
                    Screens.CHARGES_SCREEN,
                )

                StandardExpandable(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = IconSizeSmall)
                        .testTag("productCategories"),
                    dividerModifier = Modifier.padding(end = IconSizeSmall),
                    expanded = expanded.value || doesExpanded,
                    onExpandChanged = {
                        expanded.value = it
                    },
                    title = {
                        Text(text = "Products, Categories..")
                    },
                    leading = {
                        Icon(
                            imageVector = PoposIcons.OutlinedWidgets,
                            contentDescription = "Products, Categories Icon",
                            tint = MaterialTheme.colorScheme.onSurface,
                        )

                        Spacer(modifier = Modifier.width(SpaceSmall))
                    },
                    expand = { modifier ->
                        IconButton(
                            modifier = modifier,
                            onClick = {
                                expanded.value = !expanded.value
                            },
                        ) {
                            Icon(
                                imageVector = PoposIcons.ArrowDown,
                                contentDescription = "Expand Product/Category",
                            )
                        }
                    },
                    content = {
                        Column(
                            modifier = Modifier.padding(SpaceSmall),
                        ) {
                            PoposDrawerItem(
                                text = "Categories",
                                icon = if (currentRoute == Screens.CATEGORY_SCREEN) {
                                    PoposIcons.Category
                                } else {
                                    PoposIcons.OutlinedCategory
                                },
                                selected = currentRoute == Screens.CATEGORY_SCREEN,
                                onClick = {
                                    onNavigateToScreen(Screens.CATEGORY_SCREEN)
                                },
                            )

                            Spacer(modifier = Modifier.height(SpaceSmall))

                            PoposDrawerItem(
                                text = "Products",
                                icon = if (currentRoute == Screens.PRODUCT_SCREEN) {
                                    PoposIcons.Dns
                                } else {
                                    PoposIcons.OutlinedDns
                                },
                                selected = currentRoute == Screens.PRODUCT_SCREEN,
                                onClick = {
                                    onNavigateToScreen(Screens.PRODUCT_SCREEN)
                                },
                            )

                            Spacer(modifier = Modifier.height(SpaceSmall))

                            PoposDrawerItem(
                                text = "AddOn Item",
                                icon = if (currentRoute == Screens.ADD_ON_ITEM_SCREEN) {
                                    PoposIcons.InsertLink
                                } else {
                                    PoposIcons.OutlinedInsertLink
                                },
                                selected = currentRoute == Screens.ADD_ON_ITEM_SCREEN,
                                onClick = {
                                    onNavigateToScreen(Screens.ADD_ON_ITEM_SCREEN)
                                },
                            )

                            Spacer(modifier = Modifier.height(SpaceSmall))

                            PoposDrawerItem(
                                text = "Charges Item",
                                icon = if (currentRoute == Screens.CHARGES_SCREEN) {
                                    PoposIcons.Bolt
                                } else {
                                    PoposIcons.OutlinedBolt
                                },
                                selected = currentRoute == Screens.CHARGES_SCREEN,
                                onClick = {
                                    onNavigateToScreen(Screens.CHARGES_SCREEN)
                                },
                            )
                        }
                    },
                )
            }

            item(key = "App Settings, Reminders") {
                Spacer(modifier = Modifier.height(SpaceSmall))

                StandardExpandable(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = IconSizeSmall),
                    dividerModifier = Modifier.padding(end = IconSizeSmall),
                    expanded = settingsExpanded.value,
                    onExpandChanged = {
                        settingsExpanded.value = it
                    },
                    title = {
                        Text(text = "App Settings, Reminders")
                    },
                    leading = {
                        Icon(
                            imageVector = PoposIcons.OutlinedSettings,
                            contentDescription = "Settings Icon",
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(modifier = Modifier.width(SpaceSmall))
                    },
                    expand = { modifier: Modifier ->
                        IconButton(
                            modifier = modifier,
                            onClick = {
                                settingsExpanded.value = !settingsExpanded.value
                            },
                        ) {
                            Icon(
                                imageVector = PoposIcons.ArrowDown,
                                contentDescription = "Expand Settings",
                            )
                        }
                    },
                    content = {
                        Column(
                            modifier = Modifier.padding(SpaceSmall),
                        ) {
                            PoposDrawerItem(
                                text = "Reminders",
                                icon = if (currentRoute == Screens.REMINDER_SCREEN) {
                                    PoposIcons.Notifications
                                } else {
                                    PoposIcons.OutlinedNotifications
                                },
                                selected = currentRoute == Screens.REMINDER_SCREEN,
                                onClick = {
//                                    onNavigateToScreen(ReminderScreenDestination())
                                },
                            )
                            Spacer(modifier = Modifier.height(SpaceSmall))
                            PoposDrawerItem(
                                text = "App Settings",
                                icon = if (currentRoute == Screens.SETTINGS_SCREEN) {
                                    PoposIcons.Settings
                                } else {
                                    PoposIcons.OutlinedSettings
                                },
                                selected = currentRoute == Screens.SETTINGS_SCREEN,
                                onClick = {
                                    onNavigateToScreen(Screens.SETTINGS_SCREEN)
                                },
                            )
                            Spacer(modifier = Modifier.height(SpaceSmall))

                            PoposDrawerItem(
                                text = "Printer Information",
                                icon = if (currentRoute == Screens.PRINTER_INFO_SCREEN) {
                                    PoposIcons.Print
                                } else {
                                    PoposIcons.OutlinedPrint
                                },
                                selected = currentRoute == Screens.PRINTER_INFO_SCREEN,
                                onClick = {
                                    onNavigateToScreen(Screens.PRINTER_INFO_SCREEN)
                                },
                            )
                        }
                    },
                )
            }
        }

        Column(
            modifier = Modifier.weight(0.2f),
        ) {
            HorizontalDivider(thickness = 1.dp, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(SpaceSmall))

            PoposDrawerItem(
                text = "Logout",
                icon = PoposIcons.OutlinedLogout,
                selected = false,
                onClick = {
                },
            )
        }
    }
}

@Composable
fun PoposDrawerHeader(
    onNavigateToScreen: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.padding(start = SpaceSmallMax),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.splash),
                contentDescription = "Restaurant Logo",
                modifier = Modifier
                    .size(ProfilePictureSizeSmall)
                    .clip(RoundedCornerShape(SpaceMini)),
            )

            Spacer(modifier = Modifier.width(SpaceMedium))

            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(id = R.string.restaurant_name),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(id = R.string.restaurant_tag),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        IconButton(
            onClick = {
                onNavigateToScreen(Screens.PROFILE_SCREEN)
            },
            modifier = Modifier.padding(end = SpaceSmall),
        ) {
            Icon(
                imageVector = PoposIcons.OutlinedAccountCircle,
                contentDescription = "My Account",
                modifier = Modifier.size(IconSizeLarge),
                tint = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}

@Composable
fun PoposDrawerItem(
    text: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    iconColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = text.plus("DrIcon"),
                tint = iconColor,
            )
        },
        label = { Text(text) },
        shape = RoundedCornerShape(SpaceMini),
        selected = selected,
        onClick = onClick,
        modifier = Modifier
            .height(48.dp)
            .padding(NavigationDrawerItemDefaults.ItemPadding)
            .testTag(text),
    )
}
