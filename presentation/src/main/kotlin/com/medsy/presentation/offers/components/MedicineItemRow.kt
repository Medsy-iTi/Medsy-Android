package com.medsy.presentation.offers.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.presentation.R
import com.medsy.presentation.common.util.PriceFormatter
import com.medsy.domain.offers.model.RequestResultItem

@Composable
fun MedicineItemRow(
    medicine: RequestResultItem,
    modifier: Modifier = Modifier,
    isSingleLinePrice: Boolean = false
) {
    val locale = LocalConfiguration.current.locales[0]
    var isExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .animateContentSize()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BoxWithImage(imageUrl = medicine.product?.imageUrl ?: "")
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = medicine.product?.productName ?: "",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = medicine.product?.packSize ?: medicine.product?.description ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        if (isSingleLinePrice) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val formattedPrice = remember(medicine.unitPrice, locale) {
                    PriceFormatter.formatPrice(medicine.unitPrice, locale)
                }
                Text(
                    text = stringResource(R.string.search_price_egp, formattedPrice),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Visible
                )
            }
        } else {
            Column(
                horizontalAlignment = Alignment.End
            ) {
                val formattedPrice = remember(medicine.unitPrice, locale) {
                    PriceFormatter.formatPrice(medicine.unitPrice, locale)
                }
                Text(
                    text = stringResource(R.string.search_price_egp, formattedPrice),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Visible
                )
                
                Spacer(modifier = Modifier.height(4.dp))

                if (medicine.isAvailable) {
                    if (medicine.isAlternative) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Alternative",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.extendedColors.warning,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(R.string.offers_available),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.extendedColors.badgeSuccess,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.extendedColors.badgeSuccess,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.offers_not_available),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
