package com.medsy.data.prescription.remote

enum class PrescriptionImageMimeType(val value: String) {
    JPEG("image/jpeg"),
    PNG("image/png");

    companion object {
        fun fromExtension(extension: String): PrescriptionImageMimeType {
            return when (extension.lowercase()) {
                "png" -> PNG
                "jpg", "jpeg" -> JPEG
                else -> JPEG
            }
        }
    }
}
