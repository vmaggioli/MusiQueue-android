package com.example.musiqueue.HelperClasses;

import com.example.musiqueue.Activities.users_only.QueueSong;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.ArrayList;

public class HubSingleton {
    private static final HubSingleton ourInstance = new HubSingleton();
    private final ArrayList<User> users;
    private String hubName;
    private String passPin;
    private Integer hubId;
    private ArrayList<QueueSong> songsList;
    private GoogleSignInAccount account;

    private HubSingleton() {
        songsList = new ArrayList<QueueSong>();
        users = new ArrayList<User>();
    }

    public static HubSingleton getInstance() {
        return ourInstance;
    }

    public void setUserAccount(final GoogleSignInAccount account) {
        this.account = account;
    }

    public String getHubName() {
        return hubName;
    }

    public void setHubName(final String hubName) {
        this.hubName = hubName;
    }

    public String getPassPin() {
        return passPin;
    }

    public void setPassPin(final String passPin) {
        this.passPin = passPin;
    }

    public String getUsername() {
        return account.getDisplayName();
    }

    public String getUserID() {
        return account.getId();
    }

    public Integer getHubId() {
        return hubId;
    }

    public void setHubId(final Integer hubId) {
        this.hubId = hubId;
    }

    public ArrayList<QueueSong> getEntireList() {
        return songsList;
    }

    public void setEntireList(final ArrayList<QueueSong> songsList) {
        this.songsList = songsList;
    }

    public void removeAt(final int pos) {
        songsList.remove(pos);
    }

    public void insertAt(final int pos, final QueueSong song) {
        songsList.add(pos, song);
    }

    public void add(final QueueSong song) {
        songsList.add(song);
    }

    public void clearList() {
        songsList.clear();
    }

    public QueueSong getSongAt(final int pos) {
        return songsList.get(pos);
    }

    public int getQueueSize() {
        return songsList.size();
    }

    public void clearUsers() {
        users.clear();
    }

    public void addUser(final User name) {
        users.add(name);
    }

    public ArrayList<User> getUsers() {
        return this.users;
    }

}
