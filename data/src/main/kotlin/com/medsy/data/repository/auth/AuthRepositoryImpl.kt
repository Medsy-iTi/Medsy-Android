package com.medsy.data.repository.auth

import com.medsy.data.local.auth.TokenStorage
import com.medsy.data.mapper.auth.toDomain
import com.medsy.data.mapper.auth.toDto
import com.medsy.data.remote.auth.api.AuthApi
import com.medsy.data.remote.auth.dto.ApiResponseDto
import com.medsy.data.remote.auth.dto.LoginRequestDto
import com.medsy.data.remote.auth.dto.RefreshRequestDto
import com.medsy.data.remote.auth.dto.VerifyOtpRequestDto
import com.medsy.domain.auth.model.AuthSession
import com.medsy.domain.auth.model.RegisterParams
import com.medsy.domain.auth.repository.AuthRepository
import com.medsy.domain.common.DomainError
import com.medsy.domain.common.DomainResult
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val tokenStorage: TokenStorage,
    private val moshi: Moshi
) : AuthRepository {

    override suspend fun register(params: RegisterParams): DomainResult<Unit> =
        safeCall { api.register(params.toDto()) }.mapUnit { }

    override suspend fun verifyOtp(email: String, otpCode: String): DomainResult<AuthSession> =
        safeCall { api.verify(VerifyOtpRequestDto(email, otpCode)) }
            .map { it.toDomain() }
            .onSuccessSave()

    override suspend fun login(email: String, password: String): DomainResult<AuthSession> =
        safeCall { api.login(LoginRequestDto(email, password)) }
            .map { it.toDomain() }
            .onSuccessSave()

    override suspend fun refreshToken(): DomainResult<AuthSession> {
        val refreshToken = tokenStorage.refreshToken()
            ?: return DomainResult.Error(DomainError.Api("No refresh token available"))
        return safeCall { api.refresh(RefreshRequestDto(refreshToken)) }
            .map { it.toDomain() }
            .onSuccessSave()
    }

    override suspend fun logout(): DomainResult<Unit> {
        val refreshToken = tokenStorage.refreshToken()
        val result = if (refreshToken != null) {
            safeCall { api.logout(RefreshRequestDto(refreshToken)) }.mapUnit { }
        } else {
            DomainResult.Success(Unit)
        }
        tokenStorage.clear()
        return result
    }

    override fun observeSession(): Flow<AuthSession?> = tokenStorage.sessionFlow

    override suspend fun hasValidSession(): Boolean = tokenStorage.readSession() != null

    private fun DomainResult<AuthSession>.onSuccessSave(): DomainResult<AuthSession> {
        if (this is DomainResult.Success) tokenStorage.save(data)
        return this
    }

    private inline fun <T> DomainResult<T>.mapUnit(transform: (T) -> Unit): DomainResult<Unit> =
        when (this) {
            is DomainResult.Success -> { transform(data); DomainResult.Success(Unit) }
            is DomainResult.Error -> this
        }

    private inline fun <T, R> DomainResult<T>.map(transform: (T) -> R): DomainResult<R> =
        when (this) {
            is DomainResult.Success -> DomainResult.Success(transform(data))
            is DomainResult.Error -> this
        }

    private suspend fun <T> safeCall(
        block: suspend () -> Response<ApiResponseDto<T>>
    ): DomainResult<T> = try {
        val response = block()
        val body = response.body()
        when {
            response.isSuccessful && body?.data != null ->
                DomainResult.Success(body.data)

            response.isSuccessful && body != null -> {
                @Suppress("UNCHECKED_CAST")
                DomainResult.Success(Unit as T)
            }

            else -> {
                val message = body?.message
                    ?: parseErrorBody(response.errorBody()?.string())
                    ?: "Request failed (${response.code()})"
                DomainResult.Error(DomainError.Api(message, response.code()))
            }
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: IOException) {
        DomainResult.Error(DomainError.Network)
    } catch (e: Exception) {
        DomainResult.Error(DomainError.Unknown)
    }

    private fun parseErrorBody(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        return try {
            val type = Types.newParameterizedType(ApiResponseDto::class.java, Any::class.java)
            val adapter = moshi.adapter<ApiResponseDto<Any>>(type)
            adapter.fromJson(raw)?.message
        } catch (e: Exception) {
            null
        }
    }
}
