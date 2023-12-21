package com.niyaj.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.niyaj.database.dao.AbsentDao
import com.niyaj.database.dao.AddOnItemDao
import com.niyaj.database.dao.AddressDao
import com.niyaj.database.dao.CartDao
import com.niyaj.database.dao.CartOrderDao
import com.niyaj.database.dao.CartPriceDao
import com.niyaj.database.dao.CategoryDao
import com.niyaj.database.dao.ChargesDao
import com.niyaj.database.dao.CustomerDao
import com.niyaj.database.dao.EmployeeDao
import com.niyaj.database.dao.ExpenseDao
import com.niyaj.database.dao.HomeDao
import com.niyaj.database.dao.MarketItemDao
import com.niyaj.database.dao.MarketListDao
import com.niyaj.database.dao.MeasureUnitDao
import com.niyaj.database.dao.OrderDao
import com.niyaj.database.dao.PaymentDao
import com.niyaj.database.dao.PrintDao
import com.niyaj.database.dao.PrinterDao
import com.niyaj.database.dao.ProductDao
import com.niyaj.database.dao.ProfileDao
import com.niyaj.database.dao.ReportsDao
import com.niyaj.database.dao.SelectedDao
import com.niyaj.database.model.AbsentEntity
import com.niyaj.database.model.AddOnItemEntity
import com.niyaj.database.model.AddressEntity
import com.niyaj.database.model.CartAddOnItemsEntity
import com.niyaj.database.model.CartChargesEntity
import com.niyaj.database.model.CartEntity
import com.niyaj.database.model.CartOrderEntity
import com.niyaj.database.model.CartPriceEntity
import com.niyaj.database.model.CategoryEntity
import com.niyaj.database.model.CategoryWithProductCrossRef
import com.niyaj.database.model.ChargesEntity
import com.niyaj.database.model.CustomerEntity
import com.niyaj.database.model.EmployeeEntity
import com.niyaj.database.model.EmployeeWithAbsentCrossRef
import com.niyaj.database.model.EmployeeWithPaymentCrossRef
import com.niyaj.database.model.ExpenseEntity
import com.niyaj.database.model.MarketItemEntity
import com.niyaj.database.model.MarketListEntity
import com.niyaj.database.model.MarketListWithItemEntity
import com.niyaj.database.model.MeasureUnitEntity
import com.niyaj.database.model.PaymentEntity
import com.niyaj.database.model.PrinterEntity
import com.niyaj.database.model.ProductEntity
import com.niyaj.database.model.ProfileEntity
import com.niyaj.database.model.ReportsEntity
import com.niyaj.database.model.SelectedEntity
import com.niyaj.database.util.ListConverter
import com.niyaj.database.util.TimestampConverters


@Database(
    entities = [
        AddOnItemEntity::class,
        AddressEntity::class,
        ChargesEntity::class,
        CategoryEntity::class,
        CustomerEntity::class,
        EmployeeEntity::class,
        PaymentEntity::class,
        EmployeeWithPaymentCrossRef::class,
        AbsentEntity::class,
        EmployeeWithAbsentCrossRef::class,
        ExpenseEntity::class,
        ProductEntity::class,
        CategoryWithProductCrossRef::class,
        CartOrderEntity::class,
        CartPriceEntity::class,
        CartAddOnItemsEntity::class,
        CartChargesEntity::class,
        SelectedEntity::class,
        CartEntity::class,
        ProfileEntity::class,
        PrinterEntity::class,
        ReportsEntity::class,
        MarketItemEntity::class,
        MarketListEntity::class,
        MarketListWithItemEntity::class,
        MeasureUnitEntity::class,
    ],
    version = 12,
    autoMigrations = [],
    exportSchema = true,
)
@TypeConverters(TimestampConverters::class, ListConverter::class)
abstract class PoposDatabase : RoomDatabase() {
    abstract fun addOnItemDao(): AddOnItemDao
    abstract fun addressDao(): AddressDao
    abstract fun chargesDao(): ChargesDao
    abstract fun categoryDao(): CategoryDao
    abstract fun customerDao(): CustomerDao
    abstract fun employeeDao(): EmployeeDao
    abstract fun paymentDao(): PaymentDao
    abstract fun absentDao(): AbsentDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun productDao(): ProductDao
    abstract fun cartOrderDao(): CartOrderDao
    abstract fun cartPriceDao(): CartPriceDao
    abstract fun selectedDao(): SelectedDao
    abstract fun mainFeedDao(): HomeDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun printDao(): PrintDao
    abstract fun printerDao(): PrinterDao
    abstract fun profileDao(): ProfileDao
    abstract fun reportsDao(): ReportsDao
    abstract fun marketItemDao(): MarketItemDao
    abstract fun marketListDao(): MarketListDao
    abstract fun measureUnitDao(): MeasureUnitDao
}