package com.example.vince.youtubeplayertest;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class MainActivity extends AppCompatActivity {
    private String video_id = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button url_button = (Button) findViewById(R.id.url_button);

        url_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get video_id and send it as extra in new intent
                EditText url_text = (EditText) findViewById(R.id.url);
                String url = url_text.getText().toString();
                if (url.contains("=")) {
                    int n = url.length();
                    for (int i = 0; i < n; i++) {
                        if (url.substring(i,i+1).equals("=")) {
                            video_id = url.substring(i+1,n);
                            break;
                        }
                    }

                Intent intent = new Intent(MainActivity.this, YouTubePlayerActivity.class);
                intent.putExtra("video_id", video_id);
                startActivity(intent);
            }
        });
    }
}