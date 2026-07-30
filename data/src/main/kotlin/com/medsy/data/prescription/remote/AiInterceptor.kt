package com.medsy.data.prescription.remote

import com.medsy.data.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AiInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        if (original.header(AI_KEY_FLAG) != null) {
            val apiKey = BuildConfig.AI_API_KEY

            val requestBuilder = original.newBuilder()
                .removeHeader(AI_KEY_FLAG)
                .addHeader("X-AI-Api-Key", apiKey)

            val request = requestBuilder.build()

            request.body?.let { body ->
                if (body is okhttp3.MultipartBody) {
                    body.parts.forEachIndexed { index, part ->
                        val headers = part.headers
                    }
                }
            }

            try {
                val response = chain.proceed(request)
                return response
            } catch (e: Exception) {
                throw e
            }
        }
        return chain.proceed(original)
    }

    companion object {
        const val AI_KEY_FLAG = "X-Needs-AI-Key"
    }
}
