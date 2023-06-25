package com.niyaj.poposroom.features.cart.di

import com.niyaj.poposroom.features.cart.data.dao.CartDao
import com.niyaj.poposroom.features.cart.data.repository.CartRepositoryImpl
import com.niyaj.poposroom.features.cart.domain.repository.CartRepository
import com.niyaj.poposroom.features.cart_order.data.dao.CartOrderDao
import com.niyaj.poposroom.features.common.database.PoposDatabase
import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object CartModule {

    @Provides
    fun provideCartDao(database: PoposDatabase) : CartDao {
        return database.cartDao()
    }

    @Provides
    fun provideCartRepository(
        cartDao: CartDao,
        cartOrderDao: CartOrderDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): CartRepository {
        return CartRepositoryImpl(cartDao, cartOrderDao, ioDispatcher)
    }

}