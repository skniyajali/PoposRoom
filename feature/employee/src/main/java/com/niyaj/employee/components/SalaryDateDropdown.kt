package com.niyaj.employee.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.niyaj.common.utils.toYearAndMonth
import com.niyaj.model.EmployeeMonthlyDate
import com.niyaj.ui.components.StandardOutlinedAssistChip

@Composable
fun SalaryDateDropdown(
    text: String,
    salaryDates: List<EmployeeMonthlyDate> = emptyList(),
    onDateClick: (Pair<String, String>) -> Unit = {},
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Column {
        StandardOutlinedAssistChip(
            text = text,
            icon = Icons.Default.CalendarMonth,
            onClick = {
                menuExpanded = !menuExpanded
            },
            trailingIcon = Icons.Default.ArrowDropDown
        )

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false }
        ) {
            salaryDates.forEach { date ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = date.startDate.toYearAndMonth,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    },
                    onClick = {
                        onDateClick(Pair(date.startDate, date.endDate))
                        menuExpanded = false
                    }
                )
            }
        }
    }
}