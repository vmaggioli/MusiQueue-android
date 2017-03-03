package com.example.vince.youtubeplayertest.Activities.hub_admin_only;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vince.youtubeplayertest.Activities.BackgroundWorker;
import com.example.vince.youtubeplayertest.Activities.helper_classes.Hub;
import com.example.vince.youtubeplayertest.Activities.helper_classes.JoinHubResponse;
import com.example.vince.youtubeplayertest.R;
import com.google.gson.Gson;

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
        final Hub appState = ((Hub) getApplicationContext());

        // set button
        createHubButton = (Button) findViewById(R.id.create_hub_button);
        createHubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hubNameText.getText().length() == 0) {
                    Toast.makeText(appState, "Must Have Hub Name", Toast.LENGTH_LONG).show();
                    return;
                } else if (passPin.getText().length() != 4) {
                    Toast.makeText(appState, "Pass Pin Must Be 4 Digits", Toast.LENGTH_LONG).show();
                    return;
                }

                BackgroundWorker backgroundWorker = new BackgroundWorker(new BackgroundWorker.AsyncResponse() {
                    @Override
                    public void processFinish(String result) {
                        Gson gson = new Gson();
                        JoinHubResponse r = gson.fromJson(result, JoinHubResponse.class);
                        if (r.error) {
                            connectError(r.errorCode, r.errorMessage);
                        } else {
                            connectSuccess(r.getHubId());
                        }
                    }
                });
                backgroundWorker.execute("createHub", hubNameText.getText().toString(), passPin.getText().toString(), appState.getUserID(), appState.getUsername());
                appState.setHubName(hubNameText.getText().toString());
            }
        });
    }

    private void connectSuccess(Integer hubId) {
        final Hub appState = ((Hub) getApplicationContext());

        Log.d("", "Created hub successfully!");
        appState.setHubId(hubId);

        final Intent i = new Intent(CreateHub.this, QueueActivity.class);
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
