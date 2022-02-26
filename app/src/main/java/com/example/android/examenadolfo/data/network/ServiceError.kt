package com.example.android.examenadolfo.data.network

import com.google.gson.annotations.SerializedName
import java.net.HttpURLConnection

data class ServiceError(
    val httpCode: Int = HttpURLConnection.HTTP_UNAVAILABLE,

    @SerializedName("errorCode")
    val errorCode: Int,

    @SerializedName("error")
    val exception: String,

    @SerializedName("message")
    val message: String
)