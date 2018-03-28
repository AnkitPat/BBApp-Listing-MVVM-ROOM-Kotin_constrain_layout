package com.example.bbim1041.bbstore.viewmodel

import android.util.Log
import com.example.bbim1041.bbstore.model.AppInstanceCollection
import com.example.bbim1041.bbstore.model.data.App
import io.reactivex.Observable

/**
 * Created by BBIM1041 on 23/02/18.
 * View-Model class which work as a interface between view and model class. It mainly helps in trasfering data from model to view.
 */


class  AppListViewModel(val appInstanceCollection: AppInstanceCollection ) {
    
    var dummyAppList: ArrayList<App> = ArrayList()
    var dummyDateList: ArrayList<String> = ArrayList()


    fun getAppList(): Observable<List<App>> {
        return appInstanceCollection.getAppList()
                .onErrorReturn { dummyAppList }
                
    }
    
    fun getUniqueDates() : Observable<List<String>> {
       
        return appInstanceCollection.getUniqueDates()
                .onErrorReturn { dummyDateList }
    }
    
    fun getAppByDates( date: String) : Observable<List<App>> {
        return appInstanceCollection.getAppByDates(date)
                .onErrorReturn {
                    dummyAppList
                }
    }
} 