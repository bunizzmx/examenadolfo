package com.example.android.examenadolfo.domain.data


import com.example.android.examenadolfo.data.network.model.response.ListTvResponse
import com.example.android.examenadolfo.data.network.model.response.Tv
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot

import io.reactivex.Single


interface TvsRepository {
    fun getListTvs(): Single<ListTvResponse>
    fun saveAllTvs(tv:ArrayList<Tv>)
    fun getTvsOffline(): List<Tv>
}