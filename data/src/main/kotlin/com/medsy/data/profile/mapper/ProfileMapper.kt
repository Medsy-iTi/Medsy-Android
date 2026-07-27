package com.medsy.data.profile.mapper

import com.medsy.data.profile.remote.dto.CustomerDto
import com.medsy.data.profile.remote.dto.UpdateCustomerProfileRequestDto
import com.medsy.domain.profile.model.Profile
import com.medsy.domain.profile.model.UpdateProfileParams

fun CustomerDto.toDomain(): Profile = Profile(
    id = id,
    email = email.orEmpty(),
    firstName = firstName.orEmpty(),
    lastName = lastName.orEmpty(),
    homeAddress = homeAddress,
    dob = dob,
    phoneNumber = phoneNumber.orEmpty(),
    latitude = deliveryLatitude,
    longitude = deliveryLongitude,
)

fun UpdateProfileParams.toDto(): UpdateCustomerProfileRequestDto =
    UpdateCustomerProfileRequestDto(
        firstName = firstName,
        lastName = lastName,
        homeAddress = homeAddress,
        dob = dob,
        deliveryLatitude = latitude,
        deliveryLongitude = longitude,
    )
