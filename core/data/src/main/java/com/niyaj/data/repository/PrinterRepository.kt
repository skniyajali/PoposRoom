package com.niyaj.data.repository

import com.niyaj.common.result.Resource
import com.niyaj.common.utils.Constants.PRINTER_ID
import com.niyaj.model.Printer
import com.niyaj.model.Profile
import kotlinx.coroutines.flow.Flow

interface PrinterRepository {

    fun getPrinter(printerId : String = PRINTER_ID): Flow<Printer>

    suspend fun getPrinterInfo(printerId: String = PRINTER_ID): Flow<Printer>

    suspend fun addOrUpdatePrinterInfo(printer : Printer): Resource<Boolean>

    fun getProfileInfo(restaurantId: Int): Flow<Profile>
}