package de.osca.android.mobility.data

import de.osca.android.mobility.entity.MobilityRequestBody
import de.osca.android.mobility.entity.TransportData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface MobilityApiService {

    @POST("functions/mobility")
    suspend fun getMobilityForType(@Body mobilityRequestBody: MobilityRequestBody): Response<List<TransportData>>
}