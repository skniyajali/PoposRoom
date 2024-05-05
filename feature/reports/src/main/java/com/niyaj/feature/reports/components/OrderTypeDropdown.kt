package com.niyaj.feature.reports.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.model.OrderType
import com.niyaj.ui.components.StandardOutlinedAssistChip

@Composable
fun OrderTypeDropdown(
    text: String,
    onItemClick: (String) -> Unit = {},
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Column {
        StandardOutlinedAssistChip(
            text = text,
            icon = PoposIcons.CalenderMonth,
            onClick = {
                menuExpanded = !menuExpanded
            },
            trailingIcon = PoposIcons.ArrowDropDown,
        )

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = "All",
                        style = MaterialTheme.typography.labelSmall,
                    )
                },
                onClick = {
                    onItemClick("")
                    menuExpanded = false
                },
            )

            DropdownMenuItem(
                text = {
                    Text(
                        text = "DineIn",
                        style = MaterialTheme.typography.labelSmall,
                    )
                },
                onClick = {
                    onItemClick(OrderType.DineIn.name)
                    menuExpanded = false
                },
            )
            DropdownMenuItem(
                text = {
                    Text(
                        text = "DineOut",
                        style = MaterialTheme.typography.labelSmall,
                    )
                },
                onClick = {
                    onItemClick(OrderType.DineOut.name)
                    menuExpanded = false
                },
            )
        }
    }
}