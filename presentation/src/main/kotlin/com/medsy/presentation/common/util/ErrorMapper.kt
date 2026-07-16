package com.medsy.presentation.common.util

import com.medsy.designsystem.components.MedsySnackbarData
import com.medsy.designsystem.components.MedsySnackbarType
import com.medsy.domain.common.DomainError
import com.medsy.presentation.R

fun DomainError.toSnackbarData(): MedsySnackbarData {
    return when (this) {
        is DomainError.Network -> MedsySnackbarData(
            messageRes = R.string.error_network,
            type = MedsySnackbarType.Error
        )
        is DomainError.Api -> {
            // Map specific API messages to localized string resources
            val mappedRes = when {
                message.contains("Phone Number already exists", ignoreCase = true) -> R.string.error_phone_exists
                message.contains("Email already exists", ignoreCase = true) -> R.string.error_email_exists
                else -> null
            }
            
            if (mappedRes != null) {
                MedsySnackbarData(
                    messageRes = mappedRes,
                    type = MedsySnackbarType.Error
                )
            } else {
                // Fallback to the raw message if we don't have a specific mapping, 
                // or generic error if message is empty
                if (message.isNotBlank()) {
                    MedsySnackbarData(
                        message = message,
                        type = MedsySnackbarType.Error
                    )
                } else {
                    MedsySnackbarData(
                        messageRes = R.string.error_generic,
                        type = MedsySnackbarType.Error
                    )
                }
            }
        }
        else -> MedsySnackbarData(
            messageRes = R.string.error_generic,
            type = MedsySnackbarType.Error
        )
    }
}
