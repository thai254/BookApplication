package com.datn.flybookapplication.Class;

public class ChapterDataClass {
    private String chapterId;
    private String chapterName;
    private String createdAt;
    private String bookId;
    private String userId;

    public ChapterDataClass(String chapterId, String chapterName, String createdAt, String bookId, String userId) {
        this.chapterId = chapterId;
        this.chapterName = chapterName;
        this.createdAt = createdAt;
        this.bookId = bookId;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
    public String getChapterId() {
        return chapterId;
    }
    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }
    public String getChapterName() {
        return chapterName;
    }
    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }
    public String getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    public String getBookId() {
        return bookId;
    }
    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
}
