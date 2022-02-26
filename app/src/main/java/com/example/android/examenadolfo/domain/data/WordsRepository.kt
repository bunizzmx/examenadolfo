package com.example.android.examenadolfo.domain.data


import com.example.android.examenadolfo.data.network.model.response.ListTvResponse
import com.example.android.examenadolfo.data.network.model.response.Tv

import io.reactivex.Single


interface WordsRepository {
    fun getListTvs(): Single<ListTvResponse>
    fun saveAllTvs(tv:ArrayList<Tv>)
    fun getTvsOffline(): List<Tv>
}