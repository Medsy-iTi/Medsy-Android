package com.medsy.data.remote.network

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)