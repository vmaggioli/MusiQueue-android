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
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerView;

public class MainActivity extends AppCompatActivity {
    private final String API_KEY = "AIzaSyDtCJTBSLt9M1Xi_EBr49Uk4W8q4HhFHPU";
    YouTubePlayerFragment myYouTubePlayerFragment;
    YouTubePlayer mYouTubePlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize YouTube player
        myYouTubePlayerFragment = (YouTubePlayerFragment)
                getFragmentManager().findFragmentById(R.id.youtube_player);
        myYouTubePlayerFragment.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                mYouTubePlayer = youTubePlayer;
            }
        });

        // initialize other fields
        final EditText url_text = (EditText) findViewById(R.id.url);
        Button url_button = (Button) findViewById(R.id.url_button);
        url_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = url_text.getText().toString();
                String video_id = "";
                int n = url.length();
                for (int i = 0; i < n; i++) {
                    if (url.substring(i,i+1).equals("=")) {
                        video_id = url.substring(i+1,n);
                        break;
                    }
                }
                mYouTubePlayer.loadVideo(video_id);
            }
        });
    }
}