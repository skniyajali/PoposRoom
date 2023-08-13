package com.niyaj.poposroom.navigation

import androidx.compose.ui.ExperimentalComposeUiApi
import com.niyaj.addonitem.AddonitemNavGraph
import com.niyaj.address.AddressNavGraph
import com.niyaj.cart.CartNavGraph
import com.niyaj.cart_selected.CartselectedNavGraph
import com.niyaj.cartorder.CartorderNavGraph
import com.niyaj.category.CategoryNavGraph
import com.niyaj.charges.ChargesNavGraph
import com.niyaj.customer.CustomerNavGraph
import com.niyaj.employee.EmployeeNavGraph
import com.niyaj.employee_absent.EmployeeabsentNavGraph
import com.niyaj.employee_payment.EmployeepaymentNavGraph
import com.niyaj.expenses.ExpensesNavGraph
import com.niyaj.feature.reports.ReportsNavGraph
import com.niyaj.home.HomeNavGraph
import com.niyaj.order.OrderNavGraph
import com.niyaj.printer_info.PrinterinfoNavGraph
import com.niyaj.product.ProductNavGraph
import com.niyaj.profile.ProfileNavGraph
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.spec.NavGraphSpec

@ExperimentalComposeUiApi
object RootNavGraph : NavGraphSpec {

    override val route = "root"

    override val destinationsByRoute = emptyMap<String, DestinationSpec<*>>()

    override val startRoute = HomeNavGraph

    override val nestedNavGraphs = listOf(
        AddonitemNavGraph,
        AddressNavGraph,
        CartNavGraph,
        CartselectedNavGraph,
        CartorderNavGraph,
        CategoryNavGraph,
        ChargesNavGraph,
        CustomerNavGraph,
        EmployeeNavGraph,
        EmployeeabsentNavGraph,
        EmployeepaymentNavGraph,
        ExpensesNavGraph,
        HomeNavGraph,
        OrderNavGraph,
        ProductNavGraph,
        ProfileNavGraph,
        PrinterinfoNavGraph,
        ReportsNavGraph,
    )
}