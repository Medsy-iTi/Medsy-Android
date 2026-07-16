# Medsy Android — Auth Feature Implementation Plan

**Purpose:** This document is a direct implementation spec for an AI coding agent (or developer) to build the Register/Login/OTP/Session auth feature in the Medsy Android project. Follow the modules, file paths, and code exactly. Architecture: Clean Architecture + MVI + Modularization (domain / data / presentation / app / designsystem) + Hilt DI + Retrofit.

---

## 0. Ground Rules for the Implementing Agent

1. Respect module boundaries. `domain` has **zero** Android/Retrofit/Hilt imports — pure Kotlin only.
2. Dependency direction: `presentation → domain ← data`. Never let `data` import from `presentation` or vice versa.
3. Every network call goes through a `UseCase`, never call `Repository` or `Api` directly from a `ViewModel`.
4. Use `Result`-style wrapper (`DomainResult`) everywhere in domain/data boundary — no throwing exceptions across layers.
5. All screens follow strict MVI: `State` (single data class), `Intent` (sealed interface), `Effect` (sealed interface, one-shot via Channel).
6. Create files in the exact paths listed below, matching existing package `com.medsy.<module>`.
7. Do not implement UI styling decisions — use existing `designsystem` module components/theme where they exist; otherwise use plain Material3 composables so this can be restyled later.
8. Base URL, API keys, etc. go in `BuildConfig` / `local.properties` — do not hardcode.

---

## 1. API Contract (already defined — implement to match exactly)

Base path: `/api/v1/auth`

| Method | Path | Auth required | Purpose |
|---|---|---|---|
| POST | `/register` | No | Create pending account, triggers OTP email |
| POST | `/verify` | No | Verify OTP, finalizes registration, returns tokens |
| POST | `/login` | No | Email+password login, returns tokens |
| POST | `/refresh` | No | Exchange refresh token for new access+refresh token |
| POST | `/logout` | No (send refresh token in body) | Revoke refresh token |

Common response envelope:
```json
{ "success": true, "message": "string", "data": { ... } }
```

`AuthData` (returned by `/verify`, `/login`, `/refresh`):
```json
{
  "accessToken": "string",
  "refreshToken": "string",
  "user": {
    "id": 0, "email": "string", "firstName": "string", "lastName": "string",
    "role": "CUSTOMER", "homeAddress": "string", "dob": "2026-07-16"
  }
}
```

`RegisterRequest`:
```json
{
  "email": "user@example.com", "phoneNumber": "01053395548",
  "firstName": "string", "lastName": "string", "password": "string",
  "role": "CUSTOMER", "homeAddress": "string", "dob": "2026-07-16", "pharmacyId": 0
}
```

`VerifyOtpRequest`: `{ "email": "string", "otpCode": "string" }`
`LoginRequest`: `{ "email": "string", "password": "string" }`
`RefreshRequest` / `LogoutRequest`: `{ "refreshToken": "string" }`

Error responses (400/401) use the **same envelope** with `success: false` and a human-readable `message` — surface `message` directly in the UI as the inline error.

---

## 2. Module: `domain`

Path root: `domain/src/main/kotlin/com/medsy/domain`

### 2.1 `common/DomainResult.kt`
```kotlin
package com.medsy.domain.common

sealed class DomainResult<out T> {
    data class Success<T>(val data: T) : DomainResult<T>()
    data class Error(val error: DomainError) : DomainResult<Nothing>()
}

sealed class DomainError {
    data class Api(val message: String, val code: Int? = null) : DomainError()
    object Network : DomainError()
    object Unknown : DomainError()
}

inline fun <T> DomainResult<T>.onSuccess(action: (T) -> Unit): DomainResult<T> {
    if (this is DomainResult.Success) action(data)
    return this
}

inline fun <T> DomainResult<T>.onError(action: (DomainError) -> Unit): DomainResult<T> {
    if (this is DomainResult.Error) action(error)
    return this
}
```

