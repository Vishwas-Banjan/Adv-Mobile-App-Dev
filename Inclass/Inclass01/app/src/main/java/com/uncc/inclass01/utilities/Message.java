package com.uncc.inclass01.utilities;

import java.util.List;
import java.util.Map;

public class Message {

    String text;
    String postedAt;
    String id;
    String userId;
    Map<String, String> userLiking;

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

    public Map<String, String> getUserLiking() {
        return userLiking;
    }

    public void setUserLiking(Map<String, String> userLiking) {
        this.userLiking = userLiking;
    }
}
