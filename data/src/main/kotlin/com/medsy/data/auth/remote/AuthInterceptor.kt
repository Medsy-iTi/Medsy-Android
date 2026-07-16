package com.medsy.data.auth.remote

import com.medsy.data.auth.local.TokenStorage
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenStorage: TokenStorage
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        if (original.header("No-Auth") != null) {
            val stripped = original.newBuilder().removeHeader("No-Auth").build()
            return chain.proceed(stripped)
        }
        val token = tokenStorage.accessToken()
        val request = if (token != null) {
            original.newBuilder().addHeader("Authorization", "Bearer $token").build()
        } else original
        return chain.proceed(request)
    }
}