### 2.2 `auth/model/`
- `User.kt`
```kotlin
package com.medsy.domain.auth.model

data class User(
    val id: Long,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: Role,
    val homeAddress: String?,
    val dob: String?
)

enum class Role { CUSTOMER, PHARMACIST, ADMIN }
```
- `AuthSession.kt`
```kotlin
package com.medsy.domain.auth.model

data class AuthSession(
    val accessToken: String,
    val refreshToken: String,
    val user: User
)
```
- `RegisterParams.kt`
```kotlin
package com.medsy.domain.auth.model

data class RegisterParams(
    val email: String,
    val phoneNumber: String,
    val firstName: String,
    val lastName: String,
    val password: String,
    val role: Role,
    val homeAddress: String?,
    val dob: String,
    val pharmacyId: Long? = null
)
```

### 2.3 `auth/repository/AuthRepository.kt`
```kotlin
package com.medsy.domain.auth.repository

import com.medsy.domain.auth.model.AuthSession
import com.medsy.domain.auth.model.RegisterParams
import com.medsy.domain.common.DomainResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun register(params: RegisterParams): DomainResult<Unit>
    suspend fun verifyOtp(email: String, otpCode: String): DomainResult<AuthSession>
    suspend fun login(email: String, password: String): DomainResult<AuthSession>
    suspend fun refreshToken(): DomainResult<AuthSession>
    suspend fun logout(): DomainResult<Unit>
    fun observeSession(): Flow<AuthSession?>
    suspend fun hasValidSession(): Boolean
}
```

### 2.4 `auth/usecase/` — one file each
- `RegisterUseCase.kt` — validates required fields non-blank, delegates to repo.
- `VerifyOtpUseCase.kt` — validates `otpCode.length == 6` and digits-only before calling repo.
- `LoginUseCase.kt` — validates email/password non-blank.
- `RefreshTokenUseCase.kt` — thin passthrough to repo (used by Authenticator indirectly via repo, and optionally by a manual "retry" flow).
- `LogoutUseCase.kt` — calls repo logout; **must clear local session even if network call fails** (handle inside `AuthRepositoryImpl`, not here).
- `ObserveSessionUseCase.kt` — wraps `repository.observeSession()`.
- `ValidateEgyptPhoneUseCase.kt`:
```kotlin
package com.medsy.domain.auth.usecase

import javax.inject.Inject

class ValidateEgyptPhoneUseCase @Inject constructor() {
    // Accepts local 01[0-2,5]xxxxxxxx (11 digits) or +20 1[0-2,5]xxxxxxxx
    private val regex = Regex("^(\\+20|0)1[0125]\\d{8}$")

    operator fun invoke(phone: String): Boolean = regex.matches(phone.trim())
}
```

Template for use cases (apply this shape to all of them):
```kotlin
package com.medsy.domain.auth.usecase

import com.medsy.domain.auth.model.AuthSession
import com.medsy.domain.auth.repository.AuthRepository
import com.medsy.domain.common.DomainError
import com.medsy.domain.common.DomainResult
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): DomainResult<AuthSession> {
        if (email.isBlank() || password.isBlank()) {
            return DomainResult.Error(DomainError.Api("Email and password are required"))
        }
        return repository.login(email.trim(), password)
    }
}
```

---

## 3. Module: `data`

Path root: `data/src/main/kotlin/com/medsy/data`

### 3.1 `auth/remote/dto/`
- `ApiResponseDto.kt`
```kotlin
package com.medsy.data.auth.remote.dto

data class ApiResponseDto<T>(
    val success: Boolean,
    val message: String?,
    val data: T?
)
```
- `UserDto.kt`, `AuthDataDto.kt`, `RegisterRequestDto.kt`, `LoginRequestDto.kt`, `VerifyOtpRequestDto.kt`, `RefreshRequestDto.kt` — mirror the JSON in section 1 field-for-field, using your project's JSON lib annotations (Moshi `@JsonClass(generateAdapter = true)` + `@Json(name = ...)`, or kotlinx-serialization `@Serializable` — **check which converter is already configured in the project's Retrofit setup before choosing**).

