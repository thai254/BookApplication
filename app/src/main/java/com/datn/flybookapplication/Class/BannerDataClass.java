package com.datn.flybookapplication.Class;

public class BannerDataClass {
    private String txtNameBanner;
    private String txtContentBanner;
    private String imgBanner;
    private String bookId;
    private String userID;
    private String userAccount;
    public BannerDataClass(String bookId, String userID, String userAccount, String txtContentBanner, String txtNameBanner, String imgBanner){
        this.txtContentBanner = txtContentBanner;
        this.txtNameBanner = txtNameBanner;
        this.imgBanner = imgBanner;
        this.bookId = bookId;
        this.userAccount = userAccount;
        this.userID = userID;
    }

    public String getUserAccount() {
        return userAccount;
    }
    public String getUserID() {
        return userID;
    }
    public String getBookId() {
        return bookId;
    }
    public String getImgBanner() {
        return imgBanner;
    }

    public void setImgBanner(String imgBanner) {
        this.imgBanner = imgBanner;
    }

    public String getTxtContentBanner() {
        return txtContentBanner;
    }

    public void setTxtContentBanner(String txtContentBanner) {
        this.txtContentBanner = txtContentBanner;
    }

    public String getTxtNameBanner() {
        return txtNameBanner;
    }

    public void setTxtNameBanner(String txtNameBanner) {
        this.txtNameBanner = txtNameBanner;
    }
}
