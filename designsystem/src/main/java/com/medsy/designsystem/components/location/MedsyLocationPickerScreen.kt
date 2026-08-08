package com.medsy.designsystem.components.location

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.medsy.designsystem.R
import com.medsy.designsystem.components.MedsySnackbarHost
import com.medsy.designsystem.components.showError
import kotlinx.coroutines.launch

private val CairoLocation = LatLng(30.0444, 31.2357)
private const val SavedLocationZoom = 16f
private const val CairoZoom = 12f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedsyLocationPickerScreen(
    initialLatitude: Double?,
    initialLongitude: Double?,
    onDismiss: () -> Unit,
    onLocationConfirmed: (Double, Double) -> Unit,
) {
    BackHandler(onBack = onDismiss)

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val permissionDeniedMessage = stringResource(R.string.location_picker_permission_denied)
    val locationUnavailableMessage = stringResource(R.string.location_picker_unavailable)
    val markerTitle = stringResource(R.string.location_picker_selected_marker)
    val hasSavedLocation = initialLatitude != null && initialLongitude != null &&
        initialLatitude in -90.0..90.0 && initialLongitude in -180.0..180.0
    val initialPosition = if (hasSavedLocation) {
        LatLng(requireNotNull(initialLatitude), requireNotNull(initialLongitude))
    } else {
        CairoLocation
    }
    val hasGooglePlayServices = remember(context) {
        GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) ==
            ConnectionResult.SUCCESS
    }

    if (!hasGooglePlayServices) {
        LocationPickerUnavailableContent(onDismiss = onDismiss)
        return
    }

    val fusedLocationClient = remember(context) {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val cancellationTokenSource = remember { CancellationTokenSource() }
    DisposableEffect(Unit) {
        onDispose { cancellationTokenSource.cancel() }
    }

    val markerState = rememberMarkerState(position = initialPosition)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            initialPosition,
            if (hasSavedLocation) SavedLocationZoom else CairoZoom,
        )
    }
    var hasLocationPermission by remember {
        mutableStateOf(context.hasForegroundLocationPermission())
    }
    var locationRequestVersion by remember { mutableIntStateOf(0) }
    var isFindingLocation by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (hasLocationPermission) {
            locationRequestVersion++
        } else {
            scope.launch { snackbarHostState.showError(permissionDeniedMessage) }
        }
    }

    LaunchedEffect(hasSavedLocation) {
        if (!hasSavedLocation) {
            if (hasLocationPermission) {
                locationRequestVersion++
            } else {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                    )
                )
            }
        }
    }

    LaunchedEffect(locationRequestVersion) {
        if (locationRequestVersion == 0 || !hasLocationPermission) return@LaunchedEffect
        isFindingLocation = true
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token,
        ).addOnSuccessListener { location ->
            isFindingLocation = false
            if (location == null) {
                scope.launch { snackbarHostState.showError(locationUnavailableMessage) }
                return@addOnSuccessListener
            }
            val currentPosition = LatLng(location.latitude, location.longitude)
            markerState.position = currentPosition
            scope.launch {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(currentPosition, SavedLocationZoom)
                )
            }
        }.addOnFailureListener {
            isFindingLocation = false
            scope.launch { snackbarHostState.showError(locationUnavailableMessage) }
        }
    }

    Scaffold(
        snackbarHost = { MedsySnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.location_picker_title),
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.location_picker_back),
                        )
                    }
                },
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    val selectedPosition = markerState.position
                    onLocationConfirmed(
                        selectedPosition.latitude,
                        selectedPosition.longitude,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
            ) {
                Text(text = stringResource(R.string.location_picker_confirm))
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = false,
                    zoomControlsEnabled = false,
                    compassEnabled = true,
                ),
                onMapClick = { position -> markerState.position = position },
            ) {
                Marker(
                    state = markerState,
                    title = markerTitle,
                    draggable = true,
                )
            }

            FloatingActionButton(
                onClick = {
                    if (context.hasForegroundLocationPermission()) {
                        hasLocationPermission = true
                        locationRequestVersion++
                    } else {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                            )
                        )
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
            ) {
                if (isFindingLocation) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(14.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.MyLocation,
                        contentDescription = stringResource(R.string.location_picker_use_current),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocationPickerUnavailableContent(
    onDismiss: () -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(R.string.location_picker_title)) },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.location_picker_back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.location_picker_maps_unavailable),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.location_picker_back))
            }
        }
    }
}

private fun android.content.Context.hasForegroundLocationPermission(): Boolean =
    ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION,
    ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
