package com.niyaj.data.data.repository

import android.util.Patterns
import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.Resource
import com.niyaj.common.result.ValidationResult
import com.niyaj.data.mapper.toEntity
import com.niyaj.data.repository.CustomerRepository
import com.niyaj.data.repository.validation.CustomerValidationRepository
import com.niyaj.database.dao.CustomerDao
import com.niyaj.database.model.asExternalModel
import com.niyaj.model.Customer
import com.niyaj.model.searchCustomer
import com.niyaj.data.utils.CustomerTestTags
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

class CustomerRepositoryImpl(
    private val customerDao: CustomerDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : CustomerRepository, CustomerValidationRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getAllCustomer(searchText: String): Flow<List<Customer>> {
        return withContext(ioDispatcher) {
            customerDao.getAllCustomer().mapLatest { it ->
                it.map {
                    it.asExternalModel()
                }.searchCustomer(searchText)
            }
        }
    }

    override suspend fun getCustomerById(customerId: Int): Resource<Customer?> {
        return try {
            withContext(ioDispatcher) {
                Resource.Success(customerDao.getCustomerById(customerId)?.asExternalModel())
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun addOrIgnoreCustomer(newCustomer: Customer): Int {
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
                withContext(ioDispatcher) {
                    customerDao.insertOrIgnoreCustomer(newCustomer.toEntity()).toInt()
                }
            } else {
                0
            }
        } catch (e: Exception) {
            0
        }
    }

    override suspend fun updateCustomer(newCustomer: Customer): Resource<Boolean> {
        return try {
            val validateCustomerName = validateCustomerName(newCustomer.customerName)
            val validateCustomerPhone =
                validateCustomerPhone(newCustomer.customerPhone, newCustomer.customerId)
            val validateCustomerEmail = validateCustomerEmail(newCustomer.customerEmail)

            val hasError = listOf(
                validateCustomerName,
                validateCustomerPhone,
                validateCustomerEmail
            ).any { !it.successful }

            if (!hasError) {
                withContext(ioDispatcher) {
                    val result = customerDao.updateCustomer(newCustomer.toEntity())

                    Resource.Success(result > 0)
                }
            } else {
                Resource.Error("Unable to validate customer")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to update customer")
        }
    }

    override suspend fun upsertCustomer(newCustomer: Customer): Resource<Boolean> {
        return try {
            val validateCustomerName = validateCustomerName(newCustomer.customerName)
            val validateCustomerPhone =
                validateCustomerPhone(newCustomer.customerPhone, newCustomer.customerId)
            val validateCustomerEmail = validateCustomerEmail(newCustomer.customerEmail)

            val hasError = listOf(
                validateCustomerName,
                validateCustomerPhone,
                validateCustomerEmail
            ).any { !it.successful }

            if (!hasError) {
                withContext(ioDispatcher) {
                    val result = customerDao.upsertCustomer(newCustomer.toEntity())

                    Resource.Success(result > 0)
                }
            } else {
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
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete customer")
        }
    }

    override suspend fun deleteCustomers(customerIds: List<Int>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val result = customerDao.deleteCustomer(customerIds)

                Resource.Success(result > 0)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete customers")
        }
    }

    override fun validateCustomerName(customerName: String?): ValidationResult {
        if (!customerName.isNullOrEmpty()) {
            if (customerName.length < 3) {
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
        if (!customerEmail.isNullOrEmpty()) {
            if (!Patterns.EMAIL_ADDRESS.matcher(customerEmail).matches()) {
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
        customerId: Int?,
    ): ValidationResult {
        if (customerPhone.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = CustomerTestTags.CUSTOMER_PHONE_EMPTY_ERROR,
            )
        }

        if (customerPhone.length < 10 || customerPhone.length > 10) {
            return ValidationResult(
                successful = false,
                errorMessage = CustomerTestTags.CUSTOMER_PHONE_LENGTH_ERROR,
            )
        }

        val containsLetters = customerPhone.any { it.isLetter() }

        if (containsLetters) {
            return ValidationResult(
                successful = false,
                errorMessage = CustomerTestTags.CUSTOMER_PHONE_LETTER_ERROR
            )
        }

        val serverResult = withContext(ioDispatcher) {
            customerDao.findCustomerByPhone(customerPhone, customerId) != null
        }

        if (serverResult) {
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