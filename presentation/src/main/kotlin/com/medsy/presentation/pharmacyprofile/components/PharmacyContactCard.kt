package com.medsy.presentation.pharmacyprofile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import com.medsy.presentation.R

@Composable
fun PharmacyContactCard(
    phoneNumber: String?,
    address: String?,
    onCallClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hasPhoneNumber = !phoneNumber.isNullOrBlank()
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = stringResource(R.string.pharmacy_profile_contact_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            PharmacyDetailRow(
                icon = Icons.Outlined.Phone,
                title = stringResource(R.string.pharmacy_profile_phone),
                value = phoneNumber.takeUnless { it.isNullOrBlank() }
                    ?: stringResource(R.string.pharmacy_profile_not_available),
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            PharmacyDetailRow(
                icon = Icons.Outlined.Place,
                title = stringResource(R.string.pharmacy_profile_address),
                value = address.takeUnless { it.isNullOrBlank() }
                    ?: stringResource(R.string.pharmacy_profile_not_available),
            )
            Button(
                onClick = onCallClick,
                enabled = hasPhoneNumber,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Phone,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.pharmacy_profile_call))
            }
        }
    }
}

@Composable
private fun PharmacyDetailRow(
    icon: ImageVector,
    title: String,
    value: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(
                    textDirection = TextDirection.ContentOrLtr,
                ),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
