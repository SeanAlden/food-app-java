package com.example.foodapp_java.dataBaseOffline;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.foodapp_java.dataClass.CartItem;
import com.example.foodapp_java.dataBaseOffline.Converters;

import java.util.List;

@Entity(tableName = "transactions")
public class TransactionOffline {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String userId;
    private long timestamp;
    private double totalPrice;

    @TypeConverters(Converters.class)
    private List<CartItem> items; // simpan JSON dari List<CartItem>

    public TransactionOffline(String userId, long timestamp, double totalPrice, List<CartItem> items) {
        this.userId = userId;
        this.timestamp = timestamp;
        this.totalPrice = totalPrice;
        this.items = items;
    }

    // Getter Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }
}
