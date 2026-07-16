package com.medsy.data.auth.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.medsy.domain.auth.model.AuthSession
import com.medsy.domain.auth.model.Role
import com.medsy.domain.auth.model.User
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
        val accessToken = prefs.getString(KEY_ACCESS, null) ?: return null
        val refreshToken = prefs.getString(KEY_REFRESH, null) ?: return null
        
        val userId = prefs.getLong(KEY_USER_ID, -1L)
        val userEmail = prefs.getString(KEY_USER_EMAIL, null) ?: return null
        val firstName = prefs.getString(KEY_USER_FIRST, "") ?: ""
        val lastName = prefs.getString(KEY_USER_LAST, "") ?: ""
        val roleStr = prefs.getString(KEY_USER_ROLE, Role.CUSTOMER.name) ?: Role.CUSTOMER.name
        
        return AuthSession(
            accessToken = accessToken,
            refreshToken = refreshToken,
            user = User(
                id = userId,
                email = userEmail,
                firstName = firstName,
                lastName = lastName,
                role = runCatching { Role.valueOf(roleStr) }.getOrDefault(Role.CUSTOMER),
                homeAddress = prefs.getString(KEY_USER_ADDRESS, null),
                dob = prefs.getString(KEY_USER_DOB, null)
            )
        )
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
