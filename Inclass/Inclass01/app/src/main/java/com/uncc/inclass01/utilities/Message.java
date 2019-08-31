package com.uncc.inclass01.utilities;

import java.util.Date;
import java.util.List;

public class Message {

    String text;
    String postedAt;
    String id;
    String userId;
    List<String> userLiking;

    public Message() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(String postedAt) {
        this.postedAt = postedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getUserLiking() {
        return userLiking;
    }

    public void setUserLiking(List<String> userLiking) {
        this.userLiking = userLiking;
    }
}
