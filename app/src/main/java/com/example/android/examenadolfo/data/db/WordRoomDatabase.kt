package com.example.android.examenadolfo.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.android.examenadolfo.data.network.model.response.Tv

@Database(entities = [Tv::class], version = 2,exportSchema = false)
abstract class WordRoomDatabase : RoomDatabase() {

    abstract fun tvDao(): TvDao

    companion object {
        private const val NUMBER_OF_THREADS = 4
        @Volatile
        private var INSTANCE: WordRoomDatabase? = null

        fun getDatabase(context: Context): WordRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WordRoomDatabase::class.java,
                    "word_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }

    }


}