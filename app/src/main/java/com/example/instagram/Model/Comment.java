package com.example.instagram.Model;

public class Comment {
    private String comment;
    private String publisher;
    private String commentId;
    Comment(){

    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getCommentId() {
        return commentId;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }
}
