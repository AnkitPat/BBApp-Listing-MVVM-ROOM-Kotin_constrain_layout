package com.example.bbim1041.bbstore

import android.app.Application
import android.arch.persistence.room.Room
import com.example.bbim1041.bbstore.model.AppInstanceCollection
import com.example.bbim1041.bbstore.model.api.AppListApi
import com.example.bbim1041.bbstore.model.database.AppDatabase
import com.example.bbim1041.bbstore.viewmodel.AppListViewModel
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by BBIM1041 on 23/02/18.
 * 
 * Base Application, it contain methods which is need to be called at the time of start of App
 */
class BaseApplication: Application() {

    companion object {
        lateinit var retrofit: Retrofit
        lateinit var appApi: AppListApi
        lateinit var appInstanceCollection: AppInstanceCollection
        lateinit var appListViewModel: AppListViewModel
        lateinit var appDatabase: AppDatabase

        fun injectUserApi() = appApi
        fun injectUserListViewModel() = appListViewModel

        fun injectUserDao() = appDatabase.appObject()
    }
    
    override fun onCreate() {
        super.onCreate()

        retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(resources.getString(R.string.base_url))
                .build()

        val service = retrofit.create<AppListApi>(AppListApi::class.java)

        appApi = retrofit.create(AppListApi::class.java)
        
        
        appDatabase = Room.databaseBuilder(applicationContext,
                AppDatabase::class.java, "mvvm-database").build()

        appInstanceCollection = AppInstanceCollection(appApi, appDatabase.appObject())
        appListViewModel = AppListViewModel(appInstanceCollection)
    }
}