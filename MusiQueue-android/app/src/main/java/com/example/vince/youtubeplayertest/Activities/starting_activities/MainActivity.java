package com.example.vince.youtubeplayertest.Activities.starting_activities;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import com.example.vince.youtubeplayertest.Activities.BackendTestActivity;
import com.example.vince.youtubeplayertest.Activities.helper_classes.HubSingleton;
import com.example.vince.youtubeplayertest.Activities.users_only.SearchHub;
import com.example.vince.youtubeplayertest.R;

public class MainActivity extends AppCompatActivity {
    TextView createName;
    EditText usernameText;
    Button usernameButton;
    Button testSearchButton;
    Button testDatabaseButton;
    Button backendButton;
    HubSingleton appState;

    /*
        UPON ENTERING THIS ACTIVITY, WE NEED TO CHECK IF THE USER'S
        DEVICE ID IS ALREADY PRESENT WITHIN OUR DATABASE RECORDS. IF
        IT IS THEN WE NEED TO TELL THE BACKEND TO UPDATE THE USERNAME
        INSTEAD OF CREATING A NEW USERNAME WITH THAT SAME ID.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get global application for global variables
        appState = HubSingleton.getInstance();
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        appState.setUserID(android_id);

        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        String username = settings.getString("username", "");

        if (username.equals("")) {
            initView();
        }else{
            // skip this view
            appState.setUsername(username);
            startActivity(new Intent(MainActivity.this, GettingStarted.class));
            finish();
        }
    }

    protected void initView() {

        // set textView and editText
        createName = (TextView) findViewById(R.id.create_name_text_view);
        usernameText = (EditText) findViewById(R.id.username_entry);
        backendButton = (Button) findViewById(R.id.backend_button);

        // set username button
        usernameButton = (Button) findViewById(R.id.submit_username);
        usernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // PROMPT USER IF S/HE HAS NOT INPUT A USERNAME
                if (usernameText.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Must Have Username", Toast.LENGTH_LONG).show();
                    return;
                }

                // sets username to global app
                appState.setUsername(usernameText.getText().toString());

                SharedPreferences settings = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("username", appState.getUsername());
                editor.commit();

                startActivity(new Intent(MainActivity.this, GettingStarted.class));
                finish();
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
                Toast.makeText(getApplicationContext(), "Not setup yet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // FUNCTION CALLED WHEN THE TEST BACKEND BUTTON IS PRESSED
    public void toBack(View view) {
        startActivity(new Intent(this, BackendTestActivity.class));
    }

}