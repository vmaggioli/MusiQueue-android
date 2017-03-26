package com.example.vince.youtubeplayertest.Activities.helper_classes;

import android.app.Application;
import android.content.Intent;

// Helper class to store information across activities about a hub

public class Hub extends Application {
    public String getHubName() {
        return hubName;
    }

    public void setHubName(String hubName) {
        this.hubName = hubName;
    }

    public String getPassPin() {
        return passPin;
    }

    public void setPassPin(String passPin) {
        this.passPin = passPin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserID() {
        return userId;
    }

    public void setUserID(String id) {
        this.userId = id;
    }

    public Integer getHubId() { return hubId; }

    public void setHubId(Integer hubId) { this.hubId = hubId; }

    private String username;
    private String hubName;
    private String passPin;
    private String userId;
    private Integer hubId;


}
