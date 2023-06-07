package com.niyaj.poposroom.features.customer.data.repository

import android.util.Patterns
import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import com.niyaj.poposroom.features.common.utils.Resource
import com.niyaj.poposroom.features.common.utils.ValidationResult
import com.niyaj.poposroom.features.customer.data.dao.CustomerDao
import com.niyaj.poposroom.features.customer.domain.model.Customer
import com.niyaj.poposroom.features.customer.domain.model.searchCustomer
import com.niyaj.poposroom.features.customer.domain.repository.CustomerRepository
import com.niyaj.poposroom.features.customer.domain.repository.CustomerValidationRepository
import com.niyaj.poposroom.features.customer.domain.utils.CustomerTestTags
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

class CustomerRepositoryImpl(
    private val customerDao: CustomerDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher
) : CustomerRepository, CustomerValidationRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getAllCustomer(searchText: String): Flow<List<Customer>> {
        return withContext(ioDispatcher) {
            customerDao.getAllCustomer().mapLatest { it.searchCustomer(searchText) }
        }
    }

    override suspend fun getCustomerById(customerId: Int): Resource<Customer?> {
        return try {
            withContext(ioDispatcher) {
                Resource.Success(customerDao.getCustomerById(customerId))
            }
        }catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun addOrIgnoreCustomer(newCustomer: Customer): Resource<Boolean> {
        return try {
            val validateCustomerName = validateCustomerName(newCustomer.customerName)
            val validateCustomerPhone = validateCustomerPhone(newCustomer.customerPhone)
            val validateCustomerEmail = validateCustomerEmail(newCustomer.customerEmail)

            val hasError = listOf(
                validateCustomerName,
                validateCustomerPhone,
                validateCustomerEmail
            ).any { !it.successful }

            if (!hasError) {
                withContext(ioDispatcher){
                    val result = customerDao.insertOrIgnoreCustomer(newCustomer)

                    Resource.Success(result > 0)
                }
            }else {
                Resource.Error("Unable to validate customer")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to create new customer")
        }
    }

    override suspend fun updateCustomer(newCustomer: Customer): Resource<Boolean> {
        return try {
            val validateCustomerName = validateCustomerName(newCustomer.customerName)
            val validateCustomerPhone = validateCustomerPhone(newCustomer.customerPhone, newCustomer.customerId)
            val validateCustomerEmail = validateCustomerEmail(newCustomer.customerEmail)

            val hasError = listOf(
                validateCustomerName,
                validateCustomerPhone,
                validateCustomerEmail
            ).any { !it.successful }

            if (!hasError) {
                withContext(ioDispatcher){
                    val result = customerDao.updateCustomer(newCustomer)

                    Resource.Success(result > 0)
                }
            }else {
                Resource.Error("Unable to validate customer")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to update customer")
        }
    }

    override suspend fun upsertCustomer(newCustomer: Customer): Resource<Boolean> {
        return try {
            val validateCustomerName = validateCustomerName(newCustomer.customerName)
            val validateCustomerPhone = validateCustomerPhone(newCustomer.customerPhone, newCustomer.customerId)
            val validateCustomerEmail = validateCustomerEmail(newCustomer.customerEmail)

            val hasError = listOf(
                validateCustomerName,
                validateCustomerPhone,
                validateCustomerEmail
            ).any { !it.successful }

            if (!hasError) {
                withContext(ioDispatcher){
                    val result = customerDao.upsertCustomer(newCustomer)

                    Resource.Success(result > 0)
                }
            }else {
                Resource.Error("Unable to validate customer")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to create or update customer")
        }
    }

    override suspend fun deleteCustomer(customerId: Int): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val result = customerDao.deleteCustomer(customerId)

                Resource.Success(result > 0)
            }
        }catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete customer")
        }
    }

    override suspend fun deleteCustomers(customerIds: List<Int>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val result = customerDao.deleteCustomer(customerIds)

                Resource.Success(result > 0)
            }
        }catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete customers")
        }
    }

    override fun validateCustomerName(customerName: String?): ValidationResult {
        if(!customerName.isNullOrEmpty()) {
            if(customerName.length < 3) {
                return ValidationResult(
                    successful = false,
                    errorMessage = CustomerTestTags.CUSTOMER_NAME_LENGTH_ERROR,
                )
            }
        }

        return ValidationResult(
            successful = true
        )
    }

    override fun validateCustomerEmail(customerEmail: String?): ValidationResult {
        if(!customerEmail.isNullOrEmpty()) {
            if(!Patterns.EMAIL_ADDRESS.matcher(customerEmail).matches()) {
                return ValidationResult(
                    successful = false,
                    errorMessage = CustomerTestTags.CUSTOMER_EMAIL_VALID_ERROR,
                )
            }
        }

        return ValidationResult(
            successful = true
        )
    }

    override suspend fun validateCustomerPhone(
        customerPhone: String,
        customerId: Int?
    ): ValidationResult {
        if(customerPhone.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = CustomerTestTags.CUSTOMER_PHONE_EMPTY_ERROR,
            )
        }

        if(customerPhone.length < 10 || customerPhone.length > 10) {
            return ValidationResult(
                successful = false,
                errorMessage = CustomerTestTags.CUSTOMER_PHONE_LENGTH_ERROR,
            )
        }

        val containsLetters = customerPhone.any { it.isLetter() }

        if(containsLetters){
            return ValidationResult(
                successful = false,
                errorMessage = CustomerTestTags.CUSTOMER_PHONE_LETTER_ERROR
            )
        }

        val serverResult = withContext(ioDispatcher) {
            customerDao.findCustomerByPhone(customerPhone, customerId) != null
        }

        if(serverResult){
            return ValidationResult(
                successful = false,
                errorMessage = CustomerTestTags.CUSTOMER_PHONE_ALREADY_EXIST_ERROR
            )
        }

        return ValidationResult(
            successful = true
        )
    }
}