package com.example.musiqueue.Activities.starting_activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.musiqueue.Activities.hub_admin_only.CreateHub;
import com.example.musiqueue.Activities.users_only.SearchHub;
import com.example.musiqueue.HelperClasses.HubSingleton;
import com.example.musiqueue.R;

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
                startActivity(new Intent(GettingStarted.this, CreateHub.class));
            }
        });

        joinHub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SearchHub.class));
            }
        });

        final HubSingleton appState = HubSingleton.getInstance();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(GettingStarted.this, MainActivity.class));
    }
}
