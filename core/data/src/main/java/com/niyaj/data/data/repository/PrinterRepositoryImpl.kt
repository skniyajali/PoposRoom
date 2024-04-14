package com.niyaj.data.data.repository

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.Resource
import com.niyaj.data.mapper.toEntity
import com.niyaj.data.repository.PrinterRepository
import com.niyaj.database.dao.PrinterDao
import com.niyaj.database.model.toExternalModel
import com.niyaj.model.Printer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

class PrinterRepositoryImpl(
    private val printerDao: PrinterDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : PrinterRepository {

    override fun getPrinter(printerId: String): Flow<Printer> {
        return printerDao.printerInfo(printerId).mapLatest {
            it?.toExternalModel() ?: Printer.defaultPrinterInfo
        }
    }

    override suspend fun getPrinterInfo(printerId: String): Flow<Printer> {
        return flow { }
    }

    override suspend fun addOrUpdatePrinterInfo(printer: Printer): Resource<Boolean> {
        return withContext(ioDispatcher) {
            try {
                val result = printerDao.insertOrUpdatePrinterInfo(printer.toEntity())

                Resource.Success(result > 0)
            } catch (e: Exception) {
                Resource.Error(e.message)
            }
        }
    }
}