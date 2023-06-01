package com.niyaj.poposroom.features.common.utils

import androidx.compose.runtime.Composable
import com.niyaj.poposroom.features.addon_item.presentation.add_edit.AddEditItemScreen

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
                AddEditItemScreen(closeSheet = onCloseBottomSheet)
            }

            is SheetScreen.UpdateAddOnItem -> {
                AddEditItemScreen(addOnItemId = current.itemId, closeSheet = onCloseBottomSheet)
            }
        }
    }
}