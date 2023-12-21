package com.niyaj.model

import androidx.compose.runtime.Stable

@Stable
data class BluetoothDeviceState(
    val name: String = "",
    val address: String = "",
    val bondState: Int? = null,
    val type: Int? = null,
    val connected: Boolean = false,
)
