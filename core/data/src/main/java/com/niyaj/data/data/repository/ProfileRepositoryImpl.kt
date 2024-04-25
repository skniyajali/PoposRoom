package com.niyaj.data.data.repository

import android.util.Patterns
import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.Resource
import com.niyaj.common.result.ValidationResult
import com.niyaj.common.tags.ProfileTestTags
import com.niyaj.data.mapper.toEntity
import com.niyaj.data.repository.ProfileRepository
import com.niyaj.data.repository.validation.ProfileValidationRepository
import com.niyaj.database.dao.ProfileDao
import com.niyaj.database.model.ProfileEntity
import com.niyaj.database.model.asExternalModel
import com.niyaj.model.Profile
import com.niyaj.model.Profile.Companion.RESTAURANT_ID
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext


class ProfileRepositoryImpl(
    private val profileDao: ProfileDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : ProfileRepository, ProfileValidationRepository {

    override fun getProfileInfo(): Flow<Profile> {
        return profileDao.getProfileInfo().mapLatest {
            it?.asExternalModel() ?: Profile.defaultProfileInfo
        }
    }

    override suspend fun insertOrUpdateProfile(profile: Profile): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val result = profileDao.insertOrUpdateProfile(profile.toEntity())

                Resource.Success(result > 0)
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun updateRestaurantLogo(imageName: String): Resource<Boolean> {
        return withContext(ioDispatcher) {
            try {
                val profile = profileDao.getProfileById()

                val newProfile = ProfileEntity(
                    restaurantId = RESTAURANT_ID,
                    name = profile?.name ?: "",
                    email = profile?.email ?: "",
                    primaryPhone = profile?.primaryPhone ?: "",
                    secondaryPhone = profile?.secondaryPhone ?: "",
                    tagline = profile?.tagline ?: "",
                    description = profile?.description ?: "",
                    address = profile?.address ?: "",
                    logo = imageName,
                    printLogo = profile?.printLogo ?: "",
                    paymentQrCode = profile?.paymentQrCode ?: "",
                    createdAt = profile?.createdAt ?: System.currentTimeMillis().toString(),
                    updatedAt = System.currentTimeMillis().toString(),
                )

                val result = profileDao.insertOrUpdateProfile(newProfile)

                Resource.Success(result > 0)
            } catch (e: Exception) {
                Resource.Error(e.message)
            }
        }
    }

    override suspend fun updatePrintLogo(imageName: String): Resource<Boolean> {
        return withContext(ioDispatcher) {
            try {
                val profile = profileDao.getProfileById()

                val newProfile = ProfileEntity(
                    restaurantId = RESTAURANT_ID,
                    name = profile?.name ?: "",
                    email = profile?.email ?: "",
                    primaryPhone = profile?.primaryPhone ?: "",
                    secondaryPhone = profile?.secondaryPhone ?: "",
                    tagline = profile?.tagline ?: "",
                    description = profile?.description ?: "",
                    address = profile?.address ?: "",
                    logo = profile?.logo ?: "",
                    printLogo = imageName,
                    paymentQrCode = profile?.paymentQrCode ?: "",
                    createdAt = profile?.createdAt ?: System.currentTimeMillis().toString(),
                    updatedAt = System.currentTimeMillis().toString(),
                )

                val result = profileDao.insertOrUpdateProfile(newProfile)

                Resource.Success(result > 0)
            } catch (e: Exception) {
                Resource.Error(e.message)
            }
        }
    }

    override fun validateName(name: String): ValidationResult {
        if (name.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = ProfileTestTags.NAME_EMPTY_ERROR
            )
        }

        if (name.length < 5) {
            return ValidationResult(
                successful = false,
                errorMessage = ProfileTestTags.NAME_LENGTH_ERROR
            )
        }

        if (name.any { it.isDigit() }) {
            return ValidationResult(
                successful = false,
                errorMessage = ProfileTestTags.NAME_DIGITS_ERROR
            )
        }

        return ValidationResult(true)
    }

    override fun validateEmail(email: String): ValidationResult {
        if (email.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = ProfileTestTags.EMAIL_EMPTY_ERROR
            )
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return ValidationResult(
                successful = false,
                errorMessage = ProfileTestTags.EMAIL_NOT_VALID,
            )
        }

        return ValidationResult(true)
    }

    override fun validatePrimaryPhone(phone: String): ValidationResult {

        if (phone.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = ProfileTestTags.P_PHONE_EMPTY_ERROR
            )
        }

        if (phone.any { it.isLetter() }) {
            return ValidationResult(
                successful = false,
                errorMessage = ProfileTestTags.P_PHONE_CHAR_ERROR
            )
        }

        if (phone.length != 10) {
            return ValidationResult(
                successful = false,
                errorMessage = ProfileTestTags.P_PHONE_LENGTH_ERROR
            )
        }

        return ValidationResult(true)
    }

    override fun validateSecondaryPhone(phone: String): ValidationResult {
        if (phone.isNotEmpty()) {
            if (phone.any { it.isLetter() }) {
                return ValidationResult(
                    successful = false,
                    errorMessage = ProfileTestTags.S_PHONE_CHAR_ERROR
                )
            }

            if (phone.length != 10) {
                return ValidationResult(
                    successful = false,
                    errorMessage = ProfileTestTags.S_PHONE_LENGTH_ERROR
                )
            }
        }

        return ValidationResult(true)
    }

    override fun validateTagline(tagline: String): ValidationResult {
        if (tagline.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = ProfileTestTags.TAG_EMPTY_ERROR
            )
        }

        if (tagline.length > 40) {
            return ValidationResult(
                successful = false,
                errorMessage = ProfileTestTags.TAG_LENGTH_ERROR
            )
        }

        return ValidationResult(true)
    }

    override fun validateDescription(description: String): ValidationResult {
        if (description.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = ProfileTestTags.DESC_EMPTY_ERROR
            )
        }

        if (description.length > 120) {
            return ValidationResult(
                successful = false,
                errorMessage = ProfileTestTags.DESC_LENGTH_ERROR
            )
        }

        return ValidationResult(true)
    }

    override fun validateAddress(address: String): ValidationResult {
        if (address.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = ProfileTestTags.ADDRESS_EMPTY_ERROR
            )
        }

        return ValidationResult(true)
    }

    override fun validateLogo(logo: String): ValidationResult {
        if (logo.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = ProfileTestTags.LOGO_EMPTY_ERROR
            )
        }

        return ValidationResult(true)
    }
}