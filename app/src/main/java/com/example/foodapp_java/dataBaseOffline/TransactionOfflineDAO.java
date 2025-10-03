package com.example.foodapp_java.dataBaseOffline;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.foodapp_java.dataBaseOffline.TransactionOffline;

import java.util.List;

@Dao
public interface TransactionOfflineDAO {
    @Insert
    void insert(TransactionOffline transaction);

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    List<TransactionOffline> getAll();

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    TransactionOffline getById(int id);
}
