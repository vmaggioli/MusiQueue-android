package com.example.vince.youtubeplayertest.Activities.users_only;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.vince.youtubeplayertest.Activities.VideoItemAdapter;
import com.example.vince.youtubeplayertest.Activities.helper_classes.Hub;
import com.example.vince.youtubeplayertest.Activities.helper_classes.HubSingleton;
import com.example.vince.youtubeplayertest.Activities.hub_admin_only.QueueActivity;
import com.example.vince.youtubeplayertest.R;

public class ViewQueueActivity extends AppCompatActivity {
    TextView hubNameView;

    Hub appState;
    RecyclerView songListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_queue);

        appState = ((Hub)getApplicationContext());

        hubNameView = (TextView) findViewById(R.id.hub_name);
        hubNameView.setText(appState.getHubName());

        // TODO: show the queue or whatever
        HubSingleton hubSingleton = HubSingleton.getInstance();                         // SINGLETON HERE

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
        hubSingleton.add(song);

        //implement a search button
    }
}
