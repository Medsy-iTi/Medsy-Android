package com.medsy.data.pharmacyprofile.mapper

import com.medsy.data.pharmacyprofile.remote.PharmacyProfileDto
import com.medsy.domain.pharmacyprofile.model.PharmacyProfile

fun PharmacyProfileDto.toDomain() = PharmacyProfile(
    id = id,
    name = name,
    latitude = latitude,
    longitude = longitude,
    address = address,
    phoneNumber = phoneNumber,
)
