package com.thanh.kotlintesting.network.service

import com.thanh.kotlintesting.models.ResponseModel
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Url

interface NcoviService {
    @GET
    fun getData(@Url url:String):Observable<ResponseModel>
}