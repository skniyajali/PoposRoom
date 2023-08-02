package com.niyaj.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.niyaj.model.Absent
import java.util.Date

@Entity(
    tableName = "absent",
    foreignKeys = [ForeignKey(
        entity = EmployeeEntity::class,
        parentColumns = arrayOf("employeeId"),
        childColumns = arrayOf("employeeId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class AbsentEntity(
    @PrimaryKey(autoGenerate = true)
    val absentId: Int = 0,

    @ColumnInfo(index = true)
    val employeeId: Int,

    val absentReason: String = "",

    val absentDate: String,

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: Date,

    @ColumnInfo(defaultValue = "NULL")
    val updatedAt: Date? = null,
)

fun AbsentEntity.asExternalModel(): Absent {
    return Absent(
        absentId = this.absentId,
        employeeId = this.employeeId,
        absentReason = this.absentReason,
        absentDate = this.absentDate,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}