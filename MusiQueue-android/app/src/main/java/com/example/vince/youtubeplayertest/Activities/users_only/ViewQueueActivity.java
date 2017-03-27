package com.example.vince.youtubeplayertest.Activities.users_only;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.vince.youtubeplayertest.Activities.BackgroundWorker;
import com.example.vince.youtubeplayertest.Activities.SearchActivity;
import com.example.vince.youtubeplayertest.Activities.VideoItemAdapter;
import com.example.vince.youtubeplayertest.Activities.helper_classes.Hub;
import com.example.vince.youtubeplayertest.Activities.helper_classes.HubSingleton;
import com.example.vince.youtubeplayertest.Activities.hub_admin_only.QueueActivity;
import com.example.vince.youtubeplayertest.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static android.R.id.list;

public class ViewQueueActivity extends AppCompatActivity {
    TextView hubNameView;
    String flag = "User";
    Hub appState;
    RecyclerView songListView;
    BackgroundWorker.AsyncResponse callback;

    String title;
    String id;
    ArrayList<QueueSong> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_queue);

        appState = ((Hub) getApplicationContext());

        hubNameView = (TextView) findViewById(R.id.hub_name);
        hubNameView.setText(appState.getHubName());


        // TODO: show the queue or whatever
        HubSingleton hubSingleton = HubSingleton.getInstance();                         // SINGLETON HERE

        Intent intent = getIntent();
        if(intent.hasExtra("title")) {
            title = intent.getStringExtra("title");
            id = intent.getStringExtra("id");
            QueueSong song = new QueueSong();
            song.setId(id);
            song.setTitle(title);

            try {
                hubSingleton.add(song, appState.getHubId().toString(), appState.getUserID());
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //list.add(song);
            //hubSingleton.add(song);                                                 // SINGLETON HERE

            //addBW.execute("addSong", appState.getHubId().toString(), appState.getUserID(), id, title);
        }

        VideoItemAdapter adapter = new VideoItemAdapter(ViewQueueActivity.this, hubSingleton.getEntireList(), new VideoItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(QueueSong videoItem) {

            }
        });
        songListView = (RecyclerView) findViewById(R.id.song_List);
        songListView.setAdapter(adapter);
        songListView.setLayoutManager(new LinearLayoutManager(this));

        QueueSong song = new QueueSong();
        song.setTitle("hello");
        //hubSingleton.add(song);

        //implement a search button
    }

    public void searchVideo(View view) {
        Intent intent = new Intent(ViewQueueActivity.this, SearchActivity.class);
        intent.putExtra("view_queue", flag);

        startActivity(intent);
    }

}
