package com.datn.flybookapplication.Class;

public class HistoryDataClass {
    private String historyID;
    private String bookID;
    private String userID;
    private String chapterID;
    private String chapterName;
    private String createdAT;
    private String bookName;
    private String bookImage;
    private String bookType;

    public HistoryDataClass(String historyID, String bookID, String userID, String chapterID,
                            String chapterName, String createdAT, String bookName, String bookType, String bookImage) {
        this.historyID = historyID;
        this.bookID = bookID;
        this.userID = userID;
        this.chapterID = chapterID;
        this.chapterName = chapterName;
        this.createdAT = createdAT;
        this.bookName = bookName;
        this.bookImage = bookImage;
        this.bookType = bookType;
    }
    public String getUserID() {
        return userID;
    }
    public String getBookType() {
        return bookType;
    }
    public String getChapterName() {
        return chapterName;
    }
    public String getBookName() {
        return bookName;
    }
    public String getBookImage() {
        return bookImage;
    }
    public String getChapterID() {
        return chapterID;
    }
    public String getBookID() {
        return bookID;
    }
    public String getCreatedAT() {
        return createdAT;
    }
    public String getHistoryID() {
        return historyID;
    }
}
