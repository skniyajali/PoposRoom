package com.niyaj.poposroom.features.customer.domain.validation

import android.util.Patterns
import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import com.niyaj.poposroom.features.common.utils.ValidationResult
import com.niyaj.poposroom.features.customer.dao.CustomerDao
import com.niyaj.poposroom.features.customer.domain.utils.CustomerTestTags.CUSTOMER_EMAIL_VALID_ERROR
import com.niyaj.poposroom.features.customer.domain.utils.CustomerTestTags.CUSTOMER_NAME_LENGTH_ERROR
import com.niyaj.poposroom.features.customer.domain.utils.CustomerTestTags.CUSTOMER_PHONE_ALREADY_EXIST_ERROR
import com.niyaj.poposroom.features.customer.domain.utils.CustomerTestTags.CUSTOMER_PHONE_EMPTY_ERROR
import com.niyaj.poposroom.features.customer.domain.utils.CustomerTestTags.CUSTOMER_PHONE_LENGTH_ERROR
import com.niyaj.poposroom.features.customer.domain.utils.CustomerTestTags.CUSTOMER_PHONE_LETTER_ERROR
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CustomerValidationRepositoryImpl @Inject constructor(
    private val customerDao: CustomerDao,
    @Dispatcher(PoposDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : CustomerValidationRepository {
    override fun validateCustomerName(customerName: String?): ValidationResult {
        if(!customerName.isNullOrEmpty()) {
            if(customerName.length < 3) {
                return ValidationResult(
                    successful = false,
                    errorMessage = CUSTOMER_NAME_LENGTH_ERROR,
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
                    errorMessage = CUSTOMER_EMAIL_VALID_ERROR,
                )
            }
        }

        return ValidationResult(
            successful = true
        )
    }

    override suspend fun validateCustomerPhone(
        customerId: Int?,
        customerPhone: String
    ): ValidationResult {
        if(customerPhone.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = CUSTOMER_PHONE_EMPTY_ERROR,
            )
        }

        if(customerPhone.length < 10 || customerPhone.length > 10) {
            return ValidationResult(
                successful = false,
                errorMessage = CUSTOMER_PHONE_LENGTH_ERROR,
            )
        }

        val containsLetters = customerPhone.any { it.isLetter() }

        if(containsLetters){
            return ValidationResult(
                successful = false,
                errorMessage = CUSTOMER_PHONE_LETTER_ERROR
            )
        }

        val serverResult = withContext(ioDispatcher) {
            customerDao.findCustomerByPhone(customerId, customerPhone) != null
        }

        if(serverResult){
            return ValidationResult(
                successful = false,
                errorMessage = CUSTOMER_PHONE_ALREADY_EXIST_ERROR
            )
        }

        return ValidationResult(
            successful = true
        )
    }
}