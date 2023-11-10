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
import com.niyaj.cart_selected.SelectOrderScreen
import com.niyaj.cart_selected.destinations.SelectOrderScreenDestination
import com.niyaj.cartorder.CartOrderScreen
import com.niyaj.cartorder.destinations.AddEditCartOrderScreenDestination
import com.niyaj.cartorder.destinations.CartOrderScreenDestination
import com.niyaj.customer.destinations.CustomerDetailsScreenDestination
import com.niyaj.customer.details.CustomerDetailsScreen
import com.niyaj.employee.destinations.EmployeeDetailsScreenDestination
import com.niyaj.employee.details.EmployeeDetailsScreen
import com.niyaj.employee_absent.destinations.AddEditAbsentScreenDestination
import com.niyaj.employee_payment.PaymentScreen
import com.niyaj.employee_payment.destinations.AddEditPaymentScreenDestination
import com.niyaj.employee_payment.destinations.PaymentScreenDestination
import com.niyaj.feature.reports.ReportScreen
import com.niyaj.feature.reports.destinations.ReportScreenDestination
import com.niyaj.order.OrderScreen
import com.niyaj.order.destinations.OrderDetailsScreenDestination
import com.niyaj.order.destinations.OrderScreenDestination
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
import com.ramcosta.composedestinations.navigation.navigate
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
    ExperimentalComposeUiApi::class, ExperimentalMaterialNavigationApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun PoposNavHost(
    appState: PoposAppState,
    modifier: Modifier = Modifier,
    startRoute: Route,
) {
    val bottomSheetNavigator = appState.bottomSheetNavigator
    appState.navController.navigatorProvider += bottomSheetNavigator


    val navHostEngine = rememberAnimatedNavHostEngine(
        navHostContentAlignment = Alignment.TopCenter,
        //default `rootDefaultAnimations` means no animations
        rootDefaultAnimations = RootNavGraphDefaultAnimations.ACCOMPANIST_FADING,
        // all other nav graphs not specified in this map,
        // will get their animations from the `rootDefaultAnimations` above.
        defaultAnimationsForNestedNavGraph = mapOf(
            RootNavGraph to NestedNavGraphDefaultAnimations.ACCOMPANIST_FADING
        )
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
                        navController = navController,
                        onClickOrderDetails = {
                            navController.navigate(OrderDetailsScreenDestination(it))
                        },
                        resultRecipient = resultRecipient(),
                    )
                }

                composable(CartScreenDestination) {
                    CartScreen(
                        navController = navController,
                        onClickEditOrder = {
                            navController.navigate(AddEditCartOrderScreenDestination(it))
                        },
                        onClickOrderDetails = {
                            navController.navigate(OrderDetailsScreenDestination(it))
                        },
                        onNavigateToOrderScreen = {
                            navController.navigate(OrderScreenDestination())
                        }
                    )
                }

                composable(OrderScreenDestination) {
                    OrderScreen(
                        navController = navController,
                        onClickEditOrder = {
                            navController.navigate(AddEditCartOrderScreenDestination(it))
                        }
                    )
                }

                bottomSheetComposable(SelectOrderScreenDestination) {
                    SelectOrderScreen(
                        navController = navController,
                        onEditClick = {
                            navController.navigate(AddEditCartOrderScreenDestination(it))
                        },
                        resultBackNavigator = resultBackNavigator()
                    )
                }

                composable(EmployeeDetailsScreenDestination) {
                    EmployeeDetailsScreen(
                        navController = navController,
                        onClickAddPayment = {
                            navController.navigate(AddEditPaymentScreenDestination(employeeId = it))
                        },
                        onClickAddAbsent = {
                            navController.navigate(AddEditAbsentScreenDestination(employeeId = it))
                        }
                    )
                }

                composable(AddressDetailsScreenDestination) {
                    AddressDetailsScreen(
                        navController = navController,
                        onClickOrder = {
                            navController.navigate(OrderDetailsScreenDestination(it))
                        }
                    )
                }

                composable(CustomerDetailsScreenDestination) {
                    CustomerDetailsScreen(
                        navController = navController,
                        onClickOrder = {
                            navController.navigate(OrderDetailsScreenDestination(it))
                        }
                    )
                }

                composable(ProductDetailsScreenDestination) {
                    ProductDetailsScreen(
                        navController = navController,
                        onClickOrder = {
                            navController.navigate(OrderDetailsScreenDestination(it))
                        }
                    )
                }

                composable(ReportScreenDestination) {
                    ReportScreen(
                        navController = navController,
                        onClickAddress = {
                            navController.navigate(AddressDetailsScreenDestination(it))
                        },
                        onClickCustomer = {
                            navController.navigate(CustomerDetailsScreenDestination(it))
                        },
                        onClickProduct = {
                            navController.navigate(ProductDetailsScreenDestination(it))
                        }
                    )
                }

                composable(PaymentScreenDestination) {
                    PaymentScreen(
                        navController = navController,
                        resultRecipient = resultRecipient(),
                        onClickEmployee = {
                            navController.navigate(EmployeeDetailsScreenDestination(it))
                        }
                    )
                }
            }
        )
    }
}