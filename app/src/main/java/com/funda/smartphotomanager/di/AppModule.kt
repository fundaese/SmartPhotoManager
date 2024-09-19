package com.funda.smartphotomanager.di

import android.content.Context
import com.funda.smartphotomanager.data.repositories.PhotoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePhotoRepository(@ApplicationContext context: Context): PhotoRepository {
        return PhotoRepository(context)
    }
}
