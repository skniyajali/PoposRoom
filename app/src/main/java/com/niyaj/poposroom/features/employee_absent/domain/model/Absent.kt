package com.niyaj.poposroom.features.employee_absent.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.niyaj.poposroom.features.common.utils.toJoinedDate
import com.niyaj.poposroom.features.employee.domain.model.Employee
import java.util.Date

@Entity(
    tableName = "absent",
    foreignKeys = [ForeignKey(
        entity = Employee::class,
        parentColumns = arrayOf("employeeId"),
        childColumns = arrayOf("employeeId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Absent(
    @PrimaryKey(autoGenerate = true)
    val absentId: Int = 0,

    @ColumnInfo(index = true)
    val employeeId: Int,

    val absentReason: String = "",

    val absentDate: String = "",

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: Date,

    @ColumnInfo(defaultValue = "NULL")
    val updatedAt: Date? = null,
)


/**
 *
 */
fun Absent.filterAbsent(searchText: String): Boolean {
    return this.absentDate.toJoinedDate.contains(searchText, true) ||
            this.absentReason.contains(searchText, true)
}
