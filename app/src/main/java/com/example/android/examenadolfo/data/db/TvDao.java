package com.example.android.examenadolfo.data.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.android.examenadolfo.data.network.model.response.Tv;

import java.util.List;

@Dao
public interface TvDao {
    @Query("SELECT * FROM tv_table")
    List<Tv> getTvsOffline();

    @Insert()
    void insertAll(List<Tv> tvs);

    @Query("DELETE FROM tv_table")
    void deleteAll();
}
