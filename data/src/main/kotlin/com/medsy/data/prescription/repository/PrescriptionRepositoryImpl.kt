package com.medsy.data.prescription.repository

import com.medsy.data.common.media.PrescriptionImageStorage
import com.medsy.data.prescription.mapper.toDomain
import com.medsy.data.prescription.remote.PrescriptionRemoteDataSource
import com.medsy.data.remote.api.ApiService
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
    private val apiService: ApiService,
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
        return try {
            val response = if (query.isNotBlank()) {
                apiService.searchProducts(keyword = query, page = 0, size = 50, sort = null)
            } else {
                apiService.getProducts(page = 0, size = 50, sort = null)
            }
            
            val body = response.body()
            if (response.isSuccessful && body != null && body.success && body.data != null) {
                val isArabic = java.util.Locale.getDefault().language == "ar"
                val results = body.data.content.map { dto ->
                    val localizedName = if (isArabic && !dto.arabicName.isNullOrBlank()) dto.arabicName else dto.name
                    Medicine(
                        productId = dto.id,
                        name = localizedName,
                        strength = dto.scientificName,
                        form = dto.route,
                        price = dto.price.toInt(),
                        imageUrl = dto.imageUrl
                    )
                }
                MedsyResult.Success(results)
            } else {
                MedsyResult.Error(MedsyError.Remote.Unknown)
            }
        } catch (e: Exception) {
            MedsyResult.Error(MedsyError.Remote.Unknown)
        }
    }

    override suspend fun getMedicineById(productId: Int): MedsyResult<Medicine, MedsyError.Remote> {
        return try {
            val response = apiService.getProductById(
                productId,
                java.util.Locale.getDefault().language
            )
            val body = response.body()
            if (response.isSuccessful && body != null && body.success && body.data != null) {
                val dto = body.data
                val medicine = Medicine(
                    productId = dto.id,
                    name = dto.name,
                    strength = dto.scientificName,
                    form = dto.route,
                    price = dto.price.toInt(),
                    imageUrl = dto.imageUrl
                )
                MedsyResult.Success(medicine)
            } else {
                MedsyResult.Error(MedsyError.Remote.Unknown)
            }
        } catch (e: Exception) {
            MedsyResult.Error(MedsyError.Remote.Unknown)
        }
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
