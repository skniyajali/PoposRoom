package com.niyaj.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.model.AddOnItem

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CartAddOnItems(
    modifier: Modifier = Modifier,
    addOnItems: List<AddOnItem> = emptyList(),
    selectedAddOnItem: List<Int> = emptyList(),
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    selectedColor: Color = MaterialTheme.colorScheme.tertiary,
    onClick: (Int) -> Unit = {},
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor),
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.Center
    ) {
        for (addOnItem in addOnItems) {
            StandardRoundedFilterChip(
                modifier = Modifier
                    .padding(SpaceMini),
                text = addOnItem.itemName,
//                    secondaryText = if(addOnItem.itemName.startsWith("Cold")) addOnItem.itemPrice.toString() else null,
                selected = selectedAddOnItem.contains(addOnItem.itemId),
                selectedColor = selectedColor,
                onClick = {
                    onClick(addOnItem.itemId)
                }
            )
            Spacer(modifier = Modifier.width(SpaceMini))
        }
    }
}