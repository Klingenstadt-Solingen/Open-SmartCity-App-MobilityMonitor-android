package de.osca.android.mobility.entity

import com.google.gson.annotations.SerializedName

data class MobilityRequestBody(
    @SerializedName("type")
    val type: String = "",
    @SerializedName("lat")
    val lat: Double = 0.0,
    @SerializedName("lon")
    val lon: Double = 0.0,
    @SerializedName("maxItems")
    val maxItems: Int = 5,
    @SerializedName("force")
    val force: Boolean = false
)