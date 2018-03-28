package com.example.bbim1041.bbstore.model.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.example.bbim1041.bbstore.model.data.App

/**
 * Created by BBIM1041 on 23/02/18.
 * 
 * Database for saving app list in local database
 */

@Database(entities = arrayOf(App::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appObject(): AppListInterface
}
