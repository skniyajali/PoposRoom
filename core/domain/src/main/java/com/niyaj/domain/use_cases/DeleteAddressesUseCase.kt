package com.niyaj.domain.use_cases

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.Resource
import com.niyaj.data.repository.AddressRepository
import com.niyaj.data.repository.CartOrderRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeleteAddressesUseCase @Inject constructor(
    private val addressRepository: AddressRepository,
    private val cartOrderRepository: CartOrderRepository,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(addressIds: List<Int>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                addressRepository.deleteAddresses(addressIds).data?.let {
                    addressIds.forEach { addressId ->
                        val orderIds = cartOrderRepository.getCartOrderIdsByAddressId(addressId)
                        cartOrderRepository.deleteCartOrders(orderIds)
                    }
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }
}