package com.sagar.snake.di

import com.sagar.snake.data_store.LocalDataStore
import com.sagar.snake.data_store.LocalDataStoreImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class InterfaceModule {

    @Binds
    abstract fun bindsLocalDataStore(
        localDataStoreImpl: LocalDataStoreImpl
    ): LocalDataStore
}