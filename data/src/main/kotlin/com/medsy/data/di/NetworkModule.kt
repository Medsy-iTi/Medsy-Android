package com.medsy.data.di

import com.medsy.data.BuildConfig
import com.medsy.data.aichat.remote.AiService
import com.medsy.data.prescription.remote.AiInterceptor
import com.medsy.data.remote.api.ApiService
import com.medsy.data.remote.auth.AuthInterceptor
import com.medsy.data.remote.auth.TokenAuthenticator
import com.medsy.data.remote.auth.api.AuthApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        aiInterceptor: AiInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .callTimeout(2, TimeUnit.MINUTES)
            .addInterceptor { chain ->
                val original = chain.request()
                val url = original.url.newBuilder()
                    .setQueryParameter("lang", Locale.getDefault().language)
                    .build()

                val request = original.newBuilder()
                    .url(url)
                    .addHeader("Accept-Language", Locale.getDefault().language)
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(authInterceptor)
            .addInterceptor(aiInterceptor)
            .authenticator(tokenAuthenticator)
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(
                        HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BODY
                            redactHeader("Authorization")
                            redactHeader("Cookie")
                            redactHeader("Set-Cookie")
                        }
                    )
                }
            }
            .build()
    }


    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAiService(retrofit: Retrofit): AiService {
        return retrofit.create(AiService::class.java)
    }
}
