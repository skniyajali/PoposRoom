package com.niyaj.poposroom.features.common.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInbox
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BreakfastDining
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person4
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.niyaj.poposroom.features.common.ui.theme.SpaceMini
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
import com.niyaj.poposroom.features.destinations.PaymentScreenDestination
import com.niyaj.poposroom.features.destinations.ProductScreenDestination
import com.niyaj.poposroom.features.destinations.SelectOrderScreenDestination
import com.ramcosta.composedestinations.navigation.navigate

@Composable
fun DrawerContent(
    navController: NavController,
    onCloseClick: () -> Unit
) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    ModalDrawerSheet {

        Spacer(Modifier.height(12.dp))

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Home") },
            shape = RoundedCornerShape(SpaceMini),
            selected = currentRoute == MainFeedScreenDestination.route,
            onClick = {
                onCloseClick()
                navController.navigate(MainFeedScreenDestination())
            },
            modifier = Modifier
                .padding(NavigationDrawerItemDefaults.ItemPadding)
                .height(48.dp)
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Link, contentDescription = null) },
            label = { Text("AddOnItem") },
            shape = RoundedCornerShape(SpaceMini),
            selected = currentRoute == AddOnItemScreenDestination.route,
            onClick = {
                onCloseClick()
                navController.navigate(AddOnItemScreenDestination())
            },
            modifier = Modifier
                .height(48.dp)
                .padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Business, contentDescription = null) },
            label = { Text("Address") },
            shape = RoundedCornerShape(SpaceMini),
            selected = currentRoute == AddressScreenDestination.route,
            onClick = {
                onCloseClick()
                navController.navigate(AddressScreenDestination())
            },
            modifier = Modifier
                .height(48.dp)
                .padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Bolt, contentDescription = null) },
            label = { Text("Charges") },
            shape = RoundedCornerShape(SpaceMini),
            selected = currentRoute == ChargesScreenDestination.route,
            onClick = {
                onCloseClick()
                navController.navigate(ChargesScreenDestination())
            },
            modifier = Modifier
                .height(48.dp)
                .padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Category, contentDescription = null) },
            label = { Text("Category") },
            shape = RoundedCornerShape(SpaceMini),
            selected = currentRoute == CategoryScreenDestination.route,
            onClick = {
                onCloseClick()
                navController.navigate(CategoryScreenDestination())
            },
            modifier = Modifier
                .height(48.dp)
                .padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.People, contentDescription = null) },
            label = { Text("Customer") },
            shape = RoundedCornerShape(SpaceMini),
            selected = currentRoute == CustomerScreenDestination.route,
            onClick = {
                onCloseClick()
                navController.navigate(CustomerScreenDestination())
            },
            modifier = Modifier
                .height(48.dp)
                .padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Person4, contentDescription = null) },
            label = { Text("Employee") },
            shape = RoundedCornerShape(SpaceMini),
            selected = currentRoute == EmployeeScreenDestination.route,
            onClick = {
                navController.navigate(EmployeeScreenDestination())
            },
            modifier = Modifier
                .height(48.dp)
                .padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Payments, contentDescription = null) },
            label = { Text("Payments") },
            shape = RoundedCornerShape(SpaceMini),
            selected = currentRoute == PaymentScreenDestination.route,
            onClick = {
                navController.navigate(PaymentScreenDestination())
            },
            modifier = Modifier
                .height(48.dp)
                .padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
            label = { Text("Absents") },
            shape = RoundedCornerShape(SpaceMini),
            selected = currentRoute == AbsentScreenDestination.route,
            onClick = {
                navController.navigate(AbsentScreenDestination())
            },
            modifier = Modifier
                .height(48.dp)
                .padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.StickyNote2, contentDescription = null) },
            label = { Text("Expenses") },
            shape = RoundedCornerShape(SpaceMini),
            selected = currentRoute == ExpensesScreenDestination.route,
            onClick = {
                navController.navigate(ExpensesScreenDestination())
            },
            modifier = Modifier
                .height(48.dp)
                .padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.AllInbox, contentDescription = null) },
            label = { Text("Products") },
            shape = RoundedCornerShape(SpaceMini),
            selected = currentRoute == ProductScreenDestination.route,
            onClick = {
                navController.navigate(ProductScreenDestination())
            },
            modifier = Modifier
                .height(48.dp)
                .padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.BreakfastDining, contentDescription = null) },
            label = { Text("Cart Orders") },
            shape = RoundedCornerShape(SpaceMini),
            selected = currentRoute == CartOrderScreenDestination.route,
            onClick = {
                navController.navigate(CartOrderScreenDestination())
            },
            modifier = Modifier
                .height(48.dp)
                .padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.BreakfastDining, contentDescription = null) },
            label = { Text("Selected Orders") },
            shape = RoundedCornerShape(SpaceMini),
            selected = currentRoute == SelectOrderScreenDestination.route,
            onClick = {
                navController.navigate(SelectOrderScreenDestination())
            },
            modifier = Modifier
                .height(48.dp)
                .padding(NavigationDrawerItemDefaults.ItemPadding)
        )

    }
}