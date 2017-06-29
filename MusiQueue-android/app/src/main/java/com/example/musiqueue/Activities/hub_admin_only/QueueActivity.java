package com.example.musiqueue.Activities.hub_admin_only;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.musiqueue.Activities.users_only.QueueSong;
import com.example.musiqueue.HelperClasses.BackgroundWorker;
import com.example.musiqueue.HelperClasses.HubSingleton;
import com.example.musiqueue.HelperClasses.PollData;
import com.example.musiqueue.HelperClasses.UpdateResultReceiver;
import com.example.musiqueue.HelperClasses.User;
import com.example.musiqueue.HelperClasses.VideoItem;
import com.example.musiqueue.HelperClasses.YoutubeConnector;
import com.example.musiqueue.HelperClasses.adapters.UserItemAdapter;
import com.example.musiqueue.HelperClasses.adapters.VideoItemAdapter;
import com.example.musiqueue.R;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class QueueActivity extends AppCompatActivity {
    final public String API_KEY = "AIzaSyDtCJTBSLt9M1Xi_EBr49Uk4W8q4HhFHPU";
    private YouTubePlayer mYouTubePlayer = null;

    EditText searchEdit;
    Button searchButton;

    RecyclerView songListView;
    RecyclerView userListView;
    HubSingleton hubSingleton;
    VideoItemAdapter adapter;
    UserItemAdapter userAdapter;
    String currentlyPlaying;
    ToggleButton viewButton;
    private List<VideoItem> searchResults;
    private ListView videosFound;
    private Handler handler;
    private ValueEventListener voteListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // TODO
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // TODO
        }
    };


    @Override
    public void onBackPressed() {
        if (songListView != null && videosFound != null && videosFound.getVisibility() == View.VISIBLE) {
            videosFound.setVisibility(View.GONE);
            songListView.setVisibility(View.VISIBLE);
        } else if (songListView != null && userListView != null && userListView.getVisibility() == View.VISIBLE) {
            userListView.setVisibility(View.GONE);
            songListView.setVisibility(View.VISIBLE);
            viewButton.setChecked(false);
        }
    }
    @Override
    public void onDestroy() {
        if (mYouTubePlayer != null) {
            mYouTubePlayer.release();
            mYouTubePlayer = null;
        }
        super.onDestroy();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);
        currentlyPlaying = "";
        searchEdit = (EditText) findViewById(R.id.search_edit);
        searchButton = (Button) findViewById(R.id.search_button);
        videosFound = (ListView)findViewById(R.id.videos_found);
        viewButton = (ToggleButton) findViewById(R.id.q_view);

        viewButton.setText("display USERS");
        viewButton.setTextOff("display USERS");
        viewButton.setTextOn("display QUEUE");

        handler = new Handler();
        addClickListener();
        searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    searchOnYoutube(v.getText().toString());
                    return false;
                }
                return true;
            }
        });

        hubSingleton = HubSingleton.getInstance();


        userAdapter = new UserItemAdapter(QueueActivity.this,hubSingleton.getUsers(),new UserItemAdapter.OnItemClickListener() {
            public void onItemClick(User user) {

            }
        });

        adapter = new VideoItemAdapter(QueueActivity.this, hubSingleton.getEntireList(),"owner", new VideoItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(QueueSong videoItem) {

            }
        });
        viewButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonview, boolean isChecked) {
                if(isChecked) {
                    userListView.setVisibility(View.VISIBLE);
                    if (songListView != null && songListView.getVisibility() == View.VISIBLE) songListView.setVisibility(View.GONE);
                    if (videosFound != null && videosFound.getVisibility() == View.VISIBLE) videosFound.setVisibility(View.GONE);
                }
                else {
                    userListView.setVisibility(View.GONE);
                    songListView.setVisibility(View.VISIBLE);
                }
            }
        }

        );

        songListView = (RecyclerView) findViewById(R.id.songList);
        songListView.setAdapter(adapter);
        songListView.setLayoutManager(new LinearLayoutManager(this));
        songListView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).color(Color.LTGRAY).sizeResId(R.dimen.divider).marginResId(R.dimen.margin5dp, R.dimen.margin5dp).build());

        initPlayer();
        initFirebase();

        userListView = (RecyclerView) findViewById(R.id.userList);
        userListView.setVisibility(View.GONE);
        userListView.setAdapter(userAdapter);
        userListView.setLayoutManager(new LinearLayoutManager(this));
        userListView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).color(Color.LTGRAY).sizeResId(R.dimen.divider).marginResId(R.dimen.margin5dp, R.dimen.margin5dp).build());


    }

    public void initFirebase() {
        // Don't need to create hub, done in CreateHub
        // Add user to user list for this hub
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User Lists")
                .child(hubSingleton.getHubName()).child(hubSingleton.getUserID());
        DatabaseReference userSubRef = userRef.child("User Name");
        userSubRef.setValue(hubSingleton.getUsername());
        userSubRef = userRef.child("ID");
        userSubRef.setValue(hubSingleton.getUserID());
        // TODO add more fields

        // set up listener for user list
        DatabaseReference userListRef = FirebaseDatabase.getInstance().getReference().child("User Lists")
                .child(hubSingleton.getHubName());
        userListRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                updateUserList(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                updateUserList(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                updateUserList(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                updateUserList(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Create song list
        DatabaseReference songListRef = FirebaseDatabase.getInstance().getReference().child("Song Lists")
                .child(hubSingleton.getHubName());
        // This listener is to update the view when a song is added/removed
        songListRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                addToQueue(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                modifyQueue(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                switchQueue(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateUserList(DataSnapshot snapshot) {
        hubSingleton.clearUsers();
        for (DataSnapshot ds : snapshot.getChildren()) {
            hubSingleton.addUser(new User((String) ds.getValue(), (String) ds.child("ID").getValue()));
        }
        userAdapter.notifyDataSetChanged();
    }

    private void addToQueue(DataSnapshot snapshot) {
        String title = "";
        long upVotes = 0;
        long downVotes = 0;
        String id = snapshot.getKey();
        for (DataSnapshot snap : snapshot.getChildren()) {
            if (snap.getKey().equals("Title") && snap.getValue() != null)
                title = snap.getValue().toString();
            else if (snap.getKey().equals("Up-votes") && snap.getValue() != null)
                upVotes = Long.parseLong(snap.getValue().toString());
            else if (snap.getKey().equals("Down-votes") && snap.getValue() != null)
                downVotes = Long.parseLong(snap.getValue().toString());
        }
        hubSingleton.add(new QueueSong(id, title, hubSingleton.getUsername(), upVotes, downVotes));
        adapter.notifyDataSetChanged();
        queueIfNothingPlaying(id);
    }

    private void modifyQueue(DataSnapshot snapshot) {

    }

    private void switchQueue(DataSnapshot snapshot) {

    }

    public void initPlayer() {
        final YouTubePlayerFragment mYouTubePlayerFragment = (YouTubePlayerFragment)
                getFragmentManager().findFragmentById(R.id.youtube_player);
        mYouTubePlayerFragment.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer youTubePlayer, boolean b) {
                if(!b && hubSingleton.getEntireList().size() > 0){
                    youTubePlayer.loadVideo(hubSingleton.getSongAt(0).getId()); //getIntent().getStringExtra("id"));
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
                        if (hubSingleton.getEntireList() != null && hubSingleton.getEntireList().size() != 0) {
                            // Firebase logic
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Song Lists")
                                    .child(hubSingleton.getHubName()).child(currentlyPlaying);
                            ref.removeValue();

                            hubSingleton.removeAt(0);
                            adapter.notifyDataSetChanged();

                            if (hubSingleton.getEntireList().size() != 0 && mYouTubePlayer != null) {
                                mYouTubePlayer.loadVideo(hubSingleton.getSongAt(0).getId());
                                currentlyPlaying = hubSingleton.getSongAt(0).getId();
                            }
                            else if (hubSingleton.getEntireList().size() != 0 && mYouTubePlayer == null) {
                                initPlayer();
                                queueIfNothingPlaying(hubSingleton.getSongAt(0).getId());
                            } else {
                                currentlyPlaying = "";
                            }
                        }
                    }

                    @Override
                    public void onError(YouTubePlayer.ErrorReason errorReason) {

                    }

                });
            }
        });
    }

    public void searchVideo(View view) {
        if (searchEdit.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), "No Search Input", Toast.LENGTH_LONG).show();
            return;
        }
        if (userListView.getVisibility() == View.VISIBLE) viewButton.setChecked(false);
        songListView.setVisibility(View.GONE);
        userListView.setVisibility(View.GONE);
        videosFound.setVisibility(View.VISIBLE);
        searchOnYoutube(searchEdit.getText().toString());
        searchEdit.setText("");
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void searchOnYoutube(final String keywords){
        new Thread(){
            public void run(){
                YoutubeConnector yc = new YoutubeConnector(QueueActivity.this);
                searchResults = yc.search(keywords);
                handler.post(new Runnable(){
                    public void run(){
                        updateVideosFound();
                    }
                });
            }
        }.start();
    }
    private void updateVideosFound(){
        ArrayAdapter<VideoItem> adapter = new ArrayAdapter<VideoItem>(getApplicationContext(), R.layout.video_item, searchResults){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null){
                     convertView = getLayoutInflater().inflate(R.layout.video_item, parent, false);
                }
                ImageView thumbnail = (ImageView)convertView.findViewById(R.id.video_thumbnail);
                TextView title = (TextView)convertView.findViewById(R.id.video_title);
                TextView description = (TextView)convertView.findViewById(R.id.video_description);

                VideoItem searchResult = searchResults.get(position);


                Picasso.with(getApplicationContext()).load(searchResult.getThumbnailURL()).into(thumbnail);
                title.setText(searchResult.getTitle());
                description.setText(searchResult.getDescription());
                return convertView;
            }
        };

        videosFound.setAdapter(adapter);
    }
    private void addClickListener(){
        videosFound.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long ident) {
                // Firebase logic
                // Adds song to child of proper song list
                DatabaseReference songRef = FirebaseDatabase.getInstance().getReference().child("Song Lists")
                        .child(hubSingleton.getHubName()).child(searchResults.get(position).getId());
                songRef.child("Title").setValue(searchResults.get(position).getTitle());
                songRef.child("Up-votes").setValue(0);
                songRef.child("Down-votes").setValue(0);
                // TODO: add time added and playing variables

                songRef.child("Up-votes").addValueEventListener(voteListener);
                songRef.child("Down-votes").addValueEventListener(voteListener);

                videosFound.setVisibility(View.GONE);
                songListView.setVisibility(View.VISIBLE);
                userListView.setVisibility(View.GONE);
            }

        });
    }

    public void queueIfNothingPlaying(String video_id) {
        if (currentlyPlaying.equals("") && video_id != null && mYouTubePlayer != null) {
            mYouTubePlayer.loadVideo(video_id);
            currentlyPlaying = video_id;
        }
    }

}
