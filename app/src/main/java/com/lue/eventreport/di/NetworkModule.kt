package com.lue.eventreport.di

import com.lue.eventreport.data.remote.MockNetworkClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideMockNetworkClient(): MockNetworkClient {
        return MockNetworkClient()
    }
}
