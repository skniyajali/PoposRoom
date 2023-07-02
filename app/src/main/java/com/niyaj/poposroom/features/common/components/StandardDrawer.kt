package com.niyaj.poposroom.features.common.components

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
import androidx.compose.material.icons.filled.AllInbox
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Badge
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
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Divider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.niyaj.poposroom.R
import com.niyaj.poposroom.features.common.ui.theme.IconSizeLarge
import com.niyaj.poposroom.features.common.ui.theme.IconSizeSmall
import com.niyaj.poposroom.features.common.ui.theme.LightColor12
import com.niyaj.poposroom.features.common.ui.theme.ProfilePictureSizeMedium
import com.niyaj.poposroom.features.common.ui.theme.SpaceLarge
import com.niyaj.poposroom.features.common.ui.theme.SpaceMedium
import com.niyaj.poposroom.features.common.ui.theme.SpaceMini
import com.niyaj.poposroom.features.common.ui.theme.SpaceSmall
import com.niyaj.poposroom.features.destinations.AbsentScreenDestination
import com.niyaj.poposroom.features.destinations.AddOnItemScreenDestination
import com.niyaj.poposroom.features.destinations.AddressScreenDestination
import com.niyaj.poposroom.features.destinations.CartOrderScreenDestination
import com.niyaj.poposroom.features.destinations.CategoryScreenDestination
import com.niyaj.poposroom.features.destinations.ChargesScreenDestination
import com.niyaj.poposroom.features.destinations.CustomerScreenDestination
import com.niyaj.poposroom.features.destinations.EmployeeScreenDestination
import com.niyaj.poposroom.features.destinations.ExpensesScreenDestination
import com.niyaj.poposroom.features.destinations.MainFeedScreenDestination
import com.niyaj.poposroom.features.destinations.OrderScreenDestination
import com.niyaj.poposroom.features.destinations.PaymentScreenDestination
import com.niyaj.poposroom.features.destinations.ProductScreenDestination
import com.ramcosta.composedestinations.navigation.navigate


