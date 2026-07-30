package com.medsy.domain.pharmacyprofile.model

data class PharmacyProfile(
    val id: Long,
    val name: String,
    val latitude: Double?,
    val longitude: Double?,
    val address: String?,
    val phoneNumber: String?,
)
