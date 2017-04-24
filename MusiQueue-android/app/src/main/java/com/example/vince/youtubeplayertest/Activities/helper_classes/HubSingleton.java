package com.example.vince.youtubeplayertest.Activities.helper_classes;

import com.example.vince.youtubeplayertest.Activities.hub_admin_only.User;
import com.example.vince.youtubeplayertest.Activities.users_only.QueueSong;

import java.util.ArrayList;

/**
 * Created by Brian on 3/20/2017.
 */
public class HubSingleton {
    private static HubSingleton ourInstance = new HubSingleton();

    public static HubSingleton getInstance() {
        return ourInstance;
    }

    private HubSingleton() {
        songsList = new ArrayList<QueueSong>();
        users = new ArrayList<User>();
    }

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

    public void setEntireList(ArrayList<QueueSong> songsList) { this.songsList = songsList; }

    public ArrayList<QueueSong> getEntireList() { return songsList; }

    public void removeAt(int pos) { songsList.remove(pos); }

    public void insertAt(int pos, QueueSong song) { songsList.add(pos, song); }

    public void add(QueueSong song) { songsList.add(song); }

    public void clearList() { songsList.clear(); }

    public QueueSong getSongAt(int pos) { return songsList.get(pos); }

    public int getQueueSize() { return songsList.size();}

    public void clearUsers() {
        users.clear();
    }
    public void addUser(User name) {
        users.add(name);
    }

    public ArrayList<User> getUsers() {return this.users;}

    private String username;
    private String hubName;
    private String passPin;
    private String userId;
    private Integer hubId;
    private ArrayList<QueueSong> songsList;
    private ArrayList<User> users;

}
