package com.medsy.data.remote.auth

import com.medsy.data.local.auth.TokenStorage
import com.medsy.data.mapper.auth.toDomain
import com.medsy.data.remote.auth.api.AuthApi
import com.medsy.data.remote.auth.dto.RefreshRequestDto
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class TokenAuthenticator @Inject constructor(
    private val tokenStorage: TokenStorage,
    private val authApiProvider: Provider<AuthApi>
) : Authenticator {

    private val mutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) {
            tokenStorage.clear()
            return null
        }

        val requestAccessToken = response.request.header("Authorization")?.removePrefix("Bearer ")

        return runBlocking {
            mutex.withLock {
                val currentAccessToken = tokenStorage.accessToken()
                if (currentAccessToken != null && currentAccessToken != requestAccessToken) {
                    return@runBlocking response.request.newBuilder()
                        .header("Authorization", "Bearer $currentAccessToken")
                        .build()
                }

                val refreshToken = tokenStorage.refreshToken() ?: run {
                    tokenStorage.clear()
                    return@runBlocking null
                }

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
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) { count++; prior = prior.priorResponse }
        return count
    }
}
