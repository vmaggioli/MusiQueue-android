package com.example.vince.youtubeplayertest.Activities.starting_activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vince.youtubeplayertest.Activities.BackendTestActivity;
import com.example.vince.youtubeplayertest.Activities.BackgroundWorker;
import com.example.vince.youtubeplayertest.Activities.helper_classes.Hub;
import com.example.vince.youtubeplayertest.Activities.users_only.SearchHub;
import com.example.vince.youtubeplayertest.R;

public class MainActivity extends AppCompatActivity {
    TextView createName;
    EditText usernameText;
    Button usernameButton;
    Button testSearchButton;
    Button testDatabaseButton;
    Button backendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set textView and editText
        createName = (TextView) findViewById(R.id.create_name_text_view);
        usernameText = (EditText) findViewById(R.id.username_entry);
        backendButton = (Button) findViewById(R.id.backend_button);

        // get global application for global variables
        final Hub appState = ((Hub)getApplicationContext());

        // set username button
        usernameButton = (Button) findViewById(R.id.submit_username);
        usernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // PROMPT USER IF S/HE HAS NOT INPUT A USERNAME
                if (usernameText.getText().length() == 0) {
                    Toast.makeText(appState, "Must Have Username", Toast.LENGTH_LONG).show();
                    return;
                }

                // sets username to global app
                appState.setUsername(usernameText.getText().toString());

                // CREATE BACKGROUND WORKER TO ADD USER TO THE DATABASE
                // FIRST PARAMATER TELLS THE BACKGROUND WORKER WHICH TASK TO EXECUTE
                // REMAINING PARAMETERS MUST EACH BE OF THE SAME TYPE
                BackgroundWorker backgroundWorker = new BackgroundWorker(getApplicationContext());
                backgroundWorker.execute("addUser", usernameText.getText().toString());

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

    // FUNCTION CALLED WHEN THE TEST BACKEND BUTTON IS PRESSED
    public void toBack(View view) {
        startActivity(new Intent(this, BackendTestActivity.class));
    }

}