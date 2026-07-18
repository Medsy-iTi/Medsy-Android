package com.medsy.domain.common

sealed interface MedsyResult<out D, out E : MedsyError> {
    data class Success<out D>(val data: D) : MedsyResult<D, Nothing>
    data class Error<out E : MedsyError>(val error: E) : MedsyResult<Nothing, E>
}

typealias EmptyMedsyResult<E> = MedsyResult<Unit, E>

inline fun <D, E : MedsyError, R> MedsyResult<D, E>.map(
    transform: (D) -> R,
): MedsyResult<R, E> = when (this) {
    is MedsyResult.Success -> MedsyResult.Success(transform(data))
    is MedsyResult.Error -> this
}

inline fun <D, E : MedsyError, R> MedsyResult<D, E>.fold(
    onSuccess: (D) -> R,
    onError: (E) -> R,
): R = when (this) {
    is MedsyResult.Success -> onSuccess(data)
    is MedsyResult.Error -> onError(error)
}

inline fun <D, E : MedsyError> MedsyResult<D, E>.onSuccess(
    action: (D) -> Unit,
): MedsyResult<D, E> {
    if (this is MedsyResult.Success) action(data)
    return this
}

inline fun <D, E : MedsyError> MedsyResult<D, E>.onError(
    action: (E) -> Unit,
): MedsyResult<D, E> {
    if (this is MedsyResult.Error) action(error)
    return this
}

fun <D, E : MedsyError> MedsyResult<D, E>.asEmptyDataResult(): EmptyMedsyResult<E> =
    map { Unit }
