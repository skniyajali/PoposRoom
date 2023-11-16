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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StickyNote2
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.automirrored.outlined.StickyNote2
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BreakfastDining
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.InsertLink
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AllInbox
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.BreakfastDining
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.InsertLink
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Money
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PeopleAlt
import androidx.compose.material.icons.outlined.Print
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.SwitchAccount
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.niyaj.core.ui.R
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


@Composable
fun StandardDrawer(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val expanded = remember { mutableStateOf(false) }
    val settingsExpanded = remember { mutableStateOf(false) }
    val employeeExpanded = remember { mutableStateOf(false) }
    val customersExpanded = remember { mutableStateOf(false) }
    val ordersExpanded = remember { mutableStateOf(false) }

    ModalDrawerSheet(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.weight(0.3f)
        ) {
            Spacer(modifier = Modifier.height(SpaceSmall))

            DrawerHeader(navController)

            Spacer(modifier = Modifier.height(SpaceLarge))
        }

        LazyColumn(
            modifier = Modifier
                .weight(2.5f)
        ) {
            item {
                HorizontalDivider(thickness = 1.dp, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(SpaceMini))
            }

            item {
                Spacer(modifier = Modifier.height(SpaceMedium))

                DrawerItem(
                    text = "Home",
                    icon = if (currentRoute == Screens.HOME_SCREEN) Icons.Default.Home else Icons.Outlined.Home,
                    selected = currentRoute == Screens.HOME_SCREEN,
                    onClick = {
                        navController.navigate(Screens.HOME_SCREEN)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(SpaceSmall))

                DrawerItem(
                    text = "View Orders",
                    icon = if (currentRoute == Screens.ORDER_SCREEN) Icons.Default.Inventory else Icons.Outlined.Inventory2,
                    selected = currentRoute == Screens.ORDER_SCREEN,
                    onClick = {
                        navController.navigate(Screens.ORDER_SCREEN)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(SpaceSmall))

                DrawerItem(
                    text = "View Reports",
                    icon = if (currentRoute == Screens.REPORT_SCREEN) Icons.Default.Assessment else Icons.Outlined.Assessment,
                    selected = currentRoute == Screens.REPORT_SCREEN,
                    onClick = {
                        navController.navigate(Screens.REPORT_SCREEN)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(SpaceSmall))

                DrawerItem(
                    text = "View Expenses",
                    icon = if (currentRoute == Screens.EXPENSES_SCREEN) Icons.AutoMirrored.Filled.StickyNote2 else Icons.AutoMirrored.Outlined.StickyNote2,
                    selected = currentRoute == Screens.EXPENSES_SCREEN,
                    onClick = {
                        navController.navigate(Screens.EXPENSES_SCREEN)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(SpaceSmall))

                DrawerItem(
                    text = "Market List",
                    icon = if (currentRoute == Screens.MARKET_LIST_SCREEN) Icons.Default.ShoppingBag else Icons.Outlined.ShoppingBag,
                    selected = currentRoute == Screens.MARKET_LIST_SCREEN,
                    onClick = {
                        navController.navigate(Screens.MARKET_LIST_SCREEN)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(SpaceMedium))

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LightColor12)
                )

                Spacer(modifier = Modifier.height(SpaceSmall))

                val doesExpanded = currentRoute in listOf(
                    Screens.CART_ORDER_SCREEN
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
                            imageVector = Icons.Outlined.AllInbox,
                            contentDescription = "Cart Order Icon",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(SpaceSmall))
                    },
                    expand = { modifier: Modifier ->
                        IconButton(
                            modifier = modifier,
                            onClick = {
                                ordersExpanded.value = !ordersExpanded.value
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = "Expand Cart Order"
                            )
                        }
                    },
                    content = {
                        Column(
                            modifier = Modifier
                                .padding(SpaceSmall),
                        ) {
                            DrawerItem(
                                text = "Orders",
                                icon = if (currentRoute == Screens.ORDER_SCREEN)
                                    Icons.Default.Inventory else Icons.Outlined.Inventory2,
                                selected = currentRoute == Screens.ORDER_SCREEN,
                                onClick = {
                                    navController.navigate(Screens.ORDER_SCREEN)
                                }
                            )
                            Spacer(modifier = Modifier.height(SpaceSmall))
                            DrawerItem(
                                text = "Cart Orders",
                                icon = if (currentRoute == Screens.CART_ORDER_SCREEN)
                                    Icons.Default.BreakfastDining else Icons.Outlined.BreakfastDining,
                                selected = currentRoute == Screens.CART_ORDER_SCREEN,
                                onClick = {
                                    navController.navigate(Screens.CART_ORDER_SCREEN)
                                }
                            )
                        }
                    },
                )
            }

            item {
                Spacer(modifier = Modifier.height(SpaceSmall))

                val doesExpanded = currentRoute in listOf(
                    Screens.CUSTOMER_SCREEN,
                    Screens.ADDRESS_SCREEN
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
                        Text(text = "Customers, Addresses")
                    },
                    leading = {
                        Icon(
                            imageVector = Icons.Outlined.Badge,
                            contentDescription = "Customers, Addresses Icon",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(SpaceSmall))
                    },
                    expand = { modifier: Modifier ->
                        IconButton(
                            modifier = modifier,
                            onClick = {
                                customersExpanded.value = !customersExpanded.value
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = "Expand Customer, Addresses"
                            )
                        }
                    },
                    content = {
                        Column(
                            modifier = Modifier.padding(SpaceSmall),
                        ) {
                            DrawerItem(
                                text = "Customers",
                                icon = if (currentRoute == Screens.CUSTOMER_SCREEN)
                                    Icons.Default.PeopleAlt else Icons.Outlined.PeopleAlt,
                                selected = currentRoute == Screens.CUSTOMER_SCREEN,
                                onClick = {
                                    navController.navigate(Screens.CUSTOMER_SCREEN)
                                }
                            )

                            Spacer(modifier = Modifier.height(SpaceSmall))

                            DrawerItem(
                                text = "Addresses",
                                icon = if (currentRoute == Screens.ADDRESS_SCREEN)
                                    Icons.Default.Business else Icons.Outlined.Business,
                                selected = currentRoute == Screens.ADDRESS_SCREEN,
                                onClick = {
                                    navController.navigate(Screens.ADDRESS_SCREEN)
                                }
                            )
                        }
                    },
                )
            }

            item {
                Spacer(modifier = Modifier.height(SpaceSmall))

                val doesExpanded = currentRoute in listOf(
                    Screens.EMPLOYEE_SCREEN,
                    Screens.PAYMENT_SCREEN,
                    Screens.ABSENT_SCREEN
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
                        Text(text = "Employee, Salary, Advance")
                    },
                    leading = {
                        Icon(
                            imageVector = Icons.Outlined.PeopleAlt,
                            contentDescription = "Employee, Salary, Advance Icon",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(SpaceSmall))
                    },
                    expand = { modifier: Modifier ->
                        IconButton(
                            modifier = modifier,
                            onClick = {
                                employeeExpanded.value = !employeeExpanded.value
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = "Expand Employee"
                            )
                        }
                    },
                    content = {
                        Column(
                            modifier = Modifier.padding(SpaceSmall),
                        ) {
                            DrawerItem(
                                text = "Employees",
                                icon = if (currentRoute == Screens.EMPLOYEE_SCREEN)
                                    Icons.Default.SwitchAccount else Icons.Outlined.SwitchAccount,
                                selected = currentRoute == Screens.EMPLOYEE_SCREEN,
                                onClick = {
                                    navController.navigate(Screens.EMPLOYEE_SCREEN)
                                }
                            )

                            Spacer(modifier = Modifier.height(SpaceSmall))

                            DrawerItem(
                                text = "Employee Absent Report",
                                icon = if (currentRoute == Screens.ABSENT_SCREEN)
                                    Icons.Default.EventBusy else Icons.Outlined.EventBusy,
                                selected = currentRoute == Screens.ABSENT_SCREEN,
                                onClick = {
                                    navController.navigate(Screens.ABSENT_SCREEN)
                                }
                            )

                            Spacer(modifier = Modifier.height(SpaceSmall))

                            DrawerItem(
                                text = "Employee Payments",
                                icon = if (currentRoute == Screens.PAYMENT_SCREEN)
                                    Icons.Default.Money else Icons.Outlined.Money,
                                selected = currentRoute == Screens.PAYMENT_SCREEN,
                                onClick = {
                                    navController.navigate(Screens.PAYMENT_SCREEN)
                                }
                            )
                        }
                    },
                )
            }

            item {
                Spacer(modifier = Modifier.height(SpaceSmall))

                val doesExpanded = currentRoute in listOf(
                    Screens.CATEGORY_SCREEN,
                    Screens.PRODUCT_SCREEN,
                    Screens.ADD_ON_ITEM_SCREEN,
                    Screens.CHARGES_SCREEN
                )

                StandardExpandable(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = IconSizeSmall),
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
                            imageVector = Icons.Outlined.Widgets,
                            contentDescription = "Products, Categories Icon",
                            tint = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.width(SpaceSmall))
                    },
                    expand = { modifier ->
                        IconButton(
                            modifier = modifier,
                            onClick = {
                                expanded.value = !expanded.value
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = "Expand Product/Category",
                            )
                        }
                    },
                    content = {
                        Column(
                            modifier = Modifier.padding(SpaceSmall),
                        ) {

                            DrawerItem(
                                text = "Categories",
                                icon = if (currentRoute == Screens.CATEGORY_SCREEN)
                                    Icons.Default.Category else Icons.Outlined.Category,
                                selected = currentRoute == Screens.CATEGORY_SCREEN,
                                onClick = {
                                    navController.navigate(Screens.CATEGORY_SCREEN)
                                }
                            )

                            Spacer(modifier = Modifier.height(SpaceSmall))

                            DrawerItem(
                                text = "Products",
                                icon = if (currentRoute == Screens.PRODUCT_SCREEN)
                                    Icons.Default.Dns else Icons.Outlined.Dns,
                                selected = currentRoute == Screens.PRODUCT_SCREEN,
                                onClick = {
                                    navController.navigate(Screens.PRODUCT_SCREEN)
                                }
                            )

                            Spacer(modifier = Modifier.height(SpaceSmall))

                            DrawerItem(
                                text = "AddOn Item",
                                icon = if (currentRoute == Screens.ADD_ON_ITEM_SCREEN)
                                    Icons.Default.InsertLink else Icons.Outlined.InsertLink,
                                selected = currentRoute == Screens.ADD_ON_ITEM_SCREEN,
                                onClick = {
                                    navController.navigate(Screens.ADD_ON_ITEM_SCREEN)
                                }
                            )

                            Spacer(modifier = Modifier.height(SpaceSmall))

                            DrawerItem(
                                text = "Charges Item",
                                icon = if (currentRoute == Screens.CHARGES_SCREEN)
                                    Icons.Default.Bolt else Icons.Outlined.Bolt,
                                selected = currentRoute == Screens.CHARGES_SCREEN,
                                onClick = {
                                    navController.navigate(Screens.CHARGES_SCREEN)
                                }
                            )

                        }
                    },
                )
            }

            item {
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
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings Icon",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(SpaceSmall))
                    },
                    expand = { modifier: Modifier ->
                        IconButton(
                            modifier = modifier,
                            onClick = {
                                settingsExpanded.value = !settingsExpanded.value
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = "Expand Settings"
                            )
                        }
                    },
                    content = {
                        Column(
                            modifier = Modifier.padding(SpaceSmall),
                        ) {
                            DrawerItem(
                                text = "Reminders",
                                icon = if (currentRoute == Screens.REMINDER_SCREEN)
                                    Icons.Default.Notifications else Icons.Outlined.Notifications,
                                selected = currentRoute == Screens.REMINDER_SCREEN,
                                onClick = {
//                                    navController.navigate(ReminderScreenDestination())
                                }
                            )
                            Spacer(modifier = Modifier.height(SpaceSmall))
                            DrawerItem(
                                text = "App Settings",
                                icon = if (currentRoute == Screens.SETTINGS_SCREEN)
                                    Icons.Default.Settings else Icons.Outlined.Settings,
                                selected = currentRoute == Screens.SETTINGS_SCREEN,
                                onClick = {
//                                    navController.navigate(SettingsScreenDestination())
                                }
                            )
                            Spacer(modifier = Modifier.height(SpaceSmall))

                            DrawerItem(
                                text = "Printer Information",
                                icon = if (currentRoute == Screens.PRINTER_INFO_SCREEN)
                                    Icons.Default.Print else Icons.Outlined.Print,
                                selected = currentRoute == Screens.PRINTER_INFO_SCREEN,
                                onClick = {
                                    navController.navigate(Screens.PRINTER_INFO_SCREEN)
                                }
                            )
                        }
                    },
                )
            }
        }

        Column(
            modifier = Modifier.weight(0.2f)
        ) {
            HorizontalDivider(thickness = 1.dp, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(SpaceSmall))

            DrawerItem(
                text = "Logout",
                icon = Icons.AutoMirrored.Outlined.Logout,
                selected = false,
                onClick = {

                }
            )
        }
    }
}


