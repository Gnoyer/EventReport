package com.lue.eventreport.di

import com.lue.eventreport.data.local.EventDao
import com.lue.eventreport.data.remote.MockNetworkClient
import com.lue.eventreport.data.repository.EventRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideEventRepository(
        eventDao: EventDao,
        mockNetworkClient: MockNetworkClient
    ): EventRepository {
        return EventRepository(eventDao, mockNetworkClient)
    }
}
