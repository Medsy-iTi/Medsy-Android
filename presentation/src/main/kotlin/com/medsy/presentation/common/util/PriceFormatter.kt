package com.medsy.presentation.common.util

import java.text.NumberFormat
import java.util.Locale

object PriceFormatter {
    fun formatPrice(price: Number, locale: Locale): String {
        val formattingLocale = if (locale.language == "ar") {
            Locale.forLanguageTag("ar-EG-u-nu-arab")
        } else {
            locale
        }

        val numberFormat = NumberFormat.getInstance(formattingLocale)
        numberFormat.minimumFractionDigits = 1
        numberFormat.maximumFractionDigits = 1
        return numberFormat.format(price.toDouble())
    }
}
