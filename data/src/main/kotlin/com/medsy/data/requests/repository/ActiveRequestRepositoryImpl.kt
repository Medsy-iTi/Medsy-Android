package com.medsy.data.requests.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.medsy.domain.requests.model.MedicineRequest
import com.medsy.domain.requests.repository.ActiveRequestRepository
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActiveRequestRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val moshi: Moshi
) : ActiveRequestRepository {
    private val _activeRequest = MutableStateFlow<MedicineRequest?>(null)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val adapter = moshi.adapter(MedicineRequest::class.java)

    companion object {
        private val ACTIVE_REQUEST_KEY = stringPreferencesKey("active_request")
    }

    init {
        scope.launch {
            val prefs = dataStore.data.firstOrNull()
            val json = prefs?.get(ACTIVE_REQUEST_KEY)
            if (json != null) {
                try {
                    _activeRequest.value = adapter.fromJson(json)
                } catch (e: Exception) {
                    _activeRequest.value = null
                }
            }
        }
    }

    override fun observeActiveRequest(): Flow<MedicineRequest?> = _activeRequest.asStateFlow()

    override suspend fun setActiveRequest(request: MedicineRequest) {
        _activeRequest.value = request
        try {
            val json = adapter.toJson(request)
            dataStore.edit { prefs ->
                prefs[ACTIVE_REQUEST_KEY] = json
            }
        } catch (e: Exception) {
            // Ignore
        }
    }

    override suspend fun clearActiveRequest() {
        _activeRequest.value = null
        dataStore.edit { prefs ->
            prefs.remove(ACTIVE_REQUEST_KEY)
        }
    }
}
