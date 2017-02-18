package com.example.vince.youtubeplayertest.Activities.starting_activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.vince.youtubeplayertest.Activities.helper_classes.Hub;
import com.example.vince.youtubeplayertest.R;

public class GettingStarted extends AppCompatActivity {
    Button createHub;
    Button joinHub;
    TextView gettingStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getting_started);

        // set up textView
        gettingStarted = (TextView) findViewById(R.id.getting_started_text);

        // set up Buttons
        createHub = (Button) findViewById(R.id.create_hub_button);
        joinHub = (Button) findViewById(R.id.join_hub_button);
        createHub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: send new Intent to creating a hub
            }
        });

        joinHub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: send new Intent to creating a hub
            }
        });

        final Hub appState = ((Hub)getApplicationContext());


    }
}