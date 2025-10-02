package com.example.foodapp_java.dataClass;

import android.os.Parcel;
import android.os.Parcelable;

public class CartItem implements Parcelable {
    private String id;
    private String userId;
    private String foodId;
    private int quantity;

    public CartItem() {}

    public CartItem(String id, String userId, String foodId, int quantity) {
        this.id = id;
        this.userId = userId;
        this.foodId = foodId;
        this.quantity = quantity;
    }

    protected CartItem(Parcel in) {
        id = in.readString();
        userId = in.readString();
        foodId = in.readString();
        quantity = in.readInt();
    }

    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getFoodId() { return foodId; }
    public int getQuantity() { return quantity; }

    public void setId(String id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setFoodId(String foodId) { this.foodId = foodId; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(userId);
        dest.writeString(foodId);
        dest.writeInt(quantity);
    }
}
