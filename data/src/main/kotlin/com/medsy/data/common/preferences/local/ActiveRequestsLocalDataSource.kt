package com.medsy.data.common.preferences.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.medsy.domain.requests.model.MedicineRequest
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@JsonClass(generateAdapter = true)
internal data class ActiveRequestEntity(
    val id: Long,
    val createdAtMillis: Long
)

@Singleton
class ActiveRequestsLocalDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    moshi: Moshi
) {
    private companion object {
        val ACTIVE_REQUESTS_KEY = stringPreferencesKey("active_requests")
    }

    private val type = Types.newParameterizedType(List::class.java, ActiveRequestEntity::class.java)
    private val adapter = moshi.adapter<List<ActiveRequestEntity>>(type)

    val activeRequests: Flow<List<MedicineRequest>> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences -> 
            val json = preferences[ACTIVE_REQUESTS_KEY]
            if (json.isNullOrEmpty()) {
                emptyList()
            } else {
                try {
                    val entities = adapter.fromJson(json) ?: emptyList()
                    entities.map { MedicineRequest(id = it.id, createdAtMillis = it.createdAtMillis) }
                } catch (e: Exception) {
                    emptyList()
                }
            }
        }

    suspend fun setActiveRequests(requests: List<MedicineRequest>) {
        try {
            val entities = requests.map { ActiveRequestEntity(id = it.id, createdAtMillis = it.createdAtMillis) }
            val json = adapter.toJson(entities)
            dataStore.edit { preferences ->
                preferences[ACTIVE_REQUESTS_KEY] = json
            }
        } catch (_: IOException) {
            // Keep the last successfully stored preference when storage is unavailable.
        }
    }
}
