package com.niyaj.cart.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.model.AddOnItem
import com.niyaj.ui.components.StandardFilterChip

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CartAddOnItems(
    addOnItems: List<AddOnItem> = emptyList(),
    selectedAddOnItem: List<Int> = emptyList(),
    onClick: (Int) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FlowRow {
            for (addOnItem in addOnItems) {
                StandardFilterChip(
                    modifier = Modifier
                        .padding(SpaceMini),
                    text = addOnItem.itemName,
//                    secondaryText = if(addOnItem.itemName.startsWith("Cold")) addOnItem.itemPrice.toString() else null,
                    selected = selectedAddOnItem.contains(addOnItem.itemId),
                    onClick = {
                        onClick(addOnItem.itemId)
                    }
                )
                Spacer(modifier = Modifier.width(SpaceMini))
            }
        }
    }
}