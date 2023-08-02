package com.niyaj.ui.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.ui.graphics.vector.ImageVector

sealed class PaymentStatus(val status: String, val icon: ImageVector, val order: Int) {
    
    data object NotPaid : PaymentStatus(status = "Not Paid", icon = Icons.Default.Close, order = 1)

    data object Absent : PaymentStatus(status = "Absent", icon = Icons.Default.EventBusy, order = 2)

    data object Paid : PaymentStatus(status = "Paid", icon = Icons.Default.HowToReg, order = 3)
}