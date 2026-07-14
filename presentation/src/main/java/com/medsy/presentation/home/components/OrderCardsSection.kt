package com.medsy.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medsy.presentation.R

@Composable
fun OrderCardsSection(
    onSearchMedicineClick: () -> Unit,
    onUploadPrescriptionClick: () -> Unit
) {
    val primaryGreen = Color(0xFF1E7B4D)

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.home_section_order),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OrderCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.home_card_search_title),
                subtitle = stringResource(R.string.home_card_search_desc),
                icon = Icons.Default.Search,
                iconTint = Color(0xFF1E3A8A),
                onClick = onSearchMedicineClick
            )
            
            OrderCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.home_card_upload_title),
                subtitle = stringResource(R.string.home_card_upload_desc),
                icon = Icons.Default.CameraAlt,
                iconTint = primaryGreen,
                onClick = onUploadPrescriptionClick
            )
        }
    }
}

