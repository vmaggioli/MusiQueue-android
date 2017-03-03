package com.example.vince.youtubeplayertest.Activities.hub_admin_only;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.vince.youtubeplayertest.Activities.BackgroundWorker;
import com.example.vince.youtubeplayertest.Activities.SearchActivity;
import com.example.vince.youtubeplayertest.Activities.users_only.QueueSong;
import com.example.vince.youtubeplayertest.R;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;

public class QueueActivity extends AppCompatActivity {
    final public String API_KEY = "AIzaSyDtCJTBSLt9M1Xi_EBr49Uk4W8q4HhFHPU";
    private YouTubePlayer mYouTubePlayer;
    LinkedList<String> queue = new LinkedList<>();
    ArrayList<QueueSong> list;
    BackgroundWorker bw;
    String id;
    String title;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);

        final EditText url_text = (EditText) findViewById(R.id.url);
        Button url_button = (Button) findViewById(R.id.url_button);




        //need data to run this to test
        bw = new BackgroundWorker(new BackgroundWorker.AsyncResponse() {
            @Override
            public void processFinish(String result) {
                try {
                    list = new ArrayList<>();
                    JSONObject json = new JSONObject(result);
                    Log.d("foobar", json.toString());
                    JSONArray jsonArray = json.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        QueueSong item = new QueueSong();

                        JSONObject jObj = jsonArray.getJSONObject(i);

                        item.setTitle(jObj.getString("song_title"));
                        item.setUpVotes(jObj.getInt("up_votes"));
                        item.setDownVotes(jObj.getInt("down_votes"));
                        item.setId(jObj.getString("song_id"));
                        list.add(item);
                        Log.d("list", list.get(0).getTitle());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Intent intent = getIntent();
        if(intent != null) {
            title = intent.getStringExtra("title");
            id = intent.getStringExtra("id");
            QueueSong song = new QueueSong();
            song.setId(id);
            song.setTitle(title);
            list.add(song);
            bw.execute("addSong", "46","testPhone2_1487947707409", id, title);
            //TODO USE REAL DATA

        }
        bw.execute("songList", "46","testPhone2_1487947707409");
        //TODO USE REAL DATA AND REFRESH QUEUE IN SEPERATE FUNCTION CONSTANTLY


        // initialize YouTube player
        YouTubePlayerFragment mYouTubePlayerFragment = (YouTubePlayerFragment)
                getFragmentManager().findFragmentById(R.id.youtube_player);
        mYouTubePlayerFragment.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                mYouTubePlayer = youTubePlayer;
                mYouTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                    @Override
                    public void onLoading() {

                    }

                    @Override
                    public void onLoaded(String s) {

                    }

                    @Override
                    public void onAdStarted() {

                    }

                    @Override
                    public void onVideoStarted() {

                    }

                    @Override
                    public void onVideoEnded() {
                        list.remove(0);
                        if (list.size() != 0)

                            mYouTubePlayer.loadVideo(list.get(0).getId());
                    }

                    @Override
                    public void onError(YouTubePlayer.ErrorReason errorReason) {

                    }
                });
            }
        });

        url_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // retrieve video_id from url
                String url = url_text.getText().toString();
                String video_id = "";
                int n = url.length();
                for (int i = 0; i < n; i++) {
                    if (url.substring(i, i + 1).equals("=")) {
                        video_id = url.substring(i + 1, n);
                        break;
                    }
                }

                // add new data to queue
                if (list.size() == 0)
                    mYouTubePlayer.loadVideo(video_id);
                QueueSong song = new QueueSong();
                song.setId(video_id);
                queue.addLast(video_id);
                url_text.getText().clear();
            }
        });
    }

    public void searchVideo(View view) {
        startActivity(new Intent(QueueActivity.this, SearchActivity.class));
    }
}
