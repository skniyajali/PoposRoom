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
import com.niyaj.model.Charges

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CartChargesItem(
    modifier: Modifier = Modifier,
    chargesList: List<Charges> = emptyList(),
    selectedItem: List<Int> = emptyList(),
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    onClick: (Int) -> Unit = {},
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor),
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.Center
    ) {
        for (charges in chargesList) {
            StandardRoundedFilterChip(
                modifier = Modifier
                    .padding(SpaceMini),
                label = charges.chargesName,
                selected = selectedItem.contains(charges.chargesId),
                onClick = {
                    onClick(charges.chargesId)
                }
            )
            Spacer(modifier = Modifier.width(SpaceMini))
        }
    }
}