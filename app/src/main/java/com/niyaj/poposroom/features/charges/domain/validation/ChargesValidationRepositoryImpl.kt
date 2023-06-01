package com.niyaj.poposroom.features.charges.domain.validation

import com.niyaj.poposroom.features.charges.dao.ChargesDao
import com.niyaj.poposroom.features.charges.domain.utils.ChargesTestTags.CHARGES_NAME_ALREADY_EXIST_ERROR
import com.niyaj.poposroom.features.charges.domain.utils.ChargesTestTags.CHARGES_NAME_DIGIT_ERROR
import com.niyaj.poposroom.features.charges.domain.utils.ChargesTestTags.CHARGES_NAME_EMPTY_ERROR
import com.niyaj.poposroom.features.charges.domain.utils.ChargesTestTags.CHARGES_NAME_LENGTH_ERROR
import com.niyaj.poposroom.features.charges.domain.utils.ChargesTestTags.CHARGES_PRICE_EMPTY_ERROR
import com.niyaj.poposroom.features.charges.domain.utils.ChargesTestTags.CHARGES_PRICE_LESS_THAN_TEN_ERROR
import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import com.niyaj.poposroom.features.common.utils.ValidationResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChargesValidationRepositoryImpl @Inject constructor(
    private val chargesDao: ChargesDao,
    @Dispatcher(PoposDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : ChargesValidationRepository {
    override suspend fun validateChargesName(chargesName: String, chargesId: Int?): ValidationResult {
        if(chargesName.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = CHARGES_NAME_EMPTY_ERROR,
            )
        }

        if(chargesName.length < 5 ){
            return ValidationResult(
                successful = false,
                errorMessage = CHARGES_NAME_LENGTH_ERROR,
            )
        }

        if (chargesName.any { it.isDigit() }){
            return ValidationResult(
                successful = false,
                errorMessage = CHARGES_NAME_DIGIT_ERROR,
            )
        }

        val serverResult = withContext(ioDispatcher) {
            chargesDao.findChargesByName(chargesId, chargesName) != null
        }

        if (serverResult){
            return ValidationResult(
                successful = false,
                errorMessage = CHARGES_NAME_ALREADY_EXIST_ERROR,
            )
        }

        return ValidationResult(
            successful = true
        )
    }

    override fun validateChargesPrice(
        doesApplicable: Boolean,
        chargesPrice: Int
    ): ValidationResult {
        if(doesApplicable) {
            if(chargesPrice == 0){
                return ValidationResult(
                    successful = false,
                    errorMessage = CHARGES_PRICE_EMPTY_ERROR
                )
            }

            if(chargesPrice < 10){
                return ValidationResult(
                    successful = false,
                    errorMessage = CHARGES_PRICE_LESS_THAN_TEN_ERROR
                )
            }
        }

        return ValidationResult(
            successful = true
        )
    }
}