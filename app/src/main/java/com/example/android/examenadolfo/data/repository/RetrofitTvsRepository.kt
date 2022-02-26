package com.example.android.examenadolfo.data.repository




import android.util.Log
import com.example.android.examenadolfo.R
import com.example.android.examenadolfo.data.db.WordRoomDatabase
import com.example.android.examenadolfo.data.network.HandleServiceError
import com.example.android.examenadolfo.data.network.ServiceApi
import com.example.android.examenadolfo.data.network.model.response.ListTvResponse
import com.example.android.examenadolfo.data.network.model.response.Tv
import com.example.android.examenadolfo.domain.data.TvsRepository
import com.example.android.examenadolfo.utils.CONSTANTES
import com.example.android.examenadolfo.utils.treking.LocationsFirestore
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.reactivex.Single
import java.lang.Exception

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitTvsRepository
@Inject constructor(
    private val userApi: ServiceApi,
    private val db: WordRoomDatabase
) : HandleServiceError(), TvsRepository {



    override fun getListTvs(): Single<ListTvResponse> {
        return userApi.listTvs().map { serviceResponse ->
            handleResponse(serviceResponse)
            val response = serviceResponse.body()
            return@map response
        }
    }

    override fun saveAllTvs(tv:ArrayList<Tv>) {
        if(tv!=null) {
            if(tv.size>0) {
                db.tvDao().deleteAll()
                try{ db.tvDao().insertAll(tv)}catch (ex:Exception){}
            }
        }
    }

    override fun getTvsOffline(): List<Tv> {
        return db.tvDao().tvsOffline
    }



}