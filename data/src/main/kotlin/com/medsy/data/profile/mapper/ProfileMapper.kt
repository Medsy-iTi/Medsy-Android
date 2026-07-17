package com.medsy.data.profile.mapper

import com.medsy.data.profile.remote.dto.CustomerDto
import com.medsy.domain.profile.model.Profile

fun CustomerDto.toDomain(): Profile = Profile(
    id = id,
    email = email.orEmpty(),
    firstName = firstName.orEmpty(),
    lastName = lastName.orEmpty(),
    homeAddress = homeAddress,
    dob = dob,
    phoneNumber = phoneNumber.orEmpty(),
)
