package com.niyaj.expenses.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.FabPosition
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_SETTINGS_TITLE
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.expenses.destinations.ExpensesExportScreenDestination
import com.niyaj.expenses.destinations.ExpensesImportScreenDestination
import com.niyaj.ui.components.ScrollToTop
import com.niyaj.ui.components.SettingsCard
import com.niyaj.ui.components.StandardScaffoldNew
import com.niyaj.ui.utils.isScrollingUp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch

@Destination
@Composable
fun ExpensesSettingsScreen(
    navController: NavController,
    exportRecipient: ResultRecipient<ExpensesExportScreenDestination, String>,
    importRecipient: ResultRecipient<ExpensesImportScreenDestination, String>,
) {
    val snackbarState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()


    exportRecipient.onNavResult { result ->
        when(result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    snackbarState.showSnackbar(result.value)
                }
            }
        }
    }
    importRecipient.onNavResult { result ->
        when(result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    snackbarState.showSnackbar(result.value)
                }
            }
        }
    }

    StandardScaffoldNew(
        navController = navController,
        title = EXPENSE_SETTINGS_TITLE,
        snackbarHostState = snackbarState,
        showBackButton = true,
        showBottomBar = false,
        fabPosition = FabPosition.End,
        floatingActionButton = {
            ScrollToTop(
                visible = !lazyListState.isScrollingUp(),
                onClick = {
                    scope.launch {
                        lazyListState.animateScrollToItem(index = 0)
                    }
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpaceSmall),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(SpaceSmall)
        ){
            item("ImportExpenses") {
                SettingsCard(
                    title = "Import Expenses",
                    subtitle = "Click here to import data from file.",
                    icon = Icons.Default.SaveAlt,
                    onClick = {
                        navController.navigate(ExpensesImportScreenDestination())
                    }
                )
            }

            item("ExportExpense") {
                SettingsCard(
                    title = "Export Expenses",
                    subtitle = "Click here to export expenses to file.",
                    icon = Icons.Default.Upload,
                    onClick = {
                        navController.navigate(ExpensesExportScreenDestination())
                    }
                )
            }
        }
    }
}