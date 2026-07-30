package com.medsy.presentation.common.util

import com.medsy.domain.common.LocaleConstants
import java.text.NumberFormat
import java.util.Locale

object PriceFormatter {
    fun formatPrice(price: Number, locale: Locale): String {
        val formattingLocale = if (locale.language == LocaleConstants.ARABIC_TAG) {
            Locale.forLanguageTag(LocaleConstants.EGYPT_ARABIC_LOCALE_TAG)
        } else {
            locale
        }

        val numberFormat = NumberFormat.getInstance(formattingLocale)
        numberFormat.minimumFractionDigits = 1
        numberFormat.maximumFractionDigits = 1
        return numberFormat.format(price.toDouble())
    }
}
