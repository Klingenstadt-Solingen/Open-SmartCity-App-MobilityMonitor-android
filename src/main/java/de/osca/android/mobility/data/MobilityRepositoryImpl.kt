package de.osca.android.mobility.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.FileNotFoundException
import java.net.URL
import javax.inject.Inject

class MobilityRepositoryImpl @Inject constructor() : MobilityRepository {
    override suspend fun getMarkerIcon(url: String): Bitmap? {
        val url = URL(url)
        try {
            return BitmapFactory.decodeStream(url.openConnection().getInputStream())
        } catch (_: FileNotFoundException) {}
        return null
    }
}