@Composable
fun StandardDrawer(
    navController: NavController,
    onCloseClick: () -> Unit
) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route
    val expanded = remember { mutableStateOf(false) }
    val settingsExpanded = remember { mutableStateOf(false) }
    val employeeExpanded = remember { mutableStateOf(false) }
    val customersExpanded = remember { mutableStateOf(false) }
    val ordersExpanded = remember { mutableStateOf(false) }

    ModalDrawerSheet {
        Column(
            modifier = Modifier.weight(0.3f)
        ) {
            Spacer(modifier = Modifier.height(SpaceSmall))

            DrawerHeader(navController)

            Spacer(modifier = Modifier.height(SpaceLarge))

            Divider(thickness = 1.dp, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(SpaceMini))
        }

        LazyColumn(
            modifier = Modifier
                .weight(2.5f)
        ) {
            item {
                Spacer(modifier = Modifier.height(SpaceMedium))

                DrawerItem(
                    text = "Home",
                    icon = Icons.Default.Home,
                    selected = currentRoute == MainFeedScreenDestination.route,
                    onClick = {
                        navController.navigate(MainFeedScreenDestination())
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(SpaceSmall))

                DrawerItem(
                    text = "View Orders",
                    icon = Icons.Default.Inventory,
                    selected = currentRoute == OrderScreenDestination.route,
                    onClick = {
                        navController.navigate(OrderScreenDestination())
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(SpaceSmall))

                DrawerItem(
                    text = "View Reports",
                    icon = Icons.Default.Assessment,
                    selected = false, // currentRoute == OrderScreenDestination.route,
                    onClick = {
//                        navController.navigate(OrderScreenDestination())
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(SpaceSmall))

                DrawerItem(
                    text = "View Expenses",
                    icon = Icons.Default.StickyNote2,
                    selected = currentRoute == ExpensesScreenDestination.route,
                    onClick = {
                        navController.navigate(ExpensesScreenDestination())
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(SpaceMedium))

                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LightColor12)
                )

                Spacer(modifier = Modifier.height(SpaceSmall))


                StandardExpandable(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = IconSizeSmall),
                    dividerModifier = Modifier.padding(end = IconSizeSmall),
                    expanded = ordersExpanded.value,
                    onExpandChanged = {
                        ordersExpanded.value = it
                    },
                    title = {
                        Text(text = "Orders, Cart Orders..")
                    },
                    leading = {
                        Icon(
                            imageVector = Icons.Default.AllInbox,
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
                            modifier = Modifier.padding(SpaceSmall),
                        ) {
                            DrawerItem(
                                text = "Orders",
                                icon = Icons.Default.Inventory,
                                selected = currentRoute == OrderScreenDestination.route,
                                iconColor = MaterialTheme.colorScheme.secondary,
                                onClick = {
                                    navController.navigate(OrderScreenDestination())
                                }
                            )
                            Spacer(modifier = Modifier.height(SpaceSmall))
                            DrawerItem(
                                text = "Cart Orders",
                                icon = Icons.Default.BreakfastDining,
                                selected = currentRoute == CartOrderScreenDestination.route,
                                iconColor = MaterialTheme.colorScheme.secondary,
                                onClick = {
                                    navController.navigate(CartOrderScreenDestination())
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
                    expanded = customersExpanded.value,
                    onExpandChanged = {
                        customersExpanded.value = it
                    },
                    title = {
                        Text(text = "Customers, Addresses")
                    },
                    leading = {
                        Icon(
                            imageVector = Icons.Default.Badge,
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
                                icon = Icons.Default.PeopleAlt,
                                selected = currentRoute == CustomerScreenDestination.route,
                                iconColor = MaterialTheme.colorScheme.secondary,
                                onClick = {
                                    navController.navigate(CustomerScreenDestination())
                                }
                            )

                            Spacer(modifier = Modifier.height(SpaceSmall))

                            DrawerItem(
                                text = "Addresses",
                                icon = Icons.Default.Business,
                                selected = currentRoute == AddressScreenDestination.route,
                                iconColor = MaterialTheme.colorScheme.secondary,
                                onClick = {
                                    navController.navigate(AddressScreenDestination())
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
                    expanded = employeeExpanded.value,
                    onExpandChanged = {
                        employeeExpanded.value = it
                    },
                    title = {
                        Text(text = "Employee, Salary, Advance")
                    },
                    leading = {
                        Icon(
                            imageVector = Icons.Default.PeopleAlt,
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
                                icon = Icons.Default.SwitchAccount,
                                selected = currentRoute == EmployeeScreenDestination.route,
                                iconColor = MaterialTheme.colorScheme.secondary,
                                onClick = {
                                    navController.navigate(EmployeeScreenDestination())
                                }
                            )

                            Spacer(modifier = Modifier.height(SpaceSmall))

                            DrawerItem(
                                text = "Employee Absent Report",
                                icon = Icons.Default.EventBusy,
                                selected = currentRoute == AbsentScreenDestination.route,
                                iconColor = MaterialTheme.colorScheme.secondary,
                                onClick = {
                                    navController.navigate(AbsentScreenDestination())
                                }
                            )

                            Spacer(modifier = Modifier.height(SpaceSmall))

                            DrawerItem(
                                text = "Employee Payments",
                                icon = Icons.Default.Money,
                                selected = currentRoute == PaymentScreenDestination.route,
                                iconColor = MaterialTheme.colorScheme.secondary,
                                onClick = {
                                    navController.navigate(PaymentScreenDestination())
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
                    expanded = expanded.value,
                    onExpandChanged = {
                        expanded.value = it
                    },
                    title = {
                        Text(text = "Products, Categories..")
                    },
                    leading = {
                        Icon(
                            imageVector = Icons.Default.Widgets,
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
                                icon = Icons.Default.Category,
                                selected = currentRoute == CategoryScreenDestination.route,
                                iconColor = MaterialTheme.colorScheme.secondary,
                                onClick = {
                                    navController.navigate(CategoryScreenDestination())
                                }
                            )

                            Spacer(modifier = Modifier.height(SpaceSmall))

                            DrawerItem(
                                text = "Products",
                                icon = Icons.Default.Dns,
                                selected = currentRoute == ProductScreenDestination.route,
                                iconColor = MaterialTheme.colorScheme.secondary,
                                onClick = {
                                    navController.navigate(ProductScreenDestination())
                                }
                            )

                            Spacer(modifier = Modifier.height(SpaceSmall))

                            DrawerItem(
                                text = "AddOn Item",
                                icon = Icons.Default.InsertLink,
                                selected = currentRoute == AddOnItemScreenDestination.route,
                                iconColor = MaterialTheme.colorScheme.secondary,
                                onClick = {
                                    navController.navigate(AddOnItemScreenDestination())
                                }
                            )

                            Spacer(modifier = Modifier.height(SpaceSmall))

                            DrawerItem(
                                text = "Charges Item",
                                icon = Icons.Default.Bolt,
                                selected = currentRoute == ChargesScreenDestination.route,
                                iconColor = MaterialTheme.colorScheme.secondary,
                                onClick = {
                                    navController.navigate(ChargesScreenDestination())
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
                            imageVector = Icons.Default.Settings,
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
                                icon = Icons.Default.Notifications,
                                selected = false, // currentRoute == ReminderScreenDestination.route,
                                iconColor = MaterialTheme.colorScheme.secondary,
                                onClick = {
//                                    navController.navigate(ReminderScreenDestination())
                                }
                            )
                            Spacer(modifier = Modifier.height(SpaceSmall))
                            DrawerItem(
                                text = "App Settings",
                                icon = Icons.Default.Settings,
                                selected = false, // currentRoute == SettingsScreenDestination.route,
                                iconColor = MaterialTheme.colorScheme.secondary,
                                onClick = {
//                                    navController.navigate(SettingsScreenDestination())
                                }
                            )
                            Spacer(modifier = Modifier.height(SpaceSmall))

                            DrawerItem(
                                text = "Print Settings",
                                icon = Icons.Default.Print,
                                selected = false,
                                iconColor = MaterialTheme.colorScheme.secondary,
                                onClick = {
//                                    navController.navigate(PrintSettingsScreenDestination())
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
            Divider(thickness = 1.dp, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(SpaceSmall))

            DrawerItem(
                text = "Logout",
                icon = Icons.Default.Logout,
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
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painterResource(id = R.drawable.popos),
                contentDescription = null,
                modifier = Modifier.size(ProfilePictureSizeMedium)
            )

            Spacer(modifier = Modifier.width(SpaceMedium))

            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.restaurant_name),
                    style = MaterialTheme.typography.titleMedium,
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
//                navController.navigate()
            },
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