### 3.2 `auth/remote/api/AuthApi.kt`
```kotlin
package com.medsy.data.auth.remote.api

import com.medsy.data.auth.remote.dto.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {
    @POST("api/v1/auth/register")
    suspend fun register(@Body body: RegisterRequestDto): Response<ApiResponseDto<Unit>>

    @POST("api/v1/auth/verify")
    suspend fun verify(@Body body: VerifyOtpRequestDto): Response<ApiResponseDto<AuthDataDto>>

    @POST("api/v1/auth/login")
    suspend fun login(@Body body: LoginRequestDto): Response<ApiResponseDto<AuthDataDto>>

    @Header("No-Auth")
    @POST("api/v1/auth/refresh")
    suspend fun refresh(@Body body: RefreshRequestDto): Response<ApiResponseDto<AuthDataDto>>

    @POST("api/v1/auth/logout")
    suspend fun logout(@Body body: RefreshRequestDto): Response<ApiResponseDto<Unit>>
}
```
> Note: `@Header("No-Auth")` is a marker read by `AuthInterceptor` (section 3.4) to skip attaching the access token on public endpoints. Add it to register/verify/login/refresh/logout.

### 3.3 `auth/local/TokenStorage.kt`
```kotlin
package com.medsy.data.auth.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.medsy.domain.auth.model.AuthSession
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenStorage @Inject constructor(
    @ApplicationContext context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "medsy_auth_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val _sessionFlow = MutableStateFlow(readSessionInternal())
    val sessionFlow = _sessionFlow.asStateFlow()

    fun save(session: AuthSession) {
        prefs.edit()
            .putString(KEY_ACCESS, session.accessToken)
            .putString(KEY_REFRESH, session.refreshToken)
            .putLong(KEY_USER_ID, session.user.id)
            .putString(KEY_USER_EMAIL, session.user.email)
            .putString(KEY_USER_FIRST, session.user.firstName)
            .putString(KEY_USER_LAST, session.user.lastName)
            .putString(KEY_USER_ROLE, session.user.role.name)
            .putString(KEY_USER_ADDRESS, session.user.homeAddress)
            .putString(KEY_USER_DOB, session.user.dob)
            .apply()
        _sessionFlow.value = session
    }

    fun clear() {
        prefs.edit().clear().apply()
        _sessionFlow.value = null
    }

    fun accessToken(): String? = prefs.getString(KEY_ACCESS, null)
    fun refreshToken(): String? = prefs.getString(KEY_REFRESH, null)
    fun readSession(): AuthSession? = _sessionFlow.value

    private fun readSessionInternal(): AuthSession? {
        // rebuild AuthSession from prefs, or return null if access/refresh missing
        // implement using the User(...) constructor + Role.valueOf(...)
        TODO("Implement rebuild-from-prefs, return null if KEY_ACCESS missing")
    }

    companion object {
        private const val KEY_ACCESS = "access_token"
        private const val KEY_REFRESH = "refresh_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_FIRST = "user_first_name"
        private const val KEY_USER_LAST = "user_last_name"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_USER_ADDRESS = "user_address"
        private const val KEY_USER_DOB = "user_dob"
    }
}
```
> Implementing agent: fill in `readSessionInternal()` per the TODO. Requires `androidx.security:security-crypto` dependency in `data/build.gradle.kts`.

### 3.4 `auth/remote/AuthInterceptor.kt`
```kotlin
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
```

### 3.5 `auth/remote/TokenAuthenticator.kt`
```kotlin
package com.medsy.data.auth.remote

import com.medsy.data.auth.local.TokenStorage
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
    private val authApiProvider: Provider<AuthApi> // Provider breaks circular DI (OkHttp needs Authenticator, AuthApi needs Retrofit needs OkHttp)
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

            val newData = body.data!!
            // map DTO -> AuthSession and persist (reuse mapper from section 3.6)
            val newSession = newData.toDomain() // extension defined in AuthMappers.kt
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
```

