//package com.example.foodapp_java.dataClass;
//
//import android.os.Parcel;
//import android.os.Parcelable;
//
//import java.util.Date;
//
//public class Food implements Parcelable {
//    private String id;
//    private String name;
//    private double price;
//    private String description;
//    private String status;
//    private String imagePath;
//
//    // transient/helper fields (not required in Firestore)
//    private Date nearestExpDate;
//    private int totalStock;
//
//    public Food() {}
//
//    public Food(String id, String name, double price, String description, String status, String imagePath) {
//        this.id = id;
//        this.name = name;
//        this.price = price;
//        this.description = description;
//        this.status = status;
//        this.imagePath = imagePath;
//    }
//
//    protected Food(Parcel in) {
//        id = in.readString();
//        name = in.readString();
//        price = in.readDouble();
//        description = in.readString();
//        status = in.readString();
//        imagePath = in.readString();
//        long d = in.readLong();
//        nearestExpDate = d == -1 ? null : new Date(d);
//        totalStock = in.readInt();
//    }
//
//    public static final Creator<Food> CREATOR = new Creator<Food>() {
//        @Override
//        public Food createFromParcel(Parcel in) { return new Food(in); }
//        @Override
//        public Food[] newArray(int size) { return new Food[size]; }
//    };
//
//    // getters + setters
//    public String getId() { return id; }
//    public void setId(String id) { this.id = id; }
//
//    public String getName() { return name; }
//    public void setName(String name) { this.name = name; }
//
//    public double getPrice() { return price; }
//    public void setPrice(double price) { this.price = price; }
//
//    public String getDescription() { return description; }
//    public void setDescription(String description) { this.description = description; }
//
//    public String getStatus() { return status; }
//    public void setStatus(String status) { this.status = status; }
//
//    public String getImagePath() { return imagePath; }
//    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
//
//    public Date getNearestExpDate() { return nearestExpDate; }
//    public void setNearestExpDate(Date nearestExpDate) { this.nearestExpDate = nearestExpDate; }
//
//    public int getTotalStock() { return totalStock; }
//    public void setTotalStock(int totalStock) { this.totalStock = totalStock; }
//
//    @Override
//    public int describeContents() { return 0; }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(id);
//        dest.writeString(name);
//        dest.writeDouble(price);
//        dest.writeString(description);
//        dest.writeString(status);
//        dest.writeString(imagePath);
//        dest.writeLong(nearestExpDate == null ? -1 : nearestExpDate.getTime());
//        dest.writeInt(totalStock);
//    }
//}

package com.example.foodapp_java.dataClass;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.PropertyName;

import java.util.Date;

public class Food implements Parcelable {
    private String id;
    private String name;
    private double price;
    private String description;
    private String status;
    private String imagePath;

    @PropertyName("category_id")
    private String categoryId; // NEW

    // transient/helper fields (not required in Firestore)
    private Date nearestExpDate;
    private int totalStock;

    public Food() {}

    // optional constructor including categoryId
    public Food(String id, String name, double price, String description, String status, String imagePath, String categoryId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.status = status;
        this.imagePath = imagePath;
        this.categoryId = categoryId;
    }

    protected Food(Parcel in) {
        id = in.readString();
        name = in.readString();
        price = in.readDouble();
        description = in.readString();
        status = in.readString();
        imagePath = in.readString();
        categoryId = in.readString(); // read categoryId
        long d = in.readLong();
        nearestExpDate = d == -1 ? null : new Date(d);
        totalStock = in.readInt();
    }

    public static final Creator<Food> CREATOR = new Creator<Food>() {
        @Override
        public Food createFromParcel(Parcel in) { return new Food(in); }
        @Override
        public Food[] newArray(int size) { return new Food[size]; }
    };

    // getters + setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    @PropertyName("category_id")
    public String getCategoryId() { return categoryId; }

    @PropertyName("category_id")
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public Date getNearestExpDate() { return nearestExpDate; }
    public void setNearestExpDate(Date nearestExpDate) { this.nearestExpDate = nearestExpDate; }

    public int getTotalStock() { return totalStock; }
    public void setTotalStock(int totalStock) { this.totalStock = totalStock; }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeDouble(price);
        dest.writeString(description);
        dest.writeString(status);
        dest.writeString(imagePath);
        dest.writeString(categoryId); // write categoryId
        dest.writeLong(nearestExpDate == null ? -1 : nearestExpDate.getTime());
        dest.writeInt(totalStock);
    }
}
