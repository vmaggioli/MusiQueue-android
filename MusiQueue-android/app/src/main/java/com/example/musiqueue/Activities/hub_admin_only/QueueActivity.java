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
import com.squareup.picasso.Picasso;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class QueueActivity extends AppCompatActivity implements UpdateResultReceiver.Receiver {
    final public String API_KEY = "AIzaSyDtCJTBSLt9M1Xi_EBr49Uk4W8q4HhFHPU";
    private YouTubePlayer mYouTubePlayer = null;

    EditText searchEdit;
    Button searchButton;

    BackgroundWorker.AsyncResponse callback;
    BackgroundWorker addBW;
    BackgroundWorker listBW;
    BackgroundWorker removeBW;
    String id;
    String title;
    String removeId;
    RecyclerView songListView;
    RecyclerView userListView;
    HubSingleton hubSingleton;
    VideoItemAdapter adapter;
    UserItemAdapter userAdapter;
    UpdateResultReceiver receiver;
    String currentlyPlaying;
    ToggleButton viewButton;
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
        updateView();


        userListView = (RecyclerView) findViewById(R.id.userList);
        userListView.setVisibility(View.GONE);
        userListView.setAdapter(userAdapter);
        userListView.setLayoutManager(new LinearLayoutManager(this));
        userListView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).color(Color.LTGRAY).sizeResId(R.dimen.divider).marginResId(R.dimen.margin5dp, R.dimen.margin5dp).build());


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
                //mYouTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
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
                        if (hubSingleton.getEntireList() != null && hubSingleton.getEntireList().size() > 0)
                            currentlyPlaying = hubSingleton.getSongAt(0).getId();
                    }

                    @Override
                    public void onVideoEnded() {
                        if (hubSingleton.getEntireList() != null && hubSingleton.getEntireList().size() != 0) {
                            removeId = String.valueOf(hubSingleton.getSongAt(0).getPlace());
                            hubSingleton.removeAt(0);
                            changeAndUpdate("remove");
                            adapter.notifyDataSetChanged();
                            if (hubSingleton.getEntireList().size() != 0 && mYouTubePlayer != null)
                                mYouTubePlayer.loadVideo(hubSingleton.getSongAt(0).getId());
                            else if (hubSingleton.getEntireList().size() != 0 && mYouTubePlayer == null)
                                initPlayer();
                            currentlyPlaying = "";
                        }
                    }

                    @Override
                    public void onError(YouTubePlayer.ErrorReason errorReason) {

                    }

                });
            }
        });
    }

    public void changeAndUpdate(String type) {
        callback = new BackgroundWorker.AsyncResponse() {
            @Override
            public void processFinish(String result) {
                Boolean reInit = false;
                try {
                    if (hubSingleton.getEntireList().size() == 0) {
                        reInit = true;
                    }
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
                    userAdapter.notifyDataSetChanged();
                    if (currentlyPlaying.equals("") && hubSingleton.getQueueSize() != 0)
                        queueIfNothingPlaying(hubSingleton.getSongAt(0).getId());
                    else if (reInit && mYouTubePlayer != null && hubSingleton.getEntireList() != null && hubSingleton.getEntireList().size() != 0)
                        mYouTubePlayer.loadVideo(hubSingleton.getSongAt(0).getId());
                    else if (reInit && mYouTubePlayer == null)
                        initPlayer();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        addBW = new BackgroundWorker(callback);
        listBW = new BackgroundWorker(callback);
        removeBW = new BackgroundWorker(callback);

        Intent intent = getIntent();
        if(type.equals("add") && intent.hasExtra("title")) {
            title = intent.getStringExtra("title");
            id = intent.getStringExtra("id");
            QueueSong song = new QueueSong();
            song.setId(id);
            song.setTitle(title);

            addBW.execute("addSong", hubSingleton.getHubId().toString(), hubSingleton.getUserID(), id, title);
        } else if (type.equals("remove"))
            removeBW.execute("removeSong", hubSingleton.getHubId().toString(), hubSingleton.getUserID(), removeId);
        if (hubSingleton.getEntireList().size() == 0) {
            listBW.execute("hubSongList", hubSingleton.getHubId().toString(), hubSingleton.getUserID());
        }
        //listBW.execute("hubSongList", hubSingleton.getHubId().toString(), hubSingleton.getUserID());
        //queueIfNothingPlaying(id);
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
                Intent intent = getIntent();
                intent.putExtra("title", searchResults.get(position).getTitle());
                intent.putExtra("id", searchResults.get(position).getId());
                changeAndUpdate("add");
                videosFound.setVisibility(View.GONE);
                songListView.setVisibility(View.VISIBLE);
                userListView.setVisibility(View.GONE);
            }

        });
    }

    public void updateView() {
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
        Boolean reInit = false;
        try {

            if (hubSingleton.getEntireList().size() == 0) {
                reInit = true;
            }
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

                //System.out.println(name.getString("name"));

            }

            adapter.notifyDataSetChanged();
            userAdapter.notifyDataSetChanged();
            if (currentlyPlaying.equals("") && hubSingleton.getQueueSize() != 0)
                queueIfNothingPlaying(hubSingleton.getSongAt(0).getId());
            else if (reInit && mYouTubePlayer != null && hubSingleton.getEntireList() != null && hubSingleton.getEntireList().size() != 0)
                mYouTubePlayer.loadVideo(hubSingleton.getSongAt(0).getId());
            updateView();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void queueIfNothingPlaying(String video_id) {
        if (currentlyPlaying.equals("") && video_id != null && mYouTubePlayer != null)
                mYouTubePlayer.loadVideo(video_id);
    }

}
