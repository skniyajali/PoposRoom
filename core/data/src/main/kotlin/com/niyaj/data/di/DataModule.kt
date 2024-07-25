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

package com.niyaj.data.di

import com.niyaj.data.data.repository.AbsentRepositoryImpl
import com.niyaj.data.data.repository.AccountRepositoryImpl
import com.niyaj.data.data.repository.AddOnItemRepositoryImpl
import com.niyaj.data.data.repository.AddressRepositoryImpl
import com.niyaj.data.data.repository.BackupRepositoryImpl
import com.niyaj.data.data.repository.CartOrderRepositoryImpl
import com.niyaj.data.data.repository.CartRepositoryImpl
import com.niyaj.data.data.repository.CategoryRepositoryImpl
import com.niyaj.data.data.repository.ChargesRepositoryImpl
import com.niyaj.data.data.repository.CustomerRepositoryImpl
import com.niyaj.data.data.repository.DataDeletionRepositoryImpl
import com.niyaj.data.data.repository.EmployeeRepositoryImpl
import com.niyaj.data.data.repository.ExpenseRepositoryImpl
import com.niyaj.data.data.repository.HomeRepositoryImpl
import com.niyaj.data.data.repository.KeepDataConfigRepositoryImpl
import com.niyaj.data.data.repository.MarketItemRepositoryImpl
import com.niyaj.data.data.repository.MarketListItemRepositoryImpl
import com.niyaj.data.data.repository.MarketListRepositoryImpl
import com.niyaj.data.data.repository.MarketTypeRepositoryImpl
import com.niyaj.data.data.repository.MeasureUnitRepositoryImpl
import com.niyaj.data.data.repository.OrderRepositoryImpl
import com.niyaj.data.data.repository.PaymentRepositoryImpl
import com.niyaj.data.data.repository.PrintRepositoryImpl
import com.niyaj.data.data.repository.PrinterRepositoryImpl
import com.niyaj.data.data.repository.PrinterValidationRepositoryImpl
import com.niyaj.data.data.repository.ProductRepositoryImpl
import com.niyaj.data.data.repository.ProfileRepositoryImpl
import com.niyaj.data.data.repository.ReportsRepositoryImpl
import com.niyaj.data.data.repository.UserDataRepositoryImpl
import com.niyaj.data.repository.AbsentRepository
import com.niyaj.data.repository.AccountRepository
import com.niyaj.data.repository.AddOnItemRepository
import com.niyaj.data.repository.AddressRepository
import com.niyaj.data.repository.BackupRepository
import com.niyaj.data.repository.CartOrderRepository
import com.niyaj.data.repository.CartRepository
import com.niyaj.data.repository.CategoryRepository
import com.niyaj.data.repository.ChargesRepository
import com.niyaj.data.repository.CustomerRepository
import com.niyaj.data.repository.DataDeletionRepository
import com.niyaj.data.repository.EmployeeRepository
import com.niyaj.data.repository.EmployeeValidationRepository
import com.niyaj.data.repository.ExpenseRepository
import com.niyaj.data.repository.ExpenseValidationRepository
import com.niyaj.data.repository.HomeRepository
import com.niyaj.data.repository.KeepDataConfigRepository
import com.niyaj.data.repository.MarketItemRepository
import com.niyaj.data.repository.MarketListItemRepository
import com.niyaj.data.repository.MarketListRepository
import com.niyaj.data.repository.MarketTypeRepository
import com.niyaj.data.repository.MeasureUnitRepository
import com.niyaj.data.repository.OrderRepository
import com.niyaj.data.repository.PaymentRepository
import com.niyaj.data.repository.PrintRepository
import com.niyaj.data.repository.PrinterRepository
import com.niyaj.data.repository.ProductRepository
import com.niyaj.data.repository.ProfileRepository
import com.niyaj.data.repository.ReportsRepository
import com.niyaj.data.repository.UserDataRepository
import com.niyaj.data.repository.validation.AbsentValidationRepository
import com.niyaj.data.repository.validation.AddOnItemValidationRepository
import com.niyaj.data.repository.validation.AddressValidationRepository
import com.niyaj.data.repository.validation.CartOrderValidationRepository
import com.niyaj.data.repository.validation.CategoryValidationRepository
import com.niyaj.data.repository.validation.ChargesValidationRepository
import com.niyaj.data.repository.validation.CustomerValidationRepository
import com.niyaj.data.repository.validation.KeepDataConfigValidationRepository
import com.niyaj.data.repository.validation.MarketItemValidationRepository
import com.niyaj.data.repository.validation.MarketTypeValidationRepository
import com.niyaj.data.repository.validation.MeasureUnitValidationRepository
import com.niyaj.data.repository.validation.PaymentValidationRepository
import com.niyaj.data.repository.validation.PrinterValidationRepository
import com.niyaj.data.repository.validation.ProductValidationRepository
import com.niyaj.data.repository.validation.ProfileValidationRepository
import com.niyaj.data.utils.ConnectivityManagerNetworkMonitor
import com.niyaj.data.utils.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    internal abstract fun provideAccountRepository(
        accountRepository: AccountRepositoryImpl,
    ): AccountRepository

    @Binds
    internal abstract fun provideAbsentRepository(
        absentRepository: AbsentRepositoryImpl,
    ): AbsentRepository

    @Binds
    internal abstract fun provideAbsentValidationRepository(
        absentRepository: AbsentRepositoryImpl,
    ): AbsentValidationRepository

    @Binds
    internal abstract fun provideAddOnItemRepository(
        addOnItemRepositoryImpl: AddOnItemRepositoryImpl,
    ): AddOnItemRepository

    @Binds
    internal abstract fun provideAddOnItemValidationRepository(
        addOnItemRepositoryImpl: AddOnItemRepositoryImpl,
    ): AddOnItemValidationRepository

    @Binds
    internal abstract fun provideAddressRepository(
        addressRepositoryImpl: AddressRepositoryImpl,
    ): AddressRepository

    @Binds
    internal abstract fun provideAddressValidationRepository(
        addressRepositoryImpl: AddressRepositoryImpl,
    ): AddressValidationRepository

    @Binds
    internal abstract fun provideCartRepository(
        cartRepositoryImpl: CartRepositoryImpl,
    ): CartRepository

    @Binds
    internal abstract fun provideCartOrderRepository(
        cartOrderRepositoryImpl: CartOrderRepositoryImpl,
    ): CartOrderRepository

    @Binds
    internal abstract fun provideCartOrderValidationRepository(
        cartOrderRepositoryImpl: CartOrderRepositoryImpl,
    ): CartOrderValidationRepository

    @Binds
    internal abstract fun provideCategoryValidationRepository(
        categoryRepositoryImpl: CategoryRepositoryImpl,
    ): CategoryValidationRepository

    @Binds
    internal abstract fun provideCategoryRepository(
        categoryRepositoryImpl: CategoryRepositoryImpl,
    ): CategoryRepository

    @Binds
    internal abstract fun provideChargesValidationRepository(
        chargesRepositoryImpl: ChargesRepositoryImpl,
    ): ChargesValidationRepository

    @Binds
    internal abstract fun provideChargesRepository(
        chargesRepositoryImpl: ChargesRepositoryImpl,
    ): ChargesRepository

    @Binds
    internal abstract fun provideCustomerValidationRepository(
        customerRepositoryImpl: CustomerRepositoryImpl,
    ): CustomerValidationRepository

    @Binds
    internal abstract fun provideCustomerRepository(
        customerRepositoryImpl: CustomerRepositoryImpl,
    ): CustomerRepository

    @Binds
    internal abstract fun provideEmployeeValidationRepository(
        employeeRepositoryImpl: EmployeeRepositoryImpl,
    ): EmployeeValidationRepository

    @Binds
    internal abstract fun provideEmployeeRepository(
        employeeRepositoryImpl: EmployeeRepositoryImpl,
    ): EmployeeRepository

    @Binds
    internal abstract fun provideExpenseValidationRepository(
        expenseRepositoryImpl: ExpenseRepositoryImpl,
    ): ExpenseValidationRepository

    @Binds
    internal abstract fun provideExpenseRepository(
        expenseRepositoryImpl: ExpenseRepositoryImpl,
    ): ExpenseRepository

    @Binds
    internal abstract fun provideHomeRepository(
        homeRepositoryImpl: HomeRepositoryImpl,
    ): HomeRepository

    @Binds
    internal abstract fun provideMarketListValidationRepository(
        marketItemRepositoryImpl: MarketItemRepositoryImpl,
    ): MarketItemValidationRepository

    @Binds
    internal abstract fun provideMarketItemRepository(
        marketItemRepositoryImpl: MarketItemRepositoryImpl,
    ): MarketItemRepository

    @Binds
    internal abstract fun provideMarketListItemRepository(
        marketListRepositoryImpl: MarketListItemRepositoryImpl,
    ): MarketListItemRepository

    @Binds
    internal abstract fun provideMarketListRepository(
        marketListRepositoryImpl: MarketListRepositoryImpl,
    ): MarketListRepository

    @Binds
    internal abstract fun provideMarketTypeValidationRepository(
        marketTypeRepositoryImpl: MarketTypeRepositoryImpl,
    ): MarketTypeValidationRepository

    @Binds
    internal abstract fun provideMarketTypeRepository(
        marketTypeRepositoryImpl: MarketTypeRepositoryImpl,
    ): MarketTypeRepository

    @Binds
    internal abstract fun provideMeasureUnitValidationRepository(
        measureUnitRepositoryImpl: MeasureUnitRepositoryImpl,
    ): MeasureUnitValidationRepository

    @Binds
    internal abstract fun provideMeasureUnitRepository(
        measureUnitRepositoryImpl: MeasureUnitRepositoryImpl,
    ): MeasureUnitRepository

    @Binds
    internal abstract fun provideOrderRepository(
        orderRepositoryImpl: OrderRepositoryImpl,
    ): OrderRepository

    @Binds
    internal abstract fun providePaymentValidationRepository(
        paymentRepositoryImpl: PaymentRepositoryImpl,
    ): PaymentValidationRepository

    @Binds
    internal abstract fun providePaymentRepository(
        paymentRepositoryImpl: PaymentRepositoryImpl,
    ): PaymentRepository

    @Binds
    internal abstract fun providePrinterValidationRepository(
        printerRepositoryImpl: PrinterValidationRepositoryImpl,
    ): PrinterValidationRepository

    @Binds
    internal abstract fun providePrinterRepository(
        printerRepositoryImpl: PrinterRepositoryImpl,
    ): PrinterRepository

    @Binds
    internal abstract fun providePrintRepository(
        printRepositoryImpl: PrintRepositoryImpl,
    ): PrintRepository

    @Binds
    internal abstract fun provideProductValidationRepository(
        productRepositoryImpl: ProductRepositoryImpl,
    ): ProductValidationRepository

    @Binds
    internal abstract fun provideProductRepository(
        productRepositoryImpl: ProductRepositoryImpl,
    ): ProductRepository

    @Binds
    internal abstract fun profileValidationRepository(
        profileRepositoryImpl: ProfileRepositoryImpl,
    ): ProfileValidationRepository

    @Binds
    internal abstract fun provideProfileRepository(
        profileRepositoryImpl: ProfileRepositoryImpl,
    ): ProfileRepository

    @Binds
    internal abstract fun provideReportsRepositoryImpl(
        reportsRepositoryImpl: ReportsRepositoryImpl,
    ): ReportsRepository

    @Binds
    internal abstract fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor,
    ): NetworkMonitor

    @Binds
    internal abstract fun bindsUserDataRepository(
        userDataRepository: UserDataRepositoryImpl,
    ): UserDataRepository

    @Binds
    internal abstract fun bindBackupRepository(
        backupRepository: BackupRepositoryImpl,
    ): BackupRepository

    @Binds
    internal abstract fun bindsKeepDataConfigRepository(
        keepDataRepository: KeepDataConfigRepositoryImpl,
    ): KeepDataConfigRepository

    @Binds
    internal abstract fun bindsKeepDataValidationRepository(
        keepDataRepository: KeepDataConfigRepositoryImpl,
    ): KeepDataConfigValidationRepository

    @Binds
    internal abstract fun provideDataDeletionRepository(
        dataDeletionRepository: DataDeletionRepositoryImpl,
    ): DataDeletionRepository
}
