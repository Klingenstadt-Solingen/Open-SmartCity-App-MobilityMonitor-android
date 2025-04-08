package de.osca.android.mobility.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.osca.android.mobility.data.MobilityRepository
import de.osca.android.mobility.data.MobilityRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class MobilityRepositoryModule {
    @Binds
    abstract fun provideMobilityRepository(repositoryImpl: MobilityRepositoryImpl): MobilityRepository
}