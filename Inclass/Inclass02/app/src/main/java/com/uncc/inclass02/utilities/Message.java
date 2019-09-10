package com.uncc.inclass02.utilities;

import java.util.Map;

public class Message {

    private String text;
    private String postedAt;
    private String id;
    private String userId;
    private String recipientId;
    private Map<String, String> userLiking;
    private String type;
    private String tripId;

    public Message() {
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
