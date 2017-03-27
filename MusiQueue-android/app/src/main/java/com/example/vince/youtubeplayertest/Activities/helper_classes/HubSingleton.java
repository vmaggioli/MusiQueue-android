package com.example.vince.youtubeplayertest.Activities.helper_classes;

import android.util.Log;

import com.example.vince.youtubeplayertest.Activities.BackgroundWorker;
import com.example.vince.youtubeplayertest.Activities.users_only.QueueSong;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

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

    public void add(QueueSong song, String hubId, String userId) throws ExecutionException, InterruptedException {
        BackgroundWorker addBW;
        BackgroundWorker.AsyncResponse callback;
        callback = new BackgroundWorker.AsyncResponse() {

            @Override
            public void processFinish(String result) {
                try {
                    songsList = new ArrayList<>();
                    JSONObject json = new JSONObject(result);
                    Log.d("foobar", json.toString());
                    JSONArray jsonArray = json.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        QueueSong item = new QueueSong();

                        JSONObject jObj = jsonArray.getJSONObject(i);

                        item.setTitle(jObj.getString("song_title"));
                        item.setUpVotes(jObj.getInt("up_votes"));
                        item.setDownVotes(jObj.getInt("down_votes"));
                        item.setId(jObj.getString("song_id"));
                        songsList.add(item);
                        Log.d("list in bw", songsList.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        };
        addBW = new BackgroundWorker(callback);
        songsList.add(song);
        addBW.execute("addSong", hubId, userId, song.getId(), song.getTitle()).get();
        //songsList.add(song);
    }

    public QueueSong getSongAt(int pos) { return songsList.get(pos); }

    private String username;
    private String hubName;
    private String passPin;
    private String userId;
    private Integer hubId;
    ArrayList<QueueSong> songsList;
}
