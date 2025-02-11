package com.datn.flybookapplication.Class;

public class SearchDataClass {
    private String bookId;
    private String bookName;
    private String chapterName;
    private String bookImage;
    public SearchDataClass(String bookId, String bookName, String chapterName, String bookImage) {
        this.bookId = bookId;
        this.bookName = bookName;
        this.chapterName = chapterName;
        this.bookImage = bookImage;
    }
    public String getBookName() {
        return bookName;
    }
    public String getChapterName() {
        return chapterName;
    }
    public String getBookId() {
        return bookId;
    }

    public String getBookImage() {
        return bookImage;
    }
}
