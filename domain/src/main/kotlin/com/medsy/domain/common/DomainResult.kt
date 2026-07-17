package com.medsy.domain.common

sealed class DomainResult<out T> {
    data class Success<T>(val data: T) : DomainResult<T>()
    data class Error(val error: DomainError) : DomainResult<Nothing>()
}

sealed class DomainError {
    data class Api(val message: String, val code: Int? = null) : DomainError()
    data object Network : DomainError()
    data object Unknown : DomainError()
}
