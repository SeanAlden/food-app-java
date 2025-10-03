package com.example.foodapp_java.dataBaseOffline;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.foodapp_java.dataBaseOffline.Converters;
import com.example.foodapp_java.dataBaseOffline.TransactionOfflineDAO;

@Database(entities = {TransactionOffline.class}, version = 1, exportSchema = false)
@TypeConverters(Converters.class)
public abstract class TransactionOfflineDatabase extends RoomDatabase {
    private static TransactionOfflineDatabase instance;

    public abstract com.example.foodapp_java.dataBaseOffline.TransactionOfflineDAO transactionDao();

    public static synchronized TransactionOfflineDatabase getInstance(Context ctx) {
        if (instance == null) {
            instance = Room.databaseBuilder(ctx.getApplicationContext(),
                            TransactionOfflineDatabase.class, "transaction_db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries() // ⚠️ boleh dihapus kalau mau pakai background thread
                    .build();
        }
        return instance;
    }
}
