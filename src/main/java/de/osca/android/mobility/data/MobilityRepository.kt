package de.osca.android.mobility.data

import android.graphics.Bitmap

interface MobilityRepository {
    suspend fun getMarkerIcon(url: String): Bitmap?
}