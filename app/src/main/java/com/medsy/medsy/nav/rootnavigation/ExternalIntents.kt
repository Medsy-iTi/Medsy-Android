package com.medsy.medsy.nav.rootnavigation

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri

fun Context.openDialer(phoneNumber: String) {
    startActivity(
        Intent(
            Intent.ACTION_DIAL,
            Uri.fromParts("tel", phoneNumber, null),
        )
    )
}

fun Context.openDirections(
    latitude: Double,
    longitude: Double,
) {
    val destination = "$latitude,$longitude"
    val googleMapsIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("google.navigation:q=$destination"),
    ).setPackage(GOOGLE_MAPS_PACKAGE)

    try {
        startActivity(googleMapsIntent)
    } catch (_: ActivityNotFoundException) {
        val fallbackUri = Uri.Builder()
            .scheme("https")
            .authority("www.google.com")
            .appendPath("maps")
            .appendPath("dir")
            .appendPath("")
            .appendQueryParameter("api", "1")
            .appendQueryParameter("destination", destination)
            .build()
        startActivity(Intent(Intent.ACTION_VIEW, fallbackUri))
    }
}

tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private const val GOOGLE_MAPS_PACKAGE = "com.google.android.apps.maps"
