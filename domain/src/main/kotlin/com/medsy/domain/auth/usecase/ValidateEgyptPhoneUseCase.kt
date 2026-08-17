package com.medsy.domain.auth.usecase

import javax.inject.Inject

class ValidateEgyptPhoneUseCase @Inject constructor() {
    // Accepts local 01[0-2,5]xxxxxxxx (11 digits) or +20 1[0-2,5]xxxxxxxx
    private val regex = Regex("^(\\+20|0)1[0125]\\d{8}$")

    operator fun invoke(phone: String): Boolean = regex.matches(phone)
}
