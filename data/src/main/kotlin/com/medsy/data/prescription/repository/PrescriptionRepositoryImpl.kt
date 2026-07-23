package com.medsy.data.prescription.repository

import com.medsy.data.prescription.local.PrescriptionImageStorage
import com.medsy.data.prescription.mapper.toDomain
import com.medsy.data.prescription.remote.PrescriptionRemoteDataSource
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.map
import com.medsy.domain.prescription.model.Medicine
import com.medsy.domain.prescription.model.PrescriptionCartRequest
import com.medsy.domain.prescription.model.PrescriptionExtractionOutcome
import com.medsy.domain.prescription.model.PrescriptionImage
import com.medsy.domain.prescription.repository.PrescriptionRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import okhttp3.MediaType.Companion.toMediaType

@Singleton
class PrescriptionRepositoryImpl @Inject constructor(
    private val imageStorage: PrescriptionImageStorage,
    private val remoteDataSource: PrescriptionRemoteDataSource,
) : PrescriptionRepository {

    override suspend fun prepareCameraImage(): MedsyResult<PrescriptionImage, MedsyError.Local> =
        imageOperation { imageStorage.prepareCameraImage() }

    override suspend fun importGalleryImage(
        sourceUri: String,
    ): MedsyResult<PrescriptionImage, MedsyError.Local> =
        imageOperation { imageStorage.importGalleryImage(sourceUri) }

    override suspend fun deleteImage(
        image: PrescriptionImage,
    ): EmptyMedsyResult<MedsyError.Local> =
        imageOperation {
            imageStorage.delete(image)
            Unit
        }

    override suspend fun extractPrescription(
        image: PrescriptionImage,
    ): MedsyResult<PrescriptionExtractionOutcome, MedsyError> {
        val file = imageStorage.getFile(image)
        if (!file.exists()) return MedsyResult.Error(MedsyError.Local.MEDIA)


        val mimeType = when (file.extension.lowercase()) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            else -> "image/jpeg"
        }

        val requestFile = file.asRequestBody(
            mimeType.toMediaType()
        )
        val multipartImage = MultipartBody.Part.createFormData("image", file.name, requestFile)

        return remoteDataSource.analyzePrescription(multipartImage)
            .map { it.toDomain() }
    }

    override suspend fun searchMedicines(
        query: String,
    ): MedsyResult<List<Medicine>, MedsyError.Remote> {
        return MedsyResult.Success(emptyList())
    }

    override suspend fun addPrescriptionToCart(
        request: PrescriptionCartRequest,
    ): EmptyMedsyResult<MedsyError.Remote> {
        return MedsyResult.Success(Unit)
    }

    private inline fun <T> imageOperation(
        block: () -> T,
    ): MedsyResult<T, MedsyError.Local> = try {
        MedsyResult.Success(block())
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: Exception) {
        MedsyResult.Error(MedsyError.Local.MEDIA)
    }
}