### 3.6 `auth/mapper/AuthMappers.kt`
```kotlin
package com.medsy.data.auth.mapper

import com.medsy.data.auth.remote.dto.AuthDataDto
import com.medsy.data.auth.remote.dto.UserDto
import com.medsy.domain.auth.model.AuthSession
import com.medsy.domain.auth.model.RegisterParams
import com.medsy.domain.auth.model.Role
import com.medsy.domain.auth.model.User

fun AuthDataDto.toDomain(): AuthSession = AuthSession(
    accessToken = accessToken,
    refreshToken = refreshToken,
    user = user.toDomain()
)

fun UserDto.toDomain(): User = User(
    id = id, email = email, firstName = firstName, lastName = lastName,
    role = Role.valueOf(role), homeAddress = homeAddress, dob = dob
)

fun RegisterParams.toDto() = com.medsy.data.auth.remote.dto.RegisterRequestDto(
    email = email, phoneNumber = phoneNumber, firstName = firstName, lastName = lastName,
    password = password, role = role.name, homeAddress = homeAddress, dob = dob, pharmacyId = pharmacyId
)
```

### 3.7 `auth/repository/AuthRepositoryImpl.kt`
```kotlin
package com.medsy.data.auth.repository

import com.medsy.data.auth.local.TokenStorage
import com.medsy.data.auth.mapper.toDomain
import com.medsy.data.auth.mapper.toDto
import com.medsy.data.auth.remote.api.AuthApi
import com.medsy.data.auth.remote.dto.LoginRequestDto
import com.medsy.data.auth.remote.dto.RefreshRequestDto
import com.medsy.data.auth.remote.dto.VerifyOtpRequestDto
import com.medsy.domain.auth.model.AuthSession
import com.medsy.domain.auth.model.RegisterParams
import com.medsy.domain.auth.repository.AuthRepository
import com.medsy.domain.common.DomainError
import com.medsy.domain.common.DomainResult
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val tokenStorage: TokenStorage
) : AuthRepository {

    override suspend fun register(params: RegisterParams): DomainResult<Unit> =
        safeCall { api.register(params.toDto()) }.map { }

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
            safeCall { api.logout(RefreshRequestDto(refreshToken)) }.map { }
        } else {
            DomainResult.Success(Unit)
        }
        tokenStorage.clear() // ALWAYS clear locally, even if the network call failed
        return result
    }

    override fun observeSession(): Flow<AuthSession?> = tokenStorage.sessionFlow

    override suspend fun hasValidSession(): Boolean = tokenStorage.readSession() != null

    private fun DomainResult<AuthSession>.onSuccessSave(): DomainResult<AuthSession> {
        if (this is DomainResult.Success) tokenStorage.save(data)
        return this
    }

    private inline fun <T> DomainResult<T>.map(transform: (T) -> Unit): DomainResult<Unit> =
        when (this) {
            is DomainResult.Success -> { transform(data); DomainResult.Success(Unit) }
            is DomainResult.Error -> this
        }

    // Generic map for non-Unit transforms (used above for AuthSession); keep both overloads
    private inline fun <T, R> DomainResult<T>.map(transform: (T) -> R): DomainResult<R> =
        when (this) {
            is DomainResult.Success -> DomainResult.Success(transform(data))
            is DomainResult.Error -> this
        }

    private suspend fun <T> safeCall(
        block: suspend () -> Response<com.medsy.data.auth.remote.dto.ApiResponseDto<T>>
    ): DomainResult<T> = try {
        val response = block()
        val body = response.body()
        if (response.isSuccessful && body?.data != null) {
            DomainResult.Success(body.data)
        } else if (response.isSuccessful && body != null) {
            // e.g. register returns data:null but success:true
            @Suppress("UNCHECKED_CAST")
            DomainResult.Success(Unit as T)
        } else {
            val message = body?.message
                ?: response.errorBody()?.string()?.let { parseMessageFallback(it) }
                ?: "Request failed (${response.code()})"
            DomainResult.Error(DomainError.Api(message, response.code()))
        }
    } catch (e: IOException) {
        DomainResult.Error(DomainError.Network)
    } catch (e: Exception) {
        DomainResult.Error(DomainError.Unknown)
    }

    private fun parseMessageFallback(raw: String): String? = null // optionally parse JSON manually here
}
```
> Note: Kotlin will complain about two `map` overloads with erased generics colliding — **the implementing agent should rename one to `mapUnit` or just inline the transform logic per-function instead of a shared `map` helper.** Flagging this explicitly so it's not copy-pasted broken.

