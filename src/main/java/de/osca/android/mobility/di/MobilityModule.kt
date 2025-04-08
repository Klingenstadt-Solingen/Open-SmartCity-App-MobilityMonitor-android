package de.osca.android.mobility.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.osca.android.essentials.data.client.OSCAHttpClient
import de.osca.android.mobility.data.MobilityApiService
import javax.inject.Singleton

/**
 * The dependency injection
 */
@Module
@InstallIn(SingletonComponent::class)
class MobilityModule {

    @Singleton
    @Provides
    fun mobilityApiService(oscaHttpClient: OSCAHttpClient): MobilityApiService =
        oscaHttpClient.create(MobilityApiService::class.java)
}