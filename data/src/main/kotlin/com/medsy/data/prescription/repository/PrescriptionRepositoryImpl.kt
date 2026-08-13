package com.medsy.data.prescription.repository

import com.medsy.data.common.media.PrescriptionImageStorage
import com.medsy.data.prescription.mapper.toDomain
import com.medsy.data.prescription.mapper.toMedicine
import com.medsy.data.prescription.remote.PrescriptionImageMimeType
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
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException

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
        }

    override suspend fun extractPrescription(
        image: PrescriptionImage,
    ): MedsyResult<PrescriptionExtractionOutcome, MedsyError> {
        val file = imageStorage.getFile(image)
        if (!file.exists()) return MedsyResult.Error(MedsyError.Local.MEDIA)

        val mimeType = PrescriptionImageMimeType.fromExtension(file.extension).value
        val requestFile = file.asRequestBody(mimeType.toMediaType())
        val multipartImage = MultipartBody.Part.createFormData("image", file.name, requestFile)

        return remoteDataSource.analyzePrescription(multipartImage)
            .map { it.toDomain() }
    }

    override suspend fun analyzeMedicineImage(
        image: PrescriptionImage,
    ): MedsyResult<List<Medicine>, MedsyError> {
        val file = imageStorage.getFile(image)
        if (!file.exists()) return MedsyResult.Error(MedsyError.Local.MEDIA)

        val mimeType = PrescriptionImageMimeType.fromExtension(file.extension).value
        val requestFile = file.asRequestBody(mimeType.toMediaType())
        val multipartImage = MultipartBody.Part.createFormData("image", file.name, requestFile)

        return remoteDataSource.analyzeMedicineImage(multipartImage)
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getMedicineById(productId: Int): MedsyResult<Medicine, MedsyError.Remote> {
        return remoteDataSource.getProductById(
            productId,
            java.util.Locale.getDefault().language
        ).map { it.toMedicine() }
    }

    override suspend fun searchMedicines(
        query: String,
    ): MedsyResult<List<Medicine>, MedsyError.Remote> {
        return remoteDataSource.searchProducts(query)
            .map { page -> page.content.map { it.toMedicine() } }
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