### 3.8 DI: `auth/di/NetworkModule.kt` and `auth/di/AuthModule.kt`
```kotlin
package com.medsy.data.auth.di

import com.medsy.data.auth.remote.AuthInterceptor
import com.medsy.data.auth.remote.TokenAuthenticator
import com.medsy.data.auth.remote.api.AuthApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @Provides @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .authenticator(tokenAuthenticator)
        .build()

    @Provides @Singleton
    fun provideRetrofit(client: OkHttpClient, moshi: Moshi): Retrofit = Retrofit.Builder()
        .baseUrl(com.medsy.data.BuildConfig.BASE_URL) // define BASE_URL in data/build.gradle.kts buildConfigField
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    @Provides @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)
}
```
```kotlin
package com.medsy.data.auth.di

import com.medsy.data.auth.repository.AuthRepositoryImpl
import com.medsy.domain.auth.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthBindModule {
    @Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}
```
> If Moshi isn't the converter already used elsewhere in the project, swap to whatever's configured (kotlinx-serialization or Gson) and adjust DTO annotations accordingly — check `app/build.gradle.kts` / existing `data` module first.

---

## 4. Module: `presentation`

Path root: `presentation/src/main/kotlin/com/medsy/presentation/auth`

### 4.1 Structure
```
auth/
 ├─ welcome/WelcomeScreen.kt
 ├─ register/
 │   ├─ RegisterContract.kt
 │   ├─ RegisterViewModel.kt
 │   └─ RegisterScreen.kt
 ├─ otp/
 │   ├─ OtpContract.kt
 │   ├─ OtpViewModel.kt
 │   └─ OtpScreen.kt
 ├─ login/
 │   ├─ LoginContract.kt
 │   ├─ LoginViewModel.kt
 │   └─ LoginScreen.kt
 └─ navigation/AuthNavGraph.kt   (or keep under app/nav if that's the existing convention)
```

### 4.2 `login/LoginContract.kt`
```kotlin
package com.medsy.presentation.auth.login

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed interface LoginIntent {
    data class EmailChanged(val value: String) : LoginIntent
    data class PasswordChanged(val value: String) : LoginIntent
    data object Submit : LoginIntent
}

sealed interface LoginEffect {
    data object NavigateHome : LoginEffect
    data class ShowError(val message: String) : LoginEffect
}
```

### 4.3 `login/LoginViewModel.kt`
```kotlin
package com.medsy.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.auth.usecase.LoginUseCase
import com.medsy.domain.common.DomainError
import com.medsy.domain.common.DomainResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _effect = Channel<LoginEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.EmailChanged -> _state.update { it.copy(email = intent.value, errorMessage = null) }
            is LoginIntent.PasswordChanged -> _state.update { it.copy(password = intent.value, errorMessage = null) }
            LoginIntent.Submit -> submit()
        }
    }

    private fun submit() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        when (val result = loginUseCase(_state.value.email, _state.value.password)) {
            is DomainResult.Success -> {
                _state.update { it.copy(isLoading = false) }
                _effect.send(LoginEffect.NavigateHome)
            }
            is DomainResult.Error -> {
                val message = (result.error as? DomainError.Api)?.message
                    ?: "Something went wrong. Please try again."
                _state.update { it.copy(isLoading = false, errorMessage = message) }
            }
        }
    }
}
```

