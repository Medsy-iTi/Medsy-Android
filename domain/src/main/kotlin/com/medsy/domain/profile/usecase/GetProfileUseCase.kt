package com.medsy.domain.profile.usecase

import com.medsy.domain.profile.model.Profile
import javax.inject.Inject

class GetProfileUseCase @Inject constructor() {

    operator fun invoke(): Profile = Profile(
        name = "محمود الدمرداش",
        image = null,
        email = "mahmoudeldemerdash5@gmail.com",
        phoneNumber = "+20 1097662212",
        age = 24,
    )
}
