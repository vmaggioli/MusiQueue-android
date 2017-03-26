package com.example.vince.youtubeplayertest.Activities.helper_classes;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.api.client.util.NullValue;
import com.google.gson.Gson;

import java.util.Iterator;
import java.util.Vector;

public class RecentHubs {

    public static Vector<Hub> recentHubs = new Vector<>();

    public void addHub(Hub hub) {
        if (!recentHubs.contains(hub)) recentHubs.add(0, hub);
        else {
            recentHubs.remove(hub);
            recentHubs.add(0, hub);
        }
    }

    public Vector<Hub> getRecentHubs(Context context) {
        loadRecentHubs(context);
        return recentHubs;
    }

    public boolean saveRecentHubs(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("recentHubs", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("recentHubs_size", recentHubs.size());
        Iterator<Hub> itr = recentHubs.iterator();
        Gson gson = new Gson();
        int i = 0;
        String json = "";
        while (itr.hasNext())
            json = gson.toJson(itr.next());
            editor.putString("recentHubs_" + i++, json);
        return editor.commit();
    }

    public void loadRecentHubs(Context context) {
        recentHubs.clear();
        SharedPreferences prefs = context.getSharedPreferences("recentHubs", 0);
        int size = prefs.getInt("recentHubs_size", 0);
        Gson gson = new Gson();
        String json = "";
        for (int i = 0; i < size; i++) {
            json = prefs.getString("recentHubs_" + i, null);
            Hub hub = gson.fromJson(json, Hub.class);
            if (hub.isRecent()) recentHubs.add(hub);
        }
    }
}
