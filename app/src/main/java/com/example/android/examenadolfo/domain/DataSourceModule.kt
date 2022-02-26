package com.example.android.examenadolfo.domain

import android.content.Context
import com.example.android.examenadolfo.data.db.WordRoomDatabase
import com.example.android.examenadolfo.data.repository.RetrofitTvsRepository
import com.example.android.examenadolfo.domain.data.TvsRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataSourceModule {

    @Singleton
    @Provides
    fun provideWordsDatabase(context: Context): WordRoomDatabase {
        return WordRoomDatabase.getDatabase(context)
    }


    @Singleton
    @Provides
    fun provideLoginRepository(repositoryRetrofit: RetrofitTvsRepository): TvsRepository {
        return repositoryRetrofit
    }


}