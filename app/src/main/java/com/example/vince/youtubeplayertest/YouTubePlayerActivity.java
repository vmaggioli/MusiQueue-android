package com.example.vince.youtubeplayertest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

public class YouTubePlayerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    private final String API_KEY = "AIzaSyDtCJTBSLt9M1Xi_EBr49Uk4W8q4HhFHPU";
    YouTubePlayerFragment myYouTubePlayerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you_tube_player);

        myYouTubePlayerFragment = (YouTubePlayerFragment)
                getFragmentManager().findFragmentById(R.id.youtube_player);
        myYouTubePlayerFragment.initialize(API_KEY, this);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if (!b) {
            Bundle extras = getIntent().getExtras();
            String video_id = extras.getString("video_id");
            youTubePlayer.loadVideo(video_id);
        }
    }
}
