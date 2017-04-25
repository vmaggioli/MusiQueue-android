package com.example.vince.youtubeplayertest.Activities.users_only;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.vince.youtubeplayertest.Activities.BackgroundWorker;
import com.example.vince.youtubeplayertest.Activities.PollData;
import com.example.vince.youtubeplayertest.Activities.SearchActivity;
import com.example.vince.youtubeplayertest.Activities.UpdateResultReceiver;
import com.example.vince.youtubeplayertest.Activities.VideoItemAdapter;
import com.example.vince.youtubeplayertest.Activities.helper_classes.HubSingleton;
import com.example.vince.youtubeplayertest.Activities.hub_admin_only.User;
import com.example.vince.youtubeplayertest.R;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewQueueActivity extends AppCompatActivity implements UpdateResultReceiver.Receiver {
    TextView hubNameView;

    String flag = "User";
    HubSingleton hubSingleton;
    RecyclerView songListView;
    BackgroundWorker addBW;
    BackgroundWorker listBW;
    BackgroundWorker.AsyncResponse callback;
    VideoItemAdapter adapter;
    UpdateResultReceiver receiver;


    String title;
    String id;
    @Override
    public void onBackPressed() {
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_queue);

        hubSingleton = HubSingleton.getInstance();

        hubNameView = (TextView) findViewById(R.id.hub_name);
        hubNameView.setText(hubSingleton.getHubName());

        adapter = new VideoItemAdapter(ViewQueueActivity.this, hubSingleton.getEntireList(), new VideoItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(QueueSong videoItem) {

            }
        });
        songListView = (RecyclerView) findViewById(R.id.song_List);
        songListView.setAdapter(adapter);
        songListView.setLayoutManager(new LinearLayoutManager(this));

        songListView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).color(Color.LTGRAY).sizeResId(R.dimen.divider).marginResId(R.dimen.margin5dp, R.dimen.margin5dp).build());

        callback = new BackgroundWorker.AsyncResponse() {

            @Override
            public void processFinish(String result) {
                try {
                    hubSingleton.clearList();
                    JSONObject json = new JSONObject(result);
                    JSONObject jsonArray = json.getJSONObject("result");
                    JSONArray songs = jsonArray.getJSONArray("songs");
                    JSONArray users = jsonArray.getJSONArray("users");
                    for (int i = 0; i < songs.length(); i++) {
                        QueueSong item = new QueueSong();
                        JSONObject jObj = songs.getJSONObject(i);

                        item.setTitle(jObj.getString("song_title"));
                        item.setUpVotes(jObj.getInt("up_votes"));
                        item.setDownVotes(jObj.getInt("down_votes"));
                        item.setId(jObj.getString("song_id"));
                        item.setUser(jObj.getString("user_name"));
                        item.setPlace(jObj.getInt("id"));
                        item.setState(jObj.getInt("voted"));

                        hubSingleton.add(item);
                    }
                    hubSingleton.clearUsers();
                    User user;
                    for(int i = 0; i < users.length();i++){
                        user = new User();
                        JSONObject name = users.getJSONObject(i);
                        user.setName(name.getString("name"));
                        user.setId(name.getString("id"));
                        hubSingleton.addUser(user);

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

            addBW.execute("addSong", hubSingleton.getHubId().toString(), hubSingleton.getUserID(), id, title);
        }
        listBW.execute("hubSongList", hubSingleton.getHubId().toString(), hubSingleton.getUserID());

        updateView();
    }

    public void searchVideo(View view) {
        Intent intent = new Intent(ViewQueueActivity.this, SearchActivity.class);
        intent.putExtra("view_queue", flag);

        startActivity(intent);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if (resultCode != 0) return;
        String result = resultData.getString("result");
        try {
            hubSingleton.clearList();
            JSONObject json = new JSONObject(result);
            JSONObject jsonArray = json.getJSONObject("result");
            JSONArray songs = jsonArray.getJSONArray("songs");
            JSONArray users = jsonArray.getJSONArray("users");
            for (int i = 0; i < songs.length(); i++) {
                QueueSong item = new QueueSong();
                JSONObject jObj = songs.getJSONObject(i);

                item.setTitle(jObj.getString("song_title"));
                item.setUpVotes(jObj.getInt("up_votes"));
                item.setDownVotes(jObj.getInt("down_votes"));
                item.setId(jObj.getString("song_id"));
                item.setUser(jObj.getString("user_name"));
                item.setPlace(jObj.getInt("id"));
                item.setState(jObj.getInt("voted"));

                hubSingleton.add(item);
            }
            hubSingleton.clearUsers();

            User user;
            for(int i = 0; i < users.length();i++){
                user = new User();
                JSONObject name = users.getJSONObject(i);
                user.setName(name.getString("name"));
                user.setId(name.getString("id"));
                hubSingleton.addUser(user);

            }
            adapter.notifyDataSetChanged();
            updateView();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateView() {
        System.out.println("Update my view please");
        receiver = new UpdateResultReceiver(new Handler());
        receiver.setReceiver(this);

        Intent intent = new Intent(this, PollData.class);
        intent.putExtra("receiver", receiver);
        startService(intent);
    }
}
