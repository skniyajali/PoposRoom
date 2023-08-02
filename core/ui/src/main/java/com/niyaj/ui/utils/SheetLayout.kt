package com.niyaj.ui.utils

import androidx.compose.runtime.Composable

@Composable
fun SheetLayout(
    current: SheetScreen,
    onCloseBottomSheet: () -> Unit,
) {
    BottomSheetWithCloseDialog(
        text = current.type,
        onClosePressed = onCloseBottomSheet
    ) {
        when (current) {
            is SheetScreen.CreateNewAddOnItem -> {
//                AddEditItemScreen(closeSheet = onCloseBottomSheet)
            }

            is SheetScreen.UpdateAddOnItem -> {
//                AddEditItemScreen(addOnItemId = current.itemId, closeSheet = onCloseBottomSheet)
            }

            is SheetScreen.CreateNewAddress -> {
//                AddEditAddressScreen(closeSheet = onCloseBottomSheet)
            }

            is SheetScreen.UpdateAddress -> {
//                AddEditAddressScreen(
//                    addressId = current.addressId,
//                    closeSheet = onCloseBottomSheet
//                )
            }

            is SheetScreen.CreateNewCharges -> {
//                AddEditChargesScreen(closeSheet = onCloseBottomSheet)
            }

            is SheetScreen.UpdateCharges -> {
//                AddEditChargesScreen(
//                    chargesId = current.chargesId,
//                    closeSheet = onCloseBottomSheet
//                )
            }

            is SheetScreen.CreateNewCategory -> {
//                AddEditCategoryScreen(closeSheet = onCloseBottomSheet)
            }

            is SheetScreen.UpdateCategory -> {
//                AddEditCategoryScreen(
//                    categoryId = current.categoryId,
//                    closeSheet = onCloseBottomSheet
//                )
            }

            is SheetScreen.CreateNewCustomer -> {
//                AddEditCustomerScreen(closeSheet = onCloseBottomSheet)
            }

            is SheetScreen.UpdateCustomer -> {
//                AddEditCustomerScreen(
//                    customerId = current.customerId,
//                    closeSheet = onCloseBottomSheet
//                )
            }
        }
    }
}