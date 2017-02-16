package com.example.vince.youtubeplayertest.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.vince.youtubeplayertest.R;

public class MainActivity extends AppCompatActivity {
    Button backendButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        backendButton = (Button) findViewById(R.id.backend_button);
        Button button = (Button) findViewById(R.id.create_queue_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, QueueActivity.class));
            }
        });
    }

    public void toBack(View view) {
        startActivity(new Intent(this, BackendTestActivity.class));
    }
}