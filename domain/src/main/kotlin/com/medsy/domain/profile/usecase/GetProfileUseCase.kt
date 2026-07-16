package com.medsy.domain.profile.usecase

import com.medsy.domain.profile.model.Profile
import javax.inject.Inject

class GetProfileUseCase @Inject constructor() {

    operator fun invoke(): Profile = Profile(
        name = "Mahmoud ELDemerdash",
        image = null,
        email = "mahmoudeldemerdash5@gmail.com",
        phoneNumber = "+20 109 766 2212",
        age = 24,
    )
}
