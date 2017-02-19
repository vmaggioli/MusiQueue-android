package com.example.vince.youtubeplayertest.Activities.helper_classes;

import android.app.Application;

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

    public int getUserID() {
        return id;
    }

    public void setUserID(int id) {
        this.id = id;
    }

    private String username;
    private String hubName;
    private String passPin;
    private int id;


}
