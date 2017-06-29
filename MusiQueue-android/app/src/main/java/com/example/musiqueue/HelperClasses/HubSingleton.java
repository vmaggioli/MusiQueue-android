package com.example.musiqueue.HelperClasses;

import com.example.musiqueue.Activities.users_only.QueueSong;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.ArrayList;

public class HubSingleton {
    private static HubSingleton ourInstance = new HubSingleton();

    public static HubSingleton getInstance() {
        return ourInstance;
    }

    private HubSingleton() {
        songsList = new ArrayList<QueueSong>();
        users = new ArrayList<User>();
    }

    public GoogleSignInAccount getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(GoogleSignInAccount user) {
        currentUser = user;
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

    public String getUsername() {
        return currentUser.getDisplayName();
    }

    public String getUserID() {
        return currentUser.getId();
    }

    public Integer getHubId() {
        return hubId;
    }

    public void setHubId(Integer hubId) {
        this.hubId = hubId;
    }

    public ArrayList<QueueSong> getEntireList() {
        return songsList;
    }

    public void add(QueueSong song) {
        songsList.add(song);
    }

    public void clearList() {
        songsList.clear();
    }

    public QueueSong getSongAt(int pos) {
        return songsList.get(pos);
    }

    public void removeAt(int pos) {
        songsList.remove(pos);
    }

    public int getQueueSize() {
        return songsList.size();
    }

    public void clearUsers() {
        users.clear();
    }

    public void addUser(User name) {
        users.add(name);
    }

    public ArrayList<User> getUsers() {
        return this.users;
    }

    private String hubName;
    private String passPin;
    private Integer hubId;
    private ArrayList<QueueSong> songsList;
    private ArrayList<User> users;
    private static GoogleSignInAccount currentUser = null;
}