### 4.4 `login/LoginScreen.kt` — Compose skeleton
```kotlin
package com.medsy.presentation.auth.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LoginScreen(
    onNavigateHome: () -> Unit,
    onNavigateRegister: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                LoginEffect.NavigateHome -> onNavigateHome()
                is LoginEffect.ShowError -> Unit // handled via state.errorMessage inline
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        OutlinedTextField(
            value = state.email,
            onValueChange = { viewModel.onIntent(LoginIntent.EmailChanged(it)) },
            label = { Text("Email") },
            isError = state.errorMessage != null,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = state.password,
            onValueChange = { viewModel.onIntent(LoginIntent.PasswordChanged(it)) },
            label = { Text("Password") },
            isError = state.errorMessage != null,
            modifier = Modifier.fillMaxWidth()
        )
        state.errorMessage?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { viewModel.onIntent(LoginIntent.Submit) },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
            else Text("Log in")
        }
        TextButton(onClick = onNavigateRegister) { Text("Create an account") }
    }
}
```

### 4.5 `register/RegisterContract.kt` + ViewModel — key differences from Login
- `State` holds: `firstName, lastName, email, phoneNumber, dob, homeAddress, password, confirmPassword, role, isLoading, fieldErrors: Map<String, String>, errorMessage`.
- Validate on `Submit`:
  - All required fields non-blank → per-field error in `fieldErrors`
  - `password == confirmPassword`
  - `ValidateEgyptPhoneUseCase(phoneNumber)` → field error "Enter a valid Egyptian phone number (+20 1xxxxxxxxx)"
  - `dob` format `yyyy-MM-dd`
- On success (backend returns 200 on `/register`, **not** tokens — registration must go to OTP screen, not Home): emit `Effect.NavigateToOtp(email)`.

### 4.6 `otp/OtpContract.kt` + ViewModel — key requirements
```kotlin
data class OtpState(
    val email: String = "",
    val otpCode: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val resendCooldownSeconds: Int = 60,
    val canResend: Boolean = false
)

sealed interface OtpIntent {
    data class CodeChanged(val value: String) : OtpIntent
    data object Submit : OtpIntent
    data object ResendRequested : OtpIntent
}

sealed interface OtpEffect {
    data object NavigateHome : OtpEffect
}
```
ViewModel must:
- Start a 60s countdown `Job` in `init { }` using `viewModelScope`, decrementing `resendCooldownSeconds` every second via `delay(1000)`, then set `canResend = true`.
- On `ResendRequested`: only act if `canResend == true`; call `RegisterUseCase` again or a dedicated resend endpoint if the backend adds one later (currently not in the provided contract — **flag this as a backend TODO**, and for now just restart the timer client-side, or re-trigger `register` with the same payload if the backend allows re-registration to resend OTP — confirm with backend team).
- On wrong code: show `errorMessage = "Invalid or expired code"` from `DomainError.Api.message`, keep the entered code so the user can retry, do not reset the timer.
- On success: emits `OtpEffect.NavigateHome` (session is now persisted by repo automatically since `verifyOtp` uses `onSuccessSave()`).

### 4.7 `welcome/WelcomeScreen.kt`
Two buttons: "Log in" → login screen, "Create account" → register screen. No ViewModel needed (stateless).

### 4.8 Splash / session check
Add `presentation/auth/splash/SplashViewModel.kt`:
```kotlin
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val observeSessionUseCase: ObserveSessionUseCase
) : ViewModel() {
    private val _effect = Channel<SplashEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            val session = observeSessionUseCase().first() // first emission only
            _effect.send(if (session != null) SplashEffect.ToHome else SplashEffect.ToWelcome)
        }
    }
}

sealed interface SplashEffect {
    data object ToHome : SplashEffect
    data object ToWelcome : SplashEffect
}
```

