package com.example.vince.youtubeplayertest.Activities.users_only;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.vince.youtubeplayertest.R;

public class JoinHub extends AppCompatActivity {
    EditText hubCoordView;
    EditText passPinView;
    Button search;
    String hubCoord;
    String passPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_hub);

        // set views and button
        hubCoordView = (EditText) findViewById(R.id.hub_coord);
        passPinView = (EditText) findViewById(R.id.pass_pin);
        search = (Button) findViewById(R.id.join_hub_button);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hubCoord = hubCoordView.getText().toString();
                passPin = passPinView.getText().toString();

                // TODO: Send information when server/database are ready
            }
        });

    }
}
