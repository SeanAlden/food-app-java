package com.example.foodapp_java.dataClass;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.PropertyName;

import java.util.Date;

public class StockEntry implements Parcelable {

    @PropertyName("id")
    private String id;

    @PropertyName("foodId")
    private String foodId;
    private String foodName;

    @PropertyName("exp_date")
    private Date exp_date;
    private int qty;

    @PropertyName("supplierId")
    private String supplierId;

//    @PropertyName("name")
    private String supplierName;

    @PropertyName("userId")
    private String userId;
    private String operatorName;
    private String expStockId;

    @PropertyName("timestamp")
    private Date createdAt;

    private String categoryName;
    private String imagePath;

    public StockEntry() {}

    public StockEntry(String id, String foodId, String foodName, Date exp_date, int qty,
                      String supplierId, String supplierName,
                      String operatorUid, String operatorName, String expStockId, Date createdAt) {
        this.id = id;
        this.foodId = foodId;
        this.foodName = foodName;
        this.exp_date = exp_date;
        this.qty = qty;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.userId = operatorUid;
        this.operatorName = operatorName;
        this.expStockId = expStockId;
        this.createdAt = createdAt;
    }

    protected StockEntry(Parcel in) {
        id = in.readString();
        foodId = in.readString();
        foodName = in.readString();
        long e = in.readLong();
        exp_date = e == -1 ? null : new Date(e);
        qty = in.readInt();
        supplierId = in.readString();
        supplierName = in.readString();
        userId = in.readString();
        operatorName = in.readString();
        expStockId = in.readString();
        long c = in.readLong();
        createdAt = c == -1 ? null : new Date(c);
    }

    public static final Creator<StockEntry> CREATOR = new Creator<StockEntry>() {
        @Override
        public StockEntry createFromParcel(Parcel in) {
            return new StockEntry(in);
        }

        @Override
        public StockEntry[] newArray(int size) {
            return new StockEntry[size];
        }
    };

    // getters / setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFoodId() { return foodId; }
    public void setFoodId(String foodId) { this.foodId = foodId; }
    public String getFoodName() { return foodName; }
    public void setFoodName(String foodName) { this.foodName = foodName; }
    public Date getExp_date() { return exp_date; }
    public void setExp_date(Date exp_date) { this.exp_date = exp_date; }
    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }
    public String getSupplierId() { return supplierId; }
    public void setSupplierId(String supplierId) { this.supplierId = supplierId; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
    public String getExpStockId() { return expStockId; }
    public void setExpStockId(String expStockId) { this.expStockId = expStockId; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(foodId);
        dest.writeString(foodName);
        dest.writeLong(exp_date == null ? -1 : exp_date.getTime());
        dest.writeInt(qty);
        dest.writeString(supplierId);
        dest.writeString(supplierName);
        dest.writeString(userId);
        dest.writeString(operatorName);
        dest.writeString(expStockId);
        dest.writeLong(createdAt == null ? -1 : createdAt.getTime());
    }
}
