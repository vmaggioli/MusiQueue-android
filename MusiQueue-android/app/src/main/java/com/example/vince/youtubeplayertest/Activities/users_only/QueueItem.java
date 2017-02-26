package com.example.vince.youtubeplayertest.Activities.users_only;

/**
 * Created by Sam on 2/26/2017.
 */

public class QueueItem {
    private String title;
    private int upVotes;
    private int downVotes;

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
}
