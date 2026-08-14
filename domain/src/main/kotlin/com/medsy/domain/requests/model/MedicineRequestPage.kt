package com.medsy.domain.requests.model

data class MedicineRequestPage(
    val content: List<MedicineRequest>,
    val pageNumber: Int,
    val totalPages: Int,
    val last: Boolean,
)
