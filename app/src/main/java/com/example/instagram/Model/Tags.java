package com.example.instagram.Model;

public class Tags {
    private String postId;
    private String tag;

    public Tags(String postId, String tag){
        this.postId = postId;
        this.tag = tag;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
