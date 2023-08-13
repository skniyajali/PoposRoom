package com.niyaj.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.niyaj.model.Reports

@Entity(
    tableName = "reports"
)
data class ReportsEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true)
    val reportId: Int = 0,

    val expensesQty: Long = 0,
    val expensesAmount: Long = 0,

    val dineInSalesQty: Long = 0,
    val dineInSalesAmount: Long = 0,

    val dineOutSalesQty: Long = 0,
    val dineOutSalesAmount: Long = 0,

    val reportDate: String = "",

    val createdAt: String = System.currentTimeMillis().toString(),

    val updatedAt: String? = null,
)

fun ReportsEntity.toExternalModel(): Reports {
    return Reports(
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