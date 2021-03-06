package com.example.musiqueue.Activities.starting_activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musiqueue.HelperClasses.HubSingleton;
import com.example.musiqueue.R;

public class MainActivity extends AppCompatActivity {
    TextView createName;
    EditText usernameText;
    Button usernameButton;
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
            usernameText = (EditText) findViewById(R.id.username_entry);
        }else{
            initView();
            appState.setUsername(username);
            usernameText = (EditText) findViewById(R.id.username_entry);
            usernameText.setText(username);
        }
    }

    protected void initView() {

        // set textView and editText
        createName = (TextView) findViewById(R.id.create_name_text_view);

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
                editor.apply();

                startActivity(new Intent(MainActivity.this, GettingStarted.class));
                finish();
            }
        });
    }
}