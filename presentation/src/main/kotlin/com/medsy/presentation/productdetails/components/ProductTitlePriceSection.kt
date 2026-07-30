package com.medsy.presentation.productdetails.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medsy.presentation.R
import com.medsy.presentation.common.util.PriceFormatter
import java.util.regex.Pattern

@Composable
fun ProductTitlePriceSection(
    name: String,
    strength: String,
    packInfo: String,
    price: Int,
    modifier: Modifier = Modifier,
) {
    val locale = LocalConfiguration.current.locales[0]

    // Pattern to match suffixes like "30 TABS", "7 كبسولات", etc.
    val packSuffixPattern = remember {
        Pattern.compile("(\\d+\\s?(TABS|CAPS|أقراص|كبسولات|T|C))", Pattern.CASE_INSENSITIVE)
    }

    val (cleanedName, displayPackInfo) = remember(name, strength, packInfo) {
        var tempName = name
        var extraInfoFromName = ""

        // 1. Try to find pack info in name (e.g., "30 TABS")
        val matcher = packSuffixPattern.matcher(name)
        if (matcher.find()) {
            val foundSuffix = matcher.group()
            tempName = tempName.replace(foundSuffix, "").trim()
            extraInfoFromName = foundSuffix
        }

        // 2. Remove strength from name if present (using regex to avoid partial matches)
        if (strength.isNotBlank()) {
            val escapedStrength = Pattern.quote(strength)
            tempName = tempName.replace(Regex("(?i)\\b$escapedStrength\\b"), "").trim()
        }

        // 3. Final cleaning: remove trailing separators like "-" or "."
        val finalName = tempName.trim()
            .replace(Regex("\\s+"), " ")
            .removeSuffix("-")
            .removeSuffix(".")
            .trim()

        // 4. Determine final pack info string
        val finalPack = when {
            extraInfoFromName.isNotBlank() -> extraInfoFromName
            packInfo.isNotBlank() -> packInfo
            else -> ""
        }

        finalName to finalPack
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        Text(
            text = cleanedName,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        
        if (strength.isNotBlank() || displayPackInfo.isNotBlank()) {
            val subtitle = buildString {
                if (strength.isNotBlank()) append(strength)
                if (strength.isNotBlank() && displayPackInfo.isNotBlank()) append(" · ")
                if (displayPackInfo.isNotBlank()) append(displayPackInfo)
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
        }

        Text(
            text = stringResource(R.string.search_price_egp, PriceFormatter.formatPrice(price, locale)),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}
