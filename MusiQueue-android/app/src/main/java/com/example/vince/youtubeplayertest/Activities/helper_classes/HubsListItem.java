package com.example.vince.youtubeplayertest.Activities.helper_classes;

public class HubsListItem {
    public String getHubName() {
        return hubName;
    }

    public void setHubName(String hubName) {
        this.hubName = hubName;
    }

    private String hubName;

    public HubsListItem(String name) {
       hubName = name;
    }
}
