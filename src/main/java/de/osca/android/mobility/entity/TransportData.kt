package de.osca.android.mobility.entity

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.mutableStateListOf
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import de.osca.android.essentials.domain.entity.Coordinates
import de.osca.android.essentials.utils.extensions.toCoordinates
import de.osca.android.mobility.R
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toLocalDateTime
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

data class TransportData(
    @SerializedName("type") // one of publictransport, carsharing, bus, train, tram, bike, scooter, taxi
    val type: String = "",
    @SerializedName("provider")
    val provider: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("title")
    val title: String = "",
    @SerializedName("stop")  // stop is only set if type is publictransport
    val stop: TransportStop? = null,
    @SerializedName("iconUrl")
    val iconUrl: String = "",
    @SerializedName("symbolUrl")
    val symbolUrl: String? = null,
    @SerializedName("distance")
    val distance: Double = 0.0,
    @SerializedName("timestamp")
    val timestamp: Int = 0,
    @SerializedName("count")
    val count: Int = 0,
    @SerializedName("location")
    val location: TransportLocation = TransportLocation(),
    @SerializedName("availableOptions")
    val availableOptions: MutableList<TransportOption> = mutableStateListOf()
) {
    fun getNameFromType(context: Context) : String {
        return when(type) {
            "public-transport" -> stop?.name ?: ""
            "carsharing" -> title
            "bus" -> stop?.name ?: ""
            "train" -> stop?.name ?: ""
            "tram" -> stop?.name ?: ""
            "cablecar" -> title
            "airplane" -> title
            "regiotrain" -> title
            "longdistancetrain" -> title
            "bike" -> title
            "subway" -> title
            "bicycle" -> title
            "escooter" -> title
            "taxi" -> title
            else-> type
        }
    }

    fun getMarkerIcon(resource: Resources): Bitmap? {
        return when(type) {
            "public-transport" -> BitmapFactory.decodeResource(resource, R.drawable.bus_symbol)
            "carsharing" -> BitmapFactory.decodeResource(resource, R.drawable.bus_symbol)
            "bus" -> BitmapFactory.decodeResource(resource, R.drawable.bus_symbol)
            "train" -> BitmapFactory.decodeResource(resource, R.drawable.bus_symbol)
            "tram" -> BitmapFactory.decodeResource(resource, R.drawable.bus_symbol)
            "cablecar" -> BitmapFactory.decodeResource(resource, R.drawable.bus_symbol)
            "airplane" -> BitmapFactory.decodeResource(resource, R.drawable.bus_symbol)
            "regiotrain" -> BitmapFactory.decodeResource(resource, R.drawable.bus_symbol)
            "longdistancetrain" -> BitmapFactory.decodeResource(resource, R.drawable.bus_symbol)
            "bike" -> BitmapFactory.decodeResource(resource, R.drawable.bus_symbol)
            "subway" -> BitmapFactory.decodeResource(resource, R.drawable.bus_symbol)
            "bicycle" -> BitmapFactory.decodeResource(resource, R.drawable.bus_symbol)
            "escooter" -> BitmapFactory.decodeResource(resource, R.drawable.bus_symbol)
            "taxi" -> BitmapFactory.decodeResource(resource, R.drawable.bus_symbol)
            else-> null
        }
    }
}

data class TransportStop(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("distance")
    val distance: Float = 0f,
    @SerializedName("location")
    val location: TransportLocation = TransportLocation()
)

data class TransportOption(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("provider")
    val provider: String = "",
    @SerializedName("shortName")
    val shortName: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("product")
    val product: Int = 0,
    @SerializedName("distance")
    val distance: Float = 0f,
    @SerializedName("departureTimePlanned")
    val departureTimePlanned: String? = null,
    @SerializedName("departureTimeEstimated")
    val departureTimeEstimated: String? = null, // existiert nicht mehr!!
    @SerializedName("delayed")
    val delayed: Boolean = false,
    @SerializedName("delay")
    val delay: Int = 0,
    @SerializedName("energyLevel")
    val energyLevel: Float = -1f,
    @SerializedName("location")
    val location: TransportLocation = TransportLocation(),
    @SerializedName("iconUrl")
    val iconUrl: String = "",
    @SerializedName("symbolUrl")
    val symbolUrl: String = "",
    @SerializedName("station")
    val station: TransportStation = TransportStation(),
    @SerializedName("deeplinks")
    val deeplinks: TransportDeeplink = TransportDeeplink()
) {
    val departureTimeDisplay get() = departureTimeEstimated ?: departureTimePlanned

    fun getMinutesToDeparture(): Long {
        val now = LocalDateTime.now()
        val departure = Instant.parse(departureTimeDisplay).toKotlinInstant().toLocalDateTime(
            TimeZone.currentSystemDefault()).toJavaLocalDateTime()

        return ChronoUnit.MINUTES.between(now, departure).plus(getMinutesToEstimated())
    }

    fun getMinutesToEstimated(): Long {
        val planned = Instant.parse(departureTimeDisplay).toKotlinInstant().toLocalDateTime(
            TimeZone.currentSystemDefault()).toJavaLocalDateTime()
        val estimated = Instant.parse(departureTimeDisplay).toKotlinInstant().toLocalDateTime(
            TimeZone.currentSystemDefault()).toJavaLocalDateTime()
        return ChronoUnit.MINUTES.between(planned, estimated)
    }
}

data class TransportDeeplink(
    @SerializedName("android")
    val android: String = "",
    @SerializedName("ios")
    val ios: String = ""
)

data class TransportStation(
    @SerializedName("name")
    val name: String = "",
    @SerializedName("address")
    val address: String = "",
    @SerializedName("city")
    val city: String = ""
)

data class TransportLocation(
    @SerializedName("lat")
    val lat: Double = 0.0,
    @SerializedName("lon")
    val lon: Double = 0.0
) {
    fun getDistanceTo(latLng: LatLng): Float {
        return latLng.toCoordinates().distanceTo(Coordinates(latitude = lat, longitude = lon))
    }

    val getCoordinates get() = Coordinates(latitude = lat, longitude = lon)
    val getLatLng get() = getCoordinates.toLatLng()
}