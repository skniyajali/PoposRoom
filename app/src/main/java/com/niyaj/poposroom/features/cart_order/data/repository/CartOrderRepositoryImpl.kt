package com.niyaj.poposroom.features.cart_order.data.repository

import com.niyaj.poposroom.features.address.data.dao.AddressDao
import com.niyaj.poposroom.features.address.domain.model.Address
import com.niyaj.poposroom.features.address.domain.model.searchAddress
import com.niyaj.poposroom.features.cart_order.data.dao.CartOrderDao
import com.niyaj.poposroom.features.cart_order.domain.model.CartOrder
import com.niyaj.poposroom.features.cart_order.domain.model.CartOrderEntity
import com.niyaj.poposroom.features.cart_order.domain.model.filterCartOrder
import com.niyaj.poposroom.features.cart_order.domain.repository.CartOrderRepository
import com.niyaj.poposroom.features.cart_order.domain.repository.CartOrderValidationRepository
import com.niyaj.poposroom.features.cart_order.domain.utils.CartOrderTestTags.ADDRESS_NAME_LENGTH_ERROR
import com.niyaj.poposroom.features.cart_order.domain.utils.CartOrderTestTags.CART_ORDER_ID_EMPTY_ERROR
import com.niyaj.poposroom.features.cart_order.domain.utils.CartOrderTestTags.CART_ORDER_ID_EXIST_ERROR
import com.niyaj.poposroom.features.cart_order.domain.utils.CartOrderTestTags.CART_ORDER_NAME_EMPTY_ERROR
import com.niyaj.poposroom.features.cart_order.domain.utils.CartOrderTestTags.CART_ORDER_NAME_ERROR
import com.niyaj.poposroom.features.cart_order.domain.utils.CartOrderTestTags.CART_ORDER_PHONE_EMPTY_ERROR
import com.niyaj.poposroom.features.cart_order.domain.utils.CartOrderTestTags.CART_ORDER_PHONE_ERROR
import com.niyaj.poposroom.features.cart_order.domain.utils.CartOrderTestTags.CUSTOMER_PHONE_LENGTH_ERROR
import com.niyaj.poposroom.features.cart_order.domain.utils.CartOrderTestTags.CUSTOMER_PHONE_LETTER_ERROR
import com.niyaj.poposroom.features.cart_order.domain.utils.CartOrderTestTags.ORDER_PRICE_LESS_THAN_TWO_ERROR
import com.niyaj.poposroom.features.cart_order.domain.utils.CartOrderTestTags.ORDER_SHORT_NAME_EMPTY_ERROR
import com.niyaj.poposroom.features.cart_order.domain.utils.CartOrderType
import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import com.niyaj.poposroom.features.common.utils.Resource
import com.niyaj.poposroom.features.common.utils.ValidationResult
import com.niyaj.poposroom.features.customer.data.dao.CustomerDao
import com.niyaj.poposroom.features.customer.domain.model.Customer
import com.niyaj.poposroom.features.customer.domain.model.searchCustomer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

