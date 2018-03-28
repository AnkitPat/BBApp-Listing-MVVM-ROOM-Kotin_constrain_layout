package com.example.bbim1041.bbstore.model.api

import com.example.bbim1041.bbstore.model.data.App
import io.reactivex.Observable
import retrofit2.http.GET

/**
 * Created by BBIM1041 on 23/02/18.
 * 
 * Kotlin class for fetching data from API
 */


interface AppListApi {

    @GET("apk_list.json")
    fun getAppList(): Observable<List<App>>
}
