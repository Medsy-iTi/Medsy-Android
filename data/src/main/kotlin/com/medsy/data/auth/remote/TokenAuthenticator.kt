package com.medsy.data.auth.remote

import com.medsy.data.auth.local.TokenStorage
import com.medsy.data.auth.mapper.toDomain
import com.medsy.data.auth.remote.api.AuthApi
import com.medsy.data.auth.remote.dto.RefreshRequestDto
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider

class TokenAuthenticator @Inject constructor(
    private val tokenStorage: TokenStorage,
    private val authApiProvider: Provider<AuthApi> // Provider breaks circular DI
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) {
            tokenStorage.clear()
            return null
        }
        val refreshToken = tokenStorage.refreshToken() ?: run {
            tokenStorage.clear()
            return null
        }

        return runBlocking {
            val result = runCatching { authApiProvider.get().refresh(RefreshRequestDto(refreshToken)) }
                .getOrNull()

            val body = result?.body()
            if (result == null || !result.isSuccessful || body?.data == null) {
                tokenStorage.clear()
                return@runBlocking null
            }

            val newData = body.data
            val newSession = newData.toDomain()
            tokenStorage.save(newSession)

            response.request.newBuilder()
                .header("Authorization", "Bearer ${newSession.accessToken}")
                .build()
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) { count++; prior = prior.priorResponse }
        return count
    }
}
