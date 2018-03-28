package com.example.bbim1041.bbstore.model

import android.util.Log
import com.example.bbim1041.bbstore.model.api.AppListApi
import com.example.bbim1041.bbstore.model.data.App
import com.example.bbim1041.bbstore.model.database.AppListInterface
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.ArrayList

/**
 * Created by BBIM1041 on 23/02/18.
 * This class is mainly a collection of database and api class of model
 * which take api class and database class instance as constructor
 */

class AppInstanceCollection(val appApi: AppListApi, val appDatabaseInterface:  AppListInterface) {
    
    fun getAppList(): Observable<List<App>> {
        return Observable.merge(
                getAppListFromApi(),
                getAppListFromDatabase()
               
                
        )
    }
    
    fun getUniqueDates( ) : Observable<List<String>> {
        return appDatabaseInterface.getUniqueDates()
                .toObservable()
                .doOnNext {
                    Log.v("database_recieved"," data ${it.size} ")
                }
    }


    fun getAppByDates( date: String ) : Observable<List<App>> {
        return appDatabaseInterface.getAppByDate(date)
                .toObservable()
                .doOnNext {
                    Log.v("database_recieved"," data ${it.size} ")
                }
    }



    fun getAppListFromApi(): Observable<List<App>> {
        
        val listApp: List<App> = ArrayList()
        try {
            return appApi.getAppList().doOnNext{
                storeAppInDatabase(it)
            }.onErrorReturn { listApp }
        } catch (e: Exception) {
            return Observable.fromArray(listApp)
        }
    }
    
    fun getAppListFromDatabase(): Observable<List<App>> {
        return appDatabaseInterface.getAppList()
                .toObservable()
                .doOnNext { 
                    Log.v("database_recieved"," data ${it.size} ")
                }
    }
    
    fun storeAppInDatabase(apps: List<App>) {
        Observable.fromCallable { appDatabaseInterface.insertAll(apps) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { 
                    Log.v("database_inserted","data insert ${apps.size}")
                }
    }
}

