package com.example.vince.youtubeplayertest.Activities.helper_classes;

import android.app.Application;

// USE THIS CLASS IF YOU NEED A VARIABLE TO SPAN THE ENTIRE APP

public class MyApplication extends Application {
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String username;

}
