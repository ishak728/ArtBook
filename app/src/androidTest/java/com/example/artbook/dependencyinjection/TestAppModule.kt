package com.example.artbook.dependencyinjection

import android.content.Context
import androidx.room.Room
import com.example.artbook.roomdb.ArtDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named


@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {


    @Provides
    @Named("testDatabase")
    fun injectInMemory(@ApplicationContext context: Context)= Room.inMemoryDatabaseBuilder(
        context,ArtDataBase::class.java
    ).allowMainThreadQueries().build()
}