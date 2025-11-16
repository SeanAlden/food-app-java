// path: app/src/main/java/com/example/foodapp_java/dataClass/Supplier.java
package com.example.foodapp_java.dataClass;

import android.os.Parcel;
import android.os.Parcelable;

public class Supplier implements Parcelable {
    private String id;
    private String name;
    private String code;
    private String phone;
    private String description;
    private String address;
    private String image; // local path stored in Firestore
    private boolean canDelete = true;

    public Supplier() {
    }

    public Supplier(String id, String name, String code, String phone, String description, String address, String image) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.phone = phone;
        this.description = description;
        this.address = address;
        this.image = image;
    }

    protected Supplier(Parcel in) {
        id = in.readString();
        name = in.readString();
        code = in.readString();
        phone = in.readString();
        description = in.readString();
        address = in.readString();
        image = in.readString();
    }

    public static final Creator<Supplier> CREATOR = new Creator<Supplier>() {
        @Override
        public Supplier createFromParcel(Parcel in) {
            return new Supplier(in);
        }

        @Override
        public Supplier[] newArray(int size) {
            return new Supplier[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isCanDelete() {
        return canDelete;
    }

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(code);
        parcel.writeString(phone);
        parcel.writeString(description);
        parcel.writeString(address);
        parcel.writeString(image);
    }
}
