package de.osca.android.mobility.presentation.dashboard

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.graphics.scale
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import de.osca.android.essentials.presentation.base.BaseViewModel
import de.osca.android.essentials.utils.extensions.displayContent
import de.osca.android.essentials.utils.extensions.resetWith
import de.osca.android.mobility.R
import de.osca.android.mobility.data.MobilityApiService
import de.osca.android.mobility.data.MobilityRepository
import de.osca.android.mobility.entity.MobilityRequestBody
import de.osca.android.mobility.entity.TransportData
import de.osca.android.mobility.presentation.args.MobilityDesignArgs
import de.osca.android.networkservice.utils.RequestHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * This is the ViewModel for the ContactScreen
 * It handles the contact fetching from parse and the sending of the data
 *
 * @param mobilityApiService contains the routes to the classes and functions from parse
 * @param requestHandler handles the request and response from parse
 *
 * @property mobilityDesignArgs module design arguments and overrides for masterDesignArgs
 */
@HiltViewModel
class MobilityDashboardViewModel
    @Inject
    constructor(
        private val mobilityApiService: MobilityApiService,
        private val repository: MobilityRepository,
        private val requestHandler: RequestHandler,
    ) : BaseViewModel() {
        val mobilityEntriesCarsharing = mutableStateListOf<TransportData>()
        val mobilityEntriesPubTrans = mutableStateListOf<TransportData>()
        val mobilityEntriesTaxi = mutableStateListOf<TransportData>()
        val mobilityEntriesEscooter = mutableStateListOf<TransportData>()

        val mobilityEntriesCarsharingLoaded = mutableStateOf(false)
        val mobilityEntriesPubTransLoaded = mutableStateOf(false)
        val mobilityEntriesTaxiLoaded = mutableStateOf(false)
        val mobilityEntriesEscooterLoaded = mutableStateOf(false)

        val markerOptions = mutableStateListOf<MarkerOptions>()

        var isInitialized = false

        @Inject
        lateinit var mobilityDesignArgs: MobilityDesignArgs

        fun updateMobility(
            context: Context,
            userCoordinates: LatLng,
        ) {
            wrapperState.displayContent()
            viewModelScope.launch {
                async {
                    fetchMobilityEntriesForType("public-transport", userCoordinates)
                    fetchMobilityEntriesForType("escooter", userCoordinates)
                    fetchMobilityEntriesForType("carsharing", userCoordinates)
                    fetchMobilityEntriesForType("taxi", userCoordinates)
                }
            }
        }

        fun fetchMobilityEntriesForType(
            type: String,
            userCoordinates: LatLng,
        ): Job =
            launchDataLoad {
                val mobilityRequestBody =
                    MobilityRequestBody(
                        type = type,
                        lat = userCoordinates.latitude,
                        lon = userCoordinates.longitude,
                    )
                val result =
                    requestHandler.makeRequest {
                        mobilityApiService.getMobilityForType(
                            mobilityRequestBody,
                        )
                    }
                when (type) {
                    "carsharing" -> {
                        mobilityEntriesCarsharing.resetWith(result)
                        mobilityEntriesCarsharingLoaded.value = true
                        for (entry in mobilityEntriesCarsharing) {
                            addMarkerToMap(entry)
                        }
                    }

                    "public-transport" -> {
                        val sortedList: MutableList<TransportData> =
                            emptyList<TransportData>().toMutableList()
                        result?.forEach { transportData ->
                            transportData.availableOptions.sortBy { it.departureTimeDisplay }
                            sortedList.add(transportData)
                        }
                        mobilityEntriesPubTrans.resetWith(sortedList)
                        mobilityEntriesPubTransLoaded.value = true
                        for (entry in mobilityEntriesPubTrans) {
                            addMarkerToMap(entry, true)
                        }
                    }

                    "taxi" -> {
                        mobilityEntriesTaxi.resetWith(result)
                        mobilityEntriesTaxiLoaded.value = true
                        for (entry in mobilityEntriesTaxi) {
                            addMarkerToMap(entry)
                        }
                    }

                    "escooter" -> {
                        mobilityEntriesEscooter.resetWith(result)
                        mobilityEntriesEscooterLoaded.value = true
                        for (entry in mobilityEntriesEscooter) {
                            addMarkerToMap(entry)
                        }
                    }
                }
            }

        fun setUserLocationMarker(
            context: Context,
            userLocation: LatLng,
        ): MarkerOptions {
            val bmp = BitmapFactory.decodeResource(context.resources, R.drawable.googlemapbluedot)
            val bmpScaled = bmp.scale(100, 100, false)
            val icon = BitmapDescriptorFactory.fromBitmap(bmpScaled)
            val userMarkerOption =
                MarkerOptions()
                    .position(userLocation)
                    .icon(icon)
            return userMarkerOption
        }

        private fun addMarkerToMap(
            entry: TransportData,
            useParentSymbol: Boolean = false,
        ) {
            for (option in entry.availableOptions) {
                val symbolIconUrl = if (useParentSymbol) entry.symbolUrl else option.symbolUrl

                if (symbolIconUrl != null && symbolIconUrl.isNotEmpty()) {
                    viewModelScope.launch {
                        withContext(Dispatchers.IO) {
                            symbolIconUrl.let {
                                repository.getMarkerIcon(it)?.let { bmp ->
                                    val scaledBmp = bmp.scale(bmp.width * 2, bmp.height * 2, true)
                                    withContext(Dispatchers.Main) {
                                        val markerOption =
                                            MarkerOptions()
                                                .position(option.location.getLatLng)
                                                .icon(BitmapDescriptorFactory.fromBitmap(scaledBmp))

                                        markerOptions.add(markerOption)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
