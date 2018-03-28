package com.example.bbim1041.bbstore.model.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.example.bbim1041.bbstore.model.data.App
import io.reactivex.Single

/**
 * Created by BBIM1041 on 23/02/18.
 * 
 * Interface for doing create, insert and delete operation on database
 */


@Dao
interface AppListInterface {

    @Query("SELECT * FROM app_table")
    fun getAppList(): Single<List<App>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(appInstance: App)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(appList: List<App>)
    
    @Query("select distinct apk_date from app_table")
    fun getUniqueDates(): Single<List<String>>

    @Query("select * from app_table where apk_date like :arg0")
    fun getAppByDate(date: String): Single<List<App>>
}