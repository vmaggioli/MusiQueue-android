package com.example.vince.youtubeplayertest.Activities.hub_admin_only;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.vince.youtubeplayertest.Activities.helper_classes.Hub;
import com.example.vince.youtubeplayertest.R;

public class CreateHub extends AppCompatActivity {
    EditText hubNameText;
    EditText passPin;
    Button createHubButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_hub);

        // set editTexts
        hubNameText = (EditText) findViewById(R.id.hub_name);
        passPin = (EditText) findViewById(R.id.pass_pin);

        // find current app state for super globals
        final Hub appState = ((Hub)getApplicationContext());

        // set button
        createHubButton = (Button) findViewById(R.id.create_hub_button);
        createHubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appState.setHubName(hubNameText.getText().toString());
                startActivity(new Intent(CreateHub.this, QueueActivity.class));
            }
        });
    }
}
