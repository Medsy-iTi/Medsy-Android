package com.medsy.data.cart.repository

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.annotation.RequiresApi
import com.medsy.domain.cart.repository.CartLocationRepository
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CartLocationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : CartLocationRepository {
    override suspend fun getAddress(
        latitude: Double,
        longitude: Double,
    ): MedsyResult<String, MedsyError.Local> {
        if (!Geocoder.isPresent()) {
            return MedsyResult.Error(MedsyError.Local.UNKNOWN)
        }

        return try {
            val locale = context.resources.configuration.locales[0]
            val geocoder = Geocoder(context, locale)
            val address = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getAddressAsync(latitude, longitude)
            } else {
                geocoder.getAddressBlocking(latitude, longitude)
            }

            if (address == null) {
                MedsyResult.Error(MedsyError.Local.UNKNOWN)
            } else {
                MedsyResult.Success(address)
            }
        } catch (error: CancellationException) {
            throw error
        } catch (_: Exception) {
            MedsyResult.Error(MedsyError.Local.UNKNOWN)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private suspend fun Geocoder.getAddressAsync(
        latitude: Double,
        longitude: Double,
    ): String? = suspendCancellableCoroutine { continuation ->
        getFromLocation(
            latitude,
            longitude,
            MAX_RESULTS,
            object : Geocoder.GeocodeListener {
                override fun onGeocode(addresses: MutableList<Address>) {
                    if (continuation.isActive) {
                        continuation.resume(addresses.firstOrNull()?.toDisplayAddress())
                    }
                }

                override fun onError(errorMessage: String?) {
                    if (continuation.isActive) {
                        continuation.resumeWithException(IOException())
                    }
                }
            },
        )
    }

    @Suppress("DEPRECATION")
    private suspend fun Geocoder.getAddressBlocking(
        latitude: Double,
        longitude: Double,
    ): String? = withContext(Dispatchers.IO) {
        getFromLocation(latitude, longitude, MAX_RESULTS)
            ?.firstOrNull()
            ?.toDisplayAddress()
    }

    private fun Address.toDisplayAddress(): String? {
        val area = subLocality ?: thoroughfare
        val city = locality ?: subAdminArea

        return listOfNotNull(area, city, adminArea)
            .map(String::trim)
            .filter(String::isNotEmpty)
            .distinct()
            .joinToString(separator = ", ")
            .takeIf(String::isNotEmpty)
    }

    private companion object {
        const val MAX_RESULTS = 1
    }
}