@Composable
fun DrawerHeader(
    navController: NavController,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.padding(start = SpaceSmallMax),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painterResource(id = R.drawable.splash),
                contentDescription = "Restaurant Logo",
                modifier = Modifier
                    .size(ProfilePictureSizeSmall)
                    .clip(RoundedCornerShape(SpaceMini))
            )

            Spacer(modifier = Modifier.width(SpaceMedium))

            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.restaurant_name),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(id = R.string.restaurant_slogan),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }


        IconButton(
            onClick = {
                navController.navigate(Screens.PROFILE_SCREEN)
            },
            modifier = Modifier.padding(end = SpaceSmall)
        ) {
            Icon(
                imageVector = Icons.Outlined.AccountCircle,
                contentDescription = "My Account",
                modifier = Modifier.size(IconSizeLarge),
                tint = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}

@Composable
fun DrawerItem(
    text: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    iconColor: Color = MaterialTheme.colorScheme.onSurface,
//    selectedColor: Color = MaterialTheme.colorScheme.secondary,
) {
    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = text.plus("Icon"),
                tint = iconColor
            )
        },
        label = { Text(text) },
        shape = RoundedCornerShape(SpaceMini),
        selected = selected,
        onClick = onClick,
        modifier = Modifier
            .height(48.dp)
            .padding(NavigationDrawerItemDefaults.ItemPadding)
    )
}

