package com.example.android.examenadolfo.data.network


import com.example.android.examenadolfo.data.network.model.response.DetailUsersResponse
import com.example.android.examenadolfo.data.network.model.response.ListTvResponse
import io.reactivex.Single
import retrofit2.Response

import retrofit2.http.*


interface ServiceApi {

    @GET("api/users/{page}")
    fun singleUser(@Path("page") page: Int): Single<Response<DetailUsersResponse>>


    @GET("tv/popular?api_key=1023cdaaefe4131ba4a4a467f912545f&language=en-US")
    fun listUsers(): Single<Response<ListTvResponse>>


}
