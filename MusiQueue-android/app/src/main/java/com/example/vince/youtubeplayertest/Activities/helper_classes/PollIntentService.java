package com.example.vince.youtubeplayertest.Activities.helper_classes;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.example.vince.youtubeplayertest.Activities.BackgroundWorker;

/**
 * Created by Vince on 3/25/2017.
 */

public class PollIntentService extends IntentService {
    HubSingleton appState;

    public PollIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // TODO: POLL DATABASE
        BackgroundWorker listBW = new BackgroundWorker(this.getApplicationContext());
        appState = HubSingleton.getInstance();
        listBW.execute("songList", appState.getHubId().toString(), appState.getUserID());
    }
}
