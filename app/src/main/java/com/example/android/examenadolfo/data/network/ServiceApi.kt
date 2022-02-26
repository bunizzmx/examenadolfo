package com.example.android.examenadolfo.data.network


import com.example.android.examenadolfo.data.network.model.response.ListTvResponse
import io.reactivex.Single
import retrofit2.Response

import retrofit2.http.*


interface ServiceApi {

    @GET("tv/popular?api_key=1023cdaaefe4131ba4a4a467f912545f&language=en-US")
    fun listTvs(): Single<Response<ListTvResponse>>


}
