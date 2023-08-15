package com.niyaj.printer_info.add_edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DensityMedium
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Margin
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ViewHeadline
import androidx.compose.material.icons.filled.WidthNormal
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.common.tags.PrinterInfoTestTags.ADDRESS_REPORT_LIMIT_FIELD
import com.niyaj.common.tags.PrinterInfoTestTags.ADDRESS_REPORT_LIMIT_MESSAGE
import com.niyaj.common.tags.PrinterInfoTestTags.CUSTOMER_REPORT_LIMIT_FIELD
import com.niyaj.common.tags.PrinterInfoTestTags.CUSTOMER_REPORT_LIMIT_MESSAGE
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_DPI_FIELD
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_DPI_MESSAGE
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_NBR_LINES_FIELD
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_NBR_LINES_MESSAGE
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_PRODUCT_NAME_LENGTH_FIELD
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_PRODUCT_NAME_LENGTH_MESSAGE
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_WIDTH_FIELD
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_WIDTH_MESSAGE
import com.niyaj.common.tags.PrinterInfoTestTags.PRINT_LOGO_IN_BILL
import com.niyaj.common.tags.PrinterInfoTestTags.PRINT_QR_CODE_IN_BILL
import com.niyaj.common.tags.PrinterInfoTestTags.PRINT_WELCOME_TEXT_IN_BILL
import com.niyaj.common.tags.PrinterInfoTestTags.PRODUCT_REPORT_LIMIT_FIELD
import com.niyaj.common.tags.PrinterInfoTestTags.PRODUCT_REPORT_LIMIT_MESSAGE
import com.niyaj.common.tags.PrinterInfoTestTags.UPDATE_PRINTER_BUTTON
import com.niyaj.common.tags.PrinterInfoTestTags.UPDATE_PRINTER_INFO
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.ui.components.StandardButton
import com.niyaj.ui.components.StandardCheckboxWithText
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.components.StandardScaffoldWithOutDrawer
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.UiEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator

