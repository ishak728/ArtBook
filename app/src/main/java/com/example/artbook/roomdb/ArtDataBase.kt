package com.example.artbook.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Art::class], version = 1)
abstract class ArtDataBase :RoomDatabase(){

    abstract fun artDao():ArtDao


}