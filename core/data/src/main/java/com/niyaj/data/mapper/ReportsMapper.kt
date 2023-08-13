package com.niyaj.data.mapper

import com.niyaj.database.model.ReportsEntity
import com.niyaj.model.Reports

fun Reports.asEntity(): ReportsEntity {
    return ReportsEntity(
        reportId = this.reportId,
        expensesQty = this.expensesQty,
        expensesAmount = this.expensesAmount,
        dineInSalesQty = this.dineInSalesQty,
        dineInSalesAmount = this.dineInSalesAmount,
        dineOutSalesQty = this.dineOutSalesQty,
        dineOutSalesAmount = this.dineOutSalesAmount,
        reportDate = this.reportDate,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}