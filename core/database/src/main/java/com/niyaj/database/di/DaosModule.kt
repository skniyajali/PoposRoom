/*
 * Copyright 2024 Sk Niyaj Ali
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.niyaj.database.di

import com.niyaj.database.PoposDatabase
import com.niyaj.database.dao.AbsentDao
import com.niyaj.database.dao.AccountDao
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
import com.niyaj.database.dao.MarketListWIthItemsDao
import com.niyaj.database.dao.MarketListWIthTypeDao
import com.niyaj.database.dao.MarketTypeDao
import com.niyaj.database.dao.MeasureUnitDao
import com.niyaj.database.dao.OrderDao
import com.niyaj.database.dao.PaymentDao
import com.niyaj.database.dao.PrintDao
import com.niyaj.database.dao.PrinterDao
import com.niyaj.database.dao.ProductDao
import com.niyaj.database.dao.ProfileDao
import com.niyaj.database.dao.ReportsDao
import com.niyaj.database.dao.SelectedDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaosModule {

    @Provides
    fun providesAccountDao(database: PoposDatabase): AccountDao = database.accountDao()

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
    fun providesMainFeedDao(database: PoposDatabase): HomeDao = database.mainFeedDao()

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
    fun provideMarketTypeDao(database: PoposDatabase): MarketTypeDao = database.marketTypeDao()

    @Provides
    fun provideMarketItemDao(database: PoposDatabase): MarketItemDao = database.marketItemDao()

    @Provides
    fun provideMarketListDao(database: PoposDatabase): MarketListDao = database.marketListDao()

    @Provides
    fun provideMarketListWithTypeDao(database: PoposDatabase): MarketListWIthTypeDao =
        database.marketListWithTypeDao()

    @Provides
    fun provideMarketListWithItemsDao(database: PoposDatabase): MarketListWIthItemsDao =
        database.marketListWithItemsDao()

    @Provides
    fun provideMeasureUnitDao(database: PoposDatabase): MeasureUnitDao = database.measureUnitDao()
}
