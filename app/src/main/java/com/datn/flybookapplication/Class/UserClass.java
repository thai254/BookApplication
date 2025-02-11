package com.datn.flybookapplication.Class;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

public class UserClass {
    private String userId;
    private String bookAccount;
    private String bookPassword;
    private String email;
    private String bookRole;
    private String status;
    private String createdAt;
    private String updatedAt;
    private String userImgBase64; // Base64 string from API
    private Bitmap userImgBitmap; // Decoded Bitmap image

    // Constructor
    public UserClass(String userId, String bookAccount, String bookPassword, String email,
                String bookRole, String status, String createdAt, String updatedAt, String userImgBase64) {
        Log.v("User class: ", userId + " - " + bookAccount + " - " + bookPassword);
        Log.v("User class2: ", email + " - " + bookRole + " - " + status + " - " + createdAt + " - " +updatedAt);

        this.userId = userId;
        this.bookAccount = bookAccount;
        this.bookPassword = bookPassword;
        this.email = email;
        this.bookRole = bookRole;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userImgBase64 = userImgBase64;
        this.userImgBitmap = decodeBase64ToBitmap(userImgBase64); // Decode and store Bitmap
    }

    public String getUserId() { return userId; }

    public String getBookAccount() {
        return bookAccount;
    }
    public String getBookPassword() { return bookPassword; }
    public String getEmail() {
        return email;
    }
    public String getBookRole() { return bookRole; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public String getUserImgBase64() { return userImgBase64; }
    public void setUserImgBase64(String userImgBase64) {
        this.userImgBase64 = userImgBase64;
        this.userImgBitmap = decodeBase64ToBitmap(userImgBase64); // Decode and store Bitmap
    }

    public Bitmap getUserImgBitmap() { return userImgBitmap; }

    // Method to decode Base64 string to Bitmap
    private Bitmap decodeBase64ToBitmap(String base64Str) {
        try {
            byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }
}


