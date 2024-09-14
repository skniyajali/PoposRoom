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

package com.niyaj.poposroom.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.plusAssign
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.niyaj.address.destinations.AddressDetailsScreenDestination
import com.niyaj.address.details.AddressDetailsScreen
import com.niyaj.cart.CartScreen
import com.niyaj.cart.destinations.CartScreenDestination
import com.niyaj.cartSelected.SelectOrderScreen
import com.niyaj.cartSelected.destinations.SelectOrderScreenDestination
import com.niyaj.cartorder.CartOrderScreen
import com.niyaj.cartorder.destinations.AddEditCartOrderScreenDestination
import com.niyaj.cartorder.destinations.CartOrderScreenDestination
import com.niyaj.customer.destinations.CustomerDetailsScreenDestination
import com.niyaj.customer.details.CustomerDetailsScreen
import com.niyaj.employee.destinations.AddEditEmployeeScreenDestination
import com.niyaj.employee.destinations.EmployeeDetailsScreenDestination
import com.niyaj.employee.details.EmployeeDetailsScreen
import com.niyaj.employeeAbsent.createOrUpdate.AddEditAbsentScreen
import com.niyaj.employeeAbsent.destinations.AddEditAbsentScreenDestination
import com.niyaj.employeePayment.PaymentScreen
import com.niyaj.employeePayment.destinations.AddEditPaymentScreenDestination
import com.niyaj.employeePayment.destinations.PaymentScreenDestination
import com.niyaj.feature.destinations.HomeScreenDestination
import com.niyaj.feature.home.HomeScreen
import com.niyaj.feature.reports.ReportScreen
import com.niyaj.feature.reports.destinations.ReportScreenDestination
import com.niyaj.order.OrderScreen
import com.niyaj.order.destinations.OrderDetailsScreenDestination
import com.niyaj.order.destinations.OrderScreenDestination
import com.niyaj.order.details.OrderDetailsScreen
import com.niyaj.poposroom.ui.PoposAppState
import com.niyaj.product.destinations.ProductDetailsScreenDestination
import com.niyaj.product.details.ProductDetailsScreen
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.NestedNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.manualcomposablecalls.bottomSheetComposable
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.scope.resultBackNavigator
import com.ramcosta.composedestinations.scope.resultRecipient
import com.ramcosta.composedestinations.spec.Route

/**
 *  Navigation controller
 *  @author Sk Niyaj Ali
 *  @param appState
 *  @param modifier
 *  @param startRoute
 */
