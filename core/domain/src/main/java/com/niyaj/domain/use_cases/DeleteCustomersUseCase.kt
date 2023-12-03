package com.niyaj.domain.use_cases

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.Resource
import com.niyaj.data.repository.CartOrderRepository
import com.niyaj.data.repository.CustomerRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeleteCustomersUseCase @Inject constructor(
    private val customerRepository: CustomerRepository,
    private val cartOrderRepository: CartOrderRepository,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(customerIds: List<Int>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                customerRepository.deleteCustomers(customerIds).data?.let {
                    if (it) {
                        customerIds.forEach { customerId ->
                            val orderIds =
                                cartOrderRepository.getCartOrderIdsByCustomerId(customerId)
                            cartOrderRepository.deleteCartOrders(orderIds)
                        }
                    }
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }
}