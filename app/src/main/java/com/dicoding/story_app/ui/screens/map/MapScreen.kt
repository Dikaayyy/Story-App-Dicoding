package com.dicoding.story_app.ui.screens.map

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dicoding.story_app.R
import com.dicoding.story_app.data.response.Story
import com.dicoding.story_app.di.Injection
import com.dicoding.story_app.viewmodels.MapViewModel
import com.dicoding.story_app.viewmodels.ViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun MapScreen(
    viewModel: MapViewModel = viewModel(
        factory = ViewModelFactory(
            Injection.provideRepository(
                LocalContext.current
            )
        )
    )
) {
    val context = LocalContext.current
    val fusedLocationClient: FusedLocationProviderClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val cameraPositionState = rememberCameraPositionState()

    var hasLocationPermission by remember { mutableStateOf(false) }

    val locationPermissionRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            hasLocationPermission = true
        } else {
            locationPermissionRequest.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }

        if (hasLocationPermission) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val currentPosition = LatLng(it.latitude, it.longitude)
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(currentPosition, 10f)
                }
            }
        }

        viewModel.fetchStoriesWithLocation()
    }

    val storiesWithLocation = viewModel.storiesWithLocation.collectAsState().value
    val storyList = storiesWithLocation.getOrNull()?.listStory

    val mapStyleOptions = remember<MapStyleOptions?> {
        val styleJson = context.resources.openRawResource(R.raw.map_style).bufferedReader().use { it.readText() }
        MapStyleOptions(styleJson)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(mapStyleOptions = mapStyleOptions)
    ) {
        val defaultPosition = LatLng(-6.1751, 106.8650)
        LaunchedEffect(defaultPosition) {
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(defaultPosition, 10f))
        }

        storyList?.forEach { story: Story? ->
            story?.let {
                if (it.lat != null && it.lon != null) {
                    val position = LatLng(it.lat, it.lon)
                    MapMarker(
                        position = position,
                        title = it.name,
                        snippet = it.description
                    )
                }
            }
        }
    }
}

@Composable
fun MapMarker(position: LatLng, title: String, snippet: String) {
    Marker(
        state = rememberMarkerState(position = position),
        title = title,
        snippet = snippet
    )
}