package com.example.android.examenadolfo.data.repository




import android.util.Log
import com.example.android.examenadolfo.data.db.WordRoomDatabase
import com.example.android.examenadolfo.data.network.HandleServiceError
import com.example.android.examenadolfo.data.network.ServiceApi
import com.example.android.examenadolfo.data.network.model.response.ListTvResponse
import com.example.android.examenadolfo.data.network.model.response.Tv
import com.example.android.examenadolfo.domain.data.WordsRepository
import io.reactivex.Single
import java.lang.Exception

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitWordRepository
@Inject constructor(
    private val userApi: ServiceApi,
    private val db: WordRoomDatabase
) : HandleServiceError(), WordsRepository {



    override fun getListTvs(): Single<ListTvResponse> {
        return userApi.listUsers().map { serviceResponse ->
            handleResponse(serviceResponse)
            val response = serviceResponse.body()
            return@map response
        }
    }

    override fun saveAllTvs(tv:ArrayList<Tv>) {
        Log.e("SAVE_LOCAL","s:"+tv.size)
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