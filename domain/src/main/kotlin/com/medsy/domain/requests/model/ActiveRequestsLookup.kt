package com.medsy.domain.requests.model

import com.medsy.domain.orders.model.MasterOrder

data class ActiveRequestsLookup(
    val activeRequests: List<MedicineRequest>,
    val resumableOrder: MasterOrder?,
)
