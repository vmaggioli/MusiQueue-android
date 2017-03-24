package com.example.vince.youtubeplayertest.Activities.users_only;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vince.youtubeplayertest.R;

public class JoinHub extends AppCompatActivity {
    EditText passPinView;
    TextView hubNameView;
    Button join;
    String hubPin;
    String hubName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_hub);

        // set views and button
        passPinView = (EditText) findViewById(R.id.pass_pin);
        hubNameView = (TextView) findViewById(R.id.create_a_hub_text);
        join = (Button) findViewById(R.id.join_hub_button);

        Intent i = getIntent();
        hubName = i.getStringExtra("hubName");
        hubNameView.setText(hubName);
    }

    public void joinHub(View view) {
        hubPin = passPinView.getText().toString();
        if (hubPin.length() != 4) {
            Toast.makeText(JoinHub.this, "Pass Pin Must Be 4 Numbers", Toast.LENGTH_SHORT).show();
            return;
        }
        // TODO: Make check for incorrect pin number for joining
        final Intent i = new Intent(JoinHub.this, ConnectToHubActivity.class);
        i.putExtra("hubName", hubName);
        i.putExtra("hubPin", hubPin);
        startActivity(i);
    }
}
