package com.example.foodapp_java.dataClass;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.PropertyName;

import java.util.Date;

public class Favorite implements Parcelable {
    private String id;
    private String userId;
    private String foodId;
    @PropertyName("createdAt")
    private Date createdAt;

    public Favorite() {}

    public Favorite(String id, String userId, String foodId, Date createdAt) {
        this.id = id;
        this.userId = userId;
        this.foodId = foodId;
        this.createdAt = createdAt;
    }

    protected Favorite(Parcel in) {
        id = in.readString();
        userId = in.readString();
        foodId = in.readString();
        long c = in.readLong();
        createdAt = c == -1 ? null : new Date(c);
    }

    public static final Creator<Favorite> CREATOR = new Creator<Favorite>() {
        @Override
        public Favorite createFromParcel(Parcel in) {
            return new Favorite(in);
        }

        @Override
        public Favorite[] newArray(int size) {
            return new Favorite[size];
        }
    };

    // getters / setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getFoodId() { return foodId; }
    public void setFoodId(String foodId) { this.foodId = foodId; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(userId);
        dest.writeString(foodId);
        dest.writeLong(createdAt == null ? -1 : createdAt.getTime());
    }
}
