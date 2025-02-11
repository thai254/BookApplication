package com.datn.flybookapplication.Class;

public class CommentDataClass {
    private String comment_id;
    private String book_id;
    private String user_id;
    private String book_account;
    private String comment_text;
    private String created_at;

    public CommentDataClass(String comment_id, String book_id, String user_id,
                            String book_account, String created_at, String comment_text) {
        this.comment_id = comment_id;
        this.book_id = book_id;
        this.user_id = user_id;
        this.book_account = book_account;
        this.created_at = created_at;
        this.comment_text = comment_text;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getBook_id() {
        return book_id;
    }

    public String getBook_account() {
        return book_account;
    }

    public String getComment_id() {
        return comment_id;
    }

    public String getComment_text() {
        return comment_text;
    }
}
