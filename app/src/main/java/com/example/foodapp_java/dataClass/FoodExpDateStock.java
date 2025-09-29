package com.example.foodapp_java.dataClass;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class FoodExpDateStock implements Parcelable {
    private String id;
    private String foodId;
    private int stock_amount;
    private Date exp_date;

    public FoodExpDateStock() {}

    public FoodExpDateStock(String id, String foodId, int stock_amount, Date exp_date) {
        this.id = id;
        this.foodId = foodId;
        this.stock_amount = stock_amount;
        this.exp_date = exp_date;
    }

    protected FoodExpDateStock(Parcel in) {
        id = in.readString();
        foodId = in.readString();
        stock_amount = in.readInt();
        long d = in.readLong();
        exp_date = d == -1 ? null : new Date(d);
    }

    public static final Creator<FoodExpDateStock> CREATOR = new Creator<FoodExpDateStock>() {
        @Override
        public FoodExpDateStock createFromParcel(Parcel in) { return new FoodExpDateStock(in); }
        @Override
        public FoodExpDateStock[] newArray(int size) { return new FoodExpDateStock[size]; }
    };

    public String getId() { return id; }
    public String getFoodId() { return foodId; }
    public int getStock_amount() { return stock_amount; }
    public Date getExp_date() { return exp_date; }

    public void setId(String id) { this.id = id; }
    public void setFoodId(String foodId) { this.foodId = foodId; }
    public void setStock_amount(int stock_amount) { this.stock_amount = stock_amount; }
    public void setExp_date(Date exp_date) { this.exp_date = exp_date; }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(foodId);
        dest.writeInt(stock_amount);
        dest.writeLong(exp_date == null ? -1 : exp_date.getTime());
    }
}
