package com.niyaj.data.di

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.data.repository.CartRepositoryImpl
import com.niyaj.data.repository.CartRepository
import com.niyaj.database.dao.CartDao
import com.niyaj.database.dao.CartOrderDao
import com.niyaj.database.dao.CartPriceDao
import com.niyaj.database.dao.SelectedDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object CartModule {

    @Provides
    fun provideCartRepository(
        cartDao: CartDao,
        cartOrderDao: CartOrderDao,
        selectedDao: SelectedDao,
        cartPriceDao: CartPriceDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): CartRepository {
        return CartRepositoryImpl(cartDao, cartOrderDao,selectedDao, cartPriceDao, ioDispatcher)
    }
}