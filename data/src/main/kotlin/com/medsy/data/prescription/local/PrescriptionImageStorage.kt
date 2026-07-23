package com.medsy.data.prescription.local

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.medsy.domain.prescription.model.PrescriptionImage
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrescriptionImageStorage @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val directory: File
        get() = File(context.filesDir, DIRECTORY_NAME).apply { mkdirs() }

    fun prepareCameraImage(): PrescriptionImage {
        val file = File.createTempFile(FILE_PREFIX, JPG_SUFFIX, directory)
        return file.toPrescriptionImage()
    }

    fun importGalleryImage(sourceUri: String): PrescriptionImage {
        val source = Uri.parse(sourceUri)
        val extension = context.contentResolver.getType(source)
            ?.let(MimeTypeMap.getSingleton()::getExtensionFromMimeType)
            ?.takeIf { it.isNotBlank() }
            ?: DEFAULT_IMAGE_EXTENSION
        val file = File.createTempFile(FILE_PREFIX, ".$extension", directory)

        context.contentResolver.openInputStream(source).use { input ->
            requireNotNull(input) { "Selected image could not be opened" }
            file.outputStream().use { output -> input.copyTo(output) }
        }

        return file.toPrescriptionImage()
    }

    fun delete(image: PrescriptionImage) {
        getFile(image).delete()
    }

    fun getFile(image: PrescriptionImage): File {
        return File(directory, image.storageKey)
    }

    private fun File.toPrescriptionImage(): PrescriptionImage {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.prescription_files",
            this,
        )
        return PrescriptionImage(uri = uri.toString(), storageKey = name)
    }

    private companion object {
        const val DIRECTORY_NAME = "prescriptions"
        const val FILE_PREFIX = "prescription_"
        const val JPG_SUFFIX = ".jpg"
        const val DEFAULT_IMAGE_EXTENSION = "jpg"
    }
}
