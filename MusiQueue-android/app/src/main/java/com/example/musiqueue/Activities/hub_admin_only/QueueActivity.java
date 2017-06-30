package com.example.musiqueue.Activities.hub_admin_only;

import android.app.Activity;
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

import com.example.musiqueue.HelperClasses.QueueSong;
import com.example.musiqueue.HelperClasses.HubSingleton;
import com.example.musiqueue.HelperClasses.User;
import com.example.musiqueue.HelperClasses.VideoItem;
import com.example.musiqueue.HelperClasses.YoutubeConnector;
import com.example.musiqueue.HelperClasses.adapters.UserItemAdapter;
import com.example.musiqueue.HelperClasses.adapters.VideoItemAdapter;
import com.example.musiqueue.R;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.api.client.util.Data;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class QueueActivity extends AppCompatActivity {
    final public String API_KEY = "AIzaSyDtCJTBSLt9M1Xi_EBr49Uk4W8q4HhFHPU";
    private YouTubePlayer mYouTubePlayer = null;

    private EditText searchEdit;
    private RecyclerView songListView;
    private RecyclerView userListView;
    private HubSingleton hubSingleton;
    private VideoItemAdapter adapter;
    private UserItemAdapter userAdapter;
    private String currentlyPlaying;
    private ToggleButton viewButton;
    private List<VideoItem> searchResults;
    private ListView videosFound;
    private Handler handler;

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

        // set up listener for user list
        final DatabaseReference userListRef = FirebaseDatabase.getInstance().getReference().child("User Lists")
                .child(hubSingleton.getHubName());
        userListRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String name = "";
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.getKey().equals("User Name") && child.getValue() != null) {
                        name = child.getValue().toString();
                    }
                }
                hubSingleton.addUser(new User(dataSnapshot.getKey(), name));
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // TODO
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Set up listener for song list
        final DatabaseReference songListRef = FirebaseDatabase.getInstance().getReference().child("Song Lists")
                .child(hubSingleton.getHubName());
        // This listener is to update the view when a song is added/removed
        songListRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String title = "";
                int upVotes = 0;
                int downVotes = 0;
                String id = dataSnapshot.getKey();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    if (snap.getKey().equals("Title") && snap.getValue() != null)
                        title = snap.getValue().toString();
                    else if (snap.getKey().equals("Up-votes") && snap.getValue() != null)
                        upVotes = Integer.parseInt(snap.getValue().toString());
                    else if (snap.getKey().equals("Down-votes") && snap.getValue() != null)
                        downVotes = Integer.parseInt(snap.getValue().toString());
                }
                hubSingleton.add(new QueueSong(id, title, hubSingleton.getUsername(), upVotes, downVotes, 0));
                songListRef.orderByValue();
                adapter.notifyDataSetChanged();
                queueIfNothingPlaying(id);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                List<QueueSong> list = hubSingleton.getEntireList();
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getId().equals(dataSnapshot.getKey())) {
                        hubSingleton.removeAt(i);
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                List<DataSnapshot> children = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    children.add(child);
                }
                reorderQueue(children);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // adding user in onStart because it always executes after onStop()
    @Override
    protected void onStart() {
        // Add user to user list for this hub
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User Lists")
                .child(hubSingleton.getHubName()).child(hubSingleton.getUserID());
        DatabaseReference userSubRef = userRef.child("User Name");
        userSubRef.setValue(hubSingleton.getUsername());
        // TODO add more fields
        super.onStart();
    }

    @Override
    protected void onStop() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User Lists")
                .child(hubSingleton.getHubName()).child(hubSingleton.getUserID());
        userRef.removeValue();
        super.onStop();
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
                final DatabaseReference songListRef = FirebaseDatabase.getInstance().getReference().child("Song Lists");
                final DatabaseReference songRef = songListRef.child(hubSingleton.getHubName())
                        .child(searchResults.get(position).getId());
                songRef.child("Title").setValue(searchResults.get(position).getTitle());
                songRef.child("User").setValue(hubSingleton.getUsername());
                songRef.child("Up-votes").setValue(0);
                songRef.child("Down-votes").setValue(0);
                FirebaseDatabase.getInstance().getReference().child("/Song Scores/"
                        + hubSingleton.getHubName() + "/" + songRef.getKey()).setValue(0);
                // TODO: add time added and playing variables

                songRef.child("Up-votes").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // find song in Firebase and update vote count
                        for (QueueSong song : hubSingleton.getEntireList()) {
                            if (song.getId().equals(songRef.getKey())) {
                                if (dataSnapshot.getValue() != null) {
                                    song.setUpVotes(Long.parseLong(dataSnapshot.getValue().toString()));
                                    songRef.setValue(song.getUpVotes() - song.getDownVotes());
                                }
                                else
                                    song.setUpVotes(0);
                                break;
                            }
                        }
                        // Reorder
                        if (hubSingleton.getQueueSize() > 2)
                            // TODO: reorder by score
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                songRef.child("Down-votes").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (QueueSong song : hubSingleton.getEntireList()) {
                            if (song.getId().equals(songRef.getKey())) {
                                if (dataSnapshot.getValue() != null) {
                                    song.setDownVotes(Long.parseLong(dataSnapshot.getValue().toString()));
                                    songRef.setValue(song.getUpVotes() - song.getDownVotes());
                                }
                                else
                                    song.setDownVotes(0);
                                break;
                            }
                        }
                        // Reorder
                        if (hubSingleton.getQueueSize() > 2)
                           // TODO: reorder by score
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                videosFound.setVisibility(View.GONE);
                songListView.setVisibility(View.VISIBLE);
                userListView.setVisibility(View.GONE);
            }

        });
    }

    private void reorderQueue(List<DataSnapshot> songs) {
        if (songs.size() < 3)
            return;
        // Reorder based on voting scores
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("/Song Lists/" + hubSingleton.getHubName());
        ref.limitToLast(songs.size() - 1).orderByValue();
        // Removing entire list in hub singleton and rewriting in new order based on Firebase
        // Outer loop snapshots are of each song, inner loop is details of each song
        List<QueueSong> oldList = new ArrayList<>(hubSingleton.getEntireList());
        hubSingleton.clearList();
        for (DataSnapshot song : songs) {
            String id = song.getKey();
            String title = "";
            String user = "";
            int upVotes = 0;
            int downVotes = 0;
            int state = 0;
            for (DataSnapshot detail : song.getChildren()) {
                if (detail.getKey().equals("Title") && detail.getValue() != null)
                    title = detail.getValue().toString();
                else if (detail.getKey().equals("User") && detail.getValue() != null)
                    user = detail.getValue().toString();
                else if (detail.getKey().equals("Up-votes") && detail.getValue() != null)
                    upVotes = Integer.parseInt(detail.getValue().toString());
                else if (detail.getKey().equals("Down-votes") && detail.getValue() != null)
                    downVotes = Integer.parseInt(detail.getValue().toString());
            }
            // find state in old list, can't keep track of in back end since state is unique per person
            for (QueueSong oldSong : oldList) {
                if (oldSong.getId().equals(id))
                    state = oldSong.getState();
            }
            hubSingleton.add(new QueueSong(id, title, user, upVotes, downVotes, state));
        }
    }

    public void queueIfNothingPlaying(String video_id) {
        if (currentlyPlaying.equals("") && video_id != null && mYouTubePlayer != null) {
            mYouTubePlayer.loadVideo(video_id);
            currentlyPlaying = video_id;
        }
    }

}