@Destination(
    route = Screens.UpdatePrinterInfoScreen
)
@Composable
fun UpdatePrinterInfoScreen(
    navController: NavController,
    resultBackNavigator: ResultBackNavigator<String>,
    viewModel: UpdatePrinterInfoViewModel = hiltViewModel(),
) {
    val state = rememberLazyListState()

    val dpiError = viewModel.dpiError.collectAsStateWithLifecycle().value
    val widthError = viewModel.widthError.collectAsStateWithLifecycle().value
    val nbrError = viewModel.nbrError.collectAsStateWithLifecycle().value
    val nameLengthError = viewModel.nameLengthError.collectAsStateWithLifecycle().value
    val productLimitError = viewModel.productLimitError.collectAsStateWithLifecycle().value
    val addressLimitError = viewModel.addressLimitError.collectAsStateWithLifecycle().value
    val customerLimitError = viewModel.customerLimitError.collectAsStateWithLifecycle().value

    val hasError = viewModel.hasError.collectAsStateWithLifecycle().value

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.OnSuccess -> {
                    resultBackNavigator.navigateBack(event.successMessage)
                }

                is UiEvent.OnError -> {
                    resultBackNavigator.navigateBack(event.errorMessage)
                }

            }
        }
    }

    StandardScaffoldWithOutDrawer(
        title = UPDATE_PRINTER_INFO,
        onBackClick = {
            navController.navigateUp()
        },
        showBottomBar = !hasError,
        bottomBar = {
            StandardButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(UPDATE_PRINTER_BUTTON)
                    .padding(horizontal = SpaceSmallMax),
                enabled = !hasError,
                text = UPDATE_PRINTER_INFO,
                icon = Icons.Default.Edit,
                onClick = {
                    viewModel.onEvent(UpdatePrinterInfoEvent.UpdatePrinterInfo)
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
            state = state,
        ) {
            item {
                StandardOutlinedTextField(
                    value = viewModel.state.printerDpi.toString(),
                    leadingIcon = Icons.Default.DensityMedium,
                    label = PRINTER_DPI_FIELD,
                    isError = dpiError != null,
                    errorText = dpiError,
                    message = PRINTER_DPI_MESSAGE,
                    onValueChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.PrinterDpiChanged(it))
                    }
                )
            }

            item {
                StandardOutlinedTextField(
                    value = viewModel.state.printerWidth.toString(),
                    label = PRINTER_WIDTH_FIELD,
                    leadingIcon = Icons.Default.WidthNormal,
                    isError = widthError != null,
                    errorText = widthError,
                    message = PRINTER_WIDTH_MESSAGE,
                    onValueChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.PrinterWidthChanged(it))
                    }
                )
            }

            item {
                StandardOutlinedTextField(
                    value = viewModel.state.printerNbrLines.toString(),
                    label = PRINTER_NBR_LINES_FIELD,
                    leadingIcon = Icons.Default.ViewHeadline,
                    isError = nbrError != null,
                    errorText = nbrError,
                    message = PRINTER_NBR_LINES_MESSAGE,
                    onValueChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.PrinterNbrLinesChanged(it))
                    }
                )
            }

            item {
                StandardOutlinedTextField(
                    value = viewModel.state.productNameLength.toString(),
                    label = PRINTER_PRODUCT_NAME_LENGTH_FIELD,
                    leadingIcon = Icons.Default.Margin,
                    isError = nameLengthError != null,
                    errorText = nameLengthError,
                    message = PRINTER_PRODUCT_NAME_LENGTH_MESSAGE,
                    onValueChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.ProductNameLengthChanged(it))
                    }
                )
            }

            item {
                StandardOutlinedTextField(
                    value = viewModel.state.productWiseReportLimit.toString(),
                    label = PRODUCT_REPORT_LIMIT_FIELD,
                    leadingIcon = Icons.Default.ReceiptLong,
                    isError = productLimitError != null,
                    errorText = productLimitError,
                    message = PRODUCT_REPORT_LIMIT_MESSAGE,
                    onValueChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.ProductReportLimitChanged(it))
                    }
                )
            }

            item {
                StandardOutlinedTextField(
                    value = viewModel.state.addressWiseReportLimit.toString(),
                    label = ADDRESS_REPORT_LIMIT_FIELD,
                    leadingIcon = Icons.Default.Receipt,
                    isError = addressLimitError != null,
                    errorText = addressLimitError,
                    message = ADDRESS_REPORT_LIMIT_MESSAGE,
                    onValueChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.AddressReportLimitChanged(it))
                    }
                )
            }

            item {
                StandardOutlinedTextField(
                    value = viewModel.state.customerWiseReportLimit.toString(),
                    label = CUSTOMER_REPORT_LIMIT_FIELD,
                    leadingIcon = Icons.Default.Receipt,
                    isError = customerLimitError != null,
                    errorText = customerLimitError,
                    message = CUSTOMER_REPORT_LIMIT_MESSAGE,
                    onValueChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.CustomerReportLimitChanged(it))
                    }
                )
            }

            item {
                StandardCheckboxWithText(
                    text = PRINT_QR_CODE_IN_BILL,
                    checked = viewModel.state.printQRCode,
                    onCheckedChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.PrintQrCodeChanged)
                    }
                )
            }

            item {
                StandardCheckboxWithText(
                    text = PRINT_LOGO_IN_BILL,
                    checked = viewModel.state.printResLogo,
                    onCheckedChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.PrintResLogoChanged)
                    }
                )
            }

            item {
                StandardCheckboxWithText(
                    text = PRINT_WELCOME_TEXT_IN_BILL,
                    checked = viewModel.state.printWelcomeText,
                    onCheckedChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.PrintWelcomeTextChanged)
                    }
                )
            }
        }
    }
}