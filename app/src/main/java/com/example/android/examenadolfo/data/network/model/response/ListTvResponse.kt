package com.example.android.examenadolfo.data.network.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ListTvResponse(
    @Expose
    @SerializedName("page")
    var page:Int,
    @Expose
    @SerializedName("results")
    var reluts:ArrayList<Tv>
)