---

## 5. Navigation wiring (in `app` module, existing `nav/rootnavigation`)

- Add nested `authGraph` with routes: `welcome`, `register`, `otp/{email}`, `login`.
- On login/OTP success, pop the entire `authGraph` off the back stack when navigating to `home` (`popUpTo(authGraph route) { inclusive = true }`) so pressing back from Home never returns to auth screens.
- `splash` is the app's `startDestination`; it decides `authGraph` vs `homeGraph` via `SplashEffect`.

---

## 6. Gradle / Dependencies checklist

Add to `data/build.gradle.kts` (versions per project's existing catalog):
```kotlin
implementation("com.squareup.retrofit2:retrofit:<version>")
implementation("com.squareup.retrofit2:converter-moshi:<version>")
implementation("com.squareup.moshi:moshi-kotlin:<version>")
implementation("com.squareup.okhttp3:logging-interceptor:<version>") // debug builds only
implementation("androidx.security:security-crypto:<version>")
```
Add to `presentation/build.gradle.kts`:
```kotlin
implementation("androidx.hilt:hilt-navigation-compose:<version>")
implementation("androidx.lifecycle:lifecycle-runtime-compose:<version>")
```
Add `BASE_URL` in `data/build.gradle.kts`:
```kotlin
android {
    buildTypes {
        debug { buildConfigField("String", "BASE_URL", "\"https://<dev-host>/\"") }
        release { buildConfigField("String", "BASE_URL", "\"https://<prod-host>/\"") }
    }
}
```

---

## 7. Acceptance Criteria Mapping (traceability)

| Acceptance criterion | Where it's implemented |
|---|---|
| Register happy path → Home, token persisted | Register → Otp → `verifyOtp` → `AuthRepositoryImpl.onSuccessSave()` → `TokenStorage.save()` → nav to Home |
| Wrong credentials → inline error, no crash | `LoginViewModel.submit()` catches `DomainResult.Error`, sets `state.errorMessage`; `safeCall` in repo never throws |
| OTP resend cooldown 60s + wrong code state | `OtpViewModel` countdown `Job`; `errorMessage` on `DomainError.Api` |
| Kill app → reopen → still authenticated | `TokenStorage.readSessionInternal()` restores session from EncryptedSharedPreferences on process start; `SplashViewModel` routes accordingly |
| Token attached to every call | `AuthInterceptor` |
| Auto-refresh on 401 | `TokenAuthenticator` |
| Phone format (+20) validation | `ValidateEgyptPhoneUseCase`, used in `RegisterViewModel` |

---

## 8. Implementation Order (do in this sequence)

1. `domain`: common result types → models → repository interface → use cases. Build & unit test with fake repo.
2. `data`: DTOs → `AuthApi` → mappers → `TokenStorage` → `AuthInterceptor`/`TokenAuthenticator` → `AuthRepositoryImpl` → DI modules. Build.
3. `presentation`: Login contract+VM+screen first (simplest, validates the whole pipeline end-to-end) → Register → Otp → Welcome → Splash.
4. `app`: wire nav graph, Hilt `@HiltAndroidApp`, confirm `BuildConfig.BASE_URL`.
5. Manual test: register → OTP → home → kill app → reopen (still logged in) → logout → back to welcome.
6. Force a 401 manually (e.g. temporarily shorten access token expiry server-side or edit stored token) to confirm `TokenAuthenticator` refresh path works.

---

## 9. Open questions to resolve with backend before/while implementing

- No `/auth/resend-otp` endpoint is defined in the given contract — confirm whether resend re-uses `/register` or a new endpoint is planned.
- No `/auth/forgot-password` / `/auth/reset-password` endpoints given yet, though scope mentions "forgot/reset entry point" — implement the entry-point screen/button now, wire it once the endpoint exists.
- Confirm JSON converter (Moshi/kotlinx-serialization/Gson) already used elsewhere in the project so `data` module DTOs stay consistent project-wide.