class CartOrderRepositoryImpl(
    private val cartOrderDao: CartOrderDao,
    private val customerDao: CustomerDao,
    private val addressDao: AddressDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher
) : CartOrderRepository, CartOrderValidationRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getAllAddresses(searchText: String): Flow<List<Address>> {
        return withContext(ioDispatcher) {
            addressDao.getAllAddresses().mapLatest { it.searchAddress(searchText) }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getAllCustomer(searchText: String): Flow<List<Customer>> {
        return withContext(ioDispatcher) {
            customerDao.getAllCustomer().mapLatest { it.searchCustomer(searchText) }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getAllCartOrders(searchText: String): Flow<List<CartOrder>> {
        return withContext(ioDispatcher) {
            cartOrderDao.getAllCartOrders().mapLatest { list ->
                list.map { cartOrder ->
                    val address = if (cartOrder.orderType != CartOrderType.DineIn) {
                        withContext(ioDispatcher) {
                            addressDao.getAddressById(cartOrder.addressId)
                        }
                    } else null

                    val customer = if (cartOrder.orderType != CartOrderType.DineIn) {
                        withContext(ioDispatcher) {
                            customerDao.getCustomerById(cartOrder.customerId)
                        }
                    } else null

                    CartOrder(
                        cartOrderId = cartOrder.cartOrderId,
                        orderId = cartOrder.orderId,
                        orderType = cartOrder.orderType,
                        orderStatus = cartOrder.orderStatus,
                        doesChargesIncluded = cartOrder.doesChargesIncluded,
                        customer = customer ?: Customer(),
                        address = address ?: Address(),
                        createdAt = cartOrder.createdAt,
                        updatedAt = cartOrder.updatedAt
                    )
                }
            }.mapLatest {
                it.filterCartOrder(searchText)
            }
        }
    }

    override suspend fun getCartOrderById(cartOrderId: Int): Resource<CartOrder?> {
        return withContext(ioDispatcher) {
            try {
                val result = cartOrderDao.getCartOrderById(cartOrderId)

                val cartOrder = result?.let { cartOrder ->
                    val address = if (cartOrder.orderType != CartOrderType.DineIn) {
                        withContext(ioDispatcher) {
                            addressDao.getAddressById(cartOrder.addressId)
                        }
                    } else null

                    val customer = if (cartOrder.orderType != CartOrderType.DineIn) {
                        withContext(ioDispatcher) {
                            customerDao.getCustomerById(cartOrder.customerId)
                        }
                    } else null

                    CartOrder(
                        cartOrderId = cartOrder.cartOrderId,
                        orderId = cartOrder.orderId,
                        orderType = cartOrder.orderType,
                        orderStatus = cartOrder.orderStatus,
                        doesChargesIncluded = cartOrder.doesChargesIncluded,
                        customer = customer ?: Customer(),
                        address = address ?: Address(),
                        createdAt = cartOrder.createdAt,
                        updatedAt = cartOrder.updatedAt
                    )
                }

                Resource.Success(cartOrder)
            }catch (e: Exception) {
                Resource.Error(e.message ?: "Unable to get CartOrder")
            }
        }
    }

    override suspend fun getLastCreatedOrderId(cartOrderId: Int?): Int {
        return withContext(ioDispatcher) {
            if (cartOrderId != null && cartOrderId != 0) {
                cartOrderDao.getOrderIdByCartOrderId(cartOrderId)
            }else {
                val result = cartOrderDao.getLastCreatedOrderId()

                if (result == null) 1 else result + 1
            }
        }
    }

    override suspend fun addOrIgnoreAddress(newAddress: Address): Int {
        return try {
            withContext(ioDispatcher){
                val validateAddressName = validateAddressName(newAddress.addressName)
                val validateAddressShortName = validateAddressShortName(newAddress.shortName)

                val hasError = listOf(validateAddressName, validateAddressShortName).any { !it.successful}

                if (!hasError) {
                    withContext(ioDispatcher) {
                        addressDao.getAddressByName(newAddress.addressName)
                            ?: addressDao.insertOrIgnoreAddress(newAddress).toInt()
                    }
                }else {
                    0
                }
            }
        } catch (e: Exception) {
            0
        }
    }

    override suspend fun addOrIgnoreCustomer(newCustomer: Customer): Int {
        return try {
            withContext(ioDispatcher){
                val validateCustomerPhone = validateCustomerPhone(newCustomer.customerPhone)

                if (validateCustomerPhone.successful) {
                    customerDao.getCustomerByPhone(newCustomer.customerPhone) ?:
                    customerDao.insertOrIgnoreCustomer(newCustomer).toInt()
                }else {
                    0
                }
            }
        } catch (e: Exception) {
            0
        }
    }

    override suspend fun createOrUpdateCartOrder(newCartOrder: CartOrder): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val isDineOut = newCartOrder.orderType == CartOrderType.DineOut

                val addressId = async {
                    if (isDineOut) {
                        if (newCartOrder.address.addressId == 0) {
                            addOrIgnoreAddress(newCartOrder.address)
                        } else newCartOrder.address.addressId
                    }else 0
                }.await()

                val customerId = async {
                    if (isDineOut) {
                        if (newCartOrder.customer.customerId == 0) {
                            addOrIgnoreCustomer(newCartOrder.customer)
                        }else newCartOrder.customer.customerId
                    }else 0
                }.await()

                val validatedCustomer = validateCustomerPhone(newCartOrder.orderType, customerId)
                val validatedAddress = validateCustomerAddress(newCartOrder.orderType, addressId)
                val validatedOrderId = validateOrderId(newCartOrder.orderId, newCartOrder.cartOrderId)

                val hasError = listOf(validatedCustomer, validatedAddress, validatedOrderId).any {
                    !it.successful
                }

                if (!hasError) {
                    val newOrder = CartOrderEntity(
                        cartOrderId = newCartOrder.cartOrderId,
                        orderId = newCartOrder.orderId,
                        orderType = newCartOrder.orderType,
                        orderStatus = newCartOrder.orderStatus,
                        doesChargesIncluded = newCartOrder.doesChargesIncluded,
                        addressId = addressId,
                        customerId = customerId,
                        createdAt = newCartOrder.createdAt,
                        updatedAt = newCartOrder.updatedAt
                    )

                    val result = cartOrderDao.createOrUpdateCartOrder(newOrder)

                    Resource.Success(result > 0)
                }else {
                    Resource.Error("Unable to validate order details.")
                }
            }
        }catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun deleteCartOrder(cartOrderId: Int): Resource<Boolean> {
        return try {
            val result = withContext(ioDispatcher) {
                cartOrderDao.deleteCartOrder(cartOrderId)
            }

            return Resource.Success(result > 0)
        }catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun deleteCartOrders(cartOrderIds: List<Int>): Resource<Boolean> {
        return try {
            val result = withContext(ioDispatcher) {
                cartOrderDao.deleteCartOrders(cartOrderIds)
            }

            return Resource.Success(result > 0)
        }catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    private fun validateCustomerAddress(
        orderType: CartOrderType,
        addressId: Int
    ): ValidationResult {
        if (orderType != CartOrderType.DineIn) {
            if (addressId == 0) {
                return ValidationResult(
                    successful = false,
                    errorMessage = CART_ORDER_NAME_EMPTY_ERROR
                )
            }

            if (addressId < 0) {
                return ValidationResult(
                    successful = false,
                    errorMessage = CART_ORDER_NAME_ERROR
                )
            }
        }

        return ValidationResult(
            successful = true
        )
    }

    private fun validateCustomerPhone(
        orderType: CartOrderType,
        customerId: Int
    ): ValidationResult {
        if (orderType != CartOrderType.DineIn) {
            if (customerId == 0) {
                return ValidationResult(
                    successful = false,
                    errorMessage = CART_ORDER_PHONE_EMPTY_ERROR
                )
            }

            if (customerId < 0) {
                return ValidationResult(
                    successful = false,
                    errorMessage = CART_ORDER_PHONE_ERROR
                )
            }
        }

        return ValidationResult(
            successful = true
        )
    }

    private suspend fun validateOrderId(orderId: Int, cartOrderId: Int = 0): ValidationResult {
        if (orderId == 0) {
            return ValidationResult(
                successful = false,
                errorMessage = CART_ORDER_ID_EMPTY_ERROR
            )
        }

        if (cartOrderId == 0) {
            val result = withContext(ioDispatcher) {
                cartOrderDao.getCartOrderIdByOrderId(orderId) != null
            }

            if (result) {
                return ValidationResult(
                    successful = false,
                    errorMessage = CART_ORDER_ID_EXIST_ERROR
                )
            }
        }

        return ValidationResult(
            successful = true
        )
    }

    override suspend fun validateCustomerPhone(customerPhone: String): ValidationResult {
        if(customerPhone.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = CART_ORDER_PHONE_EMPTY_ERROR
            )
        }

        if(customerPhone.length < 10 || customerPhone.length > 10) {
            return ValidationResult(
                successful = false,
                errorMessage = CUSTOMER_PHONE_LENGTH_ERROR
            )
        }

        val containsLetters = customerPhone.any { it.isLetter() }

        if(containsLetters){
            return ValidationResult(
                successful = false,
                errorMessage = CUSTOMER_PHONE_LETTER_ERROR
            )
        }

        return ValidationResult(
            successful = true
        )
    }

    override suspend fun validateAddressName(addressName: String): ValidationResult {
        if(addressName.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = CART_ORDER_NAME_EMPTY_ERROR,
            )
        }

        if(addressName.length < 2) {
            return ValidationResult(
                successful = false,
                errorMessage = ADDRESS_NAME_LENGTH_ERROR
            )
        }

        return ValidationResult(
            successful = true
        )
    }

    override fun validateAddressShortName(addressShortName: String): ValidationResult {
        if(addressShortName.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = ORDER_SHORT_NAME_EMPTY_ERROR
            )
        }

        if(addressShortName.length < 2) {
            return ValidationResult(
                successful = false,
                errorMessage = ORDER_PRICE_LESS_THAN_TWO_ERROR
            )
        }

        return ValidationResult(
            successful = true
        )
    }

}