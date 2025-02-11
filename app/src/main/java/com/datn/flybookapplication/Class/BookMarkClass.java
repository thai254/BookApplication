package com.datn.flybookapplication.Class;

public class BookMarkClass {
    private String bookmark_id;
    private String book_id;
    private String created_at;
    private String book_name;
    private String book_image;
    private String user_id;
    private String chapter_name, chapter_updated;

    public BookMarkClass(String user_id, String bookmark_id, String book_id,
                         String book_name, String book_image, String created_at,
                         String chapter_name, String chapter_updated) {
        this.user_id = user_id;
        this.bookmark_id = bookmark_id;
        this.book_id = book_id;
        this.book_name = book_name;
        this.book_image = book_image;
        this.created_at = created_at;
        this.chapter_name = chapter_name;
        this.chapter_updated = chapter_updated;
    }

    public String getBook_id() {
        return book_id;
    }
    public String getBook_image() {
        return book_image;
    }
    public String getBook_name() {
        return book_name;
    }
    public String getBookmark_id() {
        return bookmark_id;
    }
    public String getCreated_at() {
        return created_at;
    }
    public String getUser_id() {
        return user_id;
    }
    public String getChapter_name() {
        return chapter_name;
    }
    public String getChapter_updated() {
        return chapter_updated;
    }
}