@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalMaterialNavigationApi::class,
    ExperimentalAnimationApi::class,
)
@Composable
fun PoposNavHost(
    appState: PoposAppState,
    startRoute: Route,
    modifier: Modifier = Modifier,
) {
    val bottomSheetNavigator = appState.bottomSheetNavigator
    appState.navController.navigatorProvider += bottomSheetNavigator

    val navHostEngine = rememberAnimatedNavHostEngine(
        navHostContentAlignment = Alignment.TopCenter,
        // default `rootDefaultAnimations` means no animations
        rootDefaultAnimations = RootNavGraphDefaultAnimations.ACCOMPANIST_FADING,
        // all other nav graphs not specified in this map,
        // will get their animations from the `rootDefaultAnimations` above.
        defaultAnimationsForNestedNavGraph = mapOf(
            RootNavGraph to NestedNavGraphDefaultAnimations.ACCOMPANIST_FADING,
        ),
    )

    ModalBottomSheetLayout(
        modifier = modifier,
        bottomSheetNavigator = bottomSheetNavigator,
        // other configuration for you bottom sheet screens, like:
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    ) {
        DestinationsNavHost(
            engine = navHostEngine,
            modifier = Modifier,
            navController = appState.navController,
            navGraph = RootNavGraph,
            startRoute = startRoute,
            dependenciesContainerBuilder = {
                dependency(navController)
            },
            manualComposableCallsBuilder = {
                composable(CartOrderScreenDestination) {
                    CartOrderScreen(
                        navigator = destinationsNavigator,
                        onClickOrderDetails = {
                            navController.navigate(OrderDetailsScreenDestination(it))
                        },
                        resultRecipient = resultRecipient(),
                    )
                }

                composable(CartScreenDestination) {
                    CartScreen(
                        navigator = destinationsNavigator,
                        onClickEditOrder = {
                            navController.navigate(AddEditCartOrderScreenDestination(it))
                        },
                        onClickOrderDetails = {
                            navController.navigate(OrderDetailsScreenDestination(it))
                        },
                    )
                }

                composable(OrderScreenDestination) {
                    OrderScreen(
                        navigator = destinationsNavigator,
                        onClickEditOrder = {
                            navController.navigate(AddEditCartOrderScreenDestination(it))
                        },
                    )
                }

                bottomSheetComposable(SelectOrderScreenDestination) {
                    SelectOrderScreen(
                        navigator = destinationsNavigator,
                        onEditClick = {
                            navController.navigate(AddEditCartOrderScreenDestination(it))
                        },
                        resultBackNavigator = resultBackNavigator(),
                    )
                }

                composable(EmployeeDetailsScreenDestination) {
                    EmployeeDetailsScreen(
                        employeeId = navBackStackEntry.arguments?.getInt("employeeId") ?: 0,
                        navigator = destinationsNavigator,
                        onClickAddPayment = {
                            navController.navigate(AddEditPaymentScreenDestination(employeeId = it))
                        },
                        onClickAddAbsent = {
                            navController.navigate(AddEditAbsentScreenDestination(employeeId = it))
                        },
                        paymentRecipient = resultRecipient<AddEditPaymentScreenDestination, String>(),
                        absentRecipient = resultRecipient<AddEditAbsentScreenDestination, String>(),
                    )
                }

                composable(AddEditAbsentScreenDestination) {
                    AddEditAbsentScreen(
                        absentId = navBackStackEntry.arguments?.getInt("absentId") ?: 0,
                        employeeId = navBackStackEntry.arguments?.getInt("employeeId") ?: 0,
                        navigator = this.destinationsNavigator,
                        onClickAddEmployee = {
                            navController.navigate(AddEditEmployeeScreenDestination())
                        },
                        resultBackNavigator = resultBackNavigator(),
                    )
                }

                composable(AddressDetailsScreenDestination) {
                    AddressDetailsScreen(
                        addressId = navBackStackEntry.arguments?.getInt("addressId") ?: 0,
                        navigator = destinationsNavigator,
                        onClickOrder = {
                            navController.navigate(OrderDetailsScreenDestination(it))
                        },
                    )
                }

                composable(CustomerDetailsScreenDestination) {
                    CustomerDetailsScreen(
                        customerId = navBackStackEntry.arguments?.getInt("customerId") ?: 0,
                        navigator = destinationsNavigator,
                        onClickOrder = {
                            navController.navigate(OrderDetailsScreenDestination(it))
                        },
                    )
                }

                composable(ProductDetailsScreenDestination) {
                    ProductDetailsScreen(
                        productId = navBackStackEntry.arguments?.getInt("productId") ?: 0,
                        navigator = destinationsNavigator,
                        onClickOrder = {
                            navController.navigate(OrderDetailsScreenDestination(it))
                        },
                    )
                }

                composable(ReportScreenDestination) {
                    ReportScreen(
                        navigator = destinationsNavigator,
                        onClickAddress = {
                            navController.navigate(AddressDetailsScreenDestination(it))
                        },
                        onClickCustomer = {
                            navController.navigate(CustomerDetailsScreenDestination(it))
                        },
                        onClickProduct = {
                            navController.navigate(ProductDetailsScreenDestination(it))
                        },
                    )
                }

                composable(PaymentScreenDestination) {
                    PaymentScreen(
                        navigator = destinationsNavigator,
                        onClickEmployee = {
                            navController.navigate(EmployeeDetailsScreenDestination(it))
                        },
                        resultRecipient = resultRecipient(),
                        exportRecipient = resultRecipient(),
                        importRecipient = resultRecipient(),
                    )
                }

                composable(OrderDetailsScreenDestination) {
                    OrderDetailsScreen(
                        orderId = navBackStackEntry.arguments?.getInt("orderId") ?: 0,
                        navigator = destinationsNavigator,
                        onClickCustomer = {
                            navController.navigate(CustomerDetailsScreenDestination(it))
                        },
                        onClickAddress = {
                            navController.navigate(AddressDetailsScreenDestination(it))
                        },
                    )
                }

                composable(HomeScreenDestination) {
                    HomeScreen(
                        navigator = destinationsNavigator,
                        resultRecipient = resultRecipient<SelectOrderScreenDestination, String>(),
                    )
                }
            },
        )
    }
}
