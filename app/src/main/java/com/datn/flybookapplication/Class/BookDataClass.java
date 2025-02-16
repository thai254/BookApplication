package com.datn.flybookapplication.Class;

import android.widget.ImageView;
import android.widget.TextView;

public class BookDataClass {
    private String bookId;
    private String chapterId;
    private String bookName;
    private String chapterName;
    private String authorId;
    private String createdAt;
    private String bookImage;
    private String userID;
    private String userAccount;
    private String bookType;

    public BookDataClass(String userID, String userAccount, String bookId, String chapterId, String bookName,
                         String chapterName, String authorId, String createdAt, String bookType, String bookImage) {
        this.userID = userID;
        this.userAccount = userAccount;
        this.bookId = bookId;
        this.chapterId = chapterId;
        this.bookName = bookName;
        this.chapterName = chapterName;
        this.authorId = authorId;
        this.createdAt = createdAt;
        this.bookType = bookType;
        this.bookImage = bookImage;
    }
    public String getUserAccount() {
        return userAccount;
    }
    public String getUserID() {
        return userID;
    }

    public String getBookType() {
        return bookType;
    }
    public String getBookId() {
        return bookId;
    }
    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
    public String getBookName() {
        return bookName;
    }
    public String getChapterName() {
        return chapterName;
    }
    public String getAuthorId() {
        return authorId;
    }
    public String getCreatedAt() {
        return createdAt;
    }
    public String getBookImage() {
        return bookImage;
    }
}
