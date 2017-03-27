package com.example.vince.youtubeplayertest.Activities.users_only;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.vince.youtubeplayertest.Activities.BackgroundWorker;
import com.example.vince.youtubeplayertest.Activities.helper_classes.HubSingleton;
import com.example.vince.youtubeplayertest.Activities.helper_classes.JoinHubResponse;
import com.example.vince.youtubeplayertest.R;
import com.google.gson.Gson;

public class ConnectToHubActivity extends AppCompatActivity {
    String hubName;
    String hubPin;
    String phoneId;
    String username;
    HubSingleton appState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_to_hub);

        appState = HubSingleton.getInstance();

        Intent i = getIntent();

        hubName = i.getStringExtra("hubName");
        hubPin = i.getStringExtra("hubPin");
        phoneId = appState.getUserID();
        username = appState.getUsername();

        connect();
    }

    private void connect() {
        BackgroundWorker backgroundWorker = new BackgroundWorker(new BackgroundWorker.AsyncResponse() {
            @Override
            public void processFinish(String result) {
                Gson gson = new Gson();
                JoinHubResponse r = gson.fromJson(result, JoinHubResponse.class);
                if(r.error) {
                    connectError(r.errorCode, r.errorMessage);
                }else{
                    connectSuccess(r.getHubId());
                }
            }
        });
        backgroundWorker.execute("joinHub", hubName, hubPin, phoneId, username);
    }

    private void connectSuccess(Integer hubId) {
        Log.d("", "Joined hub successfully!");
        appState.setHubName(hubName);
        appState.setHubId(hubId);

        final Intent i = new Intent(ConnectToHubActivity.this, ViewQueueActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    private void connectError(String errorCode, String errorMessage) {
        // TODO?
        Toast.makeText(this, errorCode + " - " + errorMessage, Toast.LENGTH_LONG).show();
        finish();
    }

}
