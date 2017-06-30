package com.example.musiqueue.Activities.users_only;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.musiqueue.Activities.SearchActivity;
import com.example.musiqueue.HelperClasses.HubSingleton;
import com.example.musiqueue.HelperClasses.QueueSong;
import com.example.musiqueue.HelperClasses.UpdateResultReceiver;
import com.example.musiqueue.HelperClasses.User;
import com.example.musiqueue.HelperClasses.adapters.VideoItemAdapter;
import com.example.musiqueue.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ViewQueueActivity extends AppCompatActivity implements UpdateResultReceiver.Receiver {
    TextView hubNameView;

    String flag = "User";
    HubSingleton hubSingleton;
    RecyclerView songListView;
    VideoItemAdapter adapter;
    UpdateResultReceiver receiver;

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

        adapter = new VideoItemAdapter(ViewQueueActivity.this, hubSingleton.getEntireList(),"notowner", new VideoItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(QueueSong videoItem) {

            }
        });
        songListView = (RecyclerView) findViewById(R.id.song_List);
        songListView.setAdapter(adapter);
        songListView.setLayoutManager(new LinearLayoutManager(this));

        songListView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).color(Color.LTGRAY).sizeResId(R.dimen.divider).marginResId(R.dimen.margin5dp, R.dimen.margin5dp).build());

        initFirebase();
    }

    private void initFirebase() {
        // Add user to users list for this hub
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("/User Lists/"
            + hubSingleton.getHubName() + "/" + hubSingleton.getUserID());
        ref.child("User name").setValue(hubSingleton.getUsername());
        // TODO: add more values
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
}
