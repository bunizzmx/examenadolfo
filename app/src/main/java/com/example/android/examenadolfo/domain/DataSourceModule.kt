package com.example.android.examenadolfo.domain

import android.content.Context
import com.example.android.examenadolfo.data.db.WordRoomDatabase
import com.example.android.examenadolfo.data.repository.RetrofitWordRepository
import com.example.android.examenadolfo.domain.data.WordsRepository

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
    fun provideLoginRepository(repositoryRetrofit: RetrofitWordRepository): WordsRepository {
        return repositoryRetrofit
    }

}