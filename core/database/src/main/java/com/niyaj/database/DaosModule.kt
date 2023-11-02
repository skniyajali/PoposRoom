package com.niyaj.database

import com.niyaj.database.dao.AbsentDao
import com.niyaj.database.dao.AddOnItemDao
import com.niyaj.database.dao.AddressDao
import com.niyaj.database.dao.CartDao
import com.niyaj.database.dao.CartOrderDao
import com.niyaj.database.dao.CartPriceDao
import com.niyaj.database.dao.CategoryDao
import com.niyaj.database.dao.CustomerDao
import com.niyaj.database.dao.EmployeeDao
import com.niyaj.database.dao.ExpenseDao
import com.niyaj.database.dao.MainFeedDao
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
import com.niyaj.poposroom.features.charges.data.dao.ChargesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaosModule {

    @Provides
    fun providesAbsentDao(database: PoposDatabase): AbsentDao = database.absentDao()

    @Provides
    fun providesAddOnItemDao(database: PoposDatabase): AddOnItemDao = database.addOnItemDao()

    @Provides
    fun providesAddressDao(database: PoposDatabase): AddressDao = database.addressDao()

    @Provides
    fun providesCartDao(database: PoposDatabase): CartDao = database.cartDao()

    @Provides
    fun providesCartOrderDao(database: PoposDatabase): CartOrderDao = database.cartOrderDao()

    @Provides
    fun providesCartPriceDao(database: PoposDatabase): CartPriceDao = database.cartPriceDao()

    @Provides
    fun providesCategoryDao(database: PoposDatabase): CategoryDao = database.categoryDao()

    @Provides
    fun providesChargesDao(database: PoposDatabase): ChargesDao = database.chargesDao()

    @Provides
    fun providesCustomerDao(database: PoposDatabase): CustomerDao = database.customerDao()

    @Provides
    fun providesEmployeeDao(database: PoposDatabase): EmployeeDao = database.employeeDao()

    @Provides
    fun providesExpenseDao(database: PoposDatabase): ExpenseDao = database.expenseDao()

    @Provides
    fun providesMainFeedDao(database: PoposDatabase): MainFeedDao = database.mainFeedDao()

    @Provides
    fun providesOrderDao(database: PoposDatabase): OrderDao = database.orderDao()

    @Provides
    fun providesPaymentDao(database: PoposDatabase): PaymentDao = database.paymentDao()

    @Provides
    fun providesPrintDao(database: PoposDatabase): PrintDao = database.printDao()

    @Provides
    fun providesPrinterDao(database: PoposDatabase): PrinterDao = database.printerDao()

    @Provides
    fun providesProductDao(database: PoposDatabase): ProductDao = database.productDao()

    @Provides
    fun provideProfileDao(database: PoposDatabase): ProfileDao = database.profileDao()

    @Provides
    fun provideReportsDao(database: PoposDatabase): ReportsDao = database.reportsDao()

    @Provides
    fun provideSelectedDao(database: PoposDatabase): SelectedDao = database.selectedDao()

    @Provides
    fun provideMarketItemDao(database: PoposDatabase): MarketItemDao = database.marketItemDao()

    @Provides
    fun provideMarketListDao(database: PoposDatabase): MarketListDao = database.marketListDao()

    @Provides
    fun provideMeasureUnitDao(database: PoposDatabase): MeasureUnitDao = database.measureUnitDao()
}
