package com.example.foodapp_java.dataBaseOffline;

import androidx.room.TypeConverter;

import com.example.foodapp_java.dataClass.CartItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class Converters {
    @TypeConverter
    public static String fromCartItemList(List<CartItem> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    @TypeConverter
    public static List<CartItem> toCartItemList(String value) {
        Type listType = new TypeToken<List<CartItem>>(){}.getType();
        return new Gson().fromJson(value, listType);
    }
}
