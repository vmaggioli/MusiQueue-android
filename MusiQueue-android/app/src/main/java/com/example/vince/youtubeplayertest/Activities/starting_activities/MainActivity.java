package com.example.vince.youtubeplayertest.Activities.starting_activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vince.youtubeplayertest.Activities.helper_classes.Hub;
import com.example.vince.youtubeplayertest.Activities.users_only.SearchHub;
import com.example.vince.youtubeplayertest.R;

public class MainActivity extends AppCompatActivity {
    TextView createName;
    EditText usernameText;
    Button usernameButton;
    Button testSearchButton;
    Button testDatabaseButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set textView and editText
        createName = (TextView) findViewById(R.id.create_name_text_view);
        usernameText = (EditText) findViewById(R.id.username_entry);

        // get global application for global variables
        final Hub appState = ((Hub)getApplicationContext());

        // set username button
        usernameButton = (Button) findViewById(R.id.submit_username);
        usernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // sets username to global app
                appState.setUsername(usernameText.getText().toString());
                startActivity(new Intent(MainActivity.this, GettingStarted.class));
            }
        });

        // TODO: remove test buttons below for final product
        testSearchButton = (Button) findViewById(R.id.test_button_search_hubs);
        testSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: change to handle data from AWS
                startActivity(new Intent(MainActivity.this, SearchHub.class));
            }
        });

        testDatabaseButton = (Button) findViewById(R.id.test_button_database);
        testDatabaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(appState, "Not setup yet", Toast.LENGTH_SHORT).show();
            }
        });
    }
}