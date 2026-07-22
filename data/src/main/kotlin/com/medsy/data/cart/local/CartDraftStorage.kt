package com.medsy.data.cart.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.medsy.data.common.media.PrescriptionImageStorage
import com.medsy.domain.cart.model.CartDraft
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.prescription.model.PrescriptionImage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartDraftStorage @Inject constructor(
    @ApplicationContext context: Context,
    private val imageStorage: PrescriptionImageStorage,
) {
    private val preferences = EncryptedSharedPreferences.create(
        context,
        PREFERENCES_NAME,
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    private val _draft = MutableStateFlow(readDraft())
    val draft = _draft.asStateFlow()

    fun updateNote(note: String): EmptyMedsyResult<MedsyError.Local> =
        update {
            check(preferences.edit().putString(KEY_NOTE, note).commit())
            _draft.value = _draft.value.copy(pharmacistNote = note)
        }

    fun attachPrescription(
        image: PrescriptionImage,
    ): EmptyMedsyResult<MedsyError.Local> = update {
        val previous = _draft.value.prescriptionImage
        check(
            preferences.edit()
                .putString(KEY_IMAGE_URI, image.uri)
                .putString(KEY_IMAGE_STORAGE_KEY, image.storageKey)
                .commit()
        )
        _draft.value = _draft.value.copy(prescriptionImage = image)
        if (previous != null && previous.storageKey != image.storageKey) {
            imageStorage.delete(previous)
        }
    }

    fun removePrescription(): EmptyMedsyResult<MedsyError.Local> = update {
        val previous = _draft.value.prescriptionImage
        check(
            preferences.edit()
                .remove(KEY_IMAGE_URI)
                .remove(KEY_IMAGE_STORAGE_KEY)
                .commit()
        )
        _draft.value = _draft.value.copy(prescriptionImage = null)
        if (previous != null) imageStorage.delete(previous)
    }

    fun clear(): EmptyMedsyResult<MedsyError.Local> = update {
        val previous = _draft.value.prescriptionImage
        check(preferences.edit().clear().commit())
        _draft.value = CartDraft()
        if (previous != null) imageStorage.delete(previous)
    }

    private fun readDraft(): CartDraft {
        val uri = preferences.getString(KEY_IMAGE_URI, null)
        val storageKey = preferences.getString(KEY_IMAGE_STORAGE_KEY, null)
        val image = if (uri != null && storageKey != null) {
            imageStorage.restore(uri, storageKey)
        } else {
            null
        }
        return CartDraft(
            prescriptionImage = image,
            pharmacistNote = preferences.getString(KEY_NOTE, "").orEmpty(),
        )
    }

    private inline fun update(
        block: () -> Unit,
    ): EmptyMedsyResult<MedsyError.Local> = try {
        block()
        MedsyResult.Success(Unit)
    } catch (_: Exception) {
        MedsyResult.Error(MedsyError.Local.UNKNOWN)
    }

    private companion object {
        const val PREFERENCES_NAME = "medsy_cart_draft"
        const val KEY_NOTE = "pharmacist_note"
        const val KEY_IMAGE_URI = "prescription_image_uri"
        const val KEY_IMAGE_STORAGE_KEY = "prescription_image_storage_key"
    }
}
