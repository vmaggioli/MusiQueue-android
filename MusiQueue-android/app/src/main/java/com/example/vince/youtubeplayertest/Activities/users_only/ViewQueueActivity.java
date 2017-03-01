package com.example.vince.youtubeplayertest.Activities.users_only;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.vince.youtubeplayertest.Activities.helper_classes.Hub;
import com.example.vince.youtubeplayertest.R;

public class ViewQueueActivity extends AppCompatActivity {
    TextView hubNameView;

    Hub appState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_queue);

        appState = ((Hub)getApplicationContext());

        hubNameView = (TextView) findViewById(R.id.hub_name);
        hubNameView.setText(appState.getHubName());

        // TODO: show the queue or whatever
    }
}
