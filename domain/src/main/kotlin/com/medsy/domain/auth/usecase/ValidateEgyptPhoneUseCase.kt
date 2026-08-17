package com.medsy.domain.auth.usecase

import javax.inject.Inject

class ValidateEgyptPhoneUseCase @Inject constructor() {
    private val regex = Regex("^(\\+20|0)1[0125]\\d{8}$")

    operator fun invoke(phone: String): Boolean = regex.matches(phone)
}
