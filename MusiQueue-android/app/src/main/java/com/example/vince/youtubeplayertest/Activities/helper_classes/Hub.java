package com.example.vince.youtubeplayertest.Activities.helper_classes;

import android.app.Application;
import android.content.Intent;
import java.util.Calendar;
import android.icu.util.TimeUnit;

import java.util.ArrayList;

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

    public Long getLastJoined() {
        return lastJoined = Calendar.getInstance().getTimeInMillis();
    }

    public void setLastJoined() {
        lastJoined = Calendar.getInstance().getTimeInMillis();
    }

    //checks for activity in last 7 days
    public boolean isRecent() {
        long msDiff = Calendar.getInstance().getTimeInMillis() - lastJoined;
        int daysDiff = (int) (msDiff / (1000 * 60 * 60 * 24));
        return daysDiff <= 7;
    }

    private String username;
    private String hubName;
    private String passPin;
    private String userId;
    private Integer hubId;
    private Long lastJoined;
}
