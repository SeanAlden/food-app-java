//package com.example.firebaseauth;
//
//import android.os.Parcel;
//import android.os.Parcelable;
//
//public class User implements Parcelable {
//    private String uid;
//    private String email;
//    private String usertype;
//
//    public User() {
//        // diperlukan untuk Firestore
//    }
//
//    public User(String uid, String email, String usertype) {
//        this.uid = uid;
//        this.email = email;
//        this.usertype = usertype;
//    }
//
//    protected User(Parcel in) {
//        uid = in.readString();
//        email = in.readString();
//        usertype = in.readString();
//    }
//
//    public static final Creator<User> CREATOR = new Creator<User>() {
//        @Override
//        public User createFromParcel(Parcel in) {
//            return new User(in);
//        }
//
//        @Override
//        public User[] newArray(int size) {
//            return new User[size];
//        }
//    };
//
//    public String getUid() {
//        return uid;
//    }
//
//    public void setUid(String uid) {
//        this.uid = uid;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getUsertype() {
//        return usertype;
//    }
//
//    public void setUsertype(String usertype) {
//        this.usertype = usertype;
//    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel parcel, int i) {
//        parcel.writeString(uid);
//        parcel.writeString(email);
//        parcel.writeString(usertype);
//    }
//}

package com.example.foodapp_java.dataClass;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String uid;
    private String email;
    private String name;
    private String phone;
    private String usertype;
    private String profileUrl;
    private String address;

    public User() {}

    public User(String uid, String email, String name, String phone, String usertype, String profileUrl, String address) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.usertype = usertype;
        this.profileUrl = profileUrl;
        this.address = address;
    }

    protected User(Parcel parcel) {
        uid = parcel.readString();
        email = parcel.readString();
        name = parcel.readString();
        phone = parcel.readString();
        usertype = parcel.readString();
        profileUrl = parcel.readString();
        address = parcel.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUid() { return uid; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getUsertype() { return usertype; }
    public String getProfileUrl() { return profileUrl; }
    public String getAddress() { return address; }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uid);
        parcel.writeString(email);
        parcel.writeString(name);
        parcel.writeString(phone);
        parcel.writeString(usertype);
        parcel.writeString(profileUrl);
        parcel.writeString(address);
    }
}
