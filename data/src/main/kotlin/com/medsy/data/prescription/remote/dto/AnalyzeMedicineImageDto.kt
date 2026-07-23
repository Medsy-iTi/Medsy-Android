package com.medsy.data.prescription.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AnalyzedMedicineImageDto(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "arabicName") val arabicName: String? = null,
    @Json(name = "scientificName") val scientificName: String? = null,
    @Json(name = "price") val price: Double? = null,
    @Json(name = "imageUrl") val imageUrl: String? = null,
    @Json(name = "route") val route: String? = null,
    @Json(name = "productName") val productName: String? = null
)
