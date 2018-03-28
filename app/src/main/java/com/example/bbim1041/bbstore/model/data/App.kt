package com.example.bbim1041.bbstore.model.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by BBIM1041 on 23/02/18.
 * Data class named as App for defining the model of App object
 */

@Entity(tableName = "app_table")
data class App(
        @PrimaryKey 
        @ColumnInfo(name = "apk_name") val apk_name: String,
        @ColumnInfo(name="apk_date") val apk_date: String,
        @ColumnInfo(name="apk_length") val apk_length: String,
        @ColumnInfo(name="apk_type") val apk_type: String,
        @ColumnInfo(name="apk_language") val apk_language: String,
        @ColumnInfo(name="apk_version") val apk_version: String,
        @ColumnInfo(name = "apk_description") val apk_description: String
        
        )