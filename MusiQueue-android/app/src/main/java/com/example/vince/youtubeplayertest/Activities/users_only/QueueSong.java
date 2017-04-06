package com.example.vince.youtubeplayertest.Activities.users_only;

/**
 * Created by Sam on 2/26/2017.
 */

public class QueueSong {
    private String title;
    private int upVotes = 0;
    private int downVotes=0;
    private String id;
    private String user;
    private int place;
    private int pressed;

    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public int getUpVotes() {
        return this.upVotes;
    }
    public void setUpVotes(int upVotes) {
        this.upVotes = upVotes;
    }
    public int getDownVotes() {
        return this.downVotes;
    }
    public void setDownVotes(int downVotes) {
        this.downVotes = downVotes;
    }
    public String getId() {return this.id;}
    public void setId(String id) {this.id = id;}
    public void setUser(String user) {this.user= user;}
    public String getUser(){return this.user;}
    public int getPlace() {
        return place;
    }
    public void setPlace(int place) {
        this.place = place;
    }
    public int getPressed() {return pressed;}
    public void setPressed(int pressed) {this.pressed = pressed;}

}
