package com.niyaj.poposroom.features.common.navigation

sealed class Screen(val route: String) {

    object SplashScreen: Screen("splash_screen")

    object LoginScreen : Screen("login_screen")

    object MainFeedScreen : Screen("main_feed_screen")

    object ProfileScreen : Screen("profile_screen")

    object CartScreen : Screen("cart_screen")

    object OrderScreen : Screen("order_screen")

    object OrderDetailsScreen : Screen("order_details_screen")

    object NotificationScreen: Screen("notification")

    object SearchScreen : Screen("search_screen")

    object SettingsScreen : Screen("settings_screen")

    object PrintSettingsScreen : Screen("print_settings_screen")

    object CategoryScreen : Screen("category_screen")

    object ProductsScreen : Screen("products_screen")

    object AddressScreen : Screen("address_screen")

    object CustomerScreen : Screen("customer_screen")

    object CartOrderScreen : Screen("cart_order_screen")

    object CartOrderSettingsScreen : Screen("cart_order_settings_screen")

    object AddOnItemScreen : Screen("add_on_items_screen")

    object ChargesScreen : Screen("charges_screen")

    object PartnerScreen : Screen("partner_screen")

    object AddEditPartnerScreen : Screen("add_edit_partner_screen")

    object EmployeeScreen : Screen("employee_screen")

    object AddEditEmployeeScreen : Screen("add_edit_employee_screen")

    object ExpensesCategoryScreen : Screen("expenses_category_screen")

    object ExpensesSubScreen : Screen("expenses_sub_category_screen")

    object ExpensesScreen : Screen("expenses_screen")

    object AddExpensesScreen : Screen("add_expenses_screen")

    object EditExpensesScreen : Screen("edit_expenses_screen")

    object ReportScreen : Screen("report_screen")

    object ViewLastSevenDaysReports: Screen("view_last_seven_days_reports")

}