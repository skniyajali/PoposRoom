package com.niyaj.poposroom.features.common.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.niyaj.poposroom.features.addon_item.data.dao.AddOnItemDao
import com.niyaj.poposroom.features.addon_item.domain.model.AddOnItem
import com.niyaj.poposroom.features.address.data.dao.AddressDao
import com.niyaj.poposroom.features.address.domain.model.Address
import com.niyaj.poposroom.features.category.data.dao.CategoryDao
import com.niyaj.poposroom.features.category.domain.model.Category
import com.niyaj.poposroom.features.charges.data.dao.ChargesDao
import com.niyaj.poposroom.features.charges.domain.model.Charges
import com.niyaj.poposroom.features.common.database.utils.TimestampConverters
import com.niyaj.poposroom.features.customer.data.dao.CustomerDao
import com.niyaj.poposroom.features.customer.domain.model.Customer
import com.niyaj.poposroom.features.employee.data.dao.EmployeeDao
import com.niyaj.poposroom.features.employee.domain.model.Employee
import com.niyaj.poposroom.features.employee_payment.data.dao.PaymentDao
import com.niyaj.poposroom.features.employee_payment.domain.model.EmployeeWithPaymentCrossRef
import com.niyaj.poposroom.features.employee_payment.domain.model.Payment

@Database(
    entities = [
        AddOnItem::class,
        Address::class,
        Charges::class,
        Category::class,
        Customer::class,
        Employee::class,
        Payment::class,
        EmployeeWithPaymentCrossRef::class,
    ],
    version = 3,
    autoMigrations = [],
    exportSchema = true,
)
@TypeConverters(TimestampConverters::class)
abstract class PoposDatabase : RoomDatabase() {
    abstract fun addOnItemDao(): AddOnItemDao
    abstract fun addressDao(): AddressDao
    abstract fun chargesDao(): ChargesDao
    abstract fun categoryDao(): CategoryDao
    abstract fun customerDao(): CustomerDao
    abstract fun employeeDao(): EmployeeDao
    abstract fun paymentDao(): PaymentDao
}