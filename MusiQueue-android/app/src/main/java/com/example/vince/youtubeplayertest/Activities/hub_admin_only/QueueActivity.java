package com.example.vince.youtubeplayertest.Activities.hub_admin_only;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.vince.youtubeplayertest.Activities.BackgroundWorker;
import com.example.vince.youtubeplayertest.Activities.PollData;
import com.example.vince.youtubeplayertest.Activities.SearchActivity;
import com.example.vince.youtubeplayertest.Activities.UpdateResultReceiver;
import com.example.vince.youtubeplayertest.Activities.VideoItemAdapter;
import com.example.vince.youtubeplayertest.Activities.helper_classes.HubSingleton;
import com.example.vince.youtubeplayertest.Activities.users_only.QueueSong;
import com.example.vince.youtubeplayertest.R;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

public class QueueActivity extends AppCompatActivity implements UpdateResultReceiver.Receiver {
    final public String API_KEY = "AIzaSyDtCJTBSLt9M1Xi_EBr49Uk4W8q4HhFHPU";
    private YouTubePlayer mYouTubePlayer;
    LinkedList<String> queue = new LinkedList<>();
    String flag = "Owner";

    BackgroundWorker.AsyncResponse callback;
    BackgroundWorker addBW;
    BackgroundWorker listBW;
    String id;
    String title;
    RecyclerView songListView;
    HubSingleton hubSingleton;
    VideoItemAdapter adapter;
    UpdateResultReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);

        final EditText url_text = (EditText) findViewById(R.id.url);
        Button url_button = (Button) findViewById(R.id.url_button);
      
        hubSingleton = HubSingleton.getInstance();

        adapter = new VideoItemAdapter(QueueActivity.this, hubSingleton.getEntireList(), new VideoItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(QueueSong videoItem) {

            }
        });
        Log.d("list",hubSingleton.getEntireList().toString());
        songListView = (RecyclerView) findViewById(R.id.songList);
        songListView.setAdapter(adapter);
        songListView.setLayoutManager(new LinearLayoutManager(this));
        songListView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).color(Color.LTGRAY).sizeResId(R.dimen.divider).marginResId(R.dimen.margin5dp, R.dimen.margin5dp).build());


        callback = new BackgroundWorker.AsyncResponse() {
            @Override
            public void processFinish(String result) {
                try {
                    hubSingleton.clearList();
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
                        item.setUser(jObj.getString("user_name"));
                        hubSingleton.add(item);
                        Log.d("list", hubSingleton.getSongAt(0).getTitle());
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        addBW = new BackgroundWorker(callback);
        listBW = new BackgroundWorker(callback);

        Intent intent = getIntent();
        if(intent.hasExtra("title")) {
            title = intent.getStringExtra("title");
            id = intent.getStringExtra("id");
            QueueSong song = new QueueSong();
            song.setId(id);
            song.setTitle(title);

            addBW.execute("addSong", hubSingleton.getHubId().toString(), hubSingleton.getUserID(), id, title);
        }

        listBW.execute("hubSongList", hubSingleton.getHubId().toString(), hubSingleton.getUserID());

        updateView();

        // initialize YouTube player
        YouTubePlayerFragment mYouTubePlayerFragment = (YouTubePlayerFragment)
                getFragmentManager().findFragmentById(R.id.youtube_player);
        mYouTubePlayerFragment.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                if(!b){
                    youTubePlayer.cueVideo(getIntent().getStringExtra("id"));
                }
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
                        hubSingleton.removeAt(0);
                        if (hubSingleton.getEntireList().size() != 0)
                            mYouTubePlayer.loadVideo(hubSingleton.getSongAt(0).getId());
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
                if (hubSingleton.getEntireList().size() == 0)
                    mYouTubePlayer.loadVideo(video_id);
                QueueSong song = new QueueSong();
                song.setId(video_id);
                queue.addLast(video_id);
                url_text.getText().clear();
            }
        });
    }

    public void searchVideo(View view) {
        Intent intent = new Intent(QueueActivity.this, SearchActivity.class);
        intent.putExtra("view_queue",flag);
        startActivity(intent);
    }

    public void updateView() {
        System.out.println("Update my view please");
        receiver = new UpdateResultReceiver(new Handler());
        receiver.setReceiver(this);

        Intent intent = new Intent(this, PollData.class);
        intent.putExtra("receiver", receiver);
        startService(intent);

    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if (resultCode != 0) return;
        String result = resultData.getString("result");
        try {
            hubSingleton.clearList();
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
                item.setUser(jObj.getString("user_name"));
                hubSingleton.add(item);
                Log.d("list in bw", hubSingleton.toString());
            }
            adapter.notifyDataSetChanged();
            updateView();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
