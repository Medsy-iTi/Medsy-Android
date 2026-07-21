package com.medsy.presentation.pharmacyprofile.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Directions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.medsy.presentation.R

@Composable
fun PharmacyLocationCard(
    pharmacyName: String,
    latitude: Double?,
    longitude: Double?,
    onDirectionsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val location = if (latitude != null && longitude != null) {
        LatLng(latitude, longitude)
    } else {
        null
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = stringResource(R.string.pharmacy_profile_location),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (location == null) {
                Text(
                    text = stringResource(R.string.pharmacy_profile_location_unavailable),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(location, 15f)
                }
                val markerState = remember(location) { MarkerState(position = location) }
                val mapDescription = stringResource(
                    R.string.pharmacy_profile_map_description,
                    pharmacyName,
                )
                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .semantics { contentDescription = mapDescription },
                    cameraPositionState = cameraPositionState,
                    uiSettings = remember {
                        MapUiSettings(
                            compassEnabled = false,
                            mapToolbarEnabled = false,
                            zoomControlsEnabled = false,
                        )
                    },
                ) {
                    Marker(
                        state = markerState,
                        title = pharmacyName,
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onDirectionsClick,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Directions,
                        contentDescription = null,
                    )
                    Text(
                        text = stringResource(R.string.pharmacy_profile_directions),
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }
        }
    }
}
