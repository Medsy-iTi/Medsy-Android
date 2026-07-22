package com.medsy.medsy.nav.rootnavigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Route : NavKey {

    @Serializable
    data object Splash : Route

    @Serializable
    data object Onboarding : Route


    @Serializable
    data object Login : Route

    @Serializable
    data class Otp(val email: String) : Route

    @Serializable
    data object Register : Route

    @Serializable
    data object NestedNav : Route {

        @Serializable
        data object Home : Route

        @Serializable
        data object Cart : Route

        @Serializable
        data object Profile : Route


    }

    @Serializable
    data class ProductDetails(val id: String) : Route

    @Serializable
    data class PharmacyProfile(val pharmacyId: Long) : Route

    @Serializable
    data class AiChat(val initialPrompt: String? = null) : Route

    @Serializable
    data object Settings : Route

    @Serializable
    data class PersonalDetails(
        val startInEditMode: Boolean = false,
    ) : Route

    @Serializable
    data object SearchNav : Route

    @Serializable
    data object Categories : Route

    @Serializable
    data class Prescription(
        val attachmentOnly: Boolean = false,
    ) : Route

    @Serializable
    data class Products(val categoryId: Int, val categoryName: String) : Route
}
