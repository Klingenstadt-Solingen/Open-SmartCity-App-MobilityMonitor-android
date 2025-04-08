package de.osca.android.mobility.presentation.dashboard

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import de.osca.android.essentials.presentation.component.design.BaseCardContainer
import de.osca.android.essentials.presentation.component.design.MasterDesignArgs
import de.osca.android.essentials.presentation.component.design.RootContainer
import de.osca.android.essentials.presentation.component.design.SimpleSpacedList
import de.osca.android.essentials.presentation.component.screen_wrapper.ScreenWrapper
import de.osca.android.essentials.presentation.component.topbar.ScreenTopBar
import de.osca.android.essentials.utils.extensions.SetSystemStatusBar
import de.osca.android.essentials.utils.extensions.getLastDeviceLocationOnce
import de.osca.android.essentials.utils.extensions.shortToast
import de.osca.android.mobility.entity.TransportOption
import de.osca.android.mobility.presentation.components.MobilityElement
import kotlinx.coroutines.launch
import java.util.Timer
import kotlin.concurrent.timer


@Composable
fun MobilityDashboardScreen(
    navController: NavController,
    initialLocation: LatLng,
    mobilityDashboardViewModel: MobilityDashboardViewModel = hiltViewModel(),
    masterDesignArgs: MasterDesignArgs = mobilityDashboardViewModel.defaultDesignArgs
) {
    val context = LocalContext.current
    val design = mobilityDashboardViewModel.mobilityDesignArgs
    val location = remember { mutableStateOf(initialLocation) }
    val userLocationFound = remember {
        mutableStateOf(false)
    }

    val selectedTransportOption = remember {
        mutableStateOf<TransportOption?>(null)
    }

    val coroutineScope = rememberCoroutineScope()
    val cameraPositionState = rememberCameraPositionState()

    val refreshTimer = remember { mutableStateOf<Timer?>(null) }
    val refreshTime: Long = remember {60}

    DisposableEffect(Unit){
        onDispose {
            refreshTimer.value?.cancel()
        }
    }

    LaunchedEffect(Unit) {
        mobilityDashboardViewModel.markerOptions.clear()

        mobilityDashboardViewModel.mobilityEntriesCarsharingLoaded.value = false
        mobilityDashboardViewModel.mobilityEntriesPubTransLoaded.value = false
        mobilityDashboardViewModel.mobilityEntriesTaxiLoaded.value = false
        mobilityDashboardViewModel.mobilityEntriesEscooterLoaded.value = false

        refreshTimer.value = timer(name = "Mobility Refresh", daemon = true, period = refreshTime * 1000){
            context.getLastDeviceLocationOnce { result ->
                result?.let { latLng ->
                    location.value = LatLng(
                        latLng.latitude,
                        latLng.longitude
                    )
                    userLocationFound.value = true
                    mobilityDashboardViewModel.updateMobility(context, location.value)
                } ?: with(context) {
                    mobilityDashboardViewModel.updateMobility(context, initialLocation)
                    shortToast(text = getString(de.osca.android.mobility.R.string.global_no_location))
                }
            }
        }
    }

    SetSystemStatusBar(
        !(design.mIsStatusBarWhite ?: masterDesignArgs.mIsStatusBarWhite), Color.Transparent
    )

    ScreenWrapper(
        topBar = {
            ScreenTopBar(
                title = stringResource(id = design.vModuleTitle),
                navController = navController,
                overrideTextColor = design.mTopBarTextColor,
                overrideBackgroundColor = design.mTopBarBackColor,
                masterDesignArgs = masterDesignArgs
            )
        },
        screenWrapperState = mobilityDashboardViewModel.wrapperState,
        retryAction = {
            mobilityDashboardViewModel.updateMobility(context, location.value)
        },
        masterDesignArgs = masterDesignArgs,
        moduleDesignArgs = design
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            BaseCardContainer(
                moduleDesignArgs = design,
                useContentPadding = false,
                overrideConstraintHeight = design.mapCardHeight,
                masterDesignArgs = masterDesignArgs
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    GoogleMap(
                        modifier = Modifier
                            .fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(isMyLocationEnabled = userLocationFound.value, mapStyleOptions = if (design.mapStyle != null) MapStyleOptions.loadRawResourceStyle(context, design.mapStyle!!) else null),
                        uiSettings = MapUiSettings(
                            compassEnabled = false,
                            tiltGesturesEnabled = false,
                            mapToolbarEnabled = false,
                            indoorLevelPickerEnabled = false,
                            myLocationButtonEnabled = userLocationFound.value,
                            zoomControlsEnabled = false
                        ),
                        onMapClick = { latLng ->
                            // ...
                        },
                        onMapLoaded = {
                            coroutineScope.launch {
                                cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLngZoom(
                                            location.value,
                                            15.0f
                                        )
                                )
                            }
                        }
                    ) {
                        mobilityDashboardViewModel.markerOptions.forEach { markerOption ->
                            Marker(
                                icon = markerOption.icon,
                                title = markerOption.title,
                                state = MarkerState(markerOption.position),
                                onClick = { marker ->
                                    coroutineScope.launch {
                                        cameraPositionState.animate(
                                            CameraUpdateFactory.newLatLngZoom(
                                                marker.position,
                                                15.0f
                                            )
                                        )
                                    }
                                    true
                                }
                            )
                        }
                    }
                }
            }
        }

        RootContainer(
            masterDesignArgs = masterDesignArgs,
            moduleDesignArgs = design
        ) {
            item {
                SimpleSpacedList(
                    masterDesignArgs = masterDesignArgs
                ) {
                    if (mobilityDashboardViewModel.mobilityEntriesPubTransLoaded.value) {
                        mobilityDashboardViewModel.mobilityEntriesPubTrans.forEach {
                            MobilityElement(
                                masterDesignArgs = masterDesignArgs,
                                moduleDesignArgs = design,
                                transportData = it,
                                onRowWasClicked = { option ->
                                    selectedTransportOption.value = option

                                    coroutineScope.launch {
                                        CameraUpdateFactory.newLatLngZoom(
                                            selectedTransportOption.value!!.location.getLatLng, 18.0f)
                                    }
                                }
                            )
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            CircularProgressIndicator()
                            Text("lade PubTrans")
                        }
                    }

                    if (mobilityDashboardViewModel.mobilityEntriesEscooterLoaded.value) {
                        mobilityDashboardViewModel.mobilityEntriesEscooter.forEach {
                            MobilityElement(
                                masterDesignArgs = masterDesignArgs,
                                moduleDesignArgs = design,
                                transportData = it,
                                onRowWasClicked = { option ->
                                    selectedTransportOption.value = option

                                    coroutineScope.launch {
                                        CameraUpdateFactory.newLatLngZoom(
                                            selectedTransportOption.value!!.location.getLatLng, 18.0f)
                                    }
                                    try {
                                        val deeplink = selectedTransportOption.value?.deeplinks?.android
                                        if(!deeplink.isNullOrEmpty()) {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deeplink))
                                        context.startActivity(intent)
                                        }
                                    } catch (e: Exception) {
                                        //TODO: Add info text if deeplink doesn't work
                                    }
                                }
                            )
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            CircularProgressIndicator()
                            Text("lade Escooter")
                        }
                    }

                    if (mobilityDashboardViewModel.mobilityEntriesCarsharingLoaded.value) {
                        mobilityDashboardViewModel.mobilityEntriesCarsharing.forEach {
                            MobilityElement(
                                masterDesignArgs = masterDesignArgs,
                                moduleDesignArgs = design,
                                transportData = it,
                                onRowWasClicked = { option ->
                                    selectedTransportOption.value = option

                                    coroutineScope.launch {
                                        CameraUpdateFactory.newLatLngZoom(
                                            selectedTransportOption.value!!.location.getLatLng, 18.0f
                                        )
                                    }
                                    try {
                                        val deeplink = selectedTransportOption.value?.deeplinks?.android
                                        if(!deeplink.isNullOrEmpty()) {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deeplink))
                                            context.startActivity(intent)
                                        }
                                    } catch (e: Exception) {
                                        //TODO: Add info text if deeplink doesn't work
                                    }
                                }
                            )
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            CircularProgressIndicator()
                            Text("lade Carsharing")
                        }
                    }

                    if (mobilityDashboardViewModel.mobilityEntriesTaxiLoaded.value) {
                        mobilityDashboardViewModel.mobilityEntriesTaxi.forEach {
                            MobilityElement(
                                masterDesignArgs = masterDesignArgs,
                                moduleDesignArgs = design,
                                transportData = it,
                                onRowWasClicked = { option ->
                                    selectedTransportOption.value = option

                                    coroutineScope.launch {
                                        CameraUpdateFactory.newLatLngZoom(
                                            selectedTransportOption.value!!.location.getLatLng, 18.0f)
                                    }
                                }
                            )
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            CircularProgressIndicator()
                            Text("lade Taxi")
                        }
                    }
                }
            }
        }
    }
